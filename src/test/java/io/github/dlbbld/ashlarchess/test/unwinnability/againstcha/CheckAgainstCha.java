// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability.againstcha;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.fen.StrictFenParser;
import io.github.dlbbld.ashlarchess.fen.StrictFenSemanticValidationException;
import io.github.dlbbld.ashlarchess.fen.model.Fen;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.test.common.utility.FileUtility;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;
import io.github.dlbbld.ashlarchess.test.unwinnability.againstcha.model.UnwinnabilityRawRead;
import io.github.dlbbld.ashlarchess.test.unwinnability.enums.UnwinnabilityMode;

public final class CheckAgainstCha {

  private CheckAgainstCha() {
  }

  public static List<UnwinnabilityRawRead> readChaRawResults(Path fenAnalysisFilePath) throws Exception {
    final List<UnwinnabilityRawRead> lines = new ArrayList<>();

    final List<String> fileLines = FileUtility.readFileLines(fenAnalysisFilePath);
    for (final String fileLine : fileLines) {
      final String[] fileLineItemArray = Nulls.split(fileLine, ";");

      final List<String> fileLineItems = Nulls.asList(fileLineItemArray);

      final String fenStrRaw = Nulls.get(fileLineItems, 0);

      final String fenStr = Nulls.trim(fenStrRaw);

      final Fen fen;
      try {
        fen = StrictFenParser.parse(fenStr);
      } catch (final StrictFenSemanticValidationException fve) {
        throw new IllegalArgumentException("Illegal FEN of \"" + fenStr + "\" for " + fve.getMessage() + " was found");
      }

      final String lichessGameId = Nulls.get(fileLineItems, 1);

      final String chaModeStr = Nulls.get(fileLineItems, 2);
      if (!UnwinnabilityMode.exists(chaModeStr)) {
        throw new IllegalArgumentException("Illegal identifier of \"" + chaModeStr + "\" was found");
      }
      final UnwinnabilityMode chaMode = UnwinnabilityMode.calculate(chaModeStr);

      final String winnerStr = Nulls.get(fileLineItems, 3);
      final Side winner = switch (winnerStr) {
        case "w" -> Side.WHITE;
        case "b" -> Side.BLACK;
        default -> throw new IllegalArgumentException("Illegal winning side of \"" + winnerStr + "\" was found");
      };

      final String result = Nulls.get(fileLineItems, 4);

      final String mateLine = Nulls.get(fileLineItems, 5);

      lines.add(new UnwinnabilityRawRead(fen, lichessGameId, chaMode, winner, result, mateLine));
    }

    return lines;
  }

  // list of the FEN of the past position for all PGN test case
  static void createFens() {

    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        System.out.println(testCase.finalFen() + ";noLichessGameId");
      }
    }
  }

  // The unwinnability comparison position subset. Shared by both the cha (C++) and chasolver (Rust) oracle
  // comparison harnesses so they cover exactly the same positions.
  public static boolean isUseTestForCha(PgnTest pgnTest) {
    switch (pgnTest) {
      case BASIC_FORCED:
      case CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR:
      case CHA_LICHESS_QUICK_DEPTH_THREE:
      case CHA_LICHESS_QUICK_DEPTH_FOUR:
      case CHA_CHASOLVER_EXCEPTIONS:
      case CHA_VARIOUS:
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
