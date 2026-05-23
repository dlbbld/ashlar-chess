package com.dlb.chess.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.model.DynamicPosition;
import com.dlb.chess.model.LegalMove;

/**
 * Phase B.2 gate: for every legal move at every node of a recursive walk, asserts that
 * {@link HelpmateSearchBoard#move} followed immediately by {@link HelpmateSearchBoard#unmove} restores every
 * observable field to its pre-move value. Covers the same scenario set as
 * {@link TestHelpmateSearchBoard} so the round-trip property is verified on the categories the parity test
 * already pins (initial, castling, EP both legal and illegal, promotion, terminal flags, check-evasion variants).
 *
 * <p>
 * "Observable fields" per the Phase B gate spec: 12 piece bitboards (via {@link BitboardPosition#equals}), side to
 * move, raw EP target, normalized EP target (via {@link DynamicPosition#enPassantCaptureTargetSquare}), castling
 * rights both sides, the {@code legalMoves} list (ordered equality — this is self-comparison of the same ply before
 * make and after unmake, so ordered list equality is the right contract), and the cached derived flags
 * ({@code isCheck} / {@code isCheckmate} / {@code isStalemate}).
 */
class TestHelpmateSearchBoardMakeUnmakeRoundTrip {

  /** Mirrors {@link TestHelpmateSearchBoard}'s scenario set. {@code null} fen means initial position. */
  private static final List<Scenario> SCENARIOS = List.of(new Scenario("initial", null, 2),
      new Scenario("all-four-castling-rights", "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1", 1),
      new Scenario("legal-en-passant", "8/8/8/8/3pP3/8/8/K6k b - e3 0 1", 2),
      new Scenario("illegal-en-passant-normalization", "8/8/8/8/k2pP2R/8/8/7K b - e3 0 1", 1),
      new Scenario("both-sides-promote", "7k/P7/8/8/8/8/7p/K7 w - - 0 1", 1),
      new Scenario("checkmate-terminal", "7k/6Q1/6K1/8/8/8/8/8 b - - 0 1", 0),
      new Scenario("stalemate-terminal", "7k/5Q2/6K1/8/8/8/8/8 b - - 0 1", 0),
      new Scenario("check-with-king-only-evasions", "4k3/8/8/8/8/8/4R3/4K3 b - - 0 1", 3),
      new Scenario("double-check-king-only", "4k3/2N5/8/8/8/2r5/8/4R2K b - - 0 1", 2),
      new Scenario("en-passant-resolves-check", "4k3/8/8/3pP3/2K5/8/8/8 w - d6 0 1", 1));

  @SuppressWarnings("static-method")
  @Test
  void everyLegalMoveRoundTrips() {
    for (final Scenario scenario : SCENARIOS) {
      try {
        final Board board = scenario.fen() == null ? new Board(false) : new Board(scenario.fen(), false);
        final HelpmateSearchBoard searchBoard = HelpmateSearchBoard.from(board);
        assertRoundTripsRecursively(searchBoard, scenario.depth());
      } catch (final AssertionError | RuntimeException e) {
        throw new AssertionError("scenario=" + scenario.label() + " fen=" + scenario.fen() + " depth="
            + scenario.depth(), e);
      }
    }
  }

  private static void assertRoundTripsRecursively(HelpmateSearchBoard searchBoard, int depth) {
    if (depth == 0 || searchBoard.getLegalMoves().isEmpty()) {
      return;
    }
    // Take a defensive copy: the act of move() will mutate searchBoard.getLegalMoves() reference.
    final List<LegalMove> legalMovesBefore = List.copyOf(searchBoard.getLegalMoves());

    for (final LegalMove legalMove : legalMovesBefore) {
      final BitboardPosition beforeBitboard = searchBoard.getBitboardPosition();
      final DynamicPosition beforeDp = searchBoard.getDynamicPosition();
      // Defensive copy: per-depth buffers preserve the live view at depth N through recursion, but a snapshot
      // here makes the round-trip comparison robust to any future buffer-management change.
      final List<LegalMove> beforeLegalMoves = List.copyOf(searchBoard.getLegalMoves());
      final Side beforeHavingMove = searchBoard.getHavingMove();
      final Square beforeRawEp = searchBoard.getEnPassantCaptureTargetSquare();
      final var beforeCheck = searchBoard.isCheck();
      final var beforeCheckmate = searchBoard.isCheckmate();
      final var beforeStalemate = searchBoard.isStalemate();

      searchBoard.move(legalMove.moveSpecification());
      assertRoundTripsRecursively(searchBoard, depth - 1);
      searchBoard.unmove();

      assertEquals(beforeBitboard, searchBoard.getBitboardPosition(), "bitboard after unmove (move " + legalMove + ")");
      assertEquals(beforeDp, searchBoard.getDynamicPosition(), "dynamicPosition after unmove (move " + legalMove + ")");
      assertEquals(beforeLegalMoves, searchBoard.getLegalMoves(), "legalMoves after unmove (move " + legalMove + ")");
      assertEquals(beforeHavingMove, searchBoard.getHavingMove(), "havingMove after unmove (move " + legalMove + ")");
      assertEquals(beforeRawEp, searchBoard.getEnPassantCaptureTargetSquare(),
          "raw EP after unmove (move " + legalMove + ")");
      assertEquals(beforeCheck, searchBoard.isCheck(), "isCheck after unmove (move " + legalMove + ")");
      assertEquals(beforeCheckmate, searchBoard.isCheckmate(), "isCheckmate after unmove (move " + legalMove + ")");
      assertEquals(beforeStalemate, searchBoard.isStalemate(), "isStalemate after unmove (move " + legalMove + ")");
    }
  }

  private record Scenario(String label, String fen, int depth) {
  }
}
