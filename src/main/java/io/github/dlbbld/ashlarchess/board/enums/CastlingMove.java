// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board.enums;

import io.github.dlbbld.ashlarchess.exceptions.NonePointerException;

/**
 * The two castling moves, plus the {@link #NONE} sentinel.
 *
 * <p>
 * A castling {@code MoveSpecification} carries only the {@code CastlingMove} (its from/to squares are deliberately
 * {@code Square.NONE}). The geometry accessors below resolve, for a given {@link Side}, the four squares a castling
 * move actually touches - the king's origin and destination and the rook's origin and destination - so callers do not
 * have to hard-code castling geometry themselves.
 */
public enum CastlingMove {

  KING_SIDE,
  QUEEN_SIDE,
  NONE;

  /**
   * The square the king starts on for this castling move - {@code E1} for white, {@code E8} for black (the same for
   * king-side and queen-side, since the king always starts on the e-file).
   *
   * @param side the castling side
   * @return the king's origin square
   * @throws NonePointerException if this is {@link #NONE} or {@code side} is {@link Side#NONE}
   */
  public Square kingFromSquare(Side side) {
    checkNotNone(side);
    return switch (side) {
      case WHITE -> Square.E1;
      case BLACK -> Square.E8;
      default -> throw new NonePointerException();
    };
  }

  /**
   * The square the king ends on for this castling move and {@code side} - {@code G1}/{@code G8} for king-side,
   * {@code C1}/{@code C8} for queen-side.
   *
   * @param side the castling side
   * @return the king's destination square
   * @throws NonePointerException if this is {@link #NONE} or {@code side} is {@link Side#NONE}
   */
  public Square kingToSquare(Side side) {
    checkNotNone(side);
    return switch (this) {
      case KING_SIDE -> switch (side) {
        case WHITE -> Square.G1;
        case BLACK -> Square.G8;
        default -> throw new NonePointerException();
      };
      case QUEEN_SIDE -> switch (side) {
        case WHITE -> Square.C1;
        case BLACK -> Square.C8;
        default -> throw new NonePointerException();
      };
      default -> throw new NonePointerException();
    };
  }

  /**
   * The square the rook starts on for this castling move and {@code side} - {@code H1}/{@code H8} for king-side,
   * {@code A1}/{@code A8} for queen-side.
   *
   * @param side the castling side
   * @return the rook's origin square
   * @throws NonePointerException if this is {@link #NONE} or {@code side} is {@link Side#NONE}
   */
  public Square rookFromSquare(Side side) {
    checkNotNone(side);
    return switch (this) {
      case KING_SIDE -> switch (side) {
        case WHITE -> Square.H1;
        case BLACK -> Square.H8;
        default -> throw new NonePointerException();
      };
      case QUEEN_SIDE -> switch (side) {
        case WHITE -> Square.A1;
        case BLACK -> Square.A8;
        default -> throw new NonePointerException();
      };
      default -> throw new NonePointerException();
    };
  }

  /**
   * The square the rook ends on for this castling move and {@code side} - {@code F1}/{@code F8} for king-side,
   * {@code D1}/{@code D8} for queen-side.
   *
   * @param side the castling side
   * @return the rook's destination square
   * @throws NonePointerException if this is {@link #NONE} or {@code side} is {@link Side#NONE}
   */
  public Square rookToSquare(Side side) {
    checkNotNone(side);
    return switch (this) {
      case KING_SIDE -> switch (side) {
        case WHITE -> Square.F1;
        case BLACK -> Square.F8;
        default -> throw new NonePointerException();
      };
      case QUEEN_SIDE -> switch (side) {
        case WHITE -> Square.D1;
        case BLACK -> Square.D8;
        default -> throw new NonePointerException();
      };
      default -> throw new NonePointerException();
    };
  }

  private void checkNotNone(Side side) {
    if (this == NONE || side == Side.NONE) {
      throw new NonePointerException();
    }
  }
}
