// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParser;
import io.github.dlbbld.ashlarchess.pgn.PgnCreate;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.PgnMove;
import io.github.dlbbld.ashlarchess.pgn.ResultTagValue;
import io.github.dlbbld.ashlarchess.test.pgntest.constants.PgnTestConstants;

/**
 * The lenient parser against real, unmodified movetext from the two servers a copy-paste is most likely to come from:
 * lichess (symbolic glyphs, {@code [%eval]}/{@code [%clk]} command comments, RAV side-lines) and chess.com (numeric
 * {@code $N} NAGs, RAV side-lines). The games are the actual exports; only the identifying tag values (player names,
 * game URL/id) were anonymised. Each fixture must parse, recover the right mainline, carry the expected NAG data, and
 * survive a parse -> write -> parse round-trip with its moves and NAGs intact.
 */
class TestPgnRealWorldGames {

  private static final Path FOLDER = Nulls.pathResolve(PgnTestConstants.LENIENT_PGN_PARSER_TEST_ROOT_FOLDER_PATH,
      "realWorld");

  @SuppressWarnings("static-method")
  @Test
  void lichessRawGameParsesToItsMainline() {
    // Plain export: no annotations at all, just the played moves.
    final PgnGame game = parse("lichess_raw.pgn");
    assertEquals(134, game.moves().size());
    assertEquals(ResultTagValue.BLACK_WON, game.terminationMarker());
    assertEquals(0, totalNags(game));
  }

  @SuppressWarnings("static-method")
  @Test
  void lichessComputerAnalysisParsesGlyphsCommentsAndSkipsVariations() {
    // lichess bundles [%eval]/[%clk] comments, two opening comments on move 1, symbolic ?!/??/? glyphs, and a RAV
    // side-line after every analysed move. The mainline is the same 134 half-moves as the raw export; the glyphs
    // become NAGs (23 annotated moves in this game).
    final PgnGame game = parse("lichess_analysis.pgn");
    assertEquals(134, game.moves().size());
    assertEquals(ResultTagValue.BLACK_WON, game.terminationMarker());
    assertEquals(23, totalNags(game));
    assertTrue(hasCommentary(game), "eval/clk comments must be preserved as commentary");
  }

  @SuppressWarnings("static-method")
  @Test
  void chesscomVariationsOnlyParsesToItsMainline() {
    // Analysis-board export with RAV side-lines but no review, so no NAGs.
    final PgnGame game = parse("chesscom_variations.pgn");
    assertEquals(16, game.moves().size());
    assertEquals(ResultTagValue.ONGOING, game.terminationMarker());
    assertEquals(0, totalNags(game));
  }

  @SuppressWarnings("static-method")
  @Test
  void chesscomReviewParsesEveryNag() {
    // Game-review export: a $N NAG on most moves, no variations.
    final PgnGame game = parse("chesscom_review.pgn");
    assertEquals(17, game.moves().size());
    assertEquals(8, totalNags(game));
  }

  @SuppressWarnings("static-method")
  @Test
  void chesscomVariationsAndReviewParsesNagsAndSkipsVariations() {
    // The heaviest chess.com export: a NAG on most moves AND RAV side-lines. Mainline is 16 half-moves.
    final PgnGame game = parse("chesscom_variations_review.pgn");
    assertEquals(16, game.moves().size());
    assertEquals(12, totalNags(game));
  }

  @SuppressWarnings("static-method")
  @Test
  void everyRealGameSurvivesAParseWriteParseRoundTrip() {
    for (final String fixture : new String[] {"lichess_raw.pgn", "lichess_analysis.pgn", "chesscom_variations.pgn",
        "chesscom_review.pgn", "chesscom_variations_review.pgn"}) {
      final PgnGame first = parse(fixture);
      final PgnGame reparsed = LenientPgnParser.parseText(PgnCreate.toPgnString(first));
      assertEquals(first.moves().size(), reparsed.moves().size(), fixture);
      for (int i = 0; i < first.moves().size(); i++) {
        final PgnMove a = Nulls.get(first.moves(), i);
        final PgnMove b = Nulls.get(reparsed.moves(), i);
        assertEquals(a.san(), b.san(), fixture + " san@" + i);
        assertEquals(a.nags(), b.nags(), fixture + " nags@" + i);
      }
    }
  }

  private static PgnGame parse(String fixture) {
    return LenientPgnParser.parsePath(FOLDER, fixture);
  }

  private static int totalNags(PgnGame game) {
    int total = 0;
    for (final PgnMove move : game.moves()) {
      total += move.nags().size();
    }
    return total;
  }

  private static boolean hasCommentary(PgnGame game) {
    for (final PgnMove move : game.moves()) {
      if (!move.commentary().value().isEmpty()) {
        return true;
      }
    }
    return false;
  }
}
