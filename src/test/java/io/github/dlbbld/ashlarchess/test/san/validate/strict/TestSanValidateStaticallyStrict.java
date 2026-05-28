package io.github.dlbbld.ashlarchess.test.san.validate.strict;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.calculate.SanValidateStaticallyStrict;

class TestSanValidateStaticallyStrict implements EnumConstants {

  @SuppressWarnings("static-method")
  @Test
  void testInvalidFormat() {
    checkNotExistsBoth("");
    checkNotExistsBoth("x");
    checkNotExistsBoth("x+");
    checkNotExistsBoth("R");
    checkNotExistsBoth("Ra");
    checkNotExistsBoth("Rxa");
    checkNotExistsBoth("axb");
    checkNotExistsBoth("Q1");
    checkNotExistsBoth("Q22");
    checkNotExistsBoth("Qx2");
  }

  @SuppressWarnings("static-method")
  @Test
  void testCastling() {
    checkExistsBoth("O-O");
    checkExistsBoth("O-O+");
    checkExistsBoth("O-O#");

    checkExistsBoth("O-O-O");
    checkExistsBoth("O-O-O+");
    checkExistsBoth("O-O-O#");

    checkNotExistsBoth("O-Ox");
    checkNotExistsBoth("O-O-Ox");
  }

  @SuppressWarnings("static-method")
  @Test
  void testStandard() {
    // rook
    checkExistsBoth("Rb2");
    checkExistsBoth("Rab2");
    checkExistsBoth("R5b2");
    checkExistsBoth("Rxb2");
    checkExistsBoth("Raxb2");
    checkExistsBoth("R5xb2");
    checkExistsBoth("Rb2+");
    checkExistsBoth("Rab2+");
    checkExistsBoth("R5b2+");
    checkExistsBoth("Rb2#");
    checkExistsBoth("Rab2#");
    checkExistsBoth("R5b2#");
    checkExistsBoth("Rxb2+");
    checkExistsBoth("Raxb2+");
    checkExistsBoth("R5xb2+");
    checkExistsBoth("Rxb2#");
    checkExistsBoth("Raxb2#");
    checkExistsBoth("R5xb2#");

    // knight
    checkExistsBoth("Nc3");
    checkExistsBoth("Nac3");
    checkExistsBoth("N4c3");
    checkExistsBoth("Na2c3");
    checkExistsBoth("Nxc3");
    checkExistsBoth("Naxc3");
    checkExistsBoth("N4xc3");
    checkExistsBoth("Na2xc3");
    checkExistsBoth("Nc3+");
    checkExistsBoth("Nac3+");
    checkExistsBoth("N4c3+");
    checkExistsBoth("Na2c3+");
    checkExistsBoth("Nc3#");
    checkExistsBoth("Nac3#");
    checkExistsBoth("N4c3#");
    checkExistsBoth("Na2c3#");
    checkExistsBoth("Nxc3+");
    checkExistsBoth("Naxc3+");
    checkExistsBoth("N4xc3+");
    checkExistsBoth("Na2xc3+");
    checkExistsBoth("Nxc3#");
    checkExistsBoth("Naxc3#");
    checkExistsBoth("N4xc3#");
    checkExistsBoth("Na2xc3#");

    // bishop
    checkExistsBoth("Bd4");
    checkExistsBoth("Bcd4");
    checkExistsBoth("B5d4");
    checkExistsBoth("Be5d4");
    checkExistsBoth("Bxd4");
    checkExistsBoth("Bcxd4");
    checkExistsBoth("B5xd4");
    checkExistsBoth("Be5xd4");
    checkExistsBoth("Bd4+");
    checkExistsBoth("Bcd4+");
    checkExistsBoth("B5d4+");
    checkExistsBoth("Be5d4+");
    checkExistsBoth("Bd4#");
    checkExistsBoth("Bcd4#");
    checkExistsBoth("B5d4#");
    checkExistsBoth("Be5d4#");
    checkExistsBoth("Bxd4+");
    checkExistsBoth("Bcxd4+");
    checkExistsBoth("B5xd4+");
    checkExistsBoth("Be5xd4+");
    checkExistsBoth("Bxd4#");
    checkExistsBoth("Bcxd4#");
    checkExistsBoth("B5xd4#");
    checkExistsBoth("Be5xd4#");

    // queen
    checkExistsBoth("Qf7");
    checkExistsBoth("Qaf7");
    checkExistsBoth("Q8f7");
    checkExistsBoth("Qg8f7");
    checkExistsBoth("Qxf7");
    checkExistsBoth("Qaxf7");
    checkExistsBoth("Q8xf7");
    checkExistsBoth("Qg8xf7");
    checkExistsBoth("Qf7+");
    checkExistsBoth("Qaf7+");
    checkExistsBoth("Q8f7+");
    checkExistsBoth("Qg8f7+");
    checkExistsBoth("Qf7#");
    checkExistsBoth("Qaf7#");
    checkExistsBoth("Q8f7#");
    checkExistsBoth("Qg8f7#");
    checkExistsBoth("Qxf7+");
    checkExistsBoth("Qaxf7+");
    checkExistsBoth("Q8xf7+");
    checkExistsBoth("Qg8xf7+");
    checkExistsBoth("Qxf7#");
    checkExistsBoth("Qaxf7#");
    checkExistsBoth("Q8xf7#");
    checkExistsBoth("Qg8xf7#");

    // king
    checkExistsBoth("Kd7");
    checkExistsBoth("Kxd7");
    checkExistsBoth("Kd7+");
    checkExistsBoth("Kd7#");
    checkExistsBoth("Kxd7+");
    checkExistsBoth("Kxd7#");

    // white pawn
    checkExistsWhite("e4");
    checkExistsWhite("dxe4");
    checkExistsWhite("fxe4");
    checkExistsWhite("e8=Q");
    checkExistsWhite("e8=N");
    checkExistsWhite("dxe8=Q");
    checkExistsWhite("fxe8=N");

    checkExistsWhite("e4+");
    checkExistsWhite("dxe4+");
    checkExistsWhite("fxe4+");
    checkExistsWhite("e8=Q+");
    checkExistsWhite("e8=N+");
    checkExistsWhite("dxe8=Q+");
    checkExistsWhite("fxe8=N+");

    checkExistsWhite("e4#");
    checkExistsWhite("dxe4#");
    checkExistsWhite("fxe4#");
    checkExistsWhite("e8=Q#");
    checkExistsWhite("e8=N#");
    checkExistsWhite("dxe8=Q#");
    checkExistsWhite("fxe8=N#");

    checkNotExistsWhite("f1=Q");
    checkNotExistsWhite("f1=B");
    checkNotExistsWhite("exf1=Q");
    checkNotExistsWhite("gxf1=B");

    checkNotExistsWhite("f1=Q+");
    checkNotExistsWhite("f1=B+");
    checkNotExistsWhite("exf1=Q+");
    checkNotExistsWhite("gxf1=B+");

    checkNotExistsWhite("f1=Q#");
    checkNotExistsWhite("f1=B#");
    checkNotExistsWhite("exf1=Q#");
    checkNotExistsWhite("gxf1=B#");

    // black pawn
    checkExistsBlack("f5");
    checkExistsBlack("exf5");
    checkExistsBlack("gxf5");
    checkExistsBlack("f1=Q");
    checkExistsBlack("f1=B");
    checkExistsBlack("exf1=Q");
    checkExistsBlack("gxf1=B");

    checkExistsBlack("f5+");
    checkExistsBlack("exf5+");
    checkExistsBlack("gxf5+");
    checkExistsBlack("f1=Q+");
    checkExistsBlack("f1=B+");
    checkExistsBlack("exf1=Q+");
    checkExistsBlack("gxf1=B+");

    checkExistsBlack("f5#");
    checkExistsBlack("exf5#");
    checkExistsBlack("gxf5#");
    checkExistsBlack("f1=Q#");
    checkExistsBlack("f1=B#");
    checkExistsBlack("exf1=Q#");
    checkExistsBlack("gxf1=B#");

    checkNotExistsBlack("e8=Q");
    checkNotExistsBlack("e8=N");
    checkNotExistsBlack("dxe8=Q");
    checkNotExistsBlack("fxe8=N");

    checkNotExistsBlack("e8=Q+");
    checkNotExistsBlack("e8=N+");
    checkNotExistsBlack("dxe8=Q+");
    checkNotExistsBlack("fxe8=N+");

    checkNotExistsBlack("e8=Q#");
    checkNotExistsBlack("e8=N#");
    checkNotExistsBlack("dxe8=Q#");
    checkNotExistsBlack("fxe8=N#");

  }

  @SuppressWarnings("static-method")
  @Test
  void testInvalidMovement() {
    // rook
    checkNotExistsBoth("R2a1");
    checkNotExistsBoth("Ra2a1");
    checkNotExistsBoth("Rc2b2");

    // knight
    checkNotExistsBoth("Nb3a1");
    checkNotExistsBoth("Naa1");
    checkNotExistsBoth("N4a1");

    // bishop
    checkNotExistsBoth("Baa1");
    checkNotExistsBoth("B2a1");
    checkNotExistsBoth("Bb3a1");

    // queen
    checkNotExistsBoth("Qb4a1");

    // king
    // all valid

    // white pawn
    checkNotExistsWhite("a1=Q");

    // black pawn
    checkNotExistsBlack("a8=Q");
  }

  private static void checkExistsBoth(String san) {
    assertTrue(SanValidateStaticallyStrict.exists(san, WHITE));
    assertTrue(SanValidateStaticallyStrict.exists(san, BLACK));
  }

  private static void checkExistsWhite(String san) {
    assertTrue(SanValidateStaticallyStrict.exists(san, WHITE));
  }

  private static void checkExistsBlack(String san) {
    assertTrue(SanValidateStaticallyStrict.exists(san, BLACK));
  }

  private static void checkNotExistsBoth(String san) {
    assertFalse(SanValidateStaticallyStrict.exists(san, WHITE));
    assertFalse(SanValidateStaticallyStrict.exists(san, BLACK));
  }

  private static void checkNotExistsWhite(String san) {
    assertFalse(SanValidateStaticallyStrict.exists(san, WHITE));
  }

  private static void checkNotExistsBlack(String san) {
    assertFalse(SanValidateStaticallyStrict.exists(san, BLACK));
  }

}
