package com.dlb.chess.analyze;

import java.util.Set;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.Rank;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.enums.KingSafetyCheck;
import com.dlb.chess.enums.MovementCheck;
import com.dlb.chess.model.EmptyBoardMove;
import com.dlb.chess.moves.CastlingUtility;
import com.dlb.chess.moves.EnPassantCaptureUtility;
import com.dlb.chess.moves.PawnDiagonalMoveUtility;
import com.dlb.chess.squares.AbstractEmptyBoardSquares;
import com.dlb.chess.squares.KingNonCastlingEmptyBoardSquares;

/**
 * Stateless chess-rules analysis shared by both validation pipelines (MoveSpecification and SAN).
 *
 * <p>
 * The Step A surface is just movement reasoning: piece geometry, path-clearing, occupancy, pawn- specifics (forward
 * one/two, diagonal, en passant) and king-specific non-safety rules (capturing a guarded piece, moving next to the
 * opponent king). Castling lives in {@link CastlingUtility}; king-safety after the move is a separate later layer.
 *
 * <p>
 * Preconditions for {@link #analyzeMovement}:
 * <ul>
 * <li>the move is not a castling move,</li>
 * <li>{@code move.fromSquare()} holds an own piece for {@code havingMove}.</li>
 * </ul>
 */
public final class ChessRuleAnalyzer implements EnumConstants {

  private ChessRuleAnalyzer() {
  }

  public static MovementCheck analyzeMovement(BitboardPosition bitboardPosition, Side havingMove,
      Square enPassantCaptureTargetSquare, MoveSpecification moveSpecification) {
    if (CastlingUtility.calculateIsCastlingMove(moveSpecification)) {
      throw new ProgrammingMistakeException("Castling is not handled by movement analysis");
    }
    final Piece movingPiece = bitboardPosition.get(moveSpecification.fromSquare());
    if (movingPiece == Piece.NONE || movingPiece.getSide() != havingMove) {
      throw new ProgrammingMistakeException("From-square does not hold an own piece");
    }
    return switch (movingPiece.getPieceType()) {
      case PAWN -> analyzePawn(bitboardPosition, havingMove, enPassantCaptureTargetSquare, moveSpecification);
      case KING -> analyzeKing(bitboardPosition, havingMove, moveSpecification);
      case ROOK, KNIGHT, BISHOP, QUEEN -> analyzeStandardPiece(bitboardPosition, havingMove, moveSpecification);
      case NONE -> throw new ProgrammingMistakeException("None piece type is not movable");
    };
  }

  // Cheap variant: returns true iff the move leaves the own king safe. Use this when callers do
  // not need the failure-reason classification (e.g. legal-vs-pseudo-legal split during legal-move
  // enumeration).
  public static boolean isMoveKingSafe(BitboardPosition bitboardPosition, Side havingMove,
      MoveSpecification moveSpecification) {
    if (CastlingUtility.calculateIsCastlingMove(moveSpecification)) {
      throw new ProgrammingMistakeException("Castling king-safety is handled by CastlingCheck");
    }
    return !bitboardPosition.afterMove(moveSpecification, havingMove).isInCheck(havingMove);
  }

  public static KingSafetyCheck analyzeKingSafety(BitboardPosition bitboardPosition, Side havingMove,
      MoveSpecification moveSpecification) {
    if (CastlingUtility.calculateIsCastlingMove(moveSpecification)) {
      throw new ProgrammingMistakeException("Castling king-safety is handled by CastlingCheck");
    }
    final Piece movingPiece = bitboardPosition.get(moveSpecification.fromSquare());
    if (movingPiece == Piece.NONE || movingPiece.getSide() != havingMove) {
      throw new ProgrammingMistakeException("From-square does not hold an own piece");
    }
    // King moves: their king-safety is fully covered by analyzeMovement (KING_CAPTURES_GUARDED_PIECE
    // / KING_MOVES_TO_ATTACKED_EMPTY_SQUARE). The was-in-check distinction has no analog for the
    // king itself, so this method only handles non-king moves.
    if (movingPiece.getPieceType() == KING
        || !bitboardPosition.afterMove(moveSpecification, havingMove).isInCheck(havingMove)) {
      return KingSafetyCheck.SUCCESS;
    }
    final boolean wasInCheck = bitboardPosition.isInCheck(havingMove);
    return wasInCheck ? KingSafetyCheck.NON_KING_LEFT_IN_CHECK : KingSafetyCheck.NON_KING_EXPOSED_TO_CHECK;
  }

  private static MovementCheck analyzePawn(BitboardPosition bitboardPosition, Side havingMove,
      Square enPassantCaptureTargetSquare, MoveSpecification moveSpecification) {
    final Square fromSquare = moveSpecification.fromSquare();
    final Square toSquare = moveSpecification.toSquare();
    final Piece movingPiece = bitboardPosition.get(fromSquare);

    final boolean isForwardMove = calculateIsPawnEmptyBoardMove(havingMove, fromSquare, toSquare);
    final boolean isDiagonalMove = PawnDiagonalMoveUtility.calculateIsPawnDiagonalMove(havingMove, fromSquare, toSquare);

    if (!isForwardMove && !isDiagonalMove) {
      return MovementCheck.NOT_POSSIBLE;
    }

    if (isForwardMove) {
      if (EnPassantCaptureUtility.calculateIsPawnTwoSquareAdvanceMove(movingPiece, moveSpecification)) {
        final MovementCheck twoSquareCheck = analyzePawnTwoSquareAdvance(bitboardPosition, havingMove,
            moveSpecification);
        if (twoSquareCheck != MovementCheck.SUCCESS) {
          return twoSquareCheck;
        }
      }
      return analyzePawnOneSquareAdvance(bitboardPosition, havingMove, moveSpecification);
    }

    return analyzePawnDiagonal(bitboardPosition, havingMove, enPassantCaptureTargetSquare, moveSpecification);
  }

  private static MovementCheck analyzePawnTwoSquareAdvance(BitboardPosition bitboardPosition, Side havingMove,
      MoveSpecification moveSpecification) {
    final Square toSquare = moveSpecification.toSquare();
    final Square jumpOverSquare = Square.calculateJumpOverSquare(havingMove, toSquare);
    final boolean jumpOverSquareIsEmpty = bitboardPosition.isEmpty(jumpOverSquare);
    final boolean toSquareIsEmpty = bitboardPosition.isEmpty(toSquare);

    if (!jumpOverSquareIsEmpty) {
      if (!toSquareIsEmpty) {
        return MovementCheck.PAWN_FORWARD_TWO_SQUARE_BOTH_SQUARE_NOT_EMPTY;
      }
      return MovementCheck.PAWN_FORWARD_TWO_SQUARE_JUMP_OVER_SQUARE_ONLY_NOT_EMPTY;
    }

    if (!toSquareIsEmpty) {
      return MovementCheck.PAWN_FORWARD_TWO_SQUARE_TO_SQUARE_ONLY_NOT_EMPTY;
    }
    return MovementCheck.SUCCESS;
  }

  private static MovementCheck analyzePawnOneSquareAdvance(BitboardPosition bitboardPosition, Side havingMove,
      MoveSpecification moveSpecification) {
    final Square toSquare = moveSpecification.toSquare();
    if (bitboardPosition.isEmpty(toSquare)) {
      return MovementCheck.SUCCESS;
    }
    final Piece pieceOnToSquare = bitboardPosition.get(toSquare);
    if (pieceOnToSquare.getSide() == havingMove) {
      return MovementCheck.PAWN_FORWARD_ONE_SQUARE_TO_SQUARE_NOT_EMPTY_OWN_PIECE;
    }
    return MovementCheck.PAWN_FORWARD_ONE_SQUARE_TO_SQUARE_NOT_EMPTY_OPPONENT_PIECE;
  }

  private static MovementCheck analyzePawnDiagonal(BitboardPosition bitboardPosition, Side havingMove,
      Square enPassantCaptureTargetSquare, MoveSpecification moveSpecification) {
    final Square toSquare = moveSpecification.toSquare();
    final Piece capturedPiece = bitboardPosition.get(toSquare);

    if (capturedPiece == Piece.NONE) {
      return analyzePawnEnPassant(havingMove, enPassantCaptureTargetSquare, moveSpecification);
    }
    if (capturedPiece.getSide() == havingMove) {
      return MovementCheck.PAWN_DIAGONAL_OWN_PIECE;
    }
    // opponent piece on diagonal target - pseudo-legal capture
    return MovementCheck.SUCCESS;
  }

  private static MovementCheck analyzePawnEnPassant(Side havingMove, Square enPassantCaptureTargetSquare,
      MoveSpecification moveSpecification) {
    final Square toSquare = moveSpecification.toSquare();
    if (!Rank.calculateIsPawnEnPassantCaptureToRank(havingMove, toSquare.getRank())) {
      return MovementCheck.PAWN_EN_PASSANT_WRONG_RANK;
    }
    if (!enPassantCaptureTargetSquare.equals(toSquare)) {
      return MovementCheck.PAWN_EN_PASSANT_NO_IMMEDIATE_BEFORE_TWO_SQUARE_ADVANCE;
    }
    return MovementCheck.SUCCESS;
  }

  private static MovementCheck analyzeStandardPiece(BitboardPosition bitboardPosition, Side havingMove,
      MoveSpecification moveSpecification) {
    final Square fromSquare = moveSpecification.fromSquare();
    final Square toSquare = moveSpecification.toSquare();
    final Piece movingPiece = bitboardPosition.get(fromSquare);
    final Piece capturedPiece = bitboardPosition.get(toSquare);

    final Set<EmptyBoardMove> emptyBoardMoves = AbstractEmptyBoardSquares
        .calculateNonPawnEmptyBoardMoves(movingPiece.getPieceType(), fromSquare);
    if (!calculateIsEmptyBoardMove(toSquare, emptyBoardMoves)) {
      return MovementCheck.NOT_POSSIBLE;
    }
    if (capturedPiece != Piece.NONE && capturedPiece.getSide() == havingMove) {
      return MovementCheck.TO_SQUARE_OCCUPIED_BY_OWN_PIECE;
    }
    return switch (movingPiece.getPieceType()) {
      case ROOK, BISHOP, QUEEN -> bitboardPosition.potentialToSquares(fromSquare, 0L).contains(toSquare)
          ? MovementCheck.SUCCESS
          : MovementCheck.LONG_RANGE_PIECE_JUMPS_OVER_PIECE;
      case KNIGHT -> MovementCheck.SUCCESS;
      case PAWN, KING, NONE -> throw new ProgrammingMistakeException(
          "Unexpected piece type for standard analysis: " + movingPiece.getPieceType());
    };
  }

  private static MovementCheck analyzeKing(BitboardPosition bitboardPosition, Side havingMove,
      MoveSpecification moveSpecification) {
    final Square fromSquare = moveSpecification.fromSquare();
    final Square toSquare = moveSpecification.toSquare();
    final Piece pieceOnToSquare = bitboardPosition.get(toSquare);

    final Set<EmptyBoardMove> emptyBoardMoves = AbstractEmptyBoardSquares.calculateNonPawnEmptyBoardMoves(KING,
        fromSquare);
    if (!calculateIsEmptyBoardMove(toSquare, emptyBoardMoves)) {
      return MovementCheck.NOT_POSSIBLE;
    }
    if (pieceOnToSquare != Piece.NONE && pieceOnToSquare.getSide() == havingMove) {
      return MovementCheck.TO_SQUARE_OCCUPIED_BY_OWN_PIECE;
    }
    if (calculateIsMoveNextToOpponentKing(bitboardPosition, havingMove, toSquare)) {
      return MovementCheck.KING_MOVES_NEXT_TO_OPPONENT_KING;
    }
    // Post-move attack detection (rather than pre-move attackedSquares): when the king moves
    // along an opponent long-range piece's line, the king itself was the blocker; pre-move
    // attackedSquares would not include the destination, but the king IS attacked there after
    // moving off its current square. BitboardPosition.afterMove + isInCheck encodes this directly.
    if (!bitboardPosition.afterMove(moveSpecification, havingMove).isInCheck(havingMove)) {
      return MovementCheck.SUCCESS;
    }
    if (pieceOnToSquare != Piece.NONE && pieceOnToSquare.getSide() == havingMove.getOppositeSide()) {
      return MovementCheck.KING_CAPTURES_GUARDED_PIECE;
    }
    // Empty destination, attacked after move - discriminated from KING_CAPTURES_GUARDED_PIECE.
    return MovementCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE;
  }

  private static boolean calculateIsMoveNextToOpponentKing(BitboardPosition bitboardPosition, Side havingMove,
      Square toSquare) {
    final Square opponentKingSquare = bitboardPosition.kingSquare(havingMove.getOppositeSide());
    final Set<Square> opponentKingAttackedSquareSet = KingNonCastlingEmptyBoardSquares
        .getKingSquares(opponentKingSquare);
    return opponentKingAttackedSquareSet.contains(toSquare);
  }

  private static boolean calculateIsEmptyBoardMove(Square toSquare, Set<EmptyBoardMove> emptyBoardMoves) {
    for (final EmptyBoardMove emptyBoardMove : emptyBoardMoves) {
      if (emptyBoardMove.toSquare() == toSquare) {
        return true;
      }
    }
    return false;
  }

  private static boolean calculateIsPawnEmptyBoardMove(Side havingMove, Square fromSquare, Square toSquare) {
    final Set<EmptyBoardMove> emptyBoardMoves = AbstractEmptyBoardSquares.calculatePawnEmptyBoardMoves(havingMove,
        fromSquare);
    return calculateIsEmptyBoardMove(toSquare, emptyBoardMoves);
  }
}
