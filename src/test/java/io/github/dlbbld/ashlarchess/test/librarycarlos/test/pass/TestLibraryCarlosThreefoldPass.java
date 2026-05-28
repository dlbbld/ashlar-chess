// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.librarycarlos.test.pass;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;

import io.github.dlbbld.ashlarchess.pgn.LenientPgnParser;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.PgnUtility;

class TestLibraryCarlosThreefoldPass {

  @SuppressWarnings("static-method")
  @Test
  void testThreefoldFailsCastleRight() throws Exception {
    final String moves = "1. h3 h6 2. Rh2 Na6 3. Rh1 Nb8 4. Nc3 Nc6 5. Nb1 Nb8 6. Nf3 Nf6 7. Ng1 Ng8";

    final MoveList moveList = new MoveList();
    moveList.loadFromSan(moves);

    final Board board = new Board();
    for (final Move element : moveList) {
      board.doMove(element);
    }
    assertTrue(board.isRepetition());
    assertTrue(isThreefold(moves));
  }

  @SuppressWarnings("static-method")
  @Test
  void testKnightRepetition() throws Exception {
    final String moves = "1. Nf3 Nf6 2. Ng1 Ng8 3. Nf3 Nf6 4. Ng1 Ng8";

    final MoveList moveList = new MoveList();
    moveList.loadFromSan(moves);

    final Board board = new Board();
    for (final Move element : moveList) {
      board.doMove(element);
    }
    assertTrue(board.isRepetition());
    assertTrue(isThreefold(moves));
  }

  @SuppressWarnings("static-method")
  @Test
  void testBishopRepetition() throws Exception {
    final String moves = "1. e4 e5 2. Be2 Be7 3. Bf1 Bf8 4. Bd3 Bd6 5. Bf1 Bf8";

    final MoveList moveList = new MoveList();
    moveList.loadFromSan(moves);

    final Board board = new Board();
    for (final Move element : moveList) {
      board.doMove(element);
    }
    assertTrue(board.isRepetition());
    assertTrue(isThreefold(moves));
  }

  @SuppressWarnings("static-method")
  @Test
  void testAfterLoosingCastlingRight() throws Exception {
    final String moves = "1. e4 e5 2. Nf3 Nf6 3. Ng1 Ng8 4. Ke2 Ke7 5. Ke1 Ke8 6. Na3 Na6 7. Nb1 Nb8";

    final MoveList moveList = new MoveList();
    moveList.loadFromSan(moves);

    final Board board = new Board();
    for (final Move element : moveList) {
      board.doMove(element);
    }
    assertFalse(board.isRepetition());
    assertFalse(isThreefold(moves));
  }

  @SuppressWarnings("static-method")
  @Test
  void testAfterTwoSquareAdvance() throws Exception {
    final String moves = "1. Nf3 Nf6 2. Nc3 c5 3. e3 d5 4. Be2 Ne4 5. Bf1 Nf6 6. Be2 Ne4 7. Bf1 Nf6";

    final MoveList moveList = new MoveList();
    moveList.loadFromSan(moves);

    final Board board = new Board();
    for (final Move element : moveList) {
      board.doMove(element);
    }
    assertTrue(board.isRepetition());
    assertTrue(isThreefold(moves));
  }

  @SuppressWarnings("static-method")
  @Test
  void testCapablancaLasker1921() throws Exception {
    final String moves = "1. d4 d5 2. Nf3 Nf6 3. c4 e6 4. Bg5 Nbd7 5. e3 Be7 6. Nc3 O-O 7. Rc1 b6 8. cxd5 exd5 9. Qa4 c5 10. Qc6 Rb8 11. Nxd5 Bb7 12. Nxe7+ Qxe7 13. Qa4 Rbc8 14. Qa3 Qe6 15. Bxf6 Qxf6 16. Ba6 Bxf3 17. Bxc8 Rxc8 18. gxf3 Qxf3 19. Rg1 Re8 20. Qd3 g6 21. Kf1 Re4 22. Qd1 Qh3+ 23. Rg2 Nf6 24. Kg1 cxd4 25. Rc4 dxe3 26. Rxe4 Nxe4 27. Qd8+ Kg7 28. Qd4+ Nf6 29. fxe3 Qe6 30. Rf2 g5 31. h4 gxh4 32. Qxh4 Ng4 33. Qg5+ Kf8 34. Rf5 h5 35. Qd8+ Kg7 36. Qg5+ Kf8 37. Qd8+ Kg7 38. Qg5+ Kf8";

    final MoveList moveList = new MoveList();
    moveList.loadFromSan(moves);

    final Board board = new Board();
    for (final Move element : moveList) {
      board.doMove(element);
    }
    assertTrue(board.isRepetition());
    assertTrue(isThreefold(moves));
  }

  @SuppressWarnings("static-method")
  @Test
  void testEnPassantCaptureNotPossibleOwnKingInCheck() throws Exception {
    final Board board = new Board();
    board.loadFromFen("8/8/8/8/4p3/8/R2P3k/K7 w - - 0 37");

    board.doMove(new Move(Square.D2, Square.D4));
    board.doMove(new Move(Square.H2, Square.H3));

    board.doMove(new Move(Square.A2, Square.A3));
    board.doMove(new Move(Square.H3, Square.H2));

    board.doMove(new Move(Square.A3, Square.A2));
    board.doMove(new Move(Square.H2, Square.H1));

    board.doMove(new Move(Square.A2, Square.A3));
    board.doMove(new Move(Square.H1, Square.H2));

    board.doMove(new Move(Square.A3, Square.A2));

    assertTrue(board.isRepetition());

    final String pgnText = """
        [FEN "8/8/8/8/4p3/8/R2P3k/K7 w - - 0 37"]

        37. d4+ Kh3 38. Ra3+ Kh2 39. Ra2+ Kh1 40. Ra3 Kh2 41. Ra2+
        """;
    assertTrue(isThreefold(pgnText));
  }

  @SuppressWarnings("static-method")
  @Test
  void testEnPassantCaptureNotPossibleWouldExposeOwnKingToCheck() throws Exception {
    final Board board = new Board();
    board.loadFromFen("6k1/8/8/8/6p1/8/5PR1/6K1 w - - 0 32");

    board.doMove(new Move(Square.F2, Square.F4));
    board.doMove(new Move(Square.G8, Square.F7));

    board.doMove(new Move(Square.G1, Square.F2));
    board.doMove(new Move(Square.F7, Square.G8));

    board.doMove(new Move(Square.F2, Square.G1));
    board.doMove(new Move(Square.G8, Square.H7));

    board.doMove(new Move(Square.G1, Square.H2));
    board.doMove(new Move(Square.H7, Square.G8));

    board.doMove(new Move(Square.H2, Square.G1));

    assertTrue(board.isRepetition());

    final String pgnText = """
        [FEN "6k1/8/8/8/6p1/8/5PR1/6K1 w - - 0 32"]

        32. f4 Kf7 33. Kf2 Kg8 34. Kg1 Kh7 35. Kh2 Kg8 36. Kg1          """;
    assertTrue(isThreefold(pgnText));
  }

  @SuppressWarnings("static-method")
  @Test
  void testEnPassantCapturePossible() throws Exception {
    final String moves = "1. e4 Nf6 2. e5 d5 3. Bc4 Nc6 4. Bf1 Nb8 5. Bc4 Nc6 6. Bf1 Nb8";

    final MoveList moveList = new MoveList();
    moveList.loadFromSan(moves);

    final Board board = new Board();
    for (final Move element : moveList) {
      board.doMove(element);
    }
    assertFalse(board.isRepetition());
    assertFalse(isThreefold(moves));
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlatnyHolzke() throws Exception {
    final String moves = "1. Nf3 Nf6 2. c4 c5 3. b3 d6 4. d4 cxd4 5. Nxd4 e5 6. Nb5 Be6 7. g3 a6 8. N5c3 d5 9. cxd5 Nxd5 10. Bg2 Bb4 11. Bd2 Nc6 12. O-O O-O 13. Na4 Rc8 14. a3 Be7 15. e3 b5 16. Nb2 Qb6 17. Nd3 Rfd8 18. Qe2 Nf6 19. Nc1 e4 20. Bc3 Nd5 21. Bxe4 Nxc3 22. Nxc3 Na5 23. N1a2 Nxb3 24. Rad1 Bc4 25. Qf3 Qf6 26. Qg4 Be6 27. Qe2 Rxc3 28. Nxc3 Qxc3 29. Rxd8+ Bxd8 30. Rd1 Be7 31. Bb7 Nc5 32. Qf3 g6 33. Bd5 Bxd5 34. Qxd5 Qxa3 35. Qe5 Ne6 36. Ra1 Qd6 37. Qxd6 Bxd6 38. Rxa6 Bc5 39. Kf1 Kf8 40. Ke2 Ke7 41. Kd3 Kd7 42. g4 Kc7 43. Ra8 Kc6 44. f4 Be7 45. Rc8+ Kd5 46. Re8 Kd6 47. g5 f5 48. Rb8 Kc6 49. Re8 Kd6 50. Rb8 Kc6 51. Re8 Kd6";

    final MoveList moveList = new MoveList();
    moveList.loadFromSan(moves);

    final Board board = new Board();
    for (final Move element : moveList) {
      board.doMove(element);
    }
    assertFalse(board.isRepetition());
    assertFalse(isThreefold(moves));
  }

  private static boolean isThreefold(String pgnText) {
    final PgnGame pgnGame = LenientPgnParser.parseText(pgnText);
    final io.github.dlbbld.ashlarchess.board.Board boardActual = PgnUtility.calculateBoard(pgnGame);
    return boardActual.isThreefoldRepetition();
  }

}
