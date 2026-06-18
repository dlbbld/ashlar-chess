// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.calculate;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.KNIGHT;

import java.util.Map;
import java.util.TreeMap;

import com.google.common.collect.ImmutableMap;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.san.SanParse;
import io.github.dlbbld.ashlarchess.test.san.model.SanValidationFromTo;
import io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.KnightSanValidateStaticallyStrict;

public class KnightSanValidateStaticallyStrictCalculate {

  static ImmutableMap<String, SanParse> calculateSanMap() {

    final Map<String, SanParse> sanValidateMap = new TreeMap<>();

    for (final String enumName : KnightSanValidateStaticallyStrict.VALUES) {
      final SanValidationFromTo model = SanValidateStaticallyStrictCalculateSupport
          .calculateFromFileAndOrRankTo(enumName, KNIGHT);
      SanValidateStaticallyStrictCalculateSupport.populateMap(sanValidateMap, model, KNIGHT);
    }

    return Nulls.copyOfMap(sanValidateMap);
  }

}
