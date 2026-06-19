// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.fen;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;

public final class FenPiecePlacementTestSupport {

  private FenPiecePlacementTestSupport() {
  }

  public static BitboardPosition validatePiecePlacement(String piecePlacement) {
    return StrictFenSemanticParser.validatePiecePlacement(piecePlacement);
  }
}
