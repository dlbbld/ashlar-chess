// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.fen;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import io.github.dlbbld.ashlarchess.common.enums.StrictFenSemanticValidationProblem;
import io.github.dlbbld.ashlarchess.fen.model.Fen;

public record StrictFenParserValidationResult(@NonNull StrictFenSemanticValidationProblem problem, @NonNull String message,
    @Nullable Fen fen) {

  public boolean isValid() {
    return problem == StrictFenSemanticValidationProblem.SUCCESS;
  }

}
