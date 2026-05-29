// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.model;

import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;

public record PseudoLegalMove(MoveSpecification moveSpecification, Piece movingPiece, Piece pieceCaptured)
    implements Comparable<PseudoLegalMove>, EnumConstants {

  @Override
  public int compareTo(PseudoLegalMove pseudoLegalMove) {
    return this.moveSpecification().compareTo(pseudoLegalMove.moveSpecification());
  }

}
