package com.dlb.chess.fen;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.NonNull;

import com.dlb.chess.common.exceptions.FenRawValidationException;
import com.dlb.chess.fen.model.FenRaw;

public class FenParserRaw {

  private FenParserRaw() {
  }

  public static FenRaw parseFenRaw(String fen) throws FenRawValidationException {
    final String regExp = "^([^ ]+) ([^ ]+) ([^ ]+) ([^ ]+) ([^ ]+) ([^ ]+)$";
    final Pattern pattern = Pattern.compile(regExp);
    final Matcher matcher = pattern.matcher(fen);
    if (!matcher.find()) {
      throw new FenRawValidationException("The format could not be identifed as valid FEN format");
    }
    // the regular expressions assures that these matches are not empty
    @SuppressWarnings("null") @NonNull final String piecePlacement = matcher.group(1);
    @SuppressWarnings("null") @NonNull final String havingMove = matcher.group(2);
    @SuppressWarnings("null") @NonNull final String castlingRight = matcher.group(3);
    @SuppressWarnings("null") @NonNull final String enPassantCaptureTargetSquare = matcher.group(4);
    @SuppressWarnings("null") @NonNull final String halfMoveClock = matcher.group(5);
    @SuppressWarnings("null") @NonNull final String fullMoveNumber = matcher.group(6);

    return new FenRaw(piecePlacement, havingMove, castlingRight, enPassantCaptureTargetSquare, halfMoveClock,
        fullMoveNumber);
  }

}
