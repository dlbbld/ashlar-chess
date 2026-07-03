// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.exceptions.UsageException;
import io.github.dlbbld.ashlarchess.internal.Nulls;

// Tests for the Figure 8 semi-static check, ported from the fun22-reference unit tests. The core test is a soundness
// sweep: over every ground-truth vector of chasolver's curated test set (community-submitted queries with verified
// winnability labels; it carries every cha C++ test vector except the orphan adopted in pgn/cha/various), whenever
// the semi-static check declares the position unwinnable for a player, that player must indeed be unable to helpmate
// (Theorem 12 soundness). It asserts only the implication UNWINNABLE => truly unwinnable; it never requires the
// check to be complete.
class TestUnwinnableSemiStatic {

  private static boolean unwinnable(String fen, Side winner) {
    final SemiStaticPosition position = SemiStaticPosition.fromBoard(Board.fromFenStrict(fen));
    return UnwinnableSemiStatic.unwinnableSemiStatic(position, winner, Mobility.mobility(position));
  }

  @SuppressWarnings("static-method")
  @Test
  void winnablePositionIsNotDeclaredUnwinnable() {
    // K+Q vs K: White can mate, and the kings can approach each other, so the semi-static check must decline.
    assertFalse(unwinnable("4k3/8/8/8/8/8/1Q6/4K3 w - - 0 1", Side.WHITE));
  }

  @SuppressWarnings("static-method")
  @Test
  void blockedFortressIsProvedUnwinnableForBoth() {
    final String fen = "2b1k3/8/8/1p1p1p1p/1P1P1P1P/8/8/2B1K3 w - - 0 1";
    assertTrue(unwinnable(fen, Side.WHITE));
    assertTrue(unwinnable(fen, Side.BLACK));
  }

  @SuppressWarnings("static-method")
  @Test
  void soundnessSweepOverGroundTruthVectors() throws Exception {
    int analysed = 0;
    int skipped = 0;
    int caught = 0;

    try (InputStream in = TestUnwinnableSemiStatic.class.getResourceAsStream("/oracle/chasolver/curated/positions.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.isBlank() || line.charAt(0) == '#') {
          continue;
        }
        final char whiteLabel = line.charAt(0); // 'W' if White can helpmate, else '-'
        final char blackLabel = line.charAt(1); // 'B' if Black can helpmate, else '-'
        final String fen = normalizeFen(Nulls.trim(Nulls.substring(line, 3)));

        final Board board;
        try {
          board = Board.fromFenStrict(fen);
        } catch (@SuppressWarnings("unused") final UsageException illegal) {
          skipped++; // strict validation rejects some vectors as illegal; out of the analyzers' domain
          continue;
        }
        analysed++;
        final SemiStaticPosition position = SemiStaticPosition.fromBoard(board);
        final MobilitySolution mobilitySolution = Mobility.mobility(position);

        if (UnwinnableSemiStatic.unwinnableSemiStatic(position, Side.WHITE, mobilitySolution)) {
          caught++;
          if (whiteLabel != '-') {
            fail("UNSOUND: declared unwinnable for White but White can helpmate: " + fen);
          }
        }
        if (UnwinnableSemiStatic.unwinnableSemiStatic(position, Side.BLACK, mobilitySolution)) {
          caught++;
          if (blackLabel != '-') {
            fail("UNSOUND: declared unwinnable for Black but Black can helpmate: " + fen);
          }
        }
      }
    }

    // Coverage guards (deterministic: committed corpus). A silent drop in `caught` would make the soundness check
    // pass vacuously; pin the counts and update them deliberately if the corpus or the algorithm changes.
    assertEquals(3151, analysed, "corpus positions analysed changed (was 3151)");
    assertEquals(263, skipped, "strict-FEN-rejected positions changed (was 263)");
    assertEquals(985, caught, "semi-static UNWINNABLE coverage changed (was 985)");
  }

  /** The corpus FENs omit the clock fields; append them so strict parsing accepts the FEN. */
  private static String normalizeFen(String fen) {
    final int fields = fen.split("\\s+").length;
    if (fields == 4) {
      return fen + " 0 1";
    }
    if (fields == 5) {
      return fen + " 1";
    }
    return fen;
  }
}
