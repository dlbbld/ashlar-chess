package com.dlb.chess.unwinnability;

import java.util.Set;
import java.util.TreeSet;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.board.enums.SquareType;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.common.utility.BasicUtility;
import com.dlb.chess.squares.KingNonCastlingEmptyBoardSquares;

//Figure 8 Semi-statically unwinnable algorithm, which may conclude that a position is
//unwinnable for an intended winner based on an admissible solution to the mobility problem.

//a We could design a more complete check that looks at all neighbours of s, but the condition on
//step 10 would be significantly more involved (to ensure monotonicity).
class UnwinnableSemiStatic {

  // Inputs: position, intended winner, solution to the mobility problem
  // Output: bool (true if position is declared unwinnable, false otherwise)
  public static boolean unwinnableSemiStatic(Board board, Side c, MobilitySolution mobilitySolution) {

    if (board.getLegalMoves().isEmpty()) {
      return !board.isCheck() || board.getHavingMove() == c;
    }

    if (board.isEnPassantCapturePossible()) {
      return false;
    }

    final PiecePlacement intendedLoserKing = calculateKing(c.getOppositeSide(), mobilitySolution);
    final Set<Square> intendedLoserKingRegion = SemiStaticFunctions.region(intendedLoserKing, mobilitySolution);
    final Set<PiecePlacement> visitorSet = calculateVisitorsExpanded(intendedLoserKingRegion, c, mobilitySolution);

    if (visitorSet.isEmpty()) {
      return true;
    }

    final Set<SquareType> visitorSquareTypeSet = calculateSquareTypeSet(visitorSet);
    if (visitorSquareTypeSet.size() > 1) {
      return false;
    }

    for (final PiecePlacement visitor : visitorSet) {
      if (visitor.pieceType() != PieceType.BISHOP) {
        return false;
      }
    }

    final SquareType visitorSquareType = BasicUtility.calculateOnlyElement(visitorSquareTypeSet);
    for (final Square matingSquare : Square.REAL) {
      final Set<PiecePlacement> matingBishopSet = removeKing(
          visitors(Nulls.setOf(matingSquare), c, false, mobilitySolution), c);

      if (matingBishopSet.isEmpty() || !intendedLoserKingRegion.contains(matingSquare)) {
        continue;
      }

      final Set<Square> escapingSquareSet = new TreeSet<>();
      final Set<Square> checkingSquareSet = new TreeSet<>();
      for (final Square adjacentSquare : KingNonCastlingEmptyBoardSquares.getKingSquares(matingSquare)) {
        if (intendedLoserKingRegion.contains(adjacentSquare)) {
          if (adjacentSquare.getSquareType() == visitorSquareType) {
            checkingSquareSet.add(adjacentSquare);
          } else {
            escapingSquareSet.add(adjacentSquare);
          }
        }
      }

      final Set<Square> neighbourSet = KingNonCastlingEmptyBoardSquares.getKingSquares(matingSquare);
      final boolean isActiveWinnerKing = visitors(neighbourSet, c, false, mobilitySolution)
          .contains(calculateKing(c, mobilitySolution));
      if (calculateHasTwoDiagonals(checkingSquareSet) && matingBishopSet.size() < 2 && !isActiveWinnerKing) {
        continue;
      }

        boolean isUnblockable = false;
      for (final Square escapingSquare : escapingSquareSet) {
        if (removeKings(visitors(Nulls.setOf(escapingSquare), c.getOppositeSide(), false, mobilitySolution))
            .isEmpty()) {
          isUnblockable = true;
          break;
        }
      }

      if (isUnblockable && !isActiveWinnerKing) {
        continue;
      }

      final Set<PiecePlacement> blockerSet = visitors(escapingSquareSet, c.getOppositeSide(), false, mobilitySolution);
      final int blockerCount = (isActiveWinnerKing ? 1 : 0) + removeKings(blockerSet).size();
      if (escapingSquareSet.size() <= blockerCount) {
        return false;
      }
    }

    return true;

  }

  static Set<PiecePlacement> calculateVisitorsExpanded(Set<Square> intendedLoserKingRegion, Side intendedWinner,
      MobilitySolution mobilitySolution) {
    return removeKing(visitors(intendedLoserKingRegion, intendedWinner, true, mobilitySolution), intendedWinner);
  }

  private static Set<PiecePlacement> visitors(Set<Square> region, Side side, boolean expandedPawnRegion,
      MobilitySolution mobilitySolution) {
    final Set<PiecePlacement> result = new TreeSet<>();
    final boolean isIgnorePawns = SemiStaticFunctions
        .region(calculateKing(side.getOppositeSide(), mobilitySolution), mobilitySolution).size() > 1;

    for (final PiecePlacement piecePlacement : mobilitySolution.getPiecePlacementSet()) {
      final Set<Square> pieceRegion = SemiStaticFunctions.region(piecePlacement, mobilitySolution);
      if (piecePlacement.pieceType() == PieceType.PAWN && isIgnorePawns && !pieceRegion.contains(Square.A1)) {
        continue;
      }

      if (piecePlacement.side() == side) {
        for (final Square target : region) {
          if (pieceRegion.contains(target)) {
            result.add(piecePlacement);
            break;
          }
          if (piecePlacement.pieceType() == PieceType.PAWN && expandedPawnRegion
              && piecePlacement.squareOriginal().getFile() != target.getFile() && !BasicUtility
                  .calculateIsDisjoint(MobilityFunctions.predecessorsCapture(piecePlacement, target), pieceRegion)) {
            result.add(piecePlacement);
            break;
          }
        }
      }
    }
    return result;
  }

  private static Set<PiecePlacement> removeKing(Set<PiecePlacement> piecePlacementSet, Side side) {
    final Set<PiecePlacement> result = new TreeSet<>();
    for (final PiecePlacement piecePlacement : piecePlacementSet) {
      if (piecePlacement.side() != side || piecePlacement.pieceType() != PieceType.KING) {
        result.add(piecePlacement);
      }
    }
    return result;
  }

  private static Set<PiecePlacement> removeKings(Set<PiecePlacement> piecePlacementSet) {
    final Set<PiecePlacement> result = new TreeSet<>();
    for (final PiecePlacement piecePlacement : piecePlacementSet) {
      if (piecePlacement.pieceType() != PieceType.KING) {
        result.add(piecePlacement);
      }
    }
    return result;
  }

  private static Set<SquareType> calculateSquareTypeSet(Set<PiecePlacement> piecePlacementSet) {
    final Set<SquareType> result = new TreeSet<>();
    for (final PiecePlacement piecePlacement : piecePlacementSet) {
      result.add(piecePlacement.squareOriginal().getSquareType());
    }
    return result;
  }

  private static boolean calculateHasTwoDiagonals(Set<Square> checkingSquareSet) {
    for (final Square squareA : checkingSquareSet) {
      for (final Square squareB : checkingSquareSet) {
        if (squareA == squareB) {
          continue;
        }
        final int fileDistance = Math.abs(squareA.getFile().getNumber() - squareB.getFile().getNumber());
        final int rankDistance = Math.abs(squareA.getRank().getNumber() - squareB.getRank().getNumber());
        if (fileDistance == 2 && rankDistance == 0 || fileDistance == 0 && rankDistance == 2) {
          return true;
        }
      }
    }
    return false;
  }

  private static PiecePlacement calculateKing(Side c, MobilitySolution mobilitySolution) {
    for (final PiecePlacement piecePlacement : mobilitySolution.getPiecePlacementSet()) {
      if (piecePlacement.side() == c && piecePlacement.pieceType() == PieceType.KING) {
        return piecePlacement;
      }
    }
    throw new ProgrammingMistakeException("King not in the list");
  }

}
