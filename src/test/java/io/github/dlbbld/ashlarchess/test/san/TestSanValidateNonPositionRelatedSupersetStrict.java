package io.github.dlbbld.ashlarchess.test.san;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.san.SanParse;
import io.github.dlbbld.ashlarchess.san.SanValidateFormat;
import io.github.dlbbld.ashlarchess.san.SanValidationException;
import io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.calculate.SanValidateStaticallyStrict;

class TestSanValidateNonPositionRelatedSupersetStrict extends AbstractTestSanValidate {

  @SuppressWarnings("static-method")
  @Test
  void testStaticallySubsetRuntime() {
    for (final Entry<String, SanParse> entry : Nulls.entrySet(SanValidateStaticallyStrict.getSanValidationWhiteMap())) {
      checkStaticallySubsetRuntime(Nulls.getKey(entry), Nulls.getValue(entry));
    }
    for (final Entry<String, SanParse> entry : Nulls.entrySet(SanValidateStaticallyStrict.getSanValidationBlackMap())) {
      checkStaticallySubsetRuntime(Nulls.getKey(entry), Nulls.getValue(entry));
    }
  }

  private static void checkStaticallySubsetRuntime(String san, SanParse staticResult) {

    boolean isRuntimeException;
    try {
      final SanParse sanParse = SanValidateFormat.validateFormat(san);
      assertEquals(sanParse, staticResult);
      isRuntimeException = false;
    } catch (@SuppressWarnings("unused") final SanValidationException e) {
      isRuntimeException = true;
    }
    assertFalse(isRuntimeException);

  }

}