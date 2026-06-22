// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.fen;

import io.github.dlbbld.ashlarchess.common.exceptions.UsageException;

public class StrictFenSemanticValidationException extends UsageException {

  private final StrictFenSemanticValidationProblem strictFenSemanticValidationProblem;

  public StrictFenSemanticValidationException(StrictFenSemanticValidationProblem strictFenSemanticValidationProblem,
      String message) {
    super(message);
    this.strictFenSemanticValidationProblem = strictFenSemanticValidationProblem;
  }

  public StrictFenSemanticValidationProblem getStrictFenSemanticValidationProblem() {
    return strictFenSemanticValidationProblem;
  }

}
