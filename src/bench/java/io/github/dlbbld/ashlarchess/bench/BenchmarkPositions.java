// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.bench;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.test.ConfigurationTestConstants;
import io.github.dlbbld.ashlarchess.test.common.utility.FileUtility;

/**
 * Fixture positions for the benchmarks.
 *
 * <p>
 * Two sources, deliberately different in character. The move-generation FENs are the standard perft positions, chosen
 * because they are slider-dense and independently verified elsewhere, so a move-generation number is comparable to
 * published figures. The unwinnability corpus is the chasolver curated oracle - real queries submitted to
 * chasolver.org - so an end-to-end number reflects the difficulty mix the analyzer actually meets rather than a mix we
 * picked to flatter it.
 */
public final class BenchmarkPositions {

  private static final Path CURATED_POSITION_PATH = Nulls.pathResolve(
      ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH, "src/test/resources/oracle/chasolver/curated/positions.txt");

  private BenchmarkPositions() {
  }

  /**
   * Slider-dense positions with full FEN fields. These are the standard perft positions: the initial position,
   * Kiwipete, the rook-and-pawn endgame, and the two promotion-heavy middlegames.
   */
  public static List<String> moveGenerationFens() {
    final List<String> fens = new ArrayList<>();
    fens.add("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    fens.add("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1");
    fens.add("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1");
    fens.add("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
    fens.add("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");
    fens.add("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P3/2NP1N2/PPPQ1PPP/R4RK1 w - - 0 10");
    return fens;
  }

  /**
   * A deterministic stride sample of {@code count} importable positions from the chasolver curated oracle.
   *
   * <p>
   * A stride rather than a prefix: the file is grouped by the kind of position (pawn walls first, then the rest), so a
   * prefix would benchmark one shape of problem. A stride spreads the sample across the whole file and is still
   * reproducible run to run. Positions the FEN parser rejects are skipped - the oracle carries a few deliberately, and
   * importability is not what these benchmarks measure.
   *
   * <p>
   * Returns exactly {@code count} boards or throws. An earlier version returned however many survived the stride,
   * which silently handed 239 boards to a caller that had asked for 256 and normalized its per-position scores by the
   * number it requested - understating them by 7%. A sampler that quietly returns a different size than asked for
   * cannot be used for normalization, so it now walks on past the stride to fill the shortfall.
   */
  public static List<Board> curatedBoards(int count) {
    if (count <= 0) {
      throw new ProgrammingMistakeException("Benchmark position count must be positive, got " + count);
    }
    final List<String> fens = curatedFens();
    if (count > fens.size()) {
      throw new ProgrammingMistakeException(
          "Requested " + count + " positions but the chasolver curated oracle holds only " + fens.size());
    }
    final int stride = Math.max(1, fens.size() / count);
    final List<Board> boards = new ArrayList<>();
    final boolean[] taken = new boolean[fens.size()];
    RuntimeException lastRejection = null;

    // First pass on the stride, then a second pass over everything still untaken to make up for positions the parser
    // rejected. The stride pass is what spreads the sample across the file; the fill pass only restores the count.
    for (int pass = 0; pass < 2 && boards.size() < count; pass++) {
      final int step = pass == 0 ? stride : 1;
      for (int i = 0; i < fens.size() && boards.size() < count; i += step) {
        if (taken[i]) {
          continue;
        }
        final String fen = Nulls.get(fens, i);
        try {
          boards.add(Board.fromFenLenient(fen));
          taken[i] = true;
        } catch (final RuntimeException rejection) {
          taken[i] = true;
          lastRejection = rejection;
        }
      }
    }

    if (boards.size() != count) {
      throw new ProgrammingMistakeException("Could only import " + boards.size() + " of the " + count
          + " requested positions from the chasolver curated oracle"
          + (lastRejection == null ? "" : "; last rejection: " + lastRejection.getMessage()));
    }
    return boards;
  }

  /**
   * Positions whose full unwinnability analysis lands in the fast bulk of the corpus - roughly 0.1 ms to 2 ms for both
   * sides together.
   *
   * <p>
   * Hardcoded rather than sampled by measured runtime, because a fixture set chosen by timing would differ from
   * machine to machine and destroy the cross-machine comparison these benchmarks exist for. The band was picked once
   * from a measured sweep of the corpus and the FENs are now fixed input.
   */
  public static List<String> unwinnabilityTypicalFens() {
    final List<String> fens = new ArrayList<>();
    fens.add("3k1b2/8/8/8/8/8/3K4/8 w - -");
    fens.add("k7/2K5/R1B5/1P6/8/8/8/8 b - -");
    fens.add("8/8/8/2qpp3/K1pkp3/2pbp3/8/N7 w - -");
    fens.add("K1k5/P1PpB3/2pP4/1pP5/bP6/8/8/8 w - -");
    fens.add("8/7k/6p1/6P1/6PB/6PK/6PP/8 w - -");
    fens.add("1k6/1p6/1Pp3p1/2P1p1Pb/N1p1P1pP/1pP3P1/1P6/1K6 w - -");
    fens.add("3k4/p2p2p1/8/p2p2p1/P2P2P1/8/P2P2P1/3K4 w - -");
    fens.add("2k5/p1p2p1p/P1P2P1P/8/8/p1p2p1p/P1P2P1P/2K5 w - -");
    fens.add("8/2k5/5b2/p1p1pBp1/P1P1P1P1/5K2/4B3/8 w - -");
    fens.add("8/5p2/5P2/5P2/1p2p3/kP2p3/2KpP3/1r1B4 w - -");
    fens.add("QN1k4/PPpPp1p1/PpP1P1P1/1Pb2K2/8/8/8/8 w - -");
    fens.add("N3k3/1p1pPp2/1P1P1P2/8/8/6p1/6Pp/7K w - -");
    fens.add("7k/8/8/8/4p1p1/p2pP1Pp/P2P3P/2K2NB1 w - -");
    fens.add("8/8/3P1k1P/p2P4/Pp5p/1p2p1pP/N1p1P1P1/K7 b - -");
    fens.add("8/8/3K4/8/1k6/4b2b/8/8 w - -");
    fens.add("NRRQRBRB/1PPPP1P1/1P6/4k3/8/8/4K3/8 w - -");
    fens.add("b7/8/8/8/8/8/p5K1/kBQ5 w - -");
    fens.add("3k4/8/3b4/8/8/3B4/8/3K4 b - -");
    fens.add("6qB/5r2/5b2/8/8/5R1p/2R1pKNk/8 w - -");
    fens.add("6b1/8/4p1p1/1pp1P1P1/1pkpKP1B/1p3P2/1P1P1P2/8 b - -");
    return fens;
  }

  /**
   * Positions whose full unwinnability analysis runs for most of a second per side - deep helpmate searches, all of
   * them blocked pawn structures.
   *
   * <p>
   * These are the corpus tail, and they matter disproportionately: a measured sweep found the slowest 5% of positions
   * accounting for roughly 40% of total analysis time, with a median of about 2.5 ms against a mean of about 120 ms.
   * Benchmarking the corpus as one mean would therefore report almost nothing but this tail, which is why the tail is
   * split out and measured on purpose instead.
   */
  public static List<String> unwinnabilityHardFens() {
    final List<String> fens = new ArrayList<>();
    fens.add("2k5/2p1p1p1/p1P1P1P1/P1p4K/8/8/2P5/8 w - -");
    fens.add("nb2kBN1/2p1PRRN/1pP2PPQ/1P2p1PP/8/8/4P3/6K1 w - -");
    fens.add("6k1/p3p1P1/6PB/p5P1/2p5/P3P3/2PK4/8 w - -");
    fens.add("k1K5/p1p5/PbP5/pBp5/P1P5/8/8/8 w - -");
    return fens;
  }

  /** Imports every FEN in {@code fens} with the lenient parser, which tolerates the corpus's missing move counters. */
  public static List<Board> boardsOf(List<String> fens) {
    final List<Board> boards = new ArrayList<>();
    for (final String fen : fens) {
      boards.add(Board.fromFenLenient(fen));
    }
    return boards;
  }

  private static List<String> curatedFens() {
    final List<String> lines = FileUtility.readFileLines(CURATED_POSITION_PATH);
    final List<String> fens = new ArrayList<>();
    for (int i = 0; i < lines.size(); i++) {
      final String line = Nulls.get(lines, i);
      if (line.isBlank() || line.startsWith("#")) {
        continue;
      }
      if (line.length() < 4 || line.charAt(2) != ' ') {
        throw new ProgrammingMistakeException("Invalid chasolver curated position row: " + line);
      }
      fens.add(Nulls.substring(line, 3));
    }
    if (fens.isEmpty()) {
      throw new ProgrammingMistakeException("The chasolver curated position oracle is empty");
    }
    return fens;
  }
}
