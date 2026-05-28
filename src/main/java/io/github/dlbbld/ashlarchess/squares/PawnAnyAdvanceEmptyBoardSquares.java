package io.github.dlbbld.ashlarchess.squares;

import java.util.EnumMap;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;

class PawnAnyAdvanceEmptyBoardSquares extends AbstractEmptyBoardSquares implements EnumConstants {

  private static final ImmutableMap<Square, ImmutableSet<Square>> PAWN_WHITE_SQUARES_MAP;
  private static final ImmutableMap<Square, ImmutableSet<Square>> PAWN_BLACK_SQUARES_MAP;

  static {
    PAWN_WHITE_SQUARES_MAP = build(Side.WHITE);
    ValidateMoveNumberUtility.validateMapOfSet(PAWN_WHITE_SQUARES_MAP, 56);

    PAWN_BLACK_SQUARES_MAP = build(Side.BLACK);
    ValidateMoveNumberUtility.validateMapOfSet(PAWN_BLACK_SQUARES_MAP, 56);
  }

  // Union of one-advance and two-advance. Pawns only exist on ranks 2-7.
  @SuppressWarnings("null")
  private static ImmutableMap<Square, ImmutableSet<Square>> build(Side side) {
    final EnumMap<Square, ImmutableSet<Square>> map = Nulls.newEnumMap(Square.class);
    for (final Square from : Square.REAL) {
      final ImmutableSet.Builder<Square> builder = ImmutableSet.builder();
      builder.addAll(PawnOneAdvanceEmptyBoardSquares.getPawnSquares(side, from));
      builder.addAll(PawnTwoAdvanceEmptyBoardSquares.getPawnSquares(side, from));
      map.put(from, builder.build());
    }
    return Nulls.copyOfMap(map);
  }

  public static Set<Square> getPawnSquares(Side havingMove, Square fromSquare) {
    return switch (havingMove) {
      case BLACK -> Nulls.get(PAWN_BLACK_SQUARES_MAP, fromSquare);
      case WHITE -> Nulls.get(PAWN_WHITE_SQUARES_MAP, fromSquare);
      case NONE -> throw new IllegalArgumentException();
    };
  }

}
