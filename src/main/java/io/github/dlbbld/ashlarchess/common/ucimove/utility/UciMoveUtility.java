package io.github.dlbbld.ashlarchess.common.ucimove.utility;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.CastlingMove;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.constants.CastlingConstants;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.model.UciMove;
import io.github.dlbbld.ashlarchess.moves.CastlingUtility;

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
