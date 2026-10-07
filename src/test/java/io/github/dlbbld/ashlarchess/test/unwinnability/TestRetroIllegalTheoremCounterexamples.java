// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityFullVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityQuickVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnableFullAnalyzer;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnableQuickAnalyzer;
import io.github.dlbbld.ashlarchess.unwinnability.internal.BasicHelpmateExistenceTheorem;
import io.github.dlbbld.ashlarchess.unwinnability.internal.BasicHelpmateExistenceTheoremResult;

/**
 * Documented out-of-domain lock-down: the known retro-illegal counterexamples to the basic-helpmate-existence theorem,
 * as enumerated in that project's README under "Illegal positions not satisfying the conclusion" (3 KBBvK
 * opposite-bishops and 15 KBNvK light-bishop representatives with White to move, plus 1 KBNvK representative with Black
 * to move).
 *
 * <p>
 * These positions are illegal - they cannot arise from the initial position by any series of legal moves (in each, the
 * side to move's opponent had no legal last move) - but only retrograde analysis can tell, so strict FEN parsing
 * accepts them. They are the theorem's known out-of-domain failures: the theorem (a test-side oracle since 22.0.0)
 * still concludes {@code WINNABLE} on them although no helpmate exists. The production analyzers, which since 22.0.0
 * follow the FUN 2022 paper without the theorem shortcut, decide these caged positions by exhausting their tiny
 * position graphs and answer {@code UNWINNABLE} - agreeing with each other and with chasolver even on this illegal
 * input. This test asserts that current behaviour so any drift is caught:
 * <ul>
 * <li>{@code Board.fromFenStrict} accepts each position (retro-illegality is not enforced),</li>
 * <li>the theorem oracle still claims {@code WINNABLE} (the documented counterexample behaviour),</li>
 * <li>the full analyzer answers {@code UNWINNABLE} for White (search exhaustion; no helpmate exists),</li>
 * <li>the quick analyzer answers {@code UNWINNABLE} for both sides (as does chasolver).</li>
 * </ul>
 */
class TestRetroIllegalTheoremCounterexamples {

  /** White to move: KBBvK opposite bishops (3) then KBNvK light bishop (15), in the README table order. */
  private static final List<String> WHITE_TO_MOVE_FENS = createWhiteToMoveFens();

  private static List<String> createWhiteToMoveFens() {
    final List<String> fens = new ArrayList<>();
    fens.add("8/8/8/8/8/B7/B7/k1K5 w - - 0 1");
    fens.add("8/8/8/8/8/B7/B1K5/k7 w - - 0 1");
    fens.add("8/8/8/8/8/8/B1K5/k1B5 w - - 0 1");
    fens.add("8/8/8/8/8/8/B7/k1KN4 w - - 0 1");
    fens.add("8/8/8/8/8/3N4/B7/k1K5 w - - 0 1");
    fens.add("8/8/8/8/2N5/8/B7/k1K5 w - - 0 1");
    fens.add("8/8/8/8/N7/8/B7/k1K5 w - - 0 1");
    fens.add("8/8/8/8/8/8/BN6/k1K5 w - - 0 1");
    fens.add("8/8/8/8/8/8/B1K5/k2N4 w - - 0 1");
    fens.add("8/8/8/8/8/3N4/B1K5/k7 w - - 0 1");
    fens.add("8/8/8/8/2N5/8/B1K5/k7 w - - 0 1");
    fens.add("8/8/8/8/N7/8/B1K5/k7 w - - 0 1");
    fens.add("8/8/8/8/8/8/BNK5/k7 w - - 0 1");
    fens.add("8/8/8/8/8/8/2K5/kB1N4 w - - 0 1");
    fens.add("8/8/8/8/8/3N4/2K5/kB6 w - - 0 1");
    fens.add("8/8/8/8/2N5/8/2K5/kB6 w - - 0 1");
    fens.add("8/8/8/8/N7/8/2K5/kB6 w - - 0 1");
    fens.add("8/8/8/8/8/8/1NK5/kB6 w - - 0 1");
    return fens;
  }

  /** Black to move: the single KBNvK light-bishop representative (Black's king is in check from the bishop). */
  private static final String BLACK_TO_MOVE_FEN = "8/8/8/8/2N5/8/k1K5/1B6 b - - 0 1";

  @SuppressWarnings("static-method")
  @Test
  void testAllRepresentativesShowTheDocumentedOutOfDomainBehaviour() {
    for (final String fen : WHITE_TO_MOVE_FENS) {
      assertCurrentBehaviour(fen);
    }
    assertCurrentBehaviour(BLACK_TO_MOVE_FEN);
  }

  private static void assertCurrentBehaviour(String fen) {
    assertDoesNotThrow(() -> Board.fromFenStrict(fen), fen + ": strict FEN must accept (retro-illegality unenforced)");

    final Board board = Board.fromFenStrict(fen);
    assertEquals(BasicHelpmateExistenceTheoremResult.WINNABLE, BasicHelpmateExistenceTheorem.decide(board, Side.WHITE),
        fen + ": the theorem's documented out-of-domain overclaim");

    assertEquals(UnwinnabilityFullVerdict.UNWINNABLE,
        UnwinnableFullAnalyzer.unwinnableFull(board, Side.WHITE).verdict(), fen);
    assertEquals(UnwinnabilityQuickVerdict.UNWINNABLE,
        UnwinnableQuickAnalyzer.unwinnableQuick(board, Side.WHITE).verdict(), fen);
    assertEquals(UnwinnabilityQuickVerdict.UNWINNABLE,
        UnwinnableQuickAnalyzer.unwinnableQuick(board, Side.BLACK).verdict(), fen);
  }
}
