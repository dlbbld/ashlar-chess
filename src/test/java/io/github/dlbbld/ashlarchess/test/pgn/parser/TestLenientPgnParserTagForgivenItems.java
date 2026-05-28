package io.github.dlbbld.ashlarchess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.pgn.ForgivenTagItem;
import io.github.dlbbld.ashlarchess.pgn.ForgivenTagItemCode;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParser;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParserValidationResult;
import io.github.dlbbld.ashlarchess.pgn.StandardTag;

/**
 * The lenient PGN parser preserves the input as given and surfaces tolerated deviations on
 * {@code LenientPgnParserValidationResult.tagForgivenItems()}. One inline fixture per {@link ForgivenTagItemCode}
 * value, asserting both presence and the expected tagName/detail metadata.
 */
@SuppressWarnings("static-method")
class TestLenientPgnParserTagForgivenItems {

  @Test
  void test01_strTagMissingEmitsOneItemPerMissingStrTagExceptResult() {
    // Only Result + Event provided; Site/Date/Round/White/Black are missing. Result has its own dedicated codes
    // and is never emitted by STR_TAG_MISSING. So we expect five items.
    final String pgn = """
        [Event "Spring Classic"]
        [Result "*"]

        1. e4 e5 *

        """;
    final ImmutableList<ForgivenTagItem> items = parseAndGetItems(pgn);

    assertEquals(5, countByCode(items, ForgivenTagItemCode.STR_TAG_MISSING));
    assertHasItem(items, ForgivenTagItemCode.STR_TAG_MISSING, StandardTag.SITE.getName(), "");
    assertHasItem(items, ForgivenTagItemCode.STR_TAG_MISSING, StandardTag.DATE.getName(), "");
    assertHasItem(items, ForgivenTagItemCode.STR_TAG_MISSING, StandardTag.ROUND.getName(), "");
    assertHasItem(items, ForgivenTagItemCode.STR_TAG_MISSING, StandardTag.WHITE.getName(), "");
    assertHasItem(items, ForgivenTagItemCode.STR_TAG_MISSING, StandardTag.BLACK.getName(), "");
  }

  @Test
  void test02_resultTagMissingButTerminationMarkerPresentEmitsCodeWithMarkerValueInDetail() {
    final String pgn = """
        [Event "?"]
        [Site "?"]
        [Date "????.??.??"]
        [Round "?"]
        [White "?"]
        [Black "?"]

        1. e4 e5 1-0

        """;
    final ImmutableList<ForgivenTagItem> items = parseAndGetItems(pgn);

    assertHasItem(items, ForgivenTagItemCode.RESULT_TAG_MISSING_BUT_TERMINATION_MARKER_PRESENT,
        StandardTag.RESULT.getName(), "1-0");
  }

  @Test
  void test03_resultAndTerminationMarkerBothMissingEmitsCode() {
    final String pgn = """
        [Event "?"]
        [Site "?"]
        [Date "????.??.??"]
        [Round "?"]
        [White "?"]
        [Black "?"]

        1. e4 e5

        """;
    final ImmutableList<ForgivenTagItem> items = parseAndGetItems(pgn);

    assertHasItem(items, ForgivenTagItemCode.RESULT_TAG_AND_TERMINATION_MARKER_BOTH_MISSING,
        StandardTag.RESULT.getName(), "");
  }

  @Test
  void test04_setUpTagMissingButFenPresentEmitsCode() {
    final String pgn = """
        [Event "?"]
        [Site "?"]
        [Date "????.??.??"]
        [Round "?"]
        [White "?"]
        [Black "?"]
        [Result "*"]
        [FEN "r1b2r2/pp1pk1pp/8/7q/3pP1n1/5N1P/PPQ2PP1/3R1RK1 w - - 0 17"]

        17. Qa4 *

        """;
    final ImmutableList<ForgivenTagItem> items = parseAndGetItems(pgn);

    assertHasItem(items, ForgivenTagItemCode.SETUP_TAG_MISSING_BUT_FEN_PRESENT, StandardTag.SET_UP.getName(), "");
  }

  @Test
  void test05_setUpTagPresentButFenMissingEmitsCode() {
    final String pgn = """
        [Event "?"]
        [Site "?"]
        [Date "????.??.??"]
        [Round "?"]
        [White "?"]
        [Black "?"]
        [Result "*"]
        [SetUp "1"]

        1. e4 e5 *

        """;
    final ImmutableList<ForgivenTagItem> items = parseAndGetItems(pgn);

    assertHasItem(items, ForgivenTagItemCode.SETUP_TAG_PRESENT_BUT_FEN_MISSING, StandardTag.FEN.getName(), "");
  }

  @Test
  void test06_redundantFenAndSetUpForInitialPositionEmitsCode() {
    // FEN value describes the initial position; the FEN+SetUp pair is redundant since the initial position is the
    // implicit default. The parse model preserves the tags; archival-mode export drops them.
    final String pgn = """
        [Event "?"]
        [Site "?"]
        [Date "????.??.??"]
        [Round "?"]
        [White "?"]
        [Black "?"]
        [Result "*"]
        [SetUp "1"]
        [FEN "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"]

        1. e4 e5 *

        """;
    final ImmutableList<ForgivenTagItem> items = parseAndGetItems(pgn);

    assertHasItem(items, ForgivenTagItemCode.REDUNDANT_FEN_AND_SETUP_FOR_INITIAL_POSITION, StandardTag.FEN.getName(),
        "");
  }

  @Test
  void test07_canonicalInputEmitsNoTagForgivenItems() {
    final String pgn = """
        [Event "Spring Classic"]
        [Site "Somewhere"]
        [Date "2024.01.01"]
        [Round "1"]
        [White "Player1"]
        [Black "Player2"]
        [Result "*"]

        1. e4 e5 *

        """;
    final ImmutableList<ForgivenTagItem> items = parseAndGetItems(pgn);

    assertTrue(items.isEmpty());
  }

  // -------------------------------------------------------------------------------------------------
  // Helpers
  // -------------------------------------------------------------------------------------------------

  private static ImmutableList<ForgivenTagItem> parseAndGetItems(String pgn) {
    final LenientPgnParserValidationResult result = LenientPgnParser.validateText(pgn);
    assertTrue(result.isValid(), () -> "Expected lenient parse to succeed but got: " + result.message());
    return result.tagForgivenItems();
  }

  private static long countByCode(ImmutableList<ForgivenTagItem> items, ForgivenTagItemCode code) {
    return items.stream().filter(i -> i.code() == code).count();
  }

  private static void assertHasItem(ImmutableList<ForgivenTagItem> items, ForgivenTagItemCode code, String tagName,
      String detail) {
    final boolean present = items.stream()
        .anyMatch(i -> i.code() == code && i.tagName().equals(tagName) && i.detail().equals(detail));
    assertTrue(present,
        () -> "Expected item " + code + "[tagName=" + tagName + ", detail=" + detail + "] in: " + items);
  }
}
