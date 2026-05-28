// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.common.exceptions;

public abstract class UsageException extends ChessApiRuntimeException {

  public UsageException() {
  }

  public UsageException(String message, Throwable cause) {
    super(message, cause);
  }

  public UsageException(String message) {
    super(message);
  }

}
