package com.dlb.chess.test.oracle.python;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dlb.chess.common.Nulls;

/**
 * Reads a python-chess-generated oracle JSONL file into {@link OracleRecord} values. One JSON object per line; objects
 * match the shape emitted by {@code src/test/python/generate_parser_fen_mechanics_oracle.py}. The embedded JSON parser
 * covers exactly the value types that script emits - strings, integers, booleans, arrays, objects - and is intended for
 * the test path only; do not generalise it.
 */
@SuppressWarnings("unchecked")
public final class OracleJsonlReader {

  private OracleJsonlReader() {
  }

  public static List<OracleRecord> readAll(Path jsonlPath) throws IOException {
    final List<OracleRecord> records = new ArrayList<>();
    try (var reader = Files.newBufferedReader(jsonlPath, StandardCharsets.UTF_8)) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.isBlank()) {
          continue;
        }
        records.add(toRecord((Map<String, Object>) parseValue(line)));
      }
    }
    return Nulls.copyOfList(records);
  }

  private static OracleRecord toRecord(Map<String, Object> obj) {
    final var pgn = (String) obj.get("pgn");
    final var startFen = (String) obj.get("startFen");
    final var finalFen = (String) obj.get("finalFen");
    final var rawMoves = (List<Object>) obj.get("moves");
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

  /**
   * Package-visible: parse one JSONL line into a fresh {@code LinkedHashMap}. Used by sibling oracle readers.
   */
  static Map<String, Object> parseLineToObject(String text) {
    return (Map<String, Object>) parseValue(text);
  }

  private static Object parseValue(String text) {
    final Cursor c = new Cursor(text);
    c.skipWhitespace();
    final Object value = parseValue(c);
    c.skipWhitespace();
    if (c.hasMore()) {
      throw new IllegalArgumentException("trailing characters at position " + c.position() + " in: " + text);
    }
    return value;
  }

  private static Object parseValue(Cursor c) {
    c.skipWhitespace();
    final var ch = c.peek();
    return switch (ch) {
      case '{' -> parseObject(c);
      case '[' -> parseArray(c);
      case '"' -> parseString(c);
      case 't', 'f' -> parseBoolean(c);
      case '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> parseInteger(c);
      default -> throw new IllegalArgumentException("unexpected character '" + ch + "' at position " + c.position());
    };
  }

  private static Map<String, Object> parseObject(Cursor c) {
    c.expect('{');
    c.skipWhitespace();
    final Map<String, Object> out = new LinkedHashMap<>();
    if (c.peek() == '}') {
      c.advance();
      return out;
    }
    while (true) {
      c.skipWhitespace();
      final String key = parseString(c);
      c.skipWhitespace();
      c.expect(':');
      final Object value = parseValue(c);
      out.put(key, value);
      c.skipWhitespace();
      final var sep = c.peek();
      if (sep == ',') {
        c.advance();
        continue;
      }
      if (sep == '}') {
        c.advance();
        return out;
      }
      throw new IllegalArgumentException("expected ',' or '}' at position " + c.position());
    }
  }

  private static List<Object> parseArray(Cursor c) {
    c.expect('[');
    c.skipWhitespace();
    final List<Object> out = new ArrayList<>();
    if (c.peek() == ']') {
      c.advance();
      return out;
    }
    while (true) {
      out.add(parseValue(c));
      c.skipWhitespace();
      final var sep = c.peek();
      if (sep == ',') {
        c.advance();
        continue;
      }
      if (sep == ']') {
        c.advance();
        return out;
      }
      throw new IllegalArgumentException("expected ',' or ']' at position " + c.position());
    }
  }

  private static String parseString(Cursor c) {
    c.expect('"');
    final StringBuilder sb = new StringBuilder();
    while (true) {
      final var ch = c.next();
      if (ch == '"') {
        return Nulls.toString(sb);
      }
      if (ch == '\\') {
        final var esc = c.next();
        switch (esc) {
          case '"' -> sb.append('"');
          case '\\' -> sb.append('\\');
          case '/' -> sb.append('/');
          case 'b' -> sb.append('\b');
          case 'f' -> sb.append('\f');
          case 'n' -> sb.append('\n');
          case 'r' -> sb.append('\r');
          case 't' -> sb.append('\t');
          case 'u' -> {
            final var code = Integer.parseInt(c.take(4), 16);
            sb.append((char) code);
          }
          default -> throw new IllegalArgumentException("bad escape '\\" + esc + "' at position " + c.position());
        }
        continue;
      }
      sb.append(ch);
    }
  }

  private static Boolean parseBoolean(Cursor c) {
    if (c.peek() == 't') {
      c.expectLiteral("true");
      return true;
    }
    c.expectLiteral("false");
    return false;
  }

  private static Integer parseInteger(Cursor c) {
    final var start = c.position();
    if (c.peek() == '-') {
      c.advance();
    }
    while (c.hasMore() && c.peek() >= '0' && c.peek() <= '9') {
      c.advance();
    }
    return Integer.parseInt(c.substring(start));
  }

  private static final class Cursor {

    private final String text;
    private int pos;

    Cursor(String text) {
      this.text = text;
      this.pos = 0;
    }

    int position() {
      return pos;
    }

    boolean hasMore() {
      return pos < text.length();
    }

    char peek() {
      if (pos >= text.length()) {
        throw new IllegalArgumentException("unexpected end of input");
      }
      return text.charAt(pos);
    }

    char next() {
      final var ch = peek();
      pos++;
      return ch;
    }

    void advance() {
      pos++;
    }

    void expect(char ch) {
      if (next() != ch) {
        throw new IllegalArgumentException("expected '" + ch + "' at position " + (pos - 1));
      }
    }

    void expectLiteral(String literal) {
      final String taken = take(literal.length());
      if (!taken.equals(literal)) {
        throw new IllegalArgumentException("expected '" + literal + "' at position " + (pos - literal.length()));
      }
    }

    String take(int count) {
      final String taken = Nulls.substring(text, pos, pos + count);
      pos += count;
      return taken;
    }

    String substring(int from) {
      return Nulls.substring(text, from, pos);
    }

    void skipWhitespace() {
      while (pos < text.length()) {
        final var ch = text.charAt(pos);
        if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
          pos++;
          continue;
        }
        return;
      }
    }
  }
}
