package com.dlb.chess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.bitboard.BitboardPositionUtility;
import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.squares.SlidingAttacksTestOracle;
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;
import com.dlb.chess.bitboard.StaticPositionBridge;

/**
 * Differential test for {@link BitboardPosition#pinnedPieces(Side)}. The reference oracle is built from
 * {@link StaticPosition}: count the opposite-side sliders that attack the king before removing a candidate piece;
 * remove the piece; count again. If the set strictly grew, the piece was pinned (independently of whether the king
 * was already in check). The oracle uses the existing reference slider-attack functions via
 * {@link SlidingAttacksTestOracle}, so my bitboard pin detection is being compared against an independently-derived
 * implementation.
 */
class TestBitboardPositionPins {

  @SuppressWarnings("static-method")
  @Test
  void corpusPinnedPiecesAgree() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnTestCase testCase : testCaseList.list()) {
        final StaticPosition staticPosition = StaticPositionBridge.toStaticPosition(testCase.finalPosition().getBitboardPosition());
        final BitboardPosition bitboardPosition = StaticPositionBridge.fromStaticPosition(staticPosition);
        assertSideAgrees(staticPosition, bitboardPosition, Side.WHITE, testCase);
        assertSideAgrees(staticPosition, bitboardPosition, Side.BLACK, testCase);
      }
    }
  }

  private static void assertSideAgrees(StaticPosition staticPosition, BitboardPosition bitboardPosition, Side side,
      PgnTestCase testCase) {
    final Set<Square> bitboardPinned = BitboardPositionUtility.toSquareSet(bitboardPosition.pinnedPieces(side));
    final Set<Square> referencePinned = referencePinnedPieces(staticPosition, side);
    assertEquals(referencePinned, bitboardPinned,
        side + " pinnedPieces in fixture " + testCase.pgnName());
  }

  private static Set<Square> referencePinnedPieces(StaticPosition staticPosition, Side side) {
    final Set<Square> pinned = new TreeSet<>();
    final Square kingSquare = findKingSquare(staticPosition, side);
    if (kingSquare == Square.NONE) {
      return pinned;
    }
    final Set<Square> attackersBefore = enemySliderAttackersToKing(staticPosition, kingSquare, side);
    for (final Square candidate : Square.REAL) {
      final Piece piece = staticPosition.get(candidate);
      if (piece == Piece.NONE || piece.getSide() != side || piece.getPieceType() == PieceType.KING) {
        continue;
      }
      final StaticPosition modified = staticPosition.createChangedPosition(candidate);
      final Set<Square> attackersAfter = enemySliderAttackersToKing(modified, kingSquare, side);
      if (attackersAfter.size() > attackersBefore.size()) {
        pinned.add(candidate);
      }
    }
    return pinned;
  }

  private static Square findKingSquare(StaticPosition staticPosition, Side side) {
    final Piece kingPiece = side == Side.WHITE ? Piece.WHITE_KING : Piece.BLACK_KING;
    for (final Square square : Square.REAL) {
      if (staticPosition.get(square) == kingPiece) {
        return square;
      }
    }
    return Square.NONE;
  }

  private static Set<Square> enemySliderAttackersToKing(StaticPosition staticPosition, Square kingSquare, Side ownSide) {
    final Set<Square> attackers = new TreeSet<>();
    final Side opp = ownSide.getOppositeSide();
    for (final Square sliderSquare : Square.REAL) {
      final Piece piece = staticPosition.get(sliderSquare);
      if (piece == Piece.NONE || piece.getSide() != opp) {
        continue;
      }
      final PieceType pieceType = piece.getPieceType();
      final Set<Square> attackSet = switch (pieceType) {
        case BISHOP -> SlidingAttacksTestOracle.bishopAttacks(staticPosition, sliderSquare, opp);
        case ROOK -> SlidingAttacksTestOracle.rookAttacks(staticPosition, sliderSquare, opp);
        case QUEEN -> SlidingAttacksTestOracle.queenAttacks(staticPosition, sliderSquare, opp);
        default -> null;
      };
      if (attackSet != null && attackSet.contains(kingSquare)) {
        attackers.add(sliderSquare);
      }
    }
    return attackers;
  }

  @SuppressWarnings("static-method")
  @Test
  void initialPositionHasNoPins() {
    assertEquals(0L, BitboardPosition.INITIAL_POSITION.pinnedPieces(Side.WHITE));
    assertEquals(0L, BitboardPosition.INITIAL_POSITION.pinnedPieces(Side.BLACK));
  }

  @SuppressWarnings("static-method")
  @Test
  void emptyPositionReturnsZero() {
    assertEquals(0L, BitboardPosition.EMPTY_POSITION.pinnedPieces(Side.WHITE));
    assertEquals(0L, BitboardPosition.EMPTY_POSITION.pinnedPieces(Side.BLACK));
  }

  @SuppressWarnings("static-method")
  @Test
  void noneSideThrows() {
    assertThrows(IllegalArgumentException.class, () -> BitboardPosition.INITIAL_POSITION.pinnedPieces(Side.NONE));
    assertThrows(IllegalArgumentException.class, () -> BitboardPosition.INITIAL_POSITION.pinRay(Square.E2, Side.NONE));
    assertThrows(IllegalArgumentException.class, () -> BitboardPosition.INITIAL_POSITION.pinRay(Square.NONE, Side.WHITE));
  }
}
