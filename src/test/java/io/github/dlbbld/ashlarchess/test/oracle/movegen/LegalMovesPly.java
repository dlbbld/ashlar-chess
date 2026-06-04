// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.oracle.movegen;

import java.util.List;

/**
 * One ply's legal-move snapshot in a {@link LegalMovesRecord}. UCI list is sorted alphabetically.
 */
public record LegalMovesPly(List<String> legalMovesUci) {
}
