package io.github.dlbbld.ashlarchess.common.exceptions;

public class ChessApiRuntimeException extends RuntimeException {

  public ChessApiRuntimeException() {
  }

  public ChessApiRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public ChessApiRuntimeException(String message) {
    super(message);
  }

  static String calculateMessage(String baseMessage, String message) {
    return baseMessage + " - " + message;
  }
}
