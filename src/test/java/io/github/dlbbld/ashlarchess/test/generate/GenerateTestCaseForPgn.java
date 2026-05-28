package io.github.dlbbld.ashlarchess.test.generate;

public class GenerateTestCaseForPgn extends AbstractGenerateTestCaseForPgn {

  // we assume for convenience the file is in one of the provided folders
  private static final String PGN_NAME = "02_last_move_added_accidentally_result_draw_one_move_in_KvK.pgn";

  public static void main(String[] args) throws Exception {
    generateTestCaseForPgn(PGN_NAME);
  }

  private static void generateTestCaseForPgn(String pgnName) throws Exception {
    final String testCaseValues = generate(pgnName);
    System.out.println(testCaseValues);
  }
}
