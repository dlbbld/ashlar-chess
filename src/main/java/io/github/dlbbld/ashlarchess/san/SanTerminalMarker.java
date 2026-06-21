// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.san;

// Value enum: the terminal state of a move's SAN/LAN text. Behaviour (the (check, checkmate) factory and the
// SAN-symbol rendering) lives in SanTerminalMarkerUtility.
public enum SanTerminalMarker {

  NONE,
  CHECK,
  CHECKMATE;
}
