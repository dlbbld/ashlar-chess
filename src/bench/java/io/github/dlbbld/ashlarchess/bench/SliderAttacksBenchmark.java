// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.bench;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.bitboard.internal.BishopAttacks;
import io.github.dlbbld.ashlarchess.bitboard.internal.QueenAttacks;
import io.github.dlbbld.ashlarchess.bitboard.internal.RookAttacks;
import io.github.dlbbld.ashlarchess.board.Board;

/**
 * The slider attack generators in isolation - the narrowest measurement of the ray-loop versus magic-bitboard
 * question.
 *
 * <p>
 * The occupancy masks are taken from real positions rather than generated at random, because the two implementations
 * respond to occupancy in opposite ways: a ray loop gets *cheaper* as the board fills up (it stops at the first
 * blocker), while a magic lookup costs the same either way. Random occupancies at ~50% density would therefore flatter
 * the ray loop relative to real play, and an empty board would flatter it enormously. Using the actual occupancy mix
 * from the curated corpus is the only way this microbenchmark predicts anything about real use.
 *
 * <p>
 * A microbenchmark win here is necessary but not sufficient: see {@link MoveGenerationBenchmark} for whether it
 * survives amortization into real move generation.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(3)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
public class SliderAttacksBenchmark {

  /** Number of (square, occupancy) probes per invocation. Large enough to dwarf JMH's per-invocation overhead. */
  private static final int PROBES = 1024;

  private int[] squares = new int[0];
  private long[] occupancies = new long[0];

  @Setup
  public void setUp() {
    final List<Board> boards = BenchmarkPositions.curatedBoards(256);
    final int[] probeSquares = new int[PROBES];
    final long[] probeOccupancies = new long[PROBES];

    int written = 0;
    // Walk the corpus repeatedly until the probe arrays are full, pairing every slider's own square with the occupancy
    // it actually sits in. Positions with no sliders contribute nothing, hence the outer repeat.
    while (written < PROBES) {
      for (final Board board : boards) {
        final BitboardPosition position = board.getBitboardPosition();
        final long occupied = position.occupied();
        long sliders = position.whiteBishops() | position.whiteRooks() | position.whiteQueens()
            | position.blackBishops() | position.blackRooks() | position.blackQueens();
        while (sliders != 0L && written < PROBES) {
          probeSquares[written] = Long.numberOfTrailingZeros(sliders);
          probeOccupancies[written] = occupied;
          sliders &= sliders - 1;
          written++;
        }
        if (written >= PROBES) {
          break;
        }
      }
    }

    this.squares = probeSquares;
    this.occupancies = probeOccupancies;
  }

  @Benchmark
  @OperationsPerInvocation(PROBES)
  public long bishopAttacks() {
    long accumulated = 0L;
    for (int i = 0; i < PROBES; i++) {
      accumulated ^= BishopAttacks.attacks(this.squares[i], this.occupancies[i]);
    }
    return accumulated;
  }

  @Benchmark
  @OperationsPerInvocation(PROBES)
  public long rookAttacks() {
    long accumulated = 0L;
    for (int i = 0; i < PROBES; i++) {
      accumulated ^= RookAttacks.attacks(this.squares[i], this.occupancies[i]);
    }
    return accumulated;
  }

  @Benchmark
  @OperationsPerInvocation(PROBES)
  public long queenAttacks() {
    long accumulated = 0L;
    for (int i = 0; i < PROBES; i++) {
      accumulated ^= QueenAttacks.attacks(this.squares[i], this.occupancies[i]);
    }
    return accumulated;
  }
}
