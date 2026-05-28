package com.dlb.chess.common.ucimove.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.PromotionPieceType;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.model.EmptyBoardMove;
import com.dlb.chess.model.UciMove;
import com.dlb.chess.squares.AbstractEmptyBoardSquares;
import com.dlb.chess.squares.PawnDiagonalSquares;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public abstract class UciMoveValidationUtility implements EnumConstants {

  private static final ImmutableList<UciMove> UCI_MOVE_LIST;
  private static final ImmutableMap<String, UciMove> UCI_MOVE_TEXT_LOOKUP;

  static {
    final List<UciMove> uciMoveList = new ArrayList<>();
    final Map<String, UciMove> uciMoveTextLookup = new TreeMap<>();

    // Non-promotion moves: every square x {rook, bishop, knight} empty-board reach
    for (final Square fromSquare : Square.REAL) {
      for (final PieceType pieceType : List.of(ROOK, BISHOP, KNIGHT)) {
        final Set<EmptyBoardMove> moveSet = AbstractEmptyBoardSquares.calculateNonPawnEmptyBoardMoves(pieceType,
            fromSquare);
        for (final EmptyBoardMove move : moveSet) {
          addUciMove(uciMoveList, uciMoveTextLookup, move.fromSquare(), move.toSquare(), PromotionPieceType.NONE);
        }
      }
    }

    // Promotion moves: white from seventh rank, black from second rank
    addPromotionMoves(uciMoveList, uciMoveTextLookup, Side.WHITE);
    addPromotionMoves(uciMoveList, uciMoveTextLookup, Side.BLACK);

    UCI_MOVE_LIST = Nulls.copyOfList(uciMoveList);
    UCI_MOVE_TEXT_LOOKUP = Nulls.copyOfMap(uciMoveTextLookup);
  }

  static boolean exists(String text) {
    return UCI_MOVE_TEXT_LOOKUP.containsKey(text);
  }

  public static UciMove lookup(String uciMoveStr) {
    if (!exists(uciMoveStr)) {
      throw new IllegalArgumentException("No such UCI move exists");
    }
    return Nulls.get(UCI_MOVE_TEXT_LOOKUP, uciMoveStr);
  }

  public static List<UciMove> getUciMoveList() {
    return UCI_MOVE_LIST;
  }

  private static void addPromotionMoves(List<UciMove> uciMoveList, Map<String, UciMove> uciMoveTextLookup, Side side) {
    for (final Square fromSquare : getRankBeforePromotionRank(side)) {
      final Set<Square> toSquareSet = new TreeSet<>();
      for (final EmptyBoardMove move : AbstractEmptyBoardSquares.calculatePawnEmptyBoardMoves(side, fromSquare)) {
        toSquareSet.add(move.toSquare());
      }
      toSquareSet.addAll(PawnDiagonalSquares.getPawnDiagonalSquares(side, fromSquare));

      for (final Square toSquare : toSquareSet) {
        for (final PromotionPieceType promotionPieceType : PromotionPieceType.REAL) {
          addUciMove(uciMoveList, uciMoveTextLookup, fromSquare, toSquare, promotionPieceType);
        }
      }
    }
  }

  private static void addUciMove(List<UciMove> uciMoveList, Map<String, UciMove> uciMoveTextLookup, Square fromSquare,
      Square toSquare, PromotionPieceType promotionPieceType) {
    final String text = calculateUciMoveStr(fromSquare, toSquare, promotionPieceType);
    final boolean isPromotion = promotionPieceType != PromotionPieceType.NONE;
    final UciMove uciMove = new UciMove(fromSquare, toSquare, text, isPromotion, promotionPieceType);
    uciMoveList.add(uciMove);
    uciMoveTextLookup.put(text, uciMove);
  }

  private static List<Square> getRankBeforePromotionRank(Side side) {
    return switch (side) {
      case WHITE -> Square.SEVENTH_RANK;
      case BLACK -> Square.SECOND_RANK;
      case NONE -> throw new IllegalArgumentException();
    };
  }

  static String calculateUciMoveStr(Square fromSquare, Square toSquare, PromotionPieceType promotionPieceType) {
    final StringBuilder uciMove = new StringBuilder();
    uciMove.append(fromSquare.getName());
    uciMove.append(toSquare.getName());
    if (promotionPieceType != PromotionPieceType.NONE) {
      final var promotionPieceTypeLetterLowerCase = Character
          .toLowerCase(promotionPieceType.getPieceType().getLetter());
      uciMove.append(promotionPieceTypeLetterLowerCase);
    }
    return Nulls.toString(uciMove);
  }
}
