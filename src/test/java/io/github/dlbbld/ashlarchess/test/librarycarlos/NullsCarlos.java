package io.github.dlbbld.ashlarchess.test.librarycarlos;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.MoveBackup;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;

import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;

// where the underlying API is checked to return non null
public class NullsCarlos {

  private NullsCarlos() {
  }

  private static <E> E checkResult(@Nullable E result) {
    if (result == null) {
      throw new ProgrammingMistakeException("Assumed value is not null");
    }
    return result;
  }

  @SuppressWarnings("null")
  public static List<MoveBackup> getBackup(Board board) {
    return checkResult(board.getBackup());
  }

  public static Piece getMovingPiece(MoveBackup moveBackup) {
    return checkResult(moveBackup.getMovingPiece());
  }

  public static Piece getCapturedPiece(MoveBackup moveBackup) {
    return checkResult(moveBackup.getCapturedPiece());
  }

  public static Square getCapturedSquare(MoveBackup moveBackup) {
    return checkResult(moveBackup.getCapturedSquare());
  }

  public static Move getMove(MoveBackup moveBackup) {
    return checkResult(moveBackup.getMove());
  }

  public static Side getSideToMove(Board board) {
    return checkResult(board.getSideToMove());
  }

  public static Side getSideToMove(MoveBackup moveBackup) {
    return checkResult(moveBackup.getSideToMove());
  }

  public static MoveBackup getLast(Board board) {
    return checkResult(board.getBackup().getLast());
  }

  public static Game getGame(PgnHolder pgnHolder, int index) {
    return checkResult(pgnHolder.getGames().get(index));
  }

  public static Piece getPiece(Board board, Square square) {
    return checkResult(board.getPiece(square));
  }

  public static Square getFrom(Move move) {
    return checkResult(move.getFrom());
  }

  public static Square getTo(Move move) {
    return checkResult(move.getTo());
  }

  public static Piece getPromotion(Move move) {
    return checkResult(move.getPromotion());
  }

  public static String getFen(Board board) {
    return checkResult(board.getFen());
  }

  public static String name(Square square) {
    return checkResult(square.name());
  }

  @SuppressWarnings("null")
  public static List<Game> getGames(PgnHolder pgnHolder) {
    return checkResult(pgnHolder.getGames());
  }
}
