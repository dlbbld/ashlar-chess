package com.dlb.chess.test.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;

class TestBoardClaimWithOwnMove {

  @SuppressWarnings("static-method")
  @Test
  void canClaimFiftyMoveRuleWithOwnMoveAtBoundary() {
    // fullMoveNumber must be consistent with halfMoveClock per FenParserAdvanced
    // (clock <= 2 * (fullmove - 1) for White to move); 99 clock requires fullmove >= 51.
    final Board oneQuietMoveBeforeFiftyMoveRule = new Board("7k/8/8/8/8/8/4K3/R7 w - - 99 51");

    assertFalse(oneQuietMoveBeforeFiftyMoveRule.isFiftyMove());
    assertTrue(oneQuietMoveBeforeFiftyMoveRule.canClaimFiftyMoveRuleWithOwnMove());
  }

  @SuppressWarnings("static-method")
  @Test
  void isFiftyMoveFalseAtCheckmateEvenWhenClockPastThreshold() {
    // White king a1 in mate from black queen a2 (protected by black king a3). Halfmove clock at
    // 100 — past the 50-move threshold. Post-A1 / python-chess parity: isFiftyMove requires legal
    // moves to exist, so a checkmate position with the clock past the threshold returns false.
    // The 50-move rule cannot fire because checkmate is a higher-precedence termination.
    final Board board = new Board("8/8/8/8/8/k7/q7/K7 w - - 100 60");
    assertTrue(board.isCheckmate(), "precondition: position must be checkmate");
    assertEquals(100, board.getHalfMoveClock(), "precondition: clock past 50-move threshold");
    assertTrue(board.getLegalMoves().isEmpty(), "precondition: no legal moves");
    assertFalse(board.isFiftyMove(), "isFiftyMove must be false at checkmate despite clock past threshold");
    assertFalse(board.canClaimFiftyMoveRule(), "no draw to claim once the game has ended by mate");
  }

  @SuppressWarnings("static-method")
  @Test
  void isSeventyFiveMoveFalseAtCheckmateEvenWhenClockPastThreshold() {
    // Same position semantics as above but with clock at 150 — past the 75-move threshold.
    final Board board = new Board("8/8/8/8/8/k7/q7/K7 w - - 150 80");
    assertTrue(board.isCheckmate(), "precondition: position must be checkmate");
    assertEquals(150, board.getHalfMoveClock(), "precondition: clock past 75-move threshold");
    assertFalse(board.isSeventyFiveMove(), "isSeventyFiveMove must be false at checkmate despite clock past threshold");
  }

  @SuppressWarnings("static-method")
  @Test
  void cannotClaimFiftyMoveRuleWithOwnMoveBeforeBoundary() {
    final Board twoQuietMovesBeforeFiftyMoveRule = new Board("7k/8/8/8/8/8/4K3/R7 w - - 98 50");

    assertFalse(twoQuietMovesBeforeFiftyMoveRule.isFiftyMove());
    assertFalse(twoQuietMovesBeforeFiftyMoveRule.canClaimFiftyMoveRuleWithOwnMove());
  }

  @SuppressWarnings("static-method")
  @Test
  void canClaimThreefoldRepetitionRuleWithOwnMoveWhenMoveCreatesThirdOccurrence() {
    final Board board = new Board();
    // 10-ply knight shuffle bringing each side's knight back twice. After ply 10 the position
    // "Black to move, Nf3, Nb8, pawns e4/e5" has 2 occurrences (after plies 3 and 7). White
    // plays Nf3 to produce its third occurrence, triggering threefold.
    board.movesStrict("e4", "e5", "Nf3", "Nc6", "Ng1", "Nb8", "Nf3", "Nc6", "Ng5", "Nb8");

    assertFalse(board.isThreefoldRepetition());
    assertTrue(board.canClaimThreefoldRepetitionRuleWithOwnMove());

    board.moveStrict("Nf3");

    assertTrue(board.isThreefoldRepetition());
  }

  @SuppressWarnings("static-method")
  @Test
  void cannotClaimThreefoldRepetitionRuleWithOwnMoveWhenNoMoveCreatesThirdOccurrence() {
    final Board board = new Board();
    // First 7 plies of the same shuffle. After ply 7 Black is on move; "Black to move, Nf3,
    // Nb8, pawns e4/e5" has reached its 2nd occurrence. No Black move can produce a third
    // occurrence of any position. Black's Nc6 reaches "White to move, Nf3, Nc6, pawns e4/e5",
    // which has only 1 prior occurrence.
    board.movesStrict("e4", "e5", "Nf3", "Nc6", "Ng1", "Nb8", "Nf3");

    assertFalse(board.isThreefoldRepetition());
    assertFalse(board.canClaimThreefoldRepetitionRuleWithOwnMove());
  }
}
