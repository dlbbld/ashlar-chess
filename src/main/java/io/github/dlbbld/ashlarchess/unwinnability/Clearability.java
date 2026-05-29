// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.eclipse.jdt.annotation.NonNull;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.utility.BasicUtility;

class Clearability {

  private final Map<PiecePlacement, VariableState> clearabilityMap = new HashMap<>();

  public void put(PiecePlacement piecePlacement, VariableState clearable) {
    clearabilityMap.put(piecePlacement, clearable);
  }

  public VariableState get(PiecePlacement piecePlacement) {
    if (!clearabilityMap.containsKey(piecePlacement)) {
      throw new IllegalArgumentException("Value is not set for piece placement " + piecePlacement);
    }
    return Nulls.get(clearabilityMap, piecePlacement);
  }

  public int calculateVariableCountSetToOne() {
    int count = 0;
    for (final Entry<PiecePlacement, VariableState> entry : clearabilityMap.entrySet()) {
      if (entry.getValue() == VariableState.ONE) {
        count++;
      }
    }
    return count;
  }

  public List<PiecePlacement> calculateEntriesWithValueZero() {
    return calculateEntries(VariableState.ZERO);
  }

  private List<PiecePlacement> calculateEntries(VariableState variableState) {
    final List<PiecePlacement> result = new ArrayList<>();
    for (final Entry<PiecePlacement, VariableState> entry : clearabilityMap.entrySet()) {
      if (entry.getValue() == variableState) {
        result.add(Nulls.getKey(entry));
      }
    }
    return result;
  }

  public String print() {

    final List<String> lineList = new ArrayList<>();

    lineList.add("");
    lineList.add("Clearability:");

    // TreeSet for ordering
    for (final PiecePlacement piecePlacement : new TreeSet<>(clearabilityMap.keySet())) {
      final VariableState variableState = Nulls.get(clearabilityMap, piecePlacement);
      final StringBuilder pieceDescription = new StringBuilder();
      pieceDescription.append(piecePlacement.toString());
      pieceDescription.append(": ");
      pieceDescription.append(variableState.getDescription());
      @SuppressWarnings("null") @NonNull final String string = pieceDescription.toString();
      lineList.add(string);
    }

    return BasicUtility.convertToString(lineList);
  }

  @Override
  public String toString() {
    return print();
  }

}
