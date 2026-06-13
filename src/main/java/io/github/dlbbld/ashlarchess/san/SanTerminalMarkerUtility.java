// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.san;

/**
 * Behaviour for the {@link SanTerminalMarker} value enum: the (check, checkmate) factory and the SAN-symbol rendering
 * that previously lived on the enum itself.
 */
public abstract class SanTerminalMarkerUtility {

  /**
   * Factory: produce the marker corresponding to a (check, checkmate) state.
   *
   * <p>
   * Checkmate-implies-check is respected: {@link SanTerminalMarker#CHECKMATE} is returned whenever {@code isCheckmate}
   * is true, regardless of {@code isCheck}.
   */
  public static SanTerminalMarker calculate(boolean isCheck, boolean isCheckmate) {
    // attention - checkmate is also a check, so checkmate must be checked first
    if (isCheckmate) {
      return SanTerminalMarker.CHECKMATE;
    }
    if (isCheck) {
      return SanTerminalMarker.CHECK;
    }
    return SanTerminalMarker.NONE;
  }

  /**
   * Append the marker's SAN textual symbol (if any) to {@code buildSan}.
   *
   * <p>
   * {@link SanTerminalMarker#NONE} appends nothing; {@link SanTerminalMarker#CHECK} appends {@code +};
   * {@link SanTerminalMarker#CHECKMATE} appends {@code #}.
   */
  public static void appendTo(StringBuilder buildSan, SanTerminalMarker sanTerminalMarker) {
    switch (sanTerminalMarker) {
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
