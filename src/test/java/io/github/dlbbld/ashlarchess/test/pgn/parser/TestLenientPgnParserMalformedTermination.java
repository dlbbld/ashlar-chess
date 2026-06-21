// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.pgn.LenientPgnParser;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParserValidationException;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParserValidationProblem;

/**
 * Regression for a leak found by the parser fuzz harness: the tokenizer classifies any digits-and-hyphens/slashes run
 * (here {@code "1-2"}) as a termination marker, but only the four canonical results are valid. The lenient parser must
 * surface a malformed marker as a {@link LenientPgnParserValidationException} (caller fault), not let a raw
 * {@code IllegalArgumentException} escape from {@code ResultTagValue.parse}.
 */
class TestLenientPgnParserMalformedTermination {

  @SuppressWarnings("static-method")
  @Test
  void malformedTerminationMarkerIsValidationProblemNotRawException() {
    @SuppressWarnings("null") final LenientPgnParserValidationException exception = assertThrows(
        LenientPgnParserValidationException.class, () -> LenientPgnParser.parseText("1. e4 e5 1-2"));
    assertEquals(LenientPgnParserValidationProblem.MOVETEXT_TERMINATION_MARKER_INVALID,
        exception.getLenientPgnParserValidationProblem());
  }
}
