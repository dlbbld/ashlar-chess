package com.dlb.chess.test.unwinnability.againstcha;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.test.ConfigurationTestConstants;
import com.dlb.chess.test.common.utility.FileUtility;
import com.dlb.chess.test.unwinnability.againstcha.model.AmbronaUnwinnabilityVerdicts;
import com.dlb.chess.unwinnability.UnwinnabilityFullVerdict;
import com.dlb.chess.unwinnability.UnwinnabilityQuickVerdict;

public final class AmbronaUnwinnabilityOracle {

  private static final Path ORACLE_PATH = Nulls.pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
      "src/test/resources/oracle/ambrona-unwinnability.tsv");
  private static final Map<String, AmbronaUnwinnabilityVerdicts> VERDICT_BY_FEN = readOracle();

  private AmbronaUnwinnabilityOracle() {
  }

  public static AmbronaUnwinnabilityVerdicts get(String fen) {
    if (!VERDICT_BY_FEN.containsKey(fen)) {
      throw new IllegalArgumentException("No Ambrona unwinnability oracle row for FEN: " + fen);
    }
    return Nulls.get(VERDICT_BY_FEN, fen);
  }

  private static Map<String, AmbronaUnwinnabilityVerdicts> readOracle() {
    final List<String> lineList = FileUtility.readFileLines(ORACLE_PATH);
    if (lineList.isEmpty()) {
      throw new ProgrammingMistakeException("The Ambrona unwinnability oracle file is empty");
    }
    final String expectedHeader = "fen\tfullWhite\tfullBlack\tquickWhite\tquickBlack";
    if (!expectedHeader.equals(Nulls.get(lineList, 0))) {
      throw new ProgrammingMistakeException("Unexpected Ambrona unwinnability oracle header");
    }

    final Map<String, AmbronaUnwinnabilityVerdicts> result = new HashMap<>();
    for (int i = 1; i < lineList.size(); i++) {
      final String line = Nulls.get(lineList, i);
      final String[] itemArray = Nulls.split(line, "\t");
      if (itemArray.length != 5) {
        throw new ProgrammingMistakeException("Invalid Ambrona unwinnability oracle row: " + line);
      }
      final String fen = Nulls.get(itemArray, 0);
      if (result.containsKey(fen)) {
        throw new ProgrammingMistakeException("Duplicate Ambrona unwinnability oracle row for FEN: " + fen);
      }
      final AmbronaUnwinnabilityVerdicts verdicts = new AmbronaUnwinnabilityVerdicts(UnwinnabilityFullVerdict.valueOf(Nulls.get(itemArray, 1)),
          UnwinnabilityFullVerdict.valueOf(Nulls.get(itemArray, 2)),
          UnwinnabilityQuickVerdict.valueOf(Nulls.get(itemArray, 3)),
          UnwinnabilityQuickVerdict.valueOf(Nulls.get(itemArray, 4)));
      result.put(fen, verdicts);
    }
    return result;
  }
}
