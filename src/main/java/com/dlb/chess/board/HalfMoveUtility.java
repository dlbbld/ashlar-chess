package com.dlb.chess.board;

import com.dlb.chess.board.enums.AddSpace;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.model.HalfMove;

public abstract class HalfMoveUtility {

  public static String calculateMoveNumberAndSanWithSpace(int fullMoveNumber, Side havingMove, String san) {
    return calculateMoveNumberAndSan(fullMoveNumber, havingMove, san, AddSpace.YES);
  }

  public static String calculateMoveNumberAndSanWithSpace(HalfMove halfMove) {
    return calculateMoveNumberAndSan(halfMove, AddSpace.YES);
  }

  public static String calculateMoveNumberAndSanWithoutSpace(HalfMove halfMove) {
    return calculateMoveNumberAndSan(halfMove, AddSpace.NO);
  }

  private static String calculateMoveNumberAndSan(HalfMove halfMove, AddSpace addSpace) {
    return calculateFullMoveNumberInitial(halfMove.fullMoveNumber(), halfMove.havingMove(), addSpace) + halfMove.san();
  }

  private static String calculateMoveNumberAndSan(int fullMoveNumber, Side havingMove, String san, AddSpace addSpace) {
    return calculateFullMoveNumberInitial(fullMoveNumber, havingMove, addSpace) + san;
  }

  public static String calculateFullMoveNumberInitialWithSpace(int initialFullMoveNumber, Side havingMove) {
    return calculateFullMoveNumberInitial(initialFullMoveNumber, havingMove, AddSpace.YES);
  }

  public static String calculateFullMoveNumberInitialWithoutSpace(int initialFullMoveNumber, Side havingMove) {
    return calculateFullMoveNumberInitial(initialFullMoveNumber, havingMove, AddSpace.NO);
  }

  private static String calculateFullMoveNumberInitial(int initialFullMoveNumber, Side havingMove, AddSpace addSpace) {

    return switch (havingMove) {
      case WHITE -> initialFullMoveNumber + "." + addSpace.getValue();
      case BLACK -> initialFullMoveNumber + "..." + addSpace.getValue();
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

}
