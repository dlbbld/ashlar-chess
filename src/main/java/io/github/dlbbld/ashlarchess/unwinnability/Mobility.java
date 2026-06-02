// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.annotation.NonNull;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.File;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.common.utility.BasicUtility;

//Figure 7 Mobility algorithm.
class Mobility {

  // Inputs: a position
  // Output: mobility solution {MP!s}P in pos,s in S
  public static MobilitySolution mobility(Board board) {

    // 1: set MP->s := 0, CP := 0, Rcs
    // := 0 for all P in pos, s in S and c in {w, b} and let X_arrow be
    // the state containing all these variables

    // set MP->s := 0
    final BitboardPosition bitboardPosition = board.getBitboardPosition();
    final List<PiecePlacement> piecePlacementList = new ArrayList<>();
    long occupied = bitboardPosition.occupied();
    while (occupied != 0L) {
      final Square square = Nulls.get(Square.REAL, Long.numberOfTrailingZeros(occupied));
      final Piece piece = bitboardPosition.get(square);
      piecePlacementList.add(new PiecePlacement(piece.getPieceType(), piece.getSide(), square));
      occupied &= occupied - 1L;
    }

    final @NonNull MobilitySolution mobility = new MobilitySolution();
    for (final PiecePlacement piecePlacement : piecePlacementList) {
      for (final Square square : Square.REAL) {
        mobility.put(piecePlacement, square, VariableState.ZERO);
      }
    }

    // CP := 0
    final @NonNull Clearability clearability = new Clearability();
    for (final PiecePlacement piecePlacement : piecePlacementList) {
      clearability.put(piecePlacement, VariableState.ZERO);
    }

    // Rcs := 0
    final @NonNull Reachability reachability = new Reachability();
    for (final Side side : Side.REAL) {
      for (final Square square : Square.REAL) {
        reachability.put(side, square, VariableState.ZERO);
      }
    }

    // 2: set MP -> P.sq := 1, for all P in pos ( -> Every piece can "move" to its current square)
    for (final PiecePlacement piecePlacement : piecePlacementList) {
      mobility.put(piecePlacement, piecePlacement.squareOriginal(), VariableState.ONE);
    }

    int whiteMovements = 0;
    int blackMovements = 0;
    Square whiteExtraKingSquare = calculateKingSquare(piecePlacementList, Side.WHITE);
    Square blackExtraKingSquare = calculateKingSquare(piecePlacementList, Side.BLACK);
    int round = 0;
    boolean isNewVariablesAreSetToOne = true;
    while (isNewVariablesAreSetToOne) {
      round++;
      boolean isFirstRoundCaptureDeferred = false;
      final int totalVariableCountSetToOneBefore = calculateTotalVariableCountSetToOne(mobility, clearability,
          reachability);
      // 3: for every variable V in X_arrow that is still set to 0 do

      // 4: if for every rule from Figure 6 of the form "V ) f", formula f evaluates to true
      // on the current state of variables ~X then set V to 1 in X_arrow

      // Update clearability
      // Clearance. A piece can be cleared from its square by moving or being captured:
      for (final PiecePlacement candidateClearability : clearability.calculateEntriesWithValueZero()) {

        // clearable by moving
        boolean isClearabilityForMoving = false;
        for (final Square evaluateToSquare : Square.REAL) {
          if (evaluateToSquare == candidateClearability.squareOriginal()) {
            continue;
          }
          if (mobility.get(candidateClearability, evaluateToSquare) == VariableState.ONE) {
            isClearabilityForMoving = true;
            clearability.put(candidateClearability, VariableState.ONE);
            break;
          }
        }

        // clearable by capturing
        if (!isClearabilityForMoving) {
          for (final MobilitySolutionVariable evaluateCapture : mobility.calculateEntriesWithValueOne()) {
            if (evaluateCapture.piecePlacement().side() != candidateClearability.side()
                && evaluateCapture.toSquare() == candidateClearability.squareOriginal()) {
              clearability.put(candidateClearability, VariableState.ONE);
              break;
            }
          }
        }
      }

      // Update reachability
      // Reachability. A square can be reached if some piece can move to it or it is occupied:
      for (final ReachabilityVariable candidateReachability : reachability.calculateEntriesWithValueZero()) {
        for (final MobilitySolutionVariable evaluateReachability : mobility.calculateEntriesWithValueOne()) {
          if (evaluateReachability.piecePlacement().side() == candidateReachability.sideWhichCanReach()
              && evaluateReachability.piecePlacement().pieceType() != PieceType.KING
              && evaluateReachability.toSquare() == candidateReachability.toSquare()) {
            reachability.put(candidateReachability.sideWhichCanReach(), candidateReachability.toSquare(),
                VariableState.ONE);
            break;
          }
        }
      }

      // Update mobility
      for (final PiecePlacement candidatePiecePlacement : piecePlacementList) {
        // Move. If a piece can move to square s, it must pass first by a predecessor of s:
        // all pieces except pawns
        for (final Square candidateToSquare : Square.REAL) {
          if (mobility.get(candidatePiecePlacement, candidateToSquare) == VariableState.ONE) {
            continue;
          }
          final MobilitySolutionVariable candidateMobility = new MobilitySolutionVariable(candidatePiecePlacement,
              candidateToSquare);
          final PieceType candidatePieceType = candidatePiecePlacement.pieceType();

          if (candidatePieceType == PieceType.PAWN) {
            if (!calculateIsPawnMoveConditionOk(candidateMobility, mobility, clearability, reachability)) {
              continue;
            }
          } else if (!calculateIsNonPawnMoveConditionOk(candidateMobility, mobility)) {
            continue;
          }

          if (!calculateIsKingNotMovingIntoCheckConditionOk(candidateMobility, mobility, clearability)
              || !calculateIsNotMovingOntoOwnPieceConditionOk(candidateMobility, mobility, clearability)) {
            continue;
          }

          if (candidatePiecePlacement.side() == Side.WHITE) {
            whiteMovements++;
            if (candidatePieceType == PieceType.KING) {
              whiteExtraKingSquare = candidateToSquare;
            }
          } else {
            blackMovements++;
            if (candidatePieceType == PieceType.KING) {
              blackExtraKingSquare = candidateToSquare;
            }
          }

          if (calculateIsKingBlockedByExtraKingSquare(candidatePiecePlacement, candidateToSquare, whiteMovements,
              blackMovements, whiteExtraKingSquare, blackExtraKingSquare, piecePlacementList)) {
            continue;
          }

          if (round <= 1 && !bitboardPosition.isEmpty(candidateToSquare)) {
            isFirstRoundCaptureDeferred = true;
            continue;
          }

          mobility.put(candidatePiecePlacement, candidateToSquare, VariableState.ONE);
        }
      }

      // 5: repeat steps 3 and 4 until no new variables are set to 1
      final int totalVariableCountSetToOneAfter = calculateTotalVariableCountSetToOne(mobility, clearability,
          reachability);
      if (totalVariableCountSetToOneBefore > totalVariableCountSetToOneAfter) {
        throw new ProgrammingMistakeException("Variable state was incorrectly set");
      }
      isNewVariablesAreSetToOne = totalVariableCountSetToOneAfter > totalVariableCountSetToOneBefore
          || isFirstRoundCaptureDeferred;
    }

    // 6: return {MP -> s}P in pos, s in S
    return mobility;

  }

  private static boolean calculateIsCanLeaveFile(PiecePlacement checkPawn, MobilitySolution mobility, File file) {

    final Set<Square> mobilityOpponentPawn = mobility.calculateSquaresWithValueOne(checkPawn);

    for (final Square pawnMobility : mobilityOpponentPawn) {
      if (pawnMobility.getFile() != file) {
        return true;
      }
    }
    return false;
  }

  private static boolean calculateIsCanCaptureOpponentPawnWithNonPawn(PiecePlacement opponentPawn,
      MobilitySolution mobilitySolution) {

    final Set<Square> pawnSquareAndForwardPushesSet = calculatePawnSquareAndForwardPushes(opponentPawn,
        mobilitySolution);

    for (final MobilitySolutionVariable evaluateCapture : mobilitySolution.calculateEntriesWithValueOne()) {
      // we only check non pawns, because for pawn we would not need to exclude forward pushes
      // which we can't do as we have no move history
      if (evaluateCapture.piecePlacement().side() != opponentPawn.side()
          && evaluateCapture.piecePlacement().pieceType() != PieceType.PAWN
          && pawnSquareAndForwardPushesSet.contains(evaluateCapture.toSquare())) {
        return true;
      }
    }
    return false;
  }

  private static Set<Square> calculatePawnSquareAndForwardPushes(PiecePlacement pawn,
      MobilitySolution mobilitySolution) {
    final Set<Square> result = new TreeSet<>();
    result.add(pawn.squareOriginal());

    for (final Square mobilitySquare : mobilitySolution.calculateSquaresWithValueOne(pawn)) {
      if (mobilitySquare.getFile() == pawn.squareOriginal().getFile()) {
        result.add(mobilitySquare);
      }

    }
    return result;
  }

  private static int calculateTotalVariableCountSetToOne(MobilitySolution mps, Clearability cp, Reachability rcs) {
    return cp.calculateVariableCountSetToOne() + rcs.calculateVariableCountSetToOne()
        + mps.calculateVariableCountSetToOne();
  }

  private static Square calculateKingSquare(List<PiecePlacement> piecePlacementList, Side side) {
    for (final PiecePlacement piecePlacement : piecePlacementList) {
      if (piecePlacement.side() == side && piecePlacement.pieceType() == PieceType.KING) {
        return piecePlacement.squareOriginal();
      }
    }
    throw new ProgrammingMistakeException("King not in the list");
  }

  private static boolean calculateIsKingBlockedByExtraKingSquare(PiecePlacement candidatePiecePlacement,
      Square candidateToSquare, int whiteMovements, int blackMovements, Square whiteExtraKingSquare,
      Square blackExtraKingSquare, List<PiecePlacement> piecePlacementList) {
    if (candidatePiecePlacement.pieceType() != PieceType.KING) {
      return false;
    }

    final Side side = candidatePiecePlacement.side();
    if (side == Side.WHITE && blackMovements > 1 || side == Side.BLACK && whiteMovements > 1) {
      return false;
    }

    final Square opponentKingSquare = calculateKingSquare(piecePlacementList, side.getOppositeSide());
    final Square opponentExtraKingSquare = side == Side.WHITE ? blackExtraKingSquare : whiteExtraKingSquare;
    return calculateIsKingDistanceAtMostOne(candidateToSquare, opponentKingSquare)
        || calculateIsKingDistanceAtMostOne(candidateToSquare, opponentExtraKingSquare);
  }

  private static boolean calculateIsKingDistanceAtMostOne(Square squareA, Square squareB) {
    final int fileDistance = Math.abs(squareA.getFile().getNumber() - squareB.getFile().getNumber());
    final int rankDistance = Math.abs(squareA.getRank().getNumber() - squareB.getRank().getNumber());
    return fileDistance <= 1 && rankDistance <= 1;
  }

  private static boolean calculateIsPawnMoveConditionOk(MobilitySolutionVariable candidateMobility,
      MobilitySolution mobility, Clearability clearability, Reachability reachability) {

    return calculateIsPossiblePawnForwardMove(candidateMobility, mobility, clearability)
        || calculateIsPossiblePawnCapture(candidateMobility, mobility, reachability)
        || calculateIsPossiblePawnPromotion(candidateMobility, mobility);
  }

  private static boolean calculateIsPossiblePawnForwardMove(MobilitySolutionVariable candidateMobility,
      MobilitySolution mobility, Clearability clearability) {
    final PiecePlacement candidatePiecePlacement = candidateMobility.piecePlacement();
    final Square candidateToSquare = candidateMobility.toSquare();

    // currently only one predecessor
    // still by the logic we check as if multiple, "any valid predecessor" returns true
    for (final Square predecessor : MobilityFunctions.predecessors(candidatePiecePlacement, candidateToSquare)) {
      if (mobility.get(candidatePiecePlacement, predecessor) == VariableState.ONE
          && !calculateHasBlockingNonPawn(candidateMobility, mobility, clearability)
          && !calculateHasBlockingPawn(candidateMobility, mobility)) {
        return true;
      }
    }
    return false;
  }

  // Pawn move. For pawn pushes, we require that a possibly enemy piece on the target
  // square can be cleared first.
  // Note: We don't need to check not moving onto own piece. That is checked for all movements in later
  // step.
  private static boolean calculateHasBlockingNonPawn(MobilitySolutionVariable candidateMobility,
      MobilitySolution mobility, Clearability clearability) {
    final PiecePlacement candidatePiecePlacement = candidateMobility.piecePlacement();
    final Side candidateSide = candidatePiecePlacement.side();
    final Side candidateOppositeSide = candidateSide.getOppositeSide();
    final Square candidateToSquare = candidateMobility.toSquare();

    for (final PiecePlacement checkNonPawn : mobility.getPiecePlacementSet()) {
      if (checkNonPawn.side() == candidateOppositeSide && checkNonPawn.squareOriginal() == candidateToSquare
          && checkNonPawn.pieceType() != PieceType.PAWN && clearability.get(checkNonPawn) == VariableState.ZERO) {
        return true;
      }
    }
    return false;
  }

  // Implemented but not specified condition in the PDF of opponent pawns must be able to leave the file
  // we only need to check if not already blocked by non pawn piece
  private static boolean calculateHasBlockingPawn(MobilitySolutionVariable candidateMobility,
      MobilitySolution mobility) {
    final PiecePlacement candidatePiecePlacement = candidateMobility.piecePlacement();
    final Side candidateSide = candidatePiecePlacement.side();
    final Side candidateOppositeSide = candidateSide.getOppositeSide();
    final Square candidateToSquare = candidateMobility.toSquare();

    for (final PiecePlacement checkPawn : mobility.getPiecePlacementSet()) {
      if (checkPawn.side() == candidateOppositeSide && checkPawn.squareOriginal() == candidateToSquare
          && checkPawn.pieceType() == PieceType.PAWN) {

        // now we check leaving the diagonal and capturing
        if (!calculateIsCanLeaveFile(checkPawn, mobility, candidateToSquare.getFile())
            && !calculateIsCanCaptureOpponentPawnWithNonPawn(checkPawn, mobility)) {
          return true;
        }
      }
    }
    return false;
  }

  private static boolean calculateIsPossiblePawnCapture(MobilitySolutionVariable candidateMobility,
      MobilitySolution mobility, Reachability reachability) {

    final PiecePlacement candidatePiecePlacement = candidateMobility.piecePlacement();
    final Side candidateSide = candidatePiecePlacement.side();
    final Side candidateOppositeSide = candidateSide.getOppositeSide();
    final Square candidateToSquare = candidateMobility.toSquare();

    // Pawn move. For pawn captures we require that the capturing square can be reached
    // by a (non-king) opponent piece.
    final Set<Square> predecessorsCaptureSet = MobilityFunctions.predecessorsCapture(candidatePiecePlacement,
        candidateToSquare);

    for (final Square predecessorCapture : predecessorsCaptureSet) {
      if (mobility.get(candidatePiecePlacement, predecessorCapture) == VariableState.ONE
          && reachability.get(candidateOppositeSide, candidateToSquare) == VariableState.ONE) {
        return true;
      }
    }
    return false;
  }

  private static boolean calculateIsPossiblePawnPromotion(MobilitySolutionVariable candidateMobility,
      MobilitySolution mobility) {
    final PiecePlacement candidatePiecePlacement = candidateMobility.piecePlacement();

    // Pawns that promote may go everywhere:
    final Set<Square> promotionSquareSet = MobilityFunctions.promotion(candidatePiecePlacement);
    final Set<Square> squareSetWithValueOne = mobility.calculateSquaresWithValueOne(candidatePiecePlacement);

    final boolean isValidPawnPromotion = !BasicUtility.calculateIsDisjoint(promotionSquareSet, squareSetWithValueOne);

    return isValidPawnPromotion;
  }

  private static boolean calculateIsNonPawnMoveConditionOk(MobilitySolutionVariable candidateMobility,
      MobilitySolution mobility) {
    final PiecePlacement candidatePiecePlacement = candidateMobility.piecePlacement();
    final Square candidateToSquare = candidateMobility.toSquare();

    // now the non pawns
    for (final Square predecessor : MobilityFunctions.predecessors(candidatePiecePlacement, candidateToSquare)) {
      if (mobility.get(candidatePiecePlacement, predecessor) == VariableState.ONE) {
        return true;
      }
    }
    return false;
  }

  private static boolean calculateIsKingNotMovingIntoCheckConditionOk(MobilitySolutionVariable candidateMobility,
      MobilitySolution mobility, Clearability clearability) {
    final PiecePlacement candidatePiecePlacement = candidateMobility.piecePlacement();
    final PieceType candidatePieceType = candidateMobility.piecePlacement().pieceType();
    final Side candidateSide = candidatePiecePlacement.side();
    final Square candidateToSquare = candidateMobility.toSquare();

    if (candidatePieceType != PieceType.KING) {
      return true;
    }

    for (final PiecePlacement attacker : MobilityFunctions.attackers(mobility.getPiecePlacementSet(),
        candidateToSquare)) {
      if (attacker.side() != candidateSide && clearability.get(attacker) != VariableState.ONE) {
        return false;
      }
    }
    return true;
  }

  private static boolean calculateIsNotMovingOntoOwnPieceConditionOk(MobilitySolutionVariable candidateMobility,
      MobilitySolution mobility, Clearability clearability) {
    final PiecePlacement candidatePiecePlacement = candidateMobility.piecePlacement();
    final Side candidateSide = candidatePiecePlacement.side();
    final Square candidateToSquare = candidateMobility.toSquare();

    // Not self-capture. A piece must be cleared from a square before other of the same
    // color can move to it:
    // all pieces
    for (final PiecePlacement checkPiecePlacement : mobility.getPiecePlacementSet()) {
      if (checkPiecePlacement != candidatePiecePlacement && checkPiecePlacement.side() == candidateSide
          && checkPiecePlacement.squareOriginal() == candidateToSquare
          && clearability.get(checkPiecePlacement) == VariableState.ZERO) {
        return false;
      }
    }
    return true;

  }
}
