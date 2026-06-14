// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.readme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParser;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParserValidationException;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParserValidationProblem;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParserValidationResult;
import io.github.dlbbld.ashlarchess.pgn.PgnCreate;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.PgnUtility;
import io.github.dlbbld.ashlarchess.pgn.PgnWriter;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParser;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParserValidationException;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParserValidationProblem;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParserValidationResult;
import io.github.dlbbld.ashlarchess.pgn.Tag;
import io.github.dlbbld.ashlarchess.pgn.WriteMode;
import io.github.dlbbld.ashlarchess.san.SanValidationProblem;

class TestReadMe {

  @Test
  @SuppressWarnings("static-method")
  void pgnCanBeWrittenAndParsed(@TempDir Path tempDir) {
    final Board sourceBoard = createOpeningExampleBoard();
    final PgnGame pgnGame = PgnCreate.createPgnGame(sourceBoard);
    final Path filePath = Nulls.pathResolve(tempDir, "myFile.pgn");

    // Archival mode for the write: createPgnGame(Board) carries no tags by design (no caller-provided input to
    // preserve), so a SEMANTIC write would produce a tag-less PGN that strict parsing rejects. Round-tripping
    // through strict parsing is the demonstration this test exists for, so the producer side asks for archival.
    PgnWriter.writePgn(pgnGame, filePath, WriteMode.ARCHIVAL);

    final Board lenientBoard = PgnUtility.calculateBoard(LenientPgnParser.parse(filePath));
    final Board strictBoard = PgnUtility.calculateBoard(StrictPgnParser.parse(filePath));

    assertEquals(sourceBoard.getFen(), lenientBoard.getFen());
    assertEquals(sourceBoard.getFen(), strictBoard.getFen());
  }

  @Test
  @SuppressWarnings("static-method")
  void lenientParserAcceptsLooseReadmeFormat() {
    final String pgn = """
        [ Event "Spring Classic"]

        1. e4 e5   2. Nf3
        Nf6
          3. Bc4 Bc5
                """;

    final PgnGame pgnGame = LenientPgnParser.parseText(pgn);
    final Board board = PgnUtility.calculateBoard(pgnGame);
    board.moveStrict("a3");

    assertEquals("Spring Classic", tagValue(pgnGame, "Event"));
    assertEquals(6, pgnGame.moveList().size());
  }

  @Test
  @SuppressWarnings("static-method")
  void lenientParserReportsInvalidSan() {
    final String pgn = """
        [ Event "Spring Classic"]

        1. e4 e5   2. Nf4
        Nf6
          3. Bc4 Bc5
                """;

    try {
      LenientPgnParser.parseText(pgn);
      fail("Expected invalid SAN to fail lenient PGN parsing");
    } catch (final LenientPgnParserValidationException e) {
      assertEquals(LenientPgnParserValidationProblem.SAN, e.getLenientPgnParserValidationProblem());
      assertEquals(SanValidationProblem.NOT_REACHABLE_RNBQ_NEITHER_MULTIPLE, e.getSanValidationProblem());
    }
  }

  @Test
  @SuppressWarnings("static-method")
  void strictParserAcceptsStrictReadmeFormat() {
    final String pgn = """
        [Event "Spring Classic"]
        [Site "Somewhere"]
        [Date "2024.01.01"]
        [Round "1"]
        [White "Player1"]
        [Black "Player2"]
        [Result "*"]

        1. e4 e5 2. Nf3 Nf6 3. Bc4 Bc5 *

        """;

    final PgnGame pgnGame = StrictPgnParser.parseText(pgn);
    final Board board = PgnUtility.calculateBoard(pgnGame);
    board.moveStrict("a3");

    assertEquals(6, pgnGame.moveList().size());
  }

  @Test
  @SuppressWarnings("static-method")
  void strictParserRejectsLenientTagSyntax() {
    final String pgn = """
        [ Event "Spring Classic"]

        1. e4 e5 2. Nf3 Nf6 3. Bc4 Bc5

        """;

    try {
      StrictPgnParser.parseText(pgn);
      fail("Expected lenient tag syntax to fail strict PGN parsing");
    } catch (final StrictPgnParserValidationException e) {
      assertEquals(StrictPgnParserValidationProblem.TAG_FORMAT_LEFT_SQUARE_BRACKET_FOLLOWED_BY_SPACE,
          e.getStrictPgnParserValidationProblem());
    }
  }

  @Test
  @SuppressWarnings("static-method")
  void strictParserRejectsMissingResultTag() {
    final String pgn = """
        [Event "Spring Classic"]

        1. e4 e5 2. Nf3 Nf6 3. Bc4 Bc5 *

        """;

    try {
      StrictPgnParser.parseText(pgn);
      fail("Expected missing Result tag to fail strict PGN parsing");
    } catch (final StrictPgnParserValidationException e) {
      assertEquals(StrictPgnParserValidationProblem.TAG_RESULT_MISSING, e.getStrictPgnParserValidationProblem());
    }
  }

  @Test
  @SuppressWarnings("static-method")
  void lenientValidationInvalidExampleReturnsProblemEnums() {
    final String invalidPgn = """
        [ Event "Spring Classic"]

        1. e4 e5   2. Nf3
        Nf6
          3. Bc4 Bc5 4. Y1
                """;
    final LenientPgnParserValidationResult invalidResult = LenientPgnParser.validateText(invalidPgn);
    assertEquals(LenientPgnParserValidationProblem.EXCEPTION_CAUGHT_FROM_STRICT_VALIDATION,
        invalidResult.problemParser());
    assertEquals(SanValidationProblem.NONE, invalidResult.problemSan());
  }

  @Test
  @SuppressWarnings("static-method")
  void lenientPgnSanTolerancesExampleReturnsExpectedForgivenItems() {
    final String pgn = """
        [Event "?"]
        [Site "?"]
        [Date "?"]
        [Round "?"]
        [White "?"]
        [Black "?"]
        [Result "*"]

        1. e4 e5 2. Nf3 Nc6 3. Bc4 Bc5 4. 0-0 nf6 *
                """;
    final LenientPgnParserValidationResult result = LenientPgnParser.validateText(pgn);
    assertEquals(2, result.sanForgivenItems().size());
    assertEquals(io.github.dlbbld.ashlarchess.san.LenientSanValidationProblem.ZERO_INSTEAD_OF_O_CASTLING,
        Nulls.get(result.sanForgivenItems(), 0).code());
    assertEquals("0-0", Nulls.get(result.sanForgivenItems(), 0).originalToken());
    assertEquals("O-O", Nulls.get(result.sanForgivenItems(), 0).canonicalSan());
    assertEquals(io.github.dlbbld.ashlarchess.san.LenientSanValidationProblem.LOWERCASE_PIECE_LETTER,
        Nulls.get(result.sanForgivenItems(), 1).code());
    assertEquals("nf6", Nulls.get(result.sanForgivenItems(), 1).originalToken());
    assertEquals("Nf6", Nulls.get(result.sanForgivenItems(), 1).canonicalSan());
  }

  @Test
  @SuppressWarnings("static-method")
  void strictValidationInvalidExampleReturnsProblemEnums() {
    final String invalidPgn = """
        [Event "Spring Classic"]
        [Site "Somewhere"]
        [Date "2024.01.01"]
        [Round "1"]
        [White "Player1"]
        [Black "Player2"]
        [Result "*"]

        1. e4 e5 2. Nf3 Nf6 2. Bc4 Bc5 *

        """;
    final StrictPgnParserValidationResult invalidResult = StrictPgnParser.validateText(invalidPgn);
    assertEquals(StrictPgnParserValidationProblem.MOVETEXT_MOVE_NUMBER_DOES_NOT_CONTINUE_AS_EXPECTED,
        invalidResult.problemParser());
    assertEquals(SanValidationProblem.NONE, invalidResult.problemSan());
  }

  private static Board createOpeningExampleBoard() {
    final Board board = new Board();
    board.movesStrict("e4", "e5", "Nf3", "Nf6", "Bc4", "Bc5");
    return board;
  }

  private static String tagValue(PgnGame pgnGame, String name) {
    for (final Tag tag : pgnGame.tagList()) {
      if (tag.name().equals(name)) {
        return tag.value();
      }
    }
    fail("Missing PGN tag: " + name);
    return "";
  }
}
