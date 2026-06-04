// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import io.github.dlbbld.ashlarchess.bitboard.BitboardLegalMoveFactory;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Square;

/**
 * Test-side performance probe for the move-generation path used inside {@link HelpmateSearchBoard}. It mirrors the
 * legal-move part of {@code HelpmateSearchBoard.refreshDerivedState}: emit directly into a reusable
 * {@link LegalMoveBuffer} via {@link BitboardLegalMoveFactory#calculateLegalMovesInto}, with no sorted set or immutable
 * list allocation.
 */
public final class HelpmateSearchBoardPerformanceProbe {

  private final LegalMoveBuffer buffer = new LegalMoveBuffer();

  public int calculateLegalMoveCount(Board board) {
    buffer.reset();
    final Square ep = board.getEnPassantCaptureTargetSquare();
    final long enPassantBit = ep == Square.NONE ? 0L : 1L << ep.ordinal();
    BitboardLegalMoveFactory.calculateLegalMovesInto(buffer::append, board.getBitboardPosition(), board.getHavingMove(),
        board.getCastlingRight(board.getHavingMove()), enPassantBit);
    return buffer.size();
  }
}
