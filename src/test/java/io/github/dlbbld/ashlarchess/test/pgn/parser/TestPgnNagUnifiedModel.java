// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import io.github.dlbbld.ashlarchess.test.PgnTestHelper;

/**
 * The unified NAG model: annotations are {@link Nag}s in both parsers, a suffix glyph is the same thing as its NAG,
 * semantic export writes the first assessment as a glyph and the rest as {@code $N}, and archival export writes
 * canonical {@code $N} tokens.
 */
class TestPgnNagUnifiedModel {

  @SuppressWarnings("static-method")
  @Test
  void strictAcceptsBothGlyphAndNagFormAsTheSameNag() {
    final PgnGame game = StrictPgnParser.parseText(PgnTestHelper.header("*") + "1. e4? e5 $2 *\n\n");
    // e4? (glyph) and e5 $2 (NAG) both mean "mistake" -> both are Nag(2).
    assertEquals(List.of(new Nag(2)), Nulls.get(game.moves(), 0).nags());
    assertEquals(List.of(new Nag(2)), Nulls.get(game.moves(), 1).nags());
  }

  @SuppressWarnings("static-method")
  @Test
  void strictAcceptsMultipleNagsOnOneMove() {
    final PgnGame game = StrictPgnParser.parseText(PgnTestHelper.header("*") + "1. Nf3 $1 $14 d5 *\n\n");
    assertEquals(List.of(new Nag(1), new Nag(14)), Nulls.getFirst(game.moves()).nags());
  }

  @SuppressWarnings("static-method")
  @Test
  void strictRejectsNagWithoutDigits() {
    final StrictPgnParserValidationException e = assertThrows(StrictPgnParserValidationException.class,
        () -> StrictPgnParser.parseText(PgnTestHelper.header("*") + "1. e4 $ e5 *\n\n"));
    assertEquals(StrictPgnParserValidationProblem.MOVETEXT_NAG_INVALID, e.getStrictPgnParserValidationProblem());
  }

  @SuppressWarnings("static-method")
  @Test
  void strictRejectsNagOutOfRange() {
    final StrictPgnParserValidationException e = assertThrows(StrictPgnParserValidationException.class,
        () -> StrictPgnParser.parseText(PgnTestHelper.header("*") + "1. e4 $999 e5 *\n\n"));
    assertEquals(StrictPgnParserValidationProblem.MOVETEXT_NAG_INVALID, e.getStrictPgnParserValidationProblem());
  }

  @SuppressWarnings("static-method")
  @Test
  void exportRendersAssessmentAsGlyphAndTheRestAsDollarTokens() {
    final PgnGame game = LenientPgnParser
        .parseText(PgnTestHelper.header("*") + "1. e4 $2 e5 $9 2. Nf3 $1 $14 Nc6 *\n\n");
    final String pgn = PgnCreate.toPgnString(game);
    final String movetext = Nulls.substring(pgn, pgn.indexOf("1. ")).trim();
    // assessment codes 2 and 1 become the glyphs ? and !; 9 and 14 (no glyph) stay as $N.
    assertEquals("1. e4? e5 $9 2. Nf3! $14 Nc6 *", movetext);
  }

  @SuppressWarnings("static-method")
  @Test
  void roundTripPreservesEveryNag() {
    final String source = PgnTestHelper.header("*") + "1. e4? e5 $9 2. Nf3 $1 $14 Nc6 *\n\n";
    final PgnGame first = StrictPgnParser.parseText(source);
    final PgnGame reparsed = StrictPgnParser.parseText(PgnCreate.toPgnString(first));
    for (int i = 0; i < first.moves().size(); i++) {
      assertEquals(Nulls.get(first.moves(), i).nags(), Nulls.get(reparsed.moves(), i).nags());
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void nagCodeIsRangeChecked() {
    assertThrows(IllegalArgumentException.class, () -> new Nag(-1));
    assertThrows(IllegalArgumentException.class, () -> new Nag(256));
    assertEquals("$0", new Nag(0).toToken());
    assertEquals("$255", new Nag(255).toToken());
    assertTrue(new Nag(2).toToken().equals("$2"));
  }
}
