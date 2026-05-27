/**
 * Game-level reports about a {@link com.dlb.chess.board.Board}: threefold-repetition listings (including the
 * missed-claim-ahead opportunities other libraries don't surface) and no-progress (50/75-move-rule) sequences, rendered
 * as a human-readable summary to {@code stdout} via {@link com.dlb.chess.report.Reporter}.
 *
 * <p>
 * Distinguishes the on-board predicates ("threefold has occurred") from the with-move predicates ("some legal move
 * would create a threefold position the side could claim before playing it") — the latter is the missed-claim feature.
 *
 * <p>
 * Internally, analysis records carry the facts and print classes format them. The records are package-private; only
 * {@code Reporter} is part of the public surface.
 */
@NonNullByDefault
package com.dlb.chess.report;

import org.eclipse.jdt.annotation.NonNullByDefault;
