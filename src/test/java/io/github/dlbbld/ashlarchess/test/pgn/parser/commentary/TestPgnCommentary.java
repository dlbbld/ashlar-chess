package io.github.dlbbld.ashlarchess.test.pgn.parser.commentary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.exceptions.PgnCommentaryValidationException;
import io.github.dlbbld.ashlarchess.pgn.PgnCommentary;

/**
 * Direct tests for the {@link PgnCommentary} value object. The parser-side behaviour is exercised in
 * {@link TestCommentaryStrict} and {@link TestCommentaryLenient}; this test class covers the programmatic-API contract
 * for callers constructing commentary outside the parsers.
 *
 * <p>
 * Contract: a constructed {@code PgnCommentary} must satisfy two prohibitions:
 * <ul>
 * <li><b>Grammar:</b> no {@code {} or {@code }} (would corrupt the brace grammar on export).</li>
 * <li><b>Unicode categories:</b> no control characters except {@code \t} / {@code \n} / {@code \r}, no surrogates, no
 * unassigned code points, no private-use code points.</li>
 * </ul>
 * Everything else is permitted — letters, marks, numbers, punctuation, symbols, separators, format characters
 * (zero-width joiner etc.), and supplementary characters above U+FFFF (emoji, rare scripts).
 */
@SuppressWarnings("null") // JUnit Assertions methods lack JDT null annotations
class TestPgnCommentary {

  // -------------------------------------------------------------------------------------------------
  // Constructor — accepted values (printing characters and ordinary whitespace)
  // -------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void emptyStringIsAccepted() {
    assertEquals("", new PgnCommentary("").value());
  }

  @SuppressWarnings("static-method")
  @Test
  void singleSpaceIsAccepted() {
    assertEquals(" ", new PgnCommentary(" ").value());
  }

  @SuppressWarnings("static-method")
  @Test
  void doubleSpacesArePreserved() {
    assertEquals("a  b", new PgnCommentary("a  b").value());
  }

  @SuppressWarnings("static-method")
  @Test
  void leadingAndTrailingSpacesArePreserved() {
    assertEquals("  hello world  ", new PgnCommentary("  hello world  ").value());
  }

  @SuppressWarnings("static-method")
  @Test
  void typicalPlainTextIsAccepted() {
    assertEquals("This is a comment.", new PgnCommentary("This is a comment.").value());
  }

  @SuppressWarnings("static-method")
  @Test
  void specialCharactersAreAccepted() {
    assertEquals("! ? + # - / .", new PgnCommentary("! ? + # - / .").value());
  }

  // -------------------------------------------------------------------------------------------------
  // Constructor — tab and newline accepted; CR rejected (T-005 model invariant: no `\r` ever).
  // -------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void tabIsAcceptedAndPreservedVerbatim() {
    assertEquals("a\tb", new PgnCommentary("a\tb").value());
  }

  @SuppressWarnings("static-method")
  @Test
  void newlineIsAcceptedAndPreservedVerbatim() {
    assertEquals("line one\nline two", new PgnCommentary("line one\nline two").value());
  }

  @SuppressWarnings("static-method")
  @Test
  void carriageReturnIsRejected() {
    // T-005: parser input is normalised to LF, so `\r` cannot reach the model from a parse path. Direct
    // construction with `\r` violates the invariant and must throw.
    assertThrows(PgnCommentaryValidationException.class, () -> new PgnCommentary("a\rb"));
  }

  @SuppressWarnings("static-method")
  @Test
  void crlfSequenceIsRejected() {
    assertThrows(PgnCommentaryValidationException.class, () -> new PgnCommentary("a\r\nb"));
  }

  @SuppressWarnings("static-method")
  @Test
  void mixedWhitespaceWithCrIsRejected() {
    assertThrows(PgnCommentaryValidationException.class, () -> new PgnCommentary("a \t b\n c\r d"));
  }

  @SuppressWarnings("static-method")
  @Test
  void mixedWhitespaceWithoutCrIsAcceptedAndPreservedVerbatim() {
    assertEquals("a \t b\n c d", new PgnCommentary("a \t b\n c d").value());
  }

  // -------------------------------------------------------------------------------------------------
  // Constructor — Unicode categories: letters, marks, numbers, punctuation, symbols, separators are accepted
  // -------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void latin1LetterWithDiacriticIsAccepted() {
    assertEquals("héllo café résumé", new PgnCommentary("héllo café résumé").value());
  }

  @SuppressWarnings("static-method")
  @Test
  void cjkScriptIsAccepted() {
    assertEquals("漢字仮名混じり", new PgnCommentary("漢字仮名混じり").value());
  }

  @SuppressWarnings("static-method")
  @Test
  void cyrillicScriptIsAccepted() {
    assertEquals("Шахматы — игра королей", new PgnCommentary("Шахматы — игра королей").value());
  }

  @SuppressWarnings("static-method")
  @Test
  void supplementaryCharacterEmojiIsAccepted() {
    // U+1F600 (grinning face) is encoded as a surrogate pair in UTF-16. Validator must iterate by code point,
    // not by char, otherwise each surrogate half would be rejected as Cs.
    final String emoji = "♔ 😀";
    assertEquals(emoji, new PgnCommentary(emoji).value());
  }

  @SuppressWarnings("static-method")
  @Test
  void mathSymbolIsAccepted() {
    assertEquals("a ≥ b", new PgnCommentary("a ≥ b").value());
  }

  @SuppressWarnings("static-method")
  @Test
  void currencySymbolIsAccepted() {
    assertEquals("price: €5", new PgnCommentary("price: €5").value());
  }

  @SuppressWarnings("static-method")
  @Test
  void chessPieceSymbolsAreAccepted() {
    // Unicode chess pieces (U+2654–U+265F).
    assertEquals("♔♕♖♗♘♙♚♛♜♝♞♟", new PgnCommentary("♔♕♖♗♘♙♚♛♜♝♞♟").value());
  }

  @SuppressWarnings("static-method")
  @Test
  void formatCharacterIsAccepted() {
    // Zero-width joiner (U+200D), Cf category — invisible but legitimate text content.
    final String withZwj = "a‍b";
    assertEquals(withZwj, new PgnCommentary(withZwj).value());
  }

  @SuppressWarnings("static-method")
  @Test
  void unicodeLineSeparatorIsAccepted() {
    // U+2028 (LINE SEPARATOR), Zl category. Not a control character; valid text.
    final String withLs = "a b";
    assertEquals(withLs, new PgnCommentary(withLs).value());
  }

  // -------------------------------------------------------------------------------------------------
  // Constructor — rejects other control characters (Unicode Cc except tab / LF / CR)
  // -------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void bellControlCharacterIsRejected() {
    final PgnCommentaryValidationException thrown = assertThrows(PgnCommentaryValidationException.class,
        () -> new PgnCommentary("ab"));
    org.junit.jupiter.api.Assertions.assertTrue(thrown.getMessage().contains("control character"),
        "Message should name the offending category: " + thrown.getMessage());
  }

  @SuppressWarnings("static-method")
  @Test
  void escapeControlCharacterIsRejected() {
    assertThrows(PgnCommentaryValidationException.class, () -> new PgnCommentary("ab"));
  }

  @SuppressWarnings("static-method")
  @Test
  void delCharacterIsRejected() {
    assertThrows(PgnCommentaryValidationException.class, () -> new PgnCommentary("ab"));
  }

  @SuppressWarnings("static-method")
  @Test
  void nelControlCharacterIsRejected() {
    // U+0085 (NEL, NEXT LINE) — Cc category, historically used as line break on some legacy platforms.
    assertThrows(PgnCommentaryValidationException.class, () -> new PgnCommentary("ab"));
  }

  // -------------------------------------------------------------------------------------------------
  // Constructor — rejects malformed Unicode (lone surrogates, unassigned, private-use)
  // -------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void loneHighSurrogateIsRejected() {
    final String commentary = "a" + Character.toString(Character.MIN_HIGH_SURROGATE) + "b";
    final PgnCommentaryValidationException thrown = assertThrows(PgnCommentaryValidationException.class,
        () -> new PgnCommentary(commentary));
    org.junit.jupiter.api.Assertions.assertTrue(thrown.getMessage().contains("surrogate"),
        "Message should name the offending category: " + thrown.getMessage());
  }

  @SuppressWarnings("static-method")
  @Test
  void loneLowSurrogateIsRejected() {
    final String commentary = "a" + Character.toString(Character.MIN_LOW_SURROGATE) + "b";
    assertThrows(PgnCommentaryValidationException.class, () -> new PgnCommentary(commentary));
  }

  @SuppressWarnings("static-method")
  @Test
  void privateUseAreaCharacterIsRejected() {
    // U+E000 — start of the BMP private-use area.
    final PgnCommentaryValidationException thrown = assertThrows(PgnCommentaryValidationException.class,
        () -> new PgnCommentary("ab"));
    org.junit.jupiter.api.Assertions.assertTrue(thrown.getMessage().contains("private-use"),
        "Message should name the offending category: " + thrown.getMessage());
  }

  @SuppressWarnings("static-method")
  @Test
  void unassignedCodePointIsRejected() {
    // U+FDD0 — first of the noncharacter range U+FDD0..U+FDEF (permanently unassigned, Cn category).
    assertThrows(PgnCommentaryValidationException.class, () -> new PgnCommentary("a﷐b"));
  }

  // -------------------------------------------------------------------------------------------------
  // Constructor — only `}` is forbidden in commentary content. `{` is allowed per PGN spec §8.2.5
  // ("a left brace character appearing in a brace comment loses its special meaning").
  // -------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void openingBraceIsAllowedInContent() {
    // T-003: `{` survives validation and round-trips verbatim. Only `}` would terminate the {...} grammar.
    final PgnCommentary commentary = new PgnCommentary("a { b");
    org.junit.jupiter.api.Assertions.assertEquals("a { b", commentary.value());
  }

  @SuppressWarnings("static-method")
  @Test
  void closingBraceIsRejected() {
    final PgnCommentaryValidationException thrown = assertThrows(PgnCommentaryValidationException.class,
        () -> new PgnCommentary("a } b"));
    org.junit.jupiter.api.Assertions.assertTrue(thrown.getMessage().contains("closing brace"),
        "Message should name the offending character: " + thrown.getMessage());
  }

  @SuppressWarnings("static-method")
  @Test
  void openingBraceAtStartIsAllowedInContent() {
    // T-003: `{` at any position is content, not an error. Round-trips verbatim.
    final PgnCommentary commentary = new PgnCommentary("{commentary");
    org.junit.jupiter.api.Assertions.assertEquals("{commentary", commentary.value());
  }

  @SuppressWarnings("static-method")
  @Test
  void closingBraceAtEndIsRejected() {
    assertThrows(PgnCommentaryValidationException.class, () -> new PgnCommentary("commentary}"));
  }

  // -------------------------------------------------------------------------------------------------
  // EMPTY constant
  // -------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void emptyConstantIsTheEmptyString() {
    assertEquals("", PgnCommentary.EMPTY.value());
  }

  @SuppressWarnings("static-method")
  @Test
  void emptyConstantIsACanonicalSingleton() {
    // EMPTY is referenced from many sites — confirm it really is the same instance every time, not allocated lazily.
    assertSame(PgnCommentary.EMPTY, PgnCommentary.EMPTY);
  }
}
