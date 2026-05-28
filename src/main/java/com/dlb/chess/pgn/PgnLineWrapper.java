package com.dlb.chess.pgn;

import java.util.ArrayList;
import java.util.List;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;

abstract class PgnLineWrapper {

  /**
   * Wraps {@code line} into at most {@code lineLength}-char lines, splitting on spaces - except inside {@code {...}} brace
   * comments, which are atomic. A brace region longer than {@code lineLength} is emitted on its own line rather than
   * broken: spaces inside commentary are content, not wrap candidates. The 79-char export-format guideline is a soft
   * target, matching python-chess.
   */
  static List<String> calculateWrappedLines(String line, int lineLength) {
    final List<String> atoms = splitIntoAtoms(line);
    if (atoms.isEmpty()) {
      throw new ProgrammingMistakeException("As the passed text cannot be null the array cannot be empty");
    }
    final List<String> result = new ArrayList<>();
    StringBuilder wrappedLine = new StringBuilder();
    wrappedLine.append(Nulls.get(atoms, 0));
    for (var i = 1; i < atoms.size(); i++) {
      final String atom = Nulls.get(atoms, i);
      if (wrappedLine.length() + 1 + atom.length() <= lineLength) {
        wrappedLine.append(" ").append(atom);
      } else {
        result.add(Nulls.toString(wrappedLine));
        wrappedLine = new StringBuilder();
        wrappedLine.append(atom);
      }
    }
    result.add(Nulls.toString(wrappedLine));
    return result;
  }

  /**
   * Splits {@code line} into space-separated atoms; each {@code {...}} region is one indivisible atom. Spaces and other
   * characters inside a brace region are content, not separators. Per PGN spec section 8.2.5 an inner {@code {} is
   * content too, so only {@code }} ends a region.
   */
  private static List<String> splitIntoAtoms(String line) {
    final List<String> atoms = new ArrayList<>();
    final var len = line.length();
    var i = 0;
    while (i < len) {
      final var c = line.charAt(i);
      if (c == ' ') {
        i++;
        continue;
      }
      if (c == '{') {
        var j = i + 1;
        while (j < len && line.charAt(j) != '}') {
          j++;
        }
        if (j < len) {
          atoms.add(Nulls.substring(line, i, j + 1));
          i = j + 1;
        } else {
          // Defensive - PgnCreate emits balanced braces, so unreachable in practice.
          atoms.add(Nulls.substring(line, i));
          i = len;
        }
        continue;
      }
      var j = i;
      while (j < len && line.charAt(j) != ' ' && line.charAt(j) != '{') {
        j++;
      }
      atoms.add(Nulls.substring(line, i, j));
      i = j;
    }
    return atoms;
  }
}
