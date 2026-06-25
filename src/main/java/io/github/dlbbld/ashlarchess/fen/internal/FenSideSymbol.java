// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.fen.internal;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.exceptions.NonePointerException;

public enum FenSideSymbol {

  WHITE('w', Side.WHITE),
  BLACK('b', Side.BLACK);

  private final char sideLetter;
  private final Side side;

  FenSideSymbol(char sideLetter, Side side) {
    this.sideLetter = sideLetter;
    this.side = side;
  }

  public char sideLetter() {
    return sideLetter;
  }

  public Side side() {
    return side;
  }

  public static FenSideSymbol of(Side side) {
    return switch (side) {
      case WHITE -> WHITE;
      case BLACK -> BLACK;
      case NONE -> throw new NonePointerException();
    };
  }

  public static boolean exists(char sideLetter) {
    for (final FenSideSymbol symbol : values()) {
      if (symbol.sideLetter == sideLetter) {
        return true;
      }
    }
    return false;
  }

  public static FenSideSymbol parse(char sideLetter) {
    for (final FenSideSymbol symbol : values()) {
      if (symbol.sideLetter == sideLetter) {
        return symbol;
      }
    }
    throw new IllegalArgumentException("Not a valid FEN side letter: '" + sideLetter + "'");
  }

}
