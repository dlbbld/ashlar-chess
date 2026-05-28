// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.common;

import java.util.Comparator;
import java.util.List;

import io.github.dlbbld.ashlarchess.common.model.HalfMove;

public class HalfMoveListListComparator implements Comparator<List<HalfMove>> {

  public static final HalfMoveListListComparator COMPARATOR = new HalfMoveListListComparator();

  @Override
  public int compare(List<HalfMove> firstList, List<HalfMove> secondList) {

    final HalfMove firstHalfMoveFirstList = Nulls.getFirst(firstList);
    final HalfMove firstHalfMoveSecondList = Nulls.getFirst(secondList);

    return Integer.compare(firstHalfMoveFirstList.halfMoveCount(), firstHalfMoveSecondList.halfMoveCount());
  }
}
