package com.dlb.chess.test.unwinnability.oracle;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.enums.GameStatus;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.common.utility.BasicChessUtility;
import com.dlb.chess.common.utility.ListUtility;
import com.dlb.chess.model.LegalMove;
import com.dlb.chess.test.unwinnability.oracle.enums.LimitedUnwinnabilityVerdict;
import com.dlb.chess.test.unwinnability.oracle.model.GameForced;

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
 * <li>{@code ForcedLineOracle} — tested against {@code PgnTest.BASIC_FORCED}.</li>
 * <li>{@code ShallowTerminationOracle} — tested against {@code PgnTest.CHA_SHALLOW_TERMINATION}.</li>
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
   * reached at the end of the chain, the number of forced half-moves walked, and which side made the last move in the
   * chain.
   */
  static GameForced evaluateForcedLine(Board board) {
    // we check position after series of forced moves
    // we cannot use early returns for after evaluation we need to undo the moves
    var countForcedHalfMoves = 0;
    while (board.getLegalMoves().size() == 1) {
      countForcedHalfMoves++;
      final LegalMove legalMove = ListUtility.getOnly(board.getLegalMoves());
      board.move(legalMove.moveSpecification());
      final GameStatus evaluation = BasicChessUtility.calculateGameStatus(board);
      switch (BasicChessUtility.calculateGameStatus(board)) {
        case CHECKMATE:
        case FIVE_FOLD_REPETITION_RULE:
        case DEAD_POSITION_INSUFFICIENT_MATERIAL:
        case INSUFFICIENT_MATERIAL_WHITE_ONLY:
        case INSUFFICIENT_MATERIAL_BLACK_ONLY:
        case SEVENTY_FIVE_MOVE_RULE:
        case STALEMATE:
          final Side sideMadeLastMove = board.getHavingMove().getOppositeSide();
          for (var i = 1; i <= countForcedHalfMoves; i++) {
            board.unmove();
          }
          return new GameForced(evaluation, countForcedHalfMoves, sideMadeLastMove);
        case ONGOING:
          break;
        default:
          throw new IllegalArgumentException();
      }
    }

    final Side sideMadeLastMove = board.getHavingMove().getOppositeSide();
    for (var i = 1; i <= countForcedHalfMoves; i++) {
      board.unmove();
    }
    return new GameForced(GameStatus.ONGOING, countForcedHalfMoves, sideMadeLastMove);
  }

  /**
   * Decodes the terminal status reached at the end of the forced single-move chain into a verdict for
   * {@code sideToEvaluate}. CHECKMATE depends on which side delivered the mate (= {@code sideMadeLastMove}); the
   * per-side insufficient-material variants depend on whether the colour with insufficient material <em>is</em>
   * {@code sideToEvaluate} (then UNWINNABLE) or the opponent (then UNKNOWN — opponent's insufficient material doesn't
   * decide our winnability either way from a forced chain alone).
   */
  private static LimitedUnwinnabilityVerdict calculateUnwinnabilityForced(GameForced gameForced, Side sideToEvaluate) {
    final Side sideMadeLastMove = gameForced.sideMadeLastMove();
    return switch (gameForced.gameStatus()) {
      case CHECKMATE -> sideMadeLastMove == sideToEvaluate ? LimitedUnwinnabilityVerdict.WINNABLE
          : LimitedUnwinnabilityVerdict.UNWINNABLE;
      // DEAD_POSITION_UNWINNABLE_QUICK is unreachable here because calculateGameStatus does not invoke the
      // analyzer; it is grouped with the other drawing terminations to keep the switch exhaustive.
      case STALEMATE, DEAD_POSITION_INSUFFICIENT_MATERIAL, DEAD_POSITION_UNWINNABLE_QUICK, FIVE_FOLD_REPETITION_RULE, SEVENTY_FIVE_MOVE_RULE -> LimitedUnwinnabilityVerdict.UNWINNABLE;
      case INSUFFICIENT_MATERIAL_WHITE_ONLY -> sideToEvaluate == Side.WHITE ? LimitedUnwinnabilityVerdict.UNWINNABLE
          : LimitedUnwinnabilityVerdict.UNKNOWN;
      case INSUFFICIENT_MATERIAL_BLACK_ONLY -> sideToEvaluate == Side.BLACK ? LimitedUnwinnabilityVerdict.UNWINNABLE
          : LimitedUnwinnabilityVerdict.UNKNOWN;
      case ONGOING -> LimitedUnwinnabilityVerdict.UNKNOWN;
    };
  }
}
