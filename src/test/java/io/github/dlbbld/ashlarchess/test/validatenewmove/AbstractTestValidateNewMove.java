// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.validatenewmove;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.enums.MoveCheck;
import io.github.dlbbld.ashlarchess.exceptions.InvalidMoveException;

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
