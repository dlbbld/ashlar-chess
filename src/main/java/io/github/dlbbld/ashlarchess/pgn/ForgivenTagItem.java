// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.pgn;

import org.eclipse.jdt.annotation.NonNull;

import com.google.common.collect.ImmutableList;

/**
 * One tag-level deviation surfaced by the lenient PGN parser. The parse model preserves input as given; this record is
 * the diagnostic channel telling consumers which deviations the parser tolerated.
 *
 * @param code    the typed deviation classifier
 * @param tagName the standard tag the diagnostic is about (e.g. {@code "Event"}, {@code "Result"}, {@code "SetUp"},
 *                {@code "FEN"}), or {@code ""} if the diagnostic spans multiple tags
 * @param detail  supplementary context - e.g. the termination-marker value for
 *                {@link ForgivenTagItemCode#RESULT_TAG_MISSING_BUT_TERMINATION_MARKER_PRESENT}, or {@code ""} when no
 *                extra context is needed
 */
public record ForgivenTagItem(ForgivenTagItemCode code, String tagName, String detail) {

  /**
   * Shared empty list for the "no tag deviations forgiven" case. Centralised here so the {@code @NonNull} suppression
   * on Guava's {@code ImmutableList.of()} (which JDT can't statically prove is non-null with non-null elements) lives
   * in one place rather than at every caller.
   */
  @SuppressWarnings("null")
  public static final @NonNull ImmutableList<@NonNull ForgivenTagItem> EMPTY_LIST = ImmutableList.of();

}
