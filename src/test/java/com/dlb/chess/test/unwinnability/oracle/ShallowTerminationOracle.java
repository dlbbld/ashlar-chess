package com.dlb.chess.test.unwinnability.oracle;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.model.Outcome;
import com.dlb.chess.common.utility.BasicChessUtility;
import com.dlb.chess.model.LegalMove;
import com.dlb.chess.test.unwinnability.oracle.enums.LimitedUnwinnabilityVerdict;

/**
 * Test-only oracle answering one question: in a depth-3 half-move tree from the given position, is there any path that
 * checkmates the loser (for {@code WINNABLE}), or do every single path resolve to a draw or self-loss (for
 * {@code UNWINNABLE})? If neither, the verdict is {@code UNKNOWN}.
 *
 * <p>
 * The search is cooperative — both sides are allowed to play any legal move. A single WIN-leaf anywhere in the 3-ply
 * tree is enough to conclude {@code WINNABLE} for the side that delivered the mate.
 *
 * <p>
 * "Drawing terminations" covered here: stalemate, fivefold repetition, seventy-five-move rule, dead-position
 * insufficient material, dead-position-by-CHA-quick, and one-sided insufficient material (when the side-to-evaluate is
 * the side that lacks the material).
 *
 * <p>
 * This oracle deliberately does <em>not</em> walk the forced unique-move chain — that's {@link ForcedLineOracle}'s job.
 * Neither does it know about pawn walls — that's the pawn-wall analyzer. {@link LimitedUnwinnabilityOracle} composes
 * the three.
 */
public class ShallowTerminationOracle {

  private static final int MAX_DEPTH = 3;

  /** Result of evaluating a single node during the recursive scan. */
  private enum NodeOutcome {
    /** Side-to-evaluate delivered checkmate somewhere in this subtree. */
    WIN,
    /** Every branch from this node terminates without side-to-evaluate winning (draw or self-loss). */
    LOSS_OR_DRAW,
    /** Either still ongoing at the depth bound, or at least one branch is itself UNRESOLVED. */
    UNRESOLVED
  }

  /**
   * Runs the oracle on a fresh history-less board built from the caller's FEN. The caller's board is not mutated, and
   * repetition history from the caller's game is lost on the fresh board.
   */
  public static LimitedUnwinnabilityVerdict calculateUnwinnability(Board input, Side side) {
    final Board board = input.copyCurrentPositionWithoutHistory();
    return switch (scan(board, side, 0)) {
      case WIN -> LimitedUnwinnabilityVerdict.WINNABLE;
      case LOSS_OR_DRAW -> LimitedUnwinnabilityVerdict.UNWINNABLE;
      case UNRESOLVED -> LimitedUnwinnabilityVerdict.UNKNOWN;
      default -> throw new IllegalArgumentException();
    };
  }

  private static NodeOutcome scan(Board board, Side side, int depth) {

    // If this position is already terminal, report its outcome — no need to descend.
    final NodeOutcome terminalOutcome = classifyTerminal(board, side);
    if (terminalOutcome != NodeOutcome.UNRESOLVED) {
      return terminalOutcome;
    }

    // Out of plies. The position is ongoing but we ran out of search depth, so nothing concluded.
    if (depth == MAX_DEPTH) {
      return NodeOutcome.UNRESOLVED;
    }

    // Cooperative scan: play every legal move, collect what each subtree reports.
    var anyChildIsWin = false;
    var everyChildIsLossOrDraw = true;

    for (final LegalMove move : board.getLegalMoves()) {
      board.move(move.moveSpecification());
      final NodeOutcome childOutcome = scan(board, side, depth + 1);
      board.unmove();

      switch (childOutcome) {
        case WIN -> anyChildIsWin = true;
        case UNRESOLVED -> everyChildIsLossOrDraw = false;
        case LOSS_OR_DRAW -> {
          // every-child-loss-or-draw still potentially true; keep looking
        }
        default -> throw new IllegalArgumentException();
      }
    }

    if (anyChildIsWin) {
      return NodeOutcome.WIN;
    }
    if (everyChildIsLossOrDraw) {
      return NodeOutcome.LOSS_OR_DRAW;
    }
    return NodeOutcome.UNRESOLVED;
  }

  /**
   * Classifies a single position as terminal-and-WIN, terminal-and-LOSS_OR_DRAW, or non-terminal ({@code UNRESOLVED}).
   * "Non-terminal for side X" includes one-sided insufficient material when X is not the side lacking material — in
   * that case X may still be able to mate, so the search must continue.
   */
  private static NodeOutcome classifyTerminal(Board board, Side side) {
    final Outcome outcome = BasicChessUtility.calculateOutcome(board);
    return switch (outcome.termination()) {
      case NONE -> {
        // Game ongoing for the calculateOutcome view, but one-sided insufficient material is a
        // diagnostic state outside that view: if the side we're evaluating lacks mating material,
        // that side cannot win — LOSS_OR_DRAW for them, UNRESOLVED for the opponent (the opponent
        // may still win).
        if (board.isInsufficientMaterial(side)) {
          yield NodeOutcome.LOSS_OR_DRAW;
        }
        yield NodeOutcome.UNRESOLVED;
      }
      case CHECKMATE ->
          // The side to move is in checkmate. If that's the side we're evaluating, they lost; otherwise
          // the side we're evaluating just delivered the mate — i.e. WIN.
          board.getHavingMove() == side ? NodeOutcome.LOSS_OR_DRAW : NodeOutcome.WIN;
      case STALEMATE, INSUFFICIENT_MATERIAL, SEVENTY_FIVE_MOVES, FIVEFOLD_REPETITION -> NodeOutcome.LOSS_OR_DRAW;
    };
  }
}
