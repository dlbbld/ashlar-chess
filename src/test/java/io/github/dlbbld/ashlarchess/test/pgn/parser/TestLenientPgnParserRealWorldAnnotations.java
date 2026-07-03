// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParser;
import io.github.dlbbld.ashlarchess.pgn.MoveSuffixAnnotation;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.test.PgnTestHelper;

/**
 * Real-world annotated-PGN tolerance for {@link LenientPgnParser}: the three constructs a lichess computer-analysis
 * export (and analysis-board exports in general) carry that the strict import format does not - {@code [%eval]} /
 * {@code [%clk]} command comments, consecutive comments on one move, and recursive annotation variations (RAV). The
 * lenient parser tolerates all three and recovers the mainline; ashlar does not model variations (a rules library
 * reads the game that was played, not the engine's side-lines), so RAV groups are skipped, not parsed into a tree.
 */
class TestLenientPgnParserRealWorldAnnotations {

  @SuppressWarnings("static-method")
  @Test
  void evalAndClockCommandCommentsAreCommentText() {
    // The `[%eval ...]` / `[%clk ...]` command syntax lives inside a {...} comment; the square brackets must be read
    // as comment content, not mistaken for a tag line.
    final PgnGame game = LenientPgnParser
        .parseText(PgnTestHelper.header("*") + "1. d4 { [%eval 0.15] [%clk 0:05:00] } d5 *\n\n");
    assertEquals(2, game.moves().size());
    // Comment content is preserved verbatim (the surrounding spaces inside `{ ... }` are part of the content).
    assertEquals("[%eval 0.15] [%clk 0:05:00]", Nulls.getFirst(game.moves()).commentary().value().trim());
  }

  @SuppressWarnings("static-method")
  @Test
  void consecutiveCommentsOnOneMoveAreMerged() {
    // lichess opens every analyzed game with two back-to-back comments on move 1.
    final PgnGame game = LenientPgnParser.parseText(
        PgnTestHelper.header("*") + "1. d4 { [%eval 0.15] [%clk 0:05:00] } { A40 Queen's Pawn Game } d5 *\n\n");
    assertEquals(2, game.moves().size());
    // Both comments survive the merge (verbatim content, joined) - no annotation is silently dropped.
    final String merged = Nulls.getFirst(game.moves()).commentary().value();
    assertTrue(merged.contains("[%eval 0.15] [%clk 0:05:00]"), merged);
    assertTrue(merged.contains("A40 Queen's Pawn Game"), merged);
  }

  @SuppressWarnings("static-method")
  @Test
  void variationsAreSkippedAndTheMainlineKept() {
    // Two variations, the second interrupting the mainline so a black-move-number indicator ("2...") reappears.
    final PgnGame game = LenientPgnParser.parseText(PgnTestHelper.header("*")
        + "1. f4 e5 2. e4 (2. d4 exd4 3. Qxd4 Bc5) 2... h6 3. h4 Rh7 4. Rh3 (4. a3 a6 5. Nc3) *\n\n");
    assertEquals(7, game.moves().size());
    assertEquals("f4", Nulls.get(game.moves(), 0).san());
    assertEquals("Rh3", Nulls.get(game.moves(), 6).san());
  }

  @SuppressWarnings("static-method")
  @Test
  void nestedVariationsAreSkipped() {
    final PgnGame game = LenientPgnParser
        .parseText(PgnTestHelper.header("*") + "1. e4 e5 (1... c5 2. Nf3 (2. Nc3 Nc6) d6) 2. Nf3 *\n\n");
    assertEquals(3, game.moves().size());
  }

  @SuppressWarnings("static-method")
  @Test
  void parenthesesInsideACommentDoNotAffectVariationBalance() {
    // lichess annotation comments carry unbalanced-looking parentheses, e.g. "(0.32 -> 1.41)". Inside a {...} comment
    // they are text, not a variation, and must not confuse the RAV skip.
    final PgnGame game = LenientPgnParser
        .parseText(PgnTestHelper.header("*") + "1. e4 { (0.32 -> 1.41) Inaccuracy. } (1. d4 d5) e5 *\n\n");
    assertEquals(2, game.moves().size());
  }

  @SuppressWarnings("static-method")
  @Test
  void moveAssessmentNagsBecomeTheEquivalentSymbolicAnnotation() {
    // The six move-assessment NAGs ($1..$6) are the numeric encoding of ashlar's six symbolic move annotations;
    // chess.com's game-review export emits these instead of the glyphs. Each is mapped to the matching enum value.
    final PgnGame game = LenientPgnParser.parseText(PgnTestHelper.header("*")
        + "1. e4 $1 e5 $2 2. Nf3 $3 Nc6 $4 3. Bb5 $5 a6 $6 *\n\n");
    assertEquals(6, game.moves().size());
    assertEquals(MoveSuffixAnnotation.GOOD_MOVE, Nulls.get(game.moves(), 0).moveSuffixAnnotation());
    assertEquals(MoveSuffixAnnotation.MISTAKE, Nulls.get(game.moves(), 1).moveSuffixAnnotation());
    assertEquals(MoveSuffixAnnotation.BRILLIANT_MOVE, Nulls.get(game.moves(), 2).moveSuffixAnnotation());
    assertEquals(MoveSuffixAnnotation.BLUNDER, Nulls.get(game.moves(), 3).moveSuffixAnnotation());
    assertEquals(MoveSuffixAnnotation.INTERESTING_MOVE, Nulls.get(game.moves(), 4).moveSuffixAnnotation());
    assertEquals(MoveSuffixAnnotation.DUBIOUS_MOVE, Nulls.get(game.moves(), 5).moveSuffixAnnotation());
  }

  @SuppressWarnings("static-method")
  @Test
  void nonAssessmentNagsAreToleratedWithoutAnnotation() {
    // Codes outside $1..$6 (positional, time, or chess.com's own $9 sprinkled across nearly every move) have no
    // symbolic equivalent; the parser consumes them and leaves the move unannotated rather than choking.
    final PgnGame game = LenientPgnParser
        .parseText(PgnTestHelper.header("*") + "1. e4 $9 e5 $10 2. Nf3 $7 Nc6 *\n\n");
    assertEquals(4, game.moves().size());
    assertEquals(MoveSuffixAnnotation.NONE, Nulls.get(game.moves(), 0).moveSuffixAnnotation());
    assertEquals(MoveSuffixAnnotation.NONE, Nulls.get(game.moves(), 1).moveSuffixAnnotation());
    assertEquals(MoveSuffixAnnotation.NONE, Nulls.get(game.moves(), 2).moveSuffixAnnotation());
  }

  @SuppressWarnings("static-method")
  @Test
  void nagsAndVariationsCombineTheWayChessComExportsThem() {
    // chess.com's "with variations and review" export carries both on the same movetext: a NAG on nearly every move
    // and RAV side-lines for the analyzed ones. The mainline is e4 e5 g3 f6 Qh5+ g6 = 6 half-moves.
    final String movetext = "1. e4 e5 2. g3 $2 f6 $9 3. Qh5+ $9 g6 $1 (3... Ke7 4. Qf7+ Kd6) 4. Qe2 *";
    final PgnGame game = LenientPgnParser.parseText(PgnTestHelper.header("*") + movetext + "\n\n");
    assertEquals(7, game.moves().size());
    assertEquals("g3", Nulls.get(game.moves(), 2).san());
    assertEquals(MoveSuffixAnnotation.MISTAKE, Nulls.get(game.moves(), 2).moveSuffixAnnotation());
    assertEquals("g6", Nulls.get(game.moves(), 5).san());
    assertEquals(MoveSuffixAnnotation.GOOD_MOVE, Nulls.get(game.moves(), 5).moveSuffixAnnotation());
    assertEquals("Qe2", Nulls.get(game.moves(), 6).san());
  }

  @SuppressWarnings("static-method")
  @Test
  void representativeLichessAnalysisSliceParsesToItsMainline() {
    // A compact slice with every lichess construct at once: two opening comments, symbolic annotations, eval/clk
    // command comments, and RAV lines for the flagged moves. The mainline is d4 a5 e4 a4 a3 d6 c4 g6 = 8 half-moves.
    final String movetext = "1. d4 { [%eval 0.15] [%clk 0:05:00] } { A40 Queen's Pawn Game } 1... a5 "
        + "{ [%eval 0.67] [%clk 0:05:00] } 2. e4 { [%eval 0.69] } 2... a4 { [%eval 1.12] } 3. a3 { [%eval 0.97] } "
        + "3... d6?! { (0.97 -> 1.16) Inaccuracy. e5 was best. } { [%eval 1.16] } (3... e5 4. dxe5 dxe5 5. Qxd8+) "
        + "4. c4 { [%eval 0.64] } 4... g6 { [%eval 0.92] } 0-1";
    final PgnGame game = LenientPgnParser.parseText(PgnTestHelper.header("0-1") + movetext + "\n\n");
    assertEquals(8, game.moves().size());
    assertEquals("d4", Nulls.get(game.moves(), 0).san());
    assertEquals("g6", Nulls.get(game.moves(), 7).san());
  }
}
