package io.github.dlbbld.ashlarchess.test.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.enums.File;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;

class TestBasicFile implements EnumConstants {

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

    assertEquals(File.FILE_A, File.calculateFile('a'));
    assertEquals(File.FILE_B, File.calculateFile('b'));
    assertEquals(File.FILE_C, File.calculateFile('c'));
    assertEquals(File.FILE_D, File.calculateFile('d'));
    assertEquals(File.FILE_E, File.calculateFile('e'));
    assertEquals(File.FILE_F, File.calculateFile('f'));
    assertEquals(File.FILE_G, File.calculateFile('g'));
    assertEquals(File.FILE_H, File.calculateFile('h'));

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
    assertFalse(File.calculateHasLeftFile(WHITE, File.FILE_A));
    assertTrue(File.calculateHasLeftFile(WHITE, File.FILE_B));
    assertTrue(File.calculateHasLeftFile(WHITE, File.FILE_C));
    assertTrue(File.calculateHasLeftFile(WHITE, File.FILE_D));
    assertTrue(File.calculateHasLeftFile(WHITE, File.FILE_E));
    assertTrue(File.calculateHasLeftFile(WHITE, File.FILE_F));
    assertTrue(File.calculateHasLeftFile(WHITE, File.FILE_G));
    assertTrue(File.calculateHasLeftFile(WHITE, File.FILE_H));

    assertTrue(File.calculateHasRightFile(WHITE, File.FILE_A));
    assertTrue(File.calculateHasRightFile(WHITE, File.FILE_B));
    assertTrue(File.calculateHasRightFile(WHITE, File.FILE_C));
    assertTrue(File.calculateHasRightFile(WHITE, File.FILE_D));
    assertTrue(File.calculateHasRightFile(WHITE, File.FILE_E));
    assertTrue(File.calculateHasRightFile(WHITE, File.FILE_F));
    assertTrue(File.calculateHasRightFile(WHITE, File.FILE_G));
    assertFalse(File.calculateHasRightFile(WHITE, File.FILE_H));

    // black existence
    assertTrue(File.calculateHasLeftFile(BLACK, File.FILE_A));
    assertTrue(File.calculateHasLeftFile(BLACK, File.FILE_B));
    assertTrue(File.calculateHasLeftFile(BLACK, File.FILE_C));
    assertTrue(File.calculateHasLeftFile(BLACK, File.FILE_D));
    assertTrue(File.calculateHasLeftFile(BLACK, File.FILE_E));
    assertTrue(File.calculateHasLeftFile(BLACK, File.FILE_F));
    assertTrue(File.calculateHasLeftFile(BLACK, File.FILE_G));
    assertFalse(File.calculateHasLeftFile(BLACK, File.FILE_H));

    assertFalse(File.calculateHasRightFile(BLACK, File.FILE_A));
    assertTrue(File.calculateHasRightFile(BLACK, File.FILE_B));
    assertTrue(File.calculateHasRightFile(BLACK, File.FILE_C));
    assertTrue(File.calculateHasRightFile(BLACK, File.FILE_D));
    assertTrue(File.calculateHasRightFile(BLACK, File.FILE_E));
    assertTrue(File.calculateHasRightFile(BLACK, File.FILE_F));
    assertTrue(File.calculateHasRightFile(BLACK, File.FILE_G));
    assertTrue(File.calculateHasRightFile(BLACK, File.FILE_H));

    // white value
    checkExceptionLeft(WHITE, File.FILE_A);
    assertEquals(File.FILE_A, File.calculateLeftFile(WHITE, File.FILE_B));
    assertEquals(File.FILE_B, File.calculateLeftFile(WHITE, File.FILE_C));
    assertEquals(File.FILE_C, File.calculateLeftFile(WHITE, File.FILE_D));
    assertEquals(File.FILE_D, File.calculateLeftFile(WHITE, File.FILE_E));
    assertEquals(File.FILE_E, File.calculateLeftFile(WHITE, File.FILE_F));
    assertEquals(File.FILE_F, File.calculateLeftFile(WHITE, File.FILE_G));

    assertEquals(File.FILE_B, File.calculateRightFile(WHITE, File.FILE_A));
    assertEquals(File.FILE_C, File.calculateRightFile(WHITE, File.FILE_B));
    assertEquals(File.FILE_D, File.calculateRightFile(WHITE, File.FILE_C));
    assertEquals(File.FILE_E, File.calculateRightFile(WHITE, File.FILE_D));
    assertEquals(File.FILE_F, File.calculateRightFile(WHITE, File.FILE_E));
    assertEquals(File.FILE_G, File.calculateRightFile(WHITE, File.FILE_F));
    assertEquals(File.FILE_H, File.calculateRightFile(WHITE, File.FILE_G));
    checkExceptionRight(WHITE, File.FILE_H);

    // black value
    assertEquals(File.FILE_B, File.calculateLeftFile(BLACK, File.FILE_A));
    assertEquals(File.FILE_C, File.calculateLeftFile(BLACK, File.FILE_B));
    assertEquals(File.FILE_D, File.calculateLeftFile(BLACK, File.FILE_C));
    assertEquals(File.FILE_E, File.calculateLeftFile(BLACK, File.FILE_D));
    assertEquals(File.FILE_F, File.calculateLeftFile(BLACK, File.FILE_E));
    assertEquals(File.FILE_G, File.calculateLeftFile(BLACK, File.FILE_F));
    assertEquals(File.FILE_H, File.calculateLeftFile(BLACK, File.FILE_G));
    checkExceptionLeft(BLACK, File.FILE_H);

    checkExceptionRight(BLACK, File.FILE_A);
    assertEquals(File.FILE_A, File.calculateRightFile(BLACK, File.FILE_B));
    assertEquals(File.FILE_B, File.calculateRightFile(BLACK, File.FILE_C));
    assertEquals(File.FILE_C, File.calculateRightFile(BLACK, File.FILE_D));
    assertEquals(File.FILE_D, File.calculateRightFile(BLACK, File.FILE_E));
    assertEquals(File.FILE_E, File.calculateRightFile(BLACK, File.FILE_F));
    assertEquals(File.FILE_F, File.calculateRightFile(BLACK, File.FILE_G));
    assertEquals(File.FILE_G, File.calculateRightFile(BLACK, File.FILE_H));
  }

  @SuppressWarnings("static-method")
  @Test
  void testMethodsAdjacentAdjacent() throws Exception {
    // white existence
    assertFalse(File.calculateHasLeftLeftFile(WHITE, File.FILE_A));
    assertFalse(File.calculateHasLeftLeftFile(WHITE, File.FILE_B));
    assertTrue(File.calculateHasLeftLeftFile(WHITE, File.FILE_C));
    assertTrue(File.calculateHasLeftLeftFile(WHITE, File.FILE_D));
    assertTrue(File.calculateHasLeftLeftFile(WHITE, File.FILE_E));
    assertTrue(File.calculateHasLeftLeftFile(WHITE, File.FILE_F));
    assertTrue(File.calculateHasLeftLeftFile(WHITE, File.FILE_G));
    assertTrue(File.calculateHasLeftLeftFile(WHITE, File.FILE_H));

    assertTrue(File.calculateHasRightRightFile(WHITE, File.FILE_A));
    assertTrue(File.calculateHasRightRightFile(WHITE, File.FILE_B));
    assertTrue(File.calculateHasRightRightFile(WHITE, File.FILE_C));
    assertTrue(File.calculateHasRightRightFile(WHITE, File.FILE_D));
    assertTrue(File.calculateHasRightRightFile(WHITE, File.FILE_E));
    assertTrue(File.calculateHasRightRightFile(WHITE, File.FILE_F));
    assertFalse(File.calculateHasRightRightFile(WHITE, File.FILE_G));
    assertFalse(File.calculateHasRightRightFile(WHITE, File.FILE_H));

    // black existence
    assertTrue(File.calculateHasLeftLeftFile(BLACK, File.FILE_A));
    assertTrue(File.calculateHasLeftLeftFile(BLACK, File.FILE_B));
    assertTrue(File.calculateHasLeftLeftFile(BLACK, File.FILE_C));
    assertTrue(File.calculateHasLeftLeftFile(BLACK, File.FILE_D));
    assertTrue(File.calculateHasLeftLeftFile(BLACK, File.FILE_E));
    assertTrue(File.calculateHasLeftLeftFile(BLACK, File.FILE_F));
    assertFalse(File.calculateHasLeftLeftFile(BLACK, File.FILE_G));
    assertFalse(File.calculateHasLeftLeftFile(BLACK, File.FILE_H));

    assertFalse(File.calculateHasRightRightFile(BLACK, File.FILE_A));
    assertFalse(File.calculateHasRightRightFile(BLACK, File.FILE_B));
    assertTrue(File.calculateHasRightRightFile(BLACK, File.FILE_C));
    assertTrue(File.calculateHasRightRightFile(BLACK, File.FILE_D));
    assertTrue(File.calculateHasRightRightFile(BLACK, File.FILE_E));
    assertTrue(File.calculateHasRightRightFile(BLACK, File.FILE_F));
    assertTrue(File.calculateHasRightRightFile(BLACK, File.FILE_G));
    assertTrue(File.calculateHasRightRightFile(BLACK, File.FILE_H));

    // white values
    checkExceptionLeftLeft(WHITE, File.FILE_A);
    checkExceptionLeftLeft(WHITE, File.FILE_B);
    assertEquals(File.FILE_A, calculateLeftLeftFile(WHITE, File.FILE_C));
    assertEquals(File.FILE_B, calculateLeftLeftFile(WHITE, File.FILE_D));
    assertEquals(File.FILE_C, calculateLeftLeftFile(WHITE, File.FILE_E));
    assertEquals(File.FILE_D, calculateLeftLeftFile(WHITE, File.FILE_F));
    assertEquals(File.FILE_E, calculateLeftLeftFile(WHITE, File.FILE_G));
    assertEquals(File.FILE_F, calculateLeftLeftFile(WHITE, File.FILE_H));

    assertEquals(File.FILE_C, calculateRightRightFile(WHITE, File.FILE_A));
    assertEquals(File.FILE_D, calculateRightRightFile(WHITE, File.FILE_B));
    assertEquals(File.FILE_E, calculateRightRightFile(WHITE, File.FILE_C));
    assertEquals(File.FILE_F, calculateRightRightFile(WHITE, File.FILE_D));
    assertEquals(File.FILE_G, calculateRightRightFile(WHITE, File.FILE_E));
    assertEquals(File.FILE_H, calculateRightRightFile(WHITE, File.FILE_F));
    checkExceptionRightRight(WHITE, File.FILE_G);
    checkExceptionRightRight(WHITE, File.FILE_H);

    // black values
    assertEquals(File.FILE_C, calculateLeftLeftFile(BLACK, File.FILE_A));
    assertEquals(File.FILE_D, calculateLeftLeftFile(BLACK, File.FILE_B));
    assertEquals(File.FILE_E, calculateLeftLeftFile(BLACK, File.FILE_C));
    assertEquals(File.FILE_F, calculateLeftLeftFile(BLACK, File.FILE_D));
    assertEquals(File.FILE_G, calculateLeftLeftFile(BLACK, File.FILE_E));
    assertEquals(File.FILE_H, calculateLeftLeftFile(BLACK, File.FILE_F));
    checkExceptionLeftLeft(BLACK, File.FILE_G);
    checkExceptionLeftLeft(BLACK, File.FILE_H);

    checkExceptionRightRight(BLACK, File.FILE_A);
    checkExceptionRightRight(BLACK, File.FILE_B);
    assertEquals(File.FILE_A, calculateRightRightFile(BLACK, File.FILE_C));
    assertEquals(File.FILE_B, calculateRightRightFile(BLACK, File.FILE_D));
    assertEquals(File.FILE_C, calculateRightRightFile(BLACK, File.FILE_E));
    assertEquals(File.FILE_D, calculateRightRightFile(BLACK, File.FILE_F));
    assertEquals(File.FILE_E, calculateRightRightFile(BLACK, File.FILE_G));
    assertEquals(File.FILE_F, calculateRightRightFile(BLACK, File.FILE_H));

  }

  private static void checkException(char fileLetter) {
    boolean isException;
    try {
      File.calculateFile(fileLetter);
      isException = false;
    } catch (@SuppressWarnings("unused") final IllegalArgumentException e) {
      isException = true;
    }
    assertTrue(isException);
  }

  private static void checkExceptionLeft(Side side, File file) {
    boolean isException;
    try {
      File.calculateLeftFile(side, file);
      isException = false;
    } catch (@SuppressWarnings("unused") final IllegalArgumentException e) {
      isException = true;
    }
    assertTrue(isException);
  }

  private static void checkExceptionRight(Side side, File file) {
    boolean isException;
    try {
      File.calculateRightFile(side, file);
      isException = false;
    } catch (@SuppressWarnings("unused") final IllegalArgumentException e) {
      isException = true;
    }
    assertTrue(isException);
  }

  private static void checkExceptionLeftLeft(Side side, File file) {
    boolean isException;
    try {
      calculateLeftLeftFile(side, file);
      isException = false;
    } catch (@SuppressWarnings("unused") final IllegalArgumentException e) {
      isException = true;
    }
    assertTrue(isException);
  }

  private static void checkExceptionRightRight(Side side, File file) {
    boolean isException;
    try {
      calculateRightRightFile(side, file);
      isException = false;
    } catch (@SuppressWarnings("unused") final IllegalArgumentException e) {
      isException = true;
    }
    assertTrue(isException);
  }

  private static File calculateLeftLeftFile(Side side, File file) {
    if (!File.calculateHasLeftLeftFile(side, file)) {
      throw new IllegalArgumentException();
    }
    return File.calculateLeftFile(side, File.calculateLeftFile(side, file));
  }

  private static File calculateRightRightFile(Side side, File file) {
    if (!File.calculateHasRightRightFile(side, file)) {
      throw new IllegalArgumentException();
    }
    return File.calculateRightFile(side, File.calculateRightFile(side, file));
  }
}
