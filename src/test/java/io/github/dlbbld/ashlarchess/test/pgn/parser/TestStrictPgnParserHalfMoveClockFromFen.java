// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.pgn.StrictPgnParser;

/**
 * Verifies that the strict PGN parser correctly initializes board state from a {@code [FEN]} tag and updates the
 * halfmove clock and move number through every recorded move. Iterates {@code parserMechanics/fromFen} and
 * {@code parserMechanics/fromFenNoProgress}, asserting the parsed-and-replayed final FEN matches the registered
 * expected FEN.
 *
 * <p>
 * See {@link TestPgnParserHalfMoveClockFromFenSupport} for the iteration body and the exact assertion shape. Runs every
 * cycle (no gate) - this is core parser coverage.
 */
class TestStrictPgnParserHalfMoveClockFromFen {

  private static final Logger logger = LogManager.getLogger(TestStrictPgnParserHalfMoveClockFromFen.class);

  @SuppressWarnings("static-method")
  @Test
  void test() {
    TestPgnParserHalfMoveClockFromFenSupport.runForBuckets(StrictPgnParser::parsePath, logger);
  }
}
