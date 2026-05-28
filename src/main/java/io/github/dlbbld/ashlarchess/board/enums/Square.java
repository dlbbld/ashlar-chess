// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board.enums;

import static io.github.dlbbld.ashlarchess.common.utility.ImmutableUtility.constructListSquare;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.exceptions.NonePointerException;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;

public enum Square implements Comparable<Square> {

  A1(SquareType.DARK_SQUARE, File.FILE_A, Rank.RANK_1, "a1"),
  B1(SquareType.LIGHT_SQUARE, File.FILE_B, Rank.RANK_1, "b1"),
  C1(SquareType.DARK_SQUARE, File.FILE_C, Rank.RANK_1, "c1"),
  D1(SquareType.LIGHT_SQUARE, File.FILE_D, Rank.RANK_1, "d1"),
  E1(SquareType.DARK_SQUARE, File.FILE_E, Rank.RANK_1, "e1"),
  F1(SquareType.LIGHT_SQUARE, File.FILE_F, Rank.RANK_1, "f1"),
  G1(SquareType.DARK_SQUARE, File.FILE_G, Rank.RANK_1, "g1"),
  H1(SquareType.LIGHT_SQUARE, File.FILE_H, Rank.RANK_1, "h1"),
  A2(SquareType.LIGHT_SQUARE, File.FILE_A, Rank.RANK_2, "a2"),
  B2(SquareType.DARK_SQUARE, File.FILE_B, Rank.RANK_2, "b2"),
  C2(SquareType.LIGHT_SQUARE, File.FILE_C, Rank.RANK_2, "c2"),
  D2(SquareType.DARK_SQUARE, File.FILE_D, Rank.RANK_2, "d2"),
  E2(SquareType.LIGHT_SQUARE, File.FILE_E, Rank.RANK_2, "e2"),
  F2(SquareType.DARK_SQUARE, File.FILE_F, Rank.RANK_2, "f2"),
  G2(SquareType.LIGHT_SQUARE, File.FILE_G, Rank.RANK_2, "g2"),
  H2(SquareType.DARK_SQUARE, File.FILE_H, Rank.RANK_2, "h2"),
  A3(SquareType.DARK_SQUARE, File.FILE_A, Rank.RANK_3, "a3"),
  B3(SquareType.LIGHT_SQUARE, File.FILE_B, Rank.RANK_3, "b3"),
  C3(SquareType.DARK_SQUARE, File.FILE_C, Rank.RANK_3, "c3"),
  D3(SquareType.LIGHT_SQUARE, File.FILE_D, Rank.RANK_3, "d3"),
  E3(SquareType.DARK_SQUARE, File.FILE_E, Rank.RANK_3, "e3"),
  F3(SquareType.LIGHT_SQUARE, File.FILE_F, Rank.RANK_3, "f3"),
  G3(SquareType.DARK_SQUARE, File.FILE_G, Rank.RANK_3, "g3"),
  H3(SquareType.LIGHT_SQUARE, File.FILE_H, Rank.RANK_3, "h3"),
  A4(SquareType.LIGHT_SQUARE, File.FILE_A, Rank.RANK_4, "a4"),
  B4(SquareType.DARK_SQUARE, File.FILE_B, Rank.RANK_4, "b4"),
  C4(SquareType.LIGHT_SQUARE, File.FILE_C, Rank.RANK_4, "c4"),
  D4(SquareType.DARK_SQUARE, File.FILE_D, Rank.RANK_4, "d4"),
  E4(SquareType.LIGHT_SQUARE, File.FILE_E, Rank.RANK_4, "e4"),
  F4(SquareType.DARK_SQUARE, File.FILE_F, Rank.RANK_4, "f4"),
  G4(SquareType.LIGHT_SQUARE, File.FILE_G, Rank.RANK_4, "g4"),
  H4(SquareType.DARK_SQUARE, File.FILE_H, Rank.RANK_4, "h4"),
  A5(SquareType.DARK_SQUARE, File.FILE_A, Rank.RANK_5, "a5"),
  B5(SquareType.LIGHT_SQUARE, File.FILE_B, Rank.RANK_5, "b5"),
  C5(SquareType.DARK_SQUARE, File.FILE_C, Rank.RANK_5, "c5"),
  D5(SquareType.LIGHT_SQUARE, File.FILE_D, Rank.RANK_5, "d5"),
  E5(SquareType.DARK_SQUARE, File.FILE_E, Rank.RANK_5, "e5"),
  F5(SquareType.LIGHT_SQUARE, File.FILE_F, Rank.RANK_5, "f5"),
  G5(SquareType.DARK_SQUARE, File.FILE_G, Rank.RANK_5, "g5"),
  H5(SquareType.LIGHT_SQUARE, File.FILE_H, Rank.RANK_5, "h5"),
  A6(SquareType.LIGHT_SQUARE, File.FILE_A, Rank.RANK_6, "a6"),
  B6(SquareType.DARK_SQUARE, File.FILE_B, Rank.RANK_6, "b6"),
  C6(SquareType.LIGHT_SQUARE, File.FILE_C, Rank.RANK_6, "c6"),
  D6(SquareType.DARK_SQUARE, File.FILE_D, Rank.RANK_6, "d6"),
  E6(SquareType.LIGHT_SQUARE, File.FILE_E, Rank.RANK_6, "e6"),
  F6(SquareType.DARK_SQUARE, File.FILE_F, Rank.RANK_6, "f6"),
  G6(SquareType.LIGHT_SQUARE, File.FILE_G, Rank.RANK_6, "g6"),
  H6(SquareType.DARK_SQUARE, File.FILE_H, Rank.RANK_6, "h6"),
  A7(SquareType.DARK_SQUARE, File.FILE_A, Rank.RANK_7, "a7"),
  B7(SquareType.LIGHT_SQUARE, File.FILE_B, Rank.RANK_7, "b7"),
  C7(SquareType.DARK_SQUARE, File.FILE_C, Rank.RANK_7, "c7"),
  D7(SquareType.LIGHT_SQUARE, File.FILE_D, Rank.RANK_7, "d7"),
  E7(SquareType.DARK_SQUARE, File.FILE_E, Rank.RANK_7, "e7"),
  F7(SquareType.LIGHT_SQUARE, File.FILE_F, Rank.RANK_7, "f7"),
  G7(SquareType.DARK_SQUARE, File.FILE_G, Rank.RANK_7, "g7"),
  H7(SquareType.LIGHT_SQUARE, File.FILE_H, Rank.RANK_7, "h7"),
  A8(SquareType.LIGHT_SQUARE, File.FILE_A, Rank.RANK_8, "a8"),
  B8(SquareType.DARK_SQUARE, File.FILE_B, Rank.RANK_8, "b8"),
  C8(SquareType.LIGHT_SQUARE, File.FILE_C, Rank.RANK_8, "c8"),
  D8(SquareType.DARK_SQUARE, File.FILE_D, Rank.RANK_8, "d8"),
  E8(SquareType.LIGHT_SQUARE, File.FILE_E, Rank.RANK_8, "e8"),
  F8(SquareType.DARK_SQUARE, File.FILE_F, Rank.RANK_8, "f8"),
  G8(SquareType.LIGHT_SQUARE, File.FILE_G, Rank.RANK_8, "g8"),
  H8(SquareType.DARK_SQUARE, File.FILE_H, Rank.RANK_8, "h8"),
  NONE(SquareType.NONE, File.NONE, Rank.NONE, "none");

  private final SquareType squareType;
  private final File file;
  private final Rank rank;
  private final String name;

  public static final ImmutableList<Square> SEVENTH_RANK = constructListSquare(A7, B7, C7, D7, E7, F7, G7, H7);
  public static final ImmutableList<Square> SECOND_RANK = constructListSquare(A2, B2, C2, D2, E2, F2, G2, H2);

  Square(SquareType squareType, File file, Rank rank, String name) {
    this.squareType = squareType;
    this.file = file;
    this.rank = rank;
    this.name = name;
  }

  public SquareType getSquareType() {
    check();
    return squareType;
  }

  public File getFile() {
    check();
    return file;
  }

  public Rank getRank() {
    check();
    return rank;
  }

  public String getName() {
    check();
    return name;
  }

  public static boolean exists(String name) {
    for (final Square square : REAL) {
      if (square.getName().equals(name)) {
        return true;
      }
    }
    return false;
  }

  public static boolean exists(File file, Rank rank) {
    for (final Square square : REAL) {
      if (square.getFile() == file && square.getRank() == rank) {
        return true;
      }
    }
    return false;
  }

  public static boolean exists(int fileNumber, int rankNumber) {
    for (final Square square : REAL) {
      if (square.getFile().getNumber() == fileNumber && square.getRank().getNumber() == rankNumber) {
        return true;
      }
    }
    return false;
  }

  public static Square calculate(String name) {
    if (!exists(name)) {
      throw new IllegalArgumentException("No board square exists for square name " + name);
    }
    for (final Square square : REAL) {
      if (square.getName().equals(name)) {
        return square;
      }
    }
    throw new ProgrammingMistakeException("The code for calculating the square by name is wrong");
  }

  public static Square calculate(File file, Rank rank) {
    if (!exists(file, rank)) {
      throw new IllegalArgumentException("No board square exists for file enum " + file + " and rank enum " + rank);
    }
    for (final Square square : REAL) {
      if (square.getFile() == file && square.getRank() == rank) {
        return square;
      }
    }
    throw new ProgrammingMistakeException("The code for calculating the square by file and rank is wrong");
  }

  public static Square calculate(int fileNumber, int rankNumber) {
    if (!exists(fileNumber, rankNumber)) {
      throw new IllegalArgumentException(
          "No board square exists for file number " + fileNumber + " and rank number " + rankNumber);
    }
    for (final Square square : REAL) {
      if (square.getFile().getNumber() == fileNumber && square.getRank().getNumber() == rankNumber) {
        return square;
      }
    }
    throw new ProgrammingMistakeException("The code for calculating the square by file and rank number is wrong");
  }

  // all squares except the empty one
  // order is not allowed to be changed as this will cause semantical errors
  public static final ImmutableList<Square> REAL = constructListSquare(A1, B1, C1, D1, E1, F1, G1, H1, A2, B2, C2, D2,
      E2, F2, G2, H2, A3, B3, C3, D3, E3, F3, G3, H3, A4, B4, C4, D4, E4, F4, G4, H4, A5, B5, C5, D5, E5, F5, G5, H5,
      A6, B6, C6, D6, E6, F6, G6, H6, A7, B7, C7, D7, E7, F7, G7, H7, A8, B8, C8, D8, E8, F8, G8, H8);

  // ---------------------------------------------------------------------------------------------
  // Single-step square-geometry lookup tables.
  //
  // For each of the eight directional relationships (left, right, ahead, behind, plus the four
  // diagonals), a per-side mapping from each source square to its neighbour in that direction.
  // Absent entries mean the source square has no neighbour in that direction (it is on the
  // relevant board edge). All public calculate*Square / calculateHas*Square methods are thin
  // accessors over these tables.
  // ---------------------------------------------------------------------------------------------

  private static EnumMap<Side, EnumMap<Square, Square>> buildOffsetTable(int fileOffsetForWhite,
      int rankOffsetForWhite) {
    final EnumMap<Side, EnumMap<Square, Square>> result = Nulls.newEnumMap(Side.class);
    for (final Side side : Side.REAL) {
      final int fileOffset = side == Side.WHITE ? fileOffsetForWhite : -fileOffsetForWhite;
      final int rankOffset = side == Side.WHITE ? rankOffsetForWhite : -rankOffsetForWhite;
      final EnumMap<Square, Square> sideMap = Nulls.newEnumMap(Square.class);
      for (final Square source : REAL) {
        final int targetFile = source.getFile().getNumber() + fileOffset;
        final int targetRank = source.getRank().getNumber() + rankOffset;
        if (targetFile >= 1 && targetFile <= 8 && targetRank >= 1 && targetRank <= 8) {
          sideMap.put(source, calculate(targetFile, targetRank));
        }
      }
      result.put(side, sideMap);
    }
    return result;
  }

  private static final EnumMap<Side, EnumMap<Square, Square>> LEFT_SQUARE = buildOffsetTable(-1, 0);
  private static final EnumMap<Side, EnumMap<Square, Square>> RIGHT_SQUARE = buildOffsetTable(1, 0);
  private static final EnumMap<Side, EnumMap<Square, Square>> AHEAD_SQUARE = buildOffsetTable(0, 1);
  private static final EnumMap<Side, EnumMap<Square, Square>> BEHIND_SQUARE = buildOffsetTable(0, -1);
  private static final EnumMap<Side, EnumMap<Square, Square>> LEFT_DIAGONAL_SQUARE = buildOffsetTable(-1, 1);
  private static final EnumMap<Side, EnumMap<Square, Square>> RIGHT_DIAGONAL_SQUARE = buildOffsetTable(1, 1);
  private static final EnumMap<Side, EnumMap<Square, Square>> BEHIND_LEFT_DIAGONAL_SQUARE = buildOffsetTable(-1, -1);
  private static final EnumMap<Side, EnumMap<Square, Square>> BEHIND_RIGHT_DIAGONAL_SQUARE = buildOffsetTable(1, -1);

  private static boolean hasNeighbour(EnumMap<Side, EnumMap<Square, Square>> table, Side havingMove, Square square) {
    if (havingMove == Side.NONE || square == NONE) {
      throw new IllegalArgumentException();
    }
    return Nulls.get(table, havingMove).containsKey(square);
  }

  private static Square getNeighbour(EnumMap<Side, EnumMap<Square, Square>> table, Side havingMove, Square square) {
    if (havingMove == Side.NONE || square == NONE) {
      throw new IllegalArgumentException();
    }
    final EnumMap<Square, Square> sideMap = Nulls.get(table, havingMove);
    if (!sideMap.containsKey(square)) {
      throw new IllegalArgumentException();
    }
    return Nulls.get(sideMap, square);
  }

  public static boolean calculateIsRightMostFile(Square square, Side side) {
    return switch (side) {
      case WHITE -> square.getFile() == File.FILE_H;
      case BLACK -> square.getFile() == File.FILE_A;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static final ImmutableList<Square> WHITE_PROMOTION_RANK = constructListSquare(A8, B8, C8, D8, E8, F8, G8, H8);

  public static final ImmutableList<Square> BLACK_PROMOTION_RANK = constructListSquare(A1, B1, C1, D1, E1, F1, G1, H1);

  public static List<Square> getPromotionRank(Side side) {
    return switch (side) {
      case WHITE -> WHITE_PROMOTION_RANK;
      case BLACK -> BLACK_PROMOTION_RANK;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  static final ImmutableList<Square> WHITE_MOVE_EN_PASSANT_CAPTURE_TARGET_SQUARE_LIST = constructListSquare(A6, B6, C6,
      D6, E6, F6, G6, H6);

  static final ImmutableList<Square> BLACK_MOVE_EN_PASSANT_CAPTURE_TARGET_SQUARE_LIST = constructListSquare(A3, B3, C3,
      D3, E3, F3, G3, H3);

  private static final ImmutableMap<Square, Square> WHITE_TWO_SQUARE_ADVANCE_TO_JUMP_OVER;

  static {
    final EnumMap<Square, Square> map = Nulls.newEnumMap(Square.class);

    map.put(A4, A3);
    map.put(B4, B3);
    map.put(C4, C3);
    map.put(D4, D3);
    map.put(E4, E3);
    map.put(F4, F3);
    map.put(G4, G3);
    map.put(H4, H3);

    WHITE_TWO_SQUARE_ADVANCE_TO_JUMP_OVER = Nulls.immutableEnumMap(map);
  }

  private static final ImmutableMap<Square, Square> BLACK_TWO_SQUARE_ADVANCE_TO_JUMP_OVER;

  static {
    final EnumMap<Square, Square> map = Nulls.newEnumMap(Square.class);

    map.put(A5, A6);
    map.put(B5, B6);
    map.put(C5, C6);
    map.put(D5, D6);
    map.put(E5, E6);
    map.put(F5, F6);
    map.put(G5, G6);
    map.put(H5, H6);

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

  public static boolean calculateHasLeftSquare(Side havingMove, Square square) {
    return hasNeighbour(LEFT_SQUARE, havingMove, square);
  }

  public static Square calculateLeftSquare(Side havingMove, Square square) {
    return getNeighbour(LEFT_SQUARE, havingMove, square);
  }

  public static boolean calculateHasRightSquare(Side havingMove, Square square) {
    return hasNeighbour(RIGHT_SQUARE, havingMove, square);
  }

  public static Square calculateRightSquare(Side havingMove, Square square) {
    return getNeighbour(RIGHT_SQUARE, havingMove, square);
  }

  public static boolean calculateHasAheadSquare(Side havingMove, Square square) {
    return hasNeighbour(AHEAD_SQUARE, havingMove, square);
  }

  public static Square calculateAheadSquare(Side havingMove, Square square) {
    return getNeighbour(AHEAD_SQUARE, havingMove, square);
  }

  public static boolean calculateHasBehindSquare(Side havingMove, Square square) {
    return hasNeighbour(BEHIND_SQUARE, havingMove, square);
  }

  public static Square calculateBehindSquare(Side havingMove, Square square) {
    return getNeighbour(BEHIND_SQUARE, havingMove, square);
  }

  public static boolean calculateHasLeftDiagonalSquare(Side havingMove, Square square) {
    return hasNeighbour(LEFT_DIAGONAL_SQUARE, havingMove, square);
  }

  public static Square calculateLeftDiagonalSquare(Side havingMove, Square square) {
    return getNeighbour(LEFT_DIAGONAL_SQUARE, havingMove, square);
  }

  public static boolean calculateHasRightDiagonalSquare(Side havingMove, Square square) {
    return hasNeighbour(RIGHT_DIAGONAL_SQUARE, havingMove, square);
  }

  public static Square calculateRightDiagonalSquare(Side havingMove, Square square) {
    return getNeighbour(RIGHT_DIAGONAL_SQUARE, havingMove, square);
  }

  public static boolean calculateHasBehindLeftDiagonalSquare(Side havingMove, Square square) {
    return hasNeighbour(BEHIND_LEFT_DIAGONAL_SQUARE, havingMove, square);
  }

  public static Square calculateBehindLeftDiagonalSquare(Side havingMove, Square square) {
    return getNeighbour(BEHIND_LEFT_DIAGONAL_SQUARE, havingMove, square);
  }

  public static boolean calculateHasBehindRightDiagonalSquare(Side havingMove, Square square) {
    return hasNeighbour(BEHIND_RIGHT_DIAGONAL_SQUARE, havingMove, square);
  }

  public static Square calculateBehindRightDiagonalSquare(Side havingMove, Square square) {
    return getNeighbour(BEHIND_RIGHT_DIAGONAL_SQUARE, havingMove, square);
  }

  public static Square calculateKingOriginalSquare(Side side) {
    return switch (side) {
      case BLACK -> E8;
      case WHITE -> E1;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static Square calculateQueenSideRookOriginalSquare(Side side) {
    return switch (side) {
      case BLACK -> A8;
      case WHITE -> A1;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static Square calculateKingSideRookOriginalSquare(Side side) {
    return switch (side) {
      case BLACK -> H8;
      case WHITE -> H1;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static List<Square> calculateEnPassantCaptureTargetSquareList(Side havingMove) {
    return switch (havingMove) {
      case BLACK -> BLACK_MOVE_EN_PASSANT_CAPTURE_TARGET_SQUARE_LIST;
      case WHITE -> WHITE_MOVE_EN_PASSANT_CAPTURE_TARGET_SQUARE_LIST;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static final ImmutableList<ImmutableList<Square>> WHITE_PAWN_TWO_SQUARE_ADVANCE;
  public static final ImmutableList<ImmutableList<Square>> BLACK_PAWN_TWO_SQUARE_ADVANCE;

  static {
    final List<ImmutableList<Square>> whiteListList = new ArrayList<>();
    initializeWhite(whiteListList);
    WHITE_PAWN_TWO_SQUARE_ADVANCE = Nulls.copyOfList(whiteListList);

    final List<ImmutableList<Square>> blackListList = new ArrayList<>();
    initializeBlack(blackListList);
    BLACK_PAWN_TWO_SQUARE_ADVANCE = Nulls.copyOfList(blackListList);
  }

  private static void initializeWhite(List<ImmutableList<Square>> listList) {
    listList.add(constructListSquare(A2, A4));
    listList.add(constructListSquare(B2, B4));
    listList.add(constructListSquare(C2, C4));
    listList.add(constructListSquare(D2, D4));
    listList.add(constructListSquare(E2, E4));
    listList.add(constructListSquare(F2, F4));
    listList.add(constructListSquare(G2, G4));
    listList.add(constructListSquare(H2, H4));
  }

  private static void initializeBlack(List<ImmutableList<Square>> listList) {
    listList.add(constructListSquare(A7, A5));
    listList.add(constructListSquare(B7, B5));
    listList.add(constructListSquare(C7, C5));
    listList.add(constructListSquare(D7, D5));
    listList.add(constructListSquare(E7, E5));
    listList.add(constructListSquare(F7, F5));
    listList.add(constructListSquare(G7, G5));
    listList.add(constructListSquare(H7, H5));
  }

  private void check() {
    if (this == NONE) {
      throw new NonePointerException();
    }
  }

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
