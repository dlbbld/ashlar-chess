package com.dlb.chess.bitboard;

import java.util.Random;

import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.Square;

/**
 * Pre-computed Zobrist random keys for chess positions. Four key groups, all drawn from the same fixed-seed
 * {@link Random} in a stable order (any change to the draw order alters every hash, so the order is part of the
 * contract):
 *
 * <ol>
 * <li>Piece-placement: 12 real pieces × 64 squares = 768 keys, indexed by {@link Piece#ordinal()} ×
 * {@link Square#ordinal()}.</li>
 * <li>Side-to-move: a single key, XORed into the hash when it is Black's turn.</li>
 * <li>Castling rights: four keys (white-king-side, white-queen-side, black-king-side, black-queen-side), each
 * XORed when that specific right is held.</li>
 * <li>En-passant file: eight keys (file A..H), the appropriate one XORed when an en-passant target exists.</li>
 * </ol>
 *
 * <p>
 * The piece-placement component is sufficient for piece-only equality (used by
 * {@link BitboardPosition#zobristPieces}). The remaining keys exist for full position equality, which the
 * helpmate-search transposition map needs.
 *
 * <p>
 * Keys are derived from a fixed seed so the table is deterministic and reproducible: same seed → same keys →
 * same hashes across JVM runs. This matters for transposition keys persisted across processes.
 */
public final class ZobristKeys {

  // Arbitrary fixed seed: stable hashes across runs without relying on any external secret.
  private static final long SEED = 0xC0FFEE_1234_ABCDL;

  // Castling-key indices.
  private static final int WHITE_KING_SIDE = 0;
  private static final int WHITE_QUEEN_SIDE = 1;
  private static final int BLACK_KING_SIDE = 2;
  private static final int BLACK_QUEEN_SIDE = 3;

  private static final long[][] PIECE_SQUARE_KEYS = new long[12][64];
  private static final long BLACK_TO_MOVE_KEY;
  private static final long[] CASTLING_KEYS = new long[4];
  private static final long[] EN_PASSANT_FILE_KEYS = new long[8];

  static {
    final Random rng = new Random(SEED);
    for (int pieceIdx = 0; pieceIdx < 12; pieceIdx++) {
      for (int squareOrdinal = 0; squareOrdinal < 64; squareOrdinal++) {
        PIECE_SQUARE_KEYS[pieceIdx][squareOrdinal] = rng.nextLong();
      }
    }
    BLACK_TO_MOVE_KEY = rng.nextLong();
    for (int i = 0; i < CASTLING_KEYS.length; i++) {
      CASTLING_KEYS[i] = rng.nextLong();
    }
    for (int i = 0; i < EN_PASSANT_FILE_KEYS.length; i++) {
      EN_PASSANT_FILE_KEYS[i] = rng.nextLong();
    }
  }

  private ZobristKeys() {
  }

  public static long pieceSquare(Piece piece, Square square) {
    if (piece == Piece.NONE) {
      throw new IllegalArgumentException("Piece.NONE has no Zobrist key");
    }
    if (square == Square.NONE) {
      throw new IllegalArgumentException("Square.NONE has no Zobrist key");
    }
    return PIECE_SQUARE_KEYS[piece.ordinal()][square.ordinal()];
  }

  public static long blackToMove() {
    return BLACK_TO_MOVE_KEY;
  }

  public static long castlingWhiteKingSide() {
    return CASTLING_KEYS[WHITE_KING_SIDE];
  }

  public static long castlingWhiteQueenSide() {
    return CASTLING_KEYS[WHITE_QUEEN_SIDE];
  }

  public static long castlingBlackKingSide() {
    return CASTLING_KEYS[BLACK_KING_SIDE];
  }

  public static long castlingBlackQueenSide() {
    return CASTLING_KEYS[BLACK_QUEEN_SIDE];
  }

  public static long enPassantFile(int fileIndex) {
    if (fileIndex < 0 || fileIndex >= 8) {
      throw new IllegalArgumentException("fileIndex out of range: " + fileIndex);
    }
    return EN_PASSANT_FILE_KEYS[fileIndex];
  }
}
