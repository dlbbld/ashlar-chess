package com.dlb.chess.moves;

import java.util.Set;
import java.util.TreeSet;

import com.dlb.chess.analyze.ChessRuleAnalyzer;
import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.CastlingRight;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.common.utility.StaticPositionUtility;
import com.dlb.chess.enums.KingSafetyCheck;
import com.dlb.chess.model.LegalMove;
import com.dlb.chess.model.LegalMoveCalculation;
import com.dlb.chess.model.LegalMoveKind;
import com.dlb.chess.model.PseudoLegalMove;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public abstract class AbstractLegalMoves implements EnumConstants {

  protected static void checkPiece(Side havingMove, Piece candidatePiece, PieceType expectedPieceType)
      throws IllegalArgumentException {
    if (candidatePiece == Piece.NONE || candidatePiece.getSide() != havingMove
        || candidatePiece.getPieceType() != expectedPieceType) {
      throw new IllegalArgumentException(
          "The source square must be occupied by a " + havingMove + " " + expectedPieceType);
    }
  }

  public static ImmutableList<LegalMove> calculateLegalMoves(StaticPosition staticPosition, Side havingMove,
      CastlingRight castlingRight, final Square enPassantCaptureTargetSquare) {
    // The bottom-up call returns a TreeSet (sorted via LegalMove.compareTo). Wrapping with copyOfList preserves the
    // sorted iteration order as a List, making the move ordering part of the public contract.
    return Nulls.copyOfList(
        calculateLegalMovesBottomUp(staticPosition, havingMove, castlingRight, enPassantCaptureTargetSquare));
  }

  /**
   * Public bridge to {@link KingCastlingLegalMoves}. Returns only the castling legal moves for {@code havingMove}
   * given the current castling rights. Used by {@code BitboardLegalMoveFactory.calculateLegalMoves} during the
   * switchover: the bitboard generates non-castling moves; castling stays on the StaticPosition-based path until
   * the relocation phase.
   */
  public static Set<LegalMove> calculateCastlingLegalMoves(StaticPosition staticPosition, Side havingMove,
      CastlingRight castlingRight) {
    return KingCastlingLegalMoves.calculateKingCastlingLegalMoves(staticPosition, havingMove, castlingRight);
  }

  private static Set<LegalMove> calculateLegalMovesBottomUp(StaticPosition staticPosition,
      Square enPassantCaptureTargetSquare, CastlingRight castlingRight, Side havingMove, Square fromSquare) {
    final PieceType pieceType = staticPosition.get(fromSquare).getPieceType();
    return switch (pieceType) {
      case PAWN -> PawnLegalMoves.calculatePawnLegalMoves(staticPosition, enPassantCaptureTargetSquare, havingMove,
          fromSquare);
      case ROOK -> RookLegalMoves.calculateRookLegalMoves(staticPosition, havingMove, fromSquare);
      case KNIGHT -> KnightLegalMoves.calculateKnightLegalMoves(staticPosition, havingMove, fromSquare);
      case BISHOP -> BishopLegalMoves.calculateBishopLegalMoves(staticPosition, havingMove, fromSquare);
      case QUEEN -> QueenLegalMoves.calculateQueenLegalMoves(staticPosition, havingMove, fromSquare);
      case KING -> KingLegalMoves.calculateKingLegalMoves(staticPosition, castlingRight, havingMove, fromSquare);
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  private static Set<LegalMove> calculateLegalMovesBottomUp(StaticPosition staticPosition, Side havingMove,
      CastlingRight castlingRight, final Square enPassantCaptureTargetSquare) {

    final Set<LegalMove> resultSet = new TreeSet<>();
    for (final Square fromSquare : Square.REAL) {
      if (staticPosition.isOwnPiece(fromSquare, havingMove)) {
        final Set<LegalMove> currentMovingPieceSet = calculateLegalMovesBottomUp(staticPosition,
            enPassantCaptureTargetSquare, castlingRight, havingMove, fromSquare);
        resultSet.addAll(currentMovingPieceSet);
      }
    }
    return resultSet;
  }

  static ImmutableSet<LegalMove> calculateLegalMoveSet(StaticPosition staticPosition, Side havingMove,
      Square fromSquare, Set<Square> toSquareSet) {
    return calculateLegalMoveCalculation(staticPosition, havingMove, fromSquare, toSquareSet).legalMoveSet();
  }

  public static LegalMoveCalculation calculateLegalMoveCalculation(StaticPosition staticPosition, Side havingMove,
      Square fromSquare, Set<Square> toSquareSet) {

    final Piece movingPiece = staticPosition.get(fromSquare);

    final Set<LegalMove> legalMoveSet = new TreeSet<>();
    final Set<PseudoLegalMove> pseudoLegalMoveSet = new TreeSet<>();

    for (final Square toSquare : toSquareSet) {
      final MoveSpecification moveSpecification = new MoveSpecification(fromSquare, toSquare);
      final var pieceCaptured = staticPosition.isEmpty(toSquare) ? Piece.NONE : staticPosition.get(toSquare);

      if (pieceCaptured != Piece.NONE && pieceCaptured.getPieceType() == PieceType.KING) {
        continue;
      }

      if (ChessRuleAnalyzer.isMoveKingSafe(staticPosition, havingMove, moveSpecification)) {
        // This helper services non-pawn, non-castling moves only (rook / knight / bishop / queen / king-non-castling).
        // Pawn moves go through PawnLegalMoves; castling goes through KingCastlingLegalMoves. None of those routes lead
        // here, so the kind is always NORMAL.
        final LegalMove legalMove = new LegalMove(moveSpecification, movingPiece, pieceCaptured, LegalMoveKind.NORMAL);
        legalMoveSet.add(legalMove);
      } else {
        final PseudoLegalMove pseudoLegalMove = new PseudoLegalMove(moveSpecification, movingPiece, pieceCaptured);
        pseudoLegalMoveSet.add(pseudoLegalMove);
      }
    }
    final KingSafetyCheck pseudoLegalKingSafety;
    if (!legalMoveSet.isEmpty() || pseudoLegalMoveSet.isEmpty()) {
      pseudoLegalKingSafety = KingSafetyCheck.SUCCESS;
    } else if (StaticPositionUtility.calculateIsCheck(staticPosition, havingMove)) {
      pseudoLegalKingSafety = KingSafetyCheck.NON_KING_LEFT_IN_CHECK;
    } else {
      pseudoLegalKingSafety = KingSafetyCheck.NON_KING_EXPOSED_TO_CHECK;
    }
    return new LegalMoveCalculation(Nulls.copyOfSet(legalMoveSet), Nulls.copyOfSet(pseudoLegalMoveSet),
        pseudoLegalKingSafety);
  }

}
