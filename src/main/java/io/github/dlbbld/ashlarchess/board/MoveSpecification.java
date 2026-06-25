// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board;

import io.github.dlbbld.ashlarchess.board.enums.CastlingMove;
import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceType;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.exceptions.ProgrammingMistakeException;

// Specification: for the non-castling non-promotion move the from and to square must be different board squares, for
// the non-castling promotion move the from and to square must be different board squares and the promotion piece type
// not the none piece type, for the castling move the castling move must be king-side or queen-side. The side to move
// is determined by the board state, not by this specification.
public record MoveSpecification(Square fromSquare, Square toSquare, CastlingMove castlingMove,
    PromotionPieceType promotionPieceType) implements Comparable<MoveSpecification> {

  // Canonical constructor: validates the invariant on every construction path, including direct callers of the 4-arg
  // form. A non-castling spec must have two distinct real squares; a castling spec must have NONE from/to squares and
  // no promotion piece type.
  public MoveSpecification {
    if (castlingMove == CastlingMove.NONE) {
      validate(fromSquare, toSquare);
    } else {
      if (fromSquare != Square.NONE || toSquare != Square.NONE) {
        throw new IllegalArgumentException("A castling move must have the none from and to square");
      }
      if (promotionPieceType != PromotionPieceType.NONE) {
        throw new IllegalArgumentException("A castling move cannot have a promotion piece type");
      }
    }
  }

  public MoveSpecification(Square fromSquare, Square toSquare) {
    this(fromSquare, toSquare, CastlingMove.NONE, PromotionPieceType.NONE);
  }

  public MoveSpecification(Square fromSquare, Square toSquare, PromotionPieceType promotionPieceType) {
    this(fromSquare, toSquare, CastlingMove.NONE, promotionPieceType);

    if (promotionPieceType == PromotionPieceType.NONE) {
      throw new IllegalArgumentException("The promotion piece type cannot be the none piece type");
    }
  }

  public MoveSpecification(CastlingMove castlingMove) {
    this(Square.NONE, Square.NONE, castlingMove, PromotionPieceType.NONE);
  }

  private static void validate(Square fromSquare, Square toSquare) {
    if (fromSquare == Square.NONE) {
      throw new IllegalArgumentException("The from square cannot be the none square");
    }
    if (toSquare == Square.NONE) {
      throw new IllegalArgumentException("The to square cannot be the none square");
    }
    if (fromSquare == toSquare) {
      throw new IllegalArgumentException("The from and to square must be different");
    }
  }

  @Override
  public int compareTo(MoveSpecification move) {
    if (this.equals(move)) {
      return 0;
    }

    if (this.fromSquare() != move.fromSquare()) {
      return this.fromSquare().compareTo(move.fromSquare());
    }

    if (this.toSquare() != move.toSquare()) {
      return this.toSquare().compareTo(move.toSquare());
    }

    if (this.castlingMove() != move.castlingMove()) {
      return this.castlingMove().compareTo(move.castlingMove());
    }

    if (this.promotionPieceType() != move.promotionPieceType()) {
      return this.promotionPieceType().compareForMoveOrdering(move.promotionPieceType());
    }

    // code cannot come here
    throw new ProgrammingMistakeException("now all fields are equal so objects are equal");
  }

  public boolean isPromotion() {
    return promotionPieceType() != PromotionPieceType.NONE;
  }

  public boolean isCastling() {
    return castlingMove() != CastlingMove.NONE;
  }

}
