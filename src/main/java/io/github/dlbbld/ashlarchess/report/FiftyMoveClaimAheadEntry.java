// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import io.github.dlbbld.ashlarchess.board.enums.Side;

/**
 * One missed 50-move claim-ahead boundary: a move at which the no-progress sequence's halfmove clock would be 99 going
 * into the candidate move, the player had at least one non-zeroing legal move that would have brought clock to 100 and
 * satisfied the FIDE 9.3 claim, but did not play one (the actually-played move was clock-resetting, or the game ended
 * at the boundary).
 *
 * <p>
 * Carries only the boundary's chronological position (sequence start, move count, fullmove number, side having move) -
 * NOT a specific candidate move. Multiple non-zeroing legal moves at the same boundary collapse into a single entry:
 * listing all of them would be 30+ rows for one missed-opportunity move with no informational gain over a single row
 * stating that the opportunity existed. The print layer renders the candidate position as a placeholder token
 * {@code [ahead claim possible]}.
 *
 * <p>
 * Only emitted when the no-progress sequence containing this boundary did not reach clock 100 in the actual played
 * history. Sequences that did reach the threshold appear in {@link FiftyMoveSequenceReport} alone; their would-be
 * claim-aheads are informationally redundant with that row.
 */
record FiftyMoveClaimAheadEntry(SequenceStart sequenceStart, int performedMoveCount, int fullMoveNumber,
    Side sideHavingMove, Side startingSide) {

  public FiftyMoveClaimAheadEntry {
    if (performedMoveCount < 1) {
      throw new IllegalArgumentException("performedMoveCount must be >= 1; was " + performedMoveCount);
    }
    if (fullMoveNumber < 1) {
      throw new IllegalArgumentException("fullMoveNumber must be >= 1; was " + fullMoveNumber);
    }
    if (sideHavingMove == Side.NONE) {
      throw new IllegalArgumentException("sideHavingMove must be WHITE or BLACK");
    }
    if (startingSide == Side.NONE) {
      throw new IllegalArgumentException("startingSide must be WHITE or BLACK");
    }
  }
}
