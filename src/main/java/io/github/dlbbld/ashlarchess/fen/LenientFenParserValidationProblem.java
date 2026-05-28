package io.github.dlbbld.ashlarchess.fen;

/**
 * Top-level outcome categories from the lenient FEN parser. The richer downstream classifiers
 * ({@link io.github.dlbbld.ashlarchess.common.enums.FenAdvancedValidationProblem}) are surfaced separately on the
 * validation result so callers can switch on the specific failure mode without parsing the message.
 */
public enum LenientFenParserValidationProblem {

  /** Lenient parse succeeded; any tolerated deviations are on the result's forgiven-items list. */
  OK,

  /**
   * Input could not be normalised into something parseable by {@link FenParserRaw} - e.g. empty, blank, or contains too
   * few fields after lenient normalisation to be a FEN. This is the failure mode for inputs that are not recognisably
   * FEN at all.
   */
  UNRECOVERABLE,

  /**
   * Lenient normalisation produced a six-field FEN but {@link FenParserRaw} rejected it (lexical-format failure after
   * normalisation). Rare - the lenient layer's normalisation pipeline should not produce raw-invalid output. Surfaces
   * when the input contains characters or shapes the normaliser does not understand.
   */
  RAW_INVALID,

  /**
   * The normalised FEN passed raw parsing but {@link FenParserAdvanced} rejected it for a structural or
   * rule-consistency issue (piece counts, kings, castling rights inconsistent with piece placement, illegal en-passant
   * target, etc.). The lenient layer does not forgive semantic invariants - a FEN with a king missing still fails. The
   * underlying {@link io.github.dlbbld.ashlarchess.common.enums.FenAdvancedValidationProblem} is on the result.
   */
  ADVANCED_INVALID,

  /** Unexpected runtime error caught during validation. Carries the original message verbatim. */
  UNKNOWN_ERROR,

}
