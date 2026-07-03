// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import java.util.List;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.LegalMove;
import io.github.dlbbld.ashlarchess.board.MoveSpecification;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.fen.model.Fen;
import io.github.dlbbld.ashlarchess.internal.Nulls;

// Figure 10 Unwinnable_quick: sound, computationally very light, deliberately incomplete.
// Footnote a: the forced-move advance must be loop-guarded (arbitrarily long single-move
// sequences exist; capping is verdict-preserving since a forced prefix is equivalence-
// preserving). Footnote b: the Lemma 5/6 material positions are DFS leaves.
/**
 * The quick unwinnability analysis - the paper's {@code Unwinnable_quick} (Figure 10). Steps: (1) advance the
 * position while there is only one legal move; (2) a plain DFS that is interrupted as soon as any variation reaches
 * the depth bound, with checkmate/stalemate and the insufficient-winning-material positions of Lemmas 5/6 (and the
 * bare winner king) as tree leaves; (3) {@code WINNABLE} if a mate by the intended winner was met,
 * {@code UNWINNABLE} if the whole tree was exhausted without interruption; else (4) the semi-static check, gated to
 * positions with only pawn/bishop/king material and no <em>semi-open files</em> (files with pawns of one colour
 * only); else (5) {@code POSSIBLY_WINNABLE}.
 *
 * <p>
 * The depth interrupt is <em>global</em>: the first line that reaches the depth bound stops the whole search (this
 * literal Figure 10 semantics is what makes the routine fast). Hence {@code WINNABLE} fires only when a mate is met
 * before the first deep dive; in general winnable positions the routine returns {@code POSSIBLY_WINNABLE}, which
 * asserts nothing.
 *
 * <p>
 * Defined for legal positions only; on an illegal position the result is undefined (and may differ from the full
 * analyzer). See this package's documentation for the legal-position contract.
 */
public final class UnwinnableQuickAnalyzer {

  private UnwinnableQuickAnalyzer() {
  }

  /**
   * The paper's empirically chosen depth bound {@code D}.
   */
  private static final int DEPTH_BOUND = 9;

  /** Guard for Figure 10 footnote a: stop advancing forced lines after this many plies. */
  private static final int FORCED_ADVANCE_CAP = 500;

  private static final long FILE_A = 0x0101_0101_0101_0101L;

  /**
   * Quick unwinnability for one intended winner: can this side ever deliver checkmate? Runs on a fresh history-less
   * board built from the caller's FEN; the caller's board is not mutated.
   */
  public static UnwinnabilityQuickAnalysis unwinnableQuick(Board input, Side c) {
    return new UnwinnabilityQuickAnalysis(calculateUnwinnabilityQuickVerdict(input, c));
  }

  private static UnwinnabilityQuickVerdict calculateUnwinnabilityQuickVerdict(Board input, Side c) {
    final Board board = copyCurrentPositionForQuickSearch(input);

    // Step 1: advance the position as long as there is only one legal move (loop-guarded, footnote a).
    int advanced = 0;
    List<MoveSpecification> legalMoves;
    while ((legalMoves = board.getLegalMoveSpecifications()).size() == 1 && advanced < FORCED_ADVANCE_CAP) {
      board.move(legalMoves.get(0));
      advanced++;
    }

    // Step 2: bounded DFS on the mutable search-board hot path, interrupted as soon as the depth bound is reached
    // anywhere.
    final QuickSearch quickSearch = new QuickSearch(c);
    quickSearch.depthFirstSearch(HelpmateSearchBoard.from(board), 0);

    if (quickSearch.mateFound) {
      return UnwinnabilityQuickVerdict.WINNABLE; // step 3
    }
    if (!quickSearch.interrupted) {
      return UnwinnabilityQuickVerdict.UNWINNABLE; // step 4: the whole tree was exhausted
    }

    // Steps 5-6: the semi-static check, gated to blocked-position candidates.
    final BitboardPosition placement = board.getBitboardPosition();
    if (hasOnlyPawnsBishopsAndKings(placement) && !hasSemiOpenFile(placement)) {
      final SemiStaticPosition semiStaticPosition = SemiStaticPosition.fromBoard(board);
      if (UnwinnableSemiStatic.unwinnableSemiStatic(semiStaticPosition, c, Mobility.mobility(semiStaticPosition))) {
        return UnwinnabilityQuickVerdict.UNWINNABLE;
      }
    }
    return UnwinnabilityQuickVerdict.POSSIBLY_WINNABLE; // step 7
  }

  /** Figure 10 step 5: only pieces of type pawn, bishop, king (either side). */
  private static boolean hasOnlyPawnsBishopsAndKings(BitboardPosition placement) {
    return (placement.whiteKnights() | placement.whiteRooks() | placement.whiteQueens() | placement.blackKnights()
        | placement.blackRooks() | placement.blackQueens()) == 0L;
  }

  /** A semi-open file: pawns of exactly one colour on it. */
  private static boolean hasSemiOpenFile(BitboardPosition placement) {
    for (int file = 0; file < 8; file++) {
      final long fileMask = FILE_A << file;
      final boolean hasWhitePawns = (placement.whitePawns() & fileMask) != 0L;
      final boolean hasBlackPawns = (placement.blackPawns() & fileMask) != 0L;
      if (hasWhitePawns ^ hasBlackPawns) {
        return true;
      }
    }
    return false;
  }

  private static Board copyCurrentPositionForQuickSearch(Board input) {
    final Fen fen = new Fen(input.getFen(), input.getBitboardPosition(), input.getSideToMove(),
        input.getCastlingRightWhite(), input.getCastlingRightBlack(), input.getEnPassantCaptureTargetSquare(), 0,
        input.getFullMoveNumber());
    return new Board(fen);
  }

  /**
   * The Figure 10 step-2 DFS. Leaves (footnote b): checkmate, stalemate, and insufficient winning material (bare
   * king, Lemma 5, Lemma 6). A non-leaf node at the depth bound interrupts the whole search.
   */
  private static final class QuickSearch {

    private final Side winner;
    private final Side loserSide;
    private boolean mateFound;
    private boolean interrupted;

    private QuickSearch(Side winner) {
      this.winner = winner;
      this.loserSide = winner.getOppositeSide();
    }

    private void depthFirstSearch(HelpmateSearchBoard board, int depth) {
      if (board.isCheckmate()) {
        if (board.getSideToMove() == loserSide) {
          mateFound = true; // interrupt (i): checkmate by the intended winner
        }
        return;
      }
      final BitboardPosition placement = board.getBitboardPosition();
      if (board.isStalemate() || MaterialLemmas.winnerHasBareKing(placement, winner)
          || MaterialLemmas.unwinnableByLemma5Or6(placement, winner)) {
        return; // a true leaf: no mate by the winner in this subtree
      }
      if (depth == DEPTH_BOUND) {
        interrupted = true; // interrupt (ii): the depth bound reached on a non-leaf
        return;
      }
      final List<LegalMove> legalMoves = board.getLegalMoves();
      final int totalLegalMoves = legalMoves.size();
      for (int i = 0; i < totalLegalMoves; i++) {
        board.move(Nulls.get(legalMoves, i).moveSpecification());
        depthFirstSearch(board, depth + 1);
        board.unmove();
        if (mateFound || interrupted) {
          return;
        }
      }
    }
  }
}
