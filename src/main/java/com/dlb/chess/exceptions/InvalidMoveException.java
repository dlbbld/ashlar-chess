package com.dlb.chess.exceptions;

import org.eclipse.jdt.annotation.Nullable;

import com.dlb.chess.common.enums.GameStatus;
import com.dlb.chess.common.exceptions.UsageException;
import com.dlb.chess.enums.MoveCheck;

public class InvalidMoveException extends UsageException {

  private final MoveCheck moveCheck;
  private final @Nullable GameStatus gameStatus;

  public InvalidMoveException(String message, MoveCheck moveCheck) {
    super(message);
    this.moveCheck = moveCheck;
    this.gameStatus = null;
  }

  /**
   * Used when {@link MoveCheck#GAME_ALREADY_ENDED} is the reason: carries the specific {@link GameStatus} (checkmate /
   * stalemate / dead-position-insufficient-material / dead-position-unwinnable-quick) that ended the game.
   */
  public InvalidMoveException(String message, MoveCheck moveCheck, GameStatus gameStatus) {
    super(message);
    this.moveCheck = moveCheck;
    this.gameStatus = gameStatus;
  }

  public MoveCheck getMoveCheck() {
    return moveCheck;
  }

  /**
   * The {@link GameStatus} that ended the game when this exception carries {@link MoveCheck#GAME_ALREADY_ENDED};
   * {@code null} otherwise.
   */
  public @Nullable GameStatus getGameStatus() {
    return gameStatus;
  }

}
