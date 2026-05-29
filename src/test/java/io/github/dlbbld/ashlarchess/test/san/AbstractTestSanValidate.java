// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.san;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.san.SanValidationException;
import io.github.dlbbld.ashlarchess.san.SanValidationProblem;
import io.github.dlbbld.ashlarchess.san.StrictSanParser;

public abstract class AbstractTestSanValidate implements EnumConstants {

  public static void checkExceptionNonMovement(String san, Board board) {
    checkException(san, board, SanValidationProblem.NON_MOVEMENT_RNBQ_SOURCE_SQUARE_EQUALS_DESTINATION_SQUARE);
  }

  public static void checkExceptionRnbqkMovingOntoOwnPiece(String san, Board board) {
    checkException(san, board, SanValidationProblem.DESTINATION_RNBQK_OWN_PIECE_NON_CAPTURING);
  }

  public static void checkExceptionRnbqkCapturingOwnPiece(String san, Board board) {
    checkException(san, board, SanValidationProblem.DESTINATION_RNBQK_OWN_PIECE_CAPTURING);
  }

  public static void checkExceptionPawnForwardOwnPiece(String san, Board board) {
    checkException(san, board, SanValidationProblem.DESTINATION_PAWN_FORWARD_OWN_PIECE);
  }

  public static void checkExceptionPawnCaptureOwnPiece(String san, Board board) {
    checkException(san, board, SanValidationProblem.DESTINATION_PAWN_CAPTURE_OWN_PIECE);
  }

  public static void checkExceptionFormat(String san, SanValidationProblem problem, Board board) {
    checkException(san, board, problem);
  }

  private static void checkException(String san, Board board, SanValidationProblem problem) {
    boolean isException;
    try {
      StrictSanParser.parseText(san, board);
      isException = false;
    } catch (final SanValidationException e) {
      isException = true;
      assertEquals(problem, e.getSanValidationProblem());
    }
    assertTrue(isException);
  }
}
