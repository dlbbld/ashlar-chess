// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.common.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;

public final class ImmutableUtility {

  private ImmutableUtility() {
  }

  public static final Set<Square> EMPTY_UNMODIFIABLE_SET;

  static {
    final List<Square> list = new ArrayList<>();
    final EnumSet<Square> enumSet = Nulls.newEnumSet(list, Square.class);
    EMPTY_UNMODIFIABLE_SET = Nulls.copyOfSet(enumSet);
  }

  public static Set<Square> constructSet(Square... squareArray) {
    if (squareArray.length == 0) {
      return EMPTY_UNMODIFIABLE_SET;
    }
    // the array is not constructed as null
    @SuppressWarnings("null") final List<Square> list = Arrays.asList(squareArray);
    // the enum set is not constructed as null
    @SuppressWarnings("null") final EnumSet<Square> enumSet = Nulls.newEnumSet(list, Square.class);
    return Nulls.copyOfSet(enumSet);
  }

}
