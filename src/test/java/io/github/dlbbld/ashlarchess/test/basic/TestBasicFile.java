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
    assertFalse(File.FILE_A.hasLeftFile(WHITE));
    assertTrue(File.FILE_B.hasLeftFile(WHITE));
    assertTrue(File.FILE_C.hasLeftFile(WHITE));
    assertTrue(File.FILE_D.hasLeftFile(WHITE));
    assertTrue(File.FILE_E.hasLeftFile(WHITE));
    assertTrue(File.FILE_F.hasLeftFile(WHITE));
    assertTrue(File.FILE_G.hasLeftFile(WHITE));
    assertTrue(File.FILE_H.hasLeftFile(WHITE));

    assertTrue(File.FILE_A.hasRightFile(WHITE));
    assertTrue(File.FILE_B.hasRightFile(WHITE));
    assertTrue(File.FILE_C.hasRightFile(WHITE));
    assertTrue(File.FILE_D.hasRightFile(WHITE));
    assertTrue(File.FILE_E.hasRightFile(WHITE));
    assertTrue(File.FILE_F.hasRightFile(WHITE));
    assertTrue(File.FILE_G.hasRightFile(WHITE));
    assertFalse(File.FILE_H.hasRightFile(WHITE));

    // black existence
    assertTrue(File.FILE_A.hasLeftFile(BLACK));
    assertTrue(File.FILE_B.hasLeftFile(BLACK));
    assertTrue(File.FILE_C.hasLeftFile(BLACK));
    assertTrue(File.FILE_D.hasLeftFile(BLACK));
    assertTrue(File.FILE_E.hasLeftFile(BLACK));
    assertTrue(File.FILE_F.hasLeftFile(BLACK));
    assertTrue(File.FILE_G.hasLeftFile(BLACK));
    assertFalse(File.FILE_H.hasLeftFile(BLACK));

    assertFalse(File.FILE_A.hasRightFile(BLACK));
    assertTrue(File.FILE_B.hasRightFile(BLACK));
    assertTrue(File.FILE_C.hasRightFile(BLACK));
    assertTrue(File.FILE_D.hasRightFile(BLACK));
    assertTrue(File.FILE_E.hasRightFile(BLACK));
    assertTrue(File.FILE_F.hasRightFile(BLACK));
    assertTrue(File.FILE_G.hasRightFile(BLACK));
    assertTrue(File.FILE_H.hasRightFile(BLACK));

    // white value
    checkExceptionLeft(WHITE, File.FILE_A);
    assertEquals(File.FILE_A, File.FILE_B.getLeftFile(WHITE));
    assertEquals(File.FILE_B, File.FILE_C.getLeftFile(WHITE));
    assertEquals(File.FILE_C, File.FILE_D.getLeftFile(WHITE));
    assertEquals(File.FILE_D, File.FILE_E.getLeftFile(WHITE));
    assertEquals(File.FILE_E, File.FILE_F.getLeftFile(WHITE));
    assertEquals(File.FILE_F, File.FILE_G.getLeftFile(WHITE));

    assertEquals(File.FILE_B, File.FILE_A.getRightFile(WHITE));
    assertEquals(File.FILE_C, File.FILE_B.getRightFile(WHITE));
    assertEquals(File.FILE_D, File.FILE_C.getRightFile(WHITE));
    assertEquals(File.FILE_E, File.FILE_D.getRightFile(WHITE));
    assertEquals(File.FILE_F, File.FILE_E.getRightFile(WHITE));
    assertEquals(File.FILE_G, File.FILE_F.getRightFile(WHITE));
    assertEquals(File.FILE_H, File.FILE_G.getRightFile(WHITE));
    checkExceptionRight(WHITE, File.FILE_H);

    // black value
    assertEquals(File.FILE_B, File.FILE_A.getLeftFile(BLACK));
    assertEquals(File.FILE_C, File.FILE_B.getLeftFile(BLACK));
    assertEquals(File.FILE_D, File.FILE_C.getLeftFile(BLACK));
    assertEquals(File.FILE_E, File.FILE_D.getLeftFile(BLACK));
    assertEquals(File.FILE_F, File.FILE_E.getLeftFile(BLACK));
    assertEquals(File.FILE_G, File.FILE_F.getLeftFile(BLACK));
    assertEquals(File.FILE_H, File.FILE_G.getLeftFile(BLACK));
    checkExceptionLeft(BLACK, File.FILE_H);

    checkExceptionRight(BLACK, File.FILE_A);
    assertEquals(File.FILE_A, File.FILE_B.getRightFile(BLACK));
    assertEquals(File.FILE_B, File.FILE_C.getRightFile(BLACK));
    assertEquals(File.FILE_C, File.FILE_D.getRightFile(BLACK));
    assertEquals(File.FILE_D, File.FILE_E.getRightFile(BLACK));
    assertEquals(File.FILE_E, File.FILE_F.getRightFile(BLACK));
    assertEquals(File.FILE_F, File.FILE_G.getRightFile(BLACK));
    assertEquals(File.FILE_G, File.FILE_H.getRightFile(BLACK));
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
      file.getLeftFile(side);
      isException = false;
    } catch (@SuppressWarnings("unused") final IllegalArgumentException e) {
      isException = true;
    }
    assertTrue(isException);
  }

  private static void checkExceptionRight(Side side, File file) {
    boolean isException;
    try {
      file.getRightFile(side);
      isException = false;
    } catch (@SuppressWarnings("unused") final IllegalArgumentException e) {
      isException = true;
    }
    assertTrue(isException);
  }
}
