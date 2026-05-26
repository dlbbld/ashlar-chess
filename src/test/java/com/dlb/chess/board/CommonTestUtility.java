package com.dlb.chess.board;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dlb.chess.bitboard.StaticPositionBridge;
import com.dlb.chess.common.constants.EnumConstants;

public abstract class CommonTestUtility implements EnumConstants {

  public static void checkBoardsAgainstEachOtherAll(Board boardFirst, LibraryCarlosBoard boardSecond) {
    checkBoardsAgainstEachOther(boardFirst, boardSecond, true);
  }

  public static void checkBoardsAgainstEachOtherAll(Board boardFirst, Board boardSecond) {
    checkBoardsAgainstEachOther(boardFirst, boardSecond, true);
  }

  public static void checkBoardsAgainstEachOtherExcludeHistory(Board boardFirst, Board boardSecond) {
    checkBoardsAgainstEachOther(boardFirst, boardSecond, false);
  }

  // RE must be aligned with board API interface
  // Must contain all methods in API interface which are not implemented in AbstractBoard
  // that is also crucial performance wise because in AbstractBoard there are methods taking very long,
  // and testing against makes no sense as always true as long board implentation match, what we test here.
  private static void checkBoardsAgainstEachOther(Board boardFirst, LibraryCarlosBoard boardSecond,
      boolean isIncludeHistory) {

    assertEquals(boardFirst.isCheck(), boardSecond.isCheck());
    assertEquals(boardFirst.isCheckmate(), boardSecond.isCheckmate());
    assertEquals(boardFirst.isStalemate(), boardSecond.isStalemate());

    assertEquals(boardFirst.isInsufficientMaterial(), boardSecond.isInsufficientMaterial());
    assertEquals(boardFirst.isInsufficientMaterial(WHITE), boardSecond.isInsufficientMaterial(WHITE));
    assertEquals(boardFirst.isInsufficientMaterial(BLACK), boardSecond.isInsufficientMaterial(BLACK));

    assertEquals(boardFirst.getHalfMoveClock(), boardSecond.getHalfMoveClock());

    assertEquals(boardFirst.getFullMoveNumber(), boardSecond.getFullMoveNumber());
    if (isIncludeHistory) {
      assertEquals(boardFirst.getInitialFenFullMoveNumber(), boardSecond.getInitialFenFullMoveNumber());
    }

    if (isIncludeHistory) {
      assertEquals(boardFirst.getRepetitionCount(), boardSecond.getRepetitionCount());
    }

    assertEquals(boardFirst.isFiftyMove(), boardSecond.isFiftyMove());
    // extremely slow for performing and unperforming all legal moves per position
    assertEquals(boardFirst.canClaimFiftyMoveRule(), boardSecond.canClaimFiftyMoveRule());
    assertEquals(boardFirst.isSeventyFiveMove(), boardSecond.isSeventyFiveMove());

    if (isIncludeHistory) {
      assertEquals(boardFirst.isThreefoldRepetition(), boardSecond.isThreefoldRepetition());
      // extremely slow for performing and unperforming all legal moves per position
      assertEquals(boardFirst.canClaimThreefoldRepetitionRule(), boardSecond.canClaimThreefoldRepetitionRule());
      assertEquals(boardFirst.isFivefoldRepetition(), boardSecond.isFivefoldRepetition());
    }

    assertEquals(boardFirst.getFen(), boardSecond.getFen());

    if (isIncludeHistory) {
      assertEquals(boardFirst.getInitialFen(), boardSecond.getInitialFen());
    }

    if (!boardFirst.isFirstMove() && !boardSecond.isFirstMove()) {
      assertEquals(boardFirst.getSan(), boardSecond.getSan());
      assertEquals(boardFirst.getLan(), boardSecond.getLan());

      assertEquals(boardFirst.getMovingPiece(), boardSecond.getMovingPiece());
      assertEquals(boardFirst.isCapture(), boardSecond.isCapture());
    }

    assertEquals(boardFirst.getHavingMove(), boardSecond.getHavingMove());

    assertEquals(boardFirst.isEnPassantCapturePossible(), boardSecond.isEnPassantCapturePossible());

    assertEquals(boardFirst.getCastlingRightWhite(), boardSecond.getCastlingRightWhite());
    assertEquals(boardFirst.getCastlingRightBlack(), boardSecond.getCastlingRightBlack());
    // in super
    // assertEquals(boardFirst.getCastlingRight(WHITE), boardSecond.getCastlingRight(WHITE));
    // in super
    // assertEquals(boardFirst.getCastlingRight(BLACK), boardSecond.getCastlingRight(BLACK));

    if (isIncludeHistory) {
      assertEquals(boardFirst.getPerformedHalfMoveCount(), boardSecond.getPerformedHalfMoveCount());
    }

    if (isIncludeHistory) {
      assertEquals(boardFirst.getDynamicPositionList(), boardSecond.getDynamicPositionList());
      assertEquals(boardFirst.getDynamicPosition(), boardSecond.getDynamicPosition());

      assertEquals(boardFirst.getPossibleMoveSpecificationList(), boardSecond.getPossibleMoveSpecificationList());

      assertEquals(boardFirst.getLegalMoves(), boardSecond.getLegalMoves());
      assertEquals(boardFirst.getPerformedMoveSpecificationList(), boardSecond.getPerformedMoveSpecificationList());
      // in super
      // assertEquals(boardFirst.getLegalMovesRepresentation(), boardSecond.getLegalMovesRepresentation());
      assertEquals(boardFirst.getLegalMovesSan(), boardSecond.getLegalMovesSan());
    }

    // in super
    // assertEquals(boardFirst.getLegalMovesUci(), boardSecond.getLegalMovesUci());
    if (!boardFirst.isFirstMove() && !boardSecond.isFirstMove()) {
      assertEquals(boardFirst.getLastMove(), boardSecond.getLastMove());
    }

    assertEquals(StaticPositionBridge.toStaticPosition(boardFirst.getBitboardPosition()),
        StaticPositionBridge.toStaticPosition(boardSecond.getBitboardPosition()));
    if (!boardFirst.isFirstMove() && !boardSecond.isFirstMove()) {
      assertEquals(boardFirst.getBitboardPositionBeforeLastMove(), boardSecond.getBitboardPositionBeforeLastMove());
    }

    if (isIncludeHistory) {
      assertEquals(boardFirst.isFirstMove(), boardSecond.isFirstMove());
    }

    assertEquals(boardFirst.getEnPassantCaptureTargetSquare(), boardSecond.getEnPassantCaptureTargetSquare());

  }

  // Body duplicated from above to support Board-vs-Board comparison (internal consistency tests).
  // Java doesn't let us share a body without re-introducing a common supertype, which is exactly the
  // ChessBoard interface that this branch deleted. Both bodies must stay in sync if either is changed.
  private static void checkBoardsAgainstEachOther(Board boardFirst, Board boardSecond, boolean isIncludeHistory) {

    assertEquals(boardFirst.isCheck(), boardSecond.isCheck());
    assertEquals(boardFirst.isCheckmate(), boardSecond.isCheckmate());
    assertEquals(boardFirst.isStalemate(), boardSecond.isStalemate());

    assertEquals(boardFirst.isInsufficientMaterial(), boardSecond.isInsufficientMaterial());
    assertEquals(boardFirst.isInsufficientMaterial(WHITE), boardSecond.isInsufficientMaterial(WHITE));
    assertEquals(boardFirst.isInsufficientMaterial(BLACK), boardSecond.isInsufficientMaterial(BLACK));

    assertEquals(boardFirst.getHalfMoveClock(), boardSecond.getHalfMoveClock());

    assertEquals(boardFirst.getFullMoveNumber(), boardSecond.getFullMoveNumber());
    if (isIncludeHistory) {
      assertEquals(boardFirst.getInitialFenFullMoveNumber(), boardSecond.getInitialFenFullMoveNumber());
    }

    if (isIncludeHistory) {
      assertEquals(boardFirst.getRepetitionCount(), boardSecond.getRepetitionCount());
    }

    assertEquals(boardFirst.isFiftyMove(), boardSecond.isFiftyMove());
    assertEquals(boardFirst.canClaimFiftyMoveRule(), boardSecond.canClaimFiftyMoveRule());
    assertEquals(boardFirst.isSeventyFiveMove(), boardSecond.isSeventyFiveMove());

    if (isIncludeHistory) {
      assertEquals(boardFirst.isThreefoldRepetition(), boardSecond.isThreefoldRepetition());
      assertEquals(boardFirst.canClaimThreefoldRepetitionRule(), boardSecond.canClaimThreefoldRepetitionRule());
      assertEquals(boardFirst.isFivefoldRepetition(), boardSecond.isFivefoldRepetition());
    }

    assertEquals(boardFirst.getFen(), boardSecond.getFen());

    if (isIncludeHistory) {
      assertEquals(boardFirst.getInitialFen(), boardSecond.getInitialFen());
    }

    if (!boardFirst.isFirstMove() && !boardSecond.isFirstMove()) {
      assertEquals(boardFirst.getSan(), boardSecond.getSan());
      assertEquals(boardFirst.getLan(), boardSecond.getLan());
      assertEquals(boardFirst.getMovingPiece(), boardSecond.getMovingPiece());
      assertEquals(boardFirst.isCapture(), boardSecond.isCapture());
    }

    assertEquals(boardFirst.getHavingMove(), boardSecond.getHavingMove());
    assertEquals(boardFirst.isEnPassantCapturePossible(), boardSecond.isEnPassantCapturePossible());
    assertEquals(boardFirst.getCastlingRightWhite(), boardSecond.getCastlingRightWhite());
    assertEquals(boardFirst.getCastlingRightBlack(), boardSecond.getCastlingRightBlack());

    if (isIncludeHistory) {
      assertEquals(boardFirst.getPerformedHalfMoveCount(), boardSecond.getPerformedHalfMoveCount());
      assertEquals(boardFirst.getDynamicPositionList(), boardSecond.getDynamicPositionList());
      assertEquals(boardFirst.getDynamicPosition(), boardSecond.getDynamicPosition());
      assertEquals(boardFirst.getPossibleMoveSpecificationList(), boardSecond.getPossibleMoveSpecificationList());
      assertEquals(boardFirst.getLegalMoves(), boardSecond.getLegalMoves());
      assertEquals(boardFirst.getPerformedMoveSpecificationList(), boardSecond.getPerformedMoveSpecificationList());
      assertEquals(boardFirst.getLegalMovesSan(), boardSecond.getLegalMovesSan());
    }

    if (!boardFirst.isFirstMove() && !boardSecond.isFirstMove()) {
      assertEquals(boardFirst.getLastMove(), boardSecond.getLastMove());
    }

    assertEquals(StaticPositionBridge.toStaticPosition(boardFirst.getBitboardPosition()),
        StaticPositionBridge.toStaticPosition(boardSecond.getBitboardPosition()));
    if (!boardFirst.isFirstMove() && !boardSecond.isFirstMove()) {
      assertEquals(boardFirst.getBitboardPositionBeforeLastMove(), boardSecond.getBitboardPositionBeforeLastMove());
    }

    if (isIncludeHistory) {
      assertEquals(boardFirst.isFirstMove(), boardSecond.isFirstMove());
    }

    assertEquals(boardFirst.getEnPassantCaptureTargetSquare(), boardSecond.getEnPassantCaptureTargetSquare());
  }

}
