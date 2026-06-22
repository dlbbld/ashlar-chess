// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.san;

import java.util.List;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.board.MoveSpecification;
import io.github.dlbbld.ashlarchess.board.LegalMove;

/**
 * Public entry point for the strict SAN pipeline. Accepts canonical SAN only and returns the resolved
 * {@link MoveSpecification}, validating by construction - an input that is not canonical SAN, or is canonical but not a
 * legal move on the position, throws. Use {@link LenientSanParser} when parsing real-world PGN that may contain
 * forgivable deviations from canonical SAN; that pipeline returns a {@link LenientSanParseResult} carrying the resolved
 * move plus the forgiven items.
 */
public final class StrictSanParser {

  private StrictSanParser() {
  }

  /**
   * Parses {@code san} as canonical SAN against {@code board} and returns the resolved {@link MoveSpecification}.
   *
   * @throws SanValidationException if the input is not canonical SAN, or is canonical but does not represent a legal
   *                                move on the current position
   */
  public static MoveSpecification parse(String san, Board board) throws SanValidationException {
    return parseInternal(san, board);
  }

  private static MoveSpecification parseInternal(String san, Board board) throws SanValidationException {
    final SanParse sanParse = SanValidateFormat.validateFormat(san);

    SanValidateNonMovement.validateNonMovement(sanParse);

    final Side sideToMove = board.getSideToMove();
    SanValidateMovement.validateMovement(sanParse, sideToMove);

    final SanFormat sanFormat = sanParse.sanFormat();
    final SanConversion sanConversion = sanParse.sanConversion();

    SanValidatePieceExists.validatePieceExists(sideToMove, sanFormat, sanConversion, sanConversion.movingPieceType(),
        board.getBitboardPosition());

    SanValidateDestination.validateDestinationSquareSemantics(board, sideToMove, sanFormat, sanConversion);

    final List<LegalMove> legalMovesCandidates = SanValidateLegalMoves.calculateLegalMovesCandidates(board, sideToMove,
        sanParse);
    SanValidateLegalMoves.validateAgainstLegalMoves(board, sideToMove, legalMovesCandidates, sanFormat, sanConversion);

    final LegalMove legalMoveOnlyCandidate = SanValidateLegalMoves.calculateOnlyPossibleLegalMove(sanFormat,
        sanConversion, legalMovesCandidates);
    final MoveSpecification moveSpecification = SanValidateLegalMoves.calculateMoveSpecificationForSan(board,
        sideToMove, sanFormat, sanConversion, legalMoveOnlyCandidate.moveSpecification());
    if (!moveSpecification.equals(legalMoveOnlyCandidate.moveSpecification())) {
      throw new ProgrammingMistakeException("A mistake happened in the move construction");
    }

    SanValidateCheck.validateSanTerminalMarker(board, sanConversion.sanTerminalMarker(), moveSpecification);

    return moveSpecification;
  }
}
