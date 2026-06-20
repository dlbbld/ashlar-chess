// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.common.utility;

public final class ExceptionUtility {

  private ExceptionUtility() {
  }

  @SuppressWarnings("null")
  public static String getMessage(Throwable throwable) {
    return String.valueOf(throwable.getMessage());
  }
}
