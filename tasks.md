# Tasks

Order within each section is the source of truth. Completed tasks move to **Done** at the bottom.

---

## The story when releases are done

*clean-chess started as a correctness-first reference implementation, built from the FIDE rules without consulting existing libraries. It found correctness bugs in python-chess and ScalaChess along the way. Once the rules were stable, a bitboard backend was added alongside the reference layer and verified bit-exact against it. Production then switched to the bitboard path; the reference layer was relocated into the test tree and remains as the permanent differential-test oracle. Cross-validation against python-chess was reactivated as primary, with `chesslib` retained as a second witness. Only then published to Maven Central.*

---

## Current release — Clean-up release mainly remove old test generators and var operator - 16.1.0

Already done in CHANGELOG.md

[x] Fix JavaDoc code in test classes and private main classes
[x] Use explicit local variable types
[x] Document how to use clean-chess on flag fall and resign in README.md

## Future release — publish to Maven Central

The capstone release. Publish to Central only when the library has stabilised — every prior release done, identity questions settled, and any tasks that surface during the prerequisite work itself addressed first. Maven Central artifacts are immutable: once published, an artifactId+version pair lives forever in the public record. The bar for moving from JitPack to Central is therefore "we are confident this artifact represents the project well, indefinitely."

### Prerequisites — must be true before any Central work begins
- [ ] Add line "// SPDX-License-Identifier: GPL-3.0-only" to each file
- [ ] Add Eclipse setting adding the obligate first line for every new Java class
- [x] Documented: how to use clean-chess on flag fall and resign in README.md
- [x] DeepSquare release complete (Auto-CHA + Zobrist + pawn-wall classifier + foundational refactors)
- [x] Bitboard release complete (performance acceptable, differential-test harness green)
- [x] python-chess primary + PGN/FEN coverage release complete
- [x] Rename decision resolved: ashlar-chess is the final name.
- [ ] Every task that surfaces during the prerequisite releases has been addressed (re-evaluate this list at the moment of starting; the bar is "library is mature")

### Sonatype Central Portal setup
- [ ] Create Sonatype Central account at https://central.sonatype.com, sign in via GitHub
- [ ] Verify the `io.github.dlbbld` namespace (auto-verified for GitHub-signed-in users — no domain needed)
- [ ] Generate a GPG key, publish it to a public keyserver (e.g. `keyserver.ubuntu.com`), record the keyID
- [ ] Configure `~/.m2/settings.xml` with Sonatype Portal credentials and GPG passphrase

### `pom.xml` — Central-required metadata
- [ ] `<groupId>` → `io.github.dlbbld` (currently `com.github.dlbbld`, the JitPack convention)
- [ ] `<version>` → strict semver (`4.x` → `4.x.0`)
- [ ] Add `<name>`, `<description>`, `<url>` (link to GitHub repo)
- [ ] Add `<licenses>` block (GPL v3, with full URL)
- [ ] Add `<developers>` block
- [ ] Add `<scm>` block (`connection`, `developerConnection`, `url`)

### `pom.xml` — required plugins
- [ ] `central-publishing-maven-plugin` (the new Sonatype Portal plugin — *not* the deprecated `nexus-staging-maven-plugin` / OSSRH that older tutorials still document)
- [ ] `maven-gpg-plugin` for artifact signing
- [ ] `maven-javadoc-plugin` to produce a javadoc jar (`maven-source-plugin` is already present)

### JAR-content audit at publish time
Whatever ships in the first Central artifact is in the public record forever. Re-audit at publish time.

- [ ] Re-audit `src/main/resources` end-to-end: anything developer-facing, test-only, or environment-specific should not ship
- [ ] Re-audit `src/main/java` for any utility classes that should have been package-private rather than public (folds the residual API-surface work in if not already done by the API-surface release)
- [ ] (The test-fixture message keys are handled in the cleanup follow-through release; this audit is the safety net for anything similar that surfaces between now and publish)

### First publish + workflow
- [ ] Update README: drop the JitPack `<repositories>` block, leave only the plain Maven dependency snippet (no extra repository declarations needed for Central)
- [ ] Drop the JitPack URL and any related framing from README and other docs
- [ ] First publish via the Central Portal — staged release, manual approval the first time
- [ ] Verify the artifact appears at https://central.sonatype.com/artifact/io.github.dlbbld/...
- [ ] Document the per-release workflow (version bump → tag → `mvn deploy` → Portal release) in `setup.md` under a new "Releasing" section, or in a dedicated `release.md`

### Post-publish
- [ ] Decide whether JitPack stays available in parallel (free, harmless) or should be deprecated by removing the JitPack publish hook
- [ ] (Optional) Add a Maven Central status badge to the README

---
## Pseudo release - Role-inversion — `StaticPosition` subtree moves to `src/test/`

Will not be released, just implemented for proof of the role independency of StaticPosition and BitBoard.
The end-state described in the Project Invariant: the `StaticPosition` subtree (record, `StaticPositionUtility`, `com.dlb.chess.squares.*` consumer subset, `AbstractLegalMoves` + `*LegalMoves` consumers in `com.dlb.chess.moves`, `UnwinnabilityMaterial`) physically moves from `src/main/java/` to `src/test/java/` and becomes the permanent differential-test oracle from that point on. **Not deleted. Relocated.**

The role inversion: today the high-level board is a peer of the bitboard in `src/main/`; afterwards, it lives strictly above (test-side of) the bitboard. Production speaks only the bitboard. The high-level board exists to assert the bitboard is correct.

### Audit findings (run at the start of 10.0.0)

~25 src/main classes outside the relocation subtree still consumed `StaticPosition` after 10.0.0 shipped. They must be ported off before the physical relocation:

- **Public API:** `Fen` (record with a `StaticPosition` field), `Board.getStaticPosition()`, `InsufficientMaterialUtility.calculateIs*` overloads, `ChessRuleAnalyzer.analyze*` overloads.
- **FEN layer:** `FenParserAdvanced`, `FenBoard`, `FenMaterialCount`, `FenConstants.FEN_INITIAL`.
- **SAN layer:** `StrictSanParser`, `LenientSanShapeNormalize`, `SanValidateDestination`, `SanValidateLegalMoves`, `SanValidatePieceExists`, `SanPieceCheck`.
- **Board layer:** `ValidateNewMove`, `BoardMaterial`, `UciMoveUtility`.
- **Unwinnability layer:** `SemiOpenFilesUtility` (StaticPosition variant remains alongside its bitboard sibling), `UnwinnableFullAnalyzer` / `UnwinnableQuickAnalyzer` (Fen pass-throughs).

Plus the public-API decisions: the `Fen` record reshape is binary-incompatible; `Board.getStaticPosition()` removal breaks API. Both are fine — no downstream consumers exist (no Maven Central publish yet), so the project is free to break.

### Design decisions settled

- **`Board.getStaticPosition()`: dropped entirely.** No deprecated derived view. After this release no `src/main/` class references `StaticPosition`.
- **Test-tree package layout: original package names under `src/test/java/`.** No `reference/` package rename. Smaller diff; matches the established pattern (`LibraryCarlosBoard` already lives in `com.dlb.chess.board` on the test source root alongside the production `Board.java`). The relocation is purely physical.
- **Square geometry tables stay in `src/main/`:** `OrthogonalRange`, `DiagonalRange`, `RayUtility`, all `*EmptyBoardSquares` in `com.dlb.chess.squares`. Pure data, useful to the bitboard layer.
- **Non-StaticPosition `com.dlb.chess.moves` stays in `src/main/`:** `CastlingUtility`, `EnPassantCaptureUtility`, `KingCastlingLegalMoves`, `PromotionUtility`, `StandardMoveUtility`, `PawnDiagonalMoveUtility`.

### Phases — each its own commit-sized step, suite green after each

- ✅ **Phase 0** — `tasks.md` restructure for the role-inversion release: 8-phase plan spelled out, design decisions captured.
- ✅ **Phase 1** — Port the SAN layer off `StaticPosition`. Six classes: `StrictSanParser`, `SanValidateLegalMoves`, `SanValidateDestination`, `SanValidatePieceExists`, `SanPieceCheck`, `LenientSanShapeNormalize`. New bitboard helpers on `BitboardPosition`: `kingSquare(Side)`, `potentialToSquares(Square, long)`, `isOwnPiece(Square, Side, PieceType)`. Bitboard overload of `EnPassantCaptureUtility.calculateIsPotentialEnPassantCapture`. Differential test for `potentialToSquares` against `AbstractPotentialToSquares.calculatePotentialToSquare`.
- ✅ **Phase 2** — Ported `ChessRuleAnalyzer`, `InsufficientMaterialUtility`, `BoardMaterial`, `ValidateNewMove`, `UciMoveUtility`, `SemiOpenFilesUtility` (StaticPosition variant) off `StaticPosition`.
- ✅ **Phase 3+4** — Ported the FEN parser / serializer off `StaticPosition` and reshaped the `Fen` record: dropped `staticPosition`, added `bitboardPosition`. `FenParserAdvanced` builds `BitboardPosition` directly. `Board(Fen)` reads `fen.bitboardPosition()`. Binary-incompatible.
- ✅ **Phase 5** — Dropped `Board.getStaticPosition()` and `getStaticPositionBeforeLastMove()` entirely. No deprecated view. All `src/main/` readers ported off.
- ✅ **Phase 6** — Physical `git mv` of the relocation subtree to `src/test/java/`:
  - `src/main/java/com/dlb/chess/board/StaticPosition.java` → `src/test/java/com/dlb/chess/board/StaticPosition.java`
  - `src/main/java/com/dlb/chess/common/utility/StaticPositionUtility.java` → `src/test/java/com/dlb/chess/common/utility/StaticPositionUtility.java`
  - `src/main/java/com/dlb/chess/squares/{AbstractAttackedSquares, AbstractToSquares, AbstractPotentialToSquares, AbstractRangeSquares, *AttackedSquares, *PotentialToSquares, *RangeSquares}.java` → test tree (consumer subset only; geometry tables stay in main)
  - `src/main/java/com/dlb/chess/moves/{AbstractLegalMoves, *LegalMoves}.java` → test tree (consumer subset only; `CastlingUtility`, `EnPassantCaptureUtility`, `KingCastlingLegalMoves`, `PromotionUtility`, `StandardMoveUtility`, `PawnDiagonalMoveUtility` stay in main)
  - `src/main/java/com/dlb/chess/unwinnability/UnwinnabilityMaterial.java` → test tree
  - Plus production-side cleanup: dropped all `StaticPosition` overloads from `CastlingUtility`, `EnPassantCaptureUtility`, `StandardMoveUtility`. `BitboardLegalMoveFactory` inlines its own castling generation. `BitboardPosition.INITIAL_POSITION` / `EMPTY_POSITION` rewritten as standalone bit constants. New `StaticPositionBridge` in `src/test/java/com/dlb/chess/bitboard/` carries the round-trip helpers; `BitboardPositionUtility` (production-side) holds only `StaticPosition`-free helpers.
  - Original package names preserved (no `reference/` rename). After Phase 6 no `src/main/` code imports `StaticPosition` or any relocated consumer; doc comments may cross-reference, code may not.
- ✅ **Phase 7** — Formalized the permanent differential-test layer in `specification.md` (new §4.1 *Piece placement: bitboard in production, mailbox as test oracle* and §6.1 *Differential testing of the bitboard backend*) and here. Project policy from this point on: every primitive on `BitboardPosition` is asserted against the relocated `StaticPosition` oracle on every fixture in the corpus, for every supported release going forward.
- ✅ **Phase 8** — Release artifacts: version bump 10.0.0 → 11.0.0 (`pom.xml`, `README.md` ×2), `[11.0.0]` `CHANGELOG.md` entry above `[10.0.0]`.

---

## Backlog — captured but unscheduled

Items here are not assigned to any release. Captured so they don't get lost; revisit if/when scope or motivation aligns.

### Records carry data, not behavior — sweep for violations
The project rule (documented in `coding-conventions.md`): records carry data; domain logic that operates on them lives in dedicated utility / service classes. Permitted on a record: compact-constructor validation, `Comparable` when ordering is intrinsic, and language-provided `equals` / `hashCode` / `toString`. Domain-operation methods are not.

Surfaced by the unused-code-detector pass on `StaticPosition`: the record carries multiple non-data methods — `createChangedPosition` (three overloads), `isPawn`, `isOwnPawn`, `isOpponentPawn`, `isOwnKing`, `isOpponentKing`, almost certainly more. Some have only test callers (suggesting test scaffolding), some have production callers, one (`isOwnKing`) has zero callers anywhere.

- [ ] Catalog every non-permitted member on `StaticPosition` and assign a disposition per member: delete (no callers anywhere), move to a test-side helper that **takes** a `StaticPosition` rather than duplicating it (test-only callers), or move to a `StaticPositionUtility` (production callers).
- [ ] Sweep every record under `src/main/java` for the same pattern. Records to check include at least `Fen`, `Tag`, `PgnGame`, `LegalMove`, `MoveSpecification`, `StaticPosition`, plus any other top-level `record` declarations under `src/main`.
- [ ] Apply the dispositions; verify only the permitted member shapes remain on each record.
- [ ] Naturally folds into the API-surface reduction release, since most "move to utility" relocations open the door to making the utility itself package-private.

---

## Obsolete

Items deemed no longer worth pursuing. Captured so the decision is visible.

### Replace `EnumConstants` constant interface
`com.dlb.chess.common.constants.EnumConstants` is a `public interface` whose only purpose is to expose ~90 `public static final` aliases for `Square.*`, `Side.*`, `Piece.*`, `PieceType.*`, `Rank.*`, `File.*` so implementing classes inherit them unqualified. This is the classic "constant interface" anti-pattern (Effective Java item 22): interfaces should describe a contract/behavior, not be a convenience-inheritance vehicle for constants. The mechanism reads as beginner Java and leaks an internal vocabulary choice into the public type surface — `ChessBoard extends EnumConstants` is the clearest symptom (the chess contract has nothing to do with how implementers prefer to spell `Square.E4`). Used by 43 files under `src/main` plus tests.

Replacement strategy options, depending on intended audience:
- public-API constants: `public final class EnumConstants` with `public static final` fields and a private constructor (callers `import static`)
- internal-only: make package-private and split closer to where they belong (domain-grouped, e.g. `BoardSquares`, `PieceLetters`)
- derived enum collections: prefer local `EnumSet` / `ImmutableSet` factories in the utility that needs them, or dedicated package-private constants classes by domain

- [ ] Pick a replacement strategy (default lean: package-private utility class with `import static`, since the constants are internal vocabulary and the audit reduces public surface anyway)
- [ ] Drop `extends EnumConstants` from `ChessBoard` regardless of strategy — the interface should not carry constants
- [ ] Convert the 43 src/main call sites + tests to static imports
- [ ] Folds naturally into the API-surface reduction release, since most "move to utility" relocations open the door to making the utility itself package-private.

### Profound-level square geometry — promote single-step calculations to lookup tables
The codebase already uses lookup tables for the geometry that matters — `OrthogonalRange`, `DiagonalRange`, `KnightEmptyBoardSquares`, `BishopEmptyBoardSquares`, `RookEmptyBoardSquares`, `DiagonalLineUtility`. Single-step instance-style methods on `Square` (`calculateLeftSquare`, `calculateLeftDiagonalSquare`, `calculateAheadSquare`, etc.) and `File` / `Rank` are the calculate-on-demand holdouts in an otherwise table-based codebase. The "calculate" form has a deeper testing problem: any independent test implementation faces a definitional regress ("left of E4 from White is D4 — but what does *left* mean if not what `calculateLeft` returns?"), which is how `Square.calculateIsLeftDiagonalSquare` ended up as a tautological method that tested itself against itself.

The fix is to promote these single-step relationships to data:
- `Map<Square, Map<Side, Square>>` (or `EnumMap<Square, EnumMap<Side, Square>>`) constants for left, right, ahead, behind, left-diagonal, right-diagonal
- The "has" predicates collapse to `map.containsKey(...)` or `value != NONE`
- The map is built once at class load; tests verify the table by inspection or via python-chess cross-reference (folds into the existing python-chess backlog)
- The bug surface shrinks to one place: the table-builder

Marked obsolete because the bitboard release will replace this whole layer of square arithmetic with bit-level operations; doing the `EnumMap` refactor first would be throwaway work. The definitional-regress testing problem also dissolves once `BitboardPosition` exists as an independent oracle.

- [ ] Inventory single-step `calculate*` methods on `Square` / `File` / `Rank` that are pure square→square (or square+side→square) lookups
- [ ] Replace each with a precomputed `EnumMap` constant + a thin accessor
- [ ] Generate the expected tables either by hand-curation or by python-chess cross-reference (latter is preferred once the python-chess infrastructure lands)
- [ ] Drop the algorithm-vs-algorithm test patterns; tests become "look up in production table, compare to reference table"
- [ ] **Companion concern — bloated lookup-table implementations.** `PawnDiagonalSquares` is 826 lines of generated code (per-square `addWhiteA1`, `addWhiteA2`, … methods) to express what is conceptually "for each pawn from-square, the 0–2 diagonal capture squares." The same shape recurs across the `com.dlb.chess.squares.emptyboard.*` family (`Knight`, `Bishop`, `Rook`, `Queen`, `King`, `PawnOneAdvance`, `PawnTwoAdvance`, `PawnAnyAdvance`). These tables are correctly precomputed, but their implementation should be a single `static {}` initializer that loops over `Square.REAL` and computes each entry via simple file/rank arithmetic — not hundreds of method-per-square stubs. Replacing them collapses ~thousand-line files to dozens of lines while preserving the precomputed-table API. Same theme as the main bullet: keep the lookup, sane the implementation.

---
