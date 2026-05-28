package io.github.dlbbld.ashlarchess.test.san.format;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.san.SanValidateFormat;
import io.github.dlbbld.ashlarchess.san.SanValidationException;
import io.github.dlbbld.ashlarchess.san.SanValidationProblem;

class TestSanValidateFormatKingDisambiguation {

  // --- File specification (never valid for king) ---

  @SuppressWarnings("static-method")
  @Test
  void testFileNonCapturing() {
    // Kae5 - king with file disambiguation, non-capturing
    checkException("Kae5", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_WRONG_DESTINATION_RANK);
    checkException("Kae5+", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_WRONG_DESTINATION_RANK);
    checkException("Kae5#", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_WRONG_DESTINATION_RANK);
  }

  @SuppressWarnings("static-method")
  @Test
  void testFileCapturing() {
    // Kaxe5 - king with file disambiguation, capturing
    checkException("Kaxe5", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_WRONG_DESTINATION_RANK);
    checkException("Kaxe5+", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_WRONG_DESTINATION_RANK);
    checkException("Kaxe5#", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_WRONG_DESTINATION_RANK);
  }

  // --- Rank specification (never valid for king) ---

  @SuppressWarnings("static-method")
  @Test
  void testRankNonCapturing() {
    // K2f3 - king with rank disambiguation, non-capturing
    checkException("K2f3", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_WRONG_DESTINATION_FILE);
    checkException("K2f3+", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_WRONG_DESTINATION_FILE);
    checkException("K2f3#", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_WRONG_DESTINATION_FILE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testRankCapturing() {
    // K2xf3 - king with rank disambiguation, capturing
    checkException("K2xf3", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_WRONG_DESTINATION_FILE);
    checkException("K2xf3+", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_WRONG_DESTINATION_FILE);
    checkException("K2xf3#", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_WRONG_DESTINATION_FILE);
  }

  // --- Square specification (never valid for king) ---

  @SuppressWarnings("static-method")
  @Test
  void testSquareNonCapturing() {
    // Ka2b3 - king with square disambiguation, non-capturing
    checkException("Ka2b3", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_OVERLENGTH);
    checkException("Ka2b3+", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_OVERLENGTH);
    checkException("Ka2b3#", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_OVERLENGTH);
  }

  @SuppressWarnings("static-method")
  @Test
  void testSquareCapturing() {
    // Ka2xb3 - king with square disambiguation, capturing
    checkException("Ka2xb3", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_OVERLENGTH);
    checkException("Ka2xb3+", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_OVERLENGTH);
    checkException("Ka2xb3#", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_OVERLENGTH);
  }

  private static void checkException(String san, SanValidationProblem expectedProblem) {
    boolean isException;
    try {
      SanValidateFormat.validateFormat(san);
      isException = false;
    } catch (final SanValidationException e) {
      isException = true;
      assertEquals(expectedProblem, e.getSanValidationProblem());
    }
    assertTrue(isException);
  }

}
