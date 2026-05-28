package io.github.dlbbld.ashlarchess.san;

import org.eclipse.jdt.annotation.Nullable;

import io.github.dlbbld.ashlarchess.board.enums.CastlingRightLoss;
import io.github.dlbbld.ashlarchess.common.exceptions.UsageException;
import io.github.dlbbld.ashlarchess.enums.MoveCheck;

public class SanValidationException extends UsageException {

  private final SanValidationProblem sanValidationProblem;
  private final @Nullable MoveCheck moveCheck;
  private final @Nullable CastlingRightLoss castlingRightLoss;

  public SanValidationException(SanValidationProblem sanValidationProblem, String message) {
    super(message);
    this.sanValidationProblem = sanValidationProblem;
    this.moveCheck = null;
    this.castlingRightLoss = null;
  }

  public SanValidationException(SanValidationProblem sanValidationProblem, String message, MoveCheck moveCheck,
      CastlingRightLoss castlingRightLoss) {
    super(message);
    this.sanValidationProblem = sanValidationProblem;
    this.moveCheck = moveCheck;
    this.castlingRightLoss = castlingRightLoss;
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

}
