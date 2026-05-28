package io.github.dlbbld.ashlarchess.squares;

import static io.github.dlbbld.ashlarchess.common.utility.ImmutableUtility.constructListSquare;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.annotation.NonNull;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.squares.BishopEmptyBoardSquares;
import io.github.dlbbld.ashlarchess.squares.BishopRange;
import io.github.dlbbld.ashlarchess.squares.KingNonCastlingEmptyBoardSquares;
import io.github.dlbbld.ashlarchess.squares.KnightEmptyBoardSquares;
import io.github.dlbbld.ashlarchess.squares.PawnAnyAdvanceEmptyBoardSquares;
import io.github.dlbbld.ashlarchess.squares.QueenEmptyBoardSquares;
import io.github.dlbbld.ashlarchess.squares.QueenRange;
import io.github.dlbbld.ashlarchess.squares.RookEmptyBoardSquares;
import io.github.dlbbld.ashlarchess.squares.RookRange;

class TestEmptyBoardSquares implements EnumConstants {

  @SuppressWarnings({ "static-method" })
  @Test
  void testRook() {

    {
      // A1

      final ImmutableList<Square> squareListNorth = constructListSquare(A2, A3, A4, A5, A6, A7, A8);
      final ImmutableList<Square> squareListEast = constructListSquare(B1, C1, D1, E1, F1, G1, H1);
      final ImmutableList<Square> squareListSouth = constructListSquare();
      final ImmutableList<Square> squareListWest = constructListSquare();
      final RookRange range = new RookRange(squareListNorth, squareListEast, squareListSouth, squareListWest);

      testRookSquares(A1, range);
    }

    {
      // A2

      final ImmutableList<Square> squareListNorth = constructListSquare(A3, A4, A5, A6, A7, A8);
      final ImmutableList<Square> squareListEast = constructListSquare(B2, C2, D2, E2, F2, G2, H2);
      final ImmutableList<Square> squareListSouth = constructListSquare(A1);
      final ImmutableList<Square> squareListWest = constructListSquare();
      final RookRange range = new RookRange(squareListNorth, squareListEast, squareListSouth, squareListWest);

      testRookSquares(A2, range);
    }

    {
      // B1

      final ImmutableList<Square> squareListNorth = constructListSquare(B2, B3, B4, B5, B6, B7, B8);
      final ImmutableList<Square> squareListEast = constructListSquare(C1, D1, E1, F1, G1, H1);
      final ImmutableList<Square> squareListSouth = constructListSquare();
      final ImmutableList<Square> squareListWest = constructListSquare(A1);
      final RookRange range = new RookRange(squareListNorth, squareListEast, squareListSouth, squareListWest);

      testRookSquares(B1, range);
    }

    {
      // B2

      final ImmutableList<Square> squareListNorth = constructListSquare(B3, B4, B5, B6, B7, B8);
      final ImmutableList<Square> squareListEast = constructListSquare(C2, D2, E2, F2, G2, H2);
      final ImmutableList<Square> squareListSouth = constructListSquare(B1);
      final ImmutableList<Square> squareListWest = constructListSquare(A2);
      final RookRange range = new RookRange(squareListNorth, squareListEast, squareListSouth, squareListWest);

      testRookSquares(B2, range);
    }

    {
      // E5

      final ImmutableList<Square> squareListNorth = constructListSquare(E6, E7, E8);
      final ImmutableList<Square> squareListEast = constructListSquare(F5, G5, H5);
      final ImmutableList<Square> squareListSouth = constructListSquare(E4, E3, E2, E1);
      final ImmutableList<Square> squareListWest = constructListSquare(D5, C5, B5, A5);
      final RookRange range = new RookRange(squareListNorth, squareListEast, squareListSouth, squareListWest);

      testRookSquares(E5, range);
    }

    {
      // D8

      final ImmutableList<Square> squareListNorth = constructListSquare();
      final ImmutableList<Square> squareListEast = constructListSquare(E8, F8, G8, H8);
      final ImmutableList<Square> squareListSouth = constructListSquare(D7, D6, D5, D4, D3, D2, D1);
      final ImmutableList<Square> squareListWest = constructListSquare(C8, B8, A8);

      final RookRange range = new RookRange(squareListNorth, squareListEast, squareListSouth, squareListWest);

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

  private static void testKnightSquares(Square testSquare, Square... squareList) {
    final Set<Square> resultList = new TreeSet<>();
    for (final Square square : squareList) {
      @SuppressWarnings("null") @NonNull final Square squareNonNull = square;
      resultList.add(squareNonNull);
    }
    final Set<Square> generatedKnightMoves = KnightEmptyBoardSquares.getKnightSquares(testSquare);
    assertEquals(generatedKnightMoves, resultList);
  }

  @SuppressWarnings({ "static-method" })
  @Test
  void testBishop() {

    // A1
    {

      final ImmutableList<Square> squareListNorthEast = constructListSquare(B2, C3, D4, E5, F6, G7, H8);
      final ImmutableList<Square> squareListSouthEast = constructListSquare();
      final ImmutableList<Square> squareListSouthWest = constructListSquare();
      final ImmutableList<Square> squareListNorthWest = constructListSquare();

      final BishopRange range = new BishopRange(squareListNorthEast, squareListSouthEast, squareListSouthWest,
          squareListNorthWest);

      testBishopSquares(A1, range);
    }

    // A2
    {

      final ImmutableList<Square> squareListNorthEast = constructListSquare(B3, C4, D5, E6, F7, G8);
      final ImmutableList<Square> squareListSouthEast = constructListSquare(B1);
      final ImmutableList<Square> squareListSouthWest = constructListSquare();
      final ImmutableList<Square> squareListNorthWest = constructListSquare();

      final BishopRange range = new BishopRange(squareListNorthEast, squareListSouthEast, squareListSouthWest,
          squareListNorthWest);

      testBishopSquares(A2, range);
    }

    // A3
    {

      final ImmutableList<Square> squareListNorthEast = constructListSquare(B4, C5, D6, E7, F8);
      final ImmutableList<Square> squareListSouthEast = constructListSquare(B2, C1);
      final ImmutableList<Square> squareListSouthWest = constructListSquare();
      final ImmutableList<Square> squareListNorthWest = constructListSquare();

      final BishopRange range = new BishopRange(squareListNorthEast, squareListSouthEast, squareListSouthWest,
          squareListNorthWest);

      testBishopSquares(A3, range);
    }

    // B1
    {

      final ImmutableList<Square> squareListNorthEast = constructListSquare(C2, D3, E4, F5, G6, H7);
      final ImmutableList<Square> squareListSouthEast = constructListSquare();
      final ImmutableList<Square> squareListSouthWest = constructListSquare();
      final ImmutableList<Square> squareListNorthWest = constructListSquare(A2);

      final BishopRange range = new BishopRange(squareListNorthEast, squareListSouthEast, squareListSouthWest,
          squareListNorthWest);

      testBishopSquares(B1, range);
    }

    // B2
    {

      final ImmutableList<Square> squareListNorthEast = constructListSquare(C3, D4, E5, F6, G7, H8);
      final ImmutableList<Square> squareListSouthEast = constructListSquare(C1);
      final ImmutableList<Square> squareListSouthWest = constructListSquare(A1);
      final ImmutableList<Square> squareListNorthWest = constructListSquare(A3);

      final BishopRange range = new BishopRange(squareListNorthEast, squareListSouthEast, squareListSouthWest,
          squareListNorthWest);

      testBishopSquares(B2, range);
    }

    // B3
    {

      final ImmutableList<Square> squareListNorthEast = constructListSquare(C4, D5, E6, F7, G8);
      final ImmutableList<Square> squareListSouthEast = constructListSquare(C2, D1);
      final ImmutableList<Square> squareListSouthWest = constructListSquare(A2);
      final ImmutableList<Square> squareListNorthWest = constructListSquare(A4);

      final BishopRange range = new BishopRange(squareListNorthEast, squareListSouthEast, squareListSouthWest,
          squareListNorthWest);

      testBishopSquares(B3, range);
    }

    // C1
    {

      final ImmutableList<Square> squareListNorthEast = constructListSquare(D2, E3, F4, G5, H6);
      final ImmutableList<Square> squareListSouthEast = constructListSquare();
      final ImmutableList<Square> squareListSouthWest = constructListSquare();
      final ImmutableList<Square> squareListNorthWest = constructListSquare(B2, A3);

      final BishopRange range = new BishopRange(squareListNorthEast, squareListSouthEast, squareListSouthWest,
          squareListNorthWest);

      testBishopSquares(C1, range);
    }

    // C2
    {

      final ImmutableList<Square> squareListNorthEast = constructListSquare(D3, E4, F5, G6, H7);
      final ImmutableList<Square> squareListSouthEast = constructListSquare(D1);
      final ImmutableList<Square> squareListSouthWest = constructListSquare(B1);
      final ImmutableList<Square> squareListNorthWest = constructListSquare(B3, A4);

      final BishopRange range = new BishopRange(squareListNorthEast, squareListSouthEast, squareListSouthWest,
          squareListNorthWest);

      testBishopSquares(C2, range);
    }

    // C3
    {

      final ImmutableList<Square> squareListNorthEast = constructListSquare(D4, E5, F6, G7, H8);
      final ImmutableList<Square> squareListSouthEast = constructListSquare(D2, E1);
      final ImmutableList<Square> squareListSouthWest = constructListSquare(B2, A1);
      final ImmutableList<Square> squareListNorthWest = constructListSquare(B4, A5);

      final BishopRange range = new BishopRange(squareListNorthEast, squareListSouthEast, squareListSouthWest,
          squareListNorthWest);

      testBishopSquares(C3, range);
    }

    // E4
    {

      final ImmutableList<Square> squareListNorthEast = constructListSquare(F5, G6, H7);
      final ImmutableList<Square> squareListSouthEast = constructListSquare(F3, G2, H1);
      final ImmutableList<Square> squareListSouthWest = constructListSquare(D3, C2, B1);
      final ImmutableList<Square> squareListNorthWest = constructListSquare(D5, C6, B7, A8);

      final BishopRange range = new BishopRange(squareListNorthEast, squareListSouthEast, squareListSouthWest,
          squareListNorthWest);

      testBishopSquares(E4, range);
    }

    // A8
    {

      final ImmutableList<Square> squareListNorthEast = constructListSquare();
      final ImmutableList<Square> squareListSouthEast = constructListSquare(B7, C6, D5, E4, F3, G2, H1);
      final ImmutableList<Square> squareListSouthWest = constructListSquare();
      final ImmutableList<Square> squareListNorthWest = constructListSquare();

      final BishopRange range = new BishopRange(squareListNorthEast, squareListSouthEast, squareListSouthWest,
          squareListNorthWest);

      testBishopSquares(A8, range);
    }

    // A7
    {

      final ImmutableList<Square> squareListNorthEast = constructListSquare(B8);
      final ImmutableList<Square> squareListSouthEast = constructListSquare(B6, C5, D4, E3, F2, G1);
      final ImmutableList<Square> squareListSouthWest = constructListSquare();
      final ImmutableList<Square> squareListNorthWest = constructListSquare();

      final BishopRange range = new BishopRange(squareListNorthEast, squareListSouthEast, squareListSouthWest,
          squareListNorthWest);

      testBishopSquares(A7, range);
    }

    // A6
    {

      final ImmutableList<Square> squareListNorthEast = constructListSquare(B7, C8);
      final ImmutableList<Square> squareListSouthEast = constructListSquare(B5, C4, D3, E2, F1);
      final ImmutableList<Square> squareListSouthWest = constructListSquare();
      final ImmutableList<Square> squareListNorthWest = constructListSquare();

      final BishopRange range = new BishopRange(squareListNorthEast, squareListSouthEast, squareListSouthWest,
          squareListNorthWest);

      testBishopSquares(A6, range);
    }

    // B8
    {

      final ImmutableList<Square> squareListNorthEast = constructListSquare();
      final ImmutableList<Square> squareListSouthEast = constructListSquare(C7, D6, E5, F4, G3, H2);
      final ImmutableList<Square> squareListSouthWest = constructListSquare(A7);
      final ImmutableList<Square> squareListNorthWest = constructListSquare();

      final BishopRange range = new BishopRange(squareListNorthEast, squareListSouthEast, squareListSouthWest,
          squareListNorthWest);

      testBishopSquares(B8, range);
    }

    // B7
    {

      final ImmutableList<Square> squareListNorthEast = constructListSquare(C8);
      final ImmutableList<Square> squareListSouthEast = constructListSquare(C6, D5, E4, F3, G2, H1);
      final ImmutableList<Square> squareListSouthWest = constructListSquare(A6);
      final ImmutableList<Square> squareListNorthWest = constructListSquare(A8);

      final BishopRange range = new BishopRange(squareListNorthEast, squareListSouthEast, squareListSouthWest,
          squareListNorthWest);

      testBishopSquares(B7, range);
    }

    // B6
    {

      final ImmutableList<Square> squareListNorthEast = constructListSquare(C7, D8);
      final ImmutableList<Square> squareListSouthEast = constructListSquare(C5, D4, E3, F2, G1);
      final ImmutableList<Square> squareListSouthWest = constructListSquare(A5);
      final ImmutableList<Square> squareListNorthWest = constructListSquare(A7);

      final BishopRange range = new BishopRange(squareListNorthEast, squareListSouthEast, squareListSouthWest,
          squareListNorthWest);

      testBishopSquares(B6, range);
    }

    // C8
    {

      final ImmutableList<Square> squareListNorthEast = constructListSquare();
      final ImmutableList<Square> squareListSouthEast = constructListSquare(D7, E6, F5, G4, H3);
      final ImmutableList<Square> squareListSouthWest = constructListSquare(B7, A6);
      final ImmutableList<Square> squareListNorthWest = constructListSquare();

      final BishopRange range = new BishopRange(squareListNorthEast, squareListSouthEast, squareListSouthWest,
          squareListNorthWest);

      testBishopSquares(C8, range);
    }

    // C7
    {

      final ImmutableList<Square> squareListNorthEast = constructListSquare(D8);
      final ImmutableList<Square> squareListSouthEast = constructListSquare(D6, E5, F4, G3, H2);
      final ImmutableList<Square> squareListSouthWest = constructListSquare(B6, A5);
      final ImmutableList<Square> squareListNorthWest = constructListSquare(B8);

      final BishopRange range = new BishopRange(squareListNorthEast, squareListSouthEast, squareListSouthWest,
          squareListNorthWest);

      testBishopSquares(C7, range);
    }

    // C6
    {

      final ImmutableList<Square> squareListNorthEast = constructListSquare(D7, E8);
      final ImmutableList<Square> squareListSouthEast = constructListSquare(D5, E4, F3, G2, H1);
      final ImmutableList<Square> squareListSouthWest = constructListSquare(B5, A4);
      final ImmutableList<Square> squareListNorthWest = constructListSquare(B7, A8);

      final BishopRange range = new BishopRange(squareListNorthEast, squareListSouthEast, squareListSouthWest,
          squareListNorthWest);

      testBishopSquares(C6, range);
    }

    // E5
    {
      final ImmutableList<Square> squareListNorthEast = constructListSquare(F6, G7, H8);
      final ImmutableList<Square> squareListSouthEast = constructListSquare(F4, G3, H2);
      final ImmutableList<Square> squareListSouthWest = constructListSquare(D4, C3, B2, A1);
      final ImmutableList<Square> squareListNorthWest = constructListSquare(D6, C7, B8);

      final BishopRange range = new BishopRange(squareListNorthEast, squareListSouthEast, squareListSouthWest,
          squareListNorthWest);

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
      final ImmutableList<Square> squareListNorth = constructListSquare(A2, A3, A4, A5, A6, A7, A8);
      final ImmutableList<Square> squareListEast = constructListSquare(B1, C1, D1, E1, F1, G1, H1);
      final ImmutableList<Square> squareListSouth = constructListSquare();
      final ImmutableList<Square> squareListWest = constructListSquare();

      // diagonal
      final ImmutableList<Square> squareListNorthEast = constructListSquare(B2, C3, D4, E5, F6, G7, H8);
      final ImmutableList<Square> squareListSouthEast = constructListSquare();
      final ImmutableList<Square> squareListSouthWest = constructListSquare();
      final ImmutableList<Square> squareListNorthWest = constructListSquare();

      final QueenRange range = new QueenRange(squareListNorth, squareListEast, squareListSouth, squareListWest,
          squareListNorthEast, squareListSouthEast, squareListSouthWest, squareListNorthWest);

      testQueenSquares(A1, range);
    }

    // A3
    {
      // orthogonal
      final ImmutableList<Square> squareListNorth = constructListSquare(A4, A5, A6, A7, A8);
      final ImmutableList<Square> squareListEast = constructListSquare(B3, C3, D3, E3, F3, G3, H3);
      final ImmutableList<Square> squareListSouth = constructListSquare(A2, A1);
      final ImmutableList<Square> squareListWest = constructListSquare();

      // diagonal
      final ImmutableList<Square> squareListNorthEast = constructListSquare(B4, C5, D6, E7, F8);
      final ImmutableList<Square> squareListSouthEast = constructListSquare(B2, C1);
      final ImmutableList<Square> squareListSouthWest = constructListSquare();
      final ImmutableList<Square> squareListNorthWest = constructListSquare();

      final QueenRange range = new QueenRange(squareListNorth, squareListEast, squareListSouth, squareListWest,
          squareListNorthEast, squareListSouthEast, squareListSouthWest, squareListNorthWest);

      testQueenSquares(A3, range);
    }

    // C8
    {
      // orthogonal
      final ImmutableList<Square> squareListNorth = constructListSquare();
      final ImmutableList<Square> squareListEast = constructListSquare(D8, E8, F8, G8, H8);
      final ImmutableList<Square> squareListSouth = constructListSquare(C7, C6, C5, C4, C3, C2, C1);
      final ImmutableList<Square> squareListWest = constructListSquare(B8, A8);

      // diagonal
      final ImmutableList<Square> squareListNorthEast = constructListSquare();
      final ImmutableList<Square> squareListSouthEast = constructListSquare(D7, E6, F5, G4, H3);
      final ImmutableList<Square> squareListSouthWest = constructListSquare(B7, A6);
      final ImmutableList<Square> squareListNorthWest = constructListSquare();

      final QueenRange range = new QueenRange(squareListNorth, squareListEast, squareListSouth, squareListWest,
          squareListNorthEast, squareListSouthEast, squareListSouthWest, squareListNorthWest);

      testQueenSquares(C8, range);
    }

    // D4
    {
      // orthogonal
      final ImmutableList<Square> squareListNorth = constructListSquare(D5, D6, D7, D8);
      final ImmutableList<Square> squareListEast = constructListSquare(E4, F4, G4, H4);
      final ImmutableList<Square> squareListSouth = constructListSquare(D3, D2, D1);
      final ImmutableList<Square> squareListWest = constructListSquare(C4, B4, A4);

      // diagonal
      final ImmutableList<Square> squareListNorthEast = constructListSquare(E5, F6, G7, H8);
      final ImmutableList<Square> squareListSouthEast = constructListSquare(E3, F2, G1);
      final ImmutableList<Square> squareListSouthWest = constructListSquare(C3, B2, A1);
      final ImmutableList<Square> squareListNorthWest = constructListSquare(C5, B6, A7);

      final QueenRange range = new QueenRange(squareListNorth, squareListEast, squareListSouth, squareListWest,
          squareListNorthEast, squareListSouthEast, squareListSouthWest, squareListNorthWest);

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

  private static void testKingSquares(Square testSquare, Square... squareList) {
    final Set<Square> resultList = new TreeSet<>();
    for (final Square square : squareList) {
      @SuppressWarnings("null") @NonNull final Square squareNonNull = square;
      resultList.add(squareNonNull);
    }
    final Set<Square> generatedKnightMoves = KingNonCastlingEmptyBoardSquares.getKingSquares(testSquare);
    assertEquals(generatedKnightMoves, resultList);
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

  private static void testPawnSquares(Side havingMove, Square testSquare, Square... squareList) {
    final Set<Square> resultList = new TreeSet<>();
    for (final Square square : squareList) {
      @SuppressWarnings("null") @NonNull final Square squareNonNull = square;
      resultList.add(squareNonNull);
    }
    final Set<Square> generatedPawnMoves = PawnAnyAdvanceEmptyBoardSquares.getPawnSquares(havingMove, testSquare);
    assertEquals(generatedPawnMoves, resultList);

  }
}
