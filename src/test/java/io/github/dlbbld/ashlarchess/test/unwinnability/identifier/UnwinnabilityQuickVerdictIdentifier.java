// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability.identifier;

import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityQuickVerdict;

/**
 * Test-side mapping between {@link UnwinnabilityQuickVerdict} values and the lowercase string identifiers emitted by
 * Ambrona's CHA C binary ({@code "unwinnable"}, {@code "undetermined"}). Used only when reading raw CHA output for
 * cross-checks against the Java port; not part of the production API.
 *
 * <p>
 * Note that {@link UnwinnabilityQuickVerdict#POSSIBLY_WINNABLE} maps to {@code "undetermined"} because that is the
 * label CHA's quick analyzer emits for the same judgment. {@link UnwinnabilityQuickVerdict#WINNABLE} (which only the
 * paper-formulation quick analyzer can produce; CHA's quick never claims winnability) maps to {@code "winnable"} and
 * therefore never matches a CHA quick label - such rows are handled by the comparison's accepted-differences path.
 */
public final class UnwinnabilityQuickVerdictIdentifier {

  private UnwinnabilityQuickVerdictIdentifier() {
  }

  public static String getIdentifier(UnwinnabilityQuickVerdict verdict) {
    return switch (verdict) {
      case UNWINNABLE -> "unwinnable";
      case WINNABLE -> "winnable";
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
