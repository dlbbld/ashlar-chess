package com.dlb.chess.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.bitboard.BitboardPositionUtility;
import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.SquareType;
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

/**
 * Differential test for the bitboard variants of {@link UnwinnabilityMaterial}: for every fixture in the corpus,
 * every bitboard-keyed predicate must agree with its {@link StaticPosition}-keyed counterpart. The bitboard
 * variants are pure additions; this test guards against drift between the two surfaces as callers migrate during
 * the switchover release.
 */
class TestUnwinnabilityMaterialBitboard {

  @SuppressWarnings("static-method")
  @Test
  void corpusBitboardVariantsAgreeWithStaticPosition() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnTestCase testCase : testCaseList.list()) {
        final StaticPosition sp = testCase.finalPosition().getStaticPosition();
        final BitboardPosition bp = BitboardPositionUtility.fromStaticPosition(sp);
        final String tag = " in fixture " + testCase.pgnName();

        // Any-side existence checks.
        assertEquals(UnwinnabilityMaterial.calculateHasRook(sp), UnwinnabilityMaterial.calculateHasRook(bp),
            "calculateHasRook (any side)" + tag);
        assertEquals(UnwinnabilityMaterial.calculateHasKnight(sp), UnwinnabilityMaterial.calculateHasKnight(bp),
            "calculateHasKnight (any side)" + tag);
        assertEquals(UnwinnabilityMaterial.calculateHasQueen(sp), UnwinnabilityMaterial.calculateHasQueen(bp),
            "calculateHasQueen (any side)" + tag);

        for (final Side side : new Side[] { Side.WHITE, Side.BLACK }) {
          final String sideTag = " side=" + side + tag;
          assertEquals(UnwinnabilityMaterial.calculateHasRook(side, sp),
              UnwinnabilityMaterial.calculateHasRook(side, bp), "calculateHasRook" + sideTag);
          assertEquals(UnwinnabilityMaterial.calculateHasKnight(side, sp),
              UnwinnabilityMaterial.calculateHasKnight(side, bp), "calculateHasKnight" + sideTag);
          assertEquals(UnwinnabilityMaterial.calculateHasQueen(side, sp),
              UnwinnabilityMaterial.calculateHasQueen(side, bp), "calculateHasQueen" + sideTag);
          assertEquals(UnwinnabilityMaterial.calculateHasNoRooks(side, sp),
              UnwinnabilityMaterial.calculateHasNoRooks(side, bp), "calculateHasNoRooks" + sideTag);
          assertEquals(UnwinnabilityMaterial.calculateHasNoKnights(side, sp),
              UnwinnabilityMaterial.calculateHasNoKnights(side, bp), "calculateHasNoKnights" + sideTag);
          assertEquals(UnwinnabilityMaterial.calculateHasNoBishops(side, sp),
              UnwinnabilityMaterial.calculateHasNoBishops(side, bp), "calculateHasNoBishops" + sideTag);
          assertEquals(UnwinnabilityMaterial.calculateHasNoPawns(side, sp),
              UnwinnabilityMaterial.calculateHasNoPawns(side, bp), "calculateHasNoPawns" + sideTag);
          assertEquals(UnwinnabilityMaterial.calculateHasLightSquareBishops(side, sp),
              UnwinnabilityMaterial.calculateHasLightSquareBishops(side, bp),
              "calculateHasLightSquareBishops" + sideTag);
          assertEquals(UnwinnabilityMaterial.calculateHasDarkSquareBishops(side, sp),
              UnwinnabilityMaterial.calculateHasDarkSquareBishops(side, bp),
              "calculateHasDarkSquareBishops" + sideTag);
          assertEquals(UnwinnabilityMaterial.calculateHasKingOnly(side, sp),
              UnwinnabilityMaterial.calculateHasKingOnly(side, bp), "calculateHasKingOnly" + sideTag);
          assertEquals(UnwinnabilityMaterial.calculateHasKingAndKnightOnly(side, sp),
              UnwinnabilityMaterial.calculateHasKingAndKnightOnly(side, bp),
              "calculateHasKingAndKnightOnly" + sideTag);

          for (final SquareType squareType : new SquareType[] { SquareType.LIGHT_SQUARE, SquareType.DARK_SQUARE }) {
            assertEquals(UnwinnabilityMaterial.calculateHasNoBishops(side, sp, squareType),
                UnwinnabilityMaterial.calculateHasNoBishops(side, bp, squareType),
                "calculateHasNoBishops(squareType=" + squareType + ")" + sideTag);
            assertEquals(UnwinnabilityMaterial.calculateHasKingAndBishopsOnly(side, sp, squareType),
                UnwinnabilityMaterial.calculateHasKingAndBishopsOnly(side, bp, squareType),
                "calculateHasKingAndBishopsOnly(squareType=" + squareType + ")" + sideTag);
          }
        }
      }
    }
  }
}
