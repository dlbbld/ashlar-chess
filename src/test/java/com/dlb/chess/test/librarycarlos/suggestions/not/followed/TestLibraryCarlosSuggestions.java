package com.dlb.chess.test.librarycarlos.suggestions.not.followed;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;

class TestLibraryCarlosSuggestions {

  @SuppressWarnings("static-method")
  @Test
  void testSuggestionAllowSanForMoveSpecification() throws Exception {
    final MoveList moveList = new MoveList();
    moveList.loadFromSan("1. e4 Nf6 2. e5 d5 3. Bc4 Nc6 4. Bf1 Nb8 5. Bc4 Nc6 6. Bf1 Nb8");

    final Board board = new Board();
    for (final Move element : moveList) {
      board.doMove(element);
    }

    board.doMove(new Move("d2d4", Side.WHITE)); // ok pseudo LAN works
    board.undoMove();

      boolean isException = false;
    try {
      board.doMove(new Move("d4", Side.WHITE)); // throws exception
    } catch (@SuppressWarnings("unused") final Exception e) {
      isException = true;
    }

    // hoping for no exception (so SAN can be used) but exception (SAN cannot be used)
    assertTrue(isException);
  }

  @SuppressWarnings("static-method")
  @Test
  void testSuggestionBoardSan() throws Exception {
    // proper SAN works
    {
      final MoveList moveList = new MoveList();
      moveList.loadFromSan("1. d4 e6 2. Nc3 Bb4 3. Nf3 Nc6 4. Ng5 h6 5. Ne4");

      final Board board = new Board();
      for (final Move element : moveList) {
        board.doMove(element);
      }
    }

    // inproper SAN should not work but works
    {

      final MoveList moveList = new MoveList();
      moveList.loadFromSan("1. d4 e6 2. Nc3 Bb4 3. Nf3 Nc6 4. Ng5 h6 5. Nge4");

        boolean isException = false;
      try {
        final Board board = new Board();
        for (final Move element : moveList) {
          board.doMove(element);
        }
      } catch (@SuppressWarnings("unused") final Exception e) {
        isException = true;
      }

      // hoping for exception (so proper SAN) but no exception (so improper SAN, overspecification)
      assertFalse(isException);
    }
  }
}
