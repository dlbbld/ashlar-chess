// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.model.DynamicPosition;
import io.github.dlbbld.ashlarchess.common.ucimove.utility.UciMoveUtility;
import io.github.dlbbld.ashlarchess.fen.model.Fen;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.model.UciMove;

//Figure 9 Main routine for deciding chess unwinnability. It is based on our semi-static
//algorithm (Figure 8) and our search routine (Figure 5) integrated via iterative deepening.
//Function bound must be increasing on d for the algorithm to be complete. The transposition
//table used by Find-Helpmatec should be initialized to empty at the beginning, but it can be
//shared between different calls to Find-Helpmatec in step 3. On the other hand, the global
//counter cnt should be initialized to 0 on every base call to Find-Helpmatec in step 3.
public class UnwinnableFullAnalyzer {

  private static final int MAX_DEPTH = 100;
  private static final int GLOBAL_NODES_BOUND = 500000;

  /**
   * Runs the algorithm on a fresh history-less board built from the caller's FEN. The caller's board is not mutated,
   * and repetition history from the caller's game is intentionally ignored.
   */
  public static UnwinnabilityFullAnalysis unwinnableFull(Board input, Side winner) {
    final Board board = copyCurrentPositionForFullSearch(input);
    return unwinnableFull(board, winner, false, new MobilitySolution());
  }

  /**
   * Dead-position-full check for the whole position (no intended winner): a winnable verdict ({@code WINNABLE_HELPMATE}
   * / {@code WINNABLE_BY_THEOREM}) means the position is not dead because that side can win; {@code UNWINNABLE} means
   * dead - neither side can deliver checkmate by any sequence of legal moves; {@code UNDETERMINED} means it could not
   * be decided within the search bound. This complete check is suggested at game end (resignation or flag-fall); during
   * the game prefer the cheaper {@link UnwinnableQuickAnalyzer#unwinnableQuick(Board)}. Short-circuits: it stops as
   * soon as one side is found winnable.
   */
  public static UnwinnabilityFullVerdict unwinnableFull(Board board) {
    final UnwinnabilityFullVerdict white = unwinnableFull(board, Side.WHITE).verdict();
    if (white.isWinnable()) {
      return white;
    }
    final UnwinnabilityFullVerdict black = unwinnableFull(board, Side.BLACK).verdict();
    if (black.isWinnable()) {
      return black;
    }
    if (white == UnwinnabilityFullVerdict.UNWINNABLE && black == UnwinnabilityFullVerdict.UNWINNABLE) {
      return UnwinnabilityFullVerdict.UNWINNABLE;
    }
    return UnwinnabilityFullVerdict.UNDETERMINED;
  }

  // Inputs: position, intended winner
  // Output: Unwinnable or Winnable (definite solution to the chess unwinnability problem)
  private static UnwinnabilityFullAnalysis unwinnableFull(Board board, Side winner, boolean isHasMobilitySolution,
      MobilitySolution calculatedMobilitySolution) {

    // add optimization from code
    // if position is advanced cannot use the provided mobility solution if any
    boolean isCanUseMobilitySolution = true;
    boolean isForcedMove = board.getLegalMoves().size() == 1;
    int totalForcedMoves = 0;
    final List<UciMove> forcedMoveLine = new ArrayList<>();
    final Set<DynamicPosition> forcedPositionSet = new HashSet<>();
    while (isForcedMove && forcedPositionSet.add(board.getDynamicPosition())) {
      isCanUseMobilitySolution = false;
      final LegalMove onlyLegalMove = Nulls.getFirst(board.getLegalMoves());
      forcedMoveLine.add(
          UciMoveUtility.convertMoveSpecificationToUci(onlyLegalMove.havingMove(), onlyLegalMove.moveSpecification()));
      board.move(onlyLegalMove.moveSpecification());
      isForcedMove = board.getLegalMoves().size() == 1;
      totalForcedMoves++;
    }

    // 1: if true UnwinnableSS(pos, c, Mobility(pos)) then return Unwinnable
    final MobilitySolution mobilitySolution;
    if (isHasMobilitySolution && isCanUseMobilitySolution) {
      mobilitySolution = calculatedMobilitySolution;
    } else {
      mobilitySolution = Mobility.mobility(board);
    }
    if (UnwinnableSemiStatic.unwinnableSemiStatic(board, winner, mobilitySolution)) {
      undoForcedMoves(board, totalForcedMoves);
      return new UnwinnabilityFullAnalysis(UnwinnabilityFullVerdict.UNWINNABLE, new ArrayList<>());
    }

    // Basic-helpmate-existence theorem: for elementary mating material, decide winnability directly instead of
    // searching for a cooperative mate. The verdict is certified by the theorem, so no mate line accompanies a
    // winnable result (see BasicHelpmateExistenceTheorem).
    switch (BasicHelpmateExistenceTheorem.decide(board, winner)) {
      case WINNABLE:
        undoForcedMoves(board, totalForcedMoves);
        return new UnwinnabilityFullAnalysis(UnwinnabilityFullVerdict.WINNABLE_BY_THEOREM, new ArrayList<>());
      case UNWINNABLE:
        undoForcedMoves(board, totalForcedMoves);
        return new UnwinnabilityFullAnalysis(UnwinnabilityFullVerdict.UNWINNABLE, new ArrayList<>());
      case NOT_APPLICABLE:
        break;
      default:
        throw new IllegalArgumentException();
    }

    // we must instantiate the class here to share the transposition table between calls
    final FindHelpmate findHelpmate = new FindHelpmate(winner);

    // 2: for every d in N do ( -> Iterative deepening)
    int globalNodeCount = 0;
    for (int maxDepth = 2; maxDepth <= MAX_DEPTH; maxDepth++) {
      // 3: set bd Find-Helpmatec(pos, 0, maxDepth = d) (global nodesBound = bound(d))

      final FindHelpmateAnalysis helpmateAnalysis = findHelpmate.calculateHelpmate(board, maxDepth);

      globalNodeCount += helpmateAnalysis.localNodesCount();

      if (globalNodeCount > GLOBAL_NODES_BOUND) {
        return new UnwinnabilityFullAnalysis(UnwinnabilityFullVerdict.UNDETERMINED, new ArrayList<>());
      }

      switch (helpmateAnalysis.findHelpmateResult()) {
        case HAS_HELPMATE:
          // 4: if bd = true then return Winnable
          undoForcedMoves(board, totalForcedMoves);
          return new UnwinnabilityFullAnalysis(UnwinnabilityFullVerdict.WINNABLE_HELPMATE,
              prependForcedMoves(forcedMoveLine, helpmateAnalysis.mateLine()));
        case HAS_NO_HELPMATE:
          // 5: else if the search was not interrupted (in step 4 of Figure 5) then
          // 6: return Unwinnable
          undoForcedMoves(board, totalForcedMoves);
          return new UnwinnabilityFullAnalysis(UnwinnabilityFullVerdict.UNWINNABLE, new ArrayList<>());
        case UNKNOWN:
          // the algorithm continues with next depth
          break;
        default:
          throw new IllegalArgumentException();
      }
    }

    undoForcedMoves(board, totalForcedMoves);
    return new UnwinnabilityFullAnalysis(UnwinnabilityFullVerdict.UNWINNABLE, new ArrayList<>());
  }

  private static void undoForcedMoves(Board board, int totalForcedMoves) {
    for (int i = 1; i <= totalForcedMoves; i++) {
      board.unmove();
    }
  }

  private static List<UciMove> prependForcedMoves(List<UciMove> forcedMoveLine, List<UciMove> helpmateLine) {
    final List<UciMove> result = new ArrayList<>(forcedMoveLine);
    result.addAll(helpmateLine);
    return result;
  }

  private static Board copyCurrentPositionForFullSearch(Board input) {
    final Fen fen = new Fen(input.getFen(), input.getBitboardPosition(), input.getHavingMove(),
        input.getCastlingRightWhite(), input.getCastlingRightBlack(), input.getEnPassantCaptureTargetSquare(), 0,
        input.getFullMoveNumber());
    return new Board(fen);
  }

}
