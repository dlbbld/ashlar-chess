// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.enums.CastlingMove;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.exceptions.NonePointerException;
import io.github.dlbbld.ashlarchess.internal.CastlingConstants;

/**
 * Lock-down coverage for the public castling-geometry accessors on {@link CastlingMove}.
 *
 * <p>
 * Two complementary checks: the absolute squares are asserted directly (documenting intent), and every accessor is
 * pinned to the internal canonical source {@link CastlingConstants} so the public API and the move-generation engine
 * can never silently drift apart.
 */
class TestCastlingMoveGeometry {

  @SuppressWarnings("static-method")
  @Test
  void testAbsoluteSquaresKingSideWhite() {
    assertEquals(Square.E1, CastlingMove.KING_SIDE.kingFromSquare(Side.WHITE));
    assertEquals(Square.G1, CastlingMove.KING_SIDE.kingToSquare(Side.WHITE));
    assertEquals(Square.H1, CastlingMove.KING_SIDE.rookFromSquare(Side.WHITE));
    assertEquals(Square.F1, CastlingMove.KING_SIDE.rookToSquare(Side.WHITE));
  }

  @SuppressWarnings("static-method")
  @Test
  void testAbsoluteSquaresQueenSideWhite() {
    assertEquals(Square.E1, CastlingMove.QUEEN_SIDE.kingFromSquare(Side.WHITE));
    assertEquals(Square.C1, CastlingMove.QUEEN_SIDE.kingToSquare(Side.WHITE));
    assertEquals(Square.A1, CastlingMove.QUEEN_SIDE.rookFromSquare(Side.WHITE));
    assertEquals(Square.D1, CastlingMove.QUEEN_SIDE.rookToSquare(Side.WHITE));
  }

  @SuppressWarnings("static-method")
  @Test
  void testAbsoluteSquaresKingSideBlack() {
    assertEquals(Square.E8, CastlingMove.KING_SIDE.kingFromSquare(Side.BLACK));
    assertEquals(Square.G8, CastlingMove.KING_SIDE.kingToSquare(Side.BLACK));
    assertEquals(Square.H8, CastlingMove.KING_SIDE.rookFromSquare(Side.BLACK));
    assertEquals(Square.F8, CastlingMove.KING_SIDE.rookToSquare(Side.BLACK));
  }

  @SuppressWarnings("static-method")
  @Test
  void testAbsoluteSquaresQueenSideBlack() {
    assertEquals(Square.E8, CastlingMove.QUEEN_SIDE.kingFromSquare(Side.BLACK));
    assertEquals(Square.C8, CastlingMove.QUEEN_SIDE.kingToSquare(Side.BLACK));
    assertEquals(Square.A8, CastlingMove.QUEEN_SIDE.rookFromSquare(Side.BLACK));
    assertEquals(Square.D8, CastlingMove.QUEEN_SIDE.rookToSquare(Side.BLACK));
  }

  @SuppressWarnings("static-method")
  @Test
  void testPinnedToInternalConstantsWhite() {
    assertEquals(CastlingConstants.WHITE_KING_FROM, CastlingMove.KING_SIDE.kingFromSquare(Side.WHITE));
    assertEquals(CastlingConstants.WHITE_KING_KING_SIDE_CASTLING_TO, CastlingMove.KING_SIDE.kingToSquare(Side.WHITE));
    assertEquals(CastlingConstants.WHITE_ROOK_KING_SIDE_CASTLING_FROM,
        CastlingMove.KING_SIDE.rookFromSquare(Side.WHITE));
    assertEquals(CastlingConstants.WHITE_ROOK_KING_SIDE_CASTLING_TO, CastlingMove.KING_SIDE.rookToSquare(Side.WHITE));
    assertEquals(CastlingConstants.WHITE_KING_QUEEN_SIDE_CASTLING_TO, CastlingMove.QUEEN_SIDE.kingToSquare(Side.WHITE));
    assertEquals(CastlingConstants.WHITE_ROOK_QUEEN_SIDE_CASTLING_FROM,
        CastlingMove.QUEEN_SIDE.rookFromSquare(Side.WHITE));
    assertEquals(CastlingConstants.WHITE_ROOK_QUEEN_SIDE_CASTLING_TO, CastlingMove.QUEEN_SIDE.rookToSquare(Side.WHITE));
  }

  @SuppressWarnings("static-method")
  @Test
  void testPinnedToInternalConstantsBlack() {
    assertEquals(CastlingConstants.BLACK_KING_FROM, CastlingMove.KING_SIDE.kingFromSquare(Side.BLACK));
    assertEquals(CastlingConstants.BLACK_KING_KING_SIDE_CASTLING_TO, CastlingMove.KING_SIDE.kingToSquare(Side.BLACK));
    assertEquals(CastlingConstants.BLACK_ROOK_KING_SIDE_CASTLING_FROM,
        CastlingMove.KING_SIDE.rookFromSquare(Side.BLACK));
    assertEquals(CastlingConstants.BLACK_ROOK_KING_SIDE_CASTLING_TO, CastlingMove.KING_SIDE.rookToSquare(Side.BLACK));
    assertEquals(CastlingConstants.BLACK_KING_QUEEN_SIDE_CASTLING_TO, CastlingMove.QUEEN_SIDE.kingToSquare(Side.BLACK));
    assertEquals(CastlingConstants.BLACK_ROOK_QUEEN_SIDE_CASTLING_FROM,
        CastlingMove.QUEEN_SIDE.rookFromSquare(Side.BLACK));
    assertEquals(CastlingConstants.BLACK_ROOK_QUEEN_SIDE_CASTLING_TO, CastlingMove.QUEEN_SIDE.rookToSquare(Side.BLACK));
  }

  @SuppressWarnings("static-method")
  @Test
  void testNoneCastlingMoveThrows() {
    assertThrows(NonePointerException.class, () -> CastlingMove.NONE.kingFromSquare(Side.WHITE));
    assertThrows(NonePointerException.class, () -> CastlingMove.NONE.kingToSquare(Side.WHITE));
    assertThrows(NonePointerException.class, () -> CastlingMove.NONE.rookFromSquare(Side.WHITE));
    assertThrows(NonePointerException.class, () -> CastlingMove.NONE.rookToSquare(Side.WHITE));
  }

  @SuppressWarnings("static-method")
  @Test
  void testNoneSideThrows() {
    assertThrows(NonePointerException.class, () -> CastlingMove.KING_SIDE.kingFromSquare(Side.NONE));
    assertThrows(NonePointerException.class, () -> CastlingMove.KING_SIDE.kingToSquare(Side.NONE));
    assertThrows(NonePointerException.class, () -> CastlingMove.QUEEN_SIDE.rookFromSquare(Side.NONE));
    assertThrows(NonePointerException.class, () -> CastlingMove.QUEEN_SIDE.rookToSquare(Side.NONE));
  }
}
