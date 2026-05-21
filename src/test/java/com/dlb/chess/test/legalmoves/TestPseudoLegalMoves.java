package com.dlb.chess.test.legalmoves;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.dlb.chess.bitboard.StaticPositionBridge;
import com.dlb.chess.board.Board;
import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.common.utility.SetUtility;
import com.dlb.chess.enums.KingSafetyCheck;
import com.dlb.chess.model.LegalMoveCalculation;
import com.dlb.chess.model.PseudoLegalMove;
import com.dlb.chess.moves.AbstractLegalMoves;
import com.dlb.chess.squares.BishopPotentialToSquares;
import com.dlb.chess.squares.KingNonCastlingPotentialToSquares;
import com.dlb.chess.squares.KnightPotentialToSquares;
import com.dlb.chess.squares.PawnPotentialToSquares;
import com.dlb.chess.squares.QueenPotentialToSquares;
import com.dlb.chess.squares.RookPotentialToSquares;

class TestPseudoLegalMoves implements EnumConstants {

  // --- White ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteKnight() {
    // Knight e4 pinned along e-file (king e1, rook e8). All knight moves expose king.
    final Board board = new Board("k3r3/8/8/8/4N3/8/8/4K3 w - - 0 1", false);
    final StaticPosition sp = StaticPositionBridge.toStaticPosition(board.getBitboardPosition());

    final Set<Square> toSquares = KnightPotentialToSquares.calculateKnightPotentialToSquares(sp, E4, WHITE);
    final LegalMoveCalculation calc = AbstractLegalMoves.calculateLegalMoveCalculation(sp, WHITE, E4, toSquares);

    assertTrue(calc.legalMoveSet().isEmpty());
    assertFalse(calc.pseudoLegalMoveSet().isEmpty());
    assertEquals(KingSafetyCheck.NON_KING_EXPOSED_TO_CHECK, calc.pseudoLegalKingSafety());
  }

  @SuppressWarnings("static-method")
  @Test
  void testWhiteBishop() {
    // Bishop e4 pinned along e-file (king e1, rook e8). All bishop moves are diagonal, off e-file.
    final Board board = new Board("4r2k/8/8/8/4B3/8/8/4K3 w - - 0 1", false);
    final StaticPosition sp = StaticPositionBridge.toStaticPosition(board.getBitboardPosition());

    final Set<Square> toSquares = BishopPotentialToSquares.calculateBishopPotentialToSquares(sp, E4, WHITE);
    final LegalMoveCalculation calc = AbstractLegalMoves.calculateLegalMoveCalculation(sp, WHITE, E4, toSquares);

    assertTrue(calc.legalMoveSet().isEmpty());
    assertFalse(calc.pseudoLegalMoveSet().isEmpty());
    assertEquals(KingSafetyCheck.NON_KING_EXPOSED_TO_CHECK, calc.pseudoLegalKingSafety());
  }

  @SuppressWarnings("static-method")
  @Test
  void testWhiteRook() {
    // Rook c3 pinned along diagonal a5-e1 (king e1, bishop a5). Diagonal clear.
    // Rook can't move diagonally, so all rook moves expose king.
    final Board board = new Board("k7/8/8/b7/8/2R5/8/4K3 w - - 0 1", false);
    final StaticPosition sp = StaticPositionBridge.toStaticPosition(board.getBitboardPosition());

    final Set<Square> toSquares = RookPotentialToSquares.calculateRookPotentialToSquares(sp, C3, WHITE);
    final LegalMoveCalculation calc = AbstractLegalMoves.calculateLegalMoveCalculation(sp, WHITE, C3, toSquares);

    assertTrue(calc.legalMoveSet().isEmpty());
    assertFalse(calc.pseudoLegalMoveSet().isEmpty());
    assertEquals(KingSafetyCheck.NON_KING_EXPOSED_TO_CHECK, calc.pseudoLegalKingSafety());
  }

  @SuppressWarnings("static-method")
  @Test
  void testWhiteQueen() {
    // Queen e4 pinned along e-file (king e1, rook e8). Diagonal clear.
    // Queen has legal moves along e-file AND pseudo-legal moves off e-file.
    final Board board = new Board("4r2k/8/8/8/4Q3/8/8/4K3 w - - 0 1", false);
    final StaticPosition sp = StaticPositionBridge.toStaticPosition(board.getBitboardPosition());

    final Set<Square> toSquares = QueenPotentialToSquares.calculateQueenPotentialToSquares(sp, E4, WHITE);
    final LegalMoveCalculation calc = AbstractLegalMoves.calculateLegalMoveCalculation(sp, WHITE, E4, toSquares);

    assertFalse(calc.legalMoveSet().isEmpty());
    assertFalse(calc.pseudoLegalMoveSet().isEmpty());
  }

  @SuppressWarnings("static-method")
  @Test
  void testWhitePawn() {
    // Pawn e2 pinned along rank 2 (king b2, rook h2). Rook blocked by pawn.
    // Forward blocked by black pawns on e3/d3. Only move exd3 leaves rank 2, exposing king.
    final Board board = new Board("k7/8/8/8/8/3pp3/1K2P2r/8 w - - 0 1", false);
    final StaticPosition sp = StaticPositionBridge.toStaticPosition(board.getBitboardPosition());

    final Set<Square> toSquares = PawnPotentialToSquares.calculatePawnPotentialToSquares(sp, Square.NONE, E2, WHITE);
    final LegalMoveCalculation calc = AbstractLegalMoves.calculateLegalMoveCalculation(sp, WHITE, E2, toSquares);

    assertTrue(calc.legalMoveSet().isEmpty());
    assertEquals(1, calc.pseudoLegalMoveSet().size());
    final PseudoLegalMove move = SetUtility.getOnly(calc.pseudoLegalMoveSet());
    assertEquals(new MoveSpecification(E2, D3), move.moveSpecification());
    assertEquals(KingSafetyCheck.NON_KING_EXPOSED_TO_CHECK, calc.pseudoLegalKingSafety());
  }

  @SuppressWarnings("static-method")
  @Test
  void testWhiteKing() {
    // King a1 boxed in by own pawn a2 and own bishop b1. Only move Kxb2 is attacked by rook b3.
    final Board board = new Board("k7/8/8/8/8/1r6/Pr6/KB6 w - - 0 1", false);
    final StaticPosition sp = StaticPositionBridge.toStaticPosition(board.getBitboardPosition());

    final Set<Square> toSquares = KingNonCastlingPotentialToSquares.calculateKingNonCastlingPotentialToSquares(sp, A1,
        WHITE);
    final LegalMoveCalculation calc = AbstractLegalMoves.calculateLegalMoveCalculation(sp, WHITE, A1, toSquares);

    assertTrue(calc.legalMoveSet().isEmpty());
    assertEquals(1, calc.pseudoLegalMoveSet().size());
    final PseudoLegalMove move = SetUtility.getOnly(calc.pseudoLegalMoveSet());
    assertEquals(new MoveSpecification(A1, B2), move.moveSpecification());
    assertEquals(KingSafetyCheck.NON_KING_EXPOSED_TO_CHECK, calc.pseudoLegalKingSafety());
  }

  // --- Black ---

  @SuppressWarnings("static-method")
  @Test
  void testBlackKnight() {
    // Knight e5 pinned along e-file (king e8, rook e1). All knight moves expose king.
    final Board board = new Board("4k3/8/8/4n3/8/8/8/K3R3 b - - 0 1", false);
    final StaticPosition sp = StaticPositionBridge.toStaticPosition(board.getBitboardPosition());

    final Set<Square> toSquares = KnightPotentialToSquares.calculateKnightPotentialToSquares(sp, E5, BLACK);
    final LegalMoveCalculation calc = AbstractLegalMoves.calculateLegalMoveCalculation(sp, BLACK, E5, toSquares);

    assertTrue(calc.legalMoveSet().isEmpty());
    assertFalse(calc.pseudoLegalMoveSet().isEmpty());
    assertEquals(KingSafetyCheck.NON_KING_EXPOSED_TO_CHECK, calc.pseudoLegalKingSafety());
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackBishop() {
    // Bishop e5 pinned along e-file (king e8, rook e1). All bishop moves are diagonal, off e-file.
    final Board board = new Board("4k3/8/8/4b3/8/8/8/4R2K b - - 0 1", false);
    final StaticPosition sp = StaticPositionBridge.toStaticPosition(board.getBitboardPosition());

    final Set<Square> toSquares = BishopPotentialToSquares.calculateBishopPotentialToSquares(sp, E5, BLACK);
    final LegalMoveCalculation calc = AbstractLegalMoves.calculateLegalMoveCalculation(sp, BLACK, E5, toSquares);

    assertTrue(calc.legalMoveSet().isEmpty());
    assertFalse(calc.pseudoLegalMoveSet().isEmpty());
    assertEquals(KingSafetyCheck.NON_KING_EXPOSED_TO_CHECK, calc.pseudoLegalKingSafety());
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackRook() {
    // Rook c6 pinned along diagonal e8-a4 (king e8, bishop a4). Diagonal clear.
    // Rook can't move diagonally, so all rook moves expose king.
    final Board board = new Board("4k3/8/2r5/8/B7/8/8/K7 b - - 0 1", false);
    final StaticPosition sp = StaticPositionBridge.toStaticPosition(board.getBitboardPosition());

    final Set<Square> toSquares = RookPotentialToSquares.calculateRookPotentialToSquares(sp, C6, BLACK);
    final LegalMoveCalculation calc = AbstractLegalMoves.calculateLegalMoveCalculation(sp, BLACK, C6, toSquares);

    assertTrue(calc.legalMoveSet().isEmpty());
    assertFalse(calc.pseudoLegalMoveSet().isEmpty());
    assertEquals(KingSafetyCheck.NON_KING_EXPOSED_TO_CHECK, calc.pseudoLegalKingSafety());
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackQueen() {
    // Queen e5 pinned along e-file (king e8, rook e1). Diagonal clear.
    // Queen has legal moves along e-file AND pseudo-legal moves off e-file.
    final Board board = new Board("4k3/8/8/4q3/8/8/8/4R2K b - - 0 1", false);
    final StaticPosition sp = StaticPositionBridge.toStaticPosition(board.getBitboardPosition());

    final Set<Square> toSquares = QueenPotentialToSquares.calculateQueenPotentialToSquares(sp, E5, BLACK);
    final LegalMoveCalculation calc = AbstractLegalMoves.calculateLegalMoveCalculation(sp, BLACK, E5, toSquares);

    assertFalse(calc.legalMoveSet().isEmpty());
    assertFalse(calc.pseudoLegalMoveSet().isEmpty());
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackPawn() {
    // Pawn e7 pinned along rank 7 (king b7, rook h7). Rook blocked by pawn.
    // Forward blocked by white pawns on e6/d6. Only move exd6 leaves rank 7, exposing king.
    final Board board = new Board("8/1k2p2R/3PP3/8/8/8/8/K7 b - - 0 1", false);
    final StaticPosition sp = StaticPositionBridge.toStaticPosition(board.getBitboardPosition());

    final Set<Square> toSquares = PawnPotentialToSquares.calculatePawnPotentialToSquares(sp, Square.NONE, E7, BLACK);
    final LegalMoveCalculation calc = AbstractLegalMoves.calculateLegalMoveCalculation(sp, BLACK, E7, toSquares);

    assertTrue(calc.legalMoveSet().isEmpty());
    assertEquals(1, calc.pseudoLegalMoveSet().size());
    final PseudoLegalMove move = SetUtility.getOnly(calc.pseudoLegalMoveSet());
    assertEquals(new MoveSpecification(E7, D6), move.moveSpecification());
    assertEquals(KingSafetyCheck.NON_KING_EXPOSED_TO_CHECK, calc.pseudoLegalKingSafety());
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackKing() {
    // King a8 boxed in by own pawn a7 and own bishop b8. Only move Kxb7 is attacked by rook b6.
    final Board board = new Board("kb6/pR6/1R6/8/8/8/8/K7 b - - 0 1", false);
    final StaticPosition sp = StaticPositionBridge.toStaticPosition(board.getBitboardPosition());

    final Set<Square> toSquares = KingNonCastlingPotentialToSquares.calculateKingNonCastlingPotentialToSquares(sp, A8,
        BLACK);
    final LegalMoveCalculation calc = AbstractLegalMoves.calculateLegalMoveCalculation(sp, BLACK, A8, toSquares);

    assertTrue(calc.legalMoveSet().isEmpty());
    assertEquals(1, calc.pseudoLegalMoveSet().size());
    final PseudoLegalMove move = SetUtility.getOnly(calc.pseudoLegalMoveSet());
    assertEquals(new MoveSpecification(A8, B7), move.moveSpecification());
    assertEquals(KingSafetyCheck.NON_KING_EXPOSED_TO_CHECK, calc.pseudoLegalKingSafety());
  }

  // --- King left in check ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteKingLeftInCheck() {
    // White king e1 in check from black rook e8. White knight on b1 can move to c3
    // but that doesn't resolve the check on the e-file.
    final Board board = new Board("4r2k/8/8/8/8/8/8/1N2K3 w - - 0 1", false);
    final StaticPosition sp = StaticPositionBridge.toStaticPosition(board.getBitboardPosition());

    final Set<Square> toSquares = KnightPotentialToSquares.calculateKnightPotentialToSquares(sp, B1, WHITE);
    final LegalMoveCalculation calc = AbstractLegalMoves.calculateLegalMoveCalculation(sp, WHITE, B1, toSquares);

    assertTrue(calc.legalMoveSet().isEmpty());
    assertFalse(calc.pseudoLegalMoveSet().isEmpty());
    assertEquals(KingSafetyCheck.NON_KING_LEFT_IN_CHECK, calc.pseudoLegalKingSafety());
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackKingLeftInCheck() {
    // Black king e8 in check from white rook e1. Black knight on b8 can move to c6
    // but that doesn't resolve the check on the e-file.
    final Board board = new Board("1n2k3/8/8/8/8/8/8/4R2K b - - 0 1", false);
    final StaticPosition sp = StaticPositionBridge.toStaticPosition(board.getBitboardPosition());

    final Set<Square> toSquares = KnightPotentialToSquares.calculateKnightPotentialToSquares(sp, B8, BLACK);
    final LegalMoveCalculation calc = AbstractLegalMoves.calculateLegalMoveCalculation(sp, BLACK, B8, toSquares);

    assertTrue(calc.legalMoveSet().isEmpty());
    assertFalse(calc.pseudoLegalMoveSet().isEmpty());
    assertEquals(KingSafetyCheck.NON_KING_LEFT_IN_CHECK, calc.pseudoLegalKingSafety());
  }

}
