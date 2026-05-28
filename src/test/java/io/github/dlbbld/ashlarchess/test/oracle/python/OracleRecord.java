// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.oracle.python;

import java.util.List;

public record OracleRecord(String pgn, String startFen, List<OracleMove> moves, String finalFen) {
}
