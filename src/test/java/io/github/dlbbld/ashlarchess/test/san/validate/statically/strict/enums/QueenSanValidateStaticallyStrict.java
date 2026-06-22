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
import static io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.SanValidateStaticallyStrictHelpers.calculateIsFromFilePossibleOrthogonal;
import static io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.SanValidateStaticallyStrictHelpers.calculateIsOppositeVertical;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.model.EmptyBoardMove;
import io.github.dlbbld.ashlarchess.squares.EmptyBoardMoveUtility;

@SuppressWarnings("null")
public final class QueenSanValidateStaticallyStrict {

  private QueenSanValidateStaticallyStrict() {
  }

  public static final Set<String> VALUES;

  static {
    final Set<String> set = new TreeSet<>();
    for (final Square toSquare : Square.REAL) {
      final Set<EmptyBoardMove> moves = EmptyBoardMoveUtility.calculateNonPawnEmptyBoardMovesTo(PieceType.QUEEN,
          toSquare);
      final List<Square> fromSquares = calculateFromSquares(moves);

      // file/rank disambiguation
      for (final Square fromSquare : fromSquares) {
        appendOnlyMove(set, toSquare, PieceType.QUEEN);
        if (calculateIsFromFilePossibleOrthogonal(fromSquare, toSquare, fromSquares)
            && calculateIsFromFilePossibleDiagonal(fromSquare, toSquare, fromSquares)) {
          appendMoveWithFile(set, toSquare, fromSquare.getFile(), PieceType.QUEEN);
        }
        if (calculateIsFromRankPossibleQueen(fromSquare, toSquare, fromSquares)) {
          appendMoveWithRank(set, toSquare, fromSquare.getRank(), PieceType.QUEEN);
        }
      }

      // square disambiguation
      for (final Square fromSquare : fromSquares) {
        appendOnlyMove(set, toSquare, PieceType.QUEEN);
        if (calculateIsFromRankPossibleQueen(fromSquare, toSquare, fromSquares)
            && calculateHasOtherMovesFromSameRank(fromSquare, fromSquares)) {
          appendMoveWithFromSquare(set, toSquare, fromSquare, PieceType.QUEEN);
        }
      }
    }
    VALUES = Nulls.copyOfSet(set);
  }

  private static boolean calculateIsFromRankPossibleQueen(Square fromSquare, Square toSquare,
      List<Square> fromSquares) {
    for (final Square otherFromSquare : fromSquares) {
      if (otherFromSquare.getFile() == fromSquare.getFile() && otherFromSquare.getRank() != fromSquare.getRank()
          && (fromSquare.getFile() != toSquare.getFile()
              || calculateIsOppositeVertical(fromSquare, toSquare, otherFromSquare))) {
        return true;
      }
    }
    return false;
  }

}
