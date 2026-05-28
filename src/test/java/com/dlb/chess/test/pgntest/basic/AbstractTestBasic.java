package com.dlb.chess.test.pgntest.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.CastlingMove;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.PromotionPieceType;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.model.LegalMove;
import com.dlb.chess.model.LegalMoveKind;
import com.dlb.chess.test.common.exceptions.SetupException;
import com.dlb.chess.test.common.utility.FileUtility;
import com.dlb.chess.test.model.PgnFen;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

public abstract class AbstractTestBasic implements EnumConstants {

  // we check the following, to detect problems with incompleted test creation and later changes to code or file names:
  // 1a) for each JUnit hardcoded file there is a file in the expected value hardcoded file list
  // 1b) for each file in the expected value hardcoded file list there is a file in the JUnit hardcoded file list
  // 2a) for each file in the JUnit hardcoded file list there is a file in the test folder
  // 2b) for each file in the test folder there is an entry in the JUnit hardcoded file list
  protected static void checkTestFolder(List<String> junitHardcodedPgnNameList, PgnTest pgnTest) {

    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
    final List<String> expectedValueHardcodedFileList = calculatePgnNameList(testCaseList.list());

    // 1a)
    for (final String pgnName : junitHardcodedPgnNameList) {
      if (!expectedValueHardcodedFileList.contains(pgnName)) {
        throw new SetupException("The JUnit hardcoded file \"" + pgnName
            + "\" has no corresponding entry in the expected value hardcoded file list");
      }
    }

    // 1b)
    for (final String pgnName : expectedValueHardcodedFileList) {
      if (!junitHardcodedPgnNameList.contains(pgnName)) {
        throw new SetupException("The expected value hardcoded file \"" + pgnName
            + "\" has no corresponding entry in the JUnit hardcoded list");
      }
    }

    // 2a)
    for (final String pgnName : junitHardcodedPgnNameList) {
      if (!FileUtility.exists(pgnTest.getFolderPath(), pgnName)) {
        throw new SetupException("The JUnit hardcoded file \"" + pgnName + "\" does not exist in the test folder");
      }
    }

    // 2b)
    final List<String> testFolderPgnNameList = FileUtility.readFileNameList(pgnTest.getFolderPath());
    for (final String pgnName : testFolderPgnNameList) {
      if (!junitHardcodedPgnNameList.contains(pgnName)) {
        throw new SetupException(
            "The test directory file \"" + pgnName + "\" does not exist in the JUnit hardcoded file list");
      }
    }

  }

  private static List<String> calculatePgnNameList(List<PgnFen> testCaseList) {
    final List<String> result = new ArrayList<>();
    for (final PgnFen testCase : testCaseList) {
      result.add(testCase.pgnName());
    }
    return result;
  }

  protected static void checkCheckmate(Board board) {
    assertTrue(board.isCheck());
    assertTrue(board.isCheckmate());
    assertFalse(board.isStalemate());
  }

  static void checkCapture(Square fromSquare, Square toSquare, Piece movingPiece, Piece capturedPiece, Board board) {
    assertTrue(board.isCapture());
    assertFalse(board.isCheckmate());
    assertFalse(board.isStalemate());

    final MoveSpecification moveSpecification = new MoveSpecification(fromSquare, toSquare);
    // Callers in this PGN-test suite never set up pawn promotions or en-passant captures via checkCapture; the
    // moving piece is always a sliding piece or knight, so the kind is NORMAL.
    final LegalMove expected = new LegalMove(moveSpecification, movingPiece, capturedPiece, LegalMoveKind.NORMAL);
    assertEquals(expected, board.getLastMove());
  }

  static void checkNonCaptureCheck(Square fromSquare, Square toSquare, Piece movingPiece, Board board) {
    assertFalse(board.isCapture());
    assertTrue(board.isCheck());
    assertFalse(board.isCheckmate());
    assertFalse(board.isStalemate());

    final MoveSpecification moveSpecification = new MoveSpecification(fromSquare, toSquare);
    final LegalMove expected = new LegalMove(moveSpecification, movingPiece, Piece.NONE, LegalMoveKind.NORMAL);
    assertEquals(expected, board.getLastMove());
  }

  static void checkNonCaptureCheckmate(Square fromSquare, Square toSquare, Piece movingPiece, Board board) {
    assertFalse(board.isCapture());
    assertTrue(board.isCheck());
    assertTrue(board.isCheckmate());
    assertFalse(board.isStalemate());

    final MoveSpecification moveSpecification = new MoveSpecification(fromSquare, toSquare);
    final LegalMove expected = new LegalMove(moveSpecification, movingPiece, Piece.NONE, LegalMoveKind.NORMAL);
    assertEquals(expected, board.getLastMove());
  }

  static void checkEnPassantCapture(Side side, Square fromSquare, Square toSquare, Board board) {

    assertTrue(board.isCapture());
    assertFalse(board.isCheck());
    assertFalse(board.isCheckmate());
    assertFalse(board.isStalemate());

    final LegalMove lastMoveEnPassantCapture = board.getLastMove();
    board.unmove();

    final LegalMove secondLastMoveTwoSquareAdvance = board.getLastMove();
    board.unmove();

    board.move(secondLastMoveTwoSquareAdvance.moveSpecification());
    assertFalse(calculateIsEnPassantCaptureLastMove(board));
    assertEquals(toSquare, board.getEnPassantCaptureTargetSquare());

    board.move(lastMoveEnPassantCapture.moveSpecification());
    assertTrue(calculateIsEnPassantCaptureLastMove(board));
    assertEquals(Square.NONE, board.getEnPassantCaptureTargetSquare());

    final MoveSpecification moveSpecification = new MoveSpecification(fromSquare, toSquare);
    final Piece movingPiece = Piece.calculatePawnPiece(side);
    final Piece capturedPiece = Piece.calculatePawnPiece(side.getOppositeSide());
    final LegalMove expected = new LegalMove(moveSpecification, movingPiece, capturedPiece, LegalMoveKind.EN_PASSANT_CAPTURE);

    assertEquals(expected, lastMoveEnPassantCapture);
  }

  static void checkMovingPiece(Square fromSquare, Square toSquare, Piece movingPiece, Board board) {

    checkMovingPiece(fromSquare, toSquare, movingPiece, board, LegalMoveKind.NORMAL);
  }

  static void checkMovingPiece(Square fromSquare, Square toSquare, Piece movingPiece, Board board, LegalMoveKind kind) {

    assertFalse(board.isCapture());
    assertFalse(board.isCheck());
    assertFalse(board.isCheckmate());
    assertFalse(board.isStalemate());

    final MoveSpecification moveSpecification = new MoveSpecification(fromSquare, toSquare);
    final LegalMove expected = new LegalMove(moveSpecification, movingPiece, Piece.NONE, kind);
    assertEquals(expected, board.getLastMove());

  }

  static void checkPromotion(Side side, Square fromSquare, Square toSquare, Piece capturedPiece,
      PromotionPieceType promotionPieceType, Board board) {

    if (capturedPiece == Piece.NONE) {
      assertFalse(board.isCapture());
    } else {
      assertTrue(board.isCapture());
    }
    assertFalse(board.isCheck());
    assertFalse(board.isCheckmate());
    assertFalse(board.isStalemate());

    final MoveSpecification moveSpecification = new MoveSpecification(fromSquare, toSquare, promotionPieceType);
    final Piece movingPiece = Piece.calculatePawnPiece(side);
    final LegalMove expected = new LegalMove(moveSpecification, movingPiece, capturedPiece, LegalMoveKind.PROMOTION);
    assertEquals(expected, board.getLastMove());
  }

  static void checkCastle(Side side, CastlingMove castlingMove, Board board) {
    final MoveSpecification moveSpecification = new MoveSpecification(castlingMove);
    final LegalMove expected = new LegalMove(moveSpecification, Piece.calculateKingPiece(side), Piece.NONE,
        LegalMoveKind.CASTLING);
    assertEquals(expected, board.getLastMove());
  }

  static void checkDoubleCheck(Piece movingPiece, Board board) {
    assertFalse(board.isCapture());
    assertTrue(board.isCheck());
    assertFalse(board.isCheckmate());
    assertFalse(board.isStalemate());

    assertEquals(movingPiece, board.getLastMove().movingPiece());
  }

  static void checkDoubleCheckCheckmate(Piece movingPiece, Board board) {
    assertFalse(board.isCapture());
    assertTrue(board.isCheck());
    assertTrue(board.isCheckmate());
    assertFalse(board.isStalemate());

    assertEquals(movingPiece, board.getLastMove().movingPiece());
  }

  private static boolean calculateIsEnPassantCaptureLastMove(Board board) {
    return board.getLastMove().kind() == LegalMoveKind.EN_PASSANT_CAPTURE;
  }

}
