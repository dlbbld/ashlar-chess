package com.dlb.chess.san;

import java.util.Set;

import com.dlb.chess.board.enums.File;
import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.Rank;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.messages.Message;
import com.dlb.chess.model.EmptyBoardMove;
import com.dlb.chess.squares.AbstractEmptyBoardSquares;

abstract class SanValidateMovementRnbq extends AbstractSan implements EnumConstants {

  public static void validateRnbqMovement(SanParse sanParse) {
    final SanConversion sanConversion = sanParse.sanConversion();

    final PieceType pieceType = sanConversion.movingPieceType();
    final Square toSquare = sanConversion.toSquare();

    switch (sanParse.sanFormat()) {
      case KING_NON_CASTLING_CAPTURING:
      case KING_NON_CASTLING_NON_CAPTURING:
      case KING_CASTLING_QUEEN_SIDE:
      case KING_CASTLING_KING_SIDE:
        return;
      case PAWN_CAPTURING_NON_PROMOTION:
      case PAWN_CAPTURING_PROMOTION:
      case PAWN_NON_CAPTURING_NON_PROMOTION:
      case PAWN_NON_CAPTURING_PROMOTION:
        return;
      case RNBQ_NON_CAPTURING_NEITHER:
      case RNBQ_CAPTURING_NEITHER:
        // with source file and rank not restricted, all pieces can move to any square
        return;
      case RNBQ_NON_CAPTURING_FILE:
      case RNBQ_CAPTURING_FILE:
        validateRnbqMovement(pieceType, sanConversion.fromFile(), toSquare);
        break;
      case RNBQ_NON_CAPTURING_RANK:
      case RNBQ_CAPTURING_RANK:
        validateRnbqMovement(pieceType, sanConversion.fromRank(), toSquare);
        break;
      case RNBQ_NON_CAPTURING_SQUARE:
      case RNBQ_CAPTURING_SQUARE: {
        final Square fromSquare = AbstractSan.calculateFromSquare(sanConversion);
        validateRnbqMovement(pieceType, fromSquare, toSquare);
      }
        break;
      default:
        throw new IllegalArgumentException();
    }
  }

  private static void validateRnbqMovement(PieceType movingPieceType, File fromFile, Square toSquare) {
    final Set<EmptyBoardMove> emptyBoardMoveSet = AbstractEmptyBoardSquares
        .calculateNonPawnEmptyBoardMoves(movingPieceType);

    for (final EmptyBoardMove emptyBoardMove : emptyBoardMoveSet) {
      final Square emptyBoardToSquare = emptyBoardMove.toSquare();
      if (emptyBoardToSquare == toSquare) {
        final Square emptyBoardFromSquare = emptyBoardMove.fromSquare();
        final File emptyBoardFromFile = emptyBoardFromSquare.getFile();
        if (emptyBoardFromFile == fromFile) {
          // found a possible move
          return;
        }
      }
    }

    switch (movingPieceType) {
      case ROOK -> throw new SanValidationException(SanValidationProblem.MOVEMENT_RNBQ_FROM_FILE, Message
          .getString("validation.san.movement.rnbq.from.file.rook", fromFile.getLetterString(), toSquare.getName()));
      case KNIGHT -> throw new SanValidationException(SanValidationProblem.MOVEMENT_RNBQ_FROM_FILE, Message
          .getString("validation.san.movement.rnbq.from.file.knight", fromFile.getLetterString(), toSquare.getName()));
      case BISHOP -> throw new SanValidationException(SanValidationProblem.MOVEMENT_RNBQ_FROM_FILE, Message
          .getString("validation.san.movement.rnbq.from.file.bishop", fromFile.getLetterString(), toSquare.getName()));
      case QUEEN -> throw new SanValidationException(SanValidationProblem.MOVEMENT_RNBQ_FROM_FILE, Message
          .getString("validation.san.movement.rnbq.from.file.queen", fromFile.getLetterString(), toSquare.getName()));
      default -> throw new IllegalArgumentException();
    }
  }

  private static void validateRnbqMovement(PieceType movingPieceType, Rank fromRank, Square toSquare) {
    final Set<EmptyBoardMove> emptyBoardMoveSet = AbstractEmptyBoardSquares
        .calculateNonPawnEmptyBoardMoves(movingPieceType);
    for (final EmptyBoardMove emptyBoardMove : emptyBoardMoveSet) {
      final Square emptyBoardToSquare = emptyBoardMove.toSquare();
      if (emptyBoardToSquare == toSquare) {
        final Square emptyBoardFromSquare = emptyBoardMove.fromSquare();
        final Rank emptyBoardFromRank = emptyBoardFromSquare.getRank();
        if (emptyBoardFromRank == fromRank) {
          // found a possible move
          return;
        }
      }
    }

    final String rankText = Nulls.valueOf(fromRank.getNumber());
    switch (movingPieceType) {
      case ROOK -> throw new SanValidationException(SanValidationProblem.MOVEMENT_RNBQ_FROM_RANK,
          Message.getString("validation.san.movement.rnbq.from.rank.rook", rankText, toSquare.getName()));
      case KNIGHT -> throw new SanValidationException(SanValidationProblem.MOVEMENT_RNBQ_FROM_RANK,
          Message.getString("validation.san.movement.rnbq.from.rank.knight", rankText, toSquare.getName()));
      case BISHOP -> throw new SanValidationException(SanValidationProblem.MOVEMENT_RNBQ_FROM_RANK,
          Message.getString("validation.san.movement.rnbq.from.rank.bishop", rankText, toSquare.getName()));
      case QUEEN -> throw new SanValidationException(SanValidationProblem.MOVEMENT_RNBQ_FROM_RANK,
          Message.getString("validation.san.movement.rnbq.from.rank.queen", rankText, toSquare.getName()));
      default -> throw new IllegalArgumentException();
    }
  }

  private static void validateRnbqMovement(PieceType movingPieceType, Square fromSquare, Square toSquare) {
    final Set<EmptyBoardMove> emptyBoardMoveSet = AbstractEmptyBoardSquares
        .calculateNonPawnEmptyBoardMoves(movingPieceType);
    for (final EmptyBoardMove emptyBoardMove : emptyBoardMoveSet) {
      final Square emptyBoardToSquare = emptyBoardMove.toSquare();
      if (emptyBoardToSquare == toSquare) {
        final Square emptyBoardFromSquare = emptyBoardMove.fromSquare();
        if (emptyBoardFromSquare == fromSquare) {
          // found a possible move
          return;
        }
      }
    }

    switch (movingPieceType) {
      case ROOK -> throw new SanValidationException(SanValidationProblem.MOVEMENT_RNBQ_FROM_SQUARE,
          Message.getString("validation.san.movement.rnbq.from.square.rook", fromSquare.getName(), toSquare.getName()));
      case KNIGHT -> throw new SanValidationException(SanValidationProblem.MOVEMENT_RNBQ_FROM_SQUARE, Message
          .getString("validation.san.movement.rnbq.from.square.knight", fromSquare.getName(), toSquare.getName()));
      case BISHOP -> throw new SanValidationException(SanValidationProblem.MOVEMENT_RNBQ_FROM_SQUARE, Message
          .getString("validation.san.movement.rnbq.from.square.bishop", fromSquare.getName(), toSquare.getName()));
      case QUEEN -> throw new SanValidationException(SanValidationProblem.MOVEMENT_RNBQ_FROM_SQUARE, Message
          .getString("validation.san.movement.rnbq.from.square.queen", fromSquare.getName(), toSquare.getName()));
      default -> throw new IllegalArgumentException();
    }
  }

}
