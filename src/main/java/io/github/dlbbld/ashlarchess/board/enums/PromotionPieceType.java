package io.github.dlbbld.ashlarchess.board.enums;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.common.exceptions.NonePointerException;

public enum PromotionPieceType {
  ROOK(PieceType.ROOK),
  KNIGHT(PieceType.KNIGHT),
  BISHOP(PieceType.BISHOP),
  QUEEN(PieceType.QUEEN),
  NONE(PieceType.NONE);

  @SuppressWarnings("null")
  // Move-ordering rule (Q, R, B, N) - see PromotionPieceTypeUtility for the rationale.
  // Enum declaration above keeps the static catalog order (P, R, N, B, Q, K) shared with PieceType.
  public static final ImmutableList<PromotionPieceType> REAL = ImmutableList.of(QUEEN, ROOK, BISHOP, KNIGHT);

  private final PieceType pieceType;

  PromotionPieceType(PieceType pieceType) {
    this.pieceType = pieceType;
  }

  public PieceType getPieceType() {
    check();
    return pieceType;
  }

  public static Piece calculate(Side havingMove, PromotionPieceType pieceType) {
    return switch (havingMove) {
      case BLACK -> switch (pieceType) {
        case ROOK -> Piece.BLACK_ROOK;
        case KNIGHT -> Piece.BLACK_KNIGHT;
        case BISHOP -> Piece.BLACK_BISHOP;
        case QUEEN -> Piece.BLACK_QUEEN;
        case NONE -> throw new IllegalArgumentException();
        default -> throw new IllegalArgumentException();
      };
      case WHITE -> switch (pieceType) {
        case ROOK -> Piece.WHITE_ROOK;
        case KNIGHT -> Piece.WHITE_KNIGHT;
        case BISHOP -> Piece.WHITE_BISHOP;
        case QUEEN -> Piece.WHITE_QUEEN;
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
