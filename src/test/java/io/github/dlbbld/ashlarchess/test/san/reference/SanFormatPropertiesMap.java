package io.github.dlbbld.ashlarchess.test.san.reference;

import java.util.EnumMap;

import io.github.dlbbld.ashlarchess.san.SanFormat;

public class SanFormatPropertiesMap {

  // length, movingPieceTypeIndex, fromFileIndex, fromRankIndex, captureSymbolIndex, toFileIndex, toRankIndex,
  // promotionSymbolIndex, promotionPieceTypeIndex, isPawn

  public static final EnumMap<SanFormat, SanFormatProperties> MAP;

  static {
    MAP = new EnumMap<>(SanFormat.class);

    // d3
    MAP.put(SanFormat.PAWN_NON_CAPTURING_NON_PROMOTION, new SanFormatProperties(2, -1, -1, -1, -1, 0, 1, -1, -1, true));
    // dxe5
    MAP.put(SanFormat.PAWN_CAPTURING_NON_PROMOTION, new SanFormatProperties(4, -1, 0, -1, 1, 2, 3, -1, -1, true));
    // d8=Q
    MAP.put(SanFormat.PAWN_NON_CAPTURING_PROMOTION, new SanFormatProperties(4, -1, -1, -1, -1, 0, 1, 2, 3, true));
    // dxe8=Q
    MAP.put(SanFormat.PAWN_CAPTURING_PROMOTION, new SanFormatProperties(6, -1, 0, -1, 1, 2, 3, 4, 5, true));
    // Qe5
    MAP.put(SanFormat.RNBQ_NON_CAPTURING_NEITHER, new SanFormatProperties(3, 0, -1, -1, -1, 1, 2, -1, -1, false));
    // Qae5
    MAP.put(SanFormat.RNBQ_NON_CAPTURING_FILE, new SanFormatProperties(4, 0, 1, -1, -1, 2, 3, -1, -1, false));
    // Q2e5
    MAP.put(SanFormat.RNBQ_NON_CAPTURING_RANK, new SanFormatProperties(4, 0, -1, 1, -1, 2, 3, -1, -1, false));
    // Qc3e5
    MAP.put(SanFormat.RNBQ_NON_CAPTURING_SQUARE, new SanFormatProperties(5, 0, 1, 2, -1, 3, 4, -1, -1, false));
    // Qxe5
    MAP.put(SanFormat.RNBQ_CAPTURING_NEITHER, new SanFormatProperties(4, 0, -1, -1, 1, 2, 3, -1, -1, false));
    // Qaxe5
    MAP.put(SanFormat.RNBQ_CAPTURING_FILE, new SanFormatProperties(5, 0, 1, -1, 2, 3, 4, -1, -1, false));
    // Q2xe5
    MAP.put(SanFormat.RNBQ_CAPTURING_RANK, new SanFormatProperties(5, 0, -1, 1, 2, 3, 4, -1, -1, false));
    // Qc3xe5
    MAP.put(SanFormat.RNBQ_CAPTURING_SQUARE, new SanFormatProperties(6, 0, 1, 2, 3, 4, 5, -1, -1, false));
    // Ke5
    MAP.put(SanFormat.KING_NON_CASTLING_NON_CAPTURING, new SanFormatProperties(3, 0, -1, -1, -1, 1, 2, -1, -1, false));
    // Kxe5
    MAP.put(SanFormat.KING_NON_CASTLING_CAPTURING, new SanFormatProperties(4, 0, -1, -1, 1, 2, 3, -1, -1, false));
    // O-O-O
    MAP.put(SanFormat.KING_CASTLING_QUEEN_SIDE, new SanFormatProperties(5, -1, -1, -1, -1, -1, -1, -1, -1, false));
    // O-O
    MAP.put(SanFormat.KING_CASTLING_KING_SIDE, new SanFormatProperties(3, -1, -1, -1, -1, -1, -1, -1, -1, false));
  }

  private SanFormatPropertiesMap() {
  }

}
