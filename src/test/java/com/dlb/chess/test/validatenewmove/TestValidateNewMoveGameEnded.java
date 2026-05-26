package com.dlb.chess.test.validatenewmove;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.enums.MoveCheck;
import com.dlb.chess.exceptions.InvalidMoveException;
import com.dlb.chess.unwinnability.DeadPositionQuick;

/**
 * Pins the library's posture at game-end states: termination is queryable, not enforced. None of the five automatic
 * terminations — checkmate, stalemate, mutual insufficient material, fivefold repetition, 75-move rule — and none of
 * the analyzer-driven dead positions block further move attempts at the validation pipeline.
 *
 * <p>
 * At checkmate and stalemate the natural barrier is the empty legal-move set: any attempted move fails through ordinary
 * move-legality checks (own-piece occupation, king-into-check, etc.), not via a dedicated game-end gate. At mutual
 * insufficient material, fivefold, 75-move, and analyzer-driven dead positions, legal moves still exist and the
 * pipeline accepts them — the caller polls {@code calculateGameStatus} or the specific predicates to learn the game
 * has reached an automatic termination.
 *
 * <p>
 * The companion {@code TestSanValidationGameEnded} mirrors this set against the SAN pipeline.
 */
class TestValidateNewMoveGameEnded implements EnumConstants {

  // --- CHECKMATE: empty legal-move set; any move fails through ordinary legality ---

  @SuppressWarnings("static-method")
  @Test
  void testCheckmateLegalMovesEmpty() {
    // Fool's mate: white is checkmated by black queen on h4.
    final Board board = new Board("rnb1kbnr/pppp1ppp/8/4p3/6Pq/5P2/PPPPP2P/RNBQKBNR w KQkq - 1 3");
    assertTrue(board.isCheckmate(), "fool's mate position must be checkmate");
    assertTrue(board.getLegalMoves().isEmpty(), "checkmate has no legal moves");

    // The pipeline rejects any attempt via ordinary legality — not via a dedicated game-end gate.
    // Ke2 fails because e2 is occupied by the white pawn.
    rejectsWith(board, new MoveSpecification(E1, E2), MoveCheck.MOVEMENT_TO_SQUARE_OCCUPIED_BY_OWN_PIECE);
  }

  // --- STALEMATE: empty legal-move set; any move fails through ordinary legality ---

  @SuppressWarnings("static-method")
  @Test
  void testStalemateLegalMovesEmpty() {
    // Black king h8 has no legal move and is not in check.
    final Board board = new Board("7k/8/6Q1/8/8/8/8/K7 b - - 0 1");
    assertTrue(board.isStalemate(), "K+Q vs K position must be stalemate for black");
    assertTrue(board.getLegalMoves().isEmpty(), "stalemate has no legal moves");

    // Kg8 fails because g8 is attacked by the white queen on g6.
    rejectsWith(board, new MoveSpecification(H8, G8), MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
  }

  // --- automatic terminations with non-empty legal-move set: pipeline accepts the move ---

  @SuppressWarnings("static-method")
  @Test
  void testMoveAcceptedAtInsufficientMaterialBoth() {
    // K vs K: dead position under FIDE 5.2.2. The pipeline accepts further moves; the caller
    // polls calculateGameStatus / isInsufficientMaterial to learn the game has terminated.
    final Board board = new Board("4k3/8/8/8/8/8/8/4K3 w - - 0 1");
    assertTrue(board.isInsufficientMaterial(), "K vs K is mutual insufficient material");
    assertDoesNotThrow(() -> board.move(new MoveSpecification(E1, E2)),
        "insufficient material is queryable only; the pipeline must accept the move");
  }

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

  @SuppressWarnings("static-method")
  @Test
  void testMoveAcceptedAtSeventyFiveMoveThreshold() {
    // FEN with halfmove clock at the 75-move threshold (150). isSeventyFiveMove() returns true,
    // but the pipeline accepts further moves.
    final Board board = new Board("4k3/8/4P3/8/8/8/2N1B3/3KQ2R w - - 150 76");
    assertTrue(board.isSeventyFiveMove(), "predicate must fire at threshold");
    assertDoesNotThrow(() -> board.move(new MoveSpecification(D1, D2)),
        "75-move is queryable only; the pipeline must accept the move");
  }

  @SuppressWarnings("static-method")
  @Test
  void testMoveAcceptedAtFivefoldThreshold() {
    // Drive the board to fivefold by alternating knight moves so the starting position recurs 5
    // times. isFivefoldRepetition() returns true, but the pipeline accepts further moves.
    final Board board = new Board();
    board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6",
        "Ng1", "Ng8");
    assertTrue(board.isFivefoldRepetition(), "predicate must fire at fivefold");
    assertDoesNotThrow(() -> board.move(new MoveSpecification(E2, E4)),
        "fivefold is queryable only; the pipeline must accept the move");
  }

  // --- helpers ---

  /**
   * Asserts that the move is rejected with the given {@link MoveCheck} — and crucially, not via the retired
   * {@link MoveCheck#GAME_ALREADY_ENDED} gate.
   */
  private static void rejectsWith(Board board, MoveSpecification move, MoveCheck expected) {
    var thrown = false;
    try {
      board.move(move);
    } catch (final InvalidMoveException e) {
      thrown = true;
      assertNotEquals(MoveCheck.GAME_ALREADY_ENDED, e.getMoveCheck(),
          "after A1 ungating no rejection should travel through the GAME_ALREADY_ENDED gate");
      assertEquals(expected, e.getMoveCheck());
    }
    assertTrue(thrown, "expected InvalidMoveException");
  }
}
