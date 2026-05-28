package io.github.dlbbld.ashlarchess.common.constants;

import io.github.dlbbld.ashlarchess.board.enums.CastlingMove;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.model.LegalMoveKind;
import io.github.dlbbld.ashlarchess.san.SanSymbol;

public abstract class CastlingConstants implements EnumConstants {

  public static final String SAN_CASTLING_KING_SIDE = "" + SanSymbol.CASTLING_O.getSymbol()
      + SanSymbol.CASTLING_HYPHEN.getSymbol() + SanSymbol.CASTLING_O.getSymbol();

  public static final String SAN_CASTLING_QUEEN_SIDE = SAN_CASTLING_KING_SIDE + SanSymbol.CASTLING_HYPHEN.getSymbol()
      + SanSymbol.CASTLING_O.getSymbol();

  public static final LegalMove WHITE_KING_SIDE_CASTLING_MOVE = new LegalMove(
      new MoveSpecification(CastlingMove.KING_SIDE), WHITE_KING, Piece.NONE, LegalMoveKind.CASTLING);
  public static final LegalMove WHITE_QUEEN_SIDE_CASTLING_MOVE = new LegalMove(
      new MoveSpecification(CastlingMove.QUEEN_SIDE), WHITE_KING, Piece.NONE, LegalMoveKind.CASTLING);

  public static final LegalMove BLACK_KING_SIDE_CASTLING_MOVE = new LegalMove(
      new MoveSpecification(CastlingMove.KING_SIDE), BLACK_KING, Piece.NONE, LegalMoveKind.CASTLING);
  public static final LegalMove BLACK_QUEEN_SIDE_CASTLING_MOVE = new LegalMove(
      new MoveSpecification(CastlingMove.QUEEN_SIDE), BLACK_KING, Piece.NONE, LegalMoveKind.CASTLING);

  // constants for white
  public static final Square WHITE_KING_FROM = E1;

  public static final Square WHITE_KING_KING_SIDE_CASTLING_TO = G1;

  public static final Square WHITE_ROOK_KING_SIDE_CASTLING_FROM = H1;

  public static final Square WHITE_ROOK_KING_SIDE_CASTLING_TO = F1;

  public static final Square WHITE_KING_QUEEN_SIDE_CASTLING_TO = C1;

  public static final Square WHITE_ROOK_QUEEN_SIDE_CASTLING_FROM = A1;

  public static final Square WHITE_ROOK_QUEEN_SIDE_CASTLING_TO = D1;

  // constants for black
  public static final Square BLACK_KING_FROM = E8;

  public static final Square BLACK_KING_KING_SIDE_CASTLING_TO = G8;

  public static final Square BLACK_ROOK_KING_SIDE_CASTLING_FROM = H8;

  public static final Square BLACK_ROOK_KING_SIDE_CASTLING_TO = F8;

  public static final Square BLACK_KING_QUEEN_SIDE_CASTLING_TO = C8;

  public static final Square BLACK_ROOK_QUEEN_SIDE_CASTLING_FROM = A8;

  public static final Square BLACK_ROOK_QUEEN_SIDE_CASTLING_TO = D8;

}
