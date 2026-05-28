// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

public enum CheckmateOrStalemate {

  CHECKMATE("checkmate"),
  STALEMATE("stalemate"),
  NA("na");

  private final String description;

  CheckmateOrStalemate(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

}
