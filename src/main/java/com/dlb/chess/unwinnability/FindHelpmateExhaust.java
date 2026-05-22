package com.dlb.chess.unwinnability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.bitboard.LeanBoard;
import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.SquareType;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.ucimove.utility.UciMoveUtility;
import com.dlb.chess.model.LegalMove;
import com.dlb.chess.model.UciMove;

//Figure 5 Find-Helpmatec routine, returns true if a checkmate sequence for player c in {w, b},
//the intended winner, is found or false otherwise. The base call should be done on depth = 0,
//cnt = 0, and an empty table. The value of maxDepth and nodesBound can be chosen to set the
//limits of the search. The Score routine is defined in Figure 12 (Appendix A).
class FindHelpmateExhaust {

  private static final Logger logger = Nulls.getLogger(FindHelpmateExhaust.class);

  // empirically enough
  private static final int LOCAL_NODES_BOUND = 10000;

  private final Side color;
  // Zobrist-keyed: identifies a node by LeanBoard.zobristKey (piece placement + side + castling + EP file).
  // Phantom EP targets are NOT normalized away, so positions that DynamicPosition would have collapsed are
  // visited separately (more cache misses, no soundness loss). 64-bit collision risk is negligible at the
  // node counts FindHelpmateExhaust explores.
  private final HashMap<Long, Integer> transpositionMap = new HashMap<>();

  private int localNodeCount = 0;

  private boolean isCanExhaust = true;
  private List<LegalMove> moveEvaluationList = new ArrayList<>();

  public FindHelpmateExhaust(Side side) {
    this.color = side;
  }

  public FindHelpmateAnalysis calculateHelpmate(Board board, int maxDepth) {

    if (maxDepth != 0 && maxDepth % 10 == 0) {
      logger.printf(Level.DEBUG, "maxDepth=%d", maxDepth);
    }

    this.localNodeCount = 0;
    this.isCanExhaust = true;
    this.moveEvaluationList = new ArrayList<>();

    final LeanBoard leanBoard = LeanBoard.fromBoard(board);
    final var findHelpmate = findHelpmate(leanBoard, 0, maxDepth, 0, false);

    switch (findHelpmate) {
      case TRUE:
        return new FindHelpmateAnalysis(FindHelpmateResult.YES, localNodeCount,
            convertLegalMoveList(moveEvaluationList));
      case FALSE:
        if (isCanExhaust) {
          return new FindHelpmateAnalysis(FindHelpmateResult.NO, localNodeCount, new ArrayList<>());
        }
        return new FindHelpmateAnalysis(FindHelpmateResult.UNKNOWN, localNodeCount, new ArrayList<>());
      default:
        throw new IllegalArgumentException();
    }
  }

  // Inputs: position, depth (int), maxDepth (int)
  // Output: bool (true if a checkmate sequence was found, false otherwise)
  private FindHelpmateRecursionResult findHelpmate(LeanBoard leanBoard, int depth, int maxDepth, int actualDepth,
      boolean isPastProgress) {

    // 1: if the intended winner is checkmating their opponent in pos then return true
    if (leanBoard.havingMove() == color.getOppositeSide() && leanBoard.isCheckmate()) {
      return FindHelpmateRecursionResult.TRUE;
    }

    // 2: if the intended winner has just the king or the position is unwinnable according
    // to Lemma 5 or Lemma 6 or the position is stalemate or the intended winner is
    // receiving checkmate in the position then return false

    // Note: below omitted as not in the C++ code
    // or the position is stalemate or the intended winner is
    // receiving checkmate in the position then return false

    // set d := limits.max-depth - depth
    final var movesLeft = maxDepth - depth;

    final long cacheKey = leanBoard.zobristKey();
    // 5: if (pos,D) in table with D >= d then return false (-> pos was already analyzed)
    if (calculateIsInTranspositionTableWithEnoughDepth(cacheKey, movesLeft)) {
      return FindHelpmateRecursionResult.FALSE;
    }

    // 4: if cnt > nodesBound or d < 0 then return false (-> The search limits are exceeded)
    if (localNodeCount > maxDepth * LOCAL_NODES_BOUND || movesLeft <= 0) {

      if (isCanExhaust) {
        isCanExhaust = false;
      }
      return FindHelpmateRecursionResult.FALSE;
    }

    // 6: store (pos,D) in table
    store(cacheKey, movesLeft);

    // Per the paper / Ambrona issue thread: 75-move and 5-fold repetition do not apply when adjudicating
    // timeouts, so the helpmate search must continue past them. The previous fivefold/seventy-five gate
    // here is removed for paper compliance.

    final BitboardPosition bitboardPosition = leanBoard.bitboardPosition();
    if (UnwinnabilityMaterialBitboard.calculateHasKingOnly(color, bitboardPosition)
        || UnwinnabilityMaterialBitboard.calculateHasNoPawns(color.getOppositeSide(), bitboardPosition)
            && calculateIsNeedLoserPromotion(color, bitboardPosition)) {
      return FindHelpmateRecursionResult.FALSE;
    }

    // 7: for every legal move m in pos do:
    for (final LegalMove legalMove : leanBoard.legalMoves()) {
      // 8: let inc = match Score(pos,m) with Normal ! 0 | Reward ! 1 | Punish ! -2
      ScoreResult score = Score.score(color, leanBoard.havingMove(), bitboardPosition, legalMove);

      if (leanBoard.havingMove() == color.getOppositeSide()
          && UnwinnabilityMaterialBitboard.calculateHasQueen(color.getOppositeSide(), bitboardPosition)) {
        score = score == ScoreResult.REWARD ? ScoreResult.NORMAL : score;
      }

      if (actualDepth > 300) {
        score = score == ScoreResult.REWARD ? ScoreResult.NORMAL : score;
      }

      var newDepth = depth + 1;
      switch (score) {
        case REWARD:
          newDepth = newDepth - 1;
          break;
        case PUNISH:
          newDepth = Math.min(maxDepth, newDepth + 2);
          break;
        case NORMAL:
          if (isPastProgress) {
            newDepth = newDepth - 1;
          }
          break;
        default:
          throw new IllegalArgumentException();
      }

      // 9: if Find-Helpmatec(pos.move(m), depth+1, maxDepth+inc) then return true
      leanBoard.move(legalMove.moveSpecification());

      moveEvaluationList.add(legalMove);

      final var isProgress = score == ScoreResult.REWARD;

      // 3: increase cnt
      localNodeCount++;

      final var findHelpmate = findHelpmate(leanBoard, newDepth, maxDepth, actualDepth + 1, isProgress);
      leanBoard.unmove();
      switch (findHelpmate) {
        case TRUE:
          return findHelpmate;
        case FALSE:
          // continue
          break;
        default:
          throw new IllegalArgumentException();
      }
      moveEvaluationList.remove(moveEvaluationList.size() - 1);
    }

    // 10: return false (-> No mate was found after exploring every legal move)
    return FindHelpmateRecursionResult.FALSE;

  }

  private boolean calculateIsInTranspositionTableWithEnoughDepth(long cacheKey, int movesLeft) {
    final Integer stored = transpositionMap.get(cacheKey);
    if (stored == null) {
      return false;
    }
    return stored.intValue() >= movesLeft;
  }

  private void store(long cacheKey, int movesLeft) {
    transpositionMap.put(cacheKey, movesLeft);
  }

  // TODO: these two lemma predicates are declared but not yet wired into the helpmate-search /
  // unwinnability-evaluation flow. They encode the "intended winner has only K+N and intended loser has nothing
  // useful" (Lemma 5) and "intended winner has only K + same-coloured bishops and intended loser has neither
  // knights nor opposite-coloured bishops" (Lemma 6) sufficient-unwinnable conditions. Find the right call site
  // in FindHelpmateExhaust / UnwinnableFullAnalyzer and connect them.
  static boolean calculateIsUnwinnableAccordingLemma5(Side color, BitboardPosition bitboardPosition) {
    if (UnwinnabilityMaterialBitboard.calculateHasKingAndKnightOnly(color, bitboardPosition)) {
      if (UnwinnabilityMaterialBitboard.calculateHasNoKnights(color.getOppositeSide(), bitboardPosition)
          && UnwinnabilityMaterialBitboard.calculateHasNoBishops(color.getOppositeSide(), bitboardPosition)
          && UnwinnabilityMaterialBitboard.calculateHasNoRooks(color.getOppositeSide(), bitboardPosition)
          && UnwinnabilityMaterialBitboard.calculateHasNoPawns(color.getOppositeSide(), bitboardPosition)) {
        return true;
      }
    }
    return false;
  }

  static boolean calculateIsUnwinnableAccordingLemma6(Side color, BitboardPosition bitboardPosition) {
    for (final SquareType squareType : SquareType.REAL) {
      if (UnwinnabilityMaterialBitboard.calculateHasKingAndBishopsOnly(color, bitboardPosition, squareType)
          && UnwinnabilityMaterialBitboard.calculateHasNoKnights(color.getOppositeSide(), bitboardPosition)
          && UnwinnabilityMaterialBitboard.calculateHasNoBishops(color, bitboardPosition, squareType.getOppositeSquareType())
          && UnwinnabilityMaterialBitboard.calculateHasNoPawns(color.getOppositeSide(), bitboardPosition)) {
        return true;
      }
    }
    return false;
  }

  static boolean calculateIsNeedLoserPromotion(Side winner, BitboardPosition bitboardPosition) {
    if (calculateIsKnightNeedsPromotion(winner, bitboardPosition)) {
      return true;
    }

    return calculateIsBishopNeedsPromotion(winner, bitboardPosition);
  }

  private static boolean calculateIsKnightNeedsPromotion(Side winner, BitboardPosition bitboardPosition) {
    // if the intended winner has just a knight and the intended loser has just pawns
    // and/or queens
    return UnwinnabilityMaterialBitboard.calculateHasKingAndKnightOnly(winner, bitboardPosition)
        && UnwinnabilityMaterialBitboard.calculateHasNoRooks(winner.getOppositeSide(), bitboardPosition)
        && UnwinnabilityMaterialBitboard.calculateHasNoBishops(winner.getOppositeSide(), bitboardPosition)
        && UnwinnabilityMaterialBitboard.calculateHasNoKnights(winner.getOppositeSide(), bitboardPosition);
  }

  private static boolean calculateIsBishopNeedsPromotion(Side winner, BitboardPosition bitboardPosition) {
    // or the intended winner has just bishops of the same square color and
    // the intended loser does not have knights or bishops of the opposite color

    for (final SquareType squareType : SquareType.REAL) {
      if (UnwinnabilityMaterialBitboard.calculateHasKingAndBishopsOnly(winner, bitboardPosition, squareType)
          && UnwinnabilityMaterialBitboard.calculateHasNoKnights(winner.getOppositeSide(), bitboardPosition)
          && UnwinnabilityMaterialBitboard.calculateHasNoBishops(winner.getOppositeSide(), bitboardPosition,
              squareType.getOppositeSquareType())) {
        return true;
      }
    }

    return false;
  }

  private static List<UciMove> convertLegalMoveList(List<LegalMove> moveProgressList) {
    final List<UciMove> result = new ArrayList<>();
    for (final LegalMove legalMove : moveProgressList) {
      result.add(UciMoveUtility.convertMoveSpecificationToUci(legalMove.havingMove(), legalMove.moveSpecification()));
    }
    return result;
  }

}
