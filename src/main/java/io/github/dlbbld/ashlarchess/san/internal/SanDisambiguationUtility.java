// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.san.internal;

import java.util.ArrayList;
import java.util.List;

import io.github.dlbbld.ashlarchess.board.enums.File;
import io.github.dlbbld.ashlarchess.board.enums.Rank;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.board.LegalMove;

/**
 * Shared SAN disambiguation helpers. The methods are {@code public} so both the SAN generator ({@link MoveToSan}) and
 * the SAN validators in the {@code san} package can call them across the package boundary; {@code san.internal} is not
 * exported, so this does not widen the consumer-facing API.
 */
public final class SanDisambiguationUtility {

  private SanDisambiguationUtility() {
  }

  public static Square calculateFromSquare(SanConversion sanConversion) {
    if (sanConversion.fromFile() == File.NONE || sanConversion.fromRank() == Rank.NONE) {
      return Square.NONE;
    }
    return Square.of(sanConversion.fromFile(), sanConversion.fromRank());
  }

  public static List<LegalMove> filterLegalMovesCandidates(List<LegalMove> legalMoves, Square toSquare) {
    final List<LegalMove> legalMovesForToSquare = new ArrayList<>();
    for (final LegalMove moveCandidate : legalMoves) {
      if (moveCandidate.moveSpecification().toSquare() == toSquare) {
        legalMovesForToSquare.add(moveCandidate);
      }
    }
    return legalMovesForToSquare;
  }

  public static List<LegalMove> calculateLegalMovesCandidates(List<LegalMove> legalMoves, File fromFile) {
    final List<LegalMove> legalMovesForFromFile = new ArrayList<>();
    for (final LegalMove moveCandidate : legalMoves) {
      if (moveCandidate.moveSpecification().fromSquare().getFile() == fromFile) {
        legalMovesForFromFile.add(moveCandidate);
      }
    }
    return legalMovesForFromFile;
  }

  public static boolean calculateHasOtherFilesHavingLegalMoves(File file, List<LegalMove> legalMoves) {
    for (final LegalMove moveCandidate : legalMoves) {
      final File candidateFromFile = moveCandidate.moveSpecification().fromSquare().getFile();
      if (candidateFromFile != file) {
        return true;
      }
    }
    return false;
  }

  public static int calculateNumberOfLegalMovesFromFile(File file, List<LegalMove> legalMoves) {
    return calculateLegalMovesFromFile(file, legalMoves).size();
  }

  private static List<LegalMove> calculateLegalMovesFromFile(File file, List<LegalMove> legalMoves) {
    final List<LegalMove> filtered = new ArrayList<>();
    for (final LegalMove moveCandidate : legalMoves) {
      final File candidateFromFile = moveCandidate.moveSpecification().fromSquare().getFile();
      if (candidateFromFile == file) {
        filtered.add(moveCandidate);
      }
    }
    return filtered;
  }

  public static int calculateNumberOfLegalMovesFromOtherFiles(File file, List<LegalMove> legalMoves) {
    return calculateLegalMovesFromOtherFiles(file, legalMoves).size();
  }

  private static List<LegalMove> calculateLegalMovesFromOtherFiles(File file, List<LegalMove> legalMoves) {
    final List<LegalMove> filtered = new ArrayList<>();
    for (final LegalMove moveCandidate : legalMoves) {
      final File candidateFromFile = moveCandidate.moveSpecification().fromSquare().getFile();
      if (candidateFromFile != file) {
        filtered.add(moveCandidate);
      }
    }
    return filtered;
  }

  public static int calculateNumberOfLegalMovesFromRank(Rank rank, List<LegalMove> legalMoves) {
    return calculateLegalMovesFromRank(rank, legalMoves).size();
  }

  private static List<LegalMove> calculateLegalMovesFromRank(Rank rank, List<LegalMove> legalMoves) {
    final List<LegalMove> filtered = new ArrayList<>();
    for (final LegalMove moveCandidate : legalMoves) {
      final Rank candidateFromRank = moveCandidate.moveSpecification().fromSquare().getRank();
      if (candidateFromRank == rank) {
        filtered.add(moveCandidate);
      }
    }
    return filtered;
  }

  public static int calculateNumberOfLegalMovesFromSquare(Square square, List<LegalMove> legalMoves) {
    return calculateLegalMovesFromSquare(square, legalMoves).size();
  }

  private static List<LegalMove> calculateLegalMovesFromSquare(Square square, List<LegalMove> legalMoves) {
    final List<LegalMove> filtered = new ArrayList<>();
    for (final LegalMove moveCandidate : legalMoves) {
      final Square candidateFromSquare = moveCandidate.moveSpecification().fromSquare();
      if (candidateFromSquare == square) {
        filtered.add(moveCandidate);
      }
    }
    return filtered;
  }

  public static boolean calculateHasOtherRanksHavingLegalMoves(Rank rank, List<LegalMove> legalMoves) {
    for (final LegalMove moveCandidate : legalMoves) {
      final Rank candidateFromRank = moveCandidate.moveSpecification().fromSquare().getRank();
      if (candidateFromRank != rank) {
        return true;
      }
    }
    return false;
  }

}
