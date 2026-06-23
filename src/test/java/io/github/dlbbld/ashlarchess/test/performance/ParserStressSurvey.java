// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.performance;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.exceptions.UsageException;
import io.github.dlbbld.ashlarchess.fen.LenientFenParser;
import io.github.dlbbld.ashlarchess.fen.StrictFenParser;
import io.github.dlbbld.ashlarchess.fen.internal.FenConstants;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParser;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.PgnMove;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParser;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

/**
 * Parser robustness + stress harness for FEN / SAN / PGN.
 *
 * <p>
 * Part A - heavy-comment PGN stress: takes the longest real game in the corpus and wraps it in a deliberately heavy PGN
 * (a game-start comment, a long brace comment on <em>every</em> move, and an end-of-line {@code ;} comment on every
 * move), at two comment sizes, then parses it strict and lenient under a hard timeout. This exercises the comment path
 * the random fuzzer never reaches, and reveals any superlinear comment handling (parse time should grow ~linearly with
 * comment bytes) or non-termination.
 *
 * <p>
 * Part B - fuzzing: feeds random garbage and mutated-valid inputs to every parser entry point and asserts the contract
 * that malformed input surfaces as a {@link UsageException} (caller fault). Anything else escaping -
 * {@code NullPointerException}, {@code ArrayIndexOutOfBoundsException}, {@code StackOverflowError},
 * {@code ProgrammingMistakeException}, ... - is a finding (a library bug), captured with the offending input. Fixed
 * seed, so runs are reproducible.
 *
 * <p>
 * Manually run diagnostic (a {@code main}), like the other surveys in this package.
 */
@SuppressWarnings("null") // Manual survey; JDT cannot model unannotated JDK/JUnit/concurrency APIs cleanly.
public class ParserStressSurvey {

  private static final int[] COMMENT_LENGTHS = { 100, 400 };
  private static final int FUZZ_ITERATIONS = 4000;
  private static final long FUZZ_SEED = 20_260_621L;
  private static final long HEAVY_PARSE_TIMEOUT_MS = 120_000L;

  private static final String[] FEN_SEEDS = { FenConstants.FEN_INITIAL.fen(),
      "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2", "8/8/8/4k3/8/8/4K3/4R3 w - - 0 1" };
  private static final String[] SAN_SEEDS = { "e4", "Nf3", "O-O", "O-O-O", "exd5", "e8=Q+", "Nbd2", "Qxh7#", "dxe6",
      "Rfe1", "a8=N" };
  private static final String[] PGN_SEEDS = {
      "[Event \"x\"]\n[Site \"?\"]\n[Date \"????.??.??\"]\n[Round \"?\"]\n[White \"?\"]\n[Black \"?\"]\n"
          + "[Result \"*\"]\n\n1. e4 e5 2. Nf3 Nc6 *\n" };

  private static final String GARBAGE_CHARS = "abcdefgh12345678KQRBNPkqrbnp /.-=+#*x{}[];:\"'\\\n\t()O0o";
  private static final char[] FILLER_CHARS = Nulls.toCharArray("abcdefghijklmnopqrstuvwxyz0123456789 ");

  private static final ExecutorService WORKER = newWorker();

  @FunctionalInterface
  private interface ParseAction {
    void parse(String input);
  }

  private static final String TAGS = "[Event \"x\"]\n[Site \"?\"]\n[Date \"????.??.??\"]\n[Round \"?\"]\n"
      + "[White \"?\"]\n[Black \"?\"]\n[Result \"*\"]\n\n";

  public static void main(String[] args) {
    commentPlacementProbe();
    heavyCommentPgnStress();
    fuzz();
    WORKER.shutdownNow();
  }

  private static void commentPlacementProbe() {
    System.out.println("================ comment-placement isolation ================");
    tryBoth("no comments", TAGS + "1. e4 e5 *\n\n");
    tryBoth("game-start comment", TAGS + "{ hello } 1. e4 e5 *\n\n");
    tryBoth("brace after each move", TAGS + "1. e4 { c } e5 { c } *\n\n");
    tryBoth("eol ; comment", TAGS + "1. e4 ; eol\ne5 *\n\n");
    tryBoth("brace then eol ;", TAGS + "1. e4 { c } ; eol\ne5 *\n\n");
    System.out.println();
  }

  private static void tryBoth(String label, String pgn) {
    System.out.printf("  %-24s strict=%-28s lenient=%s%n", label, outcome(() -> StrictPgnParser.parseText(pgn)),
        outcome(() -> LenientPgnParser.parseText(pgn)));
  }

  private static String outcome(Callable<PgnGame> parse) {
    try {
      return "OK(" + parse.call().moves().size() + ")";
    } catch (final UsageException e) {
      final String m = e.getMessage();
      return "reject(" + (m == null ? "" : m.length() > 24 ? m.substring(0, 24) : m) + ")";
    } catch (final Exception e) {
      return "THREW " + e.getClass().getSimpleName();
    }
  }

  // ----------------------------------------------------------------- Part A: heavy-comment PGN

  private static void heavyCommentPgnStress() {
    System.out.println("================ Part A: heavy-comment PGN stress ================");
    final List<String> sans = longestInitialStartGameSans();
    System.out.printf("source: longest initial-start game in corpus = %,d plies%n%n", sans.size());

    // Warm up the JIT on a heavy parse so the measured times below are steady-state, not first-run dominated.
    LenientPgnParser.parseText(buildHeavyPgn(sans, COMMENT_LENGTHS[0]));

    for (final int commentLength : COMMENT_LENGTHS) {
      final String pgn = buildHeavyPgn(sans, commentLength);
      System.out.printf("--- %d comment chars/move -> document %.2f MB ---%n", commentLength,
          pgn.length() / 1_048_576.0);
      parseHeavy("lenient", () -> LenientPgnParser.parseText(pgn), sans.size());
      parseHeavy("strict ", () -> StrictPgnParser.parseText(pgn), sans.size());
      System.out.println();
    }
    System.out.println("read: ~4x the comment bytes should cost ~4x the parse time (linear). A much larger jump would");
    System.out.println("flag superlinear comment handling.");
    System.out.println();
  }

  private static void parseHeavy(String label, Callable<PgnGame> parse, int expectedMoves) {
    final long start = System.nanoTime();
    final Future<PgnGame> future = WORKER.submit(parse);
    try {
      final PgnGame game = future.get(HEAVY_PARSE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
      final double ms = (System.nanoTime() - start) / 1_000_000.0;
      final int moves = game.moves().size();
      final String verdict = moves == expectedMoves ? "(round-trip OK)" : "(MISMATCH, expected " + expectedMoves + ")";
      System.out.printf("  %s parse: %,.1f ms   moves=%,d %s%n", label, ms, moves, verdict);
    } catch (@SuppressWarnings("unused") final TimeoutException e) {
      future.cancel(true);
      System.out.printf("  %s parse: !!! DID NOT RETURN within %ds -- pathological / non-terminating%n", label,
          HEAVY_PARSE_TIMEOUT_MS / 1000);
    } catch (@SuppressWarnings("unused") final InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (final Exception e) {
      final Throwable cause = e.getCause() == null ? e : e.getCause();
      System.out.printf("  %s parse: threw %s: %s%n", label, cause.getClass().getName(), cause.getMessage());
    }
  }

  private static String buildHeavyPgn(List<String> sans, int commentLength) {
    final StringBuilder sb = new StringBuilder(sans.size() * (commentLength + 24) + 512);
    sb.append("[Event \"Heavy comment stress\"]\n[Site \"?\"]\n[Date \"????.??.??\"]\n[Round \"?\"]\n");
    sb.append("[White \"?\"]\n[Black \"?\"]\n[Result \"*\"]\n\n");
    sb.append("{ ").append(filler("game-start ", commentLength)).append(" }\n");
    for (int i = 0; i < sans.size(); i++) {
      if (i % 2 == 0) {
        sb.append(i / 2 + 1).append(". ");
      }
      sb.append(sans.get(i));
      // One brace comment per move (a lenient-supported form). Brace + ';' on the same move is rejected as
      // consecutive comments, and ';' end-of-line comments are exercised separately by the placement isolation.
      sb.append(" { ").append(filler("ply" + i + " ", commentLength)).append(" }").append('\n');
    }
    sb.append("*\n\n");
    return Nulls.toString(sb);
  }

  private static String filler(String prefix, int length) {
    final StringBuilder sb = new StringBuilder(length);
    sb.append(prefix);
    while (sb.length() < length) {
      sb.append(FILLER_CHARS[sb.length() % FILLER_CHARS.length]);
    }
    return Nulls.substring(sb, 0, length);
  }

  private static List<String> longestInitialStartGameSans() {
    List<String> best = new ArrayList<>();
    for (final PgnTest pgnTest : new PgnTest[] { PgnTest.MAX_MOVES, PgnTest.RANDOM_NO_REPETITION }) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        final PgnGame game = PgnCacheForStrictPgnParserTestCases.getPgn(pgnTest.getFolderPath(), testCase.pgnName());
        if (!game.startFen().equals(FenConstants.FEN_INITIAL)) {
          continue;
        }
        if (game.moves().size() > best.size()) {
          final List<String> sans = new ArrayList<>();
          for (final PgnMove move : game.moves()) {
            sans.add(move.san());
          }
          best = sans;
        }
      }
    }
    return best;
  }

  // ----------------------------------------------------------------- Part B: fuzzing

  private static void fuzz() {
    System.out.println("================ Part B: parser fuzzing ================");
    System.out.println("contract: malformed input must surface as a UsageException; any other throwable is a finding.");
    final Random rnd = new Random(FUZZ_SEED);
    final List<String> findings = new ArrayList<>();

    fuzzTarget("FEN", "strict ", rnd, findings, FEN_SEEDS, StrictFenParser::parse);
    fuzzTarget("FEN", "lenient", rnd, findings, FEN_SEEDS, LenientFenParser::parse);
    fuzzTarget("SAN", "strict ", rnd, findings, SAN_SEEDS, s -> new Board().moveStrict(s));
    fuzzTarget("SAN", "lenient", rnd, findings, SAN_SEEDS, s -> new Board().moveLenient(s));
    fuzzTarget("PGN", "strict ", rnd, findings, PGN_SEEDS, StrictPgnParser::parseText);
    fuzzTarget("PGN", "lenient", rnd, findings, PGN_SEEDS, LenientPgnParser::parseText);

    System.out.printf("%nfuzz complete (%,d iterations/target, seed %d). findings=%d%n", FUZZ_ITERATIONS, FUZZ_SEED,
        findings.size());
    if (findings.isEmpty()) {
      System.out.println("  CLEAN: every malformed input was rejected with a UsageException.");
    } else {
      for (final String finding : findings) {
        System.out.println("  FINDING: " + finding);
      }
    }
  }

  private static void fuzzTarget(String kind, String mode, Random rnd, List<String> findings, String[] seeds,
      ParseAction action) {
    int accepted = 0;
    int rejected = 0;
    int found = 0;
    for (int i = 0; i < FUZZ_ITERATIONS; i++) {
      final String input = i % 2 == 0 ? randomGarbage(rnd) : mutate(seeds[rnd.nextInt(seeds.length)], rnd);
      try {
        action.parse(input);
        accepted++;
      } catch (@SuppressWarnings("unused") final UsageException expected) {
        rejected++;
      } catch (final Throwable bug) {
        found++;
        findings.add(kind + " " + mode.trim() + ": " + bug.getClass().getName() + " at " + originFrame(bug) + " on <"
            + snippet(input) + ">");
      }
    }
    System.out.printf("  %s %s: %,d accepted, %,d cleanly-rejected, %d findings%n", kind, mode, accepted, rejected,
        found);
  }

  private static String randomGarbage(Random rnd) {
    final int length = rnd.nextInt(96);
    final StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append(GARBAGE_CHARS.charAt(rnd.nextInt(GARBAGE_CHARS.length())));
    }
    return Nulls.toString(sb);
  }

  private static String mutate(String seed, Random rnd) {
    final StringBuilder sb = new StringBuilder(seed);
    final int edits = 1 + rnd.nextInt(5);
    for (int e = 0; e < edits; e++) {
      if (sb.length() == 0) {
        sb.append(GARBAGE_CHARS.charAt(rnd.nextInt(GARBAGE_CHARS.length())));
        continue;
      }
      final int op = rnd.nextInt(3);
      final int pos = rnd.nextInt(sb.length());
      switch (op) {
        case 0 -> sb.insert(pos, GARBAGE_CHARS.charAt(rnd.nextInt(GARBAGE_CHARS.length())));
        case 1 -> sb.deleteCharAt(pos);
        default -> sb.setCharAt(pos, GARBAGE_CHARS.charAt(rnd.nextInt(GARBAGE_CHARS.length())));
      }
    }
    return Nulls.toString(sb);
  }

  private static String originFrame(Throwable t) {
    for (final StackTraceElement element : t.getStackTrace()) {
      if (element.getClassName().startsWith("io.github.dlbbld.ashlarchess")) {
        final String simple = element.getClassName().substring(element.getClassName().lastIndexOf('.') + 1);
        return simple + "." + element.getMethodName() + ":" + element.getLineNumber();
      }
    }
    return t.getStackTrace().length > 0 ? t.getStackTrace()[0].toString() : "?";
  }

  private static String snippet(String input) {
    final String escaped = input.replace("\\", "\\\\").replace("\n", "\\n").replace("\t", "\\t");
    return escaped.length() <= 70 ? escaped : escaped.substring(0, 70) + "...";
  }

  private static ExecutorService newWorker() {
    return Executors.newSingleThreadExecutor(r -> {
      final Thread t = new Thread(r, "parser-stress");
      t.setDaemon(true);
      return t;
    });
  }
}
