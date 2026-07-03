// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParser;
import io.github.dlbbld.ashlarchess.pgn.Nag;
import io.github.dlbbld.ashlarchess.pgn.PgnCreate;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParser;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParserValidationException;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParserValidationProblem;
import io.github.dlbbld.ashlarchess.pgn.WriteMode;
import io.github.dlbbld.ashlarchess.test.PgnTestHelper;

/**
 * The full right/wrong matrix for move annotations: NAG value boundaries, malformed and out-of-range codes, annotation
 * position (before a move, after a variation, inside a variation), glyph handling, strict spacing and rejection, and
 * round-trip rendering. Complements the real-game fixtures in {@link TestPgnRealWorldGames} with the constructed cases
 * a fuzzer would reach.
 */
class TestPgnAnnotationEdgeCases {

  // -----------------------------------------------------------------------------------------------------------------
  // NAG value boundaries and normalisation (lenient)
  // -----------------------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void nagBoundaryValuesAreAccepted() {
    final PgnGame game = lenient("1. e4 $0 e5 $255 *");
    assertEquals(List.of(new Nag(0)), Nulls.get(game.moves(), 0).nags());
    assertEquals(List.of(new Nag(255)), Nulls.get(game.moves(), 1).nags());
  }

  @SuppressWarnings("static-method")
  @Test
  void leadingZerosAreNormalised() {
    final PgnGame game = lenient("1. e4 $01 e5 $007 *");
    assertEquals(List.of(new Nag(1)), Nulls.get(game.moves(), 0).nags());
    assertEquals(List.of(new Nag(7)), Nulls.get(game.moves(), 1).nags());
  }

  @SuppressWarnings("static-method")
  @Test
  void outOfRangeBareAndOverflowNagsAreDropped() {
    // $256 (just over), a bare $, and a value that overflows int all parse leniently with no annotation recorded.
    final PgnGame game = lenient("1. e4 $256 e5 $ 2. Nf3 $99999999999 Nc6 *");
    assertEquals(4, game.moves().size());
    for (int i = 0; i < 4; i++) {
      assertEquals(List.of(), Nulls.get(game.moves(), i).nags(), "move " + i);
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void overlongGlyphRunIsDropped() {
    // "?!?" is not one of the six glyphs; lenient drops the unrecognised run rather than failing.
    final PgnGame game = lenient("1. e4?!? e5 *");
    assertEquals(List.of(), Nulls.getFirst(game.moves()).nags());
  }

  @SuppressWarnings("static-method")
  @Test
  void nonNumericNagIsDroppedLenientlyAsOneToken() {
    // `$abc` and `$1x` are captured whole as malformed NAG tokens (the trailing letters do NOT spill out as a bad
    // SAN); lenient drops them and the game still parses.
    final PgnGame game = lenient("1. e4 $abc e5 $1x 2. Nf3 Nc6 *");
    assertEquals(4, game.moves().size());
    for (int i = 0; i < 4; i++) {
      assertEquals(List.of(), Nulls.get(game.moves(), i).nags(), "move " + i);
    }
  }

  // -----------------------------------------------------------------------------------------------------------------
  // NAG position (lenient)
  // -----------------------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void nagBeforeAnyMoveIsDropped() {
    final PgnGame game = lenient("$1 1. e4 e5 *");
    assertEquals(2, game.moves().size());
    assertEquals(List.of(), Nulls.getFirst(game.moves()).nags());
  }

  @SuppressWarnings("static-method")
  @Test
  void nagAfterAVariationAttachesToThePrecedingMainlineMove() {
    // The variation belongs to e4; a NAG right after the closing ) still annotates e4.
    final PgnGame game = lenient("1. e4 (1. d4 d5) $1 e5 *");
    assertEquals(2, game.moves().size());
    assertEquals(List.of(new Nag(1)), Nulls.getFirst(game.moves()).nags());
  }

  @SuppressWarnings("static-method")
  @Test
  void nagInsideAVariationIsSkippedWithIt() {
    // The $2 lives inside the skipped side-line; no mainline move picks it up.
    final PgnGame game = lenient("1. e4 e5 (1... c5 $2 2. Nf3) 2. Nf3 *");
    assertEquals(3, game.moves().size());
    for (int i = 0; i < 3; i++) {
      assertEquals(List.of(), Nulls.get(game.moves(), i).nags(), "move " + i);
    }
  }

  // -----------------------------------------------------------------------------------------------------------------
  // Variations (lenient)
  // -----------------------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void consecutiveVariationsAreBothSkipped() {
    final PgnGame game = lenient("1. e4 (1. d4) (1. c4) e5 *");
    assertEquals(2, game.moves().size());
    assertEquals("e4", Nulls.get(game.moves(), 0).san());
    assertEquals("e5", Nulls.get(game.moves(), 1).san());
  }

  @SuppressWarnings("static-method")
  @Test
  void unbalancedVariationAtEndOfInputIsToleratedAndMainlineKept() {
    final PgnGame game = lenient("1. e4 e5 (2. d4 exd4 *");
    assertEquals(2, game.moves().size());
    assertEquals("e5", Nulls.get(game.moves(), 1).san());
  }

  // -----------------------------------------------------------------------------------------------------------------
  // Glyphs (lenient)
  // -----------------------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void aGlyphSeparatedFromItsMoveByASpaceStillAttaches() {
    final PgnGame game = lenient("1. e4 !! e5 *");
    assertEquals(List.of(new Nag(3)), Nulls.getFirst(game.moves()).nags());
  }

  // -----------------------------------------------------------------------------------------------------------------
  // Strict acceptance and rejection
  // -----------------------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void strictAcceptsNagBoundaryValues() {
    final PgnGame game = StrictPgnParser.parseText(PgnTestHelper.header("*") + "1. e4 $0 e5 $255 *\n\n");
    assertEquals(List.of(new Nag(0)), Nulls.get(game.moves(), 0).nags());
    assertEquals(List.of(new Nag(255)), Nulls.get(game.moves(), 1).nags());
  }

  @SuppressWarnings("static-method")
  @Test
  void strictRejectsNagGluedToTheSanWithoutASpace() {
    assertStrictProblem("1. e4$1 e5 *", StrictPgnParserValidationProblem.MOVETEXT_UNEXPECTED_FORMAT);
  }

  @SuppressWarnings("static-method")
  @Test
  void strictRejectsTwoNagsWithoutASeparatingSpace() {
    assertStrictProblem("1. e4 $1$2 e5 *", StrictPgnParserValidationProblem.MOVETEXT_UNEXPECTED_FORMAT);
  }

  @SuppressWarnings("static-method")
  @Test
  void strictRejectsAVariation() {
    // The "(" makes the token an invalid SAN - strict does not tolerate RAV at all.
    assertStrictProblem("1. e4 (1. d4 d5) e5 *", StrictPgnParserValidationProblem.MOVETEXT_SAN_CHARACTER_INVALID);
  }

  @SuppressWarnings("static-method")
  @Test
  void strictRejectsANonNumericNag() {
    assertStrictProblem("1. e4 $abc e5 *", StrictPgnParserValidationProblem.MOVETEXT_NAG_INVALID);
  }

  // -----------------------------------------------------------------------------------------------------------------
  // Round-trip rendering
  // -----------------------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void boundaryAndNonGlyphNagsRoundTripAsDollarTokens() {
    final PgnGame game = lenient("1. e4 $0 e5 $255 2. Nf3 $9 Nc6 *");
    final String pgn = PgnCreate.toPgnString(game);
    final String movetext = Nulls.substring(pgn, pgn.indexOf("1. ")).trim();
    assertEquals("1. e4 $0 e5 $255 2. Nf3 $9 Nc6 *", movetext);
  }

  @SuppressWarnings("static-method")
  @Test
  void onlyTheFirstAssessmentNagBecomesAGlyphOnSemanticExport() {
    // With two assessment codes plus a positional one, only the first assessment ($1) is rendered as a glyph; the
    // second assessment ($2) and the positional ($14) stay as $N. Two glyphs would fuse ambiguously.
    final PgnGame game = lenient("1. Nf3 $1 $2 $14 d5 *");
    final String pgn = PgnCreate.toPgnString(game);
    assertEquals("1. Nf3! $2 $14 d5 *", Nulls.substring(pgn, pgn.indexOf("1. ")).trim());
  }

  @SuppressWarnings("static-method")
  @Test
  void archivalExportWritesEveryNagAsADollarTokenIncludingAssessments() {
    // Archival is PGN export-format conformant (spec 8.2.3.8): assessment codes 1..6 are written as $N, not glyphs.
    final PgnGame game = lenient("1. e4? e5 $9 2. Nf3 $1 $2 $14 Nc6 *");
    final String pgn = PgnCreate.toPgnString(game, WriteMode.ARCHIVAL);
    assertEquals("1. e4 $2 e5 $9 2. Nf3 $1 $2 $14 Nc6 *", Nulls.substring(pgn, pgn.indexOf("1. ")).trim());
  }

  @SuppressWarnings("static-method")
  @Test
  void aGlyphAndAPositionalNagOnOneMoveRoundTrip() {
    // code 1 renders as the glyph "!", code 14 (no glyph) as "$14"; reparsing recovers both.
    final PgnGame game = lenient("1. Nf3 $1 $14 d5 *");
    final String pgn = PgnCreate.toPgnString(game);
    final String movetext = Nulls.substring(pgn, pgn.indexOf("1. ")).trim();
    assertEquals("1. Nf3! $14 d5 *", movetext);
    final PgnGame reparsed = LenientPgnParser.parseText(pgn);
    assertEquals(List.of(new Nag(1), new Nag(14)), Nulls.getFirst(reparsed.moves()).nags());
  }

  private static PgnGame lenient(String movetext) {
    return LenientPgnParser.parseText(PgnTestHelper.header("*") + movetext + "\n\n");
  }

  private static void assertStrictProblem(String movetext, StrictPgnParserValidationProblem expected) {
    final StrictPgnParserValidationException e = assertThrows(StrictPgnParserValidationException.class,
        () -> StrictPgnParser.parseText(PgnTestHelper.header("*") + movetext + "\n\n"));
    assertEquals(expected, e.getStrictPgnParserValidationProblem());
  }
}
