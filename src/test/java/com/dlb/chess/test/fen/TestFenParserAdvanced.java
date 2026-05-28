package com.dlb.chess.test.fen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.CastlingRight;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.constants.ChessConstants;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.common.enums.FenAdvancedValidationProblem;
import com.dlb.chess.common.exceptions.FenAdvancedValidationException;
import com.dlb.chess.fen.FenParserAdvanced;
import com.dlb.chess.fen.FenParserRaw;
import com.dlb.chess.fen.constants.FenConstants;
import com.dlb.chess.fen.model.Fen;
import com.dlb.chess.fen.model.FenRaw;

class TestFenParserAdvanced implements EnumConstants {

  @SuppressWarnings("static-method")
  @Test
  void testParseFenString() {
    final FenRaw actual = FenParserRaw
        .parseFenRaw("position havingMove castlingRight enPassantCaptureTargetSquare halfMoveClock fullMoveNumber");

    assertEquals(new FenRaw("position", "havingMove", "castlingRight", "enPassantCaptureTargetSquare", "halfMoveClock",
        "fullMoveNumber"), actual);

  }

  @SuppressWarnings("static-method")
  @Test
  void testParseFenSuccess() {
    checkParseFenSuccess(FenConstants.FEN_INITIAL_STR);
    checkParseFenSuccess("rnbqkbnr/pppppppp/8/8/8/N7/PPPPPPPP/R1BQKBNR b KQkq - 1 1");

    // such a position is not possible to arise, but we don't check that
    checkParseFenSuccess("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 2");
  }

  @SuppressWarnings("static-method")
  @Test
  void testParseFenException() {
    checkParseFenException("", FenAdvancedValidationProblem.INVALID_FORMAT);

    // position format
    checkParseFenException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/ w KQkq - 0 1",
        FenAdvancedValidationProblem.INVALID_POSITION_ENDS_WITH_FORWARD_SLASH);

    checkParseFenException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR// w KQkq - 0 1",
        FenAdvancedValidationProblem.INVALID_POSITION_ENDS_WITH_FORWARD_SLASH);

    checkParseFenException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/// w KQkq - 0 1",
        FenAdvancedValidationProblem.INVALID_POSITION_ENDS_WITH_FORWARD_SLASH);

    checkParseFenException("//rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
        FenAdvancedValidationProblem.INVALID_POSITION_EMPTY_RANK);

    checkParseFenException("rnbqkbnr//pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
        FenAdvancedValidationProblem.INVALID_POSITION_EMPTY_RANK);

    checkParseFenException("rnbqkbnr/pppppppp/8/8/8//8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
        FenAdvancedValidationProblem.INVALID_POSITION_EMPTY_RANK);

    checkParseFenException("RNBQKBNR//rn w KQkq - 0 1", FenAdvancedValidationProblem.INVALID_POSITION_EMPTY_RANK);

    checkParseFenException("pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
        FenAdvancedValidationProblem.INVALID_POSITION_NUMBER_OF_RANKS);

    checkParseFenException("8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
        FenAdvancedValidationProblem.INVALID_POSITION_NUMBER_OF_RANKS);

    checkParseFenException("RNBQKBNR w KQkq - 0 1", FenAdvancedValidationProblem.INVALID_POSITION_NUMBER_OF_RANKS);

    checkParseFenException("xnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
        FenAdvancedValidationProblem.INVALID_POSITION_UNKNOWN_CHAR);

    checkParseFenException("-nbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
        FenAdvancedValidationProblem.INVALID_POSITION_UNKNOWN_CHAR);

    checkParseFenException("0nbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
        FenAdvancedValidationProblem.INVALID_POSITION_UNKNOWN_CHAR);

    checkParseFenException("9nbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
        FenAdvancedValidationProblem.INVALID_POSITION_UNKNOWN_CHAR);

    checkParseFenException("nbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
        FenAdvancedValidationProblem.INVALID_POSITION_LINE_EVALUATION_LENGTH);

    checkParseFenException("rnbqkbnr/pppppppp/7/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
        FenAdvancedValidationProblem.INVALID_POSITION_LINE_EVALUATION_LENGTH);

    // position illegal

    // white move - black king in check
    checkParseFenException("rnbq1rk1/pppp1Bpp/5n2/4p3/1b2P3/5N2/PPPP1PPP/RNBQ1RK1 w - - 0 5",
        FenAdvancedValidationProblem.INVALID_POSITION_CHECK);

    // black move - white king in check
    checkParseFenException("rnbq1rk1/pppp1ppp/5n2/4p3/1PB1P3/5N1P/P1PP1bP1/RNBQ1RK1 b - - 0 7",
        FenAdvancedValidationProblem.INVALID_POSITION_CHECK);

    // having move
    // range
    checkParseFenException("r3k2r/8/8/8/8/8/8/R2K3R x - - 0 100",
        FenAdvancedValidationProblem.INVALID_HAVING_MOVE_RANGE);
    checkParseFenException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR x KQkq - 0 1",
        FenAdvancedValidationProblem.INVALID_HAVING_MOVE_RANGE);

    // castling right
    // range
    checkParseFenException("r3k2r/8/8/8/8/8/8/R2K3R w x - 0 100",
        FenAdvancedValidationProblem.INVALID_CASTLING_RIGHT_RANGE);
    checkParseFenException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b x - 0 1",
        FenAdvancedValidationProblem.INVALID_CASTLING_RIGHT_RANGE);

    // invalid value
    // white both for king
    checkParseFenException("r3k2r/8/8/8/8/8/8/R2K3R w KQkq - 0 100",
        FenAdvancedValidationProblem.INVALID_CASTLING_RIGHT_WHITE_BOTH);
    // white both for rooks
    checkParseFenException("r3k2r/8/8/8/8/8/8/1R2K1R1 w KQkq - 0 100",
        FenAdvancedValidationProblem.INVALID_CASTLING_RIGHT_WHITE_BOTH);
    // white kingside for king
    checkParseFenException("r3k2r/8/8/8/8/8/8/R2K3R w Kkq - 0 100",
        FenAdvancedValidationProblem.INVALID_CASTLING_RIGHT_WHITE_KINGSIDE);
    // white kingside for rook
    checkParseFenException("r3k2r/8/8/8/8/8/8/1R2K1R1 w Kkq - 0 100",
        FenAdvancedValidationProblem.INVALID_CASTLING_RIGHT_WHITE_KINGSIDE);
    // white queenside for king
    checkParseFenException("r3k2r/8/8/8/8/8/8/R2K3R w Qkq - 0 100",
        FenAdvancedValidationProblem.INVALID_CASTLING_RIGHT_WHITE_QUEENSIDE);
    // white queenside for rook
    checkParseFenException("r3k2r/8/8/8/8/8/8/1R2K1R1 w Qkq - 0 100",
        FenAdvancedValidationProblem.INVALID_CASTLING_RIGHT_WHITE_QUEENSIDE);

    // black both for king
    checkParseFenException("r2k3r/8/8/8/8/8/8/RN2K1NR w KQkq - 0 100",
        FenAdvancedValidationProblem.INVALID_CASTLING_RIGHT_BLACK_BOTH);
    // black both for rooks
    checkParseFenException("1r2k3/7r/8/8/8/8/8/RN2K1NR w KQkq - 0 100",
        FenAdvancedValidationProblem.INVALID_CASTLING_RIGHT_BLACK_BOTH);
    // black kingside for king
    checkParseFenException("r2k3r/8/8/8/8/8/8/RN2K1NR w Kk - 0 100",
        FenAdvancedValidationProblem.INVALID_CASTLING_RIGHT_BLACK_KINGSIDE);
    // black kingside for rook
    checkParseFenException("1r2k3/7r/8/8/8/8/8/RN2K1NR w Kk - 0 100",
        FenAdvancedValidationProblem.INVALID_CASTLING_RIGHT_BLACK_KINGSIDE);
    // black queenside for king
    checkParseFenException("r2k3r/8/8/8/8/8/8/RN2K1NR w Qq - 0 100",
        FenAdvancedValidationProblem.INVALID_CASTLING_RIGHT_BLACK_QUEENSIDE);
    // black queenside for rook
    checkParseFenException("1r2k3/7r/8/8/8/8/8/RN2K1NR w Qq - 0 100",
        FenAdvancedValidationProblem.INVALID_CASTLING_RIGHT_BLACK_QUEENSIDE);

    // en passant target square

    // range
    checkParseFenException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq x 0 1",
        FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_TARGET_SQUARE_RANGE);

    checkParseFenException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq x2 0 1",
        FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_TARGET_SQUARE_RANGE);

    checkParseFenException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq a0 0 1",
        FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_TARGET_SQUARE_RANGE);

    // invalid value - no target square
    checkParseFenException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq a2 0 1",
        FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_TARGET_SQUARE_RANGE);

    checkParseFenException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq b5 0 1",
        FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_TARGET_SQUARE_RANGE);

    checkParseFenException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq c7 0 1",
        FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_TARGET_SQUARE_RANGE);

    // invalid value - target square own
    checkParseFenException("rnbqkbnr/pppppppp/8/8/8/4P3/PPPP1PPP/RNBQKBNR b KQkq d6 0 1",
        FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_TARGET_SQUARE_WRONG_COLOR);

    checkParseFenException("rnbqkbnr/ppp1pppp/3p4/8/8/4P3/PPPP1PPP/RNBQKBNR w KQkq e3 0 2",
        FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_TARGET_SQUARE_WRONG_COLOR);

    // no pawn after target square
    checkParseFenException("rnbqkbnr/pppppppp/8/8/8/5N2/PPPPPPPP/RNBQKB1R b KQkq e3 0 1",
        FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_TARGET_SQUARE_NO_PAWN_AFTER);

    checkParseFenException("r1bqkbnr/pppppppp/2n5/8/8/5N2/PPPPPPPP/RNBQKB1R w KQkq d6 0 2",
        FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_TARGET_SQUARE_NO_PAWN_AFTER);

    checkParseFenException("rnbqkbnr/pppppppp/8/8/8/4P3/PPPP1PPP/RNBQKBNR b KQkq e3 0 1",
        FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_TARGET_SQUARE_NO_PAWN_AFTER);

    checkParseFenException("rnbqkbnr/p1pppppp/1p6/8/8/4P3/PPPP1PPP/RNBQKBNR w KQkq b6 0 2",
        FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_TARGET_SQUARE_NO_PAWN_AFTER);

    // Regression: opponent NON-PAWN on the two-advance square (white knight on e4 when the claimed e2-e4
    // pawn advance would put a white pawn there for black-to-move EP). The original predicate read
    // `A || (B && C)` due to Java operator precedence - A and B&&C both false here (right side, wrong piece
    // type), so the FEN slipped past validation and tripped an unchecked IllegalArgumentException in the
    // bitboard rewind (withRelocatedPiece preconditions the piece's presence). Fixed predicate `A || B || C`
    // throws the right typed exception.
    checkParseFenException("rnbqkb1r/pppp1ppp/8/3n4/4N3/8/PPPP1PPP/RNBQKB1R b KQkq e3 0 3",
        FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_TARGET_SQUARE_NO_PAWN_AFTER);
    // Regression: OWN-side pawn on the two-advance square (white pawn on d5 with white-to-move and EP target
    // d6 - d6 is black's EP target square, so the two-advance square is d5; a white pawn there is the wrong
    // side). The original predicate accepted this case because B&&C was false (B = wrong side true, but C =
    // not a pawn false). Fixed predicate throws.
    checkParseFenException("rnbqkb1r/p1pppppp/8/3P4/8/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 3",
        FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_TARGET_SQUARE_NO_PAWN_AFTER);

    // en passant target square not empty
    checkParseFenException("r1bqkbnr/2pppppp/p1n5/1p6/4P3/3PBN2/PPP2PPP/RN1QKB1R b KQkq e3 0 4",
        FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_TARGET_SQUARE_NOT_EMPTY);
    checkParseFenException("r1bqkb1r/2ppppp1/p1n4n/1p5p/4P3/3PBN1P/PPP2PP1/RN1QKB1R w KQkq h6 0 6",
        FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_TARGET_SQUARE_NOT_EMPTY);

    // starting square not empty
    checkParseFenException("r1bqkbnr/2pppppp/p1n5/1p6/4P3/3P1N2/PPP1BPPP/RNBQK2R b KQkq e3 0 4",
        FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_STARTING_SQUARE_NOT_EMPTY);
    checkParseFenException("r1bqkb1r/2pppppn/p1n5/1p5p/4P3/3PBN1P/PPP2PP1/RN1QKB1R w KQkq h6 0 6",
        FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_STARTING_SQUARE_NOT_EMPTY);

    // both not empty
    checkParseFenException("r1bqkbnr/2pppppp/p1n5/1p6/4P3/3PBN2/PPP1BPPP/RN1QK2R b KQkq e3 0 4",
        FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_BOTH_NOT_EMPTY);
    checkParseFenException("r1bqkb1r/2pppppn/p6n/1p5p/4P3/3PBN1P/PPP2PP1/RN1QKB1R w KQkq h6 0 6",
        FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_BOTH_NOT_EMPTY);

    // position before two-square advance illegal
    checkParseFenException("8/8/6k1/p1p1p3/P1P1Pp1p/5P1P/2B5/4K3 b - e3 0 50",
        FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_PREVIOUS_POSITION_ILLEGAL);
    checkParseFenException("1b2k3/8/5p1p/4pP1P/p1p1P3/P1P3K1/8/8 w - e6 0 50",
        FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_PREVIOUS_POSITION_ILLEGAL);

    // half-move clock
    checkParseFenException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - x 1",
        FenAdvancedValidationProblem.INVALID_HALF_MOVE_CLOCK_RANGE);

    checkParseFenException("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 1 1",
        FenAdvancedValidationProblem.INVALID_HALF_MOVE_CLOCK_NOT_ZERO_BUT_EN_PASSANT_CAPTURE_TARGET_SQUARE_SET);

    checkParseFenException("rnbqkb1r/ppp1pppp/5n2/3pP3/8/8/PPPP1PPP/RNBQKBNR w KQkq d6 2 3",
        FenAdvancedValidationProblem.INVALID_HALF_MOVE_CLOCK_NOT_ZERO_BUT_EN_PASSANT_CAPTURE_TARGET_SQUARE_SET);

    // full move counter
    checkParseFenException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 x",
        FenAdvancedValidationProblem.INVALID_FULL_MOVE_NUMBER_RANGE);

    checkParseFenException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 -1",
        FenAdvancedValidationProblem.INVALID_FULL_MOVE_NUMBER_NEGATIVE);

    checkParseFenException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 0",
        FenAdvancedValidationProblem.INVALID_FULL_MOVE_NUMBER_ZERO);

  }

  @SuppressWarnings("static-method")
  @Test
  void testParseFenMaxValues() {

    final var aboveMaxValue = Long.valueOf(Integer.MAX_VALUE) + 1;

    // half-move clock

    // outside integer parsing range
    checkParseFenException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - " + aboveMaxValue + " 100",
        FenAdvancedValidationProblem.INVALID_HALF_MOVE_CLOCK_RANGE);

    // Halfmove clock at and above the 75-move rule threshold (150) is accepted - the 75-move rule
    // is surfaced as a queryable predicate, not enforced at FEN import, so a position with
    // halfmove clock above 150 is a legitimate FEN.
    checkParseFenSuccess("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - "
        + ChessConstants.SEVENTY_FIVE_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD + " 76");
    checkParseFenSuccess("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - "
        + (ChessConstants.SEVENTY_FIVE_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD + 1) + " 77");

    // fullMoveNumber

    // outside integer parsing range
    checkParseFenException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 " + aboveMaxValue,
        FenAdvancedValidationProblem.INVALID_FULL_MOVE_NUMBER_RANGE);

    // above max value
    checkParseFenException(
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 " + (FenConstants.MAX_FULL_MOVE_NUMBER + 1),
        FenAdvancedValidationProblem.INVALID_FULL_MOVE_NUMBER_TOO_BIG_ABSOLUT);

  }

  @SuppressWarnings("static-method")
  @Test
  void testParseFenExceptionHalfMoveClockTooBigRelativeToFullMoveNumber() {
    // Half-move clock cannot exceed the maximum number of half-moves played by the start of the given
    // full-move number. A FEN like "... 3 2 with white to move" claims 3 half-moves have been played but the
    // counter is still on move 2 (max half-moves at start of move 2 for white = 2). Physical impossibility.
    // White to move, max half-moves = 2 * (fullMoveNumber - 1).
    checkParseFenException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 3 2",
        FenAdvancedValidationProblem.INVALID_HALF_MOVE_CLOCK_TOO_BIG_RELATIVE_TO_FULL_MOVE_NUMBER);
    // Black to move, max half-moves = 2 * (fullMoveNumber - 1) + 1 = 3 for move 2.
    checkParseFenException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 4 2",
        FenAdvancedValidationProblem.INVALID_HALF_MOVE_CLOCK_TOO_BIG_RELATIVE_TO_FULL_MOVE_NUMBER);
    // Mid-range: hmc=20 on fullMoveNumber=10 white means 20 half-moves on move 10 white, max is 18.
    checkParseFenException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 20 10",
        FenAdvancedValidationProblem.INVALID_HALF_MOVE_CLOCK_TOO_BIG_RELATIVE_TO_FULL_MOVE_NUMBER);
    // Boundary: hmc=2 on fullMoveNumber=2 white is valid (max = 2).
    checkParseFenSuccess("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 2 2");
  }

  @SuppressWarnings("static-method")
  @Test
  void testParseFenConverted() {

    final Fen actual = FenParserAdvanced.parseFenAdvanced(FenConstants.FEN_INITIAL_STR);

    assertEquals(StaticPosition.INITIAL_POSITION,
        com.dlb.chess.bitboard.StaticPositionBridge.toStaticPosition(actual.bitboardPosition()));
    assertEquals(WHITE, actual.havingMove());
    assertEquals(CastlingRight.KING_AND_QUEEN_SIDE, actual.castlingRightWhite());
    assertEquals(CastlingRight.KING_AND_QUEEN_SIDE, actual.castlingRightBlack());
    assertEquals(Square.NONE, actual.enPassantCaptureTargetSquare());
    assertEquals(0L, actual.halfMoveClock());
    assertEquals(1L, actual.fullMoveNumber());

  }

  @SuppressWarnings("static-method")
  @Test
  void testParseFenNumberOfPieces() {
    checkParseFenException("rnbqkbnr/pppppppp/8/8/8/K7/PPPPPPPP/RNBQKBNR w kq - 18 10",
        FenAdvancedValidationProblem.INVALID_WHITE_TOO_MANY_KINGS);
    checkParseFenException("rnbqkbnr/pppppppp/8/3k4/8/8/PPPPPPPP/RNBQKBNR b KQ - 19 10",
        FenAdvancedValidationProblem.INVALID_BLACK_TOO_MANY_KINGS);

    checkParseFenException("rn1qkb1r/pbp2ppr/1p1p1n1p/2b1p3/2BPP1q1/1KN4P/PPPB1PPp/R2QK1NR w kq - 0 10",
        FenAdvancedValidationProblem.INVALID_WHITE_TOO_MANY_KINGS);
    checkParseFenException("rnkqkb1r/pbp2ppr/1p1p1n1p/2b1p3/2BPP1q1/2N4P/PPPB1PPp/R2QK1NR b KQ - 0 10",
        FenAdvancedValidationProblem.INVALID_BLACK_TOO_MANY_KINGS);

    checkParseFenException("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQ1BNR w KQkq - 0 10",
        FenAdvancedValidationProblem.INVALID_WHITE_NO_KING);
    checkParseFenException("rnbq1bnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 10",
        FenAdvancedValidationProblem.INVALID_BLACK_NO_KING);

    // we check white said first and halt on first error, if both kings are off the board an exception about white king
    // missing is expected
    checkParseFenException("rnbq1bnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQ1BNR w KQkq - 0 10",
        FenAdvancedValidationProblem.INVALID_WHITE_NO_KING);

    checkParseFenException("rnbqkbnr/pppppppp/8/8/8/P7/PPPPPPPP/RNBQKBNR w KQkq - 0 10",
        FenAdvancedValidationProblem.INVALID_WHITE_TOO_MANY_PAWNS);
    checkParseFenException("rnbqkbnr/pppppppp/3p4/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 10",
        FenAdvancedValidationProblem.INVALID_BLACK_TOO_MANY_PAWNS);

    checkParseFenException("rn1qkb1r/pbp2ppr/1p1p1n1p/P3p3/2BPP1q1/2N4P/PPPB1PP1/R2QK1NR w KQkq - 0 10",
        FenAdvancedValidationProblem.INVALID_WHITE_TOO_MANY_PAWNS);
    checkParseFenException("rn1qkb1r/pbp2ppr/1p1p1n1p/4p3/2BPP1q1/2N4P/PPPB1PPp/R2QK1NR b KQkq - 0 10",
        FenAdvancedValidationProblem.INVALID_BLACK_TOO_MANY_PAWNS);

    checkParseFenException("rnbqkbnr/pppppppp/8/8/R7/8/PPPPPPPP/RNBQKBNR w KQkq - 18 10",
        FenAdvancedValidationProblem.INVALID_WHITE_TOO_MANY_ROOKS);
    checkParseFenException("rnbqkbnr/pppppppp/3r4/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 19 10",
        FenAdvancedValidationProblem.INVALID_BLACK_TOO_MANY_ROOKS);

    checkParseFenException("rn1qkb1r/pbp2pp1/1p1p1n1p/4p3/2BPP3/2N4P/PPPB1PPR/R2QK1NR w KQkq - 0 10",
        FenAdvancedValidationProblem.INVALID_WHITE_TOO_MANY_ROOKS);
    checkParseFenException("rn1qkb1r/pbp2ppr/1p1p1n1p/4p3/2BPP3/2N4P/PPPB1PP1/R2QK1NR b KQkq - 0 10",
        FenAdvancedValidationProblem.INVALID_BLACK_TOO_MANY_ROOKS);

    checkParseFenException("rnbqkbnr/pppppppp/8/8/N7/8/PPPPPPPP/RNBQKBNR w KQkq - 0 10",
        FenAdvancedValidationProblem.INVALID_WHITE_TOO_MANY_KNIGHTS);
    checkParseFenException("rnbqkbnr/pppppppp/3n4/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 10",
        FenAdvancedValidationProblem.INVALID_BLACK_TOO_MANY_KNIGHTS);

    checkParseFenException("rn1qkb1r/pbp2ppr/1p1p1n1p/4p3/2BPP3/2N4P/PPPB1PP1/RN1QK1NR w KQkq - 0 10",
        FenAdvancedValidationProblem.INVALID_WHITE_TOO_MANY_KNIGHTS);
    checkParseFenException("rn1qkb1r/pbp2pp1/1p1p1n1p/3np3/2BPP3/2N4P/PPPB1PP1/R2QK1NR b KQkq - 0 10",
        FenAdvancedValidationProblem.INVALID_BLACK_TOO_MANY_KNIGHTS);

    checkParseFenException("rnbqkbnr/pppppppp/8/8/8/5B2/PPPPPPPP/RNBQKBNR w KQkq - 18 10",
        FenAdvancedValidationProblem.INVALID_WHITE_TOO_MANY_LIGHT_SQUARE_BISHOPS);
    checkParseFenException("rnbqkbnr/pppppppp/8/8/8/3b4/PPPPPPPP/RNBQKBNR b KQkq - 19 10",
        FenAdvancedValidationProblem.INVALID_BLACK_TOO_MANY_LIGHT_SQUARE_BISHOPS);

    checkParseFenException("rn1qkb1r/pbp2ppr/1p1p1n1p/4p3/2BPP1q1/2NB3P/PPPB1PPp/R2QK1NR w KQkq - 0 10",
        FenAdvancedValidationProblem.INVALID_WHITE_TOO_MANY_LIGHT_SQUARE_BISHOPS);
    checkParseFenException("rn1qkb1r/pbp2pp1/1pbp1n1p/4p3/2BPP3/2N4P/PPPB1PP1/R2QK1NR b KQkq - 0 10",
        FenAdvancedValidationProblem.INVALID_BLACK_TOO_MANY_LIGHT_SQUARE_BISHOPS);

    checkParseFenException("rnbqkbnr/pppppppp/1B6/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 18 10",
        FenAdvancedValidationProblem.INVALID_WHITE_TOO_MANY_DARK_SQUARE_BISHOPS);
    checkParseFenException("rnbqkbnr/pppppppp/7b/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 19 10",
        FenAdvancedValidationProblem.INVALID_BLACK_TOO_MANY_DARK_SQUARE_BISHOPS);

    checkParseFenException("rn1qkb1r/pbp2ppr/1p1p1n1p/4p3/2BPP1q1/B1N4P/PPPB1PPp/R2QK1NR w KQkq - 0 10",
        FenAdvancedValidationProblem.INVALID_WHITE_TOO_MANY_DARK_SQUARE_BISHOPS);
    checkParseFenException("rn1qkb1r/pbp2pp1/1p1p1n1p/2b1p3/2BPP3/2N4P/PPPB1PP1/R2QK1NR b KQkq - 0 10",
        FenAdvancedValidationProblem.INVALID_BLACK_TOO_MANY_DARK_SQUARE_BISHOPS);

    checkParseFenException("rnbqkbnr/pppppppp/8/8/Q7/8/PPPPPPPP/RNBQKBNR w KQkq - 18 10",
        FenAdvancedValidationProblem.INVALID_WHITE_TOO_MANY_QUEENS);
    checkParseFenException("rnbqkbnr/pppppppp/3q4/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 19 10",
        FenAdvancedValidationProblem.INVALID_BLACK_TOO_MANY_QUEENS);

    checkParseFenException("rn1qkb1r/pbp2ppr/1p1p1n1p/4p3/2BPP1Q1/2N4P/PPPB1PP1/R2QK1NR w KQkq - 0 10",
        FenAdvancedValidationProblem.INVALID_WHITE_TOO_MANY_QUEENS);
    checkParseFenException("rn1qkb1r/pbp2pp1/1p1p1n1p/4p3/2BPP1q1/2N4P/PPPB1PP1/R2QK1NR b KQkq - 0 10",
        FenAdvancedValidationProblem.INVALID_BLACK_TOO_MANY_QUEENS);

  }

  @SuppressWarnings("static-method")
  @Test
  void testParseFenPawnOnPromotionRank() {
    checkParseFenException("3k1P2/8/8/4b3/8/4N3/4B3/3K4 w - - 0 100",
        FenAdvancedValidationProblem.INVALID_WHITE_PAWN_INVALID_RANK_PROMOTION_RANK);
    checkParseFenException("8/8/3kq3/4b3/8/4N3/3KB3/6p1 b - - 0 100",
        FenAdvancedValidationProblem.INVALID_BLACK_PAWN_INVALID_RANK_PROMOTION_RANK);
  }

  @SuppressWarnings("static-method")
  @Test
  void testParseFenPawnOnGroundRank() {
    checkParseFenException("8/8/3kq3/4b3/8/2Q1N3/3KB3/3P4 w - - 0 100",
        FenAdvancedValidationProblem.INVALID_WHITE_PAWN_INVALID_RANK_GROUND_RANK);
    checkParseFenException("7p/8/3kq3/3rb3/8/2Q1N3/2K1B3/8 w - - 0 100",
        FenAdvancedValidationProblem.INVALID_BLACK_PAWN_INVALID_RANK_GROUND_RANK);
  }

  private static void checkParseFenException(String fen, FenAdvancedValidationProblem expected) {
    var actual = FenAdvancedValidationProblem.SUCCESS;
    try {
      FenParserAdvanced.parseFenAdvanced(fen);
    } catch (final FenAdvancedValidationException e) {
      actual = e.getFenAdvancedValidationProblem();
    }
    assertEquals(expected, actual);
  }

  private static void checkParseFenSuccess(String fen) {
    var isException = false;
    try {
      FenParserAdvanced.parseFenAdvanced(fen);
    } catch (@SuppressWarnings("unused") final FenAdvancedValidationException e) {
      isException = true;
    }
    assertFalse(isException);
  }

}
