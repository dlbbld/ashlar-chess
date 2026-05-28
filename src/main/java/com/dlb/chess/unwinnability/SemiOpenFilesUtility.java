package com.dlb.chess.unwinnability;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.board.enums.File;
import com.dlb.chess.board.enums.Rank;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.constants.EnumConstants;

abstract class SemiOpenFilesUtility implements EnumConstants {

  // Pawns legally live only on ranks 2-7 (0-indexed 1-6) - bits 8-55. Mask out ranks 1 and 8 defensively.
  private static final long PAWN_RANK_MASK = 0x00FFFFFFFFFFFF00L;

  // Single A-file mask (bits a1, a2, ..., a8) - shifted by file index to mask other files.
  private static final long A_FILE_MASK = 0x0101010101010101L;

  /**
   * True iff there is at least one file on which {@code side} has a "semi-open" property - i.e. the first pawn
   * encountered walking from {@code side}'s point of view down the file is one of {@code side}'s own pawns. For white
   * the walk starts at rank 7; for black at rank 2.
   *
   * <p>
   * Restricted to ranks 2-7 - pawns on rank 1 / rank 8 are illegal and ignored even if the caller's bitboard somehow
   * has bits there.
   */
  public static boolean calculateHasSemiOpenFile(BitboardPosition bitboardPosition) {
    final long whitePawns = bitboardPosition.whitePawns() & PAWN_RANK_MASK;
    final long blackPawns = bitboardPosition.blackPawns() & PAWN_RANK_MASK;
    for (int fileIndex = 0; fileIndex < 8; fileIndex++) {
      final long fileMask = A_FILE_MASK << fileIndex;
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

  /**
   * True iff {@code sideHavingSemiOpenFile} has a semi-open {@code file} - i.e. the first pawn encountered walking
   * from that side's point of view down the file is one of that side's own pawns. For white the walk starts at rank
   * 7 (look for the highest pawn bit on the file); for black at rank 2 (look for the lowest pawn bit on the file).
   * Pawns on rank 1 / rank 8 are ignored defensively.
   */
  public static boolean calculateIsSemiOpenFile(BitboardPosition bitboardPosition, File file,
      Side sideHavingSemiOpenFile) {
    final long whitePawns = bitboardPosition.whitePawns() & PAWN_RANK_MASK;
    final long blackPawns = bitboardPosition.blackPawns() & PAWN_RANK_MASK;
    final long fileMask = A_FILE_MASK << fileIndex(file);
    final long pawnsOnFile = (whitePawns | blackPawns) & fileMask;
    if (pawnsOnFile == 0L) {
      return false;
    }
    return switch (sideHavingSemiOpenFile) {
      case WHITE -> (Long.highestOneBit(pawnsOnFile) & whitePawns) != 0L;
      case BLACK -> (Long.lowestOneBit(pawnsOnFile) & blackPawns) != 0L;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  private static int fileIndex(File file) {
    // Little-endian rank-file: A1 = ordinal 0, B1 = 1, ..., H1 = 7. The ordinal of (file, RANK_1) is the file index.
    return Square.calculate(file, Rank.RANK_1).ordinal();
  }
}
