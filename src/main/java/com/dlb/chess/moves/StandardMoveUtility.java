package com.dlb.chess.moves;

import java.util.ArrayList;
import java.util.List;

import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.model.UpdateSquare;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.common.model.MoveSpecification;

public abstract class StandardMoveUtility implements EnumConstants {

  /**
   * Produces the {@link UpdateSquare} list for a non-castling, non-EP, non-promotion piece movement: the from-square
   * becomes empty, the to-square gets the moving piece. Takes the moving piece explicitly (caller has access to it via
   * whatever board representation it carries - the helper is position-representation-neutral).
   */
  public static List<UpdateSquare> performStandardMovements(Piece movingPiece, MoveSpecification moveSpecification) {
    final List<UpdateSquare> result = new ArrayList<>();
    result.add(new UpdateSquare(moveSpecification.fromSquare()));
    result.add(new UpdateSquare(moveSpecification.toSquare(), movingPiece));
    return result;
  }

}
