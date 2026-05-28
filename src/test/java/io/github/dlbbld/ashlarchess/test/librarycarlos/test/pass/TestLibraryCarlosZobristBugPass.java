package io.github.dlbbld.ashlarchess.test.librarycarlos.test.pass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;

import com.github.bhlangonijr.chesslib.MoveBackup;
import com.github.bhlangonijr.chesslib.game.Game;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.test.librarycarlos.NullsCarlos;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

class TestLibraryCarlosZobristBugPass {

  private static final Logger logger = Nulls.getLogger(TestLibraryCarlosZobristBugPass.class);

  @SuppressWarnings("static-method")
  @Test
  void testFolder() throws Exception {

    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.MAX_MOVES);
    for (final PgnFen testCase : testCaseList.list()) {
      final Path pgnPath = Nulls.pathResolve(testCaseList.pgnTest().getFolderPath(), testCase.pgnName());
      logger.info(testCase.pgnName());
      testPrintingPosition(pgnPath);
      testWithoutPrintingPosition(pgnPath);
    }
  }

  private static void testPrintingPosition(Path pgnPath) throws Exception {

    final PgnHolder pgn = new PgnHolder(pgnPath.toAbsolutePath().toString());

    pgn.loadPgn();
    final Game game = Nulls.getFirst(NullsCarlos.getGames(pgn));
    game.loadMoveText();

    final MoveList moves = game.getHalfMoves();
    final Board board = new Board();

    final Set<String> positionIdentifierSet = new TreeSet<>();
    final Set<Long> hashKeySet = new TreeSet<>();

    {
      positionIdentifierSet.add(calculatePositionIdentifier(board));
      final long hashKey = board.getHistory().getLast();
      hashKeySet.add(hashKey);
    }
    for (final Move move : moves) {
      board.doMove(move);
      {
        positionIdentifierSet.add(calculatePositionIdentifier(board));
        final long hashKey = board.getHistory().getLast();
        hashKeySet.add(hashKey);
      }
    }
    assertEquals(positionIdentifierSet.size(), hashKeySet.size());
    assertFalse(board.isRepetition());
  }

  private static void testWithoutPrintingPosition(Path pgnPath) throws Exception {

    final PgnHolder pgn = new PgnHolder(pgnPath.toAbsolutePath().toString());
    pgn.loadPgn();
    final Game game = Nulls.getFirst(NullsCarlos.getGames(pgn));
    game.loadMoveText();

    final Board board = new Board();
    for (final Move move : game.getHalfMoves()) {
      board.doMove(move);
    }
    assertFalse(board.isRepetition());
  }

  private static String calculatePositionIdentifier(Board board) {
    final StringBuilder identifier = new StringBuilder();

    identifier.append(board.getSideToMove()).append("_");

    boolean isEnPassantCapturePossible = false;
    for (final Move legalMove : MoveGenerator.generateLegalMoves(board)) {
      board.doMove(legalMove);
      final MoveBackup moveBackup = board.getBackup().getLast();
      if (moveBackup.isEnPassantMove()) {
        isEnPassantCapturePossible = true;
        board.undoMove();
        break;
      }
      board.undoMove();
    }
    identifier.append(isEnPassantCapturePossible).append("_");

    identifier.append(board.getCastleRight().get(Side.WHITE)).append("/");
    identifier.append(board.getCastleRight().get(Side.BLACK)).append("_");

    final String fen = board.getFen();
    final String staticPosition = fen.substring(0, fen.indexOf(" "));

    identifier.append(staticPosition);

    return Nulls.toString(identifier);
  }

  @SuppressWarnings("static-method")
  @Test
  void testPerformingBoardsMoves() {
    final MoveList moveList = new MoveList(); // use MoveList to play moves on the board using SAN
    moveList.loadFromSan(
        "1. a4 b5 2. axb5 c6 3. bxc6 h5 4. g4 hxg4 5. f3 gxf3 6. cxd7+ Qxd7 7. Rxa7 fxe2 8. Qxe2 Qxd2+ 9. Bxd2 Rxh2 10. Rxa8 Rxh1 11. Rxb8 Rxg1 12. Rxc8+ Kd7 13. Rc7+ Kd8 14. Rxe7 Nf6 15. Nc3 Ne4 16. Bg5 Rxg5 17. Rxf7 Rg2 18. Qg4 Rxc2 19. Qxg7 Rxb2 20. Be2 Rxe2+ 21. Kd1 Ng5 22. Rf5 Rf2 23. Nd5 Rxf5 24. Nb4 Nf3 25. Nd3 Bb4 26. Qa1 Ke7 27. Kc2 Kf7 28. Kb3 Kg6 29. Ka4 Kg5 30. Nc5 Bc3 31. Kb5 Re5 32. Kb6 Rd5 33. Qa7 Rf5 34. Nd3 Re5 35. Qb7 Rf5 36. Qe7+ Kg6 37. Qe8+ Kg5 38. Kb7 Bd4 39. Ka6 Bc3 40. Kb7");

    final Board board = new Board();
    for (final Move move : moveList) {
      board.doMove(move);
    }
    assertFalse(board.isRepetition());
  }
}
