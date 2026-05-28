// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser.against;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.test.RestrictTestConstants;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.parser.PgnCacheForLenientPgnParserTestCases;
import io.github.dlbbld.ashlarchess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;

import java.util.List;

class TestStrictPgnParserAgainstLenientPgnParser {

  private static final Logger logger = Nulls.getLogger(TestStrictPgnParserAgainstLenientPgnParser.class);

  @SuppressWarnings({ "static-method" })
  @Test
  void test() {
    // true (default) -> curated parser-integration smoke subset (~45 files).
    // false -> full ALL_EXCEPT_LONGEST_POSSIBLE corpus for a pre-release / regression sweep.
    final List<PgnTestCaseList> source = RestrictTestConstants.IS_RESTRICT_PGN_STRICT_AGAINST_LENIENT_TEST
        ? PgnTestCaseCatalog.getParserIntegrationSmokeList()
        : PgnTestCaseCatalog.getRestrictedTestListList();
    for (final PgnTestCaseList testCaseList : source) {
      for (final PgnFen testCase : testCaseList.list()) {

        final String pgnName = testCase.pgnName();

        logger.info(pgnName);

        final PgnGame pgnGameStandard = PgnCacheForLenientPgnParserTestCases
            .getPgn(testCaseList.pgnTest().getFolderPath(), pgnName);

        final PgnGame pgnGameStrict = PgnCacheForStrictPgnParserTestCases.getPgn(testCaseList.pgnTest().getFolderPath(),
            pgnName);

        assertEquals(pgnGameStandard, pgnGameStrict);
      }
    }

  }

}
