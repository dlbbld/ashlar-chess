// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability.internal;

import java.util.List;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.LegalMove;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Side;

/**
 * Finite-state basic-helpmate-existence theorem for elementary-material positions. It serves two roles, and in neither
 * does it touch the production unwinnability analyzer, which since 22.0.0 follows the FUN 2022 paper's Figure 9 exactly
 * (semi-static shortcut + Find-Helpmate search) and takes no theorem shortcut:
 * <ol>
 * <li><b>Adjudication pre-check.</b> {@link #decideForAdjudication(Board, Side)} lets the {@code Adjudicator} settle a
 * flag-fall or resignation on a covered elementary-material position instantly, without running the search - "we
 * adjudicate with the FUN 2022 analyzer, and also with this proven theorem". It is restricted to the classes where the
 * theorem is sound on any strictly FEN-legal input (see the legality note).</li>
 * <li><b>Test oracle.</b> {@link #decide(Board, Side)} is the pure theorem statement over every covered class; the
 * agreement tests check that the paper engine's verdicts match it on the curated elementary-material corpus (in
 * particular that the engine answers {@code UNWINNABLE} wherever the theorem proves unwinnability).</li>
 * </ol>
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
 * FIDE dead-position and timeout rules this serves. The enumeration found a small number of retro-illegal local states
 * (for example the KBNvK position {@code 8/8/8/8/2N5/8/k1K5/1B6 b}) where the winnable conclusion would be wrong; such
 * positions cannot arise in a game and a strictly legal root cannot reach them, but only retrograde analysis can tell
 * them apart and {@code Board.fromFenStrict} accepts them. Every such counterexample lives in one of two classes -
 * KBBvK opposite bishops and KBNvK - so for adjudication (which cannot assume a game-reachable root)
 * {@link #decideForAdjudication(Board, Side)} excludes exactly those two classes and trusts the theorem on the rest.
 * The forced-capture (unwinnable) direction is a pure material-reduction argument and is sound on any input.
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
   * better handled by the regular analysis. This is the pure theorem statement over every covered class; for a verdict
   * safe to trust on any strictly FEN-legal board (as an adjudication pre-check) use
   * {@link #decideForAdjudication(Board, Side)}.
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

  /**
   * The theorem verdict restricted to the material classes where it is sound on <em>any</em> strictly FEN-legal
   * position, for use as an adjudication pre-check. Returns {@code NOT_APPLICABLE} both when {@link #decide} does and
   * for the two classes that carry known retro-illegal counterexamples - KBBvK opposite bishops and KBNvK - where a
   * position that passes strict FEN parsing but cannot arise in a game may be reported {@code WINNABLE} although no
   * helpmate exists. ashlar does not enforce retrograde legality, so the caller must fall back to the full search on
   * those two classes; every other covered class is counterexample-free, so its {@code WINNABLE} / {@code UNWINNABLE}
   * verdict may be trusted directly.
   */
  public static BasicHelpmateExistenceTheoremResult decideForAdjudication(Board board, Side winner) {
    if (isCounterexampleBearingClass(board.getBitboardPosition(), winner)) {
      return BasicHelpmateExistenceTheoremResult.NOT_APPLICABLE;
    }
    return decide(board, winner);
  }

  // KBBvK (opposite bishops) and KBNvK against a bare defender king: the only two covered classes with retro-illegal
  // positions that pass strict FEN parsing yet violate the theorem's WINNABLE conclusion (enumerated exhaustively in
  // the theorem project's README, "Illegal positions not satisfying the conclusion"). The adjudicator, which cannot
  // assume a game-reachable root, must defer these to the search.
  private static boolean isCounterexampleBearingClass(BitboardPosition position, Side winner) {
    final Side defender = winner.getOppositeSide();
    return hasKingOnly(defender, position)
        && (isKingAndOppositeBishopsOnly(winner, position) || isKingBishopKnightOnly(winner, position));
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
    final long sideBishops = bishops(side, position);
    return count(sideBishops) == 2 && onOppositeColours(sideBishops) && count(rooks(side, position)) == 0
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

  // Two bishops stand on opposite colours iff their squares have different colour parity. The caller guarantees
  // exactly two bishops before calling, so the lowest and second-lowest set bits are the two bishop squares.
  private static boolean onOppositeColours(long twoBishops) {
    final int firstSquare = Long.numberOfTrailingZeros(twoBishops);
    final int secondSquare = Long.numberOfTrailingZeros(twoBishops & (twoBishops - 1));
    return squareColour(firstSquare) != squareColour(secondSquare);
  }

  // 0 for one colour, 1 for the other: the parity of file + rank, with file = square & 7 and rank = square >> 3.
  private static int squareColour(int square) {
    return ((square & 7) + (square >> 3)) & 1;
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
