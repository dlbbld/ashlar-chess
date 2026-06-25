// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.common.utility;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import io.github.dlbbld.ashlarchess.common.utility.IoUtility;
import io.github.dlbbld.ashlarchess.internal.Nulls;

public final class OutputCaptureUtility {

  private OutputCaptureUtility() {
  }

  public static String captureStdout(Runnable action) {
    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    final PrintStream original = System.out;
    try (PrintStream captured = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
      System.setOut(captured);
      action.run();
    } finally {
      System.setOut(original);
    }
    return IoUtility.toString(buffer);
  }

  public static List<String> captureStdoutLines(Runnable action) {
    return lines(captureStdout(action));
  }

  public static List<String> lines(String text) {
    return Nulls.copyOfList(Nulls.asList(Nulls.split(normaliseLineEndings(text), "\n")));
  }

  public static String normaliseLineEndings(String text) {
    return Nulls.replace(text, "\r\n", "\n");
  }

}
