// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.squares;

import java.util.List;

import io.github.dlbbld.ashlarchess.board.enums.Square;

record RookRange(List<Square> northSquares, List<Square> eastSquares,
    List<Square> southSquares, List<Square> westSquares) implements OrthogonalRange {

}
