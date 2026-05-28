package io.github.dlbbld.ashlarchess.san;

import java.util.ArrayList;
import java.util.List;

import io.github.dlbbld.ashlarchess.board.enums.File;
import io.github.dlbbld.ashlarchess.board.enums.Rank;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.model.LegalMove;

abstract class AbstractSan {

  static Square calculateFromSquare(SanConversion sanConversion) {
    if (sanConversion.fromFile() == File.NONE || sanConversion.fromRank() == Rank.NONE) {
      return Square.NONE;
    }
    return Square.calculate(sanConversion.fromFile(), sanConversion.fromRank());
  }

  static List<LegalMove> filterLegalMovesCandidates(List<LegalMove> legalMoves, Square toSquare) {
    final List<LegalMove> legalMovesForToSquare = new ArrayList<>();
    for (final LegalMove moveCandidate : legalMoves) {
      if (moveCandidate.moveSpecification().toSquare() == toSquare) {
        legalMovesForToSquare.add(moveCandidate);
      }
    }
    return legalMovesForToSquare;
  }

  static List<LegalMove> calculateLegalMovesCandidates(List<LegalMove> legalMoves, File fromFile) {
    final List<LegalMove> legalMovesForFromFile = new ArrayList<>();
    for (final LegalMove moveCandidate : legalMoves) {
      if (moveCandidate.moveSpecification().fromSquare().getFile() == fromFile) {
        legalMovesForFromFile.add(moveCandidate);
      }
    }
    return legalMovesForFromFile;
  }

  static boolean calculateHasOtherFilesHavingLegalMoves(File file, List<LegalMove> legalMoves) {
    for (final LegalMove moveCandidate : legalMoves) {
      final File candidateFromFile = moveCandidate.moveSpecification().fromSquare().getFile();
      if (candidateFromFile != file) {
        return true;
      }
    }
    return false;
  }

  static int calculateNumberOfLegalMovesFromFile(File file, List<LegalMove> legalMoves) {
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

  static int calculateNumberOfLegalMovesFromOtherFiles(File file, List<LegalMove> legalMoves) {
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

  static int calculateNumberOfLegalMovesFromRank(Rank rank, List<LegalMove> legalMoves) {
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

  static int calculateNumberOfLegalMovesFromSquare(Square square, List<LegalMove> legalMoves) {
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

  static boolean calculateHasOtherRanksHavingLegalMoves(Rank rank, List<LegalMove> legalMoves) {
    for (final LegalMove moveCandidate : legalMoves) {
      final Rank candidateFromRank = moveCandidate.moveSpecification().fromSquare().getRank();
      if (candidateFromRank != rank) {
        return true;
      }
    }
    return false;
  }

}
