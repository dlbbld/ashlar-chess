package com.dlb.chess.san;

/**
 * Typed diagnostic codes emitted by the lenient SAN parser when it accepts an input that the strict parser rejects.
 *
 * <p>
 * Each code names a specific, individually-detectable deviation from canonical SAN. A single move can carry multiple
 * forgiven items (e.g. {@code nbxd7+} when actually mate triggers {@link #LOWERCASE_PIECE_LETTER},
 * {@link #OVERSPECIFIED_FILE_DISAMBIGUATION}, and {@link #WRONG_CHECK_SUFFIX_FOR_CHECKMATE} simultaneously). The
 * lenient parser surfaces every applicable code; consumers decide whether to silently accept or warn.
 *
 * <p>
 * Codes are grouped by category. The grouping has no runtime meaning - it is for human readability only.
 */
public enum LenientSanValidationProblem {

  // --- Decorative suffix mismatches: trailing +/# does not match actual board state after the move ---
  MISSING_CHECK_SUFFIX,
  MISSING_CHECKMATE_SUFFIX,
  SPURIOUS_CHECK_SUFFIX,
  SPURIOUS_CHECKMATE_SUFFIX,
  WRONG_CHECK_SUFFIX_FOR_CHECKMATE,
  WRONG_CHECKMATE_SUFFIX_FOR_CHECK,

  // --- Capture marker mismatches: 'x' present without capture, or absent when capture occurs ---
  MISSING_CAPTURE_MARKER,
  SPURIOUS_CAPTURE_MARKER,

  // --- Over-specification: more disambiguation than chess rules require ---
  OVERSPECIFIED_FILE_DISAMBIGUATION,
  OVERSPECIFIED_RANK_DISAMBIGUATION,
  OVERSPECIFIED_SQUARE_DISAMBIGUATION,

  // --- Non-canonical disambiguation form: user picked rank disambig where canonical SAN prefers file disambig.
  // Distinct from OVERSPECIFIED_RANK_DISAMBIGUATION (which means no disambig was needed at all). Here a disambig IS
  // required, but the canonical form uses file rather than rank. ---
  NON_STANDARD_RANK_DISAMBIGUATION,

  // --- Notation form: alternative move syntaxes that uniquely identify the same legal move ---
  LONG_ALGEBRAIC_NOTATION,
  UCI_NOTATION,
  ZERO_INSTEAD_OF_O_CASTLING,
  EXPLICIT_PAWN_LETTER,

  // --- Promotion form ---
  MISSING_PROMOTION_EQUALS,

  // --- Case variation: SAN's case conventions are asymmetric (pieces uppercase, files lowercase, capture-marker
  // lowercase, promotion piece uppercase). These codes name each direction of deviation. Canonical-case interpretation
  // always wins when ambiguous (e.g. "bxc6" stays a pawn capture even if a bishop on the b-file could also capture).
  // ---
  LOWERCASE_PIECE_LETTER,
  UPPERCASE_FILE_LETTER,
  UPPERCASE_CAPTURE_MARKER,
  LOWERCASE_PROMOTION_PIECE;

}
