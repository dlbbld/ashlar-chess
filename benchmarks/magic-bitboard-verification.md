# Magic-bitboard verification

The release candidate replaces bishop and rook ray walks with magic-bitboard lookups.
Queen attacks combine those two lookups. The result still includes the first blocker,
including a friendly piece, and all previous square/occupancy semantics are retained.

## Correctness

The independent review of `2702d82d` and its predecessors found no production correctness
defect. Verification included:

- Regenerating all 128 magic constants and checking that they match the committed values.
- 2,239,488 comparisons against an independent geometric attack implementation, including
  edge blockers and occupancy outside the relevant masks.
- Differential legal-move, attack, check and terminal-state checks against python-chess
  over 11,709 positions, including castling, en passant and promotion cases.
- Initial-position perft through depth 6 (119,060,324 nodes) and Kiwipete through depth 5
  (193,690,690 nodes).
- A negative probe confirming that a bad magic constant fails initialization rather than
  silently creating an invalid attack table.

The lookup tables contain 107,648 longs: 861,184 bytes (841 KiB), excluding array headers
and smaller metadata arrays. One-time initialization measured approximately 12 ms on
the original review machine; it is outside the steady-state benchmark measurements.

## Performance evidence

All percentages below are reductions in execution time for `legalMoves`, measured
against the ray implementation on the same machine. They are workload-specific results,
not a hardware ranking or a chess-playing-strength claim.

| Machine | positionCount=8 | positionCount=512 | Corrected slider ray/magic ratios |
|---|---:|---:|---:|
| ThinkPad, Ryzen 5 7535U | approximately 20.0% | approximately 24.8% | approximately 6.8–8.1× |
| iMac M3 | 16.8% | 18.4% | 5.6–6.1× |
| IdeaCentre, Ryzen 5 220, reported Balanced mode | 17.0% | 18.0% | 7.7–8.8× |

The iMac and IdeaCentre experiments used before/after/after/before order, three JVM
forks per row, and the same corrected benchmark sources in both builds. Each archive
contains 44 workload rows plus six slider microbenchmark rows. For every non-control
workload on each machine, all six after-fork means were below all six before-fork means.
The unchanged `pinnedPieces` control had overlapping distributions.

The iMac used native ARM64 Temurin 17.0.19; the IdeaCentre used native amd64 Temurin
17.0.20.1. The earlier independent ThinkPad experiment used different fork/iteration
and heap settings. Compare before/after ratios within an experiment; absolute times
across machines do not isolate the CPU from the JVM and other environment differences.

The later iMac and IdeaCentre data also supports gains in the three unwinnability
workloads: approximately 10.7–19.8% on the iMac and 11.2–17.0% on the IdeaCentre.
An earlier observation of no gain does not generalize to all machines or workloads.

## Reproduction and corrections

Use the [iMac procedure](imac-benchmarks.md) or the
[Windows procedure](ideacentre-benchmarks.md). The corrected workload pair is:

- Before production code: `84b9f0b202865a2381c826b6c961b0d008f183c8`.
- After production code and corrected harness: `2702d82d752a9187dc75a3ad0083ed31451023a2`.

The procedures overlay the corrected shared benchmark sources onto the before build,
record their hashes and the patch, and preserve the machine, JVM, logs and raw results.
Production changes between these commits are confined to the four slider-attack classes;
the unrelated proposed UCI, map-sizing and KBN changes are not in this comparison.

The original 13–16× slider headline used a rewritten, slower ray baseline. The corrected
baseline restores the original four explicit constant-direction calls, and the table
above supersedes that headline. The earlier 256-position quick fixture returned only
239 positions; corrected runs return the requested count. Both defects affected benchmark
evidence and have been repaired.

Each JMH method runs in separate, sequential forks. A common invocation does not make
measurements simultaneous or immune to environmental drift. Reported JMH confidence
intervals do not cover every source of between-run variation; repeated runs, individual
fork means and the unchanged control supplement them here.
