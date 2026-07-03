// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

/**
 * The library's flagship feature - unwinnability analysis. Decides whether a position is <em>unwinnable for a
 * side</em> - no legal sequence can end with that side giving checkmate, even if the opponent cooperates - and the
 * symmetric notion of a <em>dead position</em> (unwinnable for both sides).
 *
 * <p>
 * Since 22.0.0 the engine is ashlar's own independent, paper-derived implementation of Miguel Ambrona's FUN 2022 paper <em>A
 * Practical Algorithm for Chess Unwinnability</em> (Figures 5-13, Lemmas 5/6, Theorem 12), governed by the committed
 * specification {@code fun22-spec.md} and derived from the paper only. It replaces the earlier Java port of
 * Ambrona's C++ Chess Unwinnability Analyzer (CHA / D3-Chess): the algorithm code is now traceable to the published
 * paper rather than to another codebase. Ambrona's Rust successor
 * <a href="https://github.com/miguel-ambrona/chasolver">chasolver</a> serves as an independent test oracle: every
 * definite verdict is cross-checked against the committed cha/chasolver oracle fixtures with zero soundness
 * contradictions.
 *
 * <p>
 * Insufficient material covers the trivial cases (king-vs-king, king + minor vs king); positions like blocked pawn
 * walls, certain wrong-bishop endgames, and many forced-only-moves continuations are dead but <em>not</em>
 * insufficient - and most chess libraries get them wrong. This analysis decides them correctly across the full range
 * of positions.
 *
 * <h2>Two variants</h2>
 *
 * <ul>
 * <li><strong>Quick</strong> ({@link io.github.dlbbld.ashlarchess.unwinnability.UnwinnableQuickAnalyzer}) - the
 * paper's Figure 10: structural and bounded, three-valued: {@code UNWINNABLE}, {@code WINNABLE} (only on quickly
 * matable positions), or {@code POSSIBLY_WINNABLE}. It is sound but not complete - a definite verdict is always
 * correct, and {@code POSSIBLY_WINNABLE} asserts nothing.</li>
 * <li><strong>Full</strong> ({@link io.github.dlbbld.ashlarchess.unwinnability.UnwinnableFullAnalyzer}) - the
 * paper's Figure 9: deep search, three-valued: {@code WINNABLE}, {@code UNWINNABLE}, or {@code UNDETERMINED}. The
 * direct analysis record carries the concrete mate line witnessing a {@code WINNABLE} result. The undetermined case
 * is bounded by a 500&nbsp;000-position limit; most positions resolve well below it.</li>
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
 * On an illegal position the result is undefined: it may be inaccurate, and the quick and full analyzers (or other
 * implementations of the same algorithms) may disagree. The verdict is still correct on the large majority of illegal
 * positions; only a small, known set of unreachable constructions is mis-decided - for example a checkmate delivered
 * by an impossible double-bishop check over otherwise insufficient material (called unwinnable). Such positions
 * cannot occur in a legally played game; they are of interest only for puzzles or position composition, which is out
 * of scope.
 *
 * <h2>Analyzer entry points</h2>
 *
 * <p>
 * The analyzers run only when a caller asks for a side-specific answer or a whole-position dead-position query. No
 * analyzer is run automatically during board construction or move execution.
 *
 * <p>
 * See {@code specification.md} section 3.2 for the design rationale and {@code fun22-spec.md} for the governing
 * algorithm specification.
 */
@NonNullByDefault
package io.github.dlbbld.ashlarchess.unwinnability;

import org.eclipse.jdt.annotation.NonNullByDefault;
