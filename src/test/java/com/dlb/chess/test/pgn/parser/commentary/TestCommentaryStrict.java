package com.dlb.chess.test.pgn.parser.commentary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.pgn.StrictPgnParser;
import com.dlb.chess.pgn.StrictPgnParserValidationException;
import com.dlb.chess.pgn.StrictPgnParserValidationProblem;

/**
 * Commentary delimiter validation for StrictPgnParser. Three delimiter rules:
 *
 * <ul>
 * <li>R1 - every opening delimiter must have a matching closing delimiter (else unclosed-commentary error).
 * <li>R3 - a closing delimiter outside any open commentary is a stray-close error.
 * <li>R4 - a commentary delimiter where a SAN half-move is expected is rejected.
 * </ul>
 *
 * <p>
 * (R2 - nesting - was retired by T-003; an inner opening delimiter is now content per PGN spec section 8.2.5.)
 */
class TestCommentaryStrict {

  // -------------------------------------------------------------------------------------------------
  // Valid cases - structural acceptance and commentary-text assertions
  // -------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void v01_pregameCommentaryOnly() {
    final PgnGame file = StrictPgnParser.parseText(header("*") + "{opening remark} 1. e4 e5 *\n\n");
    assertEquals("opening remark", file.pregameCommentary().value());
    assertEquals(2, file.halfMoveList().size());
    assertEquals("", Nulls.get(file.halfMoveList(), 0).commentary().value());
    assertEquals("", Nulls.get(file.halfMoveList(), 1).commentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void v02_trailingCommentaryAfterWhiteMove() {
    final PgnGame file = StrictPgnParser.parseText(header("*") + "1. e4 {good opening} 1... e5 *\n\n");
    assertEquals("", file.pregameCommentary().value());
    assertEquals("good opening", Nulls.get(file.halfMoveList(), 0).commentary().value());
    assertEquals("", Nulls.get(file.halfMoveList(), 1).commentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void v03_trailingCommentaryAfterBlackMove() {
    final PgnGame file = StrictPgnParser.parseText(header("*") + "1. e4 e5 {symmetric} 2. Nf3 Nc6 *\n\n");
    assertEquals("", Nulls.get(file.halfMoveList(), 0).commentary().value());
    assertEquals("symmetric", Nulls.get(file.halfMoveList(), 1).commentary().value());
    assertEquals("", Nulls.get(file.halfMoveList(), 2).commentary().value());
    assertEquals("", Nulls.get(file.halfMoveList(), 3).commentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void v04_commentaryAfterEveryHalfMove() {
    final PgnGame file = StrictPgnParser.parseText(header("*") + "1. e4 {a} 1... e5 {b} 2. Nf3 {c} 2... Nc6 {d} *\n\n");
    assertEquals("a", Nulls.get(file.halfMoveList(), 0).commentary().value());
    assertEquals("b", Nulls.get(file.halfMoveList(), 1).commentary().value());
    assertEquals("c", Nulls.get(file.halfMoveList(), 2).commentary().value());
    assertEquals("d", Nulls.get(file.halfMoveList(), 3).commentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void v05_leadingAndTrailingCommentary() {
    final PgnGame file = StrictPgnParser.parseText(header("*") + "{intro} 1. e4 {after-1-white} 1... e5 *\n\n");
    assertEquals("intro", file.pregameCommentary().value());
    assertEquals("after-1-white", Nulls.get(file.halfMoveList(), 0).commentary().value());
    assertEquals("", Nulls.get(file.halfMoveList(), 1).commentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void v06_emptyCommentary() {
    final PgnGame file = StrictPgnParser.parseText(header("*") + "1. e4 {} 1... e5 *\n\n");
    assertEquals("", Nulls.get(file.halfMoveList(), 0).commentary().value());
    assertEquals("", Nulls.get(file.halfMoveList(), 1).commentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void v07_commentaryWithPunctuationButNoBraces() {
    final PgnGame file = StrictPgnParser.parseText(header("*") + "1. e4 {special chars !? + # - / .} 1... e5 *\n\n");
    assertEquals("special chars !? + # - / .", Nulls.get(file.halfMoveList(), 0).commentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void v08_multilineCommentaryPreservedVerbatim() {
    final PgnGame file = StrictPgnParser.parseText(header("*") + "1. e4 {line one\nline two} 1... e5 *\n\n");
    assertEquals("line one\nline two", Nulls.get(file.halfMoveList(), 0).commentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void v09_commentaryAfterSuffixAnnotation() {
    final PgnGame file = StrictPgnParser.parseText(header("*") + "1. e4!? {spicy} 1... e5 *\n\n");
    assertEquals("spicy", Nulls.get(file.halfMoveList(), 0).commentary().value());
    assertEquals(com.dlb.chess.enums.MoveSuffixAnnotation.INTERESTING_MOVE,
        Nulls.get(file.halfMoveList(), 0).moveSuffixAnnotation());
  }

  @SuppressWarnings("static-method")
  @Test
  void v10_zeroMoveGameWithPreGameCommentaryOnly() {
    final PgnGame file = StrictPgnParser.parseText(header("*") + "{no moves played} *\n\n");
    assertEquals("no moves played", file.pregameCommentary().value());
    assertEquals(0, file.halfMoveList().size());
  }

  // -------------------------------------------------------------------------------------------------
  // R1 - unclosed commentary (start brace without matching close)
  // -------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void r1_unclosedAtEof() {
    expectError(header("*") + "1. e4 {unclosed\n\n",
        StrictPgnParserValidationProblem.MOVETEXT_COMMENTARY_START_BRACE_NOT_FOLLOWED_BY_END_BRACE);
  }

  @SuppressWarnings("static-method")
  @Test
  void r1_unclosedPreGameCommentary() {
    expectError(header("*") + "{unclosed leading 1. e4 e5 *\n\n",
        StrictPgnParserValidationProblem.MOVETEXT_COMMENTARY_START_BRACE_NOT_FOLLOWED_BY_END_BRACE);
  }

  // -------------------------------------------------------------------------------------------------
  // T-003 - an inner opening delimiter is content (PGN spec section 8.2.5). Only the closing delimiter closes a
  // comment.
  // -------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void t003_openBraceInPreGameCommentaryIsContent() {
    final PgnGame file = StrictPgnParser.parseText(header("*") + "{outer {inner} 1. e4 e5 *\n\n");
    assertEquals("outer {inner", file.pregameCommentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void t003_openBraceInTrailingCommentaryIsContent() {
    final PgnGame file = StrictPgnParser.parseText(header("*") + "1. e4 {outer {inner} 1... e5 *\n\n");
    assertEquals("outer {inner", Nulls.get(file.halfMoveList(), 0).commentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void t003_openBraceImmediatelyAtStartOfContent() {
    final PgnGame file = StrictPgnParser.parseText(header("*") + "1. e4 {{nested right away} 1... e5 *\n\n");
    assertEquals("{nested right away", Nulls.get(file.halfMoveList(), 0).commentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void t003_strayCloseAfterClosedCommentaryWithInnerOpenBraceIsR3() {
    // The comment closes at the first closing delimiter; the trailing closing delimiter is a stray close (R3).
    expectError(header("*") + "1. e4 {outer {inner}} e5 *\n\n",
        StrictPgnParserValidationProblem.MOVETEXT_COMMENTARY_END_BRACE_WITHOUT_START_BRACE);
  }

  // -------------------------------------------------------------------------------------------------
  // R3 - stray closing brace outside any open commentary
  // -------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void r3_strayCloseInLeadingPosition() {
    expectError(header("*") + "} 1. e4 e5 *\n\n",
        StrictPgnParserValidationProblem.MOVETEXT_COMMENTARY_END_BRACE_WITHOUT_START_BRACE);
  }

  @SuppressWarnings("static-method")
  @Test
  void r3_strayCloseInTrailingSlot() {
    expectError(header("*") + "1. e4 } e5 *\n\n",
        StrictPgnParserValidationProblem.MOVETEXT_COMMENTARY_END_BRACE_WITHOUT_START_BRACE);
  }

  @SuppressWarnings("static-method")
  @Test
  void r3_strayCloseAfterClosedCommentary() {
    expectError(header("*") + "1. e4 {ok}} e5 *\n\n",
        StrictPgnParserValidationProblem.MOVETEXT_COMMENTARY_END_BRACE_WITHOUT_START_BRACE);
  }

  // -------------------------------------------------------------------------------------------------
  // R4 - brace at SAN-expected position
  // -------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void r4_braceBeforeFirstSan() {
    expectError(header("*") + "1. {comment} e4 e5 *\n\n",
        StrictPgnParserValidationProblem.MOVETEXT_COMMENTARY_NOT_ALLOWED_IN_SAN);
  }

  @SuppressWarnings("static-method")
  @Test
  void r4_braceBeforeBlackSan() {
    expectError(header("*") + "1. e4 {c1} 1... {c2} e5 *\n\n",
        StrictPgnParserValidationProblem.MOVETEXT_COMMENTARY_NOT_ALLOWED_IN_SAN);
  }

  @SuppressWarnings("static-method")
  @Test
  void r3_strayCloseAtSanExpectedPosition() {
    // Broken-delimiter lexical errors take precedence over the positional R4 - this case is R3, not R4.
    expectError(header("*") + "1. } e4 e5 *\n\n",
        StrictPgnParserValidationProblem.MOVETEXT_COMMENTARY_END_BRACE_WITHOUT_START_BRACE);
  }

  // -------------------------------------------------------------------------------------------------
  // Post-termination content - commentary may not appear after the game termination marker
  // -------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void postTerminationWellFormedBraceIsRejected() {
    expectError(header("*") + "1. e4 e5 * {after result}\n\n",
        StrictPgnParserValidationProblem.MOVETEXT_CONTENT_AFTER_TERMINATION);
  }

  @SuppressWarnings("static-method")
  @Test
  void postTerminationStrayCloseUsesSpecificCategory() {
    expectError(header("*") + "1. e4 e5 * }\n\n",
        StrictPgnParserValidationProblem.MOVETEXT_COMMENTARY_END_BRACE_WITHOUT_START_BRACE);
  }

  @SuppressWarnings("static-method")
  @Test
  void postTerminationUnclosedBraceUsesSpecificCategory() {
    expectError(header("*") + "1. e4 e5 * {no closing\n\n",
        StrictPgnParserValidationProblem.MOVETEXT_COMMENTARY_START_BRACE_NOT_FOLLOWED_BY_END_BRACE);
  }

  @SuppressWarnings("static-method")
  @Test
  void postTerminationRandomSymbolIsRejected() {
    expectError(header("*") + "1. e4 e5 * garbage\n\n",
        StrictPgnParserValidationProblem.MOVETEXT_CONTENT_AFTER_TERMINATION);
  }

  // -------------------------------------------------------------------------------------------------
  // Whitespace and control-character handling in commentary content.
  // -------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void tabInPreGameCommentaryIsPreserved() {
    final PgnGame file = StrictPgnParser.parseText(header("*") + "{a\tb} 1. e4 e5 *\n\n");
    assertEquals("a\tb", file.pregameCommentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void tabInMoveCommentaryIsPreserved() {
    final PgnGame file = StrictPgnParser.parseText(header("*") + "1. e4 {a\tb} 1... e5 *\n\n");
    assertEquals("a\tb", Nulls.get(file.halfMoveList(), 0).commentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void carriageReturnInPreGameCommentaryIsNormalisedToLf() {
    // T-005: lone CR -> LF at parser input.
    final PgnGame file = StrictPgnParser.parseText(header("*") + "{a\rb} 1. e4 e5 *\n\n");
    assertEquals("a\nb", file.pregameCommentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void crlfInPreGameCommentaryIsNormalisedToLf() {
    final PgnGame file = StrictPgnParser.parseText(header("*") + "{a\r\nb} 1. e4 e5 *\n\n");
    assertEquals("a\nb", file.pregameCommentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void crlfInMoveCommentaryIsNormalisedToLf() {
    final PgnGame file = StrictPgnParser.parseText(header("*") + "1. e4 {a\r\nb} 1... e5 *\n\n");
    assertEquals("a\nb", Nulls.get(file.halfMoveList(), 0).commentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void otherControlCharInCommentaryIsRejected() {
    // Bell character (U+0007), Cc category (other than \t \n \r) - rejected per the Unicode contract.
    expectError(header("*") + "1. e4 {ab} e5 *\n\n",
        StrictPgnParserValidationProblem.MOVETEXT_COMMENTARY_CONTAINS_FORBIDDEN_CHARACTER);
  }

  @SuppressWarnings("static-method")
  @Test
  void doubleSpacesInCommentaryArePreservedAsIs() {
    final PgnGame file = StrictPgnParser.parseText(header("*") + "1. e4 {a  b} 1... e5 *\n\n");
    assertEquals("a  b", Nulls.get(file.halfMoveList(), 0).commentary().value());
  }

  @SuppressWarnings("static-method")
  @Test
  void doubleSpacesInPreGameCommentaryArePreservedAsIs() {
    final PgnGame file = StrictPgnParser.parseText(header("*") + "{line one  line two} 1. e4 e5 *\n\n");
    assertEquals("line one  line two", file.pregameCommentary().value());
  }

  // -------------------------------------------------------------------------------------------------
  // T-002 - strict requires "N..." before a Black move when commentary intervened on White's move
  // (PGN spec section 8.2.2 case 1). Lenient accepts both forms; see TestCommentaryLenient.
  // -------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void t002_rejectMissingMoveNumberAfterCommentaryOnWhite() {
    expectError(header("*") + "1. e4 {after-white} e5 *\n\n",
        StrictPgnParserValidationProblem.MOVETEXT_MOVE_NUMBER_REQUIRED_AFTER_COMMENTARY);
  }

  @SuppressWarnings("static-method")
  @Test
  void t002_rejectMissingMoveNumberAfterCommentaryOnWhiteHigherFullMoveNumber() {
    // Verifies the indicator is the current full-move number, not hardcoded to "1".
    expectError(header("*") + "1. e4 e5 2. Nf3 {after-white-2} Nc6 *\n\n",
        StrictPgnParserValidationProblem.MOVETEXT_MOVE_NUMBER_REQUIRED_AFTER_COMMENTARY);
  }

  @SuppressWarnings("static-method")
  @Test
  void t002_acceptCanonicalMoveNumberAfterCommentaryOnWhite() {
    final PgnGame file = StrictPgnParser.parseText(header("*") + "1. e4 {after-white} 1... e5 *\n\n");
    assertEquals("after-white", Nulls.get(file.halfMoveList(), 0).commentary().value());
    assertEquals("e5", Nulls.get(file.halfMoveList(), 1).san());
  }

  @SuppressWarnings("static-method")
  @Test
  void t002_rejectWrongMoveNumberAfterCommentaryOnWhite() {
    // Indicator with wrong number surfaces with the same category as a missing indicator.
    expectError(header("*") + "1. e4 {after-white} 2... e5 *\n\n",
        StrictPgnParserValidationProblem.MOVETEXT_MOVE_NUMBER_REQUIRED_AFTER_COMMENTARY);
  }

  @SuppressWarnings("static-method")
  @Test
  void t002_noIndicatorRequiredWhenNoCommentaryIntervenes() {
    final PgnGame file = StrictPgnParser.parseText(header("*") + "1. e4 e5 *\n\n");
    assertEquals(2, file.halfMoveList().size());
  }

  @SuppressWarnings("static-method")
  @Test
  void t002_indicatorNotRequiredWhenCommentaryIsOnBlackMove() {
    // Commentary on Black's move does not trigger T-002 - the next move (White) carries its own move number anyway.
    final PgnGame file = StrictPgnParser.parseText(header("*") + "1. e4 e5 {after-black} 2. Nf3 *\n\n");
    assertEquals("after-black", Nulls.get(file.halfMoveList(), 1).commentary().value());
    assertEquals("Nf3", Nulls.get(file.halfMoveList(), 2).san());
  }

  // -------------------------------------------------------------------------------------------------
  // Helpers
  // -------------------------------------------------------------------------------------------------

  /** Minimal seven-tag-roster header ending with the blank line that separates tags from movetext. */
  private static String header(String result) {
    return """
        [Event "?"]
        [Site "?"]
        [Date "?"]
        [Round "?"]
        [White "?"]
        [Black "?"]
        [Result \"""" + result + "\"]\n\n";
  }

  private static void expectError(String pgnText, StrictPgnParserValidationProblem expected) {
    boolean isException = false;
    try {
      StrictPgnParser.parseText(pgnText);
    } catch (final StrictPgnParserValidationException e) {
      isException = true;
      assertEquals(expected, e.getStrictPgnParserValidationProblem(),
          "Wrong problem category; message was: " + e.getMessage());
    }
    assertTrue(isException, "Expected " + expected + " but parser accepted the input");
  }
}
