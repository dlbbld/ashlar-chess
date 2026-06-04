// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.oracle.python;

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
 * Reads a python-chess-generated PGN-import oracle JSONL file into {@link OracleRecord} values. One JSON object per
 * line; objects match the shape emitted by {@code src/test/python/generate_pgn_import_oracle.py}. JSON parsing is
 * delegated to the shared {@link JsonLineParser}.
 */
@SuppressWarnings("unchecked")
public final class OracleJsonlReader {

  private OracleJsonlReader() {
  }

  public static List<OracleRecord> readAll(Path jsonlPath) throws IOException {
    final List<OracleRecord> records = new ArrayList<>();
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

  private static OracleRecord toRecord(Map<String, Object> obj) {
    final String pgn = (String) obj.get("pgn");
    final String startFen = (String) obj.get("startFen");
    final String finalFen = (String) obj.get("finalFen");
    final List<Object> rawMoves = (List<Object>) obj.get("moves");
    final List<OracleMove> moves = new ArrayList<>(rawMoves.size());
    for (final Object raw : rawMoves) {
      moves.add(toMove((Map<String, Object>) raw));
    }
    return new OracleRecord(pgn, startFen, Nulls.copyOfList(moves), finalFen);
  }

  private static OracleMove toMove(Map<String, Object> obj) {
    return new OracleMove((String) obj.get("san"), (String) obj.get("lan"), (String) obj.get("uci"),
        (String) obj.get("fenAfter"), (Integer) obj.get("halfmoveClock"), (Integer) obj.get("fullmoveNumber"),
        (Boolean) obj.get("isCheck"), (Boolean) obj.get("isCheckmate"), (Boolean) obj.get("isStalemate"),
        (Boolean) obj.get("isInsufficientMaterial"), (Boolean) obj.get("hasInsufficientMaterialWhite"),
        (Boolean) obj.get("hasInsufficientMaterialBlack"), (Boolean) obj.get("isRepetition2"),
        (Boolean) obj.get("isRepetition3"), (Boolean) obj.get("isRepetition4"),
        (Boolean) obj.get("isFivefoldRepetition"), (Boolean) obj.get("isFiftyMoves"),
        (Boolean) obj.get("isSeventyFiveMoves"), (Boolean) obj.get("canClaimThreefold"),
        (Boolean) obj.get("canClaimFifty"));
  }
}
