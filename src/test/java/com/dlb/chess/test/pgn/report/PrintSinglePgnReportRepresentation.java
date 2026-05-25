package com.dlb.chess.test.pgn.report;

import java.util.List;

import org.apache.logging.log4j.Logger;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.report.Reporter;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;
import com.dlb.chess.test.report.representation.BasicRepresentation;

public class PrintSinglePgnReportRepresentation extends AbstractPgnReportTest {

  private static final String PGN_NAME = "various_gundavaa_tari_2022.pgn";

  private static final Logger logger = Nulls.getLogger(PrintSinglePgnReportRepresentation.class);

  public static void main(String[] args) throws Exception {

    final PgnTest pgnTest = PgnTestCaseCatalog.findPgnTestPgnNotListed(PGN_NAME);
    final var report = Reporter.calculateReport(pgnTest.getFolderPath(), PGN_NAME);
    final List<String> representation = BasicRepresentation.calculateRepresentation(report, PGN_NAME);

    for (final String line : representation) {
      logger.info(line);
    }

  }

}
