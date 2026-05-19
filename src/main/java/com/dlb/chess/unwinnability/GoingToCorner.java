package com.dlb.chess.unwinnability;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.model.LegalMove;
import com.dlb.chess.model.LegalMoveKind;
import com.dlb.chess.moves.CastlingUtility;

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

    // 2: if P.type not in {K,N} then return false ( -> We focus on ÃƒÂ¢Ã¢â€šÂ¬Ã…â€œslowÃƒÂ¢Ã¢â€šÂ¬Ã‚Â (non-sliding)
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
    Square target;

    final var isDarkCorner = UnwinnabilityMaterialBitboard.calculateHasDarkSquareBishops(winner, bitboardPosition)
        || UnwinnabilityMaterialBitboard.calculateHasLightSquareBishops(winner.getOppositeSide(), bitboardPosition)
            && UnwinnabilityMaterialBitboard.calculateHasNoBishops(winner, bitboardPosition);

    if (isDarkCorner) {
      // 4: set target := if goal = Win then (P.type=K)?h6 : h8 else (P.type =K)?h8 : g8
      target = goal == Goal.WIN ? p.getPieceType() == KING ? H6 : H8 : p.getPieceType() == KING ? H8 : G8;

      // 5: else ( -> The target corner is set to be a8)
    } else {
      // 6: set target := if goal = Win then (P.type=K)?a6 : a8 else (P.type =K)?a8 : b8
      target = goal == Goal.WIN ? p.getPieceType() == KING ? A6 : A8 : p.getPieceType() == KING ? A8 : B8;
    }

    // 7: if the intended winner has the black pieces then
    if (winner == BLACK) {
      // 8: set target := (flip-rank  flip-file)(target) . Flip the target with respect to the
      // center of the board (a8 becomes h1, and h8 becomes a1)
      //
      target = Square.flip(target);
    }

    return target;
  }
}
