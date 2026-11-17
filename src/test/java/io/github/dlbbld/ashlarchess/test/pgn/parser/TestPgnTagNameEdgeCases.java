// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.dlbbld.ashlarchess.internal.ExceptionUtility;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParser;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParserValidationException;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParserValidationProblem;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParser;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParserValidationException;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParserValidationProblem;

class TestPgnTagNameEdgeCases {

  private static final String SEVEN_TAG_ROSTER = """
      [Event "?"]
      [Site "?"]
      [Date "????.??.??"]
      [Round "?"]
      [White "?"]
      [Black "?"]
      [Result "*"]
      """;

  private static final String MOVETEXT = "\n1. e4 *\n";

  // The strict file structure closes with an empty line.
  private static String strictText(String extraTagLines) {
    return SEVEN_TAG_ROSTER + extraTagLines + MOVETEXT + "\n";
  }

  private static String lenientText(String extraTagLines) {
    return SEVEN_TAG_ROSTER + extraTagLines + MOVETEXT;
  }

  @SuppressWarnings("static-method")
  @ParameterizedTest
  @ValueSource(strings = { "1Tag", "123Annotator", "1_Tag2", "123" })
  void digitStartedTagNamesArePreserved(String name) {
    final String tagLine = "[" + name + " \"value\"]\n";
    final PgnGame lenient = LenientPgnParser.parseText(lenientText(tagLine));
    final PgnGame strict = StrictPgnParser.parseText(strictText(tagLine));
    assertEquals(name, Nulls.getLast(lenient.tags()).name());
    assertEquals("value", Nulls.getLast(lenient.tags()).value());
    assertEquals(lenient, strict);
  }

  @SuppressWarnings("static-method")
  @ParameterizedTest
  @ValueSource(strings = { "_", "+", "#", "=", ":", "-" })
  void tagNameMustStartWithLetterOrDigit(String continuationCharacter) {
    // Regression: the lenient parser accepted a name opening with a symbol continuation character (PGN spec §7), and
    // the archival export of that game was rejected by the strict parser.
    final String tagLine = "[" + continuationCharacter + "Annotator \"?\"]\n";

    final LenientPgnParserValidationException lenient = assertThrows(LenientPgnParserValidationException.class,
        () -> LenientPgnParser.parseText(lenientText(tagLine)));
    assertEquals(LenientPgnParserValidationProblem.TAG_FORMAT_INVALID, lenient.getLenientPgnParserValidationProblem());

    final StrictPgnParserValidationException strict = assertThrows(StrictPgnParserValidationException.class,
        () -> StrictPgnParser.parseText(strictText(tagLine)));
    assertEquals(StrictPgnParserValidationProblem.TAG_NAME_FIRST_CHARACTER,
        strict.getStrictPgnParserValidationProblem());
  }

  @SuppressWarnings("static-method")
  @Test
  void continuationCharactersStayValidAfterTheFirstCharacter() {
    final String tagLine = "[A_+#=:-z \"?\"]\n";
    assertEquals(8, LenientPgnParser.parseText(lenientText(tagLine)).tags().size());
    assertEquals(8, StrictPgnParser.parseText(strictText(tagLine)).tags().size());
  }

  @SuppressWarnings("static-method")
  @Test
  void tagNameLengthLimitIsTheSameInBothParsers() {
    // Regression: the lenient parser had no limit, and the archival export of a game with a longer name was rejected
    // by the strict parser. PGN spec §7 limits a symbol to 255 characters.
    final String longestTagLine = "[" + "A".repeat(255) + " \"?\"]\n";
    assertEquals(8, LenientPgnParser.parseText(lenientText(longestTagLine)).tags().size());
    assertEquals(8, StrictPgnParser.parseText(strictText(longestTagLine)).tags().size());

    final String tooLongTagLine = "[" + "A".repeat(256) + " \"?\"]\n";
    final LenientPgnParserValidationException lenient = assertThrows(LenientPgnParserValidationException.class,
        () -> LenientPgnParser.parseText(lenientText(tooLongTagLine)));
    assertEquals(LenientPgnParserValidationProblem.TAG_FORMAT_INVALID, lenient.getLenientPgnParserValidationProblem());

    final StrictPgnParserValidationException strict = assertThrows(StrictPgnParserValidationException.class,
        () -> StrictPgnParser.parseText(strictText(tooLongTagLine)));
    assertEquals(StrictPgnParserValidationProblem.TAG_NAME_EXCEEDS_MAXIMUM_LENGTH,
        strict.getStrictPgnParserValidationProblem());
  }

  @SuppressWarnings("static-method")
  @Test
  void duplicateTagNameReportedIsTheFirstTagWithADuplicate() {
    final String tagLines = "[Annotator \"a\"]\n[Opening \"b\"]\n[Opening \"c\"]\n[Annotator \"d\"]\n";

    final LenientPgnParserValidationException lenient = assertThrows(LenientPgnParserValidationException.class,
        () -> LenientPgnParser.parseText(lenientText(tagLines)));
    assertEquals(LenientPgnParserValidationProblem.TAG_NAME_NOT_UNIQUE, lenient.getLenientPgnParserValidationProblem());
    assertTrue(ExceptionUtility.getMessage(lenient).contains("\"Annotator\""));

    final StrictPgnParserValidationException strict = assertThrows(StrictPgnParserValidationException.class,
        () -> StrictPgnParser.parseText(strictText(tagLines)));
    assertEquals(StrictPgnParserValidationProblem.TAG_NAME_NOT_UNIQUE, strict.getStrictPgnParserValidationProblem());
    assertTrue(ExceptionUtility.getMessage(strict).contains("\"Annotator\""));
  }

  @SuppressWarnings("static-method")
  @Test
  void manyTagsParseInLinearTime() {
    // Regression: tag names were compared pair by pair, so this input took minutes. The generous limit only has to
    // tell linear from quadratic.
    final int extraTagCount = 200_000;
    final StringBuilder tagLines = new StringBuilder();
    for (int i = 0; i < extraTagCount; i++) {
      tagLines.append("[X").append(i).append(" \"v\"]\n");
    }
    final String extraTagLines = Nulls.toString(tagLines);

    final PgnGame lenient = assertTimeoutPreemptively(Duration.ofSeconds(30),
        () -> LenientPgnParser.parseText(lenientText(extraTagLines)));
    assertEquals(7 + extraTagCount, lenient.tags().size());

    final PgnGame strict = assertTimeoutPreemptively(Duration.ofSeconds(30),
        () -> StrictPgnParser.parseText(strictText(extraTagLines)));
    assertEquals(7 + extraTagCount, strict.tags().size());
  }
}
