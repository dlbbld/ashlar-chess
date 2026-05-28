package io.github.dlbbld.ashlarchess.pgn;

import org.eclipse.jdt.annotation.Nullable;

import io.github.dlbbld.ashlarchess.common.Nulls;

/**
 * Pull-based PGN tokenizer. Emits tokens for every construct the grammar cares about; strict-specific inter-token
 * constraints (exact single spacing, required move numbers, termination presence) are enforced by the parser, not here.
 */
final class PgnTokenizer {

  private static final int CHAR_EOF = PgnCharStream.EOF;

  private final PgnCharStream stream;

  // Lazy peek slots. Captured into locals before use so JDT can narrow them to @NonNull before they escape.
  private @Nullable PgnToken peekedAt0;
  private @Nullable PgnToken peekedAt1;

  public PgnTokenizer(PgnCharStream stream) {
    this.stream = stream;
  }

  public PgnToken peek() {
    PgnToken result = peekedAt0;
    if (result == null) {
      result = readNext();
      peekedAt0 = result;
    }
    return result;
  }

  /**
   * One-token lookahead past {@link #peek()}.
   */
  public PgnToken peekNext() {
    if (peekedAt0 == null) {
      peekedAt0 = readNext();
    }
    PgnToken result = peekedAt1;
    if (result == null) {
      result = readNext();
      peekedAt1 = result;
    }
    return result;
  }

  public PgnToken next() {
    final PgnToken front = peekedAt0;
    if (front != null) {
      peekedAt0 = peekedAt1;
      peekedAt1 = null;
      return front;
    }
    return readNext();
  }

  private PgnToken readNext() {
    if (stream.isEof()) {
      return new PgnToken(PgnTokenType.EOF, "", stream.line(), stream.column());
    }

    final int line = stream.line();
    final int column = stream.column();
    final int c = stream.peek();

    switch (c) {
      case '\r':
      case '\n':
        return readNewline(line, column);
      case ' ':
      case '\t':
        return readSpaces(line, column);
      case '[':
        stream.read();
        return new PgnToken(PgnTokenType.TAG_BRACKET_OPEN, "[", line, column);
      case ']':
        stream.read();
        return new PgnToken(PgnTokenType.TAG_BRACKET_CLOSE, "]", line, column);
      case '"':
        return readTagValueString(line, column);
      case '{':
        return readBraceComment(line, column);
      case '}':
        stream.read();
        return new PgnToken(PgnTokenType.BRACE_STRAY_CLOSE, "}", line, column);
      case '!':
      case '?':
        return readMoveSuffixAnnotation(line, column);
      case '*':
        stream.read();
        return new PgnToken(PgnTokenType.TERMINATION_MARKER, "*", line, column);
      default:
        break;
    }
    if (isAsciiDigit(c)) {
      return readDigitStarted(line, column);
    }
    return readSymbol(line, column);
  }

  private PgnToken readNewline(int line, int column) {
    final int first = stream.read();
    final StringBuilder text = new StringBuilder();
    text.append((char) first);
    if (first == '\r' && stream.peek() == '\n') {
      text.append((char) stream.read());
    }
    return new PgnToken(PgnTokenType.NEWLINE, Nulls.toString(text), line, column);
  }

  private PgnToken readSpaces(int line, int column) {
    final StringBuilder text = new StringBuilder();
    while (stream.peek() == ' ' || stream.peek() == '\t') {
      text.append((char) stream.read());
    }
    return new PgnToken(PgnTokenType.SPACES, Nulls.toString(text), line, column);
  }

  private PgnToken readTagValueString(int line, int column) {
    stream.read(); // opening quote
    final StringBuilder text = new StringBuilder();
    boolean terminated = false;
    while (true) {
      final int c = stream.peek();
      if (c == CHAR_EOF || c == '\n' || c == '\r') {
        break;
      }
      if (c == '"') {
        stream.read();
        terminated = true;
        break;
      }
      if (c == '\\') {
        stream.read();
        final int escaped = stream.peek();
        if (escaped == '\\' || escaped == '"') {
          text.append((char) stream.read());
        } else {
          text.append('\\');
        }
        continue;
      }
      text.append((char) stream.read());
    }
    return new PgnToken(terminated ? PgnTokenType.TAG_VALUE_STRING : PgnTokenType.TAG_VALUE_STRING_UNTERMINATED,
        Nulls.toString(text), line, column);
  }

  private PgnToken readBraceComment(int line, int column) {
    stream.read(); // opening brace
    final StringBuilder text = new StringBuilder();
    while (true) {
      final int c = stream.peek();
      if (c == CHAR_EOF) {
        return new PgnToken(PgnTokenType.BRACE_COMMENT_UNCLOSED, Nulls.toString(text), line, column);
      }
      if (c == '}') {
        stream.read();
        return new PgnToken(PgnTokenType.BRACE_COMMENT, Nulls.toString(text), line, column);
      }
      // PGN spec section 8.2.5: an inner `{` is content, not a nested-comment opener. Only `}` closes a comment.
      text.append((char) stream.read());
    }
  }

  private PgnToken readMoveSuffixAnnotation(int line, int column) {
    // Coalesce consecutive ! / ? into one token so over-long runs (`!!!`, `!?!`) surface as
    // MOVETEXT_MOVE_SUFFIX_ANNOTATION_INVALID rather than misdiagnosing as a spacing error.
    final StringBuilder text = new StringBuilder();
    while (true) {
      final int c = stream.peek();
      if (c != '!' && c != '?') {
        break;
      }
      text.append((char) stream.read());
    }
    return new PgnToken(PgnTokenType.MOVE_SUFFIX_ANNOTATION, Nulls.toString(text), line, column);
  }

  private PgnToken readDigitStarted(int line, int column) {
    final StringBuilder text = new StringBuilder();
    while (isAsciiDigit(stream.peek())) {
      text.append((char) stream.read());
    }
    final int next = stream.peek();
    if (next == '.') {
      return readMoveNumberDots(line, column, text);
    }
    if (next == '-' || next == '/') {
      return readTerminationAfterDigits(line, column, text);
    }
    return new PgnToken(PgnTokenType.SYMBOL, Nulls.toString(text), line, column);
  }

  private PgnToken readMoveNumberDots(int line, int column, StringBuilder digits) {
    final StringBuilder text = new StringBuilder(digits);
    int dotCount = 0;
    while (stream.peek() == '.') {
      text.append((char) stream.read());
      dotCount++;
    }
    if (dotCount >= 3) {
      return new PgnToken(PgnTokenType.MOVE_NUMBER_BLACK, Nulls.toString(text), line, column);
    }
    return new PgnToken(PgnTokenType.MOVE_NUMBER_WHITE, Nulls.toString(text), line, column);
  }

  private PgnToken readTerminationAfterDigits(int line, int column, StringBuilder digits) {
    final StringBuilder text = new StringBuilder(digits);
    while (true) {
      final int c = stream.peek();
      if (c != '-' && c != '/' && !isAsciiDigit(c)) {
        break;
      }
      text.append((char) stream.read());
    }
    final String result = Nulls.toString(text);
    // Lenient castling with digit zero ("0-0", "0-0-0") matches the digits-and-hyphens shape but is a SAN move,
    // not a termination marker. The lenient SAN layer translates these to "O-O" / "O-O-O".
    if ("0-0".equals(result) || "0-0-0".equals(result)) {
      return new PgnToken(PgnTokenType.SYMBOL, result, line, column);
    }
    return new PgnToken(PgnTokenType.TERMINATION_MARKER, result, line, column);
  }

  private PgnToken readSymbol(int line, int column) {
    final StringBuilder text = new StringBuilder();
    while (true) {
      final int c = stream.peek();
      if (c == CHAR_EOF || isWordBreak(c)) {
        break;
      }
      text.append((char) stream.read());
    }
    if (text.length() == 0) {
      // Unrecognised single character - consume it so we always make progress. The parser surfaces the rejection.
      text.append((char) stream.read());
    }
    return new PgnToken(PgnTokenType.SYMBOL, Nulls.toString(text), line, column);
  }

  private static boolean isWordBreak(int c) {
    return c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '[' || c == ']' || c == '{' || c == '}' || c == '"'
        || c == '!' || c == '?';
  }

  private static boolean isAsciiDigit(int c) {
    return c >= '0' && c <= '9';
  }
}
