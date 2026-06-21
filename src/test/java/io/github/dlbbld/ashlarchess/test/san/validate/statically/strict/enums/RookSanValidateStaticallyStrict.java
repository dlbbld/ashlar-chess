// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums;

import static io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.SanValidateStaticallyStrictHelpers.appendMoveWithFile;
import static io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.SanValidateStaticallyStrictHelpers.appendMoveWithFromSquare;
import static io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.SanValidateStaticallyStrictHelpers.appendMoveWithRank;
import static io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.SanValidateStaticallyStrictHelpers.appendOnlyMove;
import static io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.SanValidateStaticallyStrictHelpers.calculateFromSquares;
import static io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.SanValidateStaticallyStrictHelpers.calculateHasOtherMovesFromSameRank;
import static io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.SanValidateStaticallyStrictHelpers.calculateIsFromFilePossibleOrthogonal;
import static io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.SanValidateStaticallyStrictHelpers.calculateIsFromRankPossibleOrthogonal;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSet;

import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.model.EmptyBoardMove;
import io.github.dlbbld.ashlarchess.squares.EmptyBoardMoveUtility;

@SuppressWarnings("null")
public final class RookSanValidateStaticallyStrict {

  private RookSanValidateStaticallyStrict() {
  }

  public static final ImmutableSet<String> VALUES;

  static {
    final Set<String> set = new TreeSet<>();
    for (final Square toSquare : Square.REAL) {
      final Set<EmptyBoardMove> moves = EmptyBoardMoveUtility.calculateNonPawnEmptyBoardMovesTo(PieceType.ROOK,
          toSquare);
      final List<Square> fromSquares = calculateFromSquares(moves);

      // file/rank disambiguation
      for (final Square fromSquare : fromSquares) {
        appendOnlyMove(set, toSquare, PieceType.ROOK);
        if (calculateIsFromFilePossibleOrthogonal(fromSquare, toSquare, fromSquares)) {
          appendMoveWithFile(set, toSquare, fromSquare.getFile(), PieceType.ROOK);
        }
        if (calculateIsFromRankPossibleOrthogonal(fromSquare, toSquare, fromSquares)) {
          appendMoveWithRank(set, toSquare, fromSquare.getRank(), PieceType.ROOK);
        }
      }

      // square disambiguation
      for (final Square fromSquare : fromSquares) {
        appendOnlyMove(set, toSquare, PieceType.ROOK);
        if (calculateIsFromRankPossibleOrthogonal(fromSquare, toSquare, fromSquares)
            && calculateHasOtherMovesFromSameRank(fromSquare, fromSquares)) {
          appendMoveWithFromSquare(set, toSquare, fromSquare, PieceType.ROOK);
        }
      }
    }
    VALUES = ImmutableSet.copyOf(set);
  }

}
