// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.squares;

import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;

@SuppressWarnings("null")
public final class KingNonCastlingEmptyBoardSquares {

  private KingNonCastlingEmptyBoardSquares() {
  }

  private static final int[][] KING_OFFSETS = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 }, { 1, 1 }, { 1, -1 },
      { -1, 1 }, { -1, -1 } };

  private static final Map<Square, Set<Square>> KING_SQUARES_MAP;

  static {
    final EnumMap<Square, Set<Square>> map = Nulls.newEnumMap(Square.class);
    for (final Square from : Square.REAL) {
      final int fromFile = from.getFile().getNumber();
      final int fromRank = from.getRank().getNumber();
      final Set<Square> builder = new LinkedHashSet<>();
      for (final int[] offset : KING_OFFSETS) {
        final int toFile = fromFile + offset[0];
        final int toRank = fromRank + offset[1];
        if (toFile >= 1 && toFile <= 8 && toRank >= 1 && toRank <= 8) {
          builder.add(Square.of(toFile, toRank));
        }
      }
      map.put(from, Nulls.copyOfSet(builder));
    }
    KING_SQUARES_MAP = Nulls.copyOfMap(map);
    ValidateMoveNumberUtility.validateMapOfSet(KING_SQUARES_MAP, 420);
  }

  public static Set<Square> getKingSquares(Square fromSquare) {
    return Nulls.get(KING_SQUARES_MAP, fromSquare);
  }

}
