// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board;

import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;

/**
 * A single legal move in a position, as produced by the rule pipeline (the legal-move list on a board).
 *
 * <p>
 * Components:
 * <ul>
 * <li>{@code moveSpecification} - the from / to / castling / promotion specification of the move.</li>
 * <li>{@code movingPiece} - the piece that moves. Never {@link Piece#NONE}: the canonical constructor rejects it, so
 * {@code movingPiece().getSide()} / {@code getPieceType()} are always safe.</li>
 * <li>{@code capturedPiece} - the piece this move captures, or {@link Piece#NONE} when the move captures nothing. Most
 * moves are non-captures, so this is commonly {@code Piece.NONE}; a caller must test
 * {@code capturedPiece() != Piece.NONE} (or the move {@code kind}) before treating it as a real piece, because
 * {@code Piece.NONE.getSide()} / {@code getPieceType()} throw. For an en-passant capture the captured pawn is not on
 * the destination square, but {@code capturedPiece} still names it. The asymmetry is deliberate - {@code movingPiece}
 * forbids {@code NONE} while {@code capturedPiece} uses it as the no-capture sentinel - because every move has a mover
 * but only some capture.</li>
 * <li>{@code kind} - the move category; see {@link LegalMoveKind}.</li>
 * </ul>
 */
public record LegalMove(MoveSpecification moveSpecification, Piece movingPiece, Piece capturedPiece, LegalMoveKind kind)
    implements Comparable<LegalMove> {

  public LegalMove {
    if (movingPiece == Piece.NONE) {
      throw new IllegalArgumentException("The moving piece cannot be the none piece");
    }
  }

  public Side movingSide() {
    return movingPiece.getSide();
  }

  /**
   * Whether playing this move resets the halfmove clock (FIDE 9.3): {@code true} for a pawn move or any capture,
   * {@code false} otherwise. The moving piece is never {@link Piece#NONE} (the canonical constructor rejects it), so
   * the pawn test is always safe.
   */
  public boolean resetsHalfMoveClock() {
    return movingPiece.getPieceType() == PieceType.PAWN || capturedPiece != Piece.NONE;
  }

  /** Whether this move captures a piece (including an en-passant capture); equivalent to {@code capturedPiece != NONE}. */
  public boolean isCapture() {
    return capturedPiece != Piece.NONE;
  }

  /** Whether this move is a castling move. */
  public boolean isCastling() {
    return kind == LegalMoveKind.CASTLING;
  }

  /** Whether this move is a promotion. */
  public boolean isPromotion() {
    return kind == LegalMoveKind.PROMOTION;
  }

  /** Whether this move is an en-passant capture. */
  public boolean isEnPassant() {
    return kind == LegalMoveKind.EN_PASSANT_CAPTURE;
  }

  /**
   * The square the captured pawn occupied for an en-passant capture. The captured pawn is <em>not</em> on this move's
   * destination square: it stands on the same file as the destination and the same rank as the origin.
   *
   * @return the captured pawn's square
   * @throws IllegalStateException if this move is not an en-passant capture (test {@link #isEnPassant()} first)
   */
  public Square enPassantCapturedPawnSquare() {
    if (kind != LegalMoveKind.EN_PASSANT_CAPTURE) {
      throw new IllegalStateException("not an en-passant capture: " + kind);
    }
    return Square.of(moveSpecification.toSquare().getFile(), moveSpecification.fromSquare().getRank());
  }

  /**
   * Total ordering consistent with {@link #equals(Object)}: by {@code moveSpecification}, then {@code movingPiece},
   * {@code capturedPiece}, and {@code kind}. Two legal moves compare equal only when all four components are equal, so
   * the ordering is safe for {@code TreeSet} / {@code TreeMap}.
   */
  @Override
  public int compareTo(LegalMove legalMove) {
    int comparison = this.moveSpecification().compareTo(legalMove.moveSpecification());
    if (comparison != 0) {
      return comparison;
    }
    comparison = this.movingPiece().compareTo(legalMove.movingPiece());
    if (comparison != 0) {
      return comparison;
    }
    comparison = this.capturedPiece().compareTo(legalMove.capturedPiece());
    if (comparison != 0) {
      return comparison;
    }
    return this.kind().compareTo(legalMove.kind());
  }

}
