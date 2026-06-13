// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.dlbbld.ashlarchess.board.MoveNumberFormat;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.common.model.DynamicPosition;

abstract class PositionIdentifierUtility {

  private static final int BASE = 26;
  private static final int ASCII_TABLE_BEFORE_UPPER_CASE_A_NUMBER = 64;

  /**
   * Assigns a unique letter label per distinct position across both reports. Claim-ahead entries are visited first in
   * their stored order, then any positions appearing only in the existing-repetition groups are appended.
   * Claim-ahead-first ordering matches the letter assignment users have seen in the printed report historically; the
   * second walk closes the latent throw-on-missing edge against future fixtures where a threefold-reached position
   * might not also appear as a claim-ahead opportunity.
   */
  public static Map<DynamicPosition, String> calculatePositionIdentifierMap(ThreefoldClaimAheadReport claimAhead,
      ThreefoldExistingReport existing) {
    final Map<DynamicPosition, String> result = new HashMap<>();
    int positionNumber = 1;
    for (final ClaimAheadEntry entry : claimAhead.entries()) {
      final DynamicPosition position = entry.claimAheadMove().dynamicPosition();
      if (!result.containsKey(position)) {
        result.put(position, calculateIdentifier(positionNumber));
        positionNumber++;
      }
    }
    for (final RepetitionGroup group : existing.groups()) {
      final DynamicPosition position = group.repeatedPosition();
      if (!result.containsKey(position)) {
        result.put(position, calculateIdentifier(positionNumber));
        positionNumber++;
      }
    }
    return result;
  }

  static String calculateIdentifier(int positionNumber) {

    final List<Integer> representationList = calculateRepresentation(positionNumber - 1, BASE);

    final StringBuilder result = new StringBuilder();
    for (int i = 0; i < representationList.size(); i++) {
      final int representation = Nulls.get(representationList, i);
      final int representationAdaptedForLastDigit;
      if (i == representationList.size() - 1) {
        representationAdaptedForLastDigit = representation + 1;
      } else {
        representationAdaptedForLastDigit = representation;
      }
      final char letter = (char) (ASCII_TABLE_BEFORE_UPPER_CASE_A_NUMBER + representationAdaptedForLastDigit);
      result.append(letter);
    }
    return Nulls.toString(result);
  }

  static List<Integer> calculateRepresentation(int number, int base) {
    final List<Integer> result = new ArrayList<>();

    int workingNumber = number;
    double workingNumberToDoubleForDivision = workingNumber;
    int multiplies = (int) Math.floor(workingNumberToDoubleForDivision / base);
    int remainder = workingNumber % base;
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

  static String getPositionIdentifier(DynamicPosition position, Map<DynamicPosition, String> positionIdentifierMap) {
    if (!positionIdentifierMap.containsKey(position)) {
      throw new ProgrammingMistakeException("position identifier map does not contain position: " + position);
    }
    return Nulls.get(positionIdentifierMap, position);
  }

  private static String calculatePositionInformation(MoveRecord repetitionSeriesMove, int totalRepetitionCount,
      boolean isAddAsterisk, Map<DynamicPosition, String> positionIdentifierMap) {

    final StringBuilder result = new StringBuilder();

    result.append("(");

    final String positionIdentifier = PositionIdentifierUtility
        .getPositionIdentifier(repetitionSeriesMove.dynamicPosition(), positionIdentifierMap);

    result.append(positionIdentifier);
    if (isAddAsterisk) {
      result.append("*");
    }
    result.append(" - ");
    // result.append(repetitionSeriesMove.countRepetition());
    // result.append("/");
    result.append(totalRepetitionCount);
    result.append(")");

    return Nulls.toString(result);
  }

  static String calculateMoveInformation(MoveRecord move, int totalRepetitionCount, boolean isAddAsterisk,
      boolean isAddPositionInformation, Map<DynamicPosition, String> positionIdentifierMap) {
    final StringBuilder result = new StringBuilder();

    result.append(
        MoveNumberFormat.calculateMoveNumberAndSanWithSpace(move.fullMoveNumber(), move.movingPiece().getSide(),
            move.san()));

    if (isAddPositionInformation) {
      result.append(" ");

      final String positionInformation = calculatePositionInformation(move, totalRepetitionCount, isAddAsterisk,
          positionIdentifierMap);
      result.append(positionInformation);
    }

    return Nulls.toString(result);
  }
}
