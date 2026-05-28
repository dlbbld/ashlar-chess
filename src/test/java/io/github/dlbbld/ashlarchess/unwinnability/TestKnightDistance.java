package io.github.dlbbld.ashlarchess.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.unwinnability.KnightDistance;

class TestKnightDistance implements EnumConstants {

  @SuppressWarnings("static-method")
  @Test
  void test() {
    assertEquals(0, KnightDistance.distance(A1, A1));

    assertEquals(1, KnightDistance.distance(A1, B3));
    assertEquals(2, KnightDistance.distance(A1, C5));
    assertEquals(3, KnightDistance.distance(A1, D7));
    assertEquals(4, KnightDistance.distance(A1, F8));

    assertEquals(5, KnightDistance.distance(A1, H1));
    assertEquals(6, KnightDistance.distance(A1, H8));
  }

  @SuppressWarnings("static-method")
  @Test
  void distanceIsDefinedForAllBoardSquares() {
    for (final Square fromSquare : Square.REAL) {
      for (final Square toSquare : Square.REAL) {
        final int distance = KnightDistance.distance(fromSquare, toSquare);
        assertTrue(distance >= 0);
        assertEquals(distance, KnightDistance.distance(toSquare, fromSquare));
      }
    }
  }
}
