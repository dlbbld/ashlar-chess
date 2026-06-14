// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board.enums;

/**
 * Factory behaviour for the {@link Piece} value enum: constructing the concrete {@link Piece} for a given {@link Side}
 * (and {@link PieceType}). Extracted off the enum so {@code Piece} carries only its data (piece type + side).
 *
 * <p>
 * This is the single home for the {@code (Side, PieceType) -> Piece} mapping; an identical {@code PieceType.calculate}
 * formerly duplicated it and has been removed in favour of {@link #calculate(Side, PieceType)}.
 */
public final class PieceUtility {

  private PieceUtility() {
  }

  public static Piece calculateRookPiece(Side side) {
    return switch (side) {
      case BLACK -> Piece.BLACK_ROOK;
      case WHITE -> Piece.WHITE_ROOK;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static Piece calculateKnightPiece(Side side) {
    return switch (side) {
      case BLACK -> Piece.BLACK_KNIGHT;
      case WHITE -> Piece.WHITE_KNIGHT;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static Piece calculateBishopPiece(Side side) {
    return switch (side) {
      case BLACK -> Piece.BLACK_BISHOP;
      case WHITE -> Piece.WHITE_BISHOP;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static Piece calculateQueenPiece(Side side) {
    return switch (side) {
      case BLACK -> Piece.BLACK_QUEEN;
      case WHITE -> Piece.WHITE_QUEEN;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static Piece calculateKingPiece(Side side) {
    return switch (side) {
      case BLACK -> Piece.BLACK_KING;
      case WHITE -> Piece.WHITE_KING;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static Piece calculatePawnPiece(Side side) {
    return switch (side) {
      case BLACK -> Piece.BLACK_PAWN;
      case WHITE -> Piece.WHITE_PAWN;
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

}
