package com.dlb.chess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.TreeSet;

import com.dlb.chess.bitboard.LeanBoard;
import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.common.utility.BasicChessUtility;
import com.dlb.chess.model.LegalMove;
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

/**
 * Step 3.1 differential test for {@link LeanBoard}: after each fixture's final position, {@code LeanBoard.fromBoard}
 * carries the same bitboard / havingMove / EP / castling-rights / halfmove-clock as the source {@link Board}. Then
 * for every legal move on that position, applying the move to both the LeanBoard and a parallel Board produces the
 * same state on both sides; undoing on the LeanBoard restores the original state.
 */
class TestLeanBoard {

  @SuppressWarnings("static-method")
  @Test
  void corpusInitialStateMatchesBoard() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnTestCase testCase : testCaseList.list()) {
        final Board board = testCase.finalPosition();
        final LeanBoard leanBoard = LeanBoard.fromBoard(board);
        assertEquals(board.getBitboardPosition(), leanBoard.bitboardPosition(),
            "bitboardPosition mismatch in fixture " + testCase.pgnName());
        assertEquals(board.getHavingMove(), leanBoard.havingMove(),
            "havingMove mismatch in fixture " + testCase.pgnName());
        assertEquals(board.getEnPassantCaptureTargetSquare(), leanBoard.enPassantTarget(),
            "enPassantTarget mismatch in fixture " + testCase.pgnName());
        assertEquals(board.getCastlingRightWhite(), leanBoard.castlingRight(Side.WHITE),
            "castlingRightWhite mismatch in fixture " + testCase.pgnName());
        assertEquals(board.getCastlingRightBlack(), leanBoard.castlingRight(Side.BLACK),
            "castlingRightBlack mismatch in fixture " + testCase.pgnName());
        assertEquals(board.getHalfMoveClock(), leanBoard.halfmoveClock(),
            "halfmoveClock mismatch in fixture " + testCase.pgnName());
        assertTrue(leanBoard.isFirstMove(), "fresh LeanBoard should report isFirstMove");
      }
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void corpusLegalMovesSetMatchesBoard() {
    // LeanBoard.legalMoves must include both bitboard non-castling moves AND castling targets — set-equal with the
    // production Board's legal move set (taken as MoveSpecifications, so this catches castling drops).
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnTestCase testCase : testCaseList.list()) {
        final Board board = testCase.finalPosition();
        final LeanBoard leanBoard = LeanBoard.fromBoard(board);
        final Set<MoveSpecification> boardSpecs = new TreeSet<>();
        for (final LegalMove legalMove : board.getLegalMoves()) {
          boardSpecs.add(legalMove.moveSpecification());
        }
        assertEquals(boardSpecs, leanBoard.legalMoves(),
            "legalMoves set mismatch in fixture " + testCase.pgnName());
      }
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void corpusEveryLegalMoveAndUnmoveMatchesBoardState() throws Exception {
    // For each fixture, apply EVERY legal move (one ply each) on a parallel Board AND the LeanBoard; verify state
    // matches both sides; unmove on both; verify pre-move state restored. Catches any move-shape-specific bug:
    // captures, castling, EP, all four promotions, pawn double-pushes, etc.
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnTestCase testCase : testCaseList.list()) {
        final Board board = testCase.finalPosition();
        if (board.getLegalMoves().isEmpty()
            || BasicChessUtility.calculateGameStatus(board).isAutomaticTermination()) {
          // Skip terminal positions — board.move() would throw.
          continue;
        }
        final LeanBoard leanBoard = LeanBoard.fromBoard(board);
        // Capture initial state to assert restoration after each unmove.
        final var preBitboard = leanBoard.bitboardPosition();
        final var preHavingMove = leanBoard.havingMove();
        final var preEpTarget = leanBoard.enPassantTarget();
        final var preWhiteCastling = leanBoard.castlingRight(Side.WHITE);
        final var preBlackCastling = leanBoard.castlingRight(Side.BLACK);
        final var preHalfmove = leanBoard.halfmoveClock();

        for (final LegalMove legalMove : board.getLegalMoves()) {
          final MoveSpecification moveSpec = legalMove.moveSpecification();
          board.move(moveSpec);
          leanBoard.move(moveSpec);

          assertEquals(board.getBitboardPosition(), leanBoard.bitboardPosition(),
              "post-move bitboardPosition mismatch for " + moveSpec + " in fixture " + testCase.pgnName());
          assertEquals(board.getHavingMove(), leanBoard.havingMove(),
              "post-move havingMove mismatch for " + moveSpec + " in fixture " + testCase.pgnName());
          assertEquals(board.getEnPassantCaptureTargetSquare(), leanBoard.enPassantTarget(),
              "post-move enPassantTarget mismatch for " + moveSpec + " in fixture " + testCase.pgnName());
          assertEquals(board.getCastlingRightWhite(), leanBoard.castlingRight(Side.WHITE),
              "post-move castlingRightWhite mismatch for " + moveSpec + " in fixture " + testCase.pgnName());
          assertEquals(board.getCastlingRightBlack(), leanBoard.castlingRight(Side.BLACK),
              "post-move castlingRightBlack mismatch for " + moveSpec + " in fixture " + testCase.pgnName());
          assertEquals(board.getHalfMoveClock(), leanBoard.halfmoveClock(),
              "post-move halfmoveClock mismatch for " + moveSpec + " in fixture " + testCase.pgnName());
          assertFalse(leanBoard.isFirstMove(),
              "after move, leanBoard should not report isFirstMove for " + moveSpec);

          leanBoard.unmove();
          board.unmove();

          assertEquals(preBitboard, leanBoard.bitboardPosition(),
              "post-unmove bitboardPosition not restored after " + moveSpec + " in fixture " + testCase.pgnName());
          assertEquals(preHavingMove, leanBoard.havingMove(), "post-unmove havingMove not restored");
          assertEquals(preEpTarget, leanBoard.enPassantTarget(), "post-unmove enPassantTarget not restored");
          assertEquals(preWhiteCastling, leanBoard.castlingRight(Side.WHITE),
              "post-unmove castlingRightWhite not restored");
          assertEquals(preBlackCastling, leanBoard.castlingRight(Side.BLACK),
              "post-unmove castlingRightBlack not restored");
          assertEquals(preHalfmove, leanBoard.halfmoveClock(), "post-unmove halfmoveClock not restored");
          assertTrue(leanBoard.isFirstMove(), "after unmove, leanBoard should report isFirstMove again");
        }
      }
    }
  }
}
