package com.dlb.chess.san;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.Rank;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.messages.Message;
import com.dlb.chess.moves.EnPassantCaptureUtility;

abstract class SanValidateDestination extends AbstractSan implements EnumConstants {

  public static void validateDestinationSquareSemantics(Board board, Side havingMove, SanFormat sanFormat,
      SanConversion sanConversion) {
    if (sanFormat.isKingCastlingMove()) {
      return;
    }

    final Square toSquare = sanConversion.toSquare();
    final BitboardPosition bitboardPosition = board.getBitboardPosition();
    final Piece pieceOnToSquare = bitboardPosition.get(toSquare);
    final PieceType movingPieceType = sanConversion.movingPieceType();

    if (movingPieceType == PAWN) {
      validatePawnDestination(board, havingMove, sanFormat, sanConversion, toSquare, pieceOnToSquare);
    } else {
      validateRnbqkDestination(havingMove, sanFormat, toSquare, pieceOnToSquare);
    }
  }

  private static void validatePawnDestination(Board board, Side havingMove, SanFormat sanFormat,
      SanConversion sanConversion, Square toSquare, Piece pieceOnToSquare) {
    final boolean isCapture = sanFormat.isCapture();

    if (pieceOnToSquare != Piece.NONE) {
      // own piece on destination
      if (pieceOnToSquare.getSide() == havingMove) {
        if (isCapture) {
          throw new SanValidationException(SanValidationProblem.DESTINATION_PAWN_CAPTURE_OWN_PIECE,
              Message.getString("validation.san.destination.pawn.capture.ownPiece", toSquare.getName()));
        }
        throw new SanValidationException(SanValidationProblem.DESTINATION_PAWN_FORWARD_OWN_PIECE,
            Message.getString("validation.san.destination.pawn.forward.ownPiece", toSquare.getName()));
      }

      // opponent piece on destination
      if (pieceOnToSquare.getPieceType() == KING) {
        if (isCapture) {
          throw new SanValidationException(SanValidationProblem.DESTINATION_PAWN_CAPTURE_KING,
              Message.getString("validation.san.destination.pawn.capture.king", toSquare.getName()));
        }
        throw new SanValidationException(SanValidationProblem.DESTINATION_PAWN_FORWARD_OPPONENT_PIECE_KING,
            Message.getString("validation.san.destination.pawn.forward.opponentPiece.king", toSquare.getName()));
      }

      // opponent non-king on destination
      if (!isCapture) {
        throw new SanValidationException(SanValidationProblem.DESTINATION_PAWN_FORWARD_OPPONENT_PIECE_NOT_KING,
            Message.getString("validation.san.destination.pawn.forward.opponentPiece.notKing", toSquare.getName()));
      }
      // capturing onto opponent non-king: valid, fall through
      return;
    }

    // empty destination
    if (isCapture) {
      if (calculateIsPotentialEnPassantCapture(board, havingMove, sanFormat, sanConversion, toSquare)) {
        return;
      }
      throw new SanValidationException(SanValidationProblem.DESTINATION_PAWN_CAPTURE_EMPTY_NOT_EN_PASSANT,
          Message.getString("validation.san.destination.pawn.capture.emptyNotEnPassant", toSquare.getName()));
    }
    // non-capturing pawn move to empty destination: valid, fall through
  }

  private static void validateRnbqkDestination(Side havingMove, SanFormat sanFormat, Square toSquare,
      Piece pieceOnToSquare) {
    final boolean isCapture = sanFormat.isCapture();

    if (pieceOnToSquare != Piece.NONE) {
      // own piece on destination
      if (pieceOnToSquare.getSide() == havingMove) {
        if (isCapture) {
          throw new SanValidationException(SanValidationProblem.DESTINATION_RNBQK_OWN_PIECE_CAPTURING,
              Message.getString("validation.san.destination.rnbqk.ownPiece.capturing", toSquare.getName()));
        }
        throw new SanValidationException(SanValidationProblem.DESTINATION_RNBQK_OWN_PIECE_NON_CAPTURING,
            Message.getString("validation.san.destination.rnbqk.ownPiece.nonCapturing", toSquare.getName()));
      }

      // opponent piece on destination
      if (pieceOnToSquare.getPieceType() == KING) {
        if (isCapture) {
          throw new SanValidationException(SanValidationProblem.DESTINATION_RNBQK_OPPONENT_KING_CAPTURING,
              Message.getString("validation.san.destination.rnbqk.opponentKing.capturing", toSquare.getName()));
        }
        throw new SanValidationException(SanValidationProblem.DESTINATION_RNBQK_OPPONENT_KING_NON_CAPTURING,
            Message.getString("validation.san.destination.rnbqk.opponentKing.nonCapturing", toSquare.getName()));
      }

      // opponent non-king on destination
      if (!isCapture) {
        throw new SanValidationException(SanValidationProblem.DESTINATION_RNBQK_OPPONENT_NON_KING_NO_CAPTURE_SYMBOL,
            Message.getString("validation.san.destination.rnbqk.opponentNonKingNoCaptureSymbol", toSquare.getName()));
      }
      // capturing onto opponent non-king: valid, fall through
      return;
    }

    // empty destination
    if (isCapture) {
      throw new SanValidationException(SanValidationProblem.DESTINATION_RNBQK_EMPTY_CAPTURE_SYMBOL,
          Message.getString("validation.san.destination.rnbqk.emptyCaptureSymbol", toSquare.getName()));
    }
    // non-capturing move to empty destination: valid, fall through
  }

  private static boolean calculateIsPotentialEnPassantCapture(Board board, Side havingMove, SanFormat sanFormat,
      SanConversion sanConversion, Square toSquare) {
    if (sanFormat != SanFormat.PAWN_CAPTURING_NON_PROMOTION) {
      return false;
    }
    final Rank fromRank = Rank.calculatePreviousRank(havingMove, toSquare.getRank());
    final Square fromSquare = Square.calculate(sanConversion.fromFile(), fromRank);
    final MoveSpecification pawnCapturingNonPromotionMove = new MoveSpecification(fromSquare, toSquare);
    return EnPassantCaptureUtility.calculateIsPotentialEnPassantCapture(board.getBitboardPosition(),
        pawnCapturingNonPromotionMove);
  }
}
