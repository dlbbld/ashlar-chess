package com.dlb.chess.test.validatenewmove;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.enums.MoveCheck;
import com.dlb.chess.exceptions.InvalidMoveException;

public abstract class AbstractTestValidateNewMove implements EnumConstants {

  static void check(Board board, MoveSpecification move, MoveCheck expectedMoveCheck) {
      boolean isException = false;
    try {
      board.move(move);
    } catch (final InvalidMoveException e) {
      isException = true;
      assertEquals(expectedMoveCheck, e.getMoveCheck());
    }

    assertTrue(isException);
  }

  static void check(String fen, MoveSpecification move, MoveCheck expectedMoveCheck) {
    final Board board = new Board(fen);
    check(board, move, expectedMoveCheck);

  }
}
