// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.common.ucimove.utility;

import static io.github.dlbbld.ashlarchess.board.enums.PieceType.BISHOP;
import static io.github.dlbbld.ashlarchess.board.enums.PieceType.KNIGHT;
import static io.github.dlbbld.ashlarchess.board.enums.PieceType.ROOK;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.model.EmptyBoardMove;
import io.github.dlbbld.ashlarchess.model.UciMove;
import io.github.dlbbld.ashlarchess.squares.EmptyBoardMoveUtility;
import io.github.dlbbld.ashlarchess.squares.PawnDiagonalSquares;

public final class UciMoveValidationUtility {

  private UciMoveValidationUtility() {
  }

  private static final ImmutableList<UciMove> UCI_MOVES;
  private static final ImmutableMap<String, UciMove> UCI_MOVE_TEXT_LOOKUP;
  private static final ImmutableList<PieceType> NON_PROMOTION_MOVE_GENERATORS = Nulls.listOf(ROOK, BISHOP, KNIGHT);

  static {
    final List<UciMove> uciMoves = new ArrayList<>();
    final Map<String, UciMove> uciMoveTextLookup = new TreeMap<>();

    // Rook, bishop, and knight geometries cover every non-promotion UCI from/to pair; queen, king, pawn, and
    // castling moves are subsets of those empty-board rays or jumps.
    for (final Square fromSquare : Square.REAL) {
      for (final PieceType pieceType : NON_PROMOTION_MOVE_GENERATORS) {
        final Set<EmptyBoardMove> moveSet = EmptyBoardMoveUtility.calculateNonPawnEmptyBoardMoves(pieceType,
            fromSquare);
        for (final EmptyBoardMove move : moveSet) {
          addUciMove(uciMoves, uciMoveTextLookup, move.fromSquare(), move.toSquare(), PromotionPieceType.NONE);
        }
      }
    }

    // Promotion moves: white from seventh rank, black from second rank
    addPromotionMoves(uciMoves, uciMoveTextLookup, Side.WHITE);
    addPromotionMoves(uciMoves, uciMoveTextLookup, Side.BLACK);

    UCI_MOVES = Nulls.copyOfList(uciMoves);
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

  public static List<UciMove> getUciMoves() {
    return UCI_MOVES;
  }

  private static void addPromotionMoves(List<UciMove> uciMoves, Map<String, UciMove> uciMoveTextLookup, Side side) {
    for (final Square fromSquare : getRankBeforePromotionRank(side)) {
      final Set<Square> toSquareSet = new TreeSet<>();
      for (final EmptyBoardMove move : EmptyBoardMoveUtility.calculatePawnEmptyBoardMoves(side, fromSquare)) {
        toSquareSet.add(move.toSquare());
      }
      toSquareSet.addAll(PawnDiagonalSquares.getPawnDiagonalSquares(side, fromSquare));

      for (final Square toSquare : toSquareSet) {
        for (final PromotionPieceType promotionPieceType : PromotionPieceType.REAL) {
          addUciMove(uciMoves, uciMoveTextLookup, fromSquare, toSquare, promotionPieceType);
        }
      }
    }
  }

  private static void addUciMove(List<UciMove> uciMoves, Map<String, UciMove> uciMoveTextLookup, Square fromSquare,
      Square toSquare, PromotionPieceType promotionPieceType) {
    final String text = calculateUciMoveStr(fromSquare, toSquare, promotionPieceType);
    final boolean isPromotion = promotionPieceType != PromotionPieceType.NONE;
    final UciMove uciMove = new UciMove(fromSquare, toSquare, text, isPromotion, promotionPieceType);
    uciMoves.add(uciMove);
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
      final char promotionPieceTypeLetterLowerCase = Character
          .toLowerCase(promotionPieceType.getPieceType().getLetter());
      uciMove.append(promotionPieceTypeLetterLowerCase);
    }
    return Nulls.toString(uciMove);
  }
}
