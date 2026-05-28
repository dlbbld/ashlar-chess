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
import com.dlb.chess.common.utility.BasicUtility;
import com.dlb.chess.messages.Message;
import com.dlb.chess.pgn.LenientPgnParser;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.pgn.PgnUtility;

/**
 * Prints a human-readable, game-level summary of a {@link Board} or a parsed PGN to {@code stdout}:
 * threefold-repetition listings, missed claim-ahead opportunities, and no-progress (50/75-move-rule) sequences.
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

    final ThreefoldClaimAheadReport claimAhead = ThreefoldClaimAheadReportBuilder.build(board);
    final ThreefoldExistingReport existing = ThreefoldExistingReportBuilder.build(board.getInitialDynamicPosition(),
        board.getHalfMoveList(), ChessConstants.THREEFOLD_REPETITION_RULE_THRESHOLD);
    final Map<DynamicPosition, String> positionIdentifierMap = PositionIdentifierUtility
        .calculatePositionIdentifierMap(claimAhead, existing);

    addFirstMainSection(output, "report.repetition.threefold.ahead.title");
    if (claimAhead.entries().isEmpty()) {
      output.add(Message.getString("report.repetition.threefold.ahead.none"));
    } else {
      appendLines(output, ThreefoldClaimAheadPrint.render(claimAhead, positionIdentifierMap));
    }

    addMainSection(output, "report.repetition.threefold.list.title");
    if (existing.groups().isEmpty()) {
      output.add(Message.getString("report.repetition.threefold.list.none"));
    } else {
      appendLines(output, RepetitionPrint.render(existing, positionIdentifierMap));
    }

    final FiftyMoveClaimAheadReport fiftyMoveClaimAhead = FiftyMoveClaimAheadReportBuilder.build(board);
    addMainSection(output, "report.fiftyMove.ahead.title");
    if (fiftyMoveClaimAhead.entries().isEmpty()) {
      output.add(Message.getString("report.fiftyMove.ahead.none"));
    } else {
      appendLines(output, FiftyMoveClaimAheadPrint.render(fiftyMoveClaimAhead));
    }

    final FiftyMoveSequenceReport fiftyMoveSequence = FiftyMoveSequenceReportBuilder.build(board);
    addMainSection(output, "report.fiftyMove.sequence.title");
    if (fiftyMoveSequence.sequences().isEmpty()) {
      output.add(Message.getString("report.fiftyMove.sequence.none"));
    } else {
      appendLines(output, FiftyMoveSequencePrint.render(fiftyMoveSequence));
    }

    return output;
  }

  private static void appendLines(List<String> output, List<List<String>> renderedLines) {
    for (final List<String> resultAsLine : renderedLines) {
      output.add(BasicUtility.calculateSpaceSeparatedList(resultAsLine));
    }
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
