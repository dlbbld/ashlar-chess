package io.github.dlbbld.ashlarchess.test.san.validate.statically.format.calculate;

import com.google.common.collect.ImmutableMap;

import io.github.dlbbld.ashlarchess.san.SanParse;
import io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.calculate.KingCastlingSanValidateStaticallyStrictCalculate;

public class KingCastlingSanValidateStaticallyFormatCalculate extends AbstractSanValidateStaticallyFormatCalculate {

  static ImmutableMap<String, SanParse> calculateSanMap() {

    return KingCastlingSanValidateStaticallyStrictCalculate.calculateSanMap();
  }

}
