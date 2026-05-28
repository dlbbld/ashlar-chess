package io.github.dlbbld.ashlarchess.test.san.validate.statically.format.calculate;

import java.util.Map;
import java.util.TreeMap;

import com.google.common.collect.ImmutableMap;

import io.github.dlbbld.ashlarchess.board.enums.Rank;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.san.SanParse;
import io.github.dlbbld.ashlarchess.test.san.model.SanValidationFromTo;

public class PawnSanValidateStaticallyFormatCalculate extends AbstractSanValidateStaticallyFormatCalculate {

  static ImmutableMap<String, SanParse> calculateSanMap() {
    final Map<String, SanParse> sanValidateMap = new TreeMap<>();

    // promotion only on rank 1 and 8, non-promotion only on ranks 2-7
    for (final SanValidationFromTo model : calculateWithoutDisambiguation()) {
      if (isPromotionRank(model)) {
        populatePawnPromotionMap(sanValidateMap, model, false);
      } else {
        populatePawnNonPromotionMap(sanValidateMap, model, false);
      }
    }

    for (final SanValidationFromTo model : calculateWithFile()) {
      if (isPromotionRank(model)) {
        populatePawnPromotionMap(sanValidateMap, model, true);
      } else {
        populatePawnNonPromotionMap(sanValidateMap, model, true);
      }
    }

    return Nulls.copyOfMap(sanValidateMap);
  }

  private static boolean isPromotionRank(SanValidationFromTo model) {
    final Rank rank = model.toSquare().getRank();
    return rank == Rank.RANK_1 || rank == Rank.RANK_8;
  }

}
