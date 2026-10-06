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
import io.github.dlbbld.ashlarchess.bitboard.internal.MagicSliderAttacks;
import io.github.dlbbld.ashlarchess.bitboard.internal.SliderRayAttacks;
import io.github.dlbbld.ashlarchess.board.Board;

/**
 * Ray walk against magic lookup, head to head - the narrowest form of the question.
 *
 * <p>
 * Both implementations are measured in the same run over the same probe arrays, rather than by benchmarking one build
 * and then the other. That removes every difference except the code under test: same JVM, same JIT decisions, same
 * fixtures, same machine state. A before-and-after comparison across two builds cannot rule out drift from any of
 * those, and at the few-nanosecond scale this measures, that drift would be the same size as the effect.
 *
 * <p>
 * The occupancy masks come from real positions rather than random bits, because the two implementations respond to
 * occupancy in opposite ways: a ray walk gets <em>cheaper</em> as the board fills up, since it stops at the first
 * blocker, while a magic lookup costs the same either way. Random occupancies at 50% density would therefore flatter
 * the ray walk against real play, and an empty board would flatter it enormously.
 *
 * <p>
 * A win here is necessary but not sufficient. See {@link MoveGenerationBenchmark} and {@link UnwinnabilityBenchmark}
 * for whether it survives amortization into the work that actually calls these.
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
  public long bishopRay() {
    long accumulated = 0L;
    for (int i = 0; i < PROBES; i++) {
      accumulated ^= SliderRayAttacks.bishop(this.squares[i], this.occupancies[i]);
    }
    return accumulated;
  }

  @Benchmark
  @OperationsPerInvocation(PROBES)
  public long bishopMagic() {
    long accumulated = 0L;
    for (int i = 0; i < PROBES; i++) {
      accumulated ^= MagicSliderAttacks.bishop(this.squares[i], this.occupancies[i]);
    }
    return accumulated;
  }

  @Benchmark
  @OperationsPerInvocation(PROBES)
  public long rookRay() {
    long accumulated = 0L;
    for (int i = 0; i < PROBES; i++) {
      accumulated ^= SliderRayAttacks.rook(this.squares[i], this.occupancies[i]);
    }
    return accumulated;
  }

  @Benchmark
  @OperationsPerInvocation(PROBES)
  public long rookMagic() {
    long accumulated = 0L;
    for (int i = 0; i < PROBES; i++) {
      accumulated ^= MagicSliderAttacks.rook(this.squares[i], this.occupancies[i]);
    }
    return accumulated;
  }

  @Benchmark
  @OperationsPerInvocation(PROBES)
  public long queenRay() {
    long accumulated = 0L;
    for (int i = 0; i < PROBES; i++) {
      accumulated ^= SliderRayAttacks.bishop(this.squares[i], this.occupancies[i])
          | SliderRayAttacks.rook(this.squares[i], this.occupancies[i]);
    }
    return accumulated;
  }

  @Benchmark
  @OperationsPerInvocation(PROBES)
  public long queenMagic() {
    long accumulated = 0L;
    for (int i = 0; i < PROBES; i++) {
      accumulated ^= MagicSliderAttacks.bishop(this.squares[i], this.occupancies[i])
          | MagicSliderAttacks.rook(this.squares[i], this.occupancies[i]);
    }
    return accumulated;
  }
}
