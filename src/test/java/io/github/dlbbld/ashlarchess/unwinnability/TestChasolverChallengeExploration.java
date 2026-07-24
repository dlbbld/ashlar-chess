// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.pgn.PgnUtility;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParser;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

class TestChasolverChallengeExploration {

  private static final String UNWINNABLE_EXHAUSTION_BLACK_ONLY =
      "3k1b2/2p1pBp1/KpP1P1P1/pP3B2/P3B1B1/8/8/8 w - - 7 56";

  private static final String UNWINNABLE_EXHAUSTION_BOTH_SIDES =
      "3k1b2/2p1pBp1/KpP1P1P1/1P3B2/4B1B1/5B2/8/8 w - - 3 57";

  private static final String CAGED_KING_STALEMATE_CAPTURE =
      "1k1K1b1b/p1p1pBb1/PpP1PpPp/1P3P1P/4B3/8/8/8 w - - 3 57";

  private record ExplorationPosition(String label, String fen) {
  }

  private record ProofGame(String pgnName, String finalFen, int plies) {
  }

  private record CatalogProofGame(PgnTest pgnTest, String pgnName, String finalFen, int plies) {
  }

  private static final List<ExplorationPosition> EXPLORATION_POSITIONS = List.of(
      new ExplorationPosition("queen corner mirror seed",
          "8/8/8/3b2P1/5PpP/2K1Pp1p/4p1bk/5bnq w - - 0 1"),
      new ExplorationPosition("bishop wall seed",
          "Bb1k1b2/bKp1p1p1/1pP1P1P1/pP6/P5P1/1B6/8/8 w - - 0 1"),
      new ExplorationPosition("two white bishops",
          "B2k1b2/2p1pBp1/KpP1P1P1/pP6/P5P1/8/8/8 w - - 0 1"),
      new ExplorationPosition("three white bishops",
          "B2k1b2/2p1pBp1/KpP1P1P1/pP6/P5P1/8/8/3B4 w - - 0 1"),
      new ExplorationPosition("four white bishops with a-pawns",
          "3k1b2/2p1pBp1/KpP1P1P1/pP6/P5P1/8/2B5/3B4 w - - 0 1"),
      new ExplorationPosition("four white bishops without g4 pawn",
          "3k1b2/2p1pBp1/KpP1P1P1/pP6/P7/8/2B1B3/3B4 w - - 0 1"),
      new ExplorationPosition("black mobility bishop",
          "1b1k1b2/2p1pBp1/KpP1P1P1/pP6/P7/8/2B1B3/3B4 w - - 0 1"),
      new ExplorationPosition("bishop shifted onto d7",
          "3k1b2/2pBpBp1/KpP1P1P1/pP6/P7/8/4B3/3B4 w - - 0 1"),
      new ExplorationPosition("guarded c7 basis",
          "1b1k1b2/b1p1pBp1/KpP1P1P1/pP6/P7/8/8/8 w - - 0 1"),
      new ExplorationPosition("unwinnable exhaustion black only", UNWINNABLE_EXHAUSTION_BLACK_ONLY),
      new ExplorationPosition("unwinnable exhaustion both sides, analysis clock",
          "3k1b2/2p1pBp1/KpP1P1P1/1P3B2/4B1B1/5B2/8/8 w - - 7 56"),
      new ExplorationPosition("guarded c7 plus black dark bishop",
          "1b1k1b2/b1p1pBp1/KpP1P1P1/1P3B2/4BbB1/5B2/8/8 w - - 7 56"),
      new ExplorationPosition("maxing dark bishops",
          "B2k1b2/1BpBpBp1/KpP1P1P1/1P2bB2/3b1b2/4b3/8/8 w - - 7 56"),
      new ExplorationPosition("maxing light bishops",
          "B2k1b2/1BpBpBpB/KpP1P1P1/1P2b3/3b1b2/4b3/8/8 w - - 7 56"),
      new ExplorationPosition("static proof dead end",
          "3k1b2/2pPpBp1/KpP1P1P1/1P2b3/3bBb2/3BbB2/8/8 b - - 8 56"),
      new ExplorationPosition("caged king stalemate capture", CAGED_KING_STALEMATE_CAPTURE),
      new ExplorationPosition("unwinnable exhaustion both sides", UNWINNABLE_EXHAUSTION_BOTH_SIDES));

  private static final List<ProofGame> STORED_PROOF_GAMES = List.of(
      new ProofGame("04_unwinnable_exhaustion_black_only.pgn", UNWINNABLE_EXHAUSTION_BLACK_ONLY, 110),
      new ProofGame("05_unwinnable_exhaustion_both_sides.pgn", UNWINNABLE_EXHAUSTION_BOTH_SIDES, 112));

  private static final List<CatalogProofGame> ISSUE_48_WINNABLE_FAMILY_PROOF_GAMES = List.of(
      new CatalogProofGame(PgnTest.CHA_CHASOLVER_CHALLENGES_EXCEPTIONS, "01_chasolver_node_limit_exception.pgn",
          "1Bb5/1p6/pPp3k1/2Pp3p/P2PpBpP/4P1P1/5K2/8 w - - 8 32", 62),
      new CatalogProofGame(PgnTest.CHA_CHASOLVER_CHALLENGES_EXCEPTIONS, "02_chasolver_node_limit_exception.pgn",
          "1Bb5/1p6/pPp3k1/2Pp1b1p/P2PpBpP/4P1P1/5K2/8 w - - 5 37", 72),
      new CatalogProofGame(PgnTest.CHA_CHASOLVER_CHALLENGES_EXCEPTIONS, "03_chasolver_node_limit_exception.pgn",
          "1Bb5/1p6/pPpBb1k1/2Pp1b2/P2PpBp1/4P1P1/5K2/8 w - - 10 46", 90),
      new CatalogProofGame(PgnTest.CHA_CHASOLVER_CHALLENGES_SUCCESS, "01_four_bishops_each.pgn",
          "1Bb5/8/2b1b1k1/1pBpBb2/pP1PpBp1/4P1P1/P4K2/8 w - - 9 56", 110));

  @SuppressWarnings("static-method")
  @Test
  void exploredPositionsRemainStrictAndQuickDoesNotSettle() {
    for (final ExplorationPosition position : EXPLORATION_POSITIONS) {
      final Board board = Board.fromFenStrict(position.fen());

      assertFalse(board.getLegalMoves().isEmpty(), position.label());
      assertEquals(UnwinnabilityQuickVerdict.POSSIBLY_WINNABLE, board.unwinnableQuick(Side.WHITE), position.label());
      assertEquals(UnwinnabilityQuickVerdict.POSSIBLY_WINNABLE, board.unwinnableQuick(Side.BLACK), position.label());
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void selectedFullVerdictsPinExplorationBoundaries() {
    assertFullVerdicts("queen corner mirror seed", "8/8/8/3b2P1/5PpP/2K1Pp1p/4p1bk/5bnq w - - 0 1",
        UnwinnabilityFullVerdict.WINNABLE, UnwinnabilityFullVerdict.WINNABLE);
    assertFullVerdicts("black mobility bishop",
        "1b1k1b2/2p1pBp1/KpP1P1P1/pP6/P7/8/2B1B3/3B4 w - - 0 1",
        UnwinnabilityFullVerdict.WINNABLE, UnwinnabilityFullVerdict.WINNABLE);
    assertFullVerdicts("guarded c7 basis",
        "1b1k1b2/b1p1pBp1/KpP1P1P1/pP6/P7/8/8/8 w - - 0 1", UnwinnabilityFullVerdict.UNWINNABLE,
        UnwinnabilityFullVerdict.UNWINNABLE);
    assertFullVerdicts("static proof dead end",
        "3k1b2/2pPpBp1/KpP1P1P1/1P2b3/3bBb2/3BbB2/8/8 b - - 8 56",
        UnwinnabilityFullVerdict.UNWINNABLE, UnwinnabilityFullVerdict.UNWINNABLE);
    assertFullVerdicts("caged king stalemate capture", CAGED_KING_STALEMATE_CAPTURE,
        UnwinnabilityFullVerdict.UNDETERMINED, UnwinnabilityFullVerdict.UNDETERMINED);
    assertFullVerdicts("unwinnable exhaustion black only", UNWINNABLE_EXHAUSTION_BLACK_ONLY,
        UnwinnabilityFullVerdict.UNDETERMINED, UnwinnabilityFullVerdict.UNDETERMINED);
    assertFullVerdicts("unwinnable exhaustion both sides", UNWINNABLE_EXHAUSTION_BOTH_SIDES,
        UnwinnabilityFullVerdict.UNDETERMINED, UnwinnabilityFullVerdict.UNDETERMINED);
  }

  @SuppressWarnings("static-method")
  @Test
  void storedProofGamesReachExhaustionChallengePositions() {
    for (final ProofGame proofGame : STORED_PROOF_GAMES) {
      final Path pgnPath = PgnTest.CHA_CHASOLVER_CHALLENGES_EXCEPTIONS.getFolderPath().resolve(proofGame.pgnName());
      final var pgnGame = StrictPgnParser.parsePath(pgnPath);

      assertEquals(proofGame.plies(), pgnGame.moves().size(), proofGame.pgnName());
      assertEquals(proofGame.finalFen(), PgnUtility.toBoard(pgnGame).getFen(), proofGame.pgnName());
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void issue48WinnableFamilyProofGamesRemainStored() {
    for (final CatalogProofGame proofGame : ISSUE_48_WINNABLE_FAMILY_PROOF_GAMES) {
      final Path pgnPath = proofGame.pgnTest().getFolderPath().resolve(proofGame.pgnName());
      final var pgnGame = StrictPgnParser.parsePath(pgnPath);

      assertEquals(proofGame.plies(), pgnGame.moves().size(), proofGame.pgnName());
      assertEquals(proofGame.finalFen(), PgnUtility.toBoard(pgnGame).getFen(), proofGame.pgnName());
    }
  }

  private static void assertFullVerdicts(String label, String fen, UnwinnabilityFullVerdict white,
      UnwinnabilityFullVerdict black) {
    final Board board = Board.fromFenStrict(fen);

    assertEquals(white, board.unwinnableFull(Side.WHITE), label + " White");
    assertEquals(black, board.unwinnableFull(Side.BLACK), label + " Black");
  }
}
