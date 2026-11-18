// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.pgn.internal;

/** Code point rules shared by PGN tag values and commentary. */
public final class PgnCharacters {

  private PgnCharacters() {
  }

  /**
   * The code points Unicode reserves permanently: U+FDD0..U+FDEF and the last two code points of every plane.
   *
   * <p>
   * These are rejected in place of every unassigned code point. The unassigned set shrinks with each Unicode version,
   * so a character newer than the tables of the running JDK - an emoji added after Java 17 was released, say - would
   * be valid on one Java version and invalid on another.
   */
  public static boolean isNoncharacter(int codePoint) {
    return codePoint >= 0xFDD0 && codePoint <= 0xFDEF || (codePoint & 0xFFFE) == 0xFFFE;
  }
}
