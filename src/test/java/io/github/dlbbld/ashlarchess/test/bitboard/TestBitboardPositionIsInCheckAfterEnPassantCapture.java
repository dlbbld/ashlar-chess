// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;

/**
 * Differential test for {@link BitboardPosition#isInCheckAfterEnPassantCapture(Square, Square, Side)}: per fixture and
 * per legal EP capture, the allocation-free predicate must agree with the reference
 * {@code afterMove(moveSpec, mover).isInCheck(mover)} - which IS the production EP king-safety probe inside
 * {@link io.github.dlbbld.ashlarchess.bitboard.BitboardPosition#legalMoves(Side, long)}'s pawn handler today. The
 * {@code MoveSpecification} overload is exercised in parallel to confirm the two surfaces stay in lock-step.
 *
 * <p>
 * EP-bearing positions are rare in the broad PGN corpus, so the fixture set here is hand-picked to cover the cases the
 * production probe exists to filter:
 *
 * <ol>
 * <li>Legal EP from each side (no king-safety issue).
 * <li>Illegal EP from each side where the EP capture opens a rank line to a slider that then attacks own king (the
 * canonical "EP pin" pattern via a rank rook).
 * <li>EP capture that <em>resolves</em> an existing check by removing the checking pawn.
 * </ol>
 */
class TestBitboardPositionIsInCheckAfterEnPassantCapture {

  /** Black dxe3 EP - both kings far away, no check. Expected: not in check after. */
  private static final EpCase LEGAL_EP_BLACK = new EpCase("legal-ep-black", "8/8/8/8/3pP3/8/8/K6k b - e3 0 1",
      Square.D4, Square.E3, Side.BLACK);

  /** Black dxe3 EP - Ra4...h4 line opens behind both EP-removed pawns, hitting Ka4. Expected: in check after. */
  private static final EpCase ILLEGAL_EP_BLACK_RANK_ROOK = new EpCase("illegal-ep-black-rank-rook",
      "8/8/8/8/k2pP2R/8/8/7K b - e3 0 1", Square.D4, Square.E3, Side.BLACK);

  /** White exd6 EP - both kings far away, no check. Expected: not in check after. */
  private static final EpCase LEGAL_EP_WHITE = new EpCase("legal-ep-white", "4k3/8/8/3pP3/8/8/8/4K3 w - d6 0 1",
      Square.E5, Square.D6, Side.WHITE);

  /** White exd6 EP - rank-5 line opens behind both EP-removed pawns, black Rh5 hits white Ka5. Expected: in check. */
  private static final EpCase ILLEGAL_EP_WHITE_RANK_ROOK = new EpCase("illegal-ep-white-rank-rook",
      "4k3/8/8/K2pP2r/8/8/8/8 w - d6 0 1", Square.E5, Square.D6, Side.WHITE);

  /** White exd6 EP - capturing pawn was checking Kc4; EP removes it and resolves the check. Expected: not in check. */
  private static final EpCase EP_RESOLVES_CHECK = new EpCase("ep-resolves-check", "4k3/8/8/3pP3/2K5/8/8/8 w - d6 0 1",
      Square.E5, Square.D6, Side.WHITE);

  private static final ImmutableList<EpCase> CASES = Nulls.listOf(LEGAL_EP_BLACK, ILLEGAL_EP_BLACK_RANK_ROOK,
      LEGAL_EP_WHITE, ILLEGAL_EP_WHITE_RANK_ROOK, EP_RESOLVES_CHECK);

  @SuppressWarnings("static-method")
  @Test
  void agreesWithAfterMoveIsInCheckReference() {
    for (final EpCase epCase : CASES) {
      try {
        final BitboardPosition bitboardPosition = new Board(epCase.fen()).getBitboardPosition();
        final MoveSpecification moveSpec = new MoveSpecification(epCase.fromSquare(), epCase.toSquare());

        final boolean reference = bitboardPosition.afterMove(moveSpec, epCase.mover()).isInCheck(epCase.mover());
        final boolean squareOverload = bitboardPosition.isInCheckAfterEnPassantCapture(epCase.fromSquare(),
            epCase.toSquare(), epCase.mover());
        final boolean moveSpecOverload = bitboardPosition.isInCheckAfterEnPassantCapture(moveSpec, epCase.mover());

        assertEquals(reference, squareOverload, "Square-overload must agree with afterMove(...).isInCheck(...)");
        assertEquals(reference, moveSpecOverload, "MoveSpecification-overload must agree with reference");
      } catch (final AssertionError | RuntimeException e) {
        throw new AssertionError(
            "case=" + epCase.label() + " fen=" + epCase.fen() + " ep=" + epCase.fromSquare() + epCase.toSquare(), e);
      }
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void noneSquareThrows() {
    final BitboardPosition position = BitboardPosition.INITIAL_POSITION;
    assertThrows(IllegalArgumentException.class,
        () -> position.isInCheckAfterEnPassantCapture(Square.NONE, Square.E3, Side.WHITE));
    assertThrows(IllegalArgumentException.class,
        () -> position.isInCheckAfterEnPassantCapture(Square.D4, Square.NONE, Side.WHITE));
  }

  @SuppressWarnings("static-method")
  @Test
  void noneSideThrows() {
    final BitboardPosition position = BitboardPosition.INITIAL_POSITION;
    assertThrows(IllegalArgumentException.class,
        () -> position.isInCheckAfterEnPassantCapture(Square.D4, Square.E3, Side.NONE));
  }

  private record EpCase(String label, String fen, Square fromSquare, Square toSquare, Side mover) {
  }
}
