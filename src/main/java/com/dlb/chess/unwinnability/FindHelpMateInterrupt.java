package com.dlb.chess.unwinnability;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.ucimove.utility.UciMoveUtility;
import com.dlb.chess.model.LegalMove;
import com.dlb.chess.model.UciMove;

class FindHelpMateInterrupt {

  private static final boolean IS_DEBUG = false;

  private static final Logger logger = Nulls.getLogger(FindHelpMateInterrupt.class);

  // Our quick algorithm is extremely light, requiring only a few microseconds on average per
  // position. It is also sound, but not complete. However, as we detail in Section 5, with an
  // (empirically chosen) depth bound of D = 9, all incorrectly classified games from the Lichess
  // Database except three were correctly identified by Unwinnablequick.
  private static final int D = 9;

  public static FindHelpmateAnalysis calculateHelpmate(Board board, Side c) {
    return calculateHelpmate(HelpmateSearchBoard.from(board), c);
  }

  private static FindHelpmateAnalysis calculateHelpmate(HelpmateSearchBoard board, Side c) {
    final List<LegalMove> mateList = new ArrayList<>();
    final FindHelpMateInterruptResult result = calculateHelpmate(board, c, 0, mateList);

    return switch (result) {
      case TRUE -> new FindHelpmateAnalysis(FindHelpmateResult.YES, 0, convertLegalMoveList(mateList));
      case FALSE -> new FindHelpmateAnalysis(FindHelpmateResult.NO, 0, new ArrayList<>());
      case INTERRUPTED -> new FindHelpmateAnalysis(FindHelpmateResult.UNKNOWN, 0, new ArrayList<>());
      default -> throw new IllegalArgumentException();
    };
  }

  private static FindHelpMateInterruptResult calculateHelpmate(HelpmateSearchBoard board, Side c, int currentDepth,
      List<LegalMove> mateList) {
    final boolean isIntendedWinnerHavingCheckmate = board.isCheckmate() && board.getHavingMove() == c.getOppositeSide();
    if (isIntendedWinnerHavingCheckmate) {
      return FindHelpMateInterruptResult.TRUE;
    }

    // Per the paper / Ambrona issue thread: 75-move and 5-fold repetition do not apply when adjudicating
    // timeouts, so the helpmate search must continue past them. Termination conditions here are the
    // paper's Figure 5 line 2 (intended winner has just the king / Lemma 5 / Lemma 6 / stalemate /
    // self-checkmate) plus the depth bound.
    if (currentDepth < D && !board.isInsufficientMaterial(c)) {

      for (final LegalMove legalMove : board.getLegalMoves()) {
        board.move(legalMove.moveSpecification());
        if (IS_DEBUG) {
          final UciMove uciMove = UciMoveUtility.convertMoveSpecificationToUci(legalMove.havingMove(),
              legalMove.moveSpecification());
          logger.printf(Level.DEBUG, "%s - %d", uciMove.text(), currentDepth + 1);
        }

        mateList.add(legalMove);
        final FindHelpMateInterruptResult hasCheckmate = calculateHelpmate(board, c, currentDepth + 1, mateList);
        board.unmove();
        switch (hasCheckmate) {
          case TRUE -> {
            return FindHelpMateInterruptResult.TRUE;
          }
          case INTERRUPTED -> {
            mateList.remove(mateList.size() - 1);
            return FindHelpMateInterruptResult.INTERRUPTED;
          }
          case FALSE -> mateList.remove(mateList.size() - 1);
          default -> throw new IllegalArgumentException();
        }
      }
    }
    // search could have continued
    if (currentDepth == D) {
      return FindHelpMateInterruptResult.INTERRUPTED;
    }
    return FindHelpMateInterruptResult.FALSE;
  }

  private static List<UciMove> convertLegalMoveList(List<LegalMove> moveProgressList) {
    final List<UciMove> result = new ArrayList<>();
    for (final LegalMove legalMove : moveProgressList) {
      result.add(UciMoveUtility.convertMoveSpecificationToUci(legalMove.havingMove(), legalMove.moveSpecification()));
    }
    return result;
  }

}
