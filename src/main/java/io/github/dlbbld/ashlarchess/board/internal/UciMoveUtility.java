// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board.internal;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.CastlingMove;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.internal.CastlingConstants;
import io.github.dlbbld.ashlarchess.board.MoveSpecification;
import io.github.dlbbld.ashlarchess.board.UciMove;
import io.github.dlbbld.ashlarchess.moves.CastlingUtility;

public final class UciMoveUtility {

  private UciMoveUtility() {
  }

  public static UciMove toUci(Side sideToMove, MoveSpecification moveSpecification) {
    Square fromSquare;
    Square toSquare;
    PromotionPieceType promotionPieceType;
    if (CastlingUtility.isCastlingMove(moveSpecification)) {
      fromSquare = CastlingUtility.calculateKingCastlingFrom(sideToMove, moveSpecification);
      toSquare = CastlingUtility.calculateKingCastlingTo(sideToMove, moveSpecification);
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
  public static MoveSpecification toMoveSpecification(Board board, UciMove uciMove) {
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

  public static String toSan(Board board, UciMove uciMove) {
    final MoveSpecification moveSpecification = toMoveSpecification(board, uciMove);
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
