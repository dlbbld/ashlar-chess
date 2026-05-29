// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.bitboard.BitboardPositionUtility;
import io.github.dlbbld.ashlarchess.bitboard.StaticPositionBridge;
import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.squares.KingNonCastlingEmptyBoardSquares;
import io.github.dlbbld.ashlarchess.squares.KnightEmptyBoardSquares;
import io.github.dlbbld.ashlarchess.squares.PawnDiagonalSquares;
import io.github.dlbbld.ashlarchess.squares.SlidingAttacksTestOracle;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

/**
 * Differential test for {@link BitboardPosition#attackersTo(Square, Side)}. There is no direct production counterpart,
 * so the reference is derived: walk every own-side piece and ask whether its per-piece attack set contains the target
 * square, using the existing reference attack functions from {@code io.github.dlbbld.ashlarchess.squares}.
 */
class TestBitboardPositionAttackersTo {

  @SuppressWarnings("static-method")
  @Test
  void corpusAgreesAcrossSquaresAndSides() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        final StaticPosition staticPosition = StaticPositionBridge
            .toStaticPosition(testCase.finalPosition().getBitboardPosition());
        final BitboardPosition bitboardPosition = StaticPositionBridge.fromStaticPosition(staticPosition);
        for (final Square target : Square.REAL) {
          assertSideAgrees(staticPosition, bitboardPosition, target, Side.WHITE, testCase);
          assertSideAgrees(staticPosition, bitboardPosition, target, Side.BLACK, testCase);
        }
      }
    }
  }

  private static void assertSideAgrees(StaticPosition staticPosition, BitboardPosition bitboardPosition, Square target,
      Side side, PgnFen testCase) {
    final Set<Square> bitboardAttackers = BitboardPositionUtility
        .toSquareSet(bitboardPosition.attackersTo(target, side));
    final Set<Square> referenceAttackers = referenceAttackersTo(staticPosition, target, side);
    assertEquals(referenceAttackers, bitboardAttackers,
        side + " attackersTo(" + target.getName() + ") in fixture " + testCase.pgnName());
  }

  private static Set<Square> referenceAttackersTo(StaticPosition staticPosition, Square target, Side side) {
    final Set<Square> attackers = new TreeSet<>();
    for (final Square from : Square.REAL) {
      if (!staticPosition.isOwnPiece(from, side)) {
        continue;
      }
      final Piece piece = staticPosition.get(from);
      final Set<Square> pieceAttacks = switch (piece.getPieceType()) {
        case PAWN -> PawnDiagonalSquares.getPawnDiagonalSquares(side, from);
        case KNIGHT -> KnightEmptyBoardSquares.getKnightSquares(from);
        case BISHOP -> SlidingAttacksTestOracle.bishopAttacks(staticPosition, from, side);
        case ROOK -> SlidingAttacksTestOracle.rookAttacks(staticPosition, from, side);
        case QUEEN -> SlidingAttacksTestOracle.queenAttacks(staticPosition, from, side);
        case KING -> KingNonCastlingEmptyBoardSquares.getKingSquares(from);
        case NONE -> throw new IllegalStateException();
        default -> throw new IllegalArgumentException();
      };
      if (pieceAttacks.contains(target)) {
        attackers.add(from);
      }
    }
    return attackers;
  }

  @SuppressWarnings("static-method")
  @Test
  void noneSquareThrows() {
    assertThrows(IllegalArgumentException.class,
        () -> BitboardPosition.INITIAL_POSITION.attackersTo(Square.NONE, Side.WHITE));
  }

  @SuppressWarnings("static-method")
  @Test
  void noneSideThrows() {
    assertThrows(IllegalArgumentException.class,
        () -> BitboardPosition.INITIAL_POSITION.attackersTo(Square.E4, Side.NONE));
  }
}
