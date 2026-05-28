package com.dlb.chess.common.utility;

import static com.dlb.chess.common.utility.ImmutableUtility.constructListSquare;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.board.model.UpdateSquare;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.fen.FenPieceSymbol;
import com.dlb.chess.moves.CastlingUtility;
import com.dlb.chess.moves.EnPassantCaptureUtility;
import com.dlb.chess.moves.PromotionUtility;
import com.dlb.chess.moves.StandardMoveUtility;
import com.dlb.chess.squares.AbstractAttackedSquares;
import com.google.common.collect.ImmutableList;

public abstract class StaticPositionUtility implements EnumConstants {

  // En-passant (from, to) patterns - test-side duplicate of EnPassantCaptureUtility's
  // WHITE/BLACK_EN_PASSANT_CAPTURE_FROM_TO
  // constants. Mirrored here so the StaticPosition EP-detection oracle does not borrow the production constants
  // (the differential test would then not catch a bitboard EP-detection regression that mistakenly agreed with a
  // bad production constant). Same rationale as the castling oracle duplication in KingCastlingLegalMoves.

  private static final ImmutableList<ImmutableList<Square>> WHITE_EN_PASSANT_CAPTURE_FROM_TO;

  static {
    final List<ImmutableList<Square>> list = new ArrayList<>();
    list.add(constructListSquare(Square.A5, Square.B6));
    list.add(constructListSquare(Square.B5, Square.C6));
    list.add(constructListSquare(Square.C5, Square.D6));
    list.add(constructListSquare(Square.D5, Square.E6));
    list.add(constructListSquare(Square.E5, Square.F6));
    list.add(constructListSquare(Square.F5, Square.G6));
    list.add(constructListSquare(Square.G5, Square.H6));
    list.add(constructListSquare(Square.B5, Square.A6));
    list.add(constructListSquare(Square.C5, Square.B6));
    list.add(constructListSquare(Square.D5, Square.C6));
    list.add(constructListSquare(Square.E5, Square.D6));
    list.add(constructListSquare(Square.F5, Square.E6));
    list.add(constructListSquare(Square.G5, Square.F6));
    list.add(constructListSquare(Square.H5, Square.G6));
    WHITE_EN_PASSANT_CAPTURE_FROM_TO = Nulls.copyOfList(list);
  }

  private static final ImmutableList<ImmutableList<Square>> BLACK_EN_PASSANT_CAPTURE_FROM_TO;

  static {
    final List<ImmutableList<Square>> list = new ArrayList<>();
    list.add(constructListSquare(Square.A4, Square.B3));
    list.add(constructListSquare(Square.B4, Square.C3));
    list.add(constructListSquare(Square.C4, Square.D3));
    list.add(constructListSquare(Square.D4, Square.E3));
    list.add(constructListSquare(Square.E4, Square.F3));
    list.add(constructListSquare(Square.F4, Square.G3));
    list.add(constructListSquare(Square.G4, Square.H3));
    list.add(constructListSquare(Square.B4, Square.A3));
    list.add(constructListSquare(Square.C4, Square.B3));
    list.add(constructListSquare(Square.D4, Square.C3));
    list.add(constructListSquare(Square.E4, Square.D3));
    list.add(constructListSquare(Square.F4, Square.E3));
    list.add(constructListSquare(Square.G4, Square.F3));
    list.add(constructListSquare(Square.H4, Square.G3));
    BLACK_EN_PASSANT_CAPTURE_FROM_TO = Nulls.copyOfList(list);
  }

  public static boolean calculateIsCheck(StaticPosition staticPosition, Side havingMove) {
    final Set<Square> attackedSquares = AbstractAttackedSquares.calculateAttackedSquares(staticPosition,
        havingMove.getOppositeSide());
    final Square kingSquareHavingMove = StaticPositionUtility.calculateKingSquare(staticPosition, havingMove);
    return attackedSquares.contains(kingSquareHavingMove);
  }

  public static Square calculateKingSquare(StaticPosition staticPosition, Side side) {
    for (final Square square : Square.REAL) {
      if (!staticPosition.isEmpty(square)) {
        final Piece piece = staticPosition.get(square);
        if (piece.getPieceType() == KING && piece.getSide() == side) {
          return square;
        }
      }
    }
    throw new ProgrammingMistakeException(
        "There must be a king on the board each, the fun support for no kings is yet to be implemented ....");
  }

  public static String calculatePiecePlacement(StaticPosition staticPosition) {
    final StringBuilder piecePlacement = new StringBuilder();
    for (int rankNumber = 8; rankNumber >= 1; rankNumber--) {
        int consecutiveEmptySquares = 0;
      for (int fileNumber = 1; fileNumber <= 8; fileNumber++) {
        final Square square = Square.calculate(fileNumber, rankNumber);
        final Piece pieceOnSquare = staticPosition.get(square);
        final boolean isEmptySquare = pieceOnSquare == Piece.NONE;
        if (isEmptySquare) {
          consecutiveEmptySquares++;
          if (fileNumber == 8) {
            // last square in the rank
            piecePlacement.append(consecutiveEmptySquares);
          }
        } else {
          if (consecutiveEmptySquares > 0) {
            piecePlacement.append(consecutiveEmptySquares);
            consecutiveEmptySquares = 0;
          }
          piecePlacement.append(FenPieceSymbol.calculate(pieceOnSquare).pieceLetter());
        }
      }
      if (rankNumber != 1) {
        piecePlacement.append("/");
      }
    }
    return Nulls.toString(piecePlacement);
  }

  public static boolean calculateIsKingAttackedAfterMove(StaticPosition staticPosition, Side havingMove,
      MoveSpecification moveSpecification) {
    final StaticPosition staticPositionEvaluateAfterMove = createPositionAfterMove(staticPosition, havingMove,
        moveSpecification);
    return calculateIsCheck(staticPositionEvaluateAfterMove, havingMove);
  }

  public static StaticPosition createPositionAfterMove(StaticPosition staticPosition, Side havingMove,
      MoveSpecification moveSpecification) {

    final List<UpdateSquare> updateSquareList = calculateUpdateSquareList(staticPosition, havingMove,
        moveSpecification);
    return staticPosition.createChangedPosition(updateSquareList);
  }

  private static List<UpdateSquare> calculateUpdateSquareList(StaticPosition staticPosition, Side havingMove,
      MoveSpecification moveSpecification) {

    if (calculateIsPotentialEnPassantCaptureStaticPosition(staticPosition, moveSpecification)) {
      return EnPassantCaptureUtility.performEnPassantCaptureMovements(havingMove, moveSpecification);
    }
    if (CastlingUtility.calculateIsCastlingMove(moveSpecification)) {
      return CastlingUtility.performCastlingMovements(havingMove, moveSpecification);
    }
    if (PromotionUtility.calculateIsPromotionNewMove(moveSpecification)) {
      return PromotionUtility.performPromotionMovements(havingMove, moveSpecification);
    }
    return StandardMoveUtility.performStandardMovements(staticPosition.get(moveSpecification.fromSquare()),
        moveSpecification);
  }

  // EP-capture predicate on the mailbox surface end-to-end. Mirror of EnPassantCaptureUtility's bitboard variant
  // (calculateIsPotentialEnPassantCapture(BitboardPosition, MoveSpecification)). Duplicated test-side so the
  // StaticPosition oracle does not bridge through BitboardPosition for EP detection - that would weaken the oracle
  // for after-move / king-safety checks that depend on whether a move was EP. Same independence rationale as the
  // castling oracle re-implementation in KingCastlingLegalMoves.
  private static boolean calculateIsPotentialEnPassantCaptureStaticPosition(StaticPosition staticPosition,
      MoveSpecification moveSpecification) {
    if (CastlingUtility.calculateIsCastlingMove(moveSpecification)) {
      return false;
    }
    final Piece movingPiece = staticPosition.get(moveSpecification.fromSquare());
    if (movingPiece == Piece.NONE || movingPiece.getPieceType() != PAWN) {
      return false;
    }
    final ImmutableList<Square> fromTo = constructListSquare(moveSpecification.fromSquare(),
        moveSpecification.toSquare());
    return switch (movingPiece.getSide()) {
      case WHITE -> WHITE_EN_PASSANT_CAPTURE_FROM_TO.contains(fromTo)
          && staticPosition.get(moveSpecification.toSquare()) == Piece.NONE;
      case BLACK -> BLACK_EN_PASSANT_CAPTURE_FROM_TO.contains(fromTo)
          && staticPosition.get(moveSpecification.toSquare()) == Piece.NONE;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

}
