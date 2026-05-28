// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.san.reference;

public record SanFormatProperties(int length, int movingPieceTypeIndex, int fromFileIndex, int fromRankIndex,
    int captureSymbolIndex, int toFileIndex, int toRankIndex, int promotionSymbolIndex, int promotionPieceTypeIndex,
    boolean isPawn) {

}
