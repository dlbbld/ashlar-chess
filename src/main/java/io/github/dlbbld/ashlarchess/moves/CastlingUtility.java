// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.moves;

import static io.github.dlbbld.ashlarchess.common.utility.ImmutableUtility.constructListSquare;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.enums.CastlingMove;
import io.github.dlbbld.ashlarchess.board.enums.CastlingRight;
import io.github.dlbbld.ashlarchess.board.enums.CastlingRightLoss;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.board.model.UpdateSquare;
import io.github.dlbbld.ashlarchess.common.constants.CastlingConstants;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.enums.CastlingCheck;
import io.github.dlbbld.ashlarchess.fen.model.Fen;
import io.github.dlbbld.ashlarchess.model.CastlingRightBoth;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.model.LegalMoveKind;

public abstract class CastlingUtility implements EnumConstants {

  private static final ImmutableList<Square> WHITE_QUEEN_SIDE_CASTLING_REQUIRED_EMPTY_SQUARE_LIST = constructListSquare(
      B1, C1, D1);

  private static final ImmutableList<Square> WHITE_KING_SIDE_CASTLING_REQUIRED_EMPTY_SQUARE_LIST = constructListSquare(
      F1, G1);

  private static final ImmutableList<Square> BLACK_QUEEN_SIDE_CASTLING_REQUIRED_EMPTY_SQUARE_LIST = constructListSquare(
      B8, C8, D8);

  private static final ImmutableList<Square> BLACK_KING_SIDE_CASTLING_REQUIRED_EMPTY_SQUARE_LIST = constructListSquare(
      F8, G8);

  private static List<Square> calculateQueenSideCastlingRequiredEmptySquareList(Side havingMove) {
    return switch (havingMove) {
      case BLACK -> BLACK_QUEEN_SIDE_CASTLING_REQUIRED_EMPTY_SQUARE_LIST;
      case WHITE -> WHITE_QUEEN_SIDE_CASTLING_REQUIRED_EMPTY_SQUARE_LIST;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  private static List<Square> calculateKingSideCastlingRequiredEmptySquareList(Side havingMove) {
    return switch (havingMove) {
      case BLACK -> BLACK_KING_SIDE_CASTLING_REQUIRED_EMPTY_SQUARE_LIST;
      case WHITE -> WHITE_KING_SIDE_CASTLING_REQUIRED_EMPTY_SQUARE_LIST;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  private static final Square WHITE_QUEEN_SIDE_TRAVEL_OVER_SQUARE = D1;
  private static final Square BLACK_QUEEN_SIDE_TRAVEL_OVER_SQUARE = D8;

  private static final Square WHITE_KING_SIDE_TRAVEL_OVER_SQUARE = F1;
  private static final Square BLACK_KING_SIDE_TRAVEL_OVER_SQUARE = F8;

  private static Square calculateKingOriginalSquare(Side havingMove) {
    return switch (havingMove) {
      case BLACK -> CastlingConstants.BLACK_KING_FROM;
      case WHITE -> CastlingConstants.WHITE_KING_FROM;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  private static Square calculateQueenSideKingTravelOverSquare(Side havingMove) {
    return switch (havingMove) {
      case BLACK -> BLACK_QUEEN_SIDE_TRAVEL_OVER_SQUARE;
      case WHITE -> WHITE_QUEEN_SIDE_TRAVEL_OVER_SQUARE;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  private static Square calculateKingSideKingTravelOverSquare(Side havingMove) {
    return switch (havingMove) {
      case BLACK -> BLACK_KING_SIDE_TRAVEL_OVER_SQUARE;
      case WHITE -> WHITE_KING_SIDE_TRAVEL_OVER_SQUARE;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  private static Square calculateQueenSideKingDestinationSquare(Side havingMove) {
    return switch (havingMove) {
      case BLACK -> CastlingConstants.BLACK_KING_QUEEN_SIDE_CASTLING_TO;
      case WHITE -> CastlingConstants.WHITE_KING_QUEEN_SIDE_CASTLING_TO;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  private static Square calculateKingSideKingDestinationSquare(Side havingMove) {
    return switch (havingMove) {
      case BLACK -> CastlingConstants.BLACK_KING_KING_SIDE_CASTLING_TO;
      case WHITE -> CastlingConstants.WHITE_KING_KING_SIDE_CASTLING_TO;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static List<UpdateSquare> performCastlingMovements(Side havingMove, MoveSpecification moveSpecification) {
    final List<UpdateSquare> result = new ArrayList<>(performKingMovement(havingMove, moveSpecification));
    result.addAll(performRookMovement(havingMove, moveSpecification));
    return result;
  }

  private static List<UpdateSquare> performKingMovement(Side havingMove, MoveSpecification moveSpecification) {
    final List<UpdateSquare> result = new ArrayList<>();
    switch (moveSpecification.castlingMove()) {
      case KING_SIDE:

        switch (havingMove) {
          case BLACK -> {
            // king move
            result.add(new UpdateSquare(CastlingConstants.BLACK_KING_FROM));
            result.add(new UpdateSquare(CastlingConstants.BLACK_KING_KING_SIDE_CASTLING_TO, BLACK_KING));
          }
          case WHITE -> {
            // king move
            result.add(new UpdateSquare(CastlingConstants.WHITE_KING_FROM));
            result.add(new UpdateSquare(CastlingConstants.WHITE_KING_KING_SIDE_CASTLING_TO, WHITE_KING));
          }
          case NONE -> throw new IllegalArgumentException();
          default -> throw new IllegalArgumentException();
        }
        break;
      case QUEEN_SIDE:

        switch (havingMove) {
          case BLACK -> {
            // king move
            result.add(new UpdateSquare(CastlingConstants.BLACK_KING_FROM));
            result.add(new UpdateSquare(CastlingConstants.BLACK_KING_QUEEN_SIDE_CASTLING_TO, BLACK_KING));
          }
          case WHITE -> {
            // king move
            result.add(new UpdateSquare(CastlingConstants.WHITE_KING_FROM));
            result.add(new UpdateSquare(CastlingConstants.WHITE_KING_QUEEN_SIDE_CASTLING_TO, WHITE_KING));
          }
          case NONE -> throw new IllegalArgumentException();
          default -> throw new IllegalArgumentException();
        }
        break;
      case NONE:
      default:
        throw new IllegalArgumentException();
    }
    return result;
  }

  private static List<UpdateSquare> performRookMovement(Side havingMove, MoveSpecification moveSpecification) {
    final List<UpdateSquare> result = new ArrayList<>();
    switch (moveSpecification.castlingMove()) {
      case KING_SIDE:

        switch (havingMove) {
          case BLACK -> {
            // rook move
            result.add(new UpdateSquare(CastlingConstants.BLACK_ROOK_KING_SIDE_CASTLING_FROM));
            result.add(new UpdateSquare(CastlingConstants.BLACK_ROOK_KING_SIDE_CASTLING_TO, BLACK_ROOK));
          }
          case WHITE -> {
            // rook move
            result.add(new UpdateSquare(CastlingConstants.WHITE_ROOK_KING_SIDE_CASTLING_FROM));
            result.add(new UpdateSquare(CastlingConstants.WHITE_ROOK_KING_SIDE_CASTLING_TO, WHITE_ROOK));
          }
          case NONE -> throw new IllegalArgumentException();
          default -> throw new IllegalArgumentException();
        }
        break;
      case QUEEN_SIDE:

        switch (havingMove) {
          case BLACK -> {
            // rook move
            result.add(new UpdateSquare(CastlingConstants.BLACK_ROOK_QUEEN_SIDE_CASTLING_FROM));
            result.add(new UpdateSquare(CastlingConstants.BLACK_ROOK_QUEEN_SIDE_CASTLING_TO, BLACK_ROOK));
          }
          case WHITE -> {
            // rook move
            result.add(new UpdateSquare(CastlingConstants.WHITE_ROOK_QUEEN_SIDE_CASTLING_FROM));
            result.add(new UpdateSquare(CastlingConstants.WHITE_ROOK_QUEEN_SIDE_CASTLING_TO, WHITE_ROOK));
          }
          case NONE -> throw new IllegalArgumentException();
          default -> throw new IllegalArgumentException();
        }
        break;
      case NONE:
      default:
        throw new IllegalArgumentException();
    }
    return result;
  }

  private static boolean calculateIsCastlingQueenSide(MoveSpecification moveSpecification) {
    return switch (moveSpecification.castlingMove()) {
      case NONE -> false;
      case KING_SIDE -> false;
      case QUEEN_SIDE -> true;
      default -> throw new IllegalArgumentException();
    };
  }

  private static boolean calculateIsCastlingKingSide(MoveSpecification moveSpecification) {
    return switch (moveSpecification.castlingMove()) {
      case NONE -> false;
      case KING_SIDE -> true;
      case QUEEN_SIDE -> false;
      default -> throw new IllegalArgumentException();
    };
  }

  public static boolean calculateIsCastlingMove(MoveSpecification moveSpecification) {
    return calculateIsCastlingQueenSide(moveSpecification) || calculateIsCastlingKingSide(moveSpecification);
  }

  public static CastlingRightBoth calculateCastlingRightBoth(CastlingRight lastCastlingRightWhite,
      CastlingRight lastCastlingRightBlack, LegalMove legalMove) {

    final Side havingMoveBefore = legalMove.havingMove();
    final Side havingMove = havingMoveBefore.getOppositeSide();

    final CastlingRight oldCastlingRightHavingMoveBefore = CastlingUtility.getCastlingRight(lastCastlingRightWhite,
        lastCastlingRightBlack, havingMoveBefore);
    final CastlingRight oldCastlingRightHavingMove = CastlingUtility.getCastlingRight(lastCastlingRightWhite,
        lastCastlingRightBlack, havingMove);

    final CastlingRight newCastlingRightHavingMoveBefore;
    final CastlingRight newCastlingRightHavingMove;

    // as always, the castling needs a separate treatment
    if (legalMove.kind() == LegalMoveKind.CASTLING) {
      newCastlingRightHavingMoveBefore = CastlingRight.NONE;
      newCastlingRightHavingMove = oldCastlingRightHavingMove;
    } else {
      switch (oldCastlingRightHavingMoveBefore) {
        case KING_AND_QUEEN_SIDE:
          if (calculateHasKingMoved(legalMove)) {
            newCastlingRightHavingMoveBefore = CastlingRight.NONE;
          } else if (calculateHasKingSideRookMoved(legalMove)) {
            newCastlingRightHavingMoveBefore = CastlingRight.QUEEN_SIDE;
          } else if (calculateHasQueenSideRookMoved(legalMove)) {
            newCastlingRightHavingMoveBefore = CastlingRight.KING_SIDE;
          } else {
            newCastlingRightHavingMoveBefore = CastlingRight.KING_AND_QUEEN_SIDE;
          }
          break;
        case KING_SIDE:
          if (calculateHasKingMoved(legalMove) || calculateHasKingSideRookMoved(legalMove)) {
            newCastlingRightHavingMoveBefore = CastlingRight.NONE;
          } else {
            newCastlingRightHavingMoveBefore = CastlingRight.KING_SIDE;
          }
          break;
        case QUEEN_SIDE:
          if (calculateHasKingMoved(legalMove) || calculateHasQueenSideRookMoved(legalMove)) {
            newCastlingRightHavingMoveBefore = CastlingRight.NONE;
          } else {
            newCastlingRightHavingMoveBefore = CastlingRight.QUEEN_SIDE;
          }
          break;
        case NONE:
          newCastlingRightHavingMoveBefore = CastlingRight.NONE;
          break;
        default:
          throw new IllegalArgumentException();
      }

      switch (oldCastlingRightHavingMove) {
        case KING_AND_QUEEN_SIDE:
          if (calculateHasCapturedOpponentRookKingSide(legalMove)) {
            newCastlingRightHavingMove = CastlingRight.QUEEN_SIDE;
          } else if (calculateHasCapturedOpponentRookQueenSide(legalMove)) {
            newCastlingRightHavingMove = CastlingRight.KING_SIDE;
          } else {
            newCastlingRightHavingMove = CastlingRight.KING_AND_QUEEN_SIDE;
          }
          break;
        case KING_SIDE:
          if (calculateHasCapturedOpponentRookKingSide(legalMove)) {
            newCastlingRightHavingMove = CastlingRight.NONE;
          } else {
            newCastlingRightHavingMove = CastlingRight.KING_SIDE;
          }
          break;
        case QUEEN_SIDE:
          if (calculateHasCapturedOpponentRookQueenSide(legalMove)) {
            newCastlingRightHavingMove = CastlingRight.NONE;
          } else {
            newCastlingRightHavingMove = CastlingRight.QUEEN_SIDE;
          }
          break;
        case NONE:
          newCastlingRightHavingMove = CastlingRight.NONE;
          break;
        default:
          throw new IllegalArgumentException();
      }
    }

    return lookupStaticCastlingRightBoth(havingMove, newCastlingRightHavingMoveBefore, newCastlingRightHavingMove);
  }

  public static CastlingRightLoss calculateCastlingRightLoss(LegalMove legalMove, CastlingRightLoss previousLoss,
      Side side, CastlingMove castlingSide) {
    if (previousLoss != CastlingRightLoss.NOT_LOST) {
      return previousLoss;
    }
    final Side havingMoveBefore = legalMove.havingMove();
    if (legalMove.kind() == LegalMoveKind.CASTLING && havingMoveBefore == side) {
      return CastlingRightLoss.CASTLED;
    }
    if (havingMoveBefore == side) {
      if (calculateHasKingMoved(legalMove)) {
        return CastlingRightLoss.KING_MOVED;
      }
      if (castlingSide == CastlingMove.KING_SIDE && calculateHasKingSideRookMoved(legalMove)
          || castlingSide == CastlingMove.QUEEN_SIDE && calculateHasQueenSideRookMoved(legalMove)) {
        return CastlingRightLoss.ROOK_MOVED;
      }
    } else if (castlingSide == CastlingMove.KING_SIDE && calculateHasCapturedOpponentRookKingSide(legalMove)
        || castlingSide == CastlingMove.QUEEN_SIDE && calculateHasCapturedOpponentRookQueenSide(legalMove)) {
      return CastlingRightLoss.ROOK_CAPTURED;
    }
    return CastlingRightLoss.NOT_LOST;
  }

  private static boolean calculateHasKingMoved(LegalMove legalMove) {
    return legalMove.movingPiece().getPieceType() == KING;
  }

  private static boolean calculateHasQueenSideRookMoved(LegalMove legalMove) {
    if (legalMove.movingPiece().getPieceType() != ROOK) {
      return false;
    }
    final Square rookOriginalSquare = switch (legalMove.havingMove()) {
      case BLACK -> CastlingConstants.BLACK_ROOK_QUEEN_SIDE_CASTLING_FROM;
      case WHITE -> CastlingConstants.WHITE_ROOK_QUEEN_SIDE_CASTLING_FROM;
      case NONE -> throw new IllegalArgumentException();
    };
    return legalMove.moveSpecification().fromSquare() == rookOriginalSquare;
  }

  private static boolean calculateHasKingSideRookMoved(LegalMove legalMove) {
    if (legalMove.movingPiece().getPieceType() != ROOK) {
      return false;
    }
    final Square rookOriginalSquare = switch (legalMove.havingMove()) {
      case BLACK -> CastlingConstants.BLACK_ROOK_KING_SIDE_CASTLING_FROM;
      case WHITE -> CastlingConstants.WHITE_ROOK_KING_SIDE_CASTLING_FROM;
      case NONE -> throw new IllegalArgumentException();
    };
    return legalMove.moveSpecification().fromSquare() == rookOriginalSquare;
  }

  private static boolean calculateHasCapturedOpponentRookQueenSide(LegalMove legalMove) {
    if (legalMove.pieceCaptured() != Piece.NONE && legalMove.pieceCaptured().getPieceType() == ROOK) {
      final Square rookOpponentOriginalSquare = switch (legalMove.havingMove()) {
        case BLACK -> CastlingConstants.WHITE_ROOK_QUEEN_SIDE_CASTLING_FROM;
        case WHITE -> CastlingConstants.BLACK_ROOK_QUEEN_SIDE_CASTLING_FROM;
        case NONE -> throw new IllegalArgumentException();
      };
      return legalMove.moveSpecification().toSquare() == rookOpponentOriginalSquare;
    }
    return false;
  }

  private static boolean calculateHasCapturedOpponentRookKingSide(LegalMove legalMove) {
    if (legalMove.pieceCaptured() != Piece.NONE && legalMove.pieceCaptured().getPieceType() == ROOK) {
      final Square rookOpponentOriginalSquare = switch (legalMove.havingMove()) {
        case BLACK -> CastlingConstants.WHITE_ROOK_KING_SIDE_CASTLING_FROM;
        case WHITE -> CastlingConstants.BLACK_ROOK_KING_SIDE_CASTLING_FROM;
        case NONE -> throw new IllegalArgumentException();
      };
      return legalMove.moveSpecification().toSquare() == rookOpponentOriginalSquare;
    }
    return false;
  }

  private static CastlingRightBoth lookupStaticCastlingRightBoth(Side havingMove,
      CastlingRight newCastlingRightHavingMoveBefore, CastlingRight newCastlingRightHavingMove) {
    return switch (havingMove) {
      case BLACK -> new CastlingRightBoth(newCastlingRightHavingMoveBefore, newCastlingRightHavingMove);
      case WHITE -> new CastlingRightBoth(newCastlingRightHavingMove, newCastlingRightHavingMoveBefore);
      case NONE -> throw new IllegalArgumentException();
    };
  }

  // CastlingUtility's castling-check methods are bitboard-only. After Phase 6 of the role-inversion release the
  // StaticPosition variants moved out entirely: production callers (Board.move() and
  // BitboardLegalMoveFactory.calculateLegalMoves) already used the bitboard overloads after 10.0.0 Step 4, and the
  // test-side StaticPosition castling check now lives in KingCastlingLegalMoves (re-implemented end-to-end on the
  // mailbox surface as an independent oracle - it does not call back into this class).

  public static CastlingCheck calculateQueenSideCastlingCheck(BitboardPosition bitboardPosition, Side havingMove,
      CastlingRight castlingRight) {

    final boolean hasLostCastlingRight = castlingRight != CastlingRight.KING_AND_QUEEN_SIDE
        && castlingRight != CastlingRight.QUEEN_SIDE;
    if (hasLostCastlingRight) {
      return CastlingCheck.FINAL_NO_RIGHT;
    }

    final boolean isOriginalPosition = calculateQueenSideCastlingIsOriginalPosition(bitboardPosition, havingMove);
    if (!isOriginalPosition) {
      throw new ProgrammingMistakeException(
          "Castling right held but king or rook not on required square (inconsistent board state).");
    }

    final boolean isEmptySquaresBetweenRookAndKing = calculateQueenSideCastlingIsEmptySquaresBetweenRookAndKing(
        bitboardPosition, havingMove);
    if (!isEmptySquaresBetweenRookAndKing) {
      return CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY;
    }

    return calculateQueenSideCheckCondition(bitboardPosition, havingMove);
  }

  public static CastlingCheck calculateKingSideCastlingCheck(BitboardPosition bitboardPosition, Side havingMove,
      CastlingRight castlingRight) {

    final boolean hasLostCastlingRight = castlingRight != CastlingRight.KING_AND_QUEEN_SIDE
        && castlingRight != CastlingRight.KING_SIDE;
    if (hasLostCastlingRight) {
      return CastlingCheck.FINAL_NO_RIGHT;
    }

    final boolean isOriginalPosition = calculateKingSideCastlingIsOriginalPosition(bitboardPosition, havingMove);
    if (!isOriginalPosition) {
      throw new ProgrammingMistakeException(
          "Castling right held but king or rook not on required square (inconsistent board state).");
    }

    final boolean isEmptySquaresBetweenRookAndKing = calculateKingSideCastlingIsEmptySquaresBetweenRookAndKing(
        bitboardPosition, havingMove);
    if (!isEmptySquaresBetweenRookAndKing) {
      return CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY;
    }

    return calculateKingSideCheckCondition(bitboardPosition, havingMove);
  }

  public static boolean calculateQueenSideCastlingIsOriginalPosition(BitboardPosition bitboardPosition,
      Side havingMove) {
    final Square kingOriginalSquare = Square.calculateKingOriginalSquare(havingMove);
    final Piece kingPiece = Piece.calculateKingPiece(havingMove);
    if (bitboardPosition.get(kingOriginalSquare) != kingPiece) {
      return false;
    }
    final Square rookOriginalSquare = Square.calculateQueenSideRookOriginalSquare(havingMove);
    final Piece rookPiece = Piece.calculateRookPiece(havingMove);
    return bitboardPosition.get(rookOriginalSquare) == rookPiece;
  }

  public static boolean calculateKingSideCastlingIsOriginalPosition(BitboardPosition bitboardPosition,
      Side havingMove) {
    final Square kingOriginalSquare = Square.calculateKingOriginalSquare(havingMove);
    final Piece kingPiece = Piece.calculateKingPiece(havingMove);
    if (bitboardPosition.get(kingOriginalSquare) != kingPiece) {
      return false;
    }
    final Square rookOriginalSquare = Square.calculateKingSideRookOriginalSquare(havingMove);
    final Piece rookPiece = Piece.calculateRookPiece(havingMove);
    return bitboardPosition.get(rookOriginalSquare) == rookPiece;
  }

  private static boolean calculateQueenSideCastlingIsEmptySquaresBetweenRookAndKing(BitboardPosition bitboardPosition,
      Side havingMove) {
    return calculateIsAllEmpty(bitboardPosition, calculateQueenSideCastlingRequiredEmptySquareList(havingMove));
  }

  private static boolean calculateKingSideCastlingIsEmptySquaresBetweenRookAndKing(BitboardPosition bitboardPosition,
      Side havingMove) {
    return calculateIsAllEmpty(bitboardPosition, calculateKingSideCastlingRequiredEmptySquareList(havingMove));
  }

  private static boolean calculateIsAllEmpty(BitboardPosition bitboardPosition, List<Square> squareList) {
    for (final Square square : squareList) {
      if (!bitboardPosition.isEmpty(square)) {
        return false;
      }
    }
    return true;
  }

  private static CastlingCheck calculateQueenSideCheckCondition(BitboardPosition bitboardPosition, Side havingMove) {
    final long attackedSquares = bitboardPosition.attackedSquares(havingMove.getOppositeSide());

    if ((attackedSquares & (1L << calculateKingOriginalSquare(havingMove).ordinal())) != 0L) {
      return CastlingCheck.TEMPORARY_KING_IN_CHECK;
    }
    if ((attackedSquares & (1L << calculateQueenSideKingTravelOverSquare(havingMove).ordinal())) != 0L) {
      return CastlingCheck.TEMPORARY_KING_TRAVELS_THROUGH_CHECK;
    }
    if ((attackedSquares & (1L << calculateQueenSideKingDestinationSquare(havingMove).ordinal())) != 0L) {
      return CastlingCheck.TEMPORARY_KING_ENDS_IN_CHECK;
    }
    return CastlingCheck.SUCCESS;
  }

  private static CastlingCheck calculateKingSideCheckCondition(BitboardPosition bitboardPosition, Side havingMove) {
    final long attackedSquares = bitboardPosition.attackedSquares(havingMove.getOppositeSide());

    if ((attackedSquares & (1L << calculateKingOriginalSquare(havingMove).ordinal())) != 0L) {
      return CastlingCheck.TEMPORARY_KING_IN_CHECK;
    }
    if ((attackedSquares & (1L << calculateKingSideKingTravelOverSquare(havingMove).ordinal())) != 0L) {
      return CastlingCheck.TEMPORARY_KING_TRAVELS_THROUGH_CHECK;
    }
    if ((attackedSquares & (1L << calculateKingSideKingDestinationSquare(havingMove).ordinal())) != 0L) {
      return CastlingCheck.TEMPORARY_KING_ENDS_IN_CHECK;
    }
    return CastlingCheck.SUCCESS;
  }

  public static Square calculateKingCastlingFrom(Side havingMove, MoveSpecification moveSpecification) {
    if (!calculateIsCastlingMove(moveSpecification)) {
      throw new IllegalArgumentException();
    }
    return switch (moveSpecification.castlingMove()) {
      case KING_SIDE -> switch (havingMove) {
        case BLACK -> CastlingConstants.BLACK_KING_FROM;
        case WHITE -> CastlingConstants.WHITE_KING_FROM;
        case NONE -> throw new IllegalArgumentException();
        default -> throw new IllegalArgumentException();
      };
      case QUEEN_SIDE -> switch (havingMove) {
        case BLACK -> CastlingConstants.BLACK_KING_FROM;
        case WHITE -> CastlingConstants.WHITE_KING_FROM;
        case NONE -> throw new IllegalArgumentException();
        default -> throw new IllegalArgumentException();
      };
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static Square calculateKingCastlingTo(Side havingMove, MoveSpecification moveSpecification) {
    if (!calculateIsCastlingMove(moveSpecification)) {
      throw new IllegalArgumentException();
    }
    return switch (moveSpecification.castlingMove()) {
      case KING_SIDE -> switch (havingMove) {
        case BLACK -> CastlingConstants.BLACK_KING_KING_SIDE_CASTLING_TO;
        case WHITE -> CastlingConstants.WHITE_KING_KING_SIDE_CASTLING_TO;
        case NONE -> throw new IllegalArgumentException();
        default -> throw new IllegalArgumentException();
      };
      case QUEEN_SIDE -> switch (havingMove) {
        case BLACK -> CastlingConstants.BLACK_KING_QUEEN_SIDE_CASTLING_TO;
        case WHITE -> CastlingConstants.WHITE_KING_QUEEN_SIDE_CASTLING_TO;
        case NONE -> throw new IllegalArgumentException();
        default -> throw new IllegalArgumentException();
      };
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  private static CastlingRight getCastlingRight(CastlingRight castlingRightWhite, CastlingRight castlingRightBlack,
      Side side) {
    return switch (side) {
      case WHITE -> castlingRightWhite;
      case BLACK -> castlingRightBlack;
      case NONE -> throw new IllegalArgumentException();
    };
  }

  public static CastlingRight getCastlingRight(CastlingRightBoth bothUpdate, Side side) {
    return switch (side) {
      case WHITE -> bothUpdate.castlingRightWhite();
      case BLACK -> bothUpdate.castlingRightBlack();
      case NONE -> throw new IllegalArgumentException();
    };
  }

  public static CastlingRight getCastlingRight(Fen fen, Side side) {
    return switch (side) {
      case WHITE -> fen.castlingRightWhite();
      case BLACK -> fen.castlingRightBlack();
      case NONE -> throw new IllegalArgumentException();
    };
  }

}
