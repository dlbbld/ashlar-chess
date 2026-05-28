package io.github.dlbbld.ashlarchess.model;

import io.github.dlbbld.ashlarchess.board.enums.Square;

public record EmptyBoardMove(Square fromSquare, Square toSquare) implements Comparable<EmptyBoardMove> {

  @Override
  public String toString() {
    return fromSquare + " " + toSquare;
  }

  @Override
  public int compareTo(EmptyBoardMove otherEmptyBoardMove) {
    if (this.fromSquare().compareTo(otherEmptyBoardMove.fromSquare()) != 0) {
      return this.fromSquare().compareTo(otherEmptyBoardMove.fromSquare());
    }
    if (this.toSquare().compareTo(otherEmptyBoardMove.toSquare()) != 0) {
      return this.toSquare().compareTo(otherEmptyBoardMove.toSquare());
    }
    return 0;
  }

}
