// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board.enums;

public enum CastlingRightLoss {
  NOT_LOST,
  KING_MOVED,
  ROOK_MOVED,
  ROOK_CAPTURED,
  CASTLED,
  UNKNOWN_FEN_IMPORT;
}
