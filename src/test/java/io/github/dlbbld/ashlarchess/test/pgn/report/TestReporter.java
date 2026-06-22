// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.report;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.report.Reporter;

class TestReporter {

  @SuppressWarnings("static-method")
  @Test
  void reportProducesLinesForSimpleGame() {
    final String pgn = """
        1. e4 e5 2. Nf3 Nf6 3. Bc4 Bc5
        """;
    final List<String> lines = Reporter.report(pgn);
    assertFalse(lines.isEmpty(), "report must produce at least the section headers");
  }

}
