// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums;

import static io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.SanValidateStaticallyStrictHelpers.appendMoveWithFile;
import static io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.SanValidateStaticallyStrictHelpers.appendOnlyMove;

import java.util.Set;
import java.util.TreeSet;

import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.moves.EmptyBoardMove;
import io.github.dlbbld.ashlarchess.squares.EmptyBoardMoveUtility;
import io.github.dlbbld.ashlarchess.squares.PawnDiagonalSquares;

public final class PawnWhiteSanValidateStaticallyStrict {

  private PawnWhiteSanValidateStaticallyStrict() {
  }

  public static final Set<String> VALUES;

  static {
    final Set<String> set = new TreeSet<>();

    // one and two-square pawn advances
    for (final EmptyBoardMove move : EmptyBoardMoveUtility.calculatePawnEmptyBoardMoves(Side.WHITE)) {
      appendOnlyMove(set, move.toSquare(), PieceType.PAWN);
    }

    // diagonal captures (file-disambiguated form)
    for (final Square fromSquare : Square.REAL) {
      for (final Square diagonalSquare : PawnDiagonalSquares.getPawnDiagonalSquares(Side.WHITE, fromSquare)) {
        appendMoveWithFile(set, diagonalSquare, fromSquare.getFile(), PieceType.PAWN);
      }
    }

    VALUES = Nulls.copyOfSet(set);
  }

}
