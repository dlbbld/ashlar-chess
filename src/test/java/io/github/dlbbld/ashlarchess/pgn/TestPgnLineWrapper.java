// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.pgn;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class TestPgnLineWrapper {

  @SuppressWarnings("static-method")
  @Test
  void testWrappedLines() {
    checkWrappedLines("a", 1, "a");
    checkWrappedLines("a", 2, "a");
    checkWrappedLines("a", 10, "a");

    checkWrappedLines("aaa", 1, "aaa");
    checkWrappedLines("aaa", 2, "aaa");
    checkWrappedLines("aaaaaaaaaaaaaaaaaaaaaaa", 10, "aaaaaaaaaaaaaaaaaaaaaaa");

    checkWrappedLines("1", 3, "1");
    checkWrappedLines("12", 3, "12");
    checkWrappedLines("123", 3, "123");
    checkWrappedLines("123 4", 3, "123", "4");
    checkWrappedLines("123 45", 3, "123", "45");
    checkWrappedLines("123 456", 3, "123", "456");
    checkWrappedLines("123 456 7", 3, "123", "456", "7");

    checkWrappedLines("aa", 5, "aa");
    checkWrappedLines("aa aa", 5, "aa aa");
    checkWrappedLines("aa aa aa", 5, "aa aa", "aa");
    checkWrappedLines("aa aa aa a", 5, "aa aa", "aa a");
    checkWrappedLines("aa aa aa aa", 5, "aa aa", "aa aa");
    checkWrappedLines("aa aa aa aa aa", 5, "aa aa", "aa aa", "aa");

  }

  // -------------------------------------------------------------------------------------------------
  // Brace-aware wrap: spaces inside `{...}` are content, never wrap candidates. A brace region wider than
  // lineLength goes on its own line rather than being broken.
  // -------------------------------------------------------------------------------------------------

  @SuppressWarnings("static-method")
  @Test
  void testBraceRegionTreatedAsAtomic() {
    // The brace region "{a b c}" is 7 chars and exceeds lineLength=5; emitted alone, not split on its inner spaces.
    checkWrappedLines("{a b c}", 5, "{a b c}");
  }

  @SuppressWarnings("static-method")
  @Test
  void testBraceRegionWithFollowingMoves() {
    // Brace alone on first line (longer than 10), then "1. e4" packs onto the next line.
    checkWrappedLines("{long comment} 1. e4", 10, "{long comment}", "1. e4");
  }

  @SuppressWarnings("static-method")
  @Test
  void testShortBraceFitsOnSameLine() {
    checkWrappedLines("{c} 1. e4 e5", 20, "{c} 1. e4 e5");
  }

  @SuppressWarnings("static-method")
  @Test
  void testInnerSpacesNeverBecomeNewlines() {
    // The 30-char brace region has internal spaces, none of which may be turned into a wrap point.
    checkWrappedLines("{a very spaced out comment here} 1. e4", 15, "{a very spaced out comment here}", "1. e4");
  }

  @SuppressWarnings("static-method")
  @Test
  void testInnerOpenBraceIsContent() {
    // T-003: an inner `{` is content; only `}` ends the region. The whole "{a {b c}" is one atom.
    checkWrappedLines("{a {b c} 1. e4", 5, "{a {b c}", "1. e4");
  }

  @SuppressWarnings("static-method")
  @Test
  void testMultipleBraceRegions() {
    checkWrappedLines("1. e4 {one} 1... e5 {two}", 30, "1. e4 {one} 1... e5 {two}");
    checkWrappedLines("1. e4 {one} 1... e5 {two}", 12, "1. e4 {one}", "1... e5", "{two}");
  }

  @SuppressWarnings("static-method")
  @Test
  void testWordsBeforeAndAfterBrace() {
    // Words before the brace pack normally; the brace lands on its own line if too long; words after re-pack.
    checkWrappedLines("a b {long comment} c d", 5, "a b", "{long comment}", "c d");
  }

  private static void checkWrappedLines(String line, int lineLength, String... expectedLineArray) {
    @SuppressWarnings("null") final List<String> expectedResult = Arrays.asList(expectedLineArray);
    final List<String> actualResult = PgnLineWrapper.calculateWrappedLines(line, lineLength);
    assertEquals(expectedResult, actualResult);
  }

}