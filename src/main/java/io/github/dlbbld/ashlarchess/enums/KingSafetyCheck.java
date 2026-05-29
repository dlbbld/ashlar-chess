// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.enums;

import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;

// Outcomes of the king-safety check performed by ChessRuleAnalyzer for non-king moves.
//
// "King safety" here means: after the move is made, is the own king attacked?
//
// For king moves, the analyzer's analyzeKingSafety early-returns SUCCESS - king-attack-after-move
// is a movement question for the king (handled by analyzeMovement via KING_CAPTURES_GUARDED_PIECE
// and KING_MOVES_TO_ATTACKED_EMPTY_SQUARE). The was-in-check vs not-in-check distinction is
// only meaningful for non-king pieces, where it tracks two different mechanics (failure to
// interpose-or-capture vs discovery-pin), so the king-* failure values are not part of this enum.
public enum KingSafetyCheck {
  SUCCESS,

  NON_KING_LEFT_IN_CHECK,
  NON_KING_EXPOSED_TO_CHECK;

  // Translator to the broader MoveCheck enum, used when a KingSafetyCheck failure is surfaced
  // through InvalidMoveException for MoveCheck public-API stability.
  public MoveCheck toMoveCheck() {
    return switch (this) {
      case NON_KING_LEFT_IN_CHECK -> MoveCheck.ALL_BUT_KING_KING_LEFT_IN_CHECK;
      case NON_KING_EXPOSED_TO_CHECK -> MoveCheck.ALL_BUT_KING_KING_EXPOSED_TO_CHECK;
      case SUCCESS -> throw new ProgrammingMistakeException("SUCCESS is not a king-safety failure");
    };
  }
}
