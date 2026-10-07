// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.bench;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.eclipse.jdt.annotation.NonNull;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.bitboard.internal.BitboardLegalMoveFactory;
import io.github.dlbbld.ashlarchess.board.LegalMove;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.CastlingRight;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.internal.Nulls;

/**
 * The production move-generation path, which is where a slider optimization has to prove itself. A microbenchmark win
 * on the attack generators can be amortized away here by the surrounding work (king safety, move records, castling
 * checks), and this is the number that decides whether the complexity is earned.
 *
 * <p>
 * Every benchmark does exactly {@link #PROBES} units of work regardless of {@code positionCount}, cycling through the
 * fixtures when there are fewer. That is the point of the parameter: it varies the working-set size while holding the
 * work constant, so the two rows differ only in cache pressure. Working-set size is the variable that decides the
 * magic-bitboard question - a handful of positions keeps the attack tables hot in L1 and flatters a table lookup,
 * while a broad sweep makes the tables compete for cache with the positions themselves. A verdict that holds at both
 * ends is a real verdict; one that holds only at the small end is an artifact.
 *
 * <p>
 * {@code pinnedPieces} and {@code legalMoves} are the consumers of the pin and squares-between ray walks, so they also
 * carry the separate verdict on replacing those with precomputed tables.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(3)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
public class MoveGenerationBenchmark {

  /** Work units per invocation, held constant across all {@code positionCount} values. */
  private static final int PROBES = 512;

  @Param({ "8", "512" })
  private int positionCount;

  // Element types are annotated explicitly: the package default makes the array reference non-null but leaves the
  // element type unannotated, so every read handed to the annotated bitboard API would need an unchecked conversion.
  // Annotating here rather than switching to lists keeps the measured loop unchanged.
  private @NonNull BitboardPosition[] positions = new @NonNull BitboardPosition[0];
  private @NonNull Side[] sides = new @NonNull Side[0];
  private @NonNull CastlingRight[] castlingRights = new @NonNull CastlingRight[0];
  private long[] enPassantBits = new long[0];
  private int fixtureCount;

  // Allocated once per trial: a counting sink keeps the measurement on generation cost. A collecting sink would
  // charge list growth and copying to the slider implementation under test.
  private int generatedMoveCount;
  private Consumer<LegalMove> countingSink = move -> {
    // Placeholder so the field is never null; setUp installs the real counting sink before any benchmark runs.
  };

  @Setup(Level.Trial)
  public void setUp() {
    final List<Board> boards = new ArrayList<>();
    for (final String fen : BenchmarkPositions.moveGenerationFens()) {
      boards.add(Board.fromFenStrict(fen));
    }
    boards.addAll(BenchmarkPositions.curatedBoards(this.positionCount));

    this.fixtureCount = boards.size();
    this.positions = new @NonNull BitboardPosition[this.fixtureCount];
    this.sides = new @NonNull Side[this.fixtureCount];
    this.castlingRights = new @NonNull CastlingRight[this.fixtureCount];
    this.enPassantBits = new long[this.fixtureCount];

    for (int i = 0; i < this.fixtureCount; i++) {
      final Board board = Nulls.get(boards, i);
      final Side side = board.getSideToMove();
      final Square enPassantTarget = board.getEnPassantCaptureTargetSquare();
      this.positions[i] = board.getBitboardPosition();
      this.sides[i] = side;
      this.castlingRights[i] = board.getCastlingRight(side);
      this.enPassantBits[i] = enPassantTarget == Square.NONE ? 0L : 1L << enPassantTarget.ordinal();
    }

    this.countingSink = move -> this.generatedMoveCount++;
  }

  @Benchmark
  @OperationsPerInvocation(PROBES)
  public int legalMoves() {
    this.generatedMoveCount = 0;
    int fixture = 0;
    for (int probe = 0; probe < PROBES; probe++) {
      BitboardLegalMoveFactory.calculateLegalMovesInto(this.countingSink, this.positions[fixture], this.sides[fixture],
          this.castlingRights[fixture], this.enPassantBits[fixture]);
      fixture++;
      if (fixture == this.fixtureCount) {
        fixture = 0;
      }
    }
    return this.generatedMoveCount;
  }

  @Benchmark
  @OperationsPerInvocation(PROBES)
  public long attackedSquares() {
    long accumulated = 0L;
    int fixture = 0;
    for (int probe = 0; probe < PROBES; probe++) {
      accumulated ^= this.positions[fixture].attackedSquares(this.sides[fixture]);
      fixture++;
      if (fixture == this.fixtureCount) {
        fixture = 0;
      }
    }
    return accumulated;
  }

  @Benchmark
  @OperationsPerInvocation(PROBES)
  public long pinnedPieces() {
    long accumulated = 0L;
    int fixture = 0;
    for (int probe = 0; probe < PROBES; probe++) {
      accumulated ^= this.positions[fixture].pinnedPieces(this.sides[fixture]);
      fixture++;
      if (fixture == this.fixtureCount) {
        fixture = 0;
      }
    }
    return accumulated;
  }

  @Benchmark
  @OperationsPerInvocation(PROBES)
  public int isInCheck() {
    int checks = 0;
    int fixture = 0;
    for (int probe = 0; probe < PROBES; probe++) {
      if (this.positions[fixture].isInCheck(this.sides[fixture])) {
        checks++;
      }
      fixture++;
      if (fixture == this.fixtureCount) {
        fixture = 0;
      }
    }
    return checks;
  }
}
