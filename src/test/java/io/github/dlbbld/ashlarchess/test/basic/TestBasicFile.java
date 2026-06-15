// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.basic;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.BLACK;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.WHITE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.enums.File;
import io.github.dlbbld.ashlarchess.board.enums.Side;

class TestBasicFile {

  @SuppressWarnings("static-method")
  @Test
  void testCount() throws Exception {
    int totalFiles = 0;
    for (@SuppressWarnings("unused") final File file : File.REAL) {
      totalFiles++;
    }
    assertEquals(8, totalFiles);
  }

  @SuppressWarnings("static-method")
  @Test
  void testMethodsDirect() throws Exception {
    assertTrue(File.exists('a'));
    assertTrue(File.exists('b'));
    assertTrue(File.exists('c'));
    assertTrue(File.exists('d'));
    assertTrue(File.exists('e'));
    assertTrue(File.exists('f'));
    assertTrue(File.exists('g'));
    assertTrue(File.exists('h'));

    assertFalse(File.exists('i'));
    assertFalse(File.exists('j'));
    assertFalse(File.exists('k'));
    assertFalse(File.exists('1'));
    assertFalse(File.exists('2'));
    assertFalse(File.exists('3'));
    assertFalse(File.exists('-'));
    assertFalse(File.exists('0'));
    assertFalse(File.exists('9'));

    assertEquals(File.FILE_A, File.parse('a'));
    assertEquals(File.FILE_B, File.parse('b'));
    assertEquals(File.FILE_C, File.parse('c'));
    assertEquals(File.FILE_D, File.parse('d'));
    assertEquals(File.FILE_E, File.parse('e'));
    assertEquals(File.FILE_F, File.parse('f'));
    assertEquals(File.FILE_G, File.parse('g'));
    assertEquals(File.FILE_H, File.parse('h'));

    checkException('i');
    checkException('j');
    checkException('k');
    checkException('1');
    checkException('2');
    checkException('3');
    checkException('-');
    checkException('0');
    checkException('9');
  }

  @SuppressWarnings("static-method")
  @Test
  void testMethodsAdjacent() throws Exception {

    // white existence
    assertFalse(File.hasLeftFile(WHITE, File.FILE_A));
    assertTrue(File.hasLeftFile(WHITE, File.FILE_B));
    assertTrue(File.hasLeftFile(WHITE, File.FILE_C));
    assertTrue(File.hasLeftFile(WHITE, File.FILE_D));
    assertTrue(File.hasLeftFile(WHITE, File.FILE_E));
    assertTrue(File.hasLeftFile(WHITE, File.FILE_F));
    assertTrue(File.hasLeftFile(WHITE, File.FILE_G));
    assertTrue(File.hasLeftFile(WHITE, File.FILE_H));

    assertTrue(File.hasRightFile(WHITE, File.FILE_A));
    assertTrue(File.hasRightFile(WHITE, File.FILE_B));
    assertTrue(File.hasRightFile(WHITE, File.FILE_C));
    assertTrue(File.hasRightFile(WHITE, File.FILE_D));
    assertTrue(File.hasRightFile(WHITE, File.FILE_E));
    assertTrue(File.hasRightFile(WHITE, File.FILE_F));
    assertTrue(File.hasRightFile(WHITE, File.FILE_G));
    assertFalse(File.hasRightFile(WHITE, File.FILE_H));

    // black existence
    assertTrue(File.hasLeftFile(BLACK, File.FILE_A));
    assertTrue(File.hasLeftFile(BLACK, File.FILE_B));
    assertTrue(File.hasLeftFile(BLACK, File.FILE_C));
    assertTrue(File.hasLeftFile(BLACK, File.FILE_D));
    assertTrue(File.hasLeftFile(BLACK, File.FILE_E));
    assertTrue(File.hasLeftFile(BLACK, File.FILE_F));
    assertTrue(File.hasLeftFile(BLACK, File.FILE_G));
    assertFalse(File.hasLeftFile(BLACK, File.FILE_H));

    assertFalse(File.hasRightFile(BLACK, File.FILE_A));
    assertTrue(File.hasRightFile(BLACK, File.FILE_B));
    assertTrue(File.hasRightFile(BLACK, File.FILE_C));
    assertTrue(File.hasRightFile(BLACK, File.FILE_D));
    assertTrue(File.hasRightFile(BLACK, File.FILE_E));
    assertTrue(File.hasRightFile(BLACK, File.FILE_F));
    assertTrue(File.hasRightFile(BLACK, File.FILE_G));
    assertTrue(File.hasRightFile(BLACK, File.FILE_H));

    // white value
    checkExceptionLeft(WHITE, File.FILE_A);
    assertEquals(File.FILE_A, File.getLeftFile(WHITE, File.FILE_B));
    assertEquals(File.FILE_B, File.getLeftFile(WHITE, File.FILE_C));
    assertEquals(File.FILE_C, File.getLeftFile(WHITE, File.FILE_D));
    assertEquals(File.FILE_D, File.getLeftFile(WHITE, File.FILE_E));
    assertEquals(File.FILE_E, File.getLeftFile(WHITE, File.FILE_F));
    assertEquals(File.FILE_F, File.getLeftFile(WHITE, File.FILE_G));

    assertEquals(File.FILE_B, File.getRightFile(WHITE, File.FILE_A));
    assertEquals(File.FILE_C, File.getRightFile(WHITE, File.FILE_B));
    assertEquals(File.FILE_D, File.getRightFile(WHITE, File.FILE_C));
    assertEquals(File.FILE_E, File.getRightFile(WHITE, File.FILE_D));
    assertEquals(File.FILE_F, File.getRightFile(WHITE, File.FILE_E));
    assertEquals(File.FILE_G, File.getRightFile(WHITE, File.FILE_F));
    assertEquals(File.FILE_H, File.getRightFile(WHITE, File.FILE_G));
    checkExceptionRight(WHITE, File.FILE_H);

    // black value
    assertEquals(File.FILE_B, File.getLeftFile(BLACK, File.FILE_A));
    assertEquals(File.FILE_C, File.getLeftFile(BLACK, File.FILE_B));
    assertEquals(File.FILE_D, File.getLeftFile(BLACK, File.FILE_C));
    assertEquals(File.FILE_E, File.getLeftFile(BLACK, File.FILE_D));
    assertEquals(File.FILE_F, File.getLeftFile(BLACK, File.FILE_E));
    assertEquals(File.FILE_G, File.getLeftFile(BLACK, File.FILE_F));
    assertEquals(File.FILE_H, File.getLeftFile(BLACK, File.FILE_G));
    checkExceptionLeft(BLACK, File.FILE_H);

    checkExceptionRight(BLACK, File.FILE_A);
    assertEquals(File.FILE_A, File.getRightFile(BLACK, File.FILE_B));
    assertEquals(File.FILE_B, File.getRightFile(BLACK, File.FILE_C));
    assertEquals(File.FILE_C, File.getRightFile(BLACK, File.FILE_D));
    assertEquals(File.FILE_D, File.getRightFile(BLACK, File.FILE_E));
    assertEquals(File.FILE_E, File.getRightFile(BLACK, File.FILE_F));
    assertEquals(File.FILE_F, File.getRightFile(BLACK, File.FILE_G));
    assertEquals(File.FILE_G, File.getRightFile(BLACK, File.FILE_H));
  }

  private static void checkException(char fileLetter) {
    boolean isException;
    try {
      File.parse(fileLetter);
      isException = false;
    } catch (@SuppressWarnings("unused") final IllegalArgumentException e) {
      isException = true;
    }
    assertTrue(isException);
  }

  private static void checkExceptionLeft(Side side, File file) {
    boolean isException;
    try {
      File.getLeftFile(side, file);
      isException = false;
    } catch (@SuppressWarnings("unused") final IllegalArgumentException e) {
      isException = true;
    }
    assertTrue(isException);
  }

  private static void checkExceptionRight(Side side, File file) {
    boolean isException;
    try {
      File.getRightFile(side, file);
      isException = false;
    } catch (@SuppressWarnings("unused") final IllegalArgumentException e) {
      isException = true;
    }
    assertTrue(isException);
  }
}
