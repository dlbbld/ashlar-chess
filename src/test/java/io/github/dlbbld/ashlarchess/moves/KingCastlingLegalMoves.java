// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.moves;

import static io.github.dlbbld.ashlarchess.common.utility.ImmutableUtility.constructListSquare;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.CastlingRight;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceUtility;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.board.enums.SquareUtility;
import io.github.dlbbld.ashlarchess.common.constants.CastlingConstants;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.enums.CastlingCheck;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.squares.AbstractAttackedSquares;

/**
 * Test-side StaticPosition legal-move generator for castling. Re-implements the castling check on the mailbox surface
 * ({@code StaticPosition.get(Square)} + {@link AbstractAttackedSquares}) end-to-end - it is the differential-test
 * oracle for the bitboard castling pipeline ({@code BitboardLegalMoveFactory} inlines its own castling generation
 * against {@code CastlingUtility}'s bitboard methods), so this class must not delegate to that pipeline. Both sides
 * agreeing on every fixture is the spine assertion for castling.
 */
class KingCastlingLegalMoves extends KingLegalMoves {

  // Required-empty corridor and king-travel/king-destination squares - duplicated test-side so the StaticPosition
  // overload does not borrow them from the production CastlingUtility (which would weaken the oracle).

  private static final ImmutableList<Square> WHITE_QUEEN_SIDE_CASTLING_REQUIRED_EMPTY_SQUARE_LIST = constructListSquare(
      B1, C1, D1);
  private static final ImmutableList<Square> WHITE_KING_SIDE_CASTLING_REQUIRED_EMPTY_SQUARE_LIST = constructListSquare(
      F1, G1);
  private static final ImmutableList<Square> BLACK_QUEEN_SIDE_CASTLING_REQUIRED_EMPTY_SQUARE_LIST = constructListSquare(
      B8, C8, D8);
  private static final ImmutableList<Square> BLACK_KING_SIDE_CASTLING_REQUIRED_EMPTY_SQUARE_LIST = constructListSquare(
      F8, G8);

  private static final Square WHITE_QUEEN_SIDE_TRAVEL_OVER_SQUARE = D1;
  private static final Square BLACK_QUEEN_SIDE_TRAVEL_OVER_SQUARE = D8;
  private static final Square WHITE_KING_SIDE_TRAVEL_OVER_SQUARE = F1;
  private static final Square BLACK_KING_SIDE_TRAVEL_OVER_SQUARE = F8;

  public static Set<LegalMove> calculateKingCastlingLegalMoves(StaticPosition staticPosition, Side havingMove,
      CastlingRight castlingRight) {

    final Set<LegalMove> legalMoveSet = new TreeSet<>();

    switch (havingMove) {
      case BLACK:
        if (calculateQueenSideCastlingCheck(staticPosition, havingMove, castlingRight) == CastlingCheck.SUCCESS) {
          legalMoveSet.add(CastlingConstants.BLACK_QUEEN_SIDE_CASTLING_MOVE);
        }
        if (calculateKingSideCastlingCheck(staticPosition, havingMove, castlingRight) == CastlingCheck.SUCCESS) {
          legalMoveSet.add(CastlingConstants.BLACK_KING_SIDE_CASTLING_MOVE);
        }
        break;
      case WHITE:
        if (calculateQueenSideCastlingCheck(staticPosition, havingMove, castlingRight) == CastlingCheck.SUCCESS) {
          legalMoveSet.add(CastlingConstants.WHITE_QUEEN_SIDE_CASTLING_MOVE);
        }
        if (calculateKingSideCastlingCheck(staticPosition, havingMove, castlingRight) == CastlingCheck.SUCCESS) {
          legalMoveSet.add(CastlingConstants.WHITE_KING_SIDE_CASTLING_MOVE);
        }
        break;
      case NONE:
      default:
        throw new IllegalArgumentException();
    }
    return legalMoveSet;
  }

  // --- StaticPosition-side castling check (test-only oracle) ---

  private static CastlingCheck calculateQueenSideCastlingCheck(StaticPosition staticPosition, Side havingMove,
      CastlingRight castlingRight) {
    final boolean hasLostCastlingRight = castlingRight != CastlingRight.KING_AND_QUEEN_SIDE
        && castlingRight != CastlingRight.QUEEN_SIDE;
    if (hasLostCastlingRight) {
      return CastlingCheck.FINAL_NO_RIGHT;
    }
    if (!calculateQueenSideCastlingIsOriginalPosition(staticPosition, havingMove)) {
      throw new ProgrammingMistakeException(
          "Castling right held but king or rook not on required square (inconsistent board state).");
    }
    if (!calculateIsAllEmpty(staticPosition, calculateQueenSideCastlingRequiredEmptySquareList(havingMove))) {
      return CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY;
    }
    return calculateQueenSideCheckCondition(staticPosition, havingMove);
  }

  private static CastlingCheck calculateKingSideCastlingCheck(StaticPosition staticPosition, Side havingMove,
      CastlingRight castlingRight) {
    final boolean hasLostCastlingRight = castlingRight != CastlingRight.KING_AND_QUEEN_SIDE
        && castlingRight != CastlingRight.KING_SIDE;
    if (hasLostCastlingRight) {
      return CastlingCheck.FINAL_NO_RIGHT;
    }
    if (!calculateKingSideCastlingIsOriginalPosition(staticPosition, havingMove)) {
      throw new ProgrammingMistakeException(
          "Castling right held but king or rook not on required square (inconsistent board state).");
    }
    if (!calculateIsAllEmpty(staticPosition, calculateKingSideCastlingRequiredEmptySquareList(havingMove))) {
      return CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY;
    }
    return calculateKingSideCheckCondition(staticPosition, havingMove);
  }

  private static boolean calculateQueenSideCastlingIsOriginalPosition(StaticPosition staticPosition, Side havingMove) {
    final Square kingOriginalSquare = SquareUtility.calculateKingOriginalSquare(havingMove);
    final Piece kingPiece = PieceUtility.calculateKingPiece(havingMove);
    if (staticPosition.get(kingOriginalSquare) != kingPiece) {
      return false;
    }
    final Square rookOriginalSquare = SquareUtility.calculateQueenSideRookOriginalSquare(havingMove);
    final Piece rookPiece = PieceUtility.calculateRookPiece(havingMove);
    return staticPosition.get(rookOriginalSquare) == rookPiece;
  }

  private static boolean calculateKingSideCastlingIsOriginalPosition(StaticPosition staticPosition, Side havingMove) {
    final Square kingOriginalSquare = SquareUtility.calculateKingOriginalSquare(havingMove);
    final Piece kingPiece = PieceUtility.calculateKingPiece(havingMove);
    if (staticPosition.get(kingOriginalSquare) != kingPiece) {
      return false;
    }
    final Square rookOriginalSquare = SquareUtility.calculateKingSideRookOriginalSquare(havingMove);
    final Piece rookPiece = PieceUtility.calculateRookPiece(havingMove);
    return staticPosition.get(rookOriginalSquare) == rookPiece;
  }

  private static CastlingCheck calculateQueenSideCheckCondition(StaticPosition staticPosition, Side havingMove) {
    final Side oppositeSide = havingMove.getOppositeSide();
    final Set<Square> attackedSquares = AbstractAttackedSquares.calculateAttackedSquares(staticPosition, oppositeSide);
    if (attackedSquares.contains(SquareUtility.calculateKingOriginalSquare(havingMove))) {
      return CastlingCheck.TEMPORARY_KING_IN_CHECK;
    }
    if (attackedSquares.contains(calculateQueenSideKingTravelOverSquare(havingMove))) {
      return CastlingCheck.TEMPORARY_KING_TRAVELS_THROUGH_CHECK;
    }
    if (attackedSquares.contains(calculateQueenSideKingDestinationSquare(havingMove))) {
      return CastlingCheck.TEMPORARY_KING_ENDS_IN_CHECK;
    }
    return CastlingCheck.SUCCESS;
  }

  private static CastlingCheck calculateKingSideCheckCondition(StaticPosition staticPosition, Side havingMove) {
    final Side oppositeSide = havingMove.getOppositeSide();
    final Set<Square> attackedSquares = AbstractAttackedSquares.calculateAttackedSquares(staticPosition, oppositeSide);
    if (attackedSquares.contains(SquareUtility.calculateKingOriginalSquare(havingMove))) {
      return CastlingCheck.TEMPORARY_KING_IN_CHECK;
    }
    if (attackedSquares.contains(calculateKingSideKingTravelOverSquare(havingMove))) {
      return CastlingCheck.TEMPORARY_KING_TRAVELS_THROUGH_CHECK;
    }
    if (attackedSquares.contains(calculateKingSideKingDestinationSquare(havingMove))) {
      return CastlingCheck.TEMPORARY_KING_ENDS_IN_CHECK;
    }
    return CastlingCheck.SUCCESS;
  }

  private static boolean calculateIsAllEmpty(StaticPosition staticPosition, List<Square> squareList) {
    for (final Square square : squareList) {
      if (staticPosition.get(square) != Piece.NONE) {
        return false;
      }
    }
    return true;
  }

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
}
