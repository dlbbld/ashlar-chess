// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser.commentary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParser;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParserValidationException;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParserValidationProblem;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParser;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParserValidationException;
import io.github.dlbbld.ashlarchess.test.PgnTestHelper;

/**
 * Semicolon rest-of-line comments (PGN spec section 5). These are <em>import-format only</em> - brace comments are
 * valid in both import and export format, but {@code ;} comments are import-only. The split maps onto the parsers: the
 * lenient (import) parser accepts {@code ;} comments wherever a brace comment may appear; the strict (export) parser
 * rejects them. Content is preserved verbatim after the semicolon, including a leading space, consistent with the brace
 * commentary contract (no trim, no substitution).
 *
 * @see TestCommentaryLenient
 */
class TestLineCommentSemicolon {

  // -------------------------------------------------------------------------------------------------
  // Lenient accepts ; comments (verbatim content after the semicolon, up to end of line)
  // -------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void pregameLineComment() {
    final PgnGame file = LenientPgnParser.parseText(PgnTestHelper.header("*") + "; opening remark\n1. e4 e5 *\n\n");
    assertEquals(" opening remark", file.pregameCommentary().value());
    assertEquals(2, file.moves().size());
  }

  @SuppressWarnings("static-method")
  @Test
  void trailingLineCommentAfterWhiteMove() {
    final PgnGame file = LenientPgnParser.parseText(PgnTestHelper.header("*") + "1. e4 ; good opening\ne5 *\n\n");
    assertEquals(" good opening", Nulls.get(file.moves(), 0).commentary().value());
    assertEquals("e5", Nulls.get(file.moves(), 1).san());
  }

  @SuppressWarnings("static-method")
  @Test
  void lineCommentOnItsOwnLineAttachesToPrecedingMove() {
    final PgnGame file = LenientPgnParser
        .parseText(PgnTestHelper.header("*") + "1. e4 e5\n; mid-game note\n2. Nf3 Nc6 *\n\n");
    assertEquals(" mid-game note", Nulls.get(file.moves(), 1).commentary().value());
    assertEquals("Nf3", Nulls.get(file.moves(), 2).san());
  }

  @SuppressWarnings("static-method")
  @Test
  void lineCommentRunsOnlyToEndOfLine() {
    // The newline terminates the comment; the following token is ordinary movetext, not comment content.
    final PgnGame file = LenientPgnParser.parseText(PgnTestHelper.header("*") + "1. e4 ;c1\ne5 ;c2\n*\n\n");
    assertEquals("c1", Nulls.get(file.moves(), 0).commentary().value());
    assertEquals("c2", Nulls.get(file.moves(), 1).commentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void lineCommentWithForbiddenCharacterIsRejected() {
    // Unlike a brace comment, a ; comment can carry a closing brace; PgnCommentary forbids it, so the catch path in
    // consumeCommentaryOrThrow is genuinely reachable for LINE_COMMENT.
    expectLenientError(PgnTestHelper.header("*") + "1. e4 ; has a } brace\ne5 *\n\n",
        LenientPgnParserValidationProblem.MOVETEXT_COMMENTARY_CONTAINS_FORBIDDEN_CHARACTER);
  }

  @SuppressWarnings("static-method")
  @Test
  void lineCommentAtSanExpectedPositionIsRejected() {
    expectLenientError(PgnTestHelper.header("*") + "1. ; comment\ne4 e5 *\n\n",
        LenientPgnParserValidationProblem.MOVETEXT_COMMENTARY_NOT_ALLOWED_IN_SAN);
  }

  // -------------------------------------------------------------------------------------------------
  // Strict rejects ; comments (export format does not allow them)
  // -------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void strictRejectsTrailingLineComment() {
    assertThrows(StrictPgnParserValidationException.class,
        () -> StrictPgnParser.parseText(PgnTestHelper.header("*") + "1. e4 ; good opening\ne5 *\n\n"));
  }

  @SuppressWarnings("static-method")
  @Test
  void strictRejectsPregameLineComment() {
    assertThrows(StrictPgnParserValidationException.class,
        () -> StrictPgnParser.parseText(PgnTestHelper.header("*") + "; opening remark\n1. e4 e5 *\n\n"));
  }

  // -------------------------------------------------------------------------------------------------
  // Helpers
  // -------------------------------------------------------------------------------------------------

  private static void expectLenientError(String pgnText, LenientPgnParserValidationProblem expected) {
    @SuppressWarnings("null") final LenientPgnParserValidationException e = assertThrows(
        LenientPgnParserValidationException.class, () -> LenientPgnParser.parseText(pgnText));
    assertEquals(expected, e.getLenientPgnParserValidationProblem(),
        "Wrong problem category; message was: " + e.getMessage());
  }

}
