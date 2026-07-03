// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.CastlingRight;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.exceptions.ProgrammingMistakeException;

/**
 * A chess position as the semi-static analysis sees it ({@code fun22-spec.md} section 1): the set of pieces plus
 * the two preconditions that {@link UnwinnableSemiStatic} step 1 checks (castling rights present, en passant
 * possible). Pieces occupy distinct squares.
 *
 * <p>
 * Pieces are held in a fixed order; that index is the piece's identity throughout {@link Mobility} and
 * {@link MobilitySolution}.
 */
final class SemiStaticPosition {

  private final SemiStaticPiece[] pieces;
  private final int[] indexAt; // square -> piece index, or -1
  private final boolean castlingRightsPresent;
  private final boolean enPassantPossible;
  private final int whiteKingIndex;
  private final int blackKingIndex;

  SemiStaticPosition(List<SemiStaticPiece> pieces, boolean castlingRightsPresent, boolean enPassantPossible) {
    this.pieces = pieces.toArray(new SemiStaticPiece[0]);
    this.indexAt = new int[SquareGeometry.SQUARES];
    Arrays.fill(this.indexAt, -1);
    int whiteKing = -1;
    int blackKing = -1;
    int whiteKings = 0;
    int blackKings = 0;
    for (int i = 0; i < this.pieces.length; i++) {
      final SemiStaticPiece piece = this.pieces[i];
      if (indexAt[piece.square()] != -1) {
        throw new ProgrammingMistakeException("Two pieces on square " + piece.square());
      }
      indexAt[piece.square()] = i;
      if (piece.pieceType() == io.github.dlbbld.ashlarchess.board.enums.PieceType.KING) {
        if (piece.side() == Side.WHITE) {
          whiteKing = i;
          whiteKings++;
        } else {
          blackKing = i;
          blackKings++;
        }
      }
    }
    // The semi-static analysis references both kings (K_c and K_not-c); a legal position always has exactly one of
    // each. Enforce it so an invalid hand-built position fails loudly here rather than misbehaving later.
    if (whiteKings != 1 || blackKings != 1) {
      throw new ProgrammingMistakeException(
          "Position must have exactly one king per side: white=" + whiteKings + " black=" + blackKings);
    }
    this.whiteKingIndex = whiteKing;
    this.blackKingIndex = blackKing;
    this.castlingRightsPresent = castlingRightsPresent;
    this.enPassantPossible = enPassantPossible;
  }

  /** Projects the board's current position onto the semi-static model. */
  static SemiStaticPosition fromBoard(Board board) {
    final BitboardPosition placement = board.getBitboardPosition();
    final List<SemiStaticPiece> pieces = new ArrayList<>();
    for (final Square square : Square.values()) {
      if (square == Square.NONE) {
        continue;
      }
      final Piece piece = placement.get(square);
      if (piece == Piece.NONE) {
        continue;
      }
      pieces.add(new SemiStaticPiece(piece.getPieceType(), piece.getSide(), square.ordinal()));
    }
    final boolean castlingRightsPresent = board.getCastlingRightWhite() != CastlingRight.NONE
        || board.getCastlingRightBlack() != CastlingRight.NONE;
    return new SemiStaticPosition(pieces, castlingRightsPresent, board.isEnPassantCapturePossible());
  }

  /** Number of pieces. */
  int count() {
    return pieces.length;
  }

  /**
   * The piece at index {@code i} (its stable identity).
   */
  SemiStaticPiece piece(int i) {
    return pieces[i];
  }

  /**
   * Index of the piece occupying {@code square}, or {@code -1} if empty.
   */
  int indexAt(int square) {
    return indexAt[square];
  }

  boolean castlingRightsPresent() {
    return castlingRightsPresent;
  }

  boolean enPassantPossible() {
    return enPassantPossible;
  }

  /**
   * Index of {@code side}'s king (always present - the constructor enforces exactly one per side).
   */
  int kingIndex(Side side) {
    return side == Side.WHITE ? whiteKingIndex : blackKingIndex;
  }
}
