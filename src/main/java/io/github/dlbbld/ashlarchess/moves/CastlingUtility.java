// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.moves;

import static io.github.dlbbld.ashlarchess.board.enums.Piece.BLACK_KING;
import static io.github.dlbbld.ashlarchess.board.enums.Piece.BLACK_ROOK;
import static io.github.dlbbld.ashlarchess.board.enums.Piece.WHITE_KING;
import static io.github.dlbbld.ashlarchess.board.enums.Piece.WHITE_ROOK;
import static io.github.dlbbld.ashlarchess.board.enums.PieceType.KING;
import static io.github.dlbbld.ashlarchess.board.enums.PieceType.ROOK;
import static io.github.dlbbld.ashlarchess.board.enums.Square.B1;
import static io.github.dlbbld.ashlarchess.board.enums.Square.B8;
import static io.github.dlbbld.ashlarchess.board.enums.Square.C1;
import static io.github.dlbbld.ashlarchess.board.enums.Square.C8;
import static io.github.dlbbld.ashlarchess.board.enums.Square.D1;
import static io.github.dlbbld.ashlarchess.board.enums.Square.D8;
import static io.github.dlbbld.ashlarchess.board.enums.Square.F1;
import static io.github.dlbbld.ashlarchess.board.enums.Square.F8;
import static io.github.dlbbld.ashlarchess.board.enums.Square.G1;
import static io.github.dlbbld.ashlarchess.board.enums.Square.G8;
import static io.github.dlbbld.ashlarchess.common.utility.ImmutableUtility.constructListSquare;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.enums.CastlingMove;
import io.github.dlbbld.ashlarchess.board.enums.CastlingRight;
import io.github.dlbbld.ashlarchess.board.enums.CastlingRightLoss;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.board.enums.SquareUtility;
import io.github.dlbbld.ashlarchess.board.model.UpdateSquare;
import io.github.dlbbld.ashlarchess.common.constants.CastlingConstants;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.enums.CastlingCheck;
import io.github.dlbbld.ashlarchess.fen.model.Fen;
import io.github.dlbbld.ashlarchess.model.CastlingRightBoth;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.model.LegalMoveKind;

public final class CastlingUtility {

  private CastlingUtility() {
  }

  private static final ImmutableList<Square> WHITE_QUEEN_SIDE_CASTLING_REQUIRED_EMPTY_SQUARE_LIST = constructListSquare(
      B1, C1, D1);

  private static final ImmutableList<Square> WHITE_KING_SIDE_CASTLING_REQUIRED_EMPTY_SQUARE_LIST = constructListSquare(
      F1, G1);

  private static final ImmutableList<Square> BLACK_QUEEN_SIDE_CASTLING_REQUIRED_EMPTY_SQUARE_LIST = constructListSquare(
      B8, C8, D8);

  private static final ImmutableList<Square> BLACK_KING_SIDE_CASTLING_REQUIRED_EMPTY_SQUARE_LIST = constructListSquare(
      F8, G8);

  private static List<Square> calculateQueenSideCastlingRequiredEmptySquareList(Side sideToMove) {
    return switch (sideToMove) {
      case BLACK -> BLACK_QUEEN_SIDE_CASTLING_REQUIRED_EMPTY_SQUARE_LIST;
      case WHITE -> WHITE_QUEEN_SIDE_CASTLING_REQUIRED_EMPTY_SQUARE_LIST;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  private static List<Square> calculateKingSideCastlingRequiredEmptySquareList(Side sideToMove) {
    return switch (sideToMove) {
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

  private static Square calculateKingOriginalSquare(Side sideToMove) {
    return switch (sideToMove) {
      case BLACK -> CastlingConstants.BLACK_KING_FROM;
      case WHITE -> CastlingConstants.WHITE_KING_FROM;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  private static Square calculateQueenSideKingTravelOverSquare(Side sideToMove) {
    return switch (sideToMove) {
      case BLACK -> BLACK_QUEEN_SIDE_TRAVEL_OVER_SQUARE;
      case WHITE -> WHITE_QUEEN_SIDE_TRAVEL_OVER_SQUARE;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  private static Square calculateKingSideKingTravelOverSquare(Side sideToMove) {
    return switch (sideToMove) {
      case BLACK -> BLACK_KING_SIDE_TRAVEL_OVER_SQUARE;
      case WHITE -> WHITE_KING_SIDE_TRAVEL_OVER_SQUARE;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  private static Square calculateQueenSideKingDestinationSquare(Side sideToMove) {
    return switch (sideToMove) {
      case BLACK -> CastlingConstants.BLACK_KING_QUEEN_SIDE_CASTLING_TO;
      case WHITE -> CastlingConstants.WHITE_KING_QUEEN_SIDE_CASTLING_TO;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  private static Square calculateKingSideKingDestinationSquare(Side sideToMove) {
    return switch (sideToMove) {
      case BLACK -> CastlingConstants.BLACK_KING_KING_SIDE_CASTLING_TO;
      case WHITE -> CastlingConstants.WHITE_KING_KING_SIDE_CASTLING_TO;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static List<UpdateSquare> performCastlingMovements(Side sideToMove, MoveSpecification moveSpecification) {
    final List<UpdateSquare> result = new ArrayList<>(performKingMovement(sideToMove, moveSpecification));
    result.addAll(performRookMovement(sideToMove, moveSpecification));
    return result;
  }

  private static List<UpdateSquare> performKingMovement(Side sideToMove, MoveSpecification moveSpecification) {
    final List<UpdateSquare> result = new ArrayList<>();
    switch (moveSpecification.castlingMove()) {
      case KING_SIDE:

        switch (sideToMove) {
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

        switch (sideToMove) {
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

  private static List<UpdateSquare> performRookMovement(Side sideToMove, MoveSpecification moveSpecification) {
    final List<UpdateSquare> result = new ArrayList<>();
    switch (moveSpecification.castlingMove()) {
      case KING_SIDE:

        switch (sideToMove) {
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

        switch (sideToMove) {
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

  private static boolean isCastlingQueenSide(MoveSpecification moveSpecification) {
    return switch (moveSpecification.castlingMove()) {
      case NONE -> false;
      case KING_SIDE -> false;
      case QUEEN_SIDE -> true;
      default -> throw new IllegalArgumentException();
    };
  }

  private static boolean isCastlingKingSide(MoveSpecification moveSpecification) {
    return switch (moveSpecification.castlingMove()) {
      case NONE -> false;
      case KING_SIDE -> true;
      case QUEEN_SIDE -> false;
      default -> throw new IllegalArgumentException();
    };
  }

  public static boolean isCastlingMove(MoveSpecification moveSpecification) {
    return isCastlingQueenSide(moveSpecification) || isCastlingKingSide(moveSpecification);
  }

  public static CastlingRightBoth calculateCastlingRightBoth(CastlingRight lastCastlingRightWhite,
      CastlingRight lastCastlingRightBlack, LegalMove legalMove) {

    final Side movingSide = legalMove.movingSide();
    final Side sideToMove = movingSide.getOppositeSide();

    final CastlingRight oldCastlingRightMovingSide = CastlingUtility.getCastlingRight(lastCastlingRightWhite,
        lastCastlingRightBlack, movingSide);
    final CastlingRight oldCastlingRightSideToMove = CastlingUtility.getCastlingRight(lastCastlingRightWhite,
        lastCastlingRightBlack, sideToMove);

    final CastlingRight newCastlingRightMovingSide;
    final CastlingRight newCastlingRightSideToMove;

    // as always, the castling needs a separate treatment
    if (legalMove.kind() == LegalMoveKind.CASTLING) {
      newCastlingRightMovingSide = CastlingRight.NONE;
      newCastlingRightSideToMove = oldCastlingRightSideToMove;
    } else {
      switch (oldCastlingRightMovingSide) {
        case KING_AND_QUEEN_SIDE:
          if (calculateHasKingMoved(legalMove)) {
            newCastlingRightMovingSide = CastlingRight.NONE;
          } else if (calculateHasKingSideRookMoved(legalMove)) {
            newCastlingRightMovingSide = CastlingRight.QUEEN_SIDE;
          } else if (calculateHasQueenSideRookMoved(legalMove)) {
            newCastlingRightMovingSide = CastlingRight.KING_SIDE;
          } else {
            newCastlingRightMovingSide = CastlingRight.KING_AND_QUEEN_SIDE;
          }
          break;
        case KING_SIDE:
          if (calculateHasKingMoved(legalMove) || calculateHasKingSideRookMoved(legalMove)) {
            newCastlingRightMovingSide = CastlingRight.NONE;
          } else {
            newCastlingRightMovingSide = CastlingRight.KING_SIDE;
          }
          break;
        case QUEEN_SIDE:
          if (calculateHasKingMoved(legalMove) || calculateHasQueenSideRookMoved(legalMove)) {
            newCastlingRightMovingSide = CastlingRight.NONE;
          } else {
            newCastlingRightMovingSide = CastlingRight.QUEEN_SIDE;
          }
          break;
        case NONE:
          newCastlingRightMovingSide = CastlingRight.NONE;
          break;
        default:
          throw new IllegalArgumentException();
      }

      switch (oldCastlingRightSideToMove) {
        case KING_AND_QUEEN_SIDE:
          if (calculateHasCapturedOpponentRookKingSide(legalMove)) {
            newCastlingRightSideToMove = CastlingRight.QUEEN_SIDE;
          } else if (calculateHasCapturedOpponentRookQueenSide(legalMove)) {
            newCastlingRightSideToMove = CastlingRight.KING_SIDE;
          } else {
            newCastlingRightSideToMove = CastlingRight.KING_AND_QUEEN_SIDE;
          }
          break;
        case KING_SIDE:
          if (calculateHasCapturedOpponentRookKingSide(legalMove)) {
            newCastlingRightSideToMove = CastlingRight.NONE;
          } else {
            newCastlingRightSideToMove = CastlingRight.KING_SIDE;
          }
          break;
        case QUEEN_SIDE:
          if (calculateHasCapturedOpponentRookQueenSide(legalMove)) {
            newCastlingRightSideToMove = CastlingRight.NONE;
          } else {
            newCastlingRightSideToMove = CastlingRight.QUEEN_SIDE;
          }
          break;
        case NONE:
          newCastlingRightSideToMove = CastlingRight.NONE;
          break;
        default:
          throw new IllegalArgumentException();
      }
    }

    return lookupStaticCastlingRightBoth(sideToMove, newCastlingRightMovingSide, newCastlingRightSideToMove);
  }

  public static CastlingRightLoss calculateCastlingRightLoss(LegalMove legalMove, CastlingRightLoss previousLoss,
      Side side, CastlingMove castlingSide) {
    if (previousLoss != CastlingRightLoss.NOT_LOST) {
      return previousLoss;
    }
    final Side movingSide = legalMove.movingSide();
    if (legalMove.kind() == LegalMoveKind.CASTLING && movingSide == side) {
      return CastlingRightLoss.CASTLED;
    }
    if (movingSide == side) {
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
    final Square rookOriginalSquare = switch (legalMove.movingSide()) {
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
    final Square rookOriginalSquare = switch (legalMove.movingSide()) {
      case BLACK -> CastlingConstants.BLACK_ROOK_KING_SIDE_CASTLING_FROM;
      case WHITE -> CastlingConstants.WHITE_ROOK_KING_SIDE_CASTLING_FROM;
      case NONE -> throw new IllegalArgumentException();
    };
    return legalMove.moveSpecification().fromSquare() == rookOriginalSquare;
  }

  private static boolean calculateHasCapturedOpponentRookQueenSide(LegalMove legalMove) {
    if (legalMove.capturedPiece() != Piece.NONE && legalMove.capturedPiece().getPieceType() == ROOK) {
      final Square rookOpponentOriginalSquare = switch (legalMove.movingSide()) {
        case BLACK -> CastlingConstants.WHITE_ROOK_QUEEN_SIDE_CASTLING_FROM;
        case WHITE -> CastlingConstants.BLACK_ROOK_QUEEN_SIDE_CASTLING_FROM;
        case NONE -> throw new IllegalArgumentException();
      };
      return legalMove.moveSpecification().toSquare() == rookOpponentOriginalSquare;
    }
    return false;
  }

  private static boolean calculateHasCapturedOpponentRookKingSide(LegalMove legalMove) {
    if (legalMove.capturedPiece() != Piece.NONE && legalMove.capturedPiece().getPieceType() == ROOK) {
      final Square rookOpponentOriginalSquare = switch (legalMove.movingSide()) {
        case BLACK -> CastlingConstants.WHITE_ROOK_KING_SIDE_CASTLING_FROM;
        case WHITE -> CastlingConstants.BLACK_ROOK_KING_SIDE_CASTLING_FROM;
        case NONE -> throw new IllegalArgumentException();
      };
      return legalMove.moveSpecification().toSquare() == rookOpponentOriginalSquare;
    }
    return false;
  }

  private static CastlingRightBoth lookupStaticCastlingRightBoth(Side sideToMove,
      CastlingRight newCastlingRightMovingSide, CastlingRight newCastlingRightSideToMove) {
    return switch (sideToMove) {
      case BLACK -> new CastlingRightBoth(newCastlingRightMovingSide, newCastlingRightSideToMove);
      case WHITE -> new CastlingRightBoth(newCastlingRightSideToMove, newCastlingRightMovingSide);
      case NONE -> throw new IllegalArgumentException();
    };
  }

  // CastlingUtility's castling-check methods are bitboard-only. After Phase 6 of the role-inversion release the
  // StaticPosition variants moved out entirely: production callers (Board.move() and
  // BitboardLegalMoveFactory.calculateLegalMoves) already used the bitboard overloads after 10.0.0 Step 4, and the
  // test-side StaticPosition castling check now lives in KingCastlingLegalMoves (re-implemented end-to-end on the
  // mailbox surface as an independent oracle - it does not call back into this class).

  public static CastlingCheck calculateQueenSideCastlingCheck(BitboardPosition bitboardPosition, Side sideToMove,
      CastlingRight castlingRight) {

    final boolean hasLostCastlingRight = castlingRight != CastlingRight.KING_AND_QUEEN_SIDE
        && castlingRight != CastlingRight.QUEEN_SIDE;
    if (hasLostCastlingRight) {
      return CastlingCheck.FINAL_NO_RIGHT;
    }

    final boolean isOriginalPosition = isQueenSideCastlingOriginalPosition(bitboardPosition, sideToMove);
    if (!isOriginalPosition) {
      throw new ProgrammingMistakeException(
          "Castling right held but king or rook not on required square (inconsistent board state).");
    }

    final boolean isEmptySquaresBetweenRookAndKing = calculateQueenSideCastlingIsEmptySquaresBetweenRookAndKing(
        bitboardPosition, sideToMove);
    if (!isEmptySquaresBetweenRookAndKing) {
      return CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY;
    }

    return calculateQueenSideCheckCondition(bitboardPosition, sideToMove);
  }

  public static CastlingCheck calculateKingSideCastlingCheck(BitboardPosition bitboardPosition, Side sideToMove,
      CastlingRight castlingRight) {

    final boolean hasLostCastlingRight = castlingRight != CastlingRight.KING_AND_QUEEN_SIDE
        && castlingRight != CastlingRight.KING_SIDE;
    if (hasLostCastlingRight) {
      return CastlingCheck.FINAL_NO_RIGHT;
    }

    final boolean isOriginalPosition = isKingSideCastlingOriginalPosition(bitboardPosition, sideToMove);
    if (!isOriginalPosition) {
      throw new ProgrammingMistakeException(
          "Castling right held but king or rook not on required square (inconsistent board state).");
    }

    final boolean isEmptySquaresBetweenRookAndKing = calculateKingSideCastlingIsEmptySquaresBetweenRookAndKing(
        bitboardPosition, sideToMove);
    if (!isEmptySquaresBetweenRookAndKing) {
      return CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY;
    }

    return calculateKingSideCheckCondition(bitboardPosition, sideToMove);
  }

  public static boolean isQueenSideCastlingOriginalPosition(BitboardPosition bitboardPosition, Side sideToMove) {
    final Square kingOriginalSquare = SquareUtility.calculateKingOriginalSquare(sideToMove);
    final Piece kingPiece = Piece.of(sideToMove, PieceType.KING);
    if (bitboardPosition.get(kingOriginalSquare) != kingPiece) {
      return false;
    }
    final Square rookOriginalSquare = SquareUtility.calculateQueenSideRookOriginalSquare(sideToMove);
    final Piece rookPiece = Piece.of(sideToMove, PieceType.ROOK);
    return bitboardPosition.get(rookOriginalSquare) == rookPiece;
  }

  public static boolean isKingSideCastlingOriginalPosition(BitboardPosition bitboardPosition, Side sideToMove) {
    final Square kingOriginalSquare = SquareUtility.calculateKingOriginalSquare(sideToMove);
    final Piece kingPiece = Piece.of(sideToMove, PieceType.KING);
    if (bitboardPosition.get(kingOriginalSquare) != kingPiece) {
      return false;
    }
    final Square rookOriginalSquare = SquareUtility.calculateKingSideRookOriginalSquare(sideToMove);
    final Piece rookPiece = Piece.of(sideToMove, PieceType.ROOK);
    return bitboardPosition.get(rookOriginalSquare) == rookPiece;
  }

  private static boolean calculateQueenSideCastlingIsEmptySquaresBetweenRookAndKing(BitboardPosition bitboardPosition,
      Side sideToMove) {
    return calculateIsAllEmpty(bitboardPosition, calculateQueenSideCastlingRequiredEmptySquareList(sideToMove));
  }

  private static boolean calculateKingSideCastlingIsEmptySquaresBetweenRookAndKing(BitboardPosition bitboardPosition,
      Side sideToMove) {
    return calculateIsAllEmpty(bitboardPosition, calculateKingSideCastlingRequiredEmptySquareList(sideToMove));
  }

  private static boolean calculateIsAllEmpty(BitboardPosition bitboardPosition, List<Square> squareList) {
    for (final Square square : squareList) {
      if (!bitboardPosition.isEmpty(square)) {
        return false;
      }
    }
    return true;
  }

  private static CastlingCheck calculateQueenSideCheckCondition(BitboardPosition bitboardPosition, Side sideToMove) {
    final long attackedSquares = bitboardPosition.attackedSquares(sideToMove.getOppositeSide());

    if ((attackedSquares & (1L << calculateKingOriginalSquare(sideToMove).ordinal())) != 0L) {
      return CastlingCheck.TEMPORARY_KING_IN_CHECK;
    }
    if ((attackedSquares & (1L << calculateQueenSideKingTravelOverSquare(sideToMove).ordinal())) != 0L) {
      return CastlingCheck.TEMPORARY_KING_TRAVELS_THROUGH_CHECK;
    }
    if ((attackedSquares & (1L << calculateQueenSideKingDestinationSquare(sideToMove).ordinal())) != 0L) {
      return CastlingCheck.TEMPORARY_KING_ENDS_IN_CHECK;
    }
    return CastlingCheck.SUCCESS;
  }

  private static CastlingCheck calculateKingSideCheckCondition(BitboardPosition bitboardPosition, Side sideToMove) {
    final long attackedSquares = bitboardPosition.attackedSquares(sideToMove.getOppositeSide());

    if ((attackedSquares & (1L << calculateKingOriginalSquare(sideToMove).ordinal())) != 0L) {
      return CastlingCheck.TEMPORARY_KING_IN_CHECK;
    }
    if ((attackedSquares & (1L << calculateKingSideKingTravelOverSquare(sideToMove).ordinal())) != 0L) {
      return CastlingCheck.TEMPORARY_KING_TRAVELS_THROUGH_CHECK;
    }
    if ((attackedSquares & (1L << calculateKingSideKingDestinationSquare(sideToMove).ordinal())) != 0L) {
      return CastlingCheck.TEMPORARY_KING_ENDS_IN_CHECK;
    }
    return CastlingCheck.SUCCESS;
  }

  public static Square calculateKingCastlingFrom(Side sideToMove, MoveSpecification moveSpecification) {
    if (!isCastlingMove(moveSpecification)) {
      throw new IllegalArgumentException();
    }
    return switch (moveSpecification.castlingMove()) {
      case KING_SIDE -> switch (sideToMove) {
        case BLACK -> CastlingConstants.BLACK_KING_FROM;
        case WHITE -> CastlingConstants.WHITE_KING_FROM;
        case NONE -> throw new IllegalArgumentException();
        default -> throw new IllegalArgumentException();
      };
      case QUEEN_SIDE -> switch (sideToMove) {
        case BLACK -> CastlingConstants.BLACK_KING_FROM;
        case WHITE -> CastlingConstants.WHITE_KING_FROM;
        case NONE -> throw new IllegalArgumentException();
        default -> throw new IllegalArgumentException();
      };
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static Square calculateKingCastlingTo(Side sideToMove, MoveSpecification moveSpecification) {
    if (!isCastlingMove(moveSpecification)) {
      throw new IllegalArgumentException();
    }
    return switch (moveSpecification.castlingMove()) {
      case KING_SIDE -> switch (sideToMove) {
        case BLACK -> CastlingConstants.BLACK_KING_KING_SIDE_CASTLING_TO;
        case WHITE -> CastlingConstants.WHITE_KING_KING_SIDE_CASTLING_TO;
        case NONE -> throw new IllegalArgumentException();
        default -> throw new IllegalArgumentException();
      };
      case QUEEN_SIDE -> switch (sideToMove) {
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
