package io.github.dlbbld.ashlarchess.san;

import org.eclipse.jdt.annotation.NonNull;

import com.google.common.collect.ImmutableList;

/**
 * One forgiven deviation surfaced by the lenient SAN parser.
 *
 * @param code          the typed deviation classifier
 * @param originalToken the original SAN token as the user wrote it (the entire move, not just the deviating fragment)
 * @param canonicalSan  the canonical SAN equivalent the parser resolved the input to
 */
public record ForgivenItem(LenientSanValidationProblem code, String originalToken, String canonicalSan) {

  /**
   * Shared empty list for the "no deviations forgiven" case. Centralised here so the {@code @NonNull} suppression on
   * Guava's {@code ImmutableList.of()} (which JDT can't statically prove is non-null with non-null elements) lives in
   * one place rather than at every caller.
   */
  @SuppressWarnings("null")
  public static final @NonNull ImmutableList<@NonNull ForgivenItem> EMPTY_LIST = ImmutableList.of();

  public ForgivenItem {
    if (originalToken.isBlank()) {
      throw new IllegalArgumentException("originalToken must not be blank");
    }
    if (canonicalSan.isBlank()) {
      throw new IllegalArgumentException("canonicalSan must not be blank");
    }
  }
}
