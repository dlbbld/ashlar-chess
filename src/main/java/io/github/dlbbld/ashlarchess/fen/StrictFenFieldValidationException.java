// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.fen;

import io.github.dlbbld.ashlarchess.common.exceptions.UsageException;

final class StrictFenFieldValidationException extends UsageException {

  StrictFenFieldValidationException(String message) {
    super(message);
  }

}
