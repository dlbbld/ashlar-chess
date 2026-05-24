package com.dlb.chess.board;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.board.enums.Side;

class TestInsufficientMaterialUtility {

  private static BitboardPosition position(String fen) {
    return new Board(fen).getBitboardPosition();
  }

  // =====================================================================
  // Branch 1: King only â€” always insufficient
  // =====================================================================

  @SuppressWarnings("static-method")
  @Test
  void testKingOnlyWhite() {
    // K vs k â€” white has king only
    final var pos = position("4k3/8/8/8/8/8/8/4K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingOnlyVsQueen() {
    // K vs k+q â€” white has king only (insufficient), black has queen (sufficient)
    final var pos = position("3qk3/8/8/8/8/8/8/4K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingOnlyVsRook() {
    // K vs k+r â€” white king only (insufficient), black has rook (sufficient)
    final var pos = position("r3k3/8/8/8/8/8/8/4K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingOnlyVsPawn() {
    // K vs k+p â€” white king only (insufficient), black has pawn (sufficient)
    final var pos = position("4k3/p7/8/8/8/8/8/4K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  // =====================================================================
  // Branch 2: King + knight only â€” insufficient if opponent has
  // zero or multiple queens only (no other pieces)
  // =====================================================================

  @SuppressWarnings("static-method")
  @Test
  void testKingAndKnightVsKingOnly() {
    // K+N vs k â€” opponent has king only (zero queens only) â†’ insufficient
    final var pos = position("4k3/8/8/8/8/8/8/1N2K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingAndKnightVsKingAndQueen() {
    // K+N vs k+q â€” opponent has queens only â†’ insufficient for white
    final var pos = position("3qk3/8/8/8/8/8/8/1N2K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingAndKnightVsKingAndTwoQueens() {
    // K+N vs k+q+q â€” opponent has multiple queens only â†’ insufficient for white
    final var pos = position("2qqk3/8/8/8/8/8/8/1N2K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingAndKnightVsKingAndRook() {
    // K+N vs k+r â€” opponent has rook (not queens only) â†’ sufficient for white
    final var pos = position("r3k3/8/8/8/8/8/8/1N2K3 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingAndKnightVsKingAndPawn() {
    // K+N vs k+p â€” opponent has pawn (not queens only) â†’ sufficient for white
    final var pos = position("4k3/p7/8/8/8/8/8/1N2K3 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingAndKnightVsKingAndKnight() {
    // K+N vs k+n â€” opponent has knight (not queens only) â†’ sufficient for white
    final var pos = position("1n2k3/8/8/8/8/8/8/1N2K3 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  // Black side: K+n vs K
  @SuppressWarnings("static-method")
  @Test
  void testKingAndKnightBlackVsKingOnly() {
    // k+n vs K â€” black has king+knight, white has king only â†’ insufficient for black
    final var pos = position("1n2k3/8/8/8/8/8/8/4K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingAndKnightBlackVsKingAndQueen() {
    // k+n vs K+Q â€” insufficient for black (opponent has queens only)
    final var pos = position("1n2k3/8/8/8/8/8/8/3QK3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingAndKnightBlackVsKingAndRook() {
    // k+n vs K+R â€” sufficient for black (opponent has rook)
    final var pos = position("1n2k3/8/8/8/8/8/8/R3K3 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  // =====================================================================
  // Branch 3: Light-square bishops only â€” insufficient if opponent
  // has no pawn, no knight, no dark-square bishop
  // =====================================================================

  @SuppressWarnings("static-method")
  @Test
  void testLightBishopOnlyVsKingOnly() {
    // K+B(light) vs k â€” opponent has no pawn, no knight, no dark bishop â†’ insufficient
    // f1 is a light square
    final var pos = position("4k3/8/8/8/8/8/8/4KB2 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testLightBishopOnlyVsLightBishopOnly() {
    // K+B(light) vs k+b(light) â€” opponent has light bishop only (no dark bishop) â†’ insufficient
    // f1=light, f8=light
    final var pos = position("4k3/8/8/5b2/4B3/8/8/4K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testLightBishopOnlyVsDarkBishop() {
    // K+B(light) vs k+b(dark) â€” opponent HAS dark bishop â†’ sufficient
    // f1=light, b8=dark
    final var pos = position("1b2k3/8/8/8/8/8/8/4KB2 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testLightBishopOnlyVsKnight() {
    // K+B(light) vs k+n â€” opponent has knight â†’ sufficient
    final var pos = position("1n2k3/8/8/8/8/8/8/4KB2 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testLightBishopOnlyVsPawn() {
    // K+B(light) vs k+p â€” opponent has pawn â†’ sufficient
    final var pos = position("4k3/p7/8/8/8/8/8/4KB2 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testTwoLightBishopsVsKingOnly() {
    // K+B(light)+B(light) vs k â€” multiple light bishops, no opponent pieces â†’ insufficient
    // f1=light, d3=light
    final var pos = position("4k3/8/8/8/8/3B4/8/4KB2 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  // Black side: light bishops
  @SuppressWarnings("static-method")
  @Test
  void testLightBishopOnlyBlackVsKingOnly() {
    // k+b(light) vs K â€” insufficient for black
    // f8 is a light square
    final var pos = position("4kb2/8/8/8/8/8/8/4K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testLightBishopOnlyBlackVsDarkBishop() {
    // k+b(light) vs K+B(dark) â€” opponent has dark bishop â†’ sufficient for black
    // f8=light, c1=dark
    final var pos = position("4k3/8/8/4b3/8/1B6/8/4K3 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  // =====================================================================
  // Branch 4: Dark-square bishops only â€” insufficient if opponent
  // has no pawn, no knight, no light-square bishop
  // =====================================================================

  @SuppressWarnings("static-method")
  @Test
  void testDarkBishopOnlyVsKingOnly() {
    // K+B(dark) vs k â€” opponent has no pawn, no knight, no light bishop â†’ insufficient
    // c1 is a dark square
    final var pos = position("4k3/8/8/8/8/8/8/2B1K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testDarkBishopOnlyVsDarkBishopOnly() {
    // K+B(dark) vs k+b(dark) â€” opponent has dark bishop only (no light bishop) â†’ insufficient
    // c1=dark, c8=dark
    final var pos = position("4k3/4b3/8/8/8/8/8/2B1K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testDarkBishopOnlyVsLightBishop() {
    // K+B(dark) vs k+b(light) â€” opponent HAS light bishop â†’ sufficient
    // c1=dark, f8=light
    final var pos = position("4k1b1/8/8/8/8/8/8/2B1K3 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testDarkBishopOnlyVsKnight() {
    // K+B(dark) vs k+n â€” opponent has knight â†’ sufficient
    final var pos = position("1n2k3/8/8/8/8/8/8/2B1K3 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testDarkBishopOnlyVsPawn() {
    // K+B(dark) vs k+p â€” opponent has pawn â†’ sufficient
    final var pos = position("4k3/p7/8/8/8/8/8/2B1K3 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testTwoDarkBishopsVsKingOnly() {
    // K+B(dark)+B(dark) vs k â€” multiple dark bishops â†’ insufficient
    // c1=dark, a3=dark
    final var pos = position("4k3/8/8/8/8/B7/8/2B1K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  // Black side: dark bishops
  @SuppressWarnings("static-method")
  @Test
  void testDarkBishopOnlyBlackVsKingOnly() {
    // k+b(dark) vs K â€” insufficient for black
    // c8 is a dark square
    final var pos = position("2b1k3/8/8/8/8/8/8/4K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testDarkBishopOnlyBlackVsLightBishop() {
    // k+b(dark) vs K+B(light) â€” opponent has light bishop â†’ sufficient for black
    // c8=dark, f1=light
    final var pos = position("2b1k3/8/8/8/8/8/8/4K1B1 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  // =====================================================================
  // Multiple bishops symmetry tests
  // =====================================================================

  // --- Multiple light-square bishops (black side) ---

  @SuppressWarnings("static-method")
  @Test
  void testTwoLightBishopsBlackVsKingOnly() {
    // k+b(light)+b(light) vs K â€” multiple light bishops for black â†’ insufficient
    // e4=light, g6=light
    final var pos = position("4k3/8/6b1/8/4b3/8/8/4K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  // --- Multiple dark-square bishops (black side) ---

  @SuppressWarnings("static-method")
  @Test
  void testTwoDarkBishopsBlackVsKingOnly() {
    // k+b(dark)+b(dark) vs K â€” multiple dark bishops for black â†’ insufficient
    // d7=dark, f5=dark
    final var pos = position("4k3/3b4/8/5b2/8/8/8/4K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  // --- Multiple light-square bishops vs multiple light-square bishops ---

  @SuppressWarnings("static-method")
  @Test
  void testMultipleLightBishopsVsMultipleLightBishops() {
    // K+B(light)+B(light) vs k+b(light)+b(light) â€” both sides have only light bishops â†’ both insufficient
    // White: e4=light, c2=light; Black: d7=light (wait, d7 is dark)
    // Light squares: a2, c2, e2, g2, b3, d3, f3, h3, a4, c4, e4, g4, ...
    // White: c2=light, e4=light; Black: f5=light (f5: file f=6, rank 5 â†’ 6+5=11=odd â†’ dark!)
    // Let me be careful: light square = file+rank is even (both odd or both even)
    // a1=dark, b1=light, c1=dark, d1=light, e1=dark, f1=light, g1=dark, h1=light
    // a2=light, b2=dark, c2=light, d2=dark, e2=light, f2=dark, g2=light, h2=dark
    // White: c2=light, e4=light; Black: b7=light, d5=light
    final var pos = position("4k3/1b6/8/3b4/4B3/8/2B5/4K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  // --- Multiple dark-square bishops vs multiple dark-square bishops ---

  @SuppressWarnings("static-method")
  @Test
  void testMultipleDarkBishopsVsMultipleDarkBishops() {
    // K+B(dark)+B(dark) vs k+b(dark)+b(dark) â€” both sides have only dark bishops â†’ both insufficient
    // Dark squares: a1, c1, e1, g1, b2, d2, f2, h2, a3, c3, e3, g3, ...
    // White: a1=dark, c3=dark; Black: b8=dark, d6=dark
    final var pos = position("1b2k3/8/3b4/8/8/2B5/8/B3K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  // --- Multiple light-square bishops vs single light-square bishop ---

  @SuppressWarnings("static-method")
  @Test
  void testMultipleLightBishopsVsSingleLightBishop() {
    // K+B(light)+B(light) vs k+b(light) â€” both sides light bishops only â†’ both insufficient
    // White: c2=light, e4=light; Black: b7=light
    final var pos = position("4k3/1b6/8/8/4B3/8/2B5/4K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  // --- Multiple dark-square bishops vs single dark-square bishop ---

  @SuppressWarnings("static-method")
  @Test
  void testMultipleDarkBishopsVsSingleDarkBishop() {
    // K+B(dark)+B(dark) vs k+b(dark) â€” both sides dark bishops only â†’ both insufficient
    // White: a1=dark, c3=dark; Black: b8=dark
    final var pos = position("1b2k3/8/8/8/8/2B5/8/B3K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  // --- Single light-square bishop vs multiple light-square bishops ---

  @SuppressWarnings("static-method")
  @Test
  void testSingleLightBishopVsMultipleLightBishops() {
    // K+B(light) vs k+b(light)+b(light) â€” both sides light bishops only â†’ both insufficient
    // White: e4=light; Black: b7=light, d5=light
    final var pos = position("4k3/1b6/8/3b4/4B3/8/8/4K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  // --- Single dark-square bishop vs multiple dark-square bishops ---

  @SuppressWarnings("static-method")
  @Test
  void testSingleDarkBishopVsMultipleDarkBishops() {
    // K+B(dark) vs k+b(dark)+b(dark) â€” both sides dark bishops only â†’ both insufficient
    // White: c3=dark; Black: b8=dark, d6=dark
    final var pos = position("1b2k3/8/3b4/8/8/2B5/8/4K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  // =====================================================================
  // Branch 5: Otherwise â€” sufficient (has rook, queen, pawn, etc.)
  // =====================================================================

  @SuppressWarnings("static-method")
  @Test
  void testSufficientRook() {
    final var pos = position("4k3/8/8/8/8/8/8/R3K3 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testSufficientQueen() {
    final var pos = position("4k3/8/8/8/8/8/8/3QK3 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testSufficientPawn() {
    final var pos = position("4k3/8/8/8/8/8/P7/4K3 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testSufficientKnightAndBishop() {
    // K+N+B â€” sufficient material to checkmate
    final var pos = position("4k3/8/8/8/8/8/8/1NB1K3 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testSufficientOppositeBishops() {
    // K+B(light)+B(dark) â€” has both colors, not "light only" or "dark only"
    // f1=light, c1=dark (wait, let me use correct squares)
    // c1=dark, f1=light... but both on first rank for white? c1 is dark, d1 is light.
    // Let me use: B on c1 (dark) and B on f1 (light) â€” but that's two bishops, one dark one light
    // This won't match branch 3 (not light-only) or branch 4 (not dark-only) â†’ falls through â†’ sufficient
    final var pos = position("4k3/8/8/8/8/8/8/2B1KB2 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testSufficientBlackRook() {
    final var pos = position("r3k3/8/8/8/8/8/8/4K3 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testSufficientBlackPawn() {
    final var pos = position("4k3/p7/8/8/8/8/8/4K3 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testInitialPosition() {
    final var pos = position("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

}
