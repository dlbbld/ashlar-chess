// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums;

import static io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.SanValidateStaticallyStrictHelpers.appendMoveWithFile;
import static io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.SanValidateStaticallyStrictHelpers.appendMoveWithFromSquare;
import static io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.SanValidateStaticallyStrictHelpers.appendMoveWithRank;
import static io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.SanValidateStaticallyStrictHelpers.appendOnlyMove;
import static io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.SanValidateStaticallyStrictHelpers.calculateFromSquares;
import static io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.SanValidateStaticallyStrictHelpers.calculateHasOtherMovesFromSameRank;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.moves.EmptyBoardMove;
import io.github.dlbbld.ashlarchess.squares.EmptyBoardMoveUtility;

@SuppressWarnings("null")
public final class KnightSanValidateStaticallyStrict {

  private KnightSanValidateStaticallyStrict() {
  }

  public static final Set<String> VALUES;

  static {
    final Set<String> set = new TreeSet<>();
    for (final Square toSquare : Square.REAL) {
      final Set<EmptyBoardMove> moves = EmptyBoardMoveUtility.calculateNonPawnEmptyBoardMovesTo(PieceType.KNIGHT,
          toSquare);
      final List<Square> fromSquares = calculateFromSquares(moves);

      // file/rank disambiguation
      for (final Square fromSquare : fromSquares) {
        appendOnlyMove(set, toSquare, PieceType.KNIGHT);
        if (calculateIsFromFilePossibleKnight(fromSquare, fromSquares)) {
          appendMoveWithFile(set, toSquare, fromSquare.getFile(), PieceType.KNIGHT);
        }
        if (calculateIsFromRankPossibleKnight(fromSquare, fromSquares)) {
          appendMoveWithRank(set, toSquare, fromSquare.getRank(), PieceType.KNIGHT);
        }
      }

      // square disambiguation
      for (final Square fromSquare : fromSquares) {
        appendOnlyMove(set, toSquare, PieceType.KNIGHT);
        if (calculateIsFromRankPossibleKnight(fromSquare, fromSquares)
            && calculateHasOtherMovesFromSameRank(fromSquare, fromSquares)) {
          appendMoveWithFromSquare(set, toSquare, fromSquare, PieceType.KNIGHT);
        }
      }
    }
    VALUES = Nulls.copyOfSet(set);
  }

  private static boolean calculateIsFromRankPossibleKnight(Square fromSquare, List<Square> fromSquares) {
    for (final Square otherFromSquare : fromSquares) {
      if (otherFromSquare.getRank() != fromSquare.getRank() && otherFromSquare.getFile() == fromSquare.getFile()) {
        return true;
      }
    }
    return false;
  }

  private static boolean calculateIsFromFilePossibleKnight(Square fromSquare, List<Square> fromSquares) {
    for (final Square otherFromSquare : fromSquares) {
      if (otherFromSquare.getFile() != fromSquare.getFile()) {
        return true;
      }
    }
    return false;
  }

}
