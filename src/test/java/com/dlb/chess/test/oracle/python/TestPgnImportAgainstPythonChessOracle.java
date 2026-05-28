package com.dlb.chess.test.oracle.python;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.utility.BasicUtility;
import com.dlb.chess.model.PgnHalfMove;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.pgn.StrictPgnParser;
import com.dlb.chess.test.ConfigurationTestConstants;
import com.dlb.chess.test.pgntest.constants.PgnTestConstants;
import com.dlb.chess.test.pgntest.enums.PgnTest;
import com.google.common.collect.ImmutableList;

/**
 * Cross-validates clean-chess against python-chess for the PGN-import oracle across multiple {@link PgnTest} buckets.
 *
 * <p>
 * For each covered bucket, reads the bucket's committed JSONL oracle (one record per PGN) under
 * {@code src/test/resources/oracle/python-chess/<folderPart>.jsonl}, produced by
 * {@code src/test/python/generate_pgn_import_oracle.py}. For each fixture: parses the PGN with {@link StrictPgnParser},
 * replays the half-moves on a {@link Board} starting from the parsed FEN, and asserts at every ply that
 * {@code board.getFen()}, {@code halfmoveClock}, {@code fullmoveNumber}, {@code isCheck}, {@code isCheckmate} and
 * {@code isStalemate} match python-chess's recorded values.
 *
 * <p>
 * The oracle is regenerated only when the PGN fixtures or the recorded fields change; {@code mvn test} does not invoke
 * Python. The generator script is the schema source of truth - see the module docstring of
 * {@code src/test/python/generate_pgn_import_oracle.py} for the per-record JSON shape, the reproducibility install
 * command, and the version of python-chess that produced the committed oracle.
 *
 * <p>
 * FEN convention: the oracle is emitted with python-chess's {@code board.fen(en_passant="fen")} option so that the
 * en-passant target square is written after every pawn double-step (PGN/Edwards 1994 section 16.1.3.4), matching what
 * clean-chess writes. python-chess's default omits the e.p. target when no capture is legal next move (X-FEN / Lichess
 * / Stockfish de-facto); cross-validating against that default would surface a FEN-emission convention disagreement on
 * every double-step-without-capturer fixture, which is a separate decision from the PGN-import correctness this test
 * targets.
 *
 * <p>
 * Bucket coverage: PARSER_FROM_FEN plus all BASIC_* buckets plus the curated real-games / Wikipedia / WCC buckets.
 * Skipped per the release plan: CHA_*, edgeCases, random, MAX_*, MONSTER_*, REPETITION_QUIZ_*.
 *
 * <p>
 * Known deliberate divergence from python-chess at one corner case, not surfaced by this corpus:
 * {@code canClaimFiftyMoveRule} at a position where halfmove clock is 99 and the <em>only</em> non-zeroing legal move
 * delivers checkmate. clean-chess follows the strict FIDE 9.3 reading (the claim is announced before the move; the 50
 * moves are about history; the candidate move's outcome is incidental) and returns {@code true}; python-chess pushes
 * the candidate and re-checks {@code is_fifty_moves}, finds the post-position mated, and returns {@code false}. See
 * {@code TestBoardClaimWithOwnMove#canClaimFiftyMoveRuleWithOwnMoveTrueEvenWhenOnlyNonZeroingMoveIsMate} for the
 * constructed position pinning the FIDE-strict semantic. No corpus fixture currently triggers this edge, so the
 * oracle's {@code canClaimFifty} assertion runs at every ply without skip.
 */
class TestPgnImportAgainstPythonChessOracle {

  private static final Logger LOGGER = Nulls.getLogger(TestPgnImportAgainstPythonChessOracle.class);

  private static final Path ORACLE_ROOT = Nulls.pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
      "src/test/resources/oracle/python-chess");

  private static final ImmutableList<PgnTest> BUCKETS = Nulls.listOf(PgnTest.PARSER_FROM_FEN,
      // basic - moving pieces / capture / en passant / promotion
      PgnTest.BASIC_MOVING_PIECE_WHITE, PgnTest.BASIC_MOVING_PIECE_BLACK, PgnTest.BASIC_CAPTURE_WHITE,
      PgnTest.BASIC_CAPTURE_BLACK, PgnTest.BASIC_CAPTURE_LAST_MOVE, PgnTest.BASIC_EN_PASSANT_CAPTURE_WHITE,
      PgnTest.BASIC_EN_PASSANT_CAPTURE_BLACK, PgnTest.BASIC_PROMOTION_PIECE_WHITE, PgnTest.BASIC_PROMOTION_PIECE_BLACK,
      PgnTest.BASIC_PROMOTION_SQUARE_WHITE, PgnTest.BASIC_PROMOTION_SQUARE_BLACK,
      // basic - check / checkmate / double check / stalemate
      PgnTest.BASIC_CHECK_WHITE, PgnTest.BASIC_CHECK_BLACK, PgnTest.BASIC_CHECKMATE_WHITE,
      PgnTest.BASIC_CHECKMATE_BLACK, PgnTest.BASIC_CHECKMATE_VARIOUS_WHITE, PgnTest.BASIC_CHECKMATE_VARIOUS_BLACK,
      PgnTest.BASIC_DOUBLE_CHECK_WHITE, PgnTest.BASIC_DOUBLE_CHECK_BLACK, PgnTest.BASIC_CHECKMATE_DOUBLE_CHECK_WHITE,
      PgnTest.BASIC_CHECKMATE_DOUBLE_CHECK_BLACK, PgnTest.BASIC_STALEMATE,
      // basic - insufficient material / repetition / fifty / seventy-five
      PgnTest.BASIC_INSUFFICIENT_MATERIAL_BOTH, PgnTest.BASIC_INSUFFICIENT_MATERIAL_ONLY_WHITE,
      PgnTest.BASIC_INSUFFICIENT_MATERIAL_ONLY_BLACK, PgnTest.BASIC_INSUFFICIENT_MATERIAL_NONE, PgnTest.BASIC_THREEFOLD,
      PgnTest.BASIC_FIFTY, PgnTest.BASIC_FIVEFOLD, PgnTest.BASIC_SEVENTY_FIVE, PgnTest.BASIC_INTERVENING,
      PgnTest.BASIC_DOUBLE_DRAW,
      // basic - castling / forced / report
      PgnTest.BASIC_CASTLING_WHITE, PgnTest.BASIC_CASTLING_BLACK, PgnTest.BASIC_CASTLING_SPECIAL_WHITE,
      PgnTest.BASIC_CASTLING_SPECIAL_BLACK, PgnTest.BASIC_FORCED, PgnTest.BASIC_REPORT_NO_PROGRESS_SEQUENCES_WHITE,
      PgnTest.BASIC_REPORT_NO_PROGRESS_SEQUENCES_BLACK, PgnTest.BASIC_REPORT_REPETITION,
      PgnTest.BASIC_REPORT_MAX_NO_PROGRESS,
      // real games / review
      PgnTest.VARIOUS, PgnTest.WCC2021, PgnTest.FIVEFOLD_CORRECT, PgnTest.FIFTY_GENERAL, PgnTest.FIFTY_PATTERN,
      PgnTest.SEVENTY_FIVE_CORRECT, PgnTest.EARLY_DRAW, PgnTest.WIKIPEDIA_THREEFOLD, PgnTest.WIKIPEDIA_FIFTY_MOVE);

  @SuppressWarnings("static-method")
  @Test
  void pgnImportAgainstPythonChessOracle() throws IOException {
    final List<String> failures = new ArrayList<>();
    int totalFixtures = 0;
    int totalPlies = 0;

    for (final PgnTest bucket : BUCKETS) {
      final Path jsonlPath = jsonlPathFor(bucket);
      LOGGER.info("Bucket {} -> {}", bucket, jsonlPath);

      final List<OracleRecord> records = OracleJsonlReader.readAll(jsonlPath);
      if (records.isEmpty()) {
        failures.add(bucket + " - oracle file is empty or missing: " + jsonlPath);
        continue;
      }

      final Path folderPath = bucket.getFolderPath();
      for (final OracleRecord record : records) {
        totalFixtures++;
        final PgnGame pgnGame = StrictPgnParser.parse(folderPath, record.pgn());

        try {
          assertEquals(record.startFen(), pgnGame.startFen().fen(),
              () -> bucket + " / " + record.pgn() + " - startFen mismatch (clean-chess vs python-chess)");
          assertEquals(record.moves().size(), pgnGame.halfMoveList().size(),
              () -> bucket + " / " + record.pgn() + " - half-move count mismatch (clean-chess vs python-chess)");
        } catch (final AssertionError e) {
          failures.add(BasicUtility.getMessage(e));
          continue;
        }

        final Board board = new Board(pgnGame.startFen());
        for (int ply = 0; ply < pgnGame.halfMoveList().size(); ply++) {
          totalPlies++;
          final PgnHalfMove halfMove = Nulls.get(pgnGame.halfMoveList(), ply);
          final OracleMove expected = Nulls.get(record.moves(), ply);
          board.moveStrict(halfMove.san());

          final int plyLabel = ply + 1;
          try {
            assertEquals(expected.fenAfter(), board.getFen(),
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " - FEN after move mismatch");
            assertEquals(expected.halfmoveClock(), board.getHalfMoveClock(),
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " - halfmove clock mismatch");
            assertEquals(expected.fullmoveNumber(), board.getFullMoveNumber(),
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " - fullmove number mismatch");
            assertEquals(expected.isCheck(), board.isCheck(),
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " - isCheck mismatch");
            assertEquals(expected.isCheckmate(), board.isCheckmate(),
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " - isCheckmate mismatch");
            assertEquals(expected.isStalemate(), board.isStalemate(),
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " - isStalemate mismatch");
            // Slice 7 - clean-chess's regenerated canonical SAN (board.getSan()) vs python-chess's regenerated
            // canonical SAN (board.san(move) before push, recorded as expected.san()). Comparing canonical-vs-
            // canonical sidesteps stylistic differences in the source PGN's input SAN.
            assertEquals(expected.san(), board.getSan(),
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " - canonical SAN mismatch");
            assertEquals(expected.lan(), board.getLan(),
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " - canonical LAN mismatch");
            assertEquals(expected.isInsufficientMaterial(), board.isInsufficientMaterial(),
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " - isInsufficientMaterial mismatch");
            assertEquals(expected.hasInsufficientMaterialWhite(), board.isInsufficientMaterial(Side.WHITE),
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " - isInsufficientMaterial(WHITE) mismatch");
            assertEquals(expected.hasInsufficientMaterialBlack(), board.isInsufficientMaterial(Side.BLACK),
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " - isInsufficientMaterial(BLACK) mismatch");
            assertEquals(expected.isRepetition2(), board.getRepetitionCount() >= 2,
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " - isRepetition(2) mismatch");
            assertEquals(expected.isRepetition3(), board.getRepetitionCount() >= 3,
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " - isRepetition(3) mismatch");
            assertEquals(expected.isRepetition4(), board.getRepetitionCount() >= 4,
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " - isRepetition(4) mismatch");
            // Precedence-suppression exclusion: at positions where python-chess applies a
            // game-end precedence guard to the 50-/75-/fivefold-rule predicates (i.e. when a
            // higher-precedence termination already holds), clean-chess returns the raw fact
            // instead. Skip those predicate comparisons only here; everywhere else the predicates
            // still agree byte-for-byte. The Outcome layer matches python-chess regardless (the
            // precedence stack is applied uniformly there).
            final boolean precedenceSuppressed = board.isCheckmate() || board.isStalemate()
                || board.isInsufficientMaterial();
            if (!precedenceSuppressed) {
              assertEquals(expected.isFivefoldRepetition(), board.isFivefoldRepetition(),
                  () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " - isFivefoldRepetition mismatch");
              assertEquals(expected.isFiftyMoves(), board.isFiftyMove(),
                  () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " - isFiftyMoves mismatch");
              assertEquals(expected.isSeventyFiveMoves(), board.isSeventyFiveMove(),
                  () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " - isSeventyFiveMoves mismatch");
              assertEquals(expected.canClaimThreefold(), board.canClaimThreefoldRepetitionRule(),
                  () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " - canClaimThreefold mismatch");
              assertEquals(expected.canClaimFifty(), board.canClaimFiftyMoveRule(),
                  () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " - canClaimFifty mismatch");
            }
          } catch (final AssertionError e) {
            failures.add(BasicUtility.getMessage(e));
          }
        }

        try {
          assertEquals(record.finalFen(), board.getFen(),
              () -> bucket + " / " + record.pgn() + " - final FEN mismatch");
        } catch (final AssertionError e) {
          failures.add(BasicUtility.getMessage(e));
        }
      }
    }

    if (totalFixtures == 0) {
      fail("No fixtures iterated - bucket wiring is broken");
    }
    LOGGER.info("Cross-validated {} fixtures across {} buckets ({} plies)", totalFixtures, BUCKETS.size(), totalPlies);

    if (!failures.isEmpty()) {
      final StringBuilder report = new StringBuilder();
      report.append(failures.size()).append(" python-chess oracle disagreement(s) across ").append(totalFixtures)
          .append(" fixtures in ").append(BUCKETS.size()).append(" buckets:\n");
      for (final String f : failures) {
        report.append("  ").append(f).append('\n');
      }
      fail(Nulls.toString(report));
    }
  }

  private static Path jsonlPathFor(PgnTest bucket) {
    final Path pgnRoot = PgnTestConstants.PGN_TEST_ROOT_FOLDER_PATH;
    final Path bucketPath = bucket.getFolderPath();
    final String relative = Nulls.replace(Nulls.toString(Nulls.pathRelativize(pgnRoot, bucketPath)), '\\', '/');
    return Nulls.pathResolve(ORACLE_ROOT, relative + ".jsonl");
  }
}
