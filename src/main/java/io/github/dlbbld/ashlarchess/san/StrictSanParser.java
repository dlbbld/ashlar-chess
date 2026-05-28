package io.github.dlbbld.ashlarchess.san;

import java.util.List;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.model.LegalMove;

/**
 * Public entry point for the strict SAN pipeline. Accepts canonical SAN only; the result is symmetric in shape with
 * {@link io.github.dlbbld.ashlarchess.san.LenientSanParser} so callers can switch between strict and lenient by
 * changing one method call. Use {@link io.github.dlbbld.ashlarchess.san.LenientSanParser} when parsing real-world PGN
 * that may contain forgivable deviations from canonical SAN.
 */
public class StrictSanParser extends AbstractSan {

  /**
   * Parses {@code san} as canonical SAN against {@code board} and returns the resolved {@link MoveSpecification}.
   *
   * @throws SanValidationException if the input is not canonical SAN, or is canonical but does not represent a legal
   *                                move on the current position
   */
  public static StrictSanParserValidationResult parseText(String san, Board board) throws SanValidationException {
    final MoveSpecification moveSpecification = parseTextInternal(san, board);
    return new StrictSanParserValidationResult(moveSpecification);
  }

  private static MoveSpecification parseTextInternal(String san, Board board) throws SanValidationException {
    final SanParse sanParse = SanValidateFormat.validateFormat(san);

    SanValidateNonMovement.validateNonMovement(sanParse);

    final Side havingMove = board.getHavingMove();
    SanValidateMovement.validateMovement(sanParse, havingMove);

    final SanFormat sanFormat = sanParse.sanFormat();
    final SanConversion sanConversion = sanParse.sanConversion();

    SanValidatePieceExists.validatePieceExists(havingMove, sanFormat, sanConversion, sanConversion.movingPieceType(),
        board.getBitboardPosition());

    SanValidateDestination.validateDestinationSquareSemantics(board, havingMove, sanFormat, sanConversion);

    final List<LegalMove> legalMovesCandidates = SanValidateLegalMoves.calculateLegalMovesCandidates(board, havingMove,
        sanParse);
    SanValidateLegalMoves.validateAgainstLegalMoves(board, havingMove, legalMovesCandidates, sanFormat, sanConversion);

    final LegalMove legalMoveOnlyCandidate = SanValidateLegalMoves.calculateOnlyPossibleLegalMove(sanFormat,
        sanConversion, legalMovesCandidates);
    final MoveSpecification moveSpecification = SanValidateLegalMoves.calculateMoveSpecificationForSan(board,
        havingMove, sanFormat, sanConversion, legalMoveOnlyCandidate.moveSpecification());
    if (!moveSpecification.equals(legalMoveOnlyCandidate.moveSpecification())) {
      throw new ProgrammingMistakeException("A mistake happened in the move construction");
    }

    SanValidateCheck.validateSanTerminalMarker(board, sanConversion.sanTerminalMarker(), moveSpecification);

    return moveSpecification;
  }
}
