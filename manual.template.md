# ashlar-chess Manual

This is the user-facing guide for ashlar-chess. It explains how to call the public API and how to read the results.
The deeper reasons behind the contracts live in [specification.md](specification.md).

The examples in this manual are generated from compiled test sources. Their output is captured from running the
examples, so the shown snippets are checked by the test suite.

## Documentation Map

- [README.md](README.md) is the project front door: scope, dependency snippets, and a quick start.
- This manual is the practical usage guide.
- [specification.md](specification.md) is the technical contract: scope, architecture, invariants, FIDE/PGN decisions,
  and deliberate deviations.
- [setup.md](setup.md), [workflows.md](workflows.md), and [CONTRIBUTING.md](CONTRIBUTING.md) are contributor documents.

## Installation

ashlar-chess requires JDK 17 or later. It is published to Maven Central. See the
[README dependency section](README.md#dependency) for the current Maven and Gradle coordinates.

From 20.0.0 onward, ashlar-chess is a named JPMS module:

```java
module your.module {
    requires io.github.dlbbld.ashlarchess;
}
```

The module exports only the documented public API packages. Unsupported internals are inaccessible to modular consumers.

## Board Basics

`Board` is the central public type. It represents a chess game, not only a piece placement: it keeps the current
position, move history, legal moves, SAN/LAN strings, repetition state, halfmove clock, castling-right facts, and
rule-level predicates.

Use one `Board` per thread. It is mutable and not thread-safe.

The examples below are the fastest tour through the everyday board API.

### Creating a Board

Common entry points:

- `new Board()` — initial position.
- `Board.fromFenStrict(String)` — strict FEN import.
- `Board.fromFenLenient(String)` — tolerant FEN import.
- `new Board(Fen)` — start from an already parsed FEN value.

<!-- readme:code id=board-creation -->

Move execution:

- `moveStrict(String)` parses and plays canonical SAN.
- `moveLenient(String)` parses and plays a tolerated SAN spelling, returning forgiven items.
- `movesStrict(String...)` and `movesLenient(String...)` play a sequence.
- `move(MoveSpecification)` plays a programmatic move specification.
- `unmove()` undoes the last move.

<!-- readme:code id=board-move-entry-points -->

Move execution validates the requested move against the current legal-move set. Checkmate and stalemate naturally reject
further moves because no legal moves remain. Other draw states such as fifty-move, fivefold repetition, and dead-position
analysis are queryable; the caller decides when to adjudicate.

<!-- readme:code id=board-state-queries -->

## Move Values

`MoveSpecification` is the move request value. Ordinary moves are created from their source and destination squares.
Promotions additionally carry a `PromotionPieceType`. Castling is created from the `CastlingMove` enum:
`new MoveSpecification(CastlingMove.KING_SIDE)` or `new MoveSpecification(CastlingMove.QUEEN_SIDE)`.

`LegalMove` is the validated move value returned by legal-move APIs and performed-move history. It exposes conveniences
such as `isCapture()`, `isCastling()`, `isPromotion()`, `isEnPassant()`, and `enPassantCapturedPawnSquare()`.

### Castling

Create castling requests with the `CastlingMove` enum. When you need the king and rook squares touched by the move,
resolve them from `CastlingMove` and the moving side:

<!-- readme:code id=castling-geometry -->

### En Passant

For an en-passant move, the captured pawn is not on the move destination square. Read it from the validated
`LegalMove.enPassantCapturedPawnSquare()` rather than deriving it from `MoveSpecification.toSquare()`.

## Rule Reports

`Reporter.report(String)` parses a PGN and returns human-readable rule reports for threefold claims, actual
repetitions, fifty-move claims, and fifty/75-move sequences.

### Threefold Claim Ahead

This game ended with a threefold repetition claim ahead according to
[Wikipedia](https://en.wikipedia.org/wiki/Threefold_repetition#Portisch_versus_Korchnoi,_1970):

<!-- readme:code id=threefold-claim-ahead -->

Output:

<!-- readme:output id=threefold-claim-ahead -->

Black could have claimed a threefold by writing, but not yet playing, `25... Qb5`.

### Threefold On The Board

This game contains a threefold repetition according to
[Wikipedia](https://en.wikipedia.org/wiki/Threefold_repetition#Capablanca_versus_Lasker,_1921):

<!-- readme:code id=threefold-on-board -->

Output:

<!-- readme:output id=threefold-on-board -->

The letters `A`, `B`, `C`, ... distinguish different repeated positions in order of first occurrence.

### Fifty-Move Runs

This game reaches a long no-progress run according to
[Wikipedia](https://en.wikipedia.org/wiki/Fifty-move_rule#Karpov_vs._Kasparov,_1991):

<!-- readme:code id=fifty-move -->

Output:

<!-- readme:output id=fifty-move -->

Each bracket gives the moves by each player since the last capture or pawn move, written `(White/Black)`.

## Game Adjudication

Flagfall and resignation are external losing events, but FIDE gives the same exception in both cases: the game is drawn
if the opponent could not have checkmated by any possible series of legal moves.

`Adjudicator` applies that exception. You pass the board and the side whose flag fell or who resigned.

Each event has a quick and full variant:

- Quick: bounded, live-play-safe, rules `DRAW` or `LOSS`.
- Full: deeper analysis, rules `DRAW`, `LOSS`, or `UNDETERMINED`.

### Flagfall

<!-- readme:code id=adjudication-flagfall-quick -->

The quick variant draws when it can prove the opponent cannot win. Otherwise the flag stands.

<!-- readme:code id=adjudication-flagfall-full -->

The full variant can also prove wins and can report `UNDETERMINED` when its search bound is exhausted.

### Resignation

<!-- readme:code id=adjudication-resignation -->

## Dead Position During Play

Adjudication above corrects the final result after an external event. A dead position is different: under FIDE 5.2.2,
the game is drawn at the moment neither side can checkmate by any possible series of legal moves.

ashlar-chess does not run the analyzer automatically after each move. Callers that want this termination point query it:

<!-- readme:code id=dead-position-during-play -->

The quick dead-position check is computationally cheap. Running it during live play is a product decision: it gives the
exact FIDE termination moment, while checking only at flagfall/resignation still preserves the final result because a
dead position cannot later become winnable.

## Unwinnability API

A position is unwinnable for a side if no legal sequence can end with that side giving checkmate, even if the opponent
cooperates. A dead position is unwinnable for both sides.

The quick analyzers prove unwinnability cheaply. The full analyzers search deeper and can prove concrete wins or report
`UNDETERMINED` when the search limit is exhausted.

### Legal positions only

These analyses are defined for — and guaranteed only on — **legal positions** (positions reachable from the start by
legal moves). That is exactly what game play and PGNs produce, so it covers the intended use: flag-fall / resignation
adjudication and dead-position detection. ashlar cannot validate full legality — a complete check would need an
infeasible retrograde search — so **submitting only legal positions is your responsibility.** On an illegal position
the result is undefined and the quick and full verdicts may disagree; in practice it is still correct on the large
majority of illegal positions, with only a small set of unreachable constructions decided wrongly (for example an
impossible double-bishop-check mate called unwinnable, or certain illegal `KBNvK` / `KBBvK` positions reported
`WINNABLE`). Such positions never arise in a legally played game and are out of scope.

### Reading Quick Verdicts

The verdict enums are proof results, not booleans. Read the exact constant:

- Side-specific quick: `UNWINNABLE` means proven unwinnable; `POSSIBLY_WINNABLE` means not proven unwinnable.
- Dead-position quick: `DEAD` means both sides are proven unwinnable; `POSSIBLY_ALIVE` means at least one side was not
  proven unwinnable.

Do not read `!= UNWINNABLE` as “winnable”; it also includes undecided states.

### Reading Full Verdicts

The full side-specific verdict has three public outcomes:

- `WINNABLE` means proven winnable.
- `UNWINNABLE` means proven unwinnable.
- `UNDETERMINED` means the full search stopped at its bound.

When you call `UnwinnableFullAnalyzer` directly, `UnwinnabilityFullAnalysis` also tells you how a `WINNABLE` result was
proved: `winnableProof()` returns `WinnableProof.THEOREM` for theorem-certified wins and `WinnableProof.HELPMATE` for
searched wins (where `mateLine()` carries a concrete UCI helpmate line). The `Board.unwinnableFull(Side)` convenience
method exposes only the public verdict.

Dead-position full uses its own whole-position vocabulary: `DEAD`, `ALIVE`, or `UNDETERMINED`.

### Side-Specific Examples

Insufficient material:

<!-- readme:code id=unwinnable-insufficient-material -->

Forced moves:

<!-- readme:code id=unwinnable-forced-moves -->

Pawn walls:

<!-- readme:code id=unwinnable-pawn-walls -->

Common positions where quick leaves the question open and full proves a win:

<!-- readme:code id=unwinnable-common-positions -->

Blocked positions the quick algorithm proves:

<!-- readme:code id=unwinnable-blocked-quick -->

### Dead-Position Examples

Insufficient material:

<!-- readme:code id=dead-insufficient-material -->

Pawn walls:

<!-- readme:code id=dead-pawn-walls -->

Forced moves:

<!-- readme:code id=dead-forced-moves -->

Positions quick does not prove dead:

<!-- readme:code id=dead-possibly-alive -->

For quick side-specific unwinnability, `POSSIBLY_WINNABLE` is intentionally conservative: it means "not proven
unwinnable". In the project statistics, more than 99.99% of these quick-open positions are in fact winnable, but callers
that need a proof should use the full verdict.

## Validation Model

The library validates the three notations it consumes:

- SAN: moves in Standard Algebraic Notation.
- FEN: single-position import/export.
- PGN: game import/export, with SAN movetext and optional FEN tags.

Each domain has a strict and lenient public path.

Strict means canonical input. Lenient means ashlar-chess accepts a defined set of recoverable syntactic deviations,
reports them as typed forgiven items, and then applies the same chess validation as strict input. Lenient does not turn
illegal chess into legal chess.

## SAN Validation

Strict SAN accepts canonical SAN only:

- uppercase piece letters;
- lowercase files;
- canonical disambiguation;
- `=Q`-style promotion;
- `O-O` / `O-O-O` castling with letter `O`;
- correct optional `+` / `#` suffix.

Use it through `StrictSanParser.parse(String, Board)` or `Board.moveStrict(String)`.

Lenient SAN accepts common real-world spelling deviations when they uniquely identify a legal move. Use it through
`LenientSanParser.parse(String, Board)` or `Board.moveLenient(String)`.

Accepted deviations are reported with typed codes
(`io.github.dlbbld.ashlarchess.san.LenientSanValidationProblem`):

| Category | Code | Example |
|---|---|---|
| **Suffix mismatch** (6) | `MISSING_CHECK_SUFFIX` | `Nd7` when actually check |
| | `MISSING_CHECKMATE_SUFFIX` | `Nd7` when actually mate |
| | `SPURIOUS_CHECK_SUFFIX` | `Nd7+` when not check |
| | `SPURIOUS_CHECKMATE_SUFFIX` | `Nd7#` when not mate |
| | `WRONG_CHECK_SUFFIX_FOR_CHECKMATE` | `Nd7+` when actually mate |
| | `WRONG_CHECKMATE_SUFFIX_FOR_CHECK` | `Nd7#` when only check |
| **Capture marker** (2) | `MISSING_CAPTURE_MARKER` | `Be5` (piece) or `ed5` (pawn) when actually a capture |
| | `SPURIOUS_CAPTURE_MARKER` | `Bxe5` when destination empty |
| **Disambiguation** (4) | `OVERSPECIFIED_FILE_DISAMBIGUATION` | `Nbd7` when `Nd7` would suffice |
| | `OVERSPECIFIED_RANK_DISAMBIGUATION` | `N3d7` when `Nd7` would suffice |
| | `OVERSPECIFIED_SQUARE_DISAMBIGUATION` | `Nb3d7` when less would suffice |
| | `NON_STANDARD_RANK_DISAMBIGUATION` | `R1d4` where canonical uses file (`Rad4`) |
| **Notation form** (4) | `LONG_ALGEBRAIC_NOTATION` | `e2-e4`, `Nb1-d7` |
| | `UCI_NOTATION` | `e2e4`, `e7e8q`, `e1g1` (castling) |
| | `ZERO_INSTEAD_OF_O_CASTLING` | `0-0`, `0-0-0` |
| | `EXPLICIT_PAWN_LETTER` | `Pe4` |
| **Promotion form** (1) | `MISSING_PROMOTION_EQUALS` | `e8Q` |
| **Case variation** (4) | `LOWERCASE_PIECE_LETTER` | `nf3` |
| | `UPPERCASE_FILE_LETTER` | `NF3` |
| | `UPPERCASE_CAPTURE_MARKER` | `BXe5` |
| | `LOWERCASE_PROMOTION_PIECE` | `e8=q` |

Codes are not collapsed: each distinguishable deviation has its own code, and a single move can carry multiple codes
(for example, `nbxd7+` when actually mate emits `LOWERCASE_PIECE_LETTER`,
`OVERSPECIFIED_FILE_DISAMBIGUATION`, and `WRONG_CHECK_SUFFIX_FOR_CHECKMATE`).

Mixed castling such as `0-O` is rejected. Pawn spurious capture marker recovery is also rejected because it would
silently change the user's intended pawn.

The PGN lenient parser routes movetext SAN through this same lenient SAN layer:

<!-- readme:code id=pgn-san-tolerances -->

Output:

<!-- readme:output id=pgn-san-tolerances -->

## FEN Validation

Strict FEN entry points:

- `StrictFenParser.parse(String)`
- `StrictFenParser.validate(String)`
- `Board.fromFenStrict(String)`

Strict FEN requires canonical six-field FEN and validates the position:

- piece placement is eight legal ranks and each rank has exactly eight squares;
- each side has exactly one king and physically possible piece counts;
- no pawn on rank 1 or rank 8;
- the side that just moved is not still in check;
- side to move is `w` or `b`;
- castling rights are canonical and consistent with king/rook placement;
- en-passant target square is structurally and legally possible;
- halfmove clock is non-negative, zero when an en-passant target is set, and consistent with the fullmove number;
- fullmove number is positive and within the library's maximum.

Lenient FEN entry points:

- `LenientFenParser.parse(String)`
- `LenientFenParser.validate(String)`
- `Board.fromFenLenient(String)`

Lenient FEN performs syntactic recovery, reports every forgiven item, then runs strict validation. It tolerates:

- leading/trailing whitespace;
- extra whitespace between fields;
- tab/newline field separators;
- missing halfmove/fullmove counters;
- uppercase side-to-move or en-passant file;
- non-canonical castling order;
- non-ASCII dash for no en-passant square;
- trailing garbage token;
- halfmove/fullmove inconsistency, corrected by reconstructing a conservative fullmove number.

It does not forgive semantic impossibilities such as missing kings, pawns on illegal ranks, impossible double-checks, or
illegal en-passant geometry.

Lenient PGN routes its `FEN` tag through lenient FEN. Strict PGN routes the `FEN` tag through strict FEN.

## PGN Functionality

### Limitations

PGN support intentionally does not include:

- recursive annotation variations;
- Numeric Annotation Glyphs such as `$1`;
- multi-game PGN files.

UTF-8 byte-order marks are accepted by the lenient parser and rejected by the strict parser. PGN move suffix annotations
(`!`, `?`, `!!`, `??`, `!?`, `?!`) are parsed, modeled, and round-tripped by both parsers.

### Parse Model

PGN handling separates four jobs:

- Parse: read text into `PgnGame`.
- Validation reporting: expose tolerated deviations.
- Semantic export: write the parsed model honestly, preserving tag presence/order and termination-marker presence.
- Archival export: produce PGN spec §8.1.1 archival output with filled/sorted Seven Tag Roster.

The default export mode is semantic. Archival export is opt-in via `WriteMode.ARCHIVAL`.

### Lenient PGN Parser

The lenient parser accepts real-world PGN shape issues such as whitespace variation, missing tags, optional termination
markers, SAN spelling deviations, and deficient FEN tags.

<!-- readme:code id=pgn-lenient-valid -->

Transformation to export format:

<!-- readme:code id=pgn-lenient-export-transform -->

Output:

<!-- readme:output id=pgn-lenient-export-transform -->

Invalid input produces typed exceptions with detailed messages:

<!-- readme:code id=pgn-lenient-invalid -->

Output:

<!-- readme:output id=pgn-lenient-invalid -->

File parsing:

<!-- readme:code id=pgn-lenient-file-parsing -->

### Strict PGN Parser

The strict parser enforces the PGN import-format syntax plus ashlar-chess's semantic essentials: the Result tag must be
present and match the termination marker, and SetUp/FEN coupling must hold.

It does not require the full Seven Tag Roster for parsing; that is an archival-output rule.

Valid strict PGN:

<!-- readme:code id=pgn-strict-valid -->

Invalid syntax:

<!-- readme:code id=pgn-strict-invalid-syntax -->

Output:

<!-- readme:output id=pgn-strict-invalid-syntax -->

Invalid form:

<!-- readme:code id=pgn-strict-invalid-form -->

Output:

<!-- readme:output id=pgn-strict-invalid-form -->

File parsing:

<!-- readme:code id=pgn-strict-file-parsing -->

### PGN Creation And Export

Create a `PgnGame` from a `Board`:

<!-- readme:code id=pgn-create-game -->

Output:

<!-- readme:output id=pgn-create-game -->

Format a `PgnGame` as text:

<!-- readme:code id=pgn-format -->

Write a PGN to the file system:

<!-- readme:code id=pgn-export -->

### PGN Validation

Lenient validation:

<!-- readme:code id=pgn-lenient-validation-valid -->

<!-- readme:code id=pgn-lenient-validation-invalid -->

Output:

<!-- readme:output id=pgn-lenient-validation-invalid -->

File validation:

<!-- readme:code id=pgn-lenient-validation-file -->

Strict validation:

<!-- readme:code id=pgn-strict-validation-valid -->

<!-- readme:code id=pgn-strict-validation-invalid -->

Output:

<!-- readme:output id=pgn-strict-validation-invalid -->

File validation:

<!-- readme:code id=pgn-strict-validation-file -->

## Specification Cross-References

Use [specification.md](specification.md) when you need the exact project contract:

- §3.1: FIDE rule fidelity and game termination.
- §3.3.1: full lenient SAN taxonomy and algorithm.
- §3.3.2: PGN parse model and write modes.
- §3.3.3: FEN parser tiers and strict invariants.
- §5: PGN deviations from the PGN specification.
- §6: testing strategy and release-gate rationale.
