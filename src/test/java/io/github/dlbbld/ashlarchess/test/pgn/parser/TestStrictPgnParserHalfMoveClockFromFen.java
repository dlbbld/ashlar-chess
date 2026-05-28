package io.github.dlbbld.ashlarchess.test.pgn.parser;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParser;

/**
 * Verifies that the strict PGN parser correctly initializes board state from a {@code [FEN]} tag and updates the
 * halfmove clock and move number through every recorded halfmove. Iterates {@code parserMechanics/fromFen} and
 * {@code parserMechanics/fromFenNoProgress}, asserting the parsed-and-replayed final FEN matches the registered
 * expected FEN.
 *
 * <p>
 * See {@link AbstractTestPgnParserHalfMoveClockFromFen} for the iteration body and the exact assertion shape. Runs
 * every cycle (no gate) - this is core parser coverage.
 */
class TestStrictPgnParserHalfMoveClockFromFen extends AbstractTestPgnParserHalfMoveClockFromFen {

  private static final Logger logger = Nulls.getLogger(TestStrictPgnParserHalfMoveClockFromFen.class);

  @SuppressWarnings("static-method")
  @Test
  void test() {
    runForBuckets(StrictPgnParser::parse, logger);
  }
}
