package com.dlb.chess.test.librarycarlos.test.pass;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveList;

class TestLibraryCarlosHashKeyPass {

  @SuppressWarnings("static-method")
  @Test
  void testTwoHalfMoves() throws Exception {

    final Board board = new Board();
    final Move e2e4 = new Move(Square.E2, Square.E4);
    final Move e7e5 = new Move(Square.E7, Square.E5);
    board.doMove(e2e4);
    board.doMove(e7e5);
    final long hashKeyAsIs = board.getIncrementalHashKey();

    board.undoMove();
    board.doMove(e7e5);
    final long hashKeyAsAfterRedo = board.getIncrementalHashKey();
    assertEquals(hashKeyAsIs, hashKeyAsAfterRedo);

  }

  @SuppressWarnings("static-method")
  @Test
  void testEightHalfMoves() throws Exception {
    final MoveList moveList = new MoveList();
    moveList.loadFromSan("1. e4 e5 2. Nf3 Nc6 3. Bc4 Bc5 4. d4 a5");

    final Board board = new Board();
    for (final Move element : moveList) {
      board.doMove(element);
    }

    for (final Move move : MoveGenerator.generateLegalMoves(board)) {
      board.doMove(move);
      final long hashKeyAsIs = board.getIncrementalHashKey();

      board.undoMove();
      board.doMove(move);
      final long hashKeyAsAfterRedo = board.getIncrementalHashKey();

      assertEquals(hashKeyAsIs, hashKeyAsAfterRedo);

      board.undoMove();
    }

  }

}
