package io.github.dlbbld.ashlarchess.test.generate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.exceptions.FileSystemAccessException;
import io.github.dlbbld.ashlarchess.common.utility.IoUtility;
import io.github.dlbbld.ashlarchess.test.ConfigurationTestConstants;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;
import io.github.dlbbld.ashlarchess.unwinnability.SemiStaticOracleFormatter;

/**
 * Regenerates the Ambrona semistatic oracle from the cached final FENs in {@link PgnFen}. Requires WSL with D3-Chess
 * built and Stockfish installed; pass the WSL D3-Chess checkout root as the optional first argument when it is not at
 * the default location.
 */
public final class GenerateAmbronaSemiStaticOracle {

  private static final Logger logger = Nulls.getLogger(GenerateAmbronaSemiStaticOracle.class);

  private static final String D3_CHESS_PATH_PROPERTY = "ambrona.d3.path";
  private static final String WSL_RUNNER_PATH = "/tmp/clean-chess-ambrona-oracle/semistatic-oracle";
  private static final int PROGRESS_LOG_INTERVAL = 25;

  private static final Path CPP_SOURCE_PATH = Nulls.pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
      "tools/ambrona-oracle/semistatic_oracle.cpp");
  private static final Path ORACLE_PATH = Nulls.pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
      "src/test/resources/oracle/ambrona-semistatic.tsv");

  private GenerateAmbronaSemiStaticOracle() {
  }

  public static void main(String[] args) throws Exception {
    if (args.length > 1) {
      throw new IllegalArgumentException("Usage: GenerateAmbronaSemiStaticOracle [wsl-d3-chess-root]");
    }
    final String d3ChessRoot = resolveD3ChessRoot(args);
    generate(d3ChessRoot);
  }

  private static String resolveD3ChessRoot(String[] args) throws Exception {
    if (args.length == 1) {
      return Nulls.get(args, 0);
    }
    final String propertyValue = System.getProperty(D3_CHESS_PATH_PROPERTY);
    if (propertyValue != null) {
      return propertyValue;
    }
    return readWslDefaultD3ChessRoot();
  }

  private static void generate(String d3ChessRoot) throws Exception {
    final List<String> fenList = collectDistinctFinalFenList();
    logger.info("Collected {} distinct final FENs from the PGN test cases.", fenList.size());

    buildRunner(d3ChessRoot);
    final List<String> oracleLineList = runOracle(fenList);

    Files.createDirectories(Nulls.getParent(ORACLE_PATH));
    final List<String> fileLineList = new ArrayList<>();
    fileLineList.add(SemiStaticOracleFormatter.HEADER);
    fileLineList.addAll(oracleLineList);
    Files.writeString(ORACLE_PATH, Nulls.join("\n", fileLineList) + "\n", StandardCharsets.UTF_8);
    logger.info("Wrote {} Ambrona semistatic rows to {}", oracleLineList.size(), ORACLE_PATH);
  }

  private static List<String> collectDistinctFinalFenList() {
    final Set<String> fenSet = new LinkedHashSet<>();
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        fenSet.add(testCase.finalFen());
      }
    }
    return new ArrayList<>(fenSet);
  }

  private static void buildRunner(String d3ChessRoot) throws Exception {
    final String cppSourcePath = windowsPathToWsl(CPP_SOURCE_PATH);
    final String quotedD3ChessRoot = shellQuote(d3ChessRoot);
    final String command = "mkdir -p /tmp/clean-chess-ambrona-oracle && g++ -o " + shellQuote(WSL_RUNNER_PATH) + " "
        + shellQuote(cppSourcePath) + " " + quotedD3ChessRoot + "/src/util.cpp " + quotedD3ChessRoot
        + "/src/semistatic.cpp -lpthread -O3 -I/usr/local/include/stockfish -I" + quotedD3ChessRoot
        + "/src -lstockfish";
    runWslCommand(command);
  }

  private static List<String> runOracle(List<String> fenList) throws Exception {
    final ProcessBuilder processBuilder = new ProcessBuilder("wsl", "bash", "-lc",
        "LD_LIBRARY_PATH=/usr/local/lib " + shellQuote(WSL_RUNNER_PATH));
    final Process process = IoUtility.startProcess(processBuilder);
    final List<String> result = new ArrayList<>();

    try (InputStream errorStream = IoUtility.getErrorStream(process)) {
      try (
          BufferedWriter writer = new BufferedWriter(
              new OutputStreamWriter(IoUtility.getOutputStream(process), StandardCharsets.UTF_8));
          BufferedReader reader = new BufferedReader(
              new InputStreamReader(IoUtility.getInputStream(process), StandardCharsets.UTF_8))) {

        int processed = 0;
        for (final String fen : fenList) {
          writer.write(fen);
          writer.write('\n');
          writer.flush();

          readRowsForFen(reader, fen, result);
          processed++;

          if (processed % PROGRESS_LOG_INTERVAL == 0 || processed == fenList.size()) {
            logger.info("Generated semistatic rows for {}/{} FENs.", processed, fenList.size());
          }
        }
      }

      final int exitCode = process.waitFor();
      if (exitCode != 0) {
        throw new IllegalStateException(
            "Ambrona semistatic runner exited with " + exitCode + ": " + readStream(errorStream));
      }
    }
    return result;
  }

  private static void readRowsForFen(BufferedReader reader, String fen, List<String> result) throws IOException {
    while (true) {
      final String resultLine = reader.readLine();
      if (resultLine == null) {
        throw new IllegalStateException("Ambrona semistatic runner stopped before finishing " + fen);
      }
      if (("END\t" + fen).equals(resultLine)) {
        return;
      }
      validateResultLine(resultLine, fen);
      result.add(resultLine);
    }
  }

  private static void validateResultLine(String resultLine, String fen) {
    final String[] itemArray = Nulls.split(resultLine, "\t");
    if (itemArray.length != 5 || !Nulls.get(itemArray, 0).equals(fen)) {
      throw new IllegalStateException("Invalid semistatic TSV row: " + resultLine);
    }
  }

  private static String windowsPathToWsl(Path path) throws Exception {
    final String windowsPath = Nulls.replace(Nulls.toString(Nulls.toAbsolutePath(path)), '\\', '/');
    final ProcessBuilder processBuilder = new ProcessBuilder("wsl", "wslpath", "-a", windowsPath);
    processBuilder.redirectErrorStream(true);
    final Process process = IoUtility.startProcess(processBuilder);
    try (InputStream outputStream = IoUtility.getInputStream(process)) {
      final String output = Nulls.trim(readStream(outputStream));
      final int exitCode = process.waitFor();
      if (exitCode != 0) {
        throw new IllegalStateException("wslpath failed with " + exitCode + ": " + output);
      }
      return output;
    }
  }

  private static String readWslDefaultD3ChessRoot() throws Exception {
    final ProcessBuilder processBuilder = new ProcessBuilder("wsl", "bash", "-lc", "printf '%s' \"$HOME/D3-Chess\"");
    processBuilder.redirectErrorStream(true);
    final Process process = IoUtility.startProcess(processBuilder);
    try (InputStream outputStream = IoUtility.getInputStream(process)) {
      final String output = Nulls.trim(readStream(outputStream));
      final int exitCode = process.waitFor();
      if (exitCode != 0) {
        throw new IllegalStateException(
            "Resolving the WSL D3-Chess default path failed with " + exitCode + ": " + output);
      }
      if (output.isBlank()) {
        throw new IllegalStateException("Resolving the WSL D3-Chess default path returned an empty path");
      }
      return output;
    }
  }

  private static void runWslCommand(String command) throws Exception {
    final ProcessBuilder processBuilder = new ProcessBuilder("wsl", "bash", "-lc", command);
    processBuilder.redirectErrorStream(true);
    final Process process = IoUtility.startProcess(processBuilder);
    try (InputStream outputStream = IoUtility.getInputStream(process)) {
      final String output = Nulls.trim(readStream(outputStream));
      final int exitCode = process.waitFor();
      if (!output.isEmpty()) {
        logger.info(output);
      }
      if (exitCode != 0) {
        throw new IllegalStateException("WSL command failed with " + exitCode + ": " + output);
      }
    }
  }

  private static String readStream(InputStream inputStream) {
    try {
      return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    } catch (final IOException ioe) {
      throw new FileSystemAccessException("Reading process output failed", ioe);
    }
  }

  private static String shellQuote(String value) {
    return "'" + value.replace("'", "'\"'\"'") + "'";
  }
}
