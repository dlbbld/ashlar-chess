// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.pgn;

import io.github.dlbbld.ashlarchess.san.SanValidationProblem;

public record StrictPgnParserValidationResult(StrictPgnParserValidationProblem problemParser,
    SanValidationProblem problemSan, String message) {
  public boolean isValid() {
    return problemParser == StrictPgnParserValidationProblem.OK;
  }
}