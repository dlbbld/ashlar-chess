// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.common.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;

public final class ListUtility {

  private static final String COMMA_SEPARATOR_LIST = ", ";

  private static final String SPACE_SEPARATOR_LIST = " ";

  private ListUtility() {
  }

  public static <E> E getOnly(List<E> list) {
    if (list.size() != 1) {
      throw new IllegalArgumentException("Expected exactly one element but found " + list.size());
    }
    return Nulls.getFirst(list);
  }

  public static String toCommaSeparatedString(List<String> list) {
    return Nulls.join(COMMA_SEPARATOR_LIST, list);
  }

  public static String toSpaceSeparatedString(List<String> list) {
    return Nulls.join(SPACE_SEPARATOR_LIST, list);
  }

  public static String toLineSeparatedString(List<String> list) {
    return Nulls.join("\n", list);
  }

  public static String formatSquares(Set<Square> squareSet) {
    final List<String> squareList = new ArrayList<>();
    for (final Square square : squareSet) {
      squareList.add(square.getName());
    }
    return toCommaSeparatedString(squareList);
  }

}
