// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.model.PgnHalfMove;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

/**
 * Times {@link HelpmateSearchBoard}'s search-hot-path operations on the same corpus that
 * {@code MoveGenerationPerformanceSurvey} uses for the Board-path measurements. Captures the wins from Phase B (mutable
 * make/unmake), Phase C (per-depth {@link LegalMoveBuffer} via the sink-based generator), and Phase D (exact structural
 * {@link HelpmateSearchKey} cache key) end-to-end.
 *
 * <p>
 * Two measurements per corpus, both per-root-move:
 * <ol>
 * <li><strong>cycle</strong>: {@code move(spec)} followed immediately by {@code unmove()}. This is the inner loop of
 * the helpmate search - every recursive call does exactly this for every legal move. Reports <em>microseconds per
 * make/unmake pair</em>, which includes the post-make {@code refreshDerivedState} (sink generator filling the per-depth
 * buffer + {@code isInCheck} snapshot).</li>
 * <li><strong>cycle + key</strong>: same as above plus a {@link HelpmateSearchBoard#currentTranspositionKey()} call in
 * the middle. Adds the per-node transposition-cache key construction that the search performs once per recursive
 * call.</li>
 * </ol>
 *
 * <p>
 * {@link HelpmateSearchBoard} construction is outside the timing loop (mirrors real search: the analyzer constructs
 * once at the top, then runs many move/unmove cycles during recursion). Each timing iterates the same set of pre-built
 * search boards through {@code WARMUP_ROUNDS + MEASURE_ROUNDS} rounds.
 *
 * <p>
 * Not a unit test (no {@code @Test} method); run directly as a main class for diagnostic numbers.
 */
public class HelpmateSearchBoardPerformanceSurvey {

  private static final int MAX_POSITIONS_PER_GROUP = 800;
  private static final int WARMUP_ROUNDS = 3;
  private static final int MEASURE_ROUNDS = 20;

  private static final ImmutableList<PgnTest> GROUPS = Nulls.listOf(PgnTest.MAX_MOVES, PgnTest.RANDOM_NO_REPETITION,
      PgnTest.WCC2021, PgnTest.CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR);

  public static void main(String[] args) {
    for (final PgnTest pgnTest : GROUPS) {
      final List<Setup> setupList = buildSetups(pgnTest);
      warmup(setupList);

      final Measurement cycle = measureCycle(setupList);
      final Measurement cycleWithKey = measureCycleWithKey(setupList);

      printResult(pgnTest, setupList, cycle, cycleWithKey);
    }
  }

  private static Measurement measureCycle(List<Setup> setupList) {
    long moveCount = 0L;
    final long start = System.nanoTime();
    for (int round = 0; round < MEASURE_ROUNDS; round++) {
      for (final Setup setup : setupList) {
        final HelpmateSearchBoard searchBoard = setup.searchBoard();
        for (final LegalMove legalMove : setup.rootMoves()) {
          searchBoard.move(legalMove.moveSpecification());
          searchBoard.unmove();
          moveCount++;
        }
      }
    }
    return new Measurement(System.nanoTime() - start, moveCount);
  }

  private static Measurement measureCycleWithKey(List<Setup> setupList) {
    long moveCount = 0L;
    final long start = System.nanoTime();
    for (int round = 0; round < MEASURE_ROUNDS; round++) {
      for (final Setup setup : setupList) {
        final HelpmateSearchBoard searchBoard = setup.searchBoard();
        for (final LegalMove legalMove : setup.rootMoves()) {
          searchBoard.move(legalMove.moveSpecification());
          final HelpmateSearchKey key = searchBoard.currentTranspositionKey();
          // Touch the key so the JIT can't elide the construction.
          if (key.hashCode() == Integer.MIN_VALUE) {
            throw new AssertionError("unlikely hash collision used to defeat dead-code elimination");
          }
          searchBoard.unmove();
          moveCount++;
        }
      }
    }
    return new Measurement(System.nanoTime() - start, moveCount);
  }

  private static List<Setup> buildSetups(PgnTest pgnTest) {
    final List<Setup> result = new ArrayList<>();
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
    for (final PgnFen testCase : testCaseList.list()) {
      if (result.size() >= MAX_POSITIONS_PER_GROUP) {
        break;
      }
      final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(pgnTest.getFolderPath(), testCase.pgnName());
      final Board board = new Board(pgnGame.startFen());
      addSetup(result, board);
      for (final PgnHalfMove halfMove : pgnGame.halfMoveList()) {
        board.moveStrict(halfMove.san());
        addSetup(result, board);
        if (result.size() >= MAX_POSITIONS_PER_GROUP) {
          break;
        }
      }
    }
    return result;
  }

  private static void addSetup(List<Setup> result, Board sourceBoard) {
    final Board fenBoard = new Board(sourceBoard.getFen());
    final HelpmateSearchBoard searchBoard = HelpmateSearchBoard.from(fenBoard);
    final List<LegalMove> rootMoves = List.copyOf(searchBoard.getLegalMoves());
    if (rootMoves.isEmpty()) {
      // Terminal position (checkmate or stalemate): no root moves to cycle through. Skip.
      return;
    }
    result.add(new Setup(searchBoard, rootMoves));
  }

  private static void warmup(List<Setup> setupList) {
    for (int i = 0; i < WARMUP_ROUNDS; i++) {
      measureCycle(setupList);
      measureCycleWithKey(setupList);
    }
  }

  private static void printResult(PgnTest pgnTest, List<Setup> setupList, Measurement cycle, Measurement cycleWithKey) {
    final int totalRootMoves = setupList.stream().mapToInt(s -> s.rootMoves().size()).sum();
    final double denominator = totalRootMoves * (long) MEASURE_ROUNDS;
    final double cycleUs = cycle.nanoseconds() / denominator / 1000.0;
    final double cycleWithKeyUs = cycleWithKey.nanoseconds() / denominator / 1000.0;
    final double keyOverheadUs = cycleWithKeyUs - cycleUs;

    System.out.printf("%s%n", pgnTest);
    System.out.printf("  positions: %,d  root moves: %,d%n", setupList.size(), totalRootMoves);
    System.out.printf("  cycle (move + unmove):           %.3f us/cycle%n", cycleUs);
    System.out.printf("  cycle + currentTranspositionKey: %.3f us/cycle  (key overhead %.3f us)%n%n", cycleWithKeyUs,
        keyOverheadUs);
  }

  private record Setup(HelpmateSearchBoard searchBoard, List<LegalMove> rootMoves) {
  }

  private record Measurement(long nanoseconds, long moveCount) {
  }
}
