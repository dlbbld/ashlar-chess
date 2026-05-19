package com.dlb.chess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.bitboard.BitboardPositionUtility;
import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

/**
 * Steps 1.1 + 1.2 of the switchover release: {@link Board#getBitboardPosition()} must equal a fresh
 * {@code BitboardPositionUtility.fromStaticPosition(board.getStaticPosition())} (a) on initial position, (b) at the
 * final position of every fixture, and (c) at every intermediate state of a synthetic move + unmove sequence. The
 * synthetic walk validates that the bitboard cache stays in sync with the dynamic-position list through every
 * {@code move()} append and {@code unmove()} pop.
 */
class TestBoardGetBitboardPosition {

  @SuppressWarnings("static-method")
  @Test
  void initialPositionMatchesBitboardConstant() {
    final Board board = new Board(false);
    assertEquals(BitboardPosition.INITIAL_POSITION, board.getBitboardPosition());
  }

  @SuppressWarnings("static-method")
  @Test
  void corpusFinalPositionsMatchFromStaticPosition() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnTestCase testCase : testCaseList.list()) {
        final Board board = testCase.finalPosition();
        final BitboardPosition expected = BitboardPositionUtility.fromStaticPosition(board.getStaticPosition());
        assertEquals(expected, board.getBitboardPosition(),
            "Board.getBitboardPosition disagrees with fromStaticPosition in fixture " + testCase.pgnName());
      }
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void handPlayedMoveAndUnmoveKeepsBitboardInSync() throws Exception {
    // Synthetic sequence covering both move() and unmove(): cache must equal the freshly-computed bitboard at every
    // intermediate state. Hand-played five-move opening (e4, e5, Nf3, Nc6, Bb5) plus full unmove back to initial.
    final Board board = new Board(false);
    final List<List<Square>> listMoveSquareList = new ArrayList<>();
    listMoveSquareList.add(Nulls.asList(Square.E2, Square.E4));
    listMoveSquareList.add(Nulls.asList(Square.E7, Square.E5));
    listMoveSquareList.add(Nulls.asList(Square.G1, Square.F3));
    listMoveSquareList.add(Nulls.asList(Square.B8, Square.C6));
    listMoveSquareList.add(Nulls.asList(Square.F1, Square.B5));
    for (final List<Square> moveSquareList : listMoveSquareList) {
      final Square squareFrom = Nulls.get(moveSquareList, 0);
      final Square squareTo = Nulls.get(moveSquareList, 1);
      board.move(new MoveSpecification(squareFrom, squareTo));
      assertEquals(BitboardPositionUtility.fromStaticPosition(board.getStaticPosition()), board.getBitboardPosition(),
          "cache out of sync after move " + squareFrom.getName() + "-" + squareTo.getName());
    }
    while (!board.isFirstMove()) {
      board.unmove();
      assertEquals(BitboardPositionUtility.fromStaticPosition(board.getStaticPosition()), board.getBitboardPosition(),
          "cache out of sync after unmove");
    }
    assertEquals(BitboardPosition.INITIAL_POSITION, board.getBitboardPosition(),
        "cache should be back to initial after full unmove");
  }
}
