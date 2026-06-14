// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.performance;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;

import io.github.dlbbld.ashlarchess.bitboard.BitboardLegalMoveFactory;
import io.github.dlbbld.ashlarchess.bitboard.StaticPositionBridge;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.model.PgnMove;
import io.github.dlbbld.ashlarchess.moves.AbstractLegalMoves;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;
import io.github.dlbbld.ashlarchess.unwinnability.HelpmateSearchBoardPerformanceProbe;

public class MoveGenerationPerformanceSurvey {

  private static final int MAX_POSITIONS_PER_GROUP = 800;
  private static final int WARMUP_ROUNDS = 3;
  private static final int MEASURE_ROUNDS = 20;

  private static final PgnTest[] GROUPS = { PgnTest.MAX_MOVES, PgnTest.RANDOM_NO_REPETITION, PgnTest.WCC2021,
      PgnTest.CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR };

  public static void main(String[] args) {
    for (final PgnTest pgnTest : GROUPS) {
      @SuppressWarnings("null") final @NonNull PgnTest pgnTestNotNull = pgnTest;
      final List<PositionPair> positionList = collectPositions(pgnTestNotNull);
      warmup(positionList);

      final Measurement boardBackend = measureBoardBackend(positionList);
      final Measurement helpmateSearchBoard = measureHelpmateSearchBoard(positionList);
      final Measurement reference = measureReference(positionList);
      final Measurement chessLib = measureChessLib(positionList);

      printResult(pgnTestNotNull, positionList.size(), boardBackend, helpmateSearchBoard, reference, chessLib);
    }
  }

  private static Measurement measureBoardBackend(List<PositionPair> positionList) {
    long moveCount = 0L;
    final long start = System.nanoTime();
    for (int round = 0; round < MEASURE_ROUNDS; round++) {
      for (final PositionPair position : positionList) {
        final Board board = position.ashlarBoard();
        final Square ep = board.getEnPassantCaptureTargetSquare();
        final long enPassantBit = ep == Square.NONE ? 0L : 1L << ep.ordinal();
        moveCount += BitboardLegalMoveFactory.calculateLegalMoves(board.getBitboardPosition(), board.getHavingMove(),
            board.getCastlingRight(board.getHavingMove()), enPassantBit).size();
      }
    }
    return new Measurement(System.nanoTime() - start, moveCount);
  }

  private static Measurement measureHelpmateSearchBoard(List<PositionPair> positionList) {
    final HelpmateSearchBoardPerformanceProbe probe = new HelpmateSearchBoardPerformanceProbe();
    long moveCount = 0L;
    final long start = System.nanoTime();
    for (int round = 0; round < MEASURE_ROUNDS; round++) {
      for (final PositionPair position : positionList) {
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
      for (final PgnMove move : pgnGame.moveList()) {
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
    result.add(new PositionPair(new Board(fen), chessLibBoard));
  }

  private static void warmup(List<PositionPair> positionList) {
    for (int i = 0; i < WARMUP_ROUNDS; i++) {
      measureBoardBackend(positionList);
      measureHelpmateSearchBoard(positionList);
      measureReference(positionList);
      measureChessLib(positionList);
    }
  }

  private static Measurement measureReference(List<PositionPair> positionList) {
    long moveCount = 0L;
    final long start = System.nanoTime();
    for (int round = 0; round < MEASURE_ROUNDS; round++) {
      for (final PositionPair position : positionList) {
        final Board board = position.ashlarBoard();
        moveCount += AbstractLegalMoves.calculateLegalMoves(
            StaticPositionBridge.toStaticPosition(board.getBitboardPosition()), board.getHavingMove(),
            board.getCastlingRight(board.getHavingMove()), board.getEnPassantCaptureTargetSquare()).size();
      }
    }
    return new Measurement(System.nanoTime() - start, moveCount);
  }

  private static Measurement measureChessLib(List<PositionPair> positionList) {
    long moveCount = 0L;
    final long start = System.nanoTime();
    for (int round = 0; round < MEASURE_ROUNDS; round++) {
      for (final PositionPair position : positionList) {
        moveCount += generateChessLibLegalMoves(position.chessLibBoard()).size();
      }
    }
    return new Measurement(System.nanoTime() - start, moveCount);
  }

  @SuppressWarnings("null")
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
