package com.dlb.chess.test.fen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.common.exceptions.FenRawValidationException;
import com.dlb.chess.fen.FenParserRaw;
import com.dlb.chess.fen.constants.FenConstants;

class TestFenParserRaw {
  @SuppressWarnings("static-method")
  @Test
  void testSuccessParseFields() {

    // 1. e4
    // 1... c6
    // 2. Nf3
    // 3... Nf6
    // 4. Rg1
    final String halfMove0 = FenConstants.FEN_INITIAL_STR;
    final String halfMove1 = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";
    final String halfMove2 = "rnbqkbnr/pp1ppppp/2p5/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2";
    final String halfMove3 = "rnbqkbnr/pp1ppppp/2p5/8/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2";
    final String halfMove4 = "rnbqkb1r/pp1ppppp/2p2n2/8/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3";
    final String halfMove5 = "rnbqkb1r/pp1ppppp/2p2n2/8/4P3/5N2/PPPP1PPP/RNBQKBR1 b Qkq - 3 3";

    assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR", parsePiecePlacement(halfMove0));
    assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR", parsePiecePlacement(halfMove1));
    assertEquals("rnbqkbnr/pp1ppppp/2p5/8/4P3/8/PPPP1PPP/RNBQKBNR", parsePiecePlacement(halfMove2));
    assertEquals("rnbqkbnr/pp1ppppp/2p5/8/4P3/5N2/PPPP1PPP/RNBQKB1R", parsePiecePlacement(halfMove3));
    assertEquals("rnbqkb1r/pp1ppppp/2p2n2/8/4P3/5N2/PPPP1PPP/RNBQKB1R", parsePiecePlacement(halfMove4));
    assertEquals("rnbqkb1r/pp1ppppp/2p2n2/8/4P3/5N2/PPPP1PPP/RNBQKBR1", parsePiecePlacement(halfMove5));

    assertEquals("w", parseHavingMove(halfMove0));
    assertEquals("b", parseHavingMove(halfMove1));
    assertEquals("w", parseHavingMove(halfMove2));
    assertEquals("b", parseHavingMove(halfMove3));
    assertEquals("w", parseHavingMove(halfMove4));
    assertEquals("b", parseHavingMove(halfMove5));

    assertEquals("KQkq", parseCastlingRight(halfMove0));
    assertEquals("KQkq", parseCastlingRight(halfMove1));
    assertEquals("KQkq", parseCastlingRight(halfMove2));
    assertEquals("KQkq", parseCastlingRight(halfMove3));
    assertEquals("KQkq", parseCastlingRight(halfMove4));
    assertEquals("Qkq", parseCastlingRight(halfMove5));

    assertEquals("-", parseEnPassantCaptureTargetSquare(halfMove0));
    assertEquals("e3", parseEnPassantCaptureTargetSquare(halfMove1));
    assertEquals("-", parseEnPassantCaptureTargetSquare(halfMove2));
    assertEquals("-", parseEnPassantCaptureTargetSquare(halfMove3));
    assertEquals("-", parseEnPassantCaptureTargetSquare(halfMove4));
    assertEquals("-", parseEnPassantCaptureTargetSquare(halfMove5));

    assertEquals("0", parseHalfMoveClock(halfMove0));
    assertEquals("0", parseHalfMoveClock(halfMove1));
    assertEquals("0", parseHalfMoveClock(halfMove2));
    assertEquals("1", parseHalfMoveClock(halfMove3));
    assertEquals("2", parseHalfMoveClock(halfMove4));
    assertEquals("3", parseHalfMoveClock(halfMove5));

    assertEquals("1", parseFullMoveNumber(halfMove0));
    assertEquals("1", parseFullMoveNumber(halfMove1));
    assertEquals("2", parseFullMoveNumber(halfMove2));
    assertEquals("2", parseFullMoveNumber(halfMove3));
    assertEquals("3", parseFullMoveNumber(halfMove4));
    assertEquals("3", parseFullMoveNumber(halfMove5));
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
      FenParserRaw.parseFenRaw(fen);
    } catch (@SuppressWarnings("unused") final FenRawValidationException e) {
      isException = true;
    }
    assertTrue(isException);

  }

  private static String parsePiecePlacement(String piecePlacement) {
    return FenParserRaw.parseFenRaw(piecePlacement).piecePlacement();
  }

  private static String parseHavingMove(String fen) {
    return FenParserRaw.parseFenRaw(fen).havingMove();
  }

  private static String parseCastlingRight(String fen) {
    return FenParserRaw.parseFenRaw(fen).castlingRightBothStr();
  }

  private static String parseEnPassantCaptureTargetSquare(String fen) {
    return FenParserRaw.parseFenRaw(fen).enPassantCaptureTargetSquare();
  }

  private static String parseHalfMoveClock(String fen) {
    return FenParserRaw.parseFenRaw(fen).halfMoveClock();
  }

  private static String parseFullMoveNumber(String fen) {
    return FenParserRaw.parseFenRaw(fen).fullMoveNumber();
  }

}
