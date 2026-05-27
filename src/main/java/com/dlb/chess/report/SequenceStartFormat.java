package com.dlb.chess.report;

import com.dlb.chess.board.HalfMoveUtility;
import com.dlb.chess.common.model.HalfMove;

/**
 * Shared formatter for {@link SequenceStart} markers, used by both 50-move print classes so the start-marker syntax
 * stays consistent between the claim-ahead and sequence sections.
 *
 * <p>
 * Renders {@link InitialFenStart} as {@code [Starting position] (N)} where {@code N} is the starting FEN's halfmove
 * clock, and {@link AfterResetStart} as {@code <ply>.[..] <SAN> (1)} — the first non-zeroing move after a reset, with
 * its post-move halfmove-clock value of {@code 1} in parentheses.
 */
abstract class SequenceStartFormat {

  static String format(SequenceStart start) {
    if (start instanceof InitialFenStart initialFenStart) {
      return "[Starting position] (" + initialFenStart.initialClockValue() + ")";
    }
    final AfterResetStart afterResetStart = (AfterResetStart) start;
    final HalfMove firstMove = afterResetStart.firstNonZeroingMove();
    return HalfMoveUtility.calculateMoveNumberAndSanWithSpace(firstMove) + " (" + firstMove.halfMoveClock() + ")";
  }
}
