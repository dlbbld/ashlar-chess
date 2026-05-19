package com.dlb.chess.unwinnability;

import java.util.ArrayList;
import java.util.List;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.File;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.constants.EnumConstants;

abstract class SemiOpenFilesUtility implements EnumConstants {

  // Pawns legally live only on ranks 2-7 (0-indexed 1-6) — bits 8-55. Mask out ranks 1 and 8 defensively.
  private static final long PAWN_RANK_MASK = 0x00FFFFFFFFFFFF00L;

  public static boolean calculateHasSemiOpenFile(StaticPosition staticPosition) {
    for (final File file : File.REAL) {
      if (calculateIsSemiOpenFile(staticPosition, file)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Bitboard variant of {@link #calculateHasSemiOpenFile(StaticPosition)}. Mirrors the StaticPosition reference's
   * "first pawn encountered walking from the side's promotion rank" semantic: for each file, the
   * highest-on-the-file pawn determines whether white claims semi-open; the lowest-on-the-file pawn determines
   * whether black does. Restricted to ranks 2-7 — pawns on rank 1 / rank 8 are illegal and ignored even if the
   * caller's bitboard somehow has bits there.
   */
  public static boolean calculateHasSemiOpenFile(BitboardPosition bitboardPosition) {
    final long whitePawns = bitboardPosition.whitePawns() & PAWN_RANK_MASK;
    final long blackPawns = bitboardPosition.blackPawns() & PAWN_RANK_MASK;
    for (int fileIndex = 0; fileIndex < 8; fileIndex++) {
      final long fileMask = 0x0101010101010101L << fileIndex;
      final long pawnsOnFile = (whitePawns | blackPawns) & fileMask;
      if (pawnsOnFile == 0L) {
        continue;
      }
      final long highest = Long.highestOneBit(pawnsOnFile);
      if ((highest & whitePawns) != 0L) {
        return true;
      }
      final long lowest = Long.lowestOneBit(pawnsOnFile);
      if ((lowest & blackPawns) != 0L) {
        return true;
      }
    }
    return false;
  }

  private static boolean calculateIsSemiOpenFile(StaticPosition staticPosition, File file) {
    return calculateIsSemiOpenFile(staticPosition, file, Side.WHITE)
        || calculateIsSemiOpenFile(staticPosition, file, Side.BLACK);

  }

  public static boolean calculateIsSemiOpenFile(StaticPosition staticPosition, File file, Side sideHavingSemiOpenFile) {

    final List<Square> squareListToCheck = calculateSquareListToCheck(file, sideHavingSemiOpenFile);

    for (final Square squareCheck : squareListToCheck) {
      if (!staticPosition.isEmpty(squareCheck)) {
        final Piece piece = staticPosition.get(squareCheck);
        if (piece.getPieceType() == PieceType.PAWN) {
          return piece.getSide() == sideHavingSemiOpenFile;
        }
      }
    }
    return false;
  }

  private static List<Square> calculateSquareListToCheck(File file, Side sideHavingSemiOpenFile) {

    final List<Square> result = new ArrayList<>();

    switch (sideHavingSemiOpenFile) {
      case WHITE -> {
        result.add(Square.calculate(file, RANK_7));
        result.add(Square.calculate(file, RANK_6));
        result.add(Square.calculate(file, RANK_5));
        result.add(Square.calculate(file, RANK_4));
        result.add(Square.calculate(file, RANK_3));
        result.add(Square.calculate(file, RANK_2));
      }
      case BLACK -> {
        result.add(Square.calculate(file, RANK_2));
        result.add(Square.calculate(file, RANK_3));
        result.add(Square.calculate(file, RANK_4));
        result.add(Square.calculate(file, RANK_5));
        result.add(Square.calculate(file, RANK_6));
        result.add(Square.calculate(file, RANK_7));
      }
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    }

    return result;
  }
}
