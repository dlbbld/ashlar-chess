// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability.identifier;

import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityQuickVerdict;

/**
 * Test-side mapping between {@link UnwinnabilityQuickVerdict} values and the lowercase string identifiers emitted by
 * Ambrona's CHA C binary ({@code "winnable"}, {@code "unwinnable"}, {@code "undetermined"}). Used only when reading raw
 * CHA output for cross-checks against the Java port; not part of the production API.
 *
 * <p>
 * Note that {@link UnwinnabilityQuickVerdict#POSSIBLY_WINNABLE} maps to {@code "undetermined"} because that is the
 * label CHA's quick analyzer emits for the same three-valued judgment.
 */
public final class UnwinnabilityQuickVerdictIdentifier {

  private UnwinnabilityQuickVerdictIdentifier() {
  }

  public static String getIdentifier(UnwinnabilityQuickVerdict verdict) {
    return switch (verdict) {
      case WINNABLE -> "winnable";
      case UNWINNABLE -> "unwinnable";
      case POSSIBLY_WINNABLE -> "undetermined";
    };
  }

  public static boolean exists(String identifier) {
    for (final UnwinnabilityQuickVerdict verdict : UnwinnabilityQuickVerdict.values()) {
      if (getIdentifier(verdict).equals(identifier)) {
        return true;
      }
    }
    return false;
  }
}
