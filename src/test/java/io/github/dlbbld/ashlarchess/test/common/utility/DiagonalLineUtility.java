// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.common.utility;

import static io.github.dlbbld.ashlarchess.common.utility.ImmutableUtility.constructListSquare;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;

public abstract class DiagonalLineUtility implements EnumConstants {

  public static final ImmutableList<Square> A8_UP = constructListSquare(A8);

  public static final ImmutableList<Square> A7_UP = constructListSquare(A7, B8);

  public static final ImmutableList<Square> A6_UP = constructListSquare(A6, B7, C8);

  public static final ImmutableList<Square> A5_UP = constructListSquare(A5, B6, C7, D8);

  public static final ImmutableList<Square> A4_UP = constructListSquare(A4, B5, C6, D7, E8);

  public static final ImmutableList<Square> A3_UP = constructListSquare(A3, B4, C5, D6, E7, F8);

  public static final ImmutableList<Square> A2_UP = constructListSquare(A2, B3, C4, D5, E6, F7, G8);

  public static final ImmutableList<Square> A1_UP = constructListSquare(A1, B2, C3, D4, E5, F6, G7, H8);

  public static final ImmutableList<Square> B1_UP = constructListSquare(B1, C2, D3, E4, F5, G6, H7);

  public static final ImmutableList<Square> C1_UP = constructListSquare(C1, D2, E3, F4, G5, H6);

  public static final ImmutableList<Square> D1_UP = constructListSquare(D1, E2, F3, G4, H5);

  public static final ImmutableList<Square> E1_UP = constructListSquare(E1, F2, G3, H4);

  public static final ImmutableList<Square> F1_UP = constructListSquare(F1, G2, H3);

  public static final ImmutableList<Square> G1_UP = constructListSquare(G1, H2);

  public static final ImmutableList<Square> H1_UP = constructListSquare(H1);

  public static final ImmutableList<Square> A1_DOWN = constructListSquare(A1);

  public static final ImmutableList<Square> A2_DOWN = constructListSquare(A2, B1);

  public static final ImmutableList<Square> A3_DOWN = constructListSquare(A3, B2, C1);

  public static final ImmutableList<Square> A4_DOWN = constructListSquare(A4, B3, C2, D1);

  public static final ImmutableList<Square> A5_DOWN = constructListSquare(A5, B4, C3, D2, E1);

  public static final ImmutableList<Square> A6_DOWN = constructListSquare(A6, B5, C4, D3, E2, F1);

  public static final ImmutableList<Square> A7_DOWN = constructListSquare(A7, B6, C5, D4, E3, F2, G1);

  public static final ImmutableList<Square> A8_DOWN = constructListSquare(A8, B7, C6, D5, E4, F3, G2, H1);

  public static final ImmutableList<Square> B8_DOWN = constructListSquare(B8, C7, D6, E5, F4, G3, H2);

  public static final ImmutableList<Square> C8_DOWN = constructListSquare(C8, D7, E6, F5, G4, H3);

  public static final ImmutableList<Square> D8_DOWN = constructListSquare(D8, E7, F6, G5, H4);

  public static final ImmutableList<Square> E8_DOWN = constructListSquare(E8, F7, G6, H5);

  public static final ImmutableList<Square> F8_DOWN = constructListSquare(F8, G7, H6);

  public static final ImmutableList<Square> G8_DOWN = constructListSquare(G8, H7);

  public static final ImmutableList<Square> H8_DOWN = constructListSquare(H8);

  private static final ImmutableList<ImmutableList<Square>> WHITE_DIAGONALS;
  private static final ImmutableList<ImmutableList<Square>> BLACK_DIAGONALS;
  private static final ImmutableList<ImmutableList<Square>> ALL_DIAGONALS;

  static {
    final List<ImmutableList<Square>> whiteDiagonals = new ArrayList<>();
    initializeWhiteDiagonals(whiteDiagonals);
    WHITE_DIAGONALS = Nulls.copyOfList(whiteDiagonals);

    final List<ImmutableList<Square>> blackDiagonals = new ArrayList<>();
    initializeBlackDiagonals(blackDiagonals);
    BLACK_DIAGONALS = Nulls.copyOfList(blackDiagonals);

    final List<ImmutableList<Square>> allDiagonals = new ArrayList<>();
    allDiagonals.addAll(WHITE_DIAGONALS);
    allDiagonals.addAll(BLACK_DIAGONALS);
    ALL_DIAGONALS = Nulls.copyOfList(allDiagonals);
  }

  private static void initializeWhiteDiagonals(List<ImmutableList<Square>> diagonals) {
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

  private static void initializeBlackDiagonals(List<ImmutableList<Square>> diagonals) {
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

  public static ImmutableList<Square> calculateLeftToRightUpDiagonal(Square square) {

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

  public static ImmutableList<Square> calculateLeftToRightDownDiagonal(Square square) {

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
    final ImmutableList<Square> firstDiagonal = calculateDiagonal(fromSquare, intermediarySquare);
    final ImmutableList<Square> secondDiagonal = calculateDiagonal(intermediarySquare, toSquare);

    return firstDiagonal.equals(secondDiagonal);
  }

  private static final ImmutableList<Square> calculateDiagonal(Square fromSquare, Square toSquare) {
    if (!calculateIsOnDiagonalLine(fromSquare, toSquare)) {
      throw new ProgrammingMistakeException("The method is only designed for squares on a diagonal line");
    }
    for (final ImmutableList<Square> diagonalList : ALL_DIAGONALS) {
      if (diagonalList.contains(fromSquare) && diagonalList.contains(toSquare)) {
        return diagonalList;
      }
    }
    throw new ProgrammingMistakeException("This diagonal is not there");
  }

  private static boolean calculateIsContained(Square fromSquare, Square toSquare,
      List<ImmutableList<Square>> diagonaListList) {
    for (final ImmutableList<Square> diagonalList : diagonaListList) {
      if (diagonalList.contains(fromSquare) && diagonalList.contains(toSquare)) {
        return true;
      }
    }
    return false;
  }
}
