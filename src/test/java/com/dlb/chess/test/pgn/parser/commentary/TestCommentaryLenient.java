package com.dlb.chess.test.pgn.parser.commentary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.pgn.LenientPgnParser;
import com.dlb.chess.pgn.LenientPgnParserValidationException;
import com.dlb.chess.pgn.LenientPgnParserValidationProblem;
import com.dlb.chess.pgn.PgnCreate;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.test.PgnTestHelper;

/**
 * Commentary brace validation for {@link LenientPgnParser}. Lenient is strict-equivalent on the brace rules (R1/R3/R4);
 * continuing past malformed commentary would produce unreliable downstream results.
 *
 * @see TestCommentaryStrict
 */
class TestCommentaryLenient {

  // -------------------------------------------------------------------------------------------------
  // Valid cases - structural acceptance and commentary-text assertions
  // -------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void v01_pregameCommentaryOnly() {
    final PgnGame file = LenientPgnParser.parseText(PgnTestHelper.header("*") + "{opening remark} 1. e4 e5 *\n\n");
    assertEquals("opening remark", file.pregameCommentary().value());
    assertEquals(2, file.halfMoveList().size());
  }

  @SuppressWarnings("static-method")
  @Test
  void v01_pregameCommentaryLongNoLinebreaks() {
    final String pregameCommentary = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
    assertTrue(pregameCommentary.length() > PgnCreate.MAX_LINE_LENGTH);
    final PgnGame file = LenientPgnParser
        .parseText(PgnTestHelper.header("*") + "{" + pregameCommentary + "} 1. e4 e5 *\n\n");
    assertEquals(pregameCommentary, file.pregameCommentary().value());
    assertEquals(2, file.halfMoveList().size());
  }

  /**
   * Per the commentary contract, lenient preserves source bytes verbatim - the embedded newlines from the Java
   * text-block stay in the model exactly as written. No trim, no substitution.
   */
  @SuppressWarnings("static-method")
  @Test
  void v01_pregameCommentaryLongWithLinebreaks() {
    final String pregameCommentary = """
        Lorem ipsum dolor sit amet,
        consectetur adipiscing elit,
        sed do eiusmod
        tempor incididunt ut labore et dolore magna aliqua.
        Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
        Duis aute irure dolor in reprehenderit in
        voluptate velit esse cillum dolore eu fugiat nulla pariatur.
        Excepteur sint occaecat cupidatat non proident,
        sunt in culpa qui officia deserunt mollit anim id est laborum.
            """;
    assertTrue(pregameCommentary.length() > PgnCreate.MAX_LINE_LENGTH);
    final PgnGame file = LenientPgnParser
        .parseText(PgnTestHelper.header("*") + "{" + pregameCommentary + "} 1. e4 e5 *\n\n");
    assertEquals(pregameCommentary, file.pregameCommentary().value());
    assertEquals(2, file.halfMoveList().size());
  }

  @SuppressWarnings("static-method")
  @Test
  void v01_pregameCommentaryOnlySpaces() {
    final String pregameCommentary = "   ";
    final PgnGame file = LenientPgnParser
        .parseText(PgnTestHelper.header("*") + "{" + pregameCommentary + "} 1. e4 e5 *\n\n");
    assertEquals(pregameCommentary, file.pregameCommentary().value());
    assertEquals(2, file.halfMoveList().size());
  }

  @SuppressWarnings("static-method")
  @Test
  void v02_trailingCommentaryAfterWhiteMove() {
    final PgnGame file = LenientPgnParser.parseText(PgnTestHelper.header("*") + "1. e4 {good opening} e5 *\n\n");
    assertEquals("good opening", Nulls.get(file.halfMoveList(), 0).commentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void v03_trailingCommentaryAfterBlackMove() {
    final PgnGame file = LenientPgnParser
        .parseText(PgnTestHelper.header("*") + "1. e4 e5 {symmetric} 2. Nf3 Nc6 *\n\n");
    assertEquals("symmetric", Nulls.get(file.halfMoveList(), 1).commentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void v04_commentaryAfterEveryHalfMove() {
    final PgnGame file = LenientPgnParser
        .parseText(PgnTestHelper.header("*") + "1. e4 {a} e5 {b} 2. Nf3 {c} Nc6 {d} *\n\n");
    assertEquals("a", Nulls.get(file.halfMoveList(), 0).commentary().value());
    assertEquals("b", Nulls.get(file.halfMoveList(), 1).commentary().value());
    assertEquals("c", Nulls.get(file.halfMoveList(), 2).commentary().value());
    assertEquals("d", Nulls.get(file.halfMoveList(), 3).commentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void v05_leadingAndTrailingCommentary() {
    final PgnGame file = LenientPgnParser
        .parseText(PgnTestHelper.header("*") + "{intro} 1. e4 {after-1-white} e5 *\n\n");
    assertEquals("intro", file.pregameCommentary().value());
    assertEquals("after-1-white", Nulls.get(file.halfMoveList(), 0).commentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void v06_emptyCommentary() {
    final PgnGame file = LenientPgnParser.parseText(PgnTestHelper.header("*") + "1. e4 {} e5 *\n\n");
    assertEquals("", Nulls.get(file.halfMoveList(), 0).commentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void v07_commentaryWithPunctuationButNoBraces() {
    final PgnGame file = LenientPgnParser
        .parseText(PgnTestHelper.header("*") + "1. e4 {special chars !? + # - / .} e5 *\n\n");
    assertEquals("special chars !? + # - / .", Nulls.get(file.halfMoveList(), 0).commentary().value());
  }

  /**
   * Per the commentary contract, the PGN spec allows newlines inside {@code {...}} commentary. Lenient (like strict)
   * preserves source bytes verbatim. The previous lenient-side substitution of newline-to-space has been removed - tabs
   * and line breaks are valid commentary content per spec, not whitespace to be normalised.
   */
  @SuppressWarnings("static-method")
  @Test
  void v08_multilineCommentaryPreservedVerbatim() {
    final PgnGame file = LenientPgnParser.parseText(PgnTestHelper.header("*") + "1. e4 {line one\nline two} e5 *\n\n");
    assertEquals("line one\nline two", Nulls.get(file.halfMoveList(), 0).commentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void v09_commentaryAfterSuffixAnnotation() {
    final PgnGame file = LenientPgnParser.parseText(PgnTestHelper.header("*") + "1. e4!? {spicy} e5 *\n\n");
    assertEquals("spicy", Nulls.get(file.halfMoveList(), 0).commentary().value());
    assertEquals(com.dlb.chess.enums.MoveSuffixAnnotation.INTERESTING_MOVE,
        Nulls.get(file.halfMoveList(), 0).moveSuffixAnnotation());
  }

  @SuppressWarnings("static-method")
  @Test
  void v10_zeroMoveGameWithPreGameCommentaryOnly() {
    final PgnGame file = LenientPgnParser.parseText(PgnTestHelper.header("*") + "{no moves played} *\n\n");
    assertEquals("no moves played", file.pregameCommentary().value());
    assertEquals(0, file.halfMoveList().size());
  }

  // -------------------------------------------------------------------------------------------------
  // R1 - unclosed commentary
  // -------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void r1_unclosedAtEof() {
    expectError(PgnTestHelper.header("*") + "1. e4 {unclosed\n\n",
        LenientPgnParserValidationProblem.MOVETEXT_COMMENTARY_START_BRACE_NOT_FOLLOWED_BY_END_BRACE);
  }

  @SuppressWarnings("static-method")
  @Test
  void r1_unclosedPreGameCommentary() {
    expectError(PgnTestHelper.header("*") + "{unclosed leading 1. e4 e5 *\n\n",
        LenientPgnParserValidationProblem.MOVETEXT_COMMENTARY_START_BRACE_NOT_FOLLOWED_BY_END_BRACE);
  }

  // -------------------------------------------------------------------------------------------------
  // T-003 - inner `{` is content (PGN spec section 8.2.5), same as strict.
  // -------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void t003_openBraceInPreGameCommentaryIsContent() {
    final PgnGame file = LenientPgnParser.parseText(PgnTestHelper.header("*") + "{outer {inner} 1. e4 e5 *\n\n");
    assertEquals("outer {inner", file.pregameCommentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void t003_openBraceInTrailingCommentaryIsContent() {
    final PgnGame file = LenientPgnParser.parseText(PgnTestHelper.header("*") + "1. e4 {outer {inner} e5 *\n\n");
    assertEquals("outer {inner", Nulls.get(file.halfMoveList(), 0).commentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void t003_openBraceImmediatelyAtStartOfContent() {
    final PgnGame file = LenientPgnParser.parseText(PgnTestHelper.header("*") + "1. e4 {{nested right away} e5 *\n\n");
    assertEquals("{nested right away", Nulls.get(file.halfMoveList(), 0).commentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void t003_strayCloseAfterClosedCommentaryWithInnerOpenBraceIsR3() {
    expectError(PgnTestHelper.header("*") + "1. e4 {outer {inner}} e5 *\n\n",
        LenientPgnParserValidationProblem.MOVETEXT_COMMENTARY_END_BRACE_WITHOUT_START_BRACE);
  }

  // -------------------------------------------------------------------------------------------------
  // R3 - stray closing brace
  // -------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void r3_strayCloseInLeadingPosition() {
    expectError(PgnTestHelper.header("*") + "} 1. e4 e5 *\n\n",
        LenientPgnParserValidationProblem.MOVETEXT_COMMENTARY_END_BRACE_WITHOUT_START_BRACE);
  }

  @SuppressWarnings("static-method")
  @Test
  void r3_strayCloseInTrailingSlot() {
    expectError(PgnTestHelper.header("*") + "1. e4 } e5 *\n\n",
        LenientPgnParserValidationProblem.MOVETEXT_COMMENTARY_END_BRACE_WITHOUT_START_BRACE);
  }

  @SuppressWarnings("static-method")
  @Test
  void r3_strayCloseAfterClosedCommentary() {
    expectError(PgnTestHelper.header("*") + "1. e4 {ok}} e5 *\n\n",
        LenientPgnParserValidationProblem.MOVETEXT_COMMENTARY_END_BRACE_WITHOUT_START_BRACE);
  }

  // -------------------------------------------------------------------------------------------------
  // R4 - brace at SAN-expected position
  // -------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void r4_braceBeforeFirstSan() {
    expectError(PgnTestHelper.header("*") + "1. {comment} e4 e5 *\n\n",
        LenientPgnParserValidationProblem.MOVETEXT_COMMENTARY_NOT_ALLOWED_IN_SAN);
  }

  @SuppressWarnings("static-method")
  @Test
  void r4_braceBeforeBlackSan() {
    expectError(PgnTestHelper.header("*") + "1. e4 {c1} {c2} e5 *\n\n",
        LenientPgnParserValidationProblem.MOVETEXT_COMMENTARY_NOT_ALLOWED_IN_SAN);
  }

  @SuppressWarnings("static-method")
  @Test
  void r3_strayCloseAtSanExpectedPosition() {
    // Broken-brace lexical errors take precedence over the positional R4.
    expectError(PgnTestHelper.header("*") + "1. } e4 e5 *\n\n",
        LenientPgnParserValidationProblem.MOVETEXT_COMMENTARY_END_BRACE_WITHOUT_START_BRACE);
  }

  // -------------------------------------------------------------------------------------------------
  // Post-termination content - same rule as strict
  // -------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void postTerminationWellFormedBraceIsRejected() {
    expectError(PgnTestHelper.header("*") + "1. e4 e5 * {after result}\n\n",
        LenientPgnParserValidationProblem.MOVETEXT_CONTENT_AFTER_TERMINATION);
  }

  @SuppressWarnings("static-method")
  @Test
  void postTerminationStrayCloseUsesSpecificCategory() {
    expectError(PgnTestHelper.header("*") + "1. e4 e5 * }\n\n",
        LenientPgnParserValidationProblem.MOVETEXT_COMMENTARY_END_BRACE_WITHOUT_START_BRACE);
  }

  @SuppressWarnings("static-method")
  @Test
  void postTerminationUnclosedBraceUsesSpecificCategory() {
    expectError(PgnTestHelper.header("*") + "1. e4 e5 * {no closing\n\n",
        LenientPgnParserValidationProblem.MOVETEXT_COMMENTARY_START_BRACE_NOT_FOLLOWED_BY_END_BRACE);
  }

  @SuppressWarnings("static-method")
  @Test
  void postTerminationRandomSymbolIsRejected() {
    expectError(PgnTestHelper.header("*") + "1. e4 e5 * garbage\n\n",
        LenientPgnParserValidationProblem.MOVETEXT_CONTENT_AFTER_TERMINATION);
  }

  // -------------------------------------------------------------------------------------------------
  // Whitespace and control-character handling in commentary content.
  // -------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void tabInMoveCommentaryIsPreservedVerbatim() {
    final PgnGame file = LenientPgnParser.parseText(PgnTestHelper.header("*") + "1. e4 {a\tb} e5 *\n\n");
    assertEquals("a\tb", Nulls.get(file.halfMoveList(), 0).commentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void carriageReturnInMoveCommentaryIsNormalisedToLf() {
    // T-005: lone CR -> LF at parser input.
    final PgnGame file = LenientPgnParser.parseText(PgnTestHelper.header("*") + "1. e4 {a\rb} e5 *\n\n");
    assertEquals("a\nb", Nulls.get(file.halfMoveList(), 0).commentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void crlfInMoveCommentaryIsNormalisedToLf() {
    final PgnGame file = LenientPgnParser.parseText(PgnTestHelper.header("*") + "1. e4 {a\r\nb} e5 *\n\n");
    assertEquals("a\nb", Nulls.get(file.halfMoveList(), 0).commentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void consecutiveTabsArePreservedVerbatim() {
    final PgnGame file = LenientPgnParser.parseText(PgnTestHelper.header("*") + "1. e4 {a\t\tb} e5 *\n\n");
    assertEquals("a\t\tb", Nulls.get(file.halfMoveList(), 0).commentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void tabInPreGameCommentaryIsPreservedVerbatim() {
    final PgnGame file = LenientPgnParser.parseText(PgnTestHelper.header("*") + "{intro\tnote} 1. e4 e5 *\n\n");
    assertEquals("intro\tnote", file.pregameCommentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void doubleSpacesInSourceArePreservedAsIs() {
    final PgnGame file = LenientPgnParser.parseText(PgnTestHelper.header("*") + "1. e4 {a  b} e5 *\n\n");
    assertEquals("a  b", Nulls.get(file.halfMoveList(), 0).commentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void otherControlCharInCommentaryIsRejected() {
    // Bell (U+0007), Cc category (other than \t \n \r) - rejected per the Unicode contract.
    expectError(PgnTestHelper.header("*") + "1. e4 {ab} e5 *\n\n",
        LenientPgnParserValidationProblem.MOVETEXT_COMMENTARY_CONTAINS_FORBIDDEN_CHARACTER);
  }

  // -------------------------------------------------------------------------------------------------
  // T-002 - lenient accepts both forms (with and without "N..."). Strict requires it; see TestCommentaryStrict.
  // -------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void t002_acceptCanonicalMoveNumberAfterCommentaryOnWhite() {
    final PgnGame file = LenientPgnParser.parseText(PgnTestHelper.header("*") + "1. e4 {after-white} 1... e5 *\n\n");
    assertEquals("after-white", Nulls.get(file.halfMoveList(), 0).commentary().value());
    assertEquals("e5", Nulls.get(file.halfMoveList(), 1).san());
  }

  @SuppressWarnings("static-method")
  @Test
  void t002_acceptMissingMoveNumberAfterCommentaryOnWhite() {
    final PgnGame file = LenientPgnParser.parseText(PgnTestHelper.header("*") + "1. e4 {after-white} e5 *\n\n");
    assertEquals("after-white", Nulls.get(file.halfMoveList(), 0).commentary().value());
    assertEquals("e5", Nulls.get(file.halfMoveList(), 1).san());
  }

  @SuppressWarnings("static-method")
  @Test
  void t002_acceptMissingMoveNumberAfterCommentaryAtHigherFullMoveNumber() {
    final PgnGame file = LenientPgnParser
        .parseText(PgnTestHelper.header("*") + "1. e4 e5 2. Nf3 {after-white-2} Nc6 *\n\n");
    assertEquals("after-white-2", Nulls.get(file.halfMoveList(), 2).commentary().value());
    assertEquals("Nc6", Nulls.get(file.halfMoveList(), 3).san());
  }

  // -------------------------------------------------------------------------------------------------
  // Helpers
  // -------------------------------------------------------------------------------------------------

  private static void expectError(String pgnText, LenientPgnParserValidationProblem expected) {
    boolean isException = false;
    try {
      LenientPgnParser.parseText(pgnText);
    } catch (final LenientPgnParserValidationException e) {
      isException = true;
      assertEquals(expected, e.getLenientPgnParserValidationProblem(),
          "Wrong problem category; message was: " + e.getMessage());
    }
    assertTrue(isException, "Expected " + expected + " but parser accepted the input");
  }

  @SuppressWarnings("static-method")
  @Test
  void v11_commentaryWithLinebreaks() {
    final PgnGame file = LenientPgnParser
        .parseText(PgnTestHelper.header("*") + "1. e4!? {spicy\n" + "groovy}" + " e5 *\n\n");
    assertEquals("spicy\ngroovy", Nulls.get(file.halfMoveList(), 0).commentary().value());
    assertEquals(com.dlb.chess.enums.MoveSuffixAnnotation.INTERESTING_MOVE,
        Nulls.get(file.halfMoveList(), 0).moveSuffixAnnotation());
  }

}
