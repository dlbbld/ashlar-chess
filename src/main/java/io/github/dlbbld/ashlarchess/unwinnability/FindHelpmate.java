// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.LegalMove;
import io.github.dlbbld.ashlarchess.board.MoveSpecification;
import io.github.dlbbld.ashlarchess.board.UciMove;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.internal.UciMoveUtility;
import io.github.dlbbld.ashlarchess.internal.Nulls;

// Figure 5 Find-Helpmate: a recursive routine for finding a checkmate by the intended winner.
// Footnote b: a Normal-scored move following a Reward-scored move is also rewarded (chains
// rewarded plans across both players).
/**
 * The Find-Helpmate search (paper Figure 5): a depth- and node-bounded DFS that succeeds iff it exhibits a checkmate
 * <em>by the intended winner</em> (a helpmate), with a depth-aware transposition table and the Figure 12 {@link Score}
 * depth heuristic. It runs over the mutable {@link HelpmateSearchBoard} hot path (make/unmake, per-depth legal-move
 * buffers, cached terminal flags). As an ashlar extension the exhibited mate line is recorded on the unwind - pure
 * bookkeeping, no influence on the search.
 *
 * <p>
 * One instance = one bounded search (one iterative-deepening iteration of Figure 9), exactly the paper's Figure 5
 * semantics. The transposition table is deliberately NOT shared across iterations, although the paper's prose allows
 * it: the soundness of "tree exhausted without interrupt implies UNWINNABLE" rests on the interrupted flag being
 * monotone over every visit the table's entries summarize. Within one iteration that holds - an entry written by a
 * depth-cut visit coexists with the raised flag, so that iteration can no longer claim {@code UNWINNABLE}. A stale
 * entry from an earlier, depth-cut iteration would prune a later iteration's node WITHOUT re-raising its interrupt
 * flag, letting the later iteration believe it exhausted the tree while cut lines hide behind the prune - a potential
 * false {@code UNWINNABLE}.
 */
final class FindHelpmate {

  private final Side winnerSide;
  private final Side loserSide;
  private final int nodesBound;
  private final Map<TranspositionKey, Integer> transpositionTable = new HashMap<>();
  private final Deque<UciMove> mateLine = new ArrayDeque<>();
  private int nodesUsed;
  private boolean interrupted;

  FindHelpmate(Side winner, int nodesBound) {
    this.winnerSide = winner;
    this.loserSide = winner.getOppositeSide();
    this.nodesBound = nodesBound;
  }

  /** Base call: one bounded search from the search board's current position (Figure 9 step 3). */
  HelpmateSearchResult search(HelpmateSearchBoard board, int maxDepth) {
    final boolean helpmateFound = search(board, 0, maxDepth, false);
    return new HelpmateSearchResult(helpmateFound, interrupted, nodesUsed, Nulls.copyOfList(new ArrayList<>(mateLine)));
  }

  private boolean search(HelpmateSearchBoard board, int depth, int maxDepth, boolean previousWasReward) {
    // Steps 1-2: terminal positions. Checkmate of the loser is the helpmate; stalemate and the Lemma 5/6
    // insufficient-winning-material positions (and the bare winner king) are leaves without one.
    if (board.isCheckmate()) {
      return board.getSideToMove() == loserSide;
    }
    final BitboardPosition placement = board.getBitboardPosition();
    if (board.isStalemate() || MaterialLemmas.winnerHasBareKing(placement, winnerSide)
        || MaterialLemmas.unwinnableByLemma5Or6(placement, winnerSide)) {
      return false;
    }

    // Steps 3-4: search limits.
    nodesUsed++;
    final int remainingBudget = maxDepth - depth;
    if (nodesUsed > nodesBound || remainingBudget < 0) {
      interrupted = true;
      return false;
    }

    // Steps 5-6: depth-aware transposition table - skip a position already searched with at least this budget. The
    // reward-chain flag is part of the key: a visit with the footnote-b boost pending explores a strictly stronger
    // budget shape than one without, so the two states must not prune each other.
    final TranspositionKey key = new TranspositionKey(board.currentTranspositionKey(), previousWasReward);
    // Stored budgets are always >= 0, so -1 is a safe "absent" sentinel (remainingBudget is >= 0 here).
    final int seenBudget = Nulls.getOrDefault(transpositionTable, key, -1);
    if (seenBudget >= remainingBudget) {
      return false;
    }
    transpositionTable.put(key, remainingBudget);

    // Steps 7-9: explore every legal move, adjusting the depth budget by Score. Figure 5 footnote b: also reward a
    // Normal-scored move when the preceding move's score was Reward. The per-depth legal-move buffer stays intact
    // while the recursion runs in deeper buffers, so indexed iteration over it is safe.
    final Side movingSide = board.getSideToMove();
    final List<LegalMove> legalMoves = board.getLegalMoves();
    final int totalLegalMoves = legalMoves.size();
    for (int i = 0; i < totalLegalMoves; i++) {
      final MoveSpecification move = Nulls.get(legalMoves, i).moveSpecification();
      final int score = Score.increment(placement, movingSide, move, winnerSide);
      final int increment = score == 0 && previousWasReward ? 1 : score;
      board.move(move);
      final boolean helpmateFound = search(board, depth + 1, maxDepth + increment, score == 1);
      board.unmove();
      if (helpmateFound) {
        mateLine.addFirst(UciMoveUtility.toUci(movingSide, move));
        return true;
      }
    }
    return false; // step 10
  }

  /** Search-state identity for the transposition table (exact position key + reward-chain state). */
  private record TranspositionKey(HelpmateSearchKey positionKey, boolean previousWasReward) {
  }
}
