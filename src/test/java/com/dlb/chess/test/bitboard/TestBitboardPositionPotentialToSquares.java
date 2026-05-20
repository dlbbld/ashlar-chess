package com.dlb.chess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.bitboard.BitboardPositionUtility;
import com.dlb.chess.board.Board;
import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.squares.AbstractPotentialToSquares;
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;
import com.dlb.chess.bitboard.StaticPositionBridge;

/**
 * Differential test: {@link BitboardPosition#potentialToSquares(Square, long)} must agree set-equal with
 * {@link AbstractPotentialToSquares#calculatePotentialToSquare(StaticPosition, Square, Side, Square)} for every own
 * piece on every fixture in the corpus, for both sides. This pins the bitboard pseudo-legal-target surface used by
 * the SAN error-reporting layer against the StaticPosition-backed reference.
 *
 * <p>
 * Covers all six piece types via the side-to-move's pieces and the side-not-to-move's pieces (so the EP target square
 * is only meaningful for the side-to-move's pawns; for the other side it is passed as {@code 0L} since the EP
 * opportunity does not apply to them).
 */
class TestBitboardPositionPotentialToSquares {

  @SuppressWarnings("static-method")
  @Test
  void corpusPotentialToSquaresAgreeForBothSides() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnTestCase testCase : testCaseList.list()) {
        final Board board = testCase.finalPosition();
        final StaticPosition staticPosition = StaticPositionBridge.toStaticPosition(board.getBitboardPosition());
        final BitboardPosition bitboardPosition = StaticPositionBridge.fromStaticPosition(staticPosition);
        final Side havingMove = board.getHavingMove();
        final Square epTarget = board.getEnPassantCaptureTargetSquare();
        final long epBit = epTarget == Square.NONE ? 0L : 1L << epTarget.ordinal();

        assertSidePotentialToSquaresAgree(staticPosition, bitboardPosition, havingMove, epTarget, epBit, testCase);
        // The non-side-to-move never has an EP opportunity in a real position; pass NONE / 0L for them.
        assertSidePotentialToSquaresAgree(staticPosition, bitboardPosition, havingMove.getOppositeSide(), Square.NONE,
            0L, testCase);
      }
    }
  }

  private static void assertSidePotentialToSquaresAgree(StaticPosition staticPosition, BitboardPosition bitboardPosition,
      Side side, Square epTarget, long epBit, PgnTestCase testCase) {
    for (final Square fromSquare : Square.REAL) {
      final Piece piece = staticPosition.get(fromSquare);
      if (piece == Piece.NONE || piece.getSide() != side) {
        continue;
      }
      final Set<Square> bitboardTargets = bitboardPosition.potentialToSquares(fromSquare, epBit);
      final Set<Square> referenceTargets = AbstractPotentialToSquares.calculatePotentialToSquare(staticPosition,
          epTarget, side, fromSquare);
      assertEquals(referenceTargets, bitboardTargets, side + " " + piece.getPieceType() + " potential-to-squares from "
          + fromSquare.getName() + " in fixture " + testCase.pgnName());
    }
  }
}
