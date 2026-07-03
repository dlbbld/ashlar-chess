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
 * The lenient parser against real, unmodified movetext from the three sources a copy-paste is most likely to come from:
 * lichess (symbolic glyphs, {@code [%eval]}/{@code [%clk]} command comments, RAV side-lines), chess.com (numeric
 * {@code $N} NAGs, RAV side-lines), and archival/annotated classics from game databases (long multi-line brace
 * comments, no-space move numbers like {@code 1.e4}). The lichess/chess.com games are the actual exports with only the
 * identifying tag values (player names, game URL/id) anonymised; the classics are public-domain historical games kept
 * verbatim. Each fixture must parse, recover the right mainline, carry the expected annotation data, and survive a
 * parse -> write -> parse round-trip with its moves, NAGs, and commentary intact.
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

  // -----------------------------------------------------------------------------------------------------------------
  // Annotated classics - long multi-line brace comments, no NAGs, no variations. Public-domain historical games.
  // -----------------------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void laskerCapablancaHasNoSpaceMoveNumbersAndMultiLineComments() {
    // This export writes move numbers glued to the SAN ("1.e4", "27.Qxd8+"); the lenient parser handles the missing
    // space. Capablanca's notes are long comments that wrap across several source lines.
    final PgnGame game = parse("classic_lasker_capablanca_1921.pgn");
    assertEquals(61, game.moves().size());
    assertEquals(ResultTagValue.DRAW, game.terminationMarker());
    assertEquals(0, totalNags(game));
    assertEquals(8, commentedMoveCount(game));
    // Move 1 (index 0) carries the leading "Notes by ..." comment.
    assertTrue(Nulls.getFirst(game.moves()).commentary().value().contains("Notes by J. R. Capablanca"));
  }

  @SuppressWarnings("static-method")
  @Test
  void zukertortSteinitzParsesFischersAnnotations() {
    final PgnGame game = parse("classic_zukertort_steinitz_1886.pgn");
    assertEquals(58, game.moves().size());
    assertEquals(ResultTagValue.BLACK_WON, game.terminationMarker());
    assertEquals(0, totalNags(game));
    assertEquals(8, commentedMoveCount(game));
  }

  @SuppressWarnings("static-method")
  @Test
  void nimzowitschHakanssonParsesCommentsContainingMoveLikeText() {
    // Nimzowitsch's notes quote long analysis lines ("12...Kb8 13 c3! dxc3 ...") inside the braces. That text is
    // comment content, not movetext - the brace makes it opaque, so it must not affect the parsed mainline.
    final PgnGame game = parse("classic_nimzowitsch_hakansson_1922.pgn");
    assertEquals(53, game.moves().size());
    assertEquals(ResultTagValue.WHITE_WON, game.terminationMarker());
    assertEquals(0, totalNags(game));
    assertEquals(9, commentedMoveCount(game));
  }

  @SuppressWarnings("static-method")
  @Test
  void everyRealGameSurvivesAParseWriteParseRoundTrip() {
    for (final String fixture : new String[] {"lichess_raw.pgn", "lichess_analysis.pgn", "chesscom_variations.pgn",
        "chesscom_review.pgn", "chesscom_variations_review.pgn", "classic_lasker_capablanca_1921.pgn",
        "classic_zukertort_steinitz_1886.pgn", "classic_nimzowitsch_hakansson_1922.pgn"}) {
      final PgnGame first = parse(fixture);
      final PgnGame reparsed = LenientPgnParser.parseText(PgnCreate.toPgnString(first));
      assertEquals(first.moves().size(), reparsed.moves().size(), fixture);
      for (int i = 0; i < first.moves().size(); i++) {
        final PgnMove a = Nulls.get(first.moves(), i);
        final PgnMove b = Nulls.get(reparsed.moves(), i);
        assertEquals(a.san(), b.san(), fixture + " san@" + i);
        assertEquals(a.nags(), b.nags(), fixture + " nags@" + i);
        assertEquals(a.commentary().value(), b.commentary().value(), fixture + " comment@" + i);
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

  private static int commentedMoveCount(PgnGame game) {
    int count = 0;
    for (final PgnMove move : game.moves()) {
      if (!move.commentary().value().isEmpty()) {
        count++;
      }
    }
    return count;
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
