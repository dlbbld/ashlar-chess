package com.dlb.chess.pgn;

import com.dlb.chess.san.SanValidationProblem;

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

    final int length = source.length();
    final boolean endsOnNewline = endsOnNewline(source);
    if (!endsOnNewline) {
      throw error(StrictPgnParserValidationProblem.FILE_EMPTY_LINE_MUST_END_WITH,
          "The PGN must end with an empty line.");
    }

    // Count blank lines (a line with no content chars) and detect adjacency in one forward walk.
      int blankCount = 0;
      int previousBlankLineNumber = -1;
      int currentLineNumber = 1;
      int currentLineContentChars = 0;
      boolean adjacentDetected = false;

    for (int i = 0; i < length; i++) {
      final char c = source.charAt(i);
      if (c == '\r') {
        if (i + 1 < length && source.charAt(i + 1) == '\n') {
          i++;
        }
        if (currentLineContentChars == 0) {
          blankCount++;
          if (previousBlankLineNumber != -1 && currentLineNumber == previousBlankLineNumber + 1) {
            adjacentDetected = true;
          }
          previousBlankLineNumber = currentLineNumber;
        }
        currentLineNumber++;
        currentLineContentChars = 0;
      } else if (c == '\n') {
        if (currentLineContentChars == 0) {
          blankCount++;
          if (previousBlankLineNumber != -1 && currentLineNumber == previousBlankLineNumber + 1) {
            adjacentDetected = true;
          }
          previousBlankLineNumber = currentLineNumber;
        }
        currentLineNumber++;
        currentLineContentChars = 0;
      } else {
        currentLineContentChars++;
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

  private static boolean endsOnNewline(String source) {
    final char last = source.charAt(source.length() - 1);
    return last == '\n' || last == '\r';
  }

  private static StrictPgnParserValidationException error(StrictPgnParserValidationProblem problem, String message) {
    return new StrictPgnParserValidationException(problem, SanValidationProblem.NONE,
        message + " " + BASIC_FORMAT_DESCRIPTION);
  }
}
