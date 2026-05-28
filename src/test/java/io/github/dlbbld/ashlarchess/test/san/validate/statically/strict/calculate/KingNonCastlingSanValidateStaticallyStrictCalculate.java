package io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.calculate;

import java.util.Map;
import java.util.TreeMap;

import com.google.common.collect.ImmutableMap;

import io.github.dlbbld.ashlarchess.board.enums.File;
import io.github.dlbbld.ashlarchess.board.enums.Rank;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.san.SanParse;
import io.github.dlbbld.ashlarchess.test.san.model.SanValidationFromTo;
import io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.KingNonCastlingSanValidateStaticallyStrict;

public class KingNonCastlingSanValidateStaticallyStrictCalculate extends AbstractSanValidateStaticallyStrictCalculate {

  static ImmutableMap<String, SanParse> calculateSanMap() {

    final Map<String, SanParse> sanValidateMap = new TreeMap<>();

    for (final String enumName : KingNonCastlingSanValidateStaticallyStrict.VALUES) {
      final String parse = Nulls.toLowerCase(enumName);
      final File fromFile = File.NONE;
      final Rank fromRank = Rank.NONE;
      final Square toSquare = Square.calculate(Nulls.substring(parse, 1));

      final SanValidationFromTo model = new SanValidationFromTo(fromFile, fromRank, toSquare);
      populateMap(sanValidateMap, model, KING);
    }

    return Nulls.copyOfMap(sanValidateMap);
  }

}
