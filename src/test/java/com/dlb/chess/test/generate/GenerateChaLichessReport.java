package com.dlb.chess.test.generate;

import java.nio.file.Path;

import org.apache.logging.log4j.Logger;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.pgn.PgnUtility;
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;
import com.dlb.chess.test.unwinnability.oracle.LimitedUnwinnabilityOracle;
import com.dlb.chess.test.unwinnability.oracle.enums.LimitedUnwinnabilityVerdict;

public class GenerateChaLichessReport {

  private static final Logger logger = Nulls.getLogger(GenerateChaLichessReport.class);

  public static void main(String[] args) throws Exception {
    generate(PgnTestCaseCatalog.getTestList(PgnTest.CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR));
  }

  private static void generate(PgnTestCaseList testCaseList) throws Exception {

    for (final PgnTestCase testCase : testCaseList.list()) {
      final String pgnName = testCase.pgnName();
      final Path folderPath = testCaseList.pgnTest().getFolderPath();

      final var isPawnWallAmbrona = true;
      if (isPawnWallAmbrona) {
        logger.info(pgnName + ";" + "pawn wall");
      } else {
        final Board board = PgnUtility.calculateBoard(folderPath, pgnName);
        final LimitedUnwinnabilityVerdict verdictWhite = LimitedUnwinnabilityOracle.calculateUnwinnability(board,
            Side.WHITE);
        final LimitedUnwinnabilityVerdict verdictBlack = LimitedUnwinnabilityOracle.calculateUnwinnability(board,
            Side.BLACK);

        logger.info("verdict white;" + verdictWhite);
        logger.info("verdict black;" + verdictBlack);
      }
    }
  }

}
