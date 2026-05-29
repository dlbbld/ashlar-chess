// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board.enums;

public enum CastlingRight {

  KING_SIDE("kingside", true, false),
  QUEEN_SIDE("queenside", false, true),
  KING_AND_QUEEN_SIDE("king- and queenside", true, true),
  NONE("no more castling rights", false, false);

  private final String description;
  private final boolean hasKingSide;
  private final boolean hasQueenSide;

  CastlingRight(String description, boolean hasKingSide, boolean hasQueenSide) {
    this.description = description;
    this.hasKingSide = hasKingSide;
    this.hasQueenSide = hasQueenSide;
  }

  public String getDescription() {
    return description;
  }

  public boolean getHasKingSide() {
    return hasKingSide;
  }

  public boolean getHasQueenSide() {
    return hasQueenSide;
  }

}
