package com.dlb.chess.test.common.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.pgn.StrictPgnParser;
import com.dlb.chess.pgn.StrictPgnParserValidationException;
import com.dlb.chess.pgn.StrictPgnParserValidationProblem;
import com.dlb.chess.pgn.Tag;

/**
 * Tag-parsing tests restored from the removed {@code TestParseTagUtility}. The removed tests drove the now-deleted
 * {@code StrictParseTagUtility.validateTag(String)} API directly; this version drives the sequential strict parser
 * end-to-end by embedding each subject tag line in a minimal PGN and inspecting the first tag of the result.
 *
 * <p>
 * Template construction puts the subject tag first so any format error fires before the seven-tag-roster check. Roster
 * tags that would duplicate the subject's tag name are omitted from the template.
 */
class TestParseTagUtility {

  @SuppressWarnings("static-method")
  @Test
  void testTagFormat() {
    checkTagFormat("[White \"Alpha\"]", true);
    checkTagFormat("[White \"Alpha Zero\"]", true);
    checkTagFormat("[CustomTagName \"Alpha Zero\"]", true);
    checkTagFormat("[White \"A player name which is a bit longer\"]", true);
    checkTagFormat("[White \" Alpha\"]", true);
    checkTagFormat("[White \"Alpha \"]", true);
    checkTagFormat("[White \" Alpha \"]", true);
    checkTagFormat("[White \"\"]", true);
    checkTagFormat("[WhiteTeam \"Víkingaklubburinn\"]", true);

    checkTagFormat("x[White \"Alpha Zero\"]", false);
    checkTagFormat("White \"Alpha Zero\"]", false);
    checkTagFormat("[ \"Alpha Zero\"]", false);
    checkTagFormat("[White White  \"Alpha Zero\"]", false);
    checkTagFormat("[White  \"Alpha Zero\"]", false);
    checkTagFormat("[White\"Alpha Zero\"]", false);
    checkTagFormat("[White \"\"Alpha Zero\"]", false);
    checkTagFormat("[White Alpha Zero\"]", false);
    checkTagFormat("[White \"Alpha Zero\"\"]", false);
    checkTagFormat("[White \"Alpha Zero]", false);
    checkTagFormat("[White \"Alpha Zero\"]]", false);
    checkTagFormat("[White \"Alpha Zero\"", false);
    checkTagFormat("[White \"Alpha Zero\"]x", false);

    checkTagFormat("[Event \"Live Chess\"]", true);
  }

  @SuppressWarnings("static-method")
  @Test
  void testReadValidTag() {
    checkTagValues("[Event \"Groningen\"]", "Event", "Groningen");
    checkTagValues("[Site \"Groningen NED\"]", "Site", "Groningen NED");
    checkTagValues("[Date \"1997.??.??\"]", "Date", "1997.??.??");
    checkTagValues("[Round \"9\"]", "Round", "9");
    checkTagValues("[White \"Pavel Blatny\"]", "White", "Pavel Blatny");
    checkTagValues("[Black \"Frank Holzke\"]", "Black", "Frank Holzke");
    checkTagValues("[ECO \"A15\"]", "ECO", "A15");
    checkTagValues("[PlyCount \"127\"]", "PlyCount", "127");
    checkTagValues("[EventDate \"1997.??.??\"]", "EventDate", "1997.??.??");

    checkTagValues("[Event \"Live Chess\"]", "Event", "Live Chess");

    // Arbitrary custom tag values without any validation of the value contents - the parser only enforces the
    // surrounding format, not the value semantics.
    checkTagValues("[Custom \"0\"]", "Custom", "0");
    checkTagValues("[Custom \"1\"]", "Custom", "1");
    checkTagValues("[Custom \"abc\"]", "Custom", "abc");

    // FEN and SetUp are tested together via the fromCustomPosition corpus - they cannot be exercised in isolation
    // through this minimal-PGN helper because the strict parser requires SetUp=1 to accept a FEN tag, and removes
    // the FEN tag from the output when the FEN equals the initial position. See TestStrictPgnParserFromCustomPosition
    // for end-to-end coverage.

    // Whitespace inside the quoted tag value is preserved verbatim.
    checkTagValues("[tagName \"abc  abc\"]", "tagName", "abc  abc");
    checkTagValues("[tagName \"abc abc \"]", "tagName", "abc abc ");
    checkTagValues("[tagName \"abc  abc  \"]", "tagName", "abc  abc  ");
    checkTagValues("[tagName \" abc  abc  \"]", "tagName", " abc  abc  ");
    checkTagValues("[tagName \"  abc  abc  \"]", "tagName", "  abc  abc  ");
  }

  @SuppressWarnings("static-method")
  @Test
  void testTagNameLength() {
    // 254 and 255 characters - accepted.
    checkTagFormat("[" + repeatedName(254) + " \"tagNameValue\"]", true);
    checkTagFormat("[" + repeatedName(255) + " \"tagNameValue\"]", true);

    // 256, 257 and 357 characters - rejected with the dedicated length error.
    checkTagFormat("[" + repeatedName(256) + " \"tagNameValue\"]", false);
    checkTagNameLength("[" + repeatedName(256) + " \"tagNameValue\"]");

    checkTagFormat("[" + repeatedName(257) + " \"tagNameValue\"]", false);
    checkTagNameLength("[" + repeatedName(257) + " \"tagNameValue\"]");

    checkTagFormat("[" + repeatedName(357) + " \"tagNameValue\"]", false);
    checkTagNameLength("[" + repeatedName(357) + " \"tagNameValue\"]");
  }

  @SuppressWarnings("static-method")
  @Test
  void testTagNameFirstCharacter() {
    checkException("[_vent \"Live Chess\"]", StrictPgnParserValidationProblem.TAG_NAME_FIRST_CHARACTER);
    checkException("[+vent \"Live Chess\"]", StrictPgnParserValidationProblem.TAG_NAME_FIRST_CHARACTER);
    checkException("[#vent \"Live Chess\"]", StrictPgnParserValidationProblem.TAG_NAME_FIRST_CHARACTER);
    checkException("[=vent \"Live Chess\"]", StrictPgnParserValidationProblem.TAG_NAME_FIRST_CHARACTER);
    checkException("[:vent \"Live Chess\"]", StrictPgnParserValidationProblem.TAG_NAME_FIRST_CHARACTER);
    checkException("[-vent \"Live Chess\"]", StrictPgnParserValidationProblem.TAG_NAME_FIRST_CHARACTER);
  }

  @SuppressWarnings("static-method")
  @Test
  void testTagNameSecondCharacter() {
    checkTagFormat("[E_vent \"Live Chess\"]", true);
    checkTagFormat("[E+vent \"Live Chess\"]", true);
    checkTagFormat("[E#vent \"Live Chess\"]", true);
    checkTagFormat("[E=vent \"Live Chess\"]", true);
    checkTagFormat("[E:vent \"Live Chess\"]", true);
    checkTagFormat("[E-vent \"Live Chess\"]", true);
  }

  @SuppressWarnings("static-method")
  @Test
  void testSquareBrackets() {
    checkException("Event \"Live Chess\"]",
        StrictPgnParserValidationProblem.TAG_FORMAT_NOT_STARTING_WITH_LEFT_SQUARE_BRACKET);
    checkException("]Event \"Live Chess\"]",
        StrictPgnParserValidationProblem.TAG_FORMAT_NOT_STARTING_WITH_LEFT_SQUARE_BRACKET);
    checkException("[Event \"Live Chess\"",
        StrictPgnParserValidationProblem.TAG_FORMAT_NOT_ENDING_WITH_RIGHT_SQUARE_BRACKET);
    checkException("[Event \"Live Chess\"[",
        StrictPgnParserValidationProblem.TAG_FORMAT_NOT_ENDING_WITH_RIGHT_SQUARE_BRACKET);
  }

  // -------------------------------------------------------------------------------------------------
  // Helpers - embed the subject tag in a minimal PGN and drive StrictPgnParser end-to-end
  // -------------------------------------------------------------------------------------------------

  private static void checkTagFormat(String tagLine, boolean isValid) {
    final var isException = tryParseReturnIsException(tagLine);
    if (isValid) {
      assertFalse(isException, "Expected tag to parse cleanly: " + tagLine);
    } else {
      assertTrue(isException, "Expected tag to be rejected: " + tagLine);
    }
  }

  private static void checkTagValues(String tagLine, String expectedTagName, String expectedTagValue) {
    final PgnGame file = StrictPgnParser.parseText(buildMinimalPgn(tagLine));
    final Tag subject = findTagByName(file, expectedTagName);
    assertEquals(expectedTagName, subject.name());
    assertEquals(expectedTagValue, subject.value());
  }

  private static void checkTagNameLength(String tagLine) {
    checkException(tagLine, StrictPgnParserValidationProblem.TAG_NAME_EXCEEDS_MAXIMUM_LENGTH);
  }

  private static void checkException(String tagLine, StrictPgnParserValidationProblem expected) {
    var isException = false;
    try {
      StrictPgnParser.parseText(buildMinimalPgn(tagLine));
    } catch (final StrictPgnParserValidationException e) {
      isException = true;
      assertEquals(expected, e.getStrictPgnParserValidationProblem(),
          "Wrong problem category; message was: " + e.getMessage());
    }
    assertTrue(isException, "Expected " + expected + " but parser accepted: " + tagLine);
  }

  private static boolean tryParseReturnIsException(String tagLine) {
    try {
      StrictPgnParser.parseText(buildMinimalPgn(tagLine));
      return false;
    } catch (@SuppressWarnings("unused") final StrictPgnParserValidationException e) {
      return true;
    }
  }

  /**
   * Wraps the subject tag line as the first tag in a seven-tag-roster header, followed by a zero-move game. Any roster
   * tag whose name matches the subject is omitted so the tag list remains unique.
   */
  private static String buildMinimalPgn(String subjectTagLine) {
    final String subjectName = extractTagName(subjectTagLine);
    final StringBuilder sb = new StringBuilder();
    sb.append(subjectTagLine).append('\n');
    for (final String roster : Arrays.asList("Event", "Site", "Date", "Round", "White", "Black", "Result")) {
      if (!roster.equals(subjectName)) {
        final var placeholder = "Result".equals(roster) ? "*" : "?";
        sb.append('[').append(roster).append(" \"").append(placeholder).append("\"]\n");
      }
    }
    sb.append('\n').append("*").append('\n').append('\n');
    return Nulls.toString(sb);
  }

  /**
   * Naive tag-name extraction - returns whatever sits between the opening {@code [} and the first whitespace, or the
   * empty string if the line doesn't look like a tag at all. For malformed inputs we don't care what this returns
   * because the parser errors before template wholeness matters.
   */
  private static String extractTagName(String tagLine) {
    final var open = tagLine.indexOf('[');
    if (open == -1) {
      return "";
    }
    final var afterOpen = open + 1;
    final var space = tagLine.indexOf(' ', afterOpen);
    if (space == -1) {
      return "";
    }
    return Nulls.substring(tagLine, afterOpen, space);
  }

  private static Tag findTagByName(PgnGame file, String name) {
    for (final Tag tag : file.tagList()) {
      if (tag.name().equals(name)) {
        return tag;
      }
    }
    throw new AssertionError("Tag not found in parsed file: " + name);
  }

  private static String repeatedName(int length) {
    final StringBuilder sb = new StringBuilder(length);
    sb.append("tagName");
    while (sb.length() < length) {
      sb.append((char) ('0' + sb.length() % 10));
    }
    return Nulls.substring(sb, 0, length);
  }
}
