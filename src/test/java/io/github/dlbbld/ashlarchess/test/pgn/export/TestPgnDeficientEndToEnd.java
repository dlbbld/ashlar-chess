package io.github.dlbbld.ashlarchess.test.pgn.export;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.pgn.LenientPgnParser;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParserValidationResult;
import io.github.dlbbld.ashlarchess.pgn.PgnCreate;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.StandardTag;
import io.github.dlbbld.ashlarchess.pgn.WriteMode;

/**
 * The headline end-to-end deficient-PGN fixture called out in {@code tasks.md} for this release: a single PGN
 * exercising several lenient-tolerated deviations simultaneously, then asserted under both {@link WriteMode#SEMANTIC}
 * (the parse model echoed, no fabricated tags) and {@link WriteMode#ARCHIVAL} (the canonical spec section
 * 8.1.1-conformant artifact). Covers the semantic-preservation contract that the archival-equivalence helper in
 * {@code AbstractTestLenientPgnParser} does not directly verify.
 */
@SuppressWarnings({ "static-method" })
class TestPgnDeficientEndToEnd {

  /**
   * Deviations exercised. All below:
   * <ul>
   * <li>Missing STR tags ({@code Site}, {@code Date}, {@code Round}, {@code Black}, {@code Result}).</li>
   * <li>Odd whitespace inside the {@code [Event]} tag brackets.</li>
   * <li>FEN tag present without a SetUp tag.</li>
   * <li>Lenient SAN: long-algebraic {@code Qc2-a4} (white queen on c2 in the starting FEN).</li>
   * <li>Bogus check suffix: {@code Ra8+} when the move does not actually give check.</li>
   * </ul>
   */
  private static final String DEFICIENT_PGN = """
      [Event   "Spring Classic"   ]
      [White "Alice"]
      [FEN "r1b2r2/pp1pk1pp/8/7q/3pP1n1/5N1P/PPQ2PP1/3R1RK1 w - - 0 17"]

      17. Qc2-a4 Rb8 18. Qc4 Ra8+
      """;

  @Test
  void test01_lenientParsePreservesDeficientShape() {
    final LenientPgnParserValidationResult result = LenientPgnParser.validateText(DEFICIENT_PGN);
    assertTrue(result.isValid(), () -> "expected valid; got: " + result.message());
    final PgnGame pgnGame = result.pgnGame();
    assertNotNull(pgnGame);
    // Parse model preserves: exactly the three tags the user supplied (Event, White, FEN), nothing fabricated.
    assertEquals(3, pgnGame.tagList().size());
    // No termination marker was in the input.
    assertEquals(null, pgnGame.terminationMarker());
    // Tag-forgiven-items report the deviations (missing STR tags, missing Result/marker, FEN without SetUp).
    assertFalse(result.tagForgivenItems().isEmpty());
    // SAN-forgiven-items report the long-algebraic move and the spurious check suffix.
    assertFalse(result.sanForgivenItems().isEmpty());
  }

  @Test
  void test02_semanticExportEchoesTheParseModel() {
    final PgnGame pgnGame = LenientPgnParser.parseText(DEFICIENT_PGN);
    final String semantic = PgnCreate.createPgnString(pgnGame, WriteMode.SEMANTIC);

    // Tags preserved as given (whitespace normalised inside the brackets).
    assertTrue(semantic.contains("[Event \"Spring Classic\"]"));
    assertTrue(semantic.contains("[White \"Alice\"]"));
    assertTrue(semantic.contains("[FEN \"r1b2r2/pp1pk1pp/8/7q/3pP1n1/5N1P/PPQ2PP1/3R1RK1 w - - 0 17\"]"));

    // No fabrication on the semantic path - missing tags stay missing.
    assertFalse(semantic.contains("[" + StandardTag.SITE.getName() + " "));
    assertFalse(semantic.contains("[" + StandardTag.DATE.getName() + " "));
    assertFalse(semantic.contains("[" + StandardTag.ROUND.getName() + " "));
    assertFalse(semantic.contains("[" + StandardTag.BLACK.getName() + " "));
    assertFalse(semantic.contains("[" + StandardTag.RESULT.getName() + " "));
    assertFalse(semantic.contains("[" + StandardTag.SET_UP.getName() + " "));

    // Movetext: canonical SAN (Qc2-a4 -> Qa4, Ra8+ -> Ra8). Termination marker absent (input had none).
    assertTrue(semantic.contains("Qa4"));
    assertFalse(semantic.contains("Qc2-a4"));
    assertTrue(semantic.contains("Ra8"));
    assertFalse(semantic.contains("Ra8+"));
  }

  @Test
  void test03_archivalExportProducesSpecCompliantOutput() {
    final PgnGame pgnGame = LenientPgnParser.parseText(DEFICIENT_PGN);
    final String archival = PgnCreate.createPgnString(pgnGame, WriteMode.ARCHIVAL);

    // Caller-supplied tag values preserved.
    assertTrue(archival.contains("[Event \"Spring Classic\"]"));
    assertTrue(archival.contains("[White \"Alice\"]"));

    // STR placeholders filled per PGN spec section 8.1.1.
    assertTrue(archival.contains("[" + StandardTag.SITE.getName() + " \"?\"]"));
    assertTrue(archival.contains("[" + StandardTag.DATE.getName() + " \"????.??.??\"]"));
    assertTrue(archival.contains("[" + StandardTag.ROUND.getName() + " \"?\"]"));
    assertTrue(archival.contains("[" + StandardTag.BLACK.getName() + " \"?\"]"));

    // Result tag synthesised from the (defaulted) termination marker.
    assertTrue(archival.contains("[" + StandardTag.RESULT.getName() + " \"*\"]"));

    // SetUp tag added because FEN tag is present.
    assertTrue(archival.contains("[" + StandardTag.SET_UP.getName() + " \"1\"]"));
    assertTrue(archival.contains("[FEN \"r1b2r2/pp1pk1pp/8/7q/3pP1n1/5N1P/PPQ2PP1/3R1RK1 w - - 0 17\"]"));

    // Movetext: canonical SAN + always-emitted termination marker.
    assertTrue(archival.contains("Qa4"));
    assertTrue(archival.contains("Ra8 *") || archival.endsWith("*\n") || archival.contains("Ra8\n*"));
  }
}
