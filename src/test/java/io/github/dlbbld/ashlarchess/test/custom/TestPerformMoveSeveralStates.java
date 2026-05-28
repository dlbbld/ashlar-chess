package io.github.dlbbld.ashlarchess.test.custom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.enums.CastlingCheck;
import io.github.dlbbld.ashlarchess.model.LegalMoveKind;
import io.github.dlbbld.ashlarchess.moves.CastlingUtility;

class TestPerformMoveSeveralStates implements EnumConstants {

  @SuppressWarnings("static-method")
  @Test
  void testTrivial() {
    final Board board = new Board();

    // white move 1
    // test before move
    {
      final Side havingMove = WHITE;
      final Square fromSquare = E2;
      final Square toSquare = E4;
      final Piece movingPiece = WHITE_PAWN;

      assertTrue(board.isFirstMove());
      assertEquals(havingMove, board.getHavingMove());

      assertEquals(movingPiece, board.getBitboardPosition().get(fromSquare));
      assertEquals(Piece.NONE, board.getBitboardPosition().get(toSquare));

      final MoveSpecification halfMoveWhite = new MoveSpecification(fromSquare, toSquare);

      assertEquals(CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingUtility
          .calculateQueenSideCastlingCheck(board.getBitboardPosition(), havingMove, board.getCastlingRightWhite()));
      assertEquals(CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingUtility
          .calculateKingSideCastlingCheck(board.getBitboardPosition(), havingMove, board.getCastlingRightWhite()));
      assertEquals(0, board.getHalfMoveClock());

      board.move(halfMoveWhite);

      // test after move
      assertEquals("e4", board.getSan());
      assertEquals("e2-e4", board.getLan());
      assertEquals(0, board.getHalfMoveClock());
      assertFalse(calculateIsEnPassantCaptureLastMove(board));
      assertFalse(calculateIsCastlingLastMove(board));
      assertFalse(calculateIsPromotionLastMove(board));

      assertEquals(Piece.NONE, board.getBitboardPosition().get(fromSquare));
      assertEquals(movingPiece, board.getBitboardPosition().get(toSquare));

      assertEquals(Piece.NONE, board.getLastMove().pieceCaptured());

      assertEquals(movingPiece, board.getLastMove().movingPiece());

      assertEquals(BLACK, board.getHavingMove());

    }

    // black move 1
    // test before move
    {
      final Side havingMove = BLACK;
      final Square fromSquare = D7;
      final Square toSquare = D5;
      final Piece movingPiece = BLACK_PAWN;

      assertFalse(board.isFirstMove());
      assertEquals(havingMove, board.getHavingMove());

      assertEquals(movingPiece, board.getBitboardPosition().get(fromSquare));
      assertEquals(Piece.NONE, board.getBitboardPosition().get(toSquare));

      final MoveSpecification halfMoveBlack = new MoveSpecification(fromSquare, toSquare);

      assertEquals(CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingUtility
          .calculateQueenSideCastlingCheck(board.getBitboardPosition(), havingMove, board.getCastlingRightBlack()));
      assertEquals(CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingUtility
          .calculateKingSideCastlingCheck(board.getBitboardPosition(), havingMove, board.getCastlingRightBlack()));

      board.move(halfMoveBlack);

      // test after move
      assertEquals("d5", board.getSan());
      assertEquals("d7-d5", board.getLan());
      assertEquals(0, board.getHalfMoveClock());
      assertFalse(calculateIsEnPassantCaptureLastMove(board));
      assertFalse(calculateIsCastlingLastMove(board));
      assertFalse(calculateIsPromotionLastMove(board));

      assertEquals(Piece.NONE, board.getBitboardPosition().get(fromSquare));
      assertEquals(movingPiece, board.getBitboardPosition().get(toSquare));

      assertEquals(movingPiece, board.getLastMove().movingPiece());

      assertEquals(WHITE, board.getHavingMove());

    }

    // white move 2
    // test before move
    {
      final Side havingMove = WHITE;
      final Square fromSquare = E4;
      final Square toSquare = D5;
      final Piece movingPiece = WHITE_PAWN;

      assertFalse(board.isFirstMove());
      assertEquals(havingMove, board.getHavingMove());

      assertEquals(movingPiece, board.getBitboardPosition().get(fromSquare));
      assertEquals(BLACK_PAWN, board.getBitboardPosition().get(toSquare));

      final MoveSpecification halfMoveWhite = new MoveSpecification(fromSquare, toSquare);

      assertEquals(CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingUtility
          .calculateQueenSideCastlingCheck(board.getBitboardPosition(), havingMove, board.getCastlingRightWhite()));
      assertEquals(CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingUtility
          .calculateKingSideCastlingCheck(board.getBitboardPosition(), havingMove, board.getCastlingRightWhite()));

      board.move(halfMoveWhite);

      // test after move
      assertEquals("exd5", board.getSan());
      assertEquals("e4xd5", board.getLan());
      assertEquals(0, board.getHalfMoveClock());
      assertFalse(calculateIsEnPassantCaptureLastMove(board));
      assertFalse(calculateIsCastlingLastMove(board));
      assertFalse(calculateIsPromotionLastMove(board));

      assertEquals(Piece.NONE, board.getBitboardPosition().get(fromSquare));
      assertEquals(movingPiece, board.getBitboardPosition().get(toSquare));

      assertEquals(movingPiece, board.getLastMove().movingPiece());

      assertEquals(BLACK, board.getHavingMove());

    }

    // black move 2
    // test before move
    {
      final Side havingMove = BLACK;
      final Square fromSquare = D8;
      final Square toSquare = D5;
      final Piece movingPiece = BLACK_QUEEN;

      assertFalse(board.isFirstMove());
      assertEquals(havingMove, board.getHavingMove());

      assertEquals(movingPiece, board.getBitboardPosition().get(fromSquare));
      assertEquals(WHITE_PAWN, board.getBitboardPosition().get(toSquare));

      final MoveSpecification halfMoveBlack = new MoveSpecification(fromSquare, toSquare);

      assertEquals(CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingUtility
          .calculateQueenSideCastlingCheck(board.getBitboardPosition(), havingMove, board.getCastlingRightBlack()));
      assertEquals(CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingUtility
          .calculateKingSideCastlingCheck(board.getBitboardPosition(), havingMove, board.getCastlingRightBlack()));

      board.move(halfMoveBlack);

      // test after move
      assertEquals("Qxd5", board.getSan());
      assertEquals("Qd8xd5", board.getLan());
      assertEquals(0, board.getHalfMoveClock());
      assertFalse(calculateIsEnPassantCaptureLastMove(board));
      assertFalse(calculateIsCastlingLastMove(board));
      assertFalse(calculateIsPromotionLastMove(board));

      assertEquals(Piece.NONE, board.getBitboardPosition().get(fromSquare));
      assertEquals(movingPiece, board.getBitboardPosition().get(toSquare));

      assertEquals(movingPiece, board.getLastMove().movingPiece());

      assertEquals(WHITE, board.getHavingMove());

    }

    // white move 3
    // test before move
    {
      final Side havingMove = WHITE;
      final Square fromSquare = G2;
      final Square toSquare = G4;
      final Piece movingPiece = WHITE_PAWN;

      assertFalse(board.isFirstMove());
      assertEquals(havingMove, board.getHavingMove());

      assertEquals(movingPiece, board.getBitboardPosition().get(fromSquare));
      assertEquals(Piece.NONE, board.getBitboardPosition().get(toSquare));

      final MoveSpecification halfMoveWhite = new MoveSpecification(fromSquare, toSquare);

      assertEquals(CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingUtility
          .calculateQueenSideCastlingCheck(board.getBitboardPosition(), havingMove, board.getCastlingRightWhite()));
      assertEquals(CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingUtility
          .calculateKingSideCastlingCheck(board.getBitboardPosition(), havingMove, board.getCastlingRightWhite()));

      board.move(halfMoveWhite);

      // test after move
      assertEquals("g4", board.getSan());
      assertEquals("g2-g4", board.getLan());
      assertEquals(0, board.getHalfMoveClock());
      assertFalse(calculateIsEnPassantCaptureLastMove(board));
      assertFalse(calculateIsCastlingLastMove(board));
      assertFalse(calculateIsPromotionLastMove(board));

      assertEquals(Piece.NONE, board.getBitboardPosition().get(fromSquare));
      assertEquals(movingPiece, board.getBitboardPosition().get(toSquare));

      assertEquals(movingPiece, board.getLastMove().movingPiece());

      assertEquals(BLACK, board.getHavingMove());

    }

    // black move 3
    // test before move
    {
      final Side havingMove = BLACK;
      final Square fromSquare = C8;
      final Square toSquare = D7;
      final Piece movingPiece = BLACK_BISHOP;

      assertFalse(board.isFirstMove());
      assertEquals(havingMove, board.getHavingMove());

      assertEquals(movingPiece, board.getBitboardPosition().get(fromSquare));
      assertEquals(Piece.NONE, board.getBitboardPosition().get(toSquare));

      final MoveSpecification halfMoveBlack = new MoveSpecification(fromSquare, toSquare);

      assertEquals(CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingUtility
          .calculateQueenSideCastlingCheck(board.getBitboardPosition(), havingMove, board.getCastlingRightBlack()));
      assertEquals(CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingUtility
          .calculateKingSideCastlingCheck(board.getBitboardPosition(), havingMove, board.getCastlingRightBlack()));

      board.move(halfMoveBlack);

      // test after move
      assertEquals("Bd7", board.getSan());
      assertEquals("Bc8-d7", board.getLan());
      assertEquals(1, board.getHalfMoveClock());
      assertFalse(calculateIsEnPassantCaptureLastMove(board));
      assertFalse(calculateIsCastlingLastMove(board));
      assertFalse(calculateIsPromotionLastMove(board));

      assertEquals(Piece.NONE, board.getBitboardPosition().get(fromSquare));
      assertEquals(movingPiece, board.getBitboardPosition().get(toSquare));

      assertEquals(movingPiece, board.getLastMove().movingPiece());

      assertEquals(WHITE, board.getHavingMove());

    }

    // white move 4
    // test before move
    {
      final Side havingMove = WHITE;
      final Square fromSquare = G4;
      final Square toSquare = G5;
      final Piece movingPiece = WHITE_PAWN;

      assertFalse(board.isFirstMove());
      assertEquals(havingMove, board.getHavingMove());

      assertEquals(movingPiece, board.getBitboardPosition().get(fromSquare));
      assertEquals(Piece.NONE, board.getBitboardPosition().get(toSquare));

      final MoveSpecification halfMoveWhite = new MoveSpecification(fromSquare, toSquare);

      assertEquals(CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingUtility
          .calculateQueenSideCastlingCheck(board.getBitboardPosition(), havingMove, board.getCastlingRightWhite()));
      assertEquals(CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingUtility
          .calculateKingSideCastlingCheck(board.getBitboardPosition(), havingMove, board.getCastlingRightWhite()));

      board.move(halfMoveWhite);

      // test after move
      assertEquals("g5", board.getSan());
      assertEquals("g4-g5", board.getLan());
      assertEquals(0, board.getHalfMoveClock());
      assertFalse(calculateIsEnPassantCaptureLastMove(board));
      assertFalse(calculateIsCastlingLastMove(board));
      assertFalse(calculateIsPromotionLastMove(board));

      assertEquals(Piece.NONE, board.getBitboardPosition().get(fromSquare));
      assertEquals(movingPiece, board.getBitboardPosition().get(toSquare));

      assertEquals(movingPiece, board.getLastMove().movingPiece());

      assertEquals(BLACK, board.getHavingMove());

    }

    // black move 4
    // test before move
    {
      final Side havingMove = BLACK;
      final Square fromSquare = F7;
      final Square toSquare = F5;
      final Piece movingPiece = BLACK_PAWN;

      assertFalse(board.isFirstMove());
      assertEquals(havingMove, board.getHavingMove());

      assertEquals(movingPiece, board.getBitboardPosition().get(fromSquare));
      assertEquals(Piece.NONE, board.getBitboardPosition().get(toSquare));

      final MoveSpecification halfMoveBlack = new MoveSpecification(fromSquare, toSquare);

      assertEquals(CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingUtility
          .calculateQueenSideCastlingCheck(board.getBitboardPosition(), havingMove, board.getCastlingRightBlack()));
      assertEquals(CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingUtility
          .calculateKingSideCastlingCheck(board.getBitboardPosition(), havingMove, board.getCastlingRightBlack()));

      board.move(halfMoveBlack);

      // test after move
      assertEquals("f5", board.getSan());
      assertEquals("f7-f5", board.getLan());
      assertEquals(0, board.getHalfMoveClock());
      assertFalse(calculateIsEnPassantCaptureLastMove(board));
      assertFalse(calculateIsCastlingLastMove(board));
      assertFalse(calculateIsPromotionLastMove(board));

      assertEquals(Piece.NONE, board.getBitboardPosition().get(fromSquare));
      assertEquals(movingPiece, board.getBitboardPosition().get(toSquare));

      assertEquals(movingPiece, board.getLastMove().movingPiece());

      assertEquals(WHITE, board.getHavingMove());

    }

    // white move 5
    // test before move
    {
      final Side havingMove = WHITE;
      final Square fromSquare = G5;
      final Square toSquare = F6;
      final Piece movingPiece = WHITE_PAWN;

      assertFalse(board.isFirstMove());
      assertEquals(havingMove, board.getHavingMove());

      assertEquals(movingPiece, board.getBitboardPosition().get(fromSquare));
      assertEquals(Piece.NONE, board.getBitboardPosition().get(toSquare));

      final MoveSpecification halfMoveWhite = new MoveSpecification(fromSquare, toSquare);

      assertEquals(CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingUtility
          .calculateQueenSideCastlingCheck(board.getBitboardPosition(), havingMove, board.getCastlingRightWhite()));
      assertEquals(CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingUtility
          .calculateKingSideCastlingCheck(board.getBitboardPosition(), havingMove, board.getCastlingRightWhite()));

      board.move(halfMoveWhite);

      // test after move
      assertEquals("gxf6", board.getSan());
      assertEquals("g5xf6", board.getLan());
      assertEquals(0, board.getHalfMoveClock());
      assertTrue(calculateIsEnPassantCaptureLastMove(board));
      assertFalse(calculateIsCastlingLastMove(board));
      assertFalse(calculateIsPromotionLastMove(board));

      assertEquals(Piece.NONE, board.getBitboardPosition().get(fromSquare));
      assertEquals(movingPiece, board.getBitboardPosition().get(toSquare));

      assertEquals(movingPiece, board.getLastMove().movingPiece());

      assertEquals(BLACK, board.getHavingMove());

    }

    // black move 5
    // test before move
    {
      final Side havingMove = BLACK;
      final Square fromSquare = B8;
      final Square toSquare = C6;
      final Piece movingPiece = BLACK_KNIGHT;

      assertFalse(board.isFirstMove());
      assertEquals(havingMove, board.getHavingMove());

      assertEquals(movingPiece, board.getBitboardPosition().get(fromSquare));
      assertEquals(Piece.NONE, board.getBitboardPosition().get(toSquare));

      final MoveSpecification halfMoveBlack = new MoveSpecification(fromSquare, toSquare);

      assertEquals(CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingUtility
          .calculateQueenSideCastlingCheck(board.getBitboardPosition(), havingMove, board.getCastlingRightBlack()));
      assertEquals(CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingUtility
          .calculateKingSideCastlingCheck(board.getBitboardPosition(), havingMove, board.getCastlingRightBlack()));

      board.move(halfMoveBlack);

      // test after move
      assertEquals("Nc6", board.getSan());
      assertEquals("Nb8-c6", board.getLan());
      assertEquals(1, board.getHalfMoveClock());
      assertFalse(calculateIsEnPassantCaptureLastMove(board));
      assertFalse(calculateIsCastlingLastMove(board));
      assertFalse(calculateIsPromotionLastMove(board));

      assertEquals(Piece.NONE, board.getBitboardPosition().get(fromSquare));
      assertEquals(movingPiece, board.getBitboardPosition().get(toSquare));

      assertEquals(movingPiece, board.getLastMove().movingPiece());

      assertEquals(WHITE, board.getHavingMove());

    }

    // white move 6
    // test before move
    {
      final Side havingMove = WHITE;
      final Square fromSquare = G1;
      final Square toSquare = F3;
      final Piece movingPiece = WHITE_KNIGHT;

      assertFalse(board.isFirstMove());
      assertEquals(havingMove, board.getHavingMove());

      assertEquals(movingPiece, board.getBitboardPosition().get(fromSquare));
      assertEquals(Piece.NONE, board.getBitboardPosition().get(toSquare));

      final MoveSpecification halfMoveWhite = new MoveSpecification(fromSquare, toSquare);

      assertEquals(CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingUtility
          .calculateQueenSideCastlingCheck(board.getBitboardPosition(), havingMove, board.getCastlingRightWhite()));
      assertEquals(CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingUtility
          .calculateKingSideCastlingCheck(board.getBitboardPosition(), havingMove, board.getCastlingRightWhite()));

      board.move(halfMoveWhite);

      // test after move
      assertEquals("Nf3", board.getSan());
      assertEquals("Ng1-f3", board.getLan());
      assertEquals(2, board.getHalfMoveClock());
      assertFalse(calculateIsEnPassantCaptureLastMove(board));
      assertFalse(calculateIsCastlingLastMove(board));
      assertFalse(calculateIsPromotionLastMove(board));

      assertEquals(Piece.NONE, board.getBitboardPosition().get(fromSquare));
      assertEquals(movingPiece, board.getBitboardPosition().get(toSquare));

      assertEquals(movingPiece, board.getLastMove().movingPiece());

      assertEquals(BLACK, board.getHavingMove());

    }

    // black move 6
    // test before move
    {
      final Side havingMove = BLACK;
      final Square fromSquare = A7;
      final Square toSquare = A5;
      final Piece movingPiece = BLACK_PAWN;

      assertFalse(board.isFirstMove());
      assertEquals(havingMove, board.getHavingMove());

      assertEquals(movingPiece, board.getBitboardPosition().get(fromSquare));
      assertEquals(Piece.NONE, board.getBitboardPosition().get(toSquare));

      final MoveSpecification halfMoveBlack = new MoveSpecification(fromSquare, toSquare);

      assertEquals(CastlingCheck.SUCCESS, CastlingUtility.calculateQueenSideCastlingCheck(board.getBitboardPosition(),
          havingMove, board.getCastlingRightBlack()));
      assertEquals(CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingUtility
          .calculateKingSideCastlingCheck(board.getBitboardPosition(), havingMove, board.getCastlingRightBlack()));

      board.move(halfMoveBlack);

      // test after move
      assertEquals("a5", board.getSan());
      assertEquals("a7-a5", board.getLan());
      assertEquals(0, board.getHalfMoveClock());
      assertFalse(calculateIsEnPassantCaptureLastMove(board));
      assertFalse(calculateIsCastlingLastMove(board));
      assertFalse(calculateIsPromotionLastMove(board));

      assertEquals(Piece.NONE, board.getBitboardPosition().get(fromSquare));
      assertEquals(movingPiece, board.getBitboardPosition().get(toSquare));

      assertEquals(movingPiece, board.getLastMove().movingPiece());

      assertEquals(WHITE, board.getHavingMove());

    }

    // white move 7
    // test before move
    {
      final Side havingMove = WHITE;
      final Square fromSquare = F1;
      final Square toSquare = C4;
      final Piece movingPiece = WHITE_BISHOP;

      assertFalse(board.isFirstMove());
      assertEquals(havingMove, board.getHavingMove());

      assertEquals(movingPiece, board.getBitboardPosition().get(fromSquare));
      assertEquals(Piece.NONE, board.getBitboardPosition().get(toSquare));

      final MoveSpecification halfMoveWhite = new MoveSpecification(fromSquare, toSquare);

      assertEquals(CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingUtility
          .calculateQueenSideCastlingCheck(board.getBitboardPosition(), havingMove, board.getCastlingRightWhite()));
      assertEquals(CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingUtility
          .calculateKingSideCastlingCheck(board.getBitboardPosition(), havingMove, board.getCastlingRightWhite()));

      board.move(halfMoveWhite);

      // test after move
      assertEquals("Bc4", board.getSan());
      assertEquals("Bf1-c4", board.getLan());
      assertEquals(1, board.getHalfMoveClock());
      assertFalse(calculateIsEnPassantCaptureLastMove(board));
      assertFalse(calculateIsCastlingLastMove(board));
      assertFalse(calculateIsPromotionLastMove(board));

      assertEquals(Piece.NONE, board.getBitboardPosition().get(fromSquare));
      assertEquals(movingPiece, board.getBitboardPosition().get(toSquare));

      assertEquals(movingPiece, board.getLastMove().movingPiece());

      assertEquals(BLACK, board.getHavingMove());

    }

    // black move 7
    // test before move
    {
      final Side havingMove = BLACK;
      final Square fromSquare = A5;
      final Square toSquare = A4;
      final Piece movingPiece = BLACK_PAWN;

      assertFalse(board.isFirstMove());
      assertEquals(havingMove, board.getHavingMove());

      assertEquals(movingPiece, board.getBitboardPosition().get(fromSquare));
      assertEquals(Piece.NONE, board.getBitboardPosition().get(toSquare));

      final MoveSpecification halfMoveBlack = new MoveSpecification(fromSquare, toSquare);

      assertEquals(CastlingCheck.SUCCESS, CastlingUtility.calculateQueenSideCastlingCheck(board.getBitboardPosition(),
          havingMove, board.getCastlingRightBlack()));
      assertEquals(CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingUtility
          .calculateKingSideCastlingCheck(board.getBitboardPosition(), havingMove, board.getCastlingRightBlack()));

      board.move(halfMoveBlack);

      // test after move
      assertEquals("a4", board.getSan());
      assertEquals("a5-a4", board.getLan());
      assertEquals(0, board.getHalfMoveClock());
      assertFalse(calculateIsEnPassantCaptureLastMove(board));
      assertFalse(calculateIsCastlingLastMove(board));
      assertFalse(calculateIsPromotionLastMove(board));

      assertEquals(Piece.NONE, board.getBitboardPosition().get(fromSquare));
      assertEquals(movingPiece, board.getBitboardPosition().get(toSquare));

      assertEquals(movingPiece, board.getLastMove().movingPiece());

      assertEquals(WHITE, board.getHavingMove());

    }

    // white move 8
    // test before move
    {
      final Side havingMove = WHITE;
      final Square fromSquare = B1;
      final Square toSquare = C3;
      final Piece movingPiece = WHITE_KNIGHT;

      assertFalse(board.isFirstMove());
      assertEquals(havingMove, board.getHavingMove());

      assertEquals(movingPiece, board.getBitboardPosition().get(fromSquare));
      assertEquals(Piece.NONE, board.getBitboardPosition().get(toSquare));

      final MoveSpecification halfMoveWhite = new MoveSpecification(fromSquare, toSquare);

      assertEquals(CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingUtility
          .calculateQueenSideCastlingCheck(board.getBitboardPosition(), havingMove, board.getCastlingRightWhite()));
      assertEquals(CastlingCheck.SUCCESS, CastlingUtility.calculateKingSideCastlingCheck(board.getBitboardPosition(),
          havingMove, board.getCastlingRightWhite()));

      board.move(halfMoveWhite);

      // test after move
      assertEquals("Nc3", board.getSan());
      assertEquals("Nb1-c3", board.getLan());
      assertEquals(1, board.getHalfMoveClock());
      assertFalse(calculateIsEnPassantCaptureLastMove(board));
      assertFalse(calculateIsCastlingLastMove(board));
      assertFalse(calculateIsPromotionLastMove(board));

      assertEquals(Piece.NONE, board.getBitboardPosition().get(fromSquare));
      assertEquals(movingPiece, board.getBitboardPosition().get(toSquare));

      assertEquals(movingPiece, board.getLastMove().movingPiece());

      assertEquals(BLACK, board.getHavingMove());

    }
  }

  private static boolean calculateIsEnPassantCaptureLastMove(Board board) {
    return board.getLastMove().kind() == LegalMoveKind.EN_PASSANT_CAPTURE;
  }

  private static boolean calculateIsCastlingLastMove(Board board) {
    return board.getLastMove().kind() == LegalMoveKind.CASTLING;
  }

  private static boolean calculateIsPromotionLastMove(Board board) {
    return board.getLastMove().kind() == LegalMoveKind.PROMOTION;
  }
}