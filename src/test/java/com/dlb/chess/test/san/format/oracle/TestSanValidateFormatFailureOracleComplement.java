package com.dlb.chess.test.san.format.oracle;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.san.SanValidateFormat;
import com.dlb.chess.san.SanValidationException;
import com.dlb.chess.test.RestrictTestConstants;
import com.dlb.chess.test.san.reference.SanValidateFormatBasic;
import com.dlb.chess.test.san.validate.statically.format.calculate.SanValidateStaticallyFormat;

/**
 * Exhaustive completeness test: enumerates every string that can plausibly be a SAN move (drawn from a restricted
 * alphabet, up to MAX_LENGTH characters, with per-category occurrence limits) and asserts that
 * {@link SanValidateFormat#validateFormat} and the pre-built static SAN map
 * ({@link SanValidateStaticallyFormat#getSanValidationMap()}) agree on whether each string is valid.
 *
 * <p>
 * Performance note: this test processes millions of candidates. All hot-path data structures are primitive arrays for
 * O(1) lookup. The generation uses in-place backtracking to avoid allocating intermediate strings. Do not replace these
 * structures with higher-level abstractions.
 */
class TestSanValidateFormatFailureOracleComplement {

  // ---------------------------------------------------------------------------
  // Alphabet: every character that may appear in a SAN string (check/checkmate
  // symbols +/# are deliberately excluded because the static map does not
  // include those variants).
  // ---------------------------------------------------------------------------
  private static final char[] ALPHABET = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', // file letters
      '1', '2', '3', '4', '5', '6', '7', '8', // rank digits
      'N', 'B', 'Q', 'K', 'R', // piece letters
      'x', '=' // capture / promotion symbols
  };

  /** Longest SAN string to consider (e.g. "Qc3xe5" = 6 chars). */
  private static final int MAX_LENGTH = 6;

  /**
   * Castling moves emitted as-is. They use characters ({@code O}, {@code -}) that are outside the combinatoric
   * {@link #ALPHABET}, so we handle them as literal extras rather than polluting the alphabet.
   */
  private static final Set<String> CASTLING_MOVES = Nulls.setOf("O-O", "O-O-O");

  /** How often to print a progress line to stdout. */
  private static final int PRINT_INTERVAL = 100_000;

  // ---------------------------------------------------------------------------
  // Character categories - every character in ALPHABET belongs to exactly one
  // category. A category has a maximum number of occurrences allowed in any
  // single SAN string; the generator prunes branches that would exceed it.
  // ---------------------------------------------------------------------------
  private static final int CAT_FILE = 0; // a-h (file letters, max 2: from-file + to-file)
  private static final int CAT_DIGIT = 1; // 1-8 (rank digits, max 2: from-rank + to-rank)
  private static final int CAT_RNBQ = 2; // R,N,B,Q (non-king piece letter, max 1)
  private static final int CAT_K = 3; // K (king letter, max 1)
  private static final int CAT_X = 4; // x (capture symbol, max 1)
  private static final int CAT_EQUALS = 5; // = (promotion symbol, max 1)
  private static final int NUM_CATS = 6;

  /** Maximum occurrences per category; indexed by the CAT_* constants above. */
  private static final int[] CAT_MAX = { 2, 2, 1, 1, 1, 1 };

  // ---------------------------------------------------------------------------
  // Fast lookup tables indexed by char value (ASCII only).
  // CHAR_CATEGORY maps a character to its category index (-1 = uncategorised).
  // VALID_FIRST_CHAR marks which characters may legally open a SAN string
  // (file letters a-h, or piece letters R/N/B/Q/K).
  // ---------------------------------------------------------------------------

  /** Maps ASCII char value -> category index; -1 for characters not in ALPHABET. */
  private static final int[] CHAR_CATEGORY = new int[128];

  /**
   * {@code true} if the character is a legal first character of a SAN string. SAN strings must begin with a file letter
   * (pawn move) or a piece letter.
   */
  private static final boolean[] VALID_FIRST_CHAR = new boolean[128];

  static {
    Arrays.fill(CHAR_CATEGORY, -1);

    for (final char c : new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' }) {
      CHAR_CATEGORY[c] = CAT_FILE;
      VALID_FIRST_CHAR[c] = true; // pawn move starts with a file letter
    }
    for (final char c : new char[] { '1', '2', '3', '4', '5', '6', '7', '8' }) {
      CHAR_CATEGORY[c] = CAT_DIGIT;
    }
    for (final char c : new char[] { 'R', 'N', 'B', 'Q' }) {
      CHAR_CATEGORY[c] = CAT_RNBQ;
      VALID_FIRST_CHAR[c] = true; // piece move starts with the piece letter
    }
    CHAR_CATEGORY['K'] = CAT_K;
    VALID_FIRST_CHAR['K'] = true; // king move starts with K
    CHAR_CATEGORY['x'] = CAT_X;
    CHAR_CATEGORY['='] = CAT_EQUALS;
  }

  // ---------------------------------------------------------------------------
  // Test entry point
  // ---------------------------------------------------------------------------

  // Completeness test done. Total checks: 9,737,475, total elapsed: 176.8 s
  @SuppressWarnings("static-method")
  @Test
  void testFailureOracleComplement() {
    assumeFalse(RestrictTestConstants.IS_EXCLUDE_LONG_RUNNING_SAN_VALIDATE_FORMAT_FAILURE_ORACLE_COMPLEMENT_TEST);

    final Set<String> validSanSet = Nulls.keySet(SanValidateStaticallyFormat.getSanValidationMap());
    final long startTime = System.currentTimeMillis();

    // Single-element array: the idiomatic Java way to hold a mutable long counter
    // that is updated deep inside a recursive method without using instance state.
    final long[] totalChecked = { 0 };

    generateAllCandidates(san -> checkCandidate(san, validSanSet, totalChecked, startTime));

    final long totalElapsed = System.currentTimeMillis() - startTime;
    System.out.println("Completeness test done. Total checks: " + String.format("%,d", totalChecked[0])
        + ", total elapsed: " + String.format("%.1f", totalElapsed / 1000.0) + " s");
  }

  /**
   * Asserts that the candidate generator produces every SAN in the static SAN map that is within the generator's scope
   * - i.e. castling moves plus SANs whose characters all lie in {@link #ALPHABET} (excluding terminal markers
   * {@code +}/{@code #}). Without this guarantee, the failure oracle complement test could silently miss bugs where
   * {@link SanValidateFormat#validateFormat} rejects valid SANs that the generator never exercises.
   */
  @SuppressWarnings("static-method")
  @Test
  void testGeneratorCoversStaticMap() {
    final Set<String> missing = new HashSet<>();
    for (final String san : Nulls.keySet(SanValidateStaticallyFormat.getSanValidationMap())) {
      if (isInGeneratorScope(san)) {
        missing.add(san);
      }
    }
    generateAllCandidates(missing::remove);
    assertTrue(missing.isEmpty(), "Generator did not produce " + missing.size() + " SAN(s) from the static map, e.g.: "
        + missing.stream().limit(10).toList());
  }

  private static boolean isInGeneratorScope(final String san) {
    if (CASTLING_MOVES.contains(san)) {
      return true;
    }
    for (int i = 0; i < san.length(); i++) {
      final char c = san.charAt(i);
      if (c >= CHAR_CATEGORY.length || CHAR_CATEGORY[c] == -1) {
        return false;
      }
    }
    return true;
  }

  /** Emits castling first, then all combinatorically generated candidates. */
  private static void generateAllCandidates(final Consumer<String> consumer) {
    for (final String castling : CASTLING_MOVES) {
      consumer.accept(castling);
    }
    generateCandidates(new char[MAX_LENGTH], 0, new int[NUM_CATS], consumer);
  }

  // ---------------------------------------------------------------------------
  // Core generation + checking
  // ---------------------------------------------------------------------------

  /**
   * Recursively builds all candidate SAN strings by appending one character at a time, pruning branches early when a
   * per-category occurrence limit would be exceeded.
   *
   * <p>
   * The algorithm is a depth-first search with backtracking:
   * <ol>
   * <li>Check the current prefix (if non-empty).</li>
   * <li>For each character in the alphabet, skip it if:
   * <ul>
   * <li>we are at position 0 and the character cannot start a SAN string, or</li>
   * <li>appending it would exceed that character's category limit.</li>
   * </ul>
   * </li>
   * <li>Append the character, recurse, then undo the append (backtrack).</li>
   * </ol>
   *
   * <p>
   * The {@code buffer} and {@code categoryCounts} arrays are mutated in-place to avoid allocating new objects on every
   * recursive call.
   */
  private static void generateCandidates(final char[] buffer, final int length, final int[] categoryCounts,
      final Consumer<String> consumer) {

    if (length > 0) {
      consumer.accept(new String(buffer, 0, length));
    }
    if (length == MAX_LENGTH) {
      return;
    }

    for (final char c : ALPHABET) {
      // SAN strings must start with a file letter (pawn) or a piece letter (R/N/B/Q/K)
      if (length == 0 && !VALID_FIRST_CHAR[c]) {
        continue;
      }
      // Prune: do not exceed the maximum allowed occurrences for this character's category.
      // Note: every character in ALPHABET has a valid category (CHAR_CATEGORY != -1).
      final int category = CHAR_CATEGORY[c];
      if (categoryCounts[category] < CAT_MAX[category]) {
        buffer[length] = c;
        categoryCounts[category]++;
        generateCandidates(buffer, length + 1, categoryCounts, consumer);
        categoryCounts[category]--; // backtrack
      }
    }
  }

  /**
   * Asserts that {@link SanValidateFormat#validateFormat} and the static SAN map agree on whether {@code san} is a
   * valid SAN string, then updates the progress counter.
   */
  private static void checkCandidate(final String san, final Set<String> validSanSet, final long[] totalChecked,
      final long startTime) {

    boolean isValid;
    try {
      SanValidateFormat.validateFormat(san);
      isValid = true;
    } catch (@SuppressWarnings("unused") final SanValidationException e) {
      isValid = false;
    }

    // Anything rejected by the coarse basic check must also be rejected by validateFormat itself - basic is a
    // strict superset filter, so its rejection is sufficient evidence for non-acceptance.
    if (!SanValidateFormatBasic.isBasicFormatValid(san)) {
      assertFalse(isValid, "isBasicFormatValid rejects \"" + san + "\" but validateFormat accepts it");
    }

    if (isValid) {
      assertTrue(validSanSet.contains(san),
          "validateFormat accepted \"" + san + "\" but it is absent from the static SAN map");
    } else {
      assertFalse(validSanSet.contains(san),
          "validateFormat rejected \"" + san + "\" but it is present in the static SAN map");
    }

    totalChecked[0]++;
    if (totalChecked[0] % PRINT_INTERVAL == 0) {
      final long elapsed = System.currentTimeMillis() - startTime;
      System.out.println("Processed: " + String.format("%,d", totalChecked[0]) + ", elapsed: "
          + String.format("%.1f", elapsed / 1000.0) + " s - current SAN: " + san);
    }
  }

}
