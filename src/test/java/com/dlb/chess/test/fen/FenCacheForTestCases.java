package com.dlb.chess.test.fen;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import com.dlb.chess.fen.FenParserAdvanced;
import com.dlb.chess.fen.model.Fen;

/**
 * Process-lifetime cache for the parsed {@link Fen} of a test fixture's final position. The corpus row carries the
 * final position as a FEN string ({@code PgnFen.finalFen()}); every call to {@code PgnFen.finalPosition()}
 * otherwise re-runs {@link FenParserAdvanced#parseFenAdvanced(String)} to build the board. Caching by FEN string also
 * deduplicates fixtures that happen to share a final position.
 *
 * <p>
 * Sibling of {@code PgnCacheForLenientPgnParserTestCases} on the FEN side. {@link Fen} is an immutable record so the
 * cached value is safe to share across tests.
 */
public final class FenCacheForTestCases {

  private static final Map<String, Fen> FEN_CACHE = new HashMap<>();

  private FenCacheForTestCases() {
  }

  @SuppressWarnings({ "null", "unused" })
  public static Fen getFen(String fen) {
    final @Nullable Fen cached = FEN_CACHE.get(fen);
    if (cached != null) {
      return cached;
    }
    final Fen parsed = FenParserAdvanced.parseFenAdvanced(fen);
    FEN_CACHE.put(fen, parsed);
    return parsed;
  }
}
