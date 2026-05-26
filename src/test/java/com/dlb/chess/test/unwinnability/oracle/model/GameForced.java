package com.dlb.chess.test.unwinnability.oracle.model;

import org.eclipse.jdt.annotation.Nullable;

import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.enums.Outcome;

/**
 * Result of walking the unique-legal-move chain from a position. The chain terminates when one of these states is
 * reached:
 *
 * <ul>
 * <li>An automatic {@link Outcome} — {@code outcome} is non-null and {@code singleSideInsufficientMaterial} is null.
 * <li>A one-sided insufficient-material diagnostic state — {@code outcome} is null and
 * {@code singleSideInsufficientMaterial} is the side that lacks material. The chain stops here because the
 * forced-line oracle treats it as a decisive signal (the side lacking material cannot win from a forced chain).
 * <li>Branching resumes (more than one legal move available) without any termination triggering — both fields are
 * null, signalling "ongoing" from the oracle's perspective.
 * </ul>
 *
 * <p>
 * {@code outcome} and {@code singleSideInsufficientMaterial} are mutually exclusive (never both non-null in a single
 * record).
 */
public record GameForced(@Nullable Outcome outcome, @Nullable Side singleSideInsufficientMaterial,
    int evaluatedPositions, Side sideMadeLastMove) {
}
