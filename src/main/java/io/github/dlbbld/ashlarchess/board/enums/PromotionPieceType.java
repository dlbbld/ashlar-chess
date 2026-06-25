// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board.enums;

import java.util.List;

import io.github.dlbbld.ashlarchess.exceptions.NonePointerException;

public enum PromotionPieceType {
  ROOK(PieceType.ROOK),
  KNIGHT(PieceType.KNIGHT),
  BISHOP(PieceType.BISHOP),
  QUEEN(PieceType.QUEEN),
  NONE(PieceType.NONE);

  @SuppressWarnings("null")
  // Move-ordering rule (Q, R, B, N) - see PromotionPieceTypeUtility for the rationale.
  // Enum declaration above keeps the static catalog order (P, R, N, B, Q, K) shared with PieceType.
  public static final List<PromotionPieceType> REAL = List.of(QUEEN, ROOK, BISHOP, KNIGHT);

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

  /**
   * Constructs the concrete {@link Piece} that a pawn of {@code side} becomes when promoting to
   * {@code promotionPieceType}.
   */
  public Piece toPiece(Side side) {
    return switch (side) {
      case BLACK -> switch (this) {
        case ROOK -> Piece.BLACK_ROOK;
        case KNIGHT -> Piece.BLACK_KNIGHT;
        case BISHOP -> Piece.BLACK_BISHOP;
        case QUEEN -> Piece.BLACK_QUEEN;
        case NONE -> throw new IllegalArgumentException();
        default -> throw new IllegalArgumentException();
      };
      case WHITE -> switch (this) {
        case ROOK -> Piece.WHITE_ROOK;
        case KNIGHT -> Piece.WHITE_KNIGHT;
        case BISHOP -> Piece.WHITE_BISHOP;
        case QUEEN -> Piece.WHITE_QUEEN;
        case NONE -> throw new IllegalArgumentException();
        default -> throw new IllegalArgumentException();
      };
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  /**
   * Compares this promotion piece type against {@code other} using the legal-move ordering rule: queen, rook, bishop,
   * knight, none.
   *
   * @param other the promotion piece type to compare this one against
   * @return a negative integer, zero, or a positive integer as this type orders before, the same as, or after
   *         {@code other} under the move-ordering rule
   */
  public int compareForMoveOrdering(PromotionPieceType other) {
    return Integer.compare(moveOrderingRank(this), moveOrderingRank(other));
  }

  private static int moveOrderingRank(PromotionPieceType promotionPieceType) {
    return switch (promotionPieceType) {
      case QUEEN -> 0;
      case ROOK -> 1;
      case BISHOP -> 2;
      case KNIGHT -> 3;
      case NONE -> 4;
      default -> throw new IllegalArgumentException();
    };
  }
}
