package com.dlb.chess.test.oracle.python;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dlb.chess.common.Nulls;

/**
 * Reads a python-chess-generated move-generation oracle JSONL file into {@link LegalMovesRecord} values. One JSON
 * object per line; objects match the shape emitted by {@code src/test/python/generate_move_gen_oracle.py}.
 *
 * <p>
 * Reuses the JSON parser embedded in {@link OracleJsonlReader} via the package-visible
 * {@link OracleJsonlReader#parseLineToObject(String)} hook.
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
        records.add(toRecord(OracleJsonlReader.parseLineToObject(line)));
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
