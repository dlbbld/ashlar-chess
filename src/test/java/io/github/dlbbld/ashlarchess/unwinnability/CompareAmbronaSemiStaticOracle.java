package io.github.dlbbld.ashlarchess.unwinnability;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.test.ConfigurationTestConstants;
import io.github.dlbbld.ashlarchess.test.common.utility.FileUtility;

public final class CompareAmbronaSemiStaticOracle {

  private static final int MAX_PRINTED_DIFFERENCES = 10;
  private static final int MAX_PRINTED_DIFFERENT_FENS = 10;

  private static final Path ORACLE_PATH = Nulls.pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
      "src/test/resources/oracle/ambrona-semistatic.tsv");

  private CompareAmbronaSemiStaticOracle() {
  }

  public record SemiStaticOracleComparison(int comparedFenCount, int fenDifferenceCount, int rowDifferenceCount,
      Map<String, Integer> differenceCountByKind, List<String> differentFenList, List<String> printedDifferenceList) {
  }

  public static void main(String[] args) throws Exception {
    final SemiStaticOracleComparison comparison = compare();

    System.out.println("Compared FENs: " + comparison.comparedFenCount());
    System.out.println("FENs with differences: " + comparison.fenDifferenceCount());
    System.out.println("Row differences: " + comparison.rowDifferenceCount());
    for (final Map.Entry<String, Integer> entry : Nulls.entrySet(comparison.differenceCountByKind())) {
      System.out.println("Row differences for " + Nulls.getKey(entry) + ": " + Nulls.getValue(entry));
    }
    for (final String fen : comparison.differentFenList().subList(0,
        Math.min(MAX_PRINTED_DIFFERENT_FENS, comparison.differentFenList().size()))) {
      System.out.println("Different FEN: " + fen);
    }
    for (final String difference : comparison.printedDifferenceList()) {
      System.out.println();
      System.out.println(difference);
    }
  }

  public static SemiStaticOracleComparison compare() throws Exception {
    return compare(Integer.MAX_VALUE);
  }

  /**
   * Compare against the oracle, processing at most {@code maxFens} unique FEN entries from the oracle file (in the
   * file's encounter order). Pass {@link Integer#MAX_VALUE} for the full comparison. Smoke-mode callers pass a small
   * cap to keep the test wall-clock short.
   */
  public static SemiStaticOracleComparison compare(int maxFens) throws Exception {
    final Map<String, List<String>> expectedByFen = readExpectedByFen();
    int fenDifferenceCount = 0;
    int rowDifferenceCount = 0;
    int comparedFenCount = 0;
    final List<String> differentFenList = new ArrayList<>();
    final List<String> printedDifferenceList = new ArrayList<>();
    final Map<String, Integer> differenceCountByKind = new TreeMap<>();

    for (final Map.Entry<String, List<String>> entry : Nulls.entrySet(expectedByFen)) {
      if (comparedFenCount >= maxFens) {
        break;
      }
      final String fen = Nulls.getKey(entry);
      final List<String> expectedRows = Nulls.getValue(entry);
      final List<String> actualRows = SemiStaticOracleFormatter.calculateRows(fen);
      final int differenceCount = countDifferences(expectedRows, actualRows, printedDifferenceList,
          differenceCountByKind);
      if (differenceCount != 0) {
        fenDifferenceCount++;
        differentFenList.add(fen);
        rowDifferenceCount += differenceCount;
      }
      comparedFenCount++;
    }
    return new SemiStaticOracleComparison(comparedFenCount, fenDifferenceCount, rowDifferenceCount,
        Nulls.copyOfMap(differenceCountByKind), Nulls.copyOfList(differentFenList),
        Nulls.copyOfList(printedDifferenceList));
  }

  private static Map<String, List<String>> readExpectedByFen() throws Exception {
    final List<String> lineList = FileUtility.readFileLines(ORACLE_PATH);
    if (lineList.isEmpty() || !SemiStaticOracleFormatter.HEADER.equals(Nulls.get(lineList, 0))) {
      throw new IllegalStateException("Unexpected semistatic oracle header");
    }

    final Map<String, List<String>> expectedByFen = new LinkedHashMap<>();
    for (int i = 1; i < lineList.size(); i++) {
      final String line = Nulls.get(lineList, i);
      final String[] itemArray = Nulls.split(line, "\t");
      if (itemArray.length != 5) {
        throw new IllegalStateException("Invalid semistatic oracle row: " + line);
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
      List<String> printedDifferenceList, Map<String, Integer> differenceCountByKind) {
    int differenceCount = 0;
    final int maxSize = Math.max(expectedRows.size(), actualRows.size());
    for (int i = 0; i < maxSize; i++) {
      final String expectedRow = i < expectedRows.size() ? Nulls.get(expectedRows, i) : "<missing>";
      final String actualRow = i < actualRows.size() ? Nulls.get(actualRows, i) : "<missing>";
      if (!expectedRow.equals(actualRow)) {
        differenceCount++;
        final String kind = calculateKind(expectedRow, actualRow);
        differenceCountByKind.put(kind, Nulls.getOrDefault(differenceCountByKind, kind, 0) + 1);
        if (printedDifferenceList.size() < MAX_PRINTED_DIFFERENCES) {
          printedDifferenceList.add("Expected: " + expectedRow + "\nActual:   " + actualRow);
        }
      }
    }
    return differenceCount;
  }

  private static String calculateKind(String expectedRow, String actualRow) {
    final String sourceRow = "<missing>".equals(expectedRow) ? actualRow : expectedRow;
    final String[] itemArray = Nulls.split(sourceRow, "\t");
    if (itemArray.length != 5) {
      return "<unknown>";
    }
    return Nulls.get(itemArray, 2);
  }
}
