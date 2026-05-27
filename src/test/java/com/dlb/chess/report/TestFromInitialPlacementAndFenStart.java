package com.dlb.chess.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.constants.ChessConstants;

/**
 * From-move-one coverage: positions that run into threefold, fivefold, 50-move, and 75-move rule conditions starting
 * from move 1, exercised in four "family" configurations:
 *
 * <ul>
 * <li>Initial piece placement, White to move on move 1 (the conventional standard start).</li>
 * <li>Initial piece placement, Black to move on move 1 (synthetic FEN where Black has the move at fullmove 1).</li>
 * <li>Non-initial FEN position, White to move on move 1.</li>
 * <li>Non-initial FEN position, Black to move on move 1.</li>
 * </ul>
 *
 * <p>
 * Each fixture runs three-layered assertions: (a) the public {@code Board.is*} predicate fires; (b) the corresponding
 * report object model (where one exists — the threefold-existing report covers both threefold and fivefold via its
 * {@code totalRepetitionCount}; the 50/75-move conditions do not yet have a dedicated report record, so the model
 * layer is satisfied via the predicate alone) reflects the condition; (c) the printed {@link Reporter} output contains
 * the section header for the relevant condition.
 *
 * <p>
 * The Black-to-move-on-move-1 fixtures exercise FIDE fullmove-numbering correctness: when Black has the move at fullmove
 * 1, the fullmove counter increments after each Black move (not each White move), so the report's fullmove references
 * for repeated positions are shifted relative to the more usual White-first case.
 */
class TestFromInitialPlacementAndFenStart {

  /**
   * Standard initial position with White to move. Default Board() constructor produces this.
   */
  private static final String FEN_INITIAL_BLACK_TO_MOVE =
      "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1";

  /**
   * Non-initial FEN — two rooks plus king vs lone king. Plenty of room for non-pawn, non-capture shuffling. Castling
   * rights are intentionally absent ({@code -}): the first rook move would otherwise erase the king-side right,
   * shifting the dynamic position between cycles 1 and 2 and forcing an extra cycle to reach threefold. With no
   * rights, the shuffle returns to the exact same position every cycle. White to move at fullmove 1.
   */
  private static final String FEN_KRR_K_WHITE_TO_MOVE = "4k3/8/8/8/8/8/8/R3K2R w - - 0 1";

  /**
   * Same piece placement and (absence of) rights as above but Black has the move at fullmove 1. Synthetic but valid;
   * the predicates and report model do not care that the position is reachable from the standard start.
   */
  private static final String FEN_KRR_K_BLACK_TO_MOVE = "4k3/8/8/8/8/8/8/R3K2R b - - 0 1";

  // ===========================================================================================
  // Family A — initial piece placement, White to move on move 1
  // ===========================================================================================

  @SuppressWarnings("static-method")
  @Test
  void threefoldFromInitialWhiteToMove() {
    final Board board = new Board();
    playKnightShuffleAsWhite(board, 2); // 8 plies → initial position 3rd occurrence
    assertThreefoldRepetition(board);
    assertReportSection(board, "Threefolds and beyond");
  }

  @SuppressWarnings("static-method")
  @Test
  void fivefoldFromInitialWhiteToMove() {
    final Board board = new Board();
    playKnightShuffleAsWhite(board, 4); // 16 plies → fivefold of initial position
    assertFivefoldRepetition(board);
    assertReportSection(board, "Threefolds and beyond");
  }

  @SuppressWarnings("static-method")
  @Test
  void fiftyMoveFromInitialWhiteToMove() {
    final Board board = new Board();
    playKnightShuffleAsWhite(board, 25); // 100 plies → halfmove clock 100
    assertFiftyMoveOrClaimable(board);
    assertReportSection(board, "Fifty moves without capture and pawn move");
  }

  @SuppressWarnings("static-method")
  @Test
  void seventyFiveMoveFromInitialWhiteToMove() {
    final Board board = new Board();
    playKnightShuffleAsWhite(board, 38); // 152 plies → halfmove clock 152 (past 150 threshold)
    assertSeventyFiveMove(board);
    assertReportSection(board, "Fifty moves without capture and pawn move");
  }

  // ===========================================================================================
  // Family B — initial piece placement, Black to move on move 1 (special fullmove numbering)
  // ===========================================================================================

  @SuppressWarnings("static-method")
  @Test
  void threefoldFromInitialBlackToMove() {
    final Board board = new Board(FEN_INITIAL_BLACK_TO_MOVE);
    playKnightShuffleAsBlack(board, 2); // 8 plies; Black moves first each cycle
    assertThreefoldRepetition(board);
    // Fullmove numbering: with Black starting at fullmove 1, after 8 plies (4 cycles of Nf6/Nf3/Ng8/Ng1) the fullmove
    // counter is at 5 (incremented after each Black move).
    assertEquals(5, board.getFullMoveNumber(),
        "Black-to-move-at-fullmove-1: counter increments per Black move; 4 Black moves → fullmove 5");
    assertReportSection(board, "Threefolds and beyond");
  }

  @SuppressWarnings("static-method")
  @Test
  void fivefoldFromInitialBlackToMove() {
    final Board board = new Board(FEN_INITIAL_BLACK_TO_MOVE);
    playKnightShuffleAsBlack(board, 4); // 16 plies
    assertFivefoldRepetition(board);
    assertEquals(9, board.getFullMoveNumber(),
        "Black-to-move-at-fullmove-1: 8 Black moves → fullmove 9");
    assertReportSection(board, "Threefolds and beyond");
  }

  @SuppressWarnings("static-method")
  @Test
  void fiftyMoveFromInitialBlackToMove() {
    final Board board = new Board(FEN_INITIAL_BLACK_TO_MOVE);
    playKnightShuffleAsBlack(board, 25); // 100 plies
    assertFiftyMoveOrClaimable(board);
    assertReportSection(board, "Fifty moves without capture and pawn move");
  }

  @SuppressWarnings("static-method")
  @Test
  void seventyFiveMoveFromInitialBlackToMove() {
    final Board board = new Board(FEN_INITIAL_BLACK_TO_MOVE);
    playKnightShuffleAsBlack(board, 38); // 152 plies
    assertSeventyFiveMove(board);
    assertReportSection(board, "Fifty moves without capture and pawn move");
  }

  // ===========================================================================================
  // Family C — non-initial FEN, White to move on move 1
  // ===========================================================================================

  @SuppressWarnings("static-method")
  @Test
  void threefoldFromFenWhiteToMove() {
    final Board board = new Board(FEN_KRR_K_WHITE_TO_MOVE);
    playRookShuffleAsWhite(board, 2);
    assertThreefoldRepetition(board);
    assertReportSection(board, "Threefolds and beyond");
  }

  @SuppressWarnings("static-method")
  @Test
  void fivefoldFromFenWhiteToMove() {
    final Board board = new Board(FEN_KRR_K_WHITE_TO_MOVE);
    playRookShuffleAsWhite(board, 4);
    assertFivefoldRepetition(board);
    assertReportSection(board, "Threefolds and beyond");
  }

  @SuppressWarnings("static-method")
  @Test
  void fiftyMoveFromFenWhiteToMove() {
    final Board board = new Board(FEN_KRR_K_WHITE_TO_MOVE);
    playRookShuffleAsWhite(board, 25);
    assertFiftyMoveOrClaimable(board);
    assertReportSection(board, "Fifty moves without capture and pawn move");
  }

  @SuppressWarnings("static-method")
  @Test
  void seventyFiveMoveFromFenWhiteToMove() {
    final Board board = new Board(FEN_KRR_K_WHITE_TO_MOVE);
    playRookShuffleAsWhite(board, 38);
    assertSeventyFiveMove(board);
    assertReportSection(board, "Fifty moves without capture and pawn move");
  }

  // ===========================================================================================
  // Family D — non-initial FEN, Black to move on move 1 (special fullmove numbering)
  // ===========================================================================================

  @SuppressWarnings("static-method")
  @Test
  void threefoldFromFenBlackToMove() {
    final Board board = new Board(FEN_KRR_K_BLACK_TO_MOVE);
    playRookShuffleAsBlack(board, 2);
    assertThreefoldRepetition(board);
    assertEquals(5, board.getFullMoveNumber(),
        "Black-to-move-at-fullmove-1: 4 Black moves → fullmove 5");
    assertReportSection(board, "Threefolds and beyond");
  }

  @SuppressWarnings("static-method")
  @Test
  void fivefoldFromFenBlackToMove() {
    final Board board = new Board(FEN_KRR_K_BLACK_TO_MOVE);
    playRookShuffleAsBlack(board, 4);
    assertFivefoldRepetition(board);
    assertEquals(9, board.getFullMoveNumber(),
        "Black-to-move-at-fullmove-1: 8 Black moves → fullmove 9");
    assertReportSection(board, "Threefolds and beyond");
  }

  @SuppressWarnings("static-method")
  @Test
  void fiftyMoveFromFenBlackToMove() {
    final Board board = new Board(FEN_KRR_K_BLACK_TO_MOVE);
    playRookShuffleAsBlack(board, 25);
    assertFiftyMoveOrClaimable(board);
    assertReportSection(board, "Fifty moves without capture and pawn move");
  }

  @SuppressWarnings("static-method")
  @Test
  void seventyFiveMoveFromFenBlackToMove() {
    final Board board = new Board(FEN_KRR_K_BLACK_TO_MOVE);
    playRookShuffleAsBlack(board, 38);
    assertSeventyFiveMove(board);
    assertReportSection(board, "Fifty moves without capture and pawn move");
  }

  // ===========================================================================================
  // Move-sequence helpers
  // ===========================================================================================

  /**
   * Plays {@code cycles} repetitions of the four-ply knight shuffle {@code Nf3 Nf6 Ng1 Ng8} from the initial position.
   * Each cycle returns to the initial position, so {@code cycles == 2} produces 8 plies and the initial position has
   * occurred 3 times; {@code cycles == 4} produces 16 plies and 5 occurrences; and so on.
   */
  private static void playKnightShuffleAsWhite(Board board, int cycles) {
    for (var i = 0; i < cycles; i++) {
      board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8");
    }
  }

  /**
   * Mirror for the Black-to-move-at-fullmove-1 case: each cycle is {@code Nf6 Nf3 Ng8 Ng1} (Black moves first).
   */
  private static void playKnightShuffleAsBlack(Board board, int cycles) {
    for (var i = 0; i < cycles; i++) {
      board.movesStrict("Nf6", "Nf3", "Ng8", "Ng1");
    }
  }

  /**
   * Plays {@code cycles} repetitions of the four-ply rook shuffle {@code Rg1 Kd8 Rh1 Ke8} starting from the
   * {@code FEN_KRR_K_WHITE_TO_MOVE} position. Castling rights are absent in the FEN, so the position returns to the
   * exact starting state after each cycle (no first-cycle rights erasure to shift the dynamic position).
   */
  private static void playRookShuffleAsWhite(Board board, int cycles) {
    for (var i = 0; i < cycles; i++) {
      board.movesStrict("Rg1", "Kd8", "Rh1", "Ke8");
    }
  }

  private static void playRookShuffleAsBlack(Board board, int cycles) {
    for (var i = 0; i < cycles; i++) {
      board.movesStrict("Kd8", "Rg1", "Ke8", "Rh1");
    }
  }

  // ===========================================================================================
  // Assertion helpers
  // ===========================================================================================

  /** Layers (a) and (b): predicate fires AND the threefold-existing report has a group at threefold count. */
  private static void assertThreefoldRepetition(Board board) {
    assertTrue(board.isThreefoldRepetition(), "Board.isThreefoldRepetition predicate must fire");

    final ThreefoldExistingReport report = ThreefoldExistingReportBuilder.build(board.getInitialDynamicPosition(),
        board.getHalfMoveList(), ChessConstants.THREEFOLD_REPETITION_RULE_THRESHOLD);
    assertTrue(report.groups().size() >= 1, "threefold report must surface at least one group");

    var anyAtThreefold = false;
    for (final RepetitionGroup group : report.groups()) {
      if (group.totalRepetitionCount() >= ChessConstants.THREEFOLD_REPETITION_RULE_THRESHOLD) {
        anyAtThreefold = true;
        break;
      }
    }
    assertTrue(anyAtThreefold, "at least one group must have reached the threefold threshold");
  }

  /** Layers (a) and (b) for the fivefold case. */
  private static void assertFivefoldRepetition(Board board) {
    assertTrue(board.isFivefoldRepetition(), "Board.isFivefoldRepetition predicate must fire");

    final ThreefoldExistingReport report = ThreefoldExistingReportBuilder.build(board.getInitialDynamicPosition(),
        board.getHalfMoveList(), ChessConstants.THREEFOLD_REPETITION_RULE_THRESHOLD);

    var anyAtFivefold = false;
    for (final RepetitionGroup group : report.groups()) {
      if (group.totalRepetitionCount() >= ChessConstants.FIVEFOLD_REPETITION_RULE_THRESHOLD) {
        anyAtFivefold = true;
        break;
      }
    }
    assertTrue(anyAtFivefold, "at least one group must have reached the fivefold threshold");
  }

  /** Layer (a) for 50-move: the claim is available (clock at threshold and at least one non-zeroing legal move). */
  private static void assertFiftyMoveOrClaimable(Board board) {
    assertTrue(board.getHalfMoveClock() >= ChessConstants.FIFTY_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD,
        "halfmove clock must have reached the 50-move threshold");
    assertTrue(board.canClaimFiftyMoveRule(),
        "canClaimFiftyMoveRule must be true at or past the 50-move threshold with legal moves available");
  }

  /** Layer (a) for 75-move: the threshold has been crossed. */
  private static void assertSeventyFiveMove(Board board) {
    assertTrue(board.getHalfMoveClock() >= ChessConstants.SEVENTY_FIVE_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD,
        "halfmove clock must have reached the 75-move threshold");
    assertTrue(board.isSeventyFiveMove(), "isSeventyFiveMove must be true at or past the 75-move threshold");
  }

  /** Layer (c): the printed report contains the named section header. Catches "the section quietly disappeared". */
  private static void assertReportSection(Board board, String sectionHeaderPrefix) {
    final String output = captureReporter(board);
    assertTrue(output.contains(sectionHeaderPrefix),
        () -> "printed report must contain section '" + sectionHeaderPrefix + "':\n" + output);
  }

  private static String captureReporter(Board board) {
    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    final PrintStream original = System.out;
    try (PrintStream captured = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
      System.setOut(captured);
      Reporter.printReport(board);
    } finally {
      System.setOut(original);
    }
    return buffer.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
  }
}
