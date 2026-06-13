// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.utility.BasicUtility;
import io.github.dlbbld.ashlarchess.model.PgnMove;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

/**
 * Shared body for the strict and lenient FEN-initialization parser tests.
 *
 * <p>
 * Iterates every PGN under the three {@code PARSER_FROM_FEN*} buckets and asserts:
 *
 * <ol>
 * <li>the parser accepts the file (no exception),</li>
 * <li>replaying the parsed move sequence on a {@link Board} starting from the parsed {@code startFen} reaches the
 * FEN recorded as the registered test case's {@code fen()}.</li>
 * </ol>
 *
 * <p>
 * The second step is the actual parser-mechanics check: the registered FEN encodes the expected final halfmove clock
 * and fullmove number, so divergence pinpoints either a bad FEN-tag -> board-state initialization or a bad clock
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

  private static final ImmutableList<PgnTest> BUCKETS = Nulls.listOf(PgnTest.PARSER_FROM_FEN);

  protected static void runForBuckets(BiFunction<java.nio.file.Path, String, PgnGame> parse, Logger logger) {
    final List<String> failures = new ArrayList<>();
    int totalFixtures = 0;

    for (final PgnTest bucket : BUCKETS) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(bucket);
      for (final PgnFen testCase : testCaseList.list()) {
        totalFixtures++;
        final String pgnName = testCase.pgnName();
        logger.info(pgnName);

        final PgnGame pgnGame = parse.apply(bucket.getFolderPath(), pgnName);

        final Board board = new Board(pgnGame.startFen());
        for (final PgnMove move : pgnGame.moveList()) {
          board.moveStrict(move.san());
        }

        try {
          assertEquals(testCase.finalFen(), board.getFen(),
              () -> bucket + " / " + pgnName + " - final FEN mismatch (halfmove clock or move-number drift)");
        } catch (final AssertionError e) {
          failures.add(BasicUtility.getMessage(e));
        }
      }
    }

    if (totalFixtures == 0) {
      fail("No fixtures iterated - bucket wiring is broken");
    }
    if (!failures.isEmpty()) {
      final StringBuilder report = new StringBuilder().append(failures.size()).append(" of ").append(totalFixtures)
          .append(" PARSER_FROM_FEN* fixtures produced an unexpected final FEN:\n");
      for (final String f : failures) {
        report.append("  ").append(f).append('\n');
      }
      fail(report.toString());
    }
  }
}
