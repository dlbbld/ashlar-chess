// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.san;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.san.SanValidationException;
import io.github.dlbbld.ashlarchess.san.SanValidationProblem;
import io.github.dlbbld.ashlarchess.san.StrictSanParser;

/**
 * Per-entry coverage for {@link SanValidationProblem}. For each enum constant that can be triggered from a SAN input,
 * at least one test case is provided that:
 *
 * <ol>
 * <li>exercises a SAN string (plus board position, where relevant) that produces that exact problem code, and</li>
 * <li>asserts the full exception message - both static messages and parameter-substituted messages (e.g.
 * {@code ''{0}''}) are verified against the expected final string, so message templates and their interpolation are
 * covered in one place.</li>
 * </ol>
 *
 * <p>
 * The two non-triggering values {@link SanValidationProblem#NONE} and {@link SanValidationProblem#UNKNOWN_ERROR} have
 * no test case.
 */
class TestSanValidationProblemMessage {

  private static final Set<SanValidationProblem> checkedProblems = new TreeSet<>();
  private static final Set<SanValidationProblem> FIXED_UNCHECKED_ENTRIES = Nulls
      .setOf(SanValidationProblem.UNKNOWN_ERROR, SanValidationProblem.NONE);

  /**
   * When {@code true}, each test asserts the exact full exception message (useful while messages.properties is being
   * built up, to lock down wording and parameter interpolation). When stable, flip to {@code false} - only the problem
   * code is then checked, so message wording can be freely edited in the resource bundle without breaking tests.
   */
  private static final boolean IS_CHECK_MESSAGE = true;

  @SuppressWarnings("static-method")
  @Test
  void testFormatGeneral() {
    checkException("", SanValidationProblem.FORMAT_BLANK, "The value cannot be blank.");

    checkException("Z", SanValidationProblem.FORMAT_FIRST_CHARACTER,
        "A SAN move must start with a file letter (a-h), a piece letter (R, N, B, Q, K), or O for castling (letter O not digit 0), but starts with 'Z'.");

  }

  @SuppressWarnings("static-method")
  @Test
  void testFormatPawn() {

    // pawn
    checkException("a", SanValidationProblem.FORMAT_PAWN_NO_SECOND_CHARACTER,
        "For a pawn move the file must be followed by a rank for a forward move or by a 'x' for a capture move.");

    checkException("aa", SanValidationProblem.FORMAT_PAWN_WRONG_SECOND_CHARACTER,
        "For a pawn move, the second character must be a rank digit (1-8) or the capture symbol (x), but is 'a'.");

    // pawn forward non promotion
    checkException("e7=", SanValidationProblem.FORMAT_PAWN_FORWARD_NON_PROMOTION_OVERLENGTH,
        "A non promoting pawn forward move must have exactly 2 characters (excluding check/checkmate symbol).");

    // pawn forward promotion
    checkException("e8", SanValidationProblem.FORMAT_PAWN_FORWARD_PROMOTION_NO_PROMOTION_SYMBOL,
        "For a pawn promotion, the promotion symbol '=' is expected after the promotion rank.");

    checkException("e8x", SanValidationProblem.FORMAT_PAWN_FORWARD_PROMOTION_WRONG_PROMOTION_SYMBOL,
        "For a pawn promotion, the promotion symbol '=' is expected after the promotion rank, but is 'x'.");

    checkException("e8=", SanValidationProblem.FORMAT_PAWN_FORWARD_PROMOTION_NO_PROMOTION_PIECE,
        "For a pawn promotion, a promotion piece is required after the promotion symbol '='.");

    checkException("e8=P", SanValidationProblem.FORMAT_PAWN_FORWARD_PROMOTION_WRONG_PROMOTION_PIECE,
        "For a pawn promotion, the promotion piece must be R, N, B, or Q, but is 'P'.");

    checkException("e8=Qx", SanValidationProblem.FORMAT_PAWN_FORWARD_PROMOTION_OVERLENGTH,
        "A promoting pawn forward move must have exactly 4 characters (excluding check/checkmate symbol).");

    // pawn capture
    checkException("ax", SanValidationProblem.FORMAT_PAWN_CAPTURE_NO_FILE,
        "For a pawn capture, a file letter (a-h) is expected after the capture symbol for the destination file, but was not provided.");

    checkException("axQ", SanValidationProblem.FORMAT_PAWN_CAPTURE_WRONG_FILE,
        "For a pawn capture, a file letter (a-h) is expected after the capture symbol for the destination file, but is 'Q'.");

    checkException("axb", SanValidationProblem.FORMAT_PAWN_CAPTURE_NO_RANK,
        "For a pawn capture, a rank digit (1-8) is expected after the destination file for the destination rank, but was not provided.");

    checkException("axbb", SanValidationProblem.FORMAT_PAWN_CAPTURE_WRONG_RANK,
        "For a pawn capture, a rank digit (1-8) is expected after the destination file for the destination rank, but is 'b'.");

    // pawn capture non promotion
    checkException("axb7=", SanValidationProblem.FORMAT_PAWN_CAPTURE_NON_PROMOTION_OVERLENGTH,
        "A non promoting pawn capturing move must have exactly 4 characters (excluding check/checkmate symbol).");

    // pawn capture promotion
    checkException("axb8", SanValidationProblem.FORMAT_PAWN_CAPTURE_PROMOTION_NO_PROMOTION_SYMBOL,
        "For a pawn promotion, the promotion symbol '=' is expected after the promotion rank.");

    checkException("axb8x", SanValidationProblem.FORMAT_PAWN_CAPTURE_PROMOTION_WRONG_PROMOTION_SYMBOL,
        "For a pawn promotion, the promotion symbol '=' is expected after the promotion rank, but is 'x'.");

    checkException("axb8=", SanValidationProblem.FORMAT_PAWN_CAPTURE_PROMOTION_NO_PROMOTION_PIECE,
        "For a pawn promotion, a promotion piece is required after the promotion symbol '='.");

    checkException("axb8=P", SanValidationProblem.FORMAT_PAWN_CAPTURE_PROMOTION_WRONG_PROMOTION_PIECE,
        "For a pawn promotion, the promotion piece must be R, N, B, or Q, but is 'P'.");

    checkException("axb8=Qx", SanValidationProblem.FORMAT_PAWN_CAPTURE_PROMOTION_OVERLENGTH,
        "A promoting pawn capturing move must have exactly 6 characters (excluding check/checkmate symbol).");

  }

  @SuppressWarnings("static-method")
  @Test
  void testFormatKing() {

    // king
    // king castling
    checkException("O-", SanValidationProblem.FORMAT_KING_CASTLING,
        "When the value starts with 'O', it must be either castling king-side (O-O) or castling queen-side (O-O-O).");

    // king non castling non capture - destination file (second character when not 'x')
    checkException("K", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_NO_DESTINATION_FILE,
        "For a king non-castling non-capturing move, after the king letter a file letter (a-h) is expected for the destination file, but was not provided.");

    checkException("K=", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_WRONG_DESTINATION_FILE,
        "For a king non-castling non-capturing move, after the king letter a file letter (a-h) is expected for the destination file, but is '='.");

    // rank-disambiguation attempts like "K2e5" collapse into NON_CAPTURE_WRONG_DESTINATION_FILE
    checkException("K2e5", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_WRONG_DESTINATION_FILE,
        "For a king non-castling non-capturing move, after the king letter a file letter (a-h) is expected for the destination file, but is '2'.");

    // king non castling non capture - destination rank
    checkException("Ke", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_NO_DESTINATION_RANK,
        "For a king non-castling non-capturing move, after the destination file a rank digit (1-8) is expected for the destination rank, but was not provided.");

    checkException("KeR", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_WRONG_DESTINATION_RANK,
        "For a king non-castling non-capturing move, after the destination file a rank digit (1-8) is expected for the destination rank, but is 'R'.");

    // file-disambiguation attempts like "Kae5" collapse into WRONG_DESTINATION_RANK (third char 'e' is a file, not a
    // rank)
    checkException("Kae5", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_WRONG_DESTINATION_RANK,
        "For a king non-castling non-capturing move, after the destination file a rank digit (1-8) is expected for the destination rank, but is 'e'.");

    checkException("Ke5R", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_OVERLENGTH,
        "A king non-castling non-capturing move must have exactly 3 characters (excluding check/checkmate symbol).");

    // square-disambiguation attempts like "Ka2b3" collapse into OVERLENGTH_NON_CAPTURE
    checkException("Ka2b3", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_OVERLENGTH,
        "A king non-castling non-capturing move must have exactly 3 characters (excluding check/checkmate symbol).");

    // king non castling capture
    checkException("Kx", SanValidationProblem.FORMAT_KING_NON_CASTLING_CAPTURE_NO_DESTINATION_FILE,
        "For a king non-castling capturing move, after the capture symbol a file letter (a-h) is expected for the destination file, but was not provided.");

    checkException("KxR", SanValidationProblem.FORMAT_KING_NON_CASTLING_CAPTURE_WRONG_DESTINATION_FILE,
        "For a king non-castling capturing move, after the capture symbol a file letter (a-h) is expected for the destination file, but is 'R'.");

    checkException("Kxe", SanValidationProblem.FORMAT_KING_NON_CASTLING_CAPTURE_NO_DESTINATION_RANK,
        "For a king non-castling capturing move, after the destination file a rank digit (1-8) is expected for the destination rank, but was not provided.");

    checkException("KxeR", SanValidationProblem.FORMAT_KING_NON_CASTLING_CAPTURE_WRONG_DESTINATION_RANK,
        "For a king non-castling capturing move, after the destination file a rank digit (1-8) is expected for the destination rank, but is 'R'.");

    checkException("Kxe5a", SanValidationProblem.FORMAT_KING_NON_CASTLING_CAPTURE_OVERLENGTH,
        "A king non-castling capturing move must have exactly 4 characters (excluding check/checkmate symbol).");

  }

  @SuppressWarnings("static-method")
  @Test
  void testFormatRnbq() {

    // Cases listed in sequential parse order. The parser branches at each ambiguity point and commits to a
    // disambiguation only when the next character disambiguates (or when the SAN ends and the shorter valid form
    // is complete).

    // --- pos 1: second character ---

    checkException("Q", SanValidationProblem.FORMAT_RNBQ_NO_SECOND_CHARACTER,
        "For a piece move (R, N, B, Q), the piece letter must be followed by a file letter (a-h),"
            + " a rank digit (1-8), or the capture symbol (x).");

    checkException("Q=", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER,
        "For a piece move (R, N, B, Q), the second character must be a file letter (a-h),"
            + " a rank digit (1-8), or the capture symbol (x), but is '='.");

    // --- Rx... - capture, no disambiguation ---

    checkException("Qx", SanValidationProblem.FORMAT_RNBQ_CAPTURE_NO_DESTINATION_FILE,
        "For a piece move with no disambiguation and capturing, after the capture symbol a file letter (a-h)"
            + " is expected for the destination file, but was not provided.");

    checkException("Qx=", SanValidationProblem.FORMAT_RNBQ_CAPTURE_WRONG_DESTINATION_FILE,
        "For a piece move with no disambiguation and capturing, after the capture symbol a file letter (a-h)"
            + " is expected for the destination file, but is '='.");

    checkException("Qxe", SanValidationProblem.FORMAT_RNBQ_CAPTURE_NO_DESTINATION_RANK,
        "For a piece move with no disambiguation and capturing, after the destination file a rank digit (1-8)"
            + " is expected for the destination rank, but was not provided.");

    checkException("Qxe=", SanValidationProblem.FORMAT_RNBQ_CAPTURE_WRONG_DESTINATION_RANK,
        "For a piece move with no disambiguation and capturing, after the destination file a rank digit (1-8)"
            + " is expected for the destination rank, but is '='.");

    checkException("Qxe5y", SanValidationProblem.FORMAT_RNBQ_CAPTURE_OVERLENGTH,
        "A piece move with no disambiguation and capturing must have exactly 4 characters"
            + " (excluding check/checkmate symbol).");

    // --- R[rank]... - rank branch (pos 2 expects 'x' or file letter) ---

    checkException("Q2", SanValidationProblem.FORMAT_RNBQ_RANK_NO_THIRD_CHARACTER,
        "After the source-rank digit, a file letter (destination file for a non-capturing move)"
            + " or the capture symbol (x) is expected, but was not provided.");

    checkException("Q2=", SanValidationProblem.FORMAT_RNBQ_RANK_WRONG_THIRD_CHARACTER,
        "After the source-rank digit, a file letter (destination file for a non-capturing move)"
            + " or the capture symbol (x) is expected, but is '='.");

    // R[rank][toFile][toRank] - non-capture rank disambiguation
    checkException("Q2a", SanValidationProblem.FORMAT_RNBQ_NON_CAPTURE_RANK_NO_DESTINATION_RANK,
        "For a piece move with rank disambiguation and non-capturing, after the destination file a rank digit"
            + " (1-8) is expected for the destination rank, but was not provided.");

    checkException("Q2a=", SanValidationProblem.FORMAT_RNBQ_NON_CAPTURE_RANK_WRONG_DESTINATION_RANK,
        "For a piece move with rank disambiguation and non-capturing, after the destination file a rank digit"
            + " (1-8) is expected for the destination rank, but is '='.");

    checkException("Q2a1y", SanValidationProblem.FORMAT_RNBQ_NON_CAPTURE_RANK_OVERLENGTH,
        "A piece move with rank disambiguation and non-capturing must have exactly 4 characters"
            + " (excluding check/checkmate symbol).");

    // R[rank]x[toFile][toRank] - capture rank disambiguation
    checkException("Q2x", SanValidationProblem.FORMAT_RNBQ_CAPTURE_RANK_NO_DESTINATION_FILE,
        "For a piece move with rank disambiguation and capturing, after the capture symbol a file letter (a-h)"
            + " is expected for the destination file, but was not provided.");

    checkException("Q2x=", SanValidationProblem.FORMAT_RNBQ_CAPTURE_RANK_WRONG_DESTINATION_FILE,
        "For a piece move with rank disambiguation and capturing, after the capture symbol a file letter (a-h)"
            + " is expected for the destination file, but is '='.");

    checkException("Q2xa", SanValidationProblem.FORMAT_RNBQ_CAPTURE_RANK_NO_DESTINATION_RANK,
        "For a piece move with rank disambiguation and capturing, after the destination file a rank digit (1-8)"
            + " is expected for the destination rank, but was not provided.");

    checkException("Q2xa=", SanValidationProblem.FORMAT_RNBQ_CAPTURE_RANK_WRONG_DESTINATION_RANK,
        "For a piece move with rank disambiguation and capturing, after the destination file a rank digit (1-8)"
            + " is expected for the destination rank, but is '='.");

    checkException("Q2xa1y", SanValidationProblem.FORMAT_RNBQ_CAPTURE_RANK_OVERLENGTH,
        "A piece move with rank disambiguation and capturing must have exactly 5 characters"
            + " (excluding check/checkmate symbol).");

    // --- R[file]... - file branch (3-way ambiguity at pos 2) ---

    checkException("Qa", SanValidationProblem.FORMAT_RNBQ_FILE_NO_THIRD_CHARACTER,
        "After the file letter, a rank digit (destination rank), another file letter (destination file when"
            + " a source file is being specified), or the capture symbol (x) is expected, but was not provided.");

    checkException("Qa=", SanValidationProblem.FORMAT_RNBQ_FILE_WRONG_THIRD_CHARACTER,
        "After the file letter, a rank digit (destination rank), another file letter (destination file when"
            + " a source file is being specified), or the capture symbol (x) is expected, but is '='.");

    // R[fromFile][toFile][toRank] - non-capture file disambiguation
    checkException("Qab", SanValidationProblem.FORMAT_RNBQ_NON_CAPTURE_FILE_NO_DESTINATION_RANK,
        "For a piece move with file disambiguation and non-capturing, after the destination file a rank digit"
            + " (1-8) is expected for the destination rank, but was not provided.");

    checkException("Qab=", SanValidationProblem.FORMAT_RNBQ_NON_CAPTURE_FILE_WRONG_DESTINATION_RANK,
        "For a piece move with file disambiguation and non-capturing, after the destination file a rank digit"
            + " (1-8) is expected for the destination rank, but is '='.");

    checkException("Qab1y", SanValidationProblem.FORMAT_RNBQ_NON_CAPTURE_FILE_OVERLENGTH,
        "A piece move with file disambiguation and non-capturing must have exactly 4 characters"
            + " (excluding check/checkmate symbol).");

    // R[fromFile]x[toFile][toRank] - capture file disambiguation
    checkException("Qax", SanValidationProblem.FORMAT_RNBQ_CAPTURE_FILE_NO_DESTINATION_FILE,
        "For a piece move with file disambiguation and capturing, after the capture symbol a file letter (a-h)"
            + " is expected for the destination file, but was not provided.");

    checkException("Qax=", SanValidationProblem.FORMAT_RNBQ_CAPTURE_FILE_WRONG_DESTINATION_FILE,
        "For a piece move with file disambiguation and capturing, after the capture symbol a file letter (a-h)"
            + " is expected for the destination file, but is '='.");

    checkException("Qaxb", SanValidationProblem.FORMAT_RNBQ_CAPTURE_FILE_NO_DESTINATION_RANK,
        "For a piece move with file disambiguation and capturing, after the destination file a rank digit (1-8)"
            + " is expected for the destination rank, but was not provided.");

    checkException("Qaxb=", SanValidationProblem.FORMAT_RNBQ_CAPTURE_FILE_WRONG_DESTINATION_RANK,
        "For a piece move with file disambiguation and capturing, after the destination file a rank digit (1-8)"
            + " is expected for the destination rank, but is '='.");

    checkException("Qaxb1y", SanValidationProblem.FORMAT_RNBQ_CAPTURE_FILE_OVERLENGTH,
        "A piece move with file disambiguation and capturing must have exactly 5 characters"
            + " (excluding check/checkmate symbol).");

    // --- R[file][rank]... - ambiguous: Ra1 (length 3, valid) or source-square prefix (length > 3) ---

    // When a fourth char is present we commit to source-square. It must be 'x' (capture) or a file letter.
    checkException("Qa1=", SanValidationProblem.FORMAT_RNBQ_SQUARE_WRONG_THIRD_CHARACTER,
        "After the source square, a file letter (destination file) or the capture symbol (x) is expected,"
            + " but is '='.");

    // R[fromFile][fromRank][toFile][toRank] - non-capture square disambiguation
    checkException("Qa1b", SanValidationProblem.FORMAT_RNBQ_NON_CAPTURE_SQUARE_NO_DESTINATION_RANK,
        "For a piece move with square disambiguation and non-capturing, after the destination file a rank digit"
            + " (1-8) is expected for the destination rank, but was not provided.");

    checkException("Qa1b=", SanValidationProblem.FORMAT_RNBQ_NON_CAPTURE_SQUARE_WRONG_DESTINATION_RANK,
        "For a piece move with square disambiguation and non-capturing, after the destination file a rank digit"
            + " (1-8) is expected for the destination rank, but is '='.");

    checkException("Qa1b2y", SanValidationProblem.FORMAT_RNBQ_NON_CAPTURE_SQUARE_OVERLENGTH,
        "A piece move with square disambiguation and non-capturing must have exactly 5 characters"
            + " (excluding check/checkmate symbol).");

    // R[fromFile][fromRank]x[toFile][toRank] - capture square disambiguation
    checkException("Qa1x", SanValidationProblem.FORMAT_RNBQ_CAPTURE_SQUARE_NO_DESTINATION_FILE,
        "For a piece move with square disambiguation and capturing, after the capture symbol a file letter (a-h)"
            + " is expected for the destination file, but was not provided.");

    checkException("Qa1x=", SanValidationProblem.FORMAT_RNBQ_CAPTURE_SQUARE_WRONG_DESTINATION_FILE,
        "For a piece move with square disambiguation and capturing, after the capture symbol a file letter (a-h)"
            + " is expected for the destination file, but is '='.");

    checkException("Qa1xb", SanValidationProblem.FORMAT_RNBQ_CAPTURE_SQUARE_NO_DESTINATION_RANK,
        "For a piece move with square disambiguation and capturing, after the destination file a rank digit"
            + " (1-8) is expected for the destination rank, but was not provided.");

    checkException("Qa1xb=", SanValidationProblem.FORMAT_RNBQ_CAPTURE_SQUARE_WRONG_DESTINATION_RANK,
        "For a piece move with square disambiguation and capturing, after the destination file a rank digit"
            + " (1-8) is expected for the destination rank, but is '='.");

    checkException("Qa1xb2y", SanValidationProblem.FORMAT_RNBQ_CAPTURE_SQUARE_OVERLENGTH,
        "A piece move with square disambiguation and capturing must have exactly 6 characters"
            + " (excluding check/checkmate symbol).");

  }

  @SuppressWarnings("static-method")
  @Test
  void testNonMovementRnbq() {
    checkException("Rd4d4", SanValidationProblem.NON_MOVEMENT_RNBQ_SOURCE_SQUARE_EQUALS_DESTINATION_SQUARE,
        "For a movement to be valid, the source square cannot be the same as the destination square.");

  }

  @SuppressWarnings("static-method")
  @Test
  void testMovementPawn() {
    checkException("d2", new Board(), SanValidationProblem.MOVEMENT_PAWN_FORWARD_BACKWARDS,
        "A pawn cannot move backwards.");

    checkException("fxh4", new Board(), SanValidationProblem.MOVEMENT_PAWN_CAPTURE_NON_ADJACENT_FILE,
        "A pawn can never capture on a non-adjacent file, only on adjacent files.");

  }

  @SuppressWarnings("static-method")
  @Test
  void testMovementRnbq() {
    checkException("Baa1", SanValidationProblem.MOVEMENT_RNBQ_FROM_FILE,
        "A bishop can never move from file a to square a1. A bishop can only move diagonally.");

    checkException("N2a2", SanValidationProblem.MOVEMENT_RNBQ_FROM_RANK,
        "A knight can never move from rank 2 to square a2. A knight can only move in an L-shape.");

    checkException("Qa1c2", SanValidationProblem.MOVEMENT_RNBQ_FROM_SQUARE,
        "A queen can never move from square a1 to square c2. A queen can only move orthogonally or diagonally.");

  }

  @SuppressWarnings("static-method")
  @Test
  void testExistsPawn() {
    final Board board = new Board();
    board.movesStrict("a4", "h6", "a5", "h5", "a6", "h4", "axb7", "h3");
    checkException("a4", board, SanValidationProblem.EXISTS_PAWN, "There is no pawn on file a.");
  }

  @SuppressWarnings("static-method")
  @Test
  void testExistsRnbq() {
    final Board board = new Board();
    board.movesStrict("b3", "g6", "g3", "Bg7", "Na3", "Bxa1", "Nb1", "b6", "Na3", "Bb7", "Nb1", "Bxh1");
    checkException("Ra2", board, SanValidationProblem.EXISTS_RNBQ_NEITHER, "There is no rook on the board.");
    checkException("Nac3", SanValidationProblem.EXISTS_RNBQ_FILE, "There is no knight on file a.");
    checkException("Q3d4", SanValidationProblem.EXISTS_RNBQ_RANK, "There is no queen on rank 3.");
    checkException("Bh5xf7+", SanValidationProblem.EXISTS_RNBQ_SQUARE, "There is no bishop on square h5.");
  }

  @SuppressWarnings("static-method")
  @Test
  void testDestinationPawn() {
    // DESTINATION_PAWN_FORWARD_OWN_PIECE: after Nf3 Nf6, white's f-pawn tries to advance to f3 blocked by own knight
    {
      final Board board = new Board();
      board.movesStrict("Nf3", "Nf6");
      checkException("f3", board, SanValidationProblem.DESTINATION_PAWN_FORWARD_OWN_PIECE,
          "The pawn cannot move forward to square f3 because it is occupied by an own piece.");
    }

    // DESTINATION_PAWN_FORWARD_OPPONENT_PIECE_KING: white pawn e4, black king e5, white tries e5
    {
      final Board board = new Board("8/8/8/4k3/4P3/8/8/K7 w - - 0 1");
      checkException("e5", board, SanValidationProblem.DESTINATION_PAWN_FORWARD_OPPONENT_PIECE_KING,
          "The pawn cannot move forward to square e5 because it is occupied by the opponent king (the king cannot be captured, and pawns cannot capture by moving forward).");
    }

    // DESTINATION_PAWN_FORWARD_OPPONENT_PIECE_NOT_KING: after d4 d5, white's d-pawn tries d5 blocked by black pawn
    {
      final Board board = new Board();
      board.movesStrict("d4", "d5");
      checkException("d5", board, SanValidationProblem.DESTINATION_PAWN_FORWARD_OPPONENT_PIECE_NOT_KING,
          "The pawn cannot move forward to square d5 because it is occupied by an opponent piece; pawns cannot capture by moving forward.");
    }

    // DESTINATION_PAWN_CAPTURE_OWN_PIECE: after Nc3 a6, white's b-pawn tries bxc3 onto own knight
    {
      final Board board = new Board();
      board.movesStrict("Nc3", "a6");
      checkException("bxc3", board, SanValidationProblem.DESTINATION_PAWN_CAPTURE_OWN_PIECE,
          "The pawn cannot capture on square c3 because it is occupied by an own piece.");
    }

    // DESTINATION_PAWN_CAPTURE_KING: legal position - white pawn e2 doesn't attack d5; SAN "exd5" would target
    // d5 where the black king sits. Movement layer passes (file change adjacent, rank progression OK), exists check
    // passes (pawn on e-file), destination check fires because d5 has opponent king.
    {
      final Board board = new Board("7K/8/8/3k4/8/8/4P3/8 w - - 0 1");
      checkException("exd5", board, SanValidationProblem.DESTINATION_PAWN_CAPTURE_KING,
          "The pawn cannot capture the opponent king on d5; the king cannot be captured.");
    }

    // DESTINATION_PAWN_CAPTURE_EMPTY_NOT_EN_PASSANT: from initial position, dxe3 with e3 empty and no EP target
    checkException("dxe3", SanValidationProblem.DESTINATION_PAWN_CAPTURE_EMPTY_NOT_EN_PASSANT,
        "A pawn diagonal capture requires an opponent piece on the destination square (or a valid en passant capture target); the destination square e3 is empty and no en passant capture applies.");
  }

  @SuppressWarnings("static-method")
  @Test
  void testDestinationRnbqkOwnPiece() {
    checkException("Na1", SanValidationProblem.DESTINATION_RNBQK_OWN_PIECE_NON_CAPTURING,
        "The move to square a1 is not possible because it is occupied by an own piece.");

    checkException("Nxa1", SanValidationProblem.DESTINATION_RNBQK_OWN_PIECE_CAPTURING,
        "Capturing on square a1 is not possible because it is occupied by an own piece.");

  }

  @SuppressWarnings("static-method")
  @Test
  void testDestinationRnbqkKing() {
    checkException("Re8", SanValidationProblem.DESTINATION_RNBQK_OPPONENT_KING_NON_CAPTURING,
        "The move to square e8 is not possible because it is occupied by the opponent king.");

    checkException("Rxe8", SanValidationProblem.DESTINATION_RNBQK_OPPONENT_KING_CAPTURING,
        "The opponent king can never be captured.");

  }

  @SuppressWarnings("static-method")
  @Test
  void testCaptureSymbol() {

    {
      final Board board = new Board();
      board.movesStrict("Nc3", "e6", "Nb5", "e5");

      checkException("Na7", board, SanValidationProblem.DESTINATION_RNBQK_OPPONENT_NON_KING_NO_CAPTURE_SYMBOL,
          "The move captures an opponent piece on square a7 but has not capture symbol.");
    }

    checkException("Nxc3", SanValidationProblem.DESTINATION_RNBQK_EMPTY_CAPTURE_SYMBOL,
        "The move is designated as a capture by the capture symbol, but the destination square c3 is empty.");
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingCastling() {

    // KING_CASTLING_FINAL_NO_RIGHT_KING_MOVED: white king moved to e2 and back; O-O fails.
    {
      final Board board = new Board();
      board.movesStrict("e4", "e5", "Ke2", "d6", "Ke1", "d5");
      checkException("O-O", board, SanValidationProblem.KING_CASTLING_FINAL_NO_RIGHT_KING_MOVED,
          "King-side castling is not possible anymore because the king has moved.");
    }

    // KING_CASTLING_FINAL_NO_RIGHT_ROOK_MOVED: white king-side rook moved to h3 and back; O-O fails.
    {
      final Board board = new Board();
      board.movesStrict("h4", "e5", "Rh3", "d6", "Rh1", "d5");
      checkException("O-O", board, SanValidationProblem.KING_CASTLING_FINAL_NO_RIGHT_ROOK_MOVED,
          "King-side castling is not possible anymore because the king-side rook has moved.");
    }

    // KING_CASTLING_FINAL_NO_RIGHT_ROOK_CAPTURED: black bishop on b7 captures white's h1 rook via
    // Bxg2-Bxh1. White then attempts O-O - king-side rook has been captured.
    {
      final Board board = new Board();
      board.movesStrict("a3", "b6", "a4", "Bb7", "a5", "Bxg2", "a6", "Bxh1");
      checkException("O-O", board, SanValidationProblem.KING_CASTLING_FINAL_NO_RIGHT_ROOK_CAPTURED,
          "King-side castling is not possible anymore because the king-side rook has been captured.");
    }

    // KING_CASTLING_FINAL_NO_RIGHT_CASTLED: white castles king-side; after black's reply, white
    // tries O-O-O - all castling rights are lost via having already castled.
    {
      final Board board = new Board();
      board.movesStrict("e4", "e5", "Nf3", "Nc6", "Bc4", "Bc5", "O-O", "Nf6");
      checkException("O-O-O", board, SanValidationProblem.KING_CASTLING_FINAL_NO_RIGHT_CASTLED,
          "Queen-side castling is not possible anymore because the king has already castled.");
    }

    // KING_CASTLING_FINAL_NO_RIGHT_UNKNOWN_FEN_IMPORT: FEN says no rights at all; provenance
    // unknown because it was lost before the position was imported.
    {
      final Board board = new Board("r3k2r/pppppppp/8/8/8/8/PPPPPPPP/R3K2R w - - 0 1");
      checkException("O-O", board, SanValidationProblem.KING_CASTLING_FINAL_NO_RIGHT_UNKNOWN_FEN_IMPORT,
          "King-side castling is not possible anymore, the reason is unknown for at game import the castling right was already lost.");
    }

    // KING_CASTLING_TEMPORARY_SQUARES_NOT_EMPTY: from the initial position, Nf1/Ng1 still block kingside
    // castling.
    checkException("O-O", SanValidationProblem.KING_CASTLING_TEMPORARY_SQUARES_NOT_EMPTY,
        "King-side castling is not possible because the squares between the king and the rook are not empty.");

    // KING_CASTLING_TEMPORARY_KING_IN_CHECK: black queen on e5 attacks white king on e1 along the open
    // e-file; O-O while in check.
    {
      final Board board = new Board("r3k2r/pppp1ppp/8/4q3/8/8/PPPP1PPP/R3K2R w KQkq - 0 1");
      checkException("O-O", board, SanValidationProblem.KING_CASTLING_TEMPORARY_KING_IN_CHECK,
          "King-side castling is not possible because the king is in check.");
    }

    // KING_CASTLING_TEMPORARY_KING_TRAVELS_THROUGH_CHECK: Nf6-on-e6 attacks f1; king passes through f1.
    {
      final Board board = new Board("rnbqk2r/ppppppbp/4Nnp1/8/8/8/PPPPPPPP/R1BQKBNR b KQkq - 0 25");
      checkException("O-O", board, SanValidationProblem.KING_CASTLING_TEMPORARY_KING_TRAVELS_THROUGH_CHECK,
          "King-side castling is not possible because the king would travel through check.");
    }

    // KING_CASTLING_TEMPORARY_KING_ENDS_IN_CHECK: after castling king-side, king on g1 is attacked by the
    // black bishop on c5 through an open a7-g1 diagonal.
    {
      final Board board = new Board("rnbqk1nr/pppp1ppp/4p3/2b5/2B1P3/5P1N/PPPP2PP/RNBQK2R w KQkq - 0 25");
      checkException("O-O", board, SanValidationProblem.KING_CASTLING_TEMPORARY_KING_ENDS_IN_CHECK,
          "King-side castling is not possible because the king would end in check.");
    }

  }

  @SuppressWarnings("static-method")
  @Test
  void testNotReachable() {

    // NOT_REACHABLE_PAWN_NON_CAPTURING: from initial, e5 is out of reach for the e2 pawn
    // (which can only advance to e3 or e4).
    checkException("e5", SanValidationProblem.NOT_REACHABLE_PAWN_NON_CAPTURING, "No pawn can reach square e5.");

    // NOT_REACHABLE_PAWN_CAPTURING: black pawn on d5, white c-pawn still on c2 - cxd5 cannot reach
    // (c2 can only capture diagonally on b3 or d3).
    {
      final Board board = new Board("rnbqkbnr/ppp1pppp/8/3p4/8/8/PPPPPPPP/RNBQKBNR w - - 0 2");
      checkException("cxd5", board, SanValidationProblem.NOT_REACHABLE_PAWN_CAPTURING,
          "No pawn can capture on square d5.");
    }

    // NOT_REACHABLE_KING_NON_CASTLING: lone white king on e1 cannot reach c3 (not king-adjacent).
    // Black rook on a8 ensures the position is not in DEAD_POSITION_INSUFFICIENT_MATERIAL; it does not
    // attack any square involved in the test.
    {
      final Board board = new Board("r6k/8/8/8/8/8/8/4K3 w - - 0 1");
      checkException("Kc3", board, SanValidationProblem.NOT_REACHABLE_KING_NON_CASTLING,
          "The king cannot reach square c3.");
    }

    // NOT_REACHABLE_RNBQ_NEITHER_SINGLE: single white rook on a1 cannot reach d4 (rook doesn't
    // move diagonally).
    {
      final Board board = new Board("7k/8/8/8/8/8/1P6/R6K w - - 0 1");
      checkException("Rd4", board, SanValidationProblem.NOT_REACHABLE_RNBQ_NEITHER_SINGLE,
          "The rook cannot reach square d4.");
    }

    // NOT_REACHABLE_RNBQ_NEITHER_MULTIPLE: from the initial position, neither Ra1 nor Rh1 can
    // reach a3 (both blocked by own pawns).
    checkException("Ra3", SanValidationProblem.NOT_REACHABLE_RNBQ_NEITHER_MULTIPLE, "No rook can reach square a3.");

    // NOT_REACHABLE_RNBQ_FILE_SINGLE: from the initial position, the single rook on the a-file
    // (Ra1) cannot reach b3.
    checkException("Rab3", SanValidationProblem.NOT_REACHABLE_RNBQ_FILE_SINGLE,
        "The rook on a1 cannot reach square b3.");

    // NOT_REACHABLE_RNBQ_FILE_MULTIPLE: two rooks on the a-file (a7, a2), neither can reach b4.
    {
      final Board board = new Board("7k/R7/8/8/8/8/R7/6K1 w - - 0 1");
      checkException("Rab4", board, SanValidationProblem.NOT_REACHABLE_RNBQ_FILE_MULTIPLE,
          "No rook on the a-file can reach square b4.");
    }

    // NOT_REACHABLE_RNBQ_RANK_SINGLE: single white rook on rank 7 (a7) cannot reach b4.
    {
      final Board board = new Board("7k/R7/8/8/8/8/8/6K1 w - - 0 1");
      checkException("R7b4", board, SanValidationProblem.NOT_REACHABLE_RNBQ_RANK_SINGLE,
          "The rook on a7 cannot reach square b4.");
    }

    // NOT_REACHABLE_RNBQ_RANK_MULTIPLE: two rooks on rank 7 (a7, h7), neither can reach b4.
    {
      final Board board = new Board("7k/R5R1/8/8/8/8/8/6K1 w - - 0 1");
      checkException("R7b4", board, SanValidationProblem.NOT_REACHABLE_RNBQ_RANK_MULTIPLE,
          "No rook on rank 7 can reach square b4.");
    }

    // NOT_REACHABLE_RNBQ_SQUARE: explicit source square a7, target b4 - not a rook move.
    {
      final Board board = new Board("7k/R7/8/P7/8/8/8/6K1 w - - 0 1");
      checkException("Ra7a2", board, SanValidationProblem.NOT_REACHABLE_RNBQ_SQUARE,
          "The rook on a7 cannot reach square a2.");
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingLeftInCheck() {

    // KING_LEFT_IN_CHECK_PAWN: white king e1 in check by rh1; e3 pawn push does not block or
    // capture the checking rook.
    {
      final Board board = new Board("4k3/8/8/8/8/8/4P3/4K2r w - - 0 1");
      checkException("e3", board, SanValidationProblem.KING_LEFT_IN_CHECK_PAWN,
          "No pawn can move to e3 because the own king would remain in check.");
    }

    // KING_LEFT_IN_CHECK_RNBQ_NEITHER_SINGLE: single white rook (Rb2), king d1 in check by rh1;
    // Rb4 does not address the check.
    {
      final Board board = new Board("k7/8/8/8/8/8/1R6/3K3r w - - 0 1");
      checkException("Rb4", board, SanValidationProblem.KING_LEFT_IN_CHECK_RNBQ_NEITHER_SINGLE,
          "The rook on b2 cannot move to b4 because the own king would remain in check.");
    }

    // KING_LEFT_IN_CHECK_RNBQ_NEITHER_MULTIPLE: two rooks (a3, c3), both can reach b3, king d1 in
    // check and neither move blocks/captures.
    {
      final Board board = new Board("k7/8/8/1R6/8/2R5/8/3K3r w - - 0 1");
      checkException("Rb3", board, SanValidationProblem.KING_LEFT_IN_CHECK_RNBQ_NEITHER_MULTIPLE,
          "No rook can move to b3 because the own king would remain in check.");
    }

    // KING_LEFT_IN_CHECK_RNBQ_FILE_SINGLE: single rook on the b-file (b2), king d1 in check;
    // Rbb4 does not address the check.
    {
      final Board board = new Board("k7/8/8/8/8/8/1R6/3K3r w - - 0 1");
      checkException("Rbb4", board, SanValidationProblem.KING_LEFT_IN_CHECK_RNBQ_FILE_SINGLE,
          "The rook on b2 cannot move to b4 because the own king would remain in check.");
    }

    // KING_LEFT_IN_CHECK_RNBQ_FILE_MULTIPLE: two rooks on the a-file (a7, a2), king d1 in check;
    // neither Raa4 candidate addresses the check.
    {
      final Board board = new Board("1k6/R7/8/8/8/8/R7/3K3r w - - 0 1");
      checkException("Raa4", board, SanValidationProblem.KING_LEFT_IN_CHECK_RNBQ_FILE_MULTIPLE,
          "No rook on the a-file can move to a4 because the own king would remain in check.");
    }

    // KING_LEFT_IN_CHECK_RNBQ_RANK_SINGLE: single rook on rank 5 (a5), king d1 in check; R5b5
    // does not address the check.
    {
      final Board board = new Board("1k6/8/8/R7/8/8/8/3K3r w - - 0 1");
      checkException("R5b5", board, SanValidationProblem.KING_LEFT_IN_CHECK_RNBQ_RANK_SINGLE,
          "The rook on a5 cannot move to b5 because the own king would remain in check.");
    }

    // KING_LEFT_IN_CHECK_RNBQ_RANK_MULTIPLE: two rooks on rank 5 (a5, c5), king d1 in check;
    // neither R5b5 candidate addresses the check.
    {
      final Board board = new Board("1k6/8/8/R1R5/8/8/8/3K3r w - - 0 1");
      checkException("R5b5", board, SanValidationProblem.KING_LEFT_IN_CHECK_RNBQ_RANK_MULTIPLE,
          "No rook on rank 5 can move to b5 because the own king would remain in check.");
    }

    // KING_LEFT_IN_CHECK_RNBQ_SQUARE: explicit source square a5, king d1 in check; Ra5b5 does
    // not address the check.
    {
      final Board board = new Board("1k6/8/8/R7/8/8/8/3K3r w - - 0 1");
      checkException("Ra5b5", board, SanValidationProblem.KING_LEFT_IN_CHECK_RNBQ_SQUARE,
          "The rook on a5 cannot move to b5 because the own king would remain in check.");
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingExposedToCheck() {

    // KING_EXPOSED_TO_CHECK_PAWN: pawn d2 pinned diagonally by black bishop a5 to white king e1;
    // d3 leaves the diagonal and exposes the king.
    {
      final Board board = new Board("4k3/8/8/b7/8/8/3P4/4K3 w - - 0 1");
      checkException("d3", board, SanValidationProblem.KING_EXPOSED_TO_CHECK_PAWN,
          "No pawn can move to d3 because it would expose the own king to check.");
    }

    // KING_EXPOSED_TO_CHECK_RNBQ_NEITHER_SINGLE: single rook (e4) pinned on the e-file by re7 to
    // white king e1; Ra4 leaves the e-file and exposes the king.
    {
      final Board board = new Board("4k3/4r3/8/8/4R3/8/8/4K3 w - - 0 1");
      checkException("Ra4", board, SanValidationProblem.KING_EXPOSED_TO_CHECK_RNBQ_NEITHER_SINGLE,
          "The rook on e4 cannot move to a4 because it would expose the own king to check.");
    }

    // KING_EXPOSED_TO_CHECK_RNBQ_NEITHER_MULTIPLE: rook c4 pinned on c-file by rc8 to Kc1, rook
    // f1 pinned on rank 1 by rh1 to Kc1; both can reach f4 and each move exposes the king.
    {
      final Board board = new Board("2r4k/8/8/8/2R5/8/8/2K2R1r w - - 0 1");
      checkException("Rf4", board, SanValidationProblem.KING_EXPOSED_TO_CHECK_RNBQ_NEITHER_MULTIPLE,
          "No rook can move to f4 because it would expose the own king to check.");
    }

    // KING_EXPOSED_TO_CHECK_RNBQ_FILE_SINGLE: single rook on the e-file (e4), pinned by re8;
    // Rea4 leaves the e-file and exposes the king.
    {
      final Board board = new Board("4k3/4r3/8/8/4R3/8/8/4K3 w - - 0 1");
      checkException("Rea4", board, SanValidationProblem.KING_EXPOSED_TO_CHECK_RNBQ_FILE_SINGLE,
          "The rook on e4 cannot move to a4 because it would expose the own king to check.");
    }

    // KING_EXPOSED_TO_CHECK_RNBQ_FILE_MULTIPLE: two rooks on the b-file (b5, b3), each pinned on
    // a different diagonal to white king c4 (by ba6 and ba2); neither Rbb4 candidate is legal.
    {
      final Board board = new Board("7k/8/b7/1R6/2K5/1R6/b7/8 w - - 0 1");
      checkException("Rbb4", board, SanValidationProblem.KING_EXPOSED_TO_CHECK_RNBQ_FILE_MULTIPLE,
          "No rook on the b-file can move to b4 because it would expose the own king to check.");
    }

    // KING_EXPOSED_TO_CHECK_RNBQ_RANK_SINGLE: single rook on rank 4 (e4) pinned by re8; R4a4
    // leaves the e-file and exposes the king.
    {
      final Board board = new Board("4k3/4r3/8/8/4R3/8/8/4K3 w - - 0 1");
      checkException("R4a4", board, SanValidationProblem.KING_EXPOSED_TO_CHECK_RNBQ_RANK_SINGLE,
          "The rook on e4 cannot move to a4 because it would expose the own king to check.");
    }

    // KING_EXPOSED_TO_CHECK_RNBQ_RANK_MULTIPLE: two rooks on rank 4 (b4, f4), each pinned on a
    // different diagonal to white king d2 (by ba5 and bg5); neither R4d4 candidate is legal.
    {
      final Board board = new Board("7k/8/8/b5b1/1R3R2/8/3K4/8 w - - 0 1");
      checkException("R4d4", board, SanValidationProblem.KING_EXPOSED_TO_CHECK_RNBQ_RANK_MULTIPLE,
          "No rook on rank 4 can move to d4 because it would expose the own king to check.");
    }

    // KING_EXPOSED_TO_CHECK_RNBQ_SQUARE: explicit source square e4, pinned by re8; Re4a4 leaves
    // the e-file and exposes the king.
    {
      final Board board = new Board("4k3/4r3/8/8/4R3/8/8/4K3 w - - 0 1");
      checkException("Re4a4", board, SanValidationProblem.KING_EXPOSED_TO_CHECK_RNBQ_SQUARE,
          "The rook on e4 cannot move to a4 because it would expose the own king to check.");
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingOnlySafetyReasons() {

    // KING_CAPTURES_GUARDED_PIECE: white king e1, black pawn d2 guarded by black bishop a5.
    // Kxd2 captures a guarded piece.
    {
      final Board board = new Board("4k3/8/8/b7/8/8/3p4/4K3 w - - 0 1");
      checkException("Kxd2", board, SanValidationProblem.KING_CAPTURES_GUARDED_PIECE,
          "The king cannot capture the piece on d2 because it is guarded.");
    }

    // KING_MOVES_NEXT_TO_OPPONENT_KING: white king e1, black king e3. Ke2 lands adjacent to the
    // black king. Black rook on a8 ensures the position is not in mutual insufficient material;
    // the rook attacks neither king.
    {
      final Board board = new Board("r7/8/8/8/8/4k3/8/4K3 w - - 0 1");
      checkException("Ke2", board, SanValidationProblem.KING_MOVES_NEXT_TO_OPPONENT_KING,
          "The king cannot move to e2 because it would be next to the opponent king.");
    }

    // KING_MOVES_TO_ATTACKED_EMPTY_SQUARE: king e1 not in check; Kf2 walks onto the f-file
    // attacked by rf8.
    {
      final Board board = new Board("5r2/8/8/k7/8/8/8/4K3 w - - 0 1");
      checkException("Kf2", board, SanValidationProblem.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE,
          "The king cannot move to f2 because the square is attacked.");
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testOverspecified() {

    // OVERSPECIFIED_RNBQ_FILE_ONLY_ONE_LEGAL_MOVE: single rook Ra1; Raa2 specifies file
    // redundantly since there's only one rook that can legally move to a2.
    {
      final Board board = new Board("7k/8/8/8/8/8/8/R3K3 w - - 0 1");
      checkException("Raa2", board, SanValidationProblem.OVERSPECIFIED_RNBQ_FILE_ONLY_ONE_LEGAL_MOVE,
          "The origin file specification is not necessary.");
    }

    // OVERSPECIFIED_RNBQ_RANK_ONLY_ONE_LEGAL_MOVE: same position, R1a2 specifies rank redundantly.
    {
      final Board board = new Board("7k/8/8/8/8/8/8/R3K3 w - - 0 1");
      checkException("R1a2", board, SanValidationProblem.OVERSPECIFIED_RNBQ_RANK_ONLY_ONE_LEGAL_MOVE,
          "The origin rank specification is not necessary.");
    }

    // OVERSPECIFIED_RNBQ_SQUARE_ONLY_ONE_LEGAL_MOVE: same position, Ra1a2 specifies full source
    // square redundantly.
    {
      final Board board = new Board("7k/8/8/8/8/8/8/R3K3 w - - 0 1");
      checkException("Ra1a2", board, SanValidationProblem.OVERSPECIFIED_RNBQ_SQUARE_ONLY_ONE_LEGAL_MOVE,
          "The origin square specification is not necessary.");
    }

    // OVERSPECIFIED_RNBQ_SQUARE_FILE_NOT_NECESSARY: two rooks on a-file (a7, a2), both can reach
    // a5; Ra7a5 uses square disambiguation where rank alone would have disambiguated (file alone
    // does not, but once the file is implied by the destination, rank remains the only needed
    // piece of disambiguating information - hence square is over-specified).
    {
      final Board board = new Board("7k/R7/8/8/8/8/R7/6K1 w - - 0 1");
      checkException("Ra7a5", board, SanValidationProblem.OVERSPECIFIED_RNBQ_SQUARE_FILE_NOT_NECESSARY,
          "Only the origin file specification is necessary.");
    }

    // OVERSPECIFIED_RNBQ_SQUARE_RANK_NOT_NECESSARY: two rooks on rank 3 (a3, h3), both can reach
    // d3; Ra3d3 uses square disambiguation where file alone would have disambiguated.
    {
      final Board board = new Board("7k/8/8/8/8/R5R1/8/6K1 w - - 0 1");
      checkException("Ra3d3", board, SanValidationProblem.OVERSPECIFIED_RNBQ_SQUARE_RANK_NOT_NECESSARY,
          "Only the origin rank specification is necessary.");
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testInsufficientlySpecified() {

    // INSUFFICIENTLY_SPECIFIED_RNBQ_NEITHER_MULTIPLE_LEGAL_MOVES: two rooks on rank 3 (a3, h3),
    // both can reach d3; Rd3 (no disambiguation) is ambiguous.
    {
      final Board board = new Board("7k/8/8/8/8/R3R3/8/6K1 w - - 0 1");
      checkException("Rd3", board,
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_NEITHER_EITHER_FILE_OR_RANK_OR_SQUARE_REQUIRED,
          "There is more than one rook which can move to square d3. Please further specify the piece to be moved by file or rank.");
    }

    // INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_MUST_USE_RANK: two rooks on the a-file (a7, a2), both
    // can reach a5; Raa5 (file disambig) is ambiguous - rank or square must be used.
    {
      final Board board = new Board("7k/R7/8/8/8/8/R7/6K1 w - - 0 1");
      checkException("Raa5", board, SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_RANK_REQUIRED,
          "There are multiple rook on the specified file a which can move to square a5."
              + " To move a piece from that file, please specify the rank instead.");
    }

    // INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_MUST_USE_RANK_OR_SQUARE: non-rook (queen) variant - two
    // queens on the b-file (b5, b2) can reach b4, and another queen (c4) on another file also
    // reaches b4; Qbb4 is ambiguous (and this non-rook branch fires).
    {
      final Board board = new Board("k7/8/8/1Q6/2Q5/8/1Q6/K7 w - - 0 1");
      checkException("Qbb4", board,
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_EITHER_RANK_OR_SQUARE_REQUIRED,
          "There are multiple queen on the specified file b which can move to square b4."
              + " Please specify the piece by rank or square.");
    }

    // INSUFFICIENTLY_SPECIFIED_RNBQ_RANK_MUST_USE_FILE: two rooks on rank 3 (a3, h3), both can
    // reach d3; R3d3 (rank disambig) is ambiguous - file or square must be used.
    {
      final Board board = new Board("7k/8/8/8/8/R3R3/8/6K1 w - - 0 1");
      checkException("R3d3", board, SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_RANK_FILE_REQUIRED,
          "There are multiple rook on the specified rank 3 which can move to square d3."
              + " To move a piece from that rank, please specify the file instead.");
    }

    // INSUFFICIENTLY_SPECIFIED_RNBQ_RANK_MUST_USE_FILE_OR_SQUARE: non-rook (queen) variant - two
    // queens on rank 4 (b4, g4) can reach d4, and another queen (d7) on another rank also
    // reaches d4; Q4d4 is ambiguous (non-rook branch).
    {
      final Board board = new Board("8/3Q4/7k/8/1Q4Q1/8/8/K7 w - - 0 1");
      checkException("Q4d4", board,
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_RANK_EITHER_FILE_OR_SQUARE_REQUIRED,
          "There are multiple queen on the specified rank 4 which can move to square d4."
              + " Please specify the piece by file or square.");
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testNonStandardSpecified() {

    {
      final Board board = new Board("1k6/8/8/R7/8/7r/2R5/3K4 w - - 0 1");
      checkException("R5c5", board, SanValidationProblem.NON_STANDARD_SPECIFIED_RNBQ_RANK_INSTEAD_OF_FILE,
          "The specified rank determines the piece but because the file also determines the piece, by the SAN specification, the file must be used to specify the move.");
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testCheckCheckmateSymbol() {

    // CHECKMATE_SYMBOL_BUT_CHECK_ONLY: K+Q vs K - Qe7 gives check only (Kxe7 saves, queen undefended),
    // but the SAN uses the checkmate symbol '#'.
    {
      final Board board = new Board("4k3/Q7/8/8/8/8/8/4K3 w - - 0 1");
      checkException("Qe7#", board, SanValidationProblem.CHECKMATE_SYMBOL_BUT_CHECK_ONLY,
          "The move is check only, but the checkmate symbol (#) was specified.");
    }

    // CHECKMATE_SYMBOL_BUT_NO_CHECK: from the initial position, e4 is neither check nor checkmate,
    // but the SAN uses the checkmate symbol '#'.
    checkException("e4#", SanValidationProblem.CHECKMATE_SYMBOL_BUT_NO_CHECK,
        "The move is neither check nor checkmate, but the checkmate symbol (#) was specified.");

    // CHECK_SYMBOL_BUT_CHECKMATE: back-rank mate - Rb8 is checkmate (black king g8 trapped by own pawns
    // f7/g7/h7), but the SAN uses the check symbol '+'.
    {
      final Board board = new Board("6k1/5ppp/8/8/8/8/8/1R5K w - - 0 1");
      checkException("Rb8+", board, SanValidationProblem.CHECK_SYMBOL_BUT_CHECKMATE,
          "The move is checkmate, but the check symbol (+) was specified.");
    }

    // CHECK_SYMBOL_BUT_NO_CHECK: from the initial position, e4 is neither check nor checkmate,
    // but the SAN uses the check symbol '+'.
    checkException("e4+", SanValidationProblem.CHECK_SYMBOL_BUT_NO_CHECK,
        "The move is neither check nor checkmate, but the check symbol (+) was specified.");

    // NO_SYMBOL_BUT_CHECKMATE: back-rank mate - Rb8 is checkmate, but the SAN carries no symbol.
    {
      final Board board = new Board("6k1/5ppp/8/8/8/8/8/1R5K w - - 0 1");
      checkException("Rb8", board, SanValidationProblem.NO_SYMBOL_BUT_CHECKMATE,
          "The move is checkmate, but no symbol was specified.");
    }

    // NO_SYMBOL_BUT_CHECK: K+Q vs K - Qe7 gives check, but the SAN carries no symbol.
    {
      final Board board = new Board("4k3/Q7/8/8/8/8/8/4K3 w - - 0 1");
      checkException("Qe7", board, SanValidationProblem.NO_SYMBOL_BUT_CHECK,
          "The move is check, but no symbol was specified.");
    }

  }

  /** Checks a SAN against the initial position. */
  private static void checkException(String san, SanValidationProblem expectedProblem, String expectedMessage) {
    checkException(san, new Board(), expectedProblem, expectedMessage);
  }

  /** Checks a SAN against the given board, asserting both the problem code and the full exception message. */
  private static void checkException(String san, Board board, SanValidationProblem expectedProblem,
      String expectedMessage) {
    boolean isException;
    try {
      StrictSanParser.parseText(san, board);
      isException = false;
    } catch (final SanValidationException e) {
      isException = true;
      assertEquals(expectedProblem, e.getSanValidationProblem());
      checkedProblems.add(expectedProblem);
      if (IS_CHECK_MESSAGE) {
        assertEquals(expectedMessage, e.getMessage());
      }
    }
    assertTrue(isException);
  }

  @AfterAll
  static void testCoverage() {
    final Set<SanValidationProblem> missingProblems = EnumSet.allOf(SanValidationProblem.class);
    missingProblems.removeAll(FIXED_UNCHECKED_ENTRIES);
    missingProblems.removeAll(checkedProblems);
    final String missingProblemsMessage = missingProblems.stream().map(problem -> problem.name() + " is missing")
        .reduce((left, right) -> left + System.lineSeparator() + right).orElse("");
    assertTrue(missingProblems.isEmpty(), missingProblemsMessage);
  }

}
