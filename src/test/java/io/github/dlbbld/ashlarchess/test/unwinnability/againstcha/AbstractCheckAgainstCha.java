package io.github.dlbbld.ashlarchess.test.unwinnability.againstcha;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.exceptions.FenAdvancedValidationException;
import io.github.dlbbld.ashlarchess.fen.FenParserAdvanced;
import io.github.dlbbld.ashlarchess.fen.model.Fen;
import io.github.dlbbld.ashlarchess.test.common.utility.FileUtility;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;
import io.github.dlbbld.ashlarchess.test.unwinnability.againstcha.model.UnwinnabilityRawRead;
import io.github.dlbbld.ashlarchess.test.unwinnability.enums.UnwinnabilityMode;

public abstract class AbstractCheckAgainstCha {

  public static List<UnwinnabilityRawRead> readChaRawResultList(Path fenAnalysisFilePath) throws Exception {
    final List<UnwinnabilityRawRead> resultList = new ArrayList<>();

    final List<String> fileLineList = FileUtility.readFileLines(fenAnalysisFilePath);
    for (final String fileLine : fileLineList) {
      final String[] fileLineItemArray = Nulls.split(fileLine, ";");

      final List<String> fileLineItemList = Nulls.asList(fileLineItemArray);

      final String fenStrRaw = Nulls.get(fileLineItemList, 0);

      final String fenStr = Nulls.trim(fenStrRaw);

      final Fen fen;
      try {
        fen = FenParserAdvanced.parseFenAdvanced(fenStr);
      } catch (final FenAdvancedValidationException fve) {
        throw new IllegalArgumentException("Illegal FEN of \"" + fenStr + "\" for " + fve.getMessage() + " was found");
      }

      final String lichessGameId = Nulls.get(fileLineItemList, 1);

      final String chaModeStr = Nulls.get(fileLineItemList, 2);
      if (!UnwinnabilityMode.exists(chaModeStr)) {
        throw new IllegalArgumentException("Illegal identifier of \"" + chaModeStr + "\" was found");
      }
      final UnwinnabilityMode chaMode = UnwinnabilityMode.calculate(chaModeStr);

      final String winnerStr = Nulls.get(fileLineItemList, 3);
      final Side winner = switch (winnerStr) {
        case "w" -> Side.WHITE;
        case "b" -> Side.BLACK;
        default -> throw new IllegalArgumentException("Illegal winning side of \"" + winnerStr + "\" was found");
      };

      final String result = Nulls.get(fileLineItemList, 4);

      final String mateLine = Nulls.get(fileLineItemList, 5);

      resultList.add(new UnwinnabilityRawRead(fen, lichessGameId, chaMode, winner, result, mateLine));
    }

    return resultList;
  }

  // list of the FEN of the past position for all PGN test case
  static void createFenList() throws Exception {

    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        System.out.println(testCase.finalFen() + ";noLichessGameId");
      }
    }
  }

  static boolean isUseTestForCha(PgnTest pgnTest) {
    switch (pgnTest) {
      case BASIC_FORCED:
      case CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR:
      case CHA_LICHESS_QUICK_DEPTH_THREE:
      case CHA_LICHESS_QUICK_DEPTH_FOUR:
      case CHA_AMBRONA:
      case CHA_PAWN_WALL_YES:
      case CHA_PAWN_WALL_NO:
      case CHA_SHALLOW_TERMINATION:
      case CHA_HELPMATE_BEYOND_FIVEFOLD:
      case CHA_HELPMATE_BEYOND_SEVENTY_FIVE:
      case CHA_BASIC_MATE_DRAW:
      case CHA_BASIC_MATE_HELPMATE_04:
      case CHA_BASIC_MATE_HELPMATE_10:
      case CHA_BASIC_MATE_HELPMATE_AROUND_MAX:
        return true;
      default:
        return false;
    }
  }
}
