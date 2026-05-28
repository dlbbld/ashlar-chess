package com.dlb.chess.test.san.validate.statically.strict.calculate;

import java.util.Map;

import com.dlb.chess.board.enums.File;
import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.PromotionPieceType;
import com.dlb.chess.board.enums.Rank;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.san.SanConversion;
import com.dlb.chess.san.SanFormat;
import com.dlb.chess.san.SanParse;
import com.dlb.chess.san.SanTerminalMarker;
import com.dlb.chess.test.san.SanCalculate;
import com.dlb.chess.test.san.model.SanValidationFromTo;

public abstract class AbstractSanValidateStaticallyStrictCalculate implements EnumConstants {

  public static SanValidationFromTo calculateFromFileAndOrRankTo(String enumName, PieceType movingPieceType) {

    final String parse = enumName.toLowerCase();
    File fromFile;
    Rank fromRank;
    Square toSquare;
    switch (parse.length()) {
      case 3:
        fromFile = File.NONE;
        fromRank = Rank.NONE;
        toSquare = Square.calculate(Nulls.substring(parse, 1));
        break;
      case 4:
        final char checkLetter = parse.charAt(1);

        if (File.exists(checkLetter)) {
          fromFile = File.calculateFile(checkLetter);
          fromRank = Rank.NONE;
        } else {
          if (!Rank.exists(checkLetter)) {
            throw new ProgrammingMistakeException(
                "The fourth letter in " + parse + " must be a valid file letter or rank number");
          }
          fromFile = File.NONE;
          fromRank = Rank.calculateRank(checkLetter);
        }
        toSquare = Square.calculate(Nulls.substring(parse, 2));
        break;
      case 5:
        final Square fromSquare = Square.calculate(Nulls.substring(parse, 1, 3));
        fromFile = fromSquare.getFile();
        fromRank = fromSquare.getRank();
        toSquare = Square.calculate(Nulls.substring(parse, 3));
        break;
      default:
        throw new ProgrammingMistakeException(
            "The length of the " + movingPieceType.getName() + " enum does not meet the expectation");
    }

    return new SanValidationFromTo(fromFile, fromRank, toSquare);
  }

  static SanValidationFromTo calculateFromFileXorRankTo(String enumName, PieceType movingPieceType) {
    if (enumName.length() == 5) {
      throw new ProgrammingMistakeException(
          "The file/rank of the " + movingPieceType.getName() + " enum does not meet the expectation");
    }
    return calculateFromFileAndOrRankTo(enumName, movingPieceType);
  }

  public static void populateMap(Map<String, SanParse> sanValidateMap, SanValidationFromTo model,
      PieceType movingPieceType) {
    populateMap(sanValidateMap, model, PromotionPieceType.NONE, movingPieceType);
  }

  static void populateMap(Map<String, SanParse> sanValidateMap, SanValidationFromTo model,
      PromotionPieceType promotionPieceType, PieceType movingPieceType) {
    populateMap(sanValidateMap, model, false, promotionPieceType, movingPieceType);
    populateMap(sanValidateMap, model, true, promotionPieceType, movingPieceType);
  }

  private static void populateMap(Map<String, SanParse> sanValidateMap, SanValidationFromTo model, boolean isCapture,
      PromotionPieceType promotionPieceType, PieceType movingPieceType) {
    populateMap(sanValidateMap, model, promotionPieceType, isCapture, SanTerminalMarker.NONE, movingPieceType);
    populateMap(sanValidateMap, model, promotionPieceType, isCapture, SanTerminalMarker.CHECKMATE, movingPieceType);
    populateMap(sanValidateMap, model, promotionPieceType, isCapture, SanTerminalMarker.CHECK, movingPieceType);
  }

  static void populateMap(Map<String, SanParse> sanValidateMap, SanValidationFromTo model,
      PromotionPieceType promotionPieceType, boolean isCapture, SanTerminalMarker sanTerminalMarker,
      PieceType movingPieceType) {
    final File fromFile = model.fromFile();
    final Rank fromRank = model.fromRank();
    final Square toSquare = model.toSquare();

    final String san = SanCalculate.calculateSan(fromFile, fromRank, toSquare, promotionPieceType, isCapture,
        sanTerminalMarker, movingPieceType);
    final SanFormat sanFormat = SanCalculate.calculateSanFormat(isCapture, fromFile, fromRank, movingPieceType,
        promotionPieceType);
    final SanParse sanParse = new SanParse(sanFormat,
        new SanConversion(movingPieceType, fromFile, fromRank, toSquare, promotionPieceType, sanTerminalMarker));
    sanValidateMap.put(san, sanParse);
  }

  public static void populatePawnNonPromotionMap(Map<String, SanParse> sanValidateMap, SanValidationFromTo model,
      boolean isCapture) {
    populateMap(sanValidateMap, model, PromotionPieceType.NONE, isCapture, SanTerminalMarker.NONE, PAWN);
    populateMap(sanValidateMap, model, PromotionPieceType.NONE, isCapture, SanTerminalMarker.CHECKMATE, PAWN);
    populateMap(sanValidateMap, model, PromotionPieceType.NONE, isCapture, SanTerminalMarker.CHECK, PAWN);
  }

  public static void populatePawnPromotionMap(Map<String, SanParse> sanValidateMap, SanValidationFromTo model,
      boolean isCapture) {
    for (final PromotionPieceType promotionPieceType : PromotionPieceType.REAL) {
      populateMap(sanValidateMap, model, promotionPieceType, isCapture, SanTerminalMarker.NONE, PAWN);
      populateMap(sanValidateMap, model, promotionPieceType, isCapture, SanTerminalMarker.CHECKMATE, PAWN);
      populateMap(sanValidateMap, model, promotionPieceType, isCapture, SanTerminalMarker.CHECK, PAWN);
    }
  }
}
