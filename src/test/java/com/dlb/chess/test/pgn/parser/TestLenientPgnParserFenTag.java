package com.dlb.chess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.pgn.LenientPgnParser;
import com.dlb.chess.pgn.LenientPgnParserValidationProblem;
import com.dlb.chess.pgn.LenientPgnParserValidationResult;

/**
 * The lenient PGN parser routes the FEN tag through {@code LenientFenParser}. When that fails - either because the FEN
 * tag value cannot be normalised to a parseable FEN at all (the unrecoverable case), or because the normalised FEN
 * fails strict semantic validation (a position with a missing king, an impossible double-check, an illegal en-passant
 * target, etc.) - the failure is surfaced as a typed {@link LenientPgnParserValidationProblem#FEN_TAG_INVALID}, not the
 * generic {@code UNKNOWN_ERROR}.
 */
@SuppressWarnings("static-method")
class TestLenientPgnParserFenTag {

  @Test
  void test01_fenTagWithMissingKingFailsAsFenTagInvalid() {
    // The position passes lenient FEN normalisation (it's syntactically a six-field FEN) but FenParserAdvanced
    // rejects it for the missing black king. The lenient PGN parser must surface this as FEN_TAG_INVALID
    // rather than UNKNOWN_ERROR.
    final String pgn = """
        [Event "Test"]
        [Result "*"]
        [SetUp "1"]
        [FEN "rnbq1bnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQ - 0 1"]

        *

        """;
    final LenientPgnParserValidationResult result = LenientPgnParser.validateText(pgn);
    assertFalse(result.isValid());
    assertEquals(LenientPgnParserValidationProblem.FEN_TAG_INVALID, result.problemParser());
    assertNull(result.pgnGame());
    assertTrue(result.message().contains("FEN tag is invalid"));
  }

  @Test
  void test02_fenTagUnparseableFailsAsFenTagInvalid() {
    // The FEN tag is too garbled for the lenient normalisation pipeline to recover (only one field present).
    // The lenient PGN parser must surface this as FEN_TAG_INVALID rather than UNKNOWN_ERROR.
    final String pgn = """
        [Event "Test"]
        [Result "*"]
        [SetUp "1"]
        [FEN "garbage"]

        *

        """;
    final LenientPgnParserValidationResult result = LenientPgnParser.validateText(pgn);
    assertFalse(result.isValid());
    assertEquals(LenientPgnParserValidationProblem.FEN_TAG_INVALID, result.problemParser());
    assertNull(result.pgnGame());
  }

  @Test
  void test03_fenTagWithDeficientButRecoverableValueParsesCleanly() {
    // A FEN tag with deviations the lenient FEN layer forgives (missing fullmove number, uppercase side-to-move)
    // produces a valid lenient PGN parse - the FEN-level forgiveness is applied silently at the PGN-parser
    // level, by design.
    final String pgn = """
        [Event "Test"]
        [Result "*"]
        [SetUp "1"]
        [FEN "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR W KQkq - 0"]

        *

        """;
    final LenientPgnParserValidationResult result = LenientPgnParser.validateText(pgn);
    assertTrue(result.isValid(), () -> "expected valid; got: " + result.message());
  }
}
