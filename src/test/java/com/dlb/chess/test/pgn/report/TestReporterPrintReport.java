package com.dlb.chess.test.pgn.report;

import org.junit.jupiter.api.Test;

import com.dlb.chess.report.Reporter;

class TestReporterPrintReport {

  @SuppressWarnings("static-method")
  @Test
  void printReportDoesNotThrowOnSimpleGame() {
    final var pgn = """
        1. e4 e5 2. Nf3 Nf6 3. Bc4 Bc5
        """;
    Reporter.printReport(pgn);
  }

}
