// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.fen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.enums.FenAdvancedValidationProblem;
import io.github.dlbbld.ashlarchess.common.exceptions.FenAdvancedValidationException;
import io.github.dlbbld.ashlarchess.common.utility.BasicUtility;
import io.github.dlbbld.ashlarchess.fen.model.Fen;

/**
 * Lenient FEN parser. Applies a purely syntactic-tolerance normalisation pass (whitespace, casing, missing trailing
 * counters, non-canonical castling order, non-ASCII dashes, trailing garbage) then delegates to
 * {@link FenParserAdvanced} for the structural and rule-consistency checks. Every transformation applied during
 * normalisation surfaces as a {@link ForgivenFenItem} on the result.
 *
 * <p>
 * The lenient layer does <b>not</b> weaken any of the strict semantic invariants — a FEN with a missing king, a pawn on
 * rank 1, an impossible double-check, or castling rights that contradict the piece placement still fails.
 * Strict-vs-lenient is a syntactic axis; raw-vs-advanced is a semantic axis. The lenient layer operates on the
 * syntactic axis only and reuses the strict semantic pipeline for everything else.
 *
 * <p>
 * Two entry points:
 * <ul>
 * <li>{@link #parseText(String)} — returns the parsed {@link Fen} on success, throws
 * {@link LenientFenParserValidationException} on failure (with the accumulated forgiven items carried on the exception
 * so callers can still see diagnostics).</li>
 * <li>{@link #validateText(String)} — returns a {@link LenientFenParserValidationResult} that never throws; carries the
 * parsed {@code Fen} (or {@code null} on failure), the typed problem categorisation, and the forgiven-items list.</li>
 * </ul>
 */
@SuppressWarnings("null")
public final class LenientFenParser {

  private LenientFenParser() {
  }

  // -------------------------------------------------------------------------------------------------
  // Public entry points
  // -------------------------------------------------------------------------------------------------

  /**
   * Parses {@code fen} leniently and returns the result. Throws {@link LenientFenParserValidationException} when the
   * input cannot be recovered or fails the strict semantic checks; the exception carries the typed problem plus the
   * forgiven items accumulated before the failure point.
   */
  public static Fen parseText(String fen) {
    final List<ForgivenFenItem> accumulator = new ArrayList<>();
    return parseInternal(fen, accumulator);
  }

  /**
   * Like {@link #parseText(String)} but returns a structured result instead of throwing. The result also carries the
   * parsed {@link Fen} (on success) and the list of forgiven items the lenient layer applied during normalisation.
   */
  public static LenientFenParserValidationResult validateText(String fen) {
    final List<ForgivenFenItem> accumulator = new ArrayList<>();
    try {
      final Fen parsedFen = parseInternal(fen, accumulator);
      return new LenientFenParserValidationResult(LenientFenParserValidationProblem.OK,
          FenAdvancedValidationProblem.SUCCESS, "OK", parsedFen, Nulls.copyOfList(accumulator));
    } catch (final LenientFenParserValidationException e) {
      return new LenientFenParserValidationResult(e.getLenientFenParserValidationProblem(),
          e.getFenAdvancedValidationProblem(), BasicUtility.getMessage(e), null, e.getForgivenItemsAccumulated());
    } catch (final RuntimeException e) {
      final String message = "An unexpected error occurred during lenient FEN validation. Reason: "
          + (e.getMessage() == null ? "" : e.getMessage());
      return new LenientFenParserValidationResult(LenientFenParserValidationProblem.UNKNOWN_ERROR,
          FenAdvancedValidationProblem.SUCCESS, message, null, ForgivenFenItem.EMPTY_LIST);
    }
  }

  /**
   * Shared internal pipeline: normalise then delegate, with a single auto-correction retry for the
   * halfmove clock vs fullmove number consistency check (the only strict invariant the lenient layer recovers from).
   * The accumulator is populated by both successful and failed paths so the exception (and the validation result
   * derived from it) carries every transformation that fired before the failure point.
   */
  private static Fen parseInternal(String fen, List<ForgivenFenItem> accumulator) {
    final String canonical = normalise(fen, accumulator);
    try {
      return FenParserAdvanced.parseFenAdvanced(canonical);
    } catch (final FenAdvancedValidationException e) {
      if (e
          .getFenAdvancedValidationProblem() == FenAdvancedValidationProblem.INVALID_HALF_MOVE_CLOCK_TOO_BIG_RELATIVE_TO_FULL_MOVE_NUMBER) {
        final String corrected = autoCorrectHalfMoveClockInconsistency(canonical, accumulator);
        try {
          return FenParserAdvanced.parseFenAdvanced(corrected);
        } catch (final FenAdvancedValidationException retryFailure) {
          throw new LenientFenParserValidationException(LenientFenParserValidationProblem.ADVANCED_INVALID,
              retryFailure.getFenAdvancedValidationProblem(), BasicUtility.getMessage(retryFailure),
              Nulls.copyOfList(accumulator));
        }
      }
      throw new LenientFenParserValidationException(LenientFenParserValidationProblem.ADVANCED_INVALID,
          e.getFenAdvancedValidationProblem(), BasicUtility.getMessage(e), Nulls.copyOfList(accumulator));
    }
  }

  /**
   * Rebuilds the normalised FEN with {@code fullMoveNumber} bumped up well above the minimum value consistent with the
   * stated {@code halfMoveClock}. The bump targets a generous reserve rather than the strict minimum: the new value is
   * {@code halfMoveClock} rounded up to the next multiple of ten (so {@code halfMoveClock = 15} yields
   * {@code fullMoveNumber = 20}, well above the strict-minimum of 9). The reserve makes the auto-corrected value
   * visibly approximate — a consumer looking at the FEN sees a round-numbered placeholder that signals "the
   * fullMoveNumber here was reconstructed, not measured." Called only after {@link FenParserAdvanced} has flagged
   * {@link FenAdvancedValidationProblem#INVALID_HALF_MOVE_CLOCK_TOO_BIG_RELATIVE_TO_FULL_MOVE_NUMBER}, so the input is
   * known to have six well-formed fields with parseable integer counters and a valid side-to-move letter.
   */
  private static String autoCorrectHalfMoveClockInconsistency(String canonical, List<ForgivenFenItem> accumulator) {
    final String[] fields = canonical.split(" ");
    final int halfMoveClock = Integer.parseInt(fields[4]);
    // Round up to the next multiple of ten strictly greater than halfMoveClock. For halfMoveClock = 15 the result
    // is 20; for halfMoveClock = 20 it is 30. Always at least 10 (when halfMoveClock = 0, though that case never
    // triggers a violation). The strict-minimum value would be ((halfMoveClock + 1) / 2 + 1) for white-to-move
    // and (halfMoveClock / 2 + 1) for black-to-move; the reserve value is always strictly greater than that
    // minimum, satisfying the consistency check with room to spare.
    final int reserveFullMoveNumber = (halfMoveClock / 10 + 1) * 10;
    final String oldFullMoveNumber = fields[5];
    fields[5] = Integer.toString(reserveFullMoveNumber);
    final String corrected = String.join(" ", fields);
    accumulator.add(new ForgivenFenItem(ForgivenFenItemCode.HALF_MOVE_CLOCK_INCONSISTENT_WITH_FULL_MOVE_NUMBER,
        oldFullMoveNumber, fields[5]));
    return corrected;
  }

  // -------------------------------------------------------------------------------------------------
  // Normalisation pipeline
  // -------------------------------------------------------------------------------------------------

  /**
   * Applies the lenient normalisation transforms in order, recording each as a {@link ForgivenFenItem} on the
   * accumulator. Returns the canonical six-field FEN string ready for {@link FenParserRaw} / {@link FenParserAdvanced}.
   * Throws {@link LenientFenParserValidationException} with {@link LenientFenParserValidationProblem#UNRECOVERABLE}
   * when the input has fewer than four fields after normalisation (cannot recover to a FEN with a defaulted
   * halfmove clock and fullmove number).
   */
  private static String normalise(String fen, List<ForgivenFenItem> accumulator) {
    String working = fen;

    // 1. Leading whitespace.
    final String afterLeading = stripLeading(working);
    if (afterLeading.length() != working.length()) {
      final String stripped = Nulls.substring(working, 0, working.length() - afterLeading.length());
      accumulator.add(new ForgivenFenItem(ForgivenFenItemCode.LEADING_WHITESPACE, stripped, ""));
    }
    working = afterLeading;

    // 2. Trailing whitespace.
    final String afterTrailing = stripTrailing(working);
    if (afterTrailing.length() != working.length()) {
      final String stripped = Nulls.substring(working, afterTrailing.length(), working.length());
      accumulator.add(new ForgivenFenItem(ForgivenFenItemCode.TRAILING_WHITESPACE, stripped, ""));
    }
    working = afterTrailing;

    // 3. Tab / newline as inter-field separator. Replace with single space (collapse later).
    if (containsAny(working, "\t\r\n")) {
      working = working.replace('\t', ' ').replace('\r', ' ').replace('\n', ' ');
      accumulator.add(new ForgivenFenItem(ForgivenFenItemCode.TAB_OR_NEWLINE_AS_SEPARATOR, "", ""));
    }

    // 4. Collapse multi-space runs to single space.
    if (working.contains("  ")) {
      final StringBuilder collapsed = new StringBuilder(working.length());
      boolean prevWasSpace = false;
      for (int i = 0; i < working.length(); i++) {
        final char c = working.charAt(i);
        if (c == ' ') {
          if (!prevWasSpace) {
            collapsed.append(c);
          }
          prevWasSpace = true;
        } else {
          collapsed.append(c);
          prevWasSpace = false;
        }
      }
      working = Nulls.toString(collapsed);
      accumulator.add(new ForgivenFenItem(ForgivenFenItemCode.EXTRA_WHITESPACE_BETWEEN_FIELDS, "", ""));
    }

    // 5. Split into fields.
    final String[] fieldsArray = working.isEmpty() ? new String[0] : working.split(" ");
    if (fieldsArray.length < 4) {
      throw new LenientFenParserValidationException(LenientFenParserValidationProblem.UNRECOVERABLE,
          "The input has " + fieldsArray.length + " whitespace-separated fields; a FEN needs at least four (piece"
              + " placement, side to move, castling rights, en passant target square).");
    }

    // 6. Trim trailing garbage past field 6.
    final List<String> fields = new ArrayList<>();
    Collections.addAll(fields, fieldsArray);
    if (fields.size() > 6) {
      final List<String> dropped = fields.subList(6, fields.size());
      final String droppedJoined = String.join(" ", dropped);
      // Single TRAILING_GARBAGE_TOKEN item carrying the joined drop in `original`.
      accumulator.add(new ForgivenFenItem(ForgivenFenItemCode.TRAILING_GARBAGE_TOKEN, droppedJoined, ""));
      while (fields.size() > 6) {
        fields.remove(fields.size() - 1);
      }
    }

    // 7. Default missing trailing counters.
    if (fields.size() == 4) {
      fields.add("0");
      fields.add("1");
      accumulator.add(new ForgivenFenItem(ForgivenFenItemCode.MISSING_HALF_MOVE_CLOCK_AND_FULL_MOVE_NUMBER, "", "0 1"));
    } else if (fields.size() == 5) {
      fields.add("1");
      accumulator.add(new ForgivenFenItem(ForgivenFenItemCode.MISSING_FULL_MOVE_NUMBER, "", "1"));
    }

    // 8. Field-level normalisation.
    normaliseSideToMove(fields, accumulator);
    normaliseCastlingRights(fields, accumulator);
    normaliseEnPassantTargetSquare(fields, accumulator);

    return String.join(" ", fields);
  }

  private static void normaliseSideToMove(List<String> fields, List<ForgivenFenItem> accumulator) {
    final String field = Nulls.get(fields, 1);
    if ("W".equals(field) || "B".equals(field)) {
      final String canonical = field.toLowerCase(Locale.ROOT);
      fields.set(1, canonical);
      accumulator.add(new ForgivenFenItem(ForgivenFenItemCode.UPPERCASE_SIDE_TO_MOVE, field, canonical));
    }
  }

  private static void normaliseCastlingRights(List<String> fields, List<ForgivenFenItem> accumulator) {
    final String field = Nulls.get(fields, 2);
    if ("-".equals(field) || field.isEmpty()) {
      return;
    }
    // Field must contain only letters from {K, Q, k, q}, each at most once. The lenient layer normalises ORDER
    // but does not collapse DUPLICATES — a field like "KKKQkq" is a transcription error, not a recognised
    // deviation pattern, and is left for the strict parser to reject as INVALID_CASTLING_RIGHT_RANGE.
    int seen = 0;
    for (int i = 0; i < field.length(); i++) {
      final char c = field.charAt(i);
      final int bit;
      switch (c) {
        case 'K':
          bit = 1;
          break;
        case 'Q':
          bit = 2;
          break;
        case 'k':
          bit = 4;
          break;
        case 'q':
          bit = 8;
          break;
        default:
          // Unknown character; let raw parser reject this.
          return;
      }
      if ((seen & bit) != 0) {
        // Duplicate character; let raw parser reject this.
        return;
      }
      seen |= bit;
    }
    final boolean hasK = field.indexOf('K') >= 0;
    final boolean hasQ = field.indexOf('Q') >= 0;
    final boolean haslowerK = field.indexOf('k') >= 0;
    final boolean haslowerQ = field.indexOf('q') >= 0;
    final StringBuilder canonical = new StringBuilder();
    if (hasK) {
      canonical.append('K');
    }
    if (hasQ) {
      canonical.append('Q');
    }
    if (haslowerK) {
      canonical.append('k');
    }
    if (haslowerQ) {
      canonical.append('q');
    }
    final String canonicalStr = Nulls.toString(canonical);
    if (!canonicalStr.equals(field)) {
      fields.set(2, canonicalStr);
      accumulator.add(new ForgivenFenItem(ForgivenFenItemCode.CASTLING_NON_CANONICAL_ORDER, field, canonicalStr));
    }
  }

  private static void normaliseEnPassantTargetSquare(List<String> fields, List<ForgivenFenItem> accumulator) {
    String field = Nulls.get(fields, 3);

    // Non-ASCII dash variants: replace with `-`. Detection is conservative — only when the field is a single
    // character that is one of the known dash code points. (We do not strip dashes mid-square because that would
    // never match a chess square anyway.)
    if (field.length() == 1 && isNonStandardDash(field.charAt(0))) {
      final String original = field;
      field = "-";
      fields.set(3, field);
      accumulator.add(new ForgivenFenItem(ForgivenFenItemCode.EN_PASSANT_NON_STANDARD_DASH, original, field));
      return;
    }

    // Uppercase target square (e.g. "E3"): lowercase the file letter. The rank digit is unaffected.
    if (field.length() == 2) {
      final char file = field.charAt(0);
      if (file >= 'A' && file <= 'H') {
        final String original = field;
        final char rank = field.charAt(1);
        final String canonical = "" + (char) (file - 'A' + 'a') + rank;
        fields.set(3, canonical);
        accumulator.add(new ForgivenFenItem(ForgivenFenItemCode.EN_PASSANT_UPPERCASE, original, canonical));
      }
    }
  }

  /**
   * Conservative recognition of Unicode dashes that show up in real-world FEN exports where the producer intended an
   * ASCII hyphen. Listed code points cover the dash family most likely to appear: em-dash, en-dash, hyphen,
   * non-breaking hyphen, figure dash, horizontal bar, minus sign, fullwidth hyphen-minus.
   */
  private static boolean isNonStandardDash(char c) {
    return c == '‐' || c == '‑' || c == '‒' || c == '–' || c == '—' || c == '―' || c == '−' || c == '－';
  }

  // -------------------------------------------------------------------------------------------------
  // Whitespace helpers
  // -------------------------------------------------------------------------------------------------

  /**
   * Strips Java-recognised whitespace from the start of the string. Equivalent to {@link String#stripLeading()},
   * extracted as a helper so the call site reads symmetrically with {@link #stripTrailing(String)}.
   */
  private static String stripLeading(String s) {
    return s.stripLeading();
  }

  private static String stripTrailing(String s) {
    return s.stripTrailing();
  }

  /**
   * True iff {@code s} contains any character from {@code charsToMatch}.
   */
  private static boolean containsAny(String s, String charsToMatch) {
    for (int i = 0; i < s.length(); i++) {
      if (charsToMatch.indexOf(s.charAt(i)) >= 0) {
        return true;
      }
    }
    return false;
  }
}
