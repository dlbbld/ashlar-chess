package io.github.dlbbld.ashlarchess.squares;

import java.util.Set;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;

/**
 * Test-only public bridge that exposes the package-private sliding-attack reference classes
 * ({@link BishopAttackedSquares}, {@link RookAttackedSquares}, {@link QueenAttackedSquares}) so the bitboard
 * differential tests under {@code io.github.dlbbld.ashlarchess.test.bitboard} can call them. Lives under
 * {@code src/test/} so it is not part of the production API surface.
 */
public final class SlidingAttacksTestOracle {

  private SlidingAttacksTestOracle() {
  }

  public static Set<Square> bishopAttacks(StaticPosition staticPosition, Square fromSquare, Side havingMove) {
    return BishopAttackedSquares.calculateBishopAttackedSquares(staticPosition, fromSquare, havingMove);
  }

  public static Set<Square> rookAttacks(StaticPosition staticPosition, Square fromSquare, Side havingMove) {
    return RookAttackedSquares.calculateRookAttackedSquares(staticPosition, fromSquare, havingMove);
  }

  public static Set<Square> queenAttacks(StaticPosition staticPosition, Square fromSquare, Side havingMove) {
    return QueenAttackedSquares.calculateQueenAttackedSquares(staticPosition, fromSquare, havingMove);
  }
}
