// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

/**
 * The library's flagship feature - a Java port of Miguel Ambrona's
 * <a href="https://github.com/miguel-ambrona/D3-Chess">Chess Unwinnability Analyzer (CHA)</a> (GPL v3). Decides whether
 * a position is <em>unwinnable for a side</em> - no legal sequence can end with that side giving checkmate, even if the
 * opponent cooperates - and the symmetric notion of a <em>dead position</em> (unwinnable for both sides).
 *
 * <p>
 * Insufficient material covers the trivial cases (king-vs-king, king + minor vs king); positions like blocked pawn
 * walls, certain wrong-bishop endgames, and many forced-only-moves continuations are dead but <em>not</em> insufficient
 * - and most chess libraries get them wrong. CHA decides them correctly across the full range of positions.
 *
 * <h2>Two variants</h2>
 *
 * <ul>
 * <li><strong>Quick</strong> ({@link io.github.dlbbld.ashlarchess.unwinnability.UnwinnableQuickAnalyzer}) -
 * microsecond-scale, structural, two-valued: {@code UNWINNABLE} or {@code POSSIBLY_WINNABLE}. It is sound but not
 * complete - it proves unwinnability or leaves it open, and never claims winnability.</li>
 * <li><strong>Full</strong> ({@link io.github.dlbbld.ashlarchess.unwinnability.UnwinnableFullAnalyzer}) - deep search,
 * four-valued: {@code WINNABLE_HELPMATE} (a concrete mate line was found), {@code WINNABLE_BY_THEOREM} (winnability
 * certified by the basic-checkmate-reachability theorem, no line), {@code UNWINNABLE}, or {@code UNDETERMINED}. The
 * undetermined case is bounded by a 500&nbsp;000-position limit; most positions resolve well below it.</li>
 * </ul>
 *
 * <p>
 * Dead-position detection is the symmetric whole-position notion: the no-side analyzer overloads
 * {@link io.github.dlbbld.ashlarchess.unwinnability.UnwinnableQuickAnalyzer#unwinnableQuick(io.github.dlbbld.ashlarchess.board.Board)}
 * and {@link io.github.dlbbld.ashlarchess.unwinnability.UnwinnableFullAnalyzer#unwinnableFull(io.github.dlbbld.ashlarchess.board.Board)}
 * reuse the same verdict enums, with {@code UNWINNABLE} meaning dead (neither side can mate).
 *
 * <h2>Analyzer entry points</h2>
 *
 * <p>
 * The analyzers run only when a caller asks for a side-specific answer or a whole-position dead-position query. No
 * analyzer is run automatically during board construction or move execution.
 *
 * <p>
 * See {@code specification.md} section 3.2 for the full design rationale.
 */
@NonNullByDefault
package io.github.dlbbld.ashlarchess.unwinnability;

import org.eclipse.jdt.annotation.NonNullByDefault;
