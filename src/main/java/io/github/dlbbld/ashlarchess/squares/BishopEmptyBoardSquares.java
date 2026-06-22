// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.squares;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;

final class BishopEmptyBoardSquares {

  private BishopEmptyBoardSquares() {
  }

  private static final Map<Square, BishopRange> BISHOP_SQUARES_MAP;

  static {
    final EnumMap<Square, BishopRange> map = Nulls.newEnumMap(Square.class);
    for (final Square from : Square.REAL) {
      final int file = from.getFile().getNumber();
      final int rank = from.getRank().getNumber();
      final List<Square> northEast = RayUtility.ray(file, rank, 1, 1);
      final List<Square> southEast = RayUtility.ray(file, rank, 1, -1);
      final List<Square> southWest = RayUtility.ray(file, rank, -1, -1);
      final List<Square> northWest = RayUtility.ray(file, rank, -1, 1);
      map.put(from, new BishopRange(northEast, southEast, southWest, northWest));
    }
    BISHOP_SQUARES_MAP = Nulls.copyOfMap(map);
    ValidateMoveNumberUtility.validateDiagonalMovesNumber(BISHOP_SQUARES_MAP, 560);
  }

  public static BishopRange getBishopSquares(Square fromSquare) {
    return Nulls.get(BISHOP_SQUARES_MAP, fromSquare);
  }

}
