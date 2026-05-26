package com.dlb.chess.test.common.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.enums.GameStatus;
import com.dlb.chess.common.utility.BasicChessUtility;

class TestBasicChessUtility {

  @SuppressWarnings("static-method")
  @Test
  void testSideMoved() {

    assertEquals(Side.BLACK, BasicChessUtility.calculateSideMoved(Side.WHITE, -2));
    assertEquals(Side.WHITE, BasicChessUtility.calculateSideMoved(Side.WHITE, -1));
    assertEquals(Side.BLACK, BasicChessUtility.calculateSideMoved(Side.WHITE, 0));

    assertEquals(Side.WHITE, BasicChessUtility.calculateSideMoved(Side.WHITE, 1));
    assertEquals(Side.BLACK, BasicChessUtility.calculateSideMoved(Side.WHITE, 2));

    assertEquals(Side.WHITE, BasicChessUtility.calculateSideMoved(Side.BLACK, -2));
    assertEquals(Side.BLACK, BasicChessUtility.calculateSideMoved(Side.BLACK, -1));
    assertEquals(Side.WHITE, BasicChessUtility.calculateSideMoved(Side.BLACK, 0));

    assertEquals(Side.BLACK, BasicChessUtility.calculateSideMoved(Side.BLACK, 1));
    assertEquals(Side.WHITE, BasicChessUtility.calculateSideMoved(Side.BLACK, 2));

  }

  @SuppressWarnings("static-method")
  @Test
  void testFullMoveNumberBackwards() {

    // not performed
    assertEquals(9, BasicChessUtility.calculateFullMoveNumber(Side.WHITE, 10, 0));
    assertEquals(9, BasicChessUtility.calculateFullMoveNumber(Side.WHITE, 10, -1));

    assertEquals(8, BasicChessUtility.calculateFullMoveNumber(Side.WHITE, 10, -2));
    assertEquals(8, BasicChessUtility.calculateFullMoveNumber(Side.WHITE, 10, -3));

    assertEquals(7, BasicChessUtility.calculateFullMoveNumber(Side.WHITE, 10, -4));
    assertEquals(7, BasicChessUtility.calculateFullMoveNumber(Side.WHITE, 10, -5));

    assertEquals(10, BasicChessUtility.calculateFullMoveNumber(Side.BLACK, 10, 0));
    assertEquals(9, BasicChessUtility.calculateFullMoveNumber(Side.BLACK, 10, -1));

    assertEquals(9, BasicChessUtility.calculateFullMoveNumber(Side.BLACK, 10, -2));
    assertEquals(8, BasicChessUtility.calculateFullMoveNumber(Side.BLACK, 10, -3));

    assertEquals(8, BasicChessUtility.calculateFullMoveNumber(Side.BLACK, 10, -4));
    assertEquals(7, BasicChessUtility.calculateFullMoveNumber(Side.BLACK, 10, -5));

    // performed
    assertEquals(1, BasicChessUtility.calculateFullMoveNumber(Side.WHITE, 1, 1));
    assertEquals(1, BasicChessUtility.calculateFullMoveNumber(Side.WHITE, 1, 2));

    assertEquals(2, BasicChessUtility.calculateFullMoveNumber(Side.WHITE, 1, 3));
    assertEquals(2, BasicChessUtility.calculateFullMoveNumber(Side.WHITE, 1, 4));

    assertEquals(3, BasicChessUtility.calculateFullMoveNumber(Side.WHITE, 1, 5));
    assertEquals(3, BasicChessUtility.calculateFullMoveNumber(Side.WHITE, 1, 6));

    assertEquals(1, BasicChessUtility.calculateFullMoveNumber(Side.BLACK, 1, 1));
    assertEquals(2, BasicChessUtility.calculateFullMoveNumber(Side.BLACK, 1, 2));

    assertEquals(2, BasicChessUtility.calculateFullMoveNumber(Side.BLACK, 1, 3));
    assertEquals(3, BasicChessUtility.calculateFullMoveNumber(Side.BLACK, 1, 4));

    assertEquals(3, BasicChessUtility.calculateFullMoveNumber(Side.BLACK, 1, 5));
    assertEquals(4, BasicChessUtility.calculateFullMoveNumber(Side.BLACK, 1, 6));
  }

  // ---------------------------------------------------------------------------------------------
  // calculateGameStatus precedence: when two or more termination predicates apply at the same
  // position, the method returns the most-specific status. Pinned ordering: checkmate > stalemate
  // > mutual insufficient material > fivefold > 75-move > single-side insufficient material.
  // ---------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void testCalculateGameStatusPrecedenceDeadPositionBeatsSeventyFiveMove() {
    // KvK position (insufficient material) with halfmove clock at the 75-move threshold.
    // Both isInsufficientMaterial() and isSeventyFiveMove() return true on this board; the
    // returned status is the more specific of the two.
    final Board board = new Board("4k3/8/8/8/8/8/8/4K3 w - - 150 76");
    assertEquals(true, board.isInsufficientMaterial(), "precondition: insufficient material");
    assertEquals(true, board.isSeventyFiveMove(), "precondition: 75-move threshold reached");
    assertEquals(GameStatus.DEAD_POSITION_INSUFFICIENT_MATERIAL, BasicChessUtility.calculateGameStatus(board));
  }

  @SuppressWarnings("static-method")
  @Test
  void testCalculateGameStatusFivefoldFiresWhenAlone() {
    // Drive the board to fivefold by alternating knight moves. Only the fivefold predicate fires.
    final Board board = new Board();
    board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6",
        "Ng1", "Ng8");
    assertEquals(true, board.isFivefoldRepetition(), "precondition: fivefold threshold reached");
    assertEquals(GameStatus.FIVE_FOLD_REPETITION_RULE, BasicChessUtility.calculateGameStatus(board));
  }

  @SuppressWarnings("static-method")
  @Test
  void testCalculateGameStatusSeventyFiveMoveFiresWhenAlone() {
    // FEN at the 75-move threshold with enough material for both sides — only the 75-move
    // predicate fires.
    final Board board = new Board("4k3/8/4P3/8/8/8/2N1B3/3KQ2R w - - 150 76");
    assertEquals(true, board.isSeventyFiveMove(), "precondition: 75-move threshold reached");
    assertEquals(false, board.isInsufficientMaterial(), "precondition: not insufficient material");
    assertEquals(GameStatus.SEVENTY_FIVE_MOVE_RULE, BasicChessUtility.calculateGameStatus(board));
  }
}
