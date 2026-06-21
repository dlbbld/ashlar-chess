// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.CastlingMove;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
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
 * <p>
 * Any failure is a colour-handedness bug - the kind a colour-symmetric corpus can never surface (it would be wrong the
 * same way for both sides). The mirror is built purely from the FEN (reverse ranks, swap piece case, swap side, swap
 * castling, flip the en-passant rank), and the test asserts it is a true involution (mirror twice == identity) so the
 * mirror itself can't silently be wrong.
 */
class TestMirrorSymmetry {

  private static final int MAX_CORE_POSITIONS = 6000;
  private static final int MAX_UNWINNABLE_POSITIONS = 100;
  private static final int MAX_GAME_MIRROR_PLIES = 20000;

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

  @SuppressWarnings("static-method")
  @Test
  void mirroringAWholeGameTracksTheMirrorOfEveryPosition() {
    // Replay each game on the original board and a mirror board (mirror of the start FEN), playing the mirror of every
    // move. This tests that move EXECUTION commutes with mirroring -- castling rook movement, en-passant capture,
    // promotion, the halfmove clock, and the repetition counter -- which the per-position generation test cannot reach.
    // Non-initial-start PGNs are handled naturally by mirroring the start FEN.
    int gamesExercised = 0;
    int pliesExercised = 0;
    for (final PgnTestCaseList testCaseList : PgnTestCaseCatalog.getParserIntegrationSmokeTests()) {
      for (final PgnFen testCase : testCaseList.list()) {
        final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(testCaseList.pgnTest().getFolderPath(),
            testCase.pgnName());
        final Board original = new Board(pgnGame.startFen());
        final Board mirror = Board.fromFenStrict(mirrorFen(original.getFen()));
        assertPositionMirror(original, mirror, testCase.pgnName(), 0);

        int ply = 0;
        for (final PgnMove move : pgnGame.moves()) {
          final MoveSpecification specification = original.moveStrict(move.san());
          playMirrorMove(mirror, specification, testCase.pgnName(), move.san());
          ply++;
          assertPositionMirror(original, mirror, testCase.pgnName(), ply);
          pliesExercised++;
        }
        gamesExercised++;
        if (pliesExercised >= MAX_GAME_MIRROR_PLIES) {
          assertExercised(gamesExercised);
          return;
        }
      }
    }
    assertExercised(gamesExercised);
  }

  private static void assertExercised(int gamesExercised) {
    if (gamesExercised == 0) {
      fail("no games exercised - corpus mis-configured");
    }
  }

  private static void playMirrorMove(Board mirror, MoveSpecification specification, String pgnName, String san) {
    final MoveSpecification mirrored = mirrorMove(specification);
    try {
      mirror.move(mirrored);
    } catch (final RuntimeException e) {
      fail("mirrored move " + san + " (" + mirrored + ") was illegal on the mirror board in " + pgnName + ": "
          + e.getMessage());
    }
  }

  private static MoveSpecification mirrorMove(MoveSpecification move) {
    if (move.castlingMove() != CastlingMove.NONE) {
      // King-side / queen-side is file-based, so vertical flip + colour swap keeps the same castling side.
      return new MoveSpecification(move.castlingMove());
    }
    return new MoveSpecification(verticalFlip(move.fromSquare()), verticalFlip(move.toSquare()), CastlingMove.NONE,
        move.promotionPieceType());
  }

  // Vertical flip (rank r -> 7-r, file preserved), matching the position mirror. NOT SquareUtility.rotate180, which is
  // a 180-degree rotation (file also mirrored). ordinal = file + 8*rank, so XOR 0b111000 flips only the rank.
  private static Square verticalFlip(Square square) {
    return Square.values()[square.ordinal() ^ 0b111000];
  }

  private static void assertPositionMirror(Board original, Board mirror, String pgnName, int ply) {
    final String[] expected = Nulls.split(mirrorFen(original.getFen()), "\\s+");
    final String[] actual = Nulls.split(mirror.getFen(), "\\s+");
    for (int i = 0; i < 4; i++) {
      final int field = i;
      assertEquals(Nulls.get(expected, i), Nulls.get(actual, i),
          () -> "mirror desync at FEN field " + field + " after ply " + ply + " in " + pgnName);
    }
    assertEquals(original.getHalfMoveClock(), mirror.getHalfMoveClock(),
        () -> "halfmove clock not mirror-invariant after ply " + ply + " in " + pgnName);
    assertEquals(original.getRepetitionCount(), mirror.getRepetitionCount(),
        () -> "repetition count not mirror-invariant after ply " + ply + " in " + pgnName);
    assertEquals(original.getLegalMoves().size(), mirror.getLegalMoves().size(),
        () -> "legal-move count not symmetric after ply " + ply + " in " + pgnName);
  }

  /**
   * Collects FENs from the smoke corpora. When {@code finalPositionsOnly}, takes each game's final (endgame) position.
   */
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
    final String[] original = Nulls.split(Nulls.trim(fen), "\\s+");
    final String[] doubled = Nulls.split(mirrorFen(mirrorFen(fen)), "\\s+");
    // Fields 0-4 (placement, side, castling, en-passant, halfmove clock) must round-trip; field 5 (fullmove) is
    // deliberately normalised by the mirror, so it is excluded.
    for (int i = 0; i < 5; i++) {
      final int field = i;
      assertEquals(Nulls.get(original, i), Nulls.get(doubled, i),
          () -> "mirror is not an involution at field " + field + " for: " + fen);
    }
  }

  // ---- FEN mirror: vertical flip + colour swap (the same position from the other side) ----

  private static String mirrorFen(String fen) {
    final String[] fields = Nulls.split(Nulls.trim(fen), "\\s+");
    final String placement = mirrorPlacement(Nulls.get(fields, 0));
    final String side = "w".equals(Nulls.get(fields, 1)) ? "b" : "w";
    final String castling = mirrorCastling(Nulls.get(fields, 2));
    final String enPassant = mirrorEnPassant(Nulls.get(fields, 3));
    // Keep the halfmove clock (clock-sensitive predicates must compare like-for-like); the fullmove number is metadata
    // that affects no rule, so normalise it to one that is consistent with the flipped side and any clock (clock+1
    // always satisfies clock <= plies-implied-by-fullmove for either side to move).
    final int halfMoveClock = Integer.parseInt(Nulls.get(fields, 4));
    return placement + " " + side + " " + castling + " " + enPassant + " " + halfMoveClock + " " + (halfMoveClock + 1);
  }

  private static String mirrorPlacement(String placement) {
    final String[] ranks = Nulls.split(placement, "/");
    final StringBuilder result = new StringBuilder();
    for (int i = ranks.length - 1; i >= 0; i--) {
      if (result.length() > 0) {
        result.append('/');
      }
      result.append(swapCase(Nulls.get(ranks, i)));
    }
    return Nulls.toString(result);
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
    return result.length() == 0 ? "-" : Nulls.toString(result);
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
    return Nulls.toString(result);
  }
}
