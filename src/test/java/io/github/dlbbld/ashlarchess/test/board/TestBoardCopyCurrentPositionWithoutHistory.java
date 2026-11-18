// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.board;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.bitboard.StaticPositionBridge;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.fen.internal.FenConstants;

class TestBoardCopyCurrentPositionWithoutHistory {

  @SuppressWarnings("static-method")
  @Test
  void testMatchesFenRoundtripAfterPlayedLines() {
    checkAfterMoves("e4", "e5", "Nf3", "Nc6", "Bb5", "a6", "O-O");
    checkAfterMoves("e4", "Nf6", "e5", "d5");
    checkAfterMoves("Nc3", "Nc6", "Nf3", "Nf6", "e4", "e5", "a3", "a6", "Ke2", "Rg8");
  }

  @SuppressWarnings("static-method")
  @Test
  void testMatchesFenConstructedBoardAndIsIdempotent() {
    checkFromFen(FenConstants.FEN_INITIAL_STR);
    checkFromFen("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 37 42");
    checkFromFen("4k3/8/8/3pP3/8/8/8/4K3 w - d6 0 2");
  }

  @SuppressWarnings("static-method")
  @Test
  void testFenOfCopyCarriesTheResetHalfMoveClock() {
    // Regression: the copy kept the source FEN text, so getFen() still showed the old clock until a move was played.
    final Board fromFen = Board.fromFenStrict("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 37 42");
    assertEquals("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 42", fromFen.copyCurrentPositionWithoutHistory().getFen());

    final Board played = new Board();
    played.movesStrict("Nf3", "Nf6", "Ng1", "Ng8");
    assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 4 3", played.getFen());
    assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 3",
        played.copyCurrentPositionWithoutHistory().getFen());
  }

  private static void checkAfterMoves(String... sanMoves) {
    final Board source = new Board();
    source.movesStrict(sanMoves);

    final Board actual = source.copyCurrentPositionWithoutHistory();

    assertEquivalentHistorylessBoard(source, actual);
  }

  private static void checkFromFen(String fen) {
    final Board source = Board.fromFenStrict(fen);
    final Board copy = source.copyCurrentPositionWithoutHistory();
    final Board copyOfCopy = copy.copyCurrentPositionWithoutHistory();

    assertEquivalentHistorylessBoard(source, copy);
    assertEquivalentHistorylessBoard(source, copyOfCopy);
  }

  /**
   * Same piece arrangement, side-to-move, castling rights, en-passant target square and fullmove number - but
   * <em>no</em> move history (no fivefold tracking) and the halfmove clock reset to zero (no seventy-five-move). See
   * the contract documented on {@code Board.copyCurrentPositionWithoutHistory}.
   */
  private static void assertEquivalentHistorylessBoard(Board source, Board actual) {
    // no history
    assertEquals(0, actual.getPerformedMoveCount());
    assertEquals(0, actual.getPerformedMoves().size());

    // halfmove clock reset, everything else preserved
    assertEquals(0, actual.getHalfMoveClock());
    assertEquals(actual.getFen(), Board.fromFenStrict(actual.getFen()).getFen());
    assertEquals(StaticPositionBridge.toStaticPosition(source.getBitboardPosition()),
        StaticPositionBridge.toStaticPosition(actual.getBitboardPosition()));
    assertEquals(source.getSideToMove(), actual.getSideToMove());
    assertEquals(source.getCastlingRightWhite(), actual.getCastlingRightWhite());
    assertEquals(source.getCastlingRightBlack(), actual.getCastlingRightBlack());
    assertEquals(source.getEnPassantCaptureTargetSquare(), actual.getEnPassantCaptureTargetSquare());
    assertEquals(source.getFullMoveNumber(), actual.getFullMoveNumber());
    assertEquals(source.getLegalMoves(), actual.getLegalMoves());
    assertEquals(source.isCheck(), actual.isCheck());
    assertEquals(source.isCheckmate(), actual.isCheckmate());
    assertEquals(source.isStalemate(), actual.isStalemate());
    assertEquals(source.isInsufficientMaterial(), actual.isInsufficientMaterial());
    assertEquals(source.deadPositionQuick(), actual.deadPositionQuick());
  }
}
