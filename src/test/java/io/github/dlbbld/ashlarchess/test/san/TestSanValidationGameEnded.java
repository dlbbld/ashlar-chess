// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.san;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.san.SanValidationException;
import io.github.dlbbld.ashlarchess.san.StrictSanParser;
import io.github.dlbbld.ashlarchess.unwinnability.DeadPositionQuick;

/**
 * SAN-pipeline mirror of {@code TestValidateNewMoveGameEnded}: pins the same queryable-only posture at game-end states.
 * None of the five automatic terminations and none of the analyzer-driven dead positions block further SAN input.
 *
 * <p>
 * At checkmate and stalemate the empty legal-move set causes SAN parsing to fail through ordinary legality, not via a
 * dedicated game-end gate. At mutual insufficient material, fivefold, 75-move, and analyzer-driven dead positions, SAN
 * input is accepted.
 */
class TestSanValidationGameEnded {

  // --- CHECKMATE / STALEMATE: empty legal-move set; SAN fails through ordinary legality ---

  @SuppressWarnings("static-method")
  @Test
  void testCheckmateSanRejectedThroughOrdinaryLegality() {
    // Fool's mate. "Ke2" cannot match any legal move because the king has none.
    final Board board = new Board("rnb1kbnr/pppp1ppp/8/4p3/6Pq/5P2/PPPPP2P/RNBQKBNR w KQkq - 1 3");
    assertTrue(board.isCheckmate(), "fool's mate position must be checkmate");
    assertTrue(board.getLegalMoves().isEmpty(), "checkmate has no legal moves");
    rejectsNotViaGameEnded("Ke2", board);
  }

  @SuppressWarnings("static-method")
  @Test
  void testStalemateSanRejectedThroughOrdinaryLegality() {
    final Board board = new Board("7k/8/6Q1/8/8/8/8/K7 b - - 0 1");
    assertTrue(board.isStalemate(), "K+Q vs K position must be stalemate for black");
    assertTrue(board.getLegalMoves().isEmpty(), "stalemate has no legal moves");
    rejectsNotViaGameEnded("Kg8", board);
  }

  // --- automatic terminations with non-empty legal-move set: SAN accepted ---

  @SuppressWarnings("static-method")
  @Test
  void testSanAcceptedAtInsufficientMaterialBoth() {
    final Board board = new Board("4k3/8/8/8/8/8/8/4K3 w - - 0 1");
    assertTrue(board.isInsufficientMaterial(), "K vs K is mutual insufficient material");
    assertDoesNotThrow(() -> StrictSanParser.parseText("Ke2", board),
        "insufficient material is queryable only; the SAN parser must accept the move");
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

  /** Asserts that SAN parsing is rejected. */
  private static void rejectsNotViaGameEnded(String san, Board board) {
    boolean thrown = false;
    try {
      StrictSanParser.parseText(san, board);
    } catch (@SuppressWarnings("unused") final SanValidationException e) {
      thrown = true;
    }
    assertTrue(thrown, "expected SanValidationException");
  }
}
