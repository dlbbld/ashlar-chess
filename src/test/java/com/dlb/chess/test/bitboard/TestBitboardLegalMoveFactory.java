package com.dlb.chess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.dlb.chess.bitboard.BitboardLegalMoveFactory;
import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.bitboard.StaticPositionBridge;
import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.model.LegalMove;
import com.dlb.chess.moves.AbstractLegalMoves;
import com.dlb.chess.test.model.PgnFen;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

/**
 * Differential test for {@link BitboardLegalMoveFactory#toLegalMove}: for every legal move on every corpus fixture,
 * the converter applied to the move's {@link MoveSpecification} must reproduce the reference's {@link LegalMove}
 * record (same moving piece, captured piece, and {@link com.dlb.chess.model.LegalMoveKind}).
 *
 * <p>
 * The reference is {@link AbstractLegalMoves#calculateLegalMoves} directly - NOT
 * {@code board.getLegalMoves()}, which since Switchover Step 2.2 ({@code a235d363}) is produced via this very
 * factory. Using {@code board.getLegalMoves()} as the oracle here would make the test self-referential.
 */
class TestBitboardLegalMoveFactory {

  @SuppressWarnings("static-method")
  @Test
  void corpusEveryLegalMoveRoundTripsThroughFactory() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        final Board board = testCase.finalPosition();
        final Side havingMove = board.getHavingMove();
        final BitboardPosition bitboardPosition = board.getBitboardPosition();
        for (final LegalMove referenceMove : AbstractLegalMoves.calculateLegalMoves(StaticPositionBridge.toStaticPosition(board.getBitboardPosition()),
            havingMove, board.getCastlingRight(havingMove), board.getEnPassantCaptureTargetSquare())) {
          final LegalMove converted = BitboardLegalMoveFactory.toLegalMove(bitboardPosition,
              referenceMove.moveSpecification(), havingMove);
          assertEquals(referenceMove, converted,
              "converted LegalMove disagrees with reference for "
                  + referenceMove.moveSpecification() + " in fixture " + testCase.pgnName());
        }
      }
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void initialPositionE2E4IsPawnTwoSquareAdvance() {
    final MoveSpecification e2e4 = new MoveSpecification(Square.E2, Square.E4);
    final LegalMove converted = BitboardLegalMoveFactory.toLegalMove(BitboardPosition.INITIAL_POSITION, e2e4,
        Side.WHITE);
    assertEquals(com.dlb.chess.model.LegalMoveKind.PAWN_TWO_SQUARE_ADVANCE, converted.kind());
    assertEquals(com.dlb.chess.board.enums.Piece.WHITE_PAWN, converted.movingPiece());
    assertEquals(com.dlb.chess.board.enums.Piece.NONE, converted.pieceCaptured());
  }

  @SuppressWarnings("static-method")
  @Test
  void noneSideThrows() {
    assertThrows(IllegalArgumentException.class, () -> BitboardLegalMoveFactory.toLegalMove(
        BitboardPosition.INITIAL_POSITION, new MoveSpecification(Square.E2, Square.E4), Side.NONE));
  }
}
