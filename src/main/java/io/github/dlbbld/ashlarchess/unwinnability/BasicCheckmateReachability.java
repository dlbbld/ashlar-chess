// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import java.util.List;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.model.LegalMove;

/**
 * Finite-state basic-checkmate-reachability theorem, used as a sound shortcut inside the complete unwinnability
 * analysis so it does not have to rediscover a long cooperative mating line for elementary material.
 *
 * <p>
 * The theorem was proved by exhaustive retrograde enumeration of the local legal state graph for each covered material
 * class in the sibling project basic-helpmate-existence (https://github.com/dlbbld/basic-helpmate-existence, tag
 * 1.0.0). Let {@code W} be the side holding the mating material (the intended winner here) and {@code L} the defender.
 * For every ongoing legal position in a covered class:
 * <ol>
 * <li>If {@code W} is to move, {@code W} has a helpmate (winnable for {@code W}).</li>
 * <li>If {@code L} is to move, {@code W} has a helpmate unless {@code L} has one or two legal moves and every legal
 * first move captures one of {@code W}'s pieces. In that exceptional case {@code L} is forced to destroy {@code W}'s
 * mating material, leaving {@code W} unable to checkmate, so the position is unwinnable for {@code W}.</li>
 * </ol>
 *
 * <p>
 * Covered classes (with {@code W} holding the mating material): KRvK, KQvK, KBBvK with opposite-coloured bishops,
 * KBNvK, KRvKB, and KRvKN, plus the colour-reversed statements by symmetry. The decision matches the operational
 * recipe in section 10 of the project's exposition.
 *
 * <p>
 * <b>Legality assumption.</b> The theorem holds for strictly game-legal positions, which is exactly the domain of the
 * FIDE dead-position and timeout rules this analyzer serves. The enumeration found a small number of retro-illegal
 * local states (for example the KBNvK position {@code 8/8/8/8/2N5/8/k1K5/1B6 b}) where the winnable conclusion would
 * be wrong; such positions cannot arise in a game and a strictly legal root cannot reach them, so they are outside the
 * intended input domain. The forced-capture (unwinnable) direction is a pure material-reduction argument and is sound
 * on any input.
 *
 * <p>
 * <b>No witness line.</b> A winnable decision here is certified by the theorem, not by an explicit mating sequence, so
 * callers receive a {@code WINNABLE_BY_THEOREM} verdict without a move line. This is intentional: the line is not needed
 * for the dead-position verdict and would otherwise require the very search this shortcut avoids.
 */
abstract class BasicCheckmateReachability {

  private BasicCheckmateReachability() {
  }

  /**
   * Decides the complete unwinnability verdict from the theorem, or returns {@code NOT_APPLICABLE} when the position is
   * not in a covered class with {@code winner} as the mating side, or is a terminal (checkmate/stalemate) position
   * better handled by the regular analysis.
   */
  static BasicCheckmateReachabilityResult decide(Board board, Side winner) {
    final BitboardPosition position = board.getBitboardPosition();
    if (!isCoveredClass(position, winner)) {
      return BasicCheckmateReachabilityResult.NOT_APPLICABLE;
    }

    if (board.getHavingMove() == winner) {
      // W to move: W has a helpmate.
      return BasicCheckmateReachabilityResult.WINNABLE;
    }

    // L to move. Leave genuine terminals to the regular path.
    if (board.isCheckmate() || board.isStalemate()) {
      return BasicCheckmateReachabilityResult.NOT_APPLICABLE;
    }

    final List<LegalMove> legalMoves = board.getLegalMoves();

    // With three or more legal moves the forced-capture exception cannot apply: W has a helpmate.
    if (legalMoves.size() >= 3) {
      return BasicCheckmateReachabilityResult.WINNABLE;
    }

    // One or two legal moves: winnable unless every legal first move captures one of W's pieces.
    for (final LegalMove legalMove : legalMoves) {
      final Piece capturedPiece = legalMove.pieceCaptured();
      if (capturedPiece == Piece.NONE || capturedPiece.getSide() != winner) {
        // This move preserves W's mating material, so W has a helpmate.
        return BasicCheckmateReachabilityResult.WINNABLE;
      }
    }

    // Every legal first move captures W's mating material: the reduced position is insufficient for W.
    return BasicCheckmateReachabilityResult.UNWINNABLE;
  }

  private static boolean isCoveredClass(BitboardPosition position, Side winner) {
    final Side defender = winner.getOppositeSide();

    // Classes where the defender is reduced to a bare king.
    if (UnwinnabilityMaterialBitboard.calculateHasKingOnly(defender, position)) {
      return isKingAndRookOnly(winner, position) || isKingAndQueenOnly(winner, position)
          || isKingAndOppositeBishopsOnly(winner, position) || isKingBishopKnightOnly(winner, position);
    }

    // Rook classes where the defender keeps a single minor piece.
    if (isKingAndRookOnly(winner, position)) {
      return isKingAndSingleBishopOnly(defender, position) || isKingAndSingleKnightOnly(defender, position);
    }

    return false;
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
    return count(bishops(side, position)) == 2
        && UnwinnabilityMaterialBitboard.calculateHasLightSquareBishops(side, position)
        && UnwinnabilityMaterialBitboard.calculateHasDarkSquareBishops(side, position)
        && count(rooks(side, position)) == 0 && count(queens(side, position)) == 0
        && count(knights(side, position)) == 0 && count(pawns(side, position)) == 0;
  }

  private static boolean isKingBishopKnightOnly(Side side, BitboardPosition position) {
    return count(bishops(side, position)) == 1 && count(knights(side, position)) == 1
        && count(rooks(side, position)) == 0 && count(queens(side, position)) == 0
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
