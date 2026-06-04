// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import java.util.List;

import io.github.dlbbld.ashlarchess.bitboard.BitboardLegalMoveFactory;
import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.CastlingMove;
import io.github.dlbbld.ashlarchess.board.enums.CastlingRight;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.common.model.DynamicPosition;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.model.CastlingRightBoth;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.model.LegalMoveKind;
import io.github.dlbbld.ashlarchess.moves.CastlingUtility;
import io.github.dlbbld.ashlarchess.moves.EnPassantCaptureUtility;

/**
 * Mutable helpmate-search board: owns twelve piece bitboards, side to move, raw and normalized en-passant target, and
 * castling rights for both sides as mutable instance fields. {@link #move(MoveSpecification)} mutates the bitboards in
 * place; {@link #unmove()} pops a snapshot off a growable, pre-allocated stack of mutable {@link UndoState} objects.
 * The legal-move generator emits directly into a per-depth {@link LegalMoveBuffer} via
 * {@link BitboardLegalMoveFactory#calculateLegalMovesInto} - no per-move {@code Set} / {@code ImmutableList} allocation
 * along the search hot path.
 *
 * <p>
 * {@link BitboardPosition} stays immutable (it remains a record); the mutation is local to this package-private search
 * board. Per the layer-discipline rule, the search board calls the shared bitboard layer for move generation (the sink
 * overload {@link BitboardLegalMoveFactory#calculateLegalMovesInto}) and for the EP normalization probe
 * ({@link BitboardPosition#isInCheckAfterEnPassantCapture}) - no private parallel engine. One {@link BitboardPosition}
 * snapshot is still built per {@code refreshDerivedState} call for the {@code isInCheck} query; the snapshot is
 * local-scope and the surrounding cached flags ({@code isCheckmate} / {@code isStalemate}) are derived from it together
 * with {@link LegalMoveBuffer#isEmpty}.
 *
 * <p>
 * Getters that expose record-shaped snapshots ({@link #getBitboardPosition}, {@link #getDynamicPosition}) construct a
 * fresh record on each call - they exist for the lock-step Board parity test and for debugging, not for the search hot
 * path. Internal callers read the mutable fields directly.
 */
final class HelpmateSearchBoard {

  /** Initial undo-stack capacity. Grows by doubling on overflow; no fixed max depth. */
  private static final int INITIAL_UNDO_CAPACITY = 128;

  // 12 mutable piece bitboards (one long per real Piece value, same field order as BitboardPosition).
  private long whitePawns;
  private long whiteRooks;
  private long whiteKnights;
  private long whiteBishops;
  private long whiteQueens;
  private long whiteKings;
  private long blackPawns;
  private long blackRooks;
  private long blackKnights;
  private long blackBishops;
  private long blackQueens;
  private long blackKings;

  // Per-move auxiliary state.
  private Side havingMove;
  private Square enPassantCaptureTargetSquare;
  private Square normalizedEnPassantCaptureTargetSquare;
  private CastlingRight castlingRightWhite;
  private CastlingRight castlingRightBlack;

  // Derived cache (refreshDerivedState rebuilds these on every move). legalMoves is held in the per-depth
  // LegalMoveBuffer at buffersByDepth[undoTop] rather than as a separate field.
  private boolean isCheck;
  private boolean isCheckmate;
  private boolean isStalemate;

  // Growable parallel stacks of mutable UndoState objects and per-depth LegalMoveBuffers, reused across moves.
  // buffersByDepth[d] holds the legal moves for the position at depth d; the parent's buffer at depth d is
  // preserved untouched while we recurse into depth d+1.
  private UndoState[] undoStack;
  private LegalMoveBuffer[] buffersByDepth;
  private int undoTop;

  private HelpmateSearchBoard(BitboardPosition initialBitboard, Side havingMove, Square enPassantCaptureTargetSquare,
      CastlingRight castlingRightWhite, CastlingRight castlingRightBlack) {
    loadBitboard(initialBitboard);
    this.havingMove = havingMove;
    this.enPassantCaptureTargetSquare = enPassantCaptureTargetSquare;
    this.castlingRightWhite = castlingRightWhite;
    this.castlingRightBlack = castlingRightBlack;
    this.undoStack = new UndoState[INITIAL_UNDO_CAPACITY];
    this.buffersByDepth = new LegalMoveBuffer[INITIAL_UNDO_CAPACITY];
    for (int i = 0; i < this.undoStack.length; i++) {
      this.undoStack[i] = new UndoState();
      this.buffersByDepth[i] = new LegalMoveBuffer();
    }
    this.undoTop = 0;
    this.normalizedEnPassantCaptureTargetSquare = computeNormalizedEnPassantCaptureTargetSquare();
    refreshDerivedState();
  }

  static HelpmateSearchBoard from(Board board) {
    final DynamicPosition dp = board.getDynamicPosition();
    return new HelpmateSearchBoard(dp.bitboardPosition(), dp.havingMove(), board.getEnPassantCaptureTargetSquare(),
        dp.castlingRightWhite(), dp.castlingRightBlack());
  }

  void move(MoveSpecification moveSpecification) {
    // 1. Snapshot current state into the undo stack (grow if needed). Step 8 (refreshDerivedState) writes
    //    buffersByDepth[undoTop] AFTER the undoTop++ below, so that buffer slot must also be in range; the buffer
    //    array is indexed one deeper than the undo slot saved here, hence the +1. Growing one ply early keeps
    //    buffersByDepth[undoTop] valid without a separate, larger buffer array.
    if (undoTop + 1 == undoStack.length) {
      growStacks();
    }
    saveUndoState(undoStateAt(undoTop));
    undoTop++;
    // The parent's buffer at undoTop-1 is preserved untouched; we write into buffersByDepth[undoTop] in step 8.

    // 2. Identify the move (movingPiece, capturedPiece, kind) from current mutable state - no snapshot allocation.
    final LegalMove moveToPerform = identifyLegalMove(moveSpecification);

    // 3. Mutate the piece bitboards in place.
    applyMoveInPlace(moveToPerform);

    // 4. Toggle side to move.
    havingMove = havingMove.getOppositeSide();

    // 5. Update castling rights based on the move (king/rook moves clear rights).
    final CastlingRightBoth newRights = CastlingUtility.calculateCastlingRightBoth(castlingRightWhite,
        castlingRightBlack, moveToPerform);
    castlingRightWhite = newRights.castlingRightWhite();
    castlingRightBlack = newRights.castlingRightBlack();

    // 6. Update raw EP target (a pawn 2-square advance sets it; everything else clears it).
    enPassantCaptureTargetSquare = EnPassantCaptureUtility.calculateEnPassantCaptureTargetSquare(moveToPerform);

    // 7. Normalize EP - NONE if no opposing pawn can legally execute the EP capture.
    normalizedEnPassantCaptureTargetSquare = computeNormalizedEnPassantCaptureTargetSquare();

    // 8. Refresh derived state: fill the per-depth legal-move buffer and recompute cached check / mate flags.
    refreshDerivedState();
  }

  void unmove() {
    undoTop--;
    restoreUndoState(undoStateAt(undoTop));
  }

  // ---------------------------- Getters (snapshots / debug, not the search hot path) ----------------------------

  DynamicPosition getDynamicPosition() {
    return new DynamicPosition(havingMove, getBitboardPosition(), normalizedEnPassantCaptureTargetSquare,
        castlingRightWhite, castlingRightBlack);
  }

  BitboardPosition getBitboardPosition() {
    return new BitboardPosition(whitePawns, whiteRooks, whiteKnights, whiteBishops, whiteQueens, whiteKings, blackPawns,
        blackRooks, blackKnights, blackBishops, blackQueens, blackKings);
  }

  /**
   * Exact structural transposition-cache key for the current search-board state. Constructed directly from the mutable
   * piece bitboards and per-move auxiliary state - one record allocation per call, no nested {@link BitboardPosition}
   * (the twelve piece bitboards are inlined in {@link HelpmateSearchKey}). Equivalent in equality semantics to
   * {@link #getDynamicPosition()}{@code .equals}, but without the nested-record allocation cost. See
   * {@link HelpmateSearchKey} for the included / excluded field list.
   */
  HelpmateSearchKey currentTranspositionKey() {
    return new HelpmateSearchKey(havingMove, whitePawns, whiteRooks, whiteKnights, whiteBishops, whiteQueens,
        whiteKings, blackPawns, blackRooks, blackKnights, blackBishops, blackQueens, blackKings,
        normalizedEnPassantCaptureTargetSquare, castlingRightWhite, castlingRightBlack);
  }

  Side getHavingMove() {
    return havingMove;
  }

  Square getEnPassantCaptureTargetSquare() {
    return enPassantCaptureTargetSquare;
  }

  CastlingRight getCastlingRight(Side side) {
    return switch (side) {
      case WHITE -> castlingRightWhite;
      case BLACK -> castlingRightBlack;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  List<LegalMove> getLegalMoves() {
    return legalMoveBufferAt(undoTop);
  }

  @SuppressWarnings("null")
  private UndoState undoStateAt(int index) {
    return undoStack[index];
  }

  @SuppressWarnings("null")
  private LegalMoveBuffer legalMoveBufferAt(int index) {
    return buffersByDepth[index];
  }

  boolean isCheck() {
    return isCheck;
  }

  boolean isCheckmate() {
    return isCheckmate;
  }

  boolean isStalemate() {
    return isStalemate;
  }

  boolean isInsufficientMaterial(Side side) {
    return UnwinnabilityMaterialBitboard.calculateIsInsufficientMaterial(side, getBitboardPosition());
  }

  // ---------------------------- private internals ----------------------------

  private void loadBitboard(BitboardPosition position) {
    whitePawns = position.whitePawns();
    whiteRooks = position.whiteRooks();
    whiteKnights = position.whiteKnights();
    whiteBishops = position.whiteBishops();
    whiteQueens = position.whiteQueens();
    whiteKings = position.whiteKings();
    blackPawns = position.blackPawns();
    blackRooks = position.blackRooks();
    blackKnights = position.blackKnights();
    blackBishops = position.blackBishops();
    blackQueens = position.blackQueens();
    blackKings = position.blackKings();
  }

  private void saveUndoState(UndoState undo) {
    undo.whitePawns = whitePawns;
    undo.whiteRooks = whiteRooks;
    undo.whiteKnights = whiteKnights;
    undo.whiteBishops = whiteBishops;
    undo.whiteQueens = whiteQueens;
    undo.whiteKings = whiteKings;
    undo.blackPawns = blackPawns;
    undo.blackRooks = blackRooks;
    undo.blackKnights = blackKnights;
    undo.blackBishops = blackBishops;
    undo.blackQueens = blackQueens;
    undo.blackKings = blackKings;
    undo.havingMove = havingMove;
    undo.enPassantCaptureTargetSquare = enPassantCaptureTargetSquare;
    undo.normalizedEnPassantCaptureTargetSquare = normalizedEnPassantCaptureTargetSquare;
    undo.castlingRightWhite = castlingRightWhite;
    undo.castlingRightBlack = castlingRightBlack;
    undo.isCheck = isCheck;
    undo.isCheckmate = isCheckmate;
    undo.isStalemate = isStalemate;
  }

  private void restoreUndoState(UndoState undo) {
    whitePawns = undo.whitePawns;
    whiteRooks = undo.whiteRooks;
    whiteKnights = undo.whiteKnights;
    whiteBishops = undo.whiteBishops;
    whiteQueens = undo.whiteQueens;
    whiteKings = undo.whiteKings;
    blackPawns = undo.blackPawns;
    blackRooks = undo.blackRooks;
    blackKnights = undo.blackKnights;
    blackBishops = undo.blackBishops;
    blackQueens = undo.blackQueens;
    blackKings = undo.blackKings;
    havingMove = undo.havingMove;
    enPassantCaptureTargetSquare = undo.enPassantCaptureTargetSquare;
    normalizedEnPassantCaptureTargetSquare = undo.normalizedEnPassantCaptureTargetSquare;
    castlingRightWhite = undo.castlingRightWhite;
    castlingRightBlack = undo.castlingRightBlack;
    isCheck = undo.isCheck;
    isCheckmate = undo.isCheckmate;
    isStalemate = undo.isStalemate;
  }

  private void growStacks() {
    final int oldLen = undoStack.length;
    final int newLen = oldLen * 2;
    final UndoState[] grownUndo = new UndoState[newLen];
    final LegalMoveBuffer[] grownBuffers = new LegalMoveBuffer[newLen];
    System.arraycopy(undoStack, 0, grownUndo, 0, oldLen);
    System.arraycopy(buffersByDepth, 0, grownBuffers, 0, oldLen);
    for (int i = oldLen; i < newLen; i++) {
      grownUndo[i] = new UndoState();
      grownBuffers[i] = new LegalMoveBuffer();
    }
    undoStack = grownUndo;
    buffersByDepth = grownBuffers;
  }

  /**
   * Identifies movingPiece, capturedPiece, and {@link LegalMoveKind} from the current mutable state. Mirrors the tiny
   * piece-lookup logic in {@code BitboardLegalMoveFactory.toLegalMove} but reads directly from the mutable piece
   * bitboards so the hot path does not pay for a {@link BitboardPosition} snapshot per move. Same semantics; the
   * differential coverage comes from {@code TestHelpmateSearchBoard}'s lock-step Board parity.
   */
  private LegalMove identifyLegalMove(MoveSpecification moveSpec) {
    if (moveSpec.castlingMove() != CastlingMove.NONE) {
      final Piece kingPiece = havingMove == Side.WHITE ? Piece.WHITE_KING : Piece.BLACK_KING;
      return new LegalMove(moveSpec, kingPiece, Piece.NONE, LegalMoveKind.CASTLING);
    }

    final Square from = moveSpec.fromSquare();
    final Square to = moveSpec.toSquare();
    final Piece movingPiece = pieceAt(from);
    if (movingPiece == Piece.NONE) {
      throw new IllegalArgumentException("No piece on the from-square " + from.getName());
    }

    final boolean isPawn = movingPiece.getPieceType() == PieceType.PAWN;
    final boolean diagonalPawnMove = isPawn && from.getFile() != to.getFile();
    final Piece toPiece = pieceAt(to);
    final boolean toEmpty = toPiece == Piece.NONE;

    final Piece capturedPiece;
    if (!toEmpty) {
      capturedPiece = toPiece;
    } else if (diagonalPawnMove) {
      capturedPiece = havingMove == Side.WHITE ? Piece.BLACK_PAWN : Piece.WHITE_PAWN;
    } else {
      capturedPiece = Piece.NONE;
    }

    final LegalMoveKind kind;
    if (moveSpec.promotionPieceType() != PromotionPieceType.NONE) {
      kind = LegalMoveKind.PROMOTION;
    } else if (isPawn && Math.abs(from.getRank().getNumber() - to.getRank().getNumber()) == 2) {
      kind = LegalMoveKind.PAWN_TWO_SQUARE_ADVANCE;
    } else if (diagonalPawnMove && toEmpty) {
      kind = LegalMoveKind.EN_PASSANT_CAPTURE;
    } else {
      kind = LegalMoveKind.NORMAL;
    }

    return new LegalMove(moveSpec, movingPiece, capturedPiece, kind);
  }

  /**
   * Returns the piece on {@code square} from the mutable bitboards. Mirrors {@link BitboardPosition#get(Square)}.
   */
  private Piece pieceAt(Square square) {
    final long bit = 1L << square.ordinal();
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

  /**
   * Toggles {@code bit} on the bitboard for {@code piece}. No-op for {@link Piece#NONE}.
   */
  private void togglePieceBit(Piece piece, long bit) {
    switch (piece) {
      case WHITE_PAWN -> whitePawns ^= bit;
      case WHITE_ROOK -> whiteRooks ^= bit;
      case WHITE_KNIGHT -> whiteKnights ^= bit;
      case WHITE_BISHOP -> whiteBishops ^= bit;
      case WHITE_QUEEN -> whiteQueens ^= bit;
      case WHITE_KING -> whiteKings ^= bit;
      case BLACK_PAWN -> blackPawns ^= bit;
      case BLACK_ROOK -> blackRooks ^= bit;
      case BLACK_KNIGHT -> blackKnights ^= bit;
      case BLACK_BISHOP -> blackBishops ^= bit;
      case BLACK_QUEEN -> blackQueens ^= bit;
      case BLACK_KING -> blackKings ^= bit;
      case NONE -> {
        /* no-op */ }
      default -> throw new IllegalArgumentException("Unexpected piece " + piece);
    }
  }

  /**
   * Applies {@code moveToPerform} to the piece bitboards in place - mirrors {@link BitboardPosition#afterMove}.
   */
  private void applyMoveInPlace(LegalMove moveToPerform) {
    final MoveSpecification moveSpec = moveToPerform.moveSpecification();
    if (moveSpec.castlingMove() != CastlingMove.NONE) {
      applyCastlingInPlace(moveSpec.castlingMove(), havingMove);
      return;
    }
    final Square from = moveSpec.fromSquare();
    final Square to = moveSpec.toSquare();
    final Piece movingPiece = moveToPerform.movingPiece();
    final Piece capturedPiece = moveToPerform.pieceCaptured();
    final long fromBit = 1L << from.ordinal();
    final long toBit = 1L << to.ordinal();

    final long capturedBit;
    if (capturedPiece == Piece.NONE) {
      capturedBit = 0L;
    } else if (moveToPerform.kind() == LegalMoveKind.EN_PASSANT_CAPTURE) {
      final int capturedOrdinal = havingMove == Side.WHITE ? to.ordinal() - 8 : to.ordinal() + 8;
      capturedBit = 1L << capturedOrdinal;
    } else {
      capturedBit = toBit;
    }

    final PromotionPieceType promotion = moveSpec.promotionPieceType();
    final Piece destPiece = promotion == PromotionPieceType.NONE ? movingPiece
        : Piece.calculate(havingMove, promotion.getPieceType());

    togglePieceBit(movingPiece, fromBit);
    togglePieceBit(capturedPiece, capturedBit);
    togglePieceBit(destPiece, toBit);
  }

  private void applyCastlingInPlace(CastlingMove castlingMove, Side movingSide) {
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
    togglePieceBit(kingPiece, 1L << kingFromOrdinal);
    togglePieceBit(kingPiece, 1L << kingToOrdinal);
    togglePieceBit(rookPiece, 1L << rookFromOrdinal);
    togglePieceBit(rookPiece, 1L << rookToOrdinal);
  }

  /**
   * Returns the EP target square that is actually capturable from the current mutable state, or {@link Square#NONE} if
   * no opposing pawn can legally execute the EP capture. Cheap path first: returns NONE on no raw EP target. Otherwise
   * checks the mutable pawn bitboards for adjacent same-rank own pawns; only if at least one candidate pawn exists is a
   * {@link BitboardPosition} snapshot built once and passed to {@link BitboardPosition#isInCheckAfterEnPassantCapture}.
   */
  private Square computeNormalizedEnPassantCaptureTargetSquare() {
    if (enPassantCaptureTargetSquare == Square.NONE) {
      return Square.NONE;
    }
    if (!Square.calculateHasBehindSquare(havingMove, enPassantCaptureTargetSquare)) {
      throw new ProgrammingMistakeException();
    }
    final Square squareBehind = Square.calculateBehindSquare(havingMove, enPassantCaptureTargetSquare);
    final Piece ownPawn = Piece.calculate(havingMove, PieceType.PAWN);

    final boolean hasRight = Square.calculateHasRightSquare(havingMove, squareBehind);
    final boolean hasLeft = Square.calculateHasLeftSquare(havingMove, squareBehind);
    final Square candidateRight = hasRight ? Square.calculateRightSquare(havingMove, squareBehind) : Square.NONE;
    final Square candidateLeft = hasLeft ? Square.calculateLeftSquare(havingMove, squareBehind) : Square.NONE;
    final boolean hasRightPawn = hasRight && pieceAt(candidateRight) == ownPawn;
    final boolean hasLeftPawn = hasLeft && pieceAt(candidateLeft) == ownPawn;

    if (!hasRightPawn && !hasLeftPawn) {
      return Square.NONE;
    }

    final BitboardPosition snapshot = getBitboardPosition();
    if (hasRightPawn
        && !snapshot.isInCheckAfterEnPassantCapture(candidateRight, enPassantCaptureTargetSquare, havingMove)) {
      return enPassantCaptureTargetSquare;
    }
    if (hasLeftPawn
        && !snapshot.isInCheckAfterEnPassantCapture(candidateLeft, enPassantCaptureTargetSquare, havingMove)) {
      return enPassantCaptureTargetSquare;
    }
    return Square.NONE;
  }

  private void refreshDerivedState() {
    final LegalMoveBuffer currentBuffer = buffersByDepth[undoTop];
    currentBuffer.reset();
    final BitboardPosition snapshot = getBitboardPosition();
    final long enPassantBit = enPassantCaptureTargetSquare == Square.NONE ? 0L
        : 1L << enPassantCaptureTargetSquare.ordinal();
    BitboardLegalMoveFactory.calculateLegalMovesInto(currentBuffer::append, snapshot, havingMove,
        getCastlingRight(havingMove), enPassantBit);
    isCheck = snapshot.isInCheck(havingMove);
    isCheckmate = isCheck && currentBuffer.isEmpty();
    isStalemate = !isCheck && currentBuffer.isEmpty();
  }
}
