// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.performance;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.fen.model.Fen;
import io.github.dlbbld.ashlarchess.model.PgnMove;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

/**
 * Measures the {@code Board} state-tracking paths that the move-generation survey deliberately steps around:
 * construction from a FEN, full game replay via {@link Board#move(MoveSpecification)}, and the move /
 * {@link Board#unmove()} probe that the fifty-move and threefold claim-ahead reports perform at each position. These
 * are exactly the paths that the "rich board" redesign (per-ply derived lists collapsed into one record list) touches;
 * raw legal-move generation does not exercise them.
 *
 * <p>
 * This survey times ashlar only. ChessLib is not a fair normalizer here (its mutable board mutates in place and never
 * built the per-ply derived state ashlar did), so cross-run machine drift is controlled instead by running
 * {@link MoveGenerationPerformanceSurvey} in the same boot session: its ChessLib us/position is identical code on any
 * tree and serves as the machine anchor when comparing this survey's numbers across releases.
 */
public class BoardReplayPerformanceSurvey {

  private static final int MAX_GAMES_PER_GROUP = 150;
  private static final int WARMUP_ROUNDS = 3;
  private static final int MEASURE_ROUNDS = 20;

  private static final PgnTest[] GROUPS = { PgnTest.MAX_MOVES, PgnTest.RANDOM_NO_REPETITION, PgnTest.WCC2021,
      PgnTest.CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR };

  public static void main(String[] args) {
    for (final PgnTest pgnTest : GROUPS) {
      @SuppressWarnings("null") final @NonNull PgnTest pgnTestNotNull = pgnTest;
      final List<Game> games = collectGames(pgnTestNotNull);
      final int plyCount = totalPlies(games);

      warmup(games);

      final Measurement construct = measureConstruct(games);
      final Measurement replay = measureReplay(games);
      final Measurement replayWithProbe = measureReplayWithProbe(games);

      printResult(pgnTestNotNull, games.size(), plyCount, construct, replay, replayWithProbe);
    }
  }

  /**
   * Construction only: {@code new Board(startFen)} per game.
   */
  private static Measurement measureConstruct(List<Game> games) {
    long checksum = 0L;
    final long start = System.nanoTime();
    for (int round = 0; round < MEASURE_ROUNDS; round++) {
      for (final Game game : games) {
        final Board board = new Board(game.startFen());
        checksum += board.getHalfMoveClock();
      }
    }
    return new Measurement(System.nanoTime() - start, checksum);
  }

  /** Construction plus full mainline replay: the cost of building per-ply state across a whole game. */
  private static Measurement measureReplay(List<Game> games) {
    long checksum = 0L;
    final long start = System.nanoTime();
    for (int round = 0; round < MEASURE_ROUNDS; round++) {
      for (final Game game : games) {
        final Board board = new Board(game.startFen());
        for (final MoveSpecification spec : game.specs()) {
          board.move(spec);
        }
        checksum += board.getPerformedMoveCount();
      }
    }
    return new Measurement(System.nanoTime() - start, checksum);
  }

  /**
   * Construction plus full replay, with a claim-ahead probe at every ply: play the candidate move, back it out, then
   * advance for real ({@code move; unmove; move}). This mirrors what the fifty-move / threefold claim-ahead builders do
   * as they walk the played history.
   */
  private static Measurement measureReplayWithProbe(List<Game> games) {
    long checksum = 0L;
    final long start = System.nanoTime();
    for (int round = 0; round < MEASURE_ROUNDS; round++) {
      for (final Game game : games) {
        final Board board = new Board(game.startFen());
        for (final MoveSpecification spec : game.specs()) {
          board.move(spec);
          board.unmove();
          board.move(spec);
        }
        checksum += board.getPerformedMoveCount();
      }
    }
    return new Measurement(System.nanoTime() - start, checksum);
  }

  private static List<Game> collectGames(PgnTest pgnTest) {
    final List<Game> result = new ArrayList<>();
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
    for (final PgnFen testCase : testCaseList.list()) {
      if (result.size() >= MAX_GAMES_PER_GROUP) {
        break;
      }
      final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(pgnTest.getFolderPath(), testCase.pgnName());
      final Board collect = new Board(pgnGame.startFen());
      final List<MoveSpecification> specs = new ArrayList<>();
      for (final PgnMove move : pgnGame.moves()) {
        specs.add(collect.moveStrict(move.san()));
      }
      result.add(new Game(pgnGame.startFen(), specs));
    }
    return result;
  }

  private static int totalPlies(List<Game> games) {
    int total = 0;
    for (final Game game : games) {
      total += game.specs().size();
    }
    return total;
  }

  private static void warmup(List<Game> games) {
    for (int i = 0; i < WARMUP_ROUNDS; i++) {
      measureConstruct(games);
      measureReplay(games);
      measureReplayWithProbe(games);
    }
  }

  private static void printResult(PgnTest pgnTest, int gameCount, int plyCount, Measurement construct,
      Measurement replay, Measurement replayWithProbe) {
    final double gameDenominator = (double) gameCount * MEASURE_ROUNDS;
    final double plyDenominator = (double) plyCount * MEASURE_ROUNDS;

    final double constructUsPerGame = construct.nanoseconds() / gameDenominator / 1000.0;
    final double replayUsPerGame = replay.nanoseconds() / gameDenominator / 1000.0;
    final double replayUsPerPly = replay.nanoseconds() / plyDenominator / 1000.0;
    final double probeUsPerGame = replayWithProbe.nanoseconds() / gameDenominator / 1000.0;
    final double probeUsPerPly = replayWithProbe.nanoseconds() / plyDenominator / 1000.0;

    System.out.printf("%s%n", pgnTest);
    System.out.printf("  games: %,d  plies: %,d%n", gameCount, plyCount);
    System.out.printf("  construct (new Board(fen)):     %.3f us/game%n", constructUsPerGame);
    System.out.printf("  construct + replay:             %.3f us/ply  (%.1f us/game)%n", replayUsPerPly,
        replayUsPerGame);
    System.out.printf(
        "  construct + replay + unmove:    %.3f us/ply  (%.1f us/game)  [claim-ahead: move;unmove;move]%n",
        probeUsPerPly, probeUsPerGame);
    System.out.printf("  checksum: %d / %d / %d%n%n", construct.checksum(), replay.checksum(),
        replayWithProbe.checksum());
  }

  private record Game(Fen startFen, List<MoveSpecification> specs) {

  }

  private record Measurement(long nanoseconds, long checksum) {

  }
}
