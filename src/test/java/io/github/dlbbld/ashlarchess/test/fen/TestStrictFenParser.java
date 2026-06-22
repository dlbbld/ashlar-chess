// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.fen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.fen.StrictFenSemanticValidationProblem;
import io.github.dlbbld.ashlarchess.fen.StrictFenParser;
import io.github.dlbbld.ashlarchess.fen.StrictFenParserValidationResult;
import io.github.dlbbld.ashlarchess.fen.StrictFenSemanticValidationException;
import io.github.dlbbld.ashlarchess.fen.FenConstants;
import io.github.dlbbld.ashlarchess.fen.model.Fen;

class TestStrictFenParser {

  @SuppressWarnings("static-method")
  @Test
  void parseCanonicalInitialFen() {
    final Fen fen = StrictFenParser.parse(FenConstants.FEN_INITIAL_STR);

    assertEquals(FenConstants.FEN_INITIAL, fen);
  }

  @SuppressWarnings("static-method")
  @Test
  void validateCanonicalInitialFen() {
    final StrictFenParserValidationResult result = StrictFenParser.validate(FenConstants.FEN_INITIAL_STR);

    assertTrue(result.isValid());
    assertEquals(StrictFenSemanticValidationProblem.SUCCESS, result.problem());
    assertEquals("OK", result.message());
    assertNotNull(result.fen());
  }

  @SuppressWarnings("static-method")
  @Test
  void validateInvalidFenReturnsProblem() {
    final String fenWithInvalidSideToMove = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR x KQkq - 0 1";

    final StrictFenParserValidationResult result = StrictFenParser.validate(fenWithInvalidSideToMove);

    assertFalse(result.isValid());
    assertEquals(StrictFenSemanticValidationProblem.INVALID_SIDE_TO_MOVE_RANGE, result.problem());
    assertNull(result.fen());
  }

  @SuppressWarnings("static-method")
  @Test
  void parseInvalidFenThrowsStrictException() {
    final String fenWithInvalidSideToMove = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR x KQkq - 0 1";

    @SuppressWarnings("null") final StrictFenSemanticValidationException exception = assertThrows(
        StrictFenSemanticValidationException.class, () -> StrictFenParser.parse(fenWithInvalidSideToMove));

    assertEquals(StrictFenSemanticValidationProblem.INVALID_SIDE_TO_MOVE_RANGE,
        exception.getStrictFenSemanticValidationProblem());
  }

}
