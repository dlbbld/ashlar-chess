// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.tools;

import java.nio.file.Path;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParser;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.PgnUtility;

/**
 * Scratch tool that prints the final FEN of a PGN file. Mechanical helper for fixture authoring: when a new test case
 * is added to {@code PgnTestCaseCatalog}, the {@code fen} field needs the position reached after the PGN's last move.
 * Computing it by hand is tedious; this tool replays the PGN and prints the result on stdout.
 *
 * <p>
 * Usage from Maven:
 *
 * <pre>{@code
 * mvn -q exec:java -Dexec.classpathScope=test \
 *     -Dexec.mainClass=io.github.dlbbld.ashlarchess.test.tools.FenFromPgn \
 *     -Dexec.args="src/test/resources/pgn/path/to/fixture.pgn"
 * }</pre>
 *
 * <p>
 * The argument may be an absolute path or a path relative to the working directory. The PGN is parsed with the lenient
 * parser to match the test-fixture workflow.
 */
public final class FenFromPgn {

  private FenFromPgn() {
  }

  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("Usage: FenFromPgn <path-to-pgn>");
      System.exit(2);
      return;
    }
    final Path pgnPath = Nulls.toAbsolutePath(Nulls.pathOf(Nulls.get(args, 0)));
    final Path folder = Nulls.getParent(pgnPath);
    final String fileName = Nulls.toString(Nulls.getFileName(pgnPath));

    final PgnGame pgnGame = LenientPgnParser.parse(folder, fileName);
    final Board board = PgnUtility.calculateBoard(pgnGame);
    System.out.println(board.getFen());
  }
}
