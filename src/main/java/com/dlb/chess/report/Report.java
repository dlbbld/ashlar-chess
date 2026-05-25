package com.dlb.chess.report;

import java.util.List;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.model.HalfMove;

/**
 * Result of analyzing a fully-replayed game. Under the strict move-validation pipeline a game cannot continue past
 * checkmate, stalemate, or mutual insufficient material. Fivefold, 75-move, and analyzer-driven dead positions are
 * queryable rather than enforced, so games may legitimately continue past those thresholds and the corresponding
 * predicates ({@link #hasFivefoldRepetition()}, {@link #hasSeventyFiveMoveRule()}) report the first occurrence.
 */
public record Report(Side havingMove, List<HalfMove> halfMoveList, List<List<HalfMove>> repetitionListList,
    List<List<NoProgressHalfMove>> noProgressMoveListList, boolean hasThreefoldRepetition,
    boolean hasFivefoldRepetition, boolean hasFiftyMoveRule, boolean hasSeventyFiveMoveRule, Board board) {

}
