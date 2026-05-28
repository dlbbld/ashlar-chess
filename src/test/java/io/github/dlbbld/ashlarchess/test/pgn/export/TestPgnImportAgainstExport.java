package io.github.dlbbld.ashlarchess.test.pgn.export;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParser;
import io.github.dlbbld.ashlarchess.pgn.PgnCreate;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.test.RestrictTestConstants;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.parser.PgnCacheForLenientPgnParserTestCases;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;

class TestPgnImportAgainstExport {

  private static final Logger logger = Nulls.getLogger(TestPgnImportAgainstExport.class);

  @SuppressWarnings({ "static-method" })
  @Test
  void test() {
    // true (default) -> curated export-roundtrip smoke subset (~20 files).
    // false -> full ALL_EXCEPT_LONGEST_POSSIBLE corpus for a pre-release / regression sweep.
    final List<PgnTestCaseList> source = RestrictTestConstants.IS_RESTRICT_PGN_WRITER_TEST
        ? PgnTestCaseCatalog.getExportRoundtripSmokeList()
        : PgnTestCaseCatalog.getRestrictedTestListList();
    for (final PgnTestCaseList testCaseList : source) {
      for (final PgnFen testCase : testCaseList.list()) {
        final String pgnName = testCase.pgnName();

        logger.info(pgnName);

        final PgnGame pgnGameFromFileSystem = PgnCacheForLenientPgnParserTestCases
            .getPgn(testCaseList.pgnTest().getFolderPath(), pgnName);

        final List<String> export = PgnCreate.createPgnLines(pgnGameFromFileSystem);
        final PgnGame pgnGameFromReadingExport = LenientPgnParser.parse(export);

        assertEquals(pgnGameFromFileSystem, pgnGameFromReadingExport);
      }
    }

  }

}
