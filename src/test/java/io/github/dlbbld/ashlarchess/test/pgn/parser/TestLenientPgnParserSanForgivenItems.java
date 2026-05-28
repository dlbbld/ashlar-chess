package io.github.dlbbld.ashlarchess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParser;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParserValidationResult;
import io.github.dlbbld.ashlarchess.san.ForgivenItem;
import io.github.dlbbld.ashlarchess.san.LenientSanValidationProblem;
import io.github.dlbbld.ashlarchess.test.pgntest.constants.PgnTestConstants;

/**
 * One fixture per {@link LenientSanValidationProblem} value. Each fixture is a minimal lenient PGN whose movetext
 * contains exactly one move with the named deviation; the rest of the moves (if any) are canonical SAN. The lenient PGN
 * parser must accept the fixture and surface exactly the expected forgiven code.
 */
@SuppressWarnings("static-method")
class TestLenientPgnParserSanForgivenItems {

  private static final Path FOLDER = Nulls.pathResolve(PgnTestConstants.LENIENT_PGN_PARSER_TEST_ROOT_FOLDER_PATH,
      "sanForgivenItems");

  @Test
  void test01_missingCheckSuffix() {
    assertExactlyOneCode("01_missing_check_suffix.pgn", LenientSanValidationProblem.MISSING_CHECK_SUFFIX);
  }

  @Test
  void test02_missingCheckmateSuffix() {
    assertExactlyOneCode("02_missing_checkmate_suffix.pgn", LenientSanValidationProblem.MISSING_CHECKMATE_SUFFIX);
  }

  @Test
  void test03_spuriousCheckSuffix() {
    assertExactlyOneCode("03_spurious_check_suffix.pgn", LenientSanValidationProblem.SPURIOUS_CHECK_SUFFIX);
  }

  @Test
  void test04_spuriousCheckmateSuffix() {
    assertExactlyOneCode("04_spurious_checkmate_suffix.pgn", LenientSanValidationProblem.SPURIOUS_CHECKMATE_SUFFIX);
  }

  @Test
  void test05_wrongCheckSuffixForCheckmate() {
    assertExactlyOneCode("05_wrong_check_suffix_for_checkmate.pgn",
        LenientSanValidationProblem.WRONG_CHECK_SUFFIX_FOR_CHECKMATE);
  }

  @Test
  void test06_wrongCheckmateSuffixForCheck() {
    assertExactlyOneCode("06_wrong_checkmate_suffix_for_check.pgn",
        LenientSanValidationProblem.WRONG_CHECKMATE_SUFFIX_FOR_CHECK);
  }

  @Test
  void test07_missingCaptureMarker() {
    assertExactlyOneCode("07_missing_capture_marker.pgn", LenientSanValidationProblem.MISSING_CAPTURE_MARKER);
  }

  @Test
  void test08_spuriousCaptureMarker() {
    assertExactlyOneCode("08_spurious_capture_marker.pgn", LenientSanValidationProblem.SPURIOUS_CAPTURE_MARKER);
  }

  @Test
  void test09_overspecifiedFileDisambiguation() {
    assertExactlyOneCode("09_overspecified_file_disambiguation.pgn",
        LenientSanValidationProblem.OVERSPECIFIED_FILE_DISAMBIGUATION);
  }

  @Test
  void test10_overspecifiedRankDisambiguation() {
    assertExactlyOneCode("10_overspecified_rank_disambiguation.pgn",
        LenientSanValidationProblem.OVERSPECIFIED_RANK_DISAMBIGUATION);
  }

  @Test
  void test11_overspecifiedSquareDisambiguation() {
    assertExactlyOneCode("11_overspecified_square_disambiguation.pgn",
        LenientSanValidationProblem.OVERSPECIFIED_SQUARE_DISAMBIGUATION);
  }

  @Test
  void test12_nonStandardRankDisambiguation() {
    assertExactlyOneCode("12_non_standard_rank_disambiguation.pgn",
        LenientSanValidationProblem.NON_STANDARD_RANK_DISAMBIGUATION);
  }

  @Test
  void test13_longAlgebraicNotation() {
    assertExactlyOneCode("13_long_algebraic_notation.pgn", LenientSanValidationProblem.LONG_ALGEBRAIC_NOTATION);
  }

  @Test
  void test14_uciNotation() {
    assertExactlyOneCode("14_uci_notation.pgn", LenientSanValidationProblem.UCI_NOTATION);
  }

  @Test
  void test15_zeroInsteadOfOCastling() {
    assertExactlyOneCode("15_zero_instead_of_o_castling.pgn", LenientSanValidationProblem.ZERO_INSTEAD_OF_O_CASTLING);
  }

  @Test
  void test16_explicitPawnLetter() {
    assertExactlyOneCode("16_explicit_pawn_letter.pgn", LenientSanValidationProblem.EXPLICIT_PAWN_LETTER);
  }

  @Test
  void test17_missingPromotionEquals() {
    assertExactlyOneCode("17_missing_promotion_equals.pgn", LenientSanValidationProblem.MISSING_PROMOTION_EQUALS);
  }

  @Test
  void test18_lowercasePieceLetter() {
    assertExactlyOneCode("18_lowercase_piece_letter.pgn", LenientSanValidationProblem.LOWERCASE_PIECE_LETTER);
  }

  @Test
  void test19_uppercaseFileLetter() {
    assertExactlyOneCode("19_uppercase_file_letter.pgn", LenientSanValidationProblem.UPPERCASE_FILE_LETTER);
  }

  @Test
  void test20_uppercaseCaptureMarker() {
    assertExactlyOneCode("20_uppercase_capture_marker.pgn", LenientSanValidationProblem.UPPERCASE_CAPTURE_MARKER);
  }

  @Test
  void test21_lowercasePromotionPiece() {
    assertExactlyOneCode("21_lowercase_promotion_piece.pgn", LenientSanValidationProblem.LOWERCASE_PROMOTION_PIECE);
  }

  // ---------------------------------------------------------------------------
  // Helper.
  // ---------------------------------------------------------------------------

  private static void assertExactlyOneCode(String fixtureFileName, LenientSanValidationProblem expectedCode) {
    final LenientPgnParserValidationResult result = LenientPgnParser.validate(FOLDER, fixtureFileName);
    assertTrue(result.isValid(),
        "Expected valid lenient parse of " + fixtureFileName + " but got: " + result.message());
    assertEquals(1, result.sanForgivenItems().size(),
        "Expected exactly one forgiven item in " + fixtureFileName + " but got: " + result.sanForgivenItems());
    final ForgivenItem item = Nulls.get(result.sanForgivenItems(), 0);
    assertEquals(expectedCode, item.code(),
        "Expected forgiven code " + expectedCode + " in " + fixtureFileName + " but got: " + item.code());
  }
}
