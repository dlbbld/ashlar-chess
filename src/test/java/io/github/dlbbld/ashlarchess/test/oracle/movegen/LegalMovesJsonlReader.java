// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.oracle.movegen;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.test.oracle.JsonLineParser;

/**
 * Reads a move-generation oracle JSONL file into {@link LegalMovesRecord} values. One JSON object per line; objects
 * match the shape emitted by the move-generation oracle generators - {@code src/test/python/generate_move_gen_oracle.py}
 * (python-chess) and {@code tools/scalachess-oracle/generate_legal_moves_oracle.scala} (scalachess), which share this
 * schema. Provider-neutral by design: the same reader serves both oracles, differing only in the JSONL root path.
 *
 * <p>
 * JSON parsing is delegated to the shared {@link JsonLineParser}.
 */
@SuppressWarnings("unchecked")
public final class LegalMovesJsonlReader {

  private LegalMovesJsonlReader() {
  }

  public static List<LegalMovesRecord> readAll(Path jsonlPath) throws IOException {
    final List<LegalMovesRecord> records = new ArrayList<>();
    try (BufferedReader reader = Files.newBufferedReader(jsonlPath, StandardCharsets.UTF_8)) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.isBlank()) {
          continue;
        }
        records.add(toRecord(JsonLineParser.parseLineToObject(line)));
      }
    }
    return Nulls.copyOfList(records);
  }

  private static LegalMovesRecord toRecord(Map<String, Object> obj) {
    final String pgn = (String) obj.get("pgn");
    final List<Object> rawPlies = (List<Object>) obj.get("perPly");
    final List<LegalMovesPly> perPly = new ArrayList<>(rawPlies.size());
    for (final Object raw : rawPlies) {
      perPly.add(toPly((Map<String, Object>) raw));
    }
    return new LegalMovesRecord(pgn, Nulls.copyOfList(perPly));
  }

  private static LegalMovesPly toPly(Map<String, Object> obj) {
    final List<Object> rawList = (List<Object>) obj.get("legalMovesUci");
    final List<String> uci = new ArrayList<>(rawList.size());
    for (final Object o : rawList) {
      uci.add((String) o);
    }
    return new LegalMovesPly(Nulls.copyOfList(uci));
  }
}
