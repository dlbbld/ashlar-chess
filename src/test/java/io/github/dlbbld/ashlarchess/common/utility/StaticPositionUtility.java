// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.common.utility;

import static io.github.dlbbld.ashlarchess.common.utility.ImmutableUtility.constructListSquare;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.board.model.UpdateSquare;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.fen.FenPieceSymbolUtility;
import io.github.dlbbld.ashlarchess.moves.CastlingUtility;
import io.github.dlbbld.ashlarchess.moves.EnPassantCaptureUtility;
import io.github.dlbbld.ashlarchess.moves.PromotionUtility;
import io.github.dlbbld.ashlarchess.moves.StandardMoveUtility;
import io.github.dlbbld.ashlarchess.squares.AbstractAttackedSquares;

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
          piecePlacement.append(FenPieceSymbolUtility.calculate(pieceOnSquare).pieceLetter());
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
    return StaticPositionUtility.createChangedPosition(staticPosition, updateSquareList);
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

  public static StaticPosition createChangedPosition(StaticPosition staticPosition, Square square, Piece piece) {
    final List<UpdateSquare> updateSquareList = new ArrayList<>();
    updateSquareList.add(new UpdateSquare(square, piece));
    return createChangedPosition(staticPosition, updateSquareList);
  }

  public static StaticPosition createChangedPosition(StaticPosition staticPosition, Square square) {
    final List<UpdateSquare> updateSquareList = new ArrayList<>();
    updateSquareList.add(new UpdateSquare(square));
    return createChangedPosition(staticPosition, updateSquareList);
  }

  public static StaticPosition createChangedPosition(StaticPosition staticPosition,
      List<UpdateSquare> updateSquareList) {
    Piece newA8 = staticPosition.a8();
    Piece newB8 = staticPosition.b8();
    Piece newC8 = staticPosition.c8();
    Piece newD8 = staticPosition.d8();
    Piece newE8 = staticPosition.e8();
    Piece newF8 = staticPosition.f8();
    Piece newG8 = staticPosition.g8();
    Piece newH8 = staticPosition.h8();
    Piece newA7 = staticPosition.a7();
    Piece newB7 = staticPosition.b7();
    Piece newC7 = staticPosition.c7();
    Piece newD7 = staticPosition.d7();
    Piece newE7 = staticPosition.e7();
    Piece newF7 = staticPosition.f7();
    Piece newG7 = staticPosition.g7();
    Piece newH7 = staticPosition.h7();
    Piece newA6 = staticPosition.a6();
    Piece newB6 = staticPosition.b6();
    Piece newC6 = staticPosition.c6();
    Piece newD6 = staticPosition.d6();
    Piece newE6 = staticPosition.e6();
    Piece newF6 = staticPosition.f6();
    Piece newG6 = staticPosition.g6();
    Piece newH6 = staticPosition.h6();
    Piece newA5 = staticPosition.a5();
    Piece newB5 = staticPosition.b5();
    Piece newC5 = staticPosition.c5();
    Piece newD5 = staticPosition.d5();
    Piece newE5 = staticPosition.e5();
    Piece newF5 = staticPosition.f5();
    Piece newG5 = staticPosition.g5();
    Piece newH5 = staticPosition.h5();
    Piece newA4 = staticPosition.a4();
    Piece newB4 = staticPosition.b4();
    Piece newC4 = staticPosition.c4();
    Piece newD4 = staticPosition.d4();
    Piece newE4 = staticPosition.e4();
    Piece newF4 = staticPosition.f4();
    Piece newG4 = staticPosition.g4();
    Piece newH4 = staticPosition.h4();
    Piece newA3 = staticPosition.a3();
    Piece newB3 = staticPosition.b3();
    Piece newC3 = staticPosition.c3();
    Piece newD3 = staticPosition.d3();
    Piece newE3 = staticPosition.e3();
    Piece newF3 = staticPosition.f3();
    Piece newG3 = staticPosition.g3();
    Piece newH3 = staticPosition.h3();
    Piece newA2 = staticPosition.a2();
    Piece newB2 = staticPosition.b2();
    Piece newC2 = staticPosition.c2();
    Piece newD2 = staticPosition.d2();
    Piece newE2 = staticPosition.e2();
    Piece newF2 = staticPosition.f2();
    Piece newG2 = staticPosition.g2();
    Piece newH2 = staticPosition.h2();
    Piece newA1 = staticPosition.a1();
    Piece newB1 = staticPosition.b1();
    Piece newC1 = staticPosition.c1();
    Piece newD1 = staticPosition.d1();
    Piece newE1 = staticPosition.e1();
    Piece newF1 = staticPosition.f1();
    Piece newG1 = staticPosition.g1();
    Piece newH1 = staticPosition.h1();

    for (final UpdateSquare updateSquare : updateSquareList) {
      final Square square = updateSquare.square();
      final Piece newPiece = updateSquare.piece();
      switch (square) {
        case A8 -> {
          checkUpdateSquare(square, newA8, newPiece);
          newA8 = newPiece;
        }
        case B8 -> {
          checkUpdateSquare(square, newB8, newPiece);
          newB8 = newPiece;
        }
        case C8 -> {
          checkUpdateSquare(square, newC8, newPiece);
          newC8 = newPiece;
        }
        case D8 -> {
          checkUpdateSquare(square, newD8, newPiece);
          newD8 = newPiece;
        }
        case E8 -> {
          checkUpdateSquare(square, newE8, newPiece);
          newE8 = newPiece;
        }
        case F8 -> {
          checkUpdateSquare(square, newF8, newPiece);
          newF8 = newPiece;
        }
        case G8 -> {
          checkUpdateSquare(square, newG8, newPiece);
          newG8 = newPiece;
        }
        case H8 -> {
          checkUpdateSquare(square, newH8, newPiece);
          newH8 = newPiece;
        }
        case A7 -> {
          checkUpdateSquare(square, newA7, newPiece);
          newA7 = newPiece;
        }
        case B7 -> {
          checkUpdateSquare(square, newB7, newPiece);
          newB7 = newPiece;
        }
        case C7 -> {
          checkUpdateSquare(square, newC7, newPiece);
          newC7 = newPiece;
        }
        case D7 -> {
          checkUpdateSquare(square, newD7, newPiece);
          newD7 = newPiece;
        }
        case E7 -> {
          checkUpdateSquare(square, newE7, newPiece);
          newE7 = newPiece;
        }
        case F7 -> {
          checkUpdateSquare(square, newF7, newPiece);
          newF7 = newPiece;
        }
        case G7 -> {
          checkUpdateSquare(square, newG7, newPiece);
          newG7 = newPiece;
        }
        case H7 -> {
          checkUpdateSquare(square, newH7, newPiece);
          newH7 = newPiece;
        }
        case A6 -> {
          checkUpdateSquare(square, newA6, newPiece);
          newA6 = newPiece;
        }
        case B6 -> {
          checkUpdateSquare(square, newB6, newPiece);
          newB6 = newPiece;
        }
        case C6 -> {
          checkUpdateSquare(square, newC6, newPiece);
          newC6 = newPiece;
        }
        case D6 -> {
          checkUpdateSquare(square, newD6, newPiece);
          newD6 = newPiece;
        }
        case E6 -> {
          checkUpdateSquare(square, newE6, newPiece);
          newE6 = newPiece;
        }
        case F6 -> {
          checkUpdateSquare(square, newF6, newPiece);
          newF6 = newPiece;
        }
        case G6 -> {
          checkUpdateSquare(square, newG6, newPiece);
          newG6 = newPiece;
        }
        case H6 -> {
          checkUpdateSquare(square, newH6, newPiece);
          newH6 = newPiece;
        }
        case A5 -> {
          checkUpdateSquare(square, newA5, newPiece);
          newA5 = newPiece;
        }
        case B5 -> {
          checkUpdateSquare(square, newB5, newPiece);
          newB5 = newPiece;
        }
        case C5 -> {
          checkUpdateSquare(square, newC5, newPiece);
          newC5 = newPiece;
        }
        case D5 -> {
          checkUpdateSquare(square, newD5, newPiece);
          newD5 = newPiece;
        }
        case E5 -> {
          checkUpdateSquare(square, newE5, newPiece);
          newE5 = newPiece;
        }
        case F5 -> {
          checkUpdateSquare(square, newF5, newPiece);
          newF5 = newPiece;
        }
        case G5 -> {
          checkUpdateSquare(square, newG5, newPiece);
          newG5 = newPiece;
        }
        case H5 -> {
          checkUpdateSquare(square, newH5, newPiece);
          newH5 = newPiece;
        }
        case A4 -> {
          checkUpdateSquare(square, newA4, newPiece);
          newA4 = newPiece;
        }
        case B4 -> {
          checkUpdateSquare(square, newB4, newPiece);
          newB4 = newPiece;
        }
        case C4 -> {
          checkUpdateSquare(square, newC4, newPiece);
          newC4 = newPiece;
        }
        case D4 -> {
          checkUpdateSquare(square, newD4, newPiece);
          newD4 = newPiece;
        }
        case E4 -> {
          checkUpdateSquare(square, newE4, newPiece);
          newE4 = newPiece;
        }
        case F4 -> {
          checkUpdateSquare(square, newF4, newPiece);
          newF4 = newPiece;
        }
        case G4 -> {
          checkUpdateSquare(square, newG4, newPiece);
          newG4 = newPiece;
        }
        case H4 -> {
          checkUpdateSquare(square, newH4, newPiece);
          newH4 = newPiece;
        }
        case A3 -> {
          checkUpdateSquare(square, newA3, newPiece);
          newA3 = newPiece;
        }
        case B3 -> {
          checkUpdateSquare(square, newB3, newPiece);
          newB3 = newPiece;
        }
        case C3 -> {
          checkUpdateSquare(square, newC3, newPiece);
          newC3 = newPiece;
        }
        case D3 -> {
          checkUpdateSquare(square, newD3, newPiece);
          newD3 = newPiece;
        }
        case E3 -> {
          checkUpdateSquare(square, newE3, newPiece);
          newE3 = newPiece;
        }
        case F3 -> {
          checkUpdateSquare(square, newF3, newPiece);
          newF3 = newPiece;
        }
        case G3 -> {
          checkUpdateSquare(square, newG3, newPiece);
          newG3 = newPiece;
        }
        case H3 -> {
          checkUpdateSquare(square, newH3, newPiece);
          newH3 = newPiece;
        }
        case A2 -> {
          checkUpdateSquare(square, newA2, newPiece);
          newA2 = newPiece;
        }
        case B2 -> {
          checkUpdateSquare(square, newB2, newPiece);
          newB2 = newPiece;
        }
        case C2 -> {
          checkUpdateSquare(square, newC2, newPiece);
          newC2 = newPiece;
        }
        case D2 -> {
          checkUpdateSquare(square, newD2, newPiece);
          newD2 = newPiece;
        }
        case E2 -> {
          checkUpdateSquare(square, newE2, newPiece);
          newE2 = newPiece;
        }
        case F2 -> {
          checkUpdateSquare(square, newF2, newPiece);
          newF2 = newPiece;
        }
        case G2 -> {
          checkUpdateSquare(square, newG2, newPiece);
          newG2 = newPiece;
        }
        case H2 -> {
          checkUpdateSquare(square, newH2, newPiece);
          newH2 = newPiece;
        }
        case A1 -> {
          checkUpdateSquare(square, newA1, newPiece);
          newA1 = newPiece;
        }
        case B1 -> {
          checkUpdateSquare(square, newB1, newPiece);
          newB1 = newPiece;
        }
        case C1 -> {
          checkUpdateSquare(square, newC1, newPiece);
          newC1 = newPiece;
        }
        case D1 -> {
          checkUpdateSquare(square, newD1, newPiece);
          newD1 = newPiece;
        }
        case E1 -> {
          checkUpdateSquare(square, newE1, newPiece);
          newE1 = newPiece;
        }
        case F1 -> {
          checkUpdateSquare(square, newF1, newPiece);
          newF1 = newPiece;
        }
        case G1 -> {
          checkUpdateSquare(square, newG1, newPiece);
          newG1 = newPiece;
        }
        case H1 -> {
          checkUpdateSquare(square, newH1, newPiece);
          newH1 = newPiece;
        }
        case NONE -> throw new IllegalArgumentException("The none square does not belong to the board)");
        default -> throw new IllegalArgumentException();
      }
    }

    return new StaticPosition(newA8, newB8, newC8, newD8, newE8, newF8, newG8, newH8, newA7, newB7, newC7, newD7, newE7,
        newF7, newG7, newH7, newA6, newB6, newC6, newD6, newE6, newF6, newG6, newH6, newA5, newB5, newC5, newD5, newE5,
        newF5, newG5, newH5, newA4, newB4, newC4, newD4, newE4, newF4, newG4, newH4, newA3, newB3, newC3, newD3, newE3,
        newF3, newG3, newH3, newA2, newB2, newC2, newD2, newE2, newF2, newG2, newH2, newA1, newB1, newC1, newD1, newE1,
        newF1, newG1, newH1);
  }

  // we only allow updates which change the board enforce good programming style
  private static void checkUpdateSquare(Square square, Piece currentPiece, Piece newPiece) {
    if (currentPiece == newPiece) {
      throw new IllegalArgumentException(
          "The square " + square.getName() + " is requested to be populated with the piece " + newPiece
              + " but already contains this piece. This operation is not supported.");
    }
  }

}
