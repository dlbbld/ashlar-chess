// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import io.github.dlbbld.ashlarchess.board.HalfMoveUtility;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.model.HalfMove;

abstract class FiftyMoveSequencePrint {

  /**
   * Renders the 50-move-sequence report as one line per sequence, anchored at the start, each threshold crossed, and
   * the end, each tagged with {@code (White/Black)} move counts since the last capture or pawn move. Format:
   *
   * <pre>
   *   &lt;start&gt; (W/B) - &lt;50-move threshold&gt; (50/50) [- &lt;75-move threshold&gt; (75/75)] - &lt;end&gt; (W/B)
   * </pre>
   *
   * <p>
   * The threshold anchors are the moves at which the clock reached 100 / 150 - where the 50-move claim becomes
   * available and the 75-move rule forces a draw. The span from the 50/50 anchor to the end is the window of claimable
   * draws. Anchors are de-duplicated by clock, so a threshold that coincides with the end (or with an at-threshold
   * initial-FEN start) renders once. The start marker is {@code [Starting position]} for an initial-FEN start, or the
   * first non-zeroing move otherwise; an initial-FEN start with no played continuation renders as the start alone.
   */
  static List<List<String>> render(FiftyMoveSequenceReport report) {
    final List<List<String>> resultListList = new ArrayList<>();
    for (final FiftyMoveSequence sequence : report.sequences()) {
      resultListList.add(renderSequence(sequence));
    }
    return resultListList;
  }

  private static List<String> renderSequence(FiftyMoveSequence sequence) {
    final Map<Integer, String> labelByClock = new LinkedHashMap<>();
    labelByClock.putIfAbsent(Integer.valueOf(startClock(sequence.start())), startLabel(sequence.start()));
    addPlyAnchor(labelByClock, sequence.fiftyMoveThresholdPly());
    addPlyAnchor(labelByClock, sequence.seventyFiveMoveThresholdPly());
    addPlyAnchor(labelByClock, sequence.endPly());

    final List<String> tokens = new ArrayList<>();
    for (final Map.Entry<Integer, String> anchor : labelByClock.entrySet()) {
      if (!tokens.isEmpty()) {
        tokens.add("-");
      }
      tokens.add(anchor.getValue() + " " + counts(anchor.getKey().intValue(), sequence.startingSide()));
    }
    return tokens;
  }

  private static void addPlyAnchor(Map<Integer, String> labelByClock, @Nullable HalfMove ply) {
    if (ply != null) {
      labelByClock.putIfAbsent(Integer.valueOf(ply.halfMoveClock()),
          HalfMoveUtility.calculateMoveNumberAndSanWithSpace(ply));
    }
  }

  private static int startClock(SequenceStart start) {
    return start.isInitialFen() ? start.initialClockValue() : start.firstNonZeroingMoveOrThrow().halfMoveClock();
  }

  private static String startLabel(SequenceStart start) {
    if (start.isInitialFen()) {
      return "[Starting position]";
    }
    return HalfMoveUtility.calculateMoveNumberAndSanWithSpace(start.firstNonZeroingMoveOrThrow());
  }

  /**
   * Splits a halfmove clock into {@code (White/Black)} move counts. The starting side made {@code (clock+1)/2} of the
   * run's moves, the other side {@code clock/2}; at a threshold (even clock) they are equal, at start/end (odd clock)
   * they differ by one.
   */
  private static String counts(int clock, Side startingSide) {
    final int starterCount = (clock + 1) / 2;
    final int otherCount = clock / 2;
    final int white = startingSide == Side.WHITE ? starterCount : otherCount;
    final int black = startingSide == Side.BLACK ? starterCount : otherCount;
    return "(" + white + "/" + black + ")";
  }
}
