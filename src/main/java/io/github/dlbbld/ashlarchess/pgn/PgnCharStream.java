package io.github.dlbbld.ashlarchess.pgn;

/**
 * Forward-only character stream with one-based line/column tracking for error messages. Newline normalisation (CRLF/CR
 * -> LF) is the parser's responsibility - see {@code NewlineNormalization}.
 */
final class PgnCharStream {

  public static final int EOF = -1;

  private static final char LINE_FEED = '\n';
  private static final char CARRIAGE_RETURN = '\r';

  private final String source;

  private int index;
  private int line = 1;
  private int column = 1;

  public PgnCharStream(String source) {
    this.source = source;
  }

  public int peek() {
    return peek(0);
  }

  public int peek(int offset) {
    final int position = index + offset;
    if (position >= source.length()) {
      return EOF;
    }
    return source.charAt(position);
  }

  public int read() {
    if (index >= source.length()) {
      return EOF;
    }
    final char c = source.charAt(index);
    index++;
    if (c == LINE_FEED) {
      line++;
      column = 1;
    } else if (c == CARRIAGE_RETURN) {
      // Defensive - parsers normalise CR/CRLF to LF before construction, so this branch is normally unreached.
      if (peek() != LINE_FEED) {
        line++;
        column = 1;
      }
    } else {
      column++;
    }
    return c;
  }

  public boolean isEof() {
    return index >= source.length();
  }

  public boolean atLineStart() {
    return column == 1;
  }

  public int line() {
    return line;
  }

  public int column() {
    return column;
  }

  public int index() {
    return index;
  }
}
