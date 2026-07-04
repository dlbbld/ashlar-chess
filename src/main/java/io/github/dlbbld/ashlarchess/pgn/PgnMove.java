// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.pgn;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import io.github.dlbbld.ashlarchess.internal.Nulls;

/**
 * One movetext move: its SAN, the numeric annotation glyphs (NAGs) attached to it, and its trailing commentary.
 *
 * <p>
 * Annotations are modelled uniformly as {@link Nag}s (see that type). The symbolic suffix glyphs the import format
 * allows ({@code !} {@code ?} ...) are folded into {@code nags} on parse - {@code e4?} and {@code e4 $2} both yield a
 * single {@code Nag(2)}. {@link #moveSuffixAnnotation()} is a convenience that reads back the first assessment NAG
 * ({@code 1..6}) as its glyph; it is derived from {@code nags}, not a separate stored field.
 */
@SuppressWarnings("null")
public record PgnMove(@NonNull String san, @NonNull List<@NonNull Nag> nags, @NonNull PgnCommentary commentary) {

  public PgnMove {
    nags = Nulls.copyOfList(nags);
  }

  /**
   * The first move-assessment NAG ({@code 1..6}) on this move, as its symbolic glyph, or
   * {@link MoveSuffixAnnotation#NONE} if the move carries no assessment glyph. A convenience over {@link #nags()} for
   * the common "was this a blunder?" question; positional and other non-glyph NAGs are visible only through
   * {@link #nags()}.
   */
  public MoveSuffixAnnotation moveSuffixAnnotation() {
    for (final Nag nag : nags) {
      final MoveSuffixAnnotation suffix = MoveSuffixAnnotation.fromNagCode(nag.code());
      if (suffix != null) {
        return suffix;
      }
    }
    return MoveSuffixAnnotation.NONE;
  }
}
