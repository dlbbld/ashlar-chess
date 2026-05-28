package com.dlb.chess.test.custom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.test.common.exceptions.SetupException;
import com.dlb.chess.test.common.utility.DiagonalLineUtility;

class TestDiagonalUtility implements EnumConstants {

  @SuppressWarnings("static-method")
  @Test
  void testDiagonalSetContent() {
    for (final Square square : Square.REAL) {
      assertEquals(calculateLeftToRightUpDiagonalUsingWhile(square),
          DiagonalLineUtility.calculateLeftToRightUpDiagonal(square));
      assertEquals(calculateLeftToRightDownDiagonalUsingWhile(square),
          DiagonalLineUtility.calculateLeftToRightDownDiagonal(square));

      assertEquals(calculateLeftToRightUpDiagonalUsingNumbers(square),
          DiagonalLineUtility.calculateLeftToRightUpDiagonal(square));
      assertEquals(calculateLeftToRightDownDiagonalUsingNumbers(square),
          DiagonalLineUtility.calculateLeftToRightDownDiagonal(square));
    }
  }

  private static List<Square> calculateLeftToRightUpDiagonalUsingWhile(Square square) {
    // in the tests using unbounded loops is a smaller problem than in the production code
    // this and for performance reasons we are not calculation the diagonal in the production code

    final List<Square> diagonal = new ArrayList<>();

    // first we search the most left square of the diagonal
    Square mostLeftSquare = square;
    while (Square.calculateHasRightDiagonalSquare(BLACK, mostLeftSquare)) {
      mostLeftSquare = Square.calculateRightDiagonalSquare(BLACK, mostLeftSquare);
    }

    // now we calculate the diagonal
    Square diagonalSquare = mostLeftSquare;
    diagonal.add(diagonalSquare);
    while (Square.calculateHasRightDiagonalSquare(WHITE, diagonalSquare)) {
      diagonalSquare = Square.calculateRightDiagonalSquare(WHITE, diagonalSquare);
      diagonal.add(diagonalSquare);
    }
    return diagonal;
  }

  private static List<Square> calculateLeftToRightDownDiagonalUsingWhile(Square square) {
    // in the tests using unbounded loops is a smaller problem than in the production code
    // this and for performance reasons we are not calculation the diagonal in the production code

    final List<Square> diagonal = new ArrayList<>();

    // first we search the most left square of the diagonal
    Square mostLeftSquare = square;
    while (Square.calculateHasLeftDiagonalSquare(WHITE, mostLeftSquare)) {
      mostLeftSquare = Square.calculateLeftDiagonalSquare(WHITE, mostLeftSquare);
    }

    // now we calculate the diagonal
    Square diagonalSquare = mostLeftSquare;
    diagonal.add(diagonalSquare);
    while (Square.calculateHasLeftDiagonalSquare(BLACK, diagonalSquare)) {
      diagonalSquare = Square.calculateLeftDiagonalSquare(BLACK, diagonalSquare);
      diagonal.add(diagonalSquare);
    }
    return diagonal;
  }

  private static List<Square> calculateLeftToRightUpDiagonalUsingNumbers(Square square) {

    final List<Square> diagonal = new ArrayList<>();

    final int leftNumberOfSquares = Math.min(square.getFile().getNumber(), square.getRank().getNumber()) - 1;
    final int rightNumberOfSquares = Math.min(9 - square.getFile().getNumber(), 9 - square.getRank().getNumber()) - 1;

    final int startingFileNumber = square.getFile().getNumber() - leftNumberOfSquares;
    final int startingRankNumber = square.getRank().getNumber() - leftNumberOfSquares;

    final int endFileNumber = square.getFile().getNumber() + rightNumberOfSquares;

    if (startingFileNumber > endFileNumber) {
      throw new SetupException("That is not possible");
    }

    int rankNumber = startingRankNumber;
    for (int fileNumber = startingFileNumber; fileNumber <= endFileNumber; fileNumber++) {
      final Square diagonalSquare = Square.calculate(fileNumber, rankNumber);
      diagonal.add(diagonalSquare);
      rankNumber++;
    }

    return diagonal;
  }

  private static List<Square> calculateLeftToRightDownDiagonalUsingNumbers(Square square) {

    final List<Square> diagonal = new ArrayList<>();

    final int fileNumber = square.getFile().getNumber();
    final int rankNumber = square.getRank().getNumber();

    final int leftNumberOfSquares = Math.min(fileNumber, 9 - rankNumber) - 1;
    final int rightNumberOfSquares = Math.min(9 - fileNumber, rankNumber) - 1;

    final int startingFileNumber = fileNumber - leftNumberOfSquares;
    final int startingRankNumber = rankNumber + leftNumberOfSquares;
    final int endFileNumber = fileNumber + rightNumberOfSquares;

    if (startingFileNumber > endFileNumber) {
      throw new SetupException("That is not possible");
    }

    int rankNumberProcess = startingRankNumber;
    for (int fileNumberProcess = startingFileNumber; fileNumberProcess <= endFileNumber; fileNumberProcess++) {
      final Square diagonalSquare = Square.calculate(fileNumberProcess, rankNumberProcess);
      diagonal.add(diagonalSquare);
      rankNumberProcess--;
    }

    return diagonal;
  }

  @SuppressWarnings("static-method")
  @Test
  void testIsOnDiagonalLineOrNot() {

    checkIsOnDiagonalLineException(A1, A1);
    checkIsOnDiagonalLineException(D4, D4);
    checkIsOnDiagonalLineException(E6, E6);

    checkIsOnDiagonalLine(A1, B2);
    checkIsOnDiagonalLine(H1, G2);
    checkIsOnDiagonalLine(H8, G7);
    checkIsOnDiagonalLine(A8, B7);

    checkIsOnDiagonalLine(A1, H8);
    checkIsOnDiagonalLine(H1, A8);

    checkIsOnDiagonalLine(D3, A6);
    checkIsOnDiagonalLine(D3, E4);
    checkIsOnDiagonalLine(D3, F1);
    checkIsOnDiagonalLine(D3, C2);

    checkIsOnDiagonalLine(E6, D7);
    checkIsOnDiagonalLine(E6, G8);
    checkIsOnDiagonalLine(E6, G4);
    checkIsOnDiagonalLine(E6, D5);

    checkIsNotOnDiagonalLine(A1, B1);
    checkIsNotOnDiagonalLine(A1, A3);
    checkIsNotOnDiagonalLine(B2, G8);
    checkIsNotOnDiagonalLine(B2, B4);
    checkIsNotOnDiagonalLine(B3, C8);
    checkIsNotOnDiagonalLine(B3, D3);

    checkIsOnDiagonalLineException(A1, A1, C3);
    checkIsOnDiagonalLineException(A1, B2, A1);
    checkIsOnDiagonalLineException(A1, B2, B2);

    checkIsOnDiagonalLine(A1, B2, C3);
    checkIsOnDiagonalLine(A8, B7, C6);

    checkIsOnDiagonalLine(C3, E5, F6);
    checkIsOnDiagonalLine(C4, A6, E2);

    checkIsNotOnDiagonalLine(C3, B4, F6);
    checkIsNotOnDiagonalLine(D6, E5, F6);
    checkIsNotOnDiagonalLine(G5, E5, F6);

  }

  private static void checkIsOnDiagonalLineException(Square fromSquare, Square toSquare) {
    boolean isException;
    try {
      DiagonalLineUtility.calculateIsOnDiagonalLine(fromSquare, toSquare);
      isException = false;
    } catch (@SuppressWarnings("unused") final IllegalArgumentException e) {
      isException = true;
    }
    assertTrue(isException);
  }

  private static void checkIsOnDiagonalLineException(Square fromSquare, Square intermediarySquare, Square toSquare) {
    boolean isException;
    try {
      DiagonalLineUtility.calculateIsOnDiagonalLine(fromSquare, intermediarySquare, toSquare);
      isException = false;
    } catch (@SuppressWarnings("unused") final IllegalArgumentException e) {
      isException = true;
    }
    assertTrue(isException);
  }

  private static void checkIsOnDiagonalLine(Square fromSquare, Square toSquare) {
    assertTrue(DiagonalLineUtility.calculateIsOnDiagonalLine(fromSquare, toSquare));
    assertTrue(DiagonalLineUtility.calculateIsOnDiagonalLine(toSquare, fromSquare));
  }

  private static void checkIsOnDiagonalLine(Square fromSquare, Square intermediarySquare, Square toSquare) {
    assertTrue(DiagonalLineUtility.calculateIsOnDiagonalLine(fromSquare, intermediarySquare, toSquare));
    assertTrue(DiagonalLineUtility.calculateIsOnDiagonalLine(fromSquare, toSquare, intermediarySquare));
    assertTrue(DiagonalLineUtility.calculateIsOnDiagonalLine(intermediarySquare, fromSquare, toSquare));
    assertTrue(DiagonalLineUtility.calculateIsOnDiagonalLine(intermediarySquare, toSquare, fromSquare));
    assertTrue(DiagonalLineUtility.calculateIsOnDiagonalLine(toSquare, fromSquare, intermediarySquare));
    assertTrue(DiagonalLineUtility.calculateIsOnDiagonalLine(toSquare, intermediarySquare, fromSquare));
  }

  private static void checkIsNotOnDiagonalLine(Square fromSquare, Square toSquare) {
    assertFalse(DiagonalLineUtility.calculateIsOnDiagonalLine(fromSquare, toSquare));
    assertFalse(DiagonalLineUtility.calculateIsOnDiagonalLine(toSquare, fromSquare));
  }

  private static void checkIsNotOnDiagonalLine(Square fromSquare, Square intermediarySquare, Square toSquare) {
    assertFalse(DiagonalLineUtility.calculateIsOnDiagonalLine(fromSquare, intermediarySquare, toSquare));
    assertFalse(DiagonalLineUtility.calculateIsOnDiagonalLine(fromSquare, toSquare, intermediarySquare));
    assertFalse(DiagonalLineUtility.calculateIsOnDiagonalLine(intermediarySquare, fromSquare, toSquare));
    assertFalse(DiagonalLineUtility.calculateIsOnDiagonalLine(intermediarySquare, toSquare, fromSquare));
    assertFalse(DiagonalLineUtility.calculateIsOnDiagonalLine(toSquare, fromSquare, intermediarySquare));
    assertFalse(DiagonalLineUtility.calculateIsOnDiagonalLine(toSquare, intermediarySquare, fromSquare));
  }

}
