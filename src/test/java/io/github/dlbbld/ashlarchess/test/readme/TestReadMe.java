package io.github.dlbbld.ashlarchess.test.readme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
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
import io.github.dlbbld.ashlarchess.unwinnability.DeadPositionFull;
import io.github.dlbbld.ashlarchess.unwinnability.DeadPositionQuick;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityFullVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityQuickVerdict;

class TestReadMe {

  @Test
  @SuppressWarnings("static-method")
  void unwinnabilityExamplesReturnExpectedResults() {
    assertUnwinnability("8/8/4k3/3R4/2K5/8/8/8 w - - 0 50", Side.BLACK, UnwinnabilityQuickVerdict.UNWINNABLE,
        UnwinnabilityFullVerdict.UNWINNABLE);
    assertUnwinnability("8/8/3k4/1p2p1p1/pP1pP1P1/P2P4/1K6/8 b - - 32 62", Side.BLACK,
        UnwinnabilityQuickVerdict.UNWINNABLE, UnwinnabilityFullVerdict.UNWINNABLE);
    assertUnwinnability("5r1k/6P1/7K/5q2/8/8/8/8 b - - 0 51", Side.WHITE, UnwinnabilityQuickVerdict.UNWINNABLE,
        UnwinnabilityFullVerdict.UNWINNABLE);
    assertUnwinnability("q4r2/pR3pkp/1p2p1p1/4P3/6P1/1P3Q2/1Pr2PK1/3R4 b - - 3 29", Side.WHITE,
        UnwinnabilityQuickVerdict.POSSIBLY_WINNABLE, UnwinnabilityFullVerdict.WINNABLE);
    assertUnwinnability("1k6/1P5p/BP3p2/1P6/8/8/5PKP/8 b - - 0 41", Side.WHITE, UnwinnabilityQuickVerdict.UNWINNABLE,
        UnwinnabilityFullVerdict.UNWINNABLE);
  }

  @Test
  @SuppressWarnings("static-method")
  void deadPositionExamplesReturnExpectedResults() {
    assertDeadPosition("8/8/3kn3/8/2K5/8/8/8 w - - 0 50", DeadPositionQuick.DEAD_POSITION,
        DeadPositionFull.DEAD_POSITION);
    assertDeadPosition("8/6b1/1p3k2/1Pp1p1p1/2P1PpP1/5P2/8/5K2 b - - 11 61", DeadPositionQuick.DEAD_POSITION,
        DeadPositionFull.DEAD_POSITION);
    assertDeadPosition("k7/P1K5/8/8/8/8/8/8 b - - 2 58", DeadPositionQuick.DEAD_POSITION,
        DeadPositionFull.DEAD_POSITION);
  }

  @Test
  @SuppressWarnings("static-method")
  void boardExampleEndsInCheckmate() {
    final Board board = new Board();

    board.moveStrict("e4");
    board.movesStrict("e5", "Bc4");

    final MoveSpecification newMove = new MoveSpecification(Square.F8, Square.C5);
    board.move(newMove);

    board.unmove();

    board.movesStrict("Bc5", "Qf3", "h6", "Qxf7#");

    assertTrue(board.isCheckmate());
  }

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

    assertFalse(lenientBoard.isCheckmate());
    assertFalse(strictBoard.isThreefoldRepetition());
    assertTrue(LenientPgnParser.validate(filePath).isValid());
    assertTrue(StrictPgnParser.validate(filePath).isValid());
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
    assertEquals(6, pgnGame.halfMoveList().size());
  }

  @Test
  @SuppressWarnings("static-method")
  void lenientParserCreatesExportFormat() {
    final String pgn = """
                [Black "Jane Doe"]
                [White "John Doe"]
                [ Event "Spring Classic"]

                1. e4 e5   2. Nf3
                Nf6
                3. Bc4 Bc5
        """;

    final PgnGame pgnGame = LenientPgnParser.parseText(pgn);
    // Archival mode produces a strict-spec-compliant export from the deficient lenient input: fills the missing
    // STR placeholders, synthesises a Result tag, emits the termination marker. The README example is exactly
    // the lenient-input + archival-output flow.
    final String exported = PgnCreate.createPgnString(pgnGame, WriteMode.ARCHIVAL);

    assertTrue(exported.contains("[Event \"Spring Classic\"]"));
    assertTrue(exported.contains("[White \"John Doe\"]"));
    assertTrue(exported.contains("[Black \"Jane Doe\"]"));
    assertTrue(exported.contains("1. e4 e5 2. Nf3 Nf6 3. Bc4 Bc5 *"));
    assertTrue(StrictPgnParser.validateText(exported).isValid());
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

    assertEquals(6, pgnGame.halfMoveList().size());
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
  void strictParserAcceptsPartialSevenTagRoster() {
    // PGN spec section 8.1.1 makes the Seven Tag Roster an archival-storage concern only. Strict parsing requires
    // the Result tag (so its value can match the termination marker) but accepts a PGN that omits other roster
    // entries. Archival output is opt-in via WriteMode.ARCHIVAL on PgnWriter.
    final String pgn = """
        [Event "Spring Classic"]
        [Result "*"]

        1. e4 e5 2. Nf3 Nf6 3. Bc4 Bc5 *

        """;

    assertTrue(StrictPgnParser.validateText(pgn).isValid());
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
  void pgnCreationProducesParserValidExport() {
    final PgnGame pgnGame = PgnCreate.createPgnGame(createOpeningExampleBoard());
    // Archival mode is the contract a Board-to-PGN consumer wants when round-tripping through strict parsing:
    // semantic mode would produce a tag-less PGN (createPgnGame(Board) carries no tags by design), which strict
    // parsing rejects for the missing Result tag.
    final String pgnString = PgnCreate.createPgnString(pgnGame, WriteMode.ARCHIVAL);

    assertTrue(LenientPgnParser.validateText(pgnString).isValid());
    assertTrue(StrictPgnParser.validateText(pgnString).isValid());
  }

  @Test
  @SuppressWarnings("static-method")
  void lenientValidationExamplesReturnExpectedResults() {
    final String validPgn = """
        [ Event "Spring Classic"]

        1. e4 e5   2. Nf3
        Nf6
          3. Bc4 Bc5
                """;
    final LenientPgnParserValidationResult validResult = LenientPgnParser.validateText(validPgn);
    assertTrue(validResult.isValid());

    final String invalidPgn = """
        [ Event "Spring Classic"]

        1. e4 e5   2. Nf3
        Nf6
          3. Bc4 Bc5 4. Y1
                """;
    final LenientPgnParserValidationResult invalidResult = LenientPgnParser.validateText(invalidPgn);
    assertFalse(invalidResult.isValid());
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
    assertTrue(result.isValid());
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
  void strictValidationExamplesReturnExpectedResults() {
    final String validPgn = """
        [Event "Spring Classic"]
        [Site "Somewhere"]
        [Date "2024.01.01"]
        [Round "1"]
        [White "Player1"]
        [Black "Player2"]
        [Result "*"]

        1. e4 e5 2. Nf3 Nf6 3. Bc4 Bc5 *

        """;
    final StrictPgnParserValidationResult validResult = StrictPgnParser.validateText(validPgn);
    assertTrue(validResult.isValid());

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
    assertFalse(invalidResult.isValid());
    assertEquals(StrictPgnParserValidationProblem.MOVETEXT_MOVE_NUMBER_DOES_NOT_CONTINUE_AS_EXPECTED,
        invalidResult.problemParser());
    assertEquals(SanValidationProblem.NONE, invalidResult.problemSan());
  }

  private static Board createOpeningExampleBoard() {
    final Board board = new Board();
    board.movesStrict("e4", "e5", "Nf3", "Nf6", "Bc4", "Bc5");
    return board;
  }

  private static void assertUnwinnability(String fen, Side side, UnwinnabilityQuickVerdict expectedQuick,
      UnwinnabilityFullVerdict expectedFull) {
    final Board board = new Board(fen);
    assertEquals(expectedQuick, board.isUnwinnableQuick(side));
    assertEquals(expectedFull, board.isUnwinnableFull(side));
  }

  private static void assertDeadPosition(String fen, DeadPositionQuick expectedQuick, DeadPositionFull expectedFull) {
    final Board board = new Board(fen);
    assertEquals(expectedQuick, board.isDeadPositionQuick());
    assertEquals(expectedFull, board.isDeadPositionFull());
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
