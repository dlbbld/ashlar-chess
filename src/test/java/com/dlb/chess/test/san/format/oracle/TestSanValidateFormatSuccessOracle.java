package com.dlb.chess.test.san.format.oracle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.san.SanParse;
import com.dlb.chess.san.SanValidateFormat;
import com.dlb.chess.test.san.validate.statically.format.calculate.SanValidateStaticallyFormat;

class TestSanValidateFormatSuccessOracle {

  /**
   * For every entry in the static SAN map, asserts that {@link SanValidateFormat#validateFormat} returns the identical
   * {@link SanParse} that the map pre-computed.
   *
   * <p>
   * This uses the static map as an oracle: if the new parser produces a different result for any known-valid SAN string
   * (wrong {@link com.dlb.chess.san.SanFormat}, wrong from-square, wrong promotion piece, etc.) the test will catch it
   * immediately without having to enumerate strings manually.
   */
  @SuppressWarnings("static-method")
  @Test
  void testSuccessOracle() {
    final long tBuildStart = System.currentTimeMillis();
    final Map<String, SanParse> map = SanValidateStaticallyFormat.getSanValidationMap();
    final long tBuildEnd = System.currentTimeMillis();
    System.out.println("Oracle size: " + map.size() + ", build time: " + (tBuildEnd - tBuildStart) + " ms");

    final long tLoopStart = System.currentTimeMillis();
    for (final Map.Entry<String, SanParse> entry : Nulls.entrySet(map)) {
      final String san = Nulls.getKey(entry);
      final SanParse expected = Nulls.getValue(entry);
      final SanParse actual = SanValidateFormat.validateFormat(san);
      assertEquals(expected, actual, "validateFormat result differs from static map for SAN: \"" + san + "\"");
    }
    final long tLoopEnd = System.currentTimeMillis();
    System.out.println("Loop time: " + (tLoopEnd - tLoopStart) + " ms");
  }

}
