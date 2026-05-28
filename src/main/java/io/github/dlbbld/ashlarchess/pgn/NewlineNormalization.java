package io.github.dlbbld.ashlarchess.pgn;

import io.github.dlbbld.ashlarchess.common.Nulls;

/** Normalises CRLF and lone CR to LF - see specification.md (Newline handling). */
final class NewlineNormalization {

  private NewlineNormalization() {
  }

  /**
   * Returns {@code source} unchanged if it has no {@code \r}; otherwise returns a copy with every CR/CRLF -> LF.
   */
  public static String toLf(String source) {
    if (source.indexOf('\r') < 0) {
      return source;
    }
    final StringBuilder result = new StringBuilder(source.length());
    final int len = source.length();
    for (int i = 0; i < len; i++) {
      final char c = source.charAt(i);
      if (c == '\r') {
        result.append('\n');
        // Skip the LF half of a CRLF pair.
        if (i + 1 < len && source.charAt(i + 1) == '\n') {
          i++;
        }
      } else {
        result.append(c);
      }
    }
    return Nulls.toString(result);
  }
}
