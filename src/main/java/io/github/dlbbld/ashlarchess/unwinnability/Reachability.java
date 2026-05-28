package io.github.dlbbld.ashlarchess.unwinnability;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.utility.BasicUtility;

import java.util.Set;
import java.util.TreeSet;

class Reachability {

  private final EnumMap<Side, EnumMap<Square, VariableState>> reachabilityMap = Nulls.newEnumMap(Side.class);

  public void put(Side side, Square toSquare, VariableState reachable) {
    EnumMap<Square, VariableState> enumMap;
    if (!reachabilityMap.containsKey(side)) {
      enumMap = Nulls.newEnumMap(Square.class);
      reachabilityMap.put(side, enumMap);
    } else {
      enumMap = Nulls.get(reachabilityMap, side);
    }

    enumMap.put(toSquare, reachable);
  }

  public VariableState get(Side side, Square toSquare) {
    if (!reachabilityMap.containsKey(side)) {
      throw new IllegalArgumentException("Value is not set for color " + side);
    }

    final Map<Square, VariableState> map = Nulls.get(reachabilityMap, side);
    if (!map.containsKey(toSquare)) {
      throw new IllegalArgumentException("Value is not set for square " + toSquare);
    }

    return Nulls.get(map, toSquare);
  }

  public int calculateVariableCountSetToOne() {
    int count = 0;
    for (final Entry<Side, EnumMap<Square, VariableState>> mapEntryMap : Nulls.entrySet(reachabilityMap)) {
      final EnumMap<Square, VariableState> mapEntry = Nulls.get(reachabilityMap, Nulls.getKey(mapEntryMap));
      for (final Entry<Square, VariableState> entry : mapEntry.entrySet()) {
        if (entry.getValue() == VariableState.ONE) {
          count++;
        }
      }
    }
    return count;
  }

  public List<ReachabilityVariable> calculateEntriesWithValueZero() {
    return calculateEntries(VariableState.ZERO);
  }

  private List<ReachabilityVariable> calculateEntriesWithValueOne() {
    return calculateEntries(VariableState.ONE);
  }

  private List<ReachabilityVariable> calculateEntries(VariableState reachable) {
    final List<ReachabilityVariable> result = new ArrayList<>();
    for (final Entry<Side, EnumMap<Square, VariableState>> mapEntryMap : Nulls.entrySet(reachabilityMap)) {
      final EnumMap<Square, VariableState> mapEntry = Nulls.get(reachabilityMap, Nulls.getKey(mapEntryMap));
      for (final Entry<Square, VariableState> entry : mapEntry.entrySet()) {
        if (entry.getValue() == reachable) {
          result.add(new ReachabilityVariable(Nulls.getKey(mapEntryMap), Nulls.getKey(entry)));
        }
      }
    }
    return result;
  }

  public String print() {

    final List<String> lineList = new ArrayList<>();

    lineList.add("");
    lineList.add("Reachability:");

    final List<ReachabilityVariable> entriesWithValueOneList = calculateEntriesWithValueOne();

    final Set<Square> reachableSquareSetWhite = calculateSquareSet(Side.WHITE, entriesWithValueOneList);
    final String squareListWhite = BasicUtility.calculateSquareList(reachableSquareSetWhite);
    lineList.add(Side.WHITE.getName() + ": " + squareListWhite);

    final Set<Square> reachableSquareSetBlack = calculateSquareSet(Side.BLACK, entriesWithValueOneList);
    final String squareListBlack = BasicUtility.calculateSquareList(reachableSquareSetBlack);
    lineList.add(Side.BLACK.getName() + ": " + squareListBlack);

    return BasicUtility.convertToString(lineList);
  }

  private static Set<Square> calculateSquareSet(Side side, List<ReachabilityVariable> entryList) {
    final Set<Square> squareSet = new TreeSet<>();
    for (final ReachabilityVariable reachabilityVariable : entryList) {
      if (reachabilityVariable.sideWhichCanReach() == side) {
        squareSet.add(reachabilityVariable.toSquare());
      }
    }
    return squareSet;
  }

  @Override
  public String toString() {
    return print();
  }
}
