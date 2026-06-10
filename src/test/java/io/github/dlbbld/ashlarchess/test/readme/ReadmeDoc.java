// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.readme;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.dlbbld.ashlarchess.test.common.utility.FileUtility;

/**
 * Renders {@code README.md} from {@code README.template.md}. For each registered {@link ReadmeExamples} example the
 * template carries two placeholder lines - {@code <!-- readme:code id=ID -->} and {@code <!-- readme:output id=ID -->} -
 * which are replaced by, respectively, the verbatim source slice of the matching method (its {@code // <readme:ID>} ...
 * {@code // </readme:ID>} region, de-indented) wrapped in a {@code java} fence, and the output that method prints when
 * run, wrapped in a plain fence. Every other template line passes through unchanged.
 *
 * <p>
 * Because the shown code is sliced from compiled source and the shown output is captured from running it, a rendered
 * README is correct by construction. {@code TestReadmeUpToDate} pins the committed file to a fresh render; run
 * {@link GenerateReadme} to regenerate after editing the template or an example.
 *
 * <p>
 * Paths are relative to the module root (the working directory under Maven), matching the project's other generators.
 */
public final class ReadmeDoc {

  private static final Path TEMPLATE_PATH = Path.of("README.template.md");
  private static final Path README_PATH = Path.of("README.md");
  private static final Path EXAMPLES_SOURCE_PATH = Path
      .of("src/test/java/io/github/dlbbld/ashlarchess/test/readme/ReadmeExamples.java");

  private static final Pattern PLACEHOLDER = Pattern
      .compile("^\\s*<!--\\s*readme:(code|output)\\s+id=([A-Za-z0-9-]+)\\s*-->\\s*$");

  private ReadmeDoc() {
  }

  /** Renders the README as a list of lines (no trailing line terminators), ready to compare or write. */
  public static List<String> generate() {
    final Map<String, Runnable> examples = ReadmeExamples.examples();
    final List<String> sourceLines = FileUtility.readFileLines(EXAMPLES_SOURCE_PATH);

    final Map<String, List<String>> codeById = new LinkedHashMap<>();
    final Map<String, List<String>> outputById = new LinkedHashMap<>();
    for (final Map.Entry<String, Runnable> entry : examples.entrySet()) {
      final String id = entry.getKey();
      codeById.put(id, sliceSource(sourceLines, id));
      outputById.put(id, captureOutput(entry.getValue()));
    }

    final List<String> result = new ArrayList<>();
    for (final String line : FileUtility.readFileLines(TEMPLATE_PATH)) {
      final Matcher matcher = PLACEHOLDER.matcher(line);
      if (!matcher.matches()) {
        result.add(line);
        continue;
      }
      final String kind = matcher.group(1);
      final String id = matcher.group(2);
      if (kind == null || id == null) {
        throw new IllegalStateException("Malformed README placeholder: " + line);
      }
      if ("code".equals(kind)) {
        appendFenced(result, "```java", requireExample(codeById, id, "code"));
      } else {
        appendFenced(result, "```", requireExample(outputById, id, "output"));
      }
    }
    return result;
  }

  /** Renders and writes {@code README.md}. */
  public static void writeReadme() {
    FileUtility.writeFile(README_PATH, generate());
  }

  private static List<String> requireExample(Map<String, List<String>> byId, String id, String kind) {
    final List<String> block = byId.get(id);
    if (block == null) {
      throw new IllegalStateException(
          "README " + kind + " placeholder references id \"" + id + "\", which has no registered example.");
    }
    return block;
  }

  private static void appendFenced(List<String> result, String openingFence, List<String> body) {
    result.add(openingFence);
    result.addAll(body);
    result.add("```");
  }

  private static List<String> sliceSource(List<String> sourceLines, String id) {
    final String startMarker = "// <readme:" + id + ">";
    final String endMarker = "// </readme:" + id + ">";
    int start = -1;
    int end = -1;
    for (int i = 0; i < sourceLines.size(); i++) {
      final String trimmed = sourceLines.get(i).trim();
      if (trimmed.equals(startMarker)) {
        start = i;
      } else if (trimmed.equals(endMarker)) {
        end = i;
        break;
      }
    }
    if (start < 0 || end < 0 || end <= start) {
      throw new IllegalStateException(
          "Source markers // <readme:" + id + "> ... // </readme:" + id + "> not found in " + EXAMPLES_SOURCE_PATH);
    }
    return dedent(sourceLines.subList(start + 1, end));
  }

  private static List<String> dedent(List<String> lines) {
    int minIndent = Integer.MAX_VALUE;
    for (final String line : lines) {
      if (line.isBlank()) {
        continue;
      }
      minIndent = Math.min(minIndent, line.length() - line.stripLeading().length());
    }
    if (minIndent == Integer.MAX_VALUE) {
      minIndent = 0;
    }
    final List<String> result = new ArrayList<>();
    for (final String line : lines) {
      result.add(line.isBlank() ? "" : line.substring(minIndent));
    }
    return result;
  }

  private static List<String> captureOutput(Runnable body) {
    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    final PrintStream previous = System.out;
    final PrintStream capture = new PrintStream(buffer, true, StandardCharsets.UTF_8);
    try {
      System.setOut(capture);
      body.run();
    } finally {
      System.setOut(previous);
      capture.close();
    }
    return toLines(buffer.toString(StandardCharsets.UTF_8));
  }

  private static List<String> toLines(String text) {
    final List<String> lines = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
      String line = reader.readLine();
      while (line != null) {
        lines.add(line);
        line = reader.readLine();
      }
    } catch (final IOException ioe) {
      throw new IllegalStateException("Reading captured example output failed.", ioe);
    }
    return lines;
  }
}
