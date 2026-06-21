# Board burn-in performance baseline

Per-method timing of every public `Board` read method, produced by
`BoardApiBurnInSurvey` (see that class for methodology). Used as a **release regression baseline**: a release with
material board-logic changes re-runs the burn-in and compares against the previous release's numbers here (see the
release procedure in `workflows.md`, "Cutting a release" → Pre-flight).

## How to read this

- `us/ply` is the average cost of one call, measured once per ply across a game replay, on two corpora: `WCC2021`
  (~100 plies/game) and `RANDOM_NO_REPETITION` (~1,200 plies/game).
- `ratio` = RANDOM / WCC. **~1 means O(1) per call**; a ratio growing with game length means the call is O(history),
  which becomes O(n^2) if a caller invokes it per ply. A method that *returns* n items (the `getPerformed*` / `getLegal*`
  collections) or *must* compare n (`equals`) is legitimately O(n); a scalar/boolean that is O(history) is a defect.
- **Absolute us/ply are machine- and JVM-relative.** The regression check that matters is run **same machine, same
  boot session**: measure the previous release (from its tag, via a worktree) and the new release back to back, and
  compare. The scaling `ratio` is the machine-independent signal; the absolute numbers are only comparable within one
  session.

Context for the numbers below: local developer machine, JDK 17 (Temurin), single boot session, 2026-06-21.

## 18.1.0 -> 19.0.0 verdict: no significant regression (net faster)

Two methods that were **O(history) in 18.1.0 are now O(1)** in 19.0.0:

| method | 18.1.0 (RANDOM us/ply, ratio) | 19.0.0 (RANDOM us/ply, ratio) | note |
|---|---|---|---|
| `hashCode` | 132.9 (5.9x) | **0.16 (0.5x)** | O(n) uncached -> O(1) summary |
| `fiftyMoveRuleClaimRights` | 54.3 (20.2x) | **2.97 (1.5x)** | was O(history); now bounded |
| `getLegalMovesAsSan` | 77.5 | **4.5** | SAN generation path |

One **deliberate tradeoff** — the `getPerformed*` accessors rebuild their list from the single `boardStateList` on
demand in 19.0.0, where 18.1.0 returned a pre-stored parallel list:

| method | 18.1.0 (RANDOM us/ply) | 19.0.0 (RANDOM us/ply) | note |
|---|---|---|---|
| `getPerformedMoves` | 0.53 | 7.88 | rebuilt on demand |
| `getPerformedMovesAsSan` | 0.56 | 6.10 | rebuilt on demand |
| `getPerformedMoveSpecifications` | 5.27 | 8.21 | rebuilt on demand |

This is the "less rich board" memory-for-time tradeoff (the per-ply parallel lists were dropped; see
`MemoryFootprintSurvey` — ~350 bytes/ply, no retained legal-move lists). Each is still O(n) — the cost of returning n
items — and the library calls them **once** per report build, not per ply, so there is no O(n^2). Callers should fetch
once and reuse; for the count use `getPerformedMoveCount()` (O(1) in both releases). Not a hot-path regression.

Every other method is comparable or faster in 19.0.0; no scalar/boolean accessor became O(history). `equals` and the
threefold-claim methods moved within measurement noise (WCC and RANDOM disagreed on direction).

## 19.0.0 baseline (the reference for the next release)

`BoardApiBurnInSurvey` scaling table, sorted by ratio:

```
method                                        WCC2021     RANDOM    ratio   verdict
equals(shadow)                                 20.929    234.645     11.2   <<< SUPERLINEAR (expected: compares n)
getPerformedMovesAsSan                          0.665      6.098      9.2   <<< SUPERLINEAR (expected: returns n)
getPerformedMoveSpecifications                  0.969      8.205      8.5   <<< SUPERLINEAR (expected: returns n)
getPerformedMoves                               1.015      7.880      7.8   <<< SUPERLINEAR (expected: returns n)
isInsufficientMaterial                          0.269      0.577      2.1
fiftyMoveRuleClaimRights                        1.971      2.974      1.5
outcome                                         0.332      0.428      1.3
getSan                                          0.096      0.097      1.0
getFen                                          6.228      5.680      0.9
toString                                        5.434      5.021      0.9
getLegalMoves                                   0.089      0.082      0.9
getRepetitionCount                              0.111      0.098      0.9
copyCurrentPositionWithoutHistory              10.091      7.362      0.7
getLegalMovesAsUci                              4.171      2.797      0.7
getLegalMovesAsSan                              9.873      4.526      0.5
hashCode                                        0.357      0.164      0.5
canClaimThreefoldRepetitionRule               164.110     62.039      0.4
threefoldRepetitionRuleClaimRights            175.199     65.941      0.4
getBitboardPosition                             0.181      0.057      0.3
getDynamicPosition                              0.464      0.054      0.1
```
(All ~50 other read methods are O(1), us/ply < 0.3, ratio ~1; omitted for brevity. Run the survey for the full list.)

Heavy bounded analyzer (sampled, not history-scaling): `unwinnableQuick(W+B)` ~120 us/call on RANDOM endgames, up to
~48 ms on the hard CHA_LICHESS positions — position-complexity bound, not a Board-scaling concern.

## Notable absolute costs (not regressions, but worth knowing)

`threefoldRepetitionRuleClaimRights` / `canClaimThreefoldRepetitionRule` ~165 us/call (they enumerate every legal move
and probe each). Bounded (ratio < 1), but the most expensive read methods. `getLegalMovesAsSan` ~10 us/call (SAN
generation). These are inherent to what they compute.
