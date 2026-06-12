// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.dlbbld.ashlarchess.pgn.PgnCreate;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.WriteMode;

public abstract class AbstractTestLenientPgnParser {

  /**
   * Asserts that two parse models are equivalent under archival normalisation - i.e. once both are fed through
   * {@link WriteMode#ARCHIVAL} export they produce the same PGN string. Use this instead of full {@code PgnGame}
   * equality when comparing a lenient-parsed deficient variant against its strict-parsed canonical reference: the
   * parsers now preserve input as given, so direct equality fails wherever the inputs differ in tag presence, tag
   * order, redundant FEN/SetUp, or termination-marker presence. Archival normalisation is the right lens for the
   * invariant the original tests intended ("these variants are equivalent under spec section 8.1.1 form").
   */
  static void assertEqualsArchival(PgnGame expected, PgnGame actual) {
    assertEquals(PgnCreate.createPgnString(expected, WriteMode.ARCHIVAL),
        PgnCreate.createPgnString(actual, WriteMode.ARCHIVAL));
  }

  static void assertEqualsButTagList(PgnGame expected, PgnGame actual) {
    assertEquals(expected.moveList(), actual.moveList());
    assertEquals(expected.pregameCommentary(), actual.pregameCommentary());
    // assertEquals(expected.tagList(), actual.tagList());
    assertEquals(expected.startFen(), actual.startFen());
  }

  static void assertEqualsButTagListAndResult(PgnGame expected, PgnGame actual) {
    // assertEquals(expected.resultTagValue(), actual.resultTagValue());
    assertEquals(expected.moveList(), actual.moveList());
    assertEquals(expected.pregameCommentary(), actual.pregameCommentary());
    // assertEquals(expected.tagList(), actual.tagList());
    assertEquals(expected.startFen(), actual.startFen());
  }

}
