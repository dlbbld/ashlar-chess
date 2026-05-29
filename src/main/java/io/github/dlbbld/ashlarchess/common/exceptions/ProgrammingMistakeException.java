// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

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
