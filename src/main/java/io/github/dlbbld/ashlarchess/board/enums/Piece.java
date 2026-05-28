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

  public static Piece calculateRookPiece(Side side) {
    return switch (side) {
      case BLACK -> BLACK_ROOK;
      case WHITE -> WHITE_ROOK;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static Piece calculateKnightPiece(Side side) {
    return switch (side) {
      case BLACK -> BLACK_KNIGHT;
      case WHITE -> WHITE_KNIGHT;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static Piece calculateBishopPiece(Side side) {
    return switch (side) {
      case BLACK -> BLACK_BISHOP;
      case WHITE -> WHITE_BISHOP;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static Piece calculateQueenPiece(Side side) {
    return switch (side) {
      case BLACK -> BLACK_QUEEN;
      case WHITE -> WHITE_QUEEN;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static Piece calculateKingPiece(Side side) {
    return switch (side) {
      case BLACK -> BLACK_KING;
      case WHITE -> WHITE_KING;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static Piece calculatePawnPiece(Side side) {
    return switch (side) {
      case BLACK -> BLACK_PAWN;
      case WHITE -> WHITE_PAWN;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static Piece calculate(Side side, PieceType pieceType) {
    return switch (pieceType) {
      case PAWN -> calculatePawnPiece(side);
      case ROOK -> calculateRookPiece(side);
      case KNIGHT -> calculateKnightPiece(side);
      case BISHOP -> calculateBishopPiece(side);
      case QUEEN -> calculateQueenPiece(side);
      case KING -> calculateKingPiece(side);
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  private void check() {
    if (this == NONE) {
      throw new NonePointerException();
    }
  }
}
