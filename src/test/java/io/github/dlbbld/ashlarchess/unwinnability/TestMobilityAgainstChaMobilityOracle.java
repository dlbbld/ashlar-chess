// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;

// Internal-layer oracle for the Figure 6/7 mobility fixpoint, against the archived CHA C++ mobility dump
// (oracle/cha/ashlar-pgn/mobility.tsv, one row per piece per distinct corpus position).
//
// The contract is an IMPLICATION, not equality: cha evolved beyond the paper (text footnote 12) and layers extra
// tightening ("steady pieces" refinements) below the paper's fixpoint, so cha's region must be a SUBSET of ours for
// every piece - our Mobility computes the LEAST Figure 6/7 fixpoint, and any square cha reaches that we do not would
// mean our fixpoint under-approximates, which would break the Theorem 12 admissibility argument (M >= M*). The
// measured split is pinned so any drift in either implementation surfaces: 16758 of 16845 rows are bit-identical
// (99.5%), and on the 87 remaining rows (all locked pawn structures) cha is strictly tighter.
class TestMobilityAgainstChaMobilityOracle {

  @SuppressWarnings("static-method")
  @Test
  void chaRegionsAreContainedInThePaperFixpoint() throws Exception {
    int rows = 0;
    int equal = 0;
    int chaStrictlyTighter = 0;
    final List<String> violations = new ArrayList<>();

    String currentFen = null;
    SemiStaticPosition position = null;
    MobilitySolution mobilitySolution = null;

    try (InputStream in = TestMobilityAgainstChaMobilityOracle.class
        .getResourceAsStream("/oracle/cha/ashlar-pgn/mobility.tsv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
      String line = reader.readLine(); // header
      while ((line = reader.readLine()) != null) {
        final String[] item = line.split("\t");
        final String fen = item[0];
        if (!fen.equals(currentFen)) {
          currentFen = fen;
          position = SemiStaticPosition.fromBoard(Board.fromFenStrict(fen));
          mobilitySolution = Mobility.mobility(position);
        }
        rows++;

        final long paperRegion = mobilitySolution.region(position.indexAt(squareIndex(item[3])));
        long chaRegion = 0L;
        for (final String squareName : item[4].split(",")) {
          chaRegion |= 1L << squareIndex(squareName);
        }

        if (paperRegion == chaRegion) {
          equal++;
        } else if ((chaRegion & ~paperRegion) == 0L) {
          chaStrictlyTighter++;
        } else {
          violations.add(fen + " " + item[1] + " " + item[2] + " " + item[3]);
        }
      }
    }

    assertTrue(violations.isEmpty(),
        "cha mobility reaches squares outside the paper fixpoint (our fixpoint under-approximates?): " + violations);
    assertEquals(16845, rows, "mobility oracle row count changed");
    assertEquals(16758, equal, "bit-identical mobility rows changed");
    assertEquals(87, chaStrictlyTighter, "cha-strictly-tighter mobility rows changed");
  }

  private static int squareIndex(String name) {
    return ((name.charAt(1) - '1') << 3) | (name.charAt(0) - 'a');
  }
}
