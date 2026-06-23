// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.fen;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import io.github.dlbbld.ashlarchess.exceptions.UsageException;
import io.github.dlbbld.ashlarchess.internal.Nulls;

/**
 * Thrown by {@link LenientFenParser#parse(String)} when the input cannot be parsed even after lenient normalisation, or
 * when the normalised FEN fails strict semantic validation. Mirrors the SAN- and PGN-side lenient-parser exceptions:
 * carries the typed problem category, the underlying strict-semantic-validation problem (when applicable), and the list
 * of forgiven items accumulated before the failure point.
 */
@SuppressWarnings("null")
public class LenientFenParserValidationException extends UsageException {

  private final LenientFenParserValidationProblem lenientFenParserValidationProblem;

  /**
   * The underlying {@link StrictFenSemanticValidationProblem} when {@link #lenientFenParserValidationProblem} is
   * {@link LenientFenParserValidationProblem#STRICT_SEMANTIC_INVALID};
   * {@link StrictFenSemanticValidationProblem#SUCCESS} otherwise. Carried so callers can react to the specific strict
   * semantic invariant violation without parsing the message.
   */
  private final StrictFenSemanticValidationProblem strictFenSemanticValidationProblem;

  /**
   * Forgiven items accumulated before the failure point. Lenient normalisation runs left-to-right; if the delegate
   * parser then rejects the normalised FEN, the items that fired up to that point are carried so the caller has full
   * diagnostic context.
   */
  private final @NonNull List<@NonNull ForgivenFenItem> forgivenItemsAccumulated;

  public LenientFenParserValidationException(LenientFenParserValidationProblem lenientFenParserValidationProblem,
      String message) {
    this(lenientFenParserValidationProblem, StrictFenSemanticValidationProblem.SUCCESS, message, List.of());
  }

  public LenientFenParserValidationException(LenientFenParserValidationProblem lenientFenParserValidationProblem,
      @Nullable StrictFenSemanticValidationProblem strictFenSemanticValidationProblem, String message,
      @NonNull List<@NonNull ForgivenFenItem> forgivenItemsAccumulated) {
    super(message);
    this.lenientFenParserValidationProblem = lenientFenParserValidationProblem;
    this.strictFenSemanticValidationProblem = strictFenSemanticValidationProblem == null
        ? StrictFenSemanticValidationProblem.SUCCESS
        : strictFenSemanticValidationProblem;
    this.forgivenItemsAccumulated = Nulls.copyOfList(forgivenItemsAccumulated);
  }

  public LenientFenParserValidationProblem getLenientFenParserValidationProblem() {
    return lenientFenParserValidationProblem;
  }

  public StrictFenSemanticValidationProblem getStrictFenSemanticValidationProblem() {
    return strictFenSemanticValidationProblem;
  }

  public @NonNull List<@NonNull ForgivenFenItem> getForgivenItemsAccumulated() {
    return forgivenItemsAccumulated;
  }

}
