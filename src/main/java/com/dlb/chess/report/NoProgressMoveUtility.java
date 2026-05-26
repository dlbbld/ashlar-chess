package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.List;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.constants.BasicConstants;
import com.dlb.chess.common.constants.ChessConstants;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.common.model.HalfMove;
import com.dlb.chess.common.utility.BasicChessUtility;

abstract class NoProgressMoveUtility {

  public static List<List<NoProgressHalfMove>> calculateNoProgressMoveRule(Board board,
      int numberOfHalfMovesThreshold) {

    final List<NoProgressIndex> indexList = NoProgressMoveUtility.calculateNoProgressMoveIndex(board,
        numberOfHalfMovesThreshold);

    return NoProgressMoveUtility.calculateNoProgressMoveRule(board.getInitialFen().havingMove(),
        board.getInitialFen().fullMoveNumber(), board.getHalfMoveList(), indexList, numberOfHalfMovesThreshold);
  }

  private static List<List<NoProgressHalfMove>> calculateNoProgressMoveRule(Side havingMoveInitial,
      int fullMoveNumberInitial, final List<HalfMove> halfMoveList, List<NoProgressIndex> indexList,
      int numberOfHalfMovesThreshold) {

    if (numberOfHalfMovesThreshold > ChessConstants.FIFTY_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD) {
      throw new IllegalArgumentException("The threshold cannot be below fifty moves");
    }

    final List<List<NoProgressHalfMove>> resultList = new ArrayList<>();

    for (final NoProgressIndex sequence : indexList) {

      int performedIndex;
      int sequenceLength;
      NoProgressHalfMove entry;

      final List<NoProgressHalfMove> result = new ArrayList<>();
      // we add the initial entry
      performedIndex = sequence.beginPerformedIndex();
      sequenceLength = 1;
      entry = NoProgressMoveUtility.calculateNoProgressHalfMove(havingMoveInitial, fullMoveNumberInitial, halfMoveList,
          performedIndex, sequenceLength);
      result.add(entry);

      final var halfMoveClockEnd = sequence.halfMoveClockEnd();
      if (halfMoveClockEnd < numberOfHalfMovesThreshold) {
        throw new ProgrammingMistakeException();
      }

      if (halfMoveClockEnd == numberOfHalfMovesThreshold) {
        // we add the last entry, which also ends the sequence
        performedIndex = sequence.endPerformedIndex();
        sequenceLength = numberOfHalfMovesThreshold;

        // we add the entry reaching the threshold if sequence below fifty moves
      } else if (numberOfHalfMovesThreshold < ChessConstants.FIFTY_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD
          && halfMoveClockEnd < ChessConstants.FIFTY_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD) {

        // we can add the move per index as last entry
        performedIndex = sequence.endPerformedIndex();
        sequenceLength = halfMoveClockEnd;
      } else {

        // we add the half move when the fifty-move rule becomes effective
        performedIndex = sequence.beginPerformedIndex() + ChessConstants.FIFTY_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD - 1;
        sequenceLength = ChessConstants.FIFTY_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD;
        entry = NoProgressMoveUtility.calculateNoProgressHalfMove(havingMoveInitial, fullMoveNumberInitial,
            halfMoveList, performedIndex, sequenceLength);
        result.add(entry);

        if (halfMoveClockEnd < ChessConstants.SEVENTY_FIVE_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD) {
          // we can add the move per index as last entry
          performedIndex = sequence.endPerformedIndex();
          sequenceLength = halfMoveClockEnd;

        } else if (halfMoveClockEnd == ChessConstants.SEVENTY_FIVE_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD) {
          // we add the last entry, which also ends the sequence
          performedIndex = sequence.endPerformedIndex();
          sequenceLength = ChessConstants.SEVENTY_FIVE_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD;
        } else {

          // we add the first half move when the seventy-five-move rule becomes effective
          // (the
          // 150th)
          performedIndex = sequence.beginPerformedIndex()
              + ChessConstants.SEVENTY_FIVE_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD - 1;
          sequenceLength = ChessConstants.SEVENTY_FIVE_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD;
          entry = NoProgressMoveUtility.calculateNoProgressHalfMove(havingMoveInitial, fullMoveNumberInitial,
              halfMoveList, performedIndex, sequenceLength);
          result.add(entry);

          // we add the last half move of the sequence
          // we add the last entry, which also ends the sequence
          performedIndex = sequence.endPerformedIndex();
          sequenceLength = halfMoveClockEnd;
        }
      }
      entry = NoProgressMoveUtility.calculateNoProgressHalfMove(havingMoveInitial, fullMoveNumberInitial, halfMoveList,
          performedIndex, sequenceLength);
      result.add(entry);
      resultList.add(result);
    }

    return resultList;
  }

  private static NoProgressHalfMove calculateNoProgressHalfMove(Side havingMoveInitial, int fullMoveNumberInitial,
      List<HalfMove> halfMoveList, int performedIndex, int sequenceLength) {

    if (performedIndex < 0) {
      return NoProgressMoveUtility.calculateNoProgressHalfMoveNotPerformed(havingMoveInitial, fullMoveNumberInitial,
          performedIndex, sequenceLength);
    }

    return NoProgressMoveUtility.calculateNoProgressHalfMovePerformed(havingMoveInitial, halfMoveList, performedIndex,
        sequenceLength);
  }

  private static NoProgressHalfMove calculateNoProgressHalfMoveNotPerformed(Side havingMoveInitial,
      int fullMoveNumberInitial, int performedIndex, int sequenceLength) {

    final var performedHalfMoveCount = performedIndex + 1;
    final var fullMoveNumber = BasicChessUtility.calculateFullMoveNumber(havingMoveInitial, fullMoveNumberInitial,
        performedHalfMoveCount);
    // not SAN
    final String san = BasicConstants.NA;
    final Side sideMoved = BasicChessUtility.calculateSideMoved(havingMoveInitial, performedHalfMoveCount);

    return new NoProgressHalfMove(performedHalfMoveCount, fullMoveNumber, san, sideMoved, sequenceLength);
  }

  private static NoProgressHalfMove calculateNoProgressHalfMovePerformed(Side havingMoveInitial,
      List<HalfMove> halfMoveList, int performedIndex, int sequenceLength) {

    final HalfMove firstHalfMove = Nulls.get(halfMoveList, performedIndex);

    final var performedHalfMoveCount = performedIndex + 1;
    final var fullMoveNumber = firstHalfMove.fullMoveNumber();
    final String san = firstHalfMove.san();
    final Side sideMoved = BasicChessUtility.calculateSideMoved(havingMoveInitial, performedHalfMoveCount);

    return new NoProgressHalfMove(performedHalfMoveCount, fullMoveNumber, san, sideMoved, sequenceLength);
  }

  private static List<NoProgressIndex> calculateNoProgressMoveIndex(Board board, int numberOfHalfMovesThreshold) {

    if (numberOfHalfMovesThreshold > ChessConstants.FIFTY_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD) {
      throw new IllegalArgumentException("The threshold cannot be below fifty moves");
    }

    final List<HalfMove> halfMoveList = board.getHalfMoveList();

    final List<NoProgressIndex> result = new ArrayList<>();

    // if the game starts from a FEN where the half-move clock meets the limit,
    // and begins with a pawn move or catpure, we add the initial sequence here
    if (calculateIsUseInitialFenHalfMoveClockOnly(halfMoveList)) {
      final var halfMoveClockEnd = board.getInitialFen().halfMoveClock();
      if (halfMoveClockEnd >= numberOfHalfMovesThreshold) {
        final var halfMoveCount = 0;

        final var beginHalfMoveNumber = halfMoveCount - halfMoveClockEnd + 1;
        final var endHalfMoveNumber = halfMoveCount;

        final var beginPerformedHalfMoveNumber = beginHalfMoveNumber - halfMoveCount;
        final var endPerformedHalfMoveNumber = endHalfMoveNumber - halfMoveCount;

        final var beginPerformedIndex = beginPerformedHalfMoveNumber - 1;
        final var endPerformedIndex = endPerformedHalfMoveNumber - 1;

        final NoProgressIndex sequence = new NoProgressIndex(beginPerformedIndex, endPerformedIndex, halfMoveClockEnd);

        result.add(sequence);
        // NO EARLY RETURN HERE
      }
    }

    final var maxIndex = halfMoveList.size() - 1;
    var endPerformedIndex = -1;
    for (var i = 0; i <= maxIndex; i++) {
      if (i <= endPerformedIndex) {
        // If we have found a sequence with 100 half moves until i = 213. Then the start
        // is at i = 213 - 100 + 1 = 114.
        // We will calculate when the sequence ends. When it for example ends after
        // total 137 half moves long. Then the
        // sequence ends at i = 114 + 137 - 1 = 250. So we arrived at i = 213 and we
        // resume looking for new sequences at
        // i = 251.
        continue;
      }
      final HalfMove halfMove = Nulls.get(halfMoveList, i);
      final var halfMoveClock = halfMove.halfMoveClock();
      // we are only interested in sequences of length starting from required length
      // for fifty-move rule
      if (halfMoveClock >= numberOfHalfMovesThreshold) {
        final var beginPerformedIndex = i - halfMoveClock + 1;

        // we check the first move of the sequence if in the PGN
        // the first move can not be in the PGN if starting from non initial position
        if (beginPerformedIndex >= 0) {
          final HalfMove beginHalfMove = Nulls.get(halfMoveList, beginPerformedIndex);
          final var beginSequenceLength = beginHalfMove.halfMoveClock();
          if (beginSequenceLength != 1) {
            throw new ProgrammingMistakeException();
          }
        }

        // it's easiest to calculate the end index here
        // it' not so nice for we do not follow the loop
        endPerformedIndex = calculateEndIndexSequence(halfMoveList, i);
        final HalfMove endHalfMove = Nulls.get(halfMoveList, endPerformedIndex);
        final var halfMoveClockEnd = endHalfMove.halfMoveClock();
        final NoProgressIndex sequence = new NoProgressIndex(beginPerformedIndex, endPerformedIndex, halfMoveClockEnd);

        result.add(sequence);
      }
    }
    return result;
  }

  private static int calculateEndIndexSequence(List<HalfMove> halfMoveList, int index) {
    final var maxIndex = halfMoveList.size() - 1;
    if (index == maxIndex) {
      return index;
    }
    for (var j = index + 1; j <= maxIndex; j++) {
      final HalfMove halfMove = Nulls.get(halfMoveList, j);
      if (calculateIsHalfMoveTerminatesNoProgressSequence(halfMove)) {
        return j - 1;
      }
    }
    return maxIndex;
  }

  private static boolean calculateIsUseInitialFenHalfMoveClockOnly(final List<HalfMove> halfMoveList) {
    if (halfMoveList.isEmpty()) {
      return true;
    }
    final HalfMove firstHalfMove = Nulls.get(halfMoveList, 0);
    // first half move is a pawn move or capture
    // we must calculate the indexes using the FEN only as the half move clock was
    // reset at the first half-move, so by
    // our logic no sequence will be found
    return firstHalfMove.halfMoveClock() == 0;
  }

  private static boolean calculateIsHalfMoveTerminatesNoProgressSequence(HalfMove halfMove) {
    return halfMove.halfMoveClock() == 0;
  }

}
