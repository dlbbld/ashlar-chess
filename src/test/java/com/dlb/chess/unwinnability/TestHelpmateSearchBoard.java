package com.dlb.chess.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.model.LegalMove;

class TestHelpmateSearchBoard {

  @SuppressWarnings("static-method")
  @Test
  void representativeTreesMatchBoardState() {
    assertSearchTreeMatchesBoard(new Board(false), 2);

    for (final SearchCase searchCase : List.of(
        new SearchCase("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1", 1),
        new SearchCase("8/8/8/8/3pP3/8/8/K6k b - e3 0 1", 2),
        new SearchCase("8/8/8/8/k2pP2R/8/8/7K b - e3 0 1", 1),
        new SearchCase("7k/P7/8/8/8/8/7p/K7 w - - 0 1", 1),
        new SearchCase("7k/6Q1/6K1/8/8/8/8/8 b - - 0 1", 0),
        new SearchCase("7k/5Q2/6K1/8/8/8/8/8 b - - 0 1", 0))) {
      assertSearchTreeMatchesBoard(new Board(searchCase.fen(), false), searchCase.depth());
    }
  }

  private static void assertSearchTreeMatchesBoard(Board board, int depth) {
    final HelpmateSearchBoard searchBoard = HelpmateSearchBoard.from(board);
    assertMatchesBoard(board, searchBoard);
    assertSearchTreeMatchesBoard(board, searchBoard, depth);
  }

  private static void assertSearchTreeMatchesBoard(Board board, HelpmateSearchBoard searchBoard, int depth) {
    if (depth == 0 || board.getLegalMoves().isEmpty()) {
      return;
    }
    final List<LegalMove> legalMoves = board.getLegalMoves();
    for (final LegalMove legalMove : legalMoves) {
      final var moveSpecification = legalMove.moveSpecification();
      board.move(moveSpecification);
      searchBoard.move(moveSpecification);
      assertMatchesBoard(board, searchBoard);

      assertSearchTreeMatchesBoard(board, searchBoard, depth - 1);

      searchBoard.unmove();
      board.unmove();
      assertMatchesBoard(board, searchBoard);
    }
  }

  private static void assertMatchesBoard(Board board, HelpmateSearchBoard searchBoard) {
    assertEquals(board.getDynamicPosition(), searchBoard.getDynamicPosition());
    assertEquals(board.getBitboardPosition(), searchBoard.getBitboardPosition());
    assertEquals(board.getHavingMove(), searchBoard.getHavingMove());
    assertEquals(board.getEnPassantCaptureTargetSquare(), searchBoard.getEnPassantCaptureTargetSquare());
    assertEquals(board.getCastlingRightWhite(), searchBoard.getCastlingRight(Side.WHITE));
    assertEquals(board.getCastlingRightBlack(), searchBoard.getCastlingRight(Side.BLACK));
    assertEquals(board.getLegalMoves(), searchBoard.getLegalMoves());
    assertEquals(board.isCheck(), searchBoard.isCheck());
    assertEquals(board.isCheckmate(), searchBoard.isCheckmate());
    assertEquals(board.isStalemate(), searchBoard.isStalemate());
    assertEquals(board.isInsufficientMaterial(Side.WHITE), searchBoard.isInsufficientMaterial(Side.WHITE));
    assertEquals(board.isInsufficientMaterial(Side.BLACK), searchBoard.isInsufficientMaterial(Side.BLACK));
  }

  private record SearchCase(String fen, int depth) {
  }
}
