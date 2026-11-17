// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.dlbbld.ashlarchess.pgn.LenientPgnParser;

class TestPgnTokenBoundaries {

  @SuppressWarnings("static-method")
  @ParameterizedTest
  @ValueSource(strings = { "1. e4(1. d4)e5*", "1. e4 (1. d4)e5 *",
      "1. e4(1. d4(1. c4))(1. Nf3)e5*", "1. e4(1. d4 {a ) ( comment} d5)e5*",
      "1. e4(1. d4 $1! d5)e5*", "1. e4(1. d4; ) ignored\n d5)e5*" })
  void variationsPreserveAdjacentMainlineMoves(String text) {
    assertEquals(LenientPgnParser.parseText("1. e4 e5 *"), LenientPgnParser.parseText(text));
  }

  @SuppressWarnings("static-method")
  @ParameterizedTest
  @ValueSource(strings = { "1. e4 (1. d4)*", "1. e4(1. d4)*", "1. e4*" })
  void terminationMarkerNeedsNoSeparatingSpace(String text) {
    assertEquals(LenientPgnParser.parseText("1. e4 *"), LenientPgnParser.parseText(text));
  }
}
