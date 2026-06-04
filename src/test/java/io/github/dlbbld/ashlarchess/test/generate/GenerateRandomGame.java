// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.generate;

import java.util.ArrayList;
import java.util.List;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.constants.ChessConstants;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.common.model.DynamicPosition;
import io.github.dlbbld.ashlarchess.common.model.HalfMove;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.test.common.utility.RandomUtility;
import io.github.dlbbld.ashlarchess.test.librarycomparison.enums.FindRandomGame;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityQuickVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnableQuickAnalyzer;

public class GenerateRandomGame {

  private static final boolean IS_RUN_ALL = false;

  public static void main(final String[] args) {
    generateFivefold();

    if (IS_RUN_ALL) {
      generateNoRepetition();
      generateRandomGame(FindRandomGame.STALEMATE);
      generateSeventyFive();
      generateThreefold();
      generateFivefold();
    }
  }

  private static void generateRandomGame(FindRandomGame findRandomGame) {
    final Board board = new Board();

    List<MoveSpecification> legalMoves = board.getPossibleMoveSpecificationList();
    int numberOfMoveOptions = legalMoves.size();
    List<MoveSpecification> moveOptionList = new ArrayList<>(legalMoves);

    int halfMoveNumberLastPossibleTermination = -1;
    int numberOfHalfMovesPerformed = 0;
    while (numberOfMoveOptions != 0) {
      if (numberOfMoveOptions != 0) {
        final int randomMoveNumberIndex = RandomUtility.calculateRandomNumber(0, numberOfMoveOptions - 1);
        final MoveSpecification moveSpecification = Nulls.get(moveOptionList, randomMoveNumberIndex);
        board.move(moveSpecification);
        numberOfHalfMovesPerformed++;
        if (numberOfHalfMovesPerformed == 1 || numberOfHalfMovesPerformed % 100 == 0) {
          System.out.println("Number of half-moves performed: " + numberOfHalfMovesPerformed);
        }
        legalMoves = board.getPossibleMoveSpecificationList();
      }

      moveOptionList = new ArrayList<>();
      for (final MoveSpecification moveSpecification : legalMoves) {
        board.move(moveSpecification);
        switch (findRandomGame) {
          case CHECKMATE:
            if (board.isCheckmate()) {
              halfMoveNumberLastPossibleTermination = numberOfHalfMovesPerformed + 1;
              System.out.println("Found checkmate option for half-move " + halfMoveNumberLastPossibleTermination);
            }
            break;
          case FIFTY_MOVE_RULE:
            if (board.isFiftyMove()) {
              halfMoveNumberLastPossibleTermination = numberOfHalfMovesPerformed + 1;
              System.out.println("Found fifty-move rule option for half-move " + halfMoveNumberLastPossibleTermination);
            }
            break;
          case INSUFFICIENT_MATERIAL:
            if (board.isInsufficientMaterial()) {
              halfMoveNumberLastPossibleTermination = numberOfHalfMovesPerformed + 1;
              System.out
                  .println("Found insufficient material option for half-move " + halfMoveNumberLastPossibleTermination);
            }
            break;
          case STALEMATE:
            if (board.isStalemate()) {
              halfMoveNumberLastPossibleTermination = numberOfHalfMovesPerformed + 1;
              System.out.println("Found stalemate option for half-move " + halfMoveNumberLastPossibleTermination);
            }
            break;
          case THREEFOLD_REPETITION_RULE:
            // not implemented yet
            throw new IllegalArgumentException();
          default:
            throw new IllegalArgumentException();

        }
        if (!board.isCheckmate() && !board.isStalemate()
            && UnwinnableQuickAnalyzer.unwinnableQuick(board) != UnwinnabilityQuickVerdict.UNWINNABLE && board.getRepetitionCount() == 1
            && !board.isFiftyMove()) {
          moveOptionList.add(moveSpecification);
        }
        board.unmove();
      }
      numberOfMoveOptions = moveOptionList.size();
    }

    if (halfMoveNumberLastPossibleTermination != -1) {
      System.out.println("No more non terminating moves - we have a termination option");
      for (int i = numberOfHalfMovesPerformed; i >= halfMoveNumberLastPossibleTermination; i--) {
        board.unmove();
      }
      // now we should have at least one checkmate move
      // perform first found
      legalMoves = board.getPossibleMoveSpecificationList();
      for (final MoveSpecification moveSpecification : legalMoves) {
        board.move(moveSpecification);

        boolean isTerminationMoveFound = false;
        switch (findRandomGame) {
          case CHECKMATE:
            if (board.isCheckmate()) {
              isTerminationMoveFound = true;
            }
            break;
          case FIFTY_MOVE_RULE:
            if (board.isFiftyMove()) {
              isTerminationMoveFound = true;
            }
            break;
          case INSUFFICIENT_MATERIAL:
            if (board.isInsufficientMaterial()) {
              isTerminationMoveFound = true;
            }
            break;
          case STALEMATE:
            if (board.isStalemate()) {
              isTerminationMoveFound = true;
            }
            break;
          case THREEFOLD_REPETITION_RULE:
            // not implemented yet
            throw new IllegalArgumentException();
          default:
            throw new IllegalArgumentException();

        }
        if (isTerminationMoveFound) {
          System.out.println("A game with " + board.getPerformedHalfMoveCount() / 2.0 + " moves ending in "
              + findRandomGame + " was generated");
          final String moveList = calculateMoveList(board.getHalfMoveList());
          System.out.println(moveList);
          break;
        }
        board.unmove();
      }
    } else {
      System.out.println("No more non terminating moves - no termination option");
    }
  }

  private static void generateNoRepetition() {
    final Board board = new Board();

    List<MoveSpecification> legalMoves = board.getPossibleMoveSpecificationList();
    int numberOfMoveOptions = legalMoves.size();
    List<MoveSpecification> moveOptionList = new ArrayList<>(legalMoves);

    int numberOfHalfMovesPerformed = 0;
    while (numberOfMoveOptions != 0) {
      if (numberOfMoveOptions != 0) {
        final int randomMoveNumberIndex = RandomUtility.calculateRandomNumber(0, numberOfMoveOptions - 1);
        final MoveSpecification moveSpecification = Nulls.get(moveOptionList, randomMoveNumberIndex);
        board.move(moveSpecification);
        numberOfHalfMovesPerformed++;
        if (numberOfHalfMovesPerformed == 1 || numberOfHalfMovesPerformed % 100 == 0) {
          System.out.println("Number of half-moves performed: " + numberOfHalfMovesPerformed);
        }
        legalMoves = board.getPossibleMoveSpecificationList();
      }

      moveOptionList = new ArrayList<>();
      for (final MoveSpecification moveSpecification : legalMoves) {
        board.move(moveSpecification);
        if (!board.isCheckmate() && !board.isStalemate()
            && UnwinnableQuickAnalyzer.unwinnableQuick(board) != UnwinnabilityQuickVerdict.UNWINNABLE && board.getRepetitionCount() == 1) {
          moveOptionList.add(moveSpecification);
        }
        board.unmove();
      }
      numberOfMoveOptions = moveOptionList.size();
    }

    System.out.println("A game with " + board.getPerformedHalfMoveCount() / 2.0 + " moves was generated");
    final String moveList = calculateMoveList(board.getHalfMoveList());
    System.out.println(moveList);
  }

  // we only want one sequence over 50, so after passing 50 first time, we try to reach 75 and if not successful that's
  // it, then try again
  private static void generateSeventyFive() {
    final Board board = new Board();

    List<MoveSpecification> legalMoves = board.getPossibleMoveSpecificationList();
    int numberOfMoveOptions = legalMoves.size();
    List<MoveSpecification> moveOptionList = new ArrayList<>(legalMoves);

    boolean isFiftyReached = false;
    int numberOfHalfMovesPerformed = 0;
    while (numberOfMoveOptions != 0) {
      if (numberOfMoveOptions != 0) {
        final int randomMoveNumberIndex = RandomUtility.calculateRandomNumber(0, numberOfMoveOptions - 1);
        final MoveSpecification moveSpecification = Nulls.get(moveOptionList, randomMoveNumberIndex);
        board.move(moveSpecification);
        if (!isFiftyReached && board.isFiftyMove()) {
          isFiftyReached = true;
          System.out.println("Reached fifty move");
        }
        numberOfHalfMovesPerformed++;
        if (numberOfHalfMovesPerformed == 1 || numberOfHalfMovesPerformed % 100 == 0) {
          System.out.println("Number of half-moves performed: " + numberOfHalfMovesPerformed);
        }

        if (board.isSeventyFiveMove()) {
          System.out.println("A game with " + board.getPerformedHalfMoveCount() / 2.0
              + " moves ending with seventy-five-move rule was generated");
          final String moveList = calculateMoveList(board.getHalfMoveList());
          System.out.println(moveList);
          return;
        }
        legalMoves = board.getPossibleMoveSpecificationList();
      }

      moveOptionList = new ArrayList<>();
      for (final MoveSpecification moveSpecification : legalMoves) {
        board.move(moveSpecification);
        if (!board.isCheckmate() && !board.isStalemate()
            && UnwinnableQuickAnalyzer.unwinnableQuick(board) == UnwinnabilityQuickVerdict.UNWINNABLE && board.getRepetitionCount() == 1
            && (!isFiftyReached || board.isFiftyMove())) {
          moveOptionList.add(moveSpecification);
        }
        board.unmove();
      }
      numberOfMoveOptions = moveOptionList.size();
    }

    System.out.println("Could not generate game ending with seventy-five-move rule");

  }

  private static void generateThreefold() {
    generateRepetition(ChessConstants.THREEFOLD_REPETITION_RULE_THRESHOLD, 100);
  }

  private static void generateFivefold() {
    generateRepetition(ChessConstants.FIVEFOLD_REPETITION_RULE_THRESHOLD, 100);
  }

  private static void generateRepetition(int repetitionNumber, int numberOfTries) {
    for (int i = 1; i <= numberOfTries; i++) {
      System.out.println("Try " + i + " of " + numberOfTries);
      if (generateRepetition(repetitionNumber)) {
        System.out.println("Find result");
        break;
      }
    }
  }

  private static boolean generateRepetition(int repetitionNumber) {
    final Board board = new Board();

    List<MoveSpecification> legalMoves = board.getPossibleMoveSpecificationList();
    int numberOfMoveOptions = legalMoves.size();
    List<MoveSpecification> moveOptionList = new ArrayList<>(legalMoves);

    boolean isRepetitionReached = false;
    // we need to intialize to something other than null, the below value is not meaning ful
    DynamicPosition repetitionPosition = board.getDynamicPosition();
    int numberOfHalfMovesPerformed = 0;
    while (numberOfMoveOptions != 0) {
      {
        final int randomMoveNumberIndex = RandomUtility.calculateRandomNumber(0, numberOfMoveOptions - 1);
        final MoveSpecification moveSpecification = Nulls.get(moveOptionList, randomMoveNumberIndex);
        board.move(moveSpecification);
        if (!isRepetitionReached && board.getRepetitionCount() == 2) {
          isRepetitionReached = true;
          repetitionPosition = board.getDynamicPosition();
          System.out.println("Reached first repetition");
        }
        numberOfHalfMovesPerformed++;
        if (numberOfHalfMovesPerformed == 1 || numberOfHalfMovesPerformed % 100 == 0) {
          System.out.println("Number of half-moves performed: " + numberOfHalfMovesPerformed);
        }

        if (board.getRepetitionCount() == repetitionNumber) {
          System.out.println("A game with " + board.getPerformedHalfMoveCount() / 2.0 + " moves ending with "
              + repetitionNumber + " repetitions was generated");
          final String moveList = calculateMoveList(board.getHalfMoveList());
          System.out.println(moveList);
          return true;
        }
        legalMoves = board.getPossibleMoveSpecificationList();
      }

      moveOptionList = new ArrayList<>();

      // try to speed up
      // if we find continuation we use it, otherwise we never hit more than three using random moves
      for (final MoveSpecification moveSpecification : legalMoves) {
        board.move(moveSpecification);
        if (repetitionPosition.equals(board.getDynamicPosition())) {
          moveOptionList.add(moveSpecification);
        }
        board.unmove();
      }
      if (moveOptionList.isEmpty()) {
        // means we have no continuation from 3 onwards found
        for (final MoveSpecification moveSpecification : legalMoves) {
          board.move(moveSpecification);
          if (!board.isCheckmate() && !board.isStalemate()
              && UnwinnableQuickAnalyzer.unwinnableQuick(board) == UnwinnabilityQuickVerdict.UNWINNABLE && !board.isFiftyMove()) {
            moveOptionList.add(moveSpecification);
          }
          board.unmove();
        }
      }
      numberOfMoveOptions = moveOptionList.size();
    }

    System.out.println("Could not generate game ending with " + repetitionNumber + " repetitions");
    return false;
  }

  private static String calculateMoveList(List<HalfMove> halfMoveList) {
    final StringBuilder moveList = new StringBuilder();
    for (int i = 0; i < halfMoveList.size(); i++) {
      final HalfMove halfMove = Nulls.get(halfMoveList, i);
      // after black move if following white move
      if (i > 0 && i % 2 == 0) {
        moveList.append(" ");
      }
      if (halfMove.havingMove().getIsWhite()) {
        moveList.append(halfMove.fullMoveNumber()).append(". ");
      } else if (halfMove.havingMove().getIsBlack()) {
        moveList.append(" ");
      } else {
        throw new ProgrammingMistakeException("That should never happen");
      }
      moveList.append(halfMove.san());
    }

    return Nulls.toString(moveList);

  }

}
