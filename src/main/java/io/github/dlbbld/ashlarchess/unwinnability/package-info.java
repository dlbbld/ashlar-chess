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
 * <li><strong>Quick</strong> ({@link io.github.dlbbld.ashlarchess.unwinnability.UnwinnableQuickAnalyzer}) - microsecond-scale,
 * structural, three-valued: {@code WINNABLE}, {@code UNWINNABLE}, {@code POSSIBLY_WINNABLE}. The third value is a
 * deliberate honesty signal - the quick algorithm is sound but not complete.</li>
 * <li><strong>Full</strong> ({@link io.github.dlbbld.ashlarchess.unwinnability.UnwinnableFullAnalyzer}) - deep search, three-valued:
 * {@code WINNABLE}, {@code UNWINNABLE}, {@code UNDETERMINED}. The undetermined case is bounded by a
 * 500&nbsp;000-position limit; most positions resolve well below it.</li>
 * </ul>
 *
 * <p>
 * Dead-position detection is the symmetric notion with analogous three-valued returns
 * ({@link io.github.dlbbld.ashlarchess.unwinnability.DeadPositionQuick}, {@link io.github.dlbbld.ashlarchess.unwinnability.DeadPositionFull}).
 *
 * <h2>Analyzer entry points</h2>
 *
 * <p>
 * The analyzers run only when a caller asks for a side-specific answer or a dead-position query on
 * {@link io.github.dlbbld.ashlarchess.board.Board}. No analyzer is run automatically during board construction or move execution.
 *
 * <p>
 * See {@code specification.md} section 3.2 for the full design rationale.
 */
@NonNullByDefault
package io.github.dlbbld.ashlarchess.unwinnability;

import org.eclipse.jdt.annotation.NonNullByDefault;
