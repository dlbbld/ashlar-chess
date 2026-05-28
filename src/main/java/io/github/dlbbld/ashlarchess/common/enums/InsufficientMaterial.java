// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.common.enums;

public enum InsufficientMaterial {

  NONE("none"),
  BOTH("both"),
  WHITE_ONLY("White only"),
  BLACK_ONLY("Black only");

  private final String description;

  InsufficientMaterial(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
