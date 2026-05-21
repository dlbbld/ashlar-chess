package com.dlb.chess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import com.dlb.chess.bitboard.BishopMoves;
import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.bitboard.BitboardPositionUtility;
import com.dlb.chess.bitboard.KingMoves;
import com.dlb.chess.bitboard.KnightMoves;
import com.dlb.chess.bitboard.QueenMoves;
import com.dlb.chess.bitboard.RookMoves;
import com.dlb.chess.bitboard.StaticPositionBridge;
import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.squares.KingNonCastlingEmptyBoardSquares;
import com.dlb.chess.squares.KnightEmptyBoardSquares;
import com.dlb.chess.squares.SlidingAttacksTestOracle;
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

/**
 * Differential test for the per-piece pseudo-legal target generators ({@link KnightMoves}, {@link KingMoves},
 * {@link BishopMoves}, {@link RookMoves}, {@link QueenMoves}). The reference is the corresponding attack set
 * (already includes own pieces as "defended" / blocker squares) with own-piece-occupied squares removed —
 * exactly what the bitboard generators compute as {@code attacks(...) & ~ownPieces}.
 */
class TestPseudoLegalMoves {

  @SuppressWarnings("static-method")
  @Test
  void corpusKnightTargetsAgree() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnTestCase testCase : testCaseList.list()) {
        final StaticPosition staticPosition = StaticPositionBridge.toStaticPosition(testCase.finalPosition().getBitboardPosition());
        final BitboardPosition bitboardPosition = StaticPositionBridge.fromStaticPosition(staticPosition);
        assertKnightTargetsAgree(staticPosition, bitboardPosition, Side.WHITE, testCase);
        assertKnightTargetsAgree(staticPosition, bitboardPosition, Side.BLACK, testCase);
      }
    }
  }

  private static void assertKnightTargetsAgree(StaticPosition staticPosition, BitboardPosition bitboardPosition,
      Side side, PgnTestCase testCase) {
    final long knights = side == Side.WHITE ? bitboardPosition.whiteKnights() : bitboardPosition.blackKnights();
    final long ownPieces = bitboardPosition.occupied(side);
    long remaining = knights;
    while (remaining != 0L) {
      final Square fromSquare = Nulls.get(Square.REAL, Long.numberOfTrailingZeros(remaining));
      final Set<Square> bitboardTargets = BitboardPositionUtility.toSquareSet(KnightMoves.targets(fromSquare, ownPieces));
      final Set<Square> referenceTargets = withoutOwnPieces(KnightEmptyBoardSquares.getKnightSquares(fromSquare),
          staticPosition, side);
      assertEquals(referenceTargets, bitboardTargets,
          side + " knight targets from " + fromSquare.getName() + " in fixture " + testCase.pgnName());
      remaining &= remaining - 1L;
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void corpusKingTargetsAgree() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnTestCase testCase : testCaseList.list()) {
        final StaticPosition staticPosition = StaticPositionBridge.toStaticPosition(testCase.finalPosition().getBitboardPosition());
        final BitboardPosition bitboardPosition = StaticPositionBridge.fromStaticPosition(staticPosition);
        assertKingTargetsAgree(staticPosition, bitboardPosition, Side.WHITE, testCase);
        assertKingTargetsAgree(staticPosition, bitboardPosition, Side.BLACK, testCase);
      }
    }
  }

  private static void assertKingTargetsAgree(StaticPosition staticPosition, BitboardPosition bitboardPosition,
      Side side, PgnTestCase testCase) {
    final long kings = side == Side.WHITE ? bitboardPosition.whiteKings() : bitboardPosition.blackKings();
    final long ownPieces = bitboardPosition.occupied(side);
    long remaining = kings;
    while (remaining != 0L) {
      final Square fromSquare = Nulls.get(Square.REAL, Long.numberOfTrailingZeros(remaining));
      final Set<Square> bitboardTargets = BitboardPositionUtility.toSquareSet(KingMoves.targets(fromSquare, ownPieces));
      final Set<Square> referenceTargets = withoutOwnPieces(
          KingNonCastlingEmptyBoardSquares.getKingSquares(fromSquare), staticPosition, side);
      assertEquals(referenceTargets, bitboardTargets,
          side + " king targets from " + fromSquare.getName() + " in fixture " + testCase.pgnName());
      remaining &= remaining - 1L;
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void corpusBishopTargetsAgree() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnTestCase testCase : testCaseList.list()) {
        final StaticPosition staticPosition = StaticPositionBridge.toStaticPosition(testCase.finalPosition().getBitboardPosition());
        final BitboardPosition bitboardPosition = StaticPositionBridge.fromStaticPosition(staticPosition);
        assertBishopTargetsAgree(staticPosition, bitboardPosition, Side.WHITE, testCase);
        assertBishopTargetsAgree(staticPosition, bitboardPosition, Side.BLACK, testCase);
      }
    }
  }

  private static void assertBishopTargetsAgree(StaticPosition staticPosition, BitboardPosition bitboardPosition,
      Side side, PgnTestCase testCase) {
    final long bishops = side == Side.WHITE ? bitboardPosition.whiteBishops() : bitboardPosition.blackBishops();
    final long ownPieces = bitboardPosition.occupied(side);
    final long occupied = bitboardPosition.occupied();
    long remaining = bishops;
    while (remaining != 0L) {
      final int squareOrdinal = Long.numberOfTrailingZeros(remaining);
      final Square fromSquare = Nulls.get(Square.REAL, squareOrdinal);
      final Set<Square> bitboardTargets = BitboardPositionUtility
          .toSquareSet(BishopMoves.targets(squareOrdinal, occupied, ownPieces));
      final Set<Square> referenceTargets = withoutOwnPieces(
          SlidingAttacksTestOracle.bishopAttacks(staticPosition, fromSquare, side), staticPosition, side);
      assertEquals(referenceTargets, bitboardTargets,
          side + " bishop targets from " + fromSquare.getName() + " in fixture " + testCase.pgnName());
      remaining &= remaining - 1L;
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void corpusRookTargetsAgree() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnTestCase testCase : testCaseList.list()) {
        final StaticPosition staticPosition = StaticPositionBridge.toStaticPosition(testCase.finalPosition().getBitboardPosition());
        final BitboardPosition bitboardPosition = StaticPositionBridge.fromStaticPosition(staticPosition);
        assertRookTargetsAgree(staticPosition, bitboardPosition, Side.WHITE, testCase);
        assertRookTargetsAgree(staticPosition, bitboardPosition, Side.BLACK, testCase);
      }
    }
  }

  private static void assertRookTargetsAgree(StaticPosition staticPosition, BitboardPosition bitboardPosition,
      Side side, PgnTestCase testCase) {
    final long rooks = side == Side.WHITE ? bitboardPosition.whiteRooks() : bitboardPosition.blackRooks();
    final long ownPieces = bitboardPosition.occupied(side);
    final long occupied = bitboardPosition.occupied();
    long remaining = rooks;
    while (remaining != 0L) {
      final int squareOrdinal = Long.numberOfTrailingZeros(remaining);
      final Square fromSquare = Nulls.get(Square.REAL, squareOrdinal);
      final Set<Square> bitboardTargets = BitboardPositionUtility
          .toSquareSet(RookMoves.targets(squareOrdinal, occupied, ownPieces));
      final Set<Square> referenceTargets = withoutOwnPieces(
          SlidingAttacksTestOracle.rookAttacks(staticPosition, fromSquare, side), staticPosition, side);
      assertEquals(referenceTargets, bitboardTargets,
          side + " rook targets from " + fromSquare.getName() + " in fixture " + testCase.pgnName());
      remaining &= remaining - 1L;
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void corpusQueenTargetsAgree() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnTestCase testCase : testCaseList.list()) {
        final StaticPosition staticPosition = StaticPositionBridge.toStaticPosition(testCase.finalPosition().getBitboardPosition());
        final BitboardPosition bitboardPosition = StaticPositionBridge.fromStaticPosition(staticPosition);
        assertQueenTargetsAgree(staticPosition, bitboardPosition, Side.WHITE, testCase);
        assertQueenTargetsAgree(staticPosition, bitboardPosition, Side.BLACK, testCase);
      }
    }
  }

  private static void assertQueenTargetsAgree(StaticPosition staticPosition, BitboardPosition bitboardPosition,
      Side side, PgnTestCase testCase) {
    final long queens = side == Side.WHITE ? bitboardPosition.whiteQueens() : bitboardPosition.blackQueens();
    final long ownPieces = bitboardPosition.occupied(side);
    final long occupied = bitboardPosition.occupied();
    long remaining = queens;
    while (remaining != 0L) {
      final int squareOrdinal = Long.numberOfTrailingZeros(remaining);
      final Square fromSquare = Nulls.get(Square.REAL, squareOrdinal);
      final Set<Square> bitboardTargets = BitboardPositionUtility
          .toSquareSet(QueenMoves.targets(squareOrdinal, occupied, ownPieces));
      final Set<Square> referenceTargets = withoutOwnPieces(
          SlidingAttacksTestOracle.queenAttacks(staticPosition, fromSquare, side), staticPosition, side);
      assertEquals(referenceTargets, bitboardTargets,
          side + " queen targets from " + fromSquare.getName() + " in fixture " + testCase.pgnName());
      remaining &= remaining - 1L;
    }
  }

  private static Set<Square> withoutOwnPieces(Set<Square> input, StaticPosition staticPosition, Side side) {
    final Set<Square> result = new TreeSet<>();
    for (final Square square : input) {
      if (!staticPosition.isOwnPiece(square, side)) {
        result.add(square);
      }
    }
    return result;
  }
}
