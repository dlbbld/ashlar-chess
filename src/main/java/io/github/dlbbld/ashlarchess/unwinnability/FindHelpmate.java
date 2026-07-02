// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.MoveSpecification;
import io.github.dlbbld.ashlarchess.board.UciMove;
import io.github.dlbbld.ashlarchess.board.enums.CastlingRight;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.board.internal.UciMoveUtility;
import io.github.dlbbld.ashlarchess.internal.Nulls;

// Figure 5 Find-Helpmate: a recursive routine for finding a checkmate by the intended winner.
// Footnote b: a Normal-scored move following a Reward-scored move is also rewarded (chains
// rewarded plans across both players).
/**
 * The Find-Helpmate search (paper Figure 5): a depth- and node-bounded DFS that succeeds iff it exhibits a checkmate
 * <em>by the intended winner</em> (a helpmate), with a depth-aware transposition table and the Figure 12
 * {@link Score} depth heuristic. As an ashlar extension the exhibited mate line is recorded on the unwind - pure
 * bookkeeping, no influence on the search.
 *
 * <p>
 * One instance = one bounded search (one iterative-deepening iteration of Figure 9). The transposition table is not
 * shared across iterations: after a node-bound interrupt, entries may cover a truncated subtree, and reusing them in
 * a later iteration could prune it into an unsound {@code UNWINNABLE}.
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

  /** Base call: one bounded search from the board's current position (Figure 9 step 3). */
  HelpmateSearchResult search(Board board, int maxDepth) {
    final boolean helpmateFound = search(board, 0, maxDepth, false);
    return new HelpmateSearchResult(helpmateFound, interrupted, nodesUsed,
        Nulls.copyOfList(new ArrayList<>(mateLine)));
  }

  private boolean search(Board board, int depth, int maxDepth, boolean previousWasReward) {
    final BitboardPosition placement = board.getBitboardPosition();

    // Steps 1-2: terminal positions. Checkmate of the loser is the helpmate; stalemate and the Lemma 5/6
    // insufficient-winning-material positions (and the bare winner king) are leaves without one.
    if (board.isCheckmate()) {
      return board.getSideToMove() == loserSide;
    }
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

    // Steps 5-6: depth-aware transposition table - skip a position already searched with at least this budget.
    final TranspositionKey key = keyOf(board, placement);
    final Integer seenBudget = transpositionTable.get(key);
    if (seenBudget != null && seenBudget >= remainingBudget) {
      return false;
    }
    transpositionTable.put(key, remainingBudget);

    // Steps 7-9: explore every legal move, adjusting the depth budget by Score. Figure 5 footnote b: also reward a
    // Normal-scored move when the preceding move's score was Reward.
    for (final MoveSpecification move : board.getLegalMoveSpecifications()) {
      final int score = Score.increment(board, move, winnerSide);
      final int increment = score == 0 && previousWasReward ? 1 : score;
      final Side movingSide = board.getSideToMove();
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

  private static TranspositionKey keyOf(Board board, BitboardPosition placement) {
    final Square enPassant = board.isEnPassantCapturePossible() ? board.getEnPassantCaptureTargetSquare()
        : Square.NONE;
    return new TranspositionKey(placement, board.getSideToMove(), board.getCastlingRightWhite(),
        board.getCastlingRightBlack(), enPassant);
  }

  /** Position identity for the transposition table (piece placement + game state). */
  private record TranspositionKey(BitboardPosition placement, Side sideToMove, CastlingRight castlingRightWhite,
      CastlingRight castlingRightBlack, Square enPassantCaptureTargetSquare) {
  }
}
