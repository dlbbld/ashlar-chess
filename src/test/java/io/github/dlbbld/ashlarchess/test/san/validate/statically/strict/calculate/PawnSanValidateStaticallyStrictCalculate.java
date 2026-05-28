// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.calculate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.collect.ImmutableMap;

import io.github.dlbbld.ashlarchess.board.enums.File;
import io.github.dlbbld.ashlarchess.board.enums.Rank;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.san.SanParse;
import io.github.dlbbld.ashlarchess.test.san.model.SanValidationFromTo;
import io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.PawnBlackSanValidateStaticallyStrict;
import io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.PawnWhiteSanValidateStaticallyStrict;

public class PawnSanValidateStaticallyStrictCalculate extends AbstractSanValidateStaticallyStrictCalculate {

  static ImmutableMap<String, SanParse> calculateSanMap(Side side) {
    final Map<String, SanParse> sanValidateMap = new TreeMap<>();

    final List<String> enumNameList = calculateEnumNameList(side);
    for (final String enumName : enumNameList) {
      final String parse = enumName.toLowerCase();
      File fromFile;
      final Rank fromRank = Rank.NONE;
      final Square toSquare = switch (parse.length()) {
        case 3 -> {
          fromFile = File.NONE;
          yield Square.calculate(Nulls.substring(parse, 1));
        }
        case 4 -> {
          final char fileLetter = parse.charAt(1);
          fromFile = File.calculateFile(fileLetter);
          yield Square.calculate(Nulls.substring(parse, 2));
        }
        default -> throw new ProgrammingMistakeException(
            "The length of the " + PAWN.getName() + " enum for " + side.getName() + " does not meet the expectation");
      };
      final SanValidationFromTo model = new SanValidationFromTo(fromFile, fromRank, toSquare);
      final boolean isCapture = fromFile != File.NONE;
      if (Rank.calculateIsPromotionRank(side, toSquare.getRank())) {
        populatePawnPromotionMap(sanValidateMap, model, isCapture);
      } else {
        populatePawnNonPromotionMap(sanValidateMap, model, isCapture);
      }
    }

    return Nulls.copyOfMap(sanValidateMap);
  }

  private static List<String> calculateEnumNameList(Side side) {
    return switch (side) {
      case WHITE -> new ArrayList<>(PawnWhiteSanValidateStaticallyStrict.VALUES);
      case BLACK -> new ArrayList<>(PawnBlackSanValidateStaticallyStrict.VALUES);
      case NONE -> throw new IllegalArgumentException();
    };
  }

}
