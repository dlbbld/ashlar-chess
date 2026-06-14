// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board;

import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.fen.FenPieceSymbolUtility;

public record StaticPosition(Piece a8, Piece b8, Piece c8, Piece d8, Piece e8, Piece f8, Piece g8, Piece h8, Piece a7,
    Piece b7, Piece c7, Piece d7, Piece e7, Piece f7, Piece g7, Piece h7, Piece a6, Piece b6, Piece c6, Piece d6,
    Piece e6, Piece f6, Piece g6, Piece h6, Piece a5, Piece b5, Piece c5, Piece d5, Piece e5, Piece f5, Piece g5,
    Piece h5, Piece a4, Piece b4, Piece c4, Piece d4, Piece e4, Piece f4, Piece g4, Piece h4, Piece a3, Piece b3,
    Piece c3, Piece d3, Piece e3, Piece f3, Piece g3, Piece h3, Piece a2, Piece b2, Piece c2, Piece d2, Piece e2,
    Piece f2, Piece g2, Piece h2, Piece a1, Piece b1, Piece c1, Piece d1, Piece e1, Piece f1, Piece g1, Piece h1)
    implements EnumConstants {

  public static final StaticPosition INITIAL_POSITION = new StaticPosition(BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP,
      BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN,
      BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE,
      Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE,
      Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE,
      Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE,
      WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_ROOK,
      WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK);

  public static final StaticPosition EMPTY_POSITION = new StaticPosition(Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE,
      Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE,
      Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE,
      Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE,
      Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE,
      Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE,
      Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE,
      Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE, Piece.NONE);

  @Override
  public String toString() {
    final StringBuilder output = new StringBuilder();

    output.append(calculateSquareLetter(a8));
    output.append(calculateSquareLetter(b8));
    output.append(calculateSquareLetter(c8));
    output.append(calculateSquareLetter(d8));
    output.append(calculateSquareLetter(e8));
    output.append(calculateSquareLetter(f8));
    output.append(calculateSquareLetter(g8));
    output.append(calculateSquareLetter(h8));
    output.append("\n");

    output.append(calculateSquareLetter(a7));
    output.append(calculateSquareLetter(b7));
    output.append(calculateSquareLetter(c7));
    output.append(calculateSquareLetter(d7));
    output.append(calculateSquareLetter(e7));
    output.append(calculateSquareLetter(f7));
    output.append(calculateSquareLetter(g7));
    output.append(calculateSquareLetter(h7));
    output.append("\n");

    output.append(calculateSquareLetter(a6));
    output.append(calculateSquareLetter(b6));
    output.append(calculateSquareLetter(c6));
    output.append(calculateSquareLetter(d6));
    output.append(calculateSquareLetter(e6));
    output.append(calculateSquareLetter(f6));
    output.append(calculateSquareLetter(g6));
    output.append(calculateSquareLetter(h6));
    output.append("\n");

    output.append(calculateSquareLetter(a5));
    output.append(calculateSquareLetter(b5));
    output.append(calculateSquareLetter(c5));
    output.append(calculateSquareLetter(d5));
    output.append(calculateSquareLetter(e5));
    output.append(calculateSquareLetter(f5));
    output.append(calculateSquareLetter(g5));
    output.append(calculateSquareLetter(h5));
    output.append("\n");

    output.append(calculateSquareLetter(a4));
    output.append(calculateSquareLetter(b4));
    output.append(calculateSquareLetter(c4));
    output.append(calculateSquareLetter(d4));
    output.append(calculateSquareLetter(e4));
    output.append(calculateSquareLetter(f4));
    output.append(calculateSquareLetter(g4));
    output.append(calculateSquareLetter(h4));
    output.append("\n");

    output.append(calculateSquareLetter(a3));
    output.append(calculateSquareLetter(b3));
    output.append(calculateSquareLetter(c3));
    output.append(calculateSquareLetter(d3));
    output.append(calculateSquareLetter(e3));
    output.append(calculateSquareLetter(f3));
    output.append(calculateSquareLetter(g3));
    output.append(calculateSquareLetter(h3));
    output.append("\n");

    output.append(calculateSquareLetter(a2));
    output.append(calculateSquareLetter(b2));
    output.append(calculateSquareLetter(c2));
    output.append(calculateSquareLetter(d2));
    output.append(calculateSquareLetter(e2));
    output.append(calculateSquareLetter(f2));
    output.append(calculateSquareLetter(g2));
    output.append(calculateSquareLetter(h2));
    output.append("\n");

    output.append(calculateSquareLetter(a1));
    output.append(calculateSquareLetter(b1));
    output.append(calculateSquareLetter(c1));
    output.append(calculateSquareLetter(d1));
    output.append(calculateSquareLetter(e1));
    output.append(calculateSquareLetter(f1));
    output.append(calculateSquareLetter(g1));
    output.append(calculateSquareLetter(h1));
    output.append("\n");

    return Nulls.toString(output);
  }

  private static char calculateSquareLetter(Piece piece) {
    if (piece == Piece.NONE) {
      return '.';
    }
    return FenPieceSymbolUtility.calculate(piece).pieceLetter();
  }

  public Piece get(Square square) {
    return switch (square) {
      case A8 -> a8;
      case B8 -> b8;
      case C8 -> c8;
      case D8 -> d8;
      case E8 -> e8;
      case F8 -> f8;
      case G8 -> g8;
      case H8 -> h8;
      case A7 -> a7;
      case B7 -> b7;
      case C7 -> c7;
      case D7 -> d7;
      case E7 -> e7;
      case F7 -> f7;
      case G7 -> g7;
      case H7 -> h7;
      case A6 -> a6;
      case B6 -> b6;
      case C6 -> c6;
      case D6 -> d6;
      case E6 -> e6;
      case F6 -> f6;
      case G6 -> g6;
      case H6 -> h6;
      case A5 -> a5;
      case B5 -> b5;
      case C5 -> c5;
      case D5 -> d5;
      case E5 -> e5;
      case F5 -> f5;
      case G5 -> g5;
      case H5 -> h5;
      case A4 -> a4;
      case B4 -> b4;
      case C4 -> c4;
      case D4 -> d4;
      case E4 -> e4;
      case F4 -> f4;
      case G4 -> g4;
      case H4 -> h4;
      case A3 -> a3;
      case B3 -> b3;
      case C3 -> c3;
      case D3 -> d3;
      case E3 -> e3;
      case F3 -> f3;
      case G3 -> g3;
      case H3 -> h3;
      case A2 -> a2;
      case B2 -> b2;
      case C2 -> c2;
      case D2 -> d2;
      case E2 -> e2;
      case F2 -> f2;
      case G2 -> g2;
      case H2 -> h2;
      case A1 -> a1;
      case B1 -> b1;
      case C1 -> c1;
      case D1 -> d1;
      case E1 -> e1;
      case F1 -> f1;
      case G1 -> g1;
      case H1 -> h1;
      case NONE -> throw new IllegalArgumentException("The none square does not belong to the board)");
      default -> throw new IllegalArgumentException();
    };
  }

  public boolean isEmpty(Square square) {
    return switch (square) {
      case A8 -> a8 == Piece.NONE;
      case B8 -> b8 == Piece.NONE;
      case C8 -> c8 == Piece.NONE;
      case D8 -> d8 == Piece.NONE;
      case E8 -> e8 == Piece.NONE;
      case F8 -> f8 == Piece.NONE;
      case G8 -> g8 == Piece.NONE;
      case H8 -> h8 == Piece.NONE;
      case A7 -> a7 == Piece.NONE;
      case B7 -> b7 == Piece.NONE;
      case C7 -> c7 == Piece.NONE;
      case D7 -> d7 == Piece.NONE;
      case E7 -> e7 == Piece.NONE;
      case F7 -> f7 == Piece.NONE;
      case G7 -> g7 == Piece.NONE;
      case H7 -> h7 == Piece.NONE;
      case A6 -> a6 == Piece.NONE;
      case B6 -> b6 == Piece.NONE;
      case C6 -> c6 == Piece.NONE;
      case D6 -> d6 == Piece.NONE;
      case E6 -> e6 == Piece.NONE;
      case F6 -> f6 == Piece.NONE;
      case G6 -> g6 == Piece.NONE;
      case H6 -> h6 == Piece.NONE;
      case A5 -> a5 == Piece.NONE;
      case B5 -> b5 == Piece.NONE;
      case C5 -> c5 == Piece.NONE;
      case D5 -> d5 == Piece.NONE;
      case E5 -> e5 == Piece.NONE;
      case F5 -> f5 == Piece.NONE;
      case G5 -> g5 == Piece.NONE;
      case H5 -> h5 == Piece.NONE;
      case A4 -> a4 == Piece.NONE;
      case B4 -> b4 == Piece.NONE;
      case C4 -> c4 == Piece.NONE;
      case D4 -> d4 == Piece.NONE;
      case E4 -> e4 == Piece.NONE;
      case F4 -> f4 == Piece.NONE;
      case G4 -> g4 == Piece.NONE;
      case H4 -> h4 == Piece.NONE;
      case A3 -> a3 == Piece.NONE;
      case B3 -> b3 == Piece.NONE;
      case C3 -> c3 == Piece.NONE;
      case D3 -> d3 == Piece.NONE;
      case E3 -> e3 == Piece.NONE;
      case F3 -> f3 == Piece.NONE;
      case G3 -> g3 == Piece.NONE;
      case H3 -> h3 == Piece.NONE;
      case A2 -> a2 == Piece.NONE;
      case B2 -> b2 == Piece.NONE;
      case C2 -> c2 == Piece.NONE;
      case D2 -> d2 == Piece.NONE;
      case E2 -> e2 == Piece.NONE;
      case F2 -> f2 == Piece.NONE;
      case G2 -> g2 == Piece.NONE;
      case H2 -> h2 == Piece.NONE;
      case A1 -> a1 == Piece.NONE;
      case B1 -> b1 == Piece.NONE;
      case C1 -> c1 == Piece.NONE;
      case D1 -> d1 == Piece.NONE;
      case E1 -> e1 == Piece.NONE;
      case F1 -> f1 == Piece.NONE;
      case G1 -> g1 == Piece.NONE;
      case H1 -> h1 == Piece.NONE;
      case NONE -> throw new IllegalArgumentException("The none square does not belong to the board)");
      default -> throw new IllegalArgumentException();
    };
  }

  public boolean isOwnPiece(Square square, Side havingMove) {
    if (isEmpty(square)) {
      return false;
    }
    final Piece piece = get(square);
    return piece.getSide() == havingMove;
  }

  public boolean isOwnPiece(Square square, Side havingMove, PieceType pieceType) {
    if (isEmpty(square)) {
      return false;
    }
    final Piece piece = get(square);
    return piece.getSide() == havingMove && piece.getPieceType() == pieceType;
  }

  public boolean isOpponentPiece(Square square, Side havingMove) {
    return isOwnPiece(square, havingMove.getOppositeSide());
  }

  public boolean isOwnPawn(Square square, Side havingMove) {
    return isOwnPiece(square, havingMove, PAWN);
  }

  public boolean isOpponentPawn(Square square, Side havingMove) {
    return isOwnPiece(square, havingMove.getOppositeSide(), PAWN);
  }

  public boolean isPawn(Square square) {
    return isOwnPawn(square, WHITE) || isOwnPawn(square, BLACK);
  }

  public boolean isOwnKing(Square square, Side havingMove) {
    return isOwnPiece(square, havingMove, KING);
  }

  public boolean isOpponentKing(Square square, Side havingMove) {
    return isOwnPiece(square, havingMove.getOppositeSide(), KING);
  }
}
