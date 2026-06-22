// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.moves;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.CastlingRight;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.board.MoveSpecification;
import io.github.dlbbld.ashlarchess.common.utility.StaticPositionUtility;
import io.github.dlbbld.ashlarchess.moves.KingSafetyCheck;
import io.github.dlbbld.ashlarchess.board.LegalMove;
import io.github.dlbbld.ashlarchess.model.LegalMoveCalculation;
import io.github.dlbbld.ashlarchess.board.LegalMoveKind;
import io.github.dlbbld.ashlarchess.model.PseudoLegalMove;

public final class LegalMovesSupport {

  private LegalMovesSupport() {
  }

  static void checkPiece(Side sideToMove, Piece candidatePiece, PieceType expectedPieceType)
      throws IllegalArgumentException {
    if (candidatePiece == Piece.NONE || candidatePiece.getSide() != sideToMove
        || candidatePiece.getPieceType() != expectedPieceType) {
      throw new IllegalArgumentException(
          "The source square must be occupied by a " + sideToMove + " " + expectedPieceType);
    }
  }

  public static List<LegalMove> calculateLegalMoves(StaticPosition staticPosition, Side sideToMove,
      CastlingRight castlingRight, final Square enPassantCaptureTargetSquare) {
    // The bottom-up call returns a TreeSet (sorted via LegalMove.compareTo). Wrapping with copyOfList preserves the
    // sorted iteration order as a List, making the move ordering part of the public contract.
    return Nulls.copyOfList(
        calculateLegalMovesBottomUp(staticPosition, sideToMove, castlingRight, enPassantCaptureTargetSquare));
  }

  private static Set<LegalMove> calculateLegalMovesBottomUp(StaticPosition staticPosition,
      Square enPassantCaptureTargetSquare, CastlingRight castlingRight, Side sideToMove, Square fromSquare) {
    final PieceType pieceType = staticPosition.get(fromSquare).getPieceType();
    return switch (pieceType) {
      case PAWN -> PawnLegalMoves.calculatePawnLegalMoves(staticPosition, enPassantCaptureTargetSquare, sideToMove,
          fromSquare);
      case ROOK -> RookLegalMoves.calculateRookLegalMoves(staticPosition, sideToMove, fromSquare);
      case KNIGHT -> KnightLegalMoves.calculateKnightLegalMoves(staticPosition, sideToMove, fromSquare);
      case BISHOP -> BishopLegalMoves.calculateBishopLegalMoves(staticPosition, sideToMove, fromSquare);
      case QUEEN -> QueenLegalMoves.calculateQueenLegalMoves(staticPosition, sideToMove, fromSquare);
      case KING -> KingLegalMoves.calculateKingLegalMoves(staticPosition, castlingRight, sideToMove, fromSquare);
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  private static Set<LegalMove> calculateLegalMovesBottomUp(StaticPosition staticPosition, Side sideToMove,
      CastlingRight castlingRight, final Square enPassantCaptureTargetSquare) {

    final Set<LegalMove> resultSet = new TreeSet<>();
    for (final Square fromSquare : Square.REAL) {
      if (staticPosition.isOwnPiece(fromSquare, sideToMove)) {
        final Set<LegalMove> currentMovingPieceSet = calculateLegalMovesBottomUp(staticPosition,
            enPassantCaptureTargetSquare, castlingRight, sideToMove, fromSquare);
        resultSet.addAll(currentMovingPieceSet);
      }
    }
    return resultSet;
  }

  static Set<LegalMove> calculateLegalMoveSet(StaticPosition staticPosition, Side sideToMove,
      Square fromSquare, Set<Square> toSquareSet) {
    return calculateLegalMoveCalculation(staticPosition, sideToMove, fromSquare, toSquareSet).legalMoveSet();
  }

  public static LegalMoveCalculation calculateLegalMoveCalculation(StaticPosition staticPosition, Side sideToMove,
      Square fromSquare, Set<Square> toSquareSet) {

    final Piece movingPiece = staticPosition.get(fromSquare);

    final Set<LegalMove> legalMoveSet = new TreeSet<>();
    final Set<PseudoLegalMove> pseudoLegalMoveSet = new TreeSet<>();

    for (final Square toSquare : toSquareSet) {
      final MoveSpecification moveSpecification = new MoveSpecification(fromSquare, toSquare);
      final Piece capturedPiece = staticPosition.isEmpty(toSquare) ? Piece.NONE : staticPosition.get(toSquare);

      if (capturedPiece != Piece.NONE && capturedPiece.getPieceType() == PieceType.KING) {
        continue;
      }

      if (!StaticPositionUtility.calculateIsKingAttackedAfterMove(staticPosition, sideToMove, moveSpecification)) {
        // This helper services non-pawn, non-castling moves only (rook / knight / bishop / queen / king-non-castling).
        // Pawn moves go through PawnLegalMoves; castling goes through KingCastlingLegalMoves. None of those routes lead
        // here, so the kind is always NORMAL.
        final LegalMove legalMove = new LegalMove(moveSpecification, movingPiece, capturedPiece, LegalMoveKind.NORMAL);
        legalMoveSet.add(legalMove);
      } else {
        final PseudoLegalMove pseudoLegalMove = new PseudoLegalMove(moveSpecification, movingPiece, capturedPiece);
        pseudoLegalMoveSet.add(pseudoLegalMove);
      }
    }
    final KingSafetyCheck pseudoLegalKingSafety;
    if (!legalMoveSet.isEmpty() || pseudoLegalMoveSet.isEmpty()) {
      pseudoLegalKingSafety = KingSafetyCheck.SUCCESS;
    } else if (StaticPositionUtility.calculateIsCheck(staticPosition, sideToMove)) {
      pseudoLegalKingSafety = KingSafetyCheck.NON_KING_LEFT_IN_CHECK;
    } else {
      pseudoLegalKingSafety = KingSafetyCheck.NON_KING_EXPOSED_TO_CHECK;
    }
    return new LegalMoveCalculation(Nulls.copyOfSet(legalMoveSet), Nulls.copyOfSet(pseudoLegalMoveSet),
        pseudoLegalKingSafety);
  }

}
