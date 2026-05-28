package io.github.dlbbld.ashlarchess.test.common;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.Nulls;

class TestNonNullWrapperCommon {

  @SuppressWarnings("static-method")
  @Test
  void test() {

    final String pgn = """
        line 1

        line 3
        """;

    final String[] expected = { "line 1", "", "line 3", "" };
    final String[] actual = Nulls.split(pgn, "\\n");

    assertArrayEquals(expected, actual);
  }

  @SuppressWarnings("static-method")
  @Test
  void testNormalizeSpace() {

    final String expected = "The knight is good in the attack.";

    assertEquals(expected, Nulls.normalizeSpace("  The knight is good in the attack."));
    assertEquals(expected, Nulls.normalizeSpace("The knight is good in the attack.  "));
    assertEquals(expected, Nulls.normalizeSpace("  The knight is good in the attack.    "));
    assertEquals(expected, Nulls.normalizeSpace("The knight is   good    in  the attack."));
    assertEquals(expected, Nulls.normalizeSpace("   The   knight  is good in   the  attack.   "));
  }

}