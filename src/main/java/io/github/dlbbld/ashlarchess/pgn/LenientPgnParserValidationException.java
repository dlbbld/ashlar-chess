// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.pgn;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import io.github.dlbbld.ashlarchess.exceptions.UsageException;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.san.ForgivenSanItem;
import io.github.dlbbld.ashlarchess.san.SanValidationProblem;

@SuppressWarnings("null")
public class LenientPgnParserValidationException extends UsageException {

  private final LenientPgnParserValidationProblem lenientPgnParserValidationProblem;

  private final SanValidationProblem sanValidationProblem;

  /**
   * SAN-level forgiven items accumulated during movetext replay before the failure point. Empty if the failure occurred
   * outside the movetext path (tag validation, structural error) or if no SAN deviation had been forgiven yet.
   */
  private final @NonNull List<@NonNull ForgivenSanItem> sanForgivenItemsAccumulated;

  /**
   * Tag-level forgiven items accumulated before the failure point. Tag-level forgiveness happens after tag parsing and
   * before movetext replay, so this list is empty for any failure that originated in the tag section itself, and
   * fully-populated for any failure that originated downstream (in the movetext).
   */
  private final @NonNull List<@NonNull ForgivenTagItem> tagForgivenItemsAccumulated;

  public LenientPgnParserValidationException(LenientPgnParserValidationProblem lenientPgnParserValidationProblem,
      SanValidationProblem sanValidationProblem, String message) {
    this(lenientPgnParserValidationProblem, sanValidationProblem, message, List.of(), List.of());
  }

  /**
   * Constructor used when the failure occurs during movetext replay and SAN-level forgiven items have already been
   * accumulated for earlier moves. Carries the accumulated SAN-level and tag-level items so callers can see partial
   * diagnostic data on failure.
   */
  public LenientPgnParserValidationException(LenientPgnParserValidationProblem lenientPgnParserValidationProblem,
      SanValidationProblem sanValidationProblem, String message,
      @NonNull List<@NonNull ForgivenSanItem> sanForgivenItemsAccumulated,
      @NonNull List<@NonNull ForgivenTagItem> tagForgivenItemsAccumulated) {
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

  public @NonNull List<@NonNull ForgivenSanItem> getSanForgivenItemsAccumulated() {
    return sanForgivenItemsAccumulated;
  }

  public @NonNull List<@NonNull ForgivenTagItem> getTagForgivenItemsAccumulated() {
    return tagForgivenItemsAccumulated;
  }

}
