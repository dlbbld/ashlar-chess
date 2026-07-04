// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import java.util.List;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.LegalMove;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Side;

/**
 * Finite-state basic-helpmate-existence theorem. Since 22.0.0 this is a <b>test-side oracle only</b>: the production
 * analyzer follows the FUN 2022 paper's Figure 9 exactly (semi-static shortcut + Find-Helpmate search) and no longer
 * takes a theorem shortcut; the theorem instead certifies, in the test suite, that the paper engine's verdicts agree
 * with the proven theorem on every covered elementary-material position (in particular that the engine answers
 * {@code UNWINNABLE} wherever the theorem proves unwinnability).
 *
 * <p>
 * The theorem was proved by exhaustive retrograde enumeration of the local legal state graph for each covered material
 * class in the sibling project basic-helpmate-existence (https://github.com/dlbbld/basic-helpmate-existence, tag
 * 1.2.0). Let {@code W} be the side holding the mating material (the intended winner here) and {@code L} the defender.
 *
 * <p>
 * <b>Main theorem.</b> For every ongoing legal position in a covered class:
 * <ol>
 * <li>If {@code W} is to move, {@code W} has a helpmate (winnable for {@code W}).</li>
 * <li>If {@code L} is to move, {@code W} has a helpmate unless every legal first move captures one of {@code W}'s
 * pieces. In that exceptional case {@code L} is forced to destroy {@code W}'s mating material, leaving {@code W} unable
 * to checkmate, so the position is unwinnable for {@code W}.</li>
 * </ol>
 * Covered classes (with {@code W} holding the mating material): KRvK, KQvK, KBBvK with opposite-coloured bishops,
 * KBNvK, KNNvK, KRvKB, and KRvKN, plus the colour-reversed statements by symmetry.
 *
 * <p>
 * <b>Supplementary two-major classes.</b> KRRvK and KQQvK are not part of the main theorem but are covered by a
 * separate finite-state computation: in every ongoing legal KRRvK or KQQvK position {@code W} has a helpmate,
 * regardless of the side to move and with no forced-capture exception - a forced first capture of one major piece only
 * reduces the position to KRvK or KQvK, which still suffices to mate.
 *
 * <p>
 * <b>Legality assumption.</b> The theorem holds for strictly game-legal positions, which is exactly the domain of the
 * FIDE dead-position and timeout rules this analyzer serves. The enumeration found a small number of retro-illegal
 * local states (for example the KBNvK position {@code 8/8/8/8/2N5/8/k1K5/1B6 b}) where the winnable conclusion would be
 * wrong; such positions cannot arise in a game and a strictly legal root cannot reach them, so they are outside the
 * intended input domain. The forced-capture (unwinnable) direction is a pure material-reduction argument and is sound
 * on any input.
 *
 * <p>
 * <b>No witness line.</b> A winnable decision here is certified by the theorem, not by an explicit mating sequence -
 * which is exactly what makes it a useful independent oracle: the agreement tests check that the search-based engine
 * exhibits a concrete helpmate wherever the theorem guarantees one exists.
 */
public final class BasicHelpmateExistenceTheorem {

  private BasicHelpmateExistenceTheorem() {
  }

  /**
   * Decides the complete unwinnability verdict from the theorem, or returns {@code NOT_APPLICABLE} when the position is
   * not in a covered class with {@code winner} as the mating side, or is a terminal (checkmate/stalemate) position
   * better handled by the regular analysis.
   */
  public static BasicHelpmateExistenceTheoremResult decide(Board board, Side winner) {
    final BitboardPosition position = board.getBitboardPosition();
    final boolean isTwoMajorClass = isTwoMajorWinnableClass(position, winner);
    if (!isTwoMajorClass && !isMainTheoremClass(position, winner)) {
      return BasicHelpmateExistenceTheoremResult.NOT_APPLICABLE;
    }

    if (board.getSideToMove() == winner) {
      // W to move: W has a helpmate.
      return BasicHelpmateExistenceTheoremResult.WINNABLE;
    }

    // L to move. Leave genuine terminals to the regular path.
    if (board.isCheckmate() || board.isStalemate()) {
      return BasicHelpmateExistenceTheoremResult.NOT_APPLICABLE;
    }

    if (isTwoMajorClass) {
      // KRRvK / KQQvK: winnable for every ongoing position - a forced first capture only reduces to KRvK or KQvK,
      // which still suffices, so the forced-capture exception never applies.
      return BasicHelpmateExistenceTheoremResult.WINNABLE;
    }

    final List<LegalMove> legalMoves = board.getLegalMoves();

    // With three or more legal moves the forced-capture exception cannot apply: W has a helpmate.
    if (legalMoves.size() >= 3) {
      return BasicHelpmateExistenceTheoremResult.WINNABLE;
    }

    // One or two legal moves: winnable unless every legal first move captures one of W's pieces.
    for (final LegalMove legalMove : legalMoves) {
      final Piece capturedPiece = legalMove.capturedPiece();
      if (capturedPiece == Piece.NONE || capturedPiece.getSide() != winner) {
        // This move preserves W's mating material, so W has a helpmate.
        return BasicHelpmateExistenceTheoremResult.WINNABLE;
      }
    }

    // Every legal first move captures W's mating material: the reduced position is insufficient for W.
    return BasicHelpmateExistenceTheoremResult.UNWINNABLE;
  }

  private static boolean isMainTheoremClass(BitboardPosition position, Side winner) {
    final Side defender = winner.getOppositeSide();

    // Classes where the defender is reduced to a bare king.
    if (hasKingOnly(defender, position)) {
      return isKingAndRookOnly(winner, position) || isKingAndQueenOnly(winner, position)
          || isKingAndOppositeBishopsOnly(winner, position) || isKingBishopKnightOnly(winner, position)
          || isKingAndTwoKnightsOnly(winner, position);
    }

    // Rook classes where the defender keeps a single minor piece.
    if (isKingAndRookOnly(winner, position)) {
      return isKingAndSingleBishopOnly(defender, position) || isKingAndSingleKnightOnly(defender, position);
    }

    return false;
  }

  // Supplementary two-major classes (KRRvK, KQQvK): every ongoing legal position is winnable for the major side
  // regardless of side to move, even when the defender is forced to capture one major piece on the first move (the
  // remaining KRvK or KQvK still suffices). Proved by a separate finite-state computation, not the main theorem.
  private static boolean isTwoMajorWinnableClass(BitboardPosition position, Side winner) {
    final Side defender = winner.getOppositeSide();
    return hasKingOnly(defender, position)
        && (isKingAndTwoRooksOnly(winner, position) || isKingAndTwoQueensOnly(winner, position));
  }

  private static boolean isKingAndRookOnly(Side side, BitboardPosition position) {
    return count(rooks(side, position)) == 1 && count(queens(side, position)) == 0
        && count(bishops(side, position)) == 0 && count(knights(side, position)) == 0
        && count(pawns(side, position)) == 0;
  }

  private static boolean isKingAndQueenOnly(Side side, BitboardPosition position) {
    return count(queens(side, position)) == 1 && count(rooks(side, position)) == 0
        && count(bishops(side, position)) == 0 && count(knights(side, position)) == 0
        && count(pawns(side, position)) == 0;
  }

  private static boolean isKingAndOppositeBishopsOnly(Side side, BitboardPosition position) {
    return count(bishops(side, position)) == 2 && (bishops(side, position) & SquareGeometry.LIGHT_SQUARES) != 0L
        && (bishops(side, position) & SquareGeometry.DARK_SQUARES) != 0L && count(rooks(side, position)) == 0
        && count(queens(side, position)) == 0 && count(knights(side, position)) == 0
        && count(pawns(side, position)) == 0;
  }

  private static boolean isKingBishopKnightOnly(Side side, BitboardPosition position) {
    return count(bishops(side, position)) == 1 && count(knights(side, position)) == 1
        && count(rooks(side, position)) == 0 && count(queens(side, position)) == 0 && count(pawns(side, position)) == 0;
  }

  private static boolean isKingAndTwoKnightsOnly(Side side, BitboardPosition position) {
    return count(knights(side, position)) == 2 && count(rooks(side, position)) == 0
        && count(queens(side, position)) == 0 && count(bishops(side, position)) == 0
        && count(pawns(side, position)) == 0;
  }

  private static boolean isKingAndTwoRooksOnly(Side side, BitboardPosition position) {
    return count(rooks(side, position)) == 2 && count(queens(side, position)) == 0
        && count(bishops(side, position)) == 0 && count(knights(side, position)) == 0
        && count(pawns(side, position)) == 0;
  }

  private static boolean isKingAndTwoQueensOnly(Side side, BitboardPosition position) {
    return count(queens(side, position)) == 2 && count(rooks(side, position)) == 0
        && count(bishops(side, position)) == 0 && count(knights(side, position)) == 0
        && count(pawns(side, position)) == 0;
  }

  private static boolean isKingAndSingleBishopOnly(Side side, BitboardPosition position) {
    return count(bishops(side, position)) == 1 && count(rooks(side, position)) == 0
        && count(queens(side, position)) == 0 && count(knights(side, position)) == 0
        && count(pawns(side, position)) == 0;
  }

  private static boolean isKingAndSingleKnightOnly(Side side, BitboardPosition position) {
    return count(knights(side, position)) == 1 && count(rooks(side, position)) == 0
        && count(queens(side, position)) == 0 && count(bishops(side, position)) == 0
        && count(pawns(side, position)) == 0;
  }

  private static boolean hasKingOnly(Side side, BitboardPosition position) {
    final long sideOccupancy = position.occupied(side);
    final long sideKings = side == Side.WHITE ? position.whiteKings() : position.blackKings();
    return sideOccupancy == sideKings && Long.bitCount(sideKings) == 1;
  }

  private static int count(long bitboard) {
    return Long.bitCount(bitboard);
  }

  private static long rooks(Side side, BitboardPosition position) {
    return side == Side.WHITE ? position.whiteRooks() : position.blackRooks();
  }

  private static long queens(Side side, BitboardPosition position) {
    return side == Side.WHITE ? position.whiteQueens() : position.blackQueens();
  }

  private static long bishops(Side side, BitboardPosition position) {
    return side == Side.WHITE ? position.whiteBishops() : position.blackBishops();
  }

  private static long knights(Side side, BitboardPosition position) {
    return side == Side.WHITE ? position.whiteKnights() : position.blackKnights();
  }

  private static long pawns(Side side, BitboardPosition position) {
    return side == Side.WHITE ? position.whitePawns() : position.blackPawns();
  }
}
