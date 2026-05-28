package io.github.dlbbld.ashlarchess.san;

public enum SanTerminalMarker {

  NONE,
  CHECK,
  CHECKMATE;

  /**
   * Factory: produce the marker corresponding to a (check, checkmate) state.
   *
   * <p>
   * Checkmate-implies-check is respected: {@link #CHECKMATE} is returned whenever {@code isCheckmate} is true,
   * regardless of {@code isCheck}.
   */
  public static SanTerminalMarker calculate(boolean isCheck, boolean isCheckmate) {
    // attention - checkmate is also a check, so checkmate must be checked first
    if (isCheckmate) {
      return CHECKMATE;
    }
    if (isCheck) {
      return CHECK;
    }
    return NONE;
  }

  /**
   * Append this marker's SAN textual symbol (if any) to {@code buildSan}.
   *
   * <p>
   * {@link #NONE} appends nothing; {@link #CHECK} appends {@code +}; {@link #CHECKMATE} appends {@code #}.
   */
  public void append(StringBuilder buildSan) {
    switch (this) {
      case NONE:
        break;
      case CHECK:
        buildSan.append(SanSymbol.CHECK.getSymbol());
        break;
      case CHECKMATE:
        buildSan.append(SanSymbol.CHECKMATE.getSymbol());
        break;
      default:
        throw new IllegalArgumentException();
    }
  }
}
