// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board.enums.internal;

import io.github.dlbbld.ashlarchess.board.enums.Rank;
import io.github.dlbbld.ashlarchess.board.enums.Side;

/**
 * Side-relative chess-rule ranks. These combine a {@link Rank} (or a {@link Side}) with the laws of chess to compute
 * rule-significant ranks - ground rank, promotion rank, pawn initial / two-square-advance rank, en-passant capture
 * rank, and per-side rank validity. They are kept off the {@link Rank} enum, which carries only its number identity and
 * intrinsic single-step neighbour geometry.
 */
public final class RankUtility {

  private RankUtility() {
  }

  public static Rank calculateGroundRank(Side side) {
    return switch (side) {
      case WHITE -> Rank.RANK_1;
      case BLACK -> Rank.RANK_8;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static boolean isPromotionRank(Side side, Rank rank) {
    if (side == Side.NONE || rank == Rank.NONE) {
      throw new IllegalArgumentException();
    }

    return rank == calculatePromotionRank(side);
  }

  public static Rank calculatePromotionRank(Side side) {
    return switch (side) {
      case WHITE -> Rank.RANK_8;
      case BLACK -> Rank.RANK_1;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static Rank calculatePawnInitialRank(Side side) {
    return switch (side) {
      case BLACK -> Rank.RANK_7;
      case WHITE -> Rank.RANK_2;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static boolean isPawnTwoSquareAdvanceRank(Side side, Rank rank) {
    if (side == Side.NONE || rank == Rank.NONE) {
      throw new IllegalArgumentException();
    }

    return rank == calculatePawnTwoSquareAdvanceRank(side);
  }

  public static Rank calculatePawnTwoSquareAdvanceRank(Side side) {
    return switch (side) {
      case BLACK -> Rank.RANK_5;
      case WHITE -> Rank.RANK_4;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static Rank calculateEnPassantCaptureToRank(Side side) {
    return switch (side) {
      case BLACK -> Rank.RANK_3;
      case WHITE -> Rank.RANK_6;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static boolean isPawnEnPassantCaptureToRank(Side side, Rank rank) {
    if (side == Side.NONE || rank == Rank.NONE) {
      throw new IllegalArgumentException();
    }

    return rank == calculateEnPassantCaptureToRank(side);
  }

  public static boolean isValidRank(Side side, Rank rank) {
    return switch (side) {
      case WHITE -> rank != Rank.RANK_1 && rank != Rank.RANK_2;
      case BLACK -> rank != Rank.RANK_7 && rank != Rank.RANK_8;
      case NONE -> throw new IllegalArgumentException();
    };
  }

}
