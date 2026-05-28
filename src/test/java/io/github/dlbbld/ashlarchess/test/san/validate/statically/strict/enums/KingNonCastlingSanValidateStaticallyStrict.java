// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums;

import static io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.SanValidateStaticallyStrictHelpers.appendOnlyMove;

import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSet;

import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.model.EmptyBoardMove;
import io.github.dlbbld.ashlarchess.squares.AbstractEmptyBoardSquares;

@SuppressWarnings("null")
public abstract class KingNonCastlingSanValidateStaticallyStrict {

  public static final ImmutableSet<String> VALUES;

  static {
    final Set<String> set = new TreeSet<>();
    for (final Square toSquare : Square.REAL) {
      final Set<EmptyBoardMove> moves = AbstractEmptyBoardSquares.calculateNonPawnEmptyBoardMovesTo(PieceType.KING,
          toSquare);
      for (final EmptyBoardMove move : moves) {
        appendOnlyMove(set, move.toSquare(), PieceType.KING);
      }
    }
    VALUES = ImmutableSet.copyOf(set);
  }

}
