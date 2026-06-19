// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.model;

import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;

/**
 * A single legal move in a position, as produced by the rule pipeline (the legal-move list on a board).
 *
 * <p>
 * Components:
 * <ul>
 * <li>{@code moveSpecification} - the from / to / castling / promotion specification of the move.</li>
 * <li>{@code movingPiece} - the piece that moves. Never {@link Piece#NONE}: the canonical constructor rejects it, so
 * {@code movingPiece().getSide()} / {@code getPieceType()} are always safe.</li>
 * <li>{@code pieceCaptured} - the piece this move captures, or {@link Piece#NONE} when the move captures nothing. Most
 * moves are non-captures, so this is commonly {@code Piece.NONE}; a caller must test {@code pieceCaptured() != Piece.NONE}
 * (or the move {@code kind}) before treating it as a real piece, because {@code Piece.NONE.getSide()} /
 * {@code getPieceType()} throw. For an en-passant capture the captured pawn is not on the destination square, but
 * {@code pieceCaptured} still names it. The asymmetry is deliberate - {@code movingPiece} forbids {@code NONE} while
 * {@code pieceCaptured} uses it as the no-capture sentinel - because every move has a mover but only some capture.</li>
 * <li>{@code kind} - the move category; see {@link LegalMoveKind}.</li>
 * </ul>
 */
public record LegalMove(MoveSpecification moveSpecification, Piece movingPiece, Piece pieceCaptured, LegalMoveKind kind)
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
   * Total ordering consistent with {@link #equals(Object)}: by {@code moveSpecification}, then {@code movingPiece},
   * {@code pieceCaptured}, and {@code kind}. Two legal moves compare equal only when all four components are equal, so
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
    comparison = this.pieceCaptured().compareTo(legalMove.pieceCaptured());
    if (comparison != 0) {
      return comparison;
    }
    return this.kind().compareTo(legalMove.kind());
  }

}
