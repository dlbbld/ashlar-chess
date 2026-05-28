package io.github.dlbbld.ashlarchess.squares;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.board.enums.Square;

interface DiagonalRange {
  ImmutableList<Square> squareListNorthEast();

  ImmutableList<Square> squareListSouthEast();

  ImmutableList<Square> squareListSouthWest();

  ImmutableList<Square> squareListNorthWest();
}
