package com.dlb.chess.test.model;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.enums.InsufficientMaterial;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.pgn.PgnUtility;
import com.dlb.chess.report.CheckmateOrStalemate;
import com.dlb.chess.test.fen.FenCacheForTestCases;
import com.dlb.chess.test.pgn.parser.PgnCacheForLenientPgnParserTestCases;
import com.dlb.chess.test.pgntest.enums.PgnTest;

/**
 * A single fixture row in the test corpus. The {@link #finalFen()} string is the cached final position of the PGN file
 * named by {@link #pgnName()}; populated when the fixture is added to {@code PgnTestCaseCatalog}.
 *
 * <p>
 * Two ways to materialise a board from a fixture, chosen by the test author:
 *
 * <ul>
 * <li>{@link #finalPosition()} — history-less board built from {@link #finalFen()}. <b>Cheap.</b> Use whenever the test
 * only consults the final piece arrangement and clocks (most CHA-quick / CHA-full / static-evaluation tests).
 * <li>{@link #game(PgnTest)} — full PGN replay with move history attached. <b>Expensive.</b> Use only when the test
 * genuinely needs history-derived state (repetition counts, claimable-threefold/fifty-move-rule, last-move metadata
 * such as capture/promotion/castling/en-passant, PGN export round-trips, end-to-end pipeline tests).
 * </ul>
 *
 * <p>
 * A position-only test that mistakenly chose {@code game(...)} scales as the number of plies in the fixture.
 */
public record PgnTestCase(String pgnName, String expectedRepetition, String expectedNoProgressMoveRule,
    int firstCapture, int maxNoProgressSequence, CheckmateOrStalemate checkmateOrStalemate,
    int repetitionCountFinalPosition, InsufficientMaterial insufficientMaterial, String finalFen) {

  /**
   * History-less board built directly from the cached FEN. Cheap — no PGN parse, no move replay. Use this whenever the
   * test only needs the final position.
   */
  public Board finalPosition() {
    return new Board(FenCacheForTestCases.getFen(finalFen()));
  }

  /**
   * Full PGN replay with move history. Expensive — parses the PGN and plays every half-move. Use only when the test
   * genuinely needs history-derived state (repetition counts, claimable threefold, last-move metadata, end-to-end
   * pipeline tests).
   *
   * <p>
   * The {@code pgnTest} argument supplies the folder; pass {@code testCaseList.pgnTest()} when iterating a test list,
   * or {@link com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog#findPgnTest(String)} when starting from a bare test case.
   */
  public Board game(PgnTest pgnTest) {
    final PgnGame pgnGame = PgnCacheForLenientPgnParserTestCases.getPgn(pgnTest.getFolderPath(), pgnName());
    return PgnUtility.calculateBoard(pgnGame);
  }

}
