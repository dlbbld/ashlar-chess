package com.dlb.chess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.bitboard.StaticPositionBridge;
import com.dlb.chess.board.Board;
import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.model.LegalMove;
import com.dlb.chess.test.model.PgnFen;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

/**
 * Property tests for {@link BitboardPosition#zobristPieces()}: equal positions hash equal (consistency); distinct
 * positions in the corpus hash distinctly (no collisions observed); and mutating a position by playing a legal move
 * produces a different hash (the hash actually changes with the position).
 */
class TestBitboardPositionZobrist {

  @SuppressWarnings("static-method")
  @Test
  void equalPositionsHashEqual() {
    // Two independently-constructed INITIAL_POSITION values must hash identical.
    final BitboardPosition a = StaticPositionBridge.fromStaticPosition(StaticPosition.INITIAL_POSITION);
    final BitboardPosition b = BitboardPosition.INITIAL_POSITION;
    assertEquals(a, b);
    assertEquals(a.zobristPieces(), b.zobristPieces());

    // EMPTY_POSITION twice - both 0L (XOR of nothing).
    assertEquals(0L, BitboardPosition.EMPTY_POSITION.zobristPieces());
  }

  @SuppressWarnings("static-method")
  @Test
  void corpusNoCollisionsAndConsistent() {
    // Build a hash -> position map walking the corpus. If two positions are equal, their hashes must be equal
    // (consistency). If two hashes are equal, their positions must be equal (no collisions in this corpus).
    final Map<Long, BitboardPosition> seen = new HashMap<>();
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        final BitboardPosition position = StaticPositionBridge
            .fromStaticPosition(StaticPositionBridge.toStaticPosition(testCase.finalPosition().getBitboardPosition()));
        final var hash = position.zobristPieces();
        if (seen.containsKey(hash)) {
          final @Nullable BitboardPosition existing = seen.get(hash);
          assertEquals(position, existing,
              "Zobrist collision: distinct positions hashed equal in fixture " + testCase.pgnName());
        }
      }
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void corpusEveryLegalMoveChangesTheHash() {
    // Applying any legal move changes the piece placement, so the piece-placement hash must change too.
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        final Board board = testCase.finalPosition();
        final BitboardPosition before = board.getBitboardPosition();
        final var beforeHash = before.zobristPieces();
        final Side havingMove = board.getHavingMove();
        for (final LegalMove legalMove : board.getLegalMoves()) {
          final MoveSpecification spec = legalMove.moveSpecification();
          final BitboardPosition after = before.afterMove(spec, havingMove);
          assertNotEquals(beforeHash, after.zobristPieces(),
              "applying " + spec + " in fixture " + testCase.pgnName() + " left the hash unchanged");
        }
      }
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void afterMoveHashMatchesFreshRecomputation() {
    // The hash of afterMove(...) must equal a fresh recomputation off the same position (consistency under the
    // make-move pipeline - sets up the incremental-Zobrist work in Phase 8.3).
    final MoveSpecification e2e4 = new MoveSpecification(Square.E2, Square.E4);
    final BitboardPosition after = BitboardPosition.INITIAL_POSITION.afterMove(e2e4, Side.WHITE);
    assertEquals(after.zobristPieces(),
        StaticPositionBridge.fromStaticPosition(StaticPositionBridge.toStaticPosition(after)).zobristPieces());
  }
}
