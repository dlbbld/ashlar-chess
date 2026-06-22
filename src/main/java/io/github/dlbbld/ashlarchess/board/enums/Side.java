// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board.enums;

import java.util.List;

import io.github.dlbbld.ashlarchess.common.exceptions.NonePointerException;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.messages.Message;

public enum Side {

  WHITE(true, false, Message.getString("color.white.name")),
  BLACK(false, true, Message.getString("color.black.name")),
  NONE(false, false, "");

  @SuppressWarnings("null")
  public static final List<Side> REAL = List.of(WHITE, BLACK);

  private final boolean isWhite;
  private final boolean isBlack;
  private final String name;

  Side(boolean isWhite, boolean isBlack, String name) {
    this.isWhite = isWhite;
    this.isBlack = isBlack;
    this.name = name;
  }

  public boolean isWhite() {
    check();
    return isWhite;
  }

  public boolean isBlack() {
    check();
    return isBlack;
  }

  /**
   * The human-readable side name; throws {@code NonePointerException} for {@link #NONE}. Distinct from the inherited
   * {@code name()} (the Java enum constant, e.g. {@code WHITE}).
   */
  public String getName() {
    check();
    return name;
  }

  // cannot define in constructor as cannot reference an enum befor it is defined
  public Side getOppositeSide() {
    return switch (this) {
      case WHITE -> BLACK;
      case BLACK -> WHITE;
      case NONE -> throw new ProgrammingMistakeException("The non side has no opposite side");
      default -> throw new ProgrammingMistakeException("The non side has no opposite side");
    };
  }

  private void check() {
    if (this == NONE) {
      throw new NonePointerException();
    }
  }

}
