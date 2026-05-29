// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.bitboard.StaticPositionBridge;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.CastlingMove;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.moves.AbstractLegalMoves;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

/**
 * The spine assertion of the bitboard release: for every fixture in the corpus, the bitboard's
 * {@link BitboardPosition#legalMoves(Side, long)} (non-castling only) must agree set-equal with
 * {@link AbstractLegalMoves#calculateLegalMoves} (the StaticPosition-backed reference) after filtering out castling
 * moves. Every piece type, pin filtering, check evasion, double check, EP including the rank-pin edge case, and
 * promotion expansion are exercised together.
 *
 * <p>
 * The reference is {@code AbstractLegalMoves.calculateLegalMoves} directly - NOT {@code board.getLegalMoves()}, which
 * since Switchover Step 2.2 ({@code a235d363}) is produced by the bitboard pipeline itself. Using
 * {@code board.getLegalMoves()} as the oracle here would make the test self-referential and unable to detect
 * bitboard-side regressions. The StaticPosition-backed reference path must stay independent of the bitboard until the
 * relocation phase moves it to {@code src/test/}.
 *
 * <p>
 * Castling moves are excluded because they live on {@link Board} with the castling-rights state; the bitboard layer is
 * intentionally castling-stateless.
 */
class TestBitboardPositionLegalMoves {

  @SuppressWarnings("static-method")
  @Test
  void corpusLegalMovesAgreeForSideToMove() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        final Board board = testCase.finalPosition();
        final StaticPosition staticPosition = StaticPositionBridge.toStaticPosition(board.getBitboardPosition());
        final BitboardPosition bitboardPosition = StaticPositionBridge.fromStaticPosition(staticPosition);
        final Side havingMove = board.getHavingMove();
        final Square boardEpTarget = board.getEnPassantCaptureTargetSquare();
        final long enPassantBit = boardEpTarget == Square.NONE ? 0L : 1L << boardEpTarget.ordinal();

        final Set<MoveSpecification> bitboardMoves = bitboardPosition.legalMoves(havingMove, enPassantBit);
        final Set<MoveSpecification> referenceNonCastlingMoves = staticPositionReferenceNonCastlingMoves(board,
            staticPosition, havingMove, boardEpTarget);

        assertEquals(referenceNonCastlingMoves, bitboardMoves,
            "legalMoves disagreement for " + havingMove + " in fixture " + testCase.pgnName());
      }
    }
  }

  private static Set<MoveSpecification> staticPositionReferenceNonCastlingMoves(Board board,
      StaticPosition staticPosition, Side havingMove, Square enPassantTargetSquare) {
    final Set<MoveSpecification> result = new TreeSet<>();
    for (final LegalMove legalMove : AbstractLegalMoves.calculateLegalMoves(staticPosition, havingMove,
        board.getCastlingRight(havingMove), enPassantTargetSquare)) {
      final MoveSpecification spec = legalMove.moveSpecification();
      if (spec.castlingMove() == CastlingMove.NONE) {
        result.add(spec);
      }
    }
    return result;
  }

  @SuppressWarnings("static-method")
  @Test
  void initialPositionHasTwentyMoves() {
    // Initial position has exactly 20 legal moves for white: 16 pawn moves (8 x single+double push) + 4 knight moves.
    final Set<MoveSpecification> whiteMoves = BitboardPosition.INITIAL_POSITION.legalMoves(Side.WHITE, 0L);
    assertEquals(20, whiteMoves.size(), "white legal moves from initial position");
    final Set<MoveSpecification> blackMoves = BitboardPosition.INITIAL_POSITION.legalMoves(Side.BLACK, 0L);
    assertEquals(20, blackMoves.size(), "black legal moves from initial position");
  }

  @SuppressWarnings("static-method")
  @Test
  void emptyPositionHasNoMoves() {
    assertEquals(Set.of(), BitboardPosition.EMPTY_POSITION.legalMoves(Side.WHITE, 0L));
    assertEquals(Set.of(), BitboardPosition.EMPTY_POSITION.legalMoves(Side.BLACK, 0L));
  }

  @SuppressWarnings("static-method")
  @Test
  void noneSideThrows() {
    assertThrows(IllegalArgumentException.class, () -> BitboardPosition.INITIAL_POSITION.legalMoves(Side.NONE, 0L));
  }
}
