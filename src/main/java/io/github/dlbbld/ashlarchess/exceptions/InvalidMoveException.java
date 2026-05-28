package io.github.dlbbld.ashlarchess.exceptions;

import io.github.dlbbld.ashlarchess.common.exceptions.UsageException;
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
