package io.github.dlbbld.ashlarchess.squares;

import java.util.EnumMap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;

class BishopEmptyBoardSquares extends AbstractEmptyBoardSquares implements EnumConstants {

  private static final ImmutableMap<Square, BishopRange> BISHOP_SQUARES_MAP;

  static {
    final EnumMap<Square, BishopRange> map = Nulls.newEnumMap(Square.class);
    for (final Square from : Square.REAL) {
      final int file = from.getFile().getNumber();
      final int rank = from.getRank().getNumber();
      final ImmutableList<Square> northEast = RayUtility.ray(file, rank, 1, 1);
      final ImmutableList<Square> southEast = RayUtility.ray(file, rank, 1, -1);
      final ImmutableList<Square> southWest = RayUtility.ray(file, rank, -1, -1);
      final ImmutableList<Square> northWest = RayUtility.ray(file, rank, -1, 1);
      map.put(from, new BishopRange(northEast, southEast, southWest, northWest));
    }
    BISHOP_SQUARES_MAP = Nulls.copyOfMap(map);
    ValidateMoveNumberUtility.validateDiagonalMovesNumber(BISHOP_SQUARES_MAP, 560);
  }

  public static BishopRange getBishopSquares(Square fromSquare) {
    return Nulls.get(BISHOP_SQUARES_MAP, fromSquare);
  }

}
