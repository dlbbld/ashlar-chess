// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.squares;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.internal.Nulls;

final class QueenEmptyBoardSquares {

  private QueenEmptyBoardSquares() {
  }

  private static final Map<Square, QueenRange> QUEEN_SQUARES_MAP;

  static {
    final EnumMap<Square, QueenRange> map = Nulls.newEnumMap(Square.class);
    for (final Square from : Square.REAL) {
      final int file = from.getFile().getNumber();
      final int rank = from.getRank().getNumber();
      final List<Square> north = RayUtility.ray(file, rank, 0, 1);
      final List<Square> east = RayUtility.ray(file, rank, 1, 0);
      final List<Square> south = RayUtility.ray(file, rank, 0, -1);
      final List<Square> west = RayUtility.ray(file, rank, -1, 0);
      final List<Square> northEast = RayUtility.ray(file, rank, 1, 1);
      final List<Square> southEast = RayUtility.ray(file, rank, 1, -1);
      final List<Square> southWest = RayUtility.ray(file, rank, -1, -1);
      final List<Square> northWest = RayUtility.ray(file, rank, -1, 1);
      map.put(from, new QueenRange(north, east, south, west, northEast, southEast, southWest, northWest));
    }
    QUEEN_SQUARES_MAP = Nulls.copyOfMap(map);
    ValidateMoveNumberUtility.validateOrthogonalMoveNumber(QUEEN_SQUARES_MAP, 896);
    ValidateMoveNumberUtility.validateDiagonalMovesNumber(QUEEN_SQUARES_MAP, 560);
  }

  public static QueenRange getQueenSquares(Square fromSquare) {
    return Nulls.get(QUEEN_SQUARES_MAP, fromSquare);
  }

}
