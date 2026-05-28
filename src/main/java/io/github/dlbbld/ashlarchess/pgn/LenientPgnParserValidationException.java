// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.pgn;

import org.eclipse.jdt.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.exceptions.UsageException;
import io.github.dlbbld.ashlarchess.san.ForgivenItem;
import io.github.dlbbld.ashlarchess.san.SanValidationProblem;

@SuppressWarnings("null")
public class LenientPgnParserValidationException extends UsageException {

  private final LenientPgnParserValidationProblem lenientPgnParserValidationProblem;

  private final SanValidationProblem sanValidationProblem;

  /**
   * SAN-level forgiven items accumulated during movetext replay before the failure point. Empty if the failure occurred
   * outside the movetext path (tag validation, structural error) or if no SAN deviation had been forgiven yet.
   */
  private final @NonNull ImmutableList<@NonNull ForgivenItem> sanForgivenItemsAccumulated;

  /**
   * Tag-level forgiven items accumulated before the failure point. Tag-level forgiveness happens after tag parsing and
   * before movetext replay, so this list is empty for any failure that originated in the tag section itself, and
   * fully-populated for any failure that originated downstream (in the movetext).
   */
  private final @NonNull ImmutableList<@NonNull ForgivenTagItem> tagForgivenItemsAccumulated;

  public LenientPgnParserValidationException(LenientPgnParserValidationProblem lenientPgnParserValidationProblem,
      SanValidationProblem sanValidationProblem, String message) {
    this(lenientPgnParserValidationProblem, sanValidationProblem, message, ImmutableList.of(), ImmutableList.of());
  }

  /**
   * Constructor used when the failure occurs during movetext replay and SAN-level forgiven items have already been
   * accumulated for earlier moves. Carries the accumulated SAN-level and tag-level items so callers can see partial
   * diagnostic data on failure.
   */
  public LenientPgnParserValidationException(LenientPgnParserValidationProblem lenientPgnParserValidationProblem,
      SanValidationProblem sanValidationProblem, String message,
      @NonNull ImmutableList<@NonNull ForgivenItem> sanForgivenItemsAccumulated,
      @NonNull ImmutableList<@NonNull ForgivenTagItem> tagForgivenItemsAccumulated) {
    super(message);
    this.lenientPgnParserValidationProblem = lenientPgnParserValidationProblem;
    this.sanValidationProblem = sanValidationProblem;
    this.sanForgivenItemsAccumulated = Nulls.copyOfList(sanForgivenItemsAccumulated);
    this.tagForgivenItemsAccumulated = Nulls.copyOfList(tagForgivenItemsAccumulated);
  }

  public LenientPgnParserValidationProblem getLenientPgnParserValidationProblem() {
    return lenientPgnParserValidationProblem;
  }

  public SanValidationProblem getSanValidationProblem() {
    return sanValidationProblem;
  }

  public @NonNull ImmutableList<@NonNull ForgivenItem> getSanForgivenItemsAccumulated() {
    return sanForgivenItemsAccumulated;
  }

  public @NonNull ImmutableList<@NonNull ForgivenTagItem> getTagForgivenItemsAccumulated() {
    return tagForgivenItemsAccumulated;
  }

}
