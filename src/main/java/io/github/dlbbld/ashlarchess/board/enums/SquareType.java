// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board.enums;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;

public enum SquareType {
  LIGHT_SQUARE,
  DARK_SQUARE,
  NONE;

  @SuppressWarnings("null")
  public static final ImmutableList<SquareType> REAL = ImmutableList.of(LIGHT_SQUARE, DARK_SQUARE);

  // cannot define in constructor as cannot reference an enum befor it is defined
  public SquareType getOppositeSquareType() {
    return switch (this) {
      case LIGHT_SQUARE -> DARK_SQUARE;
      case DARK_SQUARE -> LIGHT_SQUARE;
      case NONE -> throw new ProgrammingMistakeException("The non square type has no opposite");
      default -> throw new ProgrammingMistakeException("The non square type has no opposite");
    };
  }

}
