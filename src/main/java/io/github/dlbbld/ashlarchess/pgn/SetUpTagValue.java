// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.pgn;

import java.util.ArrayList;
import java.util.List;

import io.github.dlbbld.ashlarchess.common.exceptions.NonePointerException;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.common.utility.ListUtility;

enum SetUpTagValue {
  START_FROM_INITIAL_POSITION("0"),
  START_FROM_SETUP_POSITION("1"),
  NONE("");

  @SuppressWarnings("null")
  public static final List<SetUpTagValue> REAL = List.of(START_FROM_INITIAL_POSITION,
      START_FROM_SETUP_POSITION);

  private final String value;

  SetUpTagValue(String value) {
    this.value = value;
  }

  public String getValue() {
    check();
    return value;
  }

  public static String allowedValuesText() {
    final List<String> list = new ArrayList<>();
    for (final SetUpTagValue tagValue : REAL) {
      list.add(tagValue.getValue());
    }
    return ListUtility.toCommaSeparatedString(list);
  }

  public static boolean exists(String value) {
    for (final SetUpTagValue tagValue : REAL) {
      if (tagValue.getValue().equals(value)) {
        return true;
      }
    }
    return false;
  }

  public static SetUpTagValue parse(String value) {
    if (!exists(value)) {
      throw new IllegalArgumentException();
    }
    for (final SetUpTagValue tagValue : REAL) {
      if (tagValue.getValue().equals(value)) {
        return tagValue;
      }
    }
    throw new ProgrammingMistakeException();
  }

  private void check() {
    if (this == NONE) {
      throw new NonePointerException();
    }
  }
}
