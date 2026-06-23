// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.generate;

import java.util.ArrayList;
import java.util.List;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.internal.ChessConstants;
import io.github.dlbbld.ashlarchess.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.board.DynamicPosition;
import io.github.dlbbld.ashlarchess.board.MoveSpecification;
import io.github.dlbbld.ashlarchess.test.common.utility.RandomUtility;
import io.github.dlbbld.ashlarchess.test.librarycomparison.enums.FindRandomGame;
import io.github.dlbbld.ashlarchess.unwinnability.DeadPositionQuickVerdict;

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

    List<MoveSpecification> legalMoves = board.getLegalMoveSpecifications();
    int numberOfMoveOptions = legalMoves.size();
    List<MoveSpecification> moveOptions = new ArrayList<>(legalMoves);

    int moveNumberLastPossibleTermination = -1;
    int numberOfMovesPerformed = 0;
    while (numberOfMoveOptions != 0) {
      if (numberOfMoveOptions != 0) {
        final int randomMoveNumberIndex = RandomUtility.calculateRandomNumber(0, numberOfMoveOptions - 1);
        final MoveSpecification moveSpecification = Nulls.get(moveOptions, randomMoveNumberIndex);
        board.move(moveSpecification);
        numberOfMovesPerformed++;
        if (numberOfMovesPerformed == 1 || numberOfMovesPerformed % 100 == 0) {
          System.out.println("Number of moves performed: " + numberOfMovesPerformed);
        }
        legalMoves = board.getLegalMoveSpecifications();
      }

      moveOptions = new ArrayList<>();
      for (final MoveSpecification moveSpecification : legalMoves) {
        board.move(moveSpecification);
        switch (findRandomGame) {
          case CHECKMATE:
            if (board.isCheckmate()) {
              moveNumberLastPossibleTermination = numberOfMovesPerformed + 1;
              System.out.println("Found checkmate option for move " + moveNumberLastPossibleTermination);
            }
            break;
          case FIFTY_MOVE_RULE:
            if (board.isFiftyMove()) {
              moveNumberLastPossibleTermination = numberOfMovesPerformed + 1;
              System.out.println("Found fifty-move rule option for move " + moveNumberLastPossibleTermination);
            }
            break;
          case INSUFFICIENT_MATERIAL:
            if (board.isInsufficientMaterial()) {
              moveNumberLastPossibleTermination = numberOfMovesPerformed + 1;
              System.out.println("Found insufficient material option for move " + moveNumberLastPossibleTermination);
            }
            break;
          case STALEMATE:
            if (board.isStalemate()) {
              moveNumberLastPossibleTermination = numberOfMovesPerformed + 1;
              System.out.println("Found stalemate option for move " + moveNumberLastPossibleTermination);
            }
            break;
          case THREEFOLD_REPETITION_RULE:
            // not implemented yet
            throw new IllegalArgumentException();
          default:
            throw new IllegalArgumentException();

        }
        if (!board.isCheckmate() && !board.isStalemate() && board.deadPositionQuick() != DeadPositionQuickVerdict.DEAD
            && board.getRepetitionCount() == 1 && !board.isFiftyMove()) {
          moveOptions.add(moveSpecification);
        }
        board.unmove();
      }
      numberOfMoveOptions = moveOptions.size();
    }

    if (moveNumberLastPossibleTermination != -1) {
      System.out.println("No more non terminating moves - we have a termination option");
      for (int i = numberOfMovesPerformed; i >= moveNumberLastPossibleTermination; i--) {
        board.unmove();
      }
      // now we should have at least one checkmate move
      // perform first found
      legalMoves = board.getLegalMoveSpecifications();
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
          System.out.println("A game with " + board.getPerformedMoveCount() / 2.0 + " moves ending in " + findRandomGame
              + " was generated");
          final String moves = calculateMoveText(board);
          System.out.println(moves);
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

    List<MoveSpecification> legalMoves = board.getLegalMoveSpecifications();
    int numberOfMoveOptions = legalMoves.size();
    List<MoveSpecification> moveOptions = new ArrayList<>(legalMoves);

    int numberOfMovesPerformed = 0;
    while (numberOfMoveOptions != 0) {
      if (numberOfMoveOptions != 0) {
        final int randomMoveNumberIndex = RandomUtility.calculateRandomNumber(0, numberOfMoveOptions - 1);
        final MoveSpecification moveSpecification = Nulls.get(moveOptions, randomMoveNumberIndex);
        board.move(moveSpecification);
        numberOfMovesPerformed++;
        if (numberOfMovesPerformed == 1 || numberOfMovesPerformed % 100 == 0) {
          System.out.println("Number of moves performed: " + numberOfMovesPerformed);
        }
        legalMoves = board.getLegalMoveSpecifications();
      }

      moveOptions = new ArrayList<>();
      for (final MoveSpecification moveSpecification : legalMoves) {
        board.move(moveSpecification);
        if (!board.isCheckmate() && !board.isStalemate() && board.deadPositionQuick() != DeadPositionQuickVerdict.DEAD
            && board.getRepetitionCount() == 1) {
          moveOptions.add(moveSpecification);
        }
        board.unmove();
      }
      numberOfMoveOptions = moveOptions.size();
    }

    System.out.println("A game with " + board.getPerformedMoveCount() / 2.0 + " moves was generated");
    final String moves = calculateMoveText(board);
    System.out.println(moves);
  }

  // we only want one sequence over 50, so after passing 50 first time, we try to reach 75 and if not successful that's
  // it, then try again
  private static void generateSeventyFive() {
    final Board board = new Board();

    List<MoveSpecification> legalMoves = board.getLegalMoveSpecifications();
    int numberOfMoveOptions = legalMoves.size();
    List<MoveSpecification> moveOptions = new ArrayList<>(legalMoves);

    boolean isFiftyReached = false;
    int numberOfMovesPerformed = 0;
    while (numberOfMoveOptions != 0) {
      if (numberOfMoveOptions != 0) {
        final int randomMoveNumberIndex = RandomUtility.calculateRandomNumber(0, numberOfMoveOptions - 1);
        final MoveSpecification moveSpecification = Nulls.get(moveOptions, randomMoveNumberIndex);
        board.move(moveSpecification);
        if (!isFiftyReached && board.isFiftyMove()) {
          isFiftyReached = true;
          System.out.println("Reached fifty move");
        }
        numberOfMovesPerformed++;
        if (numberOfMovesPerformed == 1 || numberOfMovesPerformed % 100 == 0) {
          System.out.println("Number of moves performed: " + numberOfMovesPerformed);
        }

        if (board.isSeventyFiveMove()) {
          System.out.println("A game with " + board.getPerformedMoveCount() / 2.0
              + " moves ending with seventy-five-move rule was generated");
          final String moves = calculateMoveText(board);
          System.out.println(moves);
          return;
        }
        legalMoves = board.getLegalMoveSpecifications();
      }

      moveOptions = new ArrayList<>();
      for (final MoveSpecification moveSpecification : legalMoves) {
        board.move(moveSpecification);
        if (!board.isCheckmate() && !board.isStalemate() && board.deadPositionQuick() == DeadPositionQuickVerdict.DEAD
            && board.getRepetitionCount() == 1 && (!isFiftyReached || board.isFiftyMove())) {
          moveOptions.add(moveSpecification);
        }
        board.unmove();
      }
      numberOfMoveOptions = moveOptions.size();
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

    List<MoveSpecification> legalMoves = board.getLegalMoveSpecifications();
    int numberOfMoveOptions = legalMoves.size();
    List<MoveSpecification> moveOptions = new ArrayList<>(legalMoves);

    boolean isRepetitionReached = false;
    // we need to intialize to something other than null, the below value is not meaning ful
    DynamicPosition repetitionPosition = board.getDynamicPosition();
    int numberOfMovesPerformed = 0;
    while (numberOfMoveOptions != 0) {
      {
        final int randomMoveNumberIndex = RandomUtility.calculateRandomNumber(0, numberOfMoveOptions - 1);
        final MoveSpecification moveSpecification = Nulls.get(moveOptions, randomMoveNumberIndex);
        board.move(moveSpecification);
        if (!isRepetitionReached && board.getRepetitionCount() == 2) {
          isRepetitionReached = true;
          repetitionPosition = board.getDynamicPosition();
          System.out.println("Reached first repetition");
        }
        numberOfMovesPerformed++;
        if (numberOfMovesPerformed == 1 || numberOfMovesPerformed % 100 == 0) {
          System.out.println("Number of moves performed: " + numberOfMovesPerformed);
        }

        if (board.getRepetitionCount() == repetitionNumber) {
          System.out.println("A game with " + board.getPerformedMoveCount() / 2.0 + " moves ending with "
              + repetitionNumber + " repetitions was generated");
          final String moves = calculateMoveText(board);
          System.out.println(moves);
          return true;
        }
        legalMoves = board.getLegalMoveSpecifications();
      }

      moveOptions = new ArrayList<>();

      // try to speed up
      // if we find continuation we use it, otherwise we never hit more than three using random moves
      for (final MoveSpecification moveSpecification : legalMoves) {
        board.move(moveSpecification);
        if (repetitionPosition.equals(board.getDynamicPosition())) {
          moveOptions.add(moveSpecification);
        }
        board.unmove();
      }
      if (moveOptions.isEmpty()) {
        // means we have no continuation from 3 onwards found
        for (final MoveSpecification moveSpecification : legalMoves) {
          board.move(moveSpecification);
          if (!board.isCheckmate() && !board.isStalemate() && board.deadPositionQuick() == DeadPositionQuickVerdict.DEAD
              && !board.isFiftyMove()) {
            moveOptions.add(moveSpecification);
          }
          board.unmove();
        }
      }
      numberOfMoveOptions = moveOptions.size();
    }

    System.out.println("Could not generate game ending with " + repetitionNumber + " repetitions");
    return false;
  }

  private static String calculateMoveText(Board board) {
    final StringBuilder moveText = new StringBuilder();
    final List<MoveSpecification> performedMoves = board.getPerformedMoveSpecifications();
    final Board replay = new Board(board.getInitialFen());
    for (int i = 0; i < performedMoves.size(); i++) {
      replay.move(Nulls.get(performedMoves, i));
      // after black move if following white move
      if (i > 0 && i % 2 == 0) {
        moveText.append(" ");
      }
      final Side sideToMove = replay.getMovingPiece().getSide();
      if (sideToMove.isWhite()) {
        moveText.append(replay.getLastPlayedFullMoveNumber()).append(". ");
      } else if (sideToMove.isBlack()) {
        moveText.append(" ");
      } else {
        throw new ProgrammingMistakeException("That should never happen");
      }
      moveText.append(replay.getSan());
    }

    return Nulls.toString(moveText);

  }

}
