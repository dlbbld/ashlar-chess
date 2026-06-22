// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.squares;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;

final class PawnOneAdvanceEmptyBoardSquares {

  private PawnOneAdvanceEmptyBoardSquares() {
  }

  private static final Map<Square, Set<Square>> PAWN_WHITE_SQUARES_MAP;
  private static final Map<Square, Set<Square>> PAWN_BLACK_SQUARES_MAP;

  static {
    PAWN_WHITE_SQUARES_MAP = build(Side.WHITE);
    ValidateMoveNumberUtility.validateMapOfSet(PAWN_WHITE_SQUARES_MAP, 48);

    PAWN_BLACK_SQUARES_MAP = build(Side.BLACK);
    ValidateMoveNumberUtility.validateMapOfSet(PAWN_BLACK_SQUARES_MAP, 48);
  }

  // Pawns only exist on ranks 2-7. From those, one advance towards the player's promotion rank.
  @SuppressWarnings("null")
  private static Map<Square, Set<Square>> build(Side side) {
    final int rankOffset = side == Side.WHITE ? 1 : -1;
    final EnumMap<Square, Set<Square>> map = Nulls.newEnumMap(Square.class);
    for (final Square from : Square.REAL) {
      final int fromFile = from.getFile().getNumber();
      final int fromRank = from.getRank().getNumber();
      if (fromRank < 2 || fromRank > 7) {
        map.put(from, Set.of());
        continue;
      }
      final int toRank = fromRank + rankOffset;
      if (toRank >= 1 && toRank <= 8) {
        map.put(from, Set.of(Square.of(fromFile, toRank)));
      } else {
        map.put(from, Set.of());
      }
    }
    return Nulls.copyOfMap(map);
  }

  public static Set<Square> getPawnSquares(Side side, Square fromSquare) {
    return switch (side) {
      case BLACK -> Nulls.get(PAWN_BLACK_SQUARES_MAP, fromSquare);
      case WHITE -> Nulls.get(PAWN_WHITE_SQUARES_MAP, fromSquare);
      case NONE -> throw new IllegalArgumentException();
    };
  }

}
