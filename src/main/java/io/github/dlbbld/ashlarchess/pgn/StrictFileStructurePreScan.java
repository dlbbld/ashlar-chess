// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.pgn;

import io.github.dlbbld.ashlarchess.san.SanValidationProblem;

/**
 * Whole-file structural pre-scan for the strict format: file non-empty, no leading blank line, trailing blank line
 * present, exactly two blank lines total, no two adjacent. Kept separate from the sequential parser so global counts
 * stay out of the per-token control flow.
 */
final class StrictFileStructurePreScan {

  private static final String BASIC_FORMAT_DESCRIPTION = "The PGN must have exactly two empty lines, one after the last tag and one at the end.";

  private StrictFileStructurePreScan() {
  }

  static void validate(String source) {
    // Empty-input rejection covers both strictly-empty (zero bytes / BOM-only) and whitespace-only - any
    // combination of spaces, tabs, and newlines with no other content. Both shapes carry no signal; the
    // FILE_EMPTY_LINE_* diagnostics below are about the strict two-blank-lines layout and don't apply when
    // there's nothing to lay out in the first place.
    if (source.isBlank()) {
      throw error(StrictPgnParserValidationProblem.FILE_EMPTY, "The PGN is empty.");
    }

    final char first = source.charAt(0);
    if (first == '\n' || first == '\r') {
      throw error(StrictPgnParserValidationProblem.FILE_EMPTY_LINE_CANNOT_START_WITH,
          "The PGN cannot start with an empty line.");
    }

    if (!source.endsWith("\n")) {
      throw error(StrictPgnParserValidationProblem.FILE_EMPTY_LINE_MUST_END_WITH,
          "The PGN must end with an empty line.");
    }

    // A newline inside a brace comment belongs to the token, even when it begins a physical line.
    final PgnTokenizer tokenizer = new PgnTokenizer(new PgnCharStream(source));
    int blankCount = 0;
    int previousBlankLineNumber = -1;
    boolean adjacentDetected = false;
    for (PgnToken token = tokenizer.next(); token.type() != PgnTokenType.EOF; token = tokenizer.next()) {
      if (token.type() == PgnTokenType.BRACE_COMMENT_UNCLOSED) {
        // The missing delimiter absorbs the trailer. Let the parser diagnose it in context.
        return;
      }
      if (token.type() == PgnTokenType.NEWLINE && token.column() == 1) {
        blankCount++;
        adjacentDetected |= token.line() == previousBlankLineNumber + 1;
        previousBlankLineNumber = token.line();
      }
    }

    if (blankCount != 2) {
      throw error(StrictPgnParserValidationProblem.FILE_EMPTY_LINE_EXACTLY_TWO,
          "The PGN must have exactly two empty lines.");
    }
    if (adjacentDetected) {
      throw error(StrictPgnParserValidationProblem.FILE_EMPTY_LINE_NOT_CONSECUTIVE,
          "The PGN cannot have two consecutive empty lines.");
    }
  }

  private static StrictPgnParserValidationException error(StrictPgnParserValidationProblem problem, String message) {
    return new StrictPgnParserValidationException(problem, SanValidationProblem.NONE,
        message + " " + BASIC_FORMAT_DESCRIPTION);
  }
}
