package io.github.dlbbld.ashlarchess.squares;

import java.util.EnumMap;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;

@SuppressWarnings("null")
public class KnightEmptyBoardSquares extends AbstractEmptyBoardSquares implements EnumConstants {

  private static final int[][] KNIGHT_OFFSETS = { { 1, 2 }, { 1, -2 }, { -1, 2 }, { -1, -2 }, { 2, 1 }, { 2, -1 },
      { -2, 1 }, { -2, -1 } };

  private static final ImmutableMap<Square, ImmutableSet<Square>> KNIGHT_SQUARES_MAP;

  static {
    final EnumMap<Square, ImmutableSet<Square>> map = Nulls.newEnumMap(Square.class);
    for (final Square from : Square.REAL) {
      final int fromFile = from.getFile().getNumber();
      final int fromRank = from.getRank().getNumber();
      final ImmutableSet.Builder<Square> builder = ImmutableSet.builder();
      for (final int[] offset : KNIGHT_OFFSETS) {
        final int toFile = fromFile + offset[0];
        final int toRank = fromRank + offset[1];
        if (toFile >= 1 && toFile <= 8 && toRank >= 1 && toRank <= 8) {
          builder.add(Square.calculate(toFile, toRank));
        }
      }
      map.put(from, builder.build());
    }
    KNIGHT_SQUARES_MAP = Nulls.copyOfMap(map);
    ValidateMoveNumberUtility.validateMapOfSet(KNIGHT_SQUARES_MAP, 336);
  }

  public static Set<Square> getKnightSquares(Square fromSquare) {
    return Nulls.get(KNIGHT_SQUARES_MAP, fromSquare);
  }

}
