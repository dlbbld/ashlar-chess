package com.dlb.chess.test.pgn.export;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.pgn.LenientPgnParser;
import com.dlb.chess.pgn.PgnCreate;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.test.RestrictTestConstants;
import com.dlb.chess.test.model.PgnFen;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.parser.PgnCacheForLenientPgnParserTestCases;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;

class TestPgnExportIdempotency {

  private static final Logger logger = Nulls.getLogger(TestPgnExportIdempotency.class);

  @SuppressWarnings({ "static-method" })
  @Test
  void test() {
    // true (default) -> curated export-roundtrip smoke subset (~20 files).
    // false -> full ALL_EXCEPT_LONGEST_POSSIBLE corpus for a pre-release / regression sweep.
    final var source = RestrictTestConstants.IS_RESTRICT_PGN_EXPORT_IDEMPOTENCY_TEST
        ? PgnTestCaseCatalog.getExportRoundtripSmokeList()
        : PgnTestCaseCatalog.getRestrictedTestListList();
    for (final PgnTestCaseList testCaseList : source) {
      for (final PgnFen testCase : testCaseList.list()) {
        final String pgnName = testCase.pgnName();

        logger.info(pgnName);

        final PgnGame importedPgn = PgnCacheForLenientPgnParserTestCases.getPgn(testCaseList.pgnTest().getFolderPath(),
            pgnName);

        final List<String> exportedLines = PgnCreate.createPgnLines(importedPgn);
        final PgnGame exportedLinesImportedPgn = LenientPgnParser.parse(exportedLines);

        assertEquals(importedPgn, exportedLinesImportedPgn);

        final List<String> exportedLinesImportedPgnExportedLines = PgnCreate.createPgnLines(exportedLinesImportedPgn);

        assertEquals(exportedLines, exportedLinesImportedPgnExportedLines);

      }
    }

  }

}
