// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.fen;

import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.common.exceptions.NonePointerException;

/**
 * Translates a {@link Piece} into its {@link FenPieceSymbol}. Kept off the enum, which carries only its data (the FEN
 * piece letter) and the intrinsic letter-to-symbol parse.
 */
public final class FenPieceSymbolUtility {

  private FenPieceSymbolUtility() {
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
