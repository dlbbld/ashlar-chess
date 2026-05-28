// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.fen.constants.FenConstants;
import io.github.dlbbld.ashlarchess.model.PgnHalfMove;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.StandardTag;
import io.github.dlbbld.ashlarchess.pgn.TagUtility;
import io.github.dlbbld.ashlarchess.test.pgntest.constants.PgnTestConstants;

class TestStrictPgnParserReadTag {

  private static List<String> calculateSanList(List<PgnHalfMove> halfMoveList) {
    final List<String> sanList = new ArrayList<>();
    for (final PgnHalfMove halfMove : halfMoveList) {
      sanList.add(halfMove.san());
    }
    return sanList;
  }

  private static final Path PGN_TEST_FOLDER_PATH = Nulls
      .pathResolve(PgnTestConstants.STRICT_PGN_PARSER_TEST_ROOT_FOLDER_PATH, "tag");

  @SuppressWarnings("static-method")
  @Test
  void test() {
    {
      final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
          "01_example_white_last_move.pgn");

      assertEquals("Groningen", TagUtility.calculateTagValue(pgnGame, StandardTag.EVENT));
      assertEquals("Groningen NED", TagUtility.calculateTagValue(pgnGame, StandardTag.SITE));
      assertEquals("1997.??.??", TagUtility.calculateTagValue(pgnGame, StandardTag.DATE));
      assertEquals("9", TagUtility.calculateTagValue(pgnGame, StandardTag.ROUND));
      assertEquals("Pavel Blatny", TagUtility.calculateTagValue(pgnGame, StandardTag.WHITE));
      assertEquals("Frank Holzke", TagUtility.calculateTagValue(pgnGame, StandardTag.BLACK));
      assertEquals("1/2-1/2", TagUtility.calculateTagValue(pgnGame, StandardTag.RESULT));
      assertEquals("A15", TagUtility.calculateTagValue(pgnGame, "ECO"));
      assertEquals("1997.??.??", TagUtility.calculateTagValue(pgnGame, "EventDate"));

      assertEquals(FenConstants.FEN_INITIAL, pgnGame.startFen());
    }

    {
      final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
          "02_example_black_last_move.pgn");

      assertEquals(FenConstants.FEN_INITIAL, pgnGame.startFen());
    }

    {
      final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
          "03_example_white_last_move_short.pgn");
      final List<String> halfMoveList = new ArrayList<>();
      halfMoveList.add("Nf3");
      halfMoveList.add("Nf6");
      halfMoveList.add("c4");
      assertEquals(halfMoveList, calculateSanList(pgnGame.halfMoveList()));

      assertEquals(FenConstants.FEN_INITIAL, pgnGame.startFen());
    }

    {
      final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
          "04_example_black_last_move_short.pgn");
      final List<String> halfMoveList = new ArrayList<>();
      halfMoveList.add("Nf3");
      halfMoveList.add("Nf6");
      halfMoveList.add("c4");
      halfMoveList.add("c5");
      assertEquals(halfMoveList, calculateSanList(pgnGame.halfMoveList()));

      assertEquals(FenConstants.FEN_INITIAL, pgnGame.startFen());
    }

    {
      // we want to check that the initial position - the easiest case - is read properly
      final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
          "05_set_up_value_1_fen_initial_position.pgn");
      assertEquals(FenConstants.FEN_INITIAL, pgnGame.startFen());
    }

    {
      // the FEN must be there for this case
      final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
          "06_set_up_value_1_fen_non_initial_position.pgn");
      assertEquals("3k4/8/8/8/3K4/3R4/8/8 w - - 0 100", pgnGame.startFen().fen());
    }

    {
      // the FEN must not be there for this case
      final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
          "07_set_up_value_0_fen_not_set.pgn");
      assertEquals(FenConstants.FEN_INITIAL_STR, pgnGame.startFen().fen());
    }

  }

}
