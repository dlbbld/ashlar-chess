package io.github.dlbbld.ashlarchess.test.common.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.utility.SetUtility;

class TestSetUtility {

  @SuppressWarnings("static-method")
  @Test
  void testGetOnlySuccess() {
    final Set<String> set = new TreeSet<>(Set.of("hello"));
    assertEquals("hello", SetUtility.getOnly(set));
  }

  @SuppressWarnings("static-method")
  @Test
  void testGetOnlyFailsForEmpty() {
    final Set<String> set = new TreeSet<>();
    assertThrows(IllegalArgumentException.class, () -> SetUtility.getOnly(set));
  }

  @SuppressWarnings("static-method")
  @Test
  void testGetOnlyFailsForMultiple() {
    final Set<String> set = new TreeSet<>(Set.of("a", "b"));
    assertThrows(IllegalArgumentException.class, () -> SetUtility.getOnly(set));
  }

}
