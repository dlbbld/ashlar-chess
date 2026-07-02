// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.performance;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.MoveSpecification;
import io.github.dlbbld.ashlarchess.board.enums.CastlingMove;
import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.utility.IoUtility;
import io.github.dlbbld.ashlarchess.exceptions.FileSystemAccessException;
import io.github.dlbbld.ashlarchess.fen.model.Fen;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.PgnMove;
import io.github.dlbbld.ashlarchess.test.ConfigurationTestConstants;
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
 *
 * <p>
 * Pass {@code --stockfish-wsl} to also run the same replay workloads against the Stockfish library installed for the
 * Ambrona D3-Chess checkout in WSL. Those numbers are useful as a rough engine-grade comparison, but they are labelled
 * separately because they cross Windows/WSL and link against a native library outside Maven.
 */
@SuppressWarnings("null") // Manual survey; JDT cannot model unannotated JDK/JUnit/concurrency APIs cleanly.
public class BoardReplayPerformanceSurvey {

  private static final int MAX_GAMES_PER_GROUP = 150;
  private static final int WARMUP_ROUNDS = 3;
  private static final int MEASURE_ROUNDS = 20;
  private static final String STOCKFISH_WSL_ARGUMENT = "--stockfish-wsl";
  private static final String D3_CHESS_PATH_PROPERTY = "ambrona.d3.path";
  private static final String WSL_RUNNER_RELATIVE_PATH = ".cache/ashlar-chess-ambrona-oracle/board-replay-stockfish";
  private static final Path STOCKFISH_SOURCE_PATH = Nulls.pathResolve(
      ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH, "tools/ambrona-oracle/board_replay_stockfish.cpp");

  private static final PgnTest[] GROUPS = { PgnTest.MAX_MOVES, PgnTest.RANDOM_NO_REPETITION, PgnTest.WCC2021,
      PgnTest.CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR };

  public static void main(String[] args) throws Exception {
    String stockfishRunnerPath = null;
    if (shouldRunStockfish(args)) {
      System.out.printf("Stockfish WSL runner: %s%n", resolveD3ChessRoot(args));
      stockfishRunnerPath = readWslHomePath() + "/" + WSL_RUNNER_RELATIVE_PATH;
      buildStockfishRunner(stockfishRunnerPath);
    }

    for (final PgnTest pgnTest : GROUPS) {
      final List<Game> games = collectGames(pgnTest);
      final int plyCount = totalPlies(games);

      warmup(games);

      final Measurement construct = measureConstruct(games);
      final Measurement replay = measureReplay(games);
      final Measurement replayWithProbe = measureReplayWithProbe(games);
      final StockfishMeasurements stockfish =
          stockfishRunnerPath == null ? null : measureStockfish(games, stockfishRunnerPath);

      printResult(pgnTest, games.size(), plyCount, construct, replay, replayWithProbe, stockfish);
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
      final List<String> uciMoves = new ArrayList<>();
      for (final PgnMove move : pgnGame.moves()) {
        final Side sideToMove = collect.getSideToMove();
        final MoveSpecification specification = collect.moveStrict(move.san());
        specs.add(specification);
        uciMoves.add(toUci(specification, sideToMove));
      }
      result.add(new Game(pgnGame.startFen(), specs, uciMoves));
    }
    return result;
  }

  private static String toUci(MoveSpecification specification, Side sideToMove) {
    if (specification.isCastling()) {
      final CastlingMove castlingMove = specification.castlingMove();
      return castlingMove.kingFromSquare(sideToMove).getName() + castlingMove.kingToSquare(sideToMove).getName();
    }

    final StringBuilder result = new StringBuilder(specification.fromSquare().getName())
        .append(specification.toSquare().getName());
    if (specification.isPromotion()) {
      result.append(toUciPromotionPiece(specification.promotionPieceType()));
    }
    return result.toString();
  }

  private static char toUciPromotionPiece(PromotionPieceType promotionPieceType) {
    return switch (promotionPieceType) {
      case QUEEN -> 'q';
      case ROOK -> 'r';
      case BISHOP -> 'b';
      case KNIGHT -> 'n';
      case NONE -> throw new IllegalArgumentException("Promotion piece type is required");
      default -> throw new IllegalArgumentException();
    };
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
      Measurement replay, Measurement replayWithProbe, @Nullable StockfishMeasurements stockfish) {
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

    if (stockfish != null) {
      printStockfishResult(stockfish, gameDenominator, plyDenominator);
    }
  }

  private static void printStockfishResult(StockfishMeasurements stockfish, double gameDenominator,
      double plyDenominator) {
    final double constructUsPerGame = stockfish.construct().nanoseconds() / gameDenominator / 1000.0;
    final double replayUsPerGame = stockfish.replay().nanoseconds() / gameDenominator / 1000.0;
    final double replayUsPerPly = stockfish.replay().nanoseconds() / plyDenominator / 1000.0;
    final double probeUsPerGame = stockfish.replayWithProbe().nanoseconds() / gameDenominator / 1000.0;
    final double probeUsPerPly = stockfish.replayWithProbe().nanoseconds() / plyDenominator / 1000.0;

    System.out.printf("  Stockfish WSL construct:        %.3f us/game%n", constructUsPerGame);
    System.out.printf("  Stockfish WSL replay:           %.3f us/ply  (%.1f us/game)%n", replayUsPerPly,
        replayUsPerGame);
    System.out.printf("  Stockfish WSL replay + undo:    %.3f us/ply  (%.1f us/game)  [move;undo;move]%n",
        probeUsPerPly, probeUsPerGame);
    System.out.printf("  Stockfish WSL checksum: %d / %d / %d%n%n", stockfish.construct().checksum(),
        stockfish.replay().checksum(), stockfish.replayWithProbe().checksum());
  }

  private static boolean shouldRunStockfish(String[] args) {
    if (args.length == 0) {
      return false;
    }
    if (args.length == 1 && STOCKFISH_WSL_ARGUMENT.equals(args[0])) {
      return true;
    }
    if (args.length == 2 && STOCKFISH_WSL_ARGUMENT.equals(args[0])) {
      return true;
    }
    throw new IllegalArgumentException("Usage: BoardReplayPerformanceSurvey [--stockfish-wsl [wsl-d3-chess-root]]");
  }

  private static String resolveD3ChessRoot(String[] args) throws Exception {
    if (args.length == 2) {
      return Nulls.get(args, 1);
    }
    final String propertyValue = System.getProperty(D3_CHESS_PATH_PROPERTY);
    if (propertyValue != null) {
      return propertyValue;
    }
    return readWslDefaultD3ChessRoot();
  }

  private static void buildStockfishRunner(String stockfishRunnerPath) throws Exception {
    final String cppSourcePath = windowsPathToWsl(STOCKFISH_SOURCE_PATH);
    final String command = "mkdir -p " + shellQuote(parentWslPath(stockfishRunnerPath)) + " && g++ -o "
        + shellQuote(stockfishRunnerPath) + " " + shellQuote(cppSourcePath)
        + " -lpthread -O3 -I/usr/local/include/stockfish -lstockfish";
    runWslCommand(command);
  }

  private static StockfishMeasurements measureStockfish(List<Game> games, String stockfishRunnerPath) throws Exception {
    final ProcessBuilder processBuilder = new ProcessBuilder("wsl", "bash", "-lc", "LD_LIBRARY_PATH=/usr/local/lib "
        + shellQuote(stockfishRunnerPath) + " " + WARMUP_ROUNDS + " " + MEASURE_ROUNDS);
    processBuilder.redirectErrorStream(true);
    final Process process = IoUtility.startProcess(processBuilder);

    final List<String> outputLines = new ArrayList<>();
    try (
        BufferedWriter writer = new BufferedWriter(
            new OutputStreamWriter(IoUtility.getOutputStream(process), StandardCharsets.UTF_8));
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(IoUtility.getInputStream(process), StandardCharsets.UTF_8))) {

      for (final Game game : games) {
        writer.write(game.startFen().fen());
        writer.write('\t');
        writer.write(Nulls.join(" ", game.uciMoves()));
        writer.write('\n');
      }
      writer.close();

      String line;
      while ((line = reader.readLine()) != null) {
        outputLines.add(line);
      }
    }

    final int exitCode = process.waitFor();
    if (exitCode != 0) {
      throw new IllegalStateException(
          "Stockfish WSL runner exited with " + exitCode + ": " + Nulls.join("\n", outputLines));
    }
    return parseStockfishMeasurements(outputLines);
  }

  private static StockfishMeasurements parseStockfishMeasurements(List<String> lines) {
    Measurement construct = null;
    Measurement replay = null;
    Measurement replayWithProbe = null;
    for (final String line : lines) {
      final String[] parts = Nulls.split(line, "\t");
      if (parts.length != 3) {
        throw new IllegalStateException("Unexpected Stockfish WSL output: " + line);
      }
      final Measurement measurement = new Measurement(Long.parseLong(Nulls.get(parts, 1)),
          Long.parseUnsignedLong(Nulls.get(parts, 2)));
      switch (Nulls.get(parts, 0)) {
        case "construct" -> construct = measurement;
        case "replay" -> replay = measurement;
        case "replayWithProbe" -> replayWithProbe = measurement;
        default -> throw new IllegalStateException("Unexpected Stockfish WSL measurement: " + line);
      }
    }
    if (construct == null || replay == null || replayWithProbe == null) {
      throw new IllegalStateException("Incomplete Stockfish WSL output: " + Nulls.join("\n", lines));
    }
    return new StockfishMeasurements(construct, replay, replayWithProbe);
  }

  private static String parentWslPath(String path) {
    final int lastSlashIndex = path.lastIndexOf('/');
    if (lastSlashIndex <= 0) {
      throw new IllegalArgumentException("WSL path has no parent: " + path);
    }
    return path.substring(0, lastSlashIndex);
  }

  private static String windowsPathToWsl(Path path) throws Exception {
    final String windowsPath = Nulls.replace(Nulls.toString(Nulls.toAbsolutePath(path)), '\\', '/');
    final ProcessBuilder processBuilder = new ProcessBuilder("wsl", "wslpath", "-a", windowsPath);
    processBuilder.redirectErrorStream(true);
    final Process process = IoUtility.startProcess(processBuilder);
    try (InputStream outputStream = IoUtility.getInputStream(process)) {
      final String output = Nulls.trim(readStream(outputStream));
      final int exitCode = process.waitFor();
      if (exitCode != 0) {
        throw new IllegalStateException("wslpath failed with " + exitCode + ": " + output);
      }
      return output;
    }
  }

  private static String readWslDefaultD3ChessRoot() throws Exception {
    final ProcessBuilder processBuilder = new ProcessBuilder("wsl", "bash", "-lc", "printf '%s' \"$HOME/D3-Chess\"");
    processBuilder.redirectErrorStream(true);
    final Process process = IoUtility.startProcess(processBuilder);
    try (InputStream outputStream = IoUtility.getInputStream(process)) {
      final String output = Nulls.trim(readStream(outputStream));
      final int exitCode = process.waitFor();
      if (exitCode != 0) {
        throw new IllegalStateException(
            "Resolving the WSL D3-Chess default path failed with " + exitCode + ": " + output);
      }
      if (output.isBlank()) {
        throw new IllegalStateException("Resolving the WSL D3-Chess default path returned an empty path");
      }
      return output;
    }
  }

  private static String readWslHomePath() throws Exception {
    final ProcessBuilder processBuilder = new ProcessBuilder("wsl", "bash", "-lc", "printf '%s' \"$HOME\"");
    processBuilder.redirectErrorStream(true);
    final Process process = IoUtility.startProcess(processBuilder);
    try (InputStream outputStream = IoUtility.getInputStream(process)) {
      final String output = Nulls.trim(readStream(outputStream));
      final int exitCode = process.waitFor();
      if (exitCode != 0) {
        throw new IllegalStateException("Resolving the WSL home path failed with " + exitCode + ": " + output);
      }
      if (output.isBlank()) {
        throw new IllegalStateException("Resolving the WSL home path returned an empty path");
      }
      return output;
    }
  }

  private static void runWslCommand(String command) throws Exception {
    final ProcessBuilder processBuilder = new ProcessBuilder("wsl", "bash", "-lc", command);
    processBuilder.redirectErrorStream(true);
    final Process process = IoUtility.startProcess(processBuilder);
    try (InputStream outputStream = IoUtility.getInputStream(process)) {
      final String output = Nulls.trim(readStream(outputStream));
      final int exitCode = process.waitFor();
      if (exitCode != 0) {
        throw new IllegalStateException("WSL command failed with " + exitCode + ": " + output);
      }
    }
  }

  private static String readStream(InputStream inputStream) {
    try {
      return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    } catch (final IOException ioe) {
      throw new FileSystemAccessException("Reading process output failed", ioe);
    }
  }

  private static String shellQuote(String value) {
    return "'" + value.replace("'", "'\"'\"'") + "'";
  }

  private record Game(Fen startFen, List<MoveSpecification> specs, List<String> uciMoves) {

  }

  private record Measurement(long nanoseconds, long checksum) {

  }

  private record StockfishMeasurements(Measurement construct, Measurement replay, Measurement replayWithProbe) {

  }
}
