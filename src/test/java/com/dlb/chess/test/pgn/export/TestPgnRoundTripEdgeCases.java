package com.dlb.chess.test.pgn.export;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.pgn.LenientPgnParser;
import com.dlb.chess.pgn.LenientPgnParserValidationResult;
import com.dlb.chess.pgn.PgnCreate;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.pgn.TagUtility;
import com.dlb.chess.pgn.WriteMode;

/**
 * Round-trip edge cases for the semantic-export contract: input shapes the lenient parser accepts must survive
 * {@code parse -> semantic-write -> parse} without information loss or invalid output.
 *
 * <ul>
 * <li>Tag values containing the two escape-required characters from PGN spec section 8.1.2 (backslash and quote) - the
 * tokenizer unescapes on read, so the exporter must re-escape on write.</li>
 * <li>A tags-only PGN with no movetext and no termination marker - the lenient parser accepts this shape, semantic
 * export must produce a well-formed (re-parseable) PGN, not throw because the movetext string is empty.</li>
 * </ul>
 */
@SuppressWarnings("static-method")
class TestPgnRoundTripEdgeCases {

  @Test
  void test01_tagValueWithEmbeddedQuoteAndBackslashRoundTrips() {
    // PGN spec section 8.1.2: inside a tag string, a literal " is encoded as \" and a literal \ is encoded as
    // \\. The tokenizer unescapes; the exporter must re-escape. Without the fix, semantic export emitted the
    // raw unescaped characters inside the quotes, producing an invalid PGN that re-parsing rejected.
    final var pgn = """
        [Event "A \\"Quote\\" and slash \\\\"]
        [Site "?"]
        [Date "????.??.??"]
        [Round "?"]
        [White "?"]
        [Black "?"]
        [Result "*"]

        *

        """;
    final PgnGame parsed = LenientPgnParser.parseText(pgn);
    // Model carries the unescaped form.
    assertEquals("A \"Quote\" and slash \\", TagUtility.calculateTagValue(parsed, "Event"));

    final String exported = PgnCreate.createPgnString(parsed, WriteMode.SEMANTIC);
    // Exported form re-escapes both backslash and quote, so re-parsing recovers the same unescaped value.
    final PgnGame reparsed = LenientPgnParser.parseText(exported);
    assertEquals("A \"Quote\" and slash \\", TagUtility.calculateTagValue(reparsed, "Event"));

    // And the exported representation contains the spec-required escapes in the bracketed value.
    assertTrue(exported.contains("[Event \"A \\\"Quote\\\" and slash \\\\\"]"),
        () -> "expected escaped form in exported PGN; got: " + exported);
  }

  @Test
  void test02_tagsOnlyPgnRoundTripsViaSemanticExport() {
    // Lenient parser accepts a PGN with tags only - no movetext, no termination marker. Without the fix,
    // semantic export threw from PgnLineWrapper because the empty movetext string was passed to the wrap
    // helper (which rejects empty input). The fix: semantic export skips the wrap call when there is no
    // movetext content, producing tag section + separator + trailing blank.
    final var pgn = """
        [Event "Spring Classic"]
        [White "Alice"]

        """;
    final LenientPgnParserValidationResult parseResult = LenientPgnParser.validateText(pgn);
    assertTrue(parseResult.isValid(), () -> "expected valid; got: " + parseResult.message());
    final PgnGame parsed = pgnGameOf(parseResult);

    // Should not throw on an empty movetext.
    final String exported = PgnCreate.createPgnString(parsed, WriteMode.SEMANTIC);

    // Re-parsing the exported PGN must succeed and yield the same tag set + same empty movetext signal.
    final LenientPgnParserValidationResult reparseResult = LenientPgnParser.validateText(exported);
    assertTrue(reparseResult.isValid(), () -> "expected valid re-parse; got: " + reparseResult.message());
    final PgnGame reparsed = pgnGameOf(reparseResult);
    assertEquals(parsed.tagList(), reparsed.tagList());
    assertTrue(reparsed.halfMoveList().isEmpty());
    assertEquals(null, reparsed.terminationMarker());
  }

  /**
   * Extracts the {@link PgnGame} from a successful validation result, asserting non-null. Gives the JDT null-flow
   * analysis the narrowed type it needs at the use site - {@code LenientPgnParserValidationResult
   * .pgnGame()} is declared {@code @Nullable} (it carries {@code null} on failure), and JDT does not infer non-null
   * from {@code isValid()} alone.
   */
  private static PgnGame pgnGameOf(LenientPgnParserValidationResult result) {
    final PgnGame pgnGame = result.pgnGame();
    if (pgnGame == null) {
      throw new AssertionError("Expected a non-null PgnGame on the lenient PGN validation result; problem="
          + result.problemParser() + ", message=" + result.message());
    }
    return pgnGame;
  }
}
