package com.dlb.chess.test.moves.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.enums.PromotionPieceType;
import com.dlb.chess.common.ucimove.utility.UciMoveValidationUtility;
import com.dlb.chess.model.UciMove;

class TestUciMoveValidationUtility {

  @SuppressWarnings("static-method")
  @Test
  void testPromotionPiece() {
    for (final UciMove uciMove : UciMoveValidationUtility.getUciMoveList()) {
      if (uciMove.isPromotion()) {
        assertNotEquals(PromotionPieceType.NONE, uciMove.promotionPieceType());
      } else {
        assertEquals(PromotionPieceType.NONE, uciMove.promotionPieceType());
      }
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testMoveText() {
    for (final UciMove uciMove : UciMoveValidationUtility.getUciMoveList()) {
      final StringBuilder expectedText = new StringBuilder();
      expectedText.append(uciMove.fromSquare().getName());
      expectedText.append(uciMove.toSquare().getName());
      if (uciMove.isPromotion()) {
        final char pieceNameLowerCase = Character.toLowerCase(uciMove.promotionPieceType().getPieceType().getLetter());
        expectedText.append(pieceNameLowerCase);
      }
      assertEquals(expectedText.toString(), uciMove.text());
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testNumberOfUciMoves() {
    // total: 1968
    int total = 0;

    // rook: 896
    int rook = 0;
    // for each square there are 7 moves along the file and rank
    rook += 64 * (7 + 7);
    assertEquals(896, rook);

    total += rook;

    // bishop: 560
    int bishop = 0;
    // first outer quadrat
    bishop += (8 + 6 + 8 + 6) * 7;

    // second outer quadrat
    bishop += (6 + 4 + 4 + 6) * 9;

    // third outer quadrat
    bishop += (4 + 2 + 2 + 4) * 11;

    // fourth oouter quadrat
    bishop += (2 + 0 + 0 + 2) * 13;
    assertEquals(560, bishop);

    total += bishop;

    // knight: 336
    int knight = 0;
    // edges
    knight += 4 * 2;

    // one square orthogonal from edges (a2, b1, etc.)
    knight += 4 * 2 * 3;

    // remaining square first outer quadrat
    knight += 4 * 4 * 4;

    // edges second outer quadrat
    knight += 4 * 4;

    // remaining square second outer quadrat
    knight += 4 * 4 * 6;

    // remaining inner quadrat
    knight += 4 * 4 * 8;
    assertEquals(336, knight);

    total += knight;

    // pawn promotion: 176
    int pawnPromotionWhite = 0;
    // straight promotion
    pawnPromotionWhite += 8;
    // left diagonal promotion
    pawnPromotionWhite += 7;
    // right diagonal promotion
    pawnPromotionWhite += 7;
    // times promotion pieces
    pawnPromotionWhite *= 4;

    final int pawnPromotionBlack = pawnPromotionWhite;
    final int pawnPromotion = pawnPromotionWhite + pawnPromotionBlack;
    assertEquals(176, pawnPromotion);

    total += pawnPromotion;

    assertEquals(1968, total);

    assertEquals(total, UciMoveValidationUtility.getUciMoveList().size());
  }
}
