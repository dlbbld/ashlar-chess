// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.generate;

public class GenerateTestCaseForPgn {

  // we assume for convenience the file is in one of the provided folders
  private static final String PGN_NAME = "02_last_move_added_accidentally_result_draw_one_move_in_KvK.pgn";

  public static void main(String[] args) {
    generateTestCaseForPgn(PGN_NAME);
  }

  private static void generateTestCaseForPgn(String pgnName) {
    final String testCaseValues = GenerateTestCaseForPgnSupport.generate(pgnName);
    System.out.println(testCaseValues);
  }
}
