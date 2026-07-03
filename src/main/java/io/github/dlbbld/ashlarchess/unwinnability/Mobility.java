// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;

// Figure 7 Algorithm for over-approximating the mobility of all pieces in a given position. The
// output solution is admissible: M >= M* where M* is the true mobility (Lemma 8 / Corollary 9).
/**
 * The mobility over-approximation - the Figure 7 fixpoint over the Figure 6 implications (see
 * {@code fun22-spec.md} section 2). Given a position it returns, for every piece {@code P} and square {@code s},
 * whether {@code P} can <em>eventually</em> move to {@code s} (variable {@code M[P][s]}). The result is
 * <em>admissible</em>: it never reports a square unreachable that is in fact reachable, which is what makes the
 * composed semi-static check (Theorem 12) sound.
 */
final class Mobility {

  private Mobility() {
  }

  /**
   * Runs the fixpoint and returns the mobility solution for {@code position}.
   */
  static MobilitySolution mobility(SemiStaticPosition position) {
    final int totalPieces = position.count();

    // m[i] = region bitboard of piece i (M); cleared[i] = clearance (C); reachable[side] = squares a non-king piece
    // of that side can reach or currently occupies (R).
    final long[] m = new long[totalPieces];
    final boolean[] cleared = new boolean[totalPieces];
    final long[] reachable = new long[2];

    for (int i = 0; i < totalPieces; i++) {
      m[i] = 1L << position.piece(i).square(); // a piece can "move" to its own square (Figure 7 step 2)
    }

    boolean changed = true;
    while (changed) {
      changed = false;

      // Reachability rule: R[s][c] holds when some non-king piece of side c can reach s.
      long reachableWhite = 0L;
      long reachableBlack = 0L;
      for (int i = 0; i < totalPieces; i++) {
        final SemiStaticPiece piece = position.piece(i);
        if (piece.pieceType() == PieceType.KING) {
          continue;
        }
        if (piece.side() == Side.WHITE) {
          reachableWhite |= m[i];
        } else {
          reachableBlack |= m[i];
        }
      }
      if (reachableWhite != reachable[0]) {
        reachable[0] = reachableWhite;
        changed = true;
      }
      if (reachableBlack != reachable[1]) {
        reachable[1] = reachableBlack;
        changed = true;
      }

      // Clearance rule: a piece can be cleared if it can move away or an enemy can reach (capture on) its square.
      for (int i = 0; i < totalPieces; i++) {
        if (cleared[i]) {
          continue;
        }
        final SemiStaticPiece piece = position.piece(i);
        final long own = 1L << piece.square();
        boolean isClearable = (m[i] & ~own) != 0L;
        if (!isClearable) {
          for (int j = 0; j < totalPieces; j++) {
            if (position.piece(j).side() != piece.side() && (m[j] & own) != 0L) {
              isClearable = true;
              break;
            }
          }
        }
        if (isClearable) {
          cleared[i] = true;
          changed = true;
        }
      }

      // Mobility rules: grow each piece's region by every square whose Figure 6 rule bodies now all hold.
      for (int i = 0; i < totalPieces; i++) {
        final SemiStaticPiece piece = position.piece(i);
        final int self = piece.square();
        long region = m[i];
        for (int s = 0; s < SquareGeometry.SQUARES; s++) {
          if (s == self || ((region >> s) & 1L) != 0L) {
            continue;
          }
          if (canReach(position, m, cleared, reachable, i, s)) {
            region |= 1L << s;
          }
        }
        if (region != m[i]) {
          m[i] = region;
          changed = true;
        }
      }
    }

    return new MobilitySolution(position, m);
  }

  /**
   * All Figure 6 rule bodies with head {@code M[i][s]} hold on the current state.
   */
  private static boolean canReach(SemiStaticPosition position, long[] m, boolean[] cleared, long[] reachable, int i,
      int s) {
    final SemiStaticPiece piece = position.piece(i);

    final boolean base;
    if (piece.pieceType() == PieceType.PAWN) {
      base = pawnCanReach(position, m, cleared, reachable, i, s);
    } else if (piece.pieceType() == PieceType.KING) {
      base = (Predecessors.moves(piece.pieceType(), piece.side(), s) & m[i]) != 0L
          && kingAttackersClearable(position, cleared, i, s);
    } else {
      base = (Predecessors.moves(piece.pieceType(), piece.side(), s) & m[i]) != 0L; // move rule
    }
    if (!base) {
      return false;
    }

    // Not self-capture: to land on a square held by a same-side piece, that piece must be clearable.
    final int occupantIndex = position.indexAt(s);
    if (occupantIndex >= 0 && position.piece(occupantIndex).side() == piece.side()) {
      return cleared[occupantIndex];
    }
    return true;
  }

  /** Pawn move rule: push (enemy on target clearable) or capture (enemy reaches s) or promotion. */
  private static boolean pawnCanReach(SemiStaticPosition position, long[] m, boolean[] cleared, long[] reachable,
      int i, int s) {
    final SemiStaticPiece pawn = position.piece(i);
    final Side side = pawn.side();

    // Push.
    if ((SquareGeometry.pawnPushPredecessors(side, s) & m[i]) != 0L) {
      final int occupantIndex = position.indexAt(s);
      if (occupantIndex < 0 || position.piece(occupantIndex).side() == side) {
        return true; // no enemy on target (an own-side occupant is handled by not-self-capture)
      }
      if (cleared[occupantIndex]) {
        return true; // enemy on target is clearable (the F factor)
      }
    }
    // Capture: reach a capture-predecessor and an enemy non-king piece can reach s.
    final int enemyIndex = side == Side.WHITE ? 1 : 0;
    if ((SquareGeometry.pawnAttackPredecessors(side, s) & m[i]) != 0L && ((reachable[enemyIndex] >> s) & 1L) != 0L) {
      return true;
    }
    // Promotion: having reached any promotion square, the pawn may go everywhere.
    return (SquareGeometry.promotion(side) & m[i]) != 0L;
  }

  /**
   * King-attackers rule: every enemy piece currently attacking {@code s} must be clearable.
   */
  private static boolean kingAttackersClearable(SemiStaticPosition position, boolean[] cleared, int kingIndex,
      int s) {
    final Side kingSide = position.piece(kingIndex).side();
    for (int j = 0; j < position.count(); j++) {
      final SemiStaticPiece enemy = position.piece(j);
      if (enemy.side() == kingSide) {
        continue;
      }
      // The enemy attacks s iff its square is a capture-predecessor of s for its piece type.
      if ((Predecessors.captures(enemy.pieceType(), enemy.side(), s) & (1L << enemy.square())) != 0L && !cleared[j]) {
        return false;
      }
    }
    return true;
  }
}
