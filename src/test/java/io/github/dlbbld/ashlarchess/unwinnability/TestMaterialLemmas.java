// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;

// Unit tests for the paper's Lemma 5/6 material predicates, ported from the fun22-reference strictness tests. The
// strictness cases matter: the lemmas apply to "just A knight" (exactly one) and to bishops of ONE square colour -
// two knights or opposite-coloured bishops CAN helpmate, so the predicates must not fire there.
class TestMaterialLemmas {

  private static BitboardPosition placement(String fen) {
    return Board.fromFenStrict(fen).getBitboardPosition();
  }

  // ----- Lemma 5: winner with just a knight. -----

  @SuppressWarnings("static-method")
  @Test
  void lemma5FiresForLoneKnightAgainstBareKingOrQueens() {
    assertTrue(MaterialLemmas.unwinnableByLemma5Or6(placement("4k3/8/8/8/8/8/8/3NK3 w - - 0 1"), Side.WHITE));
    // A lone queen never enables a mate by the single knight.
    assertTrue(MaterialLemmas.unwinnableByLemma5Or6(placement("3qk3/8/8/8/8/8/8/3NK3 w - - 0 1"), Side.WHITE));
  }

  @SuppressWarnings("static-method")
  @Test
  void lemma5DeclinesWhenTheLoserHasAnEnablingPiece() {
    // A loser rook, knight or bishop enables a mate by the knight's owner.
    assertFalse(MaterialLemmas.unwinnableByLemma5Or6(placement("3rk3/8/8/8/8/8/8/3NK3 w - - 0 1"), Side.WHITE));
    assertFalse(MaterialLemmas.unwinnableByLemma5Or6(placement("3nk3/8/8/8/8/8/8/3NK3 w - - 0 1"), Side.WHITE));
    assertFalse(MaterialLemmas.unwinnableByLemma5Or6(placement("3bk3/8/8/8/8/8/8/3NK3 w - - 0 1"), Side.WHITE));
  }

  @SuppressWarnings("static-method")
  @Test
  void lemma5IsStrictAboutExactlyOneKnight() {
    // Two knights cannot FORCE mate but can helpmate; the lemma must not fire.
    assertFalse(MaterialLemmas.unwinnableByLemma5Or6(placement("k7/8/8/8/8/8/8/K5NN w - - 0 1"), Side.WHITE));
  }

  // ----- Lemma 6: winner with same-square-colour bishops. -----

  @SuppressWarnings("static-method")
  @Test
  void lemma6FiresForSameColouredBishops() {
    // Both bishops on dark squares (c1, e3); the loser has no knight and no light-squared bishop.
    assertTrue(MaterialLemmas.unwinnableByLemma5Or6(placement("4k3/8/8/8/8/4B3/8/2B1K3 w - - 0 1"), Side.WHITE));
    // A loser rook or queen does not enable a mate.
    assertTrue(MaterialLemmas.unwinnableByLemma5Or6(placement("3rk3/8/8/8/8/4B3/8/2B1K3 w - - 0 1"), Side.WHITE));
  }

  @SuppressWarnings("static-method")
  @Test
  void lemma6DeclinesWhenTheLoserHasAnEnablingPiece() {
    // A loser knight, or a bishop of the opposite square colour, enables a mate.
    assertFalse(MaterialLemmas.unwinnableByLemma5Or6(placement("3nk3/8/8/8/8/4B3/8/2B1K3 w - - 0 1"), Side.WHITE));
    // Loser bishop on f5 is a light-squared bishop, opposite to White's dark-squared pair.
    assertFalse(MaterialLemmas.unwinnableByLemma5Or6(placement("4k3/8/8/5b2/8/4B3/8/2B1K3 w - - 0 1"), Side.WHITE));
  }

  @SuppressWarnings("static-method")
  @Test
  void lemma6IsStrictAboutOneSquareColour() {
    // Opposite-coloured bishops (c1 dark, f1 light) can helpmate; the lemma must not fire.
    assertFalse(MaterialLemmas.unwinnableByLemma5Or6(placement("k7/8/8/8/8/8/8/K1B2B2 w - - 0 1"), Side.WHITE));
  }

  // ----- The pawn-freeness gate and the Figure 12 material condition. -----

  @SuppressWarnings("static-method")
  @Test
  void lemmasRequireAPawnFreePosition() {
    // The loser's pawn could promote to an enabling piece, so the lemma must not fire...
    assertFalse(MaterialLemmas.unwinnableByLemma5Or6(placement("4k3/7p/8/8/8/8/8/3NK3 w - - 0 1"), Side.WHITE));
    // ...but the Figure 12 Score material condition ignores pawn-freeness by design.
    assertTrue(MaterialLemmas.scoreMaterialCondition(placement("4k3/7p/8/8/8/8/8/3NK3 w - - 0 1"), Side.WHITE));
  }

  @SuppressWarnings("static-method")
  @Test
  void bareKingDetection() {
    assertTrue(MaterialLemmas.winnerHasBareKing(placement("4k3/8/8/8/8/8/1Q6/4K3 w - - 0 1"), Side.BLACK));
    assertFalse(MaterialLemmas.winnerHasBareKing(placement("4k3/8/8/8/8/8/1Q6/4K3 w - - 0 1"), Side.WHITE));
  }
}
