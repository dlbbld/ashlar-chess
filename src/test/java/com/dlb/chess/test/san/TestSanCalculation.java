package com.dlb.chess.test.san;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.constants.EnumConstants;

class TestSanCalculation implements EnumConstants {

  @SuppressWarnings("static-method")
  @Test
  void test() {
    final Board board = new Board();

    board.moveStrict("e4");
    assertEquals("e4", board.getSan());

    board.moveStrict("d5");
    assertEquals("d5", board.getSan());

    board.moveStrict("exd5");
    assertEquals("exd5", board.getSan());

    board.moveStrict("e5");
    assertEquals("e5", board.getSan());

    board.moveStrict("dxe6");
    assertEquals("dxe6", board.getSan());

    board.moveStrict("h6");
    assertEquals("h6", board.getSan());

    board.moveStrict("exf7+");
    assertEquals("exf7+", board.getSan());

    board.moveStrict("Ke7");
    assertEquals("Ke7", board.getSan());

    board.moveStrict("fxg8=Q");
    assertEquals("fxg8=Q", board.getSan());

    board.moveStrict("h5");
    assertEquals("h5", board.getSan());

    board.moveStrict("Qxh8");
    assertEquals("Qxh8", board.getSan());

    board.moveStrict("h4");
    assertEquals("h4", board.getSan());

    board.moveStrict("g4");
    assertEquals("g4", board.getSan());

    board.moveStrict("hxg3");
    assertEquals("hxg3", board.getSan());

    board.moveStrict("a4");
    assertEquals("a4", board.getSan());

    board.moveStrict("gxh2");
    assertEquals("gxh2", board.getSan());

    board.moveStrict("a5");
    assertEquals("a5", board.getSan());

    board.moveStrict("hxg1=Q");
    assertEquals("hxg1=Q", board.getSan());

    board.moveStrict("a6");
    assertEquals("a6", board.getSan());

    board.moveStrict("Qxh1");
    assertEquals("Qxh1", board.getSan());

  }

}