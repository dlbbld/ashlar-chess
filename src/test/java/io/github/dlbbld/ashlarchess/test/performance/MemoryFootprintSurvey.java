// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.performance;

import java.util.ArrayList;
import java.util.List;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.MoveSpecification;
import io.github.dlbbld.ashlarchess.fen.model.Fen;
import io.github.dlbbld.ashlarchess.pgn.PgnMove;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

/**
 * Memory-footprint survey for the "less rich board" design: how much heap a fully-played {@link Board} retains, and
 * whether it scales linearly with game length (no per-ply bloat, no leak). The board keeps one {@code BoardState}
 * record per ply (move + SAN/LAN + dynamic position + clock + castling-loss) but deliberately does NOT retain a
 * legal-move list per historical position; this measures the resulting bytes/ply.
 *
 * <p>
 * Method: GC, read used heap, build {@code COPIES} fully-replayed boards held by strong references, GC, read used heap;
 * the delta divided by COPIES and by plies is bytes/ply. This is a coarse, GC-noisy measurement (run with a fixed JVM
 * and ignore small differences); bytes/ply staying flat across game lengths is the signal that matters. Manually run
 * diagnostic (a {@code main}), like the other surveys in this package.
 */
@SuppressWarnings("null") // Manual survey; JDT cannot model unannotated JDK/JUnit/concurrency APIs cleanly.
public class MemoryFootprintSurvey {

  private static final int COPIES = 8;

  private static final PgnTest[] GROUPS = { PgnTest.WCC2021, PgnTest.RANDOM_NO_REPETITION, PgnTest.MAX_MOVES };

  private static final List<Board> HELD = new ArrayList<>();

  public static void main(String[] args) {
    System.out.printf("%-26s %10s %14s %12s%n", "corpus", "plies", "retained/board", "bytes/ply");
    for (final PgnTest pgnTest : GROUPS) {
      final Game game = longestInitialStartGame(pgnTest);
      final int plies = game.specs().size();

      gcSettle();
      final long baseline = usedMemory();

      HELD.clear();
      for (int i = 0; i < COPIES; i++) {
        HELD.add(replay(game));
      }

      gcSettle();
      final long after = usedMemory();
      HELD.clear();

      final double retainedPerBoard = (after - baseline) / (double) COPIES;
      final double bytesPerPly = retainedPerBoard / plies;
      System.out.printf("%-26s %,10d %,12.1f KB %,12.1f%n", pgnTest.name(), plies, retainedPerBoard / 1024.0,
          bytesPerPly);
    }
    System.out.println();
    System.out
        .println("read: bytes/ply ~constant across lengths => linear, no per-ply bloat. (coarse GC-based measure)");
  }

  private static Board replay(Game game) {
    final Board board = new Board(game.startFen());
    for (final MoveSpecification spec : game.specs()) {
      board.move(spec);
    }
    return board;
  }

  private static long usedMemory() {
    final Runtime runtime = Runtime.getRuntime();
    return runtime.totalMemory() - runtime.freeMemory();
  }

  private static void gcSettle() {
    for (int i = 0; i < 6; i++) {
      System.gc();
      try {
        Thread.sleep(60);
      } catch (@SuppressWarnings("unused") final InterruptedException e) {
        Thread.currentThread().interrupt();
        return;
      }
    }
  }

  private static Game longestInitialStartGame(PgnTest pgnTest) {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
    List<MoveSpecification> longestSpecs = new ArrayList<>();
    Fen startFen = null;
    for (final PgnFen testCase : testCaseList.list()) {
      final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(pgnTest.getFolderPath(), testCase.pgnName());
      if (pgnGame.moves().size() <= longestSpecs.size()) {
        continue;
      }
      final Board collect = new Board(pgnGame.startFen());
      final List<MoveSpecification> specs = new ArrayList<>();
      for (final PgnMove move : pgnGame.moves()) {
        specs.add(collect.moveStrict(move.san()));
      }
      longestSpecs = specs;
      startFen = pgnGame.startFen();
    }
    if (startFen == null) {
      throw new IllegalStateException("no games in " + pgnTest);
    }
    return new Game(startFen, longestSpecs);
  }

  private record Game(Fen startFen, List<MoveSpecification> specs) {
  }
}
