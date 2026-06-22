// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.squares;

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
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.BLACK;
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
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.WHITE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.annotation.NonNull;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;

class TestEmptyBoardSquares {

  @SuppressWarnings({ "static-method" })
  @Test
  void testRook() {

    {
      // A1

      final List<Square> northSquares = Nulls.listOf(A2, A3, A4, A5, A6, A7, A8);
      final List<Square> eastSquares = Nulls.listOf(B1, C1, D1, E1, F1, G1, H1);
      final List<Square> southSquares = Nulls.listOf();
      final List<Square> westSquares = Nulls.listOf();
      final RookRange range = new RookRange(northSquares, eastSquares, southSquares, westSquares);

      testRookSquares(A1, range);
    }

    {
      // A2

      final List<Square> northSquares = Nulls.listOf(A3, A4, A5, A6, A7, A8);
      final List<Square> eastSquares = Nulls.listOf(B2, C2, D2, E2, F2, G2, H2);
      final List<Square> southSquares = Nulls.listOf(A1);
      final List<Square> westSquares = Nulls.listOf();
      final RookRange range = new RookRange(northSquares, eastSquares, southSquares, westSquares);

      testRookSquares(A2, range);
    }

    {
      // B1

      final List<Square> northSquares = Nulls.listOf(B2, B3, B4, B5, B6, B7, B8);
      final List<Square> eastSquares = Nulls.listOf(C1, D1, E1, F1, G1, H1);
      final List<Square> southSquares = Nulls.listOf();
      final List<Square> westSquares = Nulls.listOf(A1);
      final RookRange range = new RookRange(northSquares, eastSquares, southSquares, westSquares);

      testRookSquares(B1, range);
    }

    {
      // B2

      final List<Square> northSquares = Nulls.listOf(B3, B4, B5, B6, B7, B8);
      final List<Square> eastSquares = Nulls.listOf(C2, D2, E2, F2, G2, H2);
      final List<Square> southSquares = Nulls.listOf(B1);
      final List<Square> westSquares = Nulls.listOf(A2);
      final RookRange range = new RookRange(northSquares, eastSquares, southSquares, westSquares);

      testRookSquares(B2, range);
    }

    {
      // E5

      final List<Square> northSquares = Nulls.listOf(E6, E7, E8);
      final List<Square> eastSquares = Nulls.listOf(F5, G5, H5);
      final List<Square> southSquares = Nulls.listOf(E4, E3, E2, E1);
      final List<Square> westSquares = Nulls.listOf(D5, C5, B5, A5);
      final RookRange range = new RookRange(northSquares, eastSquares, southSquares, westSquares);

      testRookSquares(E5, range);
    }

    {
      // D8

      final List<Square> northSquares = Nulls.listOf();
      final List<Square> eastSquares = Nulls.listOf(E8, F8, G8, H8);
      final List<Square> southSquares = Nulls.listOf(D7, D6, D5, D4, D3, D2, D1);
      final List<Square> westSquares = Nulls.listOf(C8, B8, A8);

      final RookRange range = new RookRange(northSquares, eastSquares, southSquares, westSquares);

      testRookSquares(D8, range);
    }
  }

  private static void testRookSquares(Square testSquare, RookRange rookRange) {
    final RookRange generatedRookMoves = RookEmptyBoardSquares.getRookSquares(testSquare);
    assertEquals(generatedRookMoves, rookRange);
  }

  @SuppressWarnings("static-method")
  @Test
  void testKnight() {

    // A1
    testKnightSquares(A1, B3, C2);

    // A2
    testKnightSquares(A2, B4, C3, C1);

    // A3
    testKnightSquares(A3, B5, C4, C2, B1);

    // B1
    testKnightSquares(B1, C3, D2, A3);

    // C1
    testKnightSquares(C1, D3, E2, A2, B3);

    // B2
    testKnightSquares(B2, C4, D3, D1, A4);

    // B3
    testKnightSquares(B3, C5, D4, D2, C1, A1, A5);

    // C3
    testKnightSquares(C2, D4, E3, E1, A1, A3, B4);

    // C3
    testKnightSquares(C3, D5, E4, E2, D1, B1, A2, A4, B5);

    // E5
    testKnightSquares(E5, F7, G6, G4, F3, D3, C4, C6, D7);

    // F8
    testKnightSquares(F8, E6, D7, H7, G6);

    // G8
    testKnightSquares(G8, F6, E7, H6);

  }

  private static void testKnightSquares(Square testSquare, Square... squares) {
    final Set<Square> lines = new TreeSet<>();
    for (final Square square : squares) {
      @SuppressWarnings("null") @NonNull final Square squareNonNull = square;
      lines.add(squareNonNull);
    }
    final Set<Square> generatedKnightMoves = KnightEmptyBoardSquares.getKnightSquares(testSquare);
    assertEquals(generatedKnightMoves, lines);
  }

  @SuppressWarnings({ "static-method" })
  @Test
  void testBishop() {

    // A1
    {

      final List<Square> northEastSquares = Nulls.listOf(B2, C3, D4, E5, F6, G7, H8);
      final List<Square> southEastSquares = Nulls.listOf();
      final List<Square> southWestSquares = Nulls.listOf();
      final List<Square> northWestSquares = Nulls.listOf();

      final BishopRange range = new BishopRange(northEastSquares, southEastSquares, southWestSquares, northWestSquares);

      testBishopSquares(A1, range);
    }

    // A2
    {

      final List<Square> northEastSquares = Nulls.listOf(B3, C4, D5, E6, F7, G8);
      final List<Square> southEastSquares = Nulls.listOf(B1);
      final List<Square> southWestSquares = Nulls.listOf();
      final List<Square> northWestSquares = Nulls.listOf();

      final BishopRange range = new BishopRange(northEastSquares, southEastSquares, southWestSquares, northWestSquares);

      testBishopSquares(A2, range);
    }

    // A3
    {

      final List<Square> northEastSquares = Nulls.listOf(B4, C5, D6, E7, F8);
      final List<Square> southEastSquares = Nulls.listOf(B2, C1);
      final List<Square> southWestSquares = Nulls.listOf();
      final List<Square> northWestSquares = Nulls.listOf();

      final BishopRange range = new BishopRange(northEastSquares, southEastSquares, southWestSquares, northWestSquares);

      testBishopSquares(A3, range);
    }

    // B1
    {

      final List<Square> northEastSquares = Nulls.listOf(C2, D3, E4, F5, G6, H7);
      final List<Square> southEastSquares = Nulls.listOf();
      final List<Square> southWestSquares = Nulls.listOf();
      final List<Square> northWestSquares = Nulls.listOf(A2);

      final BishopRange range = new BishopRange(northEastSquares, southEastSquares, southWestSquares, northWestSquares);

      testBishopSquares(B1, range);
    }

    // B2
    {

      final List<Square> northEastSquares = Nulls.listOf(C3, D4, E5, F6, G7, H8);
      final List<Square> southEastSquares = Nulls.listOf(C1);
      final List<Square> southWestSquares = Nulls.listOf(A1);
      final List<Square> northWestSquares = Nulls.listOf(A3);

      final BishopRange range = new BishopRange(northEastSquares, southEastSquares, southWestSquares, northWestSquares);

      testBishopSquares(B2, range);
    }

    // B3
    {

      final List<Square> northEastSquares = Nulls.listOf(C4, D5, E6, F7, G8);
      final List<Square> southEastSquares = Nulls.listOf(C2, D1);
      final List<Square> southWestSquares = Nulls.listOf(A2);
      final List<Square> northWestSquares = Nulls.listOf(A4);

      final BishopRange range = new BishopRange(northEastSquares, southEastSquares, southWestSquares, northWestSquares);

      testBishopSquares(B3, range);
    }

    // C1
    {

      final List<Square> northEastSquares = Nulls.listOf(D2, E3, F4, G5, H6);
      final List<Square> southEastSquares = Nulls.listOf();
      final List<Square> southWestSquares = Nulls.listOf();
      final List<Square> northWestSquares = Nulls.listOf(B2, A3);

      final BishopRange range = new BishopRange(northEastSquares, southEastSquares, southWestSquares, northWestSquares);

      testBishopSquares(C1, range);
    }

    // C2
    {

      final List<Square> northEastSquares = Nulls.listOf(D3, E4, F5, G6, H7);
      final List<Square> southEastSquares = Nulls.listOf(D1);
      final List<Square> southWestSquares = Nulls.listOf(B1);
      final List<Square> northWestSquares = Nulls.listOf(B3, A4);

      final BishopRange range = new BishopRange(northEastSquares, southEastSquares, southWestSquares, northWestSquares);

      testBishopSquares(C2, range);
    }

    // C3
    {

      final List<Square> northEastSquares = Nulls.listOf(D4, E5, F6, G7, H8);
      final List<Square> southEastSquares = Nulls.listOf(D2, E1);
      final List<Square> southWestSquares = Nulls.listOf(B2, A1);
      final List<Square> northWestSquares = Nulls.listOf(B4, A5);

      final BishopRange range = new BishopRange(northEastSquares, southEastSquares, southWestSquares, northWestSquares);

      testBishopSquares(C3, range);
    }

    // E4
    {

      final List<Square> northEastSquares = Nulls.listOf(F5, G6, H7);
      final List<Square> southEastSquares = Nulls.listOf(F3, G2, H1);
      final List<Square> southWestSquares = Nulls.listOf(D3, C2, B1);
      final List<Square> northWestSquares = Nulls.listOf(D5, C6, B7, A8);

      final BishopRange range = new BishopRange(northEastSquares, southEastSquares, southWestSquares, northWestSquares);

      testBishopSquares(E4, range);
    }

    // A8
    {

      final List<Square> northEastSquares = Nulls.listOf();
      final List<Square> southEastSquares = Nulls.listOf(B7, C6, D5, E4, F3, G2, H1);
      final List<Square> southWestSquares = Nulls.listOf();
      final List<Square> northWestSquares = Nulls.listOf();

      final BishopRange range = new BishopRange(northEastSquares, southEastSquares, southWestSquares, northWestSquares);

      testBishopSquares(A8, range);
    }

    // A7
    {

      final List<Square> northEastSquares = Nulls.listOf(B8);
      final List<Square> southEastSquares = Nulls.listOf(B6, C5, D4, E3, F2, G1);
      final List<Square> southWestSquares = Nulls.listOf();
      final List<Square> northWestSquares = Nulls.listOf();

      final BishopRange range = new BishopRange(northEastSquares, southEastSquares, southWestSquares, northWestSquares);

      testBishopSquares(A7, range);
    }

    // A6
    {

      final List<Square> northEastSquares = Nulls.listOf(B7, C8);
      final List<Square> southEastSquares = Nulls.listOf(B5, C4, D3, E2, F1);
      final List<Square> southWestSquares = Nulls.listOf();
      final List<Square> northWestSquares = Nulls.listOf();

      final BishopRange range = new BishopRange(northEastSquares, southEastSquares, southWestSquares, northWestSquares);

      testBishopSquares(A6, range);
    }

    // B8
    {

      final List<Square> northEastSquares = Nulls.listOf();
      final List<Square> southEastSquares = Nulls.listOf(C7, D6, E5, F4, G3, H2);
      final List<Square> southWestSquares = Nulls.listOf(A7);
      final List<Square> northWestSquares = Nulls.listOf();

      final BishopRange range = new BishopRange(northEastSquares, southEastSquares, southWestSquares, northWestSquares);

      testBishopSquares(B8, range);
    }

    // B7
    {

      final List<Square> northEastSquares = Nulls.listOf(C8);
      final List<Square> southEastSquares = Nulls.listOf(C6, D5, E4, F3, G2, H1);
      final List<Square> southWestSquares = Nulls.listOf(A6);
      final List<Square> northWestSquares = Nulls.listOf(A8);

      final BishopRange range = new BishopRange(northEastSquares, southEastSquares, southWestSquares, northWestSquares);

      testBishopSquares(B7, range);
    }

    // B6
    {

      final List<Square> northEastSquares = Nulls.listOf(C7, D8);
      final List<Square> southEastSquares = Nulls.listOf(C5, D4, E3, F2, G1);
      final List<Square> southWestSquares = Nulls.listOf(A5);
      final List<Square> northWestSquares = Nulls.listOf(A7);

      final BishopRange range = new BishopRange(northEastSquares, southEastSquares, southWestSquares, northWestSquares);

      testBishopSquares(B6, range);
    }

    // C8
    {

      final List<Square> northEastSquares = Nulls.listOf();
      final List<Square> southEastSquares = Nulls.listOf(D7, E6, F5, G4, H3);
      final List<Square> southWestSquares = Nulls.listOf(B7, A6);
      final List<Square> northWestSquares = Nulls.listOf();

      final BishopRange range = new BishopRange(northEastSquares, southEastSquares, southWestSquares, northWestSquares);

      testBishopSquares(C8, range);
    }

    // C7
    {

      final List<Square> northEastSquares = Nulls.listOf(D8);
      final List<Square> southEastSquares = Nulls.listOf(D6, E5, F4, G3, H2);
      final List<Square> southWestSquares = Nulls.listOf(B6, A5);
      final List<Square> northWestSquares = Nulls.listOf(B8);

      final BishopRange range = new BishopRange(northEastSquares, southEastSquares, southWestSquares, northWestSquares);

      testBishopSquares(C7, range);
    }

    // C6
    {

      final List<Square> northEastSquares = Nulls.listOf(D7, E8);
      final List<Square> southEastSquares = Nulls.listOf(D5, E4, F3, G2, H1);
      final List<Square> southWestSquares = Nulls.listOf(B5, A4);
      final List<Square> northWestSquares = Nulls.listOf(B7, A8);

      final BishopRange range = new BishopRange(northEastSquares, southEastSquares, southWestSquares, northWestSquares);

      testBishopSquares(C6, range);
    }

    // E5
    {
      final List<Square> northEastSquares = Nulls.listOf(F6, G7, H8);
      final List<Square> southEastSquares = Nulls.listOf(F4, G3, H2);
      final List<Square> southWestSquares = Nulls.listOf(D4, C3, B2, A1);
      final List<Square> northWestSquares = Nulls.listOf(D6, C7, B8);

      final BishopRange range = new BishopRange(northEastSquares, southEastSquares, southWestSquares, northWestSquares);

      testBishopSquares(E5, range);
    }
  }

  private static void testBishopSquares(Square testSquare, BishopRange expectedRange) {
    final BishopRange actualRange = BishopEmptyBoardSquares.getBishopSquares(testSquare);
    assertEquals(actualRange, expectedRange);
  }

  @SuppressWarnings({ "static-method" })
  @Test
  void testQueen() {
    // A1
    {
      // orthogonal
      final List<Square> northSquares = Nulls.listOf(A2, A3, A4, A5, A6, A7, A8);
      final List<Square> eastSquares = Nulls.listOf(B1, C1, D1, E1, F1, G1, H1);
      final List<Square> southSquares = Nulls.listOf();
      final List<Square> westSquares = Nulls.listOf();

      // diagonal
      final List<Square> northEastSquares = Nulls.listOf(B2, C3, D4, E5, F6, G7, H8);
      final List<Square> southEastSquares = Nulls.listOf();
      final List<Square> southWestSquares = Nulls.listOf();
      final List<Square> northWestSquares = Nulls.listOf();

      final QueenRange range = new QueenRange(northSquares, eastSquares, southSquares, westSquares, northEastSquares,
          southEastSquares, southWestSquares, northWestSquares);

      testQueenSquares(A1, range);
    }

    // A3
    {
      // orthogonal
      final List<Square> northSquares = Nulls.listOf(A4, A5, A6, A7, A8);
      final List<Square> eastSquares = Nulls.listOf(B3, C3, D3, E3, F3, G3, H3);
      final List<Square> southSquares = Nulls.listOf(A2, A1);
      final List<Square> westSquares = Nulls.listOf();

      // diagonal
      final List<Square> northEastSquares = Nulls.listOf(B4, C5, D6, E7, F8);
      final List<Square> southEastSquares = Nulls.listOf(B2, C1);
      final List<Square> southWestSquares = Nulls.listOf();
      final List<Square> northWestSquares = Nulls.listOf();

      final QueenRange range = new QueenRange(northSquares, eastSquares, southSquares, westSquares, northEastSquares,
          southEastSquares, southWestSquares, northWestSquares);

      testQueenSquares(A3, range);
    }

    // C8
    {
      // orthogonal
      final List<Square> northSquares = Nulls.listOf();
      final List<Square> eastSquares = Nulls.listOf(D8, E8, F8, G8, H8);
      final List<Square> southSquares = Nulls.listOf(C7, C6, C5, C4, C3, C2, C1);
      final List<Square> westSquares = Nulls.listOf(B8, A8);

      // diagonal
      final List<Square> northEastSquares = Nulls.listOf();
      final List<Square> southEastSquares = Nulls.listOf(D7, E6, F5, G4, H3);
      final List<Square> southWestSquares = Nulls.listOf(B7, A6);
      final List<Square> northWestSquares = Nulls.listOf();

      final QueenRange range = new QueenRange(northSquares, eastSquares, southSquares, westSquares, northEastSquares,
          southEastSquares, southWestSquares, northWestSquares);

      testQueenSquares(C8, range);
    }

    // D4
    {
      // orthogonal
      final List<Square> northSquares = Nulls.listOf(D5, D6, D7, D8);
      final List<Square> eastSquares = Nulls.listOf(E4, F4, G4, H4);
      final List<Square> southSquares = Nulls.listOf(D3, D2, D1);
      final List<Square> westSquares = Nulls.listOf(C4, B4, A4);

      // diagonal
      final List<Square> northEastSquares = Nulls.listOf(E5, F6, G7, H8);
      final List<Square> southEastSquares = Nulls.listOf(E3, F2, G1);
      final List<Square> southWestSquares = Nulls.listOf(C3, B2, A1);
      final List<Square> northWestSquares = Nulls.listOf(C5, B6, A7);

      final QueenRange range = new QueenRange(northSquares, eastSquares, southSquares, westSquares, northEastSquares,
          southEastSquares, southWestSquares, northWestSquares);

      testQueenSquares(D4, range);
    }
  }

  private static void testQueenSquares(Square testSquare, QueenRange range) {
    final QueenRange generatedQueenMoves = QueenEmptyBoardSquares.getQueenSquares(testSquare);
    assertEquals(generatedQueenMoves, range);
  }

  @SuppressWarnings("static-method")
  @Test
  void testKing() {
    // A1
    testKingSquares(A1, A2, B2, B1);

    // A2
    testKingSquares(A2, A3, B3, B2, B1, A1);

    // A3
    testKingSquares(A3, A4, B4, B3, B2, A2);

    // B1
    testKingSquares(B1, B2, C2, C1, A1, A2);

    // B2
    testKingSquares(B2, B3, C3, C2, C1, B1, A1, A2, A3);

    // B3
    testKingSquares(B3, B4, C4, C3, C2, B2, A2, A3, A4);

    // C1
    testKingSquares(C1, C2, D2, D1, B1, B2);

    // C2
    testKingSquares(C2, C3, D3, D2, D1, C1, B1, B2, B3);

    // C3
    testKingSquares(C3, C4, D4, D3, D2, C2, B2, B3, B4);

    // E5
    testKingSquares(E5, E6, F6, F5, F4, E4, D4, D5, D6);

    // G8
    testKingSquares(G8, H8, H7, G7, F7, F8);

    // A8
    testKingSquares(A8, B8, B7, A7);
  }

  private static void testKingSquares(Square testSquare, Square... squares) {
    final Set<Square> lines = new TreeSet<>();
    for (final Square square : squares) {
      @SuppressWarnings("null") @NonNull final Square squareNonNull = square;
      lines.add(squareNonNull);
    }
    final Set<Square> generatedKnightMoves = KingNonCastlingEmptyBoardSquares.getKingSquares(testSquare);
    assertEquals(generatedKnightMoves, lines);
  }

  @SuppressWarnings("static-method")
  @Test
  void testPawn() {

    testPawnSquares(WHITE, A1);
    testPawnSquares(WHITE, B1);
    testPawnSquares(WHITE, C1);
    testPawnSquares(WHITE, D1);
    testPawnSquares(WHITE, E1);
    testPawnSquares(WHITE, F1);
    testPawnSquares(WHITE, G1);
    testPawnSquares(WHITE, H1);

    testPawnSquares(BLACK, A8);
    testPawnSquares(BLACK, B8);
    testPawnSquares(BLACK, C8);
    testPawnSquares(BLACK, D8);
    testPawnSquares(BLACK, E8);
    testPawnSquares(BLACK, F8);
    testPawnSquares(BLACK, G8);
    testPawnSquares(BLACK, H8);

    // A1
    testPawnSquares(WHITE, A2, A3, A4);
    testPawnSquares(WHITE, B2, B3, B4);
    testPawnSquares(WHITE, D4, D5);
    testPawnSquares(WHITE, G7, G8);
    testPawnSquares(WHITE, A7, A8);
    testPawnSquares(WHITE, H7, H8);

    testPawnSquares(BLACK, A7, A6, A5);
    testPawnSquares(BLACK, B7, B6, B5);
    testPawnSquares(BLACK, D5, D4);
    testPawnSquares(BLACK, G2, G1);
    testPawnSquares(BLACK, A2, A1);
    testPawnSquares(BLACK, H2, H1);

  }

  private static void testPawnSquares(Side sideToMove, Square testSquare, Square... squares) {
    final Set<Square> lines = new TreeSet<>();
    for (final Square square : squares) {
      @SuppressWarnings("null") @NonNull final Square squareNonNull = square;
      lines.add(squareNonNull);
    }
    final Set<Square> generatedPawnMoves = PawnAnyAdvanceEmptyBoardSquares.getPawnSquares(sideToMove, testSquare);
    assertEquals(generatedPawnMoves, lines);

  }
}
