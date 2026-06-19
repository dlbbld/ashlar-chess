// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.perft;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.bitboard.BitboardLegalMoveFactory;
import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.enums.CastlingRight;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.fen.StrictFenParser;
import io.github.dlbbld.ashlarchess.fen.model.Fen;
import io.github.dlbbld.ashlarchess.model.CastlingRightBoth;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.moves.CastlingUtility;
import io.github.dlbbld.ashlarchess.moves.EnPassantCaptureUtility;

/**
 * Perft (move-path enumeration) against the published node counts for the standard test positions from the
 * <a href="https://www.chessprogramming.org/Perft_Results">Chess Programming Wiki</a>. Perft counts the leaves of the
 * legal-move tree to a fixed depth; matching the community-agreed counts exercises every move-generation edge case at
 * once - castling legality (not through or out of check), en passant (including the rare pin where the capture exposes
 * the king), promotions, pinned pieces, check evasions, and discovered checks. A single generation bug shows up as a
 * wrong count at some depth.
 *
 * <p>
 * This is an independent published reference, complementing the {@code StaticPosition} differential oracle and the
 * python-chess / chesslib cross-validation - a different source of truth, and the artifact every chess library is
 * expected to pass.
 *
 * <p>
 * Counting runs on the bitboard generator directly (no per-move SAN / repetition / history overhead), so the depths
 * here stay fast in the default suite. The deep signature counts (initial {@code perft(6) = 119,060,324}; Kiwipete
 * {@code perft(5) = 193,690,690}) are intentionally out of scope for the unit suite.
 */
@SuppressWarnings("static-method")
class TestPerft {

  private static final String INITIAL = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
  private static final String KIWIPETE = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1";
  private static final String POSITION_3 = "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1";
  private static final String POSITION_4 = "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1";
  private static final String POSITION_5 = "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8";
  private static final String POSITION_6 = "r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10";

  @Test
  void perftInitialPosition() {
    assertPerft(INITIAL, 1, 20L);
    assertPerft(INITIAL, 2, 400L);
    assertPerft(INITIAL, 3, 8_902L);
    assertPerft(INITIAL, 4, 197_281L);
  }

  @Test
  void perftKiwipete() {
    assertPerft(KIWIPETE, 1, 48L);
    assertPerft(KIWIPETE, 2, 2_039L);
    assertPerft(KIWIPETE, 3, 97_862L);
  }

  @Test
  void perftPosition3() {
    assertPerft(POSITION_3, 1, 14L);
    assertPerft(POSITION_3, 2, 191L);
    assertPerft(POSITION_3, 3, 2_812L);
    assertPerft(POSITION_3, 4, 43_238L);
  }

  @Test
  void perftPosition4() {
    assertPerft(POSITION_4, 1, 6L);
    assertPerft(POSITION_4, 2, 264L);
    assertPerft(POSITION_4, 3, 9_467L);
  }

  @Test
  void perftPosition5() {
    assertPerft(POSITION_5, 1, 44L);
    assertPerft(POSITION_5, 2, 1_486L);
    assertPerft(POSITION_5, 3, 62_379L);
  }

  @Test
  void perftPosition6() {
    assertPerft(POSITION_6, 1, 46L);
    assertPerft(POSITION_6, 2, 2_079L);
    assertPerft(POSITION_6, 3, 89_890L);
  }

  private static void assertPerft(String fen, int depth, long expected) {
    final Fen parsedFen = StrictFenParser.parse(fen);
    final Square enPassantCaptureTargetSquare = parsedFen.enPassantCaptureTargetSquare();
    final long enPassantBit = enPassantCaptureTargetSquare == Square.NONE ? 0L
        : 1L << enPassantCaptureTargetSquare.ordinal();
    final long actual = perft(parsedFen.bitboardPosition(), parsedFen.sideToMove(), parsedFen.castlingRightWhite(),
        parsedFen.castlingRightBlack(), enPassantBit, depth);
    assertEquals(expected, actual, "perft(" + depth + ") for " + fen);
  }

  private static long perft(BitboardPosition position, Side sideToMove, CastlingRight castlingRightWhite,
      CastlingRight castlingRightBlack, long enPassantBit, int depth) {
    if (depth == 0) {
      return 1L;
    }
    final CastlingRight castlingRightSideToMove = sideToMove == Side.WHITE ? castlingRightWhite : castlingRightBlack;
    final ImmutableList<LegalMove> legalMoves = BitboardLegalMoveFactory.calculateLegalMoves(position, sideToMove,
        castlingRightSideToMove, enPassantBit);
    if (depth == 1) {
      return legalMoves.size();
    }
    final Side afterSideToMove = sideToMove.getOppositeSide();
    long nodes = 0L;
    for (final LegalMove legalMove : legalMoves) {
      final BitboardPosition afterPosition = position.afterMove(legalMove.moveSpecification(), sideToMove);
      final CastlingRightBoth afterCastlingRightBoth = CastlingUtility.calculateCastlingRightBoth(castlingRightWhite,
          castlingRightBlack, legalMove);
      final Square afterEnPassantCaptureTargetSquare = EnPassantCaptureUtility
          .calculateEnPassantCaptureTargetSquare(legalMove);
      final long afterEnPassantBit = afterEnPassantCaptureTargetSquare == Square.NONE ? 0L
          : 1L << afterEnPassantCaptureTargetSquare.ordinal();
      nodes += perft(afterPosition, afterSideToMove, afterCastlingRightBoth.castlingRightWhite(),
          afterCastlingRightBoth.castlingRightBlack(), afterEnPassantBit, depth - 1);
    }
    return nodes;
  }
}
