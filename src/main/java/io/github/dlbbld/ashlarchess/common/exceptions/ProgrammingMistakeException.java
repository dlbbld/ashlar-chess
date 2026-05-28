package io.github.dlbbld.ashlarchess.common.exceptions;

public class ProgrammingMistakeException extends ChessApiRuntimeException {

  private static final String BASE_MESSAGE = "Programming mistake";

  public ProgrammingMistakeException() {
  }

  public ProgrammingMistakeException(String message, Throwable cause) {
    super(calculateMessage(BASE_MESSAGE, message), cause);
  }

  public ProgrammingMistakeException(String message) {
    super(calculateMessage(BASE_MESSAGE, message));
  }

}
