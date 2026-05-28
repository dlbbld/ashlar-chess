package io.github.dlbbld.ashlarchess.model;

import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;

public record LegalMove(MoveSpecification moveSpecification, Piece movingPiece, Piece pieceCaptured, LegalMoveKind kind)
    implements Comparable<LegalMove>, EnumConstants {

  public LegalMove {
    if (movingPiece == Piece.NONE) {
      throw new IllegalArgumentException("The moving piece cannot be the none piece");
    }
  }

  public Side havingMove() {
    return movingPiece.getSide();
  }

  @Override
  public int compareTo(LegalMove legalMove) {
    return this.moveSpecification().compareTo(legalMove.moveSpecification());
  }

}
