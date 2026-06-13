// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.bitboard;

import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import io.github.dlbbld.ashlarchess.board.enums.CastlingMove;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;

/**
 * Twelve-bitboard piece-placement representation: one {@code long} per real
 * {@link io.github.dlbbld.ashlarchess.board.enums.Piece} value, each bit indexed by
 * {@link io.github.dlbbld.ashlarchess.board.enums.Square#ordinal()} (little-endian rank-file:
 * {@code A1 = 0, B1 = 1, ..., H8 = 63}). Field order matches the
 * {@link io.github.dlbbld.ashlarchess.board.enums.Piece#REAL} enum order.
 *
 * <p>
 * <b>Construction invariant:</b> the twelve piece bitboards are pairwise disjoint - no square may carry two pieces. The
 * compact constructor enforces this, so any reachable {@code BitboardPosition} is guaranteed consistent under every
 * query method. Attempting to construct one with overlapping bitboards is rejected with
 * {@link IllegalArgumentException}.
 *
 * <p>
 * Built alongside {@code StaticPosition} and verified bit-exact against it via differential testing across the full
 * PGN/FEN corpus. After the role-inversion release, {@code StaticPosition} lives in {@code src/test/} as the permanent
 * differential-test oracle; this record is the production representation. See {@code tasks.md} and the package-level
 * Javadoc for the governing Project Invariant.
 *
 * <p>
 * <b>Deliberate "records carry data" exception:</b> unlike every other record in the codebase, this one carries its
 * move-generation / king-safety engine ({@code afterMove}, {@code legalMoves}, {@code attackedSquares},
 * {@code isInCheck}, {@code pinRay}, {@code pinnedPieces}, …) rather than delegating to a utility, for hot-path
 * allocation reasons on the production move-generation path. This is a conscious, bounded exception - see
 * {@code coding-conventions.md} - and not a pattern to copy onto other records.
 */
public record BitboardPosition(long whitePawns, long whiteRooks, long whiteKnights, long whiteBishops, long whiteQueens,
    long whiteKings, long blackPawns, long blackRooks, long blackKnights, long blackBishops, long blackQueens,
    long blackKings) {

  public BitboardPosition {
    final long union = whitePawns | whiteRooks | whiteKnights | whiteBishops | whiteQueens | whiteKings | blackPawns
        | blackRooks | blackKnights | blackBishops | blackQueens | blackKings;
    final int sumOfBitCounts = Long.bitCount(whitePawns) + Long.bitCount(whiteRooks) + Long.bitCount(whiteKnights)
        + Long.bitCount(whiteBishops) + Long.bitCount(whiteQueens) + Long.bitCount(whiteKings)
        + Long.bitCount(blackPawns) + Long.bitCount(blackRooks) + Long.bitCount(blackKnights)
        + Long.bitCount(blackBishops) + Long.bitCount(blackQueens) + Long.bitCount(blackKings);
    if (sumOfBitCounts != Long.bitCount(union)) {
      throw new IllegalArgumentException("Piece bitboards must be pairwise disjoint (no square may carry two pieces)");
    }
  }

  // Initial position bit layout (little-endian rank-file: A1 = bit 0, H8 = bit 63):
  // white pawns on rank 2 (bits 8-15) -> 0x000000000000FF00L
  // white rooks on a1, h1 (bits 0, 7) -> 0x0000000000000081L
  // white knights on b1, g1 (bits 1, 6) -> 0x0000000000000042L
  // white bishops on c1, f1 (bits 2, 5) -> 0x0000000000000024L
  // white queen on d1 (bit 3) -> 0x0000000000000008L
  // white king on e1 (bit 4) -> 0x0000000000000010L
  // black pawns on rank 7 (bits 48-55) -> 0x00FF000000000000L
  // black rooks on a8, h8 (bits 56, 63) -> 0x8100000000000000L
  // black knights on b8, g8 (bits 57, 62) -> 0x4200000000000000L
  // black bishops on c8, f8 (bits 58, 61) -> 0x2400000000000000L
  // black queen on d8 (bit 59) -> 0x0800000000000000L
  // black king on e8 (bit 60) -> 0x1000000000000000L
  public static final BitboardPosition INITIAL_POSITION = new BitboardPosition(0x000000000000FF00L, 0x0000000000000081L,
      0x0000000000000042L, 0x0000000000000024L, 0x0000000000000008L, 0x0000000000000010L, 0x00FF000000000000L,
      0x8100000000000000L, 0x4200000000000000L, 0x2400000000000000L, 0x0800000000000000L, 0x1000000000000000L);

  public static final BitboardPosition EMPTY_POSITION = new BitboardPosition(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L,
      0L);

  public Piece get(Square square) {
    final long bit = bitFor(square);
    if ((whitePawns & bit) != 0L) {
      return Piece.WHITE_PAWN;
    }
    if ((whiteRooks & bit) != 0L) {
      return Piece.WHITE_ROOK;
    }
    if ((whiteKnights & bit) != 0L) {
      return Piece.WHITE_KNIGHT;
    }
    if ((whiteBishops & bit) != 0L) {
      return Piece.WHITE_BISHOP;
    }
    if ((whiteQueens & bit) != 0L) {
      return Piece.WHITE_QUEEN;
    }
    if ((whiteKings & bit) != 0L) {
      return Piece.WHITE_KING;
    }
    if ((blackPawns & bit) != 0L) {
      return Piece.BLACK_PAWN;
    }
    if ((blackRooks & bit) != 0L) {
      return Piece.BLACK_ROOK;
    }
    if ((blackKnights & bit) != 0L) {
      return Piece.BLACK_KNIGHT;
    }
    if ((blackBishops & bit) != 0L) {
      return Piece.BLACK_BISHOP;
    }
    if ((blackQueens & bit) != 0L) {
      return Piece.BLACK_QUEEN;
    }
    if ((blackKings & bit) != 0L) {
      return Piece.BLACK_KING;
    }
    return Piece.NONE;
  }

  public boolean isEmpty(Square square) {
    return get(square) == Piece.NONE;
  }

  public long occupied() {
    return whitePawns | whiteRooks | whiteKnights | whiteBishops | whiteQueens | whiteKings | blackPawns | blackRooks
        | blackKnights | blackBishops | blackQueens | blackKings;
  }

  public long occupied(Side side) {
    return switch (side) {
      case WHITE -> whitePawns | whiteRooks | whiteKnights | whiteBishops | whiteQueens | whiteKings;
      case BLACK -> blackPawns | blackRooks | blackKnights | blackBishops | blackQueens | blackKings;
      case NONE -> throw new IllegalArgumentException("Side.NONE has no occupancy");
      default -> throw new IllegalArgumentException();
    };
  }

  /**
   * Convenience predicate: does {@code square} carry a piece of {@code side} and {@code pieceType}? Equivalent to
   * {@code get(square) == Piece.calculate(side, pieceType)} but avoids constructing the {@code Piece} value.
   */
  public boolean isOwnPiece(Square square, Side side, PieceType pieceType) {
    if (side != Side.WHITE && side != Side.BLACK) {
      throw new IllegalArgumentException("isOwnPiece requires Side.WHITE or Side.BLACK, got " + side);
    }
    if (pieceType == PieceType.NONE) {
      throw new IllegalArgumentException("isOwnPiece requires a real piece type, got NONE");
    }
    return get(square) == Piece.calculate(side, pieceType);
  }

  /**
   * Square of {@code side}'s king. Throws {@link IllegalStateException} if there is no king of {@code side} on the
   * board, matching the reference {@code StaticPositionUtility.calculateKingSquare}. Standard chess assumes one king
   * per side; the implementation returns the lowest-ordinal king bit when more than one is present.
   */
  public Square kingSquare(Side side) {
    if (side != Side.WHITE && side != Side.BLACK) {
      throw new IllegalArgumentException("kingSquare requires Side.WHITE or Side.BLACK, got " + side);
    }
    final long kings = side == Side.WHITE ? whiteKings : blackKings;
    if (kings == 0L) {
      throw new IllegalStateException("No king of side " + side + " on the board");
    }
    return Nulls.get(Square.REAL, Long.numberOfTrailingZeros(kings));
  }

  /**
   * Pseudo-legal target squares for the piece on {@code fromSquare}: squares the piece could move to, considering own
   * piece blocking and slider line-of-sight, but NOT king-safety. Mirrors the reference
   * {@code AbstractPotentialToSquares.calculatePotentialToSquare} surface used by the SAN error-reporting layer.
   *
   * <p>
   * For pawns, includes forward advances (single + double when applicable) and diagonal captures against opponent
   * pieces - <em>excluding</em> the opponent king (matching the reference) - and to the EP target square. For other
   * pieces, includes the standard pseudo-legal target set: own pieces are blocked, opponent pieces are capturable
   * (including the opponent king at this level - king-capture filtering happens at the legal-move-classification level,
   * not here).
   *
   * <p>
   * Returns an empty set if {@code fromSquare} is empty. {@code enPassantBit} is the single-bit bitboard of the EP
   * target square, or {@code 0L} if no EP is available.
   */
  public Set<Square> potentialToSquares(Square fromSquare, long enPassantBit) {
    if (fromSquare == Square.NONE) {
      throw new IllegalArgumentException("The NONE square does not belong to the board");
    }
    final Piece piece = get(fromSquare);
    if (piece == Piece.NONE) {
      return new TreeSet<>();
    }
    final Side side = piece.getSide();
    final long ownPieces = occupied(side);
    final int fromOrdinal = fromSquare.ordinal();
    final long targets = switch (piece.getPieceType()) {
      case KNIGHT -> KnightMoves.targets(fromSquare, ownPieces);
      case KING -> KingMoves.targets(fromSquare, ownPieces);
      case BISHOP -> BishopMoves.targets(fromOrdinal, occupied(), ownPieces);
      case ROOK -> RookMoves.targets(fromOrdinal, occupied(), ownPieces);
      case QUEEN -> QueenMoves.targets(fromOrdinal, occupied(), ownPieces);
      case PAWN -> pawnPotentialTargets(fromSquare, fromOrdinal, side, enPassantBit);
      case NONE -> throw new IllegalStateException("Unreachable - Piece.NONE filtered above");
      default -> throw new IllegalArgumentException();
    };
    return BitboardPositionUtility.toSquareSet(targets);
  }

  private long pawnPotentialTargets(Square fromSquare, int fromOrdinal, Side side, long enPassantBit) {
    final long occ = occupied();
    final long opponentPieces = occupied(side.getOppositeSide());
    final long opponentKings = side == Side.WHITE ? blackKings : whiteKings;
    // Forward pushes: single + double, blocked by occupancy.
    final long pushTargets = PawnMoves.pushes(fromOrdinal, occ, side);
    // Diagonal captures: opponent pieces excluding king (the reference excludes king on pawn diagonals).
    final long pawnAttacks = PawnAttacks.attacks(fromSquare, side);
    final long captureTargets = pawnAttacks & (opponentPieces & ~opponentKings);
    final long epTarget = pawnAttacks & enPassantBit;
    return pushTargets | captureTargets | epTarget;
  }

  /**
   * Union of all squares attacked / defended by {@code side}'s pieces, in the same "isAllowOwnPiece = true" sense the
   * reference uses: includes squares occupied by own pieces (those are defended). Differential-tested against
   * {@code AbstractAttackedSquares.calculateAttackedSquares}.
   */
  public long attackedSquares(Side side) {
    return attackedSquares(side, occupied());
  }

  /**
   * Same as {@link #attackedSquares(Side)} but computed against a caller-supplied occupancy mask rather than the
   * record's own {@link #occupied()}. Used by king-safety calculations in legal-move generation, which need to ask
   * "what would the opponent attack if my king were not on the board" so a slider's ray correctly projects through the
   * king's current square onto squares the king might move to.
   *
   * <p>
   * Only sliding-piece attacks (bishop, rook, queen) consult {@code occupiedOverride}; non-sliders (knight, king, pawn)
   * ignore it. Callers wanting "no king" semantics should pass {@code occupied() ^ kingBits}.
   */
  public long attackedSquares(Side side, long occupiedOverride) {
    if (side != Side.WHITE && side != Side.BLACK) {
      throw new IllegalArgumentException("attackedSquares requires Side.WHITE or Side.BLACK, got " + side);
    }
    final boolean white = side == Side.WHITE;
    final long pawns = white ? whitePawns : blackPawns;
    final long knights = white ? whiteKnights : blackKnights;
    final long bishops = white ? whiteBishops : blackBishops;
    final long rooks = white ? whiteRooks : blackRooks;
    final long queens = white ? whiteQueens : blackQueens;
    final long kings = white ? whiteKings : blackKings;

    long attacks = 0L;

    long remaining = pawns;
    while (remaining != 0L) {
      attacks |= PawnAttacks.attacks(Nulls.get(Square.REAL, Long.numberOfTrailingZeros(remaining)), side);
      remaining &= remaining - 1L;
    }

    remaining = knights;
    while (remaining != 0L) {
      attacks |= KnightAttacks.attacks(Nulls.get(Square.REAL, Long.numberOfTrailingZeros(remaining)));
      remaining &= remaining - 1L;
    }

    remaining = bishops;
    while (remaining != 0L) {
      attacks |= BishopAttacks.attacks(Long.numberOfTrailingZeros(remaining), occupiedOverride);
      remaining &= remaining - 1L;
    }

    remaining = rooks;
    while (remaining != 0L) {
      attacks |= RookAttacks.attacks(Long.numberOfTrailingZeros(remaining), occupiedOverride);
      remaining &= remaining - 1L;
    }

    remaining = queens;
    while (remaining != 0L) {
      attacks |= QueenAttacks.attacks(Long.numberOfTrailingZeros(remaining), occupiedOverride);
      remaining &= remaining - 1L;
    }

    remaining = kings;
    while (remaining != 0L) {
      attacks |= KingAttacks.attacks(Nulls.get(Square.REAL, Long.numberOfTrailingZeros(remaining)));
      remaining &= remaining - 1L;
    }

    return attacks;
  }

  /**
   * Returns {@code true} if {@code side}'s king is attacked by any of the opposite side's pieces. A position with no
   * king of the queried side returns {@code false} (no king to be in check) - the reference's
   * {@code StaticPositionUtility.calculateIsCheck} throws in that case, so callers comparing against the reference
   * should restrict comparisons to positions where the king of the queried side exists.
   */
  public boolean isInCheck(Side side) {
    if (side != Side.WHITE && side != Side.BLACK) {
      throw new IllegalArgumentException("isInCheck requires Side.WHITE or Side.BLACK, got " + side);
    }
    final long ownKings = side == Side.WHITE ? whiteKings : blackKings;
    if (ownKings == 0L) {
      return false;
    }
    return (attackedSquares(side.getOppositeSide()) & ownKings) != 0L;
  }

  /**
   * Bitboard of legal non-castling king target squares for {@code side}. A target is legal iff (a) it is a pseudo-legal
   * target - surrounding square not occupied by own piece - and (b) it is not attacked by the opposite side after the
   * king vacates its current square. The "king vacates" part is essential: a slider that the king was blocking would,
   * post-move, project its ray through the king's old square onto squares the king might try to move to (the XRAY
   * case). The implementation removes own kings from the occupied mask before computing opponent attacks.
   *
   * <p>
   * Captures of opponent pieces are included when the captured piece's square is not defended by another opponent
   * piece. Standard chess assumes one king per side; the implementation tolerates multiple kings (taking the union of
   * legal targets from each) for the differential test's robustness.
   */
  public long legalKingTargets(Side side) {
    if (side != Side.WHITE && side != Side.BLACK) {
      throw new IllegalArgumentException("legalKingTargets requires Side.WHITE or Side.BLACK, got " + side);
    }
    final long ownKings = side == Side.WHITE ? whiteKings : blackKings;
    if (ownKings == 0L) {
      return 0L;
    }
    final long ownPieces = occupied(side);
    final long occupiedWithoutOwnKings = occupied() ^ ownKings;
    final long opponentAttacks = attackedSquares(side.getOppositeSide(), occupiedWithoutOwnKings);

    long legalTargets = 0L;
    long remaining = ownKings;
    while (remaining != 0L) {
      final Square kingSquare = Nulls.get(Square.REAL, Long.numberOfTrailingZeros(remaining));
      legalTargets |= KingMoves.targets(kingSquare, ownPieces) & ~opponentAttacks;
      remaining &= remaining - 1L;
    }
    return legalTargets;
  }

  /**
   * Pin ray for the piece on {@code pinnedSquare} relative to {@code side}'s king: the squares from king (exclusive) to
   * pinner (inclusive) along the line through {@code pinnedSquare}. Returns {@code 0L} if the piece is not pinned. The
   * pinned piece's legal-move filter is {@code pseudoLegal & pinRay(pinnedSquare, side)} - the piece may move along the
   * pin line (capturing the pinner is allowed; vacating the line is not).
   *
   * <p>
   * Pinning requires: pinned piece on a file / rank / diagonal with own king, no other piece between king and the
   * pinned piece, and the first piece beyond the pinned piece (in the same direction) is an opposite-side slider whose
   * move type matches the direction (bishop / queen on a diagonal; rook / queen on a file or rank). Standard chess
   * one-king-per-side assumed; with no own king of {@code side}, returns {@code 0L}.
   */
  public long pinRay(Square pinnedSquare, Side side) {
    if (pinnedSquare == Square.NONE) {
      throw new IllegalArgumentException("The NONE square does not belong to the board");
    }
    if (side != Side.WHITE && side != Side.BLACK) {
      throw new IllegalArgumentException("pinRay requires Side.WHITE or Side.BLACK, got " + side);
    }
    final long ownKings = side == Side.WHITE ? whiteKings : blackKings;
    if (ownKings == 0L) {
      return 0L;
    }
    final int kingOrdinal = Long.numberOfTrailingZeros(ownKings);
    final int pinnedOrdinal = pinnedSquare.ordinal();
    final int kingFile = kingOrdinal % 8;
    final int kingRank = kingOrdinal / 8;
    final int pinnedFile = pinnedOrdinal % 8;
    final int pinnedRank = pinnedOrdinal / 8;
    final int fileDiff = pinnedFile - kingFile;
    final int rankDiff = pinnedRank - kingRank;
    if (fileDiff == 0 && rankDiff == 0) {
      return 0L;
    }
    final boolean onFile = fileDiff == 0;
    final boolean onRank = rankDiff == 0;
    final boolean onDiagonal = !onFile && !onRank && Math.abs(fileDiff) == Math.abs(rankDiff);
    if (!onFile && !onRank && !onDiagonal) {
      return 0L;
    }

    final int fileStep = Integer.signum(fileDiff);
    final int rankStep = Integer.signum(rankDiff);
    final long occ = occupied();

    int file = kingFile + fileStep;
    int rank = kingRank + rankStep;
    while (file != pinnedFile || rank != pinnedRank) {
      if ((occ & 1L << rank * 8 + file) != 0L) {
        return 0L;
      }
      file += fileStep;
      rank += rankStep;
    }

    file = pinnedFile + fileStep;
    rank = pinnedRank + rankStep;
    while (file >= 0 && file < 8 && rank >= 0 && rank < 8) {
      final int beyondOrdinal = rank * 8 + file;
      if ((occ & 1L << beyondOrdinal) != 0L) {
        final Piece beyondPiece = get(Nulls.get(Square.REAL, beyondOrdinal));
        if (beyondPiece.getSide() == side) {
          return 0L;
        }
        final PieceType beyondPieceType = beyondPiece.getPieceType();
        final boolean diagonalMover = beyondPieceType == PieceType.BISHOP || beyondPieceType == PieceType.QUEEN;
        final boolean orthogonalMover = beyondPieceType == PieceType.ROOK || beyondPieceType == PieceType.QUEEN;
        if (onDiagonal && diagonalMover || (onFile || onRank) && orthogonalMover) {
          return inclusiveRayFromKing(kingOrdinal, beyondOrdinal);
        }
        return 0L;
      }
      file += fileStep;
      rank += rankStep;
    }
    return 0L;
  }

  /**
   * Bitboard of own pieces of {@code side} that are pinned to their king. A piece is pinned iff
   * {@link #pinRay(Square, Side)} returns a non-zero ray for that square.
   */
  public long pinnedPieces(Side side) {
    if (side != Side.WHITE && side != Side.BLACK) {
      throw new IllegalArgumentException("pinnedPieces requires Side.WHITE or Side.BLACK, got " + side);
    }
    final long ownKings = side == Side.WHITE ? whiteKings : blackKings;
    if (ownKings == 0L) {
      return 0L;
    }
    final long ownNonKings = occupied(side) & ~ownKings;
    long pinned = 0L;
    long remaining = ownNonKings;
    while (remaining != 0L) {
      final long pieceBit = Long.lowestOneBit(remaining);
      final Square pieceSquare = Nulls.get(Square.REAL, Long.numberOfTrailingZeros(pieceBit));
      if (pinRay(pieceSquare, side) != 0L) {
        pinned |= pieceBit;
      }
      remaining &= ~pieceBit;
    }
    return pinned;
  }

  /**
   * Full legal non-castling move generation. Returns the set of legal {@link MoveSpecification}s for {@code side}'s
   * pieces excluding castling - castling lives on {@link io.github.dlbbld.ashlarchess.board.Board} together with the
   * castling- rights state. The {@code enPassantBit} parameter is the single-bit bitboard of the en-passant target
   * square (or {@code 0L} if no EP is available to {@code side}); the bitboard layer is stateless about whose turn it
   * is.
   *
   * <p>
   * Algorithm: generate the king's legal targets via {@link #legalKingTargets}; if double check, only king moves are
   * legal. Otherwise compute the check-evasion mask (the squares non-king pieces must land on: the checker square plus
   * the squares between king and a sliding checker). For each own non-king piece, take its pseudo-legal targets,
   * intersect with the check-evasion mask and the pin ray (if pinned), and emit {@link MoveSpecification}s. Pawn
   * promotion expands each rank-1/rank-8 target into four moves; en-passant is special-cased for the rank-pin edge case
   * where capturing the EP pawn could expose own king to a rook or queen along the rank.
   */
  public Set<MoveSpecification> legalMoves(Side side, long enPassantBit) {
    final Set<MoveSpecification> moves = new TreeSet<>();
    legalMovesInto(moves::add, side, enPassantBit);
    return moves;
  }

  /**
   * Sink-based variant of {@link #legalMoves(Side, long)}: emits the same legal {@link MoveSpecification}s to
   * {@code sink} as {@code legalMoves} would put into its returned set, but in the move generator's natural traversal
   * order rather than {@link MoveSpecification#compareTo} order - no intermediate {@link Set} / {@link TreeSet} is
   * allocated. The set-based {@link #legalMoves(Side, long)} wraps this with a {@link TreeSet}-collecting sink so the
   * existing public contract is preserved verbatim. Used by the helpmate search hot path to fill a per-depth reusable
   * move buffer without allocating a sorted-set scaffold per ply.
   */
  public void legalMovesInto(Consumer<MoveSpecification> sink, Side side, long enPassantBit) {
    if (side != Side.WHITE && side != Side.BLACK) {
      throw new IllegalArgumentException("legalMovesInto requires Side.WHITE or Side.BLACK, got " + side);
    }
    final long ownKings = side == Side.WHITE ? whiteKings : blackKings;
    if (ownKings == 0L) {
      return;
    }
    final int kingOrdinal = Long.numberOfTrailingZeros(ownKings);
    final Square kingSquare = Nulls.get(Square.REAL, kingOrdinal);

    final long kingTargets = legalKingTargets(side);
    emitTargetsAsMoves(sink, kingSquare, kingTargets);

    final long checkers = attackersTo(kingSquare, side.getOppositeSide());
    final int checkerCount = Long.bitCount(checkers);
    if (checkerCount >= 2) {
      return;
    }

    final long checkEvasionMask;
    if (checkerCount == 1) {
      final int checkerOrdinal = Long.numberOfTrailingZeros(checkers);
      final Piece checker = get(Nulls.get(Square.REAL, checkerOrdinal));
      final long betweenMask = isSlider(checker.getPieceType()) ? squaresBetween(kingOrdinal, checkerOrdinal) : 0L;
      checkEvasionMask = checkers | betweenMask;
    } else {
      checkEvasionMask = -1L;
    }

    final long ownPieces = occupied(side);
    final long ownNonKings = ownPieces & ~ownKings;
    final long occ = occupied();
    final long opponentPieces = occupied(side.getOppositeSide());

    long remaining = ownNonKings;
    while (remaining != 0L) {
      final int fromOrdinal = Long.numberOfTrailingZeros(remaining);
      final Square fromSquare = Nulls.get(Square.REAL, fromOrdinal);
      final Piece piece = get(fromSquare);
      final long pinRay = pinRay(fromSquare, side);
      final long pinFilter = pinRay == 0L ? -1L : pinRay;
      final long combinedMask = checkEvasionMask & pinFilter;

      switch (piece.getPieceType()) {
        case KNIGHT -> emitTargetsAsMoves(sink, fromSquare, KnightMoves.targets(fromSquare, ownPieces) & combinedMask);
        case BISHOP -> emitTargetsAsMoves(sink, fromSquare,
            BishopMoves.targets(fromOrdinal, occ, ownPieces) & combinedMask);
        case ROOK -> emitTargetsAsMoves(sink, fromSquare,
            RookMoves.targets(fromOrdinal, occ, ownPieces) & combinedMask);
        case QUEEN -> emitTargetsAsMoves(sink, fromSquare,
            QueenMoves.targets(fromOrdinal, occ, ownPieces) & combinedMask);
        case PAWN -> emitPawnMoves(sink, fromSquare, fromOrdinal, side, occ, opponentPieces, enPassantBit, combinedMask,
            checkers, checkerCount, kingOrdinal, pinFilter);
        default -> throw new IllegalArgumentException();
      }
      remaining &= remaining - 1L;
    }
  }

  private void emitPawnMoves(Consumer<MoveSpecification> sink, Square fromSquare, int fromOrdinal, Side side, long occ,
      long opponentPieces, long enPassantBit, long combinedMask, long checkers, int checkerCount, int kingOrdinal,
      long pinFilter) {
    final long pushTargets = PawnMoves.pushes(fromOrdinal, occ, side) & combinedMask;
    final long regularCaptureTargets = PawnMoves.captures(fromOrdinal, opponentPieces, 0L, side) & combinedMask;

    long epCaptureTarget = 0L;
    if (enPassantBit != 0L) {
      final long pawnDiagonalAttacks = PawnAttacks.attacks(fromSquare, side);
      if ((pawnDiagonalAttacks & enPassantBit) != 0L) {
        final long capturedPawnBit = side == Side.WHITE ? enPassantBit >>> 8 : enPassantBit << 8;
        final boolean epEvadesCheck = checkerCount == 0 || (enPassantBit & combinedMask) != 0L
            || checkerCount == 1 && capturedPawnBit == checkers;
        final boolean epOnPinRay = (enPassantBit & pinFilter) != 0L;
        if (epEvadesCheck && epOnPinRay
            && !epExposesKing(fromOrdinal, enPassantBit, capturedPawnBit, kingOrdinal, side)) {
          epCaptureTarget = enPassantBit;
        }
      }
    }

    emitPawnTargetsWithPromotion(sink, fromSquare, pushTargets);
    emitPawnTargetsWithPromotion(sink, fromSquare, regularCaptureTargets | epCaptureTarget);
  }

  private boolean epExposesKing(int fromOrdinal, long enPassantBit, long capturedPawnBit, int kingOrdinal, Side side) {
    final long fromBit = 1L << fromOrdinal;
    final long occAfterEp = occupied() & ~fromBit & ~capturedPawnBit | enPassantBit;

    final Side opp = side.getOppositeSide();
    final long oppPawns = (opp == Side.WHITE ? whitePawns : blackPawns) & ~capturedPawnBit;
    final long oppKnights = opp == Side.WHITE ? whiteKnights : blackKnights;
    final long oppBishops = opp == Side.WHITE ? whiteBishops : blackBishops;
    final long oppRooks = opp == Side.WHITE ? whiteRooks : blackRooks;
    final long oppQueens = opp == Side.WHITE ? whiteQueens : blackQueens;
    final long oppKings = opp == Side.WHITE ? whiteKings : blackKings;
    final Square kingSquare = Nulls.get(Square.REAL, kingOrdinal);

    if ((oppPawns & PawnAttacks.attacks(kingSquare, side)) != 0L) {
      return true;
    }
    if ((oppKnights & KnightAttacks.attacks(kingSquare)) != 0L) {
      return true;
    }
    if (((oppBishops | oppQueens) & BishopAttacks.attacks(kingOrdinal, occAfterEp)) != 0L) {
      return true;
    }
    if (((oppRooks | oppQueens) & RookAttacks.attacks(kingOrdinal, occAfterEp)) != 0L) {
      return true;
    }
    if ((oppKings & KingAttacks.attacks(kingSquare)) != 0L) {
      return true;
    }
    return false;
  }

  private static void emitTargetsAsMoves(Consumer<MoveSpecification> sink, Square fromSquare, long targets) {
    long remaining = targets;
    while (remaining != 0L) {
      final Square toSquare = Nulls.get(Square.REAL, Long.numberOfTrailingZeros(remaining));
      sink.accept(new MoveSpecification(fromSquare, toSquare));
      remaining &= remaining - 1L;
    }
  }

  private static void emitPawnTargetsWithPromotion(Consumer<MoveSpecification> sink, Square fromSquare, long targets) {
    long remaining = targets;
    while (remaining != 0L) {
      final int toOrdinal = Long.numberOfTrailingZeros(remaining);
      final Square toSquare = Nulls.get(Square.REAL, toOrdinal);
      final int toRank = toOrdinal / 8;
      if (toRank == 0 || toRank == 7) {
        for (final PromotionPieceType promotion : PromotionPieceType.REAL) {
          sink.accept(new MoveSpecification(fromSquare, toSquare, promotion));
        }
      } else {
        sink.accept(new MoveSpecification(fromSquare, toSquare));
      }
      remaining &= remaining - 1L;
    }
  }

  private static boolean isSlider(PieceType pieceType) {
    return pieceType == PieceType.BISHOP || pieceType == PieceType.ROOK || pieceType == PieceType.QUEEN;
  }

  private static long squaresBetween(int sq1, int sq2) {
    final int file1 = sq1 % 8;
    final int rank1 = sq1 / 8;
    final int file2 = sq2 % 8;
    final int rank2 = sq2 / 8;
    final int fileDiff = file2 - file1;
    final int rankDiff = rank2 - rank1;
    if (fileDiff == 0 && rankDiff == 0) {
      return 0L;
    }
    final boolean onFile = fileDiff == 0;
    final boolean onRank = rankDiff == 0;
    final boolean onDiagonal = !onFile && !onRank && Math.abs(fileDiff) == Math.abs(rankDiff);
    if (!onFile && !onRank && !onDiagonal) {
      return 0L;
    }
    final int fileStep = Integer.signum(fileDiff);
    final int rankStep = Integer.signum(rankDiff);
    long result = 0L;
    int file = file1 + fileStep;
    int rank = rank1 + rankStep;
    while (file != file2 || rank != rank2) {
      result |= 1L << rank * 8 + file;
      file += fileStep;
      rank += rankStep;
    }
    return result;
  }

  /**
   * Immutable make-move: returns the {@code BitboardPosition} that results from applying {@code moveSpec} to this
   * position with {@code movingSide} as the moving side. Handles regular moves, captures, en-passant capture,
   * promotions (the destination piece becomes {@code moveSpec.promotionPieceType()} fixed to {@code movingSide}), and
   * the piece-movement part of castling (king and rook both move).
   *
   * <p>
   * Castling rights, en-passant target square, side-to-move, and the halfmove clock and fullmove number are intentionally NOT
   * updated here - they live on {@link io.github.dlbbld.ashlarchess.board.Board} /
   * {@link io.github.dlbbld.ashlarchess.common.model.DynamicPosition}. This is the piece-placement-only equivalent of
   * {@code StaticPositionUtility.createPositionAfterMove}.
   *
   * <p>
   * The bitboard layer is intentionally stateless about whose turn it is. Callers pass {@code movingSide} explicitly -
   * for castling, this determines which king/rook pair moves; for non-castling moves, it determines the promotion
   * piece's side and the direction of en-passant capture.
   */
  public BitboardPosition afterMove(MoveSpecification moveSpec, Side movingSide) {
    if (movingSide != Side.WHITE && movingSide != Side.BLACK) {
      throw new IllegalArgumentException("afterMove requires Side.WHITE or Side.BLACK, got " + movingSide);
    }
    if (moveSpec.castlingMove() != CastlingMove.NONE) {
      return afterCastling(moveSpec.castlingMove(), movingSide);
    }
    final Square from = moveSpec.fromSquare();
    final Square to = moveSpec.toSquare();
    final Piece movingPiece = get(from);
    final long fromBit = 1L << from.ordinal();
    final long toBit = 1L << to.ordinal();

    final Piece capturedPiece;
    final long capturedBit;
    if ((toBit & occupied()) != 0L) {
      capturedPiece = get(to);
      capturedBit = toBit;
    } else if (movingPiece.getPieceType() == PieceType.PAWN && from.getFile() != to.getFile()) {
      // En-passant: captured pawn sits on the same rank as the capturing pawn, file matching `to`.
      final int capturedOrdinal = movingSide == Side.WHITE ? to.ordinal() - 8 : to.ordinal() + 8;
      capturedBit = 1L << capturedOrdinal;
      capturedPiece = movingSide == Side.WHITE ? Piece.BLACK_PAWN : Piece.WHITE_PAWN;
    } else {
      capturedPiece = Piece.NONE;
      capturedBit = 0L;
    }

    final PromotionPieceType promotion = moveSpec.promotionPieceType();
    final Piece destPiece = promotion == PromotionPieceType.NONE ? movingPiece
        : Piece.calculate(movingSide, promotion.getPieceType());

    final long[] pieces = currentPieceBitboards();
    toggleBit(pieces, movingPiece, fromBit);
    toggleBit(pieces, capturedPiece, capturedBit);
    toggleBit(pieces, destPiece, toBit);
    return fromPieceBitboards(pieces);
  }

  private BitboardPosition afterCastling(CastlingMove castlingMove, Side movingSide) {
    final int kingFromOrdinal;
    final int kingToOrdinal;
    final int rookFromOrdinal;
    final int rookToOrdinal;
    if (movingSide == Side.WHITE) {
      if (castlingMove == CastlingMove.KING_SIDE) {
        kingFromOrdinal = Square.E1.ordinal();
        kingToOrdinal = Square.G1.ordinal();
        rookFromOrdinal = Square.H1.ordinal();
        rookToOrdinal = Square.F1.ordinal();
      } else {
        kingFromOrdinal = Square.E1.ordinal();
        kingToOrdinal = Square.C1.ordinal();
        rookFromOrdinal = Square.A1.ordinal();
        rookToOrdinal = Square.D1.ordinal();
      }
    } else if (castlingMove == CastlingMove.KING_SIDE) {
      kingFromOrdinal = Square.E8.ordinal();
      kingToOrdinal = Square.G8.ordinal();
      rookFromOrdinal = Square.H8.ordinal();
      rookToOrdinal = Square.F8.ordinal();
    } else {
      kingFromOrdinal = Square.E8.ordinal();
      kingToOrdinal = Square.C8.ordinal();
      rookFromOrdinal = Square.A8.ordinal();
      rookToOrdinal = Square.D8.ordinal();
    }
    final Piece kingPiece = movingSide == Side.WHITE ? Piece.WHITE_KING : Piece.BLACK_KING;
    final Piece rookPiece = movingSide == Side.WHITE ? Piece.WHITE_ROOK : Piece.BLACK_ROOK;

    final long[] pieces = currentPieceBitboards();
    toggleBit(pieces, kingPiece, 1L << kingFromOrdinal);
    toggleBit(pieces, kingPiece, 1L << kingToOrdinal);
    toggleBit(pieces, rookPiece, 1L << rookFromOrdinal);
    toggleBit(pieces, rookPiece, 1L << rookToOrdinal);
    return fromPieceBitboards(pieces);
  }

  /**
   * Returns a new {@code BitboardPosition} in which {@code piece} is relocated from {@code from} to {@code to}. Pre:
   * {@code from} carries {@code piece}, {@code to} is empty. Used for hypothetical-position construction outside the
   * regular move pipeline - e.g. FEN-level validation rewinding a pawn two-square advance to its starting square to
   * check the prior position's legality.
   */
  public BitboardPosition withRelocatedPiece(Piece piece, Square from, Square to) {
    if (piece == Piece.NONE) {
      throw new IllegalArgumentException("withRelocatedPiece requires a real piece, got NONE");
    }
    if (get(from) != piece) {
      throw new IllegalArgumentException(
          "From square " + from + " does not carry " + piece + " (actual: " + get(from) + ")");
    }
    if (!isEmpty(to)) {
      throw new IllegalArgumentException("To square " + to + " is not empty (carries " + get(to) + ")");
    }
    final long[] pieces = currentPieceBitboards();
    toggleBit(pieces, piece, 1L << from.ordinal());
    toggleBit(pieces, piece, 1L << to.ordinal());
    return fromPieceBitboards(pieces);
  }

  private long[] currentPieceBitboards() {
    return new long[] { whitePawns, whiteRooks, whiteKnights, whiteBishops, whiteQueens, whiteKings, blackPawns,
        blackRooks, blackKnights, blackBishops, blackQueens, blackKings };
  }

  private static BitboardPosition fromPieceBitboards(long[] pieces) {
    return new BitboardPosition(pieces[0], pieces[1], pieces[2], pieces[3], pieces[4], pieces[5], pieces[6], pieces[7],
        pieces[8], pieces[9], pieces[10], pieces[11]);
  }

  private static void toggleBit(long[] pieces, Piece piece, long bit) {
    final int index = pieceIndex(piece);
    if (index >= 0) {
      pieces[index] ^= bit;
    }
  }

  private static int pieceIndex(Piece piece) {
    return switch (piece) {
      case WHITE_PAWN -> 0;
      case WHITE_ROOK -> 1;
      case WHITE_KNIGHT -> 2;
      case WHITE_BISHOP -> 3;
      case WHITE_QUEEN -> 4;
      case WHITE_KING -> 5;
      case BLACK_PAWN -> 6;
      case BLACK_ROOK -> 7;
      case BLACK_KNIGHT -> 8;
      case BLACK_BISHOP -> 9;
      case BLACK_QUEEN -> 10;
      case BLACK_KING -> 11;
      case NONE -> -1;
      default -> throw new IllegalArgumentException();
    };
  }

  /**
   * Piece-placement Zobrist hash: XOR of {@link ZobristKeys#pieceSquare} for every (piece, square) pair currently on
   * the board. Side-to-move, castling rights, and en-passant target are intentionally NOT mixed in - those state pieces
   * live on {@code Board} / {@code DynamicPosition} and their Zobrist contributions belong there.
   */
  public long zobristPieces() {
    long hash = 0L;
    hash ^= zobristForPiece(whitePawns, Piece.WHITE_PAWN);
    hash ^= zobristForPiece(whiteRooks, Piece.WHITE_ROOK);
    hash ^= zobristForPiece(whiteKnights, Piece.WHITE_KNIGHT);
    hash ^= zobristForPiece(whiteBishops, Piece.WHITE_BISHOP);
    hash ^= zobristForPiece(whiteQueens, Piece.WHITE_QUEEN);
    hash ^= zobristForPiece(whiteKings, Piece.WHITE_KING);
    hash ^= zobristForPiece(blackPawns, Piece.BLACK_PAWN);
    hash ^= zobristForPiece(blackRooks, Piece.BLACK_ROOK);
    hash ^= zobristForPiece(blackKnights, Piece.BLACK_KNIGHT);
    hash ^= zobristForPiece(blackBishops, Piece.BLACK_BISHOP);
    hash ^= zobristForPiece(blackQueens, Piece.BLACK_QUEEN);
    hash ^= zobristForPiece(blackKings, Piece.BLACK_KING);
    return hash;
  }

  /**
   * Incremental Zobrist update: returns the XOR delta that converts the piece-placement hash of this position (before
   * the move) into the piece-placement hash of the position after the move. That is the below is guaranteed equal to
   * {@code before.afterMove(moveSpec, movingSide).zobristPieces()}:
   *
   * <pre>
   * long afterHash = beforeHash ^ before.hashDelta(moveSpec, movingSide);
   * </pre>
   *
   * <p>
   * Parallel in structure to {@link #afterMove}: identifies the moving piece, captured piece (regular or en-passant),
   * and destination piece (handles promotion), and XORs the corresponding {@link ZobristKeys#pieceSquare} values rather
   * than toggling bitboards. For castling, XORs both king's and rook's from/to keys.
   */
  public long hashDelta(MoveSpecification moveSpec, Side movingSide) {
    if (movingSide != Side.WHITE && movingSide != Side.BLACK) {
      throw new IllegalArgumentException("hashDelta requires Side.WHITE or Side.BLACK, got " + movingSide);
    }
    if (moveSpec.castlingMove() != CastlingMove.NONE) {
      return castlingHashDelta(moveSpec.castlingMove(), movingSide);
    }
    final Square from = moveSpec.fromSquare();
    final Square to = moveSpec.toSquare();
    final Piece movingPiece = get(from);
    final long toBit = 1L << to.ordinal();

    final Piece capturedPiece;
    final Square capturedSquare;
    if ((toBit & occupied()) != 0L) {
      capturedPiece = get(to);
      capturedSquare = to;
    } else if (movingPiece.getPieceType() == PieceType.PAWN && from.getFile() != to.getFile()) {
      final int capturedOrdinal = movingSide == Side.WHITE ? to.ordinal() - 8 : to.ordinal() + 8;
      capturedSquare = Nulls.get(Square.REAL, capturedOrdinal);
      capturedPiece = movingSide == Side.WHITE ? Piece.BLACK_PAWN : Piece.WHITE_PAWN;
    } else {
      capturedPiece = Piece.NONE;
      capturedSquare = Square.NONE;
    }

    final PromotionPieceType promotion = moveSpec.promotionPieceType();
    final Piece destPiece = promotion == PromotionPieceType.NONE ? movingPiece
        : Piece.calculate(movingSide, promotion.getPieceType());

    long delta = ZobristKeys.pieceSquare(movingPiece, from);
    if (capturedPiece != Piece.NONE) {
      delta ^= ZobristKeys.pieceSquare(capturedPiece, capturedSquare);
    }
    delta ^= ZobristKeys.pieceSquare(destPiece, to);
    return delta;
  }

  private static long castlingHashDelta(CastlingMove castlingMove, Side movingSide) {
    final Square kingFrom;
    final Square kingTo;
    final Square rookFrom;
    final Square rookTo;
    if (movingSide == Side.WHITE) {
      if (castlingMove == CastlingMove.KING_SIDE) {
        kingFrom = Square.E1;
        kingTo = Square.G1;
        rookFrom = Square.H1;
        rookTo = Square.F1;
      } else {
        kingFrom = Square.E1;
        kingTo = Square.C1;
        rookFrom = Square.A1;
        rookTo = Square.D1;
      }
    } else if (castlingMove == CastlingMove.KING_SIDE) {
      kingFrom = Square.E8;
      kingTo = Square.G8;
      rookFrom = Square.H8;
      rookTo = Square.F8;
    } else {
      kingFrom = Square.E8;
      kingTo = Square.C8;
      rookFrom = Square.A8;
      rookTo = Square.D8;
    }
    final Piece kingPiece = movingSide == Side.WHITE ? Piece.WHITE_KING : Piece.BLACK_KING;
    final Piece rookPiece = movingSide == Side.WHITE ? Piece.WHITE_ROOK : Piece.BLACK_ROOK;
    return ZobristKeys.pieceSquare(kingPiece, kingFrom) ^ ZobristKeys.pieceSquare(kingPiece, kingTo)
        ^ ZobristKeys.pieceSquare(rookPiece, rookFrom) ^ ZobristKeys.pieceSquare(rookPiece, rookTo);
  }

  private static long zobristForPiece(long bitboard, Piece piece) {
    long hash = 0L;
    long remaining = bitboard;
    while (remaining != 0L) {
      hash ^= ZobristKeys.pieceSquare(piece, Nulls.get(Square.REAL, Long.numberOfTrailingZeros(remaining)));
      remaining &= remaining - 1L;
    }
    return hash;
  }

  private static long inclusiveRayFromKing(int kingOrdinal, int pinnerOrdinal) {
    final int kingFile = kingOrdinal % 8;
    final int kingRank = kingOrdinal / 8;
    final int pinnerFile = pinnerOrdinal % 8;
    final int pinnerRank = pinnerOrdinal / 8;
    final int fileStep = Integer.signum(pinnerFile - kingFile);
    final int rankStep = Integer.signum(pinnerRank - kingRank);
    long result = 0L;
    int file = kingFile + fileStep;
    int rank = kingRank + rankStep;
    while (file != pinnerFile || rank != pinnerRank) {
      result |= 1L << rank * 8 + file;
      file += fileStep;
      rank += rankStep;
    }
    result |= 1L << pinnerOrdinal;
    return result;
  }

  /**
   * Returns {@code true} if {@code mover}'s king would be in check after applying an en-passant capture from
   * {@code fromSquare} to {@code enPassantTargetSquare}. The capturing pawn vacates {@code fromSquare} and lands on
   * {@code enPassantTargetSquare}; the captured opposing pawn is removed from the square one rank behind
   * {@code enPassantTargetSquare} (rank 5 for a {@link Side#WHITE} move to rank 6; rank 4 for a {@link Side#BLACK} move
   * to rank 3).
   *
   * <p>
   * Allocation-free: derives the post-EP occupied mask by XORing single-bit deltas off the current state and runs the
   * standard piece-type-by-piece-type attack check against {@code mover}'s king square. This is the non-allocating
   * equivalent of {@code afterMove(epMoveSpec, mover).isInCheck(mover)} for the EP-specific case - used by EP
   * normalization probes in the helpmate search where allocating a fresh {@link BitboardPosition} for every EP
   * candidate adds up across the recursion. Differential-tested bit-exact against the {@code afterMove(...).isInCheck}
   * reference on every EP candidate in the fixture set.
   *
   * <p>
   * Preconditions (not validated): {@code fromSquare} carries a {@code mover}-side pawn; {@code enPassantTargetSquare}
   * is on the correct rank for a {@code mover} EP target (rank 6 for {@link Side#WHITE}, rank 3 for
   * {@link Side#BLACK}); the square one rank behind {@code enPassantTargetSquare} carries an opposing pawn. If these
   * don't hold, the returned value is undefined. The method does validate that the {@link Square} / {@link Side} inputs
   * are real (not {@link Square#NONE} / {@link Side#NONE}).
   */
  public boolean isInCheckAfterEnPassantCapture(Square fromSquare, Square enPassantTargetSquare, Side mover) {
    if (fromSquare == Square.NONE || enPassantTargetSquare == Square.NONE) {
      throw new IllegalArgumentException("The NONE square does not belong to the board");
    }
    if (mover != Side.WHITE && mover != Side.BLACK) {
      throw new IllegalArgumentException(
          "isInCheckAfterEnPassantCapture requires Side.WHITE or Side.BLACK, got " + mover);
    }
    final long ownKings = mover == Side.WHITE ? whiteKings : blackKings;
    if (ownKings == 0L) {
      return false;
    }
    final long enPassantBit = 1L << enPassantTargetSquare.ordinal();
    final long capturedPawnBit = mover == Side.WHITE ? enPassantBit >>> 8 : enPassantBit << 8;
    final int kingOrdinal = Long.numberOfTrailingZeros(ownKings);
    return epExposesKing(fromSquare.ordinal(), enPassantBit, capturedPawnBit, kingOrdinal, mover);
  }

  /**
   * Convenience overload: extracts {@code fromSquare} and {@code enPassantTargetSquare} from {@code epMoveSpec} and
   * delegates to {@link #isInCheckAfterEnPassantCapture(Square, Square, Side)}. Caller is responsible for ensuring
   * {@code epMoveSpec} represents an en-passant capture (the method does not inspect promotion / castling fields).
   */
  public boolean isInCheckAfterEnPassantCapture(MoveSpecification epMoveSpec, Side mover) {
    return isInCheckAfterEnPassantCapture(epMoveSpec.fromSquare(), epMoveSpec.toSquare(), mover);
  }

  public long attackersTo(Square square, Side side) {
    if (square == Square.NONE) {
      throw new IllegalArgumentException("The NONE square does not belong to the board");
    }
    if (side != Side.WHITE && side != Side.BLACK) {
      throw new IllegalArgumentException("attackersTo requires Side.WHITE or Side.BLACK, got " + side);
    }
    final int squareOrdinal = square.ordinal();
    final long occ = occupied();

    final boolean white = side == Side.WHITE;
    final long pawns = white ? whitePawns : blackPawns;
    final long knights = white ? whiteKnights : blackKnights;
    final long bishops = white ? whiteBishops : blackBishops;
    final long rooks = white ? whiteRooks : blackRooks;
    final long queens = white ? whiteQueens : blackQueens;
    final long kings = white ? whiteKings : blackKings;

    long attackers = 0L;
    attackers |= pawns & PawnAttacks.attacks(square, side.getOppositeSide());
    attackers |= knights & KnightAttacks.attacks(square);
    attackers |= bishops & BishopAttacks.attacks(squareOrdinal, occ);
    attackers |= rooks & RookAttacks.attacks(squareOrdinal, occ);
    attackers |= queens & QueenAttacks.attacks(squareOrdinal, occ);
    attackers |= kings & KingAttacks.attacks(square);
    return attackers;
  }

  private static long bitFor(Square square) {
    if (square == Square.NONE) {
      throw new IllegalArgumentException("The NONE square does not belong to the board");
    }
    return 1L << square.ordinal();
  }
}
