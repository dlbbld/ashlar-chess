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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.dlbbld.ashlarchess.test.common.utility.FileUtility;

/**
 * Renders {@code README.md} from {@code README.template.md}. For each registered {@link ReadmeExample} the template
 * carries placeholder lines that are replaced by generated content; every other template line passes through verbatim:
 *
 * <ul>
 * <li>{@code <!-- readme:code id=ID -->} - the verbatim source slice of the example's {@code // <readme:ID>} ...
 * {@code // </readme:ID>} region (de-indented), wrapped in a {@code java} fence. Within the slice, each
 * {@code [out]} marker is replaced by the next line the example printed, so short results show inline (any trailing
 * gloss text after the marker, e.g. {@code (dead)}, is preserved).</li>
 * <li>{@code <!-- readme:output id=ID -->} - the example's remaining captured output (lines not consumed by an inline
 * {@code [out]} marker), wrapped in a plain fence. Used for multi-line outputs such as a printed report or PGN.</li>
 * </ul>
 *
 * <p>
 * Because the shown code is sliced from compiled source and the shown output is captured from running it, a rendered
 * README is correct by construction. {@code TestReadmeUpToDate} pins the committed file to a fresh render; run
 * {@link GenerateReadme} to regenerate after editing the template or an example. Paths are relative to the module root
 * (the working directory under Maven).
 */
public final class ReadmeDoc {

  private static final Path TEMPLATE_PATH = Path.of("README.template.md");
  private static final Path README_PATH = Path.of("README.md");
  private static final Path EXAMPLES_SOURCE_PATH = Path
      .of("src/test/java/io/github/dlbbld/ashlarchess/test/readme/ReadmeExamples.java");

  private static final Pattern PLACEHOLDER = Pattern
      .compile("^\\s*<!--\\s*readme:(code|output)\\s+id=([A-Za-z0-9-]+)\\s*-->\\s*$");

  /** Inline marker in an example, replaced by the next captured output line (any trailing gloss text is kept). */
  private static final String OUT_TOKEN = "[out]";

  /** Volatile output (today's date in a generated PGN) is normalised so renders are reproducible. */
  private static final Pattern DATE_TAG = Pattern.compile("\\[Date \"\\d{4}\\.\\d{2}\\.\\d{2}\"\\]");

  private ReadmeDoc() {
  }

  /** Renders the README as a list of lines (no trailing line terminators), ready to compare or write. */
  public static List<String> generate() {
    final List<String> sourceLines = FileUtility.readFileLines(EXAMPLES_SOURCE_PATH);
    final List<String> templateLines = FileUtility.readFileLines(TEMPLATE_PATH);
    final Set<String> outputPlaceholderIds = collectOutputPlaceholderIds(templateLines);

    final Map<String, List<String>> codeById = new LinkedHashMap<>();
    final Map<String, List<String>> outputById = new LinkedHashMap<>();
    for (final ReadmeExample example : ReadmeExamples.examples()) {
      final String id = example.id();
      final List<String> captured = example.run()
          ? stripTrailingBlanks(normalizeVolatile(captureOutput(example.body())))
          : new ArrayList<String>();
      final Deque<String> remaining = new ArrayDeque<>(captured);
      codeById.put(id, substituteInlineOutputs(sliceSource(sourceLines, id), remaining, id));
      final List<String> blockOutput = new ArrayList<>(remaining);
      if (!blockOutput.isEmpty() && !outputPlaceholderIds.contains(id)) {
        throw new IllegalStateException("README example \"" + id
            + "\" produced output with no " + OUT_TOKEN + " marker and no output placeholder to show it.");
      }
      outputById.put(id, blockOutput);
    }

    final List<String> result = new ArrayList<>();
    for (final String line : templateLines) {
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

  private static Set<String> collectOutputPlaceholderIds(List<String> templateLines) {
    final Set<String> ids = new HashSet<>();
    for (final String line : templateLines) {
      final Matcher matcher = PLACEHOLDER.matcher(line);
      if (matcher.matches() && "output".equals(matcher.group(1))) {
        final String id = matcher.group(2);
        if (id != null) {
          ids.add(id);
        }
      }
    }
    return ids;
  }

  private static List<String> substituteInlineOutputs(List<String> code, Deque<String> remaining, String id) {
    final List<String> result = new ArrayList<>();
    for (final String line : code) {
      final int index = line.indexOf(OUT_TOKEN);
      if (index < 0) {
        result.add(line);
        continue;
      }
      final String value = remaining.poll();
      if (value == null) {
        throw new IllegalStateException(
            "README example \"" + id + "\" has more " + OUT_TOKEN + " markers than printed output lines.");
      }
      result.add(line.substring(0, index) + value + line.substring(index + OUT_TOKEN.length()));
    }
    return result;
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

  private static List<String> stripTrailingBlanks(List<String> lines) {
    final List<String> result = new ArrayList<>(lines);
    while (!result.isEmpty() && result.get(result.size() - 1).isBlank()) {
      result.remove(result.size() - 1);
    }
    return result;
  }

  private static List<String> normalizeVolatile(List<String> lines) {
    final List<String> result = new ArrayList<>();
    for (final String line : lines) {
      result.add(DATE_TAG.matcher(line).replaceAll(Matcher.quoteReplacement("[Date \"<today>\"]")));
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
