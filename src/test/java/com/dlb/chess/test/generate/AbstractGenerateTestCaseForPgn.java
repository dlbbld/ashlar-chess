package com.dlb.chess.test.generate;

import java.nio.file.Path;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.report.Report;
import com.dlb.chess.report.Reporter;
import com.dlb.chess.test.model.PgnTestCase;

public abstract class AbstractGenerateTestCaseForPgn {

  static String generate(Path pgnFolderPath, String pgnName) throws Exception {

    final Report report = Reporter.calculateReport(pgnFolderPath, pgnName);

    final StringBuilder result = new StringBuilder();
    result.append("list.add(new ").append(PgnTestCase.class.getSimpleName()).append("(");

    // begin values
    result.append("\"");
    result.append(pgnName);
    result.append("\"");
    result.append(", ");

    final var fen = report.board().getFen();

    result.append("\"");
    result.append(fen);
    result.append("\"");
    // end values

    result.append("));");

    return Nulls.toString(result);
  }

}
