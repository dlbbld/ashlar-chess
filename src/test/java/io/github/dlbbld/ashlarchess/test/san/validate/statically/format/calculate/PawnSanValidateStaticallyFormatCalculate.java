// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.san.validate.statically.format.calculate;

import java.util.Map;
import java.util.TreeMap;

import com.google.common.collect.ImmutableMap;

import io.github.dlbbld.ashlarchess.board.enums.Rank;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.san.SanParse;
import io.github.dlbbld.ashlarchess.test.san.model.SanValidationFromTo;
import io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.calculate.SanValidateStaticallyStrictCalculateSupport;

public class PawnSanValidateStaticallyFormatCalculate {

  static ImmutableMap<String, SanParse> calculateSanMap() {
    final Map<String, SanParse> sanValidateMap = new TreeMap<>();

    // promotion only on rank 1 and 8, non-promotion only on ranks 2-7
    for (final SanValidationFromTo model : SanValidateStaticallyFormatCalculateSupport.calculateWithoutDisambiguation()) {
      if (isPromotionRank(model)) {
        SanValidateStaticallyStrictCalculateSupport.populatePawnPromotionMap(sanValidateMap, model, false);
      } else {
        SanValidateStaticallyStrictCalculateSupport.populatePawnNonPromotionMap(sanValidateMap, model, false);
      }
    }

    for (final SanValidationFromTo model : SanValidateStaticallyFormatCalculateSupport.calculateWithFile()) {
      if (isPromotionRank(model)) {
        SanValidateStaticallyStrictCalculateSupport.populatePawnPromotionMap(sanValidateMap, model, true);
      } else {
        SanValidateStaticallyStrictCalculateSupport.populatePawnNonPromotionMap(sanValidateMap, model, true);
      }
    }

    return Nulls.copyOfMap(sanValidateMap);
  }

  private static boolean isPromotionRank(SanValidationFromTo model) {
    final Rank rank = model.toSquare().getRank();
    return rank == Rank.RANK_1 || rank == Rank.RANK_8;
  }

}
