package com.dlb.chess.test.unwinnability.oracle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;
import com.dlb.chess.unwinnability.UnwinnabilityQuickVerdict;
import com.dlb.chess.unwinnability.UnwinnableQuickAnalyzer;

/**
 * Tests the geometric {@link PawnWallGeometricAnalyzer#calculate(Board)} verdict on the pawn-wall PGN corpus, with two
 * independent second opinions.
 *
 * <ol>
 * <li>{@link PawnWallKingWalkOracle} — verifies the king-walk reading of the position (neither king can reach the
 * opposing king's square through any sequence of legal moves).</li>
 * <li>{@link UnwinnableQuickAnalyzer} — Ambrona's quick unwinnability check; verifies the position is unwinnable for
 * both sides.</li>
 * </ol>
 *
 * <p>
 * Contract (see {@code pawn-wall-soundness.md}):
 *
 * <ul>
 * <li>{@code geometric_YES} ⟹ {@code BFS_YES} (king-walk soundness — must hold).</li>
 * <li>{@code geometric_YES} ⟹ {@code UnwinnableQuick == UNWINNABLE} for both sides (Ambrona-confirmed unwinnability —
 * must hold).</li>
 * <li>The reverse implications are not required; both second opinions are allowed to detect more positions than the
 * geometric check.</li>
 * </ul>
 *
 * <p>
 * The {@code UnwinnableQuick} cross-check is the main soundness gate. Per the user's framing: the pawn-wall detection
 * is a geometric side product — Ambrona's analyzer is the canonical unwinnability oracle. If the geometric check says
 * YES on a position where {@code UnwinnableQuick} disagrees, the geometric check has a false positive worth
 * investigating.
 *
 * <p>
 * The pawnWall corpus is split into two PGN sub-folders: {@code yes/} (geometric YES expected) and {@code no/}
 * (geometric UNKNOWN expected). Each fixture is exercised here by {@link #testYesFixtures()} or
 * {@link #testNoFixtures()}; piece-placement-only manual tests are intentionally absent because every relevant position
 * is already covered by a PGN fixture.
 */
class TestPawnWallGeometricAnalyzer {

  @SuppressWarnings("static-method")
  @Test
  void testAmbronaTenIsRejected() {
    // ambrona_10 - WINNABLE per CHA-full despite an apparent permanent barrier. The geometric chain
    // a5-b5-b4-c4-c3-d3-d2-e2-f2-g2-h2 is pawn/attack-only and the king-walk BFS confirms trapped, but the
    // helpmate exists: the king captures the undefended a3 pawn (routing through the c2/b3 bishop squares) and
    // White's a2 pawn marches to promotion. The all-pawns-involved check rejects this position because a2 and a3
    // are not part of any spanning chain - they are floating pawns.
    // This fixture lives under the ambrona/ folder, not pawnWall/, so it is not exercised by the folder iteration
    // tests below; we keep the explicit FEN here.
    final Board board = new Board("7k/8/1p6/1Pp5/2Pp4/pB1Pp1p1/P1B1P1P1/3B2K1 b - - 0 1");

    assertEquals(PawnWallVerdict.UNKNOWN, PawnWallGeometricAnalyzer.calculate(board),
        "ambrona_10 has a floating a2/a3 pawn pair - must be rejected by the all-pawns-involved check");
  }

  @SuppressWarnings("static-method")
  @Test
  void testYesFixtures() {
    final List<PgnTestCase> fixtures = PgnTestCaseCatalog.getTestList(PgnTest.CHA_PAWN_WALL_YES).list();

    for (final PgnTestCase testCase : fixtures) {
      final Board board = testCase.finalPosition();
      // Every fixture in yes/ must be geometric YES; this is the contract that justifies the folder split.
      assertEquals(PawnWallVerdict.YES, PawnWallGeometricAnalyzer.calculate(board),
          "yes/ fixture must return geometric YES: " + testCase.pgnName() + " - " + testCase.finalFen());
      // Soundness gate 1: king-walk BFS must agree both kings are trapped.
      assertTrue(PawnWallKingWalkOracle.isKingTrappedBehindPermanentBarrier(board, Side.WHITE),
          "Geometric YES but BFS says White king is not trapped: " + testCase.pgnName() + " - "
              + testCase.finalFen());
      assertTrue(PawnWallKingWalkOracle.isKingTrappedBehindPermanentBarrier(board, Side.BLACK),
          "Geometric YES but BFS says Black king is not trapped: " + testCase.pgnName() + " - "
              + testCase.finalFen());
      // Soundness gate 2: Ambrona's quick unwinnability check must agree the position is unwinnable for both sides.
      assertEquals(UnwinnabilityQuickVerdict.UNWINNABLE,
          UnwinnableQuickAnalyzer.unwinnableQuick(board, Side.WHITE).verdict(),
          "Geometric YES but UnwinnableQuick is not UNWINNABLE for White: " + testCase.pgnName() + " - "
              + testCase.finalFen());
      assertEquals(UnwinnabilityQuickVerdict.UNWINNABLE,
          UnwinnableQuickAnalyzer.unwinnableQuick(board, Side.BLACK).verdict(),
          "Geometric YES but UnwinnableQuick is not UNWINNABLE for Black: " + testCase.pgnName() + " - "
              + testCase.finalFen());
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testNoFixtures() {
    final List<PgnTestCase> fixtures = PgnTestCaseCatalog.getTestList(PgnTest.CHA_PAWN_WALL_NO).list();

    for (final PgnTestCase testCase : fixtures) {
      final Board board = testCase.finalPosition();
      // Every fixture in no/ must be geometric UNKNOWN. Reasons: position is actually winnable (kings on wrong
      // side, en-passant capture available), or the chain fails the all-pawns-involved gate (floating pawns).
      assertEquals(PawnWallVerdict.UNKNOWN, PawnWallGeometricAnalyzer.calculate(board),
          "no/ fixture must return geometric UNKNOWN: " + testCase.pgnName() + " - " + testCase.finalFen());
    }
  }

}
