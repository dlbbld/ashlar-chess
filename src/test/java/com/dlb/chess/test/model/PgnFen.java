package com.dlb.chess.test.model;

import com.dlb.chess.board.Board;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.pgn.PgnUtility;
import com.dlb.chess.test.fen.FenCacheForTestCases;
import com.dlb.chess.test.pgn.parser.PgnCacheForLenientPgnParserTestCases;
import com.dlb.chess.test.pgntest.enums.PgnTest;

/**
 * A single fixture row in the test corpus: a PGN filename and the cached final-position FEN of that game.
 *
 * <p>
 * Two ways to materialise a board from a fixture, chosen by the test author. {@code finalPosition()} is cheap -
 * history-less, built directly from the cached FEN - and fits any test that only consults the final piece arrangement
 * and clocks. {@code game(PgnTest)} replays the PGN with move history attached and is expensive; use it only when the
 * test genuinely needs history-derived state (repetition counts, claimable threefold, last-move metadata, PGN export
 * round-trips, end-to-end pipeline tests). A position-only test that mistakenly chose {@code game(...)} scales as the
 * number of plies in the fixture.
 */
public record PgnFen(String pgnName, String finalFen) {

  public Board finalPosition() {
    return new Board(FenCacheForTestCases.getFen(finalFen()));
  }

  public Board game(PgnTest pgnTest) {
    final PgnGame pgnGame = PgnCacheForLenientPgnParserTestCases.getPgn(pgnTest.getFolderPath(), pgnName());
    return PgnUtility.calculateBoard(pgnGame);
  }

}
