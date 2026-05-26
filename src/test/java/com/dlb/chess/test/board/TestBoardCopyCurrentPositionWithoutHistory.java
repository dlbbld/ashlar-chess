package com.dlb.chess.test.board;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.dlb.chess.bitboard.StaticPositionBridge;
import com.dlb.chess.board.Board;
import com.dlb.chess.fen.constants.FenConstants;

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

  private static void checkAfterMoves(String... sanMoves) {
    final Board source = new Board();
    source.movesStrict(sanMoves);

    final Board actual = source.copyCurrentPositionWithoutHistory();

    assertEquivalentHistorylessBoard(source, actual);
  }

  private static void checkFromFen(String fen) {
    final Board source = new Board(fen);
    final Board copy = source.copyCurrentPositionWithoutHistory();
    final Board copyOfCopy = copy.copyCurrentPositionWithoutHistory();

    assertEquivalentHistorylessBoard(source, copy);
    assertEquivalentHistorylessBoard(source, copyOfCopy);
  }

  /**
   * Same piece arrangement, side-to-move, castling rights, en-passant target square and fullmove number — but
   * <em>no</em> move history (no fivefold tracking) and the halfmove clock reset to zero (no seventy-five-move). See
   * the contract documented on {@code Board.copyCurrentPositionWithoutHistory}.
   */
  private static void assertEquivalentHistorylessBoard(Board source, Board actual) {
    // no history
    assertEquals(0, actual.getPerformedHalfMoveCount());
    assertEquals(0, actual.getPerformedLegalMoveList().size());

    // halfmove clock reset, everything else preserved
    assertEquals(0, actual.getHalfMoveClock());
    assertEquals(StaticPositionBridge.toStaticPosition(source.getBitboardPosition()),
        StaticPositionBridge.toStaticPosition(actual.getBitboardPosition()));
    assertEquals(source.getHavingMove(), actual.getHavingMove());
    assertEquals(source.getCastlingRightWhite(), actual.getCastlingRightWhite());
    assertEquals(source.getCastlingRightBlack(), actual.getCastlingRightBlack());
    assertEquals(source.getEnPassantCaptureTargetSquare(), actual.getEnPassantCaptureTargetSquare());
    assertEquals(source.getFullMoveNumber(), actual.getFullMoveNumber());
    assertEquals(source.getLegalMoves(), actual.getLegalMoves());
    assertEquals(source.isCheck(), actual.isCheck());
    assertEquals(source.isCheckmate(), actual.isCheckmate());
    assertEquals(source.isStalemate(), actual.isStalemate());
    assertEquals(source.isInsufficientMaterial(), actual.isInsufficientMaterial());
    assertEquals(source.isDeadPositionQuick(), actual.isDeadPositionQuick());
  }
}
