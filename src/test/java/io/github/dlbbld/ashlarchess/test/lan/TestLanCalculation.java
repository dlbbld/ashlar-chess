// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.lan;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;

class TestLanCalculation implements EnumConstants {

  @SuppressWarnings("static-method")
  @Test
  void test() {
    final Board board = new Board();

    board.moveStrict("e4");
    assertEquals("e2-e4", board.getLan());

    board.moveStrict("d5");
    assertEquals("d7-d5", board.getLan());

    board.moveStrict("exd5");
    assertEquals("e4xd5", board.getLan());

    board.moveStrict("e5");
    assertEquals("e7-e5", board.getLan());

    board.moveStrict("dxe6");
    assertEquals("d5xe6", board.getLan());

    board.moveStrict("h6");
    assertEquals("h7-h6", board.getLan());

    board.moveStrict("exf7+");
    assertEquals("e6xf7+", board.getLan());

    board.moveStrict("Ke7");
    assertEquals("Ke8-e7", board.getLan());

    board.moveStrict("fxg8=Q");
    assertEquals("f7xg8=Q", board.getLan());

    board.moveStrict("h5");
    assertEquals("h6-h5", board.getLan());

    board.moveStrict("Qxh8");
    assertEquals("Qg8xh8", board.getLan());

    board.moveStrict("h4");
    assertEquals("h5-h4", board.getLan());

    board.moveStrict("g4");
    assertEquals("g2-g4", board.getLan());

    board.moveStrict("hxg3");
    assertEquals("h4xg3", board.getLan());

    board.moveStrict("a4");
    assertEquals("a2-a4", board.getLan());

    board.moveStrict("gxh2");
    assertEquals("g3xh2", board.getLan());

    board.moveStrict("a5");
    assertEquals("a4-a5", board.getLan());

    board.moveStrict("hxg1=Q");
    assertEquals("h2xg1=Q", board.getLan());

    board.moveStrict("a6");
    assertEquals("a5-a6", board.getLan());

    board.moveStrict("Qxh1");
    assertEquals("Qg1xh1", board.getLan());

  }

}