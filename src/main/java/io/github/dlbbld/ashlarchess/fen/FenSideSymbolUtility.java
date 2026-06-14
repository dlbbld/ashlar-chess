// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.fen;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.exceptions.NonePointerException;

/**
 * Translates a {@link Side} into its {@link FenSideSymbol}. Kept off the enum, which carries only its data (the FEN
 * side letter) and the intrinsic letter-to-symbol parse.
 */
public final class FenSideSymbolUtility {

  private FenSideSymbolUtility() {
  }

  public static FenSideSymbol calculate(Side side) {
    return switch (side) {
      case WHITE -> FenSideSymbol.WHITE;
      case BLACK -> FenSideSymbol.BLACK;
      case NONE -> throw new NonePointerException();
    };
  }

}
