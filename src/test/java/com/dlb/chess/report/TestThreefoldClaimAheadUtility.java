package com.dlb.chess.report;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.model.HalfMove;
import com.dlb.chess.fen.constants.FenConstants;
import com.dlb.chess.pgn.LenientPgnParser;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.pgn.PgnUtility;

class TestThreefoldClaimAheadUtility {

  @SuppressWarnings("static-method")
  @Test
  void testBasic() {

    final List<List<HalfMove>> expectedEmptyListList = new ArrayList<>();

    {
      final List<List<HalfMove>> actualListList = ThreefoldClaimAheadUtility.calculateClaimAheadListList(new Board());

      assertEquals(expectedEmptyListList, actualListList);
    }

    {
      final List<List<HalfMove>> actual = ThreefoldClaimAheadUtility
          .calculateClaimAheadListList(new Board(FenConstants.FEN_AFTER_E4_STR));

      assertEquals(expectedEmptyListList, actual);
    }

    {
      final PgnGame pgnGame = LenientPgnParser.parseText("e4 e5 Nf3 Nc6 Ng1 Nb8 Nf3");
      final Board board = PgnUtility.calculateBoard(pgnGame);

      final List<List<HalfMove>> actualListList = ThreefoldClaimAheadUtility.calculateClaimAheadListList(board);

      assertEquals(expectedEmptyListList, actualListList);
    }

    {
      final PgnGame pgnGame = LenientPgnParser.parseText("e4 e5 Nf3 Nc6 Ng1 Nb8 Nf3 Nc6");
      final Board board = PgnUtility.calculateBoard(pgnGame);

      final List<List<HalfMove>> actualListList = ThreefoldClaimAheadUtility.calculateClaimAheadListList(board);

      assertEquals(expectedEmptyListList, actualListList);
    }

    // White can claim ahead first
    {
      final PgnGame pgnGame = LenientPgnParser.parseText("e4 e5 Nf3 Nc6 Ng1 Nb8 Nf3 Nc6 Ng5 Nb8");
      final Board board = PgnUtility.calculateBoard(pgnGame);

      final List<List<HalfMove>> actualListList = ThreefoldClaimAheadUtility.calculateClaimAheadListList(board);

      final List<HalfMove> HalfMoveList = new ArrayList<>();

      board.moveStrict("Nf3");
      addLastMove(board, HalfMoveList);
      board.unmove();

      final List<List<HalfMove>> expectedListList = new ArrayList<>();
      expectedListList.add(HalfMoveList);

      assertEquals(expectedListList, actualListList);
    }

    // Black can claim ahead first
    {
      final PgnGame pgnGame = LenientPgnParser.parseText("e4 e5 Nf3 Nc6 Ng1 Nb8 Nf3 Nc6 Ng1");
      final Board board = PgnUtility.calculateBoard(pgnGame);

      final List<List<HalfMove>> actualListList = ThreefoldClaimAheadUtility.calculateClaimAheadListList(board);

      final List<HalfMove> HalfMoveList = new ArrayList<>();

      board.moveStrict("Nb8");
      addLastMove(board, HalfMoveList);
      board.unmove();

      final List<List<HalfMove>> expectedListList = new ArrayList<>();
      expectedListList.add(HalfMoveList);

      assertEquals(expectedListList, actualListList);
    }

    // White can claim ahead first, then Black can claim ahead
    {
      final PgnGame pgnGame = LenientPgnParser.parseText("e4 e5 Nf3 Nc6 Ng1 Nb8 Nf3 Nc6 Ng1 Nb8");
      final Board board = PgnUtility.calculateBoard(pgnGame);

      final List<List<HalfMove>> actualListList = ThreefoldClaimAheadUtility.calculateClaimAheadListList(board);

      final List<List<HalfMove>> expectedListList = new ArrayList<>();

      {
        final List<HalfMove> HalfMoveList = new ArrayList<>();
        addLastMove(board, HalfMoveList);
        expectedListList.add(HalfMoveList);
      }

      {
        board.moveStrict("Nf3");
        final List<HalfMove> HalfMoveList = new ArrayList<>();
        addLastMove(board, HalfMoveList);
        board.unmove();

        expectedListList.add(HalfMoveList);
      }

      assertEquals(expectedListList, actualListList);
    }

    // Black can claim ahead first, then White can claim ahead
    {
      final PgnGame pgnGame = LenientPgnParser.parseText("e4 e5 Nf3 Nc6 Ng1 Nb8 Nf3 Nc6 Ng5 Nb8 Nf3");
      final Board board = PgnUtility.calculateBoard(pgnGame);

      final List<List<HalfMove>> actualListList = ThreefoldClaimAheadUtility.calculateClaimAheadListList(board);

      final List<List<HalfMove>> expectedListList = new ArrayList<>();

      {
        final List<HalfMove> HalfMoveList = new ArrayList<>();
        addLastMove(board, HalfMoveList);
        expectedListList.add(HalfMoveList);
      }

      {
        board.moveStrict("Nc6");
        final List<HalfMove> HalfMoveList = new ArrayList<>();
        addLastMove(board, HalfMoveList);
        board.unmove();
        expectedListList.add(HalfMoveList);
      }

      assertEquals(expectedListList, actualListList);
    }

  }

  private static void addLastMove(Board board, List<HalfMove> halfMoveList) {
    halfMoveList.add(Nulls.getLast(board.getHalfMoveList()));
  }
}
