// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParser;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.PgnMove;
import io.github.dlbbld.ashlarchess.test.pgntest.constants.PgnTestConstants;

/**
 * Nested / consecutive recursive-annotation-variation (RAV) stress fixtures. ashlar does not model variations - the
 * lenient parser skips every balanced {@code (...)} group and recovers the mainline. These headerless, result-less
 * inputs push harder than the inline cases: three or more consecutive side-lines at one node, depth-two nesting, a long
 * trailing run of side-lines, and (03) RAV skipping while the game starts from a custom FEN position. Each asserts the
 * exact recovered mainline.
 */
class TestPgnVariationSkipFixtures {

  private static final Path FOLDER = Nulls.pathResolve(PgnTestConstants.LENIENT_PGN_PARSER_TEST_ROOT_FOLDER_PATH,
      "variation");

  @SuppressWarnings("static-method")
  @Test
  void threeConsecutiveSideLinesAtOneNodeAreAllSkipped() {
    // 3. Bc4 has three back-to-back side-lines, and 4... has a black-move side-line; all are skipped.
    final PgnGame game = parse("01_nested_rav.pgn");
    assertEquals("e4 e5 Nf3 Nc6 Bc4 Bc5 h3 h6 a3", mainline(game));
    assertNull(game.terminationMarker(), "no result marker in this bare-movetext fixture");
  }

  @SuppressWarnings("static-method")
  @Test
  void depthTwoNestingAndALongTrailingRunOfSideLinesAreSkipped() {
    // The 4... e5 line nests a further (5. f4) inside it (depth two), and move 9 is followed by five consecutive
    // side-lines (9. g4)(9. f4)(9. e4)(9. d4)(9. c4).
    final PgnGame game = parse("02_nested_rav.pgn");
    assertEquals("a3 a6 b3 b6 c3 c6 d3 d6 e3 e6 f3 f6 g3 g6 h3 h6 h4", mainline(game));
    assertNull(game.terminationMarker());
  }

  @SuppressWarnings("static-method")
  @Test
  void variationsAreSkippedWhileTheGameStartsFromACustomFen() {
    // The game begins from a From-Position FEN; nested and consecutive side-lines are skipped, and the mainline
    // replays from that custom start rather than the initial position.
    final PgnGame game = parse("03_nested_rav.pgn");
    assertEquals("rnb1kb1r/pb2pppp/5n2/q7/8/5N2/PP1BPPP1/RNBQKB1R w KQkq - 0 1", game.startFen().fen());
    assertEquals("Rh3 h6 Rh4 Rh7 Rh5 e6 Nd4 e5 Nc6 e4 Nxb8 Qxd2+ Qxd2", mainline(game));
    assertNull(game.terminationMarker());
  }

  private static PgnGame parse(String fixture) {
    return LenientPgnParser.parsePath(FOLDER, fixture);
  }

  private static String mainline(PgnGame game) {
    final StringBuilder builder = new StringBuilder();
    for (final PgnMove move : game.moves()) {
      if (builder.length() > 0) {
        builder.append(' ');
      }
      builder.append(move.san());
    }
    return Nulls.toString(builder);
  }
}
