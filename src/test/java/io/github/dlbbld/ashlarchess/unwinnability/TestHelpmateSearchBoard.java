package io.github.dlbbld.ashlarchess.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.unwinnability.HelpmateSearchBoard;

class TestHelpmateSearchBoard {

  // Enumerated fixtures for the HelpmateSearchBoard differential-test (Phase A of the helpmate
  // hot-path release). Each fixture pins a specific move-generation / state-tracking category so
  // that a regression surfaces by fixture name, not by "whatever the corpus happened to pick."
  // The recursive walker below exercises every legal move at every node to the listed depth,
  // asserting Board <-> HelpmateSearchBoard parity on state, derived flags, and legal-move order.

  // Broad-tree fixtures.
  /** Initial position: opening-tree breadth at depth 2 covers most simple move kinds. */
  private static final SearchCase SCENARIO_INITIAL = new SearchCase("initial", null, 2);
  /** All four castling rights live; both sides can castle in the tree. */
  private static final SearchCase SCENARIO_ALL_FOUR_CASTLING_RIGHTS = new SearchCase("all-four-castling-rights",
      "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1", 1);
  /** Legal en passant: the normalized EP target survives - black dxe3 is legal. */
  private static final SearchCase SCENARIO_LEGAL_EN_PASSANT = new SearchCase("legal-en-passant",
      "8/8/8/8/3pP3/8/8/K6k b - e3 0 1", 2);
  /** Illegal EP normalization: rank-4 rook x-rays the king through the EP pair -> EP target normalizes to NONE. */
  private static final SearchCase SCENARIO_ILLEGAL_EN_PASSANT_NORMALIZATION = new SearchCase(
      "illegal-en-passant-normalization", "8/8/8/8/k2pP2R/8/8/7K b - e3 0 1", 1);
  /** Both sides promote in the tree (Q/R/B/N branches on either side). */
  private static final SearchCase SCENARIO_BOTH_SIDES_PROMOTE = new SearchCase("both-sides-promote",
      "7k/P7/8/8/8/8/7p/K7 w - - 0 1", 1);

  // Terminal-flag fixtures (depth 0 - root assertion only).
  /**
   * Checkmate terminal: back-rank Qg7# - pins {@code isCheckmate} at the root node.
   */
  private static final SearchCase SCENARIO_CHECKMATE_TERMINAL = new SearchCase("checkmate-terminal",
      "7k/6Q1/6K1/8/8/8/8/8 b - - 0 1", 0);
  /**
   * Stalemate terminal: Black king h8, White Qf7 controls all king-flight squares - pins {@code isStalemate}.
   */
  private static final SearchCase SCENARIO_STALEMATE_TERMINAL = new SearchCase("stalemate-terminal",
      "7k/5Q2/6K1/8/8/8/8/8 b - - 0 1", 0);

  // Check-evasion fixtures (Phase-A additions). Hand-constructed minimal positions - no exotic
  // motifs, just enough material to pin the legal-move filter under each check pattern.
  /**
   * Single check from a rook on a clear file: black king e8 checked by Re2; only king-evasion moves are legal (no
   * friendly piece can block or capture). Exercises the check-evasion filter under a slider check.
   */
  private static final SearchCase SCENARIO_CHECK_WITH_KING_ONLY_EVASIONS = new SearchCase(
      "check-with-king-only-evasions", "4k3/8/8/8/8/8/4R3/4K3 b - - 0 1", 3);
  /**
   * Double check: black king e8 attacked simultaneously by Nc7 (knight reach) and Re1 (e-file). Black has a rook on c3
   * whose pseudo-legal moves must all be filtered - the double-check rule mandates king-only response. Verifies that
   * the legal-move generator rejects every non-king move under double check.
   */
  private static final SearchCase SCENARIO_DOUBLE_CHECK_KING_ONLY = new SearchCase("double-check-king-only",
      "4k3/2N5/8/8/8/2r5/8/4R2K b - - 0 1", 2);
  /**
   * En passant capture as a check response: white king c4 in direct check from black pawn d5 (just played d7-d5). The
   * legal-move list must include exd6 e.p. (which removes the checker) alongside the king-evasion squares. Exercises
   * the EP-probe x check-evasion interaction - the EP candidate must survive both the "post-EP king safety" probe AND
   * the check-evasion filter.
   */
  private static final SearchCase SCENARIO_EN_PASSANT_RESOLVES_CHECK = new SearchCase("en-passant-resolves-check",
      "4k3/8/8/3pP3/2K5/8/8/8 w - d6 0 1", 1);

  private static final ImmutableList<SearchCase> SCENARIOS = Nulls.listOf(SCENARIO_INITIAL,
      SCENARIO_ALL_FOUR_CASTLING_RIGHTS, SCENARIO_LEGAL_EN_PASSANT, SCENARIO_ILLEGAL_EN_PASSANT_NORMALIZATION,
      SCENARIO_BOTH_SIDES_PROMOTE, SCENARIO_CHECKMATE_TERMINAL, SCENARIO_STALEMATE_TERMINAL,
      SCENARIO_CHECK_WITH_KING_ONLY_EVASIONS, SCENARIO_DOUBLE_CHECK_KING_ONLY, SCENARIO_EN_PASSANT_RESOLVES_CHECK);

  @SuppressWarnings("static-method")
  @Test
  void representativeTreesMatchBoardState() {
    for (final SearchCase scenario : SCENARIOS) {
      try {
        final Board board = boardFrom(scenario.fen());
        assertSearchTreeMatchesBoard(board, scenario.depth());
      } catch (final AssertionError | RuntimeException e) {
        throw new AssertionError(
            "scenario=" + scenario.label() + " fen=" + scenario.fen() + " depth=" + scenario.depth(), e);
      }
    }
  }

  private static Board boardFrom(@Nullable String fen) {
    if (fen == null) {
      return new Board();
    }
    return new Board(fen);
  }

  private static void assertSearchTreeMatchesBoard(Board board, int depth) {
    final HelpmateSearchBoard searchBoard = HelpmateSearchBoard.from(board);
    assertMatchesBoard(board, searchBoard);
    assertSearchTreeMatchesBoard(board, searchBoard, depth);
  }

  private static void assertSearchTreeMatchesBoard(Board board, HelpmateSearchBoard searchBoard, int depth) {
    if (depth == 0 || board.getLegalMoves().isEmpty()) {
      return;
    }
    final List<LegalMove> legalMoves = board.getLegalMoves();
    for (final LegalMove legalMove : legalMoves) {
      final MoveSpecification moveSpecification = legalMove.moveSpecification();
      board.move(moveSpecification);
      searchBoard.move(moveSpecification);
      assertMatchesBoard(board, searchBoard);

      assertSearchTreeMatchesBoard(board, searchBoard, depth - 1);

      searchBoard.unmove();
      board.unmove();
      assertMatchesBoard(board, searchBoard);
    }
  }

  private static void assertMatchesBoard(Board board, HelpmateSearchBoard searchBoard) {
    assertEquals(board.getDynamicPosition(), searchBoard.getDynamicPosition());
    assertEquals(board.getBitboardPosition(), searchBoard.getBitboardPosition());
    assertEquals(board.getHavingMove(), searchBoard.getHavingMove());
    assertEquals(board.getEnPassantCaptureTargetSquare(), searchBoard.getEnPassantCaptureTargetSquare());
    assertEquals(board.getCastlingRightWhite(), searchBoard.getCastlingRight(Side.WHITE));
    assertEquals(board.getCastlingRightBlack(), searchBoard.getCastlingRight(Side.BLACK));
    // Move-order policy: HelpmateSearchBoard's iteration order is an internal performance choice, so we assert
    // set equality with Board, not ordered-list equality. Board's order remains stable as public API. The size
    // assertion alongside catches the case where the generator emits a duplicate move (set equality alone would
    // silently collapse duplicates).
    final List<LegalMove> boardMoves = board.getLegalMoves();
    final List<LegalMove> searchMoves = searchBoard.getLegalMoves();
    assertEquals(boardMoves.size(), searchMoves.size(), "legal-move count");
    assertEquals(Set.copyOf(boardMoves), Set.copyOf(searchMoves), "legal-move set");
    assertEquals(board.isCheck(), searchBoard.isCheck());
    assertEquals(board.isCheckmate(), searchBoard.isCheckmate());
    assertEquals(board.isStalemate(), searchBoard.isStalemate());
    assertEquals(board.isInsufficientMaterial(Side.WHITE), searchBoard.isInsufficientMaterial(Side.WHITE));
    assertEquals(board.isInsufficientMaterial(Side.BLACK), searchBoard.isInsufficientMaterial(Side.BLACK));
  }

  /**
   * Fixture for a single search-tree walk.
   *
   * @param label human-readable scenario name (matches the constant suffix, kebab-case); appears in failure traces so a
   *              regression points at the fixture, not at a raw FEN.
   * @param fen   starting position; {@code null} means "use {@code new Board()} = chess initial position."
   * @param depth tree depth to walk from the root; {@code 0} asserts the root node only (no recursion).
   */
  private record SearchCase(String label, @Nullable String fen, int depth) {
  }
}
