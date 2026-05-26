package com.dlb.chess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.apache.logging.log4j.Logger;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.utility.BasicUtility;
import com.dlb.chess.model.PgnHalfMove;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.test.model.PgnFen;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

/**
 * Shared body for the strict and lenient FEN-initialization parser tests.
 *
 * <p>
 * Iterates every PGN under the three {@code PARSER_FROM_FEN*} buckets and asserts:
 *
 * <ol>
 * <li>the parser accepts the file (no exception),</li>
 * <li>replaying the parsed halfmove sequence on a {@link Board} starting from the parsed {@code startFen} reaches the
 * FEN recorded as the registered test case's {@code fen()}.</li>
 * </ol>
 *
 * <p>
 * The second step is the actual parser-mechanics check: the registered FEN encodes the expected final halfmove clock
 * and full-move number, so divergence pinpoints either a bad FEN-tag → board-state initialization or a bad clock
 * progression through subsequent moves.
 *
 * <p>
 * Subclasses inject the parser by passing {@code (folder, fileName) -> StrictPgnParser.parse(folder, fileName)} (or the
 * lenient variant) to {@link #runForBuckets}.
 *
 * <p>
 * Logs each fixture's name as it runs so a failure mid-iteration shows progress.
 */
@SuppressWarnings("null") // BiFunction lacks JDT null annotations
abstract class AbstractTestPgnParserHalfMoveClockFromFen {

  private static final List<PgnTest> BUCKETS = Nulls.listOf(PgnTest.PARSER_FROM_FEN);

  protected static void runForBuckets(BiFunction<java.nio.file.Path, String, PgnGame> parse, Logger logger) {
    final List<String> failures = new ArrayList<>();
    var totalFixtures = 0;

    for (final PgnTest bucket : BUCKETS) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(bucket);
      for (final PgnFen testCase : testCaseList.list()) {
        totalFixtures++;
        final String pgnName = testCase.pgnName();
        logger.info(pgnName);

        final PgnGame pgnGame = parse.apply(bucket.getFolderPath(), pgnName);

        final Board board = new Board(pgnGame.startFen());
        for (final PgnHalfMove halfMove : pgnGame.halfMoveList()) {
          board.moveStrict(halfMove.san());
        }

        try {
          assertEquals(testCase.finalFen(), board.getFen(),
              () -> bucket + " / " + pgnName + " — final FEN mismatch (halfmove-clock or move-number drift)");
        } catch (final AssertionError e) {
          failures.add(BasicUtility.getMessage(e));
        }
      }
    }

    if (totalFixtures == 0) {
      fail("No fixtures iterated — bucket wiring is broken");
    }
    if (!failures.isEmpty()) {
      final var report = new StringBuilder().append(failures.size()).append(" of ").append(totalFixtures)
          .append(" PARSER_FROM_FEN* fixtures produced an unexpected final FEN:\n");
      for (final String f : failures) {
        report.append("  ").append(f).append('\n');
      }
      fail(report.toString());
    }
  }
}
