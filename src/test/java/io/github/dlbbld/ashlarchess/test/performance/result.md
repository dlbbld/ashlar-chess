# Move Generation Performance Survey

Date: 2026-06-04

Branch: `claude/add-endgame-knowledge`

Command:

```powershell
mvn -q test-compile exec:java "-Dexec.classpathScope=test" "-Dexec.mainClass=io.github.dlbbld.ashlarchess.test.performance.MoveGenerationPerformanceSurvey"
```

Java:

```text
openjdk version "17.0.19" 2026-04-21
OpenJDK Runtime Environment Temurin-17.0.19+10 (build 17.0.19+10)
OpenJDK 64-Bit Server VM Temurin-17.0.19+10 (build 17.0.19+10, mixed mode, sharing)
```

## Method

The survey compares legal-move generation on the same FEN positions collected from existing PGN test corpora.

For ashlar-chess, the survey measures two production bitboard paths. First, the public `Board` move-generation backend.
Each sample is stored as a fresh `Board(fen)`, and the timed section calls:

```java
BitboardLegalMoveFactory.calculateLegalMoves(
    board.getBitboardPosition(),
    board.getHavingMove(),
    board.getCastlingRight(board.getHavingMove()),
    enPassantBit)
```

This is the production bitboard-backed legal-move path used by `Board`. The survey deliberately does not time
`Board.getLegalMoves()`, because `Board` stores the current legal moves and that accessor would mostly measure cached
list access.

Second, the survey measures the legal-move buffer path used by `HelpmateSearchBoard` inside CHA full search. A test-side
probe mirrors the legal-move part of `HelpmateSearchBoard.refreshDerivedState`: it calls
`BitboardLegalMoveFactory.calculateLegalMovesInto(...)` and emits into a reusable `LegalMoveBuffer`, avoiding the
`TreeSet` sorting and `ImmutableList` allocation used by the public `Board` path. This isolates move generation for the
search board; it does not measure the whole recursive helpmate search or the `move` / `unmove` operations around each
node.

For the reference oracle, the survey converts the same board position back to `StaticPosition` and calls:

```java
AbstractLegalMoves.calculateLegalMoves(...)
```

For ChessLib, the survey calls:

```java
MoveGenerator.generateLegalMoves(...)
```

The harness uses:

- maximum 800 positions per corpus
- 3 warmup rounds
- 20 measured rounds

This is a lightweight `System.nanoTime()` survey, not a JMH benchmark. Treat the exact microsecond values as local-run
measurements; the order-of-magnitude comparison is the useful part.

## Results

| Corpus | Positions | Generated moves | Board backend | HelpmateSearchBoard buffer path | StaticPosition oracle | ChessLib | Board / ChessLib | Search board / ChessLib | Oracle / ChessLib |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| `MAX_MOVES` | 800 | 430,840 | 2.603 us/position | 1.136 us/position | 173.489 us/position | 1.079 us/position | 2.4x | 1.1x | 160.8x |
| `RANDOM_NO_REPETITION` | 800 | 338,680 | 1.852 us/position | 0.773 us/position | 100.470 us/position | 0.841 us/position | 2.2x | 0.9x | 119.5x |
| `WCC2021` | 800 | 494,420 | 2.887 us/position | 1.030 us/position | 208.635 us/position | 1.055 us/position | 2.7x | 1.0x | 197.8x |
| `CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR` | 800 | 365,840 | 2.202 us/position | 0.926 us/position | 147.541 us/position | 1.036 us/position | 2.1x | 0.9x | 142.4x |

The generated move counts matched in every corpus.

## Takeaway

The current `Board` production backend is roughly 2.1x to 2.7x slower than ChessLib on these sampled positions. The
`HelpmateSearchBoard` buffer path is roughly at ChessLib speed here, between 0.9x and 1.1x ChessLib. The relocated
`StaticPosition` oracle remains roughly 120x to 198x slower.

That is the intended architecture: the readable mailbox implementation remains as the test oracle, public `Board` keeps
its richer sorted/cached game-state surface, and CHA full search uses the lean sink-based bitboard path in its search
board.
