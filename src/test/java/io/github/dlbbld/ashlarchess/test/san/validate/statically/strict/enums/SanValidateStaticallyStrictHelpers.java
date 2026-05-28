package io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Rank;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.model.EmptyBoardMove;
import io.github.dlbbld.ashlarchess.test.common.utility.DiagonalLineUtility;

/**
 * Shared utilities for the SAN-validator strict-mode lookup tables: per-piece classes populate their
 * {@code static final ImmutableSet<String> VALUES} at class load time by calling these helpers. The string format
 * mirrors what the (now-deleted) {@code Generate*SanValidateStrict} scripts emitted as enum constants - e.g.
 * {@code "N1A2"} (piece-letter + rank-disambig + target square) - so the {@code *Calculate} consumers parse the same
 * shape.
 */
abstract class SanValidateStaticallyStrictHelpers {

  // ---- string-builder helpers -------------------------------------------------------------------

  // Note on the String.valueOf wrappers below: Java's `+` operator on (char, char) and (char, int) does
  // numeric addition, not string concatenation. Anchoring the left operand as String forces concatenation
  // semantics for the whole expression. (The original generator code had the same shape but the bug
  // happened to not bite because each leaf piecePlusX was assigned to a String intermediate.)

  static void appendOnlyMove(Set<String> set, Square toSquare, PieceType pieceType) {
    set.add(String.valueOf(pieceType.getLetter()) + toSquare.getName().toUpperCase());
  }

  static void appendMoveWithFromSquare(Set<String> set, Square toSquare, Square fromSquare, PieceType pieceType) {
    set.add(
        String.valueOf(pieceType.getLetter()) + fromSquare.getName().toUpperCase() + toSquare.getName().toUpperCase());
  }

  static void appendMoveWithFile(Set<String> set, Square toSquare, io.github.dlbbld.ashlarchess.board.enums.File file,
      PieceType pieceType) {
    final char fileLetterUpperCase = Character.toUpperCase(file.getLetter());
    set.add(String.valueOf(pieceType.getLetter()) + fileLetterUpperCase + toSquare.getName().toUpperCase());
  }

  static void appendMoveWithRank(Set<String> set, Square toSquare, Rank rank, PieceType pieceType) {
    set.add(String.valueOf(pieceType.getLetter()) + rank.getNumber() + toSquare.getName().toUpperCase());
  }

  // ---- from-square list extraction --------------------------------------------------------------

  static List<Square> calculateFromSquareList(Set<EmptyBoardMove> emptyBoardMoveSet) {
    final List<Square> fromSquareList = new ArrayList<>();
    for (final EmptyBoardMove move : emptyBoardMoveSet) {
      fromSquareList.add(move.fromSquare());
    }
    return fromSquareList;
  }

  // ---- disambiguation predicates ----------------------------------------------------------------

  static boolean calculateIsFromRankPossibleOrthogonal(Square fromSquare, Square toSquare,
      List<Square> fromSquareList) {
    for (final Square otherFromSquare : fromSquareList) {
      if (otherFromSquare.getFile() == fromSquare.getFile() && otherFromSquare.getRank() != fromSquare.getRank()
          && calculateIsOppositeVertical(fromSquare, toSquare, otherFromSquare)) {
        return true;
      }
    }
    return false;
  }

  static boolean calculateHasOtherMovesFromSameRank(Square fromSquare, List<Square> fromSquareList) {
    for (final Square otherFromSquare : fromSquareList) {
      if (otherFromSquare != fromSquare && otherFromSquare.getRank() == fromSquare.getRank()) {
        return true;
      }
    }
    return false;
  }

  static boolean calculateIsOppositeVertical(Square fromSquare, Square toSquare, Square otherFromSquare) {
    if (fromSquare.getFile() != toSquare.getFile() || otherFromSquare.getFile() != toSquare.getFile()) {
      return false;
    }
    if (fromSquare.getRank().getNumber() > toSquare.getRank().getNumber()) {
      return otherFromSquare.getRank().getNumber() < toSquare.getRank().getNumber();
    }
    if (fromSquare.getRank().getNumber() < toSquare.getRank().getNumber()) {
      return otherFromSquare.getRank().getNumber() > toSquare.getRank().getNumber();
    }
    throw new ProgrammingMistakeException("Unexpected program flow - at this point the rank numbers must be different");
  }

  static boolean calculateIsFromFilePossibleOrthogonal(Square fromSquare, Square toSquare,
      List<Square> fromSquareList) {
    for (final Square otherFromSquare : fromSquareList) {
      if (otherFromSquare.getFile() != fromSquare.getFile()
          && calculateIsFromFilePossibleOrthogonal(fromSquare, toSquare, otherFromSquare)) {
        return true;
      }
    }
    return false;
  }

  private static boolean calculateIsFromFilePossibleOrthogonal(Square fromSquare, Square toSquare,
      Square otherFromSquare) {
    if (fromSquare.getRank() != toSquare.getRank() || toSquare.getRank() != otherFromSquare.getRank()) {
      return true;
    }
    if (fromSquare.getFile().getNumber() < toSquare.getFile().getNumber()) {
      return otherFromSquare.getFile().getNumber() > toSquare.getFile().getNumber();
    }
    if (fromSquare.getFile().getNumber() > toSquare.getFile().getNumber()) {
      return otherFromSquare.getFile().getNumber() < toSquare.getFile().getNumber();
    }
    return false;
  }

  static boolean calculateIsFromFilePossibleDiagonal(Square fromSquare, Square toSquare, List<Square> fromSquareList) {
    for (final Square otherFromSquare : fromSquareList) {
      if (otherFromSquare.getFile() != fromSquare.getFile()
          && calculateIsFromFilePossibleDiagonal(fromSquare, toSquare, otherFromSquare)) {
        return true;
      }
    }
    return false;
  }

  private static boolean calculateIsFromFilePossibleDiagonal(Square fromSquare, Square toSquare,
      Square otherFromSquare) {
    if (!DiagonalLineUtility.calculateIsOnDiagonalLine(fromSquare, toSquare, otherFromSquare)) {
      return true;
    }
    return calculateIsOppositeDiagonal(fromSquare, toSquare, otherFromSquare);
  }

  private static boolean calculateIsOppositeDiagonal(Square fromSquare, Square toSquare, Square otherFromSquare) {
    if (!DiagonalLineUtility.calculateIsOnDiagonalLine(fromSquare, toSquare, otherFromSquare)) {
      throw new ProgrammingMistakeException(
          "The program is not expected to call this method when the squares are not on the same diagonal");
    }
    if (fromSquare.getRank().getNumber() < toSquare.getRank().getNumber()) {
      return otherFromSquare.getRank().getNumber() > toSquare.getRank().getNumber();
    }
    if (fromSquare.getRank().getNumber() > toSquare.getRank().getNumber()) {
      return otherFromSquare.getRank().getNumber() < toSquare.getRank().getNumber();
    }
    throw new ProgrammingMistakeException("Unexpected program flow - at this point the rank numbers must be different");
  }

}
