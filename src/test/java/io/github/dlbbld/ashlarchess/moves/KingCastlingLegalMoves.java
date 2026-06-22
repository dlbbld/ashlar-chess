// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.moves;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G8;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.CastlingRight;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.board.enums.SquareUtility;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.constants.CastlingConstants;
import io.github.dlbbld.ashlarchess.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.moves.CastlingCheck;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.squares.AttackedSquaresOracle;

/**
 * Test-side StaticPosition legal-move generator for castling. Re-implements the castling check on the mailbox surface
 * ({@code StaticPosition.get(Square)} + {@link AttackedSquaresOracle}) end-to-end - it is the differential-test oracle
 * for the bitboard castling pipeline ({@code BitboardLegalMoveFactory} inlines its own castling generation against
 * {@code CastlingUtility}'s bitboard methods), so this class must not delegate to that pipeline. Both sides agreeing on
 * every fixture is the spine assertion for castling.
 */
class KingCastlingLegalMoves extends KingLegalMoves {

  // Required-empty corridor and king-travel/king-destination squares - duplicated test-side so the StaticPosition
  // overload does not borrow them from the production CastlingUtility (which would weaken the oracle).

  private static final List<Square> WHITE_QUEEN_SIDE_CASTLING_REQUIRED_EMPTY_SQUARES = Nulls.listOf(B1, C1,
      D1);
  private static final List<Square> WHITE_KING_SIDE_CASTLING_REQUIRED_EMPTY_SQUARES = Nulls.listOf(F1, G1);
  private static final List<Square> BLACK_QUEEN_SIDE_CASTLING_REQUIRED_EMPTY_SQUARES = Nulls.listOf(B8, C8,
      D8);
  private static final List<Square> BLACK_KING_SIDE_CASTLING_REQUIRED_EMPTY_SQUARES = Nulls.listOf(F8, G8);

  private static final Square WHITE_QUEEN_SIDE_TRAVEL_OVER_SQUARE = D1;
  private static final Square BLACK_QUEEN_SIDE_TRAVEL_OVER_SQUARE = D8;
  private static final Square WHITE_KING_SIDE_TRAVEL_OVER_SQUARE = F1;
  private static final Square BLACK_KING_SIDE_TRAVEL_OVER_SQUARE = F8;

  public static Set<LegalMove> calculateKingCastlingLegalMoves(StaticPosition staticPosition, Side sideToMove,
      CastlingRight castlingRight) {

    final Set<LegalMove> legalMoveSet = new TreeSet<>();

    switch (sideToMove) {
      case BLACK:
        if (calculateQueenSideCastlingCheck(staticPosition, sideToMove, castlingRight) == CastlingCheck.SUCCESS) {
          legalMoveSet.add(CastlingConstants.BLACK_QUEEN_SIDE_CASTLING_MOVE);
        }
        if (calculateKingSideCastlingCheck(staticPosition, sideToMove, castlingRight) == CastlingCheck.SUCCESS) {
          legalMoveSet.add(CastlingConstants.BLACK_KING_SIDE_CASTLING_MOVE);
        }
        break;
      case WHITE:
        if (calculateQueenSideCastlingCheck(staticPosition, sideToMove, castlingRight) == CastlingCheck.SUCCESS) {
          legalMoveSet.add(CastlingConstants.WHITE_QUEEN_SIDE_CASTLING_MOVE);
        }
        if (calculateKingSideCastlingCheck(staticPosition, sideToMove, castlingRight) == CastlingCheck.SUCCESS) {
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

  private static CastlingCheck calculateQueenSideCastlingCheck(StaticPosition staticPosition, Side sideToMove,
      CastlingRight castlingRight) {
    final boolean hasLostCastlingRight = castlingRight != CastlingRight.KING_AND_QUEEN_SIDE
        && castlingRight != CastlingRight.QUEEN_SIDE;
    if (hasLostCastlingRight) {
      return CastlingCheck.FINAL_NO_RIGHT;
    }
    if (!calculateQueenSideCastlingIsOriginalPosition(staticPosition, sideToMove)) {
      throw new ProgrammingMistakeException(
          "Castling right held but king or rook not on required square (inconsistent board state).");
    }
    if (!calculateIsAllEmpty(staticPosition, calculateQueenSideCastlingRequiredEmptySquares(sideToMove))) {
      return CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY;
    }
    return calculateQueenSideCheckCondition(staticPosition, sideToMove);
  }

  private static CastlingCheck calculateKingSideCastlingCheck(StaticPosition staticPosition, Side sideToMove,
      CastlingRight castlingRight) {
    final boolean hasLostCastlingRight = castlingRight != CastlingRight.KING_AND_QUEEN_SIDE
        && castlingRight != CastlingRight.KING_SIDE;
    if (hasLostCastlingRight) {
      return CastlingCheck.FINAL_NO_RIGHT;
    }
    if (!calculateKingSideCastlingIsOriginalPosition(staticPosition, sideToMove)) {
      throw new ProgrammingMistakeException(
          "Castling right held but king or rook not on required square (inconsistent board state).");
    }
    if (!calculateIsAllEmpty(staticPosition, calculateKingSideCastlingRequiredEmptySquares(sideToMove))) {
      return CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY;
    }
    return calculateKingSideCheckCondition(staticPosition, sideToMove);
  }

  private static boolean calculateQueenSideCastlingIsOriginalPosition(StaticPosition staticPosition, Side sideToMove) {
    final Square kingOriginalSquare = SquareUtility.calculateKingOriginalSquare(sideToMove);
    final Piece kingPiece = Piece.of(sideToMove, PieceType.KING);
    if (staticPosition.get(kingOriginalSquare) != kingPiece) {
      return false;
    }
    final Square rookOriginalSquare = SquareUtility.calculateQueenSideRookOriginalSquare(sideToMove);
    final Piece rookPiece = Piece.of(sideToMove, PieceType.ROOK);
    return staticPosition.get(rookOriginalSquare) == rookPiece;
  }

  private static boolean calculateKingSideCastlingIsOriginalPosition(StaticPosition staticPosition, Side sideToMove) {
    final Square kingOriginalSquare = SquareUtility.calculateKingOriginalSquare(sideToMove);
    final Piece kingPiece = Piece.of(sideToMove, PieceType.KING);
    if (staticPosition.get(kingOriginalSquare) != kingPiece) {
      return false;
    }
    final Square rookOriginalSquare = SquareUtility.calculateKingSideRookOriginalSquare(sideToMove);
    final Piece rookPiece = Piece.of(sideToMove, PieceType.ROOK);
    return staticPosition.get(rookOriginalSquare) == rookPiece;
  }

  private static CastlingCheck calculateQueenSideCheckCondition(StaticPosition staticPosition, Side sideToMove) {
    final Side oppositeSide = sideToMove.getOppositeSide();
    final Set<Square> attackedSquares = AttackedSquaresOracle.calculateAttackedSquares(staticPosition, oppositeSide);
    if (attackedSquares.contains(SquareUtility.calculateKingOriginalSquare(sideToMove))) {
      return CastlingCheck.TEMPORARY_KING_IN_CHECK;
    }
    if (attackedSquares.contains(calculateQueenSideKingTravelOverSquare(sideToMove))) {
      return CastlingCheck.TEMPORARY_KING_TRAVELS_THROUGH_CHECK;
    }
    if (attackedSquares.contains(calculateQueenSideKingDestinationSquare(sideToMove))) {
      return CastlingCheck.TEMPORARY_KING_ENDS_IN_CHECK;
    }
    return CastlingCheck.SUCCESS;
  }

  private static CastlingCheck calculateKingSideCheckCondition(StaticPosition staticPosition, Side sideToMove) {
    final Side oppositeSide = sideToMove.getOppositeSide();
    final Set<Square> attackedSquares = AttackedSquaresOracle.calculateAttackedSquares(staticPosition, oppositeSide);
    if (attackedSquares.contains(SquareUtility.calculateKingOriginalSquare(sideToMove))) {
      return CastlingCheck.TEMPORARY_KING_IN_CHECK;
    }
    if (attackedSquares.contains(calculateKingSideKingTravelOverSquare(sideToMove))) {
      return CastlingCheck.TEMPORARY_KING_TRAVELS_THROUGH_CHECK;
    }
    if (attackedSquares.contains(calculateKingSideKingDestinationSquare(sideToMove))) {
      return CastlingCheck.TEMPORARY_KING_ENDS_IN_CHECK;
    }
    return CastlingCheck.SUCCESS;
  }

  private static boolean calculateIsAllEmpty(StaticPosition staticPosition, List<Square> squares) {
    for (final Square square : squares) {
      if (staticPosition.get(square) != Piece.NONE) {
        return false;
      }
    }
    return true;
  }

  private static List<Square> calculateQueenSideCastlingRequiredEmptySquares(Side sideToMove) {
    return switch (sideToMove) {
      case BLACK -> BLACK_QUEEN_SIDE_CASTLING_REQUIRED_EMPTY_SQUARES;
      case WHITE -> WHITE_QUEEN_SIDE_CASTLING_REQUIRED_EMPTY_SQUARES;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  private static List<Square> calculateKingSideCastlingRequiredEmptySquares(Side sideToMove) {
    return switch (sideToMove) {
      case BLACK -> BLACK_KING_SIDE_CASTLING_REQUIRED_EMPTY_SQUARES;
      case WHITE -> WHITE_KING_SIDE_CASTLING_REQUIRED_EMPTY_SQUARES;
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
}
