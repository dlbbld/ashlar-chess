ashlar-chess
===========

ashlar-chess is a Java chess library focused on rule correctness, production usability, and reproducible validation.
It implements SAN, FEN, and PGN parsing, validation, and export with a strict/lenient parser pair,
and includes a Java port of the [Chess Unwinnability Analyzer (CHA)](https://github.com/miguel-ambrona/D3-Chess) as a flagship feature.

## Scope

It's not a chess engine - it does not calculate best moves for a given position.

It is also not a move-generation benchmark library. The public `Board` is a rich game object: it keeps the position,
move history, legal moves per ply, SAN/LAN strings, repetition counts, halfmove clocks, and castling-right facts needed
for rule-level queries and reports. That rich state is backed by bitboards for piece placement and move generation.
The CHA full-search hot path is deliberately leaner: it uses mutable bitboards and make/unmake state because cooperative
mate search needs the best practical performance the design can provide.

PGN input has a few limitations - see [Limitations](#limitations) under PGN functionality.

## Correctness and design

The library is built for correctness and comprehension - for example, it produces meaningful messages for SAN, FEN, and
PGN validation.

For the design philosophy, architecture, and rule-level decisions, see [specification.md](specification.md).

The CHA port is used for unwinnability and dead-position detection.

## Testing

The test suite also cross-validates selected behavior against external chess libraries, currently python-chess as the primary oracle and [chesslib](https://github.com/bhlangonijr/chesslib) by Ben-Hur Carlos Vieira Langoni Junior as a secondary witness. These libraries are used for testing only and are not runtime dependencies of ashlar-chess.

# Using ashlar-chess as a dependency

Requires JDK 17 or later at runtime. Published to Maven Central.

## Maven

```xml
<dependency>
  <groupId>io.github.dlbbld</groupId>
  <artifactId>ashlar-chess</artifactId>
  <version>18.1.0</version>
</dependency>
```

## Gradle

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.dlbbld:ashlar-chess:18.1.0'
}
```

# Building from source

```
$ git clone git@github.com:dlbbld/ashlar-chess.git
$ cd ashlar-chess/
$ mvn clean compile package install
```

For the full Eclipse contributor workflow (project import, Checkstyle, formatter, save actions), see [setup.md](setup.md).

# Basic usage example
<!-- readme:code id=basic-usage -->

# History
Initially I needed a chess library that detects threefold repetitions and the fifty-move rule - not just for the current position, but across the whole game, including possible claims ahead. Finding none that did this, I started implementing it, and along the way it grew into a programming exercise in its own right, focused above all on correctness.

That threefold and fifty-move reporting was my personal missing piece; the Java port of the [Chess Unwinnability Analyzer (CHA)](https://github.com/miguel-ambrona/D3-Chess) is one I consider objectively missing - something every major chess program should have. From filling those gaps the name arises naturally: an ashlar is a finely cut stone that fits exactly into a wall.

# Threefold repetition and fifty-moves
## Threefold repetition claim ahead
The following game ended with a threefold repetition claim ahead according to [Wikipedia](https://en.wikipedia.org/wiki/Threefold_repetition#Portisch_versus_Korchnoi,_1970):

<!-- readme:code id=threefold-claim-ahead -->

The report mentions the possible claim ahead:
<!-- readme:output id=threefold-claim-ahead -->
Black could have claimed a threefold on the 25th move with writing (but not playing) 25... Qb5. White's possible claims are along the move number
followed by a dot (for example, 20. Ra2). Possible claims for Black are along the move number followed by three dots (for example, 25... Qb5).

## Threefold repetition on the board
The following game contains a threefold repetition according to [Wikipedia](https://en.wikipedia.org/wiki/Threefold_repetition#Capablanca_versus_Lasker,_1921):

<!-- readme:code id=threefold-on-board -->

Output:
<!-- readme:output id=threefold-on-board -->
The repetitions are indicated in order as occurred on the board. As different positions can repeat, letters A, B, C etc., indicate the different positions in the order of the first occurrence.

## Fifty-moves
According to [Wikipedia](https://en.wikipedia.org/wiki/Fifty-move_rule#Karpov_vs._Kasparov,_1991), the next game ends with a run of more than fifty moves without a capture or pawn move.

<!-- readme:code id=fifty-move -->

Output:
<!-- readme:output id=fifty-move -->
Each bracket gives the moves by each player since the last capture or pawn move, written `(White/Black)`. The line marks the start of the run, the move where it reaches **fifty moves by each player** - from there a draw is claimable - and where it ends; a run reaching `75/75` would be an automatic draw. Here the run reaches `50/50` at `113. Ng5` and continues to `51/52`, so a draw was claimable on each of those last moves.

# Game adjudication
Flag-fall and resignation are the terminations where a player loses by an external event - subject to one FIDE
exception: the game is instead a **draw** when the opponent could not have checkmated by any possible series of legal
moves. `Adjudicator` applies that rule for you. You only decide which player would otherwise win - the opponent of the
one who flagged or resigned - and it returns the verdict.

Each event has a **quick** and a **full** variant:

* **quick** - rules `DRAW` or `LOSS`, from the fast `Board.isUnwinnableQuick(Side)` analyzer. It draws only when it can
  *prove* the opponent cannot win; otherwise the flag stands. Latency is bounded - the right choice during live play.
* **full** - rules `DRAW`, `LOSS`, or `UNDETERMINED`, from the complete `Board.isUnwinnableFull(Side)` analyzer. It
  additionally *proves* wins and reports `UNDETERMINED` only when its bounded search runs out (rare). The recommended
  check at game end, where the extra cost is negligible.

## Flagfall
Under [FIDE 6.9](https://handbook.fide.com/chapter/e012023), a player who runs out of time loses, unless the opponent
cannot checkmate by any possible series of legal moves. The quick variant is the live-play path:

<!-- readme:code id=adjudication-flagfall-quick -->

A single call covers the material-only draws (a lone king, king and bishop against a lone king, ...) as a subset and -
being a port of CHA - the blocked, non-material draws such as pawn walls as well. When it cannot prove a draw it rules a
`LOSS`: the practical "no draw could be shown, so the flag stands".

How safe is that quick ruling? In Ambrona's Lichess evaluation of CHA, out of 90,546 positions that are genuinely
unwinnable the quick (semi-static) analysis proves all but three directly - **90,543 of 90,546, about 99.99%**. The tiny
remainder is exactly what the full variant is for:

<!-- readme:code id=adjudication-flagfall-full -->

The full variant draws on a proven dead position, rules a `LOSS` on a proven win, and returns `UNDETERMINED` only when
its bounded search is exhausted - the one corpus position above is the sole such case here. Use it at game end, or for
analysis, studies, and offline review.

## Resignation
Under [FIDE 5.1.2](https://handbook.fide.com/chapter/e012023), resignation carries the identical exception, so it
adjudicates exactly like a flag-fall - same test, same result, in both the quick and full variants:

<!-- readme:code id=adjudication-resignation -->

## Dead position during play
Under [FIDE 5.2.2](https://handbook.fide.com/chapter/e012023), the game is drawn as soon as a dead position arises:
neither player can checkmate by any possible series of legal moves.

The standard material-only dead positions should still be checked during play:

```text
after each move:
    if board.isInsufficientMaterial():
        return draw
```

With this library, a server could additionally check for position-wise dead positions detected by CHA quick, for example
blocked pawn walls:

```text
after each move:
    if UnwinnableQuickAnalyzer.unwinnableQuick(board) == UNWINNABLE:
        return draw
```

The quick check is computationally unproblematic after every move.
For live games my personal preference is still not to run the CHA quick dead-position
check after every move. The reason is that dead positions not due to insufficient material are rare.
Only performing the check after flag fall or resignation will not alter the game result. Once a game has entered
 a dead position, no later legal move can make it winnable again. If the players continue in a blocked position until flagfall, resignation, or draw agreement, the adjudication above still returns a draw.

The trade-off is timing, not outcome. Checking during play gives the exact FIDE 5.2.2 termination point; checking at the
end preserves the final result.

# Unwinnability API

The library implements the [Chess Unwinnability Analyzer (CHA)](https://github.com/miguel-ambrona/D3-Chess). The project
page [CHA](https://github.com/miguel-ambrona/D3-Chess), which elaborates on the subject in every aspect.

A position is unwinnable for a player if there is no legal sequence that can end with that player giving checkmate,
even if the opponent cooperates. If the position is unwinnable for both players, it's a dead position.

> **Note:** quick/full dead-position detection is caller-invoked. `Board` does not run the analyzer during
> construction or after each move; callers that want to adjudicate analyzer-driven dead positions call the no-side
> overloads `UnwinnableQuickAnalyzer.unwinnableQuick(board)` / `UnwinnableFullAnalyzer.unwinnableFull(board)`, or the
> side-specific `Board.isUnwinnableQuick(Side)` / `Board.isUnwinnableFull(Side)`.

## Methods
The library provides an implementation of CHA. So for both situations, there is a quick and a full method.

The quick method is speedy by design but might miss some corrections. The full method is slower and complete when it
returns one of the winnable verdicts or UNWINNABLE; bounded search may return UNDETERMINED.

### Unwinnability
The quick method has two return values:
* UNWINNABLE - the position is not winnable by the player
* POSSIBLY_WINNABLE - not proven unwinnable; most likely winnable, but it might be unwinnable in some rare cases

The quick method never claims winnability - proving a concrete win is the full method's job.
`Board.isUnwinnableQuick(Side)` returns this verdict directly. `UnwinnableQuickAnalyzer.unwinnableQuick(...)` returns
`UnwinnabilityQuickAnalysis` (the verdict only).

The full method has four return values:
* WINNABLE_HELPMATE - winnable, with a concrete cooperative mate line
* WINNABLE_BY_THEOREM - winnable, certified by the [basic-helmpate-existence](https://github.com/dlbbld/basic-helpmate-existence) theorem (no line). This adds nothing substantially new to CHA and does not change CHA outcome
in any way, it is only trying an alternative approach for some material cases.
* UNWINNABLE - the position is not winnable by the player
* UNDETERMINED - the limits in the code interrupted the search

Performance: The limit regarding "UNDETERMINED" is 500'000 positions. It takes around one to two seconds to reach. Most positions evaluate in milliseconds.

### Dead position
A position is dead when it is unwinnable for both players. The no-side overloads check this and reuse the same verdict
enums, so there is no separate dead-position type.

`UnwinnableQuickAnalyzer.unwinnableQuick(board)` returns an `UnwinnabilityQuickVerdict`:
* UNWINNABLE - the position is dead (neither side can mate)
* POSSIBLY_WINNABLE - not provably dead

`UnwinnableFullAnalyzer.unwinnableFull(board)` returns an `UnwinnabilityFullVerdict`:
* WINNABLE_HELPMATE / WINNABLE_BY_THEOREM - not dead (one side can win)
* UNWINNABLE - the position is dead
* UNDETERMINED - the limits in the code interrupted the search

Performance: The comment from the Unwinnability section for UNDETERMINED applies here. However, it checks both sides so that it can take double the time.

## Examples

### Unwinnable

#### Insufficient material
The most common situations of unwinnable are if one side has insufficient material.
These are treated correctly by all standard chess libraries.
For example, if White flags with the king and rook against the lone king of Black. Then, Black cannot potentially mate with the king alone.
[Position](https://lichess.org/analysis/8/8/4k3/3R4/2K5/8/8/8_w_-_-_0_50)

<!-- readme:code id=unwinnable-insufficient-material -->

#### Forced moves
There are everyday situations mainly in lower time controls like Bullet, where the game could only continue with a few
forced moves, and the game outcome is determined. Here Black flags, but there is no game continuation possible where
White could have won.
[Game](https://lichess.org/OawUhnkq#101)

<!-- readme:code id=unwinnable-forced-moves -->

#### Pawn walls
Pawn walls are blocked positions, both players cannot mate and cannot make progress, so they are dead positions. They are not detected
by most common chess libraries.
[Game](https://lichess.org/c3ew66ZV#123)

<!-- readme:code id=unwinnable-pawn-walls -->

#### Common positions
When there are still a lot of pieces on the board, so a mate is very likely, the quick algorithm says POSSIBLY_WINNABLE.
It makes an educated guess only. In this example, the full algorithm calculates an actual mate; in harder positions,
the bounded search may return UNDETERMINED.
[Game](https://lichess.org/SCKpvJQX#57)

<!-- readme:code id=unwinnable-common-positions -->

#### Blocked positions the quick algorithm proves
The quick algorithm (a port of CHA 2.6.1) also proves many blocked and fortress positions, not only material-based ones. Here White's bishop and pawns are blocked and cannot make progress against the cornered black king, so the position is unwinnable for White - and the quick algorithm already decides it. [Game](https://lichess.org/bKHPqNEw#81)

<!-- readme:code id=unwinnable-blocked-quick -->

### Dead positions
Because dead positions are just unwinnable positions for both sides, there is not much more substantially to say.

#### Insufficient material
The most straightforward dead position is when one player already has insufficient material, and the other becomes insufficient due to capture. All chess libraries detect this case.

[Position](https://lichess.org/analysis/8/8/3kn3/8/2K5/8/8/8_w_-_-_0_50)
<!-- readme:code id=dead-insufficient-material -->

#### Pawn walls
Pawn walls are dead positions, but most common chess libraries do not detect them. Here is another example.
[Game](https://lichess.org/V08kX4kz#121)

<!-- readme:code id=dead-pawn-walls -->

#### Forced moves
Positions can also often be dead due to forced moves.
[Game](https://lichess.org/8FUSHxUV#115)

<!-- readme:code id=dead-forced-moves -->

# PGN functionality

## Limitations

- PGN move variations
- PGN Numeric Annotation Glyphs (NAGs, e.g. `$1`, `$10`)
- Multi-game PGN files (one game per file)

UTF-8 byte-order marks (BOM) are accepted by the lenient parser (stripped on input) and rejected by the strict parser. PGN move suffix annotations (`!`, `?`, `!!`, `??`, `!?`, `?!`) are fully parsed, modeled, and round-tripped on export by both parsers.

## PGN parser

### Lenient PGN parser
The common PGN parser — reads the file with best effort. For example, the space after `[` below is ignored. See the [Limitations](#limitations) section above for what neither parser accepts.

ashlar-chess ships **lenient parsers for all three input languages it consumes** — SAN, PGN, and FEN. Each one applies a typed syntactic-tolerance pass and surfaces tolerated deviations as forgiven items on the validation result, then delegates the heavy lifting to the corresponding strict parser. The PGN flavour (described in this section) routes its SAN tokens through the lenient SAN layer and its `FEN` tag through the lenient FEN layer, so a single lenient PGN parse picks up deviations across all three languages. The lenient FEN layer is reachable directly via `Board.fromFenLenient(String)` for callers that consume FEN strings outside the PGN context (engine output, lichess/chess.com exports, hand-edited fixtures); see `specification.md` §3.3.3 for the strict-vs-lenient × raw-vs-advanced contract and the full `ForgivenFenItemCode` taxonomy.

In addition to structural tolerances (whitespace, missing tags, optional termination markers), the lenient parser accepts a defined set of SAN deviations from canonical — see [PGN SAN tolerances](#pgn-san-tolerances) below.

#### PGN valid

<!-- readme:code id=pgn-lenient-valid -->

#### PGN transformation to export format

The parser does a bit more than a standard parser should do. It converts the imported PGN to a PGN object which when exported will adhere to the export format. That is it for example adds missing tags and sorts them if necessary.

<!-- readme:code id=pgn-lenient-export-transform -->

Output:
<!-- readme:output id=pgn-lenient-export-transform -->

#### PGN SAN tolerances

The lenient PGN parser accepts SAN moves that deviate from canonical SAN in any of the following ways. Each accepted deviation is surfaced as a typed `ForgivenItem` via `LenientPgnParserValidationResult.sanForgivenItems()`, so consumers can either silently accept or warn the user. The full taxonomy (21 codes) is documented in `specification.md` §3.3.1.

- **Castling**: `0-0` / `0-0-0` (zero instead of letter O)
- **Notation form**: long algebraic (`e2-e4`, `Nb1-d7`), UCI (`e2e4`, `e7e8q`, `e1g1` for castling), explicit pawn letter (`Pe4`)
- **Disambiguation**: redundant file/rank/square (e.g. `Nbd7` when `Nd7` would suffice), non-canonical rank-instead-of-file (`R1d4` where canonical is `Rad4`)
- **Capture marker**: missing (`Be5` when actually a capture) or spurious (`Bxe5` to an empty square)
- **Check / mate suffix**: missing, spurious, or wrong (`Nd7+` when actually mate, `Nd7#` when only check, `Nd7` when actually check, etc.)
- **Promotion**: missing `=` (`e8Q`)
- **Case**: lowercase piece letter (`nf3`), uppercase file letter (`NF3`), uppercase capture marker (`BXe5`), lowercase promotion piece (`e8=q`)

<!-- readme:code id=pgn-san-tolerances -->

Output:
<!-- readme:output id=pgn-san-tolerances -->

The strict pipeline that performs the actual chess validation is reused unchanged; the lenient layer only translates input shape and recovers from a defined set of strict rejections. A move that's not a legal chess move (regardless of how it was written) is still rejected.

#### PGN invalid

When parsing fails, error messages are designed to be as descriptive as possible.

<!-- readme:code id=pgn-lenient-invalid -->

Output:
<!-- readme:output id=pgn-lenient-invalid -->

#### File parsing

<!-- readme:code id=pgn-lenient-file-parsing -->

### Strict PGN parser
The strict PGN parser does not allow inconsistencies as the lenient PGN parser. It expects the PGN to be in the export format according to the PGN specification.

#### PGN valid

<!-- readme:code id=pgn-strict-valid -->

#### PGN invalid syntax

<!-- readme:code id=pgn-strict-invalid-syntax -->

Output:
<!-- readme:output id=pgn-strict-invalid-syntax -->

#### PGN invalid form

<!-- readme:code id=pgn-strict-invalid-form -->

Output:
<!-- readme:output id=pgn-strict-invalid-form -->

#### File parsing

<!-- readme:code id=pgn-strict-file-parsing -->

## PGN creation

### Create PGN for game

You can create the PGN for a game played in the library or export an imported PGN.

<!-- readme:code id=pgn-create-game -->

Output:
<!-- readme:output id=pgn-create-game -->

### PGN format

The PGN is created in the unique export format as defined by the PGN specification so passes validation by the lenient and strict PGN parser.

<!-- readme:code id=pgn-format -->

## PGN export

A PGN can be written to the file system as below.

<!-- readme:code id=pgn-export -->

## PGN validation

### PGN lenient validation
Checks whether a PGN can be parsed using the PGN lenient parser.

#### PGN valid

<!-- readme:code id=pgn-lenient-validation-valid -->

#### PGN invalid

<!-- readme:code id=pgn-lenient-validation-invalid -->

Output:
<!-- readme:output id=pgn-lenient-validation-invalid -->

#### File validation

<!-- readme:code id=pgn-lenient-validation-file -->

### PGN strict validation

Checks whether a PGN adheres to the export format per the PGN specification.

#### PGN valid

<!-- readme:code id=pgn-strict-validation-valid -->

#### PGN invalid

<!-- readme:code id=pgn-strict-validation-invalid -->

Output:
<!-- readme:output id=pgn-strict-validation-invalid -->

#### File validation

<!-- readme:code id=pgn-strict-validation-file -->

# License

Copyright (C) 2020-2026  Daniel Bächli

ashlar-chess is free software, licensed under the GNU General Public License, version 3 (GPL v3). See [LICENSE](LICENSE) for the full text.

The unwinnability and dead-position detection is a Java port of the [Chess Unwinnability Analyzer (CHA)](https://github.com/miguel-ambrona/D3-Chess) by Miguel Ambrona, also licensed under GPL v3.
