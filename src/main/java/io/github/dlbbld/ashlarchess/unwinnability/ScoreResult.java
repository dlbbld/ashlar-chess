// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

enum ScoreResult {

  NORMAL(0),
  REWARD(1),
  PUNISH(-2);

  private final int increment;

  ScoreResult(int increment) {
    this.increment = increment;
  }

  public int getIncrement() {
    return increment;
  }

}
