package com.dlb.chess.test.board;

import static org.junit.jupiter.api.Assertions.fail;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.model.PgnHalfMove;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.test.model.PgnFen;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;

/**
 * Verifies the {@link com.dlb.chess.board.Board#unperformMove} contract: after performing a move and immediately
 * unperforming it, the board must be in exactly the same state it was in before the move. Run across every halfmove of
 * every PGN in the basic test corpus.
 *
 * <h2>Design</h2>
 *
 * <p>
 * For each PGN, two independent boards are kept side-by-side:
 *
 * <ul>
 * <li>{@code expected} - only ever moves <em>forward</em>. It serves as the oracle; its state after halfmove
 * {@code i-1} is the canonical pre-halfmove-{@code i} state, produced solely by {@code performMove} (independent of
 * {@code unperformMove}, the unit under test).</li>
 * <li>{@code actual} - performs and then unperforms each halfmove, then is asserted to equal {@code expected}, then
 * advanced by performing the move so the next iteration starts in lockstep.</li>
 * </ul>
 *
 * <p>
 * Equality is determined by {@link EqualsBuilder#reflectionEquals(Object, Object)}: every declared field on
 * {@code Board} (including all per-halfmove history lists) is compared. New fields added to {@code Board} in the future
 * are picked up automatically - the test does not need to be updated when {@code Board}'s state representation grows.
 *
 * <h2>Scope</h2>
 *
 * <p>
 * Iterates every {@link com.dlb.chess.test.pgntest.enums.PgnTest} category marked basic, no cap on files per folder.
 * {@code PARSER_FROM_FEN} fixtures (custom start positions) are included so the contract is verified beyond the
 * standard initial position too.
 *
 * <p>
 * The regular corpus is asserted to contain only fixtures that fully replay under the strict-game invariant (see
 * {@code TestPgnCorpusNotPlaysBeyondAudit}); this test relies on that and lets any plays-beyond exception propagate as
 * a test failure rather than silently skipping.
 */
class TestBoardUnperformMove {

  private static final Logger logger = Nulls.getLogger(TestBoardUnperformMove.class);

  @SuppressWarnings("static-method")
  @Test
  void test() {
    var pgnsExercised = 0;
    var halfMovesExercised = 0;

    for (final PgnTestCaseList testCaseList : PgnTestCaseCatalog.getParserIntegrationSmokeList()) {
      for (final PgnFen testCase : testCaseList.list()) {
        logger.info(testCase.pgnName());
        halfMovesExercised += runUnperformContractTest(testCaseList, testCase);
        pgnsExercised++;
      }
    }

    if (pgnsExercised == 0) {
      fail("No basic PGNs were exercised - test or corpus is mis-configured");
    }
    logger.info("TestBoardUnperformMove: {} basic PGNs verified ({} halfmoves).", pgnsExercised, halfMovesExercised);
  }

  /**
   * Runs the perform/unperform contract test for a single PGN: for each halfmove the PGN records, performs and
   * immediately unperforms it on {@code actual}, then asserts {@code actual} equals the parallel forward-only
   * {@code expected} board. Returns the number of halfmoves verified.
   */
  private static int runUnperformContractTest(PgnTestCaseList testCaseList, PgnFen testCase) {
    final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(testCaseList.pgnTest().getFolderPath(),
        testCase.pgnName());

    final Board expected = new Board(pgnGame.startFen());
    final Board actual = new Board(pgnGame.startFen());

    var halfMoveIndex = 0;
    for (final PgnHalfMove halfMove : pgnGame.halfMoveList()) {
      halfMoveIndex++;
      final String san = halfMove.san();

      // Test: perform then unperform on actual; it must return to the pre-move state.
      actual.moveStrict(san);
      actual.unmove();
      assertBoardsEqual(expected, actual, testCase.pgnName(), halfMoveIndex, san);

      // Advance both boards by the (now-unperformed) move so the next iteration starts in lockstep.
      expected.moveStrict(san);
      actual.moveStrict(san);
    }
    return halfMoveIndex;
  }

  private static void assertBoardsEqual(Board expected, Board actual, String pgnName, int halfMoveIndex,
      String san) {
    if (!EqualsBuilder.reflectionEquals(expected, actual)) {
      fail("Boards differ in " + pgnName + " after perform+unperform of halfmove " + halfMoveIndex + " (" + san
          + ")");
    }
  }
}
