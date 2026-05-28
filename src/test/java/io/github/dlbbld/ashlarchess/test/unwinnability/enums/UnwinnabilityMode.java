// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability.enums;

import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;

public enum UnwinnabilityMode {
  FULL("full"),
  QUICK("quick");

  private final String identifier;

  UnwinnabilityMode(String identifier) {
    this.identifier = identifier;
  }

  public String getIdentifier() {
    return identifier;
  }

  public static boolean exists(String identifier) {
    for (final UnwinnabilityMode mode : values()) {
      if (mode.getIdentifier().equals(identifier)) {
        return true;
      }
    }
    return false;
  }

  public static UnwinnabilityMode calculate(String identifier) {
    if (!exists(identifier)) {
      throw new IllegalArgumentException("No mode for this letter identifier");
    }
    for (final UnwinnabilityMode mode : values()) {
      if (mode.getIdentifier().equals(identifier)) {
        return mode;
      }
    }
    // not possible to come here
    throw new ProgrammingMistakeException();
  }

}
