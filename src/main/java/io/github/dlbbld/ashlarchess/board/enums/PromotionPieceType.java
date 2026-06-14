// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board.enums;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.common.exceptions.NonePointerException;

public enum PromotionPieceType {
  ROOK(PieceType.ROOK),
  KNIGHT(PieceType.KNIGHT),
  BISHOP(PieceType.BISHOP),
  QUEEN(PieceType.QUEEN),
  NONE(PieceType.NONE);

  @SuppressWarnings("null")
  // Move-ordering rule (Q, R, B, N) - see PromotionPieceTypeUtility for the rationale.
  // Enum declaration above keeps the static catalog order (P, R, N, B, Q, K) shared with PieceType.
  public static final ImmutableList<PromotionPieceType> REAL = ImmutableList.of(QUEEN, ROOK, BISHOP, KNIGHT);

  private final PieceType pieceType;

  PromotionPieceType(PieceType pieceType) {
    this.pieceType = pieceType;
  }

  public PieceType getPieceType() {
    check();
    return pieceType;
  }

  private void check() {
    if (this == NONE) {
      throw new NonePointerException();
    }
  }
}
