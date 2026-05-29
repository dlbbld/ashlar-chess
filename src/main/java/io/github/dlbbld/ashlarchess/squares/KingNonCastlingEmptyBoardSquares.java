// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.squares;

import java.util.EnumMap;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;

@SuppressWarnings("null")
public class KingNonCastlingEmptyBoardSquares extends AbstractEmptyBoardSquares implements EnumConstants {

  private static final int[][] KING_OFFSETS = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 }, { 1, 1 }, { 1, -1 },
      { -1, 1 }, { -1, -1 } };

  private static final ImmutableMap<Square, ImmutableSet<Square>> KING_SQUARES_MAP;

  static {
    final EnumMap<Square, ImmutableSet<Square>> map = Nulls.newEnumMap(Square.class);
    for (final Square from : Square.REAL) {
      final int fromFile = from.getFile().getNumber();
      final int fromRank = from.getRank().getNumber();
      final ImmutableSet.Builder<Square> builder = ImmutableSet.builder();
      for (final int[] offset : KING_OFFSETS) {
        final int toFile = fromFile + offset[0];
        final int toRank = fromRank + offset[1];
        if (toFile >= 1 && toFile <= 8 && toRank >= 1 && toRank <= 8) {
          builder.add(Square.calculate(toFile, toRank));
        }
      }
      map.put(from, builder.build());
    }
    KING_SQUARES_MAP = Nulls.copyOfMap(map);
    ValidateMoveNumberUtility.validateMapOfSet(KING_SQUARES_MAP, 420);
  }

  public static Set<Square> getKingSquares(Square fromSquare) {
    return Nulls.get(KING_SQUARES_MAP, fromSquare);
  }

}
