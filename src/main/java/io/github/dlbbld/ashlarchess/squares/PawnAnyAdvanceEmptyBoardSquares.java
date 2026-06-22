// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.squares;

import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;

final class PawnAnyAdvanceEmptyBoardSquares {

  private PawnAnyAdvanceEmptyBoardSquares() {
  }

  private static final Map<Square, Set<Square>> PAWN_WHITE_SQUARES_MAP;
  private static final Map<Square, Set<Square>> PAWN_BLACK_SQUARES_MAP;

  static {
    PAWN_WHITE_SQUARES_MAP = build(Side.WHITE);
    ValidateMoveNumberUtility.validateMapOfSet(PAWN_WHITE_SQUARES_MAP, 56);

    PAWN_BLACK_SQUARES_MAP = build(Side.BLACK);
    ValidateMoveNumberUtility.validateMapOfSet(PAWN_BLACK_SQUARES_MAP, 56);
  }

  // Union of one-advance and two-advance. Pawns only exist on ranks 2-7.
  @SuppressWarnings("null")
  private static Map<Square, Set<Square>> build(Side side) {
    final EnumMap<Square, Set<Square>> map = Nulls.newEnumMap(Square.class);
    for (final Square from : Square.REAL) {
      final Set<Square> builder = new LinkedHashSet<>();
      builder.addAll(PawnOneAdvanceEmptyBoardSquares.getPawnSquares(side, from));
      builder.addAll(PawnTwoAdvanceEmptyBoardSquares.getPawnSquares(side, from));
      map.put(from, Nulls.copyOfSet(builder));
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
