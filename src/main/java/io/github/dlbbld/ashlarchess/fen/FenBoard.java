// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.fen;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPositionUtility;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.CastlingRight;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;

public final class FenBoard {

  private FenBoard() {
  }

  public static String toFen(Board board) {

    final Side sideToMove = board.getSideToMove();

    final StringBuilder fen = new StringBuilder();

    final String piecePlacement = BitboardPositionUtility.calculatePiecePlacement(board.getBitboardPosition());
    fen.append(piecePlacement);
    fen.append(" ");

    // side having the move
    switch (sideToMove) {
      case BLACK -> fen.append("b");
      case WHITE -> fen.append("w");
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    }
    fen.append(" ");

    // castling rights

    final CastlingRight whiteCastlingRight = board.getCastlingRightWhite();
    final CastlingRight blackCastlingRight = board.getCastlingRightBlack();

    if (whiteCastlingRight == CastlingRight.NONE && blackCastlingRight == CastlingRight.NONE) {
      // only in this case we print a "-"
      fen.append("-");
    } else {
      // otherwise we pring KQkq as existing (not empty assured by previous check)
      // white castling rights
      switch (whiteCastlingRight) {
        case KING_AND_QUEEN_SIDE:
          fen.append("KQ");
          break;
        case KING_SIDE:
          fen.append("K");
          break;
        case QUEEN_SIDE:
          fen.append("Q");
          break;
        case NONE:
          break;
        default:
          break;
      }
      // black castling rights
      switch (blackCastlingRight) {
        case KING_AND_QUEEN_SIDE:
          fen.append("kq");
          break;
        case KING_SIDE:
          fen.append("k");
          break;
        case QUEEN_SIDE:
          fen.append("q");
          break;
        case NONE:
          break;
        default:
          break;
      }
    }
    fen.append(" ");

    // en passant capture target square
    final Square enPassantCaptureTargetSquare = board.getEnPassantCaptureTargetSquare();
    if (enPassantCaptureTargetSquare != Square.NONE) {
      fen.append(enPassantCaptureTargetSquare.getName().toLowerCase());
    } else {
      fen.append("-");
    }
    fen.append(" ");

    // halfmove clock
    fen.append(board.getHalfMoveClock());
    fen.append(" ");

    // fullmove number (of next move)
    fen.append(board.getFullMoveNumber());

    return Nulls.toString(fen);
  }

}
