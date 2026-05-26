package com.dlb.chess.report;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.constants.ChessConstants;
import com.dlb.chess.common.model.DynamicPosition;
import com.dlb.chess.common.model.HalfMove;
import com.dlb.chess.common.utility.BasicUtility;
import com.dlb.chess.common.utility.RepetitionUtility;
import com.dlb.chess.messages.Message;
import com.dlb.chess.pgn.LenientPgnParser;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.pgn.PgnUtility;

/**
 * Prints a human-readable, game-level summary of a {@link Board} or a parsed PGN to {@code stdout}: threefold-repetition
 * listings, missed claim-ahead opportunities, and no-progress (50/75-move-rule) sequences.
 *
 * <p>
 * The summary distinguishes on-board predicates ("threefold has occurred") from with-move predicates ("some legal move
 * would create a threefold position the side could claim before playing it"). The latter surfaces missed claim
 * opportunities other libraries don't.
 */
public final class Reporter {

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

  private static List<String> calculateReportLines(Board board) {
    final @NonNull List<String> output = new ArrayList<>();

    // repetition
    addFirstMainSection(output, "report.repetition.threefold.ahead.title");
    final List<List<HalfMove>> claimAheadListList = ThreefoldClaimAheadUtility.calculateClaimAheadListList(board);
    final Map<DynamicPosition, String> positionIdentifierMap = PositionIdentifierUtility
        .calculatePositionIdentifierMap(claimAheadListList);
    if (claimAheadListList.isEmpty()) {
      output.add(Message.getString("report.repetition.threefold.ahead.none"));
    } else {
      final var claimAheadListListPrint = ThreefoldClaimAheadPrint.calculateClaimAheadListListPrint(
          board.getInitialDynamicPosition(), board.getHalfMoveList(), claimAheadListList, positionIdentifierMap);
      for (final List<String> resultAsLine : claimAheadListListPrint) {
        final String line = BasicUtility.calculateSpaceSeparatedList(resultAsLine);
        output.add(line);
      }
    }

    final List<List<HalfMove>> repetitionListList = RepetitionUtility
        .calculateRepetitionListList(board.getHalfMoveList(), ChessConstants.THREEFOLD_REPETITION_RULE_THRESHOLD);
    addMainSection(output, "report.repetition.threefold.list.title");
    if (repetitionListList.isEmpty()) {
      output.add(Message.getString("report.repetition.threefold.list.none"));
    } else {
      final var repetionListList = RepetitionPrint.calculateRepetitionPrint(board.getInitialDynamicPosition(),
          repetitionListList, positionIdentifierMap);

      for (final List<String> resultAsLine : repetionListList) {
        final String line = BasicUtility.calculateSpaceSeparatedList(resultAsLine);
        output.add(line);
      }
    }

    final List<List<NoProgressHalfMove>> noProgressMoveListList = NoProgressMoveUtility
        .calculateNoProgressMoveRule(board, ChessConstants.FIFTY_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD);

    addMainSection(output, "report.noProgressMove.fiftyMoves.title");
    if (noProgressMoveListList.isEmpty()) {
      output.add(Message.getString("report.noProgressMove.fiftyMoves.yes"));
    } else {
      output.add(Message.getString("report.noProgressMove.fiftyMoves.no"));
    }

    return output;
  }

  private static void addFirstMainSection(List<String> output, String key) {
    final StringBuilder mainSection = new StringBuilder();
    mainSection.append(Message.getString(key));
    mainSection.append(":");
    output.add(Nulls.toString(mainSection));
  }

  private static void addMainSection(List<String> output, String key) {
    output.add("");
    addFirstMainSection(output, key);
  }

  private static void printList(List<String> list) {
    for (final String line : list) {
      System.out.println(line);
    }
  }
}
