// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.san;

import io.github.dlbbld.ashlarchess.board.enums.File;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceType;
import io.github.dlbbld.ashlarchess.board.enums.Rank;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.enums.NotationPromotionPiece;
import io.github.dlbbld.ashlarchess.messages.Message;

/**
 * Parses pawn SAN moves - both forward (e.g. {@code d3}, {@code d8=Q}) and capturing (e.g. {@code dxe5},
 * {@code dxe8=Q}).
 */
abstract class SanValidateFormatPawn extends AbstractSan {

  static SanParse parsePawnMove(final String core, final SanTerminalMarker sanTerminalMarker) {
    // too short
    if (core.length() == 1) {
      throw new SanValidationException(SanValidationProblem.FORMAT_PAWN_NO_SECOND_CHARACTER,
          Message.getString("validation.san.format.pawn.noSecondCharacter"));
    }

    final char secondChar = core.charAt(1);

    if (SanValidateFormat.isRankDigit(secondChar)) {
      return parsePawnForwardMove(core, sanTerminalMarker);
    }
    if (isCaptureSymbol(secondChar)) {
      return parsePawnCaptureMove(core, sanTerminalMarker);
    }

    throw new SanValidationException(SanValidationProblem.FORMAT_PAWN_WRONG_SECOND_CHARACTER,
        Message.getString("validation.san.format.pawn.wrongSecondCharacter", Nulls.toString(secondChar)));
  }

  private static SanParse parsePawnForwardMove(final String core, final SanTerminalMarker sanTerminalMarker) {
    final int length = core.length();
    final char firstChar = core.charAt(0);
    final char secondChar = core.charAt(1);

    if (!isAnyPromotionRank(secondChar)) {
      // non promotion e.g. d3

      // too long
      if (length > 2) {
        throw new SanValidationException(SanValidationProblem.FORMAT_PAWN_FORWARD_NON_PROMOTION_OVERLENGTH,
            Message.getString("validation.san.format.pawn.forward.nonPromotion.overlength"));
      }

      // valid
      return new SanParse(SanFormat.PAWN_NON_CAPTURING_NON_PROMOTION,
          new SanConversion(PieceType.PAWN, File.NONE, Rank.NONE,
              Square.calculate(SanValidateFormat.parseFile(firstChar), SanValidateFormat.parseRank(secondChar)),
              PromotionPieceType.NONE, sanTerminalMarker));
    }

    // promotion e.g. d8=Q

    // no promotion symbol
    if (length == 2) {
      throw new SanValidationException(SanValidationProblem.FORMAT_PAWN_FORWARD_PROMOTION_NO_PROMOTION_SYMBOL,
          Message.getString("validation.san.format.pawn.forward.promotion.noPromotionSymbol"));
    }

    final char thirdChar = core.charAt(2);

    // wrong promotion symbol
    if (!isPromotionSymbol(thirdChar)) {
      throw new SanValidationException(SanValidationProblem.FORMAT_PAWN_FORWARD_PROMOTION_WRONG_PROMOTION_SYMBOL,
          Message.getString("validation.san.format.pawn.forward.promotion.wrongPromotionSymbol",
              Nulls.toString(thirdChar)));
    }

    // no promotion piece
    if (length == 3) {
      throw new SanValidationException(SanValidationProblem.FORMAT_PAWN_FORWARD_PROMOTION_NO_PROMOTION_PIECE,
          Message.getString("validation.san.format.pawn.forward.promotion.noPromotionPiece"));
    }

    final char fourthChar = core.charAt(3);

    // wrong promotion piece
    if (!NotationPromotionPiece.exists(fourthChar)) {
      throw new SanValidationException(SanValidationProblem.FORMAT_PAWN_FORWARD_PROMOTION_WRONG_PROMOTION_PIECE, Message
          .getString("validation.san.format.pawn.forward.promotion.wrongPromotionPiece", Nulls.toString(fourthChar)));
    }

    // too long
    if (length > 4) {
      throw new SanValidationException(SanValidationProblem.FORMAT_PAWN_FORWARD_PROMOTION_OVERLENGTH,
          Message.getString("validation.san.format.pawn.forward.promotion.overlength"));
    }

    // valid
    return new SanParse(SanFormat.PAWN_NON_CAPTURING_PROMOTION,
        new SanConversion(PieceType.PAWN, File.NONE, Rank.NONE,
            Square.calculate(SanValidateFormat.parseFile(firstChar), SanValidateFormat.parseRank(secondChar)),
            NotationPromotionPiece.calculate(fourthChar).getPromotionPieceType(), sanTerminalMarker));
  }

  private static SanParse parsePawnCaptureMove(final String core, final SanTerminalMarker sanTerminalMarker) {
    final int length = core.length();
    final char firstChar = core.charAt(0);

    // too short
    if (length == 2) {
      throw new SanValidationException(SanValidationProblem.FORMAT_PAWN_CAPTURE_NO_FILE,
          Message.getString("validation.san.format.pawn.capture.noFile"));
    }

    final char thirdChar = core.charAt(2);

    // file check
    if (!SanValidateFormat.isFileLetter(thirdChar)) {
      throw new SanValidationException(SanValidationProblem.FORMAT_PAWN_CAPTURE_WRONG_FILE,
          Message.getString("validation.san.format.pawn.capture.wrongFile", Nulls.toString(thirdChar)));
    }

    // too short
    if (length == 3) {
      throw new SanValidationException(SanValidationProblem.FORMAT_PAWN_CAPTURE_NO_RANK,
          Message.getString("validation.san.format.pawn.capture.noRank"));
    }

    final char fourthChar = core.charAt(3);

    // rank check
    if (!SanValidateFormat.isRankDigit(fourthChar)) {
      throw new SanValidationException(SanValidationProblem.FORMAT_PAWN_CAPTURE_WRONG_RANK,
          Message.getString("validation.san.format.pawn.capture.wrongRank", Nulls.toString(fourthChar)));
    }

    if (!isAnyPromotionRank(fourthChar)) {
      // non promotion e.g. dxe5

      // too long
      if (length > 4) {
        throw new SanValidationException(SanValidationProblem.FORMAT_PAWN_CAPTURE_NON_PROMOTION_OVERLENGTH,
            Message.getString("validation.san.format.pawn.capture.nonPromotion.overlength"));
      }

      // valid
      return new SanParse(SanFormat.PAWN_CAPTURING_NON_PROMOTION,
          new SanConversion(PieceType.PAWN, SanValidateFormat.parseFile(firstChar), Rank.NONE,
              Square.calculate(SanValidateFormat.parseFile(thirdChar), SanValidateFormat.parseRank(fourthChar)),
              PromotionPieceType.NONE, sanTerminalMarker));
    }

    // promotion e.g. dxe8=Q

    // no promotion symbol
    if (length == 4) {
      throw new SanValidationException(SanValidationProblem.FORMAT_PAWN_CAPTURE_PROMOTION_NO_PROMOTION_SYMBOL,
          Message.getString("validation.san.format.pawn.capture.promotion.noPromotionSymbol"));
    }

    final char fifthChar = core.charAt(4);

    // wrong promotion symbol
    if (!isPromotionSymbol(fifthChar)) {
      throw new SanValidationException(SanValidationProblem.FORMAT_PAWN_CAPTURE_PROMOTION_WRONG_PROMOTION_SYMBOL,
          Message.getString("validation.san.format.pawn.capture.promotion.wrongPromotionSymbol",
              Nulls.toString(fifthChar)));
    }

    // no promotion piece
    if (length == 5) {
      throw new SanValidationException(SanValidationProblem.FORMAT_PAWN_CAPTURE_PROMOTION_NO_PROMOTION_PIECE,
          Message.getString("validation.san.format.pawn.capture.promotion.noPromotionPiece"));
    }

    final char sixthChar = core.charAt(5);

    // wrong promotion piece
    if (!NotationPromotionPiece.exists(sixthChar)) {
      throw new SanValidationException(SanValidationProblem.FORMAT_PAWN_CAPTURE_PROMOTION_WRONG_PROMOTION_PIECE, Message
          .getString("validation.san.format.pawn.capture.promotion.wrongPromotionPiece", Nulls.toString(sixthChar)));
    }

    // too long
    if (length > 6) {
      throw new SanValidationException(SanValidationProblem.FORMAT_PAWN_CAPTURE_PROMOTION_OVERLENGTH,
          Message.getString("validation.san.format.pawn.capture.promotion.overlength"));
    }

    // valid
    return new SanParse(SanFormat.PAWN_CAPTURING_PROMOTION,
        new SanConversion(PieceType.PAWN, SanValidateFormat.parseFile(firstChar), Rank.NONE,
            Square.calculate(SanValidateFormat.parseFile(thirdChar), SanValidateFormat.parseRank(fourthChar)),
            NotationPromotionPiece.calculate(sixthChar).getPromotionPieceType(), sanTerminalMarker));
  }

  private static boolean isAnyPromotionRank(final char c) {
    return c == '1' || c == '8';
  }

  private static boolean isCaptureSymbol(final char c) {
    return c == 'x';
  }

  private static boolean isPromotionSymbol(final char c) {
    return c == '=';
  }

}
