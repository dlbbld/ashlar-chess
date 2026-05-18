package com.dlb.chess.bitboard;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import java.util.TreeSet;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.CastlingMove;
import com.dlb.chess.board.enums.CastlingRight;
import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.model.LegalMove;
import com.dlb.chess.moves.AbstractLegalMoves;

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
   * Legal moves for the side to move — non-castling targets via {@link BitboardPosition#legalMoves} plus castling
   * moves via the existing {@link AbstractLegalMoves#calculateCastlingLegalMoves} bridge. Castling cannot be
   * dropped from a tree-search legal-move set: a position whose only escape from check is a castle would otherwise
   * be misclassified as checkmate, and games where the winning line involves castling would be silently
   * truncated.
   *
   * <p>
   * The castling bridge currently consumes a {@link com.dlb.chess.board.StaticPosition}, derived here via
   * {@link BitboardPositionUtility#toStaticPosition}. That conversion is per-call cost and will need to either
   * become a cached field or be replaced with a bitboard-native castling check before this method drives a hot
   * helpmate search (Step 3.2). TODO is captured in tasks.md.
   */
  public Set<MoveSpecification> legalMoves() {
    final long enPassantBit = enPassantTarget == Square.NONE ? 0L : 1L << enPassantTarget.ordinal();
    final Set<MoveSpecification> moves = new TreeSet<>(bitboardPosition.legalMoves(havingMove, enPassantBit));
    final CastlingRight currentCastlingRight = castlingRight(havingMove);
    if (currentCastlingRight != CastlingRight.NONE) {
      for (final LegalMove castlingLegalMove : AbstractLegalMoves.calculateCastlingLegalMoves(
          BitboardPositionUtility.toStaticPosition(bitboardPosition), havingMove, currentCastlingRight)) {
        moves.add(castlingLegalMove.moveSpecification());
      }
    }
    return moves;
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
