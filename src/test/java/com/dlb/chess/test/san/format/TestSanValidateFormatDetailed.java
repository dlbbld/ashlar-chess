package com.dlb.chess.test.san.format;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.san.SanValidateFormat;
import com.dlb.chess.san.SanValidationException;
import com.dlb.chess.san.SanValidationProblem;

class TestSanValidateFormatDetailed {

  // --- Pawn format: second character ---

  @SuppressWarnings("static-method")
  @Test
  void testPawnSecondCharacter() {
    // valid: d3 (digit), dxe5 (x)
    checkValid("d3");
    checkValid("dxe5");

    // invalid: "dz" - 'z' is not a valid SAN character
    checkException("dz", SanValidationProblem.FORMAT_PAWN_WRONG_SECOND_CHARACTER);
    checkException("dR", SanValidationProblem.FORMAT_PAWN_WRONG_SECOND_CHARACTER);
  }

  @SuppressWarnings("static-method")
  @Test
  void testPawnSecondCharacterLength4() {
    // length 4 pawn, second char neither x nor equals at position 2
    // "dae5" - countFiles > 2 (d, a, e)
    checkException("dae5", SanValidationProblem.FORMAT_PAWN_WRONG_SECOND_CHARACTER);
  }

  // --- Pawn format: capture destination ---

  @SuppressWarnings("static-method")
  @Test
  void testPawnCaptureToFile() {
    // "dx95" - '9' is not a valid SAN character
    checkException("dx95", SanValidationProblem.FORMAT_PAWN_CAPTURE_WRONG_FILE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testPawnCaptureToRank() {
    // "dxea" - countFiles > 2 (d, e, a)
    checkException("dxea", SanValidationProblem.FORMAT_PAWN_CAPTURE_WRONG_RANK);
  }

  // --- Pawn format: promotion ---

  @SuppressWarnings("static-method")
  @Test
  void testPawnPromotionRank() {
    // "da=Q" - second char 'a' is not a rank digit (non-capturing promotion path)
    checkException("da=Q", SanValidationProblem.FORMAT_PAWN_WRONG_SECOND_CHARACTER);
  }

  @SuppressWarnings("static-method")
  @Test
  void testPawnPromotionCaptureStructure() {
    // "dabcde" - countFiles > 2 (d,a,b,c,d,e)
    checkException("dabcde", SanValidationProblem.FORMAT_PAWN_WRONG_SECOND_CHARACTER);
  }

  @SuppressWarnings("static-method")
  @Test
  void testPawnPromotionCaptureToFile() {
    // "dx9e=Q" - '9' is not a valid SAN character
    checkException("dx9e=Q", SanValidationProblem.FORMAT_PAWN_CAPTURE_WRONG_FILE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testPawnPromotionCaptureToRank() {
    // "dxea=Q" - countFiles > 2 (d, e, a)
    checkException("dxea=Q", SanValidationProblem.FORMAT_PAWN_CAPTURE_WRONG_RANK);
  }

  // --- Pawn format: missing promotion on rank 1/8 ---

  @SuppressWarnings("static-method")
  @Test
  void testPawnMissingPromotionNonCapturing() {
    // rank 8 without =piece
    checkException("d8", SanValidationProblem.FORMAT_PAWN_FORWARD_PROMOTION_NO_PROMOTION_SYMBOL);
    checkException("a8", SanValidationProblem.FORMAT_PAWN_FORWARD_PROMOTION_NO_PROMOTION_SYMBOL);
    // rank 1 without =piece
    checkException("d1", SanValidationProblem.FORMAT_PAWN_FORWARD_PROMOTION_NO_PROMOTION_SYMBOL);
    checkException("a1", SanValidationProblem.FORMAT_PAWN_FORWARD_PROMOTION_NO_PROMOTION_SYMBOL);
  }

  @SuppressWarnings("static-method")
  @Test
  void testPawnMissingPromotionCapturing() {
    // capture to rank 8 without =piece
    checkException("dxe8", SanValidationProblem.FORMAT_PAWN_CAPTURE_PROMOTION_NO_PROMOTION_SYMBOL);
    checkException("axb8", SanValidationProblem.FORMAT_PAWN_CAPTURE_PROMOTION_NO_PROMOTION_SYMBOL);
    // capture to rank 1 without =piece
    checkException("dxe1", SanValidationProblem.FORMAT_PAWN_CAPTURE_PROMOTION_NO_PROMOTION_SYMBOL);
    checkException("axb1", SanValidationProblem.FORMAT_PAWN_CAPTURE_PROMOTION_NO_PROMOTION_SYMBOL);
  }

  // --- Pawn format: promotion on wrong rank (treated as length error) ---

  @SuppressWarnings("static-method")
  @Test
  void testPawnPromotionMiddleOfBoardNonCapturing() {
    // =piece on non-promotion rank
    checkException("d3=Q", SanValidationProblem.FORMAT_PAWN_FORWARD_NON_PROMOTION_OVERLENGTH);
    checkException("d4=Q", SanValidationProblem.FORMAT_PAWN_FORWARD_NON_PROMOTION_OVERLENGTH);
    checkException("d7=Q", SanValidationProblem.FORMAT_PAWN_FORWARD_NON_PROMOTION_OVERLENGTH);
  }

  @SuppressWarnings("static-method")
  @Test
  void testPawnPromotionMiddleOfBoardCapturing() {
    // capture with =piece on non-promotion rank
    checkException("dxe5=Q", SanValidationProblem.FORMAT_PAWN_CAPTURE_NON_PROMOTION_OVERLENGTH);
    checkException("dxe3=Q", SanValidationProblem.FORMAT_PAWN_CAPTURE_NON_PROMOTION_OVERLENGTH);
  }

  // --- Pawn format: valid promotion (should not throw) ---

  @SuppressWarnings("static-method")
  @Test
  void testPawnPromotionValid() {
    checkValid("d8=Q");
    checkValid("d1=Q");
    checkValid("dxe8=Q");
    checkValid("dxe1=N");
  }

  // --- Pawn format: length ---

  @SuppressWarnings("static-method")
  @Test
  void testPawnLength() {
    // length 3 starting with file - not a valid pawn length
    checkException("d3e", SanValidationProblem.FORMAT_PAWN_FORWARD_NON_PROMOTION_OVERLENGTH);
    // length 5 starting with file - not a valid pawn length
    checkException("dxe5Q", SanValidationProblem.FORMAT_PAWN_CAPTURE_NON_PROMOTION_OVERLENGTH);
  }

  // --- King format ---

  @SuppressWarnings("static-method")
  @Test
  void testKingDestination() {
    // "K9e" - '9' is not a file letter, 'x', or valid rank digit (1-8)
    checkException("K9e", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_WRONG_DESTINATION_FILE);
    // "KR5" - 'R' is not a file letter, 'x', or rank digit
    checkException("KR5", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_WRONG_DESTINATION_FILE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingSecondCharacter() {
    // "Kae5" - length 4 king, third char 'e' is not a rank digit
    checkException("Kae5", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_WRONG_DESTINATION_RANK);
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingCaptureDestination() {
    // "Kx9e" - after 'x', '9' is not a file letter for the destination
    checkException("Kx9e", SanValidationProblem.FORMAT_KING_NON_CASTLING_CAPTURE_WRONG_DESTINATION_FILE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingLength() {
    // "Ke" - too short, missing destination rank
    checkException("Ke", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_NO_DESTINATION_RANK);
    // "Kxe5a" - too long, valid Kxe5 with an extra char
    checkException("Kxe5a", SanValidationProblem.FORMAT_KING_NON_CASTLING_CAPTURE_OVERLENGTH);
  }

  // --- Piece format (R, N, B, Q) ---

  @SuppressWarnings("static-method")
  @Test
  void testPieceDestination() {
    // "QeR" - length 3, after 'e' (file letter) the third char 'R' is neither rank, file, nor 'x'
    checkException("QeR", SanValidationProblem.FORMAT_RNBQ_FILE_WRONG_THIRD_CHARACTER);
  }

  @SuppressWarnings("static-method")
  @Test
  void testPieceLength() {
    // "Qe" - length 2, file branch has no third character
    checkException("Qe", SanValidationProblem.FORMAT_RNBQ_FILE_NO_THIRD_CHARACTER);
  }

  @SuppressWarnings("static-method")
  @Test
  void testPieceMiddle1() {
    // "Q=e5" - second char '=' is not a valid second character
    checkException("Q=e5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
  }

  @SuppressWarnings("static-method")
  @Test
  void testPieceMiddle2() {
    // "Qabe5" - file-branch, non-capture file disambig; 'e' at pos 3 is not a rank digit
    checkException("Qabe5", SanValidationProblem.FORMAT_RNBQ_NON_CAPTURE_FILE_WRONG_DESTINATION_RANK);
  }

  @SuppressWarnings("static-method")
  @Test
  void testPieceMiddle3() {
    // "Qa3ae5" - file[rank] prefix commits to source-square; 'e' at pos 4 is not a rank digit
    checkException("Qa3ae5", SanValidationProblem.FORMAT_RNBQ_NON_CAPTURE_SQUARE_WRONG_DESTINATION_RANK);
  }

  // --- Castling format ---

  @SuppressWarnings("static-method")
  @Test
  void testCastling() {
    // valid
    checkValid("O-O");
    checkValid("O-O-O");

    // invalid: "O-O-" - starts with O but is neither O-O nor O-O-O
    checkException("O-O-", SanValidationProblem.FORMAT_KING_CASTLING);
    // "O-" - too short
    checkException("O-", SanValidationProblem.FORMAT_KING_CASTLING);
    // "O" - just O
    checkException("O", SanValidationProblem.FORMAT_KING_CASTLING);
  }

  // --- Helpers ---

  private static void checkException(String san, SanValidationProblem expectedProblem) {
    boolean isException;
    try {
      SanValidateFormat.validateFormat(san);
      isException = false;
    } catch (final SanValidationException e) {
      isException = true;
      System.out.println("SAN: " + san + " -> " + e.getMessage());
      assertEquals(expectedProblem, e.getSanValidationProblem());
    }
    assertTrue(isException);
  }

  private static void checkValid(String san) {
      boolean isException = false;
    try {
      SanValidateFormat.validateFormat(san);
    } catch (@SuppressWarnings("unused") final SanValidationException e) {
      isException = true;
    }
    assertFalse(isException);
  }

}
