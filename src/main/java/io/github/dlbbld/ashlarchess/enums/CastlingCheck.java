// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.enums;

// Outcomes of the castling-specific check performed in CastlingUtility.
// FINAL means the castling right is permanently lost; TEMPORARY means the
// board state blocks castling now but castling may become possible again.
// The cases are overlapping so they are checked in the order listed.
public enum CastlingCheck {
  SUCCESS,
  FINAL_NO_RIGHT,
  TEMPORARY_SQUARES_NOT_EMPTY,
  TEMPORARY_KING_IN_CHECK,
  TEMPORARY_KING_TRAVELS_THROUGH_CHECK,
  TEMPORARY_KING_ENDS_IN_CHECK;
}
