// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board.enums;

import static io.github.dlbbld.ashlarchess.common.utility.ImmutableUtility.constructListSquare;

import java.util.EnumMap;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.exceptions.NonePointerException;

/**
 * Board-geometry transformations and side-relative chess-rule squares for {@link Square}: the 180-degree reflection,
 * the promotion-rank and en-passant target-square lists, the two-square-advance jump-over square, and the castling
 * origin squares. These combine a square (or a {@link Side}) with the laws of chess, so they live here rather than on
 * the {@code Square} enum, which keeps its coordinate identity and single-step neighbour geometry. Each backing table
 * below exists solely to serve its method.
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

  private static final ImmutableList<Square> WHITE_PROMOTION_RANK = constructListSquare(Square.A8, Square.B8, Square.C8,
      Square.D8, Square.E8, Square.F8, Square.G8, Square.H8);

  private static final ImmutableList<Square> BLACK_PROMOTION_RANK = constructListSquare(Square.A1, Square.B1, Square.C1,
      Square.D1, Square.E1, Square.F1, Square.G1, Square.H1);

  public static List<Square> getPromotionRank(Side side) {
    return switch (side) {
      case WHITE -> WHITE_PROMOTION_RANK;
      case BLACK -> BLACK_PROMOTION_RANK;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  private static final ImmutableList<Square> WHITE_MOVE_EN_PASSANT_CAPTURE_TARGET_SQUARE_LIST = constructListSquare(
      Square.A6, Square.B6, Square.C6, Square.D6, Square.E6, Square.F6, Square.G6, Square.H6);

  private static final ImmutableList<Square> BLACK_MOVE_EN_PASSANT_CAPTURE_TARGET_SQUARE_LIST = constructListSquare(
      Square.A3, Square.B3, Square.C3, Square.D3, Square.E3, Square.F3, Square.G3, Square.H3);

  public static List<Square> calculateEnPassantCaptureTargetSquareList(Side havingMove) {
    return switch (havingMove) {
      case BLACK -> BLACK_MOVE_EN_PASSANT_CAPTURE_TARGET_SQUARE_LIST;
      case WHITE -> WHITE_MOVE_EN_PASSANT_CAPTURE_TARGET_SQUARE_LIST;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  private static final ImmutableMap<Square, Square> WHITE_TWO_SQUARE_ADVANCE_TO_JUMP_OVER;

  static {
    final EnumMap<Square, Square> map = Nulls.newEnumMap(Square.class);

    map.put(Square.A4, Square.A3);
    map.put(Square.B4, Square.B3);
    map.put(Square.C4, Square.C3);
    map.put(Square.D4, Square.D3);
    map.put(Square.E4, Square.E3);
    map.put(Square.F4, Square.F3);
    map.put(Square.G4, Square.G3);
    map.put(Square.H4, Square.H3);

    WHITE_TWO_SQUARE_ADVANCE_TO_JUMP_OVER = Nulls.immutableEnumMap(map);
  }

  private static final ImmutableMap<Square, Square> BLACK_TWO_SQUARE_ADVANCE_TO_JUMP_OVER;

  static {
    final EnumMap<Square, Square> map = Nulls.newEnumMap(Square.class);

    map.put(Square.A5, Square.A6);
    map.put(Square.B5, Square.B6);
    map.put(Square.C5, Square.C6);
    map.put(Square.D5, Square.D6);
    map.put(Square.E5, Square.E6);
    map.put(Square.F5, Square.F6);
    map.put(Square.G5, Square.G6);
    map.put(Square.H5, Square.H6);

    BLACK_TWO_SQUARE_ADVANCE_TO_JUMP_OVER = Nulls.immutableEnumMap(map);
  }

  public static Square calculateJumpOverSquare(Side sideHavingMadeTheMove, Square pawnTwoAdvanceSquare) {
    switch (sideHavingMadeTheMove) {
      case WHITE:
        if (!WHITE_TWO_SQUARE_ADVANCE_TO_JUMP_OVER.containsKey(pawnTwoAdvanceSquare)) {
          throw new IllegalArgumentException("The method only applies for pawn two square advance moves");
        }
        return Nulls.get(WHITE_TWO_SQUARE_ADVANCE_TO_JUMP_OVER, pawnTwoAdvanceSquare);
      case BLACK:
        if (!BLACK_TWO_SQUARE_ADVANCE_TO_JUMP_OVER.containsKey(pawnTwoAdvanceSquare)) {
          throw new IllegalArgumentException("The method only applies for pawn two square advance moves");
        }
        return Nulls.get(BLACK_TWO_SQUARE_ADVANCE_TO_JUMP_OVER, pawnTwoAdvanceSquare);
      case NONE:
      default:
        throw new IllegalArgumentException();
    }
  }

  public static Square calculateKingOriginalSquare(Side side) {
    return switch (side) {
      case BLACK -> Square.E8;
      case WHITE -> Square.E1;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static Square calculateQueenSideRookOriginalSquare(Side side) {
    return switch (side) {
      case BLACK -> Square.A8;
      case WHITE -> Square.A1;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static Square calculateKingSideRookOriginalSquare(Side side) {
    return switch (side) {
      case BLACK -> Square.H8;
      case WHITE -> Square.H1;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

}
