package io.github.dlbbld.ashlarchess.unwinnability;

import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;

record PiecePlacement(PieceType pieceType, Side side, Square squareOriginal) implements Comparable<PiecePlacement> {

  @Override
  public int compareTo(PiecePlacement o) {
    if (this.side != o.side) {
      return this.side.compareTo(o.side);
    }
    if (this.squareOriginal != o.squareOriginal) {
      return this.squareOriginal.compareTo(o.squareOriginal);
    }
    return this.pieceType.compareTo(o.pieceType);
  }

  @Override
  public String toString() {
    return side.getName() + " " + pieceType.getName() + " on " + squareOriginal.getName();
  }

}
