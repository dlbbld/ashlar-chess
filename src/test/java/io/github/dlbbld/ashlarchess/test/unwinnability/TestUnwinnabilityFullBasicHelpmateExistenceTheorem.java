// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.UciMove;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.internal.UciMoveUtility;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;
import io.github.dlbbld.ashlarchess.unwinnability.BasicHelpmateExistenceTheorem;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityFullAnalysis;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityFullVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnableFullAnalyzer;

// The basic-helpmate-existence theorem as a test oracle (since 22.0.0 it no longer short-circuits the production
// analyzer): over the curated elementary-material corpus the theorem decides every position by its proven
// finite-state statement, and the FUN22 engine must agree - in particular it must answer UNWINNABLE wherever the
// theorem proves unwinnability (the forced-capture case), and it must exhibit a concrete helpmate line wherever the
// theorem guarantees a helpmate exists.
class TestUnwinnabilityFullBasicHelpmateExistenceTheorem {

  @SuppressWarnings("static-method")
  @Test
  void fullVerdictAgreesWithTheTheoremOracle() {
    int theoremWinnable = 0;
    int theoremUnwinnable = 0;
    for (final PgnFen testCase : PgnTestCaseCatalog.getTestList(PgnTest.CHA_BASIC_HELPMATE_EXISTENCE_THEOREM).list()) {
      final Board board = testCase.finalPosition();
      final UnwinnabilityFullAnalysis analysis = UnwinnableFullAnalyzer.unwinnableFull(board, Side.WHITE);
      switch (BasicHelpmateExistenceTheorem.decide(board, Side.WHITE)) {
        case WINNABLE:
          theoremWinnable++;
          assertEquals(UnwinnabilityFullVerdict.WINNABLE, analysis.verdict(), testCase.pgnName());
          assertFalse(analysis.mateLine().isEmpty(),
              testCase.pgnName() + ": the theorem guarantees a helpmate; the search must exhibit one");
          assertHelpmateLine(testCase.finalFen(), analysis.mateLine(), testCase.pgnName());
          break;
        case UNWINNABLE:
          theoremUnwinnable++;
          assertEquals(UnwinnabilityFullVerdict.UNWINNABLE, analysis.verdict(), testCase.pgnName());
          break;
        case NOT_APPLICABLE:
          throw new AssertionError(testCase.pgnName() + ": every curated fixture is a covered, ongoing position");
        default:
          throw new IllegalArgumentException();
      }
    }
    // Both theorem branches must actually be exercised by the corpus.
    assertTrue(theoremWinnable > 0 && theoremUnwinnable > 0,
        "corpus lost a theorem branch: winnable=" + theoremWinnable + " unwinnable=" + theoremUnwinnable);
  }

  /** The exhibited line, replayed from the position, must end in a checkmate delivered by White. */
  private static void assertHelpmateLine(String fen, List<UciMove> mateLine, String pgnName) {
    final Board board = Board.fromFenStrict(fen);
    for (final UciMove uciMove : mateLine) {
      board.move(UciMoveUtility.toMoveSpecification(board, uciMove));
    }
    assertEquals(Side.BLACK, board.getSideToMove(), pgnName + ": the mate must be delivered by White");
    assertTrue(board.isCheckmate(), pgnName + ": the exhibited line must end in checkmate");
  }
}
