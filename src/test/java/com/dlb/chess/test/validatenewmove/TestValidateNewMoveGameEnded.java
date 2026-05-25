package com.dlb.chess.test.validatenewmove;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.common.enums.GameStatus;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.enums.MoveCheck;
import com.dlb.chess.exceptions.InvalidMoveException;
import com.dlb.chess.unwinnability.DeadPositionQuick;

/**
 * Surface-level tests for the strict-pipeline game-end pre-check in
 * {@link com.dlb.chess.board.ValidateNewMove#validateNewMove}: one scenario per enforced move-blocking termination
 * ({@link GameStatus#CHECKMATE}, {@link GameStatus#STALEMATE}, {@link GameStatus#DEAD_POSITION_INSUFFICIENT_MATERIAL}).
 * Each verifies that any move attempted on a terminal-state board is rejected with {@link MoveCheck#GAME_ALREADY_ENDED}
 * and that the thrown {@link InvalidMoveException} carries the originating {@link GameStatus} as payload.
 *
 * <p>
 * Fivefold, 75-move, and analyzer-driven dead positions are <em>not</em> enforced terminations in this library (see
 * {@link GameStatus#isAutomaticTermination()}); the move pipeline accepts further moves at and past those thresholds.
 *
 * <p>
 * The companion {@code TestSanValidationGameEnded} mirrors this set against the SAN pipeline.
 */
class TestValidateNewMoveGameEnded implements EnumConstants {

  // --- CHECKMATE ---

  @SuppressWarnings("static-method")
  @Test
  void testGameEndedByCheckmate() {
    // Fool's mate: white is checkmated by black queen on h4.
    final Board board = new Board("rnb1kbnr/pppp1ppp/8/4p3/6Pq/5P2/PPPPP2P/RNBQKBNR w KQkq - 1 3");
    check(board, new MoveSpecification(E1, E2), GameStatus.CHECKMATE);
  }

  // --- STALEMATE ---

  @SuppressWarnings("static-method")
  @Test
  void testGameEndedByStalemate() {
    // Classic K + Q stalemate: black king h8 has no legal move, no check, white to move.
    // White-to-move queries; it's actually black-to-move stalemate so we set black-to-move
    // and ask validation to reject any black move attempt.
    final Board board = new Board("7k/8/6Q1/8/8/8/8/K7 b - - 0 1");
    check(board, new MoveSpecification(H8, G8), GameStatus.STALEMATE);
  }

  // --- DEAD_POSITION_INSUFFICIENT_MATERIAL ---

  @SuppressWarnings("static-method")
  @Test
  void testGameEndedByInsufficientMaterialBoth() {
    // K vs K: dead position under FIDE 5.2.2.
    final Board board = new Board("4k3/8/8/8/8/8/8/4K3 w - - 0 1");
    check(board, new MoveSpecification(E1, E2), GameStatus.DEAD_POSITION_INSUFFICIENT_MATERIAL);
  }

  // --- queryable-only predicates: pipeline does NOT block past these ---

  @SuppressWarnings("static-method")
  @Test
  void testMoveAcceptedAtDeadPositionUnwinnableQuickBornDead() {
    // Pawn-wall fortress (horizontal_1 from the CHA pawn-wall corpus). Both sides have only kings
    // and locked pawns; the cheap insufficient-material detector stays quiet because pawns are
    // present, but the CHA quick analyzer classifies the position as dead.
    final Board board = new Board("4k3/8/8/p1p1p1p1/P1P1P1P1/8/8/4K3 w - - 0 50");
    assertEquals(DeadPositionQuick.DEAD_POSITION, board.isDeadPositionQuick());
    assertDoesNotThrow(() -> board.move(new MoveSpecification(E1, D1)),
        "quick-unwinnable dead position is queryable only; the pipeline must accept the move");
  }

  @SuppressWarnings("static-method")
  @Test
  void testMoveAcceptedAtDeadPositionUnwinnableQuickPlayedInto() {
    // Predecessor: same wall structure as the no-en-passant pawn_wall fixture but with the white
    // h-pawn still on h2 (one rank back). White's h3 push completes the lock.
    final Board board = new Board("4k3/8/8/p1p1p1p1/PpPpPpPp/1P1P1P2/7P/4K3 w - - 0 49");
    board.moveStrict("h3");
    assertEquals(DeadPositionQuick.DEAD_POSITION, board.isDeadPositionQuick());
    assertDoesNotThrow(() -> board.move(new MoveSpecification(E8, D8)),
        "quick-unwinnable dead position is queryable only; the pipeline must accept the move");
  }

  // --- precedence: hard blockers win when they coincide with queryable rules ---

  @SuppressWarnings("static-method")
  @Test
  void testGameEndedByInsufficientMaterialOverridesSeventyFiveMove() {
    // KvK with halfmove clock at the 75-move threshold. Both isInsufficientMaterial() and
    // isSeventyFiveMove() are true — the move pipeline must still reject the move, and the
    // payload must be the hard blocker (DEAD_POSITION_INSUFFICIENT_MATERIAL), not the
    // queryable rule. Locks in the BasicChessUtility.calculateGameStatus precedence ordering
    // at the move-pipeline layer.
    final Board board = new Board("4k3/8/8/8/8/8/8/4K3 w - - 150 76");
    check(board, new MoveSpecification(E1, E2), GameStatus.DEAD_POSITION_INSUFFICIENT_MATERIAL);
  }

  @SuppressWarnings("static-method")
  @Test
  void testMoveAcceptedAtSeventyFiveMoveThreshold() {
    // FEN with halfmove clock at the 75-move threshold (150). isSeventyFiveMove() returns true,
    // but the move pipeline accepts further moves — fivefold and 75-move are queryable, not
    // enforced.
    final Board board = new Board("4k3/8/4P3/8/8/8/2N1B3/3KQ2R w - - 150 76");
    assertTrue(board.isSeventyFiveMove(), "predicate must fire at threshold");
    assertDoesNotThrow(() -> board.move(new MoveSpecification(D1, D2)),
        "75-move is queryable only; the pipeline must accept the move");
  }

  @SuppressWarnings("static-method")
  @Test
  void testMoveAcceptedAtFivefoldThreshold() {
    // Drive the board to fivefold by alternating knight moves so the starting position recurs 5
    // times. isFivefoldRepetition() returns true, but the move pipeline accepts further moves.
    final Board board = new Board();
    board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6",
        "Ng1", "Ng8");
    assertTrue(board.isFivefoldRepetition(), "predicate must fire at fivefold");
    assertDoesNotThrow(() -> board.move(new MoveSpecification(E2, E4)),
        "fivefold is queryable only; the pipeline must accept the move");
  }

  // --- helpers ---

  private static void check(Board board, MoveSpecification move, GameStatus expectedGameStatus) {
    var isException = false;
    try {
      board.move(move);
    } catch (final InvalidMoveException e) {
      isException = true;
      assertEquals(MoveCheck.GAME_ALREADY_ENDED, e.getMoveCheck());
      assertNotNull(e.getGameStatus(), "GAME_ALREADY_ENDED must carry a GameStatus payload");
      assertEquals(expectedGameStatus, e.getGameStatus());
    }
    assertTrue(isException, "Expected InvalidMoveException with GAME_ALREADY_ENDED");
  }
}
