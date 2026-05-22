package com.dlb.chess.fen;

import com.dlb.chess.bitboard.BitboardPositionUtility;
import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.CastlingRight;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.constants.EnumConstants;

public class FenBoard implements EnumConstants {

  public static String calculateFen(Board board) {

    final Side havingMove = board.getHavingMove();

    final StringBuilder fen = new StringBuilder();

    final String piecePlacement = BitboardPositionUtility.calculatePiecePlacement(board.getBitboardPosition());
    fen.append(piecePlacement);
    fen.append(" ");

    // side having the move
    switch (havingMove) {
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

    // half move clock
    fen.append(board.getHalfMoveClock());
    fen.append(" ");

    // full move number (of next half move)
    fen.append(board.getFullMoveNumberForNextHalfMove());

    return Nulls.toString(fen);
  }

}
