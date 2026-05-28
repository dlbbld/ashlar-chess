package io.github.dlbbld.ashlarchess.squares;

import java.util.EnumMap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;

class RookEmptyBoardSquares extends AbstractEmptyBoardSquares implements EnumConstants {

  private static final ImmutableMap<Square, RookRange> ROOK_SQUARES_MAP;

  static {
    final EnumMap<Square, RookRange> map = Nulls.newEnumMap(Square.class);
    for (final Square from : Square.REAL) {
      final int file = from.getFile().getNumber();
      final int rank = from.getRank().getNumber();
      final ImmutableList<Square> north = RayUtility.ray(file, rank, 0, 1);
      final ImmutableList<Square> east = RayUtility.ray(file, rank, 1, 0);
      final ImmutableList<Square> south = RayUtility.ray(file, rank, 0, -1);
      final ImmutableList<Square> west = RayUtility.ray(file, rank, -1, 0);
      map.put(from, new RookRange(north, east, south, west));
    }
    ROOK_SQUARES_MAP = Nulls.copyOfMap(map);
    ValidateMoveNumberUtility.validateOrthogonalMoveNumber(ROOK_SQUARES_MAP, 896);
  }

  public static RookRange getRookSquares(Square fromSquare) {
    return Nulls.get(ROOK_SQUARES_MAP, fromSquare);
  }

}
