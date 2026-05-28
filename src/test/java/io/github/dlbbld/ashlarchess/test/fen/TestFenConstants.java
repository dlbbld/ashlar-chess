package io.github.dlbbld.ashlarchess.test.fen;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.fen.constants.FenConstants;

class TestFenConstants {

  @SuppressWarnings("static-method")
  @Test
  void testFirstHalfmove() {

    // first we put the hardcoded constants in a set
    final Set<String> hardCodedFenSet = new TreeSet<>(FenConstants.POSSIBLE_FEN_AFTER_FIRST_HALF_MOVE);

    // second we calculate what we expect them to be
    final Set<String> calculatedFenSet = new TreeSet<>();
    final Board board = new Board();
    for (final MoveSpecification moveSpecification : board.getPossibleMoveSpecificationList()) {
      board.move(moveSpecification);
      calculatedFenSet.add(board.getFen());
      board.unmove();
    }

    // third we compare them and expect to be equal
    assertEquals(calculatedFenSet, hardCodedFenSet);

  }

}