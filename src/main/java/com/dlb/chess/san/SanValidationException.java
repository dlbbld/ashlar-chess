package com.dlb.chess.san;

import org.eclipse.jdt.annotation.Nullable;

import com.dlb.chess.board.enums.CastlingRightLoss;
import com.dlb.chess.common.enums.GameStatus;
import com.dlb.chess.common.exceptions.UsageException;
import com.dlb.chess.enums.MoveCheck;

public class SanValidationException extends UsageException {

  private final SanValidationProblem sanValidationProblem;
  private final @Nullable MoveCheck moveCheck;
  private final @Nullable CastlingRightLoss castlingRightLoss;
  private final @Nullable GameStatus gameStatus;

  public SanValidationException(SanValidationProblem sanValidationProblem, String message) {
    super(message);
    this.sanValidationProblem = sanValidationProblem;
    this.moveCheck = null;
    this.castlingRightLoss = null;
    this.gameStatus = null;
  }

  public SanValidationException(SanValidationProblem sanValidationProblem, String message, MoveCheck moveCheck,
      CastlingRightLoss castlingRightLoss) {
    super(message);
    this.sanValidationProblem = sanValidationProblem;
    this.moveCheck = moveCheck;
    this.castlingRightLoss = castlingRightLoss;
    this.gameStatus = null;
  }

  /**
   * Used when {@link SanValidationProblem#GAME_ALREADY_ENDED} is the reason: carries the specific {@link GameStatus}
   * (checkmate / stalemate / dead-position-insufficient-material / dead-position-unwinnable-quick) that ended the
   * game.
   */
  public SanValidationException(SanValidationProblem sanValidationProblem, String message, GameStatus gameStatus) {
    super(message);
    this.sanValidationProblem = sanValidationProblem;
    this.moveCheck = null;
    this.castlingRightLoss = null;
    this.gameStatus = gameStatus;
  }

  public SanValidationProblem getSanValidationProblem() {
    return sanValidationProblem;
  }

  public @Nullable MoveCheck getMoveCheck() {
    return moveCheck;
  }

  public @Nullable CastlingRightLoss getCastlingRightLoss() {
    return castlingRightLoss;
  }

  /**
   * The {@link GameStatus} that ended the game when this exception carries
   * {@link SanValidationProblem#GAME_ALREADY_ENDED}; {@code null} otherwise.
   */
  public @Nullable GameStatus getGameStatus() {
    return gameStatus;
  }

}
