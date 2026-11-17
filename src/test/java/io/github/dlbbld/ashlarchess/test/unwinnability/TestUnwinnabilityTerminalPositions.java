// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.unwinnability.DeadPositionFullVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityFullAnalysis;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityFullVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnableFullAnalyzer;

/**
 * Lock-down for the terminal-position contract of the full unwinnability analyzer, following Ambrona's CHA
 * Find-Helpmate base cases (Figure 5): an already-checkmate position is {@code WINNABLE} for the side that delivered
 * mate - a zero-move helpmate, so the mate line is empty - and {@code UNWINNABLE} for the mated side; a stalemate is
 * {@code UNWINNABLE} for both sides. Otherwise only exercised incidentally via the CHA oracle corpus.
 */
class TestUnwinnabilityTerminalPositions {

  @SuppressWarnings("static-method")
  @Test
  void reachablePawnCoveredMateHasAZeroMoveWitness() {
    final Board board = replay("c4 b5 cxb5 f5 g4 fxg4 Qb3 Bb7 Qxg8 Bxh1 Qxf8+ Rxf8 Bg2 Rxf2 Bxa8 Rxe2+ "
        + "Kf1 Rxh2 b4 Rxd2 Bxd2 d5 Nc3 h6 Nxd5 g5 Nxc7+ Kd7 Be3 Kxc7 Bxa7 Qd4 Bxb8+ Kxb8 a4 Qxg1+ "
        + "Kxg1 e5 Re1 e4 Rxe4 Bxe4 b6 Bh1 b7 g3 b5 g2 a5 g4 b6 g3 a6 h5");
    assertEquals(Nulls.listOf("a7#"), board.getLegalMovesAsSan());
    assertEquals(UnwinnabilityFullVerdict.WINNABLE, board.unwinnableFull(Side.WHITE));
    board.moveStrict("a7#");
    assertEquals("Bk6/PP6/1P6/7p/8/6p1/6p1/6Kb b - - 0 28", board.getFen());
    assertDeliveredMate(board, Side.WHITE);
    assertDeliveredMate(Board.fromFenStrict(board.getFen()), Side.WHITE);
  }

  @SuppressWarnings("static-method")
  @Test
  void reachableBlackPawnCoveredMateHasAZeroMoveWitness() {
    final Board board = replay("b3 c5 b4 cxb4 f4 g5 fxg5 Qb6 Bb2 Qxg1 Bxh8 Qxf1+ Rxf1 Bg7 Rxf7 Bxa1 "
        + "Rxe7+ Kf8 Rxh7 b5 Rxd7 Bxd7 d4 Nc6 h3 Nxd4 g4 Nxc2+ Kd2 Be6 Kxc2 Bxa2 Qd5 Bxb1+ Kxb1 a5 "
        + "Qxg8+ Kxg8 e4 Re8 e5 Rxe5 Bxe5 b3 Bh8 b2 g6 b4 g7 a4 g5 b3 g6 a3 h4 a2#");
    assertEquals("6kB/6P1/6P1/8/7P/1p6/pp6/bK6 w - - 0 29", board.getFen());
    assertDeliveredMate(board, Side.BLACK);
    assertDeliveredMate(Board.fromFenStrict(board.getFen()), Side.BLACK);
  }

  private static void assertDeliveredMate(Board board, Side winner) {
    assertTrue(board.isCheckmate());
    final UnwinnabilityFullAnalysis analysis = UnwinnableFullAnalyzer.unwinnableFull(board, winner);
    assertEquals(UnwinnabilityFullVerdict.WINNABLE, analysis.verdict());
    assertTrue(analysis.mateLine().isEmpty());
    assertEquals(UnwinnabilityFullVerdict.UNWINNABLE, board.unwinnableFull(winner.getOppositeSide()));
    assertEquals(DeadPositionFullVerdict.ALIVE, board.deadPositionFull());
  }

  private static Board replay(String moves) {
    final Board board = new Board();
    for (final String move : Nulls.split(moves, " ")) {
      board.moveStrict(move);
    }
    return board;
  }

  /** Fool's mate (1. f3 e5 2. g4 Qh4#): White is to move and checkmated; Black delivered the mate. */
  private static final String FOOLS_MATE = "rnb1kbnr/pppp1ppp/8/4p3/6Pq/5P2/PPPPP2P/RNBQKBNR w KQkq - 1 3";

  /** Black to move and stalemated (black king h8; white queen f7, white king g6). */
  private static final String STALEMATE = "7k/5Q2/6K1/8/8/8/8/8 b - - 0 1";

  @SuppressWarnings("static-method")
  @Test
  void terminalPositionsStillRequireARealWinner() {
    for (final String fen : Nulls.listOf(FOOLS_MATE, STALEMATE)) {
      final Board board = Board.fromFenStrict(fen);
      assertThrows(ProgrammingMistakeException.class,
          () -> UnwinnableFullAnalyzer.unwinnableFull(board, Side.NONE));
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testCheckmateIsWinnableForTheMatingSideAsAZeroMoveHelpmate() {
    final Board board = Board.fromFenStrict(FOOLS_MATE);
    final UnwinnabilityFullAnalysis analysis = UnwinnableFullAnalyzer.unwinnableFull(board, Side.BLACK);
    assertEquals(UnwinnabilityFullVerdict.WINNABLE, analysis.verdict());
    assertTrue(analysis.mateLine().isEmpty(), "an already-delivered mate is a zero-move helpmate");
  }

  @SuppressWarnings("static-method")
  @Test
  void testCheckmateIsUnwinnableForTheMatedSide() {
    final Board board = Board.fromFenStrict(FOOLS_MATE);
    assertEquals(UnwinnabilityFullVerdict.UNWINNABLE,
        UnwinnableFullAnalyzer.unwinnableFull(board, Side.WHITE).verdict());
  }

  @SuppressWarnings("static-method")
  @Test
  void testStalemateIsUnwinnableForBothSides() {
    final Board board = Board.fromFenStrict(STALEMATE);
    assertEquals(UnwinnabilityFullVerdict.UNWINNABLE,
        UnwinnableFullAnalyzer.unwinnableFull(board, Side.WHITE).verdict());
    assertEquals(UnwinnabilityFullVerdict.UNWINNABLE,
        UnwinnableFullAnalyzer.unwinnableFull(board, Side.BLACK).verdict());
  }
}
