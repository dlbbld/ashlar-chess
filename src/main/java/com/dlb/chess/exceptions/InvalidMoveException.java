package com.dlb.chess.exceptions;

import com.dlb.chess.common.exceptions.UsageException;
import com.dlb.chess.enums.MoveCheck;

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
