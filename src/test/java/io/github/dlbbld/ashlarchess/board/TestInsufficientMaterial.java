// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.BoardMaterial;
import io.github.dlbbld.ashlarchess.board.InsufficientMaterialUtility;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.test.RestrictTestConstants;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;

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
  // cached final FEN via PgnFen.finalPosition() - no PGN parse, no per-ply replay - and assert that the
  // mechanical bitboard-level computation agrees with the per-side Board predicate combined.
  private static void checkInsufficientMaterial(PgnFen testCase) {

    logger.info(testCase.pgnName());

    final Board board = testCase.finalPosition();

    final boolean isInsufficientMaterialDirectlyCalculated = calculateIsInsufficientMaterial(
        board.getBitboardPosition());
    final boolean isInsufficientMaterialDerived = board.isInsufficientMaterial(Side.WHITE)
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
