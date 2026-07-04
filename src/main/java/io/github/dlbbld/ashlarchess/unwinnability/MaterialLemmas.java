// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.enums.Side;

/**
 * Material predicates used by the Find-Helpmate search and the quick analysis: the paper's Lemmas 5/6 and the Figure 12
 * Score material condition. Pure counting over the per-piece bitboards, derived from the paper
 * ({@code fun22-spec.pdf}); the same proven lemmas the retired cha-port material predicate encoded.
 *
 * <ul>
 * <li><b>Lemma 5</b>: a pawn-free position is unwinnable for a player with just a knight if the opponent has no
 * knights, bishops or rooks.</li>
 * <li><b>Lemma 6</b>: a pawn-free position is unwinnable for a player with just bishops of one square colour if the
 * opponent has no knights and no bishops of the opposite square colour.</li>
 * </ul>
 */
final class MaterialLemmas {

  private MaterialLemmas() {
  }

  /** Per-type counts for one side (kings excluded; every position has exactly one per side). */
  private record SideMaterial(int pawns, int knights, int lightBishops, int darkBishops, int rooks, int queens) {

    int bishops() {
      return lightBishops + darkBishops;
    }

    boolean bareKing() {
      return pawns == 0 && knights == 0 && bishops() == 0 && rooks == 0 && queens == 0;
    }
  }

  private static SideMaterial materialOf(BitboardPosition position, Side side) {
    return side == Side.WHITE
        ? material(position.whitePawns(), position.whiteKnights(), position.whiteBishops(), position.whiteRooks(),
            position.whiteQueens())
        : material(position.blackPawns(), position.blackKnights(), position.blackBishops(), position.blackRooks(),
            position.blackQueens());
  }

  private static SideMaterial material(long pawns, long knights, long bishops, long rooks, long queens) {
    return new SideMaterial(Long.bitCount(pawns), Long.bitCount(knights),
        Long.bitCount(bishops & SquareGeometry.LIGHT_SQUARES), Long.bitCount(bishops & SquareGeometry.DARK_SQUARES),
        Long.bitCount(rooks), Long.bitCount(queens));
  }

  /** The winner has only its king. */
  static boolean winnerHasBareKing(BitboardPosition position, Side winner) {
    return materialOf(position, winner).bareKing();
  }

  /** Lemma 5 or Lemma 6 applies (both require a pawn-free position). */
  static boolean unwinnableByLemma5Or6(BitboardPosition position, Side winner) {
    if ((position.whitePawns() | position.blackPawns()) != 0L) {
      return false; // not pawn-free
    }
    final SideMaterial winnerMaterial = materialOf(position, winner);
    final SideMaterial loserMaterial = materialOf(position, winner.getOppositeSide());
    return lemma5(winnerMaterial, loserMaterial) || bishopCase(winnerMaterial, loserMaterial);
  }

  /**
   * The Figure 12 Score material condition: the Lemma 5/6 shapes ignoring the pawn-freeness requirement (the loser may
   * have pawns).
   */
  static boolean scoreMaterialCondition(BitboardPosition position, Side winner) {
    final SideMaterial winnerMaterial = materialOf(position, winner);
    final SideMaterial loserMaterial = materialOf(position, winner.getOppositeSide());
    return lemma5(winnerMaterial, loserMaterial) || bishopCase(winnerMaterial, loserMaterial);
  }

  /** The winner has just a knight; the loser has no knights, bishops or rooks (queens do not enable a mate). */
  private static boolean lemma5(SideMaterial winner, SideMaterial loser) {
    return winnerJustKnight(winner) && loser.knights() == 0 && loser.bishops() == 0 && loser.rooks() == 0;
  }

  private static boolean winnerJustKnight(SideMaterial winner) {
    return winner.knights() == 1 && winner.pawns() == 0 && winner.bishops() == 0 && winner.rooks() == 0
        && winner.queens() == 0;
  }

  /**
   * Lemma 6 shape: the winner has only same-square-colour bishops; the loser has no knights and no opposite-colour
   * bishops.
   */
  private static boolean bishopCase(SideMaterial winner, SideMaterial loser) {
    final boolean winnerJustBishops = winner.bishops() >= 1 && winner.knights() == 0 && winner.pawns() == 0
        && winner.rooks() == 0 && winner.queens() == 0;
    if (!winnerJustBishops) {
      return false;
    }
    final boolean winnerOnLight = winner.lightBishops() > 0;
    final boolean winnerOnDark = winner.darkBishops() > 0;
    if (winnerOnLight && winnerOnDark) {
      return false; // bishops on both colours
    }
    final int loserOppositeColourBishops = winnerOnLight ? loser.darkBishops() : loser.lightBishops();
    return loser.knights() == 0 && loserOppositeColourBishops == 0;
  }
}
