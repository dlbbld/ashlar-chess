package com.dlb.chess.test.pgn.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.report.Reporter;
import com.dlb.chess.test.common.utility.OutputCaptureUtility;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

/**
 * Golden stdout guard for {@link Reporter#printReport}: any change to the printed bytes fails the test.
 *
 * <p>
 * Regenerate goldens with {@code -Dgolden.regenerate=true}; that mode deliberately fails every test so the flag cannot
 * be accidentally committed in the green state.
 */
class TestReporterGoldenOutput {

  private static final boolean REGENERATE = Boolean.getBoolean("golden.regenerate");

  private static final String CLASSPATH_GOLDEN_ROOT = "/report/golden/";
  private static final Path FILESYSTEM_GOLDEN_ROOT = Nulls.pathsGet("src", "test", "resources", "report", "golden");

  @SuppressWarnings("static-method")
  @Test
  void noThreefoldActivity() {
    final var pgn = """
        1. e4 e5 2. Nf3 Nf6 3. Bc4 Bc5
        """;
    final String actual = captureStdout(() -> Reporter.printReport(pgn));
    compareOrRegenerate(actual, "01_no_threefold_activity.txt");
  }

  @SuppressWarnings("static-method")
  @Test
  void claimAheadOnlyInitial() {
    // 1. Nf3 Nf6 2. Ng1 Ng8 3. Nf3 Nf6 4. Ng1 — Black has not played 4...Ng8 yet, so the
    // initial-position third occurrence is one ply ahead. Pure claim-ahead.
    final String actual = capturePgnFile("01_threefold_moves_very_low_one_before_first_threefold.pgn");
    compareOrRegenerate(actual, "02_claim_ahead_only_initial.txt");
  }

  @SuppressWarnings("static-method")
  @Test
  void threefoldReachedInitial() {
    // 1. Nf3 Nf6 2. Ng1 Ng8 3. Nf3 Nf6 4. Ng1 Ng8 — third occurrence of the initial position
    // is on the board. The 15.0.0 regression case for initial-position repetition.
    final String actual = capturePgnFile("02_threefold_moves_very_low_end_with_first_threefold.pgn");
    compareOrRegenerate(actual, "03_threefold_reached_initial.txt");
  }

  @SuppressWarnings("static-method")
  @Test
  void threefoldBeyond() {
    // Two threefolds reached, then many moves continue beyond — exercises the "and beyond"
    // capture and multi-group sorting.
    final String actual = capturePgnFile("18_threefold_two_threefolds_beyond.pgn");
    compareOrRegenerate(actual, "04_threefold_beyond.txt");
  }

  @SuppressWarnings("static-method")
  @Test
  void threefoldCastling() {
    // Threefold involving castling rights — different position-identity code path than the
    // pure knight-shuffle fixtures.
    final String actual = capturePgnFile("11_threefold_castling_one_before_first_threefold.pgn");
    compareOrRegenerate(actual, "05_threefold_castling.txt");
  }

  @SuppressWarnings("static-method")
  @Test
  void fivefoldCorrectPotapovAdly2018() {
    // The only corpus fixture that produces multiple claim-ahead entries for the same dynamic
    // position (length 3, 4, 5 of the A-group plus separate B / C groups). Locks in the line
    // sort order so a future regression that ranks lines by claim-ahead-ply (the old shape) or
    // by some other key would break a golden, not just slip silently into the printed report.
    final String actual = capturePgnFile("fivefold_correct_potapov_adly_2018.pgn");
    compareOrRegenerate(actual, "06_fivefold_correct_potapov_adly_2018.txt");
  }

  @SuppressWarnings("static-method")
  @Test
  void fiftyMoveInitialFenAtThresholdNoContinuation() {
    // FEN with halfmove clock already at 100; black queen on b2 caging white's king on a1. The only
    // legal move is Kxb2 — a capture, which resets the clock. The 50-move rule was met by the FEN
    // itself; no claim-ahead is possible (the predicate rejects clock-resetting candidates) but
    // the sequence-section must surface the threshold-met state with a bare start marker:
    //
    // Fifty moves and beyond:
    // [Starting position] (100)
    //
    // Locks the special-case rendering — sequence-with-no-endPly — that's hard to test from any
    // other entry point.
    final Board board = new Board("7k/8/8/8/8/8/1q6/K7 w - - 100 80");
    final String actual = captureStdout(() -> Reporter.printReport(board));
    compareOrRegenerate(actual, "07_fifty_move_initial_fen_at_threshold.txt");
  }

  @SuppressWarnings("static-method")
  @Test
  void fiftyMoveMissedClaimAheadWhenPawnPushBreaksSequence() {
    // The missed-opportunity case: FEN clock 98, play one non-zeroing move (clock 99), then a pawn
    // push resets the clock to 0. The sequence ends at clock 99 without reaching the threshold —
    // so the "Valid fifty-move claims ahead" section lists every alternative non-zeroing legal move
    // the player COULD have made at the boundary instead of the pawn push. The "Fifty moves and
    // beyond" section is empty (no sequence reached 100).
    //
    // Locks the missed-opportunity output shape; previously no corpus or inline golden exercised it.
    final Board board = new Board("4k3/p7/8/8/8/8/P7/4K2R w - - 98 80");
    board.movesStrict("Rg1", "a6");
    final String actual = captureStdout(() -> Reporter.printReport(board));
    compareOrRegenerate(actual, "08_fifty_move_missed_claim_ahead.txt");
  }

  private static String capturePgnFile(String pgnName) {
    final PgnTest pgnTest = PgnTestCaseCatalog.findPgnTestPgnNotListed(pgnName);
    return captureStdout(() -> Reporter.printReport(pgnTest.getFolderPath(), pgnName));
  }

  private static String captureStdout(Runnable action) {
    return normaliseLineEndings(OutputCaptureUtility.captureStdout(action));
  }

  private static void compareOrRegenerate(String actual, String goldenName) {
    if (REGENERATE) {
      writeGolden(goldenName, actual);
      fail("Regenerated golden " + goldenName + " — rerun without -Dgolden.regenerate=true to verify.");
    }
    final String expected = readGolden(goldenName);
    assertEquals(expected, actual);
  }

  private static String readGolden(String goldenName) {
    final var resourcePath = CLASSPATH_GOLDEN_ROOT + goldenName;
    try (var in = TestReporterGoldenOutput.class.getResourceAsStream(resourcePath)) {
      if (in == null) {
        throw new IllegalStateException("Golden resource not found on classpath: " + resourcePath
            + " — run with -Dgolden.regenerate=true to create it.");
      }
      return normaliseLineEndings(new String(in.readAllBytes(), StandardCharsets.UTF_8));
    } catch (final IOException e) {
      throw new IllegalStateException("Failed to read golden " + resourcePath, e);
    }
  }

  private static void writeGolden(String goldenName, String content) {
    try {
      Files.createDirectories(FILESYSTEM_GOLDEN_ROOT);
      Files.writeString(FILESYSTEM_GOLDEN_ROOT.resolve(goldenName), content, StandardCharsets.UTF_8);
    } catch (final IOException e) {
      throw new IllegalStateException("Failed to write golden " + goldenName, e);
    }
  }

  private static String normaliseLineEndings(String input) {
    return OutputCaptureUtility.normaliseLineEndings(input);
  }
}
