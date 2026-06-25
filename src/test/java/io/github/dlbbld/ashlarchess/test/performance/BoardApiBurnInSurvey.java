// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.performance;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.MoveSpecification;
import io.github.dlbbld.ashlarchess.board.enums.CastlingMove;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.fen.model.Fen;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.PgnMove;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

/**
 * Burn-in survey that drives every cheap public {@link Board} read method once per ply across a game replay, over
 * corpora spanning a range of game lengths (normal games ~100 plies up to the RANDOM_NO_REPETITION fixtures of ~1,200
 * plies). It times each method individually and prints a scaling table: a method whose us/ply on the longer corpus is
 * far above its us/ply on a normal-length corpus is superlinear (O(history) per call), which becomes O(n^2) when a
 * caller invokes it per ply. (~1,200 plies is already ~12x a normal game - enough to expose any O(history) method; the
 * 17,700-ply MAX_MOVES fixtures are deliberately omitted, as they only amplify the same signal while turning every
 * genuinely O(n) probe into a multi-minute O(n^2) grind.)
 *
 * <p>
 * This is a manually run diagnostic (a {@code main}, like the other surveys in this package), not an automated test: it
 * flags candidates for a human to inspect; it does not assert thresholds.
 *
 * <p>
 * Interpretation: a method that <em>returns</em> n items (getPerformedMoves, getLegalMovesAsSan, ...) being O(n) is
 * expected and not a defect. The defects to hunt are methods returning a scalar or boolean that are nonetheless
 * O(history) - those should be O(1). This is the same class of bug as the O(n^2) per-move repetition rebuild.
 *
 * <p>
 * The heavy whole-position analyzers are position-complexity bound, not history bound. The bounded quick variants
 * (unwinnableQuick, deadPositionQuick) are exercised separately for coverage on a small sample; the unbounded full
 * variants (unwinnableFull, deadPositionFull) are excluded entirely - they can run for minutes on a tangled position
 * and are not a Board-scaling concern.
 */
@SuppressWarnings("null") // Manual survey; JDT cannot model unannotated JDK/JUnit/concurrency APIs cleanly.
public class BoardApiBurnInSurvey {

  private static final int MAX_GAMES_PER_GROUP = 150;
  private static final int WARMUP_ROUNDS = 2;
  private static final int MEASURE_ROUNDS = 3;
  private static final int HEAVY_SAMPLE_GAMES = 2;

  // RANDOM_NO_REPETITION (~1,200 plies/game) is ~12x longer than WCC2021 (~100), enough to expose any O(history)
  // per-call method. MAX_MOVES (~17,700 plies) is omitted: it only amplifies the same signal while turning every
  // genuinely O(n) probe (hashCode, equals, getPerformed*) into a multi-minute O(n^2) grind.
  private static final PgnTest[] GROUPS = { PgnTest.WCC2021, PgnTest.CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR,
      PgnTest.RANDOM_NO_REPETITION };

  private static long sink;

  @FunctionalInterface
  private interface Probe {
    long run(Ctx ctx);
  }

  private record Ctx(Board board, @Nullable MoveSpecification candidate, @Nullable String candidateSan, Board shadow) {
  }

  private record NamedProbe(String name, Probe probe) {
  }

  public static void main(String[] args) {
    final List<NamedProbe> probes = probes();
    final Map<String, Map<String, Double>> perGroup = new LinkedHashMap<>();

    for (final PgnTest pgnTest : GROUPS) {
      final String groupName = Nulls.name(pgnTest);
      final List<Game> games = collectGames(pgnTest);
      final int plies = totalPlies(games);

      for (int w = 0; w < WARMUP_ROUNDS; w++) {
        measure(games, probes);
      }
      final Map<String, Long> nanos = measure(games, probes);

      final Map<String, Double> usPerPly = new LinkedHashMap<>();
      final double denominator = (double) plies * MEASURE_ROUNDS;
      for (final NamedProbe namedProbe : probes) {
        final Long total = Nulls.get(nanos, namedProbe.name());
        usPerPly.put(namedProbe.name(), total.longValue() / denominator / 1000.0);
      }
      perGroup.put(groupName, usPerPly);

      printGroup(groupName, games.size(), plies, usPerPly);
      printHeavy(games);
    }

    printScalingTable(probes, perGroup);
    System.out.printf("%n(sink=%d)%n", sink);
  }

  private static Map<String, Long> measure(List<Game> games, List<NamedProbe> probes) {
    final long[] nanos = new long[probes.size()];
    long localSink = 0L;
    for (int round = 0; round < MEASURE_ROUNDS; round++) {
      for (final Game game : games) {
        final Board board = new Board(game.startFen());
        final Board shadow = new Board(game.startFen());
        for (final MoveSpecification spec : game.specs()) {
          board.move(spec);
          shadow.move(spec);

          final List<MoveSpecification> legal = board.getLegalMoveSpecifications();
          final @Nullable MoveSpecification candidate = legal.isEmpty() ? null : Nulls.get(legal, 0);
          final List<String> legalSan = board.getLegalMovesAsSan();
          final @Nullable String candidateSan = legalSan.isEmpty() ? null : Nulls.get(legalSan, 0);
          final Ctx ctx = new Ctx(board, candidate, candidateSan, shadow);

          for (int p = 0; p < probes.size(); p++) {
            final long t0 = System.nanoTime();
            final long value = Nulls.get(probes, p).probe().run(ctx);
            nanos[p] += System.nanoTime() - t0;
            localSink += value;
          }
        }
      }
    }
    sink += localSink;

    final Map<String, Long> result = new LinkedHashMap<>();
    for (int p = 0; p < probes.size(); p++) {
      result.put(Nulls.get(probes, p).name(), nanos[p]);
    }
    return result;
  }

  private static List<NamedProbe> probes() {
    final List<NamedProbe> probes = new ArrayList<>();

    // ---- scalar / boolean accessors (expected O(1)) ----
    probes.add(new NamedProbe("getFen", c -> c.board().getFen().length()));
    probes.add(new NamedProbe("getSan", c -> c.board().getSan().length()));
    probes.add(new NamedProbe("getLan", c -> c.board().getLan().length()));
    probes.add(new NamedProbe("toString", c -> c.board().toString().length()));
    probes.add(new NamedProbe("getSideToMove", c -> c.board().getSideToMove().ordinal()));
    probes.add(
        new NamedProbe("getEnPassantCaptureTargetSquare", c -> c.board().getEnPassantCaptureTargetSquare().ordinal()));
    probes.add(new NamedProbe("getHalfMoveClock", c -> c.board().getHalfMoveClock()));
    probes.add(new NamedProbe("getFullMoveNumber", c -> c.board().getFullMoveNumber()));
    probes.add(new NamedProbe("getLastPlayedFullMoveNumber", c -> c.board().getLastPlayedFullMoveNumber()));
    probes.add(new NamedProbe("getPerformedMoveCount", c -> c.board().getPerformedMoveCount()));
    probes.add(new NamedProbe("getRepetitionCount", c -> c.board().getRepetitionCount()));
    probes.add(new NamedProbe("getMovingPiece", c -> c.board().getMovingPiece().ordinal()));
    probes.add(new NamedProbe("getLastMove", c -> c.board().getLastMove().hashCode()));
    probes.add(new NamedProbe("getBitboardPosition", c -> c.board().getBitboardPosition().hashCode()));
    probes.add(new NamedProbe("getDynamicPosition", c -> c.board().getDynamicPosition().hashCode()));
    probes.add(new NamedProbe("getInitialDynamicPosition", c -> c.board().getInitialDynamicPosition().hashCode()));
    probes.add(new NamedProbe("getInitialFen", c -> c.board().getInitialFen().hashCode()));
    probes.add(new NamedProbe("getCastlingRightWhite", c -> c.board().getCastlingRightWhite().ordinal()));
    probes.add(new NamedProbe("getCastlingRightBlack", c -> c.board().getCastlingRightBlack().ordinal()));
    probes.add(new NamedProbe("getCastlingRight(W)", c -> c.board().getCastlingRight(Side.WHITE).ordinal()));
    probes.add(new NamedProbe("getCastlingRight(B)", c -> c.board().getCastlingRight(Side.BLACK).ordinal()));
    probes.add(new NamedProbe("getWhiteKingSideLoss", c -> c.board().getWhiteKingSideLoss().ordinal()));
    probes.add(new NamedProbe("getWhiteQueenSideLoss", c -> c.board().getWhiteQueenSideLoss().ordinal()));
    probes.add(new NamedProbe("getBlackKingSideLoss", c -> c.board().getBlackKingSideLoss().ordinal()));
    probes.add(new NamedProbe("getBlackQueenSideLoss", c -> c.board().getBlackQueenSideLoss().ordinal()));
    probes.add(new NamedProbe("getCastlingRightLoss(W,KS)",
        c -> c.board().getCastlingRightLoss(Side.WHITE, CastlingMove.KING_SIDE).ordinal()));

    probes.add(new NamedProbe("isCheck", c -> c.board().isCheck() ? 1 : 0));
    probes.add(new NamedProbe("isCheckmate", c -> c.board().isCheckmate() ? 1 : 0));
    probes.add(new NamedProbe("isStalemate", c -> c.board().isStalemate() ? 1 : 0));
    probes.add(new NamedProbe("isCapture", c -> c.board().isCapture() ? 1 : 0));
    probes.add(new NamedProbe("isFirstMove", c -> c.board().isFirstMove() ? 1 : 0));
    probes.add(new NamedProbe("isEnPassantCapturePossible", c -> c.board().isEnPassantCapturePossible() ? 1 : 0));
    probes.add(new NamedProbe("isFiftyMove", c -> c.board().isFiftyMove() ? 1 : 0));
    probes.add(new NamedProbe("isSeventyFiveMove", c -> c.board().isSeventyFiveMove() ? 1 : 0));
    probes.add(new NamedProbe("isThreefoldRepetition", c -> c.board().isThreefoldRepetition() ? 1 : 0));
    probes.add(new NamedProbe("isFivefoldRepetition", c -> c.board().isFivefoldRepetition() ? 1 : 0));
    probes.add(new NamedProbe("isInsufficientMaterial", c -> c.board().isInsufficientMaterial() ? 1 : 0));
    probes.add(new NamedProbe("isInsufficientMaterial(W)", c -> c.board().isInsufficientMaterial(Side.WHITE) ? 1 : 0));
    probes.add(new NamedProbe("isInsufficientMaterial(B)", c -> c.board().isInsufficientMaterial(Side.BLACK) ? 1 : 0));

    probes.add(new NamedProbe("outcome", c -> c.board().outcome().hashCode()));
    probes.add(new NamedProbe("fiftyMoveRuleClaimRights", c -> c.board().fiftyMoveRuleClaimRights().hashCode()));
    probes.add(new NamedProbe("threefoldRepetitionRuleClaimRights",
        c -> c.board().threefoldRepetitionRuleClaimRights().hashCode()));
    probes.add(new NamedProbe("canClaimFiftyMoveRule", c -> c.board().canClaimFiftyMoveRule() ? 1 : 0));
    probes.add(
        new NamedProbe("canClaimThreefoldRepetitionRule", c -> c.board().canClaimThreefoldRepetitionRule() ? 1 : 0));
    probes.add(
        new NamedProbe("canClaimFiftyMoveRuleWithOwnMove", c -> c.board().canClaimFiftyMoveRuleWithOwnMove() ? 1 : 0));
    probes.add(new NamedProbe("canClaimThreefoldRepetitionRuleWithOwnMove",
        c -> c.board().canClaimThreefoldRepetitionRuleWithOwnMove() ? 1 : 0));

    // ---- arg-taking claim predicates (internally probe move/unmove) ----
    probes.add(new NamedProbe("canClaimDrawFor(move)", BoardApiBurnInSurvey::claimDrawMove));
    probes.add(new NamedProbe("canClaimFiftyMoveRuleFor(move)", BoardApiBurnInSurvey::claimFiftyMove));
    probes.add(new NamedProbe("canClaimThreefoldRepetitionRuleFor(move)", BoardApiBurnInSurvey::claimThreefoldMove));
    probes.add(new NamedProbe("canClaimFiftyMoveRuleFor(san)", BoardApiBurnInSurvey::claimFiftySan));
    probes.add(new NamedProbe("canClaimThreefoldRepetitionRuleFor(san)", BoardApiBurnInSurvey::claimThreefoldSan));

    // ---- collection accessors (O(n) is expected: they return n items) ----
    probes.add(new NamedProbe("getLegalMoves", c -> c.board().getLegalMoves().size()));
    probes.add(new NamedProbe("getLegalMoveSpecifications", c -> c.board().getLegalMoveSpecifications().size()));
    probes.add(new NamedProbe("getLegalMovesAsSan", c -> c.board().getLegalMovesAsSan().size()));
    probes.add(new NamedProbe("getLegalMovesAsUci", c -> c.board().getLegalMovesAsUci().size()));
    probes.add(new NamedProbe("getPerformedMoves", c -> c.board().getPerformedMoves().size()));
    probes
        .add(new NamedProbe("getPerformedMoveSpecifications", c -> c.board().getPerformedMoveSpecifications().size()));
    probes.add(new NamedProbe("getPerformedMovesAsSan", c -> c.board().getPerformedMovesAsSan().size()));

    // ---- copy + scalar O(n) suspects (hashCode/equals walk the full history) ----
    probes.add(new NamedProbe("copyCurrentPositionWithoutHistory",
        c -> c.board().copyCurrentPositionWithoutHistory().getHalfMoveClock()));
    probes.add(new NamedProbe("hashCode", c -> c.board().hashCode()));
    probes.add(new NamedProbe("equals(shadow)", c -> c.board().equals(c.shadow()) ? 1 : 0));

    return probes;
  }

  private static long claimFiftySan(Ctx c) {
    final @Nullable String candidateSan = c.candidateSan();
    if (candidateSan == null) {
      return 0L;
    }
    try {
      return c.board().canClaimFiftyMoveRuleFor(candidateSan) ? 1L : 0L;
    } catch (@SuppressWarnings("unused") final RuntimeException e) {
      return 0L;
    }
  }

  private static long claimThreefoldSan(Ctx c) {
    final @Nullable String candidateSan = c.candidateSan();
    if (candidateSan == null) {
      return 0L;
    }
    try {
      return c.board().canClaimThreefoldRepetitionRuleFor(candidateSan) ? 1L : 0L;
    } catch (@SuppressWarnings("unused") final RuntimeException e) {
      return 0L;
    }
  }

  private static long claimDrawMove(Ctx c) {
    final @Nullable MoveSpecification candidate = c.candidate();
    return candidate == null ? 0L : c.board().canClaimDrawFor(candidate) ? 1L : 0L;
  }

  private static long claimFiftyMove(Ctx c) {
    final @Nullable MoveSpecification candidate = c.candidate();
    return candidate == null ? 0L : c.board().canClaimFiftyMoveRuleFor(candidate) ? 1L : 0L;
  }

  private static long claimThreefoldMove(Ctx c) {
    final @Nullable MoveSpecification candidate = c.candidate();
    return candidate == null ? 0L : c.board().canClaimThreefoldRepetitionRuleFor(candidate) ? 1L : 0L;
  }

  private static void printHeavy(List<Game> games) {
    final int sampleCount = Math.min(HEAVY_SAMPLE_GAMES, games.size());
    if (sampleCount == 0) {
      return;
    }
    // Only the BOUNDED quick analyzers are exercised here. unwinnableFull / deadPositionFull run the full CHA helpmate
    // search, whose cost is governed by position complexity (it can run for minutes on a tangled middlegame), not by
    // history length - so they are not a Board-scaling concern and are deliberately excluded from this sweep.
    long unwinnableQuickNanos = 0L;
    long deadQuickNanos = 0L;
    for (int g = 0; g < sampleCount; g++) {
      final Game game = Nulls.get(games, g);
      final Board board = new Board(game.startFen());
      for (final MoveSpecification spec : game.specs()) {
        board.move(spec);
      }
      long t0 = System.nanoTime();
      sink += board.unwinnableQuick(Side.WHITE).hashCode() + board.unwinnableQuick(Side.BLACK).hashCode();
      unwinnableQuickNanos += System.nanoTime() - t0;
      t0 = System.nanoTime();
      sink += board.deadPositionQuick().hashCode();
      deadQuickNanos += System.nanoTime() - t0;
    }
    System.out.printf("  quick analyzers (once at final position, %d-game sample):%n", sampleCount);
    System.out.printf("    unwinnableQuick(W+B): %,.1f us/call%n",
        unwinnableQuickNanos / (double) sampleCount / 1000.0);
    System.out.printf("    deadPositionQuick:    %,.1f us/call%n%n", deadQuickNanos / (double) sampleCount / 1000.0);
  }

  private static void printGroup(String groupName, int gameCount, int plyCount, Map<String, Double> usPerPly) {
    System.out.printf("%s  (games: %,d  plies: %,d)%n", groupName, gameCount, plyCount);
    usPerPly.entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed()).limit(12)
        .forEach(e -> System.out.printf("    %-40s %8.3f us/ply%n", e.getKey(), e.getValue()));
    System.out.println();
  }

  private static void printScalingTable(List<NamedProbe> probes, Map<String, Map<String, Double>> perGroup) {
    final String shortGroup = Nulls.name(PgnTest.WCC2021);
    final String longGroup = Nulls.name(PgnTest.RANDOM_NO_REPETITION);
    final Map<String, Double> shortUs = Nulls.get(perGroup, shortGroup);
    final Map<String, Double> longUs = Nulls.get(perGroup, longGroup);

    System.out.println("================ SCALING TABLE (per-method us/ply) ================");
    System.out.println("ratio = " + longGroup + " / " + shortGroup
        + " (longer game / shorter game). ~1 => O(1) per call; growing with length => O(history) per call.");
    System.out.printf("%-42s %10s %10s %8s   %s%n", "method", "WCC2021", "RANDOM", "ratio", "verdict");

    probes.stream().map(NamedProbe::name).sorted(Comparator.comparingDouble(n -> -ratio(shortUs, longUs, n)))
        .forEach(name -> {
          final double s = value(shortUs, name);
          final double l = value(longUs, name);
          final double r = ratio(shortUs, longUs, name);
          final String verdict = r >= 5.0 ? "<<< SUPERLINEAR" : r >= 2.0 ? "<< check" : "";
          System.out.printf("%-42s %10.3f %10.3f %8.1f   %s%n", name, s, l, r, verdict);
        });
  }

  private static double value(Map<String, Double> map, String name) {
    return Nulls.get(map, name);
  }

  private static double ratio(Map<String, Double> shortUs, Map<String, Double> longUs, String name) {
    final double s = value(shortUs, name);
    final double l = value(longUs, name);
    if (s < 0.01) {
      return l < 0.01 ? 1.0 : 999.0;
    }
    return l / s;
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

  private record Game(Fen startFen, List<MoveSpecification> specs) {
  }
}
