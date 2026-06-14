// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board.enums;

import io.github.dlbbld.ashlarchess.common.exceptions.NonePointerException;

/**
 * Board-geometry transformations on {@link Square} that are not part of a single square's intrinsic identity. Currently
 * the 180-degree point reflection ({@link #flip(Square)}); a square's own coordinates and single-step neighbours stay on
 * the {@code Square} enum.
 */
public final class SquareUtility {

  private SquareUtility() {
  }

  /**
   * The square reached by a 180-degree rotation of the board (point reflection through the centre): file is mirrored
   * left-right and rank top-bottom, so {@code a1 <-> h8}.
   */
  public static Square flip(Square square) {
    return switch (square) {
      case NONE -> throw new NonePointerException();
      case A1 -> Square.H8;
      case B1 -> Square.G8;
      case C1 -> Square.F8;
      case D1 -> Square.E8;
      case E1 -> Square.D8;
      case F1 -> Square.C8;
      case G1 -> Square.B8;
      case H1 -> Square.A8;
      case A2 -> Square.H7;
      case B2 -> Square.G7;
      case C2 -> Square.F7;
      case D2 -> Square.E7;
      case E2 -> Square.D7;
      case F2 -> Square.C7;
      case G2 -> Square.B7;
      case H2 -> Square.A7;
      case A3 -> Square.H6;
      case B3 -> Square.G6;
      case C3 -> Square.F6;
      case D3 -> Square.E6;
      case E3 -> Square.D6;
      case F3 -> Square.C6;
      case G3 -> Square.B6;
      case H3 -> Square.A6;
      case A4 -> Square.H5;
      case B4 -> Square.G5;
      case C4 -> Square.F5;
      case D4 -> Square.E5;
      case E4 -> Square.D5;
      case F4 -> Square.C5;
      case G4 -> Square.B5;
      case H4 -> Square.A5;
      case A5 -> Square.H4;
      case B5 -> Square.G4;
      case C5 -> Square.F4;
      case D5 -> Square.E4;
      case E5 -> Square.D4;
      case F5 -> Square.C4;
      case G5 -> Square.B4;
      case H5 -> Square.A4;
      case A6 -> Square.H3;
      case B6 -> Square.G3;
      case C6 -> Square.F3;
      case D6 -> Square.E3;
      case E6 -> Square.D3;
      case F6 -> Square.C3;
      case G6 -> Square.B3;
      case H6 -> Square.A3;
      case A7 -> Square.H2;
      case B7 -> Square.G2;
      case C7 -> Square.F2;
      case D7 -> Square.E2;
      case E7 -> Square.D2;
      case F7 -> Square.C2;
      case G7 -> Square.B2;
      case H7 -> Square.A2;
      case A8 -> Square.H1;
      case B8 -> Square.G1;
      case C8 -> Square.F1;
      case D8 -> Square.E1;
      case E8 -> Square.D1;
      case F8 -> Square.C1;
      case G8 -> Square.B1;
      case H8 -> Square.A1;
      default -> throw new IllegalArgumentException();
    };
  }

}
