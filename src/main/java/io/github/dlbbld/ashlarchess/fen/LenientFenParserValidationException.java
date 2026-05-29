// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.fen;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.enums.FenAdvancedValidationProblem;
import io.github.dlbbld.ashlarchess.common.exceptions.UsageException;

/**
 * Thrown by {@link LenientFenParser#parseText(String)} when the input cannot be parsed even after lenient
 * normalisation, or when the normalised FEN fails strict semantic validation. Mirrors the SAN- and PGN-side
 * lenient-parser exceptions: carries the typed problem category, the underlying advanced-validation problem (when
 * applicable), and the list of forgiven items accumulated before the failure point.
 */
@SuppressWarnings("null")
public class LenientFenParserValidationException extends UsageException {

  private final LenientFenParserValidationProblem lenientFenParserValidationProblem;

  /**
   * The underlying {@link FenAdvancedValidationProblem} when {@link #lenientFenParserValidationProblem} is
   * {@link LenientFenParserValidationProblem#ADVANCED_INVALID}; {@link FenAdvancedValidationProblem#SUCCESS} otherwise.
   * Carried so callers can react to the specific advanced-invariant violation without parsing the message.
   */
  private final FenAdvancedValidationProblem fenAdvancedValidationProblem;

  /**
   * Forgiven items accumulated before the failure point. Lenient normalisation runs left-to-right; if the delegate
   * parser then rejects the normalised FEN, the items that fired up to that point are carried so the caller has full
   * diagnostic context.
   */
  private final @NonNull ImmutableList<@NonNull ForgivenFenItem> forgivenItemsAccumulated;

  public LenientFenParserValidationException(LenientFenParserValidationProblem lenientFenParserValidationProblem,
      String message) {
    this(lenientFenParserValidationProblem, FenAdvancedValidationProblem.SUCCESS, message, ImmutableList.of());
  }

  public LenientFenParserValidationException(LenientFenParserValidationProblem lenientFenParserValidationProblem,
      @Nullable FenAdvancedValidationProblem fenAdvancedValidationProblem, String message,
      @NonNull ImmutableList<@NonNull ForgivenFenItem> forgivenItemsAccumulated) {
    super(message);
    this.lenientFenParserValidationProblem = lenientFenParserValidationProblem;
    this.fenAdvancedValidationProblem = fenAdvancedValidationProblem == null ? FenAdvancedValidationProblem.SUCCESS
        : fenAdvancedValidationProblem;
    this.forgivenItemsAccumulated = Nulls.copyOfList(forgivenItemsAccumulated);
  }

  public LenientFenParserValidationProblem getLenientFenParserValidationProblem() {
    return lenientFenParserValidationProblem;
  }

  public FenAdvancedValidationProblem getFenAdvancedValidationProblem() {
    return fenAdvancedValidationProblem;
  }

  public @NonNull ImmutableList<@NonNull ForgivenFenItem> getForgivenItemsAccumulated() {
    return forgivenItemsAccumulated;
  }

}
