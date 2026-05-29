// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

enum VariableState {

  ZERO("no"),
  ONE("yes");

  private final String description;

  VariableState(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

}
