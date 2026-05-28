package io.github.dlbbld.ashlarchess.san;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.messages.Message;

abstract class SanValidateCheck extends AbstractSan {

  public static void validateSanTerminalMarker(Board board, SanTerminalMarker sanSanTerminalMarker,
      MoveSpecification moveSpecification) {
    final SanTerminalMarker boardSanTerminalMarker = calculateSanTerminalMarker(board, moveSpecification);

    switch (sanSanTerminalMarker) {
      case CHECKMATE:
        switch (boardSanTerminalMarker) {
          case CHECKMATE:
            return;
          case CHECK:
            throw new SanValidationException(SanValidationProblem.CHECKMATE_SYMBOL_BUT_CHECK_ONLY,
                Message.getString("validation.san.checkmateSymbolButCheckOnly"));
          case NONE:
            throw new SanValidationException(SanValidationProblem.CHECKMATE_SYMBOL_BUT_NO_CHECK,
                Message.getString("validation.san.checkmateSymbolButNoCheck"));
          default:
            throw new IllegalArgumentException();
        }
      case CHECK:
        switch (boardSanTerminalMarker) {
          case CHECKMATE:
            throw new SanValidationException(SanValidationProblem.CHECK_SYMBOL_BUT_CHECKMATE,
                Message.getString("validation.san.checkSymbolButCheckmate"));
          case CHECK:
            break;
          case NONE:
            throw new SanValidationException(SanValidationProblem.CHECK_SYMBOL_BUT_NO_CHECK,
                Message.getString("validation.san.checkSymbolButNoCheck"));
          default:
            throw new IllegalArgumentException();
        }
        break;
      case NONE:
        switch (boardSanTerminalMarker) {
          case CHECKMATE:
            throw new SanValidationException(SanValidationProblem.NO_SYMBOL_BUT_CHECKMATE,
                Message.getString("validation.san.noSymbolButCheckmate"));
          case CHECK:
            throw new SanValidationException(SanValidationProblem.NO_SYMBOL_BUT_CHECK,
                Message.getString("validation.san.noSymbolButCheck"));
          case NONE:
            break;
          default:
            throw new IllegalArgumentException();
        }
        break;
      default:
        throw new IllegalArgumentException();
    }
  }

  private static SanTerminalMarker calculateSanTerminalMarker(Board board, MoveSpecification moveSpecification) {
    board.move(moveSpecification);

    SanTerminalMarker sanTerminalMarker;
    if (board.isCheckmate()) {
      sanTerminalMarker = SanTerminalMarker.CHECKMATE;
    } else if (board.isCheck()) {
      sanTerminalMarker = SanTerminalMarker.CHECK;
    } else {
      sanTerminalMarker = SanTerminalMarker.NONE;
    }
    board.unmove();

    return sanTerminalMarker;
  }
}
