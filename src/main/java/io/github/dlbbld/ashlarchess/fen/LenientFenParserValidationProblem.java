// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.fen;

/**
 * Top-level outcome categories from the lenient FEN parser. The richer downstream classifiers
 * ({@link io.github.dlbbld.ashlarchess.common.enums.StrictFenSemanticValidationProblem}) are surfaced separately on the
 * validation result so callers can switch on the specific failure mode without parsing the message.
 */
public enum LenientFenParserValidationProblem {

  /** Lenient parse succeeded; any tolerated deviations are on the result's forgiven-items list. */
  OK,

  /**
   * Input could not be normalised into something parseable as FEN - e.g. empty, blank, or contains too few fields after
   * lenient normalisation. This is the failure mode for inputs that are not recognisably FEN at all.
   */
  UNRECOVERABLE,

  /**
   * Lenient normalisation produced a six-field FEN but strict field parsing rejected it (format failure after
   * normalisation). Rare - the lenient layer's normalisation pipeline should not produce field-invalid output.
   * Surfaces when the input contains characters or shapes the normaliser does not understand.
   */
  FIELD_INVALID,

  /**
   * The normalised FEN passed lexical parsing but strict validation rejected it for a structural or rule-consistency
   * issue (piece counts, kings, castling rights inconsistent with piece placement, illegal en-passant target, etc.).
   * The lenient layer does not forgive semantic invariants - a FEN with a king missing still fails. The
   * underlying {@link io.github.dlbbld.ashlarchess.common.enums.StrictFenSemanticValidationProblem} is on the result.
   */
  STRICT_SEMANTIC_INVALID,

  /** Unexpected runtime error caught during validation. Carries the original message verbatim. */
  UNKNOWN_ERROR,

}
