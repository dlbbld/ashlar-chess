// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.fen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.fen.internal.FenConstants;

class TestStrictFenFieldParser {
  @SuppressWarnings("static-method")
  @Test
  void testSuccessParseFields() {

    // 1. e4
    // 1... c6
    // 2. Nf3
    // 3... Nf6
    // 4. Rg1
    final String move0 = FenConstants.FEN_INITIAL_STR;
    final String move1 = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";
    final String move2 = "rnbqkbnr/pp1ppppp/2p5/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2";
    final String move3 = "rnbqkbnr/pp1ppppp/2p5/8/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2";
    final String move4 = "rnbqkb1r/pp1ppppp/2p2n2/8/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3";
    final String move5 = "rnbqkb1r/pp1ppppp/2p2n2/8/4P3/5N2/PPPP1PPP/RNBQKBR1 b Qkq - 3 3";

    assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR", parsePiecePlacement(move0));
    assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR", parsePiecePlacement(move1));
    assertEquals("rnbqkbnr/pp1ppppp/2p5/8/4P3/8/PPPP1PPP/RNBQKBNR", parsePiecePlacement(move2));
    assertEquals("rnbqkbnr/pp1ppppp/2p5/8/4P3/5N2/PPPP1PPP/RNBQKB1R", parsePiecePlacement(move3));
    assertEquals("rnbqkb1r/pp1ppppp/2p2n2/8/4P3/5N2/PPPP1PPP/RNBQKB1R", parsePiecePlacement(move4));
    assertEquals("rnbqkb1r/pp1ppppp/2p2n2/8/4P3/5N2/PPPP1PPP/RNBQKBR1", parsePiecePlacement(move5));

    assertEquals("w", parseSideToMove(move0));
    assertEquals("b", parseSideToMove(move1));
    assertEquals("w", parseSideToMove(move2));
    assertEquals("b", parseSideToMove(move3));
    assertEquals("w", parseSideToMove(move4));
    assertEquals("b", parseSideToMove(move5));

    assertEquals("KQkq", parseCastlingRight(move0));
    assertEquals("KQkq", parseCastlingRight(move1));
    assertEquals("KQkq", parseCastlingRight(move2));
    assertEquals("KQkq", parseCastlingRight(move3));
    assertEquals("KQkq", parseCastlingRight(move4));
    assertEquals("Qkq", parseCastlingRight(move5));

    assertEquals("-", parseEnPassantCaptureTargetSquare(move0));
    assertEquals("e3", parseEnPassantCaptureTargetSquare(move1));
    assertEquals("-", parseEnPassantCaptureTargetSquare(move2));
    assertEquals("-", parseEnPassantCaptureTargetSquare(move3));
    assertEquals("-", parseEnPassantCaptureTargetSquare(move4));
    assertEquals("-", parseEnPassantCaptureTargetSquare(move5));

    assertEquals("0", parseHalfMoveClock(move0));
    assertEquals("0", parseHalfMoveClock(move1));
    assertEquals("0", parseHalfMoveClock(move2));
    assertEquals("1", parseHalfMoveClock(move3));
    assertEquals("2", parseHalfMoveClock(move4));
    assertEquals("3", parseHalfMoveClock(move5));

    assertEquals("1", parseFullMoveNumber(move0));
    assertEquals("1", parseFullMoveNumber(move1));
    assertEquals("2", parseFullMoveNumber(move2));
    assertEquals("2", parseFullMoveNumber(move3));
    assertEquals("3", parseFullMoveNumber(move4));
    assertEquals("3", parseFullMoveNumber(move5));
  }

  @SuppressWarnings("static-method")
  @Test
  void testException() {
    checkException(" rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    checkException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR  w KQkq - 0 1");
    checkException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w  KQkq - 0 1");
    checkException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq  - 0 1");
    checkException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -  0 1");
    checkException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0  1");
    checkException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 ");

    checkException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNRw KQkq - 0 1");
    checkException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR wKQkq - 0 1");
    checkException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq- 0 1");
    checkException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -0 1");
    checkException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 01");
  }

  private static void checkException(String fen) {
    boolean isException = false;
    try {
      StrictFenFieldParser.parse(fen);
    } catch (@SuppressWarnings("unused") final StrictFenFieldValidationException e) {
      isException = true;
    }
    assertTrue(isException);

  }

  private static String parsePiecePlacement(String piecePlacement) {
    return StrictFenFieldParser.parse(piecePlacement).piecePlacement();
  }

  private static String parseSideToMove(String fen) {
    return StrictFenFieldParser.parse(fen).sideToMove();
  }

  private static String parseCastlingRight(String fen) {
    return StrictFenFieldParser.parse(fen).castlingRightBoth();
  }

  private static String parseEnPassantCaptureTargetSquare(String fen) {
    return StrictFenFieldParser.parse(fen).enPassantCaptureTargetSquare();
  }

  private static String parseHalfMoveClock(String fen) {
    return StrictFenFieldParser.parse(fen).halfMoveClock();
  }

  private static String parseFullMoveNumber(String fen) {
    return StrictFenFieldParser.parse(fen).fullMoveNumber();
  }

}
