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
    ScoreResult variation = ScoreResult.NORMAL;
    // 1: if it is the intended winner's turn in pos then
    if (havingMove == color) {
      // 2: if m is a capture or m is a pawn push or Going-to-corner(pos, m, Win) then
      // 3: return Reward

      // Spec uses pawn push, CHA 2.6.1 uses advanced pawn push.
      // We follow CHA 2.6.1 so we can use it as oracle.
      if (calculateIsCapture(legalMove) || calculateIsAdvancedPawnPush(legalMove)
          || GoingToCorner.goingToCorner(color, bitboardPosition, legalMove, Goal.WIN)) {
        variation = ScoreResult.REWARD;
      }
      // 4: else ( -> It is the intended loser's turn in pos)
    } else {

      // 5: if the intended winner has just a knight and the intended loser has just pawns
      // and/or queens or the intended winner has just bishops of the same square color and
      // the intended loser does not have knights or bishops of the opposite color then ( -> The
      // conditions of Lemma 5 or Lemma 6 apply (ignoring the pawn-freeness condition))

      // Spec for this case uses early returns which is semantically different than the assignments CHA 2.6.1 uses.
      // We follow CHA 2.6.1 so we can use it as oracle.
      final boolean isNeedLoserPromotion = FindHelpmate.calculateIsNeedLoserPromotion(color, bitboardPosition);
      if (isNeedLoserPromotion) {
        variation = calculateIsPawnMove(legalMove) && !calculateIsPromotionToHeavyPiece(legalMove) ? ScoreResult.REWARD
            : ScoreResult.PUNISH; // CHA's base ternary
      }
      if (GoingToCorner.goingToCorner(color, bitboardPosition, legalMove, Goal.LOSE)) {
        variation = ScoreResult.REWARD;
      } else if (calculateIsCapture(legalMove)) { // else-if = corner wins over capture
        variation = ScoreResult.PUNISH;
      }
    }
    return variation;
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
