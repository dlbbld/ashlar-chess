package io.github.dlbbld.ashlarchess.san;

import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.messages.Message;

abstract class SanValidateNonMovement extends AbstractSan {

  public static void validateNonMovement(SanParse sanParse) {
    final SanFormat sanFormat = sanParse.sanFormat();
    switch (sanFormat) {
      case RNBQ_NON_CAPTURING_SQUARE:
      case RNBQ_CAPTURING_SQUARE: {
        final SanConversion sanConversion = sanParse.sanConversion();
        final Square fromSquare = AbstractSan.calculateFromSquare(sanConversion);
        final Square toSquare = sanConversion.toSquare();
        if (fromSquare == toSquare) {
          throw new SanValidationException(
              SanValidationProblem.NON_MOVEMENT_RNBQ_SOURCE_SQUARE_EQUALS_DESTINATION_SQUARE,
              Message.getString("validation.san.nonMovement.rnbq.sourceSquareEqualsDestinationSquare",
                  fromSquare.getName(), toSquare.getName()));
        }
        break;
      }
      default:
    }
  }

}
