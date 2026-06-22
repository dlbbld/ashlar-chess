// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.pgn;

import io.github.dlbbld.ashlarchess.pgn.PgnCommentary;

public record PgnMove(String san, MoveSuffixAnnotation moveSuffixAnnotation, PgnCommentary commentary) {

}
