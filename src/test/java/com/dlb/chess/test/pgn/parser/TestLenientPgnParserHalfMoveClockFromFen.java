package com.dlb.chess.test.pgn.parser;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.pgn.LenientPgnParser;

/**
 * Verifies that the lenient PGN parser correctly initializes board state from a {@code [FEN]} tag and updates the
 * halfmove clock and move number through every recorded halfmove. Iterates {@code parserMechanics/fromFen} and
 * {@code parserMechanics/fromFenNoProgress}, asserting the parsed-and-replayed final FEN matches the registered
 * expected FEN.
 *
 * <p>
 * See {@link AbstractTestPgnParserHalfMoveClockFromFen} for the iteration body and the exact assertion shape. Runs
 * every cycle (no gate) - this is core parser coverage.
 */
class TestLenientPgnParserHalfMoveClockFromFen extends AbstractTestPgnParserHalfMoveClockFromFen {

  private static final Logger logger = Nulls.getLogger(TestLenientPgnParserHalfMoveClockFromFen.class);

  @SuppressWarnings("static-method")
  @Test
  void test() {
    runForBuckets(LenientPgnParser::parse, logger);
  }
}
