// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.exceptions;

/**
 * Thrown when a caller reads a property of a {@code NONE} sentinel enum (e.g. {@code Piece.NONE.getSide()} after
 * reading an empty square). This is caller misuse - the {@code NONE} members are null-object sentinels whose properties
 * carry no meaning - so it is a {@link UsageException}, not a {@link ProgrammingMistakeException}. Callers test for the
 * {@code NONE} member (e.g. {@code piece != Piece.NONE}) before reading its properties.
 */
public class NonePointerException extends UsageException {

  private static final String BASE_MESSAGE = "Properties of NONE enums have no meaning and are not supposed to be assessed";

  public NonePointerException() {
    super(BASE_MESSAGE);
  }

}
