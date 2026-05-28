// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.common.exceptions;

import io.github.dlbbld.ashlarchess.common.exceptions.ChessApiRuntimeException;

public class SetupException extends ChessApiRuntimeException {

  private static final String BASE_MESSAGE = "Invalid test";

  public SetupException() {
  }

  public SetupException(String message, Throwable cause) {
    super(BASE_MESSAGE + " - " + message, cause);
  }

  public SetupException(String message) {
    super(BASE_MESSAGE + " - " + message);
  }

}
