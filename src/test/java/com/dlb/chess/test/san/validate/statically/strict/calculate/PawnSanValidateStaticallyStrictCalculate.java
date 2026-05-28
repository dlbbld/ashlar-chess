package com.dlb.chess.test.san.validate.statically.strict.calculate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.dlb.chess.board.enums.File;
import com.dlb.chess.board.enums.Rank;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.san.SanParse;
import com.dlb.chess.test.san.model.SanValidationFromTo;
import com.dlb.chess.test.san.validate.statically.strict.enums.PawnBlackSanValidateStaticallyStrict;
import com.dlb.chess.test.san.validate.statically.strict.enums.PawnWhiteSanValidateStaticallyStrict;
import com.google.common.collect.ImmutableMap;

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
