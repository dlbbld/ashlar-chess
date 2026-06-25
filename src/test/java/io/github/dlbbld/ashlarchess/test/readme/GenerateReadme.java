// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.readme;

/**
 * Regenerates the public Markdown docs from their templates and the {@link ReadmeExamples} methods. Run after editing a
 * template or an example:
 *
 * <pre>
 * mvn -q test-compile exec:java -Dexec.mainClass=io.github.dlbbld.ashlarchess.test.readme.GenerateReadme -Dexec.classpathScope=test
 * </pre>
 *
 * {@code TestReadmeUpToDate} fails the build if a committed document differs from a fresh render.
 */
public final class GenerateReadme {

  private GenerateReadme() {
  }

  public static void main(String[] args) {
    ReadmeDoc.writeDocs();
    System.out.println("README.md and manual.md regenerated from their templates.");
  }
}
