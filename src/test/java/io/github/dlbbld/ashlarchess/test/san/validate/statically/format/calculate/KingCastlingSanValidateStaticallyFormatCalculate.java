// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.san.validate.statically.format.calculate;

import java.util.Map;

import io.github.dlbbld.ashlarchess.san.SanParse;
import io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.calculate.KingCastlingSanValidateStaticallyStrictCalculate;

public class KingCastlingSanValidateStaticallyFormatCalculate {

  static Map<String, SanParse> calculateSanMap() {

    return KingCastlingSanValidateStaticallyStrictCalculate.calculateSanMap();
  }

}
