// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board.enums;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.common.exceptions.NonePointerException;

public enum Piece {
  WHITE_PAWN(PieceType.PAWN, Side.WHITE),
  WHITE_ROOK(PieceType.ROOK, Side.WHITE),
  WHITE_KNIGHT(PieceType.KNIGHT, Side.WHITE),
  WHITE_BISHOP(PieceType.BISHOP, Side.WHITE),
  WHITE_QUEEN(PieceType.QUEEN, Side.WHITE),
  WHITE_KING(PieceType.KING, Side.WHITE),
  BLACK_PAWN(PieceType.PAWN, Side.BLACK),
  BLACK_ROOK(PieceType.ROOK, Side.BLACK),
  BLACK_KNIGHT(PieceType.KNIGHT, Side.BLACK),
  BLACK_BISHOP(PieceType.BISHOP, Side.BLACK),
  BLACK_QUEEN(PieceType.QUEEN, Side.BLACK),
  BLACK_KING(PieceType.KING, Side.BLACK),
  NONE(PieceType.NONE, Side.NONE);

  @SuppressWarnings("null")
  public static final ImmutableList<Piece> REAL = ImmutableList.of(WHITE_PAWN, WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP,
      WHITE_QUEEN, WHITE_KING, BLACK_PAWN, BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING);

  private final PieceType pieceType;
  private final Side side;

  Piece(PieceType pieceType, Side side) {
    this.pieceType = pieceType;
    this.side = side;
  }

  public PieceType getPieceType() {
    check();
    return pieceType;
  }

  public Side getSide() {
    check();
    return side;
  }

  private void check() {
    if (this == NONE) {
      throw new NonePointerException();
    }
  }
}
