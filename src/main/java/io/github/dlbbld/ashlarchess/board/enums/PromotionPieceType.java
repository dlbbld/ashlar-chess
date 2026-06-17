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

  /**
   * Constructs the concrete {@link Piece} that a pawn of {@code havingMove} becomes when promoting to
   * {@code promotionPieceType}.
   */
  public Piece toPiece(Side havingMove) {
    return switch (havingMove) {
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
   * Compares two promotion piece types using the legal-move ordering rule: queen, rook, bishop, knight, none.
   *
   * @param firstPromotionPieceType the first promotion piece type
   * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
   *         than the second under the move-ordering rule
   */
  public int compareForMoveOrdering(PromotionPieceType firstPromotionPieceType) {
    return Integer.compare(moveOrderingRank(this), moveOrderingRank(firstPromotionPieceType));
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
