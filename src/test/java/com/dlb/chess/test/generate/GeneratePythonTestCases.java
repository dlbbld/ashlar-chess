package com.dlb.chess.test.generate;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.common.model.HalfMove;
import com.dlb.chess.test.ConfigurationTestConstants;
import com.dlb.chess.test.common.utility.FileUtility;
import com.dlb.chess.test.model.PgnFen;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.constants.PgnTestConstants;

public class GeneratePythonTestCases implements EnumConstants {

  private static final Logger logger = Nulls.getLogger(GeneratePythonTestCases.class);

  private static final int PRINT_MOVES_INTERVAL = 100;
  private static final int PRINT_GENERATED_LINES_INTERVAL = 1000;

  private static final int WRITE_LINE_INTERVAL = 100000;
  private static final Path PYTHON_SCRIPT = Nulls.pathResolve(
      Nulls.pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH, "../python-chess"), "test_play_game.py");

  public static void main(String[] args) throws Exception {
    generatePythonTestCase();
  }

  public static void generatePythonTestCase() throws Exception {

    FileUtility.deleteFile(PYTHON_SCRIPT);

    logger.info("BEGIN generating code");
    List<String> codeLineList = new ArrayList<>();
    final List<Boolean> counterList = new ArrayList<>();

    processPythonCodeLine("import chess", counterList, codeLineList);
    processPythonCodeLine("import unittest", counterList, codeLineList);
    processPythonCodeLine("", counterList, codeLineList);
    processPythonCodeLine("class GameTestCase(unittest.TestCase):", counterList, codeLineList);

    for (final PgnTestCaseList testCaseList : PgnTestCaseCatalog.getRestrictedTestListList()) {
      final Path folderPath = testCaseList.pgnTest().getFolderPath();
      logger.info("Processing folder " + folderPath);
      processPythonCodeLine("", counterList, codeLineList);
      final String folderIndication;
      {
        final var folderIndication0 = folderPath.toAbsolutePath().toString();
        final var folderIndication1 = folderIndication0
            .replace(PgnTestConstants.PGN_TEST_ROOT_FOLDER_PATH.toAbsolutePath().toString(), "");
        try (final var fileSystem = FileSystems.getDefault()) {
          final var separator = fileSystem.getSeparator();
          folderIndication = folderIndication1.replace(separator, "_");
        }
      }
      processPythonCodeLine("  def test_" + folderIndication + "(self):", counterList, codeLineList);
      processPythonCodeLine("    print(\"Processing module " + folderIndication + "\")", counterList, codeLineList);
      for (final PgnFen testCase : testCaseList.list()) {
        logger.info("Processing game " + testCase.pgnName());

        processPythonCodeLine("", counterList, codeLineList);
        processPythonCodeLine("    #" + testCase.pgnName(), counterList, codeLineList);
        processPythonCodeLine("    print(\"  Processing game " + testCase.pgnName() + "\")", counterList, codeLineList);
        processPythonCodeLine("    board = chess.Board()", counterList, codeLineList);

        final Board boardPlayAlong = new Board();
        final Board board = testCase.game(testCaseList.pgnTest());
        for (final HalfMove halfMove : board.getHalfMoveList()) {
          boardPlayAlong.move(halfMove.moveSpecification());
          processPythonCodeLine("    board.push_san(\"" + halfMove.san() + "\")", counterList, codeLineList);
          final var isMadeByWhite = halfMove.havingMove().getIsWhite();
          if (isMadeByWhite && halfMove.fullMoveNumber() % PRINT_MOVES_INTERVAL == 0) {
            final var moveDescription = halfMove.fullMoveNumber() + "." + halfMove.san();
            processPythonCodeLine("    print(\"Performed " + moveDescription + "\")", counterList, codeLineList);
          }

          processPythonCodeLine("    self.assertEqual(board.is_check(), "
              + convertJavaBooleanToPythonBoolean(boardPlayAlong.isCheck()) + ")", counterList, codeLineList);

          processPythonCodeLine("    self.assertEqual(board.is_checkmate(), "
              + convertJavaBooleanToPythonBoolean(boardPlayAlong.isCheckmate()) + ")", counterList, codeLineList);

          processPythonCodeLine("    self.assertEqual(board.is_stalemate(), "
              + convertJavaBooleanToPythonBoolean(boardPlayAlong.isStalemate()) + ")", counterList, codeLineList);

          final var isInsufficientMaterial = boardPlayAlong.isInsufficientMaterial();
          processPythonCodeLine("    self.assertEqual(board.is_insufficient_material(), "
              + convertJavaBooleanToPythonBoolean(isInsufficientMaterial) + ")", counterList, codeLineList);

          final var hasInsufficientMaterialWhite = boardPlayAlong.isInsufficientMaterial(WHITE);
          processPythonCodeLine("    self.assertEqual(board.has_insufficient_material(chess.WHITE), "
              + convertJavaBooleanToPythonBoolean(hasInsufficientMaterialWhite) + ")", counterList, codeLineList);

          final var hasInsufficientMaterialBlack = boardPlayAlong.isInsufficientMaterial(BLACK);
          processPythonCodeLine("    self.assertEqual(board.has_insufficient_material(chess.BLACK), "
              + convertJavaBooleanToPythonBoolean(hasInsufficientMaterialBlack) + ")", counterList, codeLineList);

          final var canClaimThreefoldRepetitionRule = boardPlayAlong.canClaimThreefoldRepetitionRule();
          processPythonCodeLine(
              "    self.assertEqual(board.can_claim_threefold_repetition(), "
                  + convertJavaBooleanToPythonBoolean(canClaimThreefoldRepetitionRule) + ")",
              counterList, codeLineList);

          final var canClaimFiftyMoveRule = boardPlayAlong.canClaimFiftyMoveRule();
          processPythonCodeLine("    self.assertEqual(board.can_claim_fifty_moves(), "
              + convertJavaBooleanToPythonBoolean(canClaimFiftyMoveRule) + ")", counterList, codeLineList);

          final var isTwoFoldRepetition = halfMove.countRepetition() >= 2;
          processPythonCodeLine("    self.assertEqual(board.is_repetition(2), "
              + convertJavaBooleanToPythonBoolean(isTwoFoldRepetition) + ")", counterList, codeLineList);

          final var isThreeFoldRepetition = halfMove.countRepetition() >= 3;
          processPythonCodeLine("    self.assertEqual(board.is_repetition(), "
              + convertJavaBooleanToPythonBoolean(isThreeFoldRepetition) + ")", counterList, codeLineList);

          final var isFourFoldRepetition = halfMove.countRepetition() >= 4;
          processPythonCodeLine("    self.assertEqual(board.is_repetition(4), "
              + convertJavaBooleanToPythonBoolean(isFourFoldRepetition) + ")", counterList, codeLineList);

          final var isFiveFoldRepetitionRule = halfMove.countRepetition() >= 5;
          processPythonCodeLine("    self.assertEqual(board.is_fivefold_repetition(), "
              + convertJavaBooleanToPythonBoolean(isFiveFoldRepetitionRule) + ")", counterList, codeLineList);

          // no boolean methods for fifty-move rule and seventy-five-move rule in python

          final var halfMoveClock = boardPlayAlong.getHalfMoveClock();
          processPythonCodeLine("    self.assertEqual(board.halfmove_clock, " + halfMoveClock + ")", counterList,
              codeLineList);

          // test legal moves
          final List<String> legalMovesUci = boardPlayAlong.getLegalMovesUci();
          final var expectedSize = legalMovesUci.size();

          // test number of legal moves
          processPythonCodeLine("    self.assertEqual(board.legal_moves.count(), " + expectedSize + ")", counterList,
              codeLineList);

          // test legal moves values
          for (final String uci : legalMovesUci) {
            processPythonCodeLine("    move = chess.Move.from_uci(\"" + uci + "\")", counterList, codeLineList);
            processPythonCodeLine("    self.assertTrue(move in board.legal_moves)", counterList, codeLineList);
          }

          if (codeLineList.size() >= WRITE_LINE_INTERVAL) {
            FileUtility.appendFile(PYTHON_SCRIPT, codeLineList);
            codeLineList = new ArrayList<>();
          }
        }
      }
    }
    // write remaining
    FileUtility.appendFile(PYTHON_SCRIPT, codeLineList);
    logger.info("END generating code");
  }

  private static String convertJavaBooleanToPythonBoolean(boolean value) {
    if (value) {
      return "True";
    }
    return "False";
  }

  private static void processPythonCodeLine(String codeLine, List<Boolean> counterList, List<String> codeLineList) {
    // System.out.println(codeLine);
    codeLineList.add(codeLine);
    counterList.add(true);
    // the code line list is emptied after appending, so we need a separate list for the count
    if (counterList.size() % PRINT_GENERATED_LINES_INTERVAL == 0) {
      System.out.println("Generated " + counterList.size() + " lines");
    }
  }
}
