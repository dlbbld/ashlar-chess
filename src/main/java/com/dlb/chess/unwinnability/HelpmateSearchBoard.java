package com.dlb.chess.unwinnability;

import java.util.ArrayList;
import java.util.List;

import com.dlb.chess.bitboard.BitboardLegalMoveFactory;
import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.CastlingRight;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.common.model.DynamicPosition;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.model.CastlingRightBoth;
import com.dlb.chess.model.LegalMove;
import com.dlb.chess.moves.CastlingUtility;
import com.dlb.chess.moves.EnPassantCaptureUtility;
import com.google.common.collect.ImmutableList;

final class HelpmateSearchBoard {

  private DynamicPosition dynamicPosition;
  private Square enPassantCaptureTargetSquare;
  // Initialized to empty so JDT can verify @NonNull at the end of the constructor;
  // refreshDerivedState() overwrites with the real legal moves before any caller observes it.
  @SuppressWarnings("null")
  private ImmutableList<LegalMove> legalMoves = ImmutableList.of();
  private boolean isCheck;
  private boolean isCheckmate;
  private boolean isStalemate;

  private final List<State> stateList = new ArrayList<>();

  private HelpmateSearchBoard(DynamicPosition dynamicPosition, Square enPassantCaptureTargetSquare) {
    this.dynamicPosition = dynamicPosition;
    this.enPassantCaptureTargetSquare = enPassantCaptureTargetSquare;
    refreshDerivedState();
  }

  static HelpmateSearchBoard from(Board board) {
    return new HelpmateSearchBoard(board.getDynamicPosition(), board.getEnPassantCaptureTargetSquare());
  }

  void move(MoveSpecification moveSpecification) {
    stateList
        .add(new State(dynamicPosition, enPassantCaptureTargetSquare, legalMoves, isCheck, isCheckmate, isStalemate));

    final Side beforeHavingMove = getHavingMove();
    final BitboardPosition beforeBitboardPosition = getBitboardPosition();
    final LegalMove moveToPerform = BitboardLegalMoveFactory.toLegalMove(beforeBitboardPosition, moveSpecification,
        beforeHavingMove);

    final Side afterHavingMove = beforeHavingMove.getOppositeSide();
    final CastlingRightBoth afterCastlingRightBoth = CastlingUtility.calculateCastlingRightBoth(
        dynamicPosition.castlingRightWhite(), dynamicPosition.castlingRightBlack(), moveToPerform);
    final Square afterEnPassantCaptureTargetSquare = EnPassantCaptureUtility
        .calculateEnPassantCaptureTargetSquare(moveToPerform);
    final BitboardPosition afterBitboardPosition = beforeBitboardPosition.afterMove(moveSpecification,
        beforeHavingMove);
    final Square afterNormalizedEnPassantCaptureTargetSquare = calculateNormalizedEnPassantCaptureTargetSquare(
        afterEnPassantCaptureTargetSquare, afterHavingMove, afterBitboardPosition);

    dynamicPosition = new DynamicPosition(afterHavingMove, afterBitboardPosition,
        afterNormalizedEnPassantCaptureTargetSquare, afterCastlingRightBoth.castlingRightWhite(),
        afterCastlingRightBoth.castlingRightBlack());
    enPassantCaptureTargetSquare = afterEnPassantCaptureTargetSquare;
    refreshDerivedState();
  }

  void unmove() {
    final State previous = Nulls.remove(stateList, stateList.size() - 1);
    dynamicPosition = previous.dynamicPosition();
    enPassantCaptureTargetSquare = previous.enPassantCaptureTargetSquare();
    legalMoves = previous.legalMoves();
    isCheck = previous.isCheck();
    isCheckmate = previous.isCheckmate();
    isStalemate = previous.isStalemate();
  }

  DynamicPosition getDynamicPosition() {
    return dynamicPosition;
  }

  BitboardPosition getBitboardPosition() {
    return dynamicPosition.bitboardPosition();
  }

  Side getHavingMove() {
    return dynamicPosition.havingMove();
  }

  Square getEnPassantCaptureTargetSquare() {
    return enPassantCaptureTargetSquare;
  }

  CastlingRight getCastlingRight(Side side) {
    return switch (side) {
      case WHITE -> dynamicPosition.castlingRightWhite();
      case BLACK -> dynamicPosition.castlingRightBlack();
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  ImmutableList<LegalMove> getLegalMoves() {
    return legalMoves;
  }

  boolean isCheck() {
    return isCheck;
  }

  boolean isCheckmate() {
    return isCheckmate;
  }

  boolean isStalemate() {
    return isStalemate;
  }

  boolean isInsufficientMaterial(Side side) {
    return UnwinnabilityMaterialBitboard.calculateIsInsufficientMaterial(side, getBitboardPosition());
  }

  private void refreshDerivedState() {
    final var enPassantBit = enPassantCaptureTargetSquare == Square.NONE ? 0L
        : 1L << enPassantCaptureTargetSquare.ordinal();
    legalMoves = BitboardLegalMoveFactory.calculateLegalMoves(getBitboardPosition(), getHavingMove(),
        getCastlingRight(getHavingMove()), enPassantBit);
    isCheck = getBitboardPosition().isInCheck(getHavingMove());
    isCheckmate = isCheck && legalMoves.isEmpty();
    isStalemate = !isCheck && legalMoves.isEmpty();
  }

  private static Square calculateNormalizedEnPassantCaptureTargetSquare(Square enPassantCaptureTargetSquare,
      Side havingMove, BitboardPosition bitboardPosition) {
    if (calculateIsEnPassantCapturePossible(enPassantCaptureTargetSquare, havingMove, bitboardPosition)) {
      return enPassantCaptureTargetSquare;
    }
    return Square.NONE;
  }

  private static boolean calculateIsEnPassantCapturePossible(Square enPassantCaptureTargetSquare, Side havingMove,
      BitboardPosition bitboardPosition) {
    if (enPassantCaptureTargetSquare == Square.NONE) {
      return false;
    }
    if (!Square.calculateHasBehindSquare(havingMove, enPassantCaptureTargetSquare)) {
      throw new ProgrammingMistakeException();
    }
    final Square squareBehind = Square.calculateBehindSquare(havingMove, enPassantCaptureTargetSquare);
    final Piece ownPawn = Piece.calculate(havingMove, PieceType.PAWN);

    if (Square.calculateHasRightSquare(havingMove, squareBehind)) {
      final Square squareRight = Square.calculateRightSquare(havingMove, squareBehind);
      if (bitboardPosition.get(squareRight) == ownPawn && !bitboardPosition
          .isInCheckAfterEnPassantCapture(squareRight, enPassantCaptureTargetSquare, havingMove)) {
        return true;
      }
    }

    if (Square.calculateHasLeftSquare(havingMove, squareBehind)) {
      final Square squareLeft = Square.calculateLeftSquare(havingMove, squareBehind);
      if (bitboardPosition.get(squareLeft) == ownPawn && !bitboardPosition
          .isInCheckAfterEnPassantCapture(squareLeft, enPassantCaptureTargetSquare, havingMove)) {
        return true;
      }
    }
    return false;
  }

  private record State(DynamicPosition dynamicPosition, Square enPassantCaptureTargetSquare,
      ImmutableList<LegalMove> legalMoves, boolean isCheck, boolean isCheckmate, boolean isStalemate) {
  }
}
