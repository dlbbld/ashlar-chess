package com.dlb.chess.test.pgn.parser.beyond;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.enums.GameStatus;
import com.dlb.chess.pgn.LenientPgnParser;
import com.dlb.chess.pgn.LenientPgnParserValidationException;
import com.dlb.chess.pgn.LenientPgnParserValidationProblem;
import com.dlb.chess.san.SanValidationProblem;
import com.dlb.chess.test.ConfigurationTestConstants;

/**
 * Verifies that the lenient PGN parser rejects every fixture under {@code src/test/resources/pgnParser/common/beyond/}
 * and that the rejection reason carries the specific {@link GameStatus} that ended the game.
 *
 * <p>
 * Scope is the four enforced FIDE-automatic terminations only: checkmate, stalemate, and dead position by mutual
 * insufficient material (each in a white-move and black-move variant). Fivefold repetition and the 75-move rule are
 * queryable predicates in this library, not enforced at the move pipeline (see
 * {@link GameStatus#isAutomaticTermination()}); their "play past the threshold" cases are accepted by the parser,
 * exercised implicitly via the regular corpus, and pinned explicitly at the move-pipeline level by
 * {@code TestSanValidationGameEnded.testSanAcceptedAtFivefoldThreshold()} and the 75-move companion.
 * {@code DEAD_POSITION_UNWINNABLE_QUICK} is the analyzer-driven dead-position detector — the PGN parsers
 * intentionally do not enforce it on intermediate positions; production-runtime enforcement lives on
 * {@code Board.move(...)} via the {@code detectDeadPositionUnwinnable} constructor flag.
 *
 * <p>
 * Each fixture has its own {@code @Test} method with the expected {@link GameStatus} pinned literally in the method
 * body. No runtime filename parsing.
 *
 * <p>
 * The lenient parser raises {@link LenientPgnParserValidationException} with
 * {@link LenientPgnParserValidationProblem#SAN} and {@link SanValidationProblem#GAME_ALREADY_ENDED}; the
 * {@link LenientPgnParserValidationException#getGameStatus()} accessor returns the specific termination cause, which is
 * asserted directly.
 */
@SuppressWarnings("null") // JUnit Assertions methods lack JDT null annotations
class TestLenientPgnParserBeyondTermination {

  private static final Path BEYOND_FOLDER = Nulls.pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
      "src/test/resources/pgnParser/common/beyond");

  @SuppressWarnings("static-method")
  @Test
  void test01PlayBeyondCheckmateWithWhiteMove() {
    assertRejectedWith("01_play_beyond_checkmate_with_white_move.pgn", GameStatus.CHECKMATE);
  }

  @SuppressWarnings("static-method")
  @Test
  void test02PlayBeyondCheckmateWithBlackMove() {
    assertRejectedWith("02_play_beyond_checkmate_with_black_move.pgn", GameStatus.CHECKMATE);
  }

  @SuppressWarnings("static-method")
  @Test
  void test03PlayBeyondStalemateWithWhiteMove() {
    assertRejectedWith("03_play_beyond_stalemate_with_white_move.pgn", GameStatus.STALEMATE);
  }

  @SuppressWarnings("static-method")
  @Test
  void test04PlayBeyondStalemateWithBlackMove() {
    assertRejectedWith("04_play_beyond_stalemate_with_black_move.pgn", GameStatus.STALEMATE);
  }

  @SuppressWarnings("static-method")
  @Test
  void test05PlayBeyondInsufficientMaterialWithWhiteMove() {
    assertRejectedWith("05_play_beyond_insufficient_material_with_white_move.pgn",
        GameStatus.DEAD_POSITION_INSUFFICIENT_MATERIAL);
  }

  @SuppressWarnings("static-method")
  @Test
  void test06PlayBeyondInsufficientMaterialWithBlackMove() {
    assertRejectedWith("06_play_beyond_insufficient_material_with_black_move.pgn",
        GameStatus.DEAD_POSITION_INSUFFICIENT_MATERIAL);
  }

  private static void assertRejectedWith(String pgnName, GameStatus expectedStatus) {
    final LenientPgnParserValidationException e = assertThrows(LenientPgnParserValidationException.class,
        () -> LenientPgnParser.parse(BEYOND_FOLDER, pgnName));

    assertEquals(LenientPgnParserValidationProblem.SAN, e.getLenientPgnParserValidationProblem());
    assertEquals(SanValidationProblem.GAME_ALREADY_ENDED, e.getSanValidationProblem());
    assertEquals(expectedStatus, e.getGameStatus());
  }
}
