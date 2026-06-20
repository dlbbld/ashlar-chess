// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.squares;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.board.enums.Square;

record QueenRange(ImmutableList<Square> northSquares, ImmutableList<Square> eastSquares,
    ImmutableList<Square> southSquares, ImmutableList<Square> westSquares,
    ImmutableList<Square> northEastSquares, ImmutableList<Square> southEastSquares,
    ImmutableList<Square> southWestSquares, ImmutableList<Square> northWestSquares)
    implements DiagonalRange, OrthogonalRange {

}
