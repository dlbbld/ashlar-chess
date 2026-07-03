# ashlar-chess — Specification

The **technical specification** for ashlar-chess: design goals, architecture, philosophy, and the rule-level decisions that make the library what it is. Meant for someone (including future-self) who needs to understand *why* the library is shaped the way it is. User-facing how-to documentation lives in `manual.md`; `README.md` is the project front door.

---

## Manifest

ashlar-chess is not trying to be a broad, all-feature chess toolkit. It is trying to be a deeply correct, **finished**, essential orthodox-chess rules library.

By a checklist of "major features found in large chess libraries," ashlar-chess will intentionally miss things: Chess960, full PGN variation-tree / RAV workflows, engine integration, tablebases, opening books, GUI-oriented features, and other advanced or niche use cases. That is not a failure of the project scope. It **is** the scope.

The goal is an essential orthodox-chess rules library, where "essential" is defined deliberately and personally: the parts considered central to rule correctness and trustworthy chess data handling. The core feature set is fixed:

- orthodox chess only;
- strict and lenient FEN / SAN / PGN handling for the use cases ashlar supports, with explicit, instructive, educative validation messages;
- precise legal-move validation and move execution;
- exact game-state and rule predicates;
- FIDE-relevant draw and termination handling;
- unwinnability / dead-position analysis (the FUN 2022 algorithm) as a first-class rule-correctness domain;
- strong PGN/FEN corpus testing and differential / oracle testing;
- high-quality public API naming, packaging, documentation, and invariants.

For these chosen domains, the quality bar is very high. ashlar-chess should be Class-A not because it does everything, but because the things it chooses to do are implemented with exceptional correctness, consistency, and maintainability.

The guiding principle is therefore not "add every feature serious chess libraries have." It is: build the fixed essential feature set, make it internally coherent, FIDE-faithful where applicable, thoroughly tested, well documented, and pleasant to use — then **finish** it. This project should not grow forever. It should reach a stable, finished shape; after that, work is mostly quality improvement, bug fixes, documentation, performance where it matters, and API clarity — not endless feature expansion.

---

## 1. Purpose & non-goals

ashlar-chess is a Java chess library focused on **rule correctness, production usability, and reproducible validation**. Its flagship feature is an independent, paper-derived implementation of Miguel Ambrona's FUN 2022 unwinnability algorithm (the paper behind the [Chess Unwinnability Analyzer](https://github.com/miguel-ambrona/chasolver)), to the author's knowledge the only published practical algorithm for unwinnability and dead-position questions beyond material heuristics.

The library is **not**:

- A chess engine — it does not search for best moves.
- A move-generation benchmark library — performance matters, but the public API is shaped around rule fidelity, diagnostics, and reproducible behaviour rather than raw nodes per second.
- A complete tournament-management toolkit — clock handling, draw-offer state machines, and similar interactive concerns belong outside the library (scope of the companion `dumb-chessboard` project).

In spirit it is now a **production-usable rule library**: built from scratch for auditability, but using optimized runtime representations where the library needs state-of-the-art hot-path performance to compete with real chess software expectations.

---

## 2. Philosophy

### 2.1 Correctness first, optimized runtime, reproducible tests

Other Java chess libraries are correct too — ashlar-chess does not claim a correctness advantage over them. What differs is the engineering contract: production code may use optimized representations, but every optimized rule primitive must remain explainable and be backed by an independent reference path or external oracle.

The project moved in phases. The first implementation was correctness-first and mailbox-shaped (`StaticPosition`), deliberately easy to inspect. That implementation was not thrown away when production moved to bitboards; it was relocated into `src/test/` and promoted to the permanent differential-test oracle. Reproducibility therefore now lives primarily in the tests and fixtures, while production code is allowed to be faster. This is the core bargain: the runtime can be optimized, mutable, and competitive where it must be, because the from-scratch reference path remains highly tested and close enough to the rules to keep the optimized path honest.

Concrete examples:

- **Move history stores derived facts directly.** Whether a move was a two-square pawn advance or an en passant capture is recorded in the move history rather than recomputed from the position when needed. Engines like Stockfish compute these on demand because the savings matter at engine speeds. ashlar-chess stores them because the resulting code is shorter, more obviously correct, and easier to maintain.
- **The representation split follows from this bargain** — bitboard piece placement for production, a deliberately rich `Board` game object, and a mutable search board (`HelpmateSearchBoard`) for the unwinnability-search hot path, each kept honest against a readable reference. The three representations and their trade-offs are detailed in §4.1.
- **Repetition and public state prefer transparent semantics.** Position equality follows the FIDE definition directly: repetition keys on the exact `DynamicPosition` record and the helpmate transposition table on the exact `HelpmateSearchKey` — exact value records, never a hash trusted as the rule fact.

The resulting rule is: optimize where production use needs it, but make the optimization answer to a readable oracle. Where the FIDE Laws are ambiguous, the library follows the most rule-faithful reading.

### 2.2 Functional style and compile-time guarantees

The codebase uses Java's type system as far as is practical: records, immutable value objects, closed enums, null annotations, and explicit result types. The goal is to encode as many invariants as possible in types, so illegal states are hard to construct and ordinary compiler checks catch more mistakes. Mutable state is confined to a small number of well-defined classes: `Board` for the public game state, and package-private search machinery such as `HelpmateSearchBoard` where the unwinnability searches need make/unmake performance. Where Java forces compromise (mutable accessors, nullable JDK return types, search-hot-path mutation), the compromises are localised and crossed with explicit annotations and tests.

Concretely:

- **Records as value objects** (`PgnCommentary`, `Fen`, `Tag`, `PgnMove`, `PgnGame`, `MoveSpecification`). Where a record carries a non-trivial textual or grammatical contract — `PgnCommentary` is the load-bearing example — the compact constructor enforces it, and downstream code does not re-validate. For records whose invariants are field-level (`Fen`, `Tag`, `PgnMove`), validation lives one layer out, at the parser/factory boundary (`StrictFenParser`, `LenientPgnParser`, `StrictPgnParser`); a record never holds something that came in from outside the library without first passing through one of those entry points. `PgnGame`'s compact constructor performs defensive copies of its list components so the immutability claim holds end-to-end. The end result is the same — errors at construction time — but the boundary is occasionally one method out from the record itself.
- **Heavy enum use** for closed domains (`Side`, `Piece`, `Square`, `File`, `Rank`, `MoveSuffixAnnotation`, `ResultTagValue`, etc.) — the compiler enforces exhaustive `switch` handling.
- **Eclipse JDT null annotations** (`@NonNull` / `@Nullable`) used pervasively, with the Eclipse JDT compiler configured so violations are errors. (This is a JDT-toolchain guarantee enforced in the IDE/ECJ build; the Maven `javac` build does not re-check null annotations.) Null is a typed concern, not a runtime accident.
- **No reflection in the rule core.** What the type system says is what runs.

The result is a codebase where a substantial class of bugs — null-dereference, unhandled enum case, mutated-after-construction — cannot reach runtime.

### 2.3 Diagnostic quality

When validation fails, the library produces messages a human can act on. Each problem has a typed code (e.g. `StrictPgnParserValidationProblem.MOVETEXT_MOVE_NUMBER_REQUIRED_AFTER_COMMENTARY`) plus a human-readable message naming the offending construct, its position, and what was expected. Generic "illegal move" / "parse error" responses are avoided wherever a more specific category fits. This applies uniformly to SAN, FEN, and PGN validation, and to both SAN pipelines (programmatic and PGN-driven).

### 2.4 Thread-safety

The library makes only modest thread-safety guarantees, all of them honest about the underlying types:

- **`Board` is mutable and not thread-safe.** Use one `Board` per thread, or synchronize externally. `Board.equals` / `Board.hashCode` reflect current game state, so a `Board` placed in a `HashMap` or `HashSet` and then mutated will violate the collection's invariants.
- **Records are immutable and thread-safe.** `Fen`, `PgnGame`, `PgnMove`, `MoveSpecification`, `PgnCommentary`, `Tag`, `Outcome`, etc. — once constructed, they can be freely shared.
- **Static utility classes are stateless and thread-safe.** `Reporter`, `PgnCreate`, `KnightDistance`, the various `*Validation` and `*Utility` classes — all entry points are static methods on stateless classes. Multiple threads can call them concurrently.
- **Parsers expose stateless static entry points.** `StrictPgnParser.parseText(String)` / `StrictPgnParser.parsePath(Path)` and the lenient counterparts construct a fresh parser instance per call internally; the parser instances themselves carry per-parse state and should not be shared. Stick to the static entry points.

In short: share records and call static utilities from anywhere; never share a `Board`.

---

## 3. Feature specification

### 3.1 FIDE rule fidelity and game termination

The library follows the FIDE Laws of Chess closely. Termination is **information, not enforcement**: the move-validation pipeline does not consult any game-end predicate, and every FIDE termination is surfaced as a queryable artifact the caller polls to decide whether to adjudicate. Termination modes:

- **Automatic (queryable)** — FIDE ends the game; no claim required. Surfaced by `board.outcome()` as a non-null `Outcome` record carrying a `Termination` and the winner for checkmate. The move pipeline still accepts further input at these states; the natural barrier at checkmate / stalemate is the empty legal-move set, so any subsequent move attempt fails through ordinary legality.
- **Claimable** — FIDE permits but does not require the side-to-move to claim the draw; the game continues until claimed. Surfaced via dedicated on-board and with-move predicates on `Board`, not via `Outcome`.

| Rule | Mode | FIDE article |
|---|---|---|
| Checkmate | automatic | 5.1 |
| Insufficient material (structural) | automatic | 5.2.2 / 9.4 |
| Stalemate | automatic | 5.2.1 |
| 75-move rule | automatic | 9.6.2 |
| Fivefold repetition | automatic | 9.6.1 |
| Dead position by quick unwinnability | analyzer-driven, opt-in | 5.2.2 |
| Threefold repetition | claimable | 9.2 |
| 50-move rule | claimable | 9.3 |

The automatic rows are listed in the precedence order `board.outcome()` applies (python-chess parity): when two or more apply to the same position, the higher row wins. A KBvK stalemate, for instance, reports `INSUFFICIENT_MATERIAL`, not `STALEMATE`. Structural insufficient material is detected by a fast structural test (king-vs-king, king + minor vs king, etc.); analyzer-driven dead positions (FIDE 5.2.2 via the unwinnability analyzer) are intentionally not invoked by `board.outcome()` — the analyzer would silently make every status query expensive — so callers that want that verdict call `Board.deadPositionQuick()` or `Board.deadPositionFull()` (backed by `DeadPositionAnalyzer`) directly.

Single-side insufficient material (one side lacks mating material but the other does not) is a diagnostic state of the position, not a termination, and is not surfaced by `Outcome`. Callers query `Board.isInsufficientMaterial(Side)` directly when they need it.

For the claimable rules, the library exposes three predicate shapes per rule:

1. **On-board** (`Board.isFiftyMove()`, `Board.isThreefoldRepetition()`) — the current position satisfies the rule.
2. **With-move existence** (`Board.canClaimFiftyMoveRuleWithOwnMove()`, `Board.canClaimThreefoldRepetitionRuleWithOwnMove()`) — *some* legal move would satisfy the rule if played.
3. **Per-move** (`Board.canClaimFiftyMoveRuleFor(MoveSpecification)`, `Board.canClaimThreefoldRepetitionRuleFor(MoveSpecification)`, composed `Board.canClaimDrawFor(MoveSpecification)`) — *this specific announced move* would satisfy the rule.

The per-move shape is the FIDE-faithful API. FIDE 9.2 and 9.3 frame the claim as a per-move act: the player announces the move they intend to play and claims the draw on that announcement; the move is not played beyond the announcement. The on-board and with-move existence shapes are convenience derivations — yes/no answers about the current position or its legal-move set, without requiring the caller to name a candidate.

ashlar-chess inherited the on-board and existence shapes from the python-chess reference oracle, which exposes those two but not a per-move predicate (`board.can_claim_fifty_moves()` takes no move parameter). The per-move shape was added in ashlar-chess release 16.0.0 to close the gap. python-chess's collapsed shape was also the cause of the candidate-move-is-mate edge that ashlar-chess took the strict FIDE 9.3 reading on in 15.0.0; the per-move predicate makes the corner case visible at the API surface rather than buried inside an existence check. Cross-library context tracked upstream at [niklasf/python-chess#1188](https://github.com/niklasf/python-chess/issues/1188).

The library produces analysis output that names which moves *would* satisfy the claim — surfacing missed claim opportunities that other libraries do not. The claim-ahead report's per-move entries are computed via the per-move predicate, so the report and the API share a single source of truth.

Position equality follows the FIDE definition: same piece placement, same side to move, same castling rights, same en-passant possibilities.

### 3.2 Unwinnability — the FUN 2022 algorithm

The library's **flagship feature**. A position is *unwinnable for a side* if no helpmate exists for that side. A *dead position* is one unwinnable for both sides. Insufficient material covers the trivial cases; positions like blocked pawn walls, certain wrong-bishop endgames, and many forced-only-moves continuations are dead but not insufficient — and most chess libraries get them wrong.

Miguel Ambrona's FUN 2022 algorithm (*A Practical Algorithm for Chess Unwinnability*) is, to the author's knowledge, the only published practical algorithm for these cases beyond material heuristics. Since 22.0.0 ashlar-chess carries its own independent, paper-derived implementation, governed by the committed specification `fun22-spec.pdf` (it replaced the earlier Java port of Ambrona's C++ CHA/D3-Chess implementation). Two variants:

- **Quick** — the paper's Figure 10: structural and bounded, three-valued: `UNWINNABLE`, `WINNABLE` (only on quickly matable positions), or `POSSIBLY_WINNABLE`. Sound but not complete — a definite verdict is always correct, and `POSSIBLY_WINNABLE` asserts nothing.
- **Full** — the paper's Figure 9: deep search, three-valued: `WINNABLE`, `UNWINNABLE`, or `UNDETERMINED`. The direct
  analysis record carries the concrete mate line witnessing a `WINNABLE` verdict. The undetermined case is bounded by
  a 500 000-position limit; most positions resolve well below that.

`Dead position` is the symmetric whole-position notion, decided by `DeadPositionAnalyzer` (and the `Board.deadPositionQuick()` / `Board.deadPositionFull()` convenience methods): a position is dead exactly when it is unwinnable for both sides. It carries its own verdicts — `DeadPositionQuickVerdict` (`DEAD` / `POSSIBLY_ALIVE`) and `DeadPositionFullVerdict` (`DEAD` / `ALIVE` / `UNDETERMINED`) — rather than reusing the per-side unwinnable vocabulary.

The direct side-specific analyzers return analysis records. For full analysis, `UnwinnabilityFullAnalysis` carries the
proof behind the simple public verdict: `mateLine()` is the UCI helpmate line the search exhibited (replayable from the
input position), empty exactly when the submitted position is already a checkmate delivered by the intended winner —
a zero-move helpmate. A non-winnable verdict never carries a line. The `Board.unwinnableQuick(Side)` and
`Board.unwinnableFull(Side)` convenience methods expose only the verdict.

**Terminal positions.** The analyzers are *total* — they are never rejected on a finished game; they return a verdict, following the paper's `Find-Helpmate` base cases (Figure 5). An already-**checkmate** position is `WINNABLE` for the side that delivered mate — a zero-move helpmate with an **empty** `mateLine()` — and `UNWINNABLE` for the mated side. A **stalemate** is `UNWINNABLE` for both sides (no legal move can continue toward a mate). Consequently a dead-position query reports stalemate as `DEAD` and an already-delivered checkmate as not dead. In normal use the analyzer is not invoked on a finished game (the result is already known via `board.outcome()`); this behaviour matters only when a terminal position is analyzed directly, as the reference oracles do.

Side-specific quick/full unwinnability queries and whole-position dead-position queries are caller-invoked; no analyzer runs automatically during construction or move execution.

### 3.3 SAN, FEN, PGN

- **SAN** — two pipelines: **strict** (canonical SAN only; reached from `Board.moveStrict(String)` and from the PGN-driven path) and **lenient** (accepts a defined set of forgivable deviations from canonical; reached from `Board.moveLenient(String)`). See §3.3.1 for the lenient taxonomy and algorithm.
- **FEN** — strict and lenient public entry points. `StrictFenParser` (`Board.fromFenStrict(String)`) performs the full strict parse plus structural/rule-consistency validation; `LenientFenParser` (`Board.fromFenLenient(String)`) runs a syntactic-tolerance pre-pass (whitespace, casing, missing trailing counters, non-canonical castling order, non-ASCII dashes, trailing garbage, and the halfmove-clock / fullmove-number inconsistency) and then delegates to the strict parser. The halfmove clock is never capped — the FIDE 75-move rule is a queryable `Board` predicate, not a FEN-import limit. See §3.3.3 for the full strict-invariant list and the forgiven-code table.
- **PGN** — two parsers, both **preserving input as given**: **strict** (enforces the spec's import-format syntax, plus the semantic essentials: Result tag presence, SetUp/FEN coupling) and **lenient** (tolerates real-world PGN — spaced move-number indicators, missing seven-tag-roster entries, optional termination markers, extra whitespace, and the annotation constructs analysis exports carry: `[%eval]` / `[%clk]` command comments, multiple comments on one move, and recursive annotation variations). Both produce the same `PgnGame` model; neither normalises the tag list. **Move annotations are modelled uniformly as NAGs** (numeric annotation glyphs, `Nag`): the PGN standard (§8.2.3.8) defines the six symbolic suffix glyphs (`!` `?` `!!` `??` `!?` `?!`) as import-format shorthand for NAG codes `1`–`6`, so both parsers fold a glyph into the move's NAG list — `e4?` and `e4 $2` yield the same `Nag(2)`. Codes without a glyph (positional, time, chess.com's `$9`, …) are carried as NAGs too, so nothing is discarded; `PgnMove.moveSuffixAnnotation()` is a convenience that reads the first assessment NAG back as its glyph. On export the assessment codes render as the readable glyph and the rest as `$N`, so annotations round-trip. RAV side-lines `(...)`, by contrast, are *skipped* by the lenient parser to recover the mainline — consistent with the variation-tree non-goal, ashlar reads the game that was played rather than modelling engine side-lines. The exporter has two modes: **semantic** (the default, emits the parse model as-given) and **archival** (PGN spec §8.1.1-conformant output, opt-in via `WriteMode.ARCHIVAL`). See §3.3.2 for the parse/validate/export contract. The two-parser split is deliberate: a single parser with a "strictness flag" inevitably grows conditional branches that obscure both rule sets — splitting keeps each parser readable and lets the two evolve independently.

#### 3.3.1 Lenient SAN

The strict SAN pipeline (`StrictSanParser`, reached via `Board.moveStrict(String)`) accepts only canonical SAN: file-preferred disambiguation, uppercase piece letter and lowercase file letter, the `=Q` promotion form, `O-O` / `O-O-O` castling, and an optional `+` / `#` suffix that must match the actual board state. Real-world PGN — ChessBase output, hand-edited files, engine traces — routinely deviates in forgivable ways.

The lenient SAN pipeline (`LenientSanParser`, reached via `Board.moveLenient(String)`) accepts these deviations when they uniquely identify a legal move. Every accepted deviation surfaces as a typed `ForgivenItem` carrying the deviation code, the original token, and the canonical-SAN equivalent — so consumers can silently accept or warn.

**Principle: canonical-first.** A string that already parses as canonical SAN never receives a different meaning under lenient — strict is tried first; only on rejection does the lenient layer engage. So `bxc6` always means pawn capture from the b-file, even if a bishop on the b-file could also capture on c6.

**Taxonomy — 21 codes** (`io.github.dlbbld.ashlarchess.san.LenientSanValidationProblem`):

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

Codes are not collapsed: each distinguishable deviation has its own code, and a single move can carry multiple codes (e.g. `nbxd7+` when actually mate emits `LOWERCASE_PIECE_LETTER` + `OVERSPECIFIED_FILE_DISAMBIGUATION` + `WRONG_CHECK_SUFFIX_FOR_CHECKMATE`).

**Algorithm — two-phase.** *Phase 1 (shape normalization)* performs pure-string transforms plus board-aware UCI translation (look up piece on from-square): castling-zero, mixed-castling rejection, `P`-stripping, hyphen-stripping (LAN), UCI form translation, missing-`=` insertion, all four case fixups. LAN and UCI are mutually exclusive at the input level — a hyphen means LAN (`LONG_ALGEBRAIC_NOTATION` only), no hyphen means UCI shape (`UCI_NOTATION` only). *Phase 2 (semantic recovery)* feeds the normalized candidate to the strict pipeline and, on a recoverable rejection (terminal-marker mismatch, capture-marker mismatch, over-specification, non-standard disambig), mutates the candidate and retries. Each lenient code can fire at most once per parse, bounding the loop.

**API.** `LenientSanParser.parse(String, Board)` returns a `LenientSanParseResult` (move + forgiven items); it validates by construction, throwing `LenientSanParserValidationException` on an unrecoverable input. `Board.moveLenient(String)` returns the same result type, so it also surfaces the forgiven items.

**Deliberate non-recoveries.** Two categories are rejected even by the lenient pipeline:
- **Mixed castling** (`0-O`, `O-0`) — no real-world tool emits this; allowing it would add parser complexity for zero practical value.
- **Pawn `SPURIOUS_CAPTURE_MARKER`** — `dxe5` when e5 is empty has no clean string mutation that yields canonical SAN; the only "recovery" would silently swap the user's intended pawn (d-file) for a different one (e-file). That crosses the line from forgiving sloppiness to overriding intent.

The strict pipeline remains the single source of chess-validation truth. Lenient is a thin input-shape transformation layer that reuses strict for everything else.

#### 3.3.2 PGN parse model and write modes

PGN handling is structured around four separable jobs:

| Job | What it does | Where it lives |
|---|---|---|
| **Parse** | Reads PGN text into a `PgnGame`. Preserves what the source contained — tag presence/absence, tag order, FEN without SetUp, missing Result, redundant initial-position FEN/SetUp, unknown tags. No fabrication into the model. | `StrictPgnParser`, `LenientPgnParser` |
| **Validation reporting** | Surfaces tolerated deviations as typed diagnostics. SAN-level deviations on `sanForgivenItems`; tag-level deviations on `tagForgivenItems` (missing STR, Result tag absent, SetUp/FEN coupling, redundant initial FEN). | `LenientPgnParserValidationResult` |
| **Semantic export** | Emits the parse model as-given. Same tags, same values, same order, same Result presence/absence, same termination-marker presence/absence — no invented content. Formatting trivia is normalised (single-space tag brackets, standard line wrapping). Movetext SAN is canonical (canonicalised at parse time). The default. | `WriteMode.SEMANTIC` |
| **Archival export** | Produces a PGN spec §8.1.1-conformant artifact. The model is run through `PgnArchivalNormalization` first: missing STR entries filled with spec-defined placeholders (`?` for most, `????.??.??` for Date per §8.1.1.3, `*` for Result per §8.1.1.7), SetUp/FEN coupling enforced, redundant initial-position FEN/SetUp dropped, Result tag synthesised from the termination marker, tags sorted into canonical order. Opt-in. | `WriteMode.ARCHIVAL` |

**Strict parser — semantic essentials.** Strict parsing enforces the spec's import-format syntax (single-space-separated tokens, no leading/trailing whitespace per line, etc., unchanged) plus two semantic essentials: the **Result tag must be present** (its value must match the termination marker), and the **SetUp/FEN coupling** must hold (`SetUp "1"` ⇒ FEN present; FEN present ⇒ `SetUp "1"`). The full Seven Tag Roster is **not** a strict-parser mandate: PGN spec §8.1.1 introduces STR as required *"for archival storage of PGN data,"* not for general spec-compliant PGN. A four-tag PGN (Result + a few extras) parses through `StrictPgnParser` cleanly.

**Honest preservation by default.** The principle: parse preserves, validation reports, semantic export echoes, archival export normalises and fills. The library's default posture is honest preservation; archival storage is a mode the caller asks for, not a tax the parser levies. A lenient `parse → semantic-write` round-trips the meaning of the input (tag presence/absence, Result presence/absence, FEN-without-SetUp) while normalising formatting and move spelling — what is intentionally not preserved is the source bytes themselves (whitespace inside tag brackets, original SAN spelling). Source-text-preserving export would be a separate library mode if ever needed; it is out of scope.

**`createPgnGame(Board)`.** The Board → `PgnGame` factory produces the minimal honest shape — empty `tagList` for an initial-position board, `[SetUp, FEN]` for a non-initial position, `terminationMarker` derived from the board's game-status. STR fabrication does **not** happen here; archival export is the only path that fills the roster.

#### 3.3.3 FEN parser tiers and lenient pre-processor

FEN handling exposes two public entry points — strict and lenient. Internally, strict parsing is still staged into a lexical field split and a structural/rule-consistency pass, but those stages are implementation details rather than public API.

| Input kind | Public entry point | Contract |
|---|---|---|
| Strict input | `StrictFenParser.parse` / `StrictFenParser.validate` / `Board.fromFenStrict(String)` | Six-field canonical FEN plus structural and rule-consistency validation (see list below). |
| Lenient input | `LenientFenParser.parse` / `LenientFenParser.validate` / `Board.fromFenLenient(String)` | Normalise recoverable syntactic deviations, report every forgiven item, then run the same strict validation. |

**`StrictFenParser`.** The public strict facade builds on internal lexical parsing and applies these strict invariants:
- Piece placement is 8 ranks (separated by `/`), each containing only `[RNBQKPrnbqkp12345678]`, each evaluating to exactly 8 squares; no trailing or consecutive `/`.
- Piece counts within physical bounds per side (at most one king, at most eight pawns, etc.); each side has exactly one king.
- No pawn on rank 1 or rank 8.
- The opposite side (the side that just moved) is not still in check.
- Side-to-move letter is `w` or `b`.
- Castling rights are a subset of `KQkq` in canonical order (or `-`) and are consistent with the static positions of kings and rooks.
- En-passant target square is `-` or a square on rank 3 (when white-to-move) or rank 6 (when black-to-move); the square one ahead of the target carries an opponent pawn; the starting square (behind the target) is empty; the target square is empty; the previous position (with the pawn back at its starting square) is legal.
- Halfmove clock is a non-negative integer; is 0 whenever an en-passant target is set; and is consistent with the fullmove number — `halfMoveClock ≤ 2 * (fullMoveNumber - 1) + (sideToMove == BLACK ? 1 : 0)` (a FEN like `... 15 1` is physically impossible). The clock itself is not capped — the FIDE 75-move rule is a queryable predicate on `Board`, not enforced at FEN import, so halfmove clock values at and above 150 are legitimate FEN.
- Fullmove number is a positive integer ≤ `MAX_FULL_MOVE_NUMBER`.

**`LenientFenParser` — syntactic tolerance only.** A purely syntactic pre-pass: walks the input, applies normalisation transforms (whitespace stripping, casing fixes, missing counter defaults, non-canonical castling reorder, non-ASCII-dash replacement, trailing-garbage trim), then delegates to `StrictFenParser`. Every transform that fires surfaces as a typed `ForgivenFenItem` on `LenientFenParserValidationResult`. The lenient layer **does not** weaken any strict semantic invariant — a FEN with a missing king, a pawn on rank 1, or an impossible double-check still fails strict validation. The lenient layer also handles the one strict invariant that benefits from a syntactic recovery: when strict validation rejects for halfmove clock vs fullmove number inconsistency, the lenient layer auto-corrects `fullMoveNumber` up to `halfMoveClock` rounded up to the next multiple of ten — a generous reserve over the strict minimum, signalling to a reader that the value was reconstructed rather than measured (so `... 15 1` becomes `... 15 20`, not the strict minimum of `... 15 9`). All other strict-validation failures propagate.

**Forgiven codes** (`ForgivenFenItemCode`):
`LEADING_WHITESPACE`, `TRAILING_WHITESPACE`, `EXTRA_WHITESPACE_BETWEEN_FIELDS`, `TAB_OR_NEWLINE_AS_SEPARATOR`, `MISSING_HALFMOVE_AND_FULLMOVE` (four-field FEN, common from Stockfish UCI), `MISSING_FULLMOVE_NUMBER` (five-field FEN), `UPPERCASE_SIDE_TO_MOVE`, `CASTLING_NON_CANONICAL_ORDER`, `EN_PASSANT_NON_STANDARD_DASH` (em-dash, en-dash, etc.), `EN_PASSANT_UPPERCASE`, `TRAILING_GARBAGE_TOKEN`, `HALF_MOVE_CLOCK_INCONSISTENT_WITH_FULL_MOVE_NUMBER`.

**`LenientPgnParser` routes the FEN tag through `LenientFenParser`.** Lenient PGN parsing accepts a deficient FEN tag (e.g. a "speculative-fullMoveNumber" position) for symmetry with movetext leniency; the FEN-level forgiveness is applied silently at the PGN-parser level. Strict PGN parsing reads the FEN tag through strict FEN validation — a strict-parseable PGN must carry a strict-parseable FEN.

**What is intentionally *not* a strict invariant.** A test-only class historically guarded a second rule alongside the halfmove clock vs fullmove number consistency: `fullMoveNumber == 1` had to mean the initial position (or, for black-to-move, one of the 20 after-first-move positions). That rule was dropped entirely when the consistency check entered strict FEN validation — many real-world FEN exporters emit `fullMoveNumber = 1` as a placeholder for positions whose actual move number is unknown (chess.com / Lichess "speculative from last capture" exports, ChessBase legacy snapshots), and rejecting them at the strict level would be hostile to the libraries ashlar-chess interoperates with. The halfmove clock consistency check absorbs the only physically-impossible half of the historic rule; the placeholder-fullMoveNumber half is accepted at both strict and lenient levels.

---

## 4. Architecture

The top-level package `io.github.dlbbld.ashlarchess` is organised by concern:

| Package | Responsibility |
|---|---|
| `board` | `Board`, position state, move execution, game-status queries, and the public game vocabulary: `LegalMove`, `LegalMoveKind`, `MoveSpecification`, `UciMove`, `Outcome`, `Termination`, `MoveCheck`, `InvalidMoveException`; plus internal position state (`DynamicPosition`, `ClaimRights`, `ClaimableMove`) |
| `board.enums` | Core board vocabulary enums: `Side`, `Piece`, `PieceType`, `Square`, `Rank`, `File`, … |
| `board.model` | Internal board-mutation helper `UpdateSquare`, consumed by the `moves` execution helpers |
| `fen` | FEN parsing, validation, and generation (`StrictFenParser` / `LenientFenParser`, `FenConstants`, `StrictFenSemanticValidationProblem`) |
| `fen.model` | The public FEN value type `Fen`, returned by `Board`, the FEN parsers, and PGN |
| `san` | Public SAN parsing and validation (`StrictSanParser` / `LenientSanParser`, result and problem types); the conversion model, notation enums, and SAN/LAN generators live in `san.internal`, with generation reached from `Board` (`getSan()` / `getLan()`) |
| `moves` | Legal-move enumeration and execution helpers (castling, en-passant, promotion); internal move-analysis check enums (`MovementCheck`, `CastlingCheck`, `KingSafetyCheck`) and move types (`EmptyBoardMove`, `CastlingRightBoth`) |
| `pgn` | A flat package: the PGN model (`PgnGame`, `PgnMove`, `Nag`, `MoveSuffixAnnotation`), parsing (`StrictPgnParser` / `LenientPgnParser` and the tokenizer), export (`PgnCreate`), file I/O (`PgnReader` / `PgnWriter`), tag / PGN utility helpers, and `PgnCommentaryValidationException` |
| `unwinnability` | the FUN 2022 unwinnability engine (quick and full), dead-position analysis |
| `adjudication` | Game adjudication for flagfall and resignation (`Adjudicator`, `AdjudicationResult`) |
| `report` | Game-level reports: threefold-claim-ahead, repetition, 50-move sequences |
| `analyze` | Stateless chess-rule analysis used by the SAN and movement validation pipelines |
| `messages` | Validation-message bundle (`Message`, `messages.properties`) for SAN/FEN/PGN diagnostics |
| `squares` | Precomputed empty-board reachability / attack lookup tables and direction ranges (`*EmptyBoardSquares`, `*Range`) |
| `bitboard` | The public `BitboardPosition` record (12-long piece placement) — the production representation, exported as documented advanced low-level API |
| `bitboard.internal` | Internal bitboard engine (not exported): the per-piece attack/move bitboard tables, the `BitboardPosition`→`LegalMove` factory, and decode helpers |
| `exceptions` | The cross-cutting base exception hierarchy (`UsageException`, `ChessApiRuntimeException`, `ProgrammingMistakeException`, `NonePointerException`, `FileSystemAccessException`). Feature-specific exceptions live inline in their feature package (`board.InvalidMoveException`, `pgn.PgnCommentaryValidationException`, `san.SanValidationException`) |
| `internal` | Cross-cutting internal infrastructure with no single feature home: `Nulls` (the null-handling hub), the generic `ListUtility` / `SetUtility` / `ExceptionUtility` helpers, and the global `ChessConstants` / `ConfigurationConstants` / `CastlingConstants` |

Packages depend in roughly that order (top to bottom). As of 20.0.0 the layout is **package-by-feature**: feature-specific types live beside the code they serve, the duplicate by-kind buckets (`model`/`common.model`, `enums`/`common.enums`, `common.exceptions`) are gone, and a single non-exported `internal` package holds only genuinely cross-cutting infrastructure (feature-specific helpers live in each feature's own `*.internal`).

For the JPMS boundary, implementation-only types live in non-exported `*.internal` subpackages: `bitboard`, `board`, `board.enums`, `fen`, `pgn`, and `san` each carry one, alongside the top-level cross-cutting `internal` package. They stay `public` so the rest of the library and the white-box tests can use them across packages, but they are omitted from `module-info.java`'s `exports`, so modular consumers see only each exported package's public face. An API-surface audit (2026-06-23) confirmed the exported packages carry only genuine API: incidental publics like `MoveNumberFormat`, the SAN/LAN generators (`MoveToSan` / `MoveToLan`), and the `SquareUtility` / `RankUtility` geometry helpers were moved into these `*.internal` packages.

### 4.1 Piece placement: bitboard in production, mailbox as test oracle

Piece placement has two independent representations in the codebase, by design.

**`BitboardPosition`** (in `src/main/java/io/github/dlbbld/ashlarchess/bitboard/`) is the single piece-placement representation that production code sees. It is a 12-long record — one `long` per piece-and-side bitboard, little-endian rank-file with A1 at bit 0 — exposing move generation, attack queries, and immutable make-move (`afterMove`). Everything in `src/main/` that needs to ask a question about pieces on squares asks `BitboardPosition`.

**`Board`** remains a deliberately rich public game object. It stores the initial FEN plus one per-position record for every ply — the performed move, check/checkmate/stalemate facts, the dynamic position whose piece placement is `BitboardPosition`, the halfmove clock, the repetition count, SAN/LAN output strings, and castling-right loss reasons — plus the legal moves of the *current* position only (historical legal-move lists are not retained; the current set is recomputed on `unmove`). That is more memory and bookkeeping than a lean engine board, but it is the right trade-off for a rule library: public queries and reports can read already-established game facts, and the history needed for FIDE rules is explicit.

**`HelpmateSearchBoard`** is the intentional hot-path exception. The unwinnability searches (the FUN 2022 Figure 5 deepening search and the Figure 10 quick DFS) perform deep cooperative-mate exploration, so they run on a package-private mutable board with twelve mutable bitboards, make/unmake, reusable undo stacks, per-depth legal-move buffers, and an exact structural transposition key. This is not a second public representation and not a private chess engine: it is a contained search board built on the same bitboard move-generation layer, with lock-step tests against `Board`. The board is engine-agnostic machinery — it survived the 22.0.0 switch from the cha port to the paper engine unchanged.

**`StaticPosition`** (in `src/test/java/io/github/dlbbld/ashlarchess/board/`) is the 64-square mailbox reference implementation, accumulated over years of correctness-first work. It does not live in `src/main/` anymore — it was relocated, not deleted — and exists in `src/test/` strictly to act as the **permanent differential-test oracle** for the bitboard backend. Its consumer subtree relocated with it (`StaticPositionUtility`; `io.github.dlbbld.ashlarchess.squares.{Abstract,Bishop,Rook,Queen,Knight,King,Pawn}{AttackedSquares,PotentialToSquares,RangeSquares}`; `io.github.dlbbld.ashlarchess.moves.{Abstract,Bishop,Rook,Queen,Knight,King,Pawn}LegalMoves`). The bridge methods `StaticPositionBridge.fromStaticPosition` / `toStaticPosition` (also in `src/test/java/io/github/dlbbld/ashlarchess/bitboard/`) round-trip between the two on the test side only.

The contract is **permanent project policy**, not transitional:

- Every primitive on `BitboardPosition` is asserted against the relocated `StaticPosition` oracle across the full PGN/FEN corpus — every fixture × every legal move — on every release going forward. The corpus walks live in `src/test/java/io/github/dlbbld/ashlarchess/test/bitboard/` (`TestBitboardPositionAfterMove`, `TestBitboardPositionAttackedSquares`, `TestBitboardPositionLegalMoves`, etc.) and use `StaticPositionUtility.createPositionAfterMove` as the independent oracle.
- `StaticPosition` and its consumer subtree are never deleted. Future bitboard optimisations (magic bitboards, more search-board hot-path work) cannot land without the oracle keeping them honest.
- The boundary is one-way: nothing in `src/main/` may import from `io.github.dlbbld.ashlarchess.board.StaticPosition` or any relocated consumer. Doc comments may cross-reference; code may not.

This is the project invariant in §2.1 applied to representation. The bitboard exists for the production code path; mutable search bitboards exist where the full analyzer needs them; the mailbox stays as the readable, audit-friendly second opinion that the optimized path is forever measured against.

---

## 5. PGN — intentional deviations from the specification

The library implements the PGN specification closely. Two areas are intentional departures, in both cases following **python-chess** as the de-facto reference (and, for pre-game commentary, also **Lichess**), where the formal spec is silent, ambiguous, or marked as not fully defined.

### 5.1 Newlines and tabs preserved in commentary content

The PGN specification's strict export format implies that brace commentary should fit on a single line, with newlines normalised and non-printing characters generally absent. Two facts qualify that:

- The export format itself is explicitly noted in the spec as **not fully defined**.
- The strict prohibition on non-printing characters in the spec applies to **string tokens** (tag values), not to brace commentary content. The commentary case is silent.

ashlar-chess takes the more permissive (and more useful) reading: **`\t` and `\n` inside `{...}` commentary are content**, preserved verbatim through `parse → export → parse`. The rationale is round-trip fidelity for real-world PGN, where comments often carry multi-line annotation. This matches python-chess.

The library still rejects malformed Unicode (lone surrogates, unassigned code points) and other control characters; the deviation is specifically about tab and LF. CR / CRLF are normalised to LF on input, so the model never carries CR directly.

### 5.2 Pre-game commentary

The PGN specification defines brace commentary attached to moves but **does not formally specify a "pre-game" commentary slot** — commentary that appears between the tag pair section and the first move. python-chess exposes this as `Game.comment`; Lichess supports it on import.

ashlar-chess follows the same convention: `PgnGame.pregameCommentary()` carries any `{...}` content found before the first move, validated under the same commentary contract as move-attached commentary. This is an additive extension rather than a contradiction — a PGN that uses pre-game commentary remains well-formed for any reader that ignores it.

### 5.3 Conformance for everything else

The rest of the library's PGN handling follows the specification: brace-grammar rules, move-number indicators, inner-brace-as-content, FIDE game-termination markers, the strict-vs-import format split. The Seven Tag Roster is treated as a §8.1.1 archival-storage concern — required by archival output (`WriteMode.ARCHIVAL`), not by general spec-compliant parsing; see §3.3.2 for the contract. The PGN standard already documents the *what*; the code is the authoritative *how*.

---

## 6. Testing strategy

ashlar-chess relies on a large regression test suite:

- **Broad coverage by code area** — every package has dedicated tests; rule-level decisions have multi-fixture parameterised tests.
- **Edge-case fixtures** — positions and games chosen to stress the rule engine: 75-move-rule games, fivefold-repetition games, dead positions, near-misses, long forced sequences.
- **Long and random games** — hundreds of moves, including imported real-world games and synthetic stress tests; generated games surface bugs that targeted fixtures miss.
- **Cross-library validation** — selected fixtures are processed by other chess libraries; disagreements surface as test failures and have, in the past, led to issues raised against those libraries (see §6.3).

The test suite is the project's safety net. Refactors are expected to leave the test count unchanged or growing; if they don't, the change is suspect.

### 6.1 Differential testing of the bitboard backend

The permanent oracle policy is stated in §4.1; this is the concrete test pattern it produces:

- For each fixture in the corpus, take the position at every reachable game state, derive a `BitboardPosition`, and run the bitboard primitive under test.
- Compute the reference answer independently from `StaticPosition` / `StaticPositionUtility`.
- Assert the two agree exactly. For `afterMove`, walk both forward over every legal move in every fixture position.

A new primitive on `BitboardPosition` is not considered complete until its corpus walk lands alongside it (`src/test/java/io/github/dlbbld/ashlarchess/test/bitboard/`).

### 6.2 Restricted vs full suite

Day-to-day iteration runs a restricted subset; the complete suite — every `RestrictTestConstants` gate flipped and the longest-possible-game corpus included — runs under a Maven profile and is the **release gate**: the default suite is *not* sufficient to certify a release. The exact commands are in [`setup.md`](setup.md) (*Running the test suite*).

### 6.3 What the test volume buys — and a note on shared corpora

The cross-library validation above is not aimed at finding bugs in other libraries — python-chess (the primary oracle) and scalachess (the chess library behind Lichess) are mature, widely trusted references, which is precisely what makes them good oracles. But differential comparison is symmetric, and during development it surfaced a few small discrepancies, all since fixed:

- a bug in insufficient-material detection — king and bishop versus king and bishop with both bishops on same-coloured squares was not recognised — [python-chess #634](https://github.com/niklasf/python-chess/issues/634);
- a minor inconsistency I raised in the fifty-move-rule *claim-ahead* boundary — the draw was claimable one move earlier than the method reported — [python-chess #632](https://github.com/niklasf/python-chess/issues/632);
- a threefold-repetition draw claim that failed from a custom starting position (it worked from the initial position) — surfaced via [Lichess #7009](https://github.com/lichess-org/lila/issues/7009), traced to scalachess and since fixed there.

These are noted not as a boast — these are excellent libraries and every case was promptly addressed — but as evidence of the project's central premise: in this domain it is remarkably easy to introduce a subtle, behaviour-changing slip, even in first-rate code. The same effect showed up constantly *inside* ashlar-chess during development — refactors that looked semantics-preserving at the surface routinely turned swathes of tests red. That is the justification for the test volume: the many tests are not ceremony or coverage for its own sake, they are demanded by the subject. Chess rules carry a long tail of interacting edge cases — en passant only after a specific two-square advance, castling rights lost by a rook *capture*, repetition identity across transpositions, the 50- and 75-move interactions — where a plausible change is wrong in a way that only a targeted case exposes.

> **Observation (not a deliverable).** There appears to be no shared, reusable corpus of rule-level chess test cases — a conformance suite a chess library could validate against — in general circulation; if one exists, the author did not find it. As a result, much of the value of ashlar-chess is the large set of hand-crafted, rule-targeted fixtures it carries, which were not available off the shelf. Publishing them as a standalone corpus could be a worthwhile contribution; it is noted here as a possibility, not planned work.
