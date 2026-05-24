package com.dlb.chess.report;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.model.ClaimAhead;
import com.dlb.chess.fen.constants.FenConstants;
import com.dlb.chess.model.LegalMove;
import com.dlb.chess.pgn.LenientPgnParser;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.pgn.PgnUtility;

class TestThreefoldClaimAheadUtility {

  @SuppressWarnings("static-method")
  @Test
  void testBasic() {

    final List<List<ClaimAhead>> expectedEmptyListList = new ArrayList<>();

    {
      final List<List<ClaimAhead>> actualListList = ThreefoldClaimAheadUtility
          .calculateThreefoldClaimAhead(new Board(false));

      assertEquals(expectedEmptyListList, actualListList);
    }

    {
      final List<List<ClaimAhead>> actual = ThreefoldClaimAheadUtility
          .calculateThreefoldClaimAhead(new Board(FenConstants.FEN_AFTER_E4_STR, false));

      assertEquals(expectedEmptyListList, actual);
    }

    {
      final PgnGame pgnGame = LenientPgnParser.parseText("e4 e5 Nf3 Nc6 Ng1 Nb8 Nf3");
      final Board board = PgnUtility.calculateBoard(pgnGame, false);

      final List<List<ClaimAhead>> actualListList = ThreefoldClaimAheadUtility.calculateThreefoldClaimAhead(board);

      assertEquals(expectedEmptyListList, actualListList);
    }

    {
      final PgnGame pgnGame = LenientPgnParser.parseText("e4 e5 Nf3 Nc6 Ng1 Nb8 Nf3 Nc6");
      final Board board = PgnUtility.calculateBoard(pgnGame, false);

      final List<List<ClaimAhead>> actualListList = ThreefoldClaimAheadUtility.calculateThreefoldClaimAhead(board);

      assertEquals(expectedEmptyListList, actualListList);
    }

    // White can claim ahead first
    {
      final PgnGame pgnGame = LenientPgnParser.parseText("e4 e5 Nf3 Nc6 Ng1 Nb8 Nf3 Nc6 Ng5 Nb8");
      final Board board = PgnUtility.calculateBoard(pgnGame, false);

      final List<List<ClaimAhead>> actualListList = ThreefoldClaimAheadUtility.calculateThreefoldClaimAhead(board);

      final List<ClaimAhead> claimAheadList = new ArrayList<>();

      board.moveStrict("Nf3");
      addLastMove(board, claimAheadList);
      board.unmove();

      final List<List<ClaimAhead>> expectedListList = new ArrayList<>();
      expectedListList.add(claimAheadList);

      assertEquals(expectedListList, actualListList);
    }

    // Black can claim ahead first
    {
      final PgnGame pgnGame = LenientPgnParser.parseText("e4 e5 Nf3 Nc6 Ng1 Nb8 Nf3 Nc6 Ng1");
      final Board board = PgnUtility.calculateBoard(pgnGame, false);

      final List<List<ClaimAhead>> actualListList = ThreefoldClaimAheadUtility.calculateThreefoldClaimAhead(board);

      final List<ClaimAhead> claimAheadList = new ArrayList<>();

      board.moveStrict("Nb8");
      addLastMove(board, claimAheadList);
      board.unmove();

      final List<List<ClaimAhead>> expectedListList = new ArrayList<>();
      expectedListList.add(claimAheadList);

      assertEquals(expectedListList, actualListList);
    }

    // White can claim ahead first, then Black can claim ahead
    {
      final PgnGame pgnGame = LenientPgnParser.parseText("e4 e5 Nf3 Nc6 Ng1 Nb8 Nf3 Nc6 Ng1 Nb8");
      final Board board = PgnUtility.calculateBoard(pgnGame, false);

      final List<List<ClaimAhead>> actualListList = ThreefoldClaimAheadUtility.calculateThreefoldClaimAhead(board);

      final List<List<ClaimAhead>> expectedListList = new ArrayList<>();

      {
        final List<ClaimAhead> claimAheadList = new ArrayList<>();
        addLastMove(board, claimAheadList);
        expectedListList.add(claimAheadList);
      }

      {
        board.moveStrict("Nf3");
        final List<ClaimAhead> claimAheadList = new ArrayList<>();
        addLastMove(board, claimAheadList);
        board.unmove();

        expectedListList.add(claimAheadList);
      }

      assertEquals(expectedListList, actualListList);
    }

    // Black can claim ahead first, then White can claim ahead
    {
      final PgnGame pgnGame = LenientPgnParser.parseText("e4 e5 Nf3 Nc6 Ng1 Nb8 Nf3 Nc6 Ng5 Nb8 Nf3");
      final Board board = PgnUtility.calculateBoard(pgnGame, false);

      final List<List<ClaimAhead>> actualListList = ThreefoldClaimAheadUtility.calculateThreefoldClaimAhead(board);

      final List<List<ClaimAhead>> expectedListList = new ArrayList<>();

      {
        final List<ClaimAhead> claimAheadList = new ArrayList<>();
        addLastMove(board, claimAheadList);
        expectedListList.add(claimAheadList);
      }

      {
        board.moveStrict("Nc6");
        final List<ClaimAhead> claimAheadList = new ArrayList<>();
        addLastMove(board, claimAheadList);
        board.unmove();
        expectedListList.add(claimAheadList);
      }

      assertEquals(expectedListList, actualListList);
    }

  }

  private static void addLastMove(Board board, List<ClaimAhead> claimAheadList) {
    final LegalMove legalMove = board.getLastMove();
    final var fullMoveNumber = board.getLastPlayedFullMoveNumber();
    final String san = board.getSan();
    claimAheadList.add(new ClaimAhead(legalMove, fullMoveNumber, san));
  }
}
