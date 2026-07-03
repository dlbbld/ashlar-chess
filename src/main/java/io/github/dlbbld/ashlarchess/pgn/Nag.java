// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.pgn;

/**
 * A Numeric Annotation Glyph (NAG) - the PGN standard's move annotation (spec section 8.2.4), a code in the range
 * {@code 0..255}. NAGs are the single, uniform representation of move annotations in ashlar's PGN model: the six
 * symbolic suffix glyphs the import format allows ({@code !} {@code ?} {@code !!} {@code ??} {@code !?} {@code ?!})
 * are shorthand for codes {@code 1..6} and are folded into NAGs on parse - {@code ?} <em>is</em> {@code $2}. Codes
 * outside {@code 1..6} (positional, time-pressure, and the like - e.g. chess.com's {@code $9}) have no glyph shorthand
 * and are carried as NAGs only. See {@link MoveSuffixAnnotation} for the glyph-to-code bridge.
 */
public record Nag(int code) {

  public Nag {
    if (code < 0 || code > 255) {
      throw new IllegalArgumentException("A NAG code must be in the range 0..255, but was " + code + ".");
    }
  }

  /** The PGN token form of this NAG, e.g. {@code "$2"}. */
  public String toToken() {
    return "$" + code;
  }
}
