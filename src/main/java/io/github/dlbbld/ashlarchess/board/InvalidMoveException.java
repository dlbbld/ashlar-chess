// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board;

import io.github.dlbbld.ashlarchess.exceptions.UsageException;
import io.github.dlbbld.ashlarchess.enums.MoveCheck;

public class InvalidMoveException extends UsageException {

  private final MoveCheck moveCheck;

  public InvalidMoveException(String message, MoveCheck moveCheck) {
    super(message);
    this.moveCheck = moveCheck;
  }

  public MoveCheck getMoveCheck() {
    return moveCheck;
  }

}
