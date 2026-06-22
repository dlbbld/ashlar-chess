// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.performance;

import java.util.ArrayList;
import java.util.List;

import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;

import io.github.dlbbld.ashlarchess.bitboard.BitboardLegalMoveFactory;
import io.github.dlbbld.ashlarchess.bitboard.StaticPositionBridge;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.pgn.PgnMove;
import io.github.dlbbld.ashlarchess.moves.LegalMovesSupport;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;
import io.github.dlbbld.ashlarchess.unwinnability.HelpmateSearchBoardPerformanceProbe;

@SuppressWarnings("null") // Manual survey; JDT cannot model unannotated JDK/JUnit/concurrency APIs cleanly.
public class MoveGenerationPerformanceSurvey {

  private static final int MAX_POSITIONS_PER_GROUP = 800;
  private static final int WARMUP_ROUNDS = 3;
  private static final int MEASURE_ROUNDS = 20;

  private static final PgnTest[] GROUPS = { PgnTest.MAX_MOVES, PgnTest.RANDOM_NO_REPETITION, PgnTest.WCC2021,
      PgnTest.CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR };

  public static void main(String[] args) {
    for (final PgnTest pgnTest : GROUPS) {
      final List<PositionPair> positions = collectPositions(pgnTest);
      warmup(positions);

      final Measurement boardBackend = measureBoardBackend(positions);
      final Measurement helpmateSearchBoard = measureHelpmateSearchBoard(positions);
      final Measurement reference = measureReference(positions);
      final Measurement chessLib = measureChessLib(positions);

      printResult(pgnTest, positions.size(), boardBackend, helpmateSearchBoard, reference, chessLib);
    }
  }

  private static Measurement measureBoardBackend(List<PositionPair> positions) {
    long moveCount = 0L;
    final long start = System.nanoTime();
    for (int round = 0; round < MEASURE_ROUNDS; round++) {
      for (final PositionPair position : positions) {
        final Board board = position.ashlarBoard();
        final Square ep = board.getEnPassantCaptureTargetSquare();
        final long enPassantBit = ep == Square.NONE ? 0L : 1L << ep.ordinal();
        moveCount += BitboardLegalMoveFactory.calculateLegalMoves(board.getBitboardPosition(), board.getSideToMove(),
            board.getCastlingRight(board.getSideToMove()), enPassantBit).size();
      }
    }
    return new Measurement(System.nanoTime() - start, moveCount);
  }

  private static Measurement measureHelpmateSearchBoard(List<PositionPair> positions) {
    final HelpmateSearchBoardPerformanceProbe probe = new HelpmateSearchBoardPerformanceProbe();
    long moveCount = 0L;
    final long start = System.nanoTime();
    for (int round = 0; round < MEASURE_ROUNDS; round++) {
      for (final PositionPair position : positions) {
        moveCount += probe.calculateLegalMoveCount(position.ashlarBoard());
      }
    }
    return new Measurement(System.nanoTime() - start, moveCount);
  }

  private static List<PositionPair> collectPositions(PgnTest pgnTest) {
    final List<PositionPair> result = new ArrayList<>();
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
    for (final PgnFen testCase : testCaseList.list()) {
      if (result.size() >= MAX_POSITIONS_PER_GROUP) {
        break;
      }
      final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(pgnTest.getFolderPath(), testCase.pgnName());
      final Board board = new Board(pgnGame.startFen());
      addPosition(result, board);
      for (final PgnMove move : pgnGame.moves()) {
        board.moveStrict(move.san());
        addPosition(result, board);
        if (result.size() >= MAX_POSITIONS_PER_GROUP) {
          break;
        }
      }
    }
    return result;
  }

  private static void addPosition(List<PositionPair> result, Board ashlarBoard) {
    final String fen = ashlarBoard.getFen();
    final com.github.bhlangonijr.chesslib.Board chessLibBoard = new com.github.bhlangonijr.chesslib.Board();
    chessLibBoard.loadFromFen(fen);
    result.add(new PositionPair(Board.fromFenStrict(fen), chessLibBoard));
  }

  private static void warmup(List<PositionPair> positions) {
    for (int i = 0; i < WARMUP_ROUNDS; i++) {
      measureBoardBackend(positions);
      measureHelpmateSearchBoard(positions);
      measureReference(positions);
      measureChessLib(positions);
    }
  }

  private static Measurement measureReference(List<PositionPair> positions) {
    long moveCount = 0L;
    final long start = System.nanoTime();
    for (int round = 0; round < MEASURE_ROUNDS; round++) {
      for (final PositionPair position : positions) {
        final Board board = position.ashlarBoard();
        moveCount += LegalMovesSupport.calculateLegalMoves(
            StaticPositionBridge.toStaticPosition(board.getBitboardPosition()), board.getSideToMove(),
            board.getCastlingRight(board.getSideToMove()), board.getEnPassantCaptureTargetSquare()).size();
      }
    }
    return new Measurement(System.nanoTime() - start, moveCount);
  }

  private static Measurement measureChessLib(List<PositionPair> positions) {
    long moveCount = 0L;
    final long start = System.nanoTime();
    for (int round = 0; round < MEASURE_ROUNDS; round++) {
      for (final PositionPair position : positions) {
        moveCount += generateChessLibLegalMoves(position.chessLibBoard()).size();
      }
    }
    return new Measurement(System.nanoTime() - start, moveCount);
  }

  private static List<com.github.bhlangonijr.chesslib.move.Move> generateChessLibLegalMoves(
      com.github.bhlangonijr.chesslib.Board board) {
    try {
      return MoveGenerator.generateLegalMoves(board);
    } catch (final MoveGeneratorException e) {
      throw new RuntimeException("ChessLib move generation failed", e);
    }
  }

  private static void printResult(PgnTest pgnTest, int positionCount, Measurement boardBackend,
      Measurement helpmateSearchBoard, Measurement reference, Measurement chessLib) {
    final double denominator = positionCount * MEASURE_ROUNDS;
    final double boardBackendUs = boardBackend.nanoseconds() / denominator / 1000.0;
    final double helpmateSearchBoardUs = helpmateSearchBoard.nanoseconds() / denominator / 1000.0;
    final double referenceUs = reference.nanoseconds() / denominator / 1000.0;
    final double chessLibUs = chessLib.nanoseconds() / denominator / 1000.0;

    System.out.printf("%s%n", pgnTest);
    System.out.printf("  positions: %,d%n", positionCount);
    System.out.printf("  generated moves: boardBackend=%,d helpmateSearchBoard=%,d reference=%,d chesslib=%,d%n",
        boardBackend.moveCount(), helpmateSearchBoard.moveCount(), reference.moveCount(), chessLib.moveCount());
    System.out.printf("  Board backend: %.3f us/position  (%.1fx ChessLib)%n", boardBackendUs,
        boardBackendUs / chessLibUs);
    System.out.printf("  HelpmateSearchBoard buffer path: %.3f us/position  (%.1fx ChessLib)%n", helpmateSearchBoardUs,
        helpmateSearchBoardUs / chessLibUs);
    System.out.printf("  reference oracle: %.3f us/position  (%.1fx ChessLib)%n", referenceUs,
        referenceUs / chessLibUs);
    System.out.printf("  ChessLib: %.3f us/position%n%n", chessLibUs);
  }

  private record PositionPair(Board ashlarBoard, com.github.bhlangonijr.chesslib.Board chessLibBoard) {

  }

  private record Measurement(long nanoseconds, long moveCount) {

  }
}
