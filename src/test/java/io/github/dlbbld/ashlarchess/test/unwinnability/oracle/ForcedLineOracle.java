// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability.oracle;

import org.eclipse.jdt.annotation.Nullable;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.enums.Termination;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.common.model.Outcome;
import io.github.dlbbld.ashlarchess.common.utility.BasicChessUtility;
import io.github.dlbbld.ashlarchess.common.utility.ListUtility;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.test.unwinnability.oracle.enums.LimitedUnwinnabilityVerdict;
import io.github.dlbbld.ashlarchess.test.unwinnability.oracle.model.GameForced;

/**
 * Test-only oracle that walks the chain of forced single-legal-move positions starting from a board and reports the
 * verdict implied by where the chain ends. If the chain reaches a terminal status (checkmate / stalemate / insufficient
 * material / five-fold / seventy-five-move), the verdict is decisive; otherwise it is UNKNOWN.
 *
 * <p>
 * Companion to {@link ShallowTerminationOracle}, which handles the bounded 1/2/3-ply scan over <em>all</em> legal moves
 * at the root rather than only the unique-move chain. The two are deliberately separate so each can be exercised in
 * isolation:
 *
 * <ul>
 * <li>{@code ForcedLineOracle} - tested against {@code PgnTest.BASIC_FORCED}.</li>
 * <li>{@code ShallowTerminationOracle} - tested against {@code PgnTest.CHA_SHALLOW_TERMINATION}.</li>
 * </ul>
 *
 * <p>
 * {@link LimitedUnwinnabilityOracle} composes both plus the pawn-wall analyzer.
 *
 * <p>
 * The oracle is self-contained: it performs its own pre-checks for terminal-at-the-root and insufficient-material
 * positions, so callers do not need to filter the board beforehand.
 */
public class ForcedLineOracle {

  /**
   * Runs the oracle on a fresh history-less board built from the caller's FEN. The caller's board is not mutated, and
   * repetition history from the caller's game is lost on the fresh board.
   */
  public static LimitedUnwinnabilityVerdict calculateUnwinnability(Board input, Side side) {
    final Board board = input.copyCurrentPositionWithoutHistory();
    return calculateUnwinnabilityInternal(board, side);
  }

  private static LimitedUnwinnabilityVerdict calculateUnwinnabilityInternal(Board board, Side side) {

    if (board.isCheckmate()) {
      if (side == board.getHavingMove()) {
        return LimitedUnwinnabilityVerdict.UNWINNABLE;
      }
      return LimitedUnwinnabilityVerdict.WINNABLE;
    }

    if (board.isInsufficientMaterial(side) || board.isStalemate() || board.isFivefoldRepetition()
        || board.isSeventyFiveMove()) {
      return LimitedUnwinnabilityVerdict.UNWINNABLE;
    }

    if (board.getLegalMoves().isEmpty()) {
      throw new ProgrammingMistakeException("At this point we must have at least one legal move");
    }

    final GameForced forced = evaluateForcedLine(board);
    return calculateUnwinnabilityForced(forced, side);
  }

  /**
   * Walks the unique-legal-move chain from the current position. Mutates the board during the walk and undoes all moves
   * before returning, leaving the board unchanged. Returns a {@link GameForced} carrying the terminal status (if any)
   * reached at the end of the chain, the number of forced plies walked, and which side made the last move in the chain.
   */
  static GameForced evaluateForcedLine(Board board) {
    // we check position after series of forced moves
    // we cannot use early returns for after evaluation we need to undo the moves
    int countForcedHalfMoves = 0;
    while (board.getLegalMoves().size() == 1) {
      countForcedHalfMoves++;
      final LegalMove legalMove = ListUtility.getOnly(board.getLegalMoves());
      board.move(legalMove.moveSpecification());
      final Outcome outcome = BasicChessUtility.calculateOutcome(board);
      final boolean terminated = outcome.termination() != Termination.NONE;
      // One-sided insufficient material is a diagnostic position state outside the Outcome view but
      // a decisive signal for the forced-line oracle: the side that lacks material cannot win along
      // this chain. Carry it explicitly on GameForced for calculateUnwinnabilityForced to consume.
      final @Nullable Side singleSideIm = terminated ? null : singleSideInsufficientMaterial(board);
      if (terminated || singleSideIm != null) {
        final Side sideMadeLastMove = board.getHavingMove().getOppositeSide();
        for (int i = 1; i <= countForcedHalfMoves; i++) {
          board.unmove();
        }
        return new GameForced(outcome, singleSideIm, countForcedHalfMoves, sideMadeLastMove);
      }
    }

    final Side sideMadeLastMove = board.getHavingMove().getOppositeSide();
    for (int i = 1; i <= countForcedHalfMoves; i++) {
      board.unmove();
    }
    return new GameForced(Outcome.ONGOING, null, countForcedHalfMoves, sideMadeLastMove);
  }

  private static @Nullable Side singleSideInsufficientMaterial(Board board) {
    if (board.isInsufficientMaterial(Side.WHITE)) {
      return Side.WHITE;
    }
    if (board.isInsufficientMaterial(Side.BLACK)) {
      return Side.BLACK;
    }
    return null;
  }

  /**
   * Decodes the terminal status reached at the end of the forced single-move chain into a verdict for
   * {@code sideToEvaluate}. CHECKMATE depends on which side delivered the mate (= {@code sideMadeLastMove});
   * single-side insufficient material depends on whether the colour with insufficient material <em>is</em>
   * {@code sideToEvaluate} (then UNWINNABLE) or the opponent (then UNKNOWN - opponent's insufficient material doesn't
   * decide our winnability either way from a forced chain alone).
   */
  private static LimitedUnwinnabilityVerdict calculateUnwinnabilityForced(GameForced gameForced, Side sideToEvaluate) {
    final Outcome outcome = gameForced.outcome();
    final Side singleSideIm = gameForced.singleSideInsufficientMaterial();
    if (gameForced.hasTermination()) {
      return switch (outcome.termination()) {
        case CHECKMATE -> gameForced.sideMadeLastMove() == sideToEvaluate ? LimitedUnwinnabilityVerdict.WINNABLE
            : LimitedUnwinnabilityVerdict.UNWINNABLE;
        case STALEMATE, INSUFFICIENT_MATERIAL, FIVEFOLD_REPETITION, SEVENTY_FIVE_MOVES -> LimitedUnwinnabilityVerdict.UNWINNABLE;
        case NONE -> throw new ProgrammingMistakeException("hasTermination() guard precludes Termination.NONE here");
      };
    }
    if (singleSideIm != null) {
      return singleSideIm == sideToEvaluate ? LimitedUnwinnabilityVerdict.UNWINNABLE
          : LimitedUnwinnabilityVerdict.UNKNOWN;
    }
    // No outcome reached, no single-side IM along the forced chain - chain ended naturally.
    return LimitedUnwinnabilityVerdict.UNKNOWN;
  }
}
