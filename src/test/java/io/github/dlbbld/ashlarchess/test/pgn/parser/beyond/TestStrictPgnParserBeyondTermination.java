// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser.beyond;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParser;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParserValidationException;
import io.github.dlbbld.ashlarchess.test.ConfigurationTestConstants;

/**
 * Strict-parser counterpart of {@link TestLenientPgnParserBeyondTermination}. Same behavioral split: checkmate /
 * stalemate fixtures (01-04) are rejected through ordinary legality (legal-move set is empty, attempted move cannot
 * match), insufficient-material fixtures (05-06) are accepted (queryable only, legal moves exist).
 */
class TestStrictPgnParserBeyondTermination {

  private static final Path BEYOND_FOLDER = Nulls.pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
      "src/test/resources/pgnParser/common/beyond");

  @SuppressWarnings("static-method")
  @Test
  void test01PlayBeyondCheckmateWithWhiteMove() {
    assertRejectedNotViaGameEnded("01_play_beyond_checkmate_with_white_move.pgn");
  }

  @SuppressWarnings("static-method")
  @Test
  void test02PlayBeyondCheckmateWithBlackMove() {
    assertRejectedNotViaGameEnded("02_play_beyond_checkmate_with_black_move.pgn");
  }

  @SuppressWarnings("static-method")
  @Test
  void test03PlayBeyondStalemateWithWhiteMove() {
    assertRejectedNotViaGameEnded("03_play_beyond_stalemate_with_white_move.pgn");
  }

  @SuppressWarnings("static-method")
  @Test
  void test04PlayBeyondStalemateWithBlackMove() {
    assertRejectedNotViaGameEnded("04_play_beyond_stalemate_with_black_move.pgn");
  }

  @SuppressWarnings("static-method")
  @Test
  void test05PlayBeyondInsufficientMaterialWithWhiteMove() {
    assertAccepted("05_play_beyond_insufficient_material_with_white_move.pgn");
  }

  @SuppressWarnings("static-method")
  @Test
  void test06PlayBeyondInsufficientMaterialWithBlackMove() {
    assertAccepted("06_play_beyond_insufficient_material_with_black_move.pgn");
  }

  private static void assertRejectedNotViaGameEnded(String pgnName) {
    assertThrows(StrictPgnParserValidationException.class, () -> StrictPgnParser.parse(BEYOND_FOLDER, pgnName));
  }

  private static void assertAccepted(String pgnName) {
    assertDoesNotThrow(() -> StrictPgnParser.parse(BEYOND_FOLDER, pgnName),
        "insufficient material is queryable only; the parser must replay the full game");
  }
}
