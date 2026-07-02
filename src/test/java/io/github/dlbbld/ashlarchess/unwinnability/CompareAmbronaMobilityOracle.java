// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.test.ConfigurationTestConstants;
import io.github.dlbbld.ashlarchess.test.common.utility.FileUtility;

public final class CompareAmbronaMobilityOracle {

  private static final int MAX_PRINTED_DIFFERENCES = 10;

  private static final Path ORACLE_PATH = Nulls.pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
      "src/test/resources/oracle/cha/ashlar-pgn/mobility.tsv");

  private CompareAmbronaMobilityOracle() {
  }

  public record MobilityOracleComparison(int comparedFenCount, int fenDifferenceCount, int rowDifferenceCount,
      List<String> differentFens, List<String> printedDifferences) {
  }

  public static void main(String[] args) throws Exception {
    final MobilityOracleComparison comparison = compare();

    System.out.println("Compared FENs: " + comparison.comparedFenCount());
    System.out.println("FENs with differences: " + comparison.fenDifferenceCount());
    System.out.println("Row differences: " + comparison.rowDifferenceCount());
    for (final String fen : comparison.differentFens()) {
      System.out.println("Different FEN: " + fen);
    }
    for (final String difference : comparison.printedDifferences()) {
      System.out.println();
      System.out.println(difference);
    }
  }

  public static MobilityOracleComparison compare() throws Exception {
    final Map<String, List<String>> expectedByFen = readExpectedByFen();
    int fenDifferenceCount = 0;
    int rowDifferenceCount = 0;
    final List<String> differentFens = new ArrayList<>();
    final List<String> printedDifferences = new ArrayList<>();

    for (final Map.Entry<String, List<String>> entry : Nulls.entrySet(expectedByFen)) {
      final String fen = Nulls.getKey(entry);
      final List<String> expectedRows = Nulls.getValue(entry);
      final List<String> actualRows = MobilityOracleFormatter.calculateRows(fen);
      final int differenceCount = countDifferences(expectedRows, actualRows, printedDifferences);
      if (differenceCount != 0) {
        fenDifferenceCount++;
        differentFens.add(fen);
        rowDifferenceCount += differenceCount;
      }
    }
    return new MobilityOracleComparison(expectedByFen.size(), fenDifferenceCount, rowDifferenceCount,
        Nulls.copyOfList(differentFens), Nulls.copyOfList(printedDifferences));
  }

  private static Map<String, List<String>> readExpectedByFen() throws Exception {
    final List<String> lines = FileUtility.readFileLines(ORACLE_PATH);
    if (lines.isEmpty() || !MobilityOracleFormatter.HEADER.equals(Nulls.get(lines, 0))) {
      throw new IllegalStateException("Unexpected mobility oracle header");
    }

    final Map<String, List<String>> expectedByFen = new LinkedHashMap<>();
    for (int i = 1; i < lines.size(); i++) {
      final String line = Nulls.get(lines, i);
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
      List<String> printedDifferences) {
    int differenceCount = 0;
    final int maxSize = Math.max(expectedRows.size(), actualRows.size());
    for (int i = 0; i < maxSize; i++) {
      final String expectedRow = i < expectedRows.size() ? Nulls.get(expectedRows, i) : "<missing>";
      final String actualRow = i < actualRows.size() ? Nulls.get(actualRows, i) : "<missing>";
      if (!expectedRow.equals(actualRow)) {
        differenceCount++;
        if (printedDifferences.size() < MAX_PRINTED_DIFFERENCES) {
          printedDifferences.add("Expected: " + expectedRow + "\nActual:   " + actualRow);
        }
      }
    }
    return differenceCount;
  }
}
