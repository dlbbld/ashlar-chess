package com.dlb.chess.common.ucimove.utility;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.CastlingMove;
import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.PromotionPieceType;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.constants.CastlingConstants;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.model.UciMove;
import com.dlb.chess.moves.CastlingUtility;

public abstract class UciMoveUtility {

  public static UciMove convertMoveSpecificationToUci(Side havingMove, MoveSpecification moveSpecification) {
    Square fromSquare;
    Square toSquare;
    PromotionPieceType promotionPieceType;
    if (CastlingUtility.calculateIsCastlingMove(moveSpecification)) {
      fromSquare = CastlingUtility.calculateKingCastlingFrom(havingMove, moveSpecification);
      toSquare = CastlingUtility.calculateKingCastlingTo(havingMove, moveSpecification);
      promotionPieceType = PromotionPieceType.NONE;
    } else {
      fromSquare = moveSpecification.fromSquare();
      toSquare = moveSpecification.toSquare();
      promotionPieceType = moveSpecification.promotionPieceType();
    }

    final String uciMoveStr = UciMoveValidationUtility.calculateUciMoveStr(fromSquare, toSquare, promotionPieceType);

    return UciMoveValidationUtility.lookup(uciMoveStr);
  }

  // we are avoiding checks weather the uci move is legal move or not
  // the goal is to provide a move specification
  // the move specificatoin can then be checked to be legal
  public static MoveSpecification convertUciMoveToMoveSpecification(Board board, UciMove uciMove) {
    // we need the board to identify the castling move

    final Square fromSquare = uciMove.fromSquare();
    final Square toSquare = uciMove.toSquare();

    if (uciMove.isPromotion()) {
      return new MoveSpecification(fromSquare, toSquare, uciMove.promotionPieceType());
    }

    if (!board.getBitboardPosition().isEmpty(fromSquare)
        && board.getBitboardPosition().get(fromSquare).getPieceType() == PieceType.KING) {
      final CastlingMove potentialCastlingMove = calculatePotentialCastlingMove(fromSquare, toSquare);
      switch (potentialCastlingMove) {
        case KING_SIDE:
        case QUEEN_SIDE:
          return new MoveSpecification(potentialCastlingMove);
        case NONE:
          break;
        default:
          throw new IllegalArgumentException();
      }
    }

    return new MoveSpecification(fromSquare, toSquare);
  }

  public static String convertUciMoveToSan(Board board, UciMove uciMove) {
    final MoveSpecification moveSpecification = convertUciMoveToMoveSpecification(board, uciMove);
    board.move(moveSpecification);
    final String san = board.getSan();
    board.unmove();
    return san;
  }

  private static CastlingMove calculatePotentialCastlingMove(Square firstSquare, Square secondSquare) {
    if (firstSquare == CastlingConstants.WHITE_KING_FROM
        && secondSquare == CastlingConstants.WHITE_KING_KING_SIDE_CASTLING_TO) {
      return CastlingMove.KING_SIDE;
    }
    if (firstSquare == CastlingConstants.WHITE_KING_FROM
        && secondSquare == CastlingConstants.WHITE_KING_QUEEN_SIDE_CASTLING_TO) {
      return CastlingMove.QUEEN_SIDE;
    }
    if (firstSquare == CastlingConstants.BLACK_KING_FROM
        && secondSquare == CastlingConstants.BLACK_KING_KING_SIDE_CASTLING_TO) {
      return CastlingMove.KING_SIDE;
    }
    if (firstSquare == CastlingConstants.BLACK_KING_FROM
        && secondSquare == CastlingConstants.BLACK_KING_QUEEN_SIDE_CASTLING_TO) {
      return CastlingMove.QUEEN_SIDE;
    }
    return CastlingMove.NONE;
  }

}
