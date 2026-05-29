// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.test.ConfigurationTestConstants;
import io.github.dlbbld.ashlarchess.test.common.utility.FileUtility;

public final class CompareAmbronaMobilityOracle {

  private static final int MAX_PRINTED_DIFFERENCES = 10;

  private static final Path ORACLE_PATH = Nulls.pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
      "src/test/resources/oracle/ambrona-mobility.tsv");

  private CompareAmbronaMobilityOracle() {
  }

  public record MobilityOracleComparison(int comparedFenCount, int fenDifferenceCount, int rowDifferenceCount,
      List<String> differentFenList, List<String> printedDifferenceList) {
  }

  public static void main(String[] args) throws Exception {
    final MobilityOracleComparison comparison = compare();

    System.out.println("Compared FENs: " + comparison.comparedFenCount());
    System.out.println("FENs with differences: " + comparison.fenDifferenceCount());
    System.out.println("Row differences: " + comparison.rowDifferenceCount());
    for (final String fen : comparison.differentFenList()) {
      System.out.println("Different FEN: " + fen);
    }
    for (final String difference : comparison.printedDifferenceList()) {
      System.out.println();
      System.out.println(difference);
    }
  }

  public static MobilityOracleComparison compare() throws Exception {
    final Map<String, List<String>> expectedByFen = readExpectedByFen();
    int fenDifferenceCount = 0;
    int rowDifferenceCount = 0;
    final List<String> differentFenList = new ArrayList<>();
    final List<String> printedDifferenceList = new ArrayList<>();

    for (final Map.Entry<String, List<String>> entry : Nulls.entrySet(expectedByFen)) {
      final String fen = Nulls.getKey(entry);
      final List<String> expectedRows = Nulls.getValue(entry);
      final List<String> actualRows = MobilityOracleFormatter.calculateRows(fen);
      final int differenceCount = countDifferences(expectedRows, actualRows, printedDifferenceList);
      if (differenceCount != 0) {
        fenDifferenceCount++;
        differentFenList.add(fen);
        rowDifferenceCount += differenceCount;
      }
    }
    return new MobilityOracleComparison(expectedByFen.size(), fenDifferenceCount, rowDifferenceCount,
        Nulls.copyOfList(differentFenList), Nulls.copyOfList(printedDifferenceList));
  }

  private static Map<String, List<String>> readExpectedByFen() throws Exception {
    final List<String> lineList = FileUtility.readFileLines(ORACLE_PATH);
    if (lineList.isEmpty() || !MobilityOracleFormatter.HEADER.equals(Nulls.get(lineList, 0))) {
      throw new IllegalStateException("Unexpected mobility oracle header");
    }

    final Map<String, List<String>> expectedByFen = new LinkedHashMap<>();
    for (int i = 1; i < lineList.size(); i++) {
      final String line = Nulls.get(lineList, i);
      final String[] itemArray = Nulls.split(line, "\t");
      if (itemArray.length != 5) {
        throw new IllegalStateException("Invalid mobility oracle row: " + line);
      }
      final String fen = Nulls.get(itemArray, 0);
      if (!expectedByFen.containsKey(fen)) {
        expectedByFen.put(fen, new ArrayList<>());
      }
      Nulls.get(expectedByFen, fen).add(line);
    }
    return expectedByFen;
  }

  private static int countDifferences(List<String> expectedRows, List<String> actualRows,
      List<String> printedDifferenceList) {
    int differenceCount = 0;
    final int maxSize = Math.max(expectedRows.size(), actualRows.size());
    for (int i = 0; i < maxSize; i++) {
      final String expectedRow = i < expectedRows.size() ? Nulls.get(expectedRows, i) : "<missing>";
      final String actualRow = i < actualRows.size() ? Nulls.get(actualRows, i) : "<missing>";
      if (!expectedRow.equals(actualRow)) {
        differenceCount++;
        if (printedDifferenceList.size() < MAX_PRINTED_DIFFERENCES) {
          printedDifferenceList.add("Expected: " + expectedRow + "\nActual:   " + actualRow);
        }
      }
    }
    return differenceCount;
  }
}
