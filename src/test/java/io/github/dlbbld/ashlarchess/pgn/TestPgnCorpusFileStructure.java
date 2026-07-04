// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.pgn;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.test.pgntest.constants.PgnTestConstants;

/**
 * Cheap structural lint over the whole strict PGN corpus, in the <em>default</em> profile. Every file under
 * {@code src/test/resources/pgn/} must pass {@link StrictFileStructurePreScan} (the exactly-two-empty-lines rule and
 * friends) - the corpus is strict-format by definition.
 *
 * <p>
 * Motivation (22.0.0 pre-flight): a fixture adopted into the corpus ending {@code *\n} instead of {@code *\n\n} stayed
 * invisible for a full day because every test that strict-parses the corpus lives in the {@code -Pfull} profile, and
 * surfaced only at release pre-flight as 1 failure + 5 errors. Structure validation is O(file length) - the whole
 * corpus costs a second or two - so it belongs in the default profile and fails the same commit that adds a malformed
 * file. The expensive semantic sweeps (replay, oracle comparisons) stay in {@code -Pfull}.
 *
 * <p>
 * Lives in the {@code pgn} package (not {@code test.pgn}) because {@link StrictFileStructurePreScan} is deliberately
 * package-private: the lint checks file structure only, mirroring exactly what {@code StrictPgnParser.parseInternal}
 * feeds it (LF-normalised source, no BOM strip - strict rejects BOMs by design).
 */
class TestPgnCorpusFileStructure {

  @SuppressWarnings("static-method")
  @Test
  void everyCorpusFilePassesTheStrictStructurePreScan() throws IOException {
    final Path corpusRoot = PgnTestConstants.PGN_TEST_ROOT_FOLDER_PATH;
    final List<String> failures = new ArrayList<>();
    int fileCount = 0;

    try (Stream<Path> paths = Files.walk(corpusRoot)) {
      final List<Path> pgnFiles = paths.filter(p -> Nulls.toString(p).endsWith(".pgn")).sorted().toList();
      for (final Path pgnFile : pgnFiles) {
        fileCount++;
        final String source = new String(Files.readAllBytes(pgnFile), StandardCharsets.UTF_8);
        try {
          StrictFileStructurePreScan.validate(NewlineNormalization.toLf(source));
        } catch (final StrictPgnParserValidationException e) {
          failures.add(Nulls.toString(corpusRoot.relativize(pgnFile)) + "  -  " + e.getMessage());
        }
      }
    }

    if (fileCount == 0) {
      fail("No corpus files found under " + corpusRoot + " - corpus root wiring is broken");
    }
    if (!failures.isEmpty()) {
      final StringBuilder message = new StringBuilder();
      message.append(failures.size()).append(" of ").append(fileCount)
          .append(" corpus PGN files fail the strict file-structure pre-scan:\n");
      for (final String failure : failures) {
        message.append("  ").append(failure).append('\n');
      }
      fail(Nulls.toString(message));
    }
  }
}
