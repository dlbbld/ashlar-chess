// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.bench;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnableFullAnalyzer;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnableQuickAnalyzer;

/**
 * Unwinnability analysis end to end - the second workload a slider optimization has to pay off in. The helpmate search
 * reaches the same slider generators through the shared bitboard layer, so it inherits any change made there, but its
 * access pattern is nothing like single-position move generation: the search makes and unmakes moves down a deep tree,
 * re-probing the same few squares with slowly changing occupancy.
 *
 * <p>
 * The full analyzer is measured in two separate bands rather than as one average over the corpus. A sweep of the
 * curated oracle found a median around 2.5 ms against a mean around 120 ms, with the slowest 5% of positions taking
 * some 40% of total time - so a single corpus-wide mean is a tail measurement wearing an average's clothes, and it
 * moves whenever the sample composition shifts. Measuring the bulk and the tail separately gives two stable numbers
 * instead of one unstable one, and they can legitimately disagree: the tail is where slider cost compounds across
 * search depth, so it is the band most likely to reward a faster generator.
 *
 * <p>
 * Neither band is filtered by measured runtime at setup time. That would make the fixture set machine-dependent and
 * destroy the cross-machine comparison these benchmarks exist for; the bands are fixed FEN lists instead.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(3)
public class UnwinnabilityBenchmark {

  /** Must equal {@code BenchmarkPositions.unwinnabilityTypicalFens().size()}; checked at setup. */
  private static final int TYPICAL_POSITIONS = 20;

  /** Must equal {@code BenchmarkPositions.unwinnabilityHardFens().size()}; checked at setup. */
  private static final int HARD_POSITIONS = 4;

  /** Positions per invocation for the quick analyzer, which is cheap enough to sweep the corpus broadly. */
  private static final int QUICK_POSITIONS = 256;

  private List<Board> typicalBoards = Nulls.listOf();
  private List<Board> hardBoards = Nulls.listOf();
  private List<Board> quickBoards = Nulls.listOf();

  @Setup(Level.Trial)
  public void setUp() {
    this.typicalBoards = BenchmarkPositions.boardsOf(BenchmarkPositions.unwinnabilityTypicalFens());
    this.hardBoards = BenchmarkPositions.boardsOf(BenchmarkPositions.unwinnabilityHardFens());
    this.quickBoards = BenchmarkPositions.curatedBoards(QUICK_POSITIONS);

    // The @OperationsPerInvocation values below are compile-time constants, so a changed fixture list would silently
    // rescale every reported number. Fail instead.
    if (this.typicalBoards.size() != TYPICAL_POSITIONS) {
      throw new ProgrammingMistakeException(
          "Typical fixture count " + this.typicalBoards.size() + " does not match TYPICAL_POSITIONS");
    }
    if (this.hardBoards.size() != HARD_POSITIONS) {
      throw new ProgrammingMistakeException(
          "Hard fixture count " + this.hardBoards.size() + " does not match HARD_POSITIONS");
    }
    // The quick band was missing this check, and the sampler was quietly returning 239 boards for a requested 256,
    // so every quick score was normalized by a count that was never delivered.
    if (this.quickBoards.size() != QUICK_POSITIONS) {
      throw new ProgrammingMistakeException(
          "Quick fixture count " + this.quickBoards.size() + " does not match QUICK_POSITIONS");
    }
  }

  @Benchmark
  @Warmup(iterations = 5, time = 1)
  @Measurement(iterations = 10, time = 1)
  @OperationsPerInvocation(QUICK_POSITIONS)
  public int unwinnableQuick() {
    int verdicts = 0;
    for (final Board board : this.quickBoards) {
      verdicts += UnwinnableQuickAnalyzer.unwinnableQuick(board, Side.WHITE).verdict().ordinal();
      verdicts += UnwinnableQuickAnalyzer.unwinnableQuick(board, Side.BLACK).verdict().ordinal();
    }
    return verdicts;
  }

  @Benchmark
  @Warmup(iterations = 3, time = 2)
  @Measurement(iterations = 6, time = 2)
  @OperationsPerInvocation(TYPICAL_POSITIONS)
  public int unwinnableFullTypical() {
    return analyzeFull(this.typicalBoards);
  }

  // Each invocation is seconds long, so iteration counts are cut back: the JMH minimum time per iteration is already
  // exceeded by a single invocation, and more iterations would only lengthen the run without narrowing the interval.
  @Benchmark
  @Warmup(iterations = 2, time = 1)
  @Measurement(iterations = 4, time = 1)
  @OperationsPerInvocation(HARD_POSITIONS)
  public int unwinnableFullHard() {
    return analyzeFull(this.hardBoards);
  }

  private static int analyzeFull(List<Board> boards) {
    int verdicts = 0;
    for (final Board board : boards) {
      verdicts += UnwinnableFullAnalyzer.unwinnableFull(board, Side.WHITE).verdict().ordinal();
      verdicts += UnwinnableFullAnalyzer.unwinnableFull(board, Side.BLACK).verdict().ordinal();
    }
    return verdicts;
  }
}
