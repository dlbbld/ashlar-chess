// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParser;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.PgnMove;
import io.github.dlbbld.ashlarchess.test.pgntest.constants.PgnTestConstants;

/**
 * Nested / consecutive recursive-annotation-variation (RAV) stress fixtures. ashlar does not model variations - the
 * lenient parser skips every balanced {@code (...)} group and recovers the mainline. These inputs push harder than the
 * inline cases: three or more consecutive side-lines at one node, depth-two nesting, a long trailing run of side-lines,
 * RAV skipping while the game starts from a custom FEN position (03), and - the important robustness case - side-lines
 * that themselves carry comments, NAGs, and suffix glyphs at every nesting level (04, 05). Every annotation inside a
 * variation must be consumed with it and never leak onto a mainline move, and parentheses inside a variation's comment
 * must not disturb the paren-depth balance. Each test asserts the exact recovered mainline.
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

  @SuppressWarnings("static-method")
  @Test
  void annotationsInsideNestedVariationsAreSkippedNotLeakedOntoTheMainline() {
    // The side-lines carry suffix glyphs (!?, ?!), comments, and NAGs ($1, $2, $4) at both nesting levels. None may
    // attach to a mainline move: the recovered mainline is annotation-free.
    final PgnGame game = parse("04_nested_rav_with_comments_nags_suffixes.pgn");
    assertEquals("e4 e5 Nf3 Nc6 Bb5 a6 Ba4", mainline(game));
    assertNoAnnotationsLeaked(game);
  }

  @SuppressWarnings("static-method")
  @Test
  void parenthesesInsideAVariationsCommentDoNotDisturbTheDepthBalance() {
    // The comments inside the (nested) side-lines contain "(main line)" and "(solid)". Because comment content is not
    // scanned for parens, the balance is unaffected and the mainline is recovered exactly; a $14 inside is dropped too.
    final PgnGame game = parse("05_variation_comment_with_parens.pgn");
    assertEquals("d4 d5 c4 e6 Nc3", mainline(game));
    assertNoAnnotationsLeaked(game);
  }

  private static PgnGame parse(String fixture) {
    return LenientPgnParser.parsePath(FOLDER, fixture);
  }

  private static void assertNoAnnotationsLeaked(PgnGame game) {
    for (final PgnMove move : game.moves()) {
      assertEquals(List.of(), move.nags(), "NAG leaked onto mainline move " + move.san());
      assertEquals("", move.commentary().value(), "comment leaked onto mainline move " + move.san());
    }
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
