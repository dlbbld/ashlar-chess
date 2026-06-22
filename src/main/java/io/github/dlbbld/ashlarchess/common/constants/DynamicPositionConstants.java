// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.common.constants;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.board.DynamicPosition;
import io.github.dlbbld.ashlarchess.fen.FenConstants;

public final class DynamicPositionConstants {

  private DynamicPositionConstants() {
  }

  public static final DynamicPosition INITIAL = new DynamicPosition(FenConstants.FEN_INITIAL.sideToMove(),
      BitboardPosition.INITIAL_POSITION, Square.NONE, FenConstants.FEN_INITIAL.castlingRightWhite(),
      FenConstants.FEN_INITIAL.castlingRightBlack());
}
