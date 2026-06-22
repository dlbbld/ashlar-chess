// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.fen;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.fen.model.Fen;

/**
 * Outcome of a lenient FEN parse-with-validation. On success, {@link #fen} carries the parsed model and
 * {@link #forgivenItems} lists every syntactic-tolerance transformation the lenient layer applied (empty when the input
 * was already canonical FEN). On failure, {@link #fen} is {@code null} and {@link #forgivenItems} contains whatever was
 * accumulated up to the failure point.
 *
 * <p>
 * The lenient layer only forgives syntactic deviations (whitespace, casing, missing trailing counters, etc.); it does
 * not weaken {@link StrictFenParser}'s structural/rule-consistency checks. When the underlying strict parser rejects
 * the normalised FEN, {@link #strictFenSemanticValidationProblem} carries the specific cause so callers can react
 * without parsing the message.
 */
@SuppressWarnings("null")
public record LenientFenParserValidationResult(@NonNull LenientFenParserValidationProblem problem,
    @NonNull StrictFenSemanticValidationProblem strictFenSemanticValidationProblem, @NonNull String message,
    @Nullable Fen fen, @NonNull List<@NonNull ForgivenFenItem> forgivenItems) {

  public LenientFenParserValidationResult {
    forgivenItems = Nulls.copyOfList(forgivenItems);
  }

  public boolean isValid() {
    return problem == LenientFenParserValidationProblem.OK;
  }
}
