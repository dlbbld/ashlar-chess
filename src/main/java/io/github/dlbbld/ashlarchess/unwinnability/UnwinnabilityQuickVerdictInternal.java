// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

/**
 * Internal three-valued verdict of the quick unwinnability search, including the {@code WINNABLE} state the search can
 * in principle establish. The public {@link UnwinnabilityQuickVerdict} drops {@code WINNABLE} (collapsing it to
 * {@code POSSIBLY_WINNABLE}): the quick analysis does not advertise winnability on its API surface.
 */
enum UnwinnabilityQuickVerdictInternal {
  WINNABLE,
  UNWINNABLE,
  POSSIBLY_WINNABLE
}
