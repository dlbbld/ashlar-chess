// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceType;
import io.github.dlbbld.ashlarchess.board.enums.Rank;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.model.LegalMoveKind;

// Figure 12 Score routine used in Figure 5. Algorithm Going-to-corner is defined in Figure 13.
class Score {

  // Inputs: position, legal move in the position
  // Output: Normal, Reward, or Punish (variation score)
  public static ScoreResult score(Side color, Side havingMove, BitboardPosition bitboardPosition, LegalMove legalMove) {
    // 1: if it is the intended winner's turn in pos then
    if (havingMove == color) {
      // 2: if m is a capture or m is a pawn push or Going-to-corner(pos, m, Win) then
      // 3: return Reward
      if (calculateIsCapture(legalMove) || calculateIsAdvancedPawnPush(legalMove)
          || GoingToCorner.goingToCorner(color, bitboardPosition, legalMove, Goal.WIN)) {
        return ScoreResult.REWARD;
      }
      // 4: else ( -> It is the intended loser's turn in pos)
    } else {

      // 5: if the intended winner has just a knight and the intended loser has just pawns
      // and/or queens or the intended winner has just bishops of the same square color and
      // the intended loser does not have knights or bishops of the opposite color then ( -> The
      // conditions of Lemma 5 or Lemma 6 apply (ignoring the pawn-freeness condition))
      //

      // Note: We are not immediately returning the value when evaluated as in the PDF, but also evaluating the
      // later condition as in the code. Must be checked what is todot.
      final boolean isNeedLoserPromotion = FindHelpmateExhaust.calculateIsNeedLoserPromotion(color, bitboardPosition);
      if (isNeedLoserPromotion) {
        // 6: if m is a promotion to a queen or rook then return Punish
        // Note: we implement the code with differences to the PDF for this case
        if (calculateIsPromotionToHeavyPiece(legalMove)) {
          return ScoreResult.PUNISH;
        }
        if (calculateIsPawnMove(legalMove)) {
          return ScoreResult.REWARD;
        }
        // here we follow CHA 2.6.1 implementation and not the spec so we can use CHA 2.6.1 as oracle
        return ScoreResult.PUNISH;
      }

      // 8: if Going-to-corner(pos, m, Lose) then return Reward
      if (GoingToCorner.goingToCorner(color, bitboardPosition, legalMove, Goal.LOSE)) {
        return ScoreResult.REWARD;
      }

      // 9: if m is a capture then return Punish
      if (calculateIsCapture(legalMove)) {
        return ScoreResult.PUNISH;
      }
    }

    // 10: return Normal ( -> The default output if none of the above conditions hold)
    // Note: Not what the code is doing
    return ScoreResult.NORMAL;
  }

  private static boolean calculateIsCapture(LegalMove legalMove) {
    return legalMove.pieceCaptured() != Piece.NONE;
  }

  private static boolean calculateIsAdvancedPawnPush(LegalMove legalMove) {

    if (!calculateIsPawnMove(legalMove)) {
      return false;
    }

    return calculateIsAdvancedRank(legalMove.havingMove(), legalMove.moveSpecification().toSquare().getRank());
  }

  private static boolean calculateIsPawnMove(LegalMove legalMove) {
    // in the castling we don't set the king as the moving piece. querying the
    // moving piece type would trigger an error as the moving piece is not set.
    // so we must treat it separately, otherwise there is a runtime exception.
    if (legalMove.kind() == LegalMoveKind.CASTLING) {
      return false;
    }
    return legalMove.movingPiece().getPieceType() == PieceType.PAWN;
  }

  private static boolean calculateIsAdvancedRank(Side side, Rank rank) {
    return switch (side) {
      case WHITE -> switch (rank) {
        case RANK_1, RANK_2, RANK_3, RANK_4, RANK_5 -> false;
        case RANK_6, RANK_7, RANK_8 -> true;
        case NONE -> throw new IllegalArgumentException();
        default -> throw new IllegalArgumentException();
      };
      case BLACK -> switch (rank) {
        case RANK_1, RANK_2, RANK_3 -> true;
        case RANK_4, RANK_5, RANK_6, RANK_7, RANK_8 -> false;
        case NONE -> throw new IllegalArgumentException();
        default -> throw new IllegalArgumentException();
      };
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  private static boolean calculateIsPromotionToHeavyPiece(LegalMove legalMove) {
    return legalMove.kind() == LegalMoveKind.PROMOTION
        && (legalMove.moveSpecification().promotionPieceType() == PromotionPieceType.QUEEN
            || legalMove.moveSpecification().promotionPieceType() == PromotionPieceType.ROOK);
  }

}
