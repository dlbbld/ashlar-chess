package com.dlb.chess.test.librarycarlos.test.pass;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.test.librarycarlos.NullsCarlos;
import com.dlb.chess.test.model.PgnFen;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;

class TestLibraryCarlosPerformancePass {

  private static final Logger logger = Nulls.getLogger(TestLibraryCarlosPerformancePass.class);

  private static final double LOAD_PGN_DURATION_MAX_MILLISECONDS = 1000.0;

  private static final double LOAD_MOVE_TEXT_DURATION_MAX_MILLISECONDS = 500.0;

  private static final double PER_HALF_MOVE_MAX_MILLISECONDS = 0.5;

  private static List<PgnTest> PGN_TEST_LIST = Nulls.asList(PgnTest.MAX_MOVES);

  @SuppressWarnings("static-method")
  @Test
  void testPerformance() throws Exception {
    for (final PgnTest pgnTest : PGN_TEST_LIST) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        final String pgnName = testCase.pgnName();
        logger.info(pgnName);
        final Path filePath = Nulls.pathResolve(pgnTest.getFolderPath(), pgnName);
        final var pgn = new PgnHolder(filePath.toAbsolutePath().toString());

        final var millisecondsBeforeLoadPgn = System.currentTimeMillis();
        pgn.loadPgn();
        final double millisecondDurationLoadPgn = System.currentTimeMillis() - millisecondsBeforeLoadPgn;

        assertTrue(millisecondDurationLoadPgn < LOAD_PGN_DURATION_MAX_MILLISECONDS);

        logger.info("loadPgn duration seconds: {}", millisecondDurationLoadPgn / 1000);

        final var game = Nulls.getFirst(NullsCarlos.getGames(pgn));
        final var millisecondsBeforeLoadMoveText = System.currentTimeMillis();
        game.loadMoveText();
        final var millisecondDurationLoadMoveText = System.currentTimeMillis() - millisecondsBeforeLoadMoveText;
        assertTrue(millisecondDurationLoadMoveText < LOAD_MOVE_TEXT_DURATION_MAX_MILLISECONDS);

        logger.info("loadMoveText duration seconds: {}", millisecondDurationLoadMoveText / 1000);

        final var moves = game.getHalfMoves();
        final var halfMoves = moves.size();
        logger.info("Half-moves to perform: {}", halfMoves);
        final Board board = new Board();
        final var millisecondsBeforePlayingMoves = System.currentTimeMillis();
        for (final Move move : moves) {
          board.doMove(move);
        }
        final var millisecondDurationPlayingMoves = System.currentTimeMillis() - millisecondsBeforePlayingMoves;

        final double perHalfMoveMilliseconds = millisecondDurationPlayingMoves / halfMoves;

        assertTrue(perHalfMoveMilliseconds < PER_HALF_MOVE_MAX_MILLISECONDS);
        logger.info("Milliseconds per half-move: {}", perHalfMoveMilliseconds);
        logger.info("");
      }
    }

  }

}
