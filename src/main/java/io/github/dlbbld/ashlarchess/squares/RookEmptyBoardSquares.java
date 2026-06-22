// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.squares;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;

final class RookEmptyBoardSquares {

  private RookEmptyBoardSquares() {
  }

  private static final Map<Square, RookRange> ROOK_SQUARES_MAP;

  static {
    final EnumMap<Square, RookRange> map = Nulls.newEnumMap(Square.class);
    for (final Square from : Square.REAL) {
      final int file = from.getFile().getNumber();
      final int rank = from.getRank().getNumber();
      final List<Square> north = RayUtility.ray(file, rank, 0, 1);
      final List<Square> east = RayUtility.ray(file, rank, 1, 0);
      final List<Square> south = RayUtility.ray(file, rank, 0, -1);
      final List<Square> west = RayUtility.ray(file, rank, -1, 0);
      map.put(from, new RookRange(north, east, south, west));
    }
    ROOK_SQUARES_MAP = Nulls.copyOfMap(map);
    ValidateMoveNumberUtility.validateOrthogonalMoveNumber(ROOK_SQUARES_MAP, 896);
  }

  public static RookRange getRookSquares(Square fromSquare) {
    return Nulls.get(ROOK_SQUARES_MAP, fromSquare);
  }

}
