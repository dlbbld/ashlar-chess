// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.common.utility;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H8;

import java.util.ArrayList;
import java.util.List;

import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.internal.Nulls;

public final class DiagonalLineUtility {

  private DiagonalLineUtility() {
  }

  public static final List<Square> A8_UP = Nulls.listOf(A8);

  public static final List<Square> A7_UP = Nulls.listOf(A7, B8);

  public static final List<Square> A6_UP = Nulls.listOf(A6, B7, C8);

  public static final List<Square> A5_UP = Nulls.listOf(A5, B6, C7, D8);

  public static final List<Square> A4_UP = Nulls.listOf(A4, B5, C6, D7, E8);

  public static final List<Square> A3_UP = Nulls.listOf(A3, B4, C5, D6, E7, F8);

  public static final List<Square> A2_UP = Nulls.listOf(A2, B3, C4, D5, E6, F7, G8);

  public static final List<Square> A1_UP = Nulls.listOf(A1, B2, C3, D4, E5, F6, G7, H8);

  public static final List<Square> B1_UP = Nulls.listOf(B1, C2, D3, E4, F5, G6, H7);

  public static final List<Square> C1_UP = Nulls.listOf(C1, D2, E3, F4, G5, H6);

  public static final List<Square> D1_UP = Nulls.listOf(D1, E2, F3, G4, H5);

  public static final List<Square> E1_UP = Nulls.listOf(E1, F2, G3, H4);

  public static final List<Square> F1_UP = Nulls.listOf(F1, G2, H3);

  public static final List<Square> G1_UP = Nulls.listOf(G1, H2);

  public static final List<Square> H1_UP = Nulls.listOf(H1);

  public static final List<Square> A1_DOWN = Nulls.listOf(A1);

  public static final List<Square> A2_DOWN = Nulls.listOf(A2, B1);

  public static final List<Square> A3_DOWN = Nulls.listOf(A3, B2, C1);

  public static final List<Square> A4_DOWN = Nulls.listOf(A4, B3, C2, D1);

  public static final List<Square> A5_DOWN = Nulls.listOf(A5, B4, C3, D2, E1);

  public static final List<Square> A6_DOWN = Nulls.listOf(A6, B5, C4, D3, E2, F1);

  public static final List<Square> A7_DOWN = Nulls.listOf(A7, B6, C5, D4, E3, F2, G1);

  public static final List<Square> A8_DOWN = Nulls.listOf(A8, B7, C6, D5, E4, F3, G2, H1);

  public static final List<Square> B8_DOWN = Nulls.listOf(B8, C7, D6, E5, F4, G3, H2);

  public static final List<Square> C8_DOWN = Nulls.listOf(C8, D7, E6, F5, G4, H3);

  public static final List<Square> D8_DOWN = Nulls.listOf(D8, E7, F6, G5, H4);

  public static final List<Square> E8_DOWN = Nulls.listOf(E8, F7, G6, H5);

  public static final List<Square> F8_DOWN = Nulls.listOf(F8, G7, H6);

  public static final List<Square> G8_DOWN = Nulls.listOf(G8, H7);

  public static final List<Square> H8_DOWN = Nulls.listOf(H8);

  private static final List<List<Square>> WHITE_DIAGONALS;
  private static final List<List<Square>> BLACK_DIAGONALS;
  private static final List<List<Square>> ALL_DIAGONALS;

  static {
    final List<List<Square>> whiteDiagonals = new ArrayList<>();
    initializeWhiteDiagonals(whiteDiagonals);
    WHITE_DIAGONALS = Nulls.copyOfList(whiteDiagonals);

    final List<List<Square>> blackDiagonals = new ArrayList<>();
    initializeBlackDiagonals(blackDiagonals);
    BLACK_DIAGONALS = Nulls.copyOfList(blackDiagonals);

    final List<List<Square>> allDiagonals = new ArrayList<>(WHITE_DIAGONALS);
    allDiagonals.addAll(BLACK_DIAGONALS);
    ALL_DIAGONALS = Nulls.copyOfList(allDiagonals);
  }

  private static void initializeWhiteDiagonals(List<List<Square>> diagonals) {
    diagonals.add(A8_UP);
    diagonals.add(A6_UP);
    diagonals.add(A4_UP);
    diagonals.add(A2_UP);
    diagonals.add(B1_UP);
    diagonals.add(D1_UP);
    diagonals.add(F1_UP);
    diagonals.add(H1_UP);

    diagonals.add(A2_DOWN);
    diagonals.add(A4_DOWN);
    diagonals.add(A6_DOWN);
    diagonals.add(A8_DOWN);
    diagonals.add(C8_DOWN);
    diagonals.add(E8_DOWN);
    diagonals.add(G8_DOWN);
  }

  private static void initializeBlackDiagonals(List<List<Square>> diagonals) {
    diagonals.add(A7_UP);
    diagonals.add(A5_UP);
    diagonals.add(A3_UP);
    diagonals.add(A1_UP);
    diagonals.add(C1_UP);
    diagonals.add(E1_UP);
    diagonals.add(G1_UP);

    diagonals.add(A1_DOWN);
    diagonals.add(A3_DOWN);
    diagonals.add(A5_DOWN);
    diagonals.add(A7_DOWN);
    diagonals.add(B8_DOWN);
    diagonals.add(D8_DOWN);
    diagonals.add(G8_DOWN);
    diagonals.add(H8_DOWN);
  }

  public static List<Square> calculateLeftToRightUpDiagonal(Square square) {

    if (A8_UP.contains(square)) {
      return A8_UP;
    }
    if (A7_UP.contains(square)) {
      return A7_UP;
    }
    if (A6_UP.contains(square)) {
      return A6_UP;
    }
    if (A5_UP.contains(square)) {
      return A5_UP;
    }
    if (A4_UP.contains(square)) {
      return A4_UP;
    }
    if (A3_UP.contains(square)) {
      return A3_UP;
    }
    if (A2_UP.contains(square)) {
      return A2_UP;
    }
    if (A1_UP.contains(square)) {
      return A1_UP;
    }

    if (B1_UP.contains(square)) {
      return B1_UP;
    }
    if (C1_UP.contains(square)) {
      return C1_UP;
    }
    if (D1_UP.contains(square)) {
      return D1_UP;
    }
    if (E1_UP.contains(square)) {
      return E1_UP;
    }
    if (F1_UP.contains(square)) {
      return F1_UP;
    }
    if (G1_UP.contains(square)) {
      return G1_UP;
    }
    if (H1_UP.contains(square)) {
      return H1_UP;
    }

    throw new ProgrammingMistakeException("The corresponding diagonal for " + square.getName() + " was not found");
  }

  public static List<Square> calculateLeftToRightDownDiagonal(Square square) {

    if (A1_DOWN.contains(square)) {
      return A1_DOWN;
    }
    if (A2_DOWN.contains(square)) {
      return A2_DOWN;
    }
    if (A3_DOWN.contains(square)) {
      return A3_DOWN;
    }
    if (A4_DOWN.contains(square)) {
      return A4_DOWN;
    }
    if (A5_DOWN.contains(square)) {
      return A5_DOWN;
    }
    if (A6_DOWN.contains(square)) {
      return A6_DOWN;
    }
    if (A7_DOWN.contains(square)) {
      return A7_DOWN;
    }
    if (A8_DOWN.contains(square)) {
      return A8_DOWN;
    }

    if (B8_DOWN.contains(square)) {
      return B8_DOWN;
    }
    if (C8_DOWN.contains(square)) {
      return C8_DOWN;
    }
    if (D8_DOWN.contains(square)) {
      return D8_DOWN;
    }
    if (E8_DOWN.contains(square)) {
      return E8_DOWN;
    }
    if (F8_DOWN.contains(square)) {
      return F8_DOWN;
    }
    if (G8_DOWN.contains(square)) {
      return G8_DOWN;
    }
    if (H8_DOWN.contains(square)) {
      return H8_DOWN;
    }

    throw new ProgrammingMistakeException("The corresponding diagonal for " + square.getName() + " was not found");
  }

  public static boolean calculateIsOnDiagonalLine(Square fromSquare, Square toSquare) {
    if (fromSquare == toSquare) {
      throw new IllegalArgumentException("The squares cannot be the same");
    }

    return calculateIsContained(fromSquare, toSquare, WHITE_DIAGONALS)
        || calculateIsContained(fromSquare, toSquare, BLACK_DIAGONALS);
  }

  public static boolean calculateIsOnDiagonalLine(Square fromSquare, Square intermediarySquare, Square toSquare) {
    if (fromSquare == intermediarySquare || fromSquare == toSquare || intermediarySquare == toSquare) {
      throw new IllegalArgumentException("The squares must all be different");
    }

    if (!calculateIsOnDiagonalLine(fromSquare, intermediarySquare)
        || !calculateIsOnDiagonalLine(intermediarySquare, toSquare)) {
      return false;
    }
    // now we must check if the diagonals are the same
    final List<Square> firstDiagonal = calculateDiagonal(fromSquare, intermediarySquare);
    final List<Square> secondDiagonal = calculateDiagonal(intermediarySquare, toSquare);

    return firstDiagonal.equals(secondDiagonal);
  }

  private static List<Square> calculateDiagonal(Square fromSquare, Square toSquare) {
    if (!calculateIsOnDiagonalLine(fromSquare, toSquare)) {
      throw new ProgrammingMistakeException("The method is only designed for squares on a diagonal line");
    }
    for (final List<Square> diagonals : ALL_DIAGONALS) {
      if (diagonals.contains(fromSquare) && diagonals.contains(toSquare)) {
        return diagonals;
      }
    }
    throw new ProgrammingMistakeException("This diagonal is not there");
  }

  private static boolean calculateIsContained(Square fromSquare, Square toSquare, List<List<Square>> diagonalGroups) {
    for (final List<Square> diagonals : diagonalGroups) {
      if (diagonals.contains(fromSquare) && diagonals.contains(toSquare)) {
        return true;
      }
    }
    return false;
  }
}
