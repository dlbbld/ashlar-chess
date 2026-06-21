// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.enums.File;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Rank;
import io.github.dlbbld.ashlarchess.board.enums.Square;

/**
 * Pins the conventional-notation {@code toString()} contract added to the core board enums in 19.0.0: a chess-readable
 * representation (algebraic square, file letter, rank number, FEN piece letter) with {@code "none"} for the
 * {@code NONE} sentinels. The rest of the suite only proves nothing depended on the previous (constant-name) output;
 * this locks the new public output.
 */
@SuppressWarnings("static-method")
class TestEnumToString {

  @Test
  void squareToStringIsAlgebraic() {
    assertEquals("a1", Square.A1.toString());
    assertEquals("h8", Square.H8.toString());
    assertEquals("none", Square.NONE.toString());
  }

  @Test
  void fileToStringIsLetter() {
    assertEquals("a", File.FILE_A.toString());
    assertEquals("h", File.FILE_H.toString());
    assertEquals("none", File.NONE.toString());
  }

  @Test
  void rankToStringIsNumber() {
    assertEquals("1", Rank.RANK_1.toString());
    assertEquals("8", Rank.RANK_8.toString());
    assertEquals("none", Rank.NONE.toString());
  }

  @Test
  void pieceToStringIsFenLetter() {
    assertEquals("P", Piece.WHITE_PAWN.toString());
    assertEquals("p", Piece.BLACK_PAWN.toString());
    assertEquals("K", Piece.WHITE_KING.toString());
    assertEquals("q", Piece.BLACK_QUEEN.toString());
    assertEquals("none", Piece.NONE.toString());
  }

  @Test
  void pieceTypeToStringIsLetter() {
    assertEquals("P", PieceType.PAWN.toString());
    assertEquals("N", PieceType.KNIGHT.toString());
    assertEquals("K", PieceType.KING.toString());
    assertEquals("none", PieceType.NONE.toString());
  }
}
