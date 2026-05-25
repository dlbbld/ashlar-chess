package com.dlb.chess.test.generate;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.annotation.NonNull;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.constants.ConfigurationConstants;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.common.model.HalfMove;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.common.ucimove.utility.UciMoveUtility;
import com.dlb.chess.common.ucimove.utility.UciMoveValidationUtility;
import com.dlb.chess.model.UciMove;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.pgn.PgnWriter;
import com.dlb.chess.pgn.ResultTagValue;
import com.dlb.chess.pgn.StandardTag;
import com.dlb.chess.pgn.Tag;
import com.dlb.chess.report.Report;
import com.dlb.chess.report.Reporter;
import com.dlb.chess.test.common.utility.PgnExtensionUtility;
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

public class GenerateAmbronaHelpMateTestCases {

  private static final boolean IS_CREATE_UCI_REQUIRED = false;

  private static final Logger logger = Nulls.getLogger(GenerateAmbronaHelpMateTestCases.class);

  private static final String OUTPUT_FOLDER_NAME = "lichessHelpmate";
  private static final Path OUTPUT_FOLDER_PATH = Nulls.pathResolve(ConfigurationConstants.TEMP_FOLDER_PATH,
      OUTPUT_FOLDER_NAME);

  public static void main(String[] args) throws Exception {

    final var folder = OUTPUT_FOLDER_PATH.toFile();
    if (!folder.exists()) {
      folder.mkdir();
    }

    final Map<String, String> havingHelpMate = new TreeMap<>();
    populateHelpMateUci(havingHelpMate);

    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR);
    for (final PgnTestCase testCase : testCaseList.list()) {
      final Path folderPath = testCaseList.pgnTest().getFolderPath();
      final var report = Reporter.calculateReport(folderPath, testCase.pgnName());
      if (IS_CREATE_UCI_REQUIRED) {
        printMovesAsUci(testCase.pgnName(), report);
      }
      writeGameAsContinued(havingHelpMate, folderPath, testCase.pgnName(), report);
    }
  }

  private static void updateTag(List<Tag> tagList, String tagName, String newTagValue) {

    var indexOfResult = -1;
    var isFound = false;
    for (final Tag tag : tagList) {
      indexOfResult++;
      if (tagName.equals(tag.name())) {
        isFound = true;
        break;
      }
    }

    if (!isFound) {
      throw new ProgrammingMistakeException("That cannot be");
    }

    final Tag newPgnResultTag = new Tag(tagName, newTagValue);
    tagList.remove(indexOfResult);
    tagList.add(indexOfResult, newPgnResultTag);

  }

  private static void writeGameAsContinued(Map<String, String> havingHelpMate, Path folderExisting, String pgnName,
      Report report) {

    if (havingHelpMate.containsKey(pgnName)) {

      final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(folderExisting, pgnName);

      final Side helpMatingSide = report.havingMove();
      final var newResultTagValue = switch (helpMatingSide) {
        case BLACK -> ResultTagValue.BLACK_WON;
        case WHITE -> ResultTagValue.WHITE_WON;
        case NONE -> throw new IllegalArgumentException();
      };

      final List<Tag> newTagList = new ArrayList<>(pgnGame.tagList());

      updateTag(newTagList, StandardTag.RESULT.getName(), newResultTagValue.getValue());
      updateTag(newTagList, "Termination", "Adjudication");

      // play the existing moves
      final Board board = new Board();
      for (final HalfMove halfMove : report.board().getHalfMoveList()) {
        board.move(halfMove.moveSpecification());
      }

      // play the helpmating moves
      @SuppressWarnings("null") final var uciListAsText = havingHelpMate.get(pgnName);
      @SuppressWarnings("null") final @NonNull String[] uciMoveStrList = uciListAsText.split(" ");
      for (@NonNull final String uciMoveStr : uciMoveStrList) {
        final UciMove uciMove = UciMoveValidationUtility.lookup(uciMoveStr);
        final MoveSpecification moveSpecification = UciMoveUtility.convertUciMoveToMoveSpecification(board, uciMove);
        board.move(moveSpecification);
      }

      // write the file
      final String pgnNameCreate = calculatePgnNameCreate(pgnName);
      PgnWriter.writePgn(board, newTagList, OUTPUT_FOLDER_PATH, pgnNameCreate);
      logger.info("Wrote " + pgnNameCreate);
    }
  }

  private static String calculatePgnNameCreate(String pgnName) {
    final String pgnNameWithoutExtension = PgnExtensionUtility.removePgnExtension(pgnName);
    return PgnExtensionUtility.addPgnExtension(pgnNameWithoutExtension + "_helpmate");
  }

  // we print out the below and add it to the map below manually, so we can use it in the code
  private static void printMovesAsUci(String game, Report report) {
    final StringBuilder uciMoveList = new StringBuilder();
    for (final HalfMove halfMove : report.board().getHalfMoveList()) {
      final String uci = UciMoveUtility
          .convertMoveSpecificationToUci(halfMove.havingMove(), halfMove.moveSpecification()).text();
      uciMoveList.append(uci);
      uciMoveList.append(" ");
    }
    System.out.println(game + ";" + report.havingMove().getName() + ";" + uciMoveList.toString());
  }

  private static void populateHelpMateUci(Map<String, String> havingHelpMate) {
    havingHelpMate.put("lichess_03VIxJUJ.pgn",
        "a6a5 c2d3 b7b6 d3c2 a2a1q c2d3 c6h1 d3e2 c7c6 e2f2 h1g2 f2g2 a1h1 g2h1 a5b4 h1g1 b4c3 g1f1 c3d2 f1g2 c6c5 g2h1 d2e1 h1h2 e1f2 h2h1 f2g3 h1g1 b5b1");
    havingHelpMate.put("lichess_0dKUIfwq.pgn",
        "h8g7 e3d2 h7h8q d2c1 h8h1 c1b2 h1a1 b2b3 a1b1 b3c4 b1a1 c4d5 a1b1 d5c6 b1a1 c6b7 a1b1 b7a8 g7f6 a8a7 f6e5 a7a6 e5d4 a6a5 d4c4 a5a4 b1a1");
    havingHelpMate.put("lichess_0ehvDno9.pgn",
        "e5e1 h3h4 a3a4 h4g3 f5f6 g3h4 a2a3 h4g5 a4a5 g5h6 a3a4 h6h7 a5a6 h7g6 a6a7 g6f5 f6f7 f5g6 a4a5 g6h7 a5a6 h7g6 a7a8q g6f5 a6a7 f5g6 f7f8q g6h7 a8e4");
    havingHelpMate.put("lichess_0u6eh8ZP.pgn",
        "a5b5 b7c8 b5a4 c8b7 b4b5 b7a8 b5b6 a8b8 b6b7 b8c7 b7b8q c7d7 a4a5 d7c6 b8h2 c6b7 h2d2 b7a8 a5a6 a8b8 d2d8");
    havingHelpMate.put("lichess_0xMzcUDT.pgn",
        "c8c7 a2a1 d3d4 a1a2 d4d5 a2a3 f4f5 a3a4 d5d6 a4a3 f5f6 a3a2 d6d7 a2a1 e6e7 a1b1 f6f7 b1a1 d7d8q a1b1 e7e8q b1a1 f7f8q a1b1 c7b6 b1a1 b6a6 a1a2 d8d2 a2b3 d2d1 b3c4 d1a1 c4d5 e8e1 d5c6 a6a5 c6b7 f8f4 b7a8 a5b6");
    havingHelpMate.put("lichess_16myWOwv.pgn",
        "c2e1 f2g3 c4c3 g3f2 c3c2 f2g3 c2c1q g3f2 e1c2 f2g1 c2a1 g1h1 a1c2 h1g2 c2e1 g2f1 e1d3 f1g2 d3b2 g2h1 b2d3 h1g1 d3f2 g1f1 f2h1 f1g2 c1a1 g2h1 d1e1 h1h2 e1f1 h2h1 a1h8");
    havingHelpMate.put("lichess_1rcB0Tru.pgn",
        "h8g8 h6g6 c4c3 g6h6 c3c2 h6g6 c2c1q g6f6 a5a4 f6e5 a4a3 e5d4 a3a2 d4e5 a2a1q e5e6 g8g7 e6e7 a1f6");
    havingHelpMate.put("lichess_29moHRC8.pgn",
        "h4g4 e4d3 h6h5 d3e2 h5h4 e2f1 h4h3 f1e1 h3h2 e1d1 h2h1q d1c2 g4h3 c2d3 g5g4 d3e2 g4g3 e2e3 g3g2 e3d2 g2g1q d2e2 g1a1 e2f2 a1e1");
    havingHelpMate.put("lichess_2gCvxApk.pgn",
        "g1g2 c4b3 a3a4 b3c4 e5a1 c4d5 a4a5 d5c6 a5a6 c6b5 a6a7 b5c6 a7a8q c6b5 g2f1 b5b6 a1a5");
    havingHelpMate.put("lichess_2k5piUl6.pgn",
        "a2a1 g7f6 c2c3 f6e5 b3b4 e5f6 a1a2 f6e5 b2b3 e5d5 c3c4 d5c6 b4b5 c6b7 b3b4 b7a8 b5b6 a8b8 b6b7 b8a7 b7b8q a7b8 a2a3 b8a7 a3a4 a7b7 a4a5 b7c8 a5a6 c8b8 b4b5 b8a8 b5b6 a8b8 b6b7 b8c7 b7b8q c7b8 c4c5 b8a8 c5c6 a8b8 c6c7 b8a8 c7c8q");
    havingHelpMate.put("lichess_2mgjVvcC.pgn", "d8g5");
    havingHelpMate.put("lichess_2pwjX9B1.pgn",
        "h7g8 g5h6 b5b4 h6g5 b4b3 g5h6 b3b2 h6g5 a6a5 g5g4 b2b1q g4h5 g8f7 h5g4 f1g1 g4f3 g1f1 f3g2 f7e6 g2h3 e6f5 h3g2 f5g4 g2h2 f1f2");
    havingHelpMate.put("lichess_2sFHWmg3.pgn",
        "h7h8 f7e6 h6h7 e6d5 h8g7 d5c4 h7h8q c4b3 h8h1 b3a4 h1a1 a4b5 a1b1 b5a6 b1a1 a6b7 a1b1 b7a8 g7f6 a8a7 f6e5 a7a6 e5d4 a6a5 d4c4 a5a4 b1a1");
    havingHelpMate.put("lichess_2wVTK5vV.pgn",
        "g8f8 a5a4 h7h8q a4a3 h8a1 a3b4 a1b1 b4a5 b1a1 a5b6 a1b1 b6a7 b1c1 a7a8 f8e7 a8a7 e7d6 a7a6 d6c5 a6a5 c1a1");
    havingHelpMate.put("lichess_3bKyjTHM.pgn",
        "a1a2 f2e1 f4f3 e1d1 f3f2 d1e2 g4g3 e2d1 g3g2 d1e2 a5a4 e2d1 a4a3 d1e2 b2b1q e2f3 f2f1q f3g4 g2g1q g4h5 b1h7");
    havingHelpMate.put("lichess_3cYOGNS6.pgn",
        "e6g4 h5h6 g4h3 h6g7 g3g4 g7f6 f4f5 f6e5 g4g5 e5d6 f5f6 d6c5 g5g6 c5b4 f6f7 b4c5 g6g7 c5b4 f7f8q b4b3 g7g8q b3b2 f3e2 b2b1 e2d3 b1b2 g8g1 b2b3 g1b1");
    havingHelpMate.put("lichess_3jesiit8.pgn",
        "g1h1 b1a1 a2a3 a1a2 c2c3 a2b3 f2f3 b3a4 g2g3 a4a5 a3a4 a5b6 c3c4 b6a7 f3f4 a7a8 h1g1 a8a7 g1f1 a7a6 f1e2 a6b7 a4a5 b7a8 a5a6 a8b8 a6a7 b8b7 a7a8q b7b6 d3d6");
    havingHelpMate.put("lichess_3xFc21eB.pgn",
        "g7h8n e7d6 h8g6 d6c5 g6f4 c5b4 f4e2 b4c5 e2c1 c5b6 c1a2 b6a5 a2c3 a5b6 c3b1 b6a7 b1d2 a7a6 d2c4 a6b7 c4b2 b7a8 b2a4 a8a7 a4b6 a7a6 b6a8 a6a5 g8f7 a5a4 f7e6 a4b5 a8b6 b5c6 b6a4 c6b7 a4b2 b7a8 b2a4 a8a7 a4b6 a7a6 b6a8 a6a5 e6d5 a5a4 d5c4 a4a5 a8b6 a5b6 f3f1 b6b7 f1c1 b7a8 c4b5 a8b8 b5a6 b8a8 c1c8");
    havingHelpMate.put("lichess_49QwULSR.pgn",
        "h3g2 g7f6 h4h3 f6e5 h3h2 e5d4 g5g4 d4e3 g4g3 e3d2 h2h1q d2e3 g2h3 e3d2 g3g2 d2e3 g2g1r e3f2 h1f3 f2g1 f3e2 g1h1 e2d1");
    havingHelpMate.put("lichess_55wBEu8Z.pgn", "h4g5");
    havingHelpMate.put("lichess_6kfJPvsA.pgn",
        "h2h1 b5a4 g3g2 a4a3 h7h6 a3b2 h6h5 b2c1 h5h4 c1d1 h4h3 d1c1 h3h2 c1b1 g2g1q b1a2 h1g2 a2b3 h2h1q b3a2 g2h3 a2b3 g1a1 b3c4 a1b1 c4d4 b1a1 d4e3 a1b1 e3f2 b1e1");
    havingHelpMate.put("lichess_78XhRaIr.pgn",
        "h6g7 c3b2 g7g8q b2a1 g8g1 a1a2 g1a1 a2b3 a1b1 b3c4 b1a1 c4d5 a1b1 d5c6 b1a1 c6b7 a1b1 b7a8 h7g6 a8a7 g6f5 a7a6 f5e4 a6a5 e4d3 a5a4 d3c4 a4a3 b1a1");
    havingHelpMate.put("lichess_7alMRe6y.pgn",
        "c8d8 c6b7 e5e4 b7c6 e4e3 c6b7 e3e2 b7b8 e2e1q b8b7 b6b5 b7c6 f6f5 c6d5 d8d7 d5c5 d7e6 c5d4 e1a1 d4e3 b5b4 e3f2 b4b3 f2g3 b3b2 g3f2 b2b1b f2g1 f5f4 g1h1 f4f3 h1g1 f3f2 g1f1 e6f5 f1g2 c7c6 g2h1 f2f1q h1h2 a1e5");
    havingHelpMate.put("lichess_7fWsFh0J.pgn", "h1h2 f4e3 g3g2 e3d2 g2g1q d2d3 g1a1 d3e4 a1b1 e4f3 b1d3");
    havingHelpMate.put("lichess_8cTl7SrQ.pgn",
        "f5h7 f2f3 g4g2 f3g2 g5g4 g2h1 g4g3 h1g1 g3g2 g1h2 g2g1b h2h1 h4g4 h1g2 g1f2 g2f1 f2e1 f1e2 e1d2 e2d1 d2e1 d1c1 e1d2 c1b2 d2e1 b2a1 g4f3 a1b2 f3e2 b2a1 e2d3 a1b1 d3c3 b1c1 c3b4 c1d1 b4a3 d1c1 h7g8 c1b1 g8a2 b1a1 e1c3");
    havingHelpMate.put("lichess_APEww4mD.pgn", "h1g1 f3g4 h2h1q g4g5 c2c1 g5f4 c1c4 f4g3 h1g2");
    havingHelpMate.put("lichess_B8bpMZ71.pgn", "g1g2 e2d3 a2a3 d3e4 f2f3 e4d3 g2h3 d3e2 h3g4 e2d3 f3f4 d3e4 f6d4");
    havingHelpMate.put("lichess_bfJbt6GP.pgn",
        "b2b4 c3d2 f6f5 d2c1 e6d4 c1d2 f5f4 d2e1 f4f3 e1d1 f3f2 d1c1 f2f1q c1d2 g6g5 d2e3 d4e2 e3d2 e2g3 d2e3 g3h1 e3d2 c5d4 d2c2 d4e5 c2c3 e5f4 c3b4 f4g3 b4a3 g3h3 a3b2 b5b4 b2c2 b4b3 c2c3 b3b2 c3c2 b2b1q c2c3 f1d3");
    havingHelpMate.put("lichess_bQ0EXLal.pgn",
        "g8f8 c2b1 a6a5 b1a1 a5a4 a1b1 a4a3 b1a1 a3a2 a1b2 a2a1q b2a1 f8e7 a1b1 e7d6 b1a1 d6c5 a1a2 c5b4 a2b1 b4a3 b1a1 h3h1");
    havingHelpMate.put("lichess_cZ8QxYP8.pgn",
        "e1f2 a5a4 b2b3 a4b5 c2c3 b5c6 d2d3 c6d5 e2e3 d5e6 g2g3 e6f6 b3b4 f6g7 c3c4 g7h8 b1d2 h8g7 g1e2 g7f6 d2f3 f6g7 d3d4 g7h8 e2f4 h8g7 f3e5 g7f6 f4g6 f6g7 e5f7 g7f6 g6h8 f6g7 f7e5 g7h8 e5g6 h8g7 g6h8 g7f6 f2f3 f6g7 b4b5 g7h8 b5b6 h8g7 b6b7 g7f6 b7b8q f6g7 b8b1 g7h8 f3f4 h8g7 f4g5 g7g8 g5h6 g8h8 b1h7");
    havingHelpMate.put("lichess_D4vIOthl.pgn",
        "h6g7 d4c3 h5h4 c3d2 h4h3 d2c1 h3h2 c1b1 g2g1q b1a2 h2h1q a2b3 g1a1 b3c4 a1b1 c4d4 b1a1 d4e3 a1b1 e3f2 b1e1");
    havingHelpMate.put("lichess_dBergEkt.pgn",
        "h8g8 b8a7 f3f2 a7a6 g4g3 a6a5 g3g2 a5a4 f2f1q a4a3 g2g1q a3a2 f1a1 a2b3 a1b1 b3c4 b1a1 c4d5 a1c1 d5e4 c1a1 e4f3 g1b1 f3g2 g8f7 g2h3 f7e6 h3g2 e6f5 g2f2 f5g4 f2e3 g4h3 e3f2 h3g4 f2g2 a1a2");
    havingHelpMate.put("lichess_DiKq9jcy.pgn", "d6d5 g7h8 e5e6 h8g7 e6e7 g7f6 g6g7 f6g7 e7e8q g7f6 h7h8q");
    havingHelpMate.put("lichess_E9P918aj.pgn",
        "h3g4 d4c3 g4g5 c3b4 g5g6 b4a3 g6g7 a3a2 g7g8q a2a1 h4g3 a1b1 g3f2 b1a1 f2e2 a1b1 e2d3 b1a1 d3c4 a1b1 c4b5 b1a1 b5a6 a1b2 g8g1 b2c3 g1a1 c3b4 a1b1 b4c5 b1a1 c5c6 a6a5 c6b7 a1d1 b7a8 a5a6 a8b8 d1d8");
    havingHelpMate.put("lichess_eExHpNDf.pgn",
        "g1g2 e4d3 g3g4 d3c4 g4g5 c4b5 g5g6 b5a4 g6g7 a4a3 g7g8q a3b4 d8c6 b4c5 c6b4 c5b4 g2f1 b4a3 f1e2 a3b4 e8a8 b4c5 e7e8q c5b4 e2d3 b4c5 a8a1 c5b6 a1b1 b6a7 e8a4");
    havingHelpMate.put("lichess_eKwKl6Y9.pgn",
        "h3g3 c1b1 h5h6 b1a1 h6h7 a1b1 h7h8q b1c2 h8a1 c2d3 a1b1 d3d2 g3f2 d2c3 b1a1 c3b4 a1b1 b4a5 b1a1 a5b6 a1b1 b6a7 b1c1 a7a8 f2e2 a8a7 e2d3 a7a6 d3c4 a6b7 c1b1 b7a8 c4b5 a8b8 b5a6 b8a8 b1b7");
    havingHelpMate.put("lichess_EQCMW0jB.pgn",
        "g1h2 f4e3 a4a3 e3e4 a3a2 e4d3 a2a1q d3e4 h5a5 e4d3 h4h3 d3e4 a5a4 e4f3 a1a3");
    havingHelpMate.put("lichess_EsFX1Urt.pgn",
        "h5g4 e5d4 h6h5 d4e3 h5h4 e3f2 h4h3 f2e1 h3h2 e1d1 h2h1q d1c2 g4h3 c2d3 h1a1 d3e2 a1b1 e2f3 h3h4 f3g2 b1a2 g2h1 h4h3 h1g1 a2g2");
    havingHelpMate.put("lichess_EstAIWqd.pgn",
        "h2h1 b3c4 g2g1 c4b3 g3g2 b3a2 h1h2 a2b3 g1a1 b3c4 g2g1q c4b3 h2h3 b3c4 a1b1 c4d5 b1a1 d5e4 a1e1 e4f3 g1f1");
    havingHelpMate.put("lichess_Ew7uTqu0.pgn",
        "g8h8 f4g5 a7a6 g5f4 g6d3 f4f3 a6a5 f3g2 a5a4 g2h1 h8h7 h1g1 h7g6 g1h2 g6f5 h2h1 f5g4 h1g1 g4h3 g1h1 a3a1");
    havingHelpMate.put("lichess_f7x3bSzh.pgn", "b5c6");
    havingHelpMate.put("lichess_FMDN8gJ7.pgn", "c4b3 e1d1 c5c4 d1c1 c4c3 c1b1 c3c2 b1c1 d4e3");
    havingHelpMate.put("lichess_fN4vRqp0.pgn", "g1f2 h3h2 g5g2");
    havingHelpMate.put("lichess_fPnoYQtW.pgn",
        "h1h2 b5a4 h5h4 a4b3 h4h3 b3a2 g2g1q a2b3 g1a1 b3c4 a1b1 c4d5 b1a1 d5e4 h2g1 e4f3 h3h2 f3e2 h2h1q e2e3 g1h2 e3d2 h2h3 d2e3 a1b1 e3f2 b1e1");
    havingHelpMate.put("lichess_FYTeO80q.pgn", "e6g4");
    havingHelpMate.put("lichess_G0xDBYwE.pgn",
        "c4b5 h8g7 b5b6 g7f6 b6b7 f6e5 b7b8q e5d4 c5b3 d4d5 b3a1 d5c6 a1c2 c6d7 c2a3 d7c6 a3c4 c6d7 c4b6 d7c6 b8h2 c6b7 b6a4 b7a8 a4b6 a8a7 h2c7");
    havingHelpMate.put("lichess_G8i5Bn7F.pgn",
        "h7h8 f6e5 g2g1q e5f6 g1a1 f6f5 a1b1 f5f4 b1a1 f4f3 a1b1 f3g2 b1a2 g2h1 h8g7 h1g1 g7f6 g1f1 f6f5 f1e1 f5g4 e1d1 g4h3 d1e1 a2a1 e1f2 a1a2 f2g1 a2b2 g1h1 b2a1");
    havingHelpMate.put("lichess_GCuwJ5bP.pgn",
        "a4b5 g4f3 b5b6 f3e2 b6b7 e2d1 b7b8q d1c1 b8b1 c1d2 b1a1 d2e3 a1b1 e3d4 b1a1 d4c5 a1b1 c5c6 b1a1 c6b7 a1d1 b7a8 a5a6 a8b8 d1d8");
    havingHelpMate.put("lichess_GIwBNz5S.pgn",
        "f1g1 d3c2 a5a4 c2b1 a4a3 b1a1 a3a2 a1b2 b6b5 b2a1 g1f1 a1b2 a2a1q b2a1 f1e1 a1b2 e1d1 b2c3 d1c1 c3d3 c1b2 d3e2 b2a3 e2f2 a3b2 f2e1 b2c1 e1f2 b5b4 f2g1 b4b3 g1f1 b3b2 f1e1 b2b1q e1f2 b1a1 f2g1 a1b1 g1h1 c1d1 h1g1 d1e1 g1h2 e1f1 h2h1 b1h7");
    havingHelpMate.put("lichess_gtzPUYWo.pgn", "e4d3 f1g1 d3d2 g1f1 f3f2 f1g2 f4f3 g2f1 d2d1q");
    havingHelpMate.put("lichess_GzoCTk46.pgn",
        "f4e3 e1d1 g4g3 d1c1 g3g2 c1b1 f5f4 b1c1 f4f3 c1b1 f3f2 b1a1 f2f1q a1a2 g2g1q a2b3 h6h5 b3c2 e3f4 c2c3 f4g3 c3b2 g3h3 b2c3 h5h4 c3d2 f1a1 d2e2 a1c1 e2f3 c1e3");
    havingHelpMate.put("lichess_hdott1S4.pgn",
        "f4h2 f3e2 g5g6 e2d1 g6g7 d1c1 g7g8q c1b1 h5g3 b1c2 g3f1 c2d3 f1d2 d3c2 d2f3 c2b1 f3e5 b1a1 e5g6 a1b1 g6h8 b1a1 h4g5 a1b1 g5h6 b1c2 h8g6 c2d3 g6f4 d3e4 f4g6 e4d3 g6h8 d3e4 h2g1 e4e5 g1h2 e5f6 g8f7");
    havingHelpMate.put("lichess_hkiG96je.pgn",
        "h8g8 a3a2 h6h7 a2a1 h7h8q a1b1 h8a1 b1c2 a1b1 c2c3 b1a1 c3b4 a1b1 b4a5 b1a1 a5b6 a1b1 b6a7 b1c1 a7a8 g8f7 a8a7 f7e6 a7a6 e6d5 a6a5 d5c4 a5a4 c1a1");
    havingHelpMate.put("lichess_IcjtzT9i.pgn",
        "h3g4 c4b3 f3f4 b3a4 f4f5 a4a5 g4g5 a5a6 f5f6 a6a5 g5g6 a5a4 f6f7 a4a3 g6g7 a3a2 f7f8q a2a1 g7g8q a1b1 h4g3 b1a1 g3f2 a1b1 f2e2 b1a1 e2d3 a1b1 d3c4 b1a1 c4b5 a1b1 b5a6 b1c2 f8f1 c2c3 f1a1 c3b4 a1b1 b4c5 b1a1 c5c6 a6a5 c6b7 g8g3 b7a8 a5b6");
    havingHelpMate.put("lichess_if6AKvpb.pgn",
        "e3d2 d5d6 a2a3 d6c7 a3a4 c7b7 f3f4 b7a8 d2c3 a8a7 c3c4 a7b7 c4b5 b7c8 b5a6 c8b8 a4a5 b8a8 e4e8");
    havingHelpMate.put("lichess_ig7fudY7.pgn", "h3h4 f4f5 g2g1q f5e4 b3b1 e4d3 h4h3 d3e2 b1e1 e2f3 g1f1");
    havingHelpMate.put("lichess_Iy1VEUMC.pgn",
        "d8e8 g7f6 a4a5 f6e5 a5a6 e5d4 a6a7 d4c3 a7a8q c3b2 c7c8q b2b3 a8a1 b3b4 a1b1 b4a3 c8a6");
    havingHelpMate.put("lichess_j17qnxd0.pgn",
        "h8g7 e8d7 h6h5 d7c6 h5h4 c6b5 h4h3 b5a4 h3h2 a4a3 h2h1q a3a2 g7f6 a2b3 h1a1 b3c4 a1b1 c4d5 b1a1 d5e4 a1b1 e4f3 b1a1 f3g2 a1a2 g2h1 f6f5 h1g1 f5g4 g1f1 g4h3 f1g1 a2b2 g1h1 b2a1");
    havingHelpMate.put("lichess_j4phmaNj.pgn",
        "f8g8 e6d5 h4h5 d5c4 h5h6 c4b3 h6h7 b3a2 h7h8q a2b3 h8a1 b3c4 a1b1 c4d5 b1a1 d5c6 a1c1 c6b7 c1b1 b7a8 g8f7 a8a7 f7e6 a7a6 e6d5 a6a5 d5c4 a5a4 b1a1");
    havingHelpMate.put("lichess_jehHS0Jj.pgn",
        "h5g5 g7h8 h3h4 h8g7 h4h5 g7f8 h5h6 f8e7 h6h7 e7d6 h7h8q d6c5 g5f4 c5b4 f4e3 b4a3 e3d3 a3a2 d3c4 a2a3 h8a1");
    havingHelpMate.put("lichess_JfHZLRvD.pgn", "c4e4");
    havingHelpMate.put("lichess_jK46bYGo.pgn",
        "g8h8 d5c4 a7a6 c4d3 e6e2 d3e2 f7f1 e2f1 a6a5 f1g1 a5a4 g1h1 a4a3 h1g1 a3a2 g1f1 a2a1q f1e2 h8h7 e2f3 g7g6 f3g2 a1a2 g2h1 h7h6 h1g1 h6g5 g1f1 g5g4 f1e1 g4h3 e1f1 g6g5 f1g1 g5g4 g1h1 g4g3 h1g1 a2a1");
    havingHelpMate.put("lichess_jnox9kxK.pgn",
        "c1b2 f5e4 a3a2 e4d3 b2b1 d3e2 b1c1 e2f3 c1b1 f3g2 b1h1 g2h1 a1b1 h1g1 a2a1q g1f1 b1c1 f1g1 a1b1 g1h1 c1d1 h1g1 d1e1 g1h2 e1f1 h2h1 b1h7");
    havingHelpMate.put("lichess_jnvJUlDw.pgn",
        "h6h7 c4b3 g5g4 b3c2 g4g3 c2b1 g3g2 b1a1 g2g1q a1a2 e6d4 a2a3 d4e2 a3a2 e2g3 a2b3 g6g5 b3c2 g3h1 c2c3 h7g6 c3b2 g6f5 b2c3 d6d2 c3d2 g5g4 d2e2 g4g3 e2f3 g3g2 f3e2 f5g4 e2d3 g4h3 d3e2 g1a1 e2f3 g2g1q f3e2 a1d1");
    havingHelpMate.put("lichess_KbEG3yK3.pgn",
        "a1b1 f2e1 h6h5 e1f1 h5h4 f1g1 h4h3 g1f1 h3h2 f1e1 h2h1q e1e2 b1c1 e2e3 c1d1 e3f4 d1e1 f4e3 e1f1 e3d2 f1g2 d2e3 g2h3 e3f2 h1f3 f2g1 f3e2 g1h1 e2d1");
    havingHelpMate.put("lichess_kbutR5IJ.pgn", "e3e8");
    havingHelpMate.put("lichess_Kdf0f1OE.pgn",
        "h2g1 f4e3 h3h2 e3d2 h2h1q d2c1 h1g2 c1d1 g2h1 d1e1 g1h2 e1d2 h2h3 d2e3 h1a1 e3f2 a1a2 f2g1 a2b2 g1h1 b2a1");
    havingHelpMate.put("lichess_KE47c49r.pgn",
        "h7h8 c3b2 h6h5 b2c1 h5h4 c1d1 h4h3 d1c1 h3h2 c1b1 h2h1q b1a2 h8h7 a2b3 f7f6 b3c2 f6f5 c2d3 e4e2 d3e2 f5f4 e2f2 f4f3 f2g3 f3f2 g3f2 h7h6 f2g3 h6g5 g3f2 g5g4 f2e3 g4h3 e3f2 h1f3 f2g1 f3e2 g1h1 e2d1");
    havingHelpMate.put("lichess_knQsF7R8.pgn",
        "g8f8 d7c6 g4g5 c6b5 g5g6 b5a4 g6g7 a4a3 g7g8q a3b4 h7h8q b4a3 f8e7 a3b4 g8g1 b4b5 g1a1 b5c6 a1c1 c6b7 h8b2 b7a8 c1a1");
    havingHelpMate.put("lichess_KP23RUWa.pgn",
        "b3h3 f6e5 a2a3 e5d4 h5g4 d4e5 g4f3 e5d4 f3e2 d4e5 e2d3 e5f6 d3c4 f6e5 a3a4 e5d6 a4a5 d6c7 a5a6 c7b6 a6a7 b6a5 a7a8q a5b6 a8a1 b6b7 a1b1 b7a8 h3a3");
    havingHelpMate.put("lichess_KPvcgsfS.pgn",
        "c8b8 e6d5 g3g4 d5e6 b8a7 e6e5 a7a6 e5d5 c7c6 d5c6 a6a5 c6b7 g4g5 b7a8 g5g6 a8a7 g6g7 a7b8 a5a6 b8a8 g7g8q");
    havingHelpMate.put("lichess_Ks0yR98N.pgn",
        "h8h4 f3e2 g7g8q e2d1 h4e1 d1c2 e1a1 c2d3 a1b1 d3d4 b1a1 d4c5 a1b1 c5c6 b1a1 c6b7 h3g2 b7c7 g2f1 c7b6 f1e2 b6c7 e2d3 c7b6 d3c4 b6c7 c4b5 c7d7 b5a6 d7c6 a6a5 c6b7 g8g3 b7a8 a5b6");
    havingHelpMate.put("lichess_LC76ur18.pgn",
        "a1a2 c3d4 c6c5 d4e5 a2b2 e5f6 b2c1 f6e5 c5c4 e5f4 c4c3 f4e3 c3c2 e3f4 c1d1 f4e3 c2c1q e3d4 b1e4");
    havingHelpMate.put("lichess_lHtqM0fz.pgn",
        "h1g1 g4f3 h2h1q f3e2 h1h4 e2f3 g1h2 f3e2 h2h3 e2f1 h4a4 f1g1 a4a2 g1h1 a2a1");
    havingHelpMate.put("lichess_lk42iihk.pgn",
        "h2h1 g4h5 g3g4 h5g4 f2e1 g4f3 e1a1 f3e4 a7a2 e4d5 a2b2 d5c6 b2a2 c6b7 h1g1 b7c8 g1f1 c8b7 f1e2 b7c8 e2d3 c8b7 d3c4 b7c8 c4b5 c8b7 b5a4 b7a8 a4a5 a8b8 a5a6 b8a8 a1h8");
    havingHelpMate.put("lichess_LqTNOgyT.pgn",
        "d6d1 f3f4 a4a5 f4e4 a5a6 e4f5 a6a7 f5e4 a7a8q e4f5 f1e2 f5g6 e2d3 g6f5 h4h5 f5e6 h5h6 e6f7 h6h7 f7e6 h7h8q e6f7 a8e8");
    havingHelpMate.put("lichess_lYf34Uiq.pgn",
        "g1f1 c5b4 h2h1q b4a3 h1g1 a3b2 g1h1 b2c1 h1g1 c1d1 f1g2 d1c2 g2h3 c2d3 g1a1 d3e2 a1b1 e2f3 h3h4 f3g2 b1a2 g2h1 h4h3 h1g1 a2g2");
    havingHelpMate.put("lichess_m5WDCW16.pgn",
        "g2h1 g7f6 f2f3 f6g7 h2g1 g7f6 f3f4 f6e7 g3g4 e7d6 h3h4 d6c7 f4f5 c7b8 f5f6 b8a7 f6f7 a7a6 f7f8q a6a7 g1f1 a7a6 f1e2 a6a7 e2d3 a7b6 d3c4 b6c7 c4b5 c7d7 b5a6 d7c7 h5h7");
    havingHelpMate.put("lichess_McLow5Hz.pgn",
        "h2h1 e5d4 f3f4 d4c5 f4f5 c5b6 f5f6 b6a5 f6f7 a5a4 f7f8q a4b5 h7h8q b5a4 h1g1 a4b5 e4b1 b5c6 b1a2 c6b7 g1f1 b7a6 f1e2 a6a5 e2d3 a5a4 d3c4 a4a5 a2b1 a5b6 b1a2 b6b7 c4b5 b7c7 g3g7");
    havingHelpMate.put("lichess_MvEif0NV.pgn",
        "h5g5 c5b4 h4h5 b4a5 h5h6 a5a4 h6h7 a4a3 h7h8q a3a2 g5f4 a2b3 g4g5 b3c4 g5g6 c4b3 g6g7 b3a2 g7g8q a2a3 g8g1 a3b4 g1a1 b4c5 a1b1 c5c6 b1a1 c6b7 f4e3 b7c7 e3d3 c7b6 d3c4 b6c7 c4b5 c7d7 b5a6 d7c6 a6a5 c6b7 h8h2 b7a8 a5b6");
    havingHelpMate.put("lichess_N6AYM22R.pgn",
        "f4h5 h1g2 a3a2 g2f1 f7f6 f1g1 f6f5 g1h1 a2a1q h1g2 h5g3 g2h3 g3h1 h3g2 a1a2 g2h1 g6g5 h1g1 g5g4 g1f1 g4h3 f1g1 f5f4 g1h1 f4f3 h1g1 f3f2 g1h1 f2f1q");
    havingHelpMate.put("lichess_nvxFBquo.pgn", "h8h7 f7e6 d6d7 e6d5 c7c8q d5e6 d7d8q e6f7 g3f3");
    havingHelpMate.put("lichess_om0bCR5w.pgn",
        "h1a8 f6e5 h6h5 e5d4 f8e7 d4c5 e7e6 c5b4 e6f5 b4c5 h5h4 c5d4 h4h3 d4c5 h3h2 c5b4 h2h1q b4c5 g3g1 c5d4 g1a1 d4e3 a1b1 e3f2 h1e1");
    havingHelpMate.put("lichess_Omq6Fdhm.pgn",
        "a3b2 e2d1 a4a3 d1e2 a3a2 e2d1 b4b3 d1e2 a2a1q e2f3 a1b1 f3e2 b2c1 e2f3 b3b2 f3f2 c1d1 f2g3 d1e1 g3h4 e1f1 h4g3 b1a1 g3h2 b2b1q h2h1 a1h8");
    havingHelpMate.put("lichess_ooSZuhhE.pgn",
        "c7b8 e7d6 c6c5 d6d5 c5c4 d5d4 c4c3 d4d3 c3c2 d3d2 c2c1q d2d3 b8c7 d3e4 c1a1 e4f3 a1b1 f3g2 b1a2 g2h1 c7d6 h1g1 d6e5 g1f1 e5f4 f1e1 f4g3 e1d1 g3h3 d1e1 a2a1 e1f2 a1a2 f2g1 a2b2 g1h1 b2a1");
    havingHelpMate.put("lichess_p4CeCK3J.pgn",
        "b2a1 h1h2 c2c3 h2g2 b3b4 g2f3 e8a4 f3e4 c5g1 e4d5 a4d1 d5c6 c3c4 c6b7 b4b5 b7a8 b5b6 a8b8 b6b7 b8c7 b7b8q c7b8 c4c5 b8a8 c5c6 a8b8 c6c7 b8c8 a1a2 c8b7 c7c8q b7c8 a2a3 c8b7 d1a1 b7a8 a3a4 a8b8 a4a5 b8c8 a5a6 c8b8 a1b1 b8a8 b1b7");
    havingHelpMate.put("lichess_PAjZeTkN.pgn", "a2b3 b6b7 b3b4 b7a8 b4b5 a8a7 b5b6 a7a8 b6b7 a8a7 b7b8q");
    havingHelpMate.put("lichess_pFsUTatm.pgn",
        "e6f7 c6b5 e3e4 b5c6 d4d5 c6b7 c5c6 b7a6 d5d6 a6a7 c6c7 a7a6 d6d7 a6b7 c7c8q b7b6 d7d8q b6a7 e5a1");
    havingHelpMate.put("lichess_PIL4PUtT.pgn",
        "h2g1 g4f3 a3a4 f3g4 c5c6 g4f3 a4a5 f3e4 a5a6 e4f5 a6a7 f5e4 c6c7 e4f5 a7a8q f5g5 c7c8q g5h4 d6h6");
    havingHelpMate.put("lichess_PkZ6qiA6.pgn",
        "h1e4 a2b3 a3a2 b3a2 c5c4 a2b2 c4c3 b2a1 c3c2 a1b2 h7h6 b2c3 h6h5 c3d2 c2c1q d2c1 d5e5 c1d2 e4b1 d2e3 h5h4 e3f2 h4h3 f2g3 h3h2 g3f2 h2h1q f2g3 b1g1");
    havingHelpMate.put("lichess_QirOKYTP.pgn", "e3d2");
    havingHelpMate.put("lichess_QnFE4Znl.pgn",
        "h2g1 c1b1 h3h4 b1a2 g3e1 a2b3 g2g3 b3c4 g3g4 c4d5 g4g5 d5c6 h4h5 c6b7 g5g6 b7a6 h5h6 a6b7 g6g7 b7a6 h6h7 a6b7 g7g8q b7a6 h7h8q a6b7 g1f1 b7a6 f1e2 a6b7 e2d3 b7a6 d3c4 a6b7 c4b5 b7c7 e1e7");
    havingHelpMate.put("lichess_RF0MOp86.pgn",
        "a7a8 d1e2 a5a4 e2d1 a8b7 d1e2 b6b5 e2f1 b5b4 f1g1 b4b3 g1f1 b3b2 f1e1 b2b1q e1d2 c4c2");
    havingHelpMate.put("lichess_RSqLhejA.pgn",
        "g1h2 f3e4 c4c3 e4d3 c3c2 d3e4 e6e5 e4d3 e7f5 d3e4 f5e3 e4f3 e3d1 f3e4 d1f2 e4f5 f2h1 f5e4 h1g3 e4f3 h2e2");
    havingHelpMate.put("lichess_RvN1zpCB.pgn", "d2h2");
    havingHelpMate.put("lichess_rVN4T4Tz.pgn",
        "a8b7 g1f1 a5a6 f1e1 a6a7 e1d1 a7a8q d1c1 a8a1 c1d2 a1b1 d2e3 b1a1 e3e4 a1e1 e4d5 b7a6 d5c6 a6a5 c6b7 e1d1 b7a8 a5a6 a8b8 d1d8");
    havingHelpMate.put("lichess_SDy9Y3D0.pgn",
        "d3d4 g4h5 d4d1 h5g4 d1a1 g4g3 a1b1 g3g2 b1h1 g2h1 e4f3 h1g1 f3g4 g1f1 g4h3 f1g1 e5e4 g1h1 e4e3 h1g1 e3e2 g1h1 e2e1q");
    havingHelpMate.put("lichess_Sdz1yTzS.pgn",
        "h6g6 e1d1 a2a3 d1c2 a3a4 c2b3 g3g4 b3a4 g4g5 a4a5 g6f5 a5a4 g5g6 a4a3 g6g7 a3a2 g7g8q a2a1 f5e4 a1b1 e4d3 b1a1 d3c4 a1b1 c4b5 b1a1 b5a6 a1b2 g8g1 b2c3 g1a1 c3b4 a1b1 b4c5 b1a1 c5c6 a6a5 c6b7 a1d1 b7a8 a5a6 a8b8 d1d8");
    havingHelpMate.put("lichess_sMv8Hh43.pgn", "c4c5");
    havingHelpMate.put("lichess_sp0oSyY2.pgn",
        "a5b5 h5g4 a7a6 g4f3 a6a5 f3e2 b5c4 e2f3 a5a4 f3g2 a4a3 g2h3 c4d3 h3g2 c1h1 g2h1 d3e2 h1g1 e2f3 g1f1 f3g4 f1e1 g4h3 e1f1 b7b6 f1g1 a2b1 g1h1 a3a2 h1g1 a2a1q g1h1 b1a2");
    havingHelpMate.put("lichess_SwCSdv8K.pgn",
        "d1e1 d3c2 g6g5 c2d3 g5g4 d3e4 g4g3 e4d3 g3g2 d3c2 g2g1q c2b1 e1f1 b1a1 f1g2 a1a2 g2h3 a2b3 g1a1 b3c4 a1b1 c4d5 b1a1 d5e4 a1b1 e4f3 h3h4 f3g2 b1a2 g2h1 h4h3 h1g1 a2g2");
    havingHelpMate.put("lichess_SX3iSehH.pgn",
        "g8f7 e6e5 a5a6 e5d4 a6a7 d4c3 a7a8q c3b2 f7f1 b2c3 f1a1 c3b4 a1b1 b4c5 b1a1 c5b6 a1a5");
    havingHelpMate.put("lichess_Szu1FAr2.pgn",
        "h7h8 g2f1 c6c5 f1g2 c5c4 g2h1 c4c3 h1h2 c3c2 h2h3 c2c1q h3g2 c1h1 g2h1 h8h7 h1h2 h7h6 h2h3 h6g5 h3g2 d4b2 g2h1 g5g4 h1g1 g4h3 g1h1 b2a1");
    havingHelpMate.put("lichess_U5uv4wPs.pgn",
        "h5h6 h2h3 e4d2 h3h2 d2b1 h2h1 b1c3 h1h2 c3d1 h2h3 d1f2 h3h2 f2h1 h2h3 h6h5 h3h2 g4a4 h2h1 h5g4 h1g1 g4h3 g1h1 a4a1");
    havingHelpMate.put("lichess_uBSFb2kx.pgn",
        "h2h1 d7c6 g3e1 c6d5 g2g3 d5e5 g3g4 e5f6 g4g5 f6g7 g5g6 g7f6 g6g7 f6e5 g7g8q e5d4 h1g2 d4e5 e1d2 e5f6 g8a2 f6g7 d2h6 g7h8 h6f8");
    havingHelpMate.put("lichess_UdUE6AvZ.pgn",
        "a6b7 g4f3 b7b8q f3e2 b8b1 e2f3 b1a1 f3e4 a1e1 e4d5 a7a6 d5c6 a6a5 c6b7 e1d1 b7a8 a5a6 a8b8 d1d8");
    havingHelpMate.put("lichess_UuNO3dGf.pgn",
        "b4b1 f3g4 a3a4 g4f3 a4a5 f3e4 a5a6 e4d3 a6a7 d3c2 b6b7 c2b1 a7a8q b1c2 b7b8q c2d3 a8a1 d3e4 a1b1 e4d5 b1a1 d5c6 b8h2 c6b7 a1b1 b7a8 b1b8");
    havingHelpMate.put("lichess_uwwUPCFw.pgn",
        "h5g4 e3d2 h6h5 d2e1 h5h4 e1f1 h4h3 f1e1 h3h2 e1d1 h2h1q d1c2 g4h3 c2d3 h1a1 d3e2 a1b1 e2f3 h3h4 f3g2 b1a2 g2h1 h4h3 h1g1 a2g2");
    havingHelpMate.put("lichess_ux9sCCZ8.pgn", "e8f8");
    havingHelpMate.put("lichess_v4xJHVln.pgn",
        "g2h1n d2c1 g3g2 c1b1 h4h3 b1a1 g2g1q a1a2 h1f2 a2b3 f2d1 b3c4 d1b2 c4d5 b2d1 d5e6 d1f2 e6d5 f2d3 d5e4 h2h1 e4f3 h3h2 f3e2 d3f2 e2e3 h1g2 e3d2 h2h1q d2e3 g2h3 e3e2 g1e1");
    havingHelpMate.put("lichess_VdYuP1l4.pgn",
        "a1b2 h2g1 a5a4 g1h1 a4a3 h1g1 a3a2 g1f1 a2a1q f1e2 b2c1 e2f3 a1b1 f3g2 b1a1 g2h1 c1d1 h1g1 d1e1 g1h2 e1f1 h2h1 a1h8");
    havingHelpMate.put("lichess_VeCbG5uw.pgn",
        "f1f2 f4g5 f2e1 g5f4 g4g5 f4e4 g5g6 e4d3 g6g7 d3c2 g7g8q c2b1 e1d2 b1a1 d2c3 a1b1 c3c4 b1a1 c4b5 a1b1 b5a6 b1c2 g8g1 c2d3 g1a1 d3e4 a1b1 e4d5 b1a1 d5c6 a6a5 c6b7 a1d1 b7a8 a5a6 a8b8 d1d8");
    havingHelpMate.put("lichess_VIdrelSz.pgn", "f4g5");
    havingHelpMate.put("lichess_Vnqfiwd6.pgn",
        "g8h8 b6c7 e6e5 c7c6 e5e4 c6d5 e4e3 d5c4 e3e2 c4b3 e2e1q b3a2 h8h7 a2b3 g6g5 b3c2 g5g4 c2d3 g4g3 d3c2 g3g2 c2d3 g2g1q d3c2 h7g6 c2d3 e1a1 d3e2 f7f6 e2f3 g1b1 f3g2 g6f5 g2f2 f5g4 f2g2 a1a2");
    havingHelpMate.put("lichess_vS3VdvtP.pgn",
        "d4d8 e6f5 e5e4 f5e4 c3d4 e4d3 g7g6 d3c2 g6g5 c2b1 d4a1 b1a1 g5g4 a1b1 g4g3 b1a1 g3g2 a1b1 h7h6 b1c1 g2g1q c1b2 g8f7 b2c3 d8d2 c3d2 h6h5 d2e2 h5h4 e2f3 h4h3 f3e2 h3h2 e2f3 h2h1q f3e2 f7e6 e2d3 e6f5 d3c2 f5g4 c2d3 g1a1 d3e2 a1b1 e2f2 b1e1");
    havingHelpMate.put("lichess_vytUGbcM.pgn",
        "h8h7 d5c4 g7g8q c4c3 g8g1 c3b4 g1a1 b4c5 a1b1 c5c6 b1a1 c6b7 a1b1 b7a8 h7g6 a8a7 g6f5 a7a6 f5e4 a6a5 e4d3 a5a4 d3c4 a4a3 b1a1");
    havingHelpMate.put("lichess_W5huoA6x.pgn",
        "b7c8 c5b4 a7a6 b4c3 a6a5 c3d2 a5a4 d2e1 a4a3 e1d1 a3a2 d1c1 a2a1q c1d2 a1b1 d2e3 b1a1 e3f2 a1b1 f2g2 b1a2 g2h1 c8d7 h1g1 d7e6 g1f1 e6f5 f1e1 f5g4 e1d1 g4h3 d1e1 a2a1 e1f2 a1a2 f2g1 a2b2 g1h1 b2a1");
    havingHelpMate.put("lichess_wCL9HARO.pgn",
        "g8g7 h3g2 h2h3 g2f1 g7f6 f1e1 f6e5 e1d1 e5d4 d1c1 d4c4 c1b1 c4b5 b1a1 b5a6 a1a2 h3h4 a2a3 h4h5 a3a4 h5h6 a4a3 h6h7 a3a2 h7h8q a2b3 h8a1 b3c4 a1b1 c4d5 b1a1 d5c6 a6a5 c6b7 a1d1 b7a8 a5a6 a8b8 d1d8");
    havingHelpMate.put("lichess_WdedP6nc.pgn",
        "a2a1 g5f4 b3b2 f4e3 b2b1q e3d2 b1c1 d2e2 c1b1 e2f3 b1c1 f3g2 c1b2 g2h1 a1b1 h1g1 b1c1 g1f1 c1d1 f1g1 b2a1 g1h1 d1e1 h1h2 e1f1 h2h1 a1h8");
    havingHelpMate.put("lichess_wUCLya3K.pgn",
        "d1d2 b6a5 g6g5 a5b4 g5g4 b4c5 g4g3 c5b4 g3g2 b4a3 g2g1q a3a2 d2e1 a2a1 e1f1 a1b1 f7f6 b1c1 f6f5 c1d1 f1g2 d1c2 g2h3 c2d3 f5f4 d3e2 f4f3 e2d3 f3f2 d3c2 f2f1q c2d2 f1a1 d2e2 a1c1 e2f3 c1e3");
    havingHelpMate.put("lichess_wuHnMP2q.pgn", "d2h2");
    havingHelpMate.put("lichess_xD85FRxa.pgn",
        "h8g8 e6d5 f2f3 d5e6 g5c1 e6f6 g8h7 f6e6 h7h6 e6f7 c1b2 f7g8 b2h8 g8h8 f3f4 h8g8 f4f5 g8f7 f5f6 f7e6 f6f7 e6d5 f7f8q d5c4 h6g5 c4b3 g5f4 b3a2 f4e3 a2a1 e3d3 a1b1 d3c4 b1a1 c4b5 a1b1 b5a6 b1a2 f8f1 a2b3 f1a1 b3c4 a1b1 c4d5 b1a1 d5c6 a6a5 c6b7 a1d1 b7a8 a5a6 a8b8 d1d8");
    havingHelpMate.put("lichess_xfvaW7PK.pgn",
        "h7g8q f7f6 g8g1 f6e5 g1a1 e5d5 a1b1 d5c6 b1a1 c6b7 a1b1 b7a8 h8g7 a8a7 g7f6 a7a6 f6e5 a6a5 e5d4 a5a4 d4c4 a4a3 b1a1");
    havingHelpMate.put("lichess_xmpqlKFo.pgn",
        "a7b7 f1e1 a2a3 e1d2 a3a4 d2c3 a4a5 c3b4 a5a6 b4a3 a6a7 a3a2 a7a8q a2b1 b7a6 b1a2 a8h1 a2b3 h1a1 b3c4 a1b1 c4d5 b1a1 d5c6 a6a5 c6b7 a1d1 b7a8 a5a6 a8b8 d1d8");
    havingHelpMate.put("lichess_XtJJQxwF.pgn", "g2h1q f3e2 h1a1 e2f3 h3h4 f3g2 a1a2 g2h1 h4h3 h1g1 a2g2");
    havingHelpMate.put("lichess_XtvgUXHD.pgn",
        "g5g6 b4a3 a7a6 a3b2 a6a5 b2c3 a5a4 c3d4 a4a3 d4c3 a3a2 c3b2 a2a1q b2a1 h6g5 a1b2 h7h6 b2c3 h6h5 c3d4 h5h4 d4e3 h4h3 e3d2 h3h2 d2c1 h2h1q c1b2 g5g4 b2c3 h1a1 c3d2 a1b1 d2e3 b1a1 e3f2 a1b1 f2g2 b1a2 g2h1 g6b1");
    havingHelpMate.put("lichess_YGj1C2WB.pgn",
        "f3g3 d5c4 e2e1q c4b3 e1a1 b3c4 a1b1 c4d5 b1a1 d5e4 g3h4 e4f3 a1b1 f3g2 b1a2 g2h1 h4h3 h1g1 a2g2");
    havingHelpMate.put("lichess_YictG6WZ.pgn",
        "h5h4 g7h8 g5g6 h8g7 f5f1 g7h8 g6g7 h8g7 f1a1 g7f6 a1b1 f6e5 b1a1 e5d5 a1b1 d5c6 b1a1 c6b7 a1b1 b7a8 h4g3 a8a7 g3f2 a7a6 f2e2 a6a5 e2d3 a5a4 d3c4 a4a3 c4b5 a3a2 b5a6 a2a3 b1a1 a3b4 a6b7 b4c5 b7c8 c5b6 a1b1 b6a7 b1c1 a7a8 c1a1");
    havingHelpMate.put("lichess_yjfLthhQ.pgn",
        "e3f3 b8a7 d4d3 a7a6 d2d1q a6a5 d3d2 a5b6 d1a1 b6c7 d2d1q c7b6 f3g2 b6c7 g2h3 c7c6 d1b1 c6d5 b1c1 d5e4 a1a2 e4f3 a2g2");
    havingHelpMate.put("lichess_YRvWOIpy.pgn",
        "a5b6 h5g4 b6b7 g4f3 b7b8q f3e2 a7b7 e2d3 b7b1 d3c4 b1a1 c4d5 a1d1 d5c6 b8b6");
    havingHelpMate.put("lichess_z3xkCyBY.pgn", "c7h7");
    havingHelpMate.put("lichess_zmelXKvA.pgn",
        "h3h2 d2c1 g7g6 c1d1 g6g5 d1e1 g3g1 e1f2 g4g3 f2f3 g3g2 f3e2 h2h3 e2f2 h3h4 f2g1 h4h3 g1f2 g2g1b f2e1 g5g4 e1d1 g4g3 d1c1 g3g2 c1b1 g1f2 b1a1 g2g1q a1a2 h3g2 a2b3 g2f1 b3a2 f2e1 a2a1 f1e2 a1b1 e2d1 b1a1 d1c2 a1a2 g1a7");

  }

}
