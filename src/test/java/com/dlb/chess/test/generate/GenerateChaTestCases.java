package com.dlb.chess.test.generate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.apache.logging.log4j.Logger;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.constants.ConfigurationConstants;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.common.exceptions.FileSystemAccessException;
import com.dlb.chess.model.PgnHalfMove;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.test.common.utility.FileUtility;
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;

public class GenerateChaTestCases implements EnumConstants {

  private static final Logger logger = Nulls.getLogger(GenerateChaTestCases.class);

  private static final Path LIST_FOR_CHA_FILE_PATH = Nulls.pathResolve(ConfigurationConstants.TEMP_FOLDER_PATH,
      "list_cha_2.txt");

  public static void main(String[] args) throws Exception {
    generateChaTestCases(LIST_FOR_CHA_FILE_PATH);
  }

  public static void generateChaTestCases(Path filePath) throws Exception {

    FileUtility.deleteIfExists(filePath);

    logger.info("BEGIN generating code");
    final var file = filePath.toFile();
    try (Writer w = new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8.name());
        PrintWriter pw = new PrintWriter(w)) {

      for (final PgnTestCaseList testCaseList : PgnTestCaseCatalog.getRestrictedTestListList()) {
        final Path folderPath = testCaseList.pgnTest().getFolderPath();
        logger.info("Processing folder " + folderPath);

        for (final PgnTestCase testCase : testCaseList.list()) {
          // if (!testList.getFolder().equals(PgnTest.BASIC_CHECKMATE)) {
          // continue;
          // }
          logger.info("Processing game " + testCase.pgnName());

          final String pgnName = testCase.pgnName();

          final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(folderPath, pgnName);

          final Board board = new Board(pgnGame.startFen());

          var halfMoveCounter = 0;
          pw.println(calculateLine(board, folderPath, pgnName, halfMoveCounter));

          for (final PgnHalfMove halfMove : pgnGame.halfMoveList()) {
            halfMoveCounter++;
            board.moveStrict(halfMove.san());

            pw.println(calculateLine(board, folderPath, pgnName, halfMoveCounter));

          }

        }
      }
    } catch (final IOException ioe) {
      throw new FileSystemAccessException("File appending to " + filePath + " failed", ioe);
    }
    logger.info("END generating code");
  }

  private static String calculateLine(Board board, Path folderPath, String game, int halfMoveCounter) {
    final StringBuilder line = new StringBuilder();
    line.append(board.getFen());

    line.append(";");
    line.append(folderPath);

    line.append(";");
    line.append(game);

    line.append(";");
    line.append(halfMoveCounter);

    line.append(";");
    line.append(board.getHavingMove());

    final var isLastMove = halfMoveCounter == board.getLegalMoves().size();
    line.append(";");
    line.append(isLastMove);

    String result;
    if (board.isCheckmate()) {
      result = "checkmate";
    } else if (board.isStalemate()) {
      result = "stalemate";
    } else {
      result = "none";
    }
    line.append(";");
    line.append(result);

    final var isHavingMoveInsufficientMaterial = board.isInsufficientMaterial(board.getHavingMove().getOppositeSide());
    line.append(";");
    line.append(isHavingMoveInsufficientMaterial);

    final var isToReplyInsufficientMaterial = board.isInsufficientMaterial(board.getHavingMove().getOppositeSide());
    line.append(";");
    line.append(isToReplyInsufficientMaterial);

    return Nulls.toString(line);
  }

}
