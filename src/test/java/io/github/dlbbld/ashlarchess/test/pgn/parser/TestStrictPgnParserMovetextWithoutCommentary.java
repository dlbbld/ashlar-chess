// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.model.PgnMove;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParser;

class TestStrictPgnParserMovetextWithoutCommentary {

  @SuppressWarnings("static-method")
  @Test
  void testInitialWithoutCommentary() {
    checkInitialWithoutCommentary("1. e4", Nulls.asList("e4"));
    checkInitialWithoutCommentary("1. e4 e5", Nulls.asList("e4", "e5"));
    checkInitialWithoutCommentary("1. e4 e5 2. d4", Nulls.asList("e4", "e5", "d4"));
    checkInitialWithoutCommentary("1. e4 e5 2. d4 d5", Nulls.asList("e4", "e5", "d4", "d5"));
    checkInitialWithoutCommentary("1. e4 e5 2. d4 d5 3. Nc3", Nulls.asList("e4", "e5", "d4", "d5", "Nc3"));
    checkInitialWithoutCommentary("1. e4 e5 2. d4 d5 3. Nc3 Nc6", Nulls.asList("e4", "e5", "d4", "d5", "Nc3", "Nc6"));
    checkInitialWithoutCommentary("1. e4 e5 2. d4 d5 3. Nc3 Nc6 4. a4",
        Nulls.asList("e4", "e5", "d4", "d5", "Nc3", "Nc6", "a4"));
    checkInitialWithoutCommentary("1. e4 e5 2. d4 d5 3. Nc3 Nc6 4. a4 h5",
        Nulls.asList("e4", "e5", "d4", "d5", "Nc3", "Nc6", "a4", "h5"));
    checkInitialWithoutCommentary("1. e4 e5 2. d4 d5 3. Nc3 Nc6 4. a4 h5 5. Ra2",
        Nulls.asList("e4", "e5", "d4", "d5", "Nc3", "Nc6", "a4", "h5", "Ra2"));
    checkInitialWithoutCommentary("1. e4 e5 2. d4 d5 3. Nc3 Nc6 4. a4 h5 5. Ra2 Rh7",
        Nulls.asList("e4", "e5", "d4", "d5", "Nc3", "Nc6", "a4", "h5", "Ra2", "Rh7"));

    checkInitialWithoutCommentary(
        "1. e4 e5 2. d4 d5 3. Nc3 Nc6 4. a4 h5 5. Ra2 Rh7 6. a5 h4 7. Ra3 Rh6 8. a6 h3 9. Ra4 Rh5"
            + " 10. Ra5 Rh4 11. Ra1 Rh8 12. exd5 exd4",
        Nulls.asList("e4", "e5", "d4", "d5", "Nc3", "Nc6", "a4", "h5", "Ra2", "Rh7", "a5", "h4", "Ra3", "Rh6", "a6",
            "h3", "Ra4", "Rh5", "Ra5", "Rh4", "Ra1", "Rh8", "exd5", "exd4"));
  }

  /**
   * Verifies that the given movetext body, wrapped in a minimal seven-tag-roster PGN with a trailing termination
   * marker, produces the expected ordered move SAN list and leaves every move's commentary empty.
   */
  private static void checkInitialWithoutCommentary(String movetextPart, List<String> expectedSanList) {
    final PgnGame file = StrictPgnParser.parseText(header() + movetextPart + " *\n\n");
    assertEquals("", file.pregameCommentary().value());
    assertEquals(expectedSanList, calculateSanList(file.moveList()));
    for (final io.github.dlbbld.ashlarchess.model.PgnMove move : file.moveList()) {
      assertEquals("", move.commentary().value(), "Expected no commentary on " + move.san());
    }
  }

  /** Minimal strict-format seven-tag-roster header ending with the blank-line tag/movetext separator. */
  private static String header() {
    return """
        [Event "?"]
        [Site "?"]
        [Date "?"]
        [Round "?"]
        [White "?"]
        [Black "?"]
        [Result "*"]

        """;
  }

  private static List<String> calculateSanList(List<PgnMove> moveList) {
    final List<String> sanList = new ArrayList<>();
    for (final PgnMove move : moveList) {
      sanList.add(move.san());
    }
    return sanList;
  }

}
