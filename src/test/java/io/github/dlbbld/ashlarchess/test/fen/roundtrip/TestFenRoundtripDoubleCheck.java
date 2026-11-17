// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.fen.roundtrip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.LegalMove;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.exceptions.UsageException;
import io.github.dlbbld.ashlarchess.internal.Nulls;

/**
 * A position reached by a legal move must be accepted by the FEN parsers. Double checks are where that can fail: strict
 * FEN rejects check configurations that no single move can create, and a promotion or an en passant capture creates
 * double checks that an ordinary move cannot - two rooks, two queens, a queen and a rook on a rank and a file.
 */
class TestFenRoundtripDoubleCheck {

  @SuppressWarnings("static-method")
  @Test
  void testPromotionDoubleChecks() {
    // Promoted queen on the diagonal, queen on the rank.
    checkDoubleCheckRoundtrip("8/5kPQ/8/8/8/8/8/4K3 w - - 0 1", "g7g8q", "6Q1/5k1Q/8/8/8/8/8/4K3 b - - 0 1");
    // Capture promotion next to the king's file: both checkers orthogonal, on the king's file and rank.
    checkDoubleCheckRoundtrip("7n/R5Pk/8/8/8/8/8/4K3 w - - 0 1", "g7h8r", "7R/R6k/8/8/8/8/8/4K3 b - - 0 1");
    checkDoubleCheckRoundtrip("7n/R5Pk/8/8/8/8/PPPPPPP1/4K3 w - - 0 30", "g7h8r",
        "7R/R6k/8/8/8/8/PPPPPPP1/4K3 b - - 0 30");
    checkDoubleCheckRoundtrip("7n/R5Pk/8/8/8/8/PPPPPPP1/R3K3 w - - 0 30", "g7h8r",
        "7R/R6k/8/8/8/8/PPPPPPP1/R3K3 b - - 0 30");
    checkDoubleCheckRoundtrip("7n/R5Pk/8/8/8/8/8/4K3 w - - 0 1", "g7h8q", "7Q/R6k/8/8/8/8/8/4K3 b - - 0 1");
    checkDoubleCheckRoundtrip("7n/Q5Pk/8/8/8/8/8/4K3 w - - 0 1", "g7h8q", "7Q/Q6k/8/8/8/8/8/4K3 b - - 0 1");
    // King on the promotion rank: the pawn leaves the king's file by capturing beside the king.
    checkDoubleCheckRoundtrip("3nk3/4P3/8/8/8/8/8/4RK2 w - - 0 1", "e7d8r", "3Rk3/8/8/8/8/8/8/4RK2 b - - 0 1");
    // Promoted rook on the king's file, uncovered queen on the diagonal behind the pawn's origin square.
    checkDoubleCheckRoundtrip("4Q1n1/5P2/6k1/8/8/8/8/4K3 w - - 0 1", "f7g8r", "4Q1R1/8/6k1/8/8/8/8/4K3 b - - 0 1");
    // Black to move.
    checkDoubleCheckRoundtrip("4k3/8/8/8/8/8/r5pK/7N b - - 0 1", "g2h1r", "4k3/8/8/8/8/8/r6K/7r w - - 0 2");
    checkDoubleCheckRoundtrip("4k3/8/8/8/8/8/5KpQ/8 b - - 0 1", "g2g1q", "4k3/8/8/8/8/8/5K1Q/6q1 w - - 0 2");
  }

  @SuppressWarnings("static-method")
  @Test
  void testEnPassantDoubleCheckByTwoQueens() {
    // The capturing pawn leaves the e-file and the captured pawn leaves the a2-g8 diagonal.
    checkDoubleCheckRoundtrip("8/8/4k3/3pP3/8/8/Q7/4QK2 w - d6 0 2", "e5d6", "8/8/3Pk3/8/8/8/Q7/4QK2 b - - 0 2");
    checkDoubleCheckRoundtrip("4qk2/q7/8/8/3Pp3/4K3/8/8 b - d3 0 1", "e4d3", "4qk2/q7/8/8/8/3pK3/8/8 w - - 0 2");
  }

  /**
   * Every promotion from a position with one uncovered slider behind the pawn: king anywhere, pawn on its seventh rank
   * between the king and a queen, rook or bishop, with and without an enemy piece to capture on the promotion rank.
   */
  @SuppressWarnings("static-method")
  @Test
  void testGeneratedPromotionDoubleChecks() {
    int doubleChecks = 0;
    for (final Side attacker : Side.REAL) {
      for (int kingFile = 1; kingFile <= 8; kingFile++) {
        for (int kingRank = 1; kingRank <= 8; kingRank++) {
          for (int pawnFile = 1; pawnFile <= 8; pawnFile++) {
            doubleChecks += countPromotionDoubleChecks(attacker, kingFile, kingRank, pawnFile);
          }
        }
      }
    }
    assertTrue(doubleChecks > 1000, "Expected the enumeration to reach double checks, got " + doubleChecks);
  }

  // Coordinates are from the attacker's point of view (its pawn promotes on rank 8); toFen mirrors them for Black.
  private static int countPromotionDoubleChecks(Side attacker, int kingFile, int kingRank, int pawnFile) {
    final int pawnRank = 7;
    final int fileStep = Integer.signum(pawnFile - kingFile);
    final int rankStep = Integer.signum(pawnRank - kingRank);
    final boolean isAligned = kingFile == pawnFile || kingRank == pawnRank
        || Math.abs(kingFile - pawnFile) == Math.abs(kingRank - pawnRank);
    if (!isAligned || fileStep == 0 && rankStep == 0) {
      return 0;
    }
    final boolean isDiagonal = fileStep != 0 && rankStep != 0;

    int doubleChecks = 0;
    for (int distance = 1; distance < 8; distance++) {
      final int sliderFile = pawnFile + distance * fileStep;
      final int sliderRank = pawnRank + distance * rankStep;
      if (sliderFile < 1 || sliderFile > 8 || sliderRank < 1 || sliderRank > 8) {
        break;
      }
      for (final char slider : new char[] { 'Q', isDiagonal ? 'B' : 'R' }) {
        // 0: no piece to capture; otherwise the file of an enemy knight on the promotion rank.
        for (final int capturedFile : new int[] { 0, pawnFile - 1, pawnFile + 1 }) {
          final char[][] squares = new char[9][9];
          squares[kingFile][kingRank] = 'k';
          squares[pawnFile][pawnRank] = 'P';
          squares[sliderFile][sliderRank] = slider;
          if (capturedFile != 0) {
            if (capturedFile < 1 || capturedFile > 8 || squares[capturedFile][8] != 0) {
              continue;
            }
            squares[capturedFile][8] = 'n';
          }
          final int ownKingFile = squares[1][1] == 0 && (kingFile > 2 || kingRank > 2) ? 1 : 8;
          if (squares[ownKingFile][1] != 0) {
            continue;
          }
          squares[ownKingFile][1] = 'K';
          doubleChecks += countDoubleChecksAfterPromotion(toFen(squares, attacker));
        }
      }
    }
    return doubleChecks;
  }

  private static int countDoubleChecksAfterPromotion(String fen) {
    final Board board;
    try {
      board = Board.fromFenStrict(fen);
    } catch (@SuppressWarnings("unused") final UsageException e) {
      // The enumeration also builds illegal positions, such as adjacent kings or a king already in check.
      return 0;
    }
    int doubleChecks = 0;
    for (final LegalMove legalMove : board.getLegalMoves()) {
      if (!legalMove.isPromotion()) {
        continue;
      }
      board.move(legalMove.moveSpecification());
      final String fenAfter = board.getFen();
      final Side sideInCheck = board.getSideToMove();
      final boolean isDoubleCheck = Long.bitCount(board.getBitboardPosition()
          .attackersTo(board.getBitboardPosition().kingSquare(sideInCheck), sideInCheck.getOppositeSide())) == 2;
      board.unmove();
      if (isDoubleCheck) {
        doubleChecks++;
        assertEquals(fenAfter, Board.fromFenStrict(fenAfter).getFen(), fen + " " + legalMove.moveSpecification());
      }
    }
    return doubleChecks;
  }

  private static String toFen(char[][] squares, Side attacker) {
    final boolean isWhite = attacker == Side.WHITE;
    final StringBuilder fen = new StringBuilder();
    for (int row = 8; row >= 1; row--) {
      final int rank = isWhite ? row : 9 - row;
      int emptyCount = 0;
      for (int file = 1; file <= 8; file++) {
        final char piece = squares[file][rank];
        if (piece == 0) {
          emptyCount++;
          continue;
        }
        if (emptyCount > 0) {
          fen.append(emptyCount);
          emptyCount = 0;
        }
        final boolean isAttackerPiece = Character.isUpperCase(piece);
        fen.append(isAttackerPiece == isWhite ? Character.toUpperCase(piece) : Character.toLowerCase(piece));
      }
      if (emptyCount > 0) {
        fen.append(emptyCount);
      }
      if (row > 1) {
        fen.append('/');
      }
    }
    fen.append(isWhite ? " w - - 0 1" : " b - - 0 1");
    return Nulls.toString(fen);
  }

  private static void checkDoubleCheckRoundtrip(String fenBefore, String uciMove, String expectedFenAfter) {
    final Board board = Board.fromFenStrict(fenBefore);
    board.moveLenient(uciMove);
    assertTrue(board.isCheck());
    assertEquals(expectedFenAfter, board.getFen());
    assertEquals(expectedFenAfter, Board.fromFenStrict(expectedFenAfter).getFen());
    assertEquals(expectedFenAfter, Board.fromFenLenient(expectedFenAfter).getFen());
  }
}
