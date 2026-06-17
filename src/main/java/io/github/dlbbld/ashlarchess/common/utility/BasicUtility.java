// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.common.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;

public final class BasicUtility {

  private static final String COMMA_SEPARATOR_LIST = ", ";

  private static final String SPACE_SEPARATOR_LIST = " ";

  private BasicUtility() {
  }

  public static String calculateCommaSeparatedList(List<String> list) {
    return Nulls.join(COMMA_SEPARATOR_LIST, list);
  }

  public static String calculateSpaceSeparatedList(List<String> list) {
    return Nulls.join(SPACE_SEPARATOR_LIST, list);
  }

  public static <E> E calculateOnlyElement(Set<E> set) {
    if (set.size() != 1) {
      throw new IllegalArgumentException("The set must contain exactly one element");
    }
    return Nulls.getFirst(new ArrayList<>(set));
  }

  public static <E> boolean isDisjoint(Set<E> firstSet, Set<E> secondSet) {
    for (final E elementFirstSet : firstSet) {
      if (secondSet.contains(elementFirstSet)) {
        return false;
      }
    }
    for (final E elementSecondSet : secondSet) {
      if (firstSet.contains(elementSecondSet)) {
        return false;
      }
    }
    return true;
  }

  public static String convertToString(List<String> list) {
    return Nulls.join("\n", list);
  }

  @SuppressWarnings("null")
  public static String getMessage(Throwable throwable) {
    return String.valueOf(throwable.getMessage());
  }

  public static String calculateSquareList(Set<Square> squareSet) {
    final List<String> squareList = new ArrayList<>();
    for (final Square square : squareSet) {
      squareList.add(square.getName());
    }
    return calculateCommaSeparatedList(squareList);
  }
}
