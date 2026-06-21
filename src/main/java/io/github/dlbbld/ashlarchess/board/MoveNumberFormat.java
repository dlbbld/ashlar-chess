// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board;

import io.github.dlbbld.ashlarchess.board.enums.AddSpace;
import io.github.dlbbld.ashlarchess.board.enums.Side;

public final class MoveNumberFormat {

  private MoveNumberFormat() {
  }

  public static String calculateMoveNumberAndSanWithSpace(int fullMoveNumber, Side sideToMove, String san) {
    return calculateMoveNumberAndSan(fullMoveNumber, sideToMove, san, AddSpace.YES);
  }

  private static String calculateMoveNumberAndSan(int fullMoveNumber, Side sideToMove, String san, AddSpace addSpace) {
    return calculateFullMoveNumberInitial(fullMoveNumber, sideToMove, addSpace) + san;
  }

  public static String calculateFullMoveNumberInitialWithSpace(int initialFullMoveNumber, Side sideToMove) {
    return calculateFullMoveNumberInitial(initialFullMoveNumber, sideToMove, AddSpace.YES);
  }

  public static String calculateFullMoveNumberInitialWithoutSpace(int initialFullMoveNumber, Side sideToMove) {
    return calculateFullMoveNumberInitial(initialFullMoveNumber, sideToMove, AddSpace.NO);
  }

  private static String calculateFullMoveNumberInitial(int initialFullMoveNumber, Side sideToMove, AddSpace addSpace) {

    return switch (sideToMove) {
      case WHITE -> initialFullMoveNumber + "." + addSpace.getValue();
      case BLACK -> initialFullMoveNumber + "..." + addSpace.getValue();
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

}
