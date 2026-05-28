package io.github.dlbbld.ashlarchess.fen;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.exceptions.NonePointerException;

public enum FenSideSymbol {

  WHITE('w', Side.WHITE),
  BLACK('b', Side.BLACK);

  private final char sideLetter;
  private final Side side;

  FenSideSymbol(char sideLetter, Side side) {
    this.sideLetter = sideLetter;
    this.side = side;
  }

  public char sideLetter() {
    return sideLetter;
  }

  public Side side() {
    return side;
  }

  public static boolean exists(char sideLetter) {
    for (final FenSideSymbol symbol : values()) {
      if (symbol.sideLetter == sideLetter) {
        return true;
      }
    }
    return false;
  }

  public static FenSideSymbol calculate(char sideLetter) {
    for (final FenSideSymbol symbol : values()) {
      if (symbol.sideLetter == sideLetter) {
        return symbol;
      }
    }
    throw new IllegalArgumentException("Not a valid FEN side letter: '" + sideLetter + "'");
  }

  public static FenSideSymbol calculate(Side side) {
    return switch (side) {
      case WHITE -> FenSideSymbol.WHITE;
      case BLACK -> FenSideSymbol.BLACK;
      case NONE -> throw new NonePointerException();
    };
  }

}
