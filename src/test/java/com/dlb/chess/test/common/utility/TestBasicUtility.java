package com.dlb.chess.test.common.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.utility.BasicUtility;

class TestBasicUtility {

  @SuppressWarnings("static-method")
  @Test
  void testRandom() {
    checkIntervalException(0, -1);
    checkIntervalException(1, 0);
    checkIntervalException(2, 0);
    checkIntervalException(2, -1);
    checkIntervalException(99, 97);
    checkIntervalException(99, 0);
    checkIntervalException(99, -97);

    // do this 1000 times
    for (int i = 1; i <= 1000; i++) {
      checkInterval(0, 0);
      checkInterval(0, 1);
      checkInterval(1, 1);
      checkInterval(-1, 0);
      checkInterval(1, 1);
      checkInterval(1, 2);
      checkInterval(1, 3);
      checkInterval(1, 10);
      checkInterval(1, 99);
      checkInterval(1, 100);
      checkInterval(0, 1);
      checkInterval(0, 2);
      checkInterval(0, 3);
      checkInterval(0, 10);
      checkInterval(0, 99);
      checkInterval(0, 100);
    }

    // check we hit all number from 0 to 10
    // with assumption it happens before Integer.MAX_VALUE
    for (int i = 0; i <= 10; i++) {
      boolean isFound = false;
      for (int repeat = 1; repeat <= Integer.MAX_VALUE; repeat++) {
        final int generated = RandomUtility.calculateRandomNumber(0, 10);
        if (generated == i) {
          isFound = true;
          break;
        }
      }
      assertTrue(isFound);
    }
  }

  private static void checkInterval(int leftBoundInclusive, int rightBoundInclusive) {
    final int value = RandomUtility.calculateRandomNumber(leftBoundInclusive, rightBoundInclusive);
    assertTrue(leftBoundInclusive <= value);
    assertTrue(value <= rightBoundInclusive);
  }

  private static void checkIntervalException(int leftBoundInclusive, int rightBoundInclusive) {
    boolean isException = false;
    try {
      RandomUtility.calculateRandomNumber(leftBoundInclusive, rightBoundInclusive);
    } catch (@SuppressWarnings("unused") final IllegalArgumentException e) {
      isException = true;
    }
    assertTrue(isException);
  }

  @SuppressWarnings("static-method")
  @Test
  void testConvertToString() {
    final String expected = """
        line 1

        line 3""";

    final List<String> lines = Nulls.asList("line 1", "", "line 3");

    final String actual = BasicUtility.convertToString(lines);

    assertEquals(expected, actual);

  }

}