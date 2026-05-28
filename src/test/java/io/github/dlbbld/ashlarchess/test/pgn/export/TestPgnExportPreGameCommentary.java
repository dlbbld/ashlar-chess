// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.export;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.pgn.LenientPgnParser;
import io.github.dlbbld.ashlarchess.pgn.PgnCreate;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParser;
import io.github.dlbbld.ashlarchess.test.PgnTestHelper;

class TestPgnExportPreGameCommentary {

  @SuppressWarnings("static-method")
  @Test
  void testFromImportStrictHasMoves() {

    final String pregameCommentary = "This is the pregame commentary.";

    final PgnGame fileImport = StrictPgnParser
        .parseText(PgnTestHelper.header("*") + "{" + pregameCommentary + "}" + " 1. e4 e5 *\n\n");
    assertEquals(pregameCommentary, fileImport.pregameCommentary().value());

    final String fileString = PgnCreate.createPgnString(fileImport);

    final PgnGame fileExport = StrictPgnParser.parseText(fileString);

    assertEquals(pregameCommentary, fileExport.pregameCommentary().value());

  }

  @SuppressWarnings("static-method")
  @Test
  void testFromImportStrictHasNoMoves() {

    final String pregameCommentary = "This is the pregame commentary.";

    final PgnGame fileImport = StrictPgnParser
        .parseText(PgnTestHelper.header("*") + "{" + pregameCommentary + "}" + " *\n\n");
    assertEquals(pregameCommentary, fileImport.pregameCommentary().value());

    final String fileString = PgnCreate.createPgnString(fileImport);

    final PgnGame fileExport = StrictPgnParser.parseText(fileString);

    assertEquals(pregameCommentary, fileExport.pregameCommentary().value());

  }

  @SuppressWarnings("static-method")
  @Test
  void testFromImportStrictLong() {

    final String pregameCommentary = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
    assertTrue(pregameCommentary.length() > PgnCreate.MAX_LINE_LENGTH);

    final PgnGame fileImport = StrictPgnParser
        .parseText(PgnTestHelper.header("*") + "{" + pregameCommentary + "}" + " *\n\n");
    assertEquals(pregameCommentary, fileImport.pregameCommentary().value());

    final String fileString = PgnCreate.createPgnString(fileImport);

    final PgnGame fileExport = StrictPgnParser.parseText(fileString);

    assertEquals(pregameCommentary, fileExport.pregameCommentary().value());

  }

  @SuppressWarnings("static-method")
  @Test
  void testFromImportStrictWithLinebreakIsPreservedThroughRoundTrip() {

    final String pregameCommentary = "This is the pregame\ncommentary.";

    final PgnGame fileImport = StrictPgnParser
        .parseText(PgnTestHelper.header("*") + "{" + pregameCommentary + "}" + " 1. e4 e5 *\n\n");
    assertEquals(pregameCommentary, fileImport.pregameCommentary().value());

    final String fileString = PgnCreate.createPgnString(fileImport);

    final PgnGame fileExport = StrictPgnParser.parseText(fileString);

    assertEquals(pregameCommentary, fileExport.pregameCommentary().value());

  }

  @SuppressWarnings("static-method")
  @Test
  void testFromImportLenientHasMoves() {

    final String pregameCommentary = "This is the pregame commentary.";

    final PgnGame fileImport = LenientPgnParser
        .parseText(PgnTestHelper.header("*") + "{" + pregameCommentary + "}" + " 1. e4 e5 *\n\n");
    assertEquals(pregameCommentary, fileImport.pregameCommentary().value());

    final String fileString = PgnCreate.createPgnString(fileImport);

    final PgnGame fileExport = LenientPgnParser.parseText(fileString);

    assertEquals(pregameCommentary, fileExport.pregameCommentary().value());

  }

  @SuppressWarnings("static-method")
  @Test
  void testFromImportLenientHasNoMoves() {

    final String pregameCommentary = "This is the pregame" + " commentary.";

    final PgnGame fileImport = LenientPgnParser
        .parseText(PgnTestHelper.header("*") + "{" + pregameCommentary + "}" + " *\n\n");
    assertEquals(pregameCommentary, fileImport.pregameCommentary().value());

    final String fileString = PgnCreate.createPgnString(fileImport);

    final PgnGame fileExport = LenientPgnParser.parseText(fileString);

    assertEquals(pregameCommentary, fileExport.pregameCommentary().value());

  }

  @SuppressWarnings("static-method")
  @Test
  void testFromImportLenientLong() {

    final String pregameCommentary = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
    assertTrue(pregameCommentary.length() > PgnCreate.MAX_LINE_LENGTH);

    final PgnGame fileImport = LenientPgnParser
        .parseText(PgnTestHelper.header("*") + "{" + pregameCommentary + "}" + " *\n\n");
    assertEquals(pregameCommentary, fileImport.pregameCommentary().value());

    final String fileString = PgnCreate.createPgnString(fileImport);

    final PgnGame fileExport = LenientPgnParser.parseText(fileString);

    assertEquals(pregameCommentary, fileExport.pregameCommentary().value());

  }

  // -------------------------------------------------------------------------------------------------
  // Round-trip property: parse(export(parse(text))) == parse(text). T-005 normalises CR/CRLF to LF.
  // -------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void roundTripPreGameCommentaryWithNewline() {
    assertRoundTripStable(PgnTestHelper.header("*") + "{line one\nline two} 1. e4 e5 *\n\n");
  }

  @SuppressWarnings("static-method")
  @Test
  void roundTripPreGameCommentaryWithTab() {
    assertRoundTripStable(PgnTestHelper.header("*") + "{line one\tline two} 1. e4 e5 *\n\n");
  }

  @SuppressWarnings("static-method")
  @Test
  void roundTripPreGameCommentaryWithCrlf() {
    assertRoundTripStable(PgnTestHelper.header("*") + "{line one\r\nline two} 1. e4 e5 *\n\n");
  }

  @SuppressWarnings("static-method")
  @Test
  void roundTripMoveCommentaryWithNewline() {
    assertRoundTripStable(PgnTestHelper.header("*") + "1. e4 {white\nmove note} e5 *\n\n");
  }

  @SuppressWarnings("static-method")
  @Test
  void roundTripMoveCommentaryWithTab() {
    assertRoundTripStable(PgnTestHelper.header("*") + "1. e4 {white\tmove note} e5 *\n\n");
  }

  @SuppressWarnings("static-method")
  @Test
  void roundTripBothLeadingAndMoveCommentary() {
    assertRoundTripStable(PgnTestHelper.header("*") + "{intro\nline} 1. e4 {one\ttwo} e5 {three\rfour} *\n\n");
  }

  @SuppressWarnings("static-method")
  @Test
  void roundTripDoubleSpacesPreserved() {
    assertRoundTripStable(PgnTestHelper.header("*") + "1. e4 {a  b  c} e5 *\n\n");
  }

  private static void assertRoundTripStable(String inputPgn) {
    final PgnGame p1 = LenientPgnParser.parseText(inputPgn);
    final String t1 = PgnCreate.createPgnString(p1);
    final PgnGame p2 = LenientPgnParser.parseText(t1);

    assertEquals(p1, p2, "Round-trip changed the parsed model");
  }

  @SuppressWarnings("static-method")
  @Test
  void crlfInputProducesLfOnlyExport() {
    // T-005: export emits LF only.
    final PgnGame fileImport = LenientPgnParser
        .parseText(PgnTestHelper.header("*") + "{intro\r\nnote} 1. e4 {move\r\nnote} e5 *\n\n");
    final String exported = PgnCreate.createPgnString(fileImport);
    org.junit.jupiter.api.Assertions.assertFalse(exported.contains("\r"));
  }

}
