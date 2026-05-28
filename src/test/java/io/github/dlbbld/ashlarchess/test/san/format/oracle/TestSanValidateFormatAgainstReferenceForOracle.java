// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.san.format.oracle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.san.SanParse;
import io.github.dlbbld.ashlarchess.san.SanValidateFormat;
import io.github.dlbbld.ashlarchess.test.san.reference.SanValidateFormatReference;
import io.github.dlbbld.ashlarchess.test.san.validate.statically.format.calculate.SanValidateStaticallyFormat;

class TestSanValidateFormatAgainstReferenceForOracle {

  @SuppressWarnings("static-method")
  @Test
  void testAgainstReferenceForOracle() {
    for (final String san : Nulls.keySet(SanValidateStaticallyFormat.getSanValidationMap())) {
      final SanParse actual = SanValidateFormat.validateFormat(san);
      final SanParse expected = SanValidateFormatReference.validateFormat(san);
      assertEquals(expected, actual, "validateFormat result differs from reference for SAN: \"" + san + "\"");
    }
  }
}
