// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.calculate;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.FILE_NONE;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.RANK_NONE;

import java.util.Map;
import java.util.TreeMap;

import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceType;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.internal.CastlingConstants;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.san.internal.SanConversion;
import io.github.dlbbld.ashlarchess.san.internal.SanFormat;
import io.github.dlbbld.ashlarchess.san.internal.SanParse;
import io.github.dlbbld.ashlarchess.san.internal.SanSymbol;
import io.github.dlbbld.ashlarchess.san.internal.SanTerminalMarker;

public class KingCastlingSanValidateStaticallyStrictCalculate {

  public static Map<String, SanParse> calculateSanMap() {

    final Map<String, SanParse> sanCastlingMap = new TreeMap<>();

    initializeKingSide(sanCastlingMap);
    initializeQueenSide(sanCastlingMap);

    return Nulls.copyOfMap(sanCastlingMap);
  }

  private static void initializeKingSide(Map<String, SanParse> sanCastlingMap) {
    initializeKingSideNoCheck(sanCastlingMap);
    initializeKingSideCheckmate(sanCastlingMap);
    initializeKingSideCheck(sanCastlingMap);
  }

  private static void initializeKingSideNoCheck(Map<String, SanParse> sanCastlingMap) {
    final String san = CastlingConstants.SAN_CASTLING_KING_SIDE;
    final SanParse model = new SanParse(SanFormat.KING_CASTLING_KING_SIDE, new SanConversion(PieceType.NONE, FILE_NONE,
        RANK_NONE, Square.NONE, PromotionPieceType.NONE, SanTerminalMarker.NONE));
    sanCastlingMap.put(san, model);

  }

  private static void initializeKingSideCheckmate(Map<String, SanParse> sanCastlingMap) {
    final String san = CastlingConstants.SAN_CASTLING_KING_SIDE + SanSymbol.CHECKMATE.getSymbol();
    final SanParse model = new SanParse(SanFormat.KING_CASTLING_KING_SIDE, new SanConversion(PieceType.NONE, FILE_NONE,
        RANK_NONE, Square.NONE, PromotionPieceType.NONE, SanTerminalMarker.CHECKMATE));
    sanCastlingMap.put(san, model);

  }

  private static void initializeKingSideCheck(Map<String, SanParse> sanCastlingMap) {
    final String san = CastlingConstants.SAN_CASTLING_KING_SIDE + SanSymbol.CHECK.getSymbol();
    final SanParse model = new SanParse(SanFormat.KING_CASTLING_KING_SIDE, new SanConversion(PieceType.NONE, FILE_NONE,
        RANK_NONE, Square.NONE, PromotionPieceType.NONE, SanTerminalMarker.CHECK));
    sanCastlingMap.put(san, model);

  }

  private static void initializeQueenSide(Map<String, SanParse> sanCastlingMap) {
    initializeQueenSideNoCheck(sanCastlingMap);
    initializeQueenSideCheckmate(sanCastlingMap);
    initializeQueenSideCheck(sanCastlingMap);
  }

  private static void initializeQueenSideNoCheck(Map<String, SanParse> sanCastlingMap) {
    final String san = CastlingConstants.SAN_CASTLING_QUEEN_SIDE;
    final SanParse model = new SanParse(SanFormat.KING_CASTLING_QUEEN_SIDE, new SanConversion(PieceType.NONE, FILE_NONE,
        RANK_NONE, Square.NONE, PromotionPieceType.NONE, SanTerminalMarker.NONE));
    sanCastlingMap.put(san, model);

  }

  private static void initializeQueenSideCheckmate(Map<String, SanParse> sanCastlingMap) {
    final String san = CastlingConstants.SAN_CASTLING_QUEEN_SIDE + SanSymbol.CHECKMATE.getSymbol();
    final SanParse model = new SanParse(SanFormat.KING_CASTLING_QUEEN_SIDE, new SanConversion(PieceType.NONE, FILE_NONE,
        RANK_NONE, Square.NONE, PromotionPieceType.NONE, SanTerminalMarker.CHECKMATE));
    sanCastlingMap.put(san, model);
  }

  private static void initializeQueenSideCheck(Map<String, SanParse> sanCastlingMap) {
    final String san = CastlingConstants.SAN_CASTLING_QUEEN_SIDE + SanSymbol.CHECK.getSymbol();
    final SanParse model = new SanParse(SanFormat.KING_CASTLING_QUEEN_SIDE, new SanConversion(PieceType.NONE, FILE_NONE,
        RANK_NONE, Square.NONE, PromotionPieceType.NONE, SanTerminalMarker.CHECK));
    sanCastlingMap.put(san, model);
  }
}
