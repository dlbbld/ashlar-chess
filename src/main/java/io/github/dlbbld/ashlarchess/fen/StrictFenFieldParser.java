// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.fen;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.NonNull;

import io.github.dlbbld.ashlarchess.fen.internal.FenField;

final class StrictFenFieldParser {

  private StrictFenFieldParser() {
  }

  static FenField parse(String fen) throws StrictFenFieldValidationException {
    final String regExp = "^([^ ]+) ([^ ]+) ([^ ]+) ([^ ]+) ([^ ]+) ([^ ]+)$";
    final Pattern pattern = Pattern.compile(regExp);
    final Matcher matcher = pattern.matcher(fen);
    if (!matcher.find()) {
      throw new StrictFenFieldValidationException("The format could not be identifed as valid FEN format");
    }
    // the regular expressions assures that these matches are not empty
    @SuppressWarnings("null") @NonNull final String piecePlacement = matcher.group(1);
    @SuppressWarnings("null") @NonNull final String sideToMove = matcher.group(2);
    @SuppressWarnings("null") @NonNull final String castlingRight = matcher.group(3);
    @SuppressWarnings("null") @NonNull final String enPassantCaptureTargetSquare = matcher.group(4);
    @SuppressWarnings("null") @NonNull final String halfMoveClock = matcher.group(5);
    @SuppressWarnings("null") @NonNull final String fullMoveNumber = matcher.group(6);

    return new FenField(piecePlacement, sideToMove, castlingRight, enPassantCaptureTargetSquare, halfMoveClock,
        fullMoveNumber);
  }

}
