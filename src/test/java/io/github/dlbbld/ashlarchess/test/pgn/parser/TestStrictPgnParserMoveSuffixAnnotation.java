// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.enums.MoveSuffixAnnotation;
import io.github.dlbbld.ashlarchess.model.PgnMove;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParserValidationException;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParserValidationProblem;
import io.github.dlbbld.ashlarchess.test.pgntest.constants.PgnTestConstants;

class TestStrictPgnParserMoveSuffixAnnotation {

  private static final Path PGN_CUSTOM_TEST_FOLDER_PATH = Nulls
      .pathResolve(PgnTestConstants.STRICT_PGN_PARSER_TEST_ROOT_FOLDER_PATH, "moveSuffixAnnotation");

  // -------------------------------------------------------------------------------------------------
  // Move-suffix annotation - exception and success
  // -------------------------------------------------------------------------------------------------

  private static final Path PGN_TEST_MOVE_SUFFIX_ANNOTATION_EXCEPTION_FOLDER_PATH = Nulls
      .pathResolve(PGN_CUSTOM_TEST_FOLDER_PATH, "moveSuffixAnnotationOnly/exception");

  @SuppressWarnings("static-method")
  @Test
  void testMoveSuffixAnnotationException() {
    // 01: `e4!!!` - three !/? characters lex as one suffix token; the resulting "!!!" fails validation.
    checkMoveSuffixAnnotationException("01_example.pgn",
        StrictPgnParserValidationProblem.MOVETEXT_MOVE_SUFFIX_ANNOTATION_INVALID);
    // 02: `e5!x` - `!` then letter with no space. Tokenises as SUFFIX + SYMBOL, surfaces as UNEXPECTED_FORMAT.
    checkMoveSuffixAnnotationException("02_example.pgn", StrictPgnParserValidationProblem.MOVETEXT_UNEXPECTED_FORMAT);
    // 03: `Qxf7#!?!` - trailing `!?!` lex as one suffix token; "!?!" fails validation.
    checkMoveSuffixAnnotationException("03_example.pgn",
        StrictPgnParserValidationProblem.MOVETEXT_MOVE_SUFFIX_ANNOTATION_INVALID);
  }

  private static void checkMoveSuffixAnnotationException(String pgnName,
      StrictPgnParserValidationProblem expectedProblem) {
    boolean isException = false;
    try {
      PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_MOVE_SUFFIX_ANNOTATION_EXCEPTION_FOLDER_PATH, pgnName);
    } catch (final StrictPgnParserValidationException pre) {
      assertEquals(expectedProblem, pre.getStrictPgnParserValidationProblem());
      isException = true;
    }
    assertTrue(isException);
  }

  private static final Path PGN_TEST_MOVE_SUFFIX_ANNOTATION_SUCCESS_FOLDER_PATH = Nulls
      .pathResolve(PGN_CUSTOM_TEST_FOLDER_PATH, "moveSuffixAnnotationOnly/success");

  @SuppressWarnings("static-method")
  @Test
  void testMoveSuffixAnnotationSuccess() {
    checkMoveSuffixAnnotationSuccess("01_example.pgn",
        Nulls.asList(MoveSuffixAnnotation.BLUNDER, MoveSuffixAnnotation.NONE, MoveSuffixAnnotation.BRILLIANT_MOVE));
    checkMoveSuffixAnnotationSuccess("02_example.pgn",
        Nulls.asList(MoveSuffixAnnotation.BLUNDER, MoveSuffixAnnotation.NONE, MoveSuffixAnnotation.GOOD_MOVE));
  }

  private static void checkMoveSuffixAnnotationSuccess(String pgnName,
      List<MoveSuffixAnnotation> moveSuffixAnnotationListExpected) {
    final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases
        .getPgn(PGN_TEST_MOVE_SUFFIX_ANNOTATION_SUCCESS_FOLDER_PATH, pgnName);
    assertEquals(moveSuffixAnnotationListExpected, calculateMoveSuffixAnnotationList(pgnGame.moveList()));
  }

  // -------------------------------------------------------------------------------------------------
  // Combined (SAN + suffix + commentary) - exception (non-commentary subset) and success
  // -------------------------------------------------------------------------------------------------

  private static final Path PGN_TEST_COMBINED_EXCEPTION_FOLDER_PATH = Nulls.pathResolve(PGN_CUSTOM_TEST_FOLDER_PATH,
      "moveSuffixAnnotationAndCommentary/exception");

  @SuppressWarnings("static-method")
  @Test
  void testCombinedException() {
    checkCombinedException("01_example.pgn", StrictPgnParserValidationProblem.MOVETEXT_SAN_CHARACTER_INVALID);
    checkCombinedException("02_example.pgn", StrictPgnParserValidationProblem.MOVETEXT_MOVE_SUFFIX_ANNOTATION_INVALID);
  }

  private static void checkCombinedException(String pgnName, StrictPgnParserValidationProblem expectedProblem) {
    boolean isException = false;
    try {
      PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_COMBINED_EXCEPTION_FOLDER_PATH, pgnName);
    } catch (final StrictPgnParserValidationException pre) {
      assertEquals(expectedProblem, pre.getStrictPgnParserValidationProblem());
      isException = true;
    }
    assertTrue(isException);
  }

  private static final Path PGN_TEST_COMBINED_SUCCESS_FOLDER_PATH = Nulls.pathResolve(PGN_CUSTOM_TEST_FOLDER_PATH,
      "moveSuffixAnnotationAndCommentary/success");

  @SuppressWarnings("static-method")
  @Test
  void testCombinedSuccess() {
    // SAN + suffix + commentary together. Commentary alone is covered by TestCommentaryStrict.
    checkCombinedSuccess("01_example.pgn", "pregame commentary", Nulls.asList("e4", "d5", "d4"),
        Nulls.asList(MoveSuffixAnnotation.BLUNDER, MoveSuffixAnnotation.NONE, MoveSuffixAnnotation.BRILLIANT_MOVE),
        Nulls.asList("commentWhite1", "commentBlack", "commentWhite2"));
    checkCombinedSuccess("02_example.pgn", "pregame commentary", Nulls.asList("d5", "a3", "Qd6"),
        Nulls.asList(MoveSuffixAnnotation.BLUNDER, MoveSuffixAnnotation.NONE, MoveSuffixAnnotation.BRILLIANT_MOVE),
        Nulls.asList("commentBlack1", "commentWhite", "commentBlack2"));
  }

  private static void checkCombinedSuccess(String pgnName, String pregameCommentaryExpected,
      List<String> sanListExpected, List<MoveSuffixAnnotation> moveSuffixAnnotationListExpected,
      List<String> commentaryListExpected) {
    final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_COMBINED_SUCCESS_FOLDER_PATH, pgnName);
    assertEquals(pregameCommentaryExpected, pgnGame.pregameCommentary().value());
    assertEquals(sanListExpected, calculateSanList(pgnGame.moveList()));
    assertEquals(moveSuffixAnnotationListExpected, calculateMoveSuffixAnnotationList(pgnGame.moveList()));
    assertEquals(commentaryListExpected, calculateCommentaryList(pgnGame.moveList()));
  }

  private static List<String> calculateSanList(List<PgnMove> moveList) {
    final List<String> sanList = new ArrayList<>();
    for (final PgnMove halfMove : moveList) {
      sanList.add(halfMove.san());
    }
    return sanList;
  }

  private static List<MoveSuffixAnnotation> calculateMoveSuffixAnnotationList(List<PgnMove> moveList) {
    final List<MoveSuffixAnnotation> moveSuffixAnnotationList = new ArrayList<>();
    for (final PgnMove halfMove : moveList) {
      moveSuffixAnnotationList.add(halfMove.moveSuffixAnnotation());
    }
    return moveSuffixAnnotationList;
  }

  private static List<String> calculateCommentaryList(List<PgnMove> moveList) {
    final List<String> commentaryList = new ArrayList<>();
    for (final PgnMove halfMove : moveList) {
      commentaryList.add(halfMove.commentary().value());
    }
    return commentaryList;
  }
}
