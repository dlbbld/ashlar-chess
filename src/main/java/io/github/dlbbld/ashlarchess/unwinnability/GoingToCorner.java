// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.MoveSpecification;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;

// Figure 13 Going-to-corner routine, called from the Score heuristic on King and Knight moves.
/**
 * The Going-to-corner heuristic (paper Figure 13), used by {@link Score}. It rewards a "slow" piece (King or Knight)
 * move that steps <em>closer</em> to a target square near the mating corner - the depth extension that lets the
 * winner drive the loser's king into a corner. Pure heuristic: affects search effectiveness only, never correctness.
 */
final class GoingToCorner {

  private GoingToCorner() {
  }

  // Target squares near the h8 / a8 corners (square indices; rotated 180 degrees when the winner is Black).
  private static final int H6 = 47;
  private static final int H8 = 63;
  private static final int G8 = 62;
  private static final int A6 = 40;
  private static final int A8 = 56;
  private static final int B8 = 57;

  /**
   * @param goalWin {@code true} for the winner's move (drive to mate), {@code false} for the loser's
   * @return whether {@code move} decreases the piece's distance to the target corner square
   */
  static boolean towardCorner(Board board, MoveSpecification move, Side winner, boolean goalWin) {
    if (move.isCastling()) {
      return false;
    }
    final BitboardPosition position = board.getBitboardPosition();
    final Square from = move.fromSquare();
    final Square to = move.toSquare();
    final PieceType pieceType = position.get(from).getPieceType();
    if (pieceType != PieceType.KING && pieceType != PieceType.KNIGHT) {
      return false; // line 2: only slow pieces
    }

    final long winnerBishops = winner == Side.WHITE ? position.whiteBishops() : position.blackBishops();
    final long loserBishops = winner == Side.WHITE ? position.blackBishops() : position.whiteBishops();
    final boolean winnerDark = (winnerBishops & SquareGeometry.DARK_SQUARES) != 0L;
    final boolean winnerLight = (winnerBishops & SquareGeometry.LIGHT_SQUARES) != 0L;
    final boolean loserLight = (loserBishops & SquareGeometry.LIGHT_SQUARES) != 0L;

    final boolean isKing = pieceType == PieceType.KING;
    int target;
    if (winnerDark || (loserLight && !winnerLight)) {
      target = goalWin ? (isKing ? H6 : H8) : (isKing ? H8 : G8); // corner h8
    } else {
      target = goalWin ? (isKing ? A6 : A8) : (isKing ? A8 : B8); // corner a8
    }
    if (winner == Side.BLACK) {
      target = 63 - target; // flip-rank plus flip-file (180 degree rotation)
    }

    final int toIndex = to.ordinal();
    final int fromIndex = from.ordinal();
    return isKing ? SquareGeometry.kingDistance(toIndex, target) < SquareGeometry.kingDistance(fromIndex, target)
        : SquareGeometry.knightDistance(toIndex, target) < SquareGeometry.knightDistance(fromIndex, target);
  }
}
