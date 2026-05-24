package com.dlb.chess.test.san;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.enums.GameStatus;
import com.dlb.chess.common.utility.BasicChessUtility;
import com.dlb.chess.san.SanValidationException;
import com.dlb.chess.san.SanValidationProblem;
import com.dlb.chess.san.StrictSanParser;
import com.dlb.chess.unwinnability.DeadPositionQuick;

/**
 * Surface-level tests for the strict-pipeline game-end pre-check in {@link StrictSanParser#parseText}: one scenario per
 * enforced move-blocking termination ({@link GameStatus#CHECKMATE}, {@link GameStatus#STALEMATE},
 * {@link GameStatus#DEAD_POSITION_INSUFFICIENT_MATERIAL}). Each verifies that any SAN attempted on a terminal-state
 * board is rejected with {@link SanValidationProblem#GAME_ALREADY_ENDED} and that the thrown
 * {@link SanValidationException} carries the originating {@link GameStatus} as payload.
 *
 * <p>
 * Fivefold, 75-move, and analyzer-driven dead positions are <em>not</em> enforced terminations in this library (see
 * {@link GameStatus#isAutomaticTermination()}); the SAN parser accepts further moves at and past those thresholds.
 *
 * <p>
 * The companion {@code TestValidateNewMoveGameEnded} mirrors this set against the MoveSpecification pipeline.
 */
class TestSanValidationGameEnded {

  @SuppressWarnings("static-method")
  @Test
  void testGameEndedByCheckmate() {
    // Fool's mate.
    final Board board = new Board("rnb1kbnr/pppp1ppp/8/4p3/6Pq/5P2/PPPPP2P/RNBQKBNR w KQkq - 1 3");
    check("Ke2", board, GameStatus.CHECKMATE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testGameEndedByStalemate() {
    final Board board = new Board("7k/8/6Q1/8/8/8/8/K7 b - - 0 1");
    check("Kg8", board, GameStatus.STALEMATE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testGameEndedByInsufficientMaterialBoth() {
    final Board board = new Board("4k3/8/8/8/8/8/8/4K3 w - - 0 1");
    check("Ke2", board, GameStatus.DEAD_POSITION_INSUFFICIENT_MATERIAL);
  }

  @SuppressWarnings("static-method")
  @Test
  void testSanAcceptedAtDeadPositionUnwinnableQuickBornDead() {
    // Pawn-wall fortress (horizontal_1 from the CHA pawn-wall corpus).
    final Board board = new Board("4k3/8/8/p1p1p1p1/P1P1P1P1/8/8/4K3 w - - 0 50");
    assertEquals(DeadPositionQuick.DEAD_POSITION, board.isDeadPositionQuick());
    assertDoesNotThrow(() -> StrictSanParser.parseText("Kd1", board),
        "quick-unwinnable dead position is queryable only; the SAN parser must accept the move");
  }

  @SuppressWarnings("static-method")
  @Test
  void testSanAcceptedAtDeadPositionUnwinnableQuickPlayedInto() {
    // Predecessor: pawn wall with white h-pawn still on h2. h3 completes the lock.
    final Board board = new Board("4k3/8/8/p1p1p1p1/PpPpPpPp/1P1P1P2/7P/4K3 w - - 0 49");
    board.moveStrict("h3");
    assertEquals(DeadPositionQuick.DEAD_POSITION, board.isDeadPositionQuick());
    assertDoesNotThrow(() -> StrictSanParser.parseText("Kd8", board),
        "quick-unwinnable dead position is queryable only; the SAN parser must accept the move");
  }

  // --- queryable-only predicates: SAN parser does NOT block past these ---

  @SuppressWarnings("static-method")
  @Test
  void testSanAcceptedAtSeventyFiveMoveThreshold() {
    final Board board = new Board("4k3/8/4P3/8/8/8/2N1B3/3KQ2R w - - 150 76");
    assertTrue(board.isSeventyFiveMove(), "predicate must fire at threshold");
    assertDoesNotThrow(() -> StrictSanParser.parseText("Kd2", board),
        "75-move is queryable only; the SAN parser must accept the move");
  }

  @SuppressWarnings("static-method")
  @Test
  void testSanAcceptedAtFivefoldThreshold() {
    final Board board = new Board();
    board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6",
        "Ng1", "Ng8");
    assertTrue(board.isFivefoldRepetition(), "predicate must fire at fivefold");
    assertDoesNotThrow(() -> StrictSanParser.parseText("e4", board),
        "fivefold is queryable only; the SAN parser must accept the move");
  }

  // --- helpers ---

  private static void check(String san, Board board, GameStatus expectedGameStatus) {
    var isException = false;
    try {
      StrictSanParser.parseText(san, board);
    } catch (final SanValidationException e) {
      isException = true;
      assertEquals(SanValidationProblem.GAME_ALREADY_ENDED, e.getSanValidationProblem());
      assertNotNull(e.getGameStatus(), "GAME_ALREADY_ENDED must carry a GameStatus payload");
      assertEquals(expectedGameStatus, e.getGameStatus());
    }
    assertTrue(isException, "Expected SanValidationException with GAME_ALREADY_ENDED");
  }
}
