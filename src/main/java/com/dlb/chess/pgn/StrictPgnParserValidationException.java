package com.dlb.chess.pgn;

import com.dlb.chess.common.exceptions.UsageException;
import com.dlb.chess.san.SanValidationProblem;

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
