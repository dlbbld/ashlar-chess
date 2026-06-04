// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.oracle;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.dlbbld.ashlarchess.common.Nulls;

/**
 * Minimal, provider-neutral JSONL line parser shared by the oracle readers (python-chess, scalachess). One JSON object
 * per line. Covers exactly the value types the oracle generators emit - strings, integers, booleans, arrays, objects -
 * and is intended for the test path only; do not generalise it.
 */
@SuppressWarnings("unchecked")
public final class JsonLineParser {

  private JsonLineParser() {
  }

  /**
   * Parse one JSONL line into a fresh {@code LinkedHashMap}.
   */
  public static Map<String, Object> parseLineToObject(String text) {
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
    final char ch = c.peek();
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
      final char sep = c.peek();
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
      final char sep = c.peek();
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
      final char ch = c.next();
      if (ch == '"') {
        return Nulls.toString(sb);
      }
      if (ch == '\\') {
        final char esc = c.next();
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
            final int code = Integer.parseInt(c.take(4), 16);
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
    final int start = c.position();
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
      final char ch = peek();
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
        final char ch = text.charAt(pos);
        if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
          pos++;
          continue;
        }
        return;
      }
    }
  }
}
