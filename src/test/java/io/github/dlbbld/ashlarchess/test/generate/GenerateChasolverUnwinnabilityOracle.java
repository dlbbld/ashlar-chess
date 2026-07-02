// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

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

import io.github.dlbbld.ashlarchess.common.utility.IoUtility;
import io.github.dlbbld.ashlarchess.exceptions.FileSystemAccessException;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.test.ConfigurationTestConstants;
import io.github.dlbbld.ashlarchess.test.common.utility.Loggers;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

/**
 * Regenerates the chasolver unwinnability oracle from the cached final FENs in {@link PgnFen}, over the exact same FEN
 * set as {@link GenerateAmbronaUnwinnabilityOracle} so the two oracles can be diffed 1:1. Requires WSL with a Rust
 * toolchain (rustup/cargo); the runner crate under {@code tools/chasolver-oracle} (which depends on Miguel Ambrona's
 * {@code chasolver} crate) is copied into WSL and built with {@code cargo build --release}, then streamed FENs.
 *
 * <p>
 * Run with: {@code mvn -o -q test-compile exec:java
 * -Dexec.mainClass=io.github.dlbbld.ashlarchess.test.generate.GenerateChasolverUnwinnabilityOracle
 * -Dexec.classpathScope=test}
 */
public final class GenerateChasolverUnwinnabilityOracle {
  private static final Logger logger = Loggers.getLogger(GenerateChasolverUnwinnabilityOracle.class);

  private static final String WSL_BUILD_DIR = "/tmp/ashlar-chess-chasolver-oracle";
  private static final String WSL_RUNNER_PATH = WSL_BUILD_DIR + "/target/release/chasolver-oracle";
  private static final int PROGRESS_LOG_INTERVAL = 100;

  private static final Path CRATE_SOURCE_PATH = Nulls.pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
      "tools/chasolver-oracle");
  private static final Path ORACLE_PATH = Nulls.pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
      "src/test/resources/oracle/chasolver/ashlar-pgn/unwinnability.tsv");

  private GenerateChasolverUnwinnabilityOracle() {
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 0) {
      throw new IllegalArgumentException("Usage: GenerateChasolverUnwinnabilityOracle (no arguments)");
    }
    generate();
  }

  private static void generate() throws Exception {
    final List<String> fens = collectDistinctFinalFens();
    logger.info("Collected {} distinct final FENs from the PGN test cases.", fens.size());

    buildRunner();
    final List<String> oracleLines = runOracle(fens);

    Files.createDirectories(Nulls.getParent(ORACLE_PATH));
    final List<String> fileLines = new ArrayList<>();
    fileLines.add("fen\tfullWhite\tfullBlack\tquickWhite\tquickBlack");
    fileLines.addAll(oracleLines);
    Files.writeString(ORACLE_PATH, Nulls.join("\n", fileLines) + "\n", StandardCharsets.UTF_8);
    logger.info("Wrote {} oracle rows to {}", oracleLines.size(), ORACLE_PATH);
  }

  private static List<String> collectDistinctFinalFens() {
    final Set<String> fenSet = new LinkedHashSet<>();
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        fenSet.add(testCase.finalFen());
      }
    }
    return new ArrayList<>(fenSet);
  }

  private static void buildRunner() throws Exception {
    final String crateWsl = windowsPathToWsl(CRATE_SOURCE_PATH);
    final String build = shellQuote(WSL_BUILD_DIR);
    final String command = ". \"$HOME/.cargo/env\" >/dev/null 2>&1; rm -rf " + build + " && mkdir -p " + build
        + " && cp " + shellQuote(crateWsl) + "/Cargo.toml " + build + "/ && cp -r " + shellQuote(crateWsl) + "/src "
        + build + "/ && cd " + build + " && cargo build --release";
    logger.info("Building the chasolver oracle runner in WSL (cargo build --release); first build downloads crates...");
    runWslCommand(command);
  }

  private static List<String> runOracle(List<String> fens) throws Exception {
    final ProcessBuilder processBuilder = new ProcessBuilder("wsl", "bash", "-lc", shellQuote(WSL_RUNNER_PATH));
    final Process process = IoUtility.startProcess(processBuilder);
    final List<String> result = new ArrayList<>();

    try (InputStream errorStream = IoUtility.getErrorStream(process)) {
      try (
          BufferedWriter writer = new BufferedWriter(
              new OutputStreamWriter(IoUtility.getOutputStream(process), StandardCharsets.UTF_8));
          BufferedReader reader = new BufferedReader(
              new InputStreamReader(IoUtility.getInputStream(process), StandardCharsets.UTF_8))) {

        int processed = 0;
        for (final String fen : fens) {
          writer.write(fen);
          writer.write('\n');
          writer.flush();

          final String resultLine = reader.readLine();
          if (resultLine == null) {
            throw new IllegalStateException("chasolver oracle runner stopped before returning a result for " + fen);
          }
          validateResultLine(resultLine);
          result.add(resultLine);
          processed++;

          if (processed % PROGRESS_LOG_INTERVAL == 0 || processed == fens.size()) {
            logger.info("Generated {}/{} chasolver oracle rows.", processed, fens.size());
          }
        }
      }

      final int exitCode = process.waitFor();
      if (exitCode != 0) {
        throw new IllegalStateException(
            "chasolver oracle runner exited with " + exitCode + ": " + readStream(errorStream));
      }
    }
    return result;
  }

  private static void validateResultLine(String resultLine) {
    final String[] itemArray = Nulls.split(resultLine, "\t");
    if (itemArray.length != 5) {
      throw new IllegalStateException("Invalid oracle TSV row: " + resultLine);
    }
    for (final String item : itemArray) {
      if ("PARSE_ERROR".equals(item)) {
        throw new IllegalStateException("chasolver runner could not parse a FEN (PARSE_ERROR): " + resultLine);
      }
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
