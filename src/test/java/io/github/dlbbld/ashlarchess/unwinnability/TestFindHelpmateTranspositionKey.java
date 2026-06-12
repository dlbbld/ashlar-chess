// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;

/**
 * Verifies the helpmate-search transposition-key behaviour now exposed via {@link Board#getDynamicPosition()}.
 *
 * <p>
 * The helpmate search keys its visited-position map on {@code DynamicPosition}. The properties tested here are the ones
 * that distinguish {@code DynamicPosition} from a raw FEN-equality test: the halfmove clock and fullmove number are not
 * part of position identity, and the en-passant target square is normalised away when no opposing pawn can actually
 * capture there.
 */
class TestFindHelpmateTranspositionKey {

  @SuppressWarnings("static-method")
  @Test
  void testIgnoresMoveCounters() {
    final Board boardFirst = new Board("8/8/8/8/8/8/8/K6k w - - 0 1");
    final Board boardSecond = new Board("8/8/8/8/8/8/8/K6k w - - 10 9");

    assertEquals(boardFirst.getDynamicPosition(), boardSecond.getDynamicPosition());
  }

  @SuppressWarnings("static-method")
  @Test
  void testIgnoresUncapturableEnPassantTarget() {
    final Board boardWithTarget = new Board("8/8/8/8/4P3/8/8/K6k b - e3 0 1");
    final Board boardWithoutTarget = new Board("8/8/8/8/4P3/8/8/K6k b - - 0 1");

    assertEquals(boardWithTarget.getDynamicPosition(), boardWithoutTarget.getDynamicPosition());
  }

  @SuppressWarnings("static-method")
  @Test
  void testKeepsCapturableEnPassantTarget() {
    final Board boardWithTarget = new Board("8/8/8/8/3pP3/8/8/K6k b - e3 0 1");
    final Board boardWithoutTarget = new Board("8/8/8/8/3pP3/8/8/K6k b - - 0 1");

    assertNotEquals(boardWithTarget.getDynamicPosition(), boardWithoutTarget.getDynamicPosition());
  }

  @SuppressWarnings("static-method")
  @Test
  void testNormalizesPinnedEnPassantCapturer() {
    // Black king on a4, White rook on h4. Black's d4 pawn is adjacent to the just-double-stepped
    // e4 White pawn, so a looser "is there an opposing pawn next door?" check would keep e3 as the
    // e.p. target. But the d4 pawn is pinned along rank 4: capturing e.p. (d4xe3) would clear both
    // d4 and e4 from rank 4, exposing the Black king on a4 to the White rook on h4. The capture is
    // illegal, so the king-safety-aware normalization correctly drops the e.p. target to NONE -
    // collapsing this position into the same DynamicPosition as one with no e.p. target at all.
    final Board boardWithPinnedCapturer = new Board("8/8/8/8/k2pP2R/8/8/7K b - e3 0 1");
    final Board boardWithoutTarget = new Board("8/8/8/8/k2pP2R/8/8/7K b - - 0 1");

    assertEquals(boardWithPinnedCapturer.getDynamicPosition(), boardWithoutTarget.getDynamicPosition());
  }
}
