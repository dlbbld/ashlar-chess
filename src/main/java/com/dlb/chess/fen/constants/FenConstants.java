package com.dlb.chess.fen.constants;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.board.enums.CastlingRight;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.fen.model.Fen;
import com.google.common.collect.ImmutableList;

public class FenConstants implements EnumConstants {

  public static final String FEN_INITIAL_STR = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

  public static final String FEN_AFTER_E4_STR = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";

  static final String PIECE_PLACEMENT_INITIAL = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";

  static final String PIECE_PLACEMENT_AFTER_E4 = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR";

  static final String PIECE_PLACEMENT_AFTER_E4_E5 = "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR";

  // we set the max full move counter so that the halfmove clock still fits in the int range
  public static final int MAX_FULL_MOVE_NUMBER = (Integer.MAX_VALUE - 1) / 2;

  public static final Fen FEN_INITIAL = new Fen(FEN_INITIAL_STR, BitboardPosition.INITIAL_POSITION, WHITE,
      CastlingRight.KING_AND_QUEEN_SIDE, CastlingRight.KING_AND_QUEEN_SIDE, Square.NONE, 0, 1);

  public static final ImmutableList<String> POSSIBLE_FEN_AFTER_FIRST_HALF_MOVE;

  static {
    @SuppressWarnings("null") final @NonNull List<String> fenAfterFirstHalfMoveList = Arrays.asList(
        "rnbqkbnr/pppppppp/8/8/8/P7/1PPPPPPP/RNBQKBNR b KQkq - 0 1",
        "rnbqkbnr/pppppppp/8/8/8/1P6/P1PPPPPP/RNBQKBNR b KQkq - 0 1",
        "rnbqkbnr/pppppppp/8/8/8/2P5/PP1PPPPP/RNBQKBNR b KQkq - 0 1",
        "rnbqkbnr/pppppppp/8/8/8/3P4/PPP1PPPP/RNBQKBNR b KQkq - 0 1",
        "rnbqkbnr/pppppppp/8/8/8/4P3/PPPP1PPP/RNBQKBNR b KQkq - 0 1",
        "rnbqkbnr/pppppppp/8/8/8/5P2/PPPPP1PP/RNBQKBNR b KQkq - 0 1",
        "rnbqkbnr/pppppppp/8/8/8/6P1/PPPPPP1P/RNBQKBNR b KQkq - 0 1",
        "rnbqkbnr/pppppppp/8/8/8/7P/PPPPPPP1/RNBQKBNR b KQkq - 0 1",
        "rnbqkbnr/pppppppp/8/8/P7/8/1PPPPPPP/RNBQKBNR b KQkq a3 0 1",
        "rnbqkbnr/pppppppp/8/8/1P6/8/P1PPPPPP/RNBQKBNR b KQkq b3 0 1",
        "rnbqkbnr/pppppppp/8/8/2P5/8/PP1PPPPP/RNBQKBNR b KQkq c3 0 1",
        "rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq d3 0 1", FEN_AFTER_E4_STR,
        "rnbqkbnr/pppppppp/8/8/5P2/8/PPPPP1PP/RNBQKBNR b KQkq f3 0 1",
        "rnbqkbnr/pppppppp/8/8/6P1/8/PPPPPP1P/RNBQKBNR b KQkq g3 0 1",
        "rnbqkbnr/pppppppp/8/8/7P/8/PPPPPPP1/RNBQKBNR b KQkq h3 0 1",
        "rnbqkbnr/pppppppp/8/8/8/N7/PPPPPPPP/R1BQKBNR b KQkq - 1 1",
        "rnbqkbnr/pppppppp/8/8/8/2N5/PPPPPPPP/R1BQKBNR b KQkq - 1 1",
        "rnbqkbnr/pppppppp/8/8/8/5N2/PPPPPPPP/RNBQKB1R b KQkq - 1 1",
        "rnbqkbnr/pppppppp/8/8/8/7N/PPPPPPPP/RNBQKB1R b KQkq - 1 1");

    POSSIBLE_FEN_AFTER_FIRST_HALF_MOVE = Nulls.copyOfList(fenAfterFirstHalfMoveList);
  }

}
