package com.dlb.chess.test.performance;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import com.dlb.chess.bitboard.StaticPositionBridge;
import com.dlb.chess.board.Board;
import com.dlb.chess.model.PgnHalfMove;
import com.dlb.chess.moves.AbstractLegalMoves;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;

public class MoveGenerationPerformanceSurvey {

  private static final int MAX_POSITIONS_PER_GROUP = 800;
  private static final int WARMUP_ROUNDS = 3;
  private static final int MEASURE_ROUNDS = 20;

  private static final PgnTest[] GROUPS = { PgnTest.MAX_MOVES, PgnTest.RANDOM_NO_REPETITION, PgnTest.WCC2021,
      PgnTest.CHA_LICHESS_QUICK_NOT_DEPTH_THREE };

  public static void main(String[] args) {
    for (final PgnTest pgnTest : GROUPS) {
      @SuppressWarnings("null") final @NonNull PgnTest pgnTestNotNull = pgnTest;
      final List<PositionPair> positionList = collectPositions(pgnTestNotNull);
      warmup(positionList);

      final Measurement cleanChess = measureCleanChess(positionList);
      final Measurement chessLib = measureChessLib(positionList);

      printResult(pgnTestNotNull, positionList.size(), cleanChess, chessLib);
    }
  }

  private static List<PositionPair> collectPositions(PgnTest pgnTest) {
    final List<PositionPair> result = new ArrayList<>();
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
    for (final PgnTestCase testCase : testCaseList.list()) {
      if (result.size() >= MAX_POSITIONS_PER_GROUP) {
        break;
      }
      final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(pgnTest.getFolderPath(),
          testCase.pgnName());
      final Board board = new Board(pgnGame.startFen(), false);
      addPosition(result, board);
      for (final PgnHalfMove halfMove : pgnGame.halfMoveList()) {
        board.moveStrict(halfMove.san());
        addPosition(result, board);
        if (result.size() >= MAX_POSITIONS_PER_GROUP) {
          break;
        }
      }
    }
    return result;
  }

  private static void addPosition(List<PositionPair> result, Board cleanChessBoard) {
    final String fen = cleanChessBoard.getFen();
    final var chessLibBoard = new com.github.bhlangonijr.chesslib.Board();
    chessLibBoard.loadFromFen(fen);
    result.add(new PositionPair(new Board(fen, false), chessLibBoard));
  }

  private static void warmup(List<PositionPair> positionList) {
    for (var i = 0; i < WARMUP_ROUNDS; i++) {
      measureCleanChess(positionList);
      measureChessLib(positionList);
    }
  }

  private static Measurement measureCleanChess(List<PositionPair> positionList) {
    var moveCount = 0L;
    final var start = System.nanoTime();
    for (var round = 0; round < MEASURE_ROUNDS; round++) {
      for (final PositionPair position : positionList) {
        final Board board = position.cleanChessBoard();
        moveCount += AbstractLegalMoves.calculateLegalMoves(StaticPositionBridge.toStaticPosition(board.getBitboardPosition()), board.getHavingMove(),
            board.getCastlingRight(board.getHavingMove()), board.getEnPassantCaptureTargetSquare()).size();
      }
    }
    return new Measurement(System.nanoTime() - start, moveCount);
  }

  private static Measurement measureChessLib(List<PositionPair> positionList) {
    var moveCount = 0L;
    final var start = System.nanoTime();
    for (var round = 0; round < MEASURE_ROUNDS; round++) {
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

  private static void printResult(PgnTest pgnTest, int positionCount, Measurement cleanChess, Measurement chessLib) {
    final double denominator = positionCount * MEASURE_ROUNDS;
    final var cleanChessMicroseconds = cleanChess.nanoseconds() / denominator / 1000.0;
    final var chessLibMicroseconds = chessLib.nanoseconds() / denominator / 1000.0;
    final var ratio = cleanChessMicroseconds / chessLibMicroseconds;

    System.out.printf("%s%n", pgnTest);
    System.out.printf("  positions: %,d%n", positionCount);
    System.out.printf("  generated moves: clean-chess=%,d chesslib=%,d%n", cleanChess.moveCount(),
        chessLib.moveCount());
    System.out.printf("  clean-chess: %.3f us/position%n", cleanChessMicroseconds);
    System.out.printf("  ChessLib:    %.3f us/position%n", chessLibMicroseconds);
    System.out.printf("  ratio:       %.1fx%n%n", ratio);
  }

  private record PositionPair(Board cleanChessBoard, com.github.bhlangonijr.chesslib.Board chessLibBoard) {

  }

  private record Measurement(long nanoseconds, long moveCount) {

  }
}
