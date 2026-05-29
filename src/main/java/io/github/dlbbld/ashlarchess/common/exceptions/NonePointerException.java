// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.common.exceptions;

public class NonePointerException extends ProgrammingMistakeException {

  private static final String BASE_MESSAGE = "Properties of NONE enums have no meaning and are not supposed to be assessed";

  public NonePointerException() {
    super(BASE_MESSAGE);
  }

}
