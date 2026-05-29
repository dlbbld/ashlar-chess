// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.fen;

/**
 * FEN-level deviations the lenient FEN parser accepts. Surfaced on the validation result so consumers can see what the
 * parser tolerated without rejecting. None of these alter the semantic content of the position - they are purely
 * syntactic-tolerance transformations applied before delegating to {@link FenParserRaw} / {@link FenParserAdvanced}.
 */
public enum ForgivenFenItemCode {

  /** Leading whitespace stripped from the input. */
  LEADING_WHITESPACE,

  /** Trailing whitespace stripped from the input. */
  TRAILING_WHITESPACE,

  /** Two or more consecutive spaces between fields collapsed to a single space. */
  EXTRA_WHITESPACE_BETWEEN_FIELDS,

  /** Tab and/or newline used as inter-field separator (common in engine output) normalised to single space. */
  TAB_OR_NEWLINE_AS_SEPARATOR,

  /**
   * Four-field FEN - half-move clock and full-move number both absent (common in engine output, e.g. Stockfish UCI
   * {@code position fen ...}). Defaulted to {@code 0 1}.
   */
  MISSING_HALFMOVE_AND_FULLMOVE,

  /**
   * Five-field FEN - full-move number absent but half-move clock present. Defaulted to {@code 1} for the missing
   * full-move number.
   */
  MISSING_FULLMOVE_NUMBER,

  /**
   * Side-to-move letter in uppercase ({@code W} or {@code B}) normalised to lowercase.
   */
  UPPERCASE_SIDE_TO_MOVE,

  /**
   * Castling-rights letters present in non-canonical order (e.g. {@code QKqk}, {@code kqKQ}). Reordered to canonical
   * {@code KQkq} subset.
   */
  CASTLING_NON_CANONICAL_ORDER,

  /**
   * En-passant target field uses a non-ASCII dash (em-dash, en-dash, etc.) instead of {@code -}. Normalised.
   */
  EN_PASSANT_NON_STANDARD_DASH,

  /**
   * En-passant target square in uppercase (e.g. {@code E3}) normalised to lowercase.
   */
  EN_PASSANT_UPPERCASE,

  /**
   * More than six fields in the input. The first six are kept; the trailing field(s) are dropped. The detail carries
   * the dropped fragment.
   */
  TRAILING_GARBAGE_TOKEN,

  /**
   * Half-move clock and full-move number are inconsistent: a FEN like {@code ... 15 1} claims 15 half-moves have been
   * played but the full-move counter is still at 1. Physically impossible in a single chess game. The lenient parser
   * auto-corrects by bumping {@code fullMoveNumber} up to {@code halfMoveClock} rounded up to the next multiple of ten
   * - {@code halfMoveClock = 15} gives {@code fullMoveNumber = 20}, {@code halfMoveClock = 2} gives
   * {@code fullMoveNumber = 10}. The round-numbered result is well above the strict minimum and signals to a reader
   * that the value was reconstructed rather than measured. Strict parsing rejects (see
   * {@code FenAdvancedValidationProblem.INVALID_HALF_MOVE_CLOCK_TOO_BIG_RELATIVE_TO_FULL_MOVE_NUMBER}).
   */
  HALF_MOVE_CLOCK_INCONSISTENT_WITH_FULL_MOVE_NUMBER,

}
