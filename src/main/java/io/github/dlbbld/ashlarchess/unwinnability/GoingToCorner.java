// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.model.LegalMoveKind;
import io.github.dlbbld.ashlarchess.moves.CastlingUtility;

//Figure 13 Going-to-corner routine used in Figure 12.
class GoingToCorner implements EnumConstants {

  // Inputs: position, legal move in the position, objective (Win or Lose)
  // Output: bool (indicating whether or not m is leading to a corner mating position)
  public static boolean goingToCorner(Side color, BitboardPosition bitboardPosition, LegalMove m, Goal goal) {

    // 1: let P be moved piece in m and let s be the square P is moving to

    // let P be moved piece in m
    final Piece movingPiece;
    // let s be the square P is moving to
    final Square toSquare;
    final Square fromSquare;
    if (m.kind() == LegalMoveKind.CASTLING) {
      movingPiece = m.movingPiece();
      toSquare = CastlingUtility.calculateKingCastlingTo(m.havingMove(), m.moveSpecification());
      fromSquare = CastlingUtility.calculateKingCastlingFrom(m.havingMove(), m.moveSpecification());
    } else {
      movingPiece = m.movingPiece();
      toSquare = m.moveSpecification().toSquare();
      fromSquare = m.moveSpecification().fromSquare();
    }

    // 2: if P.type not in {K,N} then return false ( -> We focus on "slow" (non-sliding)
    // pieces
    // that could take several turns to reach the desired square)
    if (movingPiece.getPieceType() != PieceType.KING && movingPiece.getPieceType() != PieceType.KNIGHT) {
      return false;
    }

    // 3: if the intended winner has dark-squared bishops or the intended loser has lightsquared
    // bishops (and the intended winner does not) then ( -> The target corner is set
    // to be h8)
    final Square targetSquare = calculateTargetSquare(color, bitboardPosition, goal, movingPiece);

    // 9: if P.type =K then return king-distance(s, target) < king-distance(P.sq, target)
    if (movingPiece.getPieceType() == KING) {
      return KingDistance.distance(toSquare, targetSquare) < KingDistance.distance(fromSquare, targetSquare);
    }
    // 10: else return knight-distance(s, target) < knight-distance(P.sq, target) ( -> P.type =N)
    return KnightDistance.distance(toSquare, targetSquare) < KnightDistance.distance(fromSquare, targetSquare);

  }

  private static Square calculateTargetSquare(Side winner, BitboardPosition bitboardPosition, Goal goal, Piece p) {
    final boolean isDarkCorner = UnwinnabilityMaterialBitboard.calculateHasDarkSquareBishops(winner, bitboardPosition)
        || UnwinnabilityMaterialBitboard.calculateHasLightSquareBishops(winner.getOppositeSide(), bitboardPosition)
            && UnwinnabilityMaterialBitboard.calculateHasNoBishops(winner, bitboardPosition);

    Square target = calculateTargetSquare(isDarkCorner, goal, p.getPieceType());
    if (winner == BLACK) {
      // 8: set target := (flip-rank  flip-file)(target) . Flip the target with respect to the
      // center of the board (a8 becomes h1, and h8 becomes a1)
      target = Square.flip(target);
    }

    return target;
  }

  private static Square calculateTargetSquare(boolean isDarkCorner, Goal goal, PieceType pieceType) {
    if (isDarkCorner) {
      // 4: set target := if goal = Win then (P.type=K)?h6 : h8 else (P.type =K)?h8 : g8
      switch (goal) {
        case WIN:
          if (pieceType == KING) {
            return H6;
          }
          return H8;
        case LOSE:
          if (pieceType == KING) {
            return H8;
          }
          return G8;
        default:
          throw new IllegalArgumentException();
      }
    }
    // 5: else ( -> The target corner is set to be a8)
    // 6: set target := if goal = Win then (P.type=K)?a6 : a8 else (P.type =K)?a8 : b8
    switch (goal) {
      case WIN:
        if (pieceType == KING) {
          return A6;
        }
        return A8;
      case LOSE:
        if (pieceType == KING) {
          return A8;
        }
        return B8;
      default:
        throw new IllegalArgumentException();
    }
  }
}
