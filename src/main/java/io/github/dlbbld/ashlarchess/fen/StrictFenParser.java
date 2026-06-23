// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.fen;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import io.github.dlbbld.ashlarchess.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.internal.ExceptionUtility;
import io.github.dlbbld.ashlarchess.fen.model.Fen;

/**
 * Strict FEN parser. Accepts only canonical six-field FEN text and applies the library's structural and
 * rule-consistency validation before returning a {@link Fen} model.
 */
public final class StrictFenParser {

  private StrictFenParser() {
  }

  /**
   * Parses a strict FEN string.
   *
   * @throws StrictFenSemanticValidationException if the FEN is malformed or violates the strict
   *                                              structural/rule-consistency checks
   */
  public static Fen parse(String fen) {
    return StrictFenSemanticParser.parse(fen);
  }

  /**
   * Validates a strict FEN string without throwing. On success the result carries the parsed {@link Fen}; on failure it
   * carries the typed validation problem and a diagnostic message.
   */
  public static StrictFenParserValidationResult validate(String fen) {
    try {
      final Fen parsedFen = parse(fen);
      return new StrictFenParserValidationResult(StrictFenSemanticValidationProblem.SUCCESS, "OK", parsedFen);
    } catch (final StrictFenSemanticValidationException e) {
      return new StrictFenParserValidationResult(e.getStrictFenSemanticValidationProblem(),
          ExceptionUtility.getMessage(e), null);
    } catch (final ProgrammingMistakeException e) {
      // A library bug must fail fast, not be masked as an UNKNOWN_ERROR validation result.
      throw e;
    } catch (final RuntimeException e) {
      return new StrictFenParserValidationResult(StrictFenSemanticValidationProblem.UNKNOWN_ERROR,
          unexpectedValidationErrorMessage(e), null);
    }
  }

  @SuppressWarnings("null")
  private static @NonNull String unexpectedValidationErrorMessage(RuntimeException e) {
    final @Nullable String nullableReason = e.getMessage();
    final String reason = nullableReason == null ? "" : nullableReason;
    return "An unexpected error occurred during strict FEN validation. Reason: " + reason;
  }

}
