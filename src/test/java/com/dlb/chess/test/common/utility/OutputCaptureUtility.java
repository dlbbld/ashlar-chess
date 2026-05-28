package com.dlb.chess.test.common.utility;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import com.dlb.chess.common.Nulls;
import com.google.common.collect.ImmutableList;

public abstract class OutputCaptureUtility {

  public static String captureStdout(Runnable action) {
    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    final PrintStream original = System.out;
    try (PrintStream captured = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
      System.setOut(captured);
      action.run();
    } finally {
      System.setOut(original);
    }
    return utf8String(buffer);
  }

  public static ImmutableList<String> captureStdoutLines(Runnable action) {
    return lines(captureStdout(action));
  }

  public static ImmutableList<String> lines(String text) {
    return Nulls.copyOfList(Nulls.asList(Nulls.split(normaliseLineEndings(text), "\n")));
  }

  public static String normaliseLineEndings(String text) {
    return Nulls.replace(text, "\r\n", "\n");
  }

  @SuppressWarnings("null")
  private static String utf8String(ByteArrayOutputStream buffer) {
    return buffer.toString(StandardCharsets.UTF_8);
  }
}
