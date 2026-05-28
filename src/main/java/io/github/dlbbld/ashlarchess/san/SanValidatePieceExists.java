package io.github.dlbbld.ashlarchess.san;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.enums.File;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.messages.Message;

abstract class SanValidatePieceExists extends AbstractSan {

  public static void validatePieceExists(Side havingMove, SanFormat sanFormat, SanConversion sanConversion,
      PieceType movingPieceType, BitboardPosition bitboardPosition) {
    switch (sanFormat) {
      case KING_CASTLING_KING_SIDE:
      case KING_CASTLING_QUEEN_SIDE:
      case KING_NON_CASTLING_CAPTURING:
      case KING_NON_CASTLING_NON_CAPTURING:
        return;
      case PAWN_NON_CAPTURING_NON_PROMOTION:
      case PAWN_NON_CAPTURING_PROMOTION: {
        // for non-capturing pawn moves, the pawn must be on the to-square's file
        final File pawnFile = sanConversion.toSquare().getFile();
        if (!SanPieceCheck.calculateHasPieceType(havingMove, PieceType.PAWN, bitboardPosition, pawnFile)) {
          throw new SanValidationException(SanValidationProblem.EXISTS_PAWN,
              Message.getString("validation.san.exists.pawn", pawnFile.getLetterString()));
        }
        break;
      }
      case PAWN_CAPTURING_NON_PROMOTION:
      case PAWN_CAPTURING_PROMOTION: {
        // for capturing pawn moves, the SAN specifies the from-file explicitly
        final File pawnFile = sanConversion.fromFile();
        if (!SanPieceCheck.calculateHasPieceType(havingMove, PieceType.PAWN, bitboardPosition, pawnFile)) {
          throw new SanValidationException(SanValidationProblem.EXISTS_PAWN,
              Message.getString("validation.san.exists.pawn", pawnFile.getLetterString()));
        }
        break;
      }
      case RNBQ_CAPTURING_NEITHER:
      case RNBQ_NON_CAPTURING_NEITHER:
        if (!SanPieceCheck.calculateHasPieceType(havingMove, movingPieceType, bitboardPosition)) {
          throw new SanValidationException(SanValidationProblem.EXISTS_RNBQ_NEITHER,
              Message.getString("validation.san.exists.rnbq.neither", movingPieceType.getName()));
        }
        break;
      case RNBQ_CAPTURING_FILE:
      case RNBQ_NON_CAPTURING_FILE:
        if (!SanPieceCheck.calculateHasPieceType(havingMove, movingPieceType, bitboardPosition,
            sanConversion.fromFile())) {
          throw new SanValidationException(SanValidationProblem.EXISTS_RNBQ_FILE,
              Message.getString("validation.san.exists.rnbq.file", movingPieceType.getName(),
                  sanConversion.fromFile().getLetterString()));
        }
        break;
      case RNBQ_CAPTURING_RANK:
      case RNBQ_NON_CAPTURING_RANK:
        if (!SanPieceCheck.calculateHasPieceType(havingMove, movingPieceType, bitboardPosition,
            sanConversion.fromRank())) {
          throw new SanValidationException(SanValidationProblem.EXISTS_RNBQ_RANK,
              Message.getString("validation.san.exists.rnbq.rank", movingPieceType.getName(),
                  Nulls.valueOf(sanConversion.fromRank().getNumber())));
        }
        break;
      case RNBQ_CAPTURING_SQUARE:
      case RNBQ_NON_CAPTURING_SQUARE:
        final Square fromSquare = Square.calculate(sanConversion.fromFile(), sanConversion.fromRank());
        final Piece pieceOnFromSquare = bitboardPosition.get(fromSquare);
        if (pieceOnFromSquare == Piece.NONE || pieceOnFromSquare.getSide() != havingMove
            || pieceOnFromSquare.getPieceType() != movingPieceType) {
          throw new SanValidationException(SanValidationProblem.EXISTS_RNBQ_SQUARE,
              Message.getString("validation.san.exists.rnbq.square", movingPieceType.getName(), fromSquare.getName()));
        }
        break;
      default:
        throw new IllegalArgumentException();

    }
  }

}
