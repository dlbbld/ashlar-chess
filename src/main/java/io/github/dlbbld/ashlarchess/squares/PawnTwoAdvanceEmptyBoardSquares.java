// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.squares;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.internal.Nulls;

final class PawnTwoAdvanceEmptyBoardSquares {

  private PawnTwoAdvanceEmptyBoardSquares() {
  }

  private static final Map<Square, Set<Square>> PAWN_WHITE_SQUARES_MAP;
  private static final Map<Square, Set<Square>> PAWN_BLACK_SQUARES_MAP;

  static {
    PAWN_WHITE_SQUARES_MAP = build(Side.WHITE);
    ValidateMoveNumberUtility.validateMapOfSet(PAWN_WHITE_SQUARES_MAP, 8);

    PAWN_BLACK_SQUARES_MAP = build(Side.BLACK);
    ValidateMoveNumberUtility.validateMapOfSet(PAWN_BLACK_SQUARES_MAP, 8);
  }

  // Two-square advance is only available from the player's starting rank (2 for white, 7 for black).
  @SuppressWarnings("null")
  private static Map<Square, Set<Square>> build(Side side) {
    final int startRank = side == Side.WHITE ? 2 : 7;
    final int targetRank = side == Side.WHITE ? 4 : 5;
    final EnumMap<Square, Set<Square>> map = Nulls.newEnumMap(Square.class);
    for (final Square from : Square.REAL) {
      if (from.getRank().getNumber() == startRank) {
        map.put(from, Set.of(Square.of(from.getFile().getNumber(), targetRank)));
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
