package io.github.dlbbld.ashlarchess.common.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;

public abstract class ImmutableUtility implements EnumConstants {

  public static final ImmutableSet<Square> EMPTY_UNMODIFIABLE_SET;

  static {
    final List<Square> list = new ArrayList<>();
    final EnumSet<Square> enumSet = Nulls.newEnumSet(list, Square.class);
    EMPTY_UNMODIFIABLE_SET = Nulls.copyOfSet(enumSet);
  }

  private static final ImmutableList<Square> EMPTY_UNMODIFIABLE_LIST_SQUARE;

  static {
    final List<Square> list = new ArrayList<>();
    EMPTY_UNMODIFIABLE_LIST_SQUARE = Nulls.copyOfList(list);
  }

  public static ImmutableSet<Square> constructSet(Square... squareArray) {
    if (squareArray.length == 0) {
      return EMPTY_UNMODIFIABLE_SET;
    }
    // the array is not constructed as null
    @SuppressWarnings("null") final List<Square> list = Arrays.asList(squareArray);
    // the enum set is not constructed as null
    @SuppressWarnings("null") final EnumSet<Square> enumSet = Nulls.newEnumSet(list, Square.class);
    return Nulls.copyOfSet(enumSet);
  }

  public static ImmutableList<Square> constructListSquare(Square... squareArray) {
    if (squareArray.length == 0) {
      return EMPTY_UNMODIFIABLE_LIST_SQUARE;
    }
    // the array is not constructed as null
    @SuppressWarnings("null") final List<Square> list = Nulls.asList(squareArray);
    return Nulls.copyOfList(list);
  }

}
