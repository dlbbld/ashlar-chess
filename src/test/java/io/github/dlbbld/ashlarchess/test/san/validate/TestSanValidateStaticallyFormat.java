package io.github.dlbbld.ashlarchess.test.san.validate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.test.san.validate.statically.format.calculate.SanValidateStaticallyFormat;

//at this stage we allow a lot of invalid SAN's which are then checked later
class TestSanValidateStaticallyFormat implements EnumConstants {

  @SuppressWarnings("static-method")
  @Test
  void testInvalidFormat() {
    checkNotExists("");
    checkNotExists("x");
    checkNotExists("x+");
    checkNotExists("R");
    checkNotExists("Ra");
    checkNotExists("Rxa");
    checkNotExists("axb");
    checkNotExists("Q1");
    checkNotExists("Q22");
    checkNotExists("Qx2");
  }

  @SuppressWarnings("static-method")
  @Test
  void testCastling() {
    checkExists("O-O");
    checkExists("O-O+");
    checkExists("O-O#");

    checkExists("O-O-O");
    checkExists("O-O-O+");
    checkExists("O-O-O#");

    checkNotExists("O-Ox");
    checkNotExists("O-O-Ox");
  }

  @SuppressWarnings("static-method")
  @Test
  void testStandard() {
    // rook
    checkExists("Rb2");
    checkExists("Rab2");
    checkExists("R5b2");
    checkExists("Rxb2");
    checkExists("Raxb2");
    checkExists("R5xb2");
    checkExists("Rb2+");
    checkExists("Rab2+");
    checkExists("R5b2+");
    checkExists("Rb2#");
    checkExists("Rab2#");
    checkExists("R5b2#");
    checkExists("Rxb2+");
    checkExists("Raxb2+");
    checkExists("R5xb2+");
    checkExists("Rxb2#");
    checkExists("Raxb2#");
    checkExists("R5xb2#");

    // knight
    checkExists("Nc3");
    checkExists("Nac3");
    checkExists("N4c3");
    checkExists("Na2c3");
    checkExists("Nxc3");
    checkExists("Naxc3");
    checkExists("N4xc3");
    checkExists("Na2xc3");
    checkExists("Nc3+");
    checkExists("Nac3+");
    checkExists("N4c3+");
    checkExists("Na2c3+");
    checkExists("Nc3#");
    checkExists("Nac3#");
    checkExists("N4c3#");
    checkExists("Na2c3#");
    checkExists("Nxc3+");
    checkExists("Naxc3+");
    checkExists("N4xc3+");
    checkExists("Na2xc3+");
    checkExists("Nxc3#");
    checkExists("Naxc3#");
    checkExists("N4xc3#");
    checkExists("Na2xc3#");

    // bishop
    checkExists("Bd4");
    checkExists("Bcd4");
    checkExists("B5d4");
    checkExists("Be5d4");
    checkExists("Bxd4");
    checkExists("Bcxd4");
    checkExists("B5xd4");
    checkExists("Be5xd4");
    checkExists("Bd4+");
    checkExists("Bcd4+");
    checkExists("B5d4+");
    checkExists("Be5d4+");
    checkExists("Bd4#");
    checkExists("Bcd4#");
    checkExists("B5d4#");
    checkExists("Be5d4#");
    checkExists("Bxd4+");
    checkExists("Bcxd4+");
    checkExists("B5xd4+");
    checkExists("Be5xd4+");
    checkExists("Bxd4#");
    checkExists("Bcxd4#");
    checkExists("B5xd4#");
    checkExists("Be5xd4#");

    // queen
    checkExists("Qf7");
    checkExists("Qaf7");
    checkExists("Q8f7");
    checkExists("Qg8f7");
    checkExists("Qxf7");
    checkExists("Qaxf7");
    checkExists("Q8xf7");
    checkExists("Qg8xf7");
    checkExists("Qf7+");
    checkExists("Qaf7+");
    checkExists("Q8f7+");
    checkExists("Qg8f7+");
    checkExists("Qf7#");
    checkExists("Qaf7#");
    checkExists("Q8f7#");
    checkExists("Qg8f7#");
    checkExists("Qxf7+");
    checkExists("Qaxf7+");
    checkExists("Q8xf7+");
    checkExists("Qg8xf7+");
    checkExists("Qxf7#");
    checkExists("Qaxf7#");
    checkExists("Q8xf7#");
    checkExists("Qg8xf7#");

    // king
    checkExists("Kd7");
    checkExists("Kxd7");
    checkExists("Kd7+");
    checkExists("Kd7#");
    checkExists("Kxd7+");
    checkExists("Kxd7#");

    // white pawn
    checkExists("e4");
    checkExists("dxe4");
    checkExists("fxe4");
    checkExists("e8=Q");
    checkExists("e8=N");
    checkExists("dxe8=Q");
    checkExists("fxe8=N");

    checkExists("e4+");
    checkExists("dxe4+");
    checkExists("fxe4+");
    checkExists("e8=Q+");
    checkExists("e8=N+");
    checkExists("dxe8=Q+");
    checkExists("fxe8=N+");

    checkExists("e4#");
    checkExists("dxe4#");
    checkExists("fxe4#");
    checkExists("e8=Q#");
    checkExists("e8=N#");
    checkExists("dxe8=Q#");
    checkExists("fxe8=N#");

    // black pawn
    checkExists("f5");
    checkExists("exf5");
    checkExists("gxf5");
    checkExists("f1=Q");
    checkExists("f1=B");
    checkExists("exf1=Q");
    checkExists("gxf1=B");

    checkExists("f5+");
    checkExists("exf5+");
    checkExists("gxf5+");
    checkExists("f1=Q+");
    checkExists("f1=B+");
    checkExists("exf1=Q+");
    checkExists("gxf1=B+");

    checkExists("f5#");
    checkExists("exf5#");
    checkExists("gxf5#");
    checkExists("f1=Q#");
    checkExists("f1=B#");
    checkExists("exf1=Q#");
    checkExists("gxf1=B#");

  }

  @SuppressWarnings("static-method")
  @Test
  void testMovingOntoItself() {
    // rook
    checkExists("Ra1a1");

    // knight
    checkExists("Na1a1");

    // bishop
    checkExists("Ba1a1");

    // queen
    checkExists("Qa1a1");

    // king
    // for the king we don't allow this
    // so to have to treat less cases - could also be allowed
    checkNotExists("Ka1a1");
  }

  @SuppressWarnings("static-method")
  @Test
  void testRankFileSpecification() {
    // rook
    checkExists("Raa1");
    checkExists("R1a1");
    checkExists("Ra2a1");

    // knight
    checkExists("Nab3");
    checkExists("N1b3");
    checkExists("Na1b3");

    // bishop
    checkExists("Bba1");
    checkExists("B2a1");
    checkExists("Bb2a1");

    // queen
    checkExists("Qaa1");
    checkExists("Q1a1");
    checkExists("Qb2a1");

    // king
    // for the king we don't allow this
    // so to have to treat less cases - could also be allowed
    checkNotExists("K1a1");
    checkNotExists("Kaa1");
    checkNotExists("Ka2a1");
  }

  @SuppressWarnings("static-method")
  @Test
  void testInvalidMovement() {
    // rook
    checkExists("Rb2a1");

    // knight
    checkExists("Nda1");
    checkExists("N4a1");
    checkExists("Nd4a1");

    // bishop
    checkExists("Baa1");
    checkExists("B1a1");
    checkExists("Ba2a1");

    // queen
    checkExists("Qb3a1");

    // king
    // for the king we don't allow this
    // so to have to treat less cases - could also be allowed
    checkNotExists("Kca1");
    checkNotExists("K2a1");
    checkNotExists("Ka3a1");
  }

  private static void checkExists(String san) {
    assertTrue(SanValidateStaticallyFormat.exists(san));
  }

  private static void checkNotExists(String san) {
    assertFalse(SanValidateStaticallyFormat.exists(san));
  }

}
