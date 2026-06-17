// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.squares;

import java.util.EnumMap;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;

class PawnTwoAdvanceEmptyBoardSquares extends AbstractEmptyBoardSquares {

  private static final ImmutableMap<Square, ImmutableSet<Square>> PAWN_WHITE_SQUARES_MAP;
  private static final ImmutableMap<Square, ImmutableSet<Square>> PAWN_BLACK_SQUARES_MAP;

  static {
    PAWN_WHITE_SQUARES_MAP = build(Side.WHITE);
    ValidateMoveNumberUtility.validateMapOfSet(PAWN_WHITE_SQUARES_MAP, 8);

    PAWN_BLACK_SQUARES_MAP = build(Side.BLACK);
    ValidateMoveNumberUtility.validateMapOfSet(PAWN_BLACK_SQUARES_MAP, 8);
  }

  // Two-square advance is only available from the player's starting rank (2 for white, 7 for black).
  @SuppressWarnings("null")
  private static ImmutableMap<Square, ImmutableSet<Square>> build(Side side) {
    final int startRank = side == Side.WHITE ? 2 : 7;
    final int targetRank = side == Side.WHITE ? 4 : 5;
    final EnumMap<Square, ImmutableSet<Square>> map = Nulls.newEnumMap(Square.class);
    for (final Square from : Square.REAL) {
      if (from.getRank().getNumber() == startRank) {
        map.put(from, ImmutableSet.of(Square.of(from.getFile().getNumber(), targetRank)));
      } else {
        map.put(from, ImmutableSet.of());
      }
    }
    return Nulls.copyOfMap(map);
  }

  public static ImmutableSet<Square> getPawnSquares(Side havingMove, Square fromSquare) {
    return switch (havingMove) {
      case BLACK -> Nulls.get(PAWN_BLACK_SQUARES_MAP, fromSquare);
      case WHITE -> Nulls.get(PAWN_WHITE_SQUARES_MAP, fromSquare);
      case NONE -> throw new IllegalArgumentException();
    };
  }

}
