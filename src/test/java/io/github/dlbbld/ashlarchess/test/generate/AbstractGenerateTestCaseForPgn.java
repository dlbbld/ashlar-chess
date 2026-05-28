package io.github.dlbbld.ashlarchess.test.generate;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;

public abstract class AbstractGenerateTestCaseForPgn {

  static String generate(String pgnName) throws Exception {

    final StringBuilder result = new StringBuilder();
    result.append("list.add(new ").append(PgnFen.class.getSimpleName()).append("(");

    // begin values
    result.append("\"");
    result.append(pgnName);
    result.append("\"");
    result.append(", ");

    final String fen = PgnTestCaseCatalog.findTestCase(pgnName).finalFen();

    result.append("\"");
    result.append(fen);
    result.append("\"");
    // end values

    result.append("));");

    return Nulls.toString(result);
  }

}
