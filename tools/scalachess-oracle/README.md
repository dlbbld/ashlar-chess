# scalachess oracle

`generate_legal_moves_oracle.scala` generates the scalachess legal-move-generation
oracle: for every PGN in the covered move-rule-mechanics buckets it replays the game
with [scalachess](https://github.com/lichess-org/scalachess) (lichess.org's rules
engine) and records the sorted set of legal UCIs at each visited position. Output goes
to `src/test/resources/oracle/scalachess/move-gen/<bucket>.jsonl`, one JSON object per
PGN.

This is the scalachess counterpart of `src/test/python/generate_move_gen_oracle.py`:
**identical bucket scope and identical JSONL schema**, so the same provider-neutral
Java reader (`io.github.dlbbld.ashlarchess.test.oracle.movegen`) consumes both. The
JSONL files are committed and read by `TestLegalMovesAgainstScalachessOracle`; `mvn`
never invokes scala-cli.

```text
Record := {"perPly": [Ply, ...], "pgn": <string>}   # keys sorted -> "perPly" < "pgn"
Ply    := {"legalMovesUci": [<string>, ...]}         # sorted ascending
```

`perPly[0]` is the legal-move set at the start position (honouring the optional
`[FEN]`/`[SetUp]` header); `perPly[k]` is the set after the k-th played move;
`perPly[halfMoveCount]` is the empty list for mate/stalemate.

**Castling.** scalachess emits castling as king-to-rook-square (`e1h1` / `e1a1`,
UCI_Chess960 style) and lists each castle twice (also the king-two-squares form). The
generator normalises genuine castles to the standard `e1g1` / `e1c1` form and
de-duplicates, matching ashlar-chess and python-chess. With that one normalisation the
committed oracle is **byte-identical** to the python-chess move-gen oracle wherever the
two share a fixture — the two engines agree on every legal-move set in this corpus.

## Insufficient-material oracle

`generate_insufficient_material_oracle.scala` generates a second, position-only oracle:
for every PGN in the four core insufficient-material buckets it replays the game, takes
the final position, and records scalachess's combined and per-side insufficient-material
verdicts plus the final FEN, to
`src/test/resources/oracle/scalachess/insufficient-material/<bucket>.jsonl`:

```text
{"fen": <string>, "hasInsufficientMaterialBlack": <bool>,
 "hasInsufficientMaterialWhite": <bool>, "isInsufficientMaterial": <bool>, "pgn": <string>}
```

scalachess frames the per-side verdict relative to the side to move
(`playerHasInsufficientMaterial` / `opponentHasInsufficientMaterial`); the generator maps
it to absolute white/black via the position's colour, matching python-chess's
`has_insufficient_material(WHITE|BLACK)`. Consumed by
`TestInsufficientMaterialAgainstScalachessOracle`. ashlar-chess and scalachess agree on
all 84 fixtures, and scalachess agrees with python-chess on all 84. The two claim-ahead
predicates are intentionally **not** part of this oracle — scalachess has no native query
for them, so re-deriving them would test the generator rather than scalachess.

## Version of record

- **scalachess 17.15.5**, resolved from **JitPack** (`com.github.lichess-org.scalachess::scalachess`).
  Not on Maven Central; lichess's `lila-maven` repo only carries older `scalachess_3`.
- scalachess class files are **Java 21** (class file 65), so the generator runs on a
  JDK 21 fetched by scala-cli (`--jvm temurin:21`) — out-of-process and independent of
  the Maven Java-17 build.
- The Bloop build server is disabled (`--server=false`); it fails to start on Windows.

## One-time setup

Install [scala-cli](https://scala-cli.virtuslab.org/) (it fetches the Scala compiler,
the JDK 21, and all dependencies on first run):

```powershell
scoop install scala-cli
```

## Regenerate the oracles

Run from the ashlar-chess checkout root:

```powershell
scala-cli run tools/scalachess-oracle/generate_legal_moves_oracle.scala --server=false --jvm temurin:21 -- .
scala-cli run tools/scalachess-oracle/generate_insufficient_material_oracle.scala --server=false --jvm temurin:21 -- .
```

The trailing `.` is the repo root (defaults to the working directory if omitted). Each
script rewrites its `<bucket>.jsonl` files with LF line endings.
