// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import io.github.dlbbld.ashlarchess.board.MoveNumberFormat;
import io.github.dlbbld.ashlarchess.board.enums.Side;

/**
 * Shared per-player anchor helpers for the 50-move report, used by both the sequence print and the claim-ahead print so
 * the {@code (White/Black)} move-count syntax and the start-marker shape stay consistent between the two sections.
 *
 * <p>
 * A no-progress run's counts are expressed in moves by each player, never halfmoves: at a halfmove clock of {@code c}
 * the starting side has made {@code (c+1)/2} of the run's moves and the other side {@code c/2} (so at an even clock -
 * the 50/50 and 75/75 thresholds - they are equal, and at an odd clock they differ by one).
 */
abstract class SequenceStartFormat {

  /**
   * The start anchor: {@code [Starting position] (W/B)} for an initial-FEN start, else {@code <first move> (W/B)}.
   */
  static String startAnchor(SequenceStart start, Side startingSide) {
    if (start.isInitialFen()) {
      return "[Starting position] " + counts(start.initialClockValue(), startingSide);
    }
    return moveAnchor(start.firstNonZeroingMoveOrThrow(), startingSide);
  }

  /**
   * A played-move anchor: {@code <move> (W/B)}.
   */
  static String moveAnchor(MoveRecord move, Side startingSide) {
    return MoveNumberFormat.calculateMoveNumberAndSanWithSpace(move.fullMoveNumber(), move.movingPiece().getSide(),
        move.san()) + " " + counts(move.halfMoveClock(), startingSide);
  }

  /**
   * The {@code (White/Black)} move counts for a halfmove clock, given the side that started the run.
   */
  static String counts(int clock, Side startingSide) {
    final int starterCount = (clock + 1) / 2;
    final int otherCount = clock / 2;
    final int white = startingSide == Side.WHITE ? starterCount : otherCount;
    final int black = startingSide == Side.BLACK ? starterCount : otherCount;
    return "(" + white + "/" + black + ")";
  }

  /**
   * The side that made the first move of the run. For an after-reset start it is the first non-zeroing move's side. For
   * an initial-FEN start the run began before the loaded position, so it is recovered from the FEN's side to move and
   * clock parity: the FEN's side to move makes run-move {@code initialFenClock + 1} and moves alternate, so the first
   * run-move was made by the FEN's side to move when the clock is even and by the opposite side when odd.
   */
  static Side startingSide(SequenceStart start, int initialFenClock, Side initialFenSideToMove) {
    if (!start.isInitialFen()) {
      return start.firstNonZeroingMoveOrThrow().movingPiece().getSide();
    }
    return initialFenClock % 2 == 0 ? initialFenSideToMove : initialFenSideToMove.getOppositeSide();
  }
}
