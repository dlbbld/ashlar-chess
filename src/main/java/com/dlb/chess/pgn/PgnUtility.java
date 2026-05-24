package com.dlb.chess.pgn;

import java.nio.file.Path;

import com.dlb.chess.board.Board;
import com.dlb.chess.model.PgnHalfMove;

public abstract class PgnUtility {

  /**
   * Replays the half-moves of {@code pgnGame} on a fresh board and returns the resulting state.
   */
  public static Board calculateBoard(PgnGame pgnGame) {

    final Board board = new Board(pgnGame.startFen());

    for (final PgnHalfMove halfMove : pgnGame.halfMoveList()) {
      final String san = halfMove.san();
      board.moveStrict(san);
    }

    return board;
  }

  public static Board calculateBoard(Path folderPath, String pgnName) {
    final PgnGame pgnGame = LenientPgnParser.parse(folderPath, pgnName);
    return calculateBoard(pgnGame);
  }

}
