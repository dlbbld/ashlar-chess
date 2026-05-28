package io.github.dlbbld.ashlarchess.test.pgn.parser;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParser;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

public class PgnCacheForLenientPgnParserTestCases {
  private static final Map<String, PgnGame> PGN_CACHE = new HashMap<>();

  public static PgnGame getPgn(Path pgnFolderPath, String pgnName) {
    final Path pgnPath = Nulls.pathResolve(pgnFolderPath, pgnName);
    if (PgnTest.existsFolderPath(pgnFolderPath)) {
      @SuppressWarnings("null") final @NonNull String key = pgnPath.toAbsolutePath().toString();
      if (PGN_CACHE.containsKey(key)) {
        @SuppressWarnings("null") final PgnGame pgnGame = PGN_CACHE.get(key);
        return pgnGame;
      }
      final PgnGame pgnGame = LenientPgnParser.parse(pgnFolderPath, pgnName);
      PGN_CACHE.put(key, pgnGame);
      return pgnGame;
    }
    // other PGN's we are not caching
    return LenientPgnParser.parse(pgnFolderPath, pgnName);

  }
}
