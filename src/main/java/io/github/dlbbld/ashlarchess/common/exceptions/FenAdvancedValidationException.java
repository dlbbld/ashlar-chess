// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.common.exceptions;

import io.github.dlbbld.ashlarchess.common.enums.FenAdvancedValidationProblem;

public class FenAdvancedValidationException extends UsageException {

  private final FenAdvancedValidationProblem fenAdvancedValidationProblem;

  public FenAdvancedValidationException(FenAdvancedValidationProblem fenAdvancedValidationProblem, String message) {
    super(message);
    this.fenAdvancedValidationProblem = fenAdvancedValidationProblem;
  }

  public FenAdvancedValidationProblem getFenAdvancedValidationProblem() {
    return fenAdvancedValidationProblem;
  }

}
