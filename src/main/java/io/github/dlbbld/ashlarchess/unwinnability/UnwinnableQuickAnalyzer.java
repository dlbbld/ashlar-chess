// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.common.model.DynamicPosition;
import io.github.dlbbld.ashlarchess.fen.model.Fen;
import io.github.dlbbld.ashlarchess.model.LegalMove;

// Faithful port of CHA 2.6.1 DYNAMIC::quick_analysis (the deployed -quick), NOT the paper's Figure 10. It is
// deliberately 2-valued: it returns UNWINNABLE when it can prove the position dead for the intended winner, and
// otherwise POSSIBLY_WINNABLE (CHA's "undetermined -> guessed winnable"). It never returns WINNABLE; finding a
// helpmate is the job of the full analyzer. The depth-7 search is unconditional; the depth-15 pass is CHA's ad hoc
// deeper retry for restricted pawn/bishop positions (CHA comment: "TODO: remove if too ad hoc for capturing bKHPqNEw").
public class UnwinnableQuickAnalyzer {

  /**
   * Quick unwinnability for one intended winner.
   *
   * <p>
   * It answer the question "can this side ever deliver checkmate?"
   */
  public static UnwinnabilityQuickAnalysis unwinnableQuick(Board input, Side c) {
    return new UnwinnabilityQuickAnalysis(calculateUnwinnabilityQuickVerdict(input, c));
  }

  /**
   * Dead-position-quick check for the whole position (no intended winner): {@code UNWINNABLE} means the position is
   * dead - neither side can deliver checkmate by any sequence of legal moves - and {@code POSSIBLY_WINNABLE} means it
   * is not provably dead. This is the quick, during-the-game counterpart to
   * {@link UnwinnableFullAnalyzer#unwinnableFull(Board)}, the complete check suggested at game end (resignation or
   * flag-fall). Short-circuits: it stops as soon as one side is not provably unwinnable.
   */
  public static UnwinnabilityQuickVerdict unwinnableQuick(Board board) {
    if (unwinnableQuick(board, Side.WHITE).verdict() != UnwinnabilityQuickVerdict.UNWINNABLE) {
      return UnwinnabilityQuickVerdict.POSSIBLY_WINNABLE;
    }
    if (unwinnableQuick(board, Side.BLACK).verdict() != UnwinnabilityQuickVerdict.UNWINNABLE) {
      return UnwinnabilityQuickVerdict.POSSIBLY_WINNABLE;
    }
    return UnwinnabilityQuickVerdict.UNWINNABLE;
  }

  private static UnwinnabilityQuickVerdict calculateUnwinnabilityQuickVerdict(Board input, Side c) {
    final Board board = copyCurrentPositionForQuickSearch(input);
    final String invariant = board.getFen();

    // CHA trivial_progress: advance the position while there is exactly one legal move.
    int countPlies = 0;
    final Set<DynamicPosition> forcedPositionSet = new HashSet<>();
    while (board.getLegalMoves().size() == 1 && forcedPositionSet.add(board.getDynamicPosition())) {
      board.move(Nulls.getFirst(board.getLegalMoves()).moveSpecification());
      countPlies++;
    }

    final boolean isUnwinnable = calculateIsQuickUnwinnable(board, c);

    unperformPlies(board, countPlies);
    if (!invariant.equals(board.getFen())) {
      throw new ProgrammingMistakeException("Board was changed");
    }
    return isUnwinnable ? UnwinnabilityQuickVerdict.UNWINNABLE : UnwinnabilityQuickVerdict.POSSIBLY_WINNABLE;
  }

  // Mirrors the body of DYNAMIC::quick_analysis: an unconditional depth-7 dynamic search, an ad hoc deeper depth-15
  // retry, then the blocked-position semi-static checks. Returns true only when one of them proves unwinnability.
  private static boolean calculateIsQuickUnwinnable(Board board, Side c) {
    final BitboardPosition bitboardPosition = board.getBitboardPosition();
    final boolean hasOnlyPawnsAndBishops = calculateHasOnlyPawnsBishopsAndKings(bitboardPosition);
    final MovedKings movedKings = new MovedKings();

    boolean isUnwinnable = calculateIsDynamicallyUnwinnable(board, c, 7, movedKings, new HashMap<>());

    if (!isUnwinnable && hasOnlyPawnsAndBishops && movedKings.value != 3 && board.getLegalMoves().size() <= 8) {
      isUnwinnable = calculateIsDynamicallyUnwinnable(board, c, 15, movedKings, new HashMap<>());
    }

    final boolean isBlockedCandidate = calculateIsBlockedCandidate(bitboardPosition);

    if (isBlockedCandidate && !isUnwinnable && hasOnlyPawnsAndBishops) {
      isUnwinnable = UnwinnableSemiStatic.unwinnableSemiStatic(board, c, Mobility.mobility(board));
    }

    if (isBlockedCandidate && !isUnwinnable && calculateIsAlmostOnlyPawnsBishopsAndKings(bitboardPosition)
        && (board.isCheck() || UnwinnabilityMaterialBitboard.calculateHasKnight(bitboardPosition))) {
      isUnwinnable = calculateIsUnwinnableAfterOneMove(board, c);
    }

    return isUnwinnable;
  }

  // CHA dynamically_unwinnable: returns true iff every line, within the depth bound, reaches a position that is
  // impossible to win for the intended winner (or a checkmate of the intended winner). A transposition map memoizes
  // the (position, depth) verdict; the boolean result is unaffected by it, though movedKings may be under-counted on
  // a cache hit (only relevant to the ad hoc depth-15 gate).
  private static boolean calculateIsDynamicallyUnwinnable(Board board, Side intendedWinner, int depth,
      MovedKings movedKings, Map<DynamicSearchKey, Boolean> transpositionMap) {
    // impossible_to_win: winner has just the king, or the loser must promote but has no pawns (Lemmas 5/6).
    if (UnwinnabilityMaterialBitboard.calculateIsInsufficientMaterial(intendedWinner, board.getBitboardPosition())) {
      return true;
    }

    if (board.getLegalMoves().isEmpty() && board.isCheck()) {
      return board.getHavingMove() == intendedWinner;
    }

    if (depth <= 0) {
      return false;
    }

    final DynamicSearchKey cacheKey = new DynamicSearchKey(board.getDynamicPosition(), depth);
    if (transpositionMap.containsKey(cacheKey)) {
      return Nulls.get(transpositionMap, cacheKey);
    }

    for (final LegalMove legalMove : board.getLegalMoves()) {
      if (legalMove.movingPiece().getPieceType() == PieceType.KING) {
        movedKings.value |= board.getHavingMove() == Side.WHITE ? 2 : 1;
      }
      board.move(legalMove.moveSpecification());
      final boolean isUnwinnable = calculateIsDynamicallyUnwinnable(board, intendedWinner, depth - 1, movedKings,
          transpositionMap);
      board.unmove();
      if (!isUnwinnable) {
        transpositionMap.put(cacheKey, false);
        return false;
      }
    }

    transpositionMap.put(cacheKey, true);
    return true;
  }

  // CHA is_unwinnable_after_one_move: unwinnable if every legal move leads to a semi-statically unwinnable position.
  private static boolean calculateIsUnwinnableAfterOneMove(Board board, Side intendedWinner) {
    if (board.getLegalMoves().isEmpty()) {
      return !board.isCheck() || board.getHavingMove() == intendedWinner;
    }

    for (final LegalMove legalMove : board.getLegalMoves()) {
      board.move(legalMove.moveSpecification());
      final MobilitySolution mobilitySolution = Mobility.mobility(board);
      final boolean isUnwinnable = UnwinnableSemiStatic.unwinnableSemiStatic(board, intendedWinner, mobilitySolution);
      board.unmove();
      if (!isUnwinnable) {
        return false;
      }
    }
    return true;
  }

  private static boolean calculateHasOnlyPawnsBishopsAndKings(BitboardPosition bitboardPosition) {
    return !UnwinnabilityMaterialBitboard.calculateHasRook(bitboardPosition)
        && !UnwinnabilityMaterialBitboard.calculateHasKnight(bitboardPosition)
        && !UnwinnabilityMaterialBitboard.calculateHasQueen(bitboardPosition);
  }

  private static boolean calculateIsAlmostOnlyPawnsBishopsAndKings(BitboardPosition bitboardPosition) {
    final long heavyPieces = bitboardPosition.whiteKnights() | bitboardPosition.blackKnights()
        | bitboardPosition.whiteRooks() | bitboardPosition.blackRooks() | bitboardPosition.whiteQueens()
        | bitboardPosition.blackQueens();
    return Long.bitCount(heavyPieces) <= 1;
  }

  private static boolean calculateIsBlockedCandidate(BitboardPosition bitboardPosition) {
    return calculateNumberOfBlockedPawns(bitboardPosition) >= 1 && !calculateHasLonelyPawns(bitboardPosition);
  }

  private static int calculateNumberOfBlockedPawns(BitboardPosition bitboardPosition) {
    // A white pawn one rank below a black pawn = the white pawn's bit shifted up 8 lands on a black pawn.
    return Long.bitCount((bitboardPosition.whitePawns() << 8) & bitboardPosition.blackPawns());
  }

  // Mask matching the reference: white < rank 7, black > rank 2 (0-indexed).
  private static final long WHITE_LONELY_RANK_MASK = 0x0000FFFFFFFFFFFFL;
  private static final long BLACK_LONELY_RANK_MASK = 0xFFFFFFFFFFFF0000L;

  private static boolean calculateHasLonelyPawns(BitboardPosition bitboardPosition) {
    final int whitePawnFileMask = projectToFiles(bitboardPosition.whitePawns() & WHITE_LONELY_RANK_MASK);
    final int blackPawnFileMask = projectToFiles(bitboardPosition.blackPawns() & BLACK_LONELY_RANK_MASK);
    return whitePawnFileMask != blackPawnFileMask;
  }

  private static int projectToFiles(long bitboard) {
    long result = bitboard;
    result |= result >>> 32;
    result |= result >>> 16;
    result |= result >>> 8;
    return (int) (result & 0xFFL);
  }

  private static Board copyCurrentPositionForQuickSearch(Board input) {
    final Fen fen = new Fen(input.getFen(), input.getBitboardPosition(), input.getHavingMove(),
        input.getCastlingRightWhite(), input.getCastlingRightBlack(), input.getEnPassantCaptureTargetSquare(), 0,
        input.getFullMoveNumber());
    return new Board(fen);
  }

  private static void unperformPlies(Board board, int countPlies) {
    for (int i = 1; i <= countPlies; i++) {
      board.unmove();
    }
  }

  private static final class MovedKings {
    private int value;
  }

  private record DynamicSearchKey(DynamicPosition dynamicPosition, int depth) {
  }
}
