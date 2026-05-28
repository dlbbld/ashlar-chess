package io.github.dlbbld.ashlarchess.san;

import io.github.dlbbld.ashlarchess.common.Nulls;

public enum SanSymbol {
  CAPTURE('x'),
  PROMOTION('='),
  CHECK('+'),
  CHECKMATE('#'),
  CASTLING_O('O'),
  CASTLING_HYPHEN('-');

  private final char symbol;
  private final String symbolString;

  SanSymbol(char symbol) {
    this.symbol = symbol;
    this.symbolString = Nulls.valueOf(symbol);
  }

  public char getSymbol() {
    return symbol;
  }

  public String getSymbolString() {
    return symbolString;
  }

  public static boolean exists(char symbol) {
    for (final SanSymbol sanSymbol : SanSymbol.values()) {
      if (sanSymbol.getSymbol() == symbol) {
        return true;
      }
    }
    return false;
  }
}
