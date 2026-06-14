// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board;

import io.github.dlbbld.ashlarchess.analyze.CastlingCheckTranslator;
import io.github.dlbbld.ashlarchess.analyze.ChessRuleAnalyzer;
import io.github.dlbbld.ashlarchess.analyze.KingSafetyCheckTranslator;
import io.github.dlbbld.ashlarchess.analyze.MovementCheckTranslator;
import io.github.dlbbld.ashlarchess.board.enums.CastlingMove;
import io.github.dlbbld.ashlarchess.board.enums.CastlingRight;
import io.github.dlbbld.ashlarchess.board.enums.CastlingRightLoss;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceType;
import io.github.dlbbld.ashlarchess.board.enums.RankUtility;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.enums.CastlingCheck;
import io.github.dlbbld.ashlarchess.enums.KingSafetyCheck;
import io.github.dlbbld.ashlarchess.enums.MoveCheck;
import io.github.dlbbld.ashlarchess.enums.MovementCheck;
import io.github.dlbbld.ashlarchess.exceptions.InvalidMoveException;
import io.github.dlbbld.ashlarchess.moves.CastlingUtility;

class ValidateNewMove implements EnumConstants {

  public static MoveCheck validateNewMove(Board board, MoveSpecification moveSpecification)
      throws InvalidMoveException {

    if (CastlingUtility.calculateIsCastlingMove(moveSpecification)) {
      validateCastling(board, moveSpecification);
      return MoveCheck.SUCCESS;
    }

    validateNonCastlingBasic(board, moveSpecification);
    final Piece movingPiece = board.getBitboardPosition().get(moveSpecification.fromSquare());

    validateNonPawnPromotionPieceFlag(moveSpecification, movingPiece);

    validateMovement(board, moveSpecification);

    if (movingPiece.getPieceType() == PAWN) {
      validatePawnPromotionPieceConsistency(board, moveSpecification);
    }

    validateKingSafety(board, moveSpecification);

    return MoveCheck.SUCCESS;
  }

  private static void validateCastling(Board board, MoveSpecification moveSpecification) throws InvalidMoveException {

    if (!CastlingUtility.calculateIsCastlingMove(moveSpecification)) {
      throw new ProgrammingMistakeException("Precondition is not met");
    }

    final Side havingMove = board.getHavingMove();

    final CastlingMove castlingMove = moveSpecification.castlingMove();
    final CastlingCheck castlingCheck = switch (castlingMove) {
      case KING_SIDE -> CastlingUtility.calculateKingSideCastlingCheck(board.getBitboardPosition(), havingMove,
          board.getCastlingRight(havingMove));
      case QUEEN_SIDE -> CastlingUtility.calculateQueenSideCastlingCheck(board.getBitboardPosition(), havingMove,
          board.getCastlingRight(havingMove));
      case NONE -> throw new IllegalArgumentException();
    };
    final CastlingRightLoss castlingRightLoss = castlingCheck == CastlingCheck.FINAL_NO_RIGHT
        ? board.getCastlingRightLoss(havingMove, castlingMove)
        : CastlingRightLoss.NOT_LOST;
    switch (castlingCheck) {
      case FINAL_NO_RIGHT:
        final CastlingRight castlingRight = board.getCastlingRight(havingMove);
        if (castlingRight == CastlingRight.NONE) {
          throw new InvalidMoveException("there are no castling rights anymore on both sides",
              CastlingCheckTranslator.toMoveCheck(castlingCheck, castlingRightLoss));
        }
        throw new InvalidMoveException("there is no castling right anymore on this side",
            CastlingCheckTranslator.toMoveCheck(castlingCheck, castlingRightLoss));
      case TEMPORARY_SQUARES_NOT_EMPTY:
        throw new InvalidMoveException("not all squares between the rook and the king are empty",
            CastlingCheckTranslator.toMoveCheck(castlingCheck, castlingRightLoss));
      case TEMPORARY_KING_IN_CHECK:
        throw new InvalidMoveException("castling is not possible because the king is in check",
            CastlingCheckTranslator.toMoveCheck(castlingCheck, castlingRightLoss));
      case TEMPORARY_KING_TRAVELS_THROUGH_CHECK:
        throw new InvalidMoveException("the king would travel over a field that is in check",
            CastlingCheckTranslator.toMoveCheck(castlingCheck, castlingRightLoss));
      case TEMPORARY_KING_ENDS_IN_CHECK:
        throw new InvalidMoveException("the king would end in check",
            CastlingCheckTranslator.toMoveCheck(castlingCheck, castlingRightLoss));
      case SUCCESS:
        // valid castling
        break;
      default:
        throw new IllegalArgumentException();
    }
  }

  private static void validateNonCastlingBasic(Board board, MoveSpecification moveSpecification)
      throws InvalidMoveException {
    final Side havingMove = board.getHavingMove();
    final Square fromSquare = moveSpecification.fromSquare();
    final Piece movingPiece = board.getBitboardPosition().get(fromSquare);

    if (movingPiece == Piece.NONE) {
      throw new InvalidMoveException("the from square is empty", MoveCheck.MOVE_SPEC_FROM_SQUARE_EMPTY);
    }
    if (movingPiece.getSide() != havingMove) {
      throw new InvalidMoveException("the moving piece is not an own piece",
          MoveCheck.MOVE_SPEC_FROM_SQUARE_OCCUPIED_BY_OPPONENT);
    }
  }

  private static void validateNonPawnPromotionPieceFlag(MoveSpecification moveSpecification, Piece movingPiece)
      throws InvalidMoveException {
    if (movingPiece.getPieceType() == PAWN) {
      return;
    }
    if (moveSpecification.promotionPieceType() != PromotionPieceType.NONE) {
      throw new InvalidMoveException("the promotion piece type which was set as "
          + moveSpecification.promotionPieceType() + " can only be specified for pawn promotion moves",
          MoveCheck.MOVE_SPEC_NON_PAWN_PROMOTION_PIECE_SET);
    }
  }

  private static void validatePawnPromotionPieceConsistency(Board board, MoveSpecification moveSpecification)
      throws InvalidMoveException {
    final Side havingMove = board.getHavingMove();
    if (RankUtility.calculateIsPromotionRank(havingMove, moveSpecification.toSquare().getRank())) {
      if (moveSpecification.promotionPieceType() == PromotionPieceType.NONE) {
        throw new InvalidMoveException("this is a pawn promotion move but the promotion piece was not specified",
            MoveCheck.MOVE_SPEC_PAWN_PROMOTION_NO_PROMOTION_PIECE);
      }
    } else if (moveSpecification.promotionPieceType() != PromotionPieceType.NONE) {
      throw new InvalidMoveException("this is not a pawn promotion move but the promotion piece was specified",
          MoveCheck.MOVE_SPEC_PAWN_NON_PROMOTION_PROMOTION_PIECE);
    }
  }

  private static void validateMovement(Board board, MoveSpecification moveSpecification) throws InvalidMoveException {
    final MovementCheck movementCheck = ChessRuleAnalyzer.analyzeMovement(board.getBitboardPosition(),
        board.getHavingMove(), board.getEnPassantCaptureTargetSquare(), moveSpecification);
    if (movementCheck == MovementCheck.SUCCESS) {
      return;
    }
    throw new InvalidMoveException(movementMessage(movementCheck, board, moveSpecification),
        MovementCheckTranslator.toMoveCheck(movementCheck));
  }

  private static String movementMessage(MovementCheck check, Board board, MoveSpecification moveSpecification) {
    final Piece movingPiece = board.getBitboardPosition().get(moveSpecification.fromSquare());
    return switch (check) {
      case NOT_POSSIBLE -> movingPiece.getPieceType() == PAWN ? "pawns cannot move in this way"
          : "the " + movingPiece.getPieceType().getName() + " cannot move in this way";
      case TO_SQUARE_OCCUPIED_BY_OWN_PIECE -> "you cannot capture an own piece";
      case LONG_RANGE_PIECE_JUMPS_OVER_PIECE -> "the " + movingPiece.getPieceType().getName()
          + " cannot jump over other pieces";
      case PAWN_FORWARD_TWO_SQUARE_JUMP_OVER_SQUARE_ONLY_NOT_EMPTY -> "when moving two squares both the passing and "
          + "destination square must be empty, but the passing square is occcupied";
      case PAWN_FORWARD_TWO_SQUARE_TO_SQUARE_ONLY_NOT_EMPTY -> "when moving two squares both the passing and "
          + "destination square must be empty, but the destination square is occcupied";
      case PAWN_FORWARD_TWO_SQUARE_BOTH_SQUARE_NOT_EMPTY -> "when moving two squares both the passing and "
          + "destination square must be empty, but both squares are occcupied";
      case PAWN_FORWARD_ONE_SQUARE_TO_SQUARE_NOT_EMPTY_OWN_PIECE -> "when moving a pawn one square forwards, the destination square must be empty, but the destination "
          + "square is occupied by an own piece";
      case PAWN_FORWARD_ONE_SQUARE_TO_SQUARE_NOT_EMPTY_OPPONENT_PIECE -> "when moving a pawn one square forwards, the destination square must be empty, but the destination "
          + "square is occupied by an opponent pieces";
      case PAWN_DIAGONAL_OWN_PIECE -> "the pawn you cannot diagonally capture an own piece";
      case PAWN_EN_PASSANT_WRONG_RANK -> "the pawn cannot move diagonally to an empty field, except when en passant capture is possible, "
          + "which is not the case";
      case PAWN_EN_PASSANT_NO_IMMEDIATE_BEFORE_TWO_SQUARE_ADVANCE -> "the en passant capture requires that the pawn "
          + "move " + Square.calculateBehindSquare(board.getHavingMove(), moveSpecification.toSquare()).getName()
          + " was immediately played before, which is not the case";
      case KING_CAPTURES_GUARDED_PIECE -> "the king cannot capture this piece because it is guarded by another piece";
      case KING_MOVES_NEXT_TO_OPPONENT_KING -> "the king can not be moved next to the opponent king";
      case KING_MOVES_TO_ATTACKED_EMPTY_SQUARE -> "the king cannot move to a square that is attacked";
      case SUCCESS -> throw new ProgrammingMistakeException("SUCCESS has no message");
    };
  }

  private static void validateKingSafety(Board board, MoveSpecification moveSpecification) throws InvalidMoveException {
    final KingSafetyCheck kingSafetyCheck = ChessRuleAnalyzer.analyzeKingSafety(board.getBitboardPosition(),
        board.getHavingMove(), moveSpecification);
    if (kingSafetyCheck == KingSafetyCheck.SUCCESS) {
      return;
    }
    throw new InvalidMoveException(kingSafetyMessage(kingSafetyCheck),
        KingSafetyCheckTranslator.toMoveCheck(kingSafetyCheck));
  }

  private static String kingSafetyMessage(KingSafetyCheck check) {
    return switch (check) {
      case NON_KING_LEFT_IN_CHECK -> "it would leave the own king in check";
      case NON_KING_EXPOSED_TO_CHECK -> "it would expose the own king to check";
      case SUCCESS -> throw new ProgrammingMistakeException("SUCCESS has no message");
    };
  }

}
