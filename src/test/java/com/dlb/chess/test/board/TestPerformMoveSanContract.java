package com.dlb.chess.test.board;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.model.PgnHalfMove;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.san.StrictSanParser;
import com.dlb.chess.test.model.PgnFen;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;

/**
 * Verifies the round-trip consistency between SAN and MoveSpecification that
 * {@link com.dlb.chess.board.Board#moveStrict(String)} relies on: once {@link com.dlb.chess.san.StrictSanParser}'s
 * {@code parseText} has produced a MoveSpecification from a SAN, that MoveSpec is the canonical representation of the
 * move and round-trips both ways.
 * The board therefore performs the move with no further re-validation of the spec.
 *
 * <h2>Forward (played moves)</h2>
 *
 * <p>
 * For each halfmove of each PGN: derive the MoveSpec via {@code StrictSanParser.parseText(san, board)} <em>before</em>
 * performing, then perform via {@code board.moveStrict(san)} and assert:
 *
 * <ol>
 * <li>the derived MoveSpec equals the legal move that was actually played
 * ({@code board.getLastMove().moveSpecification()});</li>
 * <li>the SAN reconstructed from the legal move ({@code board.getSan()}) equals the original PGN SAN.</li>
 * </ol>
 *
 * <h2>Reverse (canonical SAN back to MoveSpecification)</h2>
 *
 * <p>
 * At each played halfmove: perform it, then capture both the stored MoveSpec
 * ({@code board.getLastMove().moveSpecification()}) and the board's canonical SAN ({@code board.getSan()});
 * {@link com.dlb.chess.board.Board#unmove} back to the prior position and re-derive a MoveSpec from that canonical SAN
 * via {@code StrictSanParser.parseText}. The re-derived MoveSpec must equal the stored one - the SAN the board emits
 * round-trips back to the same move.
 *
 * <h2>Scope and runtime</h2>
 *
 * <p>
 * Iterates the parser-integration smoke list (~45 PGNs spanning every major parser code path - standard moves,
 * captures, en passant, promotion, castling, check, checkmate, custom starting positions). The reverse test is the
 * slower of the two but completes in seconds.
 */
class TestPerformMoveSanContract {

  private static final Logger logger = Nulls.getLogger(TestPerformMoveSanContract.class);

  @SuppressWarnings("static-method")
  @Test
  void testPlayedMoveSanMoveSpecRoundtrip() {
    for (final PgnTestCaseList testCaseList : PgnTestCaseCatalog.getParserIntegrationSmokeList()) {
      for (final PgnFen testCase : testCaseList.list()) {
        logger.info(testCase.pgnName());
        verifyProvidedSanToCalculatedSan(testCaseList, testCase);
      }
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testAllLegalMovesSanMoveSpecRoundtrip() {
    for (final PgnTestCaseList testCaseList : PgnTestCaseCatalog.getParserIntegrationSmokeList()) {
      for (final PgnFen testCase : testCaseList.list()) {
        logger.info(testCase.pgnName());
        verifyCalculatedSanToCalculatedMoveSpecification(testCaseList, testCase);
      }
    }
  }

  /**
   * Forward direction: for each played halfmove, derive MoveSpec from SAN, perform via SAN, then assert the played
   * LegalMove and the reconstructed SAN both match.
   */
  private static void verifyProvidedSanToCalculatedSan(PgnTestCaseList testCaseList, PgnFen testCase) {
    final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(testCaseList.pgnTest().getFolderPath(),
        testCase.pgnName());
    final Board board = new Board(pgnGame.startFen());

      int halfMoveIndex = 0;
    for (final PgnHalfMove halfMove : pgnGame.halfMoveList()) {
      halfMoveIndex++;
      final int hmi = halfMoveIndex;
      final String expectedProvidedSan = halfMove.san();

      final MoveSpecification expectedCalculatedMoveSpecification = StrictSanParser
          .parseText(expectedProvidedSan, board).moveSpecification();

      board.moveStrict(expectedProvidedSan);

      final MoveSpecification actualStoredMoveSpecification = board.getLastMove().moveSpecification();
      assertEquals(expectedCalculatedMoveSpecification, actualStoredMoveSpecification,
          () -> testCase.pgnName() + ": halfmove " + hmi + " (" + expectedProvidedSan
              + ") - MoveSpec derived from SAN does not match the LegalMove's MoveSpec after perform");

      final String actualCalculatedSan = board.getSan();
      assertEquals(expectedProvidedSan, actualCalculatedSan, () -> testCase.pgnName() + ": halfmove " + hmi + " ("
          + expectedProvidedSan + ") - SAN reconstructed from LegalMove does not match the original PGN SAN");
    }
  }

  /**
   * Reverse direction: at each position, for every legal move (not just the played one), perform -> capture SAN ->
   * unperform -> derive MoveSpec from SAN at the original position -> assert it equals the LegalMove's stored MoveSpec.
   */
  private static void verifyCalculatedSanToCalculatedMoveSpecification(PgnTestCaseList testCaseList, PgnFen testCase) {
    final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(testCaseList.pgnTest().getFolderPath(),
        testCase.pgnName());
    final Board board = new Board(pgnGame.startFen());

    for (final PgnHalfMove halfMove : pgnGame.halfMoveList()) {
      board.moveStrict(halfMove.san());
      final MoveSpecification expectedStoredMoveSpecification = board.getLastMove().moveSpecification();
      final String calculatedSan = board.getSan();
      board.unmove();
      final MoveSpecification actualCalculatedMoveSpecification = StrictSanParser.parseText(calculatedSan, board)
          .moveSpecification();
      assertEquals(expectedStoredMoveSpecification, actualCalculatedMoveSpecification);
      board.moveStrict(halfMove.san());
    }
  }

}
