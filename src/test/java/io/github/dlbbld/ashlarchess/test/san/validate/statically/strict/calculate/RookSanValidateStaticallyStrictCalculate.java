// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.calculate;

import java.util.Map;
import java.util.TreeMap;

import com.google.common.collect.ImmutableMap;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.san.SanParse;
import io.github.dlbbld.ashlarchess.test.san.model.SanValidationFromTo;
import io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.RookSanValidateStaticallyStrict;

public class RookSanValidateStaticallyStrictCalculate extends AbstractSanValidateStaticallyStrictCalculate {

  static ImmutableMap<String, SanParse> calculateSanMap() {

    final Map<String, SanParse> sanValidateMap = new TreeMap<>();

    for (final String enumName : RookSanValidateStaticallyStrict.VALUES) {
      final SanValidationFromTo model = calculateFromFileXorRankTo(enumName, ROOK);
      populateMap(sanValidateMap, model, ROOK);
    }

    return Nulls.copyOfMap(sanValidateMap);
  }

}
