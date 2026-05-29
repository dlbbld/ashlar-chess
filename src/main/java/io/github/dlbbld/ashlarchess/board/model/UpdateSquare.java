// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board.model;

import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Square;

public record UpdateSquare(Square square, Piece piece) {

  public UpdateSquare(Square square) {
    this(square, Piece.NONE);
  }

}
