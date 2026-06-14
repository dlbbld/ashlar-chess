# Tasks

Live planning only: current release work, backlog, and obsolete decisions. Shipped release history lives in
**CHANGELOG.md**; recurring procedures live in **workflows.md**. Order within each live section is the source of truth.

---

## The story when releases are done

*ashlar-chess started as a correctness-first reference implementation, built from the FIDE rules without consulting existing libraries. It found correctness bugs in python-chess and ScalaChess along the way. Once the rules were stable, a bitboard backend was added alongside the reference layer and verified bit-exact against it. Production then switched to the bitboard path; the reference layer was relocated into the test tree and remains as the permanent differential-test oracle. Cross-validation against python-chess was reactivated as primary, with `chesslib` retained as a second witness. Only then published to Maven Central.*

---

## 19.0.0 — Retire `HalfMove`, records carry data (breaking; in progress)

Current release work, and a **breaking** (major) release: it renames/removes public API (`HalfMove` -> `MoveRecord`,
`PgnHalfMove` -> `PgnMove`, `PgnGame.halfMoveList` -> `moveList`, `Board.getHalfMoveList` / `getLastHalfMove` /
`getPerformedHalfMoveCount`, `DynamicPosition.isEnPassantCapturePossible`, the enum `toMoveCheck` conversions). Three
tasks, ordered. Task 2 (terminology) governs the naming used in Tasks 1 and 3.
Release procedure lives in **workflows.md**; ship notes archive to **CHANGELOG.md** `[19.0.0]` on release.

### Task 1 - Retire `HalfMove`; build `MoveRecord` in the report layer

**Status: done** - shipped on this branch (commits `7b9008d`, `6931557`). `MoveRecord` / `MoveRecords` /
`RepetitionGrouping` are package-private in `report`; the `Board` bridge and `HalfMoveListListComparator` are gone; full
suite green. The behavior-strip of `MoveRecord.havingMove()` / `toString()` was deferred to Task 3 as planned.

Shipped design (notes kept brief; the pre-implementation plan is in the commits): `MoveRecords` rebuilds the
play-history rows by replaying and snapshotting `Board`'s existing public per-move accessors - no `Board` bridge and no
new `Board` primitives - and the repetition grouping moved to `report.RepetitionGrouping` to avoid a `common -> report`
cycle. Deferred to Task 3 (as planned): strip `MoveRecord.havingMove()` / `toString()` so it is a pure data record.

### Task 2 - A played turn is a "move", not a "ply" or "half move" (scoped vocabulary)

**Status: done** - shipped on this branch in six commits: `8bf4fb57` (`PgnHalfMove` -> `PgnMove`, `PgnGame.halfMoveList`
-> `moveList`), `2807b355` (`halfMoveCount` / `getPerformedHalfMoveCount` -> `performedMoveCount`), `317f9a9f`
(`HalfMoveUtility` -> `MoveNumberFormat`), `30d907d8` (report `ply` -> `move`), and `b615cd84` + `bbc6aaf7` (the residual
`halfMove` method/variable/local names the first allowlist grep missed). Default suite green. The narrow allowlist grep
originally written below proved insufficient twice - it misses bare `halfMove` locals and `PerPly`; the *comprehensive*
gate that actually proved completeness is in **Verification** below. KEEP zones that emerged in practice: the FEN
`halfMoveClock` family; the `test/librarycarlos/**` package (mirrors chesslib's "half move" vocabulary, just as
`oracle/python` mirrors python-chess's "ply"); `ForcedLineOracle.countForcedHalfMoves`; and the external chesslib
`getHalfMoves()` / `getHalfMoveCounter()`.

Vocabulary is scoped by layer, not banned globally: the public chess model speaks chess; PGN/FEN counters keep their
protocol names; engine internals may speak engine. One player's action is a **move** - never a "half move" (reads as a
second kind of move beside `LegalMove`) and, in the move/history/report layers, never a "ply". This rule governs the
naming in Tasks 1 and 3.

- **Move-domain language** (board history, PGN model, reports, repetition / claim-ahead output, public API, general
  docs): `move`, `played move`, `moveIndex` (0-based array index), `performedMoveCount`. No `*HalfMove*` or `*Ply*`
  identifiers here. Note the 1-based per-record ordinal that was `HalfMove.halfMoveCount()` is the *same* concept as the
  board total at a point - both become `performedMoveCount`, **not** `moveIndex` (which is reserved for the 0-based
  array index).
- **Protocol / rule-counter names, kept verbatim** (FEN/PGN/standard-tag vocabulary, not domain "moves"): `halfMoveClock`
  (FEN no-progress counter), `fullMoveNumber` (PGN move number), and the literal `PlyCount` supplemental tag -
  `StandardTag.PLY_COUNT` and the `"PlyCount"` tag string stay exactly as the spec spells them.
- **Engine / search language - the only place `ply` survives**: helpmate / search-tree depth, make/unmake stacks,
  per-search-level buffers (`UndoState`, `LegalMoveBuffer`, `HelpmateSearchBoard`, "3-ply scan", ...). After Task 1 there
  is no `ply`-named *identifier* left in the search code - only descriptive prose - so this layer needs no edits; the
  idiomatic "N-ply" wording stays as-is.

**The sweep, as independent sub-passes.** Each compiles and passes the default `mvn -o -q test` on its own (per
`agents.md`; reserve `mvn -Pfull test` for the final / release sign-off); land each as its own commit. Counts are
post-Task-1 (verified by inventory). 2a and 2c are fully independent of the rest and of each other - do them first or in
parallel; 2b and 2d are the move-domain core.

- **2a. PGN model: `PgnHalfMove` -> `PgnMove` (the big one; public, breaking).** `PgnHalfMove` is the SAN movetext item
  (san + suffix annotation + commentary), public API in `model`, ~82 references across ~26 files - the PGN parsers
  (`StrictPgnParser`, `LenientPgnParser`), `PgnCreate`, `PgnUtility`, `PgnGame`, `model/package-info`, and ~18 test
  files. Rename the type to `PgnMove`; rename `PgnGame`'s public component `halfMoveList` -> `moveList` (its accessor
  `pgnGame.halfMoveList()` -> `moveList()` ripples through the parsers, `TestReadMe`, and the python-oracle tests).
  Rationale: the PGN spec calls these "chess moves", not halfmoves; "halfmove" is FEN-clock / `PlyCount` vocabulary.
- **2b. Move-count: `halfMoveCount` -> `performedMoveCount`, `getPerformedHalfMoveCount` -> `getPerformedMoveCount`**
  (the 1-based played-move ordinal; `Board` method is public, breaking). Sites: `Board.getPerformedHalfMoveCount()`
  (callers in `CommonTestUtility`, `TestBoardClaimRights`, `TestBoardCopyCurrentPositionWithoutHistory`,
  `GenerateRandomGame`, Board-internal); the `MoveRecord` component `halfMoveCount` (accessor used by `ReportLineOrder`,
  `ThreefoldClaimAheadReportBuilder`, `RepetitionGrouping`, `SequenceStart`, report tests); the `FiftyMoveClaimAheadEntry`
  component `halfMoveCount` (+ its compact-constructor guard message); the `int halfMoveCount` parameters in
  `Board.calculateFullMoveNumber(...)` and `BasicChessUtility.calculateSideMoved(...)`; and the test double
  `LibraryCarlosBoard.performedHalfMoveCount` + its getter. `halfMoveClock` stays everywhere (FEN field).
- **2c. `HalfMoveUtility` -> a move-number-format name (rename only; do NOT relocate).** It formats a PGN-style
  move-number-plus-SAN label and is **shared by both `pgn` and `report`** (`StrictPgnParser`, `LenientPgnParser`,
  `PgnCreate` + `SequenceStartFormat`, `PositionIdentifierUtility`, `FiftyMoveClaimAheadPrint`, `MoveRecord`), so it must
  stay in a neutral package (currently `board`; `common.utility` is the other candidate) - moving it into `report` would
  create a `pgn -> report` dependency. ~7 caller files. The misnomer is only the class name (it formats a *move*'s
  number); the method names (`calculateMoveNumberAndSan...`) are already move-shaped and stay. Suggested name
  `MoveNumberFormat` (confirm during execution).
- **2d. Report-internal `ply` -> `move` (package-private; non-breaking).** Record components
  `FiftyMoveSequence.{fiftyMoveThresholdPly, seventyFiveMoveThresholdPly, endPly}` -> `{fiftyMoveThresholdMove,
  seventyFiveMoveThresholdMove, endMove}`; helpers `SequenceStartFormat.plyAnchor` -> `moveAnchor` and
  `FiftyMoveSequencePrint.addPlyAnchor` -> `addMoveAnchor`; the `ply`-named locals in `FiftyMoveSequenceReportBuilder` /
  `FiftyMoveSequencePrint` / `SequenceStartFormat` -> `move`; and "ply"/"plies" -> "move"/"moves" in the report-package
  JavaDoc/comments and report test comments.
- **2e. Stragglers + docs.** `TestReadMe` `halfMoveList()` -> `moveList()` rides with 2a; "ply"/"plies" prose in
  `PgnFen`, `TestInsufficientMaterial`, and report tests -> "move(s)"; the "Converge `TestReadMe`" backlog item's
  `halfMoveList` reference updates with 2a.

**KEEP - do not touch** (protocol / engine vocabulary, not domain "moves"):

- FEN/PGN spec terms: `halfMoveClock`, `fullMoveNumber`, `StandardTag.PLY_COUNT` / the `"PlyCount"` tag string.
- Engine/search `ply` prose - all descriptive, no identifiers (`UndoState`, `LegalMoveBuffer`, `HelpmateSearchBoard`,
  `BitboardPosition` per-ply move-buffer note, `ShallowTerminationOracle` / `TestShallowTerminationOracle` "3-ply",
  `TestHelpmateSearchBoardMakeUnmakeRoundTrip`).
- **python-chess oracle (`test/oracle/python`) - a protocol-mirror zone.** The JSON-mapped names `LegalMovesPly` /
  `LegalMovesRecord.perPly()` and the `"perPly"` key read by `LegalMovesJsonlReader` mirror python-chess's own data
  contract. Extend the same exception to this package's *incidental* oracle-comparison vocabulary - local `ply` /
  `plyLabel` variables and "ply N" diagnostic messages (e.g. `TestPgnImportAgainstPythonChessOracle`). **Decision: keep
  `ply` throughout `test/oracle/python`** so the comparison reads in the source's terms; do not rename to `moveIndex` /
  `moveNumber` there.

**Verification (comprehensive - earlier forms missed bare `halfMove` locals, `PerPly`, AND hyphenated `half-move` /
`per-ply` *prose* in comments/JavaDocs/messages/docs).** Run three scans over `*.java` AND `*.md` (docs included):

1. identifiers: `rg -g '*.java' -g '!**/librarycarlos/**' -g '!**/oracle/python/**' '[Hh]alfMove' src | rg -v 'halfMoveClock|HalfMoveClock|getHalfMoves\(|getHalfMoveCounter|countForcedHalfMoves'` -> must be empty.
2. bare ply: `rg -g '*.java' -g '!**/oracle/python/**' -g '!**/unwinnability/**' -g '!**/bitboard/**' '\bply\b|\bplies\b|PerPly' src` -> must be empty.
3. prose + docs: `rg -i -g '*.java' -g '*.md' -g '!**/librarycarlos/**' -g '!**/oracle/python/**' -g '!CHANGELOG.md' 'half[- ]?moves?|per[- ]ply|PgnHalfMove' .` -> remaining hits must be KEEP-only.

**Scan 1 has a filename blind spot - do not trust it alone.** Its `rg -v 'halfMoveClock|HalfMoveClock|...'`
allowlist filters each `path:content` line, so it also drops any line whose *path* (not just content) contains an
allowlisted token - e.g. a file named `*HalfMoveClock*` masks its own `halfMove` locals. This hid the move-domain
`halfMove` local in `AbstractTestPgnParserHalfMoveClockFromFen` until commit `def426fe` (2026-06-13); only Scan 3
surfaced it, via the path. Catch this class with a path-agnostic identifier scan:
`rg -g '*.java' -g '!**/librarycarlos/**' '\bhalfMove\b|\bHalfMove\b|halfMoveCount|halfMoveList' src` -> empty
(KEEP: `halfMoveClock`, python-chess's `halfmoveClock()` data field, `getHalfMoves`, `countForcedHalfMoves`).

Scan 3 (`half[- ]?moves?` - the optional separator catches `half-move`, `half move`, AND unseparated `halfmove` /
`halfmoves`) catches the prose the camelCase scans miss - PGN validation messages, report /
PGN JavaDocs, `specification.md`, `README*.md`. Its only allowed remaining hits are KEEP zones: the FEN **half-move
clock** family (any line about the clock / full-move-number consistency), engine-search "half-move tree" / "per-ply"
prose, the chesslib-mirror docs, and this plan file. (`halfMoveClock` / `fullMoveNumber` / `PlyCount` stay by design.)
This was the largest mechanical change of 19.0.0.

### Task 3 - Records carry data, not behavior (records + enums cleanup)

The project rule (`coding-conventions.md`): records carry data; domain logic that operates on them lives in dedicated
utility/service classes. A full sweep found the production record layer already ~95% compliant - the actionable set is
small and mostly low-risk.

**Confirmed moves:**

- **`StaticPosition.createChangedPosition` (3 overloads) + the private `checkUpdateSquare`** -> `StaticPositionUtility`
  (test util; already exists and already wraps it). It simulates a state change - the rule's clearest category and its
  own named counter-example (`afterMove` -> `Utility.createPositionAfterMove`). Keep the lightweight query predicates
  (`get`, `isEmpty`, `isOwnPiece`, `isPawn`, ...) on the record. ~170 call-site edits, mechanical and concentrated in
  `TestPerformMoveMainlyStaticPositionState` (test-only, low risk).
- **`DynamicPosition.isEnPassantCapturePossible()`** -> `EnPassantCaptureUtility` (a home exists). The
  attribute-vs-computed smell: a `field != NONE` check dressed as a property. ~7 call sites; public-API removal (fits the
  breaking release).
- **Enum `toMoveCheck` conversions** (`KingSafetyCheck`, `CastlingCheck`, `MovementCheck`) -> consolidate into translator
  utilities. The `TestKingSafetyCheckTranslator` / `TestMovementCheckTranslator` tests already exist with no main class,
  and `CastlingCheckMapper` is the precedent. ~10 call sites (mostly `ValidateNewMove`).
- **Strip behavior from `MoveRecord`** (was `HalfMove`): `havingMove()` and `toString()` (delegates to `HalfMoveUtility`)
  come off the record - closes this record's own violation and links back to Task 1.

**Documented exception (no code-behavior change):**

- **`BitboardPosition` stays as-is.** It deliberately carries the move-generation / king-safety engine (`afterMove`,
  `legalMoves`, `attackedSquares`, `pinRay`, `pinnedPieces`, ...) on the record for hot-path allocation reasons. Add a
  note to `coding-conventions.md` (and a one-line pointer in the class JavaDoc) marking it a conscious, bounded exception
  - not a licence to add domain methods to other records. No extraction.

**Borderline, decide during execution (lean keep):** the trivial `isValid()` predicates on the parser validation
results; `havingMove()` on `LegalMove` (mirrors `DynamicPosition`'s real `havingMove()` component). `Square.flip()`
(heavy geometry, one caller) **was extracted** to `board.enums.SquareUtility` (commit `77a95c72`) as part of the
enum-logic sweep below.

**Breaking:** `DynamicPosition.isEnPassantCapturePossible()` and the enum `toMoveCheck()` removals; the StaticPosition
work is test-only.

**Enum-logic sweep (in progress).** An audit of all 50 enums (beyond the three `toMoveCheck` enums above) found more
behaviour living on enums. Extracted, each its own commit: `SanTerminalMarker` -> `SanTerminalMarkerUtility`
(`41d09c7f`); `SanFormat.isCapture` -> `SanFormatUtility` (`1fbb7dea`); the `(Side, Type) -> Piece` factory family
across `Piece` / `PieceType` / `PromotionPieceType` consolidated into `PieceUtility` (+ existing
`PromotionPieceTypeUtility`), removing a `Piece.calculate` / `PieceType.calculate` duplicate (`a0476c28`);
`Fen{Side,Piece}Symbol.calculate(Side/Piece)` translations -> `Fen{Side,Piece}SymbolUtility` (`c19797de`);
`Square.flip` -> `SquareUtility` (`77a95c72`). The governing rule is now codified in **coding-conventions.md**
("Enums carry data, not behavior"): self-canonical parse stays on the enum; cross-type translation, side-relative
chess-rule logic, and presentation move to utilities. KEEP decisions: intrinsic involutions (`Side.getOppositeSide`,
`SquareType.getOppositeSquareType`), self-parse `exists` / `calculate(symbol)` (`MoveSuffixAnnotation`, `StandardTag`,
`Notation*`, the `Fen*Symbol` char parse), and the trivial value-list renderers (too small to warrant dedicated classes).

**Remaining (dedicated pass): `Rank` / `File` / `Square` side-relative chess-rule methods.** Each foundational enum
mixes intrinsic geometry (coordinate identity + single-step neighbours - keep) with `switch(Side)` rule logic
(ground / promotion / pawn-initial / two-advance / en-passant ranks; jump-over and king/rook-origin squares; per-side
left/right file tables - extract to `RankUtility` / `FileUtility` / `SquareUtility`). They share private helpers and
`public static final` per-side maps, so each extracted method must move together with the tables/helpers that exist
only to serve it - don't leave the enum half data, half hidden service. Order: `Rank` (cleanest - rule methods are
independent of the geometry helpers), then `File`, then `Square` (most entangled; `SquareUtility` already seeded by
`flip`).

### Cleanup (done) - dead code and test-only-production relocations

Surface-trimming done during the 19.0.0 cycle, alongside the HalfMove/records work:

- **Deleted dead production types.** `BoardUtility` + `CheckmateOrStalemate` (commit `93f59cf8`) and
  `SemiOpenFilesUtility` (commit `af7013b7`). `SemiOpenFilesUtility` was orphaned by the 18.0.0 CHA 2.6.1 realignment of
  the quick unwinnability analyzer - its only referent was its own self-test; the first pair had no caller at all. All
  recoverable from git history.
- **Relocated test-only support from `main` into the test tree** (commit `e48082a6`): `StandardMoveUtility` (`moves`),
  `LegalMoveCalculation` (`model`), `SquareOccupation` (`enums`), `IoUtility` (`common.utility`). None had a
  production-code caller - only the reference-oracle, README-doc-capture, and generator test layers used them. Each keeps
  its package (caller imports unchanged); three were public API, so dropping them from the published surface is part of
  the breaking bump. The dangling `{@link SquareOccupation}` in `enums/package-info.java` was dropped for strict doclint.
  This completes the reference-layer relocation: test `StaticPositionUtility`'s call into `StandardMoveUtility` is now
  test -> test. Follow-up (commit `5cf7e1d8`): `PseudoLegalMove` relocated too - moving `LegalMoveCalculation` (its only
  referent) orphaned it, the expected cascade.
- A reference-count scan over `src/main` found no fully-unreferenced production type. Caveat: relocating a test-only type
  can newly orphan its dependencies (as `PseudoLegalMove` showed), so re-scan after each relocation.

---

## Released 18.1.0 — Adjudication API (see CHANGELOG)

Shipped. Minor release: a new public `adjudication` package (`Adjudicator` / `AdjudicationResult`) for flag-fall and
resignation per FIDE 6.9 / 5.1.2, the fifty-move report's per-player `(White/Black)` move counts, and a doctested,
template-generated README. The "extend insufficient material" task (lone K+B / K+N winners) was found already covered by
the 18.0.0 theorem and shipped no new code. Release details live in **CHANGELOG.md** `[18.1.0]`; the repeatable release
procedure lives in **workflows.md**.

---

## Released 18.0.0 — published 2026-06-04 to Maven Central (see CHANGELOG)

Shipped. Breaking release: added the basic-helpmate-existence theorem shortcut in the full analyzer, realigned the
quick analyzer to a faithful two-valued port of CHA 2.6.1, and simplified the dead-position / game-end API. Release
details live in **CHANGELOG.md** `[18.0.0]`; the repeatable release procedure lives in **workflows.md**.

---

## Released 17.0.0 — published 2026-05-29 to Maven Central (see CHANGELOG)

Shipped. The one-time publication checklist was collapsed after release so this file stays focused on live work.
Release details live in **CHANGELOG.md** `[17.0.0]`; the repeatable release procedure lives in **workflows.md**.

---

## Backlog — captured but unscheduled

Items here are not assigned to any release. Captured so they don't get lost; revisit if/when scope or motivation aligns.

### Report terminology: speak FIDE "moves by each player", not halfmoves or fullmove decimals

**Next up after the 18.1.0 README mechanization (Task 1).** Surfaced while mechanizing the README report output: the
printed 50-move (and 75-move) report leaks engine-internal units. The bracketed counts are currently the halfmove clock
(`63... Rg8 (1) - 114... Rf6+ (103)`); an earlier form used fullmove decimals (`(0.5) / (50) / (51.5)`). Neither is how
the laws of chess speak.

**Principle.** `fullmove number` (PGN-spec move numbering) and `halfmove` (ply) are *internal* terms - fine in the
application internals and in PGN validation (where PGN move numbers are official), but they must not leak into
user-facing report prose/output. FIDE Articles 9.3.1 / 9.3.2 / 9.6.2 frame the rules as "50 moves by each player" and
"75 moves ... by each player". The reporter should translate the precise internal units (`halfMoveClock`, ply, PGN
fullmove number) into that law-shaped vocabulary at the print boundary - keep the internals precise, change only the
output.

**Proposed report shape** (repository-owned, non-breaking - `Reporter` is print-only):

- Fifty-move sequence:
  `63... Rg8 - 113. Ng5 (50 moves by each player) - 114... Rf6+ (White 51, Black 52)`
  - normal chess move labels for the moves; the legal threshold named exactly as the law ("50 moves by each player");
    per-player counts after the threshold, not pseudo-fullmove decimals.
- Fifty-move claim-ahead: `113. Ng5 (would complete 50 moves by each player)`.
- Seventy-five-move: `138... Rc1 (75 moves by each player)`.
- Start the sequence at the start move (not `0/0`); the load-bearing moments are "threshold reached" and "sequence
  ends". (A fully tabular `0/0 ... 50/50` per-player diagnostic mode was considered and deferred unless wanted.)

**Touch points.** [`FiftyMoveSequencePrint`](src/main/java/io/github/dlbbld/ashlarchess/report/FiftyMoveSequencePrint.java),
`FiftyMoveClaimAheadPrint`, the `report.fiftyMove.*` message-bundle titles, the threefold prints for vocabulary
consistency, the README report prose + examples (regenerate via the Task 1 pipeline), and new report-format tests.

**Related.** The 19.0.0 `HalfMove` -> `MoveRecord` rename fixes the *type/concept* leak; this item remains the separate
*print-output* vocabulary fix and naturally follows it.

### Implement the flagfall / resignation adjudication method (then de-pseudocode the README)

**Status: method + tests done** (`io.github.dlbbld.ashlarchess.adjudication.Adjudicator` + `TestAdjudicator`, fast
suite green). The README de-pseudocode below is the only remaining step.

The README "Game adjudication" section currently gives the flagfall / resignation procedure only as ` ```text `
pseudocode. The method is now real; the remaining work is to replace the pseudocode with a generated, compiled README
example (Task 1 pipeline).

The suggested procedure is already written out in the README, per FIDE 6.9 (flagfall) and 5.1.2 (resignation, which
adjudicates exactly as flagfall):

```
on flagfall(flaggingPlayer):
    wouldBeWinner = opponent(flaggingPlayer)
    if board.isInsufficientMaterial(wouldBeWinner): return draw            # material-only, cheap
    if board.isUnwinnableQuick(wouldBeWinner) == UNWINNABLE: return draw   # CHA quick, position-wise
    return loss for flaggingPlayer
```

- **New public API (done).** `Adjudicator` (new `io.github.dlbbld.ashlarchess.adjudication` package) exposes
  `adjudicateFlagfall{Quick,Full}(Board, Side)` and `adjudicateResignation{Quick,Full}(Board, Side)` (resignation
  delegates to flag-fall). Returns `AdjudicationResult`: the **quick** variant rules only `DRAW` / `LOSS` (fast, from
  `isUnwinnableQuick`); the **full** variant adds `UNDETERMINED` (from the complete `isUnwinnableFull`). New package
  avoids a `common.utility` <-> `unwinnability` cycle.
- **No new rule logic (done)** - composes `Board.isUnwinnableQuick(Side)` (quick) / `Board.isUnwinnableFull(Side)`
  (full). The quick analyzer already includes the insufficient-material check, so no separate material call is needed.
  Quick `POSSIBLY_WINNABLE` -> `LOSS` (no draw could be shown); full `WINNABLE_*` -> `LOSS`, `UNWINNABLE` -> `DRAW`,
  `UNDETERMINED` -> `UNDETERMINED`.
- **README.** Replace the `on flagfall(...)` / `on resignation(...)` pseudocode blocks with real compiled examples
  wired into the generator (`ReadmeExamples` + placeholders), so the adjudication snippet compiles and shows real
  output like every other example. The dead-position-during-play blocks can get the same treatment or stay
  illustrative.
- **Tests (done).** `TestAdjudicator` covers quick (DRAW / LOSS) and full (DRAW / LOSS / UNDETERMINED - the last via
  the one node-bound-exhausting corpus position), resignation == flag-fall, and `Side.NONE` rejection.
- Minor release (additive API); depends on the Task 1 README pipeline for the example wiring.

### Converge `TestReadMe` with the generated README examples

Cleanup left from the 18.1.0 README mechanization (per the "generator is source of truth" decision). Every example
body now lives in `ReadmeExamples`, and `TestReadmeUpToDate` + the generator guarantee each README snippet compiles and
prints exactly what it shows. `TestReadMe` still re-implements those same examples with hand-coded assertions. Slim it
to only the behavioral assertions **not** visible in README output - parser problem-enum checks, `halfMoveList` sizes,
the file write/parse round-trip - and drop the now-redundant ones the drift guard covers (unwinnability / dead-position
verdicts, `isCheckmate`, `isValid`). First confirm the kept assertions aren't already covered by the dedicated
parser / unwinnability suites; if they are, `TestReadMe` can retire entirely.

### Maven Central badge for README

Optional: add a Maven Central status/version badge to the README if it improves the landing page without adding visual
noise.

---

## Obsolete

Items deemed no longer worth pursuing. Captured so the decision is visible.

### scalachess as a permanent differential oracle

Validated, then declined. scalachess (lichess.org's rules engine) was wired up on the `test-against-scalachess` branch
as a one-time, out-of-process cross-check: a scala-cli generator (resolved from JitPack) replayed the move-rule-mechanics
buckets and emitted legal-move and insufficient-material verdicts, compared against ashlar. **Every verdict matched** —
ashlar and scalachess agreed across all fixtures, a clean confirmation that ashlar is correct on those predicates.

Decision: do **not** keep it as a standing oracle. It would be redundant alongside python-chess (primary) and chesslib
(second witness), would inflate the suite with duplicate assertions for no new coverage, and scalachess's interface
can't even exercise the report-only predicates (threefold-ahead / fifty-move-ahead claims). The check served its
purpose — confirming ashlar, not policing scalachess — and is done. The `test-against-scalachess` branch is retained for
the record and is **not** merged into `main`; the local `tools/scalachess-oracle/` working copy was removed (the source
lives on that branch).

### Replace `EnumConstants` constant interface
`io.github.dlbbld.ashlarchess.common.constants.EnumConstants` is a `public interface` whose only purpose is to expose ~90 `public static final` aliases for `Square.*`, `Side.*`, `Piece.*`, `PieceType.*`, `Rank.*`, `File.*` so implementing classes inherit them unqualified. This is the classic "constant interface" anti-pattern (Effective Java item 22): interfaces should describe a contract/behavior, not be a convenience-inheritance vehicle for constants. The mechanism reads as beginner Java and leaks an internal vocabulary choice into the public type surface — `ChessBoard extends EnumConstants` is the clearest symptom (the chess contract has nothing to do with how implementers prefer to spell `Square.E4`). Used by 43 files under `src/main` plus tests.
