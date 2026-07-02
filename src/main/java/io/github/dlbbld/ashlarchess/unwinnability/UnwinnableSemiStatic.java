// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.exceptions.ProgrammingMistakeException;

// Figure 8 Semi-static algorithm for deciding unwinnability. Together with the Figure 7 mobility
// over-approximation this is Theorem 12: a true return value proves the position unwinnable for
// the intended winner.
/**
 * The semi-static unwinnability check - the paper's {@code Unwinnable_static} (Figure 8; {@code fun22-spec.md}
 * section 3). Given a position, an intended winner {@code c}, and an admissible mobility solution, it returns
 * {@code true} only when it can <em>prove</em> the position unwinnable for {@code c}. It is sound but deliberately
 * incomplete (may return {@code false} on a truly unwinnable position).
 */
final class UnwinnableSemiStatic {

  private UnwinnableSemiStatic() {
  }

  /**
   * @return {@code true} iff the position is proved unwinnable for {@code winner}
   */
  static boolean unwinnableSemiStatic(SemiStaticPosition position, Side winner, MobilitySolution mobilitySolution) {
    if (mobilitySolution.position() != position) {
      throw new ProgrammingMistakeException("Mobility solution was computed for a different position");
    }
    // Step 1: the mobility soundness lemma (Lemma 8) assumes no castling rights and no possible en passant.
    if (position.enPassantPossible() || position.castlingRightsPresent()) {
      return false;
    }

    final int totalPieces = position.count();
    final Side loser = winner.getOppositeSide();
    final long loserKingRegion = mobilitySolution.region(position.kingIndex(loser));

    // Steps 4-6: intruders are the winner's pieces that can enter the loser king's region; they must be empty or all
    // same-square-coloured bishops.
    boolean sawIntruder = false;
    int intruderSquareColor = -1;
    for (int i = 0; i < totalPieces; i++) {
      final SemiStaticPiece piece = position.piece(i);
      if (piece.side() != winner || (mobilitySolution.region(i) & loserKingRegion) == 0L) {
        continue;
      }
      if (piece.pieceType() != PieceType.BISHOP) {
        return false; // step 5: a non-bishop intruder
      }
      final int squareColor = squareColor(piece.square());
      if (!sawIntruder) {
        sawIntruder = true;
        intruderSquareColor = squareColor;
      } else if (squareColor != intruderSquareColor) {
        return false; // step 6: bishops of opposite square colours
      }
    }

    // Step 7: att-region for the winner's pieces (blockers use region; only the winner's att-region is needed, for
    // assistants and the step-10 attacker).
    final long[] attackRegions = new long[totalPieces];
    for (int i = 0; i < totalPieces; i++) {
      final SemiStaticPiece piece = position.piece(i);
      if (piece.side() == winner) {
        attackRegions[i] = attackRegion(piece, mobilitySolution.region(i));
      }
    }

    // Step 10: is there a candidate mate square for the loser's king? Steps 8-10 range over alpha(s) - the squares
    // SHARING A BORDER with s (the paper's share-a-border glyph, verified on the 500-dpi render), NOT all 8
    // neighbours delta(s). Figure 8 footnote a says a "more complete check" would look at all neighbours, and the
    // Lemma 11 footnote's at-most-one-square-per-assistant argument holds specifically for alpha(s) (opposite square
    // colour to s, so the same-coloured bishop intruders can never attack them).
    for (int s = 0; s < SquareGeometry.SQUARES; s++) {
      if (((loserKingRegion >> s) & 1L) == 0L) {
        continue;
      }
      final long alpha = SquareGeometry.alpha(s);
      final int alphaCount = Long.bitCount(alpha);

      int coverers = 0; // |blockers(s)| + |assistants(s)|
      boolean winnerAttacksS = false;
      for (int i = 0; i < totalPieces; i++) {
        final SemiStaticPiece piece = position.piece(i);
        if (piece.side() != winner) {
          // Blocker (step 8): a loser non-king piece that can occupy a bordering square.
          if (piece.pieceType() != PieceType.KING && (mobilitySolution.region(i) & alpha) != 0L) {
            coverers++;
          }
        } else {
          // Assistant (step 9): a winner piece that can attack a bordering square.
          if ((attackRegions[i] & alpha) != 0L) {
            coverers++;
          }
          if (((attackRegions[i] >> s) & 1L) != 0L) {
            winnerAttacksS = true;
          }
        }
      }
      if (winnerAttacksS && coverers >= alphaCount) {
        return false; // s looks matable: reachable, attacked, and all alpha-escapes coverable
      }
    }

    return true; // no candidate mate square anywhere: unwinnable for the intended winner
  }

  /** att-region(P) = the squares s with pred-captP(s) intersecting region(P). */
  private static long attackRegion(SemiStaticPiece piece, long region) {
    long result = 0L;
    for (int s = 0; s < SquareGeometry.SQUARES; s++) {
      if ((Predecessors.captures(piece.pieceType(), piece.side(), s) & region) != 0L) {
        result |= 1L << s;
      }
    }
    return result;
  }

  /** A 2-colouring of the board (light/dark); only equality is used, for step 6. */
  private static int squareColor(int square) {
    return ((square & 7) + (square >> 3)) & 1;
  }
}
