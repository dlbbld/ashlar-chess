// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.readme;

/**
 * Regenerates {@code README.md} from {@code README.template.md} and the {@link ReadmeExamples} methods. Run after
 * editing the template or an example:
 *
 * <pre>
 * mvn -q exec:java -Dexec.mainClass=io.github.dlbbld.ashlarchess.test.readme.GenerateReadme -Dexec.classpathScope=test
 * </pre>
 *
 * {@code TestReadmeUpToDate} fails the build if the committed README differs from a fresh render.
 */
public final class GenerateReadme {

  private GenerateReadme() {
  }

  public static void main(String[] args) {
    ReadmeDoc.writeReadme();
    System.out.println("README.md regenerated from README.template.md.");
  }
}
