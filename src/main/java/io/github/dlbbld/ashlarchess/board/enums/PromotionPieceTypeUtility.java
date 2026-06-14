// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board.enums;

/**
 * Move-ordering rule for promotion piece types.
 *
 * <p>
 * Legal moves are sorted by from-square, then to-square, then castling move, then promotion choice. For the promotion
 * component the order is by descending practical piece value: queen first, then rook, bishop, knight, and finally the
 * {@link PromotionPieceType#NONE} placeholder. This is the natural way to think about a promotion choice (strongest
 * outcome first) and is deliberately independent of the enum declaration order in {@link PromotionPieceType}, which
 * follows the codebase's static catalog convention (rook, knight, bishop, queen).
 */
public final class PromotionPieceTypeUtility {

  private PromotionPieceTypeUtility() {
  }

  /**
   * Compares two promotion piece types using the legal-move ordering rule: queen, rook, bishop, knight, none.
   *
   * @param firstPromotionPieceType  the first promotion piece type
   * @param secondPromotionPieceType the second promotion piece type
   * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
   *         than the second under the move-ordering rule
   */
  public static int compareForMoveOrdering(PromotionPieceType firstPromotionPieceType,
      PromotionPieceType secondPromotionPieceType) {
    return Integer.compare(rank(firstPromotionPieceType), rank(secondPromotionPieceType));
  }

  /**
   * Constructs the concrete {@link Piece} that a pawn of {@code havingMove} becomes when promoting to
   * {@code promotionPieceType}.
   */
  public static Piece calculate(Side havingMove, PromotionPieceType promotionPieceType) {
    return switch (havingMove) {
      case BLACK -> switch (promotionPieceType) {
        case ROOK -> Piece.BLACK_ROOK;
        case KNIGHT -> Piece.BLACK_KNIGHT;
        case BISHOP -> Piece.BLACK_BISHOP;
        case QUEEN -> Piece.BLACK_QUEEN;
        case NONE -> throw new IllegalArgumentException();
        default -> throw new IllegalArgumentException();
      };
      case WHITE -> switch (promotionPieceType) {
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

  private static int rank(PromotionPieceType promotionPieceType) {
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
