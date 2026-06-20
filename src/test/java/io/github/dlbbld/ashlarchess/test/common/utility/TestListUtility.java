// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.common.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.utility.ListUtility;

class TestListUtility {

  @SuppressWarnings("static-method")
  @Test
  void lineSeparatedList() {
    final String expected = """
        line 1

        line 3""";

    final List<String> lines = Nulls.asList("line 1", "", "line 3");

    assertEquals(expected, ListUtility.calculateLineSeparatedList(lines));
  }

}
