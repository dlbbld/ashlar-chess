package io.github.dlbbld.ashlarchess.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.bitboard.StaticPositionBridge;
import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.SquareType;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityMaterialBitboard;

/**
 * Differential test pairing {@link UnwinnabilityMaterialBitboard} (production) against {@link UnwinnabilityMaterial}
 * (StaticPosition reference oracle). For every fixture in the corpus, every bitboard-keyed predicate must agree with
 * its StaticPosition-keyed counterpart. Guards against drift between the two surfaces - and will continue to do so once
 * {@link UnwinnabilityMaterial} relocates to {@code src/test/} alongside the rest of the StaticPosition layer.
 */
class TestUnwinnabilityMaterialBitboard {

  @SuppressWarnings("static-method")
  @Test
  void corpusBitboardVariantsAgreeWithStaticPosition() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        final StaticPosition sp = StaticPositionBridge.toStaticPosition(testCase.finalPosition().getBitboardPosition());
        final BitboardPosition bp = StaticPositionBridge.fromStaticPosition(sp);
        final String tag = " in fixture " + testCase.pgnName();

        // Any-side existence checks.
        assertEquals(UnwinnabilityMaterial.calculateHasRook(sp), UnwinnabilityMaterialBitboard.calculateHasRook(bp),
            "calculateHasRook (any side)" + tag);
        assertEquals(UnwinnabilityMaterial.calculateHasKnight(sp), UnwinnabilityMaterialBitboard.calculateHasKnight(bp),
            "calculateHasKnight (any side)" + tag);
        assertEquals(UnwinnabilityMaterial.calculateHasQueen(sp), UnwinnabilityMaterialBitboard.calculateHasQueen(bp),
            "calculateHasQueen (any side)" + tag);

        for (final Side side : Side.REAL) {
          final String sideTag = " side=" + side + tag;
          assertEquals(UnwinnabilityMaterial.calculateHasRook(side, sp),
              UnwinnabilityMaterialBitboard.calculateHasRook(side, bp), "calculateHasRook" + sideTag);
          assertEquals(UnwinnabilityMaterial.calculateHasKnight(side, sp),
              UnwinnabilityMaterialBitboard.calculateHasKnight(side, bp), "calculateHasKnight" + sideTag);
          assertEquals(UnwinnabilityMaterial.calculateHasQueen(side, sp),
              UnwinnabilityMaterialBitboard.calculateHasQueen(side, bp), "calculateHasQueen" + sideTag);
          assertEquals(UnwinnabilityMaterial.calculateHasNoRooks(side, sp),
              UnwinnabilityMaterialBitboard.calculateHasNoRooks(side, bp), "calculateHasNoRooks" + sideTag);
          assertEquals(UnwinnabilityMaterial.calculateHasNoKnights(side, sp),
              UnwinnabilityMaterialBitboard.calculateHasNoKnights(side, bp), "calculateHasNoKnights" + sideTag);
          assertEquals(UnwinnabilityMaterial.calculateHasNoBishops(side, sp),
              UnwinnabilityMaterialBitboard.calculateHasNoBishops(side, bp), "calculateHasNoBishops" + sideTag);
          assertEquals(UnwinnabilityMaterial.calculateHasNoPawns(side, sp),
              UnwinnabilityMaterialBitboard.calculateHasNoPawns(side, bp), "calculateHasNoPawns" + sideTag);
          assertEquals(UnwinnabilityMaterial.calculateHasLightSquareBishops(side, sp),
              UnwinnabilityMaterialBitboard.calculateHasLightSquareBishops(side, bp),
              "calculateHasLightSquareBishops" + sideTag);
          assertEquals(UnwinnabilityMaterial.calculateHasDarkSquareBishops(side, sp),
              UnwinnabilityMaterialBitboard.calculateHasDarkSquareBishops(side, bp),
              "calculateHasDarkSquareBishops" + sideTag);
          assertEquals(UnwinnabilityMaterial.calculateHasKingOnly(side, sp),
              UnwinnabilityMaterialBitboard.calculateHasKingOnly(side, bp), "calculateHasKingOnly" + sideTag);
          assertEquals(UnwinnabilityMaterial.calculateHasKingAndKnightOnly(side, sp),
              UnwinnabilityMaterialBitboard.calculateHasKingAndKnightOnly(side, bp),
              "calculateHasKingAndKnightOnly" + sideTag);

          for (final SquareType squareType : SquareType.REAL) {
            assertEquals(UnwinnabilityMaterial.calculateHasNoBishops(side, sp, squareType),
                UnwinnabilityMaterialBitboard.calculateHasNoBishops(side, bp, squareType),
                "calculateHasNoBishops(squareType=" + squareType + ")" + sideTag);
            assertEquals(UnwinnabilityMaterial.calculateHasKingAndBishopsOnly(side, sp, squareType),
                UnwinnabilityMaterialBitboard.calculateHasKingAndBishopsOnly(side, bp, squareType),
                "calculateHasKingAndBishopsOnly(squareType=" + squareType + ")" + sideTag);
          }
        }
      }
    }
  }
}
