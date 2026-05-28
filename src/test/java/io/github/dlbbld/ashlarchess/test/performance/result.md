# Move Generation Performance Survey

Date: 2026-05-14

Branch: `codex/movegen-performance-survey`

Base commit: `5a9f52754f98a1bb9ed1dfe2f0f7961cd7bac09c`

Java:

```text
openjdk version "17.0.19" 2026-04-21
OpenJDK Runtime Environment Temurin-17.0.19+10 (build 17.0.19+10)
OpenJDK 64-Bit Server VM Temurin-17.0.19+10 (build 17.0.19+10, mixed mode, sharing)
```

## Method

The survey compares direct legal-move generation for ashlar-chess and ChessLib on the same FEN positions collected from existing PGN test corpora.

For ashlar-chess, the survey calls `AbstractLegalMoves.calculateLegalMoves(...)` directly. It does not time `Board.getLegalMoveSet()`, because the board stores the current legal moves and that would measure cached access rather than generation.

For ChessLib, the survey calls `MoveGenerator.generateLegalMoves(...)`.

The harness uses:

- maximum 800 positions per corpus
- 3 warmup rounds
- 20 measured rounds

This is a lightweight `System.nanoTime()` survey, not a JMH benchmark. The ratios are still useful because the observed gap is well above normal measurement noise.

## Results

| Corpus | Positions | Generated moves ashlar-chess | Generated moves ChessLib | ashlar-chess | ChessLib | Ratio |
|---|---:|---:|---:|---:|---:|---:|
| `MAX_MOVES` | 800 | 430,840 | 430,840 | 209.562 us/position | 1.475 us/position | 142.1x |
| `RANDOM_NO_REPETITION` | 364 | 167,280 | 167,280 | 140.537 us/position | 0.983 us/position | 142.9x |
| `WCC2021` | 800 | 494,420 | 494,420 | 240.051 us/position | 1.377 us/position | 174.4x |
| `CHA_LICHESS_QUICK_NOT_DEPTH_THREE` | 800 | 365,600 | 365,600 | 168.740 us/position | 1.166 us/position | 144.7x |

The generated move counts matched in every corpus.

## Takeaway

The current ashlar-chess move generator is roughly 140x to 175x slower than ChessLib on these sampled positions. This supports treating bitboard move generation as a major prerequisite before publishing the performance-oriented Maven Central release, especially for `FindHelpmate` / CHA full search.
