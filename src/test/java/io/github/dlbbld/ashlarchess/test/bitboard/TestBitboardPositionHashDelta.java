package io.github.dlbbld.ashlarchess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.bitboard.StaticPositionBridge;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

/**
 * Differential test for {@link BitboardPosition#hashDelta}: applying the XOR delta to the before-hash must match the
 * full Zobrist recomputation on the after-position. Validates the incremental Zobrist update across every fixture x
 * every legal move shape (normal, capture, en-passant, all four promotion targets, castling).
 */
class TestBitboardPositionHashDelta {

  @SuppressWarnings("static-method")
  @Test
  void corpusEveryLegalMoveDeltaMatchesFreshHash() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        final Board board = testCase.finalPosition();
        final StaticPosition staticPosition = StaticPositionBridge.toStaticPosition(board.getBitboardPosition());
        final BitboardPosition before = StaticPositionBridge.fromStaticPosition(staticPosition);
        final long beforeHash = before.zobristPieces();
        final Side havingMove = board.getHavingMove();
        for (final LegalMove legalMove : board.getLegalMoves()) {
          final MoveSpecification spec = legalMove.moveSpecification();
          final long incrementalHash = beforeHash ^ before.hashDelta(spec, havingMove);
          final long freshHash = before.afterMove(spec, havingMove).zobristPieces();
          assertEquals(freshHash, incrementalHash,
              "hashDelta disagreement for " + spec + " in fixture " + testCase.pgnName());
        }
      }
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void initialPositionE2E4Delta() {
    final MoveSpecification e2e4 = new MoveSpecification(Square.E2, Square.E4);
    final BitboardPosition before = BitboardPosition.INITIAL_POSITION;
    final long beforeHash = before.zobristPieces();
    final long incrementalHash = beforeHash ^ before.hashDelta(e2e4, Side.WHITE);
    final long freshHash = before.afterMove(e2e4, Side.WHITE).zobristPieces();
    assertEquals(freshHash, incrementalHash);
  }

  @SuppressWarnings("static-method")
  @Test
  void noneSideThrows() {
    assertThrows(IllegalArgumentException.class,
        () -> BitboardPosition.INITIAL_POSITION.hashDelta(new MoveSpecification(Square.E2, Square.E4), Side.NONE));
  }
}
