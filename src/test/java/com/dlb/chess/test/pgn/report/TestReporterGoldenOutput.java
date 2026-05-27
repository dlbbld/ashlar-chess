package com.dlb.chess.test.pgn.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.dlb.chess.report.Reporter;
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
  private static final Path FILESYSTEM_GOLDEN_ROOT = Paths.get("src", "test", "resources", "report", "golden");

  @SuppressWarnings("static-method")
  @Test
  void noThreefoldActivity() {
    final String pgn = """
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

  private static String capturePgnFile(String pgnName) {
    final PgnTest pgnTest = PgnTestCaseCatalog.findPgnTestPgnNotListed(pgnName);
    return captureStdout(() -> Reporter.printReport(pgnTest.getFolderPath(), pgnName));
  }

  private static String captureStdout(Runnable action) {
    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    final PrintStream original = System.out;
    final PrintStream captured = new PrintStream(buffer, true, StandardCharsets.UTF_8);
    System.setOut(captured);
    try {
      action.run();
    } finally {
      System.setOut(original);
    }
    return normaliseLineEndings(buffer.toString(StandardCharsets.UTF_8));
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
    final String resourcePath = CLASSPATH_GOLDEN_ROOT + goldenName;
    try (InputStream in = TestReporterGoldenOutput.class.getResourceAsStream(resourcePath)) {
      if (in == null) {
        throw new IllegalStateException("Golden resource not found on classpath: " + resourcePath
            + " — run with -Dgolden.regenerate=true to create it.");
      }
      return normaliseLineEndings(new String(in.readAllBytes(), StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read golden " + resourcePath, e);
    }
  }

  private static void writeGolden(String goldenName, String content) {
    try {
      Files.createDirectories(FILESYSTEM_GOLDEN_ROOT);
      Files.writeString(FILESYSTEM_GOLDEN_ROOT.resolve(goldenName), content, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to write golden " + goldenName, e);
    }
  }

  private static String normaliseLineEndings(String input) {
    return input.replace("\r\n", "\n");
  }
}
