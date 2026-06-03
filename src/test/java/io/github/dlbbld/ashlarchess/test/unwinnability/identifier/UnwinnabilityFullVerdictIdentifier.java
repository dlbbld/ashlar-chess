// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability.identifier;

import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityFullVerdict;

/**
 * Test-side mapping between {@link UnwinnabilityFullVerdict} values and the lowercase string identifiers emitted by
 * Ambrona's CHA C binary ({@code "winnable"}, {@code "unwinnable"}, {@code "undetermined"}). Used only when reading raw
 * CHA output for cross-checks against the Java port; not part of the production API.
 */
public final class UnwinnabilityFullVerdictIdentifier {

  private UnwinnabilityFullVerdictIdentifier() {
  }

  public static String getIdentifier(UnwinnabilityFullVerdict verdict) {
    return switch (verdict) {
      case WINNABLE_HELPMATE, WINNABLE_BY_THEOREM -> "winnable";
      case UNWINNABLE -> "unwinnable";
      case UNDETERMINED -> "undetermined";
    };
  }

  public static boolean exists(String identifier) {
    for (final UnwinnabilityFullVerdict verdict : UnwinnabilityFullVerdict.values()) {
      if (getIdentifier(verdict).equals(identifier)) {
        return true;
      }
    }
    return false;
  }

  public static UnwinnabilityFullVerdict calculate(String identifier) {
    if (!exists(identifier)) {
      throw new IllegalArgumentException("No mode for this letter identifier");
    }
    for (final UnwinnabilityFullVerdict verdict : UnwinnabilityFullVerdict.values()) {
      if (getIdentifier(verdict).equals(identifier)) {
        return verdict;
      }
    }
    // not possible to come here
    throw new ProgrammingMistakeException();
  }
}
