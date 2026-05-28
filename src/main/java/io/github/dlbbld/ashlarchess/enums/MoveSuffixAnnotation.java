// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.enums;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.common.utility.BasicUtility;

public enum MoveSuffixAnnotation {

  MISTAKE("?"),
  GOOD_MOVE("!"),
  BLUNDER("??"),
  DUBIOUS_MOVE("?!"),
  INTERESTING_MOVE("!?"),
  BRILLIANT_MOVE("!!"),
  NONE("");

  @SuppressWarnings("null")
  public static final ImmutableList<MoveSuffixAnnotation> REAL = ImmutableList.of(MISTAKE, GOOD_MOVE, BLUNDER,
      DUBIOUS_MOVE, INTERESTING_MOVE, BRILLIANT_MOVE);

  private final String suffix;

  MoveSuffixAnnotation(String suffix) {
    this.suffix = suffix;
  }

  public String getSuffix() {
    return suffix;
  }

  public static boolean exists(String suffix) {
    for (final MoveSuffixAnnotation suffixEnum : REAL) {
      if (suffixEnum.getSuffix().equals(suffix)) {
        return true;
      }
    }
    return false;
  }

  public static MoveSuffixAnnotation calculate(String suffix) {
    if (!exists(suffix)) {
      throw new IllegalArgumentException("No enum exists for this suffix");
    }
    for (final MoveSuffixAnnotation suffixEnum : REAL) {
      if (suffixEnum.getSuffix().equals(suffix)) {
        return suffixEnum;
      }
    }
    throw new ProgrammingMistakeException("The code for calculating the suffix enum is wrong");
  }

  public static String calculateValueList() {
    final List<String> list = new ArrayList<>();
    for (final MoveSuffixAnnotation suffixEnum : REAL) {
      list.add(suffixEnum.getSuffix());
    }
    return BasicUtility.calculateCommaSeparatedList(list);
  }
}
