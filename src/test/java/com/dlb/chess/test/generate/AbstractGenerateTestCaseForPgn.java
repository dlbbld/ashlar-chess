package com.dlb.chess.test.generate;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.test.model.PgnFen;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;

public abstract class AbstractGenerateTestCaseForPgn {

  static String generate(String pgnName) throws Exception {

    final StringBuilder result = new StringBuilder();
    result.append("list.add(new ").append(PgnFen.class.getSimpleName()).append("(");

    // begin values
    result.append("\"");
    result.append(pgnName);
    result.append("\"");
    result.append(", ");

    final var fen = PgnTestCaseCatalog.findTestCase(pgnName).finalFen();

    result.append("\"");
    result.append(fen);
    result.append("\"");
    // end values

    result.append("));");

    return Nulls.toString(result);
  }

}
