package io.github.dlbbld.ashlarchess.pgn;

/** Lexical categories the sequential PGN tokenizer can emit. */
enum PgnTokenType {

  TAG_BRACKET_OPEN,
  TAG_BRACKET_CLOSE,

  /** Unescaped tag value, without the surrounding quotes. */
  TAG_VALUE_STRING,

  TAG_VALUE_STRING_UNTERMINATED,

  /** Run of space and tab characters inside a line. */
  SPACES,

  NEWLINE,

  /**
   * e.g. {@code 1.} or {@code 12.}.
   */
  MOVE_NUMBER_WHITE,

  /**
   * e.g. {@code 1...} or {@code 12...}.
   */
  MOVE_NUMBER_BLACK,

  /**
   * Generic PGN symbol token - letters, digits, and the continuation punctuation set. The parser decides by context
   * whether a given symbol is a tag name, a SAN half-move, or something else.
   */
  SYMBOL,

  /**
   * {@code !}, {@code !!}, {@code ?}, {@code ??}, {@code !?}, {@code ?!}.
   */
  MOVE_SUFFIX_ANNOTATION,

  /**
   * Brace-delimited commentary {@code {...}}. The token text is the commentary content without the braces.
   */
  BRACE_COMMENT,

  /**
   * Opening {@code {} with no
    *  matching {@code }} before EOF. The token text holds the partial content.
   */
  BRACE_COMMENT_UNCLOSED,

  /**
   * Closing {@code }} outside any open commentary.
   */
  BRACE_STRAY_CLOSE,

  /**
   * {@code 1-0}, {@code 0-1}, {@code 1/2-1/2}, {@code *}.
   */
  TERMINATION_MARKER,

  EOF
}
