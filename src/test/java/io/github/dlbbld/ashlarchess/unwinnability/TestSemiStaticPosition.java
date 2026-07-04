// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.internal.Nulls;

// Invariants of the semi-static position model and its Board projection, ported from the fun22-reference unit tests.
class TestSemiStaticPosition {

  private static SemiStaticPiece king(Side side, int square) {
    return new SemiStaticPiece(PieceType.KING, side, square);
  }

  @SuppressWarnings("static-method")
  @Test
  void acceptsExactlyOneKingPerSide() {
    final SemiStaticPosition position = new SemiStaticPosition(Nulls.listOf(king(Side.WHITE, 0), king(Side.BLACK, 63)),
        false, false);
    assertEquals(0, position.kingIndex(Side.WHITE));
    assertEquals(1, position.kingIndex(Side.BLACK));
  }

  @SuppressWarnings("static-method")
  @Test
  void rejectsMissingKing() {
    assertThrows(ProgrammingMistakeException.class,
        () -> new SemiStaticPosition(Nulls.listOf(king(Side.WHITE, 0)), false, false));
  }

  @SuppressWarnings("static-method")
  @Test
  void rejectsDuplicateKing() {
    assertThrows(ProgrammingMistakeException.class,
        () -> new SemiStaticPosition(Nulls.listOf(king(Side.WHITE, 0), king(Side.WHITE, 1), king(Side.BLACK, 63)),
            false, false));
  }

  @SuppressWarnings("static-method")
  @Test
  void rejectsTwoPiecesOnOneSquare() {
    assertThrows(ProgrammingMistakeException.class, () -> new SemiStaticPosition(
        Nulls.listOf(king(Side.WHITE, 0), king(Side.BLACK, 63), new SemiStaticPiece(PieceType.ROOK, Side.WHITE, 63)),
        false, false));
  }

  @SuppressWarnings("static-method")
  @Test
  void boardProjectionCarriesPiecesAndPreconditions() {
    final SemiStaticPosition startPosition = SemiStaticPosition.fromBoard(new Board());
    assertEquals(32, startPosition.count());
    assertTrue(startPosition.castlingRightsPresent());
    assertFalse(startPosition.enPassantPossible());

    final SemiStaticPosition bareKings = SemiStaticPosition
        .fromBoard(Board.fromFenStrict("4k3/8/8/8/8/8/8/4K3 w - - 0 1"));
    assertEquals(2, bareKings.count());
    assertFalse(bareKings.castlingRightsPresent());
    assertFalse(bareKings.enPassantPossible());
  }
}
