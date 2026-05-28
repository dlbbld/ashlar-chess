package com.dlb.chess.model;

/**
 * Categorises a {@link LegalMove} produced by the rule pipeline. The five values are mutually exclusive - a single
 * legal move cannot, for example, be both a castling move and a pawn two-square advance. The move pipeline knows the
 * category at the moment it constructs the {@code LegalMove}, so the kind is stored as a field rather than recomputed
 * by consumers from {@link com.dlb.chess.common.model.MoveSpecification} fields.
 *
 * <ul>
 * <li>{@link #NORMAL} - any non-pawn move that is not castling, and any pawn move that is neither an en-passant
 * capture, nor a two-square advance, nor a promotion (i.e. a one-square advance or a regular diagonal capture).</li>
 * <li>{@link #CASTLING} - king-and-rook castling, either king-side or queen-side.</li>
 * <li>{@link #EN_PASSANT_CAPTURE} - a pawn captures an opponent pawn that just made a two-square advance; the captured
 * pawn is not on the destination square.</li>
 * <li>{@link #PAWN_TWO_SQUARE_ADVANCE} - a pawn's initial two-square advance, which creates an en-passant capture
 * target on the square the pawn skipped over. The opponent may capture en passant on its next move only.</li>
 * <li>{@link #PROMOTION} - a pawn reaching its eighth rank, promoting to a queen, rook, bishop, or knight. May or may
 * not also be a capture.</li>
 * </ul>
 */
public enum LegalMoveKind {
  NORMAL,
  CASTLING,
  EN_PASSANT_CAPTURE,
  PAWN_TWO_SQUARE_ADVANCE,
  PROMOTION;
}
