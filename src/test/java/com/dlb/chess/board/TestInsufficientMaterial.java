package com.dlb.chess.board;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.test.RestrictTestConstants;
import com.dlb.chess.test.model.PgnFen;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;

class TestInsufficientMaterial implements EnumConstants {

  private static final Logger logger = Nulls.getLogger(TestInsufficientMaterial.class);

  @SuppressWarnings("static-method")
  @Test
  void testPgnSample() throws Exception {
    for (final PgnTestCaseList testCaseList : PgnTestCaseCatalog.getRestrictedTestListList()) {
      if (RestrictTestConstants.IS_RESTRICT_PGN_INSUFFICIENT_MATERIAL_TEST) {
        switch (testCaseList.pgnTest()) {
          case BASIC_INSUFFICIENT_MATERIAL_BOTH:
          case BASIC_INSUFFICIENT_MATERIAL_ONLY_WHITE:
          case BASIC_INSUFFICIENT_MATERIAL_ONLY_BLACK:
          case BASIC_INSUFFICIENT_MATERIAL_NONE:
          case BASIC_CHECK_WHITE:
          case BASIC_CHECK_BLACK:
          case BASIC_CHECKMATE_WHITE:
          case BASIC_CHECKMATE_BLACK:
          case BASIC_STALEMATE:
            break;
          // $CASES-OMITTED$
          default:
            continue;
        }
      }
      for (final PgnFen testCase : testCaseList.list()) {
        checkInsufficientMaterial(testCase);
      }
    }
  }

  // Insufficient material is a function of the position alone, not the path. Build a history-less board from the
  // cached final FEN via PgnFen.finalPosition() — no PGN parse, no per-ply replay — and assert that the
  // mechanical bitboard-level computation agrees with the per-side Board predicate combined.
  private static void checkInsufficientMaterial(PgnFen testCase) {

    logger.info(testCase.pgnName());

    final Board board = testCase.finalPosition();

    final var isInsufficientMaterialDirectlyCalculated = calculateIsInsufficientMaterial(board.getBitboardPosition());
    final var isInsufficientMaterialDerived = board.isInsufficientMaterial(Side.WHITE)
        && board.isInsufficientMaterial(Side.BLACK);

    assertEquals(isInsufficientMaterialDirectlyCalculated, isInsufficientMaterialDerived);
  }

  private static boolean calculateIsInsufficientMaterial(BitboardPosition bitboardPosition) {

    // KNvK, KvKN
    if (BoardMaterial.calculateHasKingAndKnightOnly(WHITE, bitboardPosition)
        && BoardMaterial.calculateHasKingOnly(BLACK, bitboardPosition)
        || BoardMaterial.calculateHasKingOnly(WHITE, bitboardPosition)
            && BoardMaterial.calculateHasKingAndKnightOnly(BLACK, bitboardPosition)
        || BoardMaterial.calculateHasKingOnly(WHITE, bitboardPosition)
            && BoardMaterial.calculateHasKingAndBishopOnly(BLACK, bitboardPosition)
        || BoardMaterial.calculateHasKingOnly(WHITE, bitboardPosition)
            && BoardMaterial.calculateHasKingAndBishopOnly(BLACK, bitboardPosition)

    ) {
      return true;
    }

    // K(B^lightSquares)*vK(B^lightSquares)*, K(B^darkSquares)*vK(B^darkSquares)* (includes KvK)
    if (InsufficientMaterialUtility.calculateHasZeroOrMultipleLightSquareBishopOnly(WHITE, bitboardPosition)
        && InsufficientMaterialUtility.calculateHasZeroOrMultipleLightSquareBishopOnly(BLACK, bitboardPosition)
        || InsufficientMaterialUtility.calculateHasZeroOrMultipleDarkSquareBishopOnly(WHITE, bitboardPosition)
            && InsufficientMaterialUtility.calculateHasZeroOrMultipleDarkSquareBishopOnly(BLACK, bitboardPosition)) {
      return true;
    }

    return false;
  }

}
