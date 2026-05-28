package com.dlb.chess.test.common.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.constants.CastlingConstants;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.common.ucimove.utility.UciMoveUtility;
import com.dlb.chess.common.ucimove.utility.UciMoveValidationUtility;
import com.dlb.chess.model.LegalMove;
import com.dlb.chess.model.UciMove;
import com.dlb.chess.moves.CastlingUtility;
import com.dlb.chess.test.custom.model.UciMoveTest;

class TestUciMoveUtility {

  @SuppressWarnings("static-method")
  @Test
  void test() {

    // here we omit castling
    {
      final List<UciMoveTest> list = new ArrayList<>();

      list.add(new UciMoveTest("e4", "e2e4"));
      list.add(new UciMoveTest("d5", "d7d5"));
      list.add(new UciMoveTest("exd5", "e4d5"));
      list.add(new UciMoveTest("c6", "c7c6"));
      list.add(new UciMoveTest("dxc6", "d5c6"));
      list.add(new UciMoveTest("Nf6", "g8f6"));
      list.add(new UciMoveTest("cxb7", "c6b7"));
      list.add(new UciMoveTest("Rg8", "h8g8"));
      list.add(new UciMoveTest("bxa8=Q", "b7a8q"));
      list.add(new UciMoveTest("Bf5", "c8f5"));
      list.add(new UciMoveTest("Qxa7", "a8a7"));
      list.add(new UciMoveTest("Qd7", "d8d7"));
      list.add(new UciMoveTest("Qe2", "d1e2"));
      list.add(new UciMoveTest("Kd8", "e8d8"));
      list.add(new UciMoveTest("Kd1", "e1d1"));
      list.add(new UciMoveTest("h6", "h7h6"));
      list.add(new UciMoveTest("Nc3", "b1c3"));
      list.add(new UciMoveTest("h5", "h6h5"));
      list.add(new UciMoveTest("a4", "a2a4"));
      list.add(new UciMoveTest("h4", "h5h4"));
      list.add(new UciMoveTest("Ra3", "a1a3"));
      list.add(new UciMoveTest("h3", "h4h3"));
      list.add(new UciMoveTest("g3", "g2g3"));
      list.add(new UciMoveTest("Ng4", "f6g4"));
      list.add(new UciMoveTest("Bg2", "f1g2"));

      check(list);
    }

    // here we check castling
    {
      final List<UciMoveTest> list = new ArrayList<>();

      list.add(new UciMoveTest("e4", "e2e4"));
      list.add(new UciMoveTest("d5", "d7d5"));
      list.add(new UciMoveTest("Nf3", "g1f3"));
      list.add(new UciMoveTest("Nc6", "b8c6"));
      list.add(new UciMoveTest("Bc4", "f1c4"));
      list.add(new UciMoveTest("Bf5", "c8f5"));
      list.add(new UciMoveTest("O-O", "e1g1"));
      list.add(new UciMoveTest("Qd7", "d8d7"));
      list.add(new UciMoveTest("Re1", "f1e1"));
      list.add(new UciMoveTest("O-O-O", "e8c8"));
      list.add(new UciMoveTest("Bxd5", "c4d5"));
      list.add(new UciMoveTest("Kb8", "c8b8"));
      list.add(new UciMoveTest("Bb3", "d5b3"));
      list.add(new UciMoveTest("Qxd2", "d7d2"));

      check(list);
    }
  }

  private static void check(List<UciMoveTest> list) {
    checkMoveSpecificationToUci(list);
    checkUciMoveToMoveSpecification(list);
    checkUciMoveToSan(list);
  }

  private static void checkMoveSpecificationToUci(List<UciMoveTest> list) {
    final Board board = new Board();

    for (final UciMoveTest test : list) {
      board.moveStrict(test.san());
      final LegalMove lastMove = board.getLastMove();
      final MoveSpecification moveSpecification = lastMove.moveSpecification();

      final String actualUci = UciMoveUtility.convertMoveSpecificationToUci(lastMove.havingMove(), moveSpecification)
          .text();
      assertEquals(test.uciMoveStr(), actualUci);

      final String actualUciForScala = convertMoveSpecificationToUciForScala(lastMove.havingMove(), moveSpecification);
      if (CastlingUtility.calculateIsCastlingMove(moveSpecification)) {
        // O-O or O-O-O as provided in the san is expected
        assertEquals(test.san(), actualUciForScala);
      } else {
        assertEquals(test.uciMoveStr(), actualUciForScala);
      }
    }
  }

  private static void checkUciMoveToMoveSpecification(List<UciMoveTest> list) {
    final Board board = new Board();

    for (final UciMoveTest test : list) {
      final UciMove moveModel = UciMoveValidationUtility.lookup(test.uciMoveStr());
      final MoveSpecification moveSpecificationActual = UciMoveUtility.convertUciMoveToMoveSpecification(board,
          moveModel);
      board.moveStrict(test.san());

      final MoveSpecification moveSpecificationExpected = board.getLastMove().moveSpecification();
      assertEquals(moveSpecificationExpected, moveSpecificationActual);
    }
  }

  private static void checkUciMoveToSan(List<UciMoveTest> list) {
    final var board = new Board();

    for (final UciMoveTest test : list) {
      final UciMove uciMove = UciMoveValidationUtility.lookup(test.uciMoveStr());
      final String san = UciMoveUtility.convertUciMoveToSan(board, uciMove);
      assertEquals(test.san(), san);
      board.moveStrict(test.san());
    }
  }

  private static String convertMoveSpecificationToUciForScala(Side havingMove, MoveSpecification moveSpecification) {
    if (CastlingUtility.calculateIsCastlingMove(moveSpecification)) {
      return switch (moveSpecification.castlingMove()) {
        case KING_SIDE -> CastlingConstants.SAN_CASTLING_KING_SIDE;
        case QUEEN_SIDE -> CastlingConstants.SAN_CASTLING_QUEEN_SIDE;
        case NONE -> throw new IllegalArgumentException();
        default -> throw new IllegalArgumentException();
      };
    }

    return UciMoveUtility.convertMoveSpecificationToUci(havingMove, moveSpecification).text();
  }
}
