package io.github.dlbbld.ashlarchess.test.librarycomparison.utility;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Square;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.test.librarycarlos.NullsCarlos;

public class BoardConversionUtitlity {

  public static StaticPosition convertBoardToStaticPosition(Board board) {
    return new StaticPosition(convert(board, Square.A8), convert(board, Square.B8), convert(board, Square.C8),
        convert(board, Square.D8), convert(board, Square.E8), convert(board, Square.F8), convert(board, Square.G8),
        convert(board, Square.H8), convert(board, Square.A7), convert(board, Square.B7), convert(board, Square.C7),
        convert(board, Square.D7), convert(board, Square.E7), convert(board, Square.F7), convert(board, Square.G7),
        convert(board, Square.H7), convert(board, Square.A6), convert(board, Square.B6), convert(board, Square.C6),
        convert(board, Square.D6), convert(board, Square.E6), convert(board, Square.F6), convert(board, Square.G6),
        convert(board, Square.H6), convert(board, Square.A5), convert(board, Square.B5), convert(board, Square.C5),
        convert(board, Square.D5), convert(board, Square.E5), convert(board, Square.F5), convert(board, Square.G5),
        convert(board, Square.H5), convert(board, Square.A4), convert(board, Square.B4), convert(board, Square.C4),
        convert(board, Square.D4), convert(board, Square.E4), convert(board, Square.F4), convert(board, Square.G4),
        convert(board, Square.H4), convert(board, Square.A3), convert(board, Square.B3), convert(board, Square.C3),
        convert(board, Square.D3), convert(board, Square.E3), convert(board, Square.F3), convert(board, Square.G3),
        convert(board, Square.H3), convert(board, Square.A2), convert(board, Square.B2), convert(board, Square.C2),
        convert(board, Square.D2), convert(board, Square.E2), convert(board, Square.F2), convert(board, Square.G2),
        convert(board, Square.H2), convert(board, Square.A1), convert(board, Square.B1), convert(board, Square.C1),
        convert(board, Square.D1), convert(board, Square.E1), convert(board, Square.F1), convert(board, Square.G1),
        convert(board, Square.H1));
  }

  private static Piece convert(Board board, Square square) {
    return EnumConversionUtility.convertPiece(NullsCarlos.getPiece(board, square));
  }
}
