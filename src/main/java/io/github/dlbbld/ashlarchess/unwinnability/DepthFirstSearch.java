// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import java.util.ArrayList;
import java.util.List;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.ucimove.utility.UciMoveUtility;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.model.UciMove;

class DepthFirstSearch {

  // Our quick algorithm is extremely light, requiring only a few microseconds on average per
  // position. It is also sound, but not complete. However, as we detail in Section 5, with an
  // (empirically chosen) depth bound of D = 9, all incorrectly classified games from the Lichess
  // Database except three were correctly identified by Unwinnablequick.
  private static final int D = 9;

  public static DepthFirstSearchAnalysis performDepthFirstSearch(Board board, Side c) {
    return performDepthFirstSearch(HelpmateSearchBoard.from(board), c);
  }

  private static DepthFirstSearchAnalysis performDepthFirstSearch(HelpmateSearchBoard board, Side c) {
    final List<LegalMove> mateList = new ArrayList<>();
    final DepthFirstSearchRecursionResult result = performDepthFirstSearch(board, c, 0, mateList);

    return switch (result) {
      case HAS_HELPMATE -> new DepthFirstSearchAnalysis(DepthFirstSearchResult.HAS_HELPMATE, 0,
          convertLegalMoveList(mateList));
      case HAS_NO_HELPMATE -> new DepthFirstSearchAnalysis(DepthFirstSearchResult.HAS_NO_HELPMATE, 0,
          new ArrayList<>());
      case INTERRUPTED -> new DepthFirstSearchAnalysis(DepthFirstSearchResult.UNKNOWN, 0, new ArrayList<>());
      default -> throw new IllegalArgumentException();
    };
  }

  private static DepthFirstSearchRecursionResult performDepthFirstSearch(HelpmateSearchBoard board, Side c,
      int currentDepth, List<LegalMove> mateList) {
    final boolean isIntendedWinnerHavingCheckmate = board.isCheckmate() && board.getHavingMove() == c.getOppositeSide();
    if (isIntendedWinnerHavingCheckmate) {
      return DepthFirstSearchRecursionResult.HAS_HELPMATE;
    }

    // Per the paper / Ambrona issue thread: 75-move and 5-fold repetition do not apply when adjudicating
    // timeouts, so the helpmate search must continue past them. Termination conditions here are the
    // paper's Figure 5 line 2 (intended winner has just the king / Lemma 5 / Lemma 6 / stalemate /
    // self-checkmate) plus the depth bound.
    if (currentDepth < D && !board.isInsufficientMaterial(c)) {

      for (final LegalMove legalMove : board.getLegalMoves()) {
        board.move(legalMove.moveSpecification());

        mateList.add(legalMove);
        final DepthFirstSearchRecursionResult hasCheckmate = performDepthFirstSearch(board, c, currentDepth + 1,
            mateList);
        board.unmove();
        switch (hasCheckmate) {
          case HAS_HELPMATE -> {
            return DepthFirstSearchRecursionResult.HAS_HELPMATE;
          }
          case INTERRUPTED -> {
            mateList.remove(mateList.size() - 1);
            return DepthFirstSearchRecursionResult.INTERRUPTED;
          }
          case HAS_NO_HELPMATE -> mateList.remove(mateList.size() - 1);
          default -> throw new IllegalArgumentException();
        }
      }
    }
    // search could have continued
    if (currentDepth == D) {
      return DepthFirstSearchRecursionResult.INTERRUPTED;
    }
    return DepthFirstSearchRecursionResult.HAS_NO_HELPMATE;
  }

  private static List<UciMove> convertLegalMoveList(List<LegalMove> moveProgressList) {
    final List<UciMove> result = new ArrayList<>();
    for (final LegalMove legalMove : moveProgressList) {
      result.add(UciMoveUtility.convertMoveSpecificationToUci(legalMove.havingMove(), legalMove.moveSpecification()));
    }
    return result;
  }

}
