package com.dlb.chess.test.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.model.UciMove;
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.unwinnability.UnwinnabilityFullAnalysis;
import com.dlb.chess.unwinnability.UnwinnabilityFullVerdict;
import com.dlb.chess.unwinnability.UnwinnabilityQuickAnalysis;
import com.dlb.chess.unwinnability.UnwinnabilityQuickVerdict;
import com.dlb.chess.unwinnability.UnwinnableFullAnalyzer;
import com.dlb.chess.unwinnability.UnwinnableQuickAnalyzer;

/**
 * Pins the EXACT UCI mate-line that the quick and full helpmate analyzers return on selected fixtures. The
 * companion tests {@link TestUnwinnabilityQuickHelpMateIsHelpMate} and {@link TestUnwinnabilityFullHelpMateIsHelpMate}
 * verify that the analyzer's mate-line, when played out, actually reaches checkmate — a semantic check that
 * still passes if the analyzer picks a different but equally valid mate-line. The 12.1.0 helpmate hot-path
 * release refactors the search internals (mutable make/unmake, per-ply move buffers, exact structural transposition
 * key); any of those changes could in principle cause the analyzer to enumerate moves in a different order and
 * surface a different mate-line. This regression test fails loudly when that happens, so the change shows up at
 * review time rather than as a silent behavioral drift on downstream consumers.
 */
class TestUnwinnabilityHelpMateLineRegression {

  @SuppressWarnings("static-method")
  @Test
  void quickMateLineFixture_lichess_pUEeHLfu() {
    assertQuickMateLine("lichess_pUEeHLfu.pgn", Side.WHITE, List.of("g3f2", "h4h3", "g6h5"));
  }

  @SuppressWarnings("static-method")
  @Test
  void quickMateLineFixture_lichess_UNX9jAKK() {
    assertQuickMateLine("lichess_UNX9jAKK.pgn", Side.BLACK, List.of("f7e6", "h3h4", "c4c5"));
  }

  @SuppressWarnings("static-method")
  @Test
  void quickMateLineFixture_lichess_sMv8Hh43() {
    assertQuickMateLine("lichess_sMv8Hh43.pgn", Side.BLACK, List.of("c4c5"));
  }

  @SuppressWarnings("static-method")
  @Test
  void quickMateLineFixture_01_forced_checkmate() {
    assertQuickMateLine("01_forced_checkmate.pgn", Side.WHITE,
        List.of("a7g7", "f6g7", "h8g8", "f3f4", "a6a5", "f4f5", "a5a4", "f5f6", "a4a3", "f6f7"));
  }

  @SuppressWarnings("static-method")
  @Test
  void fullMateLineFixture_lichess_pUEeHLfu() {
    assertFullMateLine("lichess_pUEeHLfu.pgn", Side.WHITE, List.of("g3f2", "h4h3", "g6h5"));
  }

  private static void assertQuickMateLine(String pgnName, Side winner, List<String> expectedUciTexts) {
    final PgnTestCase testCase = PgnTestCaseCatalog.findTestCase(pgnName);
    final Board board = testCase.finalPosition();
    final UnwinnabilityQuickAnalysis analysis = UnwinnableQuickAnalyzer.unwinnableQuick(board, winner);
    assertEquals(UnwinnabilityQuickVerdict.WINNABLE, analysis.verdict(),
        "Quick analyzer must return WINNABLE for a helpmate-bearing fixture: " + pgnName);
    final List<String> actualUciTexts = analysis.mateLine().stream().map(UciMove::text).toList();
    assertEquals(expectedUciTexts, actualUciTexts, "Quick mate-line regression for " + pgnName);
  }

  private static void assertFullMateLine(String pgnName, Side winner, List<String> expectedUciTexts) {
    final PgnTestCase testCase = PgnTestCaseCatalog.findTestCase(pgnName);
    final Board board = testCase.finalPosition();
    final UnwinnabilityFullAnalysis analysis = UnwinnableFullAnalyzer.unwinnableFull(board, winner);
    assertEquals(UnwinnabilityFullVerdict.WINNABLE, analysis.verdict(),
        "Full analyzer must return WINNABLE for a helpmate-bearing fixture: " + pgnName);
    final List<String> actualUciTexts = analysis.mateLine().stream().map(UciMove::text).toList();
    assertEquals(expectedUciTexts, actualUciTexts, "Full mate-line regression for " + pgnName);
  }
}
