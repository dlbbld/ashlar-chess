// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability.oracle.model;

import org.eclipse.jdt.annotation.Nullable;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.enums.Termination;
import io.github.dlbbld.ashlarchess.common.model.Outcome;

/**
 * Result of walking the unique-legal-move chain from a position. The chain terminates when one of these states is
 * reached:
 *
 * <ul>
 * <li>An automatic {@link Outcome} - {@code outcome.termination() != Termination.NONE} and
 * {@code singleSideInsufficientMaterial} is null.
 * <li>A one-sided insufficient-material diagnostic state - {@code outcome} is {@link Outcome#ONGOING} and
 * {@code singleSideInsufficientMaterial} is the side that lacks material. The chain stops here because the forced-line
 * oracle treats it as a decisive signal (the side lacking material cannot win from a forced chain).
 * <li>Branching resumes (more than one legal move available) without any termination triggering - {@code outcome} is
 * {@link Outcome#ONGOING} and {@code singleSideInsufficientMaterial} is null, signalling "ongoing" from the oracle's
 * perspective.
 * </ul>
 *
 * <p>
 * {@code outcome.termination() != Termination.NONE} and {@code singleSideInsufficientMaterial != null} are mutually
 * exclusive (never both true in a single record).
 */
public record GameForced(Outcome outcome, @Nullable Side singleSideInsufficientMaterial, int evaluatedPositions,
    Side sideMadeLastMove) {

  /**
   * Convenience: {@code true} iff an automatic termination was reached at the end of the forced chain.
   */
  public boolean hasTermination() {
    return outcome.termination() != Termination.NONE;
  }
}
