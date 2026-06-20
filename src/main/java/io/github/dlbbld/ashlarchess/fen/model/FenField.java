// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.fen.model;

public record FenField(String piecePlacement, String sideToMove, String castlingRightBoth,
    String enPassantCaptureTargetSquare, String halfMoveClock, String fullMoveNumber) {

}
