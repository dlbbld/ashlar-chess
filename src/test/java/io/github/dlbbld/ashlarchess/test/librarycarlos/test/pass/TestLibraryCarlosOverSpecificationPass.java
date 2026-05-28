// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.librarycarlos.test.pass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveConversionException;
import com.github.bhlangonijr.chesslib.move.MoveList;

class TestLibraryCarlosOverSpecificationPass {

  @SuppressWarnings("static-method")
  @Test
  void testSanGenerationNoOverspecificationForPinnedPieceOne() throws Exception {
    final MoveList moveList = new MoveList();
    // 1. d4 e6 2. Nc3 Bb4 3. Nf3 Nc6 4. Ng5 h6 5. Ne4

    moveList.add(new Move(Square.D2, Square.D4));
    moveList.add(new Move(Square.E7, Square.E6));
    moveList.add(new Move(Square.B1, Square.C3));
    moveList.add(new Move(Square.F8, Square.B4));
    moveList.add(new Move(Square.G1, Square.F3));
    moveList.add(new Move(Square.B8, Square.C6));
    moveList.add(new Move(Square.F3, Square.G5));
    moveList.add(new Move(Square.H7, Square.H6));
    moveList.add(new Move(Square.G5, Square.E4));

    // correct, last move is not Nge4 because the knight on c3 cannot move to e4
    assertEquals("d4 e6 Nc3 Bb4 Nf3 Nc6 Ng5 h6 Ne4", moveList.toSan().trim());
  }

  @SuppressWarnings("static-method")
  @Test
  void testSanGenerationNoOverspecificationForPinnedPieceTwo() throws Exception {
    final MoveList moveList = new MoveList("4k3/8/8/8/1b6/2N5/8/4K1N1 w - - 0 1");

    // correct, first move is not Nge2 because the knight on c3 cannot move to e2
    moveList.add(new Move(Square.G1, Square.E2)); // Ne2
    assertEquals("Ne2", moveList.toSan().trim());

    // correct, first move is not Nge2 because the knight on c3 cannot move to e2
    moveList.add(new Move(Square.E8, Square.E7)); // Ne2
    assertEquals("Ne2 Ke7", moveList.toSan().trim());
  }

  @SuppressWarnings("static-method")
  @Test
  void testSanParseExceptionOnOverspecification() throws Exception {
    {
      final MoveList moveList = new MoveList("4k3/8/8/8/1b6/2N5/8/4K1N1 w - - 0 1");
      boolean isException = false;
      try {
        moveList.addSanMove("Nge2", false, true);
      } catch (@SuppressWarnings("unused") final MoveConversionException e) {
        isException = true;
      }
      assertTrue(isException);
    }

  }

}
