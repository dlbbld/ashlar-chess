package com.dlb.chess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.bitboard.BitboardPositionUtility;
import com.dlb.chess.board.Board;
import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.common.utility.StaticPositionUtility;
import com.dlb.chess.bitboard.StaticPositionBridge;

/**
 * {@link Board#getBitboardPosition()} integration tests. Step 3 of the switchover release removed the cached
 * StaticPosition from {@code DynamicPosition} (the bitboard is the single source of truth on the data path now;
 * {@code Board.getStaticPosition()} survives as an on-demand derived view of the cached bitboard), so the Step 1.2
 * parallel-cache invariant cannot be asserted by reading {@code Board.getStaticPosition()} anymore — that would be
 * tautological (mailbox derived from the very bitboard we want to verify). The cache-correctness invariant is still
 * meaningful and is asserted here against a <em>genuinely independent</em> oracle: a StaticPosition maintained in
 * the test by applying {@link StaticPositionUtility#createPositionAfterMove} for every move played on the
 * {@code Board}. The bitboard cache must match {@code fromStaticPosition} of this independently-maintained
 * StaticPosition at every step.
 *
 * <p>
 * Corpus-wide cache verification is no longer needed at this level: every other bitboard differential test
 * ({@code TestBitboardPositionLegalMoves}, {@code TestBitboardPositionAfterMove},
 * {@code TestBitboardPositionAttackedSquares}, etc.) exercises {@code board.getBitboardPosition()} on every fixture
 * and asserts it against the StaticPosition reference; a stale cache would fail those.
 */
class TestBoardGetBitboardPosition {

  @SuppressWarnings("static-method")
  @Test
  void initialPositionMatchesBitboardConstant() {
    final Board board = new Board(false);
    assertEquals(BitboardPosition.INITIAL_POSITION, board.getBitboardPosition());
  }

  @SuppressWarnings("static-method")
  @Test
  void handPlayedMoveAndUnmoveKeepsBitboardInSync() throws Exception {
    // Hand-played opening (e4, e5, Nf3, Nc6, Bb5) plus full unmove walk back to initial. At each intermediate
    // state the bitboard cache must equal fromStaticPosition(independentlyMaintainedSP). The independent SP is
    // built by applying StaticPositionUtility.createPositionAfterMove move-by-move — no bitboard involved in
    // computing the reference. This is the cache-correctness invariant from Step 1.2, expressed against a truly
    // independent oracle now that the Board-side StaticPosition cache is gone.
    final Board board = new Board(false);
    final List<StaticPosition> independentHistory = new ArrayList<>();
    independentHistory.add(StaticPosition.INITIAL_POSITION);

    final List<List<Square>> listMoveSquareList = new ArrayList<>();
    listMoveSquareList.add(Nulls.asList(Square.E2, Square.E4));
    listMoveSquareList.add(Nulls.asList(Square.E7, Square.E5));
    listMoveSquareList.add(Nulls.asList(Square.G1, Square.F3));
    listMoveSquareList.add(Nulls.asList(Square.B8, Square.C6));
    listMoveSquareList.add(Nulls.asList(Square.F1, Square.B5));

    Side currentSide = Side.WHITE;
    for (final List<Square> moveSquareList : listMoveSquareList) {
      final Square squareFrom = Nulls.get(moveSquareList, 0);
      final Square squareTo = Nulls.get(moveSquareList, 1);
      final MoveSpecification spec = new MoveSpecification(squareFrom, squareTo);

      final StaticPosition previousIndependent = Nulls.get(independentHistory, independentHistory.size() - 1);
      final StaticPosition nextIndependent = StaticPositionUtility.createPositionAfterMove(previousIndependent,
          currentSide, spec);
      independentHistory.add(nextIndependent);

      board.move(spec);

      assertEquals(StaticPositionBridge.fromStaticPosition(nextIndependent), board.getBitboardPosition(),
          "cache out of sync after move " + squareFrom.getName() + "-" + squareTo.getName());

      currentSide = currentSide.getOppositeSide();
    }

    while (!board.isFirstMove()) {
      board.unmove();
      independentHistory.remove(independentHistory.size() - 1);
      final StaticPosition expectedIndependent = Nulls.get(independentHistory, independentHistory.size() - 1);
      assertEquals(StaticPositionBridge.fromStaticPosition(expectedIndependent), board.getBitboardPosition(),
          "cache out of sync after unmove (expected independent step " + (independentHistory.size() - 1) + ")");
    }
    assertEquals(BitboardPosition.INITIAL_POSITION, board.getBitboardPosition(),
        "cache should be back to initial after full unmove");
  }
}
