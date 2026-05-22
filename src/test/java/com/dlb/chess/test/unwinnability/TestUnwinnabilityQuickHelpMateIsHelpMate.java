package com.dlb.chess.test.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.ucimove.utility.UciMoveUtility;
import com.dlb.chess.model.UciMove;
import com.dlb.chess.unwinnability.UnwinnabilityQuickAnalysis;
import com.dlb.chess.unwinnability.UnwinnabilityQuickVerdict;
import com.dlb.chess.unwinnability.UnwinnableQuickAnalyzer;

class TestUnwinnabilityQuickHelpMateIsHelpMate {

  @SuppressWarnings("static-method")
  @Test
  void mateLinesActuallyCheckmate() {
    final String fen = "8/8/8/8/8/5k2/2p5/4K3 w - - 1 50";
    final Side winner = Side.BLACK;
    final Board board = new Board(fen, false);
    final UnwinnabilityQuickAnalysis analysis = UnwinnableQuickAnalyzer.unwinnableQuick(board, winner);
    assertEquals(UnwinnabilityQuickVerdict.WINNABLE, analysis.verdict());
    assertFalse(analysis.mateLine().isEmpty());
    assertHelpmateLine(fen, winner, analysis.mateLine());
  }

  private static void assertHelpmateLine(String fen, Side winner, List<UciMove> mateLine) {
    final var board = new Board(fen, false);
    for (final UciMove uciMove : mateLine) {
      board.move(UciMoveUtility.convertUciMoveToMoveSpecification(board, uciMove));
    }
    assertEquals(winner.getOppositeSide(), board.getHavingMove());
    assertTrue(board.isCheckmate());
  }

}
