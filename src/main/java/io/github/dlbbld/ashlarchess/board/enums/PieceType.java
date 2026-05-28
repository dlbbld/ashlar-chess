package io.github.dlbbld.ashlarchess.board.enums;

import io.github.dlbbld.ashlarchess.common.exceptions.NonePointerException;
import io.github.dlbbld.ashlarchess.messages.Message;

public enum PieceType {
  PAWN('P', 1, Message.getString("pieceType.pawn.name")),
  ROOK('R', 5, Message.getString("pieceType.rook.name")),
  KNIGHT('N', 3, Message.getString("pieceType.knight.name")),
  BISHOP('B', 3, Message.getString("pieceType.bishop.name")),
  QUEEN('Q', 9, Message.getString("pieceType.queen.name")),
  // value is only used for move ordering for unwinnability
  KING('K', 0, Message.getString("pieceType.king.name")),
  NONE('\0', -1, "");

  private final char letter;
  private final int value;
  private final String name;

  public char getLetter() {
    check();
    return letter;
  }

  public int getValue() {
    check();
    return value;
  }

  public String getName() {
    check();
    return name;
  }

  PieceType(char letter, int value, String name) {
    this.letter = letter;
    this.value = value;
    this.name = name;
  }

  public static Piece calculate(Side havingMove, PieceType pieceType) {
    return switch (havingMove) {
      case BLACK -> switch (pieceType) {
        case PAWN -> Piece.BLACK_PAWN;
        case ROOK -> Piece.BLACK_ROOK;
        case KNIGHT -> Piece.BLACK_KNIGHT;
        case BISHOP -> Piece.BLACK_BISHOP;
        case QUEEN -> Piece.BLACK_QUEEN;
        case KING -> Piece.BLACK_KING;
        case NONE -> throw new IllegalArgumentException();
        default -> throw new IllegalArgumentException();
      };
      case WHITE -> switch (pieceType) {
        case PAWN -> Piece.WHITE_PAWN;
        case ROOK -> Piece.WHITE_ROOK;
        case KNIGHT -> Piece.WHITE_KNIGHT;
        case BISHOP -> Piece.WHITE_BISHOP;
        case QUEEN -> Piece.WHITE_QUEEN;
        case KING -> Piece.WHITE_KING;
        case NONE -> throw new IllegalArgumentException();
        default -> throw new IllegalArgumentException();
      };
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  private void check() {
    if (this == NONE) {
      throw new NonePointerException();
    }
  }

}
