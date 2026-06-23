// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.validatenewmove;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.InvalidMoveException;
import io.github.dlbbld.ashlarchess.board.MoveCheck;
import io.github.dlbbld.ashlarchess.board.MoveSpecification;

public final class TestValidateNewMoveSupport {

  private TestValidateNewMoveSupport() {
  }

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
    final Board board = Board.fromFenStrict(fen);
    check(board, move, expectedMoveCheck);

  }
}
