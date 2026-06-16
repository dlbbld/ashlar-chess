// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H8;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.squares.KingNonCastlingEmptyBoardSquares;
import io.github.dlbbld.ashlarchess.squares.KnightEmptyBoardSquares;

class MobilityFunctions {

  public static Set<Square> predecessorsCapture(PiecePlacement piecePlacement, Square square) {
    switch (piecePlacement.pieceType()) {
      case PAWN:
        final Set<Square> result = new TreeSet<>();
        if (square.hasBehindLeftDiagonalSquare(piecePlacement.side())) {
          result.add(square.getBehindLeftDiagonalSquare(piecePlacement.side()));
        }
        if (square.hasBehindRightDiagonalSquare(piecePlacement.side())) {
          result.add(square.getBehindRightDiagonalSquare(piecePlacement.side()));
        }
        return result;
      case ROOK:
      case KNIGHT:
      case BISHOP:
      case QUEEN:
      case KING:
        return MobilityFunctions.predecessors(piecePlacement, square);
      case NONE:
      default:
        throw new IllegalArgumentException();
    }
  }

  static Set<Square> promotion(PiecePlacement piecePlacement) {
    return switch (piecePlacement.pieceType()) {
      case PAWN -> switch (piecePlacement.side()) {
        case WHITE -> new TreeSet<>(Arrays.asList(A8, B8, C8, D8, E8, F8, G8, H8));
        case BLACK -> new TreeSet<>(Arrays.asList(A1, B1, C1, D1, E1, F1, G1, H1));
        case NONE -> throw new IllegalArgumentException();
        default -> throw new IllegalArgumentException();
      };
      case ROOK, KNIGHT, BISHOP, QUEEN, KING -> new TreeSet<>();
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  static Set<Square> predecessors(PiecePlacement piecePlacement, Square square) {
    return switch (piecePlacement.pieceType()) {
      case PAWN -> calculateBehindSquare(piecePlacement.side(), square);
      case ROOK -> KingDistanceOneFunctions.calculateOrthogonalSquares(square);
      case KNIGHT -> KnightEmptyBoardSquares.getKnightSquares(square);
      case BISHOP -> KingDistanceOneFunctions.calculateDiagonalSquares(square);
      case QUEEN, KING -> KingNonCastlingEmptyBoardSquares.getKingSquares(square);
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  private static Set<Square> calculateBehindSquare(Side side, Square square) {
    if (!square.hasBehindSquare(side)) {
      return new TreeSet<>();
    }
    final Set<Square> result = new TreeSet<>();
    result.add(square.getBehindSquare(side));
    return result;
  }

  static Set<PiecePlacement> attackers(Set<PiecePlacement> piecePlacementList, Square square) {
    final Set<PiecePlacement> result = new TreeSet<>();

    for (final PiecePlacement piecePlacement : piecePlacementList) {
      if (predecessorsCapture(piecePlacement, square).contains(piecePlacement.squareOriginal())) {
        result.add(piecePlacement);
      }
    }
    return result;
  }

}
