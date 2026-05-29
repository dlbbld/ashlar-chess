// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.common.utility;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.fen.FenSideSymbol;

public abstract class FenUtility {

  public static String createDummyFenForPiecePlacement(String piecePlacement, Side side) {
    final StringBuilder fen = new StringBuilder();

    fen.append(piecePlacement);
    fen.append(" ");
    fen.append(FenSideSymbol.calculate(side).sideLetter());
    fen.append(" - - 0 100");

    return Nulls.toString(fen);

  }
}
