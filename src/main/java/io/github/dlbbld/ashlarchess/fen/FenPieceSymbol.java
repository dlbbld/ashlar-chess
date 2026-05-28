// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.fen;

import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.common.exceptions.NonePointerException;

public enum FenPieceSymbol {

  WHITE_PAWN('P', Piece.WHITE_PAWN),
  WHITE_ROOK('R', Piece.WHITE_ROOK),
  WHITE_KNIGHT('N', Piece.WHITE_KNIGHT),
  WHITE_BISHOP('B', Piece.WHITE_BISHOP),
  WHITE_QUEEN('Q', Piece.WHITE_QUEEN),
  WHITE_KING('K', Piece.WHITE_KING),
  BLACK_PAWN('p', Piece.BLACK_PAWN),
  BLACK_ROOK('r', Piece.BLACK_ROOK),
  BLACK_KNIGHT('n', Piece.BLACK_KNIGHT),
  BLACK_BISHOP('b', Piece.BLACK_BISHOP),
  BLACK_QUEEN('q', Piece.BLACK_QUEEN),
  BLACK_KING('k', Piece.BLACK_KING);

  private final char pieceLetter;
  private final Piece piece;

  FenPieceSymbol(char pieceLetter, Piece piece) {
    this.pieceLetter = pieceLetter;
    this.piece = piece;
  }

  public char pieceLetter() {
    return pieceLetter;
  }

  public Piece piece() {
    return piece;
  }

  public static boolean exists(char pieceLetter) {
    for (final FenPieceSymbol symbol : values()) {
      if (symbol.pieceLetter == pieceLetter) {
        return true;
      }
    }
    return false;
  }

  public static FenPieceSymbol calculate(char pieceLetter) {
    for (final FenPieceSymbol symbol : values()) {
      if (symbol.pieceLetter == pieceLetter) {
        return symbol;
      }
    }
    throw new IllegalArgumentException("Not a valid FEN piece letter: '" + pieceLetter + "'");
  }

  public static FenPieceSymbol calculate(Piece piece) {
    return switch (piece) {
      case WHITE_PAWN -> FenPieceSymbol.WHITE_PAWN;
      case WHITE_ROOK -> FenPieceSymbol.WHITE_ROOK;
      case WHITE_KNIGHT -> FenPieceSymbol.WHITE_KNIGHT;
      case WHITE_BISHOP -> FenPieceSymbol.WHITE_BISHOP;
      case WHITE_QUEEN -> FenPieceSymbol.WHITE_QUEEN;
      case WHITE_KING -> FenPieceSymbol.WHITE_KING;
      case BLACK_PAWN -> FenPieceSymbol.BLACK_PAWN;
      case BLACK_ROOK -> FenPieceSymbol.BLACK_ROOK;
      case BLACK_KNIGHT -> FenPieceSymbol.BLACK_KNIGHT;
      case BLACK_BISHOP -> FenPieceSymbol.BLACK_BISHOP;
      case BLACK_QUEEN -> FenPieceSymbol.BLACK_QUEEN;
      case BLACK_KING -> FenPieceSymbol.BLACK_KING;
      case NONE -> throw new NonePointerException();
    };
  }

}
