package com.dlb.chess.pgn;

import com.dlb.chess.common.exceptions.PgnCommentaryValidationException;

/**
 * Value object for PGN commentary content. Construction validates the contract - see specification.md (Commentary
 * contract).
 */
public record PgnCommentary(String value) {

  public PgnCommentary {
    validate(value);
  }

  public static final PgnCommentary EMPTY = new PgnCommentary("");

  private static void validate(String value) {
    for (var i = 0; i < value.length();) {
      final var cp = value.codePointAt(i);

      // `}` would terminate the {...} grammar on export. `{` is allowed (PGN spec section 8.2.5).
      if (cp == '}') {
        throw new PgnCommentaryValidationException(formatBraceProblem(value, i));
      }

      // `\t` and `\n` are the only permitted control characters. `\r` is rejected - T-005 normalises CR to
      // LF at the parser input, so the model invariant is "no CR ever".
      if (cp != '\t' && cp != '\n') {
        final var type = Character.getType(cp);
        if (type == Character.CONTROL || type == Character.SURROGATE || type == Character.UNASSIGNED
            || type == Character.PRIVATE_USE) {
          throw new PgnCommentaryValidationException(formatTypeProblem(value, i, cp, type));
        }
      }

      i += Character.charCount(cp);
    }
  }

  private static String formatBraceProblem(String value, int index) {
    return "PGN commentary must not contain closing brace (at index " + index + " of: \"" + summarizeForError(value)
        + "\").";
  }

  private static String formatTypeProblem(String value, int index, int cp, int type) {
    final var typeLabel = switch (type) {
      case Character.CONTROL -> "a control character";
      case Character.SURROGATE -> "a lone surrogate code point";
      case Character.UNASSIGNED -> "an unassigned code point";
      case Character.PRIVATE_USE -> "a private-use code point";
      default -> "an invalid character";
    };
    return "PGN commentary must not contain " + typeLabel + " (U+" + String.format("%04X", cp) + " at index " + index
        + " of: \"" + summarizeForError(value) + "\").";
  }

  private static String summarizeForError(String value) {
    if (value.length() <= 80) {
      return value;
    }
    return value.substring(0, 77) + "...";
  }

}
