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
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.utility.BasicUtility;
import com.dlb.chess.model.PgnHalfMove;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.pgn.StrictPgnParser;
import com.dlb.chess.test.ConfigurationTestConstants;
import com.dlb.chess.test.pgntest.enums.PgnTest;

/**
 * Cross-validates clean-chess against python-chess for every fixture in {@link PgnTest#PARSER_FROM_FEN}.
 *
 * <p>
 * Reads the committed JSONL oracle at {@code src/test/resources/oracle/python-chess/parserFenMechanics.jsonl} produced
 * by {@code src/test/python/generate_parser_fen_mechanics_oracle.py}. For each fixture: parses the PGN with
 * {@link StrictPgnParser}, replays the half-moves on a {@link Board} starting from the parsed FEN, and asserts at every
 * ply that {@code board.getFen()}, {@code halfmoveClock}, {@code fullmoveNumber}, {@code isCheck}, {@code isCheckmate}
 * and {@code isStalemate} match python-chess's recorded values.
 *
 * <p>
 * The oracle is regenerated only when the PGN fixtures or the recorded fields change; {@code mvn test} does not invoke
 * Python. See {@code setup.md} for the regeneration command.
 *
 * <p>
 * FEN convention: the oracle is emitted with python-chess's {@code board.fen(en_passant="fen")} option so that the
 * en-passant target square is written after every pawn double-step (PGN/Edwards 1994 §16.1.3.4), matching what
 * clean-chess writes. python-chess's default omits the e.p. target when no capture is legal next move (X-FEN / Lichess
 * / Stockfish de-facto); cross-validating against that default would surface a FEN-emission convention disagreement on
 * every double-step-without-capturer fixture, which is a separate decision from the PGN-import correctness this test
 * targets.
 */
class TestParserFenMechanicsAgainstPythonChessOracle {

  private static final Logger LOGGER = Nulls.getLogger(TestParserFenMechanicsAgainstPythonChessOracle.class);

  private static final Path ORACLE_JSONL_PATH = Nulls.pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
      "src/test/resources/oracle/python-chess/parserFenMechanics.jsonl");

  @Test
  void parserFenMechanicsAgainstPythonChessOracle() throws IOException {
    final List<OracleRecord> records = OracleJsonlReader.readAll(ORACLE_JSONL_PATH);
    if (records.isEmpty()) {
      fail("Oracle file is empty: " + ORACLE_JSONL_PATH);
    }

    final Path folderPath = PgnTest.PARSER_FROM_FEN.getFolderPath();
    final List<String> failures = new ArrayList<>();

    for (final OracleRecord record : records) {
      LOGGER.info(record.pgn());

      final PgnGame pgnGame = StrictPgnParser.parse(folderPath, record.pgn());

      try {
        assertEquals(record.startFen(), pgnGame.startFen().fen(),
            () -> record.pgn() + " — startFen mismatch (clean-chess vs python-chess)");
        assertEquals(record.moves().size(), pgnGame.halfMoveList().size(),
            () -> record.pgn() + " — half-move count mismatch (clean-chess vs python-chess)");
      } catch (final AssertionError e) {
        failures.add(BasicUtility.getMessage(e));
        continue;
      }

      final Board board = new Board(pgnGame.startFen(), false);
      for (var ply = 0; ply < pgnGame.halfMoveList().size(); ply++) {
        final PgnHalfMove halfMove = pgnGame.halfMoveList().get(ply);
        final OracleMove expected = record.moves().get(ply);
        board.moveStrict(halfMove.san());

        final var plyLabel = ply + 1;
        try {
          assertEquals(expected.fenAfter(), board.getFen(),
              () -> record.pgn() + " ply " + plyLabel + " — FEN after move mismatch");
          assertEquals(expected.halfmoveClock(), board.getHalfMoveClock(),
              () -> record.pgn() + " ply " + plyLabel + " — halfmove clock mismatch");
          assertEquals(expected.fullmoveNumber(), board.getFullMoveNumber(),
              () -> record.pgn() + " ply " + plyLabel + " — fullmove number mismatch");
          assertEquals(expected.isCheck(), board.isCheck(),
              () -> record.pgn() + " ply " + plyLabel + " — isCheck mismatch");
          assertEquals(expected.isCheckmate(), board.isCheckmate(),
              () -> record.pgn() + " ply " + plyLabel + " — isCheckmate mismatch");
          assertEquals(expected.isStalemate(), board.isStalemate(),
              () -> record.pgn() + " ply " + plyLabel + " — isStalemate mismatch");
        } catch (final AssertionError e) {
          failures.add(BasicUtility.getMessage(e));
        }
      }

      try {
        assertEquals(record.finalFen(), board.getFen(), () -> record.pgn() + " — final FEN mismatch");
      } catch (final AssertionError e) {
        failures.add(BasicUtility.getMessage(e));
      }
    }

    if (!failures.isEmpty()) {
      final var report = new StringBuilder().append(failures.size())
          .append(" python-chess oracle disagreement(s) across ").append(records.size())
          .append(" PARSER_FROM_FEN fixtures:\n");
      for (final String f : failures) {
        report.append("  ").append(f).append('\n');
      }
      fail(Nulls.toString(report));
    }
  }
}
