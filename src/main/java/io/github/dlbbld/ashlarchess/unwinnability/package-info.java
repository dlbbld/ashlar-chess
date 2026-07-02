// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

/**
 * The library's flagship feature - a Java port of Miguel Ambrona's <b>C++</b> Chess Unwinnability Analyzer (CHA),
 * originally published as D3-Chess (GPL v3). Ambrona has since replaced that repository with his Rust successor
 * <a href="https://github.com/miguel-ambrona/chasolver">chasolver</a>, which ashlar does <em>not</em> port. Decides
 * whether a position is <em>unwinnable for a side</em> - no legal sequence can end with that side giving checkmate,
 * even if the opponent cooperates - and the symmetric notion of a <em>dead position</em> (unwinnable for both sides).
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
 * three-valued: {@code WINNABLE}, {@code UNWINNABLE}, or {@code UNDETERMINED}. The direct analysis record additionally
 * tells whether a {@code WINNABLE} result was theorem-certified and carries a concrete mate line for searched wins. The
 * undetermined case is bounded by a 500&nbsp;000-position limit; most positions resolve well below it.</li>
 * </ul>
 *
 * <p>
 * Dead-position detection is the symmetric whole-position notion, decided by
 * {@link io.github.dlbbld.ashlarchess.unwinnability.DeadPositionAnalyzer}: a position is dead exactly when it is
 * unwinnable for both sides. It carries its own verdicts -
 * {@link io.github.dlbbld.ashlarchess.unwinnability.DeadPositionQuickVerdict} ({@code DEAD} / {@code POSSIBLY_ALIVE})
 * and {@link io.github.dlbbld.ashlarchess.unwinnability.DeadPositionFullVerdict} ({@code DEAD} / {@code ALIVE} /
 * {@code UNDETERMINED}) - rather than reusing the per-side unwinnable vocabulary.
 *
 * <h2>Legal positions only</h2>
 *
 * <p>
 * <b>This analysis is defined for, and guaranteed only on, legal positions</b> - those reachable from the standard
 * starting position by a sequence of legal moves. That is the whole domain of the game-play use cases it serves
 * (flag-fall and resignation adjudication, dead-position detection): a game played with legal moves, and the PGNs it
 * produces, never contains an illegal position, and winnability is meaningless on one.
 *
 * <p>
 * Strict FEN validation ({@code Board.fromFenStrict}) rejects the illegal positions it can detect by structural rule -
 * wrong piece counts, the side not to move left in check, and unreachable check geometries (a double check by two
 * same-coloured bishops, two rooks, two queens, or two knights, or three or more checkers). But full legality cannot be
 * validated in practice - a complete check needs an infeasible retrograde / proof-game search - so <b>ashlar does not
 * enforce legality, and submitting only legal positions is the caller's responsibility.</b>
 *
 * <p>
 * On an illegal position the result is undefined: it may be inaccurate, and the quick and full analyzers (or other CHA
 * implementations) may disagree. The verdict is still correct on the large majority of illegal positions; only a small,
 * known set of unreachable constructions is mis-decided - for example a checkmate delivered by an impossible
 * double-bishop check over otherwise insufficient material (called unwinnable), or the retro-illegal basic-helpmate
 * counterexamples {@code 8/8/8/8/2N5/8/k1K5/1B6 b} (KBNvK) and {@code 8/8/8/8/8/B7/B7/k1K5 w} (KBBvK), genuinely
 * unwinnable yet reported {@code WINNABLE} by the helpmate-existence shortcut. Such positions cannot occur in a legally
 * played game; they are of interest only for puzzles or position composition, which is out of scope.
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
