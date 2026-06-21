// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.san;

import org.eclipse.jdt.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;

/**
 * Outcome of a successful lenient SAN parse: the resolved move, plus the list of deviations the parser forgave to get
 * there. {@code forgivenItems} is empty when the input was already canonical SAN.
 */
@SuppressWarnings("null")
public record LenientSanParseResult(@NonNull MoveSpecification moveSpecification,
    @NonNull ImmutableList<@NonNull ForgivenSanItem> forgivenItems) {

  public LenientSanParseResult {
    forgivenItems = Nulls.copyOfList(forgivenItems);
  }
}
