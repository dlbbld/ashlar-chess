// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board.enums;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.exceptions.NonePointerException;
import io.github.dlbbld.ashlarchess.messages.Message;

public enum PieceType {
  PAWN('P', 1, Message.getString("pieceType.pawn.name")),
  ROOK('R', 5, Message.getString("pieceType.rook.name")),
  KNIGHT('N', 3, Message.getString("pieceType.knight.name")),
  BISHOP('B', 3, Message.getString("pieceType.bishop.name")),
  QUEEN('Q', 9, Message.getString("pieceType.queen.name")),
  // value is only used for move ordering for unwinnability
  KING('K', 0, Message.getString("pieceType.king.name")),
  NONE('\0', -1, "");

  private final char letter;
  private final int value;
  private final String name;

  public char getLetter() {
    check();
    return letter;
  }

  public int getValue() {
    check();
    return value;
  }

  /**
   * The human-readable piece-type name; throws {@code NonePointerException} for {@link #NONE}. Distinct from the
   * inherited {@code name()} (the Java enum constant, e.g. {@code QUEEN}) and from {@link #getLetter()} /
   * {@link #toString()} (the single-letter symbol, e.g. {@code Q}).
   */
  public String getName() {
    check();
    return name;
  }

  /**
   * Piece-type letter: {@code "P" "N" "B" "R" "Q" "K"}, and {@code "none"} for {@link #NONE}.
   */
  @Override
  public String toString() {
    return this == NONE ? "none" : Nulls.valueOf(letter);
  }

  PieceType(char letter, int value, String name) {
    this.letter = letter;
    this.value = value;
    this.name = name;
  }

  private void check() {
    if (this == NONE) {
      throw new NonePointerException();
    }
  }

}
