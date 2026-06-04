// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.oracle.insufficientmaterial;

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
 * Reads an insufficient-material oracle JSONL file into {@link InsufficientMaterialRecord} values. One JSON object per
 * line; objects match the shape emitted by
 * {@code tools/scalachess-oracle/generate_insufficient_material_oracle.scala}. Provider-neutral by design: the schema is
 * engine-agnostic. JSON parsing is delegated to the shared {@link JsonLineParser}.
 */
@SuppressWarnings("unchecked")
public final class InsufficientMaterialJsonlReader {

  private InsufficientMaterialJsonlReader() {
  }

  public static List<InsufficientMaterialRecord> readAll(Path jsonlPath) throws IOException {
    final List<InsufficientMaterialRecord> records = new ArrayList<>();
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

  private static InsufficientMaterialRecord toRecord(Map<String, Object> obj) {
    return new InsufficientMaterialRecord((String) obj.get("pgn"), (String) obj.get("fen"),
        (Boolean) obj.get("isInsufficientMaterial"), (Boolean) obj.get("hasInsufficientMaterialWhite"),
        (Boolean) obj.get("hasInsufficientMaterialBlack"));
  }
}
