package com.dlb.chess.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.constants.ChessConstants;
import com.dlb.chess.common.model.DynamicPosition;

class TestPositionIdentifierUtility {

  @Test
  @SuppressWarnings("static-method")
  void testRepresentation() {
    checkRepresentation(0, 10, 0);
    checkRepresentation(1, 10, 1);
    checkRepresentation(10, 10, 1, 0);
    checkRepresentation(11, 10, 1, 1);
    checkRepresentation(111, 10, 1, 1, 1);
    checkRepresentation(1111, 10, 1, 1, 1, 1);

    checkRepresentation(0, 5, 0);
    checkRepresentation(1, 5, 1);
    checkRepresentation(5, 5, 1, 0);
    checkRepresentation(6, 5, 1, 1);
    checkRepresentation(10, 5, 2, 0);
    checkRepresentation(11, 5, 2, 1);
    checkRepresentation(37, 5, 1, 2, 2);

    checkRepresentation(0, 25, 0);
    checkRepresentation(1, 25, 1);
    checkRepresentation(2, 25, 2);

    checkRepresentation(23, 25, 23);
    checkRepresentation(24, 25, 24);
    checkRepresentation(25, 25, 1, 0);
    checkRepresentation(26, 25, 1, 1);
    checkRepresentation(27, 25, 1, 2);

    checkRepresentation(0, 26, 0);
    checkRepresentation(1, 26, 1);
    checkRepresentation(2, 26, 2);
    checkRepresentation(25, 26, 25);

    checkRepresentation(26, 26, 1, 0);
    checkRepresentation(27, 26, 1, 1);
    checkRepresentation(28, 26, 1, 2);

    checkRepresentation(49, 26, 1, 23);
    checkRepresentation(50, 26, 1, 24);
    checkRepresentation(51, 26, 1, 25);

    checkRepresentation(52, 26, 2, 0);
    checkRepresentation(53, 26, 2, 1);
    checkRepresentation(54, 26, 2, 2);

    checkRepresentation(1, 27, 1);
    checkRepresentation(2, 27, 2);
    checkRepresentation(25, 27, 25);

    checkRepresentation(26, 27, 26);
    checkRepresentation(27, 27, 1, 0);
    checkRepresentation(28, 27, 1, 1);

    checkRepresentation(53, 27, 1, 26);

  }

  private static void checkRepresentation(int number, int base, int... expectedArray) {

    final List<Integer> expected = new ArrayList<>();
    for (final int entry : expectedArray) {
      expected.add(entry);
    }
    final List<Integer> actual = PositionIdentifierUtility.calculateRepresentation(number, base);

    assertEquals(expected, actual);
  }

  @Test
  @SuppressWarnings("static-method")
  void testIdentifer() {
    checkIdentifer("A", 1);
    checkIdentifer("B", 2);
    checkIdentifer("C", 3);
    checkIdentifer("X", 24);
    checkIdentifer("Y", 25);
    checkIdentifer("Z", 26);

    checkIdentifer("AA", 27);
    checkIdentifer("AB", 28);
    checkIdentifer("AC", 29);
    checkIdentifer("AX", 50);
    checkIdentifer("AY", 51);
    checkIdentifer("AZ", 52);

    checkIdentifer("BA", 53);

  }

  private static void checkIdentifer(String expected, int number) {
    assertEquals(expected, PositionIdentifierUtility.calculateIdentifier(number));
  }

  // -------------------------------------------------------------------------------------------
  // calculatePositionIdentifierMap(claimAhead, existing) — added in Phase 1 when the print path
  // moved off raw lists onto the report records. Tests assert the label-assignment contract:
  // claim-ahead positions are visited first in their stored order; existing-only positions get
  // appended labels; the same position appearing in both shares one label.
  // -------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void emptyReportsYieldEmptyMap() {
    final Board board = new Board();
    board.movesStrict("e4", "e5");
    final ThreefoldClaimAheadReport claimAhead = ThreefoldClaimAheadReportBuilder.build(board);
    final ThreefoldExistingReport existing = ThreefoldExistingReportBuilder.build(board.getInitialDynamicPosition(),
        board.getHalfMoveList(), ChessConstants.THREEFOLD_REPETITION_RULE_THRESHOLD);

    final Map<DynamicPosition, String> map = PositionIdentifierUtility.calculatePositionIdentifierMap(claimAhead,
        existing);
    assertEquals(0, map.size(), "no claim-aheads and no existing threefolds -> empty map");
  }

  @SuppressWarnings("static-method")
  @Test
  void claimAheadOverlapsExistingShareSingleLabel() {
    // 8-ply knight shuffle: claim-ahead has the initial-position entry (Ng8 played at ply 8 with
    // hasBeenPlayed == true); existing has the initial-position group. Same position in both
    // reports -> the map must hold a single entry for that position, not two.
    final Board board = new Board();
    board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1", "Ng8");

    final ThreefoldClaimAheadReport claimAhead = ThreefoldClaimAheadReportBuilder.build(board);
    final ThreefoldExistingReport existing = ThreefoldExistingReportBuilder.build(board.getInitialDynamicPosition(),
        board.getHalfMoveList(), ChessConstants.THREEFOLD_REPETITION_RULE_THRESHOLD);

    final Map<DynamicPosition, String> map = PositionIdentifierUtility.calculatePositionIdentifierMap(claimAhead,
        existing);

    final String initialLabel = map.get(board.getInitialDynamicPosition());
    assertTrue(initialLabel != null, "initial position must be present in the label map");
    assertEquals("A", initialLabel, "first distinct position seen in the claim-ahead walk gets label 'A'");
  }

  @SuppressWarnings("static-method")
  @Test
  void labelsAreAssignedABCInClaimAheadFirstOrder() {
    // Long knight shuffle drives the report to multiple distinct repeated positions across both
    // claim-ahead and existing reports. The label-assignment contract: visit claim-ahead entries
    // first in their stored order, assigning A, B, C... to distinct positions; then walk existing
    // and append labels for any positions only-seen-there. Assertion shape: labels form a contiguous
    // {A..Z} prefix and each appears exactly once.
    final Board board = new Board();
    board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1", "Ng8",
                      "Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1", "Ng8");

    final ThreefoldClaimAheadReport claimAhead = ThreefoldClaimAheadReportBuilder.build(board);
    final ThreefoldExistingReport existing = ThreefoldExistingReportBuilder.build(board.getInitialDynamicPosition(),
        board.getHalfMoveList(), ChessConstants.THREEFOLD_REPETITION_RULE_THRESHOLD);

    final Map<DynamicPosition, String> map = PositionIdentifierUtility.calculatePositionIdentifierMap(claimAhead,
        existing);
    assertTrue(map.size() >= 2, "fivefold-of-initial shuffle drives multiple distinct repeated positions");

    // All labels are uppercase single letters (still inside the {A..Z} 26-position range for this
    // fixture); each appears exactly once.
    final List<String> labels = new ArrayList<>(map.values());
    labels.sort(null);
    for (var i = 0; i < labels.size(); i++) {
      final String expected = String.valueOf((char) ('A' + i));
      assertEquals(expected, labels.get(i),
          "labels must form a contiguous prefix A, B, C, ... with no gaps and no repeats");
    }
  }
}
