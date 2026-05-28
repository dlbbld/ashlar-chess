// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.pgn;

import io.github.dlbbld.ashlarchess.common.exceptions.UsageException;
import io.github.dlbbld.ashlarchess.san.SanValidationProblem;

public class StrictPgnParserValidationException extends UsageException {

  private final StrictPgnParserValidationProblem strictPgnParserValidationProblem;

  private final SanValidationProblem sanValidationProblem;

  public StrictPgnParserValidationException(StrictPgnParserValidationProblem strictPgnParserValidationProblem,
      SanValidationProblem sanValidationProblem, String message) {
    super(message);
    this.strictPgnParserValidationProblem = strictPgnParserValidationProblem;
    this.sanValidationProblem = sanValidationProblem;
  }

  public StrictPgnParserValidationProblem getStrictPgnParserValidationProblem() {
    return strictPgnParserValidationProblem;
  }

  public SanValidationProblem getSanValidationProblem() {
    return sanValidationProblem;
  }

}
