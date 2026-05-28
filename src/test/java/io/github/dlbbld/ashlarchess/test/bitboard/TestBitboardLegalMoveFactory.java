// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.bitboard.BitboardLegalMoveFactory;
import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.bitboard.StaticPositionBridge;
import io.github.dlbbld.ashlarchess.board.Board;
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
 * Differential test for {@link BitboardLegalMoveFactory#toLegalMove}: for every legal move on every corpus fixture, the
 * converter applied to the move's {@link MoveSpecification} must reproduce the reference's {@link LegalMove} record
 * (same moving piece, captured piece, and {@link io.github.dlbbld.ashlarchess.model.LegalMoveKind}).
 *
 * <p>
 * The reference is {@link AbstractLegalMoves#calculateLegalMoves} directly - NOT {@code board.getLegalMoves()}, which
 * since Switchover Step 2.2 ({@code a235d363}) is produced via this very factory. Using {@code board.getLegalMoves()}
 * as the oracle here would make the test self-referential.
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
        for (final LegalMove referenceMove : AbstractLegalMoves.calculateLegalMoves(
            StaticPositionBridge.toStaticPosition(board.getBitboardPosition()), havingMove,
            board.getCastlingRight(havingMove), board.getEnPassantCaptureTargetSquare())) {
          final LegalMove converted = BitboardLegalMoveFactory.toLegalMove(bitboardPosition,
              referenceMove.moveSpecification(), havingMove);
          assertEquals(referenceMove, converted, "converted LegalMove disagrees with reference for "
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
    assertEquals(io.github.dlbbld.ashlarchess.model.LegalMoveKind.PAWN_TWO_SQUARE_ADVANCE, converted.kind());
    assertEquals(io.github.dlbbld.ashlarchess.board.enums.Piece.WHITE_PAWN, converted.movingPiece());
    assertEquals(io.github.dlbbld.ashlarchess.board.enums.Piece.NONE, converted.pieceCaptured());
  }

  @SuppressWarnings("static-method")
  @Test
  void noneSideThrows() {
    assertThrows(IllegalArgumentException.class, () -> BitboardLegalMoveFactory
        .toLegalMove(BitboardPosition.INITIAL_POSITION, new MoveSpecification(Square.E2, Square.E4), Side.NONE));
  }
}
