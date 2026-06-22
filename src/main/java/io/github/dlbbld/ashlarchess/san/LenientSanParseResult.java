// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.san;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.board.MoveSpecification;

/**
 * Outcome of a successful lenient SAN parse: the resolved move, plus the list of deviations the parser forgave to get
 * there. {@code forgivenItems} is empty when the input was already canonical SAN.
 */
@SuppressWarnings("null")
public record LenientSanParseResult(@NonNull MoveSpecification moveSpecification,
    @NonNull List<@NonNull ForgivenSanItem> forgivenItems) {

  public LenientSanParseResult {
    forgivenItems = Nulls.copyOfList(forgivenItems);
  }
}
