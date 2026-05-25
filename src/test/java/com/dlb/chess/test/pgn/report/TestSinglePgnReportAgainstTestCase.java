package com.dlb.chess.test.pgn.report;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.report.Reporter;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;
import com.dlb.chess.test.report.representation.BasicRepresentation;

class TestSinglePgnReportAgainstTestCase extends AbstractPgnReportTest {

  private static final String PGN_NAME = "various_pranav_savic_2021_incomplete_speculative_from_last_capture.pgn";

  private static final Logger logger = Nulls.getLogger(TestSinglePgnReportAgainstTestCase.class);

  @SuppressWarnings("static-method")
  @Test
  void testPgn() throws Exception {

    logger.info(PGN_NAME);

    final PgnTest pgnTest = PgnTestCaseCatalog.findPgnTestPgnNotListed(PGN_NAME);
    final var expectedReports = Reporter.calculateReport(pgnTest.getFolderPath(), PGN_NAME);
    final List<String> visualIndication = BasicRepresentation.calculateRepresentation(expectedReports, PGN_NAME);

    for (final String line : visualIndication) {
      logger.info(line);
    }

    testReportAgainstTestCase(PGN_NAME, expectedReports);

  }

}
