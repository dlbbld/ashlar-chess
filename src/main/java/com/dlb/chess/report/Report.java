package com.dlb.chess.report;

import java.util.List;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.enums.InsufficientMaterial;
import com.dlb.chess.common.model.HalfMove;

/**
 * Result of analyzing a fully-replayed game. Note that under the strict move-validation pipeline a game cannot continue
 * past the four enforced FIDE-automatic terminations (checkmate, stalemate, the two dead-position statuses), so the
 * "continued past" diagnostics that previously lived here are no longer reported for those. Fivefold and 75-move are
 * now queryable rather than enforced, so games may legitimately continue past those thresholds and the corresponding
 * predicates ({@link #hasFivefoldRepetition()}, {@link #hasSeventyFiveMoveRule()}) report the first occurrence.
 */
public record Report(Side havingMove, List<HalfMove> halfMoveList, List<List<HalfMove>> repetitionListList,
    List<List<NoProgressHalfMove>> noProgressMoveListList, boolean hasThreefoldRepetition,
    boolean hasFivefoldRepetition, boolean hasFiftyMoveRule, boolean hasSeventyFiveMoveRule, int firstCapture,
    boolean hasCapture, int maxNoProgressSequence, CheckmateOrStalemate checkmateOrStalemate,
    InsufficientMaterial insufficientMaterial, String fen, Board board) {

}
