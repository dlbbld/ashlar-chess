package com.dlb.chess.test.generate;

import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

public class GenerateTestCaseForPgn extends AbstractGenerateTestCaseForPgn {

  // we assume for convenience the file is in one of the provided folders
  private static final String PGN_NAME = "02_last_move_added_accidentally_result_draw_one_move_in_KvK.pgn";

  public static void main(String[] args) throws Exception {
    generateTestCaseForPgn(PGN_NAME);
  }

  private static void generateTestCaseForPgn(String pgnName) throws Exception {
    final PgnTest pgnTest = PgnTestCaseCatalog.findPgnTestPgnNotListed(pgnName);
    final String testCaseValues = generate(pgnTest.getFolderPath(), pgnName);
    System.out.println(testCaseValues);
  }
}
