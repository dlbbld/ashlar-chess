package io.github.dlbbld.ashlarchess.test.pgn.parser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParser;

public class PgnCacheForStrictPgnParserTestCases {
  private static final Map<String, PgnGame> PGN_CACHE = new HashMap<>();

  public static PgnGame getPgn(Path pgnFolderPath, String pgnName) {
    // Validate the folder path
    if (!Files.isDirectory(pgnFolderPath)) {
      throw new IllegalArgumentException("Invalid folder path: " + pgnFolderPath);
    }

    // Construct the full path to the PGN file
    final Path pgnPath = Nulls.pathResolve(pgnFolderPath, pgnName);

    // Convert to a canonical String for cache lookups
    @SuppressWarnings("null") final @NonNull String key = pgnPath.toAbsolutePath().toString();

    // Check the cache
    if (PGN_CACHE.containsKey(key)) {
      @SuppressWarnings("null") final PgnGame pgnGame = PGN_CACHE.get(key);
      return pgnGame;
    }

    // Not in cache; read it from disk and store
    final PgnGame pgnGame = StrictPgnParser.parse(pgnFolderPath, pgnName);
    PGN_CACHE.put(key, pgnGame);

    return pgnGame;
  }

}
