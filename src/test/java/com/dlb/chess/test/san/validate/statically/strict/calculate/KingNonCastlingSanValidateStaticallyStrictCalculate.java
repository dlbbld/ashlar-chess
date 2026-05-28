package com.dlb.chess.test.san.validate.statically.strict.calculate;

import java.util.Map;
import java.util.TreeMap;

import com.dlb.chess.board.enums.File;
import com.dlb.chess.board.enums.Rank;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.san.SanParse;
import com.dlb.chess.test.san.model.SanValidationFromTo;
import com.dlb.chess.test.san.validate.statically.strict.enums.KingNonCastlingSanValidateStaticallyStrict;
import com.google.common.collect.ImmutableMap;

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
