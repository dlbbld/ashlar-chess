package com.dlb.chess.test.generate;

import org.eclipse.jdt.annotation.NonNull;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.enums.InsufficientMaterial;
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

public class GenerateLibraryCarlosInsufficientMaterialTestCases {

  public static void main(String[] args) throws Exception {
    generateTestCase();
  }

  private static void generateTestCase() throws Exception {

    for (final PgnTestCaseList testCaseList : PgnTestCaseCatalog.getTestList(PgnTest.BASIC_INSUFFICIENT_MATERIAL_BOTH,
        PgnTest.BASIC_INSUFFICIENT_MATERIAL_ONLY_WHITE, PgnTest.BASIC_INSUFFICIENT_MATERIAL_ONLY_BLACK,
        PgnTest.BASIC_INSUFFICIENT_MATERIAL_NONE)) {
      for (final PgnTestCase testCase : testCaseList.list()) {

        final Board board = testCase.game(testCaseList.pgnTest());
        final InsufficientMaterial insufficientMaterial = board.calculateInsufficientMaterial();
        final String fen = board.getFen();

        final String testCaseTitel = calculateTestCaseTitel(testCase.pgnName());
        System.out.println("//" + testCaseTitel);
        System.out.println("board.loadFromFen(\"" + fen + "\");");

        if (insufficientMaterial == InsufficientMaterial.BOTH) {
          System.out.println("assertTrue(board.isInsufficientMaterial());");
        } else {
          System.out.println("assertFalse(board.isInsufficientMaterial());");
        }
        if (insufficientMaterial == InsufficientMaterial.BOTH
            || insufficientMaterial == InsufficientMaterial.WHITE_ONLY) {
          System.out.println("assertTrue(board.isInsufficientMaterial(Side.WHITE));");
        } else {
          System.out.println("assertFalse(board.isInsufficientMaterial(Side.WHITE));");
        }
        if (insufficientMaterial == InsufficientMaterial.BOTH
            || insufficientMaterial == InsufficientMaterial.BLACK_ONLY) {
          System.out.println("assertTrue(board.isInsufficientMaterial(Side.BLACK));");
        } else {
          System.out.println("assertFalse(board.isInsufficientMaterial(Side.BLACK));");
        }
        System.out.println("");
      }
    }
  }

  private static String calculateTestCaseTitel(String game) {
    final var step1 = game.replace("insufficient_material_", "");
    @SuppressWarnings("null") final @NonNull String step2 = step1.replace("_", "v");
    return step2;
  }

}
