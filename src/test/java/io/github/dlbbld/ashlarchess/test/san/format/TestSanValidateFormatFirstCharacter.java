// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.san.format;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.san.SanValidateFormat;
import io.github.dlbbld.ashlarchess.san.SanValidationException;
import io.github.dlbbld.ashlarchess.san.SanValidationProblem;

class TestSanValidateFormatFirstCharacter {

  // --- Valid first characters ---

  @SuppressWarnings("static-method")
  @Test
  void testValidFileLetters() {
    // file letters a-h start pawn moves
    checkValid("a3");
    checkValid("b3");
    checkValid("c3");
    checkValid("d3");
    checkValid("e3");
    checkValid("f3");
    checkValid("g3");
    checkValid("h3");
  }

  @SuppressWarnings("static-method")
  @Test
  void testValidPieceLetters() {
    // R, N, B, Q, K are valid piece letters
    checkValid("Nf3");
    checkValid("Bc4");
    checkValid("Qd3");
    checkValid("Re1");
    checkValid("Ke2");
  }

  @SuppressWarnings("static-method")
  @Test
  void testValidCastling() {
    checkValid("O-O");
    checkValid("O-O-O");
  }

  // --- Invalid first characters ---

  @SuppressWarnings("static-method")
  @Test
  void testInvalidDigits() {
    // digits are not valid first characters
    checkException("1e4");
    checkException("2d4");
    checkException("8e4");
  }

  @SuppressWarnings("static-method")
  @Test
  void testInvalidLowercasePieceLetters() {
    // lowercase piece letters are not valid starts - only uppercase R, N, B, Q, K
    checkException("nf3", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("qd1", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("re1", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("ke2", SanValidationProblem.FORMAT_FIRST_CHARACTER);
  }

  @SuppressWarnings("static-method")
  @Test
  void testInvalidSpecialCharacters() {
    // special characters are not valid first characters
    checkException("xd4");
    checkException("=Q");
    checkException("+e4");
    checkException("#e4");
  }

  @SuppressWarnings("static-method")
  @Test
  void testInvalidOtherLetters() {
    // letters that are not file letters (a-h) or piece letters (R, N, B, Q, K)
    // these contain invalid SAN characters, caught by validateFormatBasic
    checkException("ie4", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("Pe4", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("Ze4", SanValidationProblem.FORMAT_FIRST_CHARACTER);
  }

  private static void checkException(String san) {
    checkException(san, SanValidationProblem.FORMAT_FIRST_CHARACTER);
  }

  private static void checkException(String san, SanValidationProblem expectedProblem) {
    boolean isException;
    try {
      SanValidateFormat.validateFormat(san);
      isException = false;
    } catch (final SanValidationException e) {
      isException = true;
      System.out.println("SAN: " + san + " -> " + e.getMessage());
      assertEquals(expectedProblem, e.getSanValidationProblem());
    }
    assertTrue(isException);
  }

  private static void checkValid(String san) {
    boolean isException = false;
    try {
      SanValidateFormat.validateFormat(san);
    } catch (@SuppressWarnings("unused") final SanValidationException e) {
      isException = true;
    }
    assertFalse(isException);
  }

}
