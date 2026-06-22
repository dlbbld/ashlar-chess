// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums;

import static io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.SanValidateStaticallyStrictHelpers.appendMoveWithFile;
import static io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.SanValidateStaticallyStrictHelpers.appendMoveWithFromSquare;
import static io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.SanValidateStaticallyStrictHelpers.appendMoveWithRank;
import static io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.SanValidateStaticallyStrictHelpers.appendOnlyMove;
import static io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.SanValidateStaticallyStrictHelpers.calculateFromSquares;
import static io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.SanValidateStaticallyStrictHelpers.calculateHasOtherMovesFromSameRank;
import static io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.SanValidateStaticallyStrictHelpers.calculateIsFromFilePossibleDiagonal;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.moves.EmptyBoardMove;
import io.github.dlbbld.ashlarchess.squares.EmptyBoardMoveUtility;

@SuppressWarnings("null")
public final class BishopSanValidateStaticallyStrict {

  private BishopSanValidateStaticallyStrict() {
  }

  public static final Set<String> VALUES;

  static {
    final Set<String> set = new TreeSet<>();
    for (final Square toSquare : Square.REAL) {
      final Set<EmptyBoardMove> moves = EmptyBoardMoveUtility.calculateNonPawnEmptyBoardMovesTo(PieceType.BISHOP,
          toSquare);
      final List<Square> fromSquares = calculateFromSquares(moves);

      // file/rank disambiguation
      for (final Square fromSquare : fromSquares) {
        appendOnlyMove(set, toSquare, PieceType.BISHOP);
        if (calculateIsFromFilePossibleDiagonal(fromSquare, toSquare, fromSquares)) {
          appendMoveWithFile(set, toSquare, fromSquare.getFile(), PieceType.BISHOP);
        }
        if (calculateIsFromRankPossibleBishop(fromSquare, fromSquares)) {
          appendMoveWithRank(set, toSquare, fromSquare.getRank(), PieceType.BISHOP);
        }
      }

      // square disambiguation
      for (final Square fromSquare : fromSquares) {
        appendOnlyMove(set, toSquare, PieceType.BISHOP);
        if (calculateIsFromRankPossibleBishop(fromSquare, fromSquares)
            && calculateHasOtherMovesFromSameRank(fromSquare, fromSquares)) {
          appendMoveWithFromSquare(set, toSquare, fromSquare, PieceType.BISHOP);
        }
      }
    }
    VALUES = Nulls.copyOfSet(set);
  }

  private static boolean calculateIsFromRankPossibleBishop(Square fromSquare, List<Square> fromSquares) {
    for (final Square otherFromSquare : fromSquares) {
      if (otherFromSquare.getFile() == fromSquare.getFile() && otherFromSquare.getRank() != fromSquare.getRank()) {
        return true;
      }
    }
    return false;
  }

}
