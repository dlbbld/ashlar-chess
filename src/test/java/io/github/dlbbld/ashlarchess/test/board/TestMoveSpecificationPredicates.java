// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.board;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.MoveSpecification;
import io.github.dlbbld.ashlarchess.board.enums.CastlingMove;
import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceType;
import io.github.dlbbld.ashlarchess.board.enums.Square;

/**
 * Coverage for the {@link MoveSpecification#isCastling()} and {@link MoveSpecification#isPromotion()} convenience
 * predicates across the three move shapes (normal, castling, promotion).
 */
class TestMoveSpecificationPredicates {

  @SuppressWarnings("static-method")
  @Test
  void testNormalMoveIsNeitherCastlingNorPromotion() {
    final MoveSpecification normal = new MoveSpecification(Square.E2, Square.E4);
    assertFalse(normal.isCastling());
    assertFalse(normal.isPromotion());
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingSideCastlingIsCastling() {
    final MoveSpecification castling = new MoveSpecification(CastlingMove.KING_SIDE);
    assertTrue(castling.isCastling());
    assertFalse(castling.isPromotion());
  }

  @SuppressWarnings("static-method")
  @Test
  void testQueenSideCastlingIsCastling() {
    final MoveSpecification castling = new MoveSpecification(CastlingMove.QUEEN_SIDE);
    assertTrue(castling.isCastling());
    assertFalse(castling.isPromotion());
  }

  @SuppressWarnings("static-method")
  @Test
  void testPromotionIsPromotion() {
    final MoveSpecification promotion = new MoveSpecification(Square.A7, Square.A8, PromotionPieceType.QUEEN);
    assertTrue(promotion.isPromotion());
    assertFalse(promotion.isCastling());
  }
}
