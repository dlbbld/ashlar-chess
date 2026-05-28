package io.github.dlbbld.ashlarchess.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.constants.ChessConstants;
import io.github.dlbbld.ashlarchess.report.RepetitionGroup;
import io.github.dlbbld.ashlarchess.report.Reporter;
import io.github.dlbbld.ashlarchess.report.ThreefoldExistingReport;
import io.github.dlbbld.ashlarchess.report.ThreefoldExistingReportBuilder;
import io.github.dlbbld.ashlarchess.test.common.utility.OutputCaptureUtility;

/**
 * From-move-one coverage: positions that run into threefold, fivefold, 50-move, and 75-move rule conditions starting
 * from move 1, exercised in four "family" configurations.
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
 * report object model (where one exists - the threefold-existing report covers both threefold and fivefold via its
 * {@code totalRepetitionCount}; the 50/75-move conditions do not yet have a dedicated report record, so the model layer
 * is satisfied via the predicate alone) reflects the condition; (c) the printed {@link Reporter} output contains the
 * section header for the relevant condition.
 *
 * <p>
 * The Black-to-move-on-move-1 fixtures exercise FIDE fullmove-numbering correctness: when Black has the move at
 * fullmove 1, the fullmove counter increments after each Black move (not each White move), so the report's fullmove
 * references for repeated positions are shifted relative to the more usual White-first case.
 */
class TestFromInitialPlacementAndFenStart {

  /**
   * Standard initial position with White to move. Default Board() constructor produces this.
   */
  private static final String FEN_INITIAL_BLACK_TO_MOVE = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1";

  /**
   * Non-initial FEN - two rooks plus king vs lone king. Plenty of room for non-pawn, non-capture shuffling. Castling
   * rights are intentionally absent ({@code -}): the first rook move would otherwise erase the king-side right,
   * shifting the dynamic position between cycles 1 and 2 and forcing an extra cycle to reach threefold. With no rights,
   * the shuffle returns to the exact same position every cycle. White to move at fullmove 1.
   */
  private static final String FEN_KRR_K_WHITE_TO_MOVE = "4k3/8/8/8/8/8/8/R3K2R w - - 0 1";

  /**
   * Same piece placement and (absence of) rights as above but Black has the move at fullmove 1. Synthetic but valid;
   * the predicates and report model do not care that the position is reachable from the standard start.
   */
  private static final String FEN_KRR_K_BLACK_TO_MOVE = "4k3/8/8/8/8/8/8/R3K2R b - - 0 1";

  // ===========================================================================================
  // Family A - initial piece placement, White to move on move 1
  // ===========================================================================================

  @SuppressWarnings("static-method")
  @Test
  void threefoldFromInitialWhiteToMove() {
    final Board board = new Board();
    playKnightShuffleAsWhite(board, 2); // 8 plies -> initial position 3rd occurrence
    assertThreefoldRepetition(board);
    assertReporterOutput(board, /* threefoldSectionNonEmpty */ true, /* expectedFiftyMoveSequenceReached */ false);
  }

  @SuppressWarnings("static-method")
  @Test
  void fivefoldFromInitialWhiteToMove() {
    final Board board = new Board();
    playKnightShuffleAsWhite(board, 4); // 16 plies -> fivefold of initial position
    assertFivefoldRepetition(board);
    assertReporterOutput(board, /* threefoldSectionNonEmpty */ true, /* expectedFiftyMoveSequenceReached */ false);
  }

  @SuppressWarnings("static-method")
  @Test
  void fiftyMoveFromInitialWhiteToMove() {
    final Board board = new Board();
    playKnightShuffleAsWhite(board, 25); // 100 plies -> halfmove clock 100
    assertFiftyMoveOrClaimable(board);
    assertReporterOutput(board, /* threefoldSectionNonEmpty */ true, /* expectedFiftyMoveSequenceReached */ true);
  }

  @SuppressWarnings("static-method")
  @Test
  void seventyFiveMoveFromInitialWhiteToMove() {
    final Board board = new Board();
    playKnightShuffleAsWhite(board, 38); // 152 plies -> halfmove clock 152 (past 150 threshold)
    assertSeventyFiveMove(board);
    assertReporterOutput(board, /* threefoldSectionNonEmpty */ true, /* expectedFiftyMoveSequenceReached */ true);
  }

  // ===========================================================================================
  // Family B - initial piece placement, Black to move on move 1 (special fullmove numbering)
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
        "Black-to-move-at-fullmove-1: counter increments per Black move; 4 Black moves -> fullmove 5");
    assertReporterOutput(board, /* threefoldSectionNonEmpty */ true, /* expectedFiftyMoveSequenceReached */ false);
  }

  @SuppressWarnings("static-method")
  @Test
  void fivefoldFromInitialBlackToMove() {
    final Board board = new Board(FEN_INITIAL_BLACK_TO_MOVE);
    playKnightShuffleAsBlack(board, 4); // 16 plies
    assertFivefoldRepetition(board);
    assertEquals(9, board.getFullMoveNumber(), "Black-to-move-at-fullmove-1: 8 Black moves -> fullmove 9");
    assertReporterOutput(board, /* threefoldSectionNonEmpty */ true, /* expectedFiftyMoveSequenceReached */ false);
  }

  @SuppressWarnings("static-method")
  @Test
  void fiftyMoveFromInitialBlackToMove() {
    final Board board = new Board(FEN_INITIAL_BLACK_TO_MOVE);
    playKnightShuffleAsBlack(board, 25); // 100 plies
    assertFiftyMoveOrClaimable(board);
    assertReporterOutput(board, /* threefoldSectionNonEmpty */ true, /* expectedFiftyMoveSequenceReached */ true);
  }

  @SuppressWarnings("static-method")
  @Test
  void seventyFiveMoveFromInitialBlackToMove() {
    final Board board = new Board(FEN_INITIAL_BLACK_TO_MOVE);
    playKnightShuffleAsBlack(board, 38); // 152 plies
    assertSeventyFiveMove(board);
    assertReporterOutput(board, /* threefoldSectionNonEmpty */ true, /* expectedFiftyMoveSequenceReached */ true);
  }

  // ===========================================================================================
  // Family C - non-initial FEN, White to move on move 1
  // ===========================================================================================

  @SuppressWarnings("static-method")
  @Test
  void threefoldFromFenWhiteToMove() {
    final Board board = new Board(FEN_KRR_K_WHITE_TO_MOVE);
    playRookShuffleAsWhite(board, 2);
    assertThreefoldRepetition(board);
    assertReporterOutput(board, /* threefoldSectionNonEmpty */ true, /* expectedFiftyMoveSequenceReached */ false);
  }

  @SuppressWarnings("static-method")
  @Test
  void fivefoldFromFenWhiteToMove() {
    final Board board = new Board(FEN_KRR_K_WHITE_TO_MOVE);
    playRookShuffleAsWhite(board, 4);
    assertFivefoldRepetition(board);
    assertReporterOutput(board, /* threefoldSectionNonEmpty */ true, /* expectedFiftyMoveSequenceReached */ false);
  }

  @SuppressWarnings("static-method")
  @Test
  void fiftyMoveFromFenWhiteToMove() {
    final Board board = new Board(FEN_KRR_K_WHITE_TO_MOVE);
    playRookShuffleAsWhite(board, 25);
    assertFiftyMoveOrClaimable(board);
    assertReporterOutput(board, /* threefoldSectionNonEmpty */ true, /* expectedFiftyMoveSequenceReached */ true);
  }

  @SuppressWarnings("static-method")
  @Test
  void seventyFiveMoveFromFenWhiteToMove() {
    final Board board = new Board(FEN_KRR_K_WHITE_TO_MOVE);
    playRookShuffleAsWhite(board, 38);
    assertSeventyFiveMove(board);
    assertReporterOutput(board, /* threefoldSectionNonEmpty */ true, /* expectedFiftyMoveSequenceReached */ true);
  }

  // ===========================================================================================
  // Family D - non-initial FEN, Black to move on move 1 (special fullmove numbering)
  // ===========================================================================================

  @SuppressWarnings("static-method")
  @Test
  void threefoldFromFenBlackToMove() {
    final Board board = new Board(FEN_KRR_K_BLACK_TO_MOVE);
    playRookShuffleAsBlack(board, 2);
    assertThreefoldRepetition(board);
    assertEquals(5, board.getFullMoveNumber(), "Black-to-move-at-fullmove-1: 4 Black moves -> fullmove 5");
    assertReporterOutput(board, /* threefoldSectionNonEmpty */ true, /* expectedFiftyMoveSequenceReached */ false);
  }

  @SuppressWarnings("static-method")
  @Test
  void fivefoldFromFenBlackToMove() {
    final Board board = new Board(FEN_KRR_K_BLACK_TO_MOVE);
    playRookShuffleAsBlack(board, 4);
    assertFivefoldRepetition(board);
    assertEquals(9, board.getFullMoveNumber(), "Black-to-move-at-fullmove-1: 8 Black moves -> fullmove 9");
    assertReporterOutput(board, /* threefoldSectionNonEmpty */ true, /* expectedFiftyMoveSequenceReached */ false);
  }

  @SuppressWarnings("static-method")
  @Test
  void fiftyMoveFromFenBlackToMove() {
    final Board board = new Board(FEN_KRR_K_BLACK_TO_MOVE);
    playRookShuffleAsBlack(board, 25);
    assertFiftyMoveOrClaimable(board);
    assertReporterOutput(board, /* threefoldSectionNonEmpty */ true, /* expectedFiftyMoveSequenceReached */ true);
  }

  @SuppressWarnings("static-method")
  @Test
  void seventyFiveMoveFromFenBlackToMove() {
    final Board board = new Board(FEN_KRR_K_BLACK_TO_MOVE);
    playRookShuffleAsBlack(board, 38);
    assertSeventyFiveMove(board);
    assertReporterOutput(board, /* threefoldSectionNonEmpty */ true, /* expectedFiftyMoveSequenceReached */ true);
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
    for (int i = 0; i < cycles; i++) {
      board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8");
    }
  }

  /**
   * Mirror for the Black-to-move-at-fullmove-1 case: each cycle is {@code Nf6 Nf3 Ng8 Ng1} (Black moves first).
   */
  private static void playKnightShuffleAsBlack(Board board, int cycles) {
    for (int i = 0; i < cycles; i++) {
      board.movesStrict("Nf6", "Nf3", "Ng8", "Ng1");
    }
  }

  /**
   * Plays {@code cycles} repetitions of the four-ply rook shuffle {@code Rg1 Kd8 Rh1 Ke8} starting from the
   * {@code FEN_KRR_K_WHITE_TO_MOVE} position. Castling rights are absent in the FEN, so the position returns to the
   * exact starting state after each cycle (no first-cycle rights erasure to shift the dynamic position).
   */
  private static void playRookShuffleAsWhite(Board board, int cycles) {
    for (int i = 0; i < cycles; i++) {
      board.movesStrict("Rg1", "Kd8", "Rh1", "Ke8");
    }
  }

  private static void playRookShuffleAsBlack(Board board, int cycles) {
    for (int i = 0; i < cycles; i++) {
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

    boolean anyAtThreefold = false;
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

    boolean anyAtFivefold = false;
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

  /**
   * Layer (c) - content-aware check of the printed report. Reporter always emits the section headers regardless of
   * whether any condition fired, so a header-presence test is too weak: a regression that quietly rendered "None" for
   * the threefold section or for the fifty-move sequence section would still pass. This helper asserts the actual
   * content of the threefold-existing section and the fifty-move sequence section (the two sections that fire as a
   * direct consequence of the shuffle test fixtures).
   *
   * @param threefoldSectionNonEmpty         {@code true} if the threefold section is expected to contain repetition
   *                                         group lines (not the "None" sentinel). All four families in this test set
   *                                         produce threefolds in their shuffle, so every test passes {@code true} here
   *                                         today.
   * @param expectedFiftyMoveSequenceReached {@code true} if a 50-move (or 75-move) non-progress stretch is expected and
   *                                         the "Fifty moves and beyond" section should contain at least one sequence
   *                                         line; {@code false} if the section should render the "None" sentinel (no
   *                                         50-move stretch was reached in play). Threefold / fivefold tests pass
   *                                         {@code false} (8/16 plies are well below the 50-move threshold); 50-move /
   *                                         75-move tests pass {@code true}.
   */
  private static void assertReporterOutput(Board board, boolean threefoldSectionNonEmpty,
      boolean expectedFiftyMoveSequenceReached) {
    final List<String> lines = captureReporter(board);

    final List<String> threefoldSection = extractSection(lines, "Threefolds and beyond",
        "Valid fifty-move claims ahead");
    if (threefoldSectionNonEmpty) {
      assertTrue(!threefoldSection.isEmpty(), "threefold section must have content");
      assertTrue(threefoldSection.size() != 1 || !"None".equals(threefoldSection.get(0)),
          () -> "threefold section must contain repetition group lines, not the 'None' sentinel; got:\n  "
              + String.join("\n  ", threefoldSection));
    }

    final List<String> fiftyMoveSequenceSection = extractSectionToEnd(lines, "Fifty moves and beyond");
    if (expectedFiftyMoveSequenceReached) {
      assertTrue(!fiftyMoveSequenceSection.isEmpty(), "fifty-move sequence section must have content");
      assertTrue(fiftyMoveSequenceSection.size() != 1 || !"None".equals(fiftyMoveSequenceSection.get(0)),
          () -> "fifty-move sequence section must contain sequence lines, not the 'None' sentinel; got:\n  "
              + String.join("\n  ", fiftyMoveSequenceSection));
    } else {
      assertEquals(1, fiftyMoveSequenceSection.size(),
          () -> "fifty-move sequence section must render exactly one 'None' line when no stretch was reached; got:\n  "
              + String.join("\n  ", fiftyMoveSequenceSection));
      assertEquals("None", fiftyMoveSequenceSection.get(0),
          "fifty-move sequence section must show the 'None' sentinel when no 50-move stretch was reached");
    }
  }

  private static List<String> captureReporter(Board board) {
    return OutputCaptureUtility.captureStdoutLines(() -> Reporter.printReport(board));
  }

  /**
   * Returns the non-blank content lines of the section starting at the line whose trimmed content begins with
   * {@code sectionHeaderPrefix} (exclusive of the header itself) and ending immediately before the next section (a line
   * whose trimmed content begins with {@code nextSectionHeaderPrefix}).
   */
  private static List<String> extractSection(List<String> lines, String sectionHeaderPrefix,
      String nextSectionHeaderPrefix) {
    boolean inSection = false;
    final List<String> contents = new ArrayList<>();
    for (final String raw : lines) {
      final String line = raw.trim();
      if (!inSection && line.startsWith(sectionHeaderPrefix)) {
        inSection = true;
        continue;
      }
      if (inSection) {
        if (line.startsWith(nextSectionHeaderPrefix)) {
          break;
        }
        if (line.isEmpty()) {
          continue;
        }
        contents.add(line);
      }
    }
    assertTrue(inSection, "section header '" + sectionHeaderPrefix + "' must appear in the captured output");
    return contents;
  }

  /**
   * Like {@link #extractSection} but for the final section - runs to the end of input rather than to a successor
   * header.
   */
  private static List<String> extractSectionToEnd(List<String> lines, String sectionHeaderPrefix) {
    boolean inSection = false;
    final List<String> contents = new ArrayList<>();
    for (final String raw : lines) {
      final String line = raw.trim();
      if (!inSection && line.startsWith(sectionHeaderPrefix)) {
        inSection = true;
        continue;
      }
      if (inSection) {
        if (line.isEmpty()) {
          continue;
        }
        contents.add(line);
      }
    }
    assertTrue(inSection, "section header '" + sectionHeaderPrefix + "' must appear in the captured output");
    return contents;
  }
}
