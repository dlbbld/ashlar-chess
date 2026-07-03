// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.pgn;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import io.github.dlbbld.ashlarchess.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.internal.ListUtility;

/**
 * The six symbolic PGN move-suffix glyphs and their equivalent {@link Nag} codes. The PGN standard (spec section
 * 8.2.3.8) defines these glyphs as import-format shorthand for NAG codes {@code 1..6}; ashlar folds them into NAGs on
 * parse, so this enum is the glyph-to-code bridge (used by the parsers on input and the writer on output) rather than a
 * stored field on {@link PgnMove}. The mapping is the standard one: {@code !}=1, {@code ?}=2, {@code !!}=3,
 * {@code ??}=4, {@code !?}=5, {@code ?!}=6.
 */
public enum MoveSuffixAnnotation {

  MISTAKE("?", 2),
  GOOD_MOVE("!", 1),
  BLUNDER("??", 4),
  DUBIOUS_MOVE("?!", 6),
  INTERESTING_MOVE("!?", 5),
  BRILLIANT_MOVE("!!", 3),
  NONE("", 0);

  @SuppressWarnings("null")
  public static final List<MoveSuffixAnnotation> REAL = List.of(MISTAKE, GOOD_MOVE, BLUNDER, DUBIOUS_MOVE,
      INTERESTING_MOVE, BRILLIANT_MOVE);

  private final String suffix;
  private final int nagCode;

  MoveSuffixAnnotation(String suffix, int nagCode) {
    this.suffix = suffix;
    this.nagCode = nagCode;
  }

  public String getSuffix() {
    return suffix;
  }

  /** The NAG code this glyph is shorthand for ({@code 1..6}); {@code 0} for {@link #NONE}. */
  public int getNagCode() {
    return nagCode;
  }

  /** The glyph for NAG code {@code 1..6}, or {@code null} for any other code (no symbolic shorthand exists). */
  public static @Nullable MoveSuffixAnnotation fromNagCode(int nagCode) {
    for (final MoveSuffixAnnotation suffixEnum : REAL) {
      if (suffixEnum.nagCode == nagCode) {
        return suffixEnum;
      }
    }
    return null;
  }

  public static boolean exists(String suffix) {
    for (final MoveSuffixAnnotation suffixEnum : REAL) {
      if (suffixEnum.getSuffix().equals(suffix)) {
        return true;
      }
    }
    return false;
  }

  public static MoveSuffixAnnotation parse(String suffix) {
    if (!exists(suffix)) {
      throw new IllegalArgumentException("No enum exists for this suffix");
    }
    for (final MoveSuffixAnnotation suffixEnum : REAL) {
      if (suffixEnum.getSuffix().equals(suffix)) {
        return suffixEnum;
      }
    }
    throw new ProgrammingMistakeException("The code for calculating the suffix enum is wrong");
  }

  public static String allowedValuesText() {
    final List<String> list = new ArrayList<>();
    for (final MoveSuffixAnnotation suffixEnum : REAL) {
      list.add(suffixEnum.getSuffix());
    }
    return ListUtility.toCommaSeparatedString(list);
  }
}
