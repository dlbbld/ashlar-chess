package com.dlb.chess.test.pgn.export;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.pgn.PgnCreate;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.pgn.ResultTagValue;
import com.dlb.chess.pgn.StandardTag;
import com.dlb.chess.pgn.Tag;
import com.dlb.chess.pgn.TagUtility;
import com.dlb.chess.pgn.WriteMode;

/**
 * The {@link PgnCreate#createPgnGame(Board)} path produces a minimal honest model - no STR fabrication. Archival export
 * ({@link WriteMode#ARCHIVAL}) is the opt-in path that fills the Seven Tag Roster, synthesises a Result tag from the
 * termination marker, and emits PGN spec section 8.1.1-conformant output. This test exercises that archival path on
 * Board-derived inputs.
 */
class TestPgnExportBoardArchival {

  @SuppressWarnings("static-method")
  @Test
  void test() {
    check(ResultTagValue.ONGOING);
    check(ResultTagValue.ONGOING, "e4");
    check(ResultTagValue.WHITE_WON, "e4", "e5", "Bc4", "Nc6", "Qh5", "h6", "Qxf7#");
    check(ResultTagValue.BLACK_WON, "f3", "e5", "g4", "Qh4#");
  }

  private static void check(ResultTagValue expectedResult, String... sanArray) {

    final Board board = new Board();
    for (final String san : sanArray) {
      @SuppressWarnings("null") @NonNull final String sanIsNotNull = san;
      board.moveStrict(sanIsNotNull);
    }

    final PgnGame boardPgnGame = PgnCreate.createPgnGame(board);

    // Sanity precondition: the Board path itself does not fabricate STR. Archival output is the only place STR
    // gets filled.
    assertTrue(boardPgnGame.tagList().isEmpty());

    final String archivalOutput = PgnCreate.createPgnString(boardPgnGame, WriteMode.ARCHIVAL);

    assertContainsAllStrPlaceholders(archivalOutput);
    assertResultTagMatchesExpected(archivalOutput, expectedResult);
    assertNoSetUpOrFenTag(archivalOutput);
  }

  private static void assertContainsAllStrPlaceholders(String pgn) {
    // PGN spec section 8.1.1: archival storage requires the full Seven Tag Roster. Date uses "????.??.??" per
    // 8.1.1.3; the other STR entries use "?" placeholders.
    assertTrue(pgn.contains("[" + StandardTag.EVENT.getName() + " \"?\"]"));
    assertTrue(pgn.contains("[" + StandardTag.SITE.getName() + " \"?\"]"));
    assertTrue(pgn.contains("[" + StandardTag.DATE.getName() + " \"????.??.??\"]"));
    assertTrue(pgn.contains("[" + StandardTag.ROUND.getName() + " \"?\"]"));
    assertTrue(pgn.contains("[" + StandardTag.WHITE.getName() + " \"?\"]"));
    assertTrue(pgn.contains("[" + StandardTag.BLACK.getName() + " \"?\"]"));
  }

  private static void assertResultTagMatchesExpected(String pgn, ResultTagValue expectedResult) {
    assertTrue(pgn.contains("[" + StandardTag.RESULT.getName() + " \"" + expectedResult.getValue() + "\"]"));
  }

  private static void assertNoSetUpOrFenTag(String pgn) {
    // The boards in this test all start from the initial position. Archival mode drops the redundant SetUp/FEN
    // signals (per PGN spec section 8.1.1's archival convention; the initial position is the implicit default).
    assertFalse(pgn.contains("[" + StandardTag.SET_UP.getName() + " "));
    assertFalse(pgn.contains("[" + StandardTag.FEN.getName() + " "));
  }

  @SuppressWarnings("static-method")
  @Test
  void boardFromNonInitialPositionArchivalEmitsSetUpAndFen() {
    // Caller passes a tagList already containing FEN+SetUp for a custom starting position, then makes moves on
    // the board. Archival export must preserve the position-encoding tags.
    final String customFen = "r1b2r2/pp1pk1pp/8/7q/3pP1n1/5N1P/PPQ2PP1/3R1RK1 w - - 0 17";
    final Board board = new Board(customFen);
    board.moveStrict("Qa4");

    final List<Tag> tagList = new java.util.ArrayList<>();
    tagList.add(new Tag(StandardTag.SET_UP.getName(), "1"));
    tagList.add(new Tag(StandardTag.FEN.getName(), customFen));

    final PgnGame boardPgnGame = PgnCreate.createPgnGame(board, tagList);
    final String archivalOutput = PgnCreate.createPgnString(boardPgnGame, WriteMode.ARCHIVAL);

    assertTrue(archivalOutput.contains("[" + StandardTag.SET_UP.getName() + " \"1\"]"));
    assertTrue(archivalOutput.contains("[" + StandardTag.FEN.getName() + " \"" + customFen + "\"]"));
    assertContainsAllStrPlaceholders(archivalOutput);
    assertResultTagMatchesExpected(archivalOutput, ResultTagValue.ONGOING);
  }

  @SuppressWarnings("static-method")
  @Test
  void archivalTagOrderIsCanonical() {
    // Tags emitted in canonical sort order (STR first by sortOrder, then supplemental tags). Verify by checking
    // that Event appears before Result in the output.
    final Board board = new Board();
    final PgnGame pgnGame = PgnCreate.createPgnGame(board);

    final String archivalOutput = PgnCreate.createPgnString(pgnGame, WriteMode.ARCHIVAL);

    final int eventIdx = archivalOutput.indexOf("[" + StandardTag.EVENT.getName() + " ");
    final int resultIdx = archivalOutput.indexOf("[" + StandardTag.RESULT.getName() + " ");

    assertTrue(eventIdx >= 0);
    assertTrue(resultIdx > eventIdx);
  }

  @SuppressWarnings("static-method")
  @Test
  void semanticOutputForInitialBoardIsTagless() {
    // Counterpart to the archival path: semantic mode of an initial-position Board emits a tag-less PGN - just
    // the movetext and termination marker. Honest preservation of "no caller-provided tags."
    final Board board = new Board();
    final PgnGame pgnGame = PgnCreate.createPgnGame(board);

    final String semanticOutput = PgnCreate.createPgnString(pgnGame, WriteMode.SEMANTIC);

    // Tag section is empty - no '[' appears in the output, no STR placeholders.
    assertFalse(semanticOutput.contains("[" + StandardTag.EVENT.getName() + " "));
    assertFalse(semanticOutput.contains("[" + StandardTag.RESULT.getName() + " "));
    // Termination marker is still emitted (carried by the parse model's terminationMarker field).
    assertTrue(semanticOutput.contains("*"));

    // And the tagList check that no fabrication slipped into the model on this path.
    assertTrue(!TagUtility.calculateIsContainsAllSevenTagRosterTags(pgnGame.tagList()));
  }

  @SuppressWarnings("static-method")
  @Test
  void callerSuppliedResultTagDisagreeingWithBoardThrows() {
    // The PgnGame invariant: when a Result tag is present in tagList AND terminationMarker is non-null, the
    // two must agree. The Board-to-PgnGame path derives terminationMarker from the board's game status; if the
    // caller supplies a Result tag with a different value, the compact constructor of PgnGame catches the
    // inconsistency and throws - rather than silently producing a model that archival export would then have
    // to choose between when emitting the Result tag vs the termination marker.
    final Board boardWonByWhite = new Board();
    boardWonByWhite.moveStrict("e4");
    boardWonByWhite.moveStrict("e5");
    boardWonByWhite.moveStrict("Bc4");
    boardWonByWhite.moveStrict("Nc6");
    boardWonByWhite.moveStrict("Qh5");
    boardWonByWhite.moveStrict("h6");
    boardWonByWhite.moveStrict("Qxf7#");
    assertTrue(boardWonByWhite.isCheckmate());

    // Nulls.copyOfList wraps List.of with the @NonNull-annotated immutable list shape JDT requires for the
    // {@code createPgnGame(Board, List<Tag>)} parameter.
    @SuppressWarnings("null") final List<Tag> contradictoryTagList = Nulls
        .copyOfList(java.util.Collections.singletonList(new Tag(StandardTag.RESULT.getName(), "0-1")));
    assertThrows(IllegalArgumentException.class, () -> PgnCreate.createPgnGame(boardWonByWhite, contradictoryTagList));
  }
}
