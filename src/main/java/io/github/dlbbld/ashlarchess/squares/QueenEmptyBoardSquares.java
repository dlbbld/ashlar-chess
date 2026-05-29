// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.squares;

import java.util.EnumMap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;

class QueenEmptyBoardSquares extends AbstractEmptyBoardSquares implements EnumConstants {

  private static final ImmutableMap<Square, QueenRange> QUEEN_SQUARES_MAP;

  static {
    final EnumMap<Square, QueenRange> map = Nulls.newEnumMap(Square.class);
    for (final Square from : Square.REAL) {
      final int file = from.getFile().getNumber();
      final int rank = from.getRank().getNumber();
      final ImmutableList<Square> north = RayUtility.ray(file, rank, 0, 1);
      final ImmutableList<Square> east = RayUtility.ray(file, rank, 1, 0);
      final ImmutableList<Square> south = RayUtility.ray(file, rank, 0, -1);
      final ImmutableList<Square> west = RayUtility.ray(file, rank, -1, 0);
      final ImmutableList<Square> northEast = RayUtility.ray(file, rank, 1, 1);
      final ImmutableList<Square> southEast = RayUtility.ray(file, rank, 1, -1);
      final ImmutableList<Square> southWest = RayUtility.ray(file, rank, -1, -1);
      final ImmutableList<Square> northWest = RayUtility.ray(file, rank, -1, 1);
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
