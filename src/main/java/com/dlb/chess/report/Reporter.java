package com.dlb.chess.report;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.constants.ChessConstants;
import com.dlb.chess.common.enums.InsufficientMaterial;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.common.model.ClaimAhead;
import com.dlb.chess.common.model.HalfMove;
import com.dlb.chess.common.utility.BoardUtility;
import com.dlb.chess.common.utility.RepetitionUtility;
import com.dlb.chess.messages.Message;
import com.dlb.chess.pgn.LenientPgnParser;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.pgn.PgnUtility;

/**
 * Generates game-level reports â€” threefold-repetition listings (including missed-claim-ahead opportunities),
 * no-progress (50/75-move-rule) sequences, and a printable summary â€” from a {@link Board} or a parsed PGN.
 *
 * <p>
 * Two surfaces:
 *
 * <ul>
 * <li>{@code calculateReport(...)} returns a {@link com.dlb.chess.report.Report} record carrying all the analytical
 * data â€” repetition lists, threefold-claim-ahead slots, no-progress sequences. Use this for programmatic
 * inspection.</li>
 * <li>{@code printReport(...)} emits a human-readable summary to {@code stdout} via
 * {@link com.dlb.chess.messages.Message}. Use this for the kind of CLI-style output shown in the README examples.</li>
 * </ul>
 *
 * <p>
 * The report distinguishes the on-board predicates ("threefold has occurred") from the with-move predicates ("some
 * legal move would create a threefold position the side could claim before playing it"). The latter surfaces missed
 * claim opportunities other libraries don't.
 *
 * <p>
 * Final class with a private constructor â€” all entry points are static.
 */
public final class Reporter {

  private static final int REPETITION_COUNT_THRESHOLD = ChessConstants.THREEFOLD_REPETITION_RULE_THRESHOLD;
  private static final int NO_PROGRESS_FULL_MOVE_COUNT_THRESHOLD = 25;

  private Reporter() {
  }

  public static void printReport(String pgnString) {
    final PgnGame pgnGame = LenientPgnParser.parseText(pgnString);
    final Board board = PgnUtility.calculateBoard(pgnGame);
    printReport(board);
  }

  public static void printReport(Path folderPath, String pgnName) {
    final Board board = PgnUtility.calculateBoard(folderPath, pgnName);
    printReport(board);
  }

  public static void printReport(Board board) {
    printList(calculateReportLines(board));
  }

  /**
   * Returns the same human-readable report as {@link #printReport(String)} but as a single string, lines joined by
   * {@code "\n"}. Use this when the consumer is not stdout â€” web responses, file writes, GUI displays, etc.
   */
  public static String calculateReportText(String pgnString) {
    final PgnGame pgnGame = LenientPgnParser.parseText(pgnString);
    final Board board = PgnUtility.calculateBoard(pgnGame);
    return calculateReportText(board);
  }

  /**
   * Returns the same human-readable report as {@link #printReport(Path, String)} but as a single string, lines joined
   * by {@code "\n"}.
   */
  public static String calculateReportText(Path folderPath, String pgnName) {
    final Board board = PgnUtility.calculateBoard(folderPath, pgnName);
    return calculateReportText(board);
  }

  /**
   * Returns the same human-readable report as {@link #printReport(Board)} but as a single string, lines joined by
   * {@code "\n"}.
   */
  public static String calculateReportText(Board board) {
    return Nulls.join("\n", calculateReportLines(board));
  }

  private static List<String> calculateReportLines(Board board) {
    final @NonNull List<String> output = new ArrayList<>();

    // repetition
    addFirstMainSection(output, "report.repetition.threefold.ahead.title");
    final List<List<ClaimAhead>> claimAheadListList = ThreefoldClaimAheadUtility.calculateThreefoldClaimAhead(board);
    if (claimAheadListList.isEmpty()) {
      output.add(Message.getString("report.repetition.threefold.ahead.none"));
    } else {
      final var claimAheadList = ThreefoldClaimAheadPrint.calculateClaimAheadList(claimAheadListList);
      output.addAll(claimAheadList);
    }

    final List<List<HalfMove>> repetitionListList = RepetitionUtility
        .calculateRepetitionListList(board.getHalfMoveList(), REPETITION_COUNT_THRESHOLD);
    addMainSection(output, "report.repetition.threefold.list.title");
    if (repetitionListList.isEmpty()) {
      output.add(Message.getString("report.repetition.threefold.list.none"));
    } else {
      final var listChronic = RepetitionPrint.calculateOutputRepetitionChronlogically(repetitionListList);
      output.add(listChronic);
    }

    // no progress move
    final List<List<NoProgressHalfMove>> noProgressMoveListList = NoProgressMoveUtility
        .calculateNoProgressMoveRule(board, 2 * NO_PROGRESS_FULL_MOVE_COUNT_THRESHOLD);
    addMainSection(output, "report.noProgressMove.sequence.title",
        Nulls.valueOf(NO_PROGRESS_FULL_MOVE_COUNT_THRESHOLD));
    if (noProgressMoveListList.isEmpty()) {
      output.add(Message.getString("report.noProgressMove.sequence.none"));
    } else {
      final var list = NoProgressPrint.calculateOutputNoProgressMoveListList(noProgressMoveListList);
      output.addAll(list);
    }

    addMainSection(output, "report.noProgressMove.fiftyMoves.title");
    final var hasFiftyMoveRule = calculateHasFiftyMoveRule(noProgressMoveListList);
    if (hasFiftyMoveRule) {
      output.add(Message.getString("report.noProgressMove.fiftyMoves.yes"));
    } else {
      output.add(Message.getString("report.noProgressMove.fiftyMoves.no"));
    }

    return output;
  }

  public static Report calculateReport(Path folderPath, String pgnName) throws Exception {

    final Board board = PgnUtility.calculateBoard(folderPath, pgnName);
    return calculateReport(board);
  }

  public static Report calculateReport(Board board) {

    final String invariant = board.getFen();

    final Side havingMove = board.getHavingMove();

    final List<HalfMove> halfMoveList = board.getHalfMoveList();

    final List<List<HalfMove>> repetitionListList = RepetitionUtility.calculateRepetitionListList(halfMoveList,
        ChessConstants.THREEFOLD_REPETITION_RULE_THRESHOLD);

    final List<List<NoProgressHalfMove>> noProgressMoveListList = NoProgressMoveUtility
        .calculateNoProgressMoveRule(board, ChessConstants.FIFTY_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD);

    final var hasThreefoldRepetition = !repetitionListList.isEmpty();
    final var hasFivefoldRepetition = calculateHasFivefoldRepetition(repetitionListList);
    final var hasFiftyMoveRule = !noProgressMoveListList.isEmpty();
    final var hasSeventyFiveMoveRule = calculateHasSeventyFiveMoveRule(noProgressMoveListList);

    final var firstCapture = calculateFirstCapture(halfMoveList);
    final var hasCapture = calculateHasCapture(halfMoveList);

    final var maxNoProgressSequence = calculateMaxNoProgressSequence(board);

    final var checkmateOrStalemate = BoardUtility.calculateEvaluation(board);
    final InsufficientMaterial insufficientMaterial = board.calculateInsufficientMaterial();

    final String fen = board.getFen();

    if (!invariant.equals(board.getFen())) {
      throw new ProgrammingMistakeException("Board was changed");
    }

    return new Report(havingMove, halfMoveList, repetitionListList, noProgressMoveListList, hasThreefoldRepetition,
        hasFivefoldRepetition, hasFiftyMoveRule, hasSeventyFiveMoveRule, firstCapture, hasCapture,
        maxNoProgressSequence, checkmateOrStalemate, insufficientMaterial, fen, board);
  }

  public static boolean calculateIsHalfMoveTerminatesNoProgressSequence(HalfMove halfMove) {
    return halfMove.halfMoveClock() == 0;
  }

  // ---------------------------------------------------------------------------------------------
  // Private helpers â€” calculate*
  // ---------------------------------------------------------------------------------------------

  private static boolean calculateHasFivefoldRepetition(List<List<HalfMove>> repetitionListList) {
    for (final List<HalfMove> currentHalfMoveList : repetitionListList) {
      if (currentHalfMoveList.size() >= ChessConstants.FIVEFOLD_REPETITION_RULE_THRESHOLD) {
        return true;
      }
    }
    return false;
  }

  private static boolean calculateHasSeventyFiveMoveRule(List<List<NoProgressHalfMove>> noProgressMoveListList) {
    for (final List<NoProgressHalfMove> list : noProgressMoveListList) {
      final NoProgressHalfMove lastNoProgressHalfMove = Nulls.getLast(list);

      if (lastNoProgressHalfMove.sequenceLength() >= ChessConstants.SEVENTY_FIVE_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD) {
        return true;
      }
    }
    return false;
  }

  private static boolean calculateHasFiftyMoveRule(List<List<NoProgressHalfMove>> noProgressMoveListList) {
    for (final List<NoProgressHalfMove> noProgressMoveList : noProgressMoveListList) {
      for (final NoProgressHalfMove noProgressHalfMove : noProgressMoveList) {
        if (noProgressHalfMove.sequenceLength() >= ChessConstants.FIFTY_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD) {
          return true;
        }
      }
    }
    return false;
  }

  private static int calculateFirstCapture(List<HalfMove> halfMoveList) {
    for (final HalfMove halfMove : halfMoveList) {
      if (halfMove.isCapture()) {
        return halfMove.halfMoveCount();
      }
    }
    return -1;
  }

  private static boolean calculateHasCapture(List<HalfMove> halfMoveList) {
    for (final HalfMove halfMove : halfMoveList) {
      if (halfMove.isCapture()) {
        return true;
      }
    }
    return false;
  }

  private static int calculateMaxNoProgressSequence(Board board) {
    final List<HalfMove> halfMoveList = board.getHalfMoveList();

    if (board.getHalfMoveList().isEmpty()) {
      return board.getInitialFen().halfMoveClock();
    }

    // if the first move is capture or pawn move the initial half move clock is
    // reset
    // so we initialize the max with the initial half move clock to assure it is
    // considered in the calculation
    var maxHalfMoveClock = board.getInitialFen().halfMoveClock();
    final var maxIndex = halfMoveList.size() - 1;
    for (var i = 0; i <= maxIndex; i++) {
      final HalfMove halfMove = Nulls.get(halfMoveList, i);
      final var halfMoveClock = halfMove.halfMoveClock();
      if (halfMoveClock > maxHalfMoveClock) {
        maxHalfMoveClock = halfMoveClock;
      }
    }
    return maxHalfMoveClock;
  }

  // ---------------------------------------------------------------------------------------------
  // Private helpers â€” output formatting
  // ---------------------------------------------------------------------------------------------

  private static void addFirstMainSection(List<String> output, String key) {
    final StringBuilder mainSection = new StringBuilder();
    mainSection.append(Message.getString(key));
    mainSection.append(":");
    output.add(Nulls.toString(mainSection));
  }

  private static void addFirstMainSection(List<String> output, String key, String placeHolder) {
    final StringBuilder mainSection = new StringBuilder();
    mainSection.append(Message.getString(key, placeHolder));
    mainSection.append(":");
    output.add(Nulls.toString(mainSection));
  }

  private static void addMainSection(List<String> output, String key) {
    output.add("");
    addFirstMainSection(output, key);
  }

  private static void addMainSection(List<String> output, String key, String placeHolder) {
    output.add("");
    addFirstMainSection(output, key, placeHolder);
  }

  private static void printList(List<String> list) {
    for (final String line : list) {
      System.out.println(line);
    }
  }
}
