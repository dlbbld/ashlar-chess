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

For ashlar-chess, the survey measures the public `Board` move-generation backend, not the CHA full-search board. Each
sample is stored as a fresh `Board(fen)`, and the timed section calls:

```java
BitboardLegalMoveFactory.calculateLegalMoves(
    board.getBitboardPosition(),
    board.getHavingMove(),
    board.getCastlingRight(board.getHavingMove()),
    enPassantBit)
```

This is the production bitboard-backed legal-move path used by `Board`. The survey deliberately does not time
`Board.getLegalMoves()`, because `Board` stores the current legal moves and that accessor would mostly measure cached
list access. It also does not time `HelpmateSearchBoard`, the mutable make/unmake board used inside CHA full search.

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

| Corpus | Positions | Generated moves | Board backend | StaticPosition oracle | ChessLib | Board / ChessLib | Oracle / ChessLib |
|---|---:|---:|---:|---:|---:|---:|---:|
| `MAX_MOVES` | 800 | 430,840 | 2.418 us/position | 184.210 us/position | 1.231 us/position | 2.0x | 149.6x |
| `RANDOM_NO_REPETITION` | 800 | 338,680 | 2.064 us/position | 107.570 us/position | 0.962 us/position | 2.1x | 111.8x |
| `WCC2021` | 800 | 494,420 | 2.971 us/position | 214.532 us/position | 1.198 us/position | 2.5x | 179.1x |
| `CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR` | 800 | 365,840 | 2.213 us/position | 154.812 us/position | 1.204 us/position | 1.8x | 128.6x |

The generated move counts matched in every corpus.

## Takeaway

The current `Board` production backend is roughly 1.8x to 2.5x slower than ChessLib on these sampled positions, while
the relocated `StaticPosition` oracle is still roughly 112x to 179x slower. That is the intended architecture: the
readable mailbox implementation remains as the test oracle, and production move generation runs on the bitboard backend.

This survey does not measure the even leaner mutable bitboard representation used by CHA full search.
