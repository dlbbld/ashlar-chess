package com.dlb.chess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.bitboard.StaticPositionBridge;
import com.dlb.chess.board.Board;
import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.common.utility.StaticPositionUtility;
import com.dlb.chess.model.LegalMove;
import com.dlb.chess.test.model.PgnFen;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

/**
 * The second spine assertion: for every fixture x every legal move, the bitboard's
 * {@link BitboardPosition#afterMove(MoveSpecification, Side)} must produce the same piece placement as the
 * reference {@code StaticPositionUtility.createPositionAfterMove}. Covers all move shapes (normal, capture,
 * en-passant, all four promotion targets, both castling sides) on every position the corpus walks through.
 */
class TestBitboardPositionAfterMove {

  @SuppressWarnings("static-method")
  @Test
  void corpusEveryLegalMoveAfterMatchesReference() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        final Board board = testCase.finalPosition();
        final StaticPosition staticPosition = StaticPositionBridge.toStaticPosition(board.getBitboardPosition());
        final BitboardPosition bitboardPosition = StaticPositionBridge.fromStaticPosition(staticPosition);
        final Side havingMove = board.getHavingMove();

        for (final LegalMove legalMove : board.getLegalMoves()) {
          final MoveSpecification moveSpec = legalMove.moveSpecification();
          final StaticPosition referenceAfter = StaticPositionUtility.createPositionAfterMove(staticPosition,
              havingMove, moveSpec);
          final BitboardPosition expectedAfter = StaticPositionBridge.fromStaticPosition(referenceAfter);
          final BitboardPosition bitboardAfter = bitboardPosition.afterMove(moveSpec, havingMove);
          assertEquals(expectedAfter, bitboardAfter,
              "afterMove disagreement for " + moveSpec + " in fixture " + testCase.pgnName());
        }
      }
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void initialPositionAfterEFourMatchesReference() {
    final MoveSpecification e2e4 = new MoveSpecification(Square.E2, Square.E4);
    final StaticPosition referenceAfter = StaticPositionUtility
        .createPositionAfterMove(StaticPosition.INITIAL_POSITION, Side.WHITE, e2e4);
    final BitboardPosition expectedAfter = StaticPositionBridge.fromStaticPosition(referenceAfter);
    final BitboardPosition bitboardAfter = BitboardPosition.INITIAL_POSITION.afterMove(e2e4, Side.WHITE);
    assertEquals(expectedAfter, bitboardAfter);
  }

  @SuppressWarnings("static-method")
  @Test
  void noneSideThrows() {
    assertThrows(IllegalArgumentException.class, () -> BitboardPosition.INITIAL_POSITION
        .afterMove(new MoveSpecification(Square.E2, Square.E4), Side.NONE));
  }
}
