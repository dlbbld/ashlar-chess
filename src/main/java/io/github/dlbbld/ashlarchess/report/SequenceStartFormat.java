package io.github.dlbbld.ashlarchess.report;

import io.github.dlbbld.ashlarchess.board.HalfMoveUtility;
import io.github.dlbbld.ashlarchess.common.model.HalfMove;

/**
 * Shared formatter for {@link SequenceStart} markers, used by both 50-move print classes so the start-marker syntax
 * stays consistent between the claim-ahead and sequence sections.
 *
 * <p>
 * Renders an initial-FEN start as {@code [Starting position] (N)} where {@code N} is the starting FEN's halfmove clock,
 * and an after-reset start as {@code <ply>.[..] <SAN> (1)} - the first non-zeroing move after a reset, with its
 * post-move halfmove-clock value of {@code 1} in parentheses.
 */
abstract class SequenceStartFormat {

  static String format(SequenceStart start) {
    if (start.isInitialFen()) {
      return "[Starting position] (" + start.initialClockValue() + ")";
    }
    final HalfMove firstMove = start.firstNonZeroingMoveOrThrow();
    return HalfMoveUtility.calculateMoveNumberAndSanWithSpace(firstMove) + " (" + firstMove.halfMoveClock() + ")";
  }
}
