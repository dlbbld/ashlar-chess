// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.report;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.report.Reporter;

class TestReporter {

  @SuppressWarnings("static-method")
  @Test
  void printReportDoesNotThrowOnSimpleGame() {
    final String pgn = """
        1. e4 e5 2. Nf3 Nf6 3. Bc4 Bc5
        """;
    Reporter.printReport(pgn);
  }

}
