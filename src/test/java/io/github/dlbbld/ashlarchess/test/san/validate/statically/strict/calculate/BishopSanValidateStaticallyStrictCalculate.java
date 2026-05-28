package io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.calculate;

import java.util.Map;
import java.util.TreeMap;

import com.google.common.collect.ImmutableMap;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.san.SanParse;
import io.github.dlbbld.ashlarchess.test.san.model.SanValidationFromTo;
import io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.BishopSanValidateStaticallyStrict;

public class BishopSanValidateStaticallyStrictCalculate extends AbstractSanValidateStaticallyStrictCalculate {

  static ImmutableMap<String, SanParse> calculateSanMap() {

    final Map<String, SanParse> sanValidateMap = new TreeMap<>();

    for (final String enumName : BishopSanValidateStaticallyStrict.VALUES) {
      final SanValidationFromTo model = calculateFromFileAndOrRankTo(enumName, BISHOP);
      populateMap(sanValidateMap, model, BISHOP);
    }

    return Nulls.copyOfMap(sanValidateMap);
  }

}
