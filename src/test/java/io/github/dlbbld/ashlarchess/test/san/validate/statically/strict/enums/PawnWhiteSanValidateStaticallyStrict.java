package io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums;

import static io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.SanValidateStaticallyStrictHelpers.appendMoveWithFile;
import static io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.enums.SanValidateStaticallyStrictHelpers.appendOnlyMove;

import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSet;

import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.model.EmptyBoardMove;
import io.github.dlbbld.ashlarchess.squares.AbstractEmptyBoardSquares;
import io.github.dlbbld.ashlarchess.squares.PawnDiagonalSquares;

@SuppressWarnings("null")
public abstract class PawnWhiteSanValidateStaticallyStrict {

  public static final ImmutableSet<String> VALUES;

  static {
    final Set<String> set = new TreeSet<>();

    // one and two-square pawn advances
    for (final EmptyBoardMove move : AbstractEmptyBoardSquares.calculatePawnEmptyBoardMoves(Side.WHITE)) {
      appendOnlyMove(set, move.toSquare(), PieceType.PAWN);
    }

    // diagonal captures (file-disambiguated form)
    for (final Square fromSquare : Square.REAL) {
      for (final Square diagonalSquare : PawnDiagonalSquares.getPawnDiagonalSquares(Side.WHITE, fromSquare)) {
        appendMoveWithFile(set, diagonalSquare, fromSquare.getFile(), PieceType.PAWN);
      }
    }

    VALUES = ImmutableSet.copyOf(set);
  }

}
