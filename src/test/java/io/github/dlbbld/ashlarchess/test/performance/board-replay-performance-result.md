# Board replay performance survey

Date: 2026-06-26

Branch: `jpms-post-fixing`

Command:

```powershell
mvn -o -q exec:java "-Dexec.classpathScope=test" "-Dexec.mainClass=io.github.dlbbld.ashlarchess.test.performance.BoardReplayPerformanceSurvey" "-Dexec.args=--stockfish-wsl"
```

Java:

```text
openjdk version "17.0.19" 2026-04-21
OpenJDK Runtime Environment Temurin-17.0.19+10 (build 17.0.19+10)
OpenJDK 64-Bit Server VM Temurin-17.0.19+10 (build 17.0.19+10, mixed mode, sharing)
```

## Method

`BoardReplayPerformanceSurvey` measures the `Board` paths that are not isolated by the move-generation survey:
construction from FEN, full game replay, and the claim-ahead pattern that plays a move, unmoves it, then plays it for
real.

With `--stockfish-wsl`, the survey also builds and runs a tiny C++ runner against the Stockfish library installed for
the Ambrona D3-Chess setup in WSL on Windows. The C++ runner receives the same start FEN and resolved UCI move list for
each game, then times:

- `Position::set`
- `Position::set` plus legal UCI move resolution and `Position::do_move`
- the same replay with `do_move; undo_move; do_move`

These Stockfish numbers are useful as an engine-grade reference point, but they are not a native-machine benchmark:
they run in WSL on Windows and link against a native library outside Maven. They also measure Stockfish's position
machinery, not ashlar's richer public board state and reporting data.

Harness settings:

- maximum 150 games per corpus
- 3 warmup rounds
- 20 measured rounds

## Results

| Corpus | Games | Moves | Ashlar construct | Stockfish WSL construct | Ashlar replay | Stockfish WSL replay | Ashlar replay + unmove | Stockfish WSL replay + undo |
|---|---:|---:|---:|---:|---:|---:|---:|---:|
| `MAX_MOVES` | 2 | 35,394 | 3.608 us/game | 1.449 us/game | 10.091 us/move | 1.541 us/move | 25.863 us/move | 1.800 us/move |
| `RANDOM_NO_REPETITION` | 5 | 5,974 | 3.196 us/game | 1.326 us/game | 3.766 us/move | 1.351 us/move | 7.965 us/move | 2.308 us/move |
| `WCC2021` | 14 | 1,378 | 3.444 us/game | 0.819 us/game | 7.059 us/move | 0.695 us/move | 17.322 us/move | 0.727 us/move |
| `CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR` | 150 | 17,983 | 2.146 us/game | 1.250 us/game | 4.834 us/move | 0.914 us/move | 11.546 us/move | 0.904 us/move |

| Corpus | Replay ratio | Replay + unmove ratio |
|---|---:|---:|
| `MAX_MOVES` | 6.5x Stockfish WSL | 14.4x Stockfish WSL |
| `RANDOM_NO_REPETITION` | 2.8x Stockfish WSL | 3.5x Stockfish WSL |
| `WCC2021` | 10.2x Stockfish WSL | 23.8x Stockfish WSL |
| `CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR` | 5.3x Stockfish WSL | 12.8x Stockfish WSL |

## Takeaway

Stockfish is still dramatically faster on the raw replay and undo-heavy paths, even through WSL on Windows. The gap is
smallest on the long random corpus and largest on short game corpora where fixed per-game costs dominate more strongly.

That comparison should not be read as a public-board API failure: ashlar's `Board` maintains sorted/cached legal-move
state, SAN-facing history, repetition and claim data, and report-oriented derived facts. Stockfish is optimized for
engine search state. The useful signal is scale: the new board generation is in the same broad microsecond domain for
ordinary replay, while Stockfish remains the reference for engine-grade make/unmake speed.
