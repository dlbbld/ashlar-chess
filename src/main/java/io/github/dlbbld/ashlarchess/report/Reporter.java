// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.constants.ChessConstants;
import io.github.dlbbld.ashlarchess.common.model.DynamicPosition;
import io.github.dlbbld.ashlarchess.common.utility.BasicUtility;
import io.github.dlbbld.ashlarchess.messages.Message;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParser;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.PgnUtility;

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
        MoveRecords.played(board), ChessConstants.THREEFOLD_REPETITION_RULE_THRESHOLD);
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
