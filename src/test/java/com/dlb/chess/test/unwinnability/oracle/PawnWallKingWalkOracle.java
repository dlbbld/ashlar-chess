package com.dlb.chess.test.unwinnability.oracle;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.utility.StaticPositionUtility;
import com.dlb.chess.bitboard.BitboardPositionUtility;

/**
 * Test-only BFS oracle for the pawn-wall classifier.
 *
 * <p>
 * The production {@link PawnWallGeometricAnalyzer} predicate uses a geometric chain check. This oracle is the second
 * opinion: a king-walk BFS asking whether the side's king can reach the opposing king's square through passable squares
 * — i.e. whether the wall is a genuine separator between the two kings.
 *
 * <p>
 * <b>Passable squares</b>: empty squares; squares occupied by same-side non-pawn pieces (they can move out of the way
 * in a future move, opponent cooperating); squares occupied by undefended opposing pawns (the king captures them and
 * continues from the captured square); squares occupied by opposing non-pawn pieces (king captures). The opposing
 * king's square is treated as the BFS target — if the king's reach extends to it, the wall isn't a separator.
 *
 * <p>
 * <b>Impassable squares</b>: squares occupied by same-side pawns (locked in a wall configuration); squares attacked by
 * any opposing pawn still on the board. The "attacked" check covers both empty attacked squares (king can't enter
 * check) and opposing pawns defended by another opposing pawn (the defender's attack also hits the defended pawn's
 * square — capture would be re-captured).
 *
 * <p>
 * <b>Dynamic capture model</b>: the oracle iterates to a fixed point. Each round computes opposing-pawn attacks from
 * the <em>remaining</em> opposing pawns (after previous captures), then BFS-expands the king's reach. Any undefended
 * opposing pawn within reach is captured (removed from the remaining set) and a new round runs with updated attacks.
 * The process terminates when a round produces no new captures. This avoids the conservative under-approximation of a
 * single-pass static BFS, which would leave squares attacked by an already-captured pawn marked impassable forever.
 *
 * <p>
 * See {@code pawn-wall-soundness.md} for the full design and the asymmetric agreement contract
 * ({@code geometric_YES ⟹ BFS_YES}).
 */
final class PawnWallKingWalkOracle {

  private PawnWallKingWalkOracle() {
  }

  /**
   * Returns {@code true} iff the king of {@code side} cannot reach the opposing king's square through any sequence of
   * king moves and captures of undefended opposing pawns — i.e. the wall topologically separates the two kings under
   * the helpmate-cooperation movement model, considering dynamic capture effects on opposing-pawn attacks.
   */
  static boolean isKingTrappedBehindPermanentBarrier(Board board, Side side) {
    final StaticPosition position = BitboardPositionUtility.toStaticPosition(board.getBitboardPosition());
    final Side opponent = side.getOppositeSide();

    final Square ownKingSquare = StaticPositionUtility.calculateKingSquare(position, side);
    final Square opposingKingSquare = StaticPositionUtility.calculateKingSquare(position, opponent);

    final Set<Square> remainingOpposingPawns = collectOpposingPawnSquares(position, opponent);
    final Set<Square> reachable = EnumSet.noneOf(Square.class);
    reachable.add(ownKingSquare);

    while (true) {
      final Set<Square> attacks = calculatePawnAttacksFrom(remainingOpposingPawns, opponent);
      expandReachable(reachable, position, side, attacks);
      final List<Square> capturedThisRound = new ArrayList<>();
      for (final Square pawn : remainingOpposingPawns) {
        if (reachable.contains(pawn)) {
          capturedThisRound.add(pawn);
        }
      }
      if (capturedThisRound.isEmpty()) {
        break;
      }
      remainingOpposingPawns.removeAll(capturedThisRound);
    }
    // King is trapped iff the reach does not include the opposing king's square.
    return !reachable.contains(opposingKingSquare);
  }

  /**
   * BFS-expands the {@code reachable} set in place. Adds every passable neighbour of every reachable square, iterating
   * until no new squares are added.
   */
  private static void expandReachable(Set<Square> reachable, StaticPosition position, Side side,
      Set<Square> opposingPawnAttacks) {
    final Deque<Square> queue = new ArrayDeque<>(reachable);
    while (!queue.isEmpty()) {
      @SuppressWarnings("null") final Square current = queue.poll();
      for (final Square neighbour : kingNeighbours(current)) {
        if (reachable.contains(neighbour)) {
          continue;
        }
        final Piece piece = position.get(neighbour);
        // Impassable: same-side pawn (locked in a wall configuration).
        if (piece != Piece.NONE && piece.getSide() == side && piece.getPieceType() == PieceType.PAWN) {
          continue;
        }
        // Impassable: attacked by a remaining opposing pawn.
        if (opposingPawnAttacks.contains(neighbour)) {
          continue;
        }
        // Passable: everything else (empty; own non-pawn piece; undefended opposing pawn; opposing non-pawn piece;
        // opposing king's square). Captures are modelled by continuing BFS from the captured-piece's square; the
        // capture itself is finalised in the outer fixed-point loop, which then recomputes attacks.
        reachable.add(neighbour);
        queue.add(neighbour);
      }
    }
  }

  /**
   * Returns the set of squares attacked by pawns of {@code side} that occupy any of the squares in {@code pawnSquares}.
   * Each pawn attacks the two squares one rank forward (from {@code side}'s perspective) and one file left or right.
   */
  private static Set<Square> calculatePawnAttacksFrom(Set<Square> pawnSquares, Side side) {
    final Set<Square> attacks = Nulls.noneOf(Square.class);
    for (final Square square : pawnSquares) {
      if (Square.calculateHasLeftDiagonalSquare(side, square)) {
        attacks.add(Square.calculateLeftDiagonalSquare(side, square));
      }
      if (Square.calculateHasRightDiagonalSquare(side, square)) {
        attacks.add(Square.calculateRightDiagonalSquare(side, square));
      }
    }
    return attacks;
  }

  /**
   * Returns the mutable set of squares currently occupied by pawns of {@code side}.
   */
  private static Set<Square> collectOpposingPawnSquares(StaticPosition position, Side side) {
    final Set<Square> pawns = Nulls.noneOf(Square.class);
    for (final Square square : Square.REAL) {
      if (position.isOwnPawn(square, side)) {
        pawns.add(square);
      }
    }
    return pawns;
  }

  /**
   * Returns the up-to-eight king-move neighbours of {@code square} (all squares within one file and one rank).
   */
  private static EnumSet<Square> kingNeighbours(Square square) {
    final EnumSet<Square> neighbours = Nulls.noneOf(Square.class);
    final var fileNumber = square.getFile().getNumber();
    final var rankNumber = square.getRank().getNumber();
    for (var df = -1; df <= 1; df++) {
      for (var dr = -1; dr <= 1; dr++) {
        if (df == 0 && dr == 0) {
          continue;
        }
        final var newFile = fileNumber + df;
        final var newRank = rankNumber + dr;
        if (newFile < 1 || newFile > 8 || newRank < 1 || newRank > 8) {
          continue;
        }
        neighbours.add(Square.calculate(newFile, newRank));
      }
    }
    return neighbours;
  }

}
