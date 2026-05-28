package io.github.dlbbld.ashlarchess.board;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.InsufficientMaterialUtility;
import io.github.dlbbld.ashlarchess.board.enums.Side;

class TestInsufficientMaterialUtility {

  private static BitboardPosition position(String fen) {
    return new Board(fen).getBitboardPosition();
  }

  // =====================================================================
  // Branch 1: King only - always insufficient
  // =====================================================================

  @SuppressWarnings("static-method")
  @Test
  void testKingOnlyWhite() {
    // K vs k - white has king only
    final BitboardPosition pos = position("4k3/8/8/8/8/8/8/4K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingOnlyVsQueen() {
    // K vs k+q - white has king only (insufficient), black has queen (sufficient)
    final BitboardPosition pos = position("3qk3/8/8/8/8/8/8/4K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingOnlyVsRook() {
    // K vs k+r - white king only (insufficient), black has rook (sufficient)
    final BitboardPosition pos = position("r3k3/8/8/8/8/8/8/4K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingOnlyVsPawn() {
    // K vs k+p - white king only (insufficient), black has pawn (sufficient)
    final BitboardPosition pos = position("4k3/p7/8/8/8/8/8/4K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  // =====================================================================
  // Branch 2: King + knight only - insufficient if opponent has
  // zero or multiple queens only (no other pieces)
  // =====================================================================

  @SuppressWarnings("static-method")
  @Test
  void testKingAndKnightVsKingOnly() {
    // K+N vs k - opponent has king only (zero queens only) -> insufficient
    final BitboardPosition pos = position("4k3/8/8/8/8/8/8/1N2K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingAndKnightVsKingAndQueen() {
    // K+N vs k+q - opponent has queens only -> insufficient for white
    final BitboardPosition pos = position("3qk3/8/8/8/8/8/8/1N2K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingAndKnightVsKingAndTwoQueens() {
    // K+N vs k+q+q - opponent has multiple queens only -> insufficient for white
    final BitboardPosition pos = position("2qqk3/8/8/8/8/8/8/1N2K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingAndKnightVsKingAndRook() {
    // K+N vs k+r - opponent has rook (not queens only) -> sufficient for white
    final BitboardPosition pos = position("r3k3/8/8/8/8/8/8/1N2K3 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingAndKnightVsKingAndPawn() {
    // K+N vs k+p - opponent has pawn (not queens only) -> sufficient for white
    final BitboardPosition pos = position("4k3/p7/8/8/8/8/8/1N2K3 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingAndKnightVsKingAndKnight() {
    // K+N vs k+n - opponent has knight (not queens only) -> sufficient for white
    final BitboardPosition pos = position("1n2k3/8/8/8/8/8/8/1N2K3 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  // Black side: K+n vs K
  @SuppressWarnings("static-method")
  @Test
  void testKingAndKnightBlackVsKingOnly() {
    // k+n vs K - black has king+knight, white has king only -> insufficient for black
    final BitboardPosition pos = position("1n2k3/8/8/8/8/8/8/4K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingAndKnightBlackVsKingAndQueen() {
    // k+n vs K+Q - insufficient for black (opponent has queens only)
    final BitboardPosition pos = position("1n2k3/8/8/8/8/8/8/3QK3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingAndKnightBlackVsKingAndRook() {
    // k+n vs K+R - sufficient for black (opponent has rook)
    final BitboardPosition pos = position("1n2k3/8/8/8/8/8/8/R3K3 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  // =====================================================================
  // Branch 3: Light-square bishops only - insufficient if opponent
  // has no pawn, no knight, no dark-square bishop
  // =====================================================================

  @SuppressWarnings("static-method")
  @Test
  void testLightBishopOnlyVsKingOnly() {
    // K+B(light) vs k - opponent has no pawn, no knight, no dark bishop -> insufficient
    // f1 is a light square
    final BitboardPosition pos = position("4k3/8/8/8/8/8/8/4KB2 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testLightBishopOnlyVsLightBishopOnly() {
    // K+B(light) vs k+b(light) - opponent has light bishop only (no dark bishop) -> insufficient
    // f1=light, f8=light
    final BitboardPosition pos = position("4k3/8/8/5b2/4B3/8/8/4K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testLightBishopOnlyVsDarkBishop() {
    // K+B(light) vs k+b(dark) - opponent HAS dark bishop -> sufficient
    // f1=light, b8=dark
    final BitboardPosition pos = position("1b2k3/8/8/8/8/8/8/4KB2 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testLightBishopOnlyVsKnight() {
    // K+B(light) vs k+n - opponent has knight -> sufficient
    final BitboardPosition pos = position("1n2k3/8/8/8/8/8/8/4KB2 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testLightBishopOnlyVsPawn() {
    // K+B(light) vs k+p - opponent has pawn -> sufficient
    final BitboardPosition pos = position("4k3/p7/8/8/8/8/8/4KB2 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testTwoLightBishopsVsKingOnly() {
    // K+B(light)+B(light) vs k - multiple light bishops, no opponent pieces -> insufficient
    // f1=light, d3=light
    final BitboardPosition pos = position("4k3/8/8/8/8/3B4/8/4KB2 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  // Black side: light bishops
  @SuppressWarnings("static-method")
  @Test
  void testLightBishopOnlyBlackVsKingOnly() {
    // k+b(light) vs K - insufficient for black
    // f8 is a light square
    final BitboardPosition pos = position("4kb2/8/8/8/8/8/8/4K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testLightBishopOnlyBlackVsDarkBishop() {
    // k+b(light) vs K+B(dark) - opponent has dark bishop -> sufficient for black
    // f8=light, c1=dark
    final BitboardPosition pos = position("4k3/8/8/4b3/8/1B6/8/4K3 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  // =====================================================================
  // Branch 4: Dark-square bishops only - insufficient if opponent
  // has no pawn, no knight, no light-square bishop
  // =====================================================================

  @SuppressWarnings("static-method")
  @Test
  void testDarkBishopOnlyVsKingOnly() {
    // K+B(dark) vs k - opponent has no pawn, no knight, no light bishop -> insufficient
    // c1 is a dark square
    final BitboardPosition pos = position("4k3/8/8/8/8/8/8/2B1K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testDarkBishopOnlyVsDarkBishopOnly() {
    // K+B(dark) vs k+b(dark) - opponent has dark bishop only (no light bishop) -> insufficient
    // c1=dark, c8=dark
    final BitboardPosition pos = position("4k3/4b3/8/8/8/8/8/2B1K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testDarkBishopOnlyVsLightBishop() {
    // K+B(dark) vs k+b(light) - opponent HAS light bishop -> sufficient
    // c1=dark, f8=light
    final BitboardPosition pos = position("4k1b1/8/8/8/8/8/8/2B1K3 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testDarkBishopOnlyVsKnight() {
    // K+B(dark) vs k+n - opponent has knight -> sufficient
    final BitboardPosition pos = position("1n2k3/8/8/8/8/8/8/2B1K3 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testDarkBishopOnlyVsPawn() {
    // K+B(dark) vs k+p - opponent has pawn -> sufficient
    final BitboardPosition pos = position("4k3/p7/8/8/8/8/8/2B1K3 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testTwoDarkBishopsVsKingOnly() {
    // K+B(dark)+B(dark) vs k - multiple dark bishops -> insufficient
    // c1=dark, a3=dark
    final BitboardPosition pos = position("4k3/8/8/8/8/B7/8/2B1K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  // Black side: dark bishops
  @SuppressWarnings("static-method")
  @Test
  void testDarkBishopOnlyBlackVsKingOnly() {
    // k+b(dark) vs K - insufficient for black
    // c8 is a dark square
    final BitboardPosition pos = position("2b1k3/8/8/8/8/8/8/4K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testDarkBishopOnlyBlackVsLightBishop() {
    // k+b(dark) vs K+B(light) - opponent has light bishop -> sufficient for black
    // c8=dark, f1=light
    final BitboardPosition pos = position("2b1k3/8/8/8/8/8/8/4K1B1 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  // =====================================================================
  // Multiple bishops symmetry tests
  // =====================================================================

  // --- Multiple light-square bishops (black side) ---

  @SuppressWarnings("static-method")
  @Test
  void testTwoLightBishopsBlackVsKingOnly() {
    // k+b(light)+b(light) vs K - multiple light bishops for black -> insufficient
    // e4=light, g6=light
    final BitboardPosition pos = position("4k3/8/6b1/8/4b3/8/8/4K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  // --- Multiple dark-square bishops (black side) ---

  @SuppressWarnings("static-method")
  @Test
  void testTwoDarkBishopsBlackVsKingOnly() {
    // k+b(dark)+b(dark) vs K - multiple dark bishops for black -> insufficient
    // d7=dark, f5=dark
    final BitboardPosition pos = position("4k3/3b4/8/5b2/8/8/8/4K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  // --- Multiple light-square bishops vs multiple light-square bishops ---

  @SuppressWarnings("static-method")
  @Test
  void testMultipleLightBishopsVsMultipleLightBishops() {
    // K+B(light)+B(light) vs k+b(light)+b(light) - both sides have only light bishops -> both insufficient
    // White: e4=light, c2=light; Black: d7=light (wait, d7 is dark)
    // Light squares: a2, c2, e2, g2, b3, d3, f3, h3, a4, c4, e4, g4, ...
    // White: c2=light, e4=light; Black: f5=light (f5: file f=6, rank 5 -> 6+5=11=odd -> dark!)
    // Let me be careful: light square = file+rank is even (both odd or both even)
    // a1=dark, b1=light, c1=dark, d1=light, e1=dark, f1=light, g1=dark, h1=light
    // a2=light, b2=dark, c2=light, d2=dark, e2=light, f2=dark, g2=light, h2=dark
    // White: c2=light, e4=light; Black: b7=light, d5=light
    final BitboardPosition pos = position("4k3/1b6/8/3b4/4B3/8/2B5/4K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  // --- Multiple dark-square bishops vs multiple dark-square bishops ---

  @SuppressWarnings("static-method")
  @Test
  void testMultipleDarkBishopsVsMultipleDarkBishops() {
    // K+B(dark)+B(dark) vs k+b(dark)+b(dark) - both sides have only dark bishops -> both insufficient
    // Dark squares: a1, c1, e1, g1, b2, d2, f2, h2, a3, c3, e3, g3, ...
    // White: a1=dark, c3=dark; Black: b8=dark, d6=dark
    final BitboardPosition pos = position("1b2k3/8/3b4/8/8/2B5/8/B3K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  // --- Multiple light-square bishops vs single light-square bishop ---

  @SuppressWarnings("static-method")
  @Test
  void testMultipleLightBishopsVsSingleLightBishop() {
    // K+B(light)+B(light) vs k+b(light) - both sides light bishops only -> both insufficient
    // White: c2=light, e4=light; Black: b7=light
    final BitboardPosition pos = position("4k3/1b6/8/8/4B3/8/2B5/4K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  // --- Multiple dark-square bishops vs single dark-square bishop ---

  @SuppressWarnings("static-method")
  @Test
  void testMultipleDarkBishopsVsSingleDarkBishop() {
    // K+B(dark)+B(dark) vs k+b(dark) - both sides dark bishops only -> both insufficient
    // White: a1=dark, c3=dark; Black: b8=dark
    final BitboardPosition pos = position("1b2k3/8/8/8/8/2B5/8/B3K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  // --- Single light-square bishop vs multiple light-square bishops ---

  @SuppressWarnings("static-method")
  @Test
  void testSingleLightBishopVsMultipleLightBishops() {
    // K+B(light) vs k+b(light)+b(light) - both sides light bishops only -> both insufficient
    // White: e4=light; Black: b7=light, d5=light
    final BitboardPosition pos = position("4k3/1b6/8/3b4/4B3/8/8/4K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  // --- Single dark-square bishop vs multiple dark-square bishops ---

  @SuppressWarnings("static-method")
  @Test
  void testSingleDarkBishopVsMultipleDarkBishops() {
    // K+B(dark) vs k+b(dark)+b(dark) - both sides dark bishops only -> both insufficient
    // White: c3=dark; Black: b8=dark, d6=dark
    final BitboardPosition pos = position("1b2k3/8/3b4/8/8/2B5/8/4K3 w - - 0 1");
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
    assertTrue(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  // =====================================================================
  // Branch 5: Otherwise - sufficient (has rook, queen, pawn, etc.)
  // =====================================================================

  @SuppressWarnings("static-method")
  @Test
  void testSufficientRook() {
    final BitboardPosition pos = position("4k3/8/8/8/8/8/8/R3K3 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testSufficientQueen() {
    final BitboardPosition pos = position("4k3/8/8/8/8/8/8/3QK3 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testSufficientPawn() {
    final BitboardPosition pos = position("4k3/8/8/8/8/8/P7/4K3 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testSufficientKnightAndBishop() {
    // K+N+B - sufficient material to checkmate
    final BitboardPosition pos = position("4k3/8/8/8/8/8/8/1NB1K3 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testSufficientOppositeBishops() {
    // K+B(light)+B(dark) - has both colors, not "light only" or "dark only"
    // f1=light, c1=dark (wait, let me use correct squares)
    // c1=dark, f1=light... but both on first rank for white? c1 is dark, d1 is light.
    // Let me use: B on c1 (dark) and B on f1 (light) - but that's two bishops, one dark one light
    // This won't match branch 3 (not light-only) or branch 4 (not dark-only) -> falls through -> sufficient
    final BitboardPosition pos = position("4k3/8/8/8/8/8/8/2B1KB2 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testSufficientBlackRook() {
    final BitboardPosition pos = position("r3k3/8/8/8/8/8/8/4K3 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testSufficientBlackPawn() {
    final BitboardPosition pos = position("4k3/p7/8/8/8/8/8/4K3 w - - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

  @SuppressWarnings("static-method")
  @Test
  void testInitialPosition() {
    final BitboardPosition pos = position("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.WHITE, pos));
    assertFalse(InsufficientMaterialUtility.calculateIsInsufficientMaterial(Side.BLACK, pos));
  }

}
