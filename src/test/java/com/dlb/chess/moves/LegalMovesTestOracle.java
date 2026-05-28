package com.dlb.chess.moves;

import java.util.Set;
import java.util.TreeSet;

import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.model.LegalMove;

/**
 * Test-only public bridge that exposes the package-private legal-move generators under {@code com.dlb.chess.moves} so
 * the bitboard differential tests under {@code com.dlb.chess.test.bitboard} can call them. Lives under
 * {@code src/test/} so it is not part of the production API surface.
 */
public final class LegalMovesTestOracle {

  private LegalMovesTestOracle() {
  }

  public static Set<Square> kingNonCastlingLegalTargets(StaticPosition staticPosition, Square fromSquare, Side side) {
    final Set<LegalMove> legalMoves = KingNonCastlingLegalMoves.calculateKingNonCastlingLegalMoves(staticPosition, side,
        fromSquare);
    final Set<Square> targets = new TreeSet<>();
    for (final LegalMove legalMove : legalMoves) {
      targets.add(legalMove.moveSpecification().toSquare());
    }
    return targets;
  }
}
