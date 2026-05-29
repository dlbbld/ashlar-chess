// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.san;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.exceptions.UsageException;

/**
 * Thrown when the lenient SAN parser cannot resolve the input to a legal move even after applying every supported
 * tolerance. Carries the original text, the deepest underlying strict-pipeline reason (when the failure originated
 * there), and any forgiven items the lenient layer had already accumulated before the failure point - so the consumer
 * can see how far the parse got.
 */
@SuppressWarnings("null")
public class LenientSanParserValidationException extends UsageException {

  private final String originalText;
  private final @Nullable SanValidationProblem underlyingSanValidationProblem;
  private final @NonNull ImmutableList<@NonNull ForgivenItem> forgivenItemsAccumulated;

  public LenientSanParserValidationException(String message, String originalText,
      @Nullable SanValidationProblem underlyingSanValidationProblem,
      @NonNull ImmutableList<@NonNull ForgivenItem> forgivenItemsAccumulated) {
    super(message);
    this.originalText = originalText;
    this.underlyingSanValidationProblem = underlyingSanValidationProblem;
    this.forgivenItemsAccumulated = Nulls.copyOfList(forgivenItemsAccumulated);
  }

  public String getOriginalText() {
    return originalText;
  }

  /**
   * The deepest underlying strict-pipeline rejection reason, when the lenient parser exhausted its recovery options and
   * the final failure came from the strict layer; {@code null} if the lenient layer rejected the input on shape grounds
   * before the strict layer was consulted (e.g. mixed {@code 0-O} castling).
   */
  public @Nullable SanValidationProblem getUnderlyingSanValidationProblem() {
    return underlyingSanValidationProblem;
  }

  /**
   * The forgiven items the lenient parser had already accumulated before failing. Useful for diagnostics - shows which
   * tolerances applied before the input became unrecoverable.
   */
  public @NonNull ImmutableList<@NonNull ForgivenItem> getForgivenItemsAccumulated() {
    return forgivenItemsAccumulated;
  }

}
