package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.model.DynamicPosition;
import com.dlb.chess.common.model.HalfMove;

abstract class PositionIdentifierUtility {

  private static final int BASE = 26;
  private static final int ASCII_TABLE_BEFORE_UPPER_CASE_A_NUMBER = 64;

  public static Map<DynamicPosition, String> calculatePositionIdentifierMap(List<List<HalfMove>> halfMoveListList) {
    final Map<DynamicPosition, String> result = new HashMap<>();
    var positionNumber = 1;
    for (final List<HalfMove> halfMoveList : halfMoveListList) {
      for (final HalfMove halfMove : halfMoveList) {
        final DynamicPosition position = halfMove.dynamicPosition();
        if (!result.containsKey(position)) {
          result.put(position, calculateIdentifier(positionNumber));
          positionNumber++;
        }
      }
    }
    return result;
  }

  static String calculateIdentifier(int positionNumber) {

    final List<Integer> representationList = calculateRepresentation(positionNumber - 1, BASE);

    final StringBuilder result = new StringBuilder();
    for (var i = 0; i < representationList.size(); i++) {
      final int representation = Nulls.get(representationList, i);
      final int representationAdaptedForLastDigit;
      if (i == representationList.size() - 1) {
        representationAdaptedForLastDigit = representation + 1;
      } else {
        representationAdaptedForLastDigit = representation;
      }
      final var letter = (char) (ASCII_TABLE_BEFORE_UPPER_CASE_A_NUMBER + representationAdaptedForLastDigit);
      result.append(letter);
    }
    return Nulls.toString(result);
  }

  static List<Integer> calculateRepresentation(int number, int base) {
    final List<Integer> result = new ArrayList<>();

    var workingNumber = number;
    double workingNumberToDoubleForDivision = workingNumber;
    var multiplies = (int) Math.floor(workingNumberToDoubleForDivision / base);
    var remainder = workingNumber % base;
    result.add(remainder);
    while (multiplies > 0) {
      workingNumber = (workingNumber - remainder) / base;
      workingNumberToDoubleForDivision = workingNumber;
      multiplies = (int) Math.floor(workingNumberToDoubleForDivision / base);
      remainder = workingNumber % base;
      result.add(remainder);
    }

    Collections.reverse(result);

    return result;
  }
}
