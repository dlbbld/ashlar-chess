// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.enums;

// Outcomes of the chess-rules movement check performed by ChessRuleAnalyzer.
//
// "Movement" here means everything that decides whether a piece can pseudo-legally make a given
// move from its square to another:
// - empty-board geometry of the piece type;
// - path-clearing for long-range pieces;
// - destination-square occupancy;
// - pawn-specific rules (forward one/two, diagonal capture, en passant);
// - king-specific rules other than king-safety (capturing a guarded piece, moving next to the
//   opponent king).
//
// Castling is NOT covered here; it is handled by CastlingCheck. King safety after the move (does
// the move leave/expose the own king to check) is also NOT covered here; that is a separate
// pass.
//
// Preconditions for analysis:
// - the move is not a castling move;
// - the from-square holds an own piece (i.e. spec-coherence already validated).
public enum MovementCheck {
  SUCCESS,

  NOT_POSSIBLE,
  TO_SQUARE_OCCUPIED_BY_OWN_PIECE,
  LONG_RANGE_PIECE_JUMPS_OVER_PIECE,

  PAWN_FORWARD_TWO_SQUARE_JUMP_OVER_SQUARE_ONLY_NOT_EMPTY,
  PAWN_FORWARD_TWO_SQUARE_TO_SQUARE_ONLY_NOT_EMPTY,
  PAWN_FORWARD_TWO_SQUARE_BOTH_SQUARE_NOT_EMPTY,

  PAWN_FORWARD_ONE_SQUARE_TO_SQUARE_NOT_EMPTY_OWN_PIECE,
  PAWN_FORWARD_ONE_SQUARE_TO_SQUARE_NOT_EMPTY_OPPONENT_PIECE,

  PAWN_DIAGONAL_OWN_PIECE,

  PAWN_EN_PASSANT_WRONG_RANK,
  PAWN_EN_PASSANT_NO_IMMEDIATE_BEFORE_TWO_SQUARE_ADVANCE,

  KING_CAPTURES_GUARDED_PIECE,
  KING_MOVES_NEXT_TO_OPPONENT_KING,
  KING_MOVES_TO_ATTACKED_EMPTY_SQUARE;
}
