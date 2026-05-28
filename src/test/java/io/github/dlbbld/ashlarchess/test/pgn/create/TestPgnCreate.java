package io.github.dlbbld.ashlarchess.test.pgn.create;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.pgn.LenientPgnParser;
import io.github.dlbbld.ashlarchess.pgn.PgnCreate;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.test.PgnTestHelper;

class TestPgnCreate {

  @SuppressWarnings("static-method")
  @Test
  void test() {

    final String expectedString = PgnTestHelper.header("*") + "1. e4 e5 *\n\n";

    final PgnGame fileImport = LenientPgnParser.parseText(expectedString);

    final String actualString = PgnCreate.createPgnString(fileImport);

    assertEquals(expectedString, actualString);

  }

  // -------------------------------------------------------------------------------------------------
  // T-002 - exporter always emits "N..." indicator after intervening commentary on White's move
  //
  // Mirrors python-chess's `force_movenumber` flag. The lenient parser accepts the indicator-less form on import,
  // but on export we always emit it so the round-trip output also satisfies strict validation.
  // -------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void t002_exportEmitsMoveNumberAfterCommentaryOnWhite() {
    // Import (lenient) without the "1..." indicator; export must add it so the produced PGN is strict-valid.
    final String inputWithoutIndicator = PgnTestHelper.header("*") + "1. e4 {after-white} e5 *\n\n";
    final String expectedExport = PgnTestHelper.header("*") + "1. e4 {after-white} 1... e5 *\n\n";

    final PgnGame fileImport = LenientPgnParser.parseText(inputWithoutIndicator);
    final String actualExport = PgnCreate.createPgnString(fileImport);

    assertEquals(expectedExport, actualExport);
  }

  @SuppressWarnings("static-method")
  @Test
  void t002_exportNoIndicatorWhenNoCommentaryIntervenes() {
    // Sanity check: without intervening commentary, exporter must NOT inject any "N..." indicator.
    final String input = PgnTestHelper.header("*") + "1. e4 e5 2. Nf3 Nc6 *\n\n";

    final PgnGame fileImport = LenientPgnParser.parseText(input);
    final String actualExport = PgnCreate.createPgnString(fileImport);

    assertEquals(input, actualExport);
  }

  @SuppressWarnings("static-method")
  @Test
  void t002_exportEmitsMoveNumberAtHigherFullMoveNumber() {
    // The indicator carries the current full-move number. Verify it matches the move number in question, not "1".
    final String inputWithoutIndicator = PgnTestHelper.header("*") + "1. e4 e5 2. Nf3 {after-white-2} Nc6 *\n\n";
    final String expectedExport = PgnTestHelper.header("*") + "1. e4 e5 2. Nf3 {after-white-2} 2... Nc6 *\n\n";

    final PgnGame fileImport = LenientPgnParser.parseText(inputWithoutIndicator);
    final String actualExport = PgnCreate.createPgnString(fileImport);

    assertEquals(expectedExport, actualExport);
  }

}
