// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board.enums;

import java.util.EnumMap;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.exceptions.NonePointerException;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;

public enum Rank {
  RANK_1(1),
  RANK_2(2),
  RANK_3(3),
  RANK_4(4),
  RANK_5(5),
  RANK_6(6),
  RANK_7(7),
  RANK_8(8),
  NONE(0);

  @SuppressWarnings("null")
  public static final ImmutableList<Rank> REAL = ImmutableList.of(RANK_1, RANK_2, RANK_3, RANK_4, RANK_5, RANK_6,
      RANK_7, RANK_8);

  private final int number;

  Rank(int number) {
    this.number = number;
  }

  public int getNumber() {
    check();
    return number;
  }

  public static boolean exists(char character) {
    return exists(Character.getNumericValue(character));
  }

  public static boolean exists(int number) {
    for (final Rank rank : values()) {
      if (rank == NONE) {
        continue;
      }
      if (rank.getNumber() == number) {
        return true;
      }
    }
    return false;
  }

  public static Rank parse(char character) {
    return of(Character.getNumericValue(character));
  }

  public static Rank of(int number) {
    if (!exists(number)) {
      throw new IllegalArgumentException("For this number no corresponding non dummy Rank exists");
    }
    for (final Rank rank : values()) {
      if (rank == NONE) {
        continue;
      }
      if (rank.getNumber() == number) {
        return rank;
      }
    }
    throw new ProgrammingMistakeException("The code for calculating the rank is wrong");
  }

  // ---------------------------------------------------------------------------------------------
  // Single-step rank-geometry lookup tables.
  //
  // For each Side, a mapping from each Rank to its previous / next neighbour from that side's
  // perspective. Absent entries mean the source rank is on the relevant board edge.
  // ---------------------------------------------------------------------------------------------

  private static EnumMap<Side, EnumMap<Rank, Rank>> buildOffsetTable(int offsetForWhite) {
    final EnumMap<Side, EnumMap<Rank, Rank>> result = Nulls.newEnumMap(Side.class);
    for (final Side side : Side.REAL) {
      final int offset = side == Side.WHITE ? offsetForWhite : -offsetForWhite;
      final EnumMap<Rank, Rank> sideMap = Nulls.newEnumMap(Rank.class);
      for (final Rank source : REAL) {
        final int targetNumber = source.getNumber() + offset;
        if (targetNumber >= 1 && targetNumber <= 8) {
          sideMap.put(source, calculateByNumberInternal(targetNumber));
        }
      }
      result.put(side, sideMap);
    }
    return result;
  }

  private static Rank calculateByNumberInternal(int number) {
    for (final Rank rank : REAL) {
      if (rank.getNumber() == number) {
        return rank;
      }
    }
    throw new ProgrammingMistakeException("No rank for number " + number);
  }

  private static final EnumMap<Side, EnumMap<Rank, Rank>> PREVIOUS_RANK = buildOffsetTable(-1);

  public Rank getPreviousRank(Side havingMove) {
    if (havingMove == Side.NONE || this == NONE) {
      throw new IllegalArgumentException();
    }
    final EnumMap<Rank, Rank> sideMap = Nulls.get(PREVIOUS_RANK, havingMove);
    if (!sideMap.containsKey(this)) {
      throw new IllegalArgumentException();
    }
    return Nulls.get(sideMap, this);
  }

  private void check() {
    if (this == NONE) {
      throw new NonePointerException();
    }
  }
}
