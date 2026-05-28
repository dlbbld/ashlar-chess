package io.github.dlbbld.ashlarchess.squares;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.board.enums.Square;

interface OrthogonalRange {

  ImmutableList<Square> squareListNorth();

  ImmutableList<Square> squareListEast();

  ImmutableList<Square> squareListSouth();

  ImmutableList<Square> squareListWest();
}
