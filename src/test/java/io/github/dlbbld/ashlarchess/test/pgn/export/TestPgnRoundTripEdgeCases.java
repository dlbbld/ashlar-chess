// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.export;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.fen.internal.FenConstants;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParser;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParserValidationProblem;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParserValidationResult;
import io.github.dlbbld.ashlarchess.pgn.PgnCreate;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParser;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParserValidationProblem;
import io.github.dlbbld.ashlarchess.pgn.Tag;
import io.github.dlbbld.ashlarchess.pgn.WriteMode;
import io.github.dlbbld.ashlarchess.pgn.internal.TagUtility;

/**
 * Round-trip edge cases for the semantic-export contract: input shapes the lenient parser accepts must survive
 * {@code parse -> semantic-write -> parse} without information loss or invalid output.
 *
 * <ul>
 * <li>Tag values containing the two escape-required characters from PGN spec section 8.1.2 (backslash and quote) - the
 * tokenizer unescapes on read, so the exporter must re-escape on write.</li>
 * <li>A tags-only PGN with no movetext and no termination marker - the lenient parser accepts this shape, semantic
 * export must produce a well-formed (re-parseable) PGN, not throw because the movetext string is empty.</li>
 * <li>No-move games that carry only a starting position (from the initial position, and from a non-initial FEN with
 * white or black to move, including castling rights and an en-passant target) - these exercise the FEN-start /
 * zero-move export-and-reparse path on its own, independent of the CHA position fixtures that happen to use the same
 * shape for a different purpose.</li>
 * </ul>
 */
@SuppressWarnings("static-method")
class TestPgnRoundTripEdgeCases {

  @Test
  void emptyPregameCommentRoundTripsWithoutInventingTagsOrResult() {
    final PgnGame game = LenientPgnParser.parseText("{}");
    final String semantic = PgnCreate.toPgnString(game);
    assertEquals("{}\n\n", semantic);
    assertEquals(game, LenientPgnParser.parseText(semantic));
    assertEquals(LenientPgnParserValidationProblem.FILE_EMPTY, LenientPgnParser.validateText("\n\n").parserProblem());
  }

  @Test
  void blankLinesInsideCommentsSurviveArchivalExport() {
    final String input = "[Event \"{\"]\n[Result \"*\"]\n\n{intro\r\n\r\n\r\nend} 1. e4 {first{\n\nsecond} e5 *";
    final PgnGame game = LenientPgnParser.parseText(input);
    final String archive = PgnCreate.toPgnString(game, WriteMode.ARCHIVAL);
    final PgnGame strict = StrictPgnParser.parseText(archive);
    assertEquals("intro\n\n\nend", strict.pregameCommentary().value());
    assertEquals(game.moves(), strict.moves());
    assertEquals("first{\n\nsecond", Nulls.getFirst(strict.moves()).commentary().value());
    assertEquals(StrictPgnParserValidationProblem.FILE_EMPTY_LINE_EXACTLY_TWO,
        StrictPgnParser.validateText(archive + "\n").parserProblem());
  }

  @Test
  void tagValueLengthCountsDecodedCharacters() {
    final String prefix = "v".repeat(252) + "\uD83D\uDE00";
    final String input = "[Label \"" + prefix + "\\\"\\\\\"]\n[Result \"*\"]\n\n*\n\n";
    final PgnGame game = StrictPgnParser.parseText(input);
    assertEquals(prefix + "\"\\", TagUtility.readTagValue(game, "Label"));
    assertEquals(game, LenientPgnParser.parseText(input));
    final String archive = PgnCreate.toPgnString(game, WriteMode.ARCHIVAL);
    assertEquals(prefix + "\"\\", TagUtility.readTagValue(StrictPgnParser.parseText(archive), "Label"));
  }

  @Test
  void invalidTagStringsAreRejectedAtImportAndArchivalExport() {
    assertInvalidTagString("a\0b");
    assertInvalidTagString("a\tb");
    assertInvalidTagString("a\u007fb");
    assertInvalidTagString("a\u0085b");
    assertInvalidTagString("a\uD800b");
    assertInvalidTagString("a\u2028b");
    assertInvalidTagString("a\u2029b");
    assertInvalidTagString("a\uE000b");
    assertInvalidTagString("a\uFDD0b");
    assertInvalidTagString("a\uFFFFb");
    assertInvalidTagString("v".repeat(255) + "v");
  }

  @Test
  void tagValuesKeepFormatCharactersAndCodePointsNewerThanTheJdk() {
    // Regression: these were rejected as non-printing. The zero-width non-joiner is part of Persian spelling and the
    // zero-width joiner of emoji sequences. U+1FAE8 is assigned since Unicode 15, so Java 17 still reports it as
    // unassigned, and U+0378 is unassigned in every Unicode version so far: accepting both keeps the verdict the same
    // on every Java version.
    for (final String value : Nulls.listOf("\u0639\u0644\u06CC\u200C\u0631\u0636\u0627",
        "\uD83D\uDC68\u200D\uD83D\uDC69", "a\u00ADb", "a\u200Bb", "a\u200Fb", "a\uFEFFb",
        "a" + Character.toString(0x1FAE8) + "b", "a" + Character.toString(0x0378) + "b")) {
      final String input = "[Label \"" + value + "\"]\n[Result \"*\"]\n\n*\n\n";
      final PgnGame game = StrictPgnParser.parseText(input);
      assertEquals(value, TagUtility.readTagValue(game, "Label"));
      assertEquals(game, LenientPgnParser.parseText(input));
      final String archive = PgnCreate.toPgnString(game, WriteMode.ARCHIVAL);
      assertEquals(value, TagUtility.readTagValue(StrictPgnParser.parseText(archive), "Label"));
    }
  }

  private static void assertInvalidTagString(String value) {
    final String input = "[Label \"" + value + "\"]\n[Result \"*\"]\n\n*\n\n";
    assertEquals(StrictPgnParserValidationProblem.TAG_FORMAT_INVALID,
        StrictPgnParser.validateText(input).parserProblem());
    assertEquals(LenientPgnParserValidationProblem.TAG_FORMAT_INVALID,
        LenientPgnParser.validateText(input).parserProblem());
    final PgnGame game = PgnCreate.createPgnGame(new Board(), Nulls.asList(new Tag("Label", value)));
    assertThrows(IllegalArgumentException.class, () -> PgnCreate.toPgnString(game, WriteMode.ARCHIVAL));
  }

  @Test
  void test01_tagValueWithEmbeddedQuoteAndBackslashRoundTrips() {
    // PGN spec section 8.1.2: inside a tag string, a literal " is encoded as \" and a literal \ is encoded as
    // \\. The tokenizer unescapes; the exporter must re-escape. Without the fix, semantic export emitted the
    // raw unescaped characters inside the quotes, producing an invalid PGN that re-parsing rejected.
    final String pgn = """
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
    assertEquals("A \"Quote\" and slash \\", TagUtility.readTagValue(parsed, "Event"));

    final String exported = PgnCreate.toPgnString(parsed, WriteMode.SEMANTIC);
    // Exported form re-escapes both backslash and quote, so re-parsing recovers the same unescaped value.
    final PgnGame reparsed = LenientPgnParser.parseText(exported);
    assertEquals("A \"Quote\" and slash \\", TagUtility.readTagValue(reparsed, "Event"));

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
    final String pgn = """
        [Event "Spring Classic"]
        [White "Alice"]

        """;
    final LenientPgnParserValidationResult parseResult = LenientPgnParser.validateText(pgn);
    assertTrue(parseResult.isValid(), () -> "expected valid; got: " + parseResult.message());
    final PgnGame parsed = pgnGameOf(parseResult);

    // Should not throw on an empty movetext.
    final String exported = PgnCreate.toPgnString(parsed, WriteMode.SEMANTIC);

    // Re-parsing the exported PGN must succeed and yield the same tag set + same empty movetext signal.
    final LenientPgnParserValidationResult reparseResult = LenientPgnParser.validateText(exported);
    assertTrue(reparseResult.isValid(), () -> "expected valid re-parse; got: " + reparseResult.message());
    final PgnGame reparsed = pgnGameOf(reparseResult);
    assertEquals(parsed.tags(), reparsed.tags());
    assertTrue(reparsed.moves().isEmpty());
    assertEquals(null, reparsed.terminationMarker());
  }

  @Test
  void test03_noMoveGameFromStartingPositionRoundTrips() {
    final PgnGame reparsed = assertNoMoveRoundTrip("""
        [Event "?"]
        [Site "?"]
        [Date "????.??.??"]
        [Round "?"]
        [White "?"]
        [Black "?"]
        [Result "*"]

        *

        """);
    assertEquals(FenConstants.FEN_INITIAL, reparsed.startFen(), "a no-FEN no-move game must round-trip as the start");
  }

  @Test
  void test04_noMoveGameFromNonStartingPositionWhiteToMoveRoundTrips() {
    // Non-initial position, White to move, full castling rights - exercises FEN + SetUp + castling-tag round-trip.
    final PgnGame reparsed = assertNoMoveRoundTrip("""
        [Event "?"]
        [Site "?"]
        [Date "????.??.??"]
        [Round "?"]
        [White "?"]
        [Black "?"]
        [Result "*"]
        [SetUp "1"]
        [FEN "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1"]

        *

        """);
    assertEquals(Side.WHITE, reparsed.startFen().sideToMove());
    assertTrue(!FenConstants.FEN_INITIAL.equals(reparsed.startFen()), "must be a non-starting position");
  }

  @Test
  void test05_noMoveGameFromNonStartingPositionBlackToMoveRoundTrips() {
    // Same shape, Black to move.
    final PgnGame reparsed = assertNoMoveRoundTrip("""
        [Event "?"]
        [Site "?"]
        [Date "????.??.??"]
        [Round "?"]
        [White "?"]
        [Black "?"]
        [Result "*"]
        [SetUp "1"]
        [FEN "r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 0 1"]

        *

        """);
    assertEquals(Side.BLACK, reparsed.startFen().sideToMove());
  }

  @Test
  void test06_noMoveGameWithEnPassantTargetRoundTrips() {
    // Non-initial position with a capturable en-passant target (after 1. e4 d5 2. e5 f5), White to move - exercises
    // the en-passant FEN field on the zero-move round-trip.
    final PgnGame reparsed = assertNoMoveRoundTrip("""
        [Event "?"]
        [Site "?"]
        [Date "????.??.??"]
        [Round "?"]
        [White "?"]
        [Black "?"]
        [Result "*"]
        [SetUp "1"]
        [FEN "rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 3"]

        *

        """);
    assertEquals(Square.F6, reparsed.startFen().enPassantCaptureTargetSquare(),
        "the en-passant target must survive the zero-move round-trip");
  }

  /**
   * Round-trips a no-move PGN through {@code parse -> semantic export -> parse}: the start position, the zero-move
   * shape, and the termination marker must be preserved, and a second export must equal the first (fixed point).
   */
  private static PgnGame assertNoMoveRoundTrip(String pgn) {
    final PgnGame parsed = LenientPgnParser.parseText(pgn);
    assertTrue(parsed.moves().isEmpty(), "fixture is supposed to have no moves");

    final String exported = PgnCreate.toPgnString(parsed, WriteMode.SEMANTIC);
    final PgnGame reparsed = LenientPgnParser.parseText(exported);

    assertEquals(parsed.startFen(), reparsed.startFen(),
        () -> "start position lost on round-trip; exported was:\n" + exported);
    assertTrue(reparsed.moves().isEmpty(), "round-trip must keep zero moves");
    assertEquals(parsed.terminationMarker(), reparsed.terminationMarker(), "termination marker lost on round-trip");
    assertEquals(exported, PgnCreate.toPgnString(reparsed, WriteMode.SEMANTIC), "semantic export must be idempotent");
    return reparsed;
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
          + result.parserProblem() + ", message=" + result.message());
    }
    return pgnGame;
  }
}
