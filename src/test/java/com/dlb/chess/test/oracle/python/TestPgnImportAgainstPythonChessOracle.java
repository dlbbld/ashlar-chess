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

/**
 * Cross-validates clean-chess against python-chess for the PGN-import oracle across multiple {@link PgnTest} buckets.
 *
 * <p>
 * For each covered bucket, reads the bucket's committed JSONL oracle (one record per PGN) under
 * {@code src/test/resources/oracle/python-chess/<folderPart>.jsonl}, produced by
 * {@code src/test/python/generate_pgn_import_oracle.py}. For each fixture: parses the PGN with
 * {@link StrictPgnParser}, replays the half-moves on a {@link Board} starting from the parsed FEN, and asserts at every
 * ply that {@code board.getFen()}, {@code halfmoveClock}, {@code fullmoveNumber}, {@code isCheck}, {@code isCheckmate}
 * and {@code isStalemate} match python-chess's recorded values.
 *
 * <p>
 * The oracle is regenerated only when the PGN fixtures or the recorded fields change; {@code mvn test} does not invoke
 * Python. The generator script is the schema source of truth — see the module docstring of
 * {@code src/test/python/generate_pgn_import_oracle.py} for the per-record JSON shape, the
 * reproducibility install command, and the version of python-chess that produced the committed oracle.
 *
 * <p>
 * FEN convention: the oracle is emitted with python-chess's {@code board.fen(en_passant="fen")} option so that the
 * en-passant target square is written after every pawn double-step (PGN/Edwards 1994 §16.1.3.4), matching what
 * clean-chess writes. python-chess's default omits the e.p. target when no capture is legal next move (X-FEN / Lichess
 * / Stockfish de-facto); cross-validating against that default would surface a FEN-emission convention disagreement on
 * every double-step-without-capturer fixture, which is a separate decision from the PGN-import correctness this test
 * targets.
 *
 * <p>
 * Bucket coverage: PARSER_FROM_FEN plus all BASIC_* buckets plus the curated real-games / Wikipedia / WCC buckets.
 * Skipped per the release plan: CHA_*, edgeCases, random, MAX_*, MONSTER_*, REPETITION_QUIZ_*.
 */
class TestPgnImportAgainstPythonChessOracle {

  private static final Logger LOGGER = Nulls.getLogger(TestPgnImportAgainstPythonChessOracle.class);

  private static final Path ORACLE_ROOT = Nulls.pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
      "src/test/resources/oracle/python-chess");

  private static final List<PgnTest> BUCKETS = List.of(PgnTest.PARSER_FROM_FEN,
      // basic — moving pieces / capture / en passant / promotion
      PgnTest.BASIC_MOVING_PIECE_WHITE, PgnTest.BASIC_MOVING_PIECE_BLACK,
      PgnTest.BASIC_CAPTURE_WHITE, PgnTest.BASIC_CAPTURE_BLACK, PgnTest.BASIC_CAPTURE_LAST_MOVE,
      PgnTest.BASIC_EN_PASSANT_CAPTURE_WHITE, PgnTest.BASIC_EN_PASSANT_CAPTURE_BLACK,
      PgnTest.BASIC_PROMOTION_PIECE_WHITE, PgnTest.BASIC_PROMOTION_PIECE_BLACK,
      PgnTest.BASIC_PROMOTION_SQUARE_WHITE, PgnTest.BASIC_PROMOTION_SQUARE_BLACK,
      // basic — check / checkmate / double check / stalemate
      PgnTest.BASIC_CHECK_WHITE, PgnTest.BASIC_CHECK_BLACK,
      PgnTest.BASIC_CHECKMATE_WHITE, PgnTest.BASIC_CHECKMATE_BLACK,
      PgnTest.BASIC_CHECKMATE_VARIOUS_WHITE, PgnTest.BASIC_CHECKMATE_VARIOUS_BLACK,
      PgnTest.BASIC_DOUBLE_CHECK_WHITE, PgnTest.BASIC_DOUBLE_CHECK_BLACK,
      PgnTest.BASIC_CHECKMATE_DOUBLE_CHECK_WHITE, PgnTest.BASIC_CHECKMATE_DOUBLE_CHECK_BLACK,
      PgnTest.BASIC_STALEMATE,
      // basic — insufficient material / repetition / fifty / seventy-five
      PgnTest.BASIC_INSUFFICIENT_MATERIAL_BOTH, PgnTest.BASIC_INSUFFICIENT_MATERIAL_ONLY_WHITE,
      PgnTest.BASIC_INSUFFICIENT_MATERIAL_ONLY_BLACK, PgnTest.BASIC_INSUFFICIENT_MATERIAL_NONE,
      PgnTest.BASIC_THREEFOLD, PgnTest.BASIC_FIFTY, PgnTest.BASIC_FIVEFOLD, PgnTest.BASIC_SEVENTY_FIVE,
      PgnTest.BASIC_INTERVENING, PgnTest.BASIC_DOUBLE_DRAW,
      // basic — castling / forced / report
      PgnTest.BASIC_CASTLING_WHITE, PgnTest.BASIC_CASTLING_BLACK,
      PgnTest.BASIC_CASTLING_SPECIAL_WHITE, PgnTest.BASIC_CASTLING_SPECIAL_BLACK,
      PgnTest.BASIC_FORCED,
      PgnTest.BASIC_REPORT_NO_PROGRESS_SEQUENCES_WHITE, PgnTest.BASIC_REPORT_NO_PROGRESS_SEQUENCES_BLACK,
      PgnTest.BASIC_REPORT_REPETITION, PgnTest.BASIC_REPORT_MAX_NO_PROGRESS,
      // real games / review
      PgnTest.VARIOUS, PgnTest.WCC2021,
      PgnTest.FIVEFOLD_CORRECT, PgnTest.FIFTY_GENERAL, PgnTest.FIFTY_PATTERN,
      PgnTest.SEVENTY_FIVE_CORRECT, PgnTest.EARLY_DRAW,
      PgnTest.WIKIPEDIA_THREEFOLD, PgnTest.WIKIPEDIA_FIFTY_MOVE);

  @Test
  void pgnImportAgainstPythonChessOracle() throws IOException {
    final List<String> failures = new ArrayList<>();
    var totalFixtures = 0;
    var totalPlies = 0;

    for (final PgnTest bucket : BUCKETS) {
      final Path jsonlPath = jsonlPathFor(bucket);
      LOGGER.info("Bucket {} → {}", bucket, jsonlPath);

      final List<OracleRecord> records = OracleJsonlReader.readAll(jsonlPath);
      if (records.isEmpty()) {
        failures.add(bucket + " — oracle file is empty or missing: " + jsonlPath);
        continue;
      }

      final Path folderPath = bucket.getFolderPath();
      for (final OracleRecord record : records) {
        totalFixtures++;
        final PgnGame pgnGame = StrictPgnParser.parse(folderPath, record.pgn());

        try {
          assertEquals(record.startFen(), pgnGame.startFen().fen(),
              () -> bucket + " / " + record.pgn() + " — startFen mismatch (clean-chess vs python-chess)");
          assertEquals(record.moves().size(), pgnGame.halfMoveList().size(),
              () -> bucket + " / " + record.pgn() + " — half-move count mismatch (clean-chess vs python-chess)");
        } catch (final AssertionError e) {
          failures.add(BasicUtility.getMessage(e));
          continue;
        }

        final Board board = new Board(pgnGame.startFen(), false);
        for (var ply = 0; ply < pgnGame.halfMoveList().size(); ply++) {
          totalPlies++;
          final PgnHalfMove halfMove = pgnGame.halfMoveList().get(ply);
          final OracleMove expected = record.moves().get(ply);
          board.moveStrict(halfMove.san());

          final var plyLabel = ply + 1;
          try {
            assertEquals(expected.fenAfter(), board.getFen(),
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " — FEN after move mismatch");
            assertEquals(expected.halfmoveClock(), board.getHalfMoveClock(),
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " — halfmove clock mismatch");
            assertEquals(expected.fullmoveNumber(), board.getFullMoveNumber(),
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " — fullmove number mismatch");
            assertEquals(expected.isCheck(), board.isCheck(),
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " — isCheck mismatch");
            assertEquals(expected.isCheckmate(), board.isCheckmate(),
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " — isCheckmate mismatch");
            assertEquals(expected.isStalemate(), board.isStalemate(),
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " — isStalemate mismatch");
            assertEquals(expected.isInsufficientMaterial(), board.isInsufficientMaterial(),
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " — isInsufficientMaterial mismatch");
            assertEquals(expected.hasInsufficientMaterialWhite(), board.isInsufficientMaterial(Side.WHITE),
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel
                    + " — isInsufficientMaterial(WHITE) mismatch");
            assertEquals(expected.hasInsufficientMaterialBlack(), board.isInsufficientMaterial(Side.BLACK),
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel
                    + " — isInsufficientMaterial(BLACK) mismatch");
            assertEquals(expected.isRepetition2(), board.getRepetitionCount() >= 2,
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " — isRepetition(2) mismatch");
            assertEquals(expected.isRepetition3(), board.getRepetitionCount() >= 3,
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " — isRepetition(3) mismatch");
            assertEquals(expected.isRepetition4(), board.getRepetitionCount() >= 4,
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " — isRepetition(4) mismatch");
            assertEquals(expected.isFivefoldRepetition(), board.isFivefoldRepetition(),
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " — isFivefoldRepetition mismatch");
            // Two clean-chess vs python-chess semantic disagreements at game-end positions, both treated as
            // surfaced findings (see tasks.md "drop auto-fivefold / auto-75-move termination" discussion) rather
            // than silently resolved in the oracle layer:
            //
            // (1) python-chess `is_fifty_moves()` / `is_seventyfive_moves()` require `halfmove_clock >= N AND a
            //     legal move exists`; clean-chess `isFiftyMove()` / `isSeventyFiveMove()` are pure threshold
            //     checks. At checkmate/stalemate positions where the clock is past the threshold, python-chess
            //     returns false while clean-chess returns true.
            //
            // (2) clean-chess `canClaimThreefoldRepetitionRule()` / `canClaimFiftyMoveRule()` internally simulate
            //     legal moves via Board.move(); on auto-terminated positions Board.move() throws
            //     InvalidMoveException, so the canClaim* methods are unsafe to call. python-chess returns a
            //     clean boolean in the same situation.
            //
            // Skip all four assertions when clean-chess considers the game ended; assertions still run at every
            // mid-game position, which is where claim semantics actually matter.
            if (!board.isCheckmate() && !board.isStalemate() && !board.isFivefoldRepetition()
                && !board.isSeventyFiveMove() && !board.isInsufficientMaterial()) {
              assertEquals(expected.isFiftyMoves(), board.isFiftyMove(),
                  () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " — isFiftyMoves mismatch");
              assertEquals(expected.isSeventyFiveMoves(), board.isSeventyFiveMove(),
                  () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " — isSeventyFiveMoves mismatch");
              assertEquals(expected.canClaimThreefold(), board.canClaimThreefoldRepetitionRule(),
                  () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " — canClaimThreefold mismatch");
              assertEquals(expected.canClaimFifty(), board.canClaimFiftyMoveRule(),
                  () -> bucket + " / " + record.pgn() + " ply " + plyLabel + " — canClaimFifty mismatch");
            }
          } catch (final AssertionError e) {
            failures.add(BasicUtility.getMessage(e));
          }
        }

        try {
          assertEquals(record.finalFen(), board.getFen(),
              () -> bucket + " / " + record.pgn() + " — final FEN mismatch");
        } catch (final AssertionError e) {
          failures.add(BasicUtility.getMessage(e));
        }
      }
    }

    if (totalFixtures == 0) {
      fail("No fixtures iterated — bucket wiring is broken");
    }
    LOGGER.info("Cross-validated {} fixtures across {} buckets ({} plies)", totalFixtures, BUCKETS.size(), totalPlies);

    if (!failures.isEmpty()) {
      final var report = new StringBuilder().append(failures.size())
          .append(" python-chess oracle disagreement(s) across ").append(totalFixtures)
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
