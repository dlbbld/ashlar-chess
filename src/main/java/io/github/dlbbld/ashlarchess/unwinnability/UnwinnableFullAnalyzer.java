// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.fen.model.Fen;
import io.github.dlbbld.ashlarchess.internal.Nulls;

// Figure 9 Main routine for deciding chess unwinnability: the semi-static algorithm (Figure 8)
// as a fast shortcut, then the Find-Helpmate search (Figure 5) under iterative deepening. The
// routine is sound on every definite verdict and, given large enough limits, complete; with the
// finite budget below it may return UNDETERMINED.
/**
 * The complete unwinnability analysis - the paper's Figure 9 main routine.
 *
 * <p>
 * Defined for legal positions only; on an illegal position the result is undefined (and may differ from the quick
 * analyzer). See this package's documentation for the legal-position contract.
 */
public final class UnwinnableFullAnalyzer {

  private UnwinnableFullAnalyzer() {
  }

  // The paper leaves bound(d) as a parameter (its practical note fixes a small constant); ashlar keeps the 21.x
  // budget envelope: one global node budget shared by all deepening iterations, and a depth ceiling.
  private static final int MAX_DEPTH = 100;
  private static final int GLOBAL_NODES_BOUND = 500000;

  /**
   * Runs the algorithm on a fresh history-less board built from the caller's FEN. The caller's board is not mutated,
   * and repetition history from the caller's game is intentionally ignored.
   *
   * <p>
   * Terminal positions are handled, not rejected (Figure 5 base cases): an already-checkmate position is
   * {@code WINNABLE} for the side that delivered mate - a zero-move helpmate, so
   * {@link UnwinnabilityFullAnalysis#mateLine()} is empty - and {@code UNWINNABLE} for the mated side; a stalemate is
   * {@code UNWINNABLE} for both sides.
   */
  public static UnwinnabilityFullAnalysis unwinnableFull(Board input, Side winner) {
    final Board board = copyCurrentPositionForFullSearch(input);

    // 1: if UnwinnableSS(pos, c, Mobility(pos)) then return Unwinnable
    final SemiStaticPosition semiStaticPosition = SemiStaticPosition.fromBoard(board);
    if (UnwinnableSemiStatic.unwinnableSemiStatic(semiStaticPosition, winner,
        Mobility.mobility(semiStaticPosition))) {
      return new UnwinnabilityFullAnalysis(UnwinnabilityFullVerdict.UNWINNABLE, Nulls.listOf());
    }

    // 2: for every d in N do (-> iterative deepening). The transposition table is per-iteration - see FindHelpmate
    // for why sharing it across iterations could produce a false UNWINNABLE.
    int remainingNodes = GLOBAL_NODES_BOUND;
    for (int maxDepth = 0; maxDepth <= MAX_DEPTH; maxDepth++) {
      // 3: set b_d = Find-Helpmate_c(pos, 0, maxDepth = d), with the iteration's node bound being what is left of
      // the global budget.
      final FindHelpmate findHelpmate = new FindHelpmate(winner, remainingNodes);
      final HelpmateSearchResult searchResult = findHelpmate.search(board, maxDepth);
      remainingNodes -= searchResult.nodesUsed();

      // 4: if b_d = true then return Winnable
      if (searchResult.helpmateFound()) {
        return new UnwinnabilityFullAnalysis(UnwinnabilityFullVerdict.WINNABLE, searchResult.mateLine());
      }
      // 5-6: if the search was not interrupted then return Unwinnable
      if (!searchResult.interrupted()) {
        return new UnwinnabilityFullAnalysis(UnwinnabilityFullVerdict.UNWINNABLE, Nulls.listOf());
      }
      if (remainingNodes <= 0) {
        break; // global budget exhausted
      }
    }

    // Budget or depth ceiling reached without a definite verdict.
    return new UnwinnabilityFullAnalysis(UnwinnabilityFullVerdict.UNDETERMINED, Nulls.listOf());
  }

  private static Board copyCurrentPositionForFullSearch(Board input) {
    final Fen fen = new Fen(input.getFen(), input.getBitboardPosition(), input.getSideToMove(),
        input.getCastlingRightWhite(), input.getCastlingRightBlack(), input.getEnPassantCaptureTargetSquare(), 0,
        input.getFullMoveNumber());
    return new Board(fen);
  }
}
