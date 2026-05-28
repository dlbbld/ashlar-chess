package io.github.dlbbld.ashlarchess.test.unwinnability.oracle;

/**
 * Verdict for the pawn-wall classifier.
 *
 * <p>
 * {@link #YES} is sound: a position classified {@code YES} is guaranteed to be a permanent pawn-wall barrier (neither
 * king can reach the opposing king's square through legal play, in the helpmate-cooperation sense, and the remaining
 * piece configuration is restricted to ones where mate cannot be forced without the king crossing). {@link #UNKNOWN} is
 * the safe fallback for cases the classifier cannot decide - the caller should consult a stronger analysis (e.g.
 * CHA-quick) for these positions.
 *
 * <p>
 * Callers asserting unwinnability based on the pawn-wall heuristic must use {@code verdict == YES}; {@code UNKNOWN}
 * means "fall through to the next detection path."
 *
 * <p>
 * The enum is intentionally binary today. A definite {@code NO} value was considered but no caller currently needs to
 * distinguish "definitely not a wall" from "no claim" - both produce the same fall-through behaviour. If a concrete
 * need arises later (e.g. asserting that two adjacent moves are not both walls), add a {@code NO} constant then.
 *
 * <p>
 * See {@code pawn-wall-soundness.md} for the full design - production geometric check, BFS as test oracle, and the
 * asymmetric agreement contract.
 */
public enum PawnWallVerdict {

  YES,
  UNKNOWN;

}
