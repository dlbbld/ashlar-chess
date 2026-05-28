// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser.model;

import java.util.List;

public record PgnSan(String startingFen, List<String> sanList) {

}
