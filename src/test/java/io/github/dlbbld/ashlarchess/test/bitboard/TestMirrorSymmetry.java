// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.model.PgnMove;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;

/**
 * Mirror-symmetry property test. For any position P, the vertical flip + colour swap M (the same position seen from the
 * other side) must preserve every rule-level fact under a side swap:
 *
 * <ul>
 * <li>{@code |legalMoves(P)|} == {@code |legalMoves(M)|}</li>
 * <li>check / checkmate / stalemate match</li>
 * <li>insufficient material matches with colours swapped</li>
 * <li>(sampled) the unwinnableQuick verdict matches with colours swapped</li>
 * </ul>
 *
 * Any failure is a colour-handedness bug - the kind a colour-symmetric corpus can never surface (it would be wrong the
 * same way for both sides). The mirror is built purely from the FEN (reverse ranks, swap piece case, swap side, swap
 * castling, flip the en-passant rank), and the test asserts it is a true involution (mirror twice == identity) so the
 * mirror itself can't silently be wrong.
 */
class TestMirrorSymmetry {

  private static final int MAX_CORE_POSITIONS = 6000;
  private static final int MAX_UNWINNABLE_POSITIONS = 100;

  @SuppressWarnings("static-method")
  @Test
  void mirrorPreservesMoveCountAndRulePredicates() {
    final List<String> fens = collectFens(MAX_CORE_POSITIONS, false);
    if (fens.isEmpty()) {
      fail("no positions collected - corpus mis-configured");
    }
    for (final String fen : fens) {
      assertMirrorInvolution(fen);
      final Board board = Board.fromFenStrict(fen);
      final Board mirror = Board.fromFenStrict(mirrorFen(fen));

      assertEquals(board.getLegalMoves().size(), mirror.getLegalMoves().size(),
          () -> "legal-move count not symmetric under mirror: " + fen);
      assertEquals(board.isCheck(), mirror.isCheck(), () -> "check not symmetric: " + fen);
      assertEquals(board.isCheckmate(), mirror.isCheckmate(), () -> "checkmate not symmetric: " + fen);
      assertEquals(board.isStalemate(), mirror.isStalemate(), () -> "stalemate not symmetric: " + fen);
      assertEquals(board.isInsufficientMaterial(Side.WHITE), mirror.isInsufficientMaterial(Side.BLACK),
          () -> "white insufficient material != mirror black: " + fen);
      assertEquals(board.isInsufficientMaterial(Side.BLACK), mirror.isInsufficientMaterial(Side.WHITE),
          () -> "black insufficient material != mirror white: " + fen);
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void mirrorPreservesUnwinnableQuickVerdict() {
    final List<String> fens = collectFens(MAX_UNWINNABLE_POSITIONS, true);
    if (fens.isEmpty()) {
      fail("no endgame positions collected - corpus mis-configured");
    }
    for (final String fen : fens) {
      final Board board = Board.fromFenStrict(fen);
      final Board mirror = Board.fromFenStrict(mirrorFen(fen));
      assertEquals(board.unwinnableQuick(Side.WHITE), mirror.unwinnableQuick(Side.BLACK),
          () -> "unwinnableQuick(WHITE) != mirror unwinnableQuick(BLACK): " + fen);
      assertEquals(board.unwinnableQuick(Side.BLACK), mirror.unwinnableQuick(Side.WHITE),
          () -> "unwinnableQuick(BLACK) != mirror unwinnableQuick(WHITE): " + fen);
    }
  }

  /** Collects FENs from the smoke corpora. When {@code finalPositionsOnly}, takes each game's final (endgame) position. */
  private static List<String> collectFens(int max, boolean finalPositionsOnly) {
    final List<String> fens = new ArrayList<>();
    for (final PgnTestCaseList testCaseList : PgnTestCaseCatalog.getParserIntegrationSmokeTests()) {
      for (final PgnFen testCase : testCaseList.list()) {
        final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(testCaseList.pgnTest().getFolderPath(),
            testCase.pgnName());
        final Board board = new Board(pgnGame.startFen());
        if (!finalPositionsOnly) {
          fens.add(board.getFen());
        }
        for (final PgnMove move : pgnGame.moves()) {
          board.moveStrict(move.san());
          if (!finalPositionsOnly) {
            fens.add(board.getFen());
            if (fens.size() >= max) {
              return fens;
            }
          }
        }
        if (finalPositionsOnly) {
          fens.add(board.getFen());
          if (fens.size() >= max) {
            return fens;
          }
        }
      }
    }
    return fens;
  }

  private static void assertMirrorInvolution(String fen) {
    final String[] original = fen.trim().split("\\s+");
    final String[] doubled = mirrorFen(mirrorFen(fen)).split("\\s+");
    // Fields 0-4 (placement, side, castling, en-passant, halfmove clock) must round-trip; field 5 (fullmove) is
    // deliberately normalised by the mirror, so it is excluded.
    for (int i = 0; i < 5; i++) {
      final int field = i;
      assertEquals(original[i], doubled[i], () -> "mirror is not an involution at field " + field + " for: " + fen);
    }
  }

  // ---- FEN mirror: vertical flip + colour swap (the same position from the other side) ----

  private static String mirrorFen(String fen) {
    final String[] fields = fen.trim().split("\\s+");
    final String placement = mirrorPlacement(fields[0]);
    final String side = "w".equals(fields[1]) ? "b" : "w";
    final String castling = mirrorCastling(fields[2]);
    final String enPassant = mirrorEnPassant(fields[3]);
    // Keep the halfmove clock (clock-sensitive predicates must compare like-for-like); the fullmove number is metadata
    // that affects no rule, so normalise it to one that is consistent with the flipped side and any clock (clock+1
    // always satisfies clock <= plies-implied-by-fullmove for either side to move).
    final int halfMoveClock = Integer.parseInt(fields[4]);
    return placement + " " + side + " " + castling + " " + enPassant + " " + halfMoveClock + " " + (halfMoveClock + 1);
  }

  private static String mirrorPlacement(String placement) {
    final String[] ranks = placement.split("/");
    final StringBuilder result = new StringBuilder();
    for (int i = ranks.length - 1; i >= 0; i--) {
      if (result.length() > 0) {
        result.append('/');
      }
      result.append(swapCase(ranks[i]));
    }
    return result.toString();
  }

  private static String mirrorCastling(String castling) {
    if ("-".equals(castling)) {
      return "-";
    }
    final String swapped = swapCase(castling);
    final StringBuilder result = new StringBuilder();
    for (final char canonical : new char[] { 'K', 'Q', 'k', 'q' }) {
      if (swapped.indexOf(canonical) >= 0) {
        result.append(canonical);
      }
    }
    return result.length() == 0 ? "-" : result.toString();
  }

  private static String mirrorEnPassant(String enPassant) {
    if ("-".equals(enPassant)) {
      return "-";
    }
    final char file = enPassant.charAt(0);
    final char flippedRank = (char) ('0' + (9 - (enPassant.charAt(1) - '0')));
    return "" + file + flippedRank;
  }

  private static String swapCase(String text) {
    final StringBuilder result = new StringBuilder(text.length());
    for (int i = 0; i < text.length(); i++) {
      final char c = text.charAt(i);
      if (Character.isUpperCase(c)) {
        result.append(Character.toLowerCase(c));
      } else if (Character.isLowerCase(c)) {
        result.append(Character.toUpperCase(c));
      } else {
        result.append(c);
      }
    }
    return result.toString();
  }
}
