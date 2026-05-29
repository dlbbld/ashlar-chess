// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board.enums;

public enum AddSpace {

  YES(" "),
  NO("");

  private final String value;

  AddSpace(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
