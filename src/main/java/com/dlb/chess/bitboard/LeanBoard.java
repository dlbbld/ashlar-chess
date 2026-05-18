package com.dlb.chess.bitboard;

import java.util.ArrayDeque;
import java.util.Deque;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.CastlingMove;
import com.dlb.chess.board.enums.CastlingRight;
import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.model.LegalMove;
import com.google.common.collect.ImmutableList;

/**
 * Lean position-and-state container for tree search inside the unwinnability / helpmate analyzers. Carries only the
 * state that legal-move generation and check / mate detection actually need: a {@link BitboardPosition}, side-to-move,
 * en-passant target, per-side castling rights, halfmove clock. No SAN/LAN lists, no disambiguation tracking, no full
 * move history, no repetition counts — those live on the rich {@link Board} for live play but are pure cost in a
 * helpmate-search inner loop.
 *
 * <p>
 * {@link #move} pushes the previous state onto an undo stack; {@link #unmove} pops and restores. Snapshot-style undo
 * is intentionally chosen over delta-style for correctness in this initial pass; an incremental variant is a
 * follow-up if profiling shows the snapshot cost matters.
 *
 * <p>
 * Built off {@link Board#getBitboardPosition} for the initial state, so the lean board starts in perfect sync with
 * the corresponding rich Board. After construction the two are independent.
 */
public final class LeanBoard {

  private BitboardPosition bitboardPosition;
  private Side havingMove;
  private Square enPassantTarget;
  private CastlingRight castlingRightWhite;
  private CastlingRight castlingRightBlack;
  private int halfmoveClock;

  private final Deque<UndoEntry> undoStack = new ArrayDeque<>();

  private LeanBoard() {
  }

  public static LeanBoard fromBoard(Board board) {
    final LeanBoard leanBoard = new LeanBoard();
    leanBoard.bitboardPosition = board.getBitboardPosition();
    leanBoard.havingMove = board.getHavingMove();
    leanBoard.enPassantTarget = board.getEnPassantCaptureTargetSquare();
    leanBoard.castlingRightWhite = board.getCastlingRightWhite();
    leanBoard.castlingRightBlack = board.getCastlingRightBlack();
    leanBoard.halfmoveClock = board.getHalfMoveClock();
    return leanBoard;
  }

  public BitboardPosition bitboardPosition() {
    return bitboardPosition;
  }

  public Side havingMove() {
    return havingMove;
  }

  public Square enPassantTarget() {
    return enPassantTarget;
  }

  public CastlingRight castlingRight(Side side) {
    return switch (side) {
      case WHITE -> castlingRightWhite;
      case BLACK -> castlingRightBlack;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public int halfmoveClock() {
    return halfmoveClock;
  }

  /**
   * Sorted list of legal {@link LegalMove} records for the side to move — full piece / capture / kind info, not
   * bare {@link MoveSpecification}s, so a search loop can pass the result straight to {@code Score.score}. Delegates
   * to {@link BitboardLegalMoveFactory#calculateLegalMoves}, which composes bitboard non-castling moves with the
   * castling bridge.
   *
   * <p>
   * Castling cannot be dropped from a tree-search legal-move set: a position whose only escape from check is a
   * castle would otherwise be misclassified as checkmate, and games where the winning line involves castling would
   * be silently truncated.
   *
   * <p>
   * The castling bridge inside {@code BitboardLegalMoveFactory} currently consumes a
   * {@link com.dlb.chess.board.StaticPosition}, derived per call via
   * {@link BitboardPositionUtility#toStaticPosition}. That conversion is per-call cost and is the obvious
   * optimisation target if {@code findHelpMate} profiling shows it dominating; replacing it with a cached
   * StaticPosition or a bitboard-native castling check stays as a follow-on.
   */
  public ImmutableList<LegalMove> legalMoves() {
    final long enPassantBit = enPassantTarget == Square.NONE ? 0L : 1L << enPassantTarget.ordinal();
    return BitboardLegalMoveFactory.calculateLegalMoves(bitboardPosition,
        BitboardPositionUtility.toStaticPosition(bitboardPosition), havingMove, castlingRight(havingMove),
        enPassantBit);
  }

  /**
   * Full-position Zobrist key combining {@link BitboardPosition#zobristPieces} with side-to-move, castling rights,
   * and en-passant file contributions from {@link ZobristKeys}. Suitable as a transposition map key for tree search:
   * two LeanBoard states with the same piece placement, side, castling rights, and EP file produce the same key.
   *
   * <p>
   * The EP contribution uses the raw {@link #enPassantTarget} field — not a normalized "EP only if capturable"
   * version. That means two positions differing only in a phantom EP target hash differently and miss the
   * transposition cache. Acceptable for correctness (cache misses are slower, not wrong); a future optimisation
   * could normalise the EP target via a bitboard adjacency check before mixing it in.
   */
  public long zobristKey() {
    long key = bitboardPosition.zobristPieces();
    if (havingMove == Side.BLACK) {
      key ^= ZobristKeys.blackToMove();
    }
    if (castlingRightWhite.getHasKingSide()) {
      key ^= ZobristKeys.castlingWhiteKingSide();
    }
    if (castlingRightWhite.getHasQueenSide()) {
      key ^= ZobristKeys.castlingWhiteQueenSide();
    }
    if (castlingRightBlack.getHasKingSide()) {
      key ^= ZobristKeys.castlingBlackKingSide();
    }
    if (castlingRightBlack.getHasQueenSide()) {
      key ^= ZobristKeys.castlingBlackQueenSide();
    }
    if (enPassantTarget != Square.NONE) {
      key ^= ZobristKeys.enPassantFile(enPassantTarget.ordinal() % 8);
    }
    return key;
  }

  public boolean isInCheck() {
    return bitboardPosition.isInCheck(havingMove);
  }

  public boolean isCheckmate() {
    return isInCheck() && legalMoves().isEmpty();
  }

  public boolean isStalemate() {
    return !isInCheck() && legalMoves().isEmpty();
  }

  public void move(MoveSpecification moveSpec) {
    undoStack.push(
        new UndoEntry(bitboardPosition, havingMove, enPassantTarget, castlingRightWhite, castlingRightBlack,
            halfmoveClock));

    final boolean isPawnMove = moveSpec.castlingMove() == CastlingMove.NONE
        && bitboardPosition.get(moveSpec.fromSquare()).getPieceType() == PieceType.PAWN;
    final boolean isCapture = moveSpec.castlingMove() == CastlingMove.NONE
        && !bitboardPosition.isEmpty(moveSpec.toSquare());

    bitboardPosition = bitboardPosition.afterMove(moveSpec, havingMove);

    enPassantTarget = computeEnPassantTarget(moveSpec, havingMove, isPawnMove);
    castlingRightWhite = nextCastlingRight(bitboardPosition, Side.WHITE, castlingRightWhite);
    castlingRightBlack = nextCastlingRight(bitboardPosition, Side.BLACK, castlingRightBlack);
    halfmoveClock = isPawnMove || isCapture ? 0 : halfmoveClock + 1;
    havingMove = havingMove.getOppositeSide();
  }

  public void unmove() {
    final UndoEntry undo = undoStack.pop();
    bitboardPosition = undo.bitboardPosition();
    havingMove = undo.havingMove();
    enPassantTarget = undo.enPassantTarget();
    castlingRightWhite = undo.castlingRightWhite();
    castlingRightBlack = undo.castlingRightBlack();
    halfmoveClock = undo.halfmoveClock();
  }

  public boolean isFirstMove() {
    return undoStack.isEmpty();
  }

  private static Square computeEnPassantTarget(MoveSpecification moveSpec, Side movingSide, boolean isPawnMove) {
    if (moveSpec.castlingMove() != CastlingMove.NONE || !isPawnMove) {
      return Square.NONE;
    }
    final Square from = moveSpec.fromSquare();
    final Square to = moveSpec.toSquare();
    final int fromRank = from.ordinal() / 8;
    final int toRank = to.ordinal() / 8;
    if (Math.abs(toRank - fromRank) != 2) {
      return Square.NONE;
    }
    // Pawn-two-square advance: EP target is the square the pawn jumped over.
    final int epOrdinal = movingSide == Side.WHITE ? from.ordinal() + 8 : from.ordinal() - 8;
    return Square.REAL.get(epOrdinal);
  }

  private static CastlingRight nextCastlingRight(BitboardPosition position, Side side, CastlingRight previous) {
    if (previous == CastlingRight.NONE) {
      return CastlingRight.NONE;
    }
    final long kingHomeBit;
    final long kingSideRookHomeBit;
    final long queenSideRookHomeBit;
    final long sideKings;
    final long sideRooks;
    if (side == Side.WHITE) {
      kingHomeBit = 1L << Square.E1.ordinal();
      kingSideRookHomeBit = 1L << Square.H1.ordinal();
      queenSideRookHomeBit = 1L << Square.A1.ordinal();
      sideKings = position.whiteKings();
      sideRooks = position.whiteRooks();
    } else {
      kingHomeBit = 1L << Square.E8.ordinal();
      kingSideRookHomeBit = 1L << Square.H8.ordinal();
      queenSideRookHomeBit = 1L << Square.A8.ordinal();
      sideKings = position.blackKings();
      sideRooks = position.blackRooks();
    }
    final boolean kingHome = (sideKings & kingHomeBit) != 0L;
    final boolean kingSideRookHome = (sideRooks & kingSideRookHomeBit) != 0L;
    final boolean queenSideRookHome = (sideRooks & queenSideRookHomeBit) != 0L;
    final boolean canKingSide = previous.getHasKingSide() && kingHome && kingSideRookHome;
    final boolean canQueenSide = previous.getHasQueenSide() && kingHome && queenSideRookHome;
    if (canKingSide && canQueenSide) {
      return CastlingRight.KING_AND_QUEEN_SIDE;
    }
    if (canKingSide) {
      return CastlingRight.KING_SIDE;
    }
    if (canQueenSide) {
      return CastlingRight.QUEEN_SIDE;
    }
    return CastlingRight.NONE;
  }

  private record UndoEntry(BitboardPosition bitboardPosition, Side havingMove, Square enPassantTarget,
      CastlingRight castlingRightWhite, CastlingRight castlingRightBlack, int halfmoveClock) {
  }
}
