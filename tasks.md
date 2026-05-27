# Tasks

Order within each section is the source of truth. Completed tasks move to **Done** at the bottom.

---

## The story when releases are done

*clean-chess started as a correctness-first reference implementation, built from the FIDE rules without consulting existing libraries. It found correctness bugs in python-chess and ScalaChess along the way. Once the rules were stable, a bitboard backend was added alongside the reference layer and verified bit-exact against it. Production then switched to the bitboard path; the reference layer was relocated into the test tree and remains as the permanent differential-test oracle. Cross-validation against python-chess was reactivated as primary, with `chesslib` retained as a second witness. Only then published to Maven Central.*

---

## Current release — 15.0.0: termination is information, not enforcement

✅ Shipped 2026-05-26. The move-validation pipeline no longer consults any game-end predicate. Checkmate, stalemate,
mutual insufficient material, fivefold repetition, the 75-move rule, and analyzer-driven dead positions are all
queryable artifacts the caller polls. At checkmate / stalemate the empty legal-move set is the natural barrier; at the
other terminations legal moves still exist and play continues until the caller adjudicates.

The release also replaced the `GameStatus` enum with a structured `Outcome` record (`Outcome(Termination, @Nullable Side
winner)` — python-chess parity), reordered precedence to match python-chess (`mate → IM → stale → 75 → fivefold`),
aligned `isFiftyMove` / `isSeventyFiveMove` with python-chess and FIDE (require legal moves to exist), and unlocked the
python-chess import oracle to run unguarded across all 35,001 plies in the corpus. The `canClaimFiftyMoveRuleWithOwnMove`
predicate follows the strict FIDE 9.3 reading — one deliberate divergence from python-chess at a constructed corner
case where the only non-zeroing legal move at clock 99 delivers mate. python-chess's behavior at that edge is
collateral from code reuse (not from documented intent — verified against blame and the maintainer's own tests, which
pin the current-position-is-mate case but not the candidate-move-is-mate case).

Supporting work shipped in the same release: `Reporter` slimmed to print-only (`Report` record deleted); `PgnTestCase`
reduced to `(pgnName, finalFen)` and renamed `PgnFen` with ~1,400 catalog entries rewritten; internal `HalfMove`
slimmed; initial-position repetition bug fixed; doc passes through `specification.md` §3.1, the board package-info,
and the relevant test JavaDoc.

See `CHANGELOG.md` `[15.0.0]` for the full Notable / Behavioral / Breaking breakdown including migration snippets.

---

## Released — 14.0.0: drop auto-CHA-per-move; dead-position queries become request-based

✅ Shipped 2026-05-24. Analyzer-driven dead-position detection no longer runs automatically on construction or every
move, `DEAD_POSITION_UNWINNABLE_QUICK` is reportable but non-blocking, and the boolean `Board` constructor/config
overloads are removed. Structural insufficient material remains move-blocking _at the time of this release_; the
follow-up ungating in commits `d123759f` (A1) and `6d69c3e0` (A2) drops that too, leaving the move pipeline with no
game-end gate and no `GAME_ALREADY_ENDED` plumbing.

The construction we have today is too complicated and does work the library doesn't need. Today every `Board.move()` (and every `Board` constructor) runs the unwinnability quick analyzer on the new position and caches the verdict in `isDeadPositionUnwinnableQuickList`. The cached value drives `Board.isDeadPositionUnwinnableQuick()`, feeds `Board.isDeadPosition()` (alongside the cheap mechanical `isInsufficientMaterial`), and through `ValidateNewMove` causes the move pipeline to throw `MoveCheck.GAME_ALREADY_ENDED` with `GameStatus.DEAD_POSITION_UNWINNABLE_QUICK` if a consumer tries to play on. The whole apparatus exists to model FIDE 5.2.2 "dead position" as an automatic termination.

**The premise the auto-termination relies on is false in practice.** Playing on in a dead position is harmless: no win is reachable, the position can only resolve to a draw. In a real game the practical outcomes are all draws — flagfall in a dead position is a draw by adjudication, resignation in a dead position is a draw under current FIDE rules (no win available to the opponent), and the players can always agree to a draw. Nothing changes if they keep moving. So the library does not need to *enforce* dead-position termination at the move-pipeline boundary; it only needs to make it *queryable* so consumers that want to surface it can do so.

The current eager-per-move construction also costs more than just complexity: it makes every move pay the analyzer cost (mitigated but not erased by the bitboard work), it adds a constructor flag (`detectDeadPositionUnwinnable`) that has to be threaded through every `Board` overload, it requires a recursion-suppression guard inside the analyzer when it builds throwaway sub-boards, and it leaks into the test corpus — one fixture remains parked in `pgnParser/legacy/common/beyond/` whenever the recorded game steps into a dead position even though the PGN is otherwise well-formed. The fix simplifies all of that.

The fivefold / 75-move counterpart shipped in **13.0.0** (the *reallow-play-beyond* release): the same eager-termination pattern was dropped for those rules first, since (a) the same arguments applied symmetrically, (b) playing past those thresholds is the more common real-world situation (historical PGN corpora routinely contain games that do), and (c) the python-chess cross-validation work surfaced concrete frictions — `is_fifty_moves` / `is_seventyfive_moves` semantics differ at game-end positions because python-chess requires legal-moves-exist, and `canClaimThreefoldRepetitionRule()` / `canClaimFiftyMoveRule()` crash on auto-terminated positions because they internally simulate `Board.move()`. The dead-position drop here is the analogous step for FIDE 5.2.2.

### Phase 1 — Drop auto-detection from `Board`

**Step 1.1** — Remove the per-ply cache and computation. Drop `Board.isDeadPositionUnwinnableQuickList`, `Board.computeDeadPositionUnwinnableQuick()`, the `isDetectDeadPositionUnwinnable` field, every `Board` constructor overload that took the `detectDeadPositionUnwinnable` flag (including `copyCurrentPositionWithoutHistory(boolean)`), and the eager calls from `Board` constructors and `performMoveWithoutValidation`. `Board.isDeadPositionUnwinnableQuick()` as a stateful accessor is removed.

**Step 1.2** — Reshape `Board.isDeadPosition()` and the `isDeadPosition*` family as pure on-demand queries that run the analyzer when called. `isDeadPositionQuick()` and `isDeadPositionFull()` are already that today (`Board` lines ~951 / ~999); the change is that `isDeadPosition()` joins them — it computes `isInsufficientMaterial() || isDeadPositionQuick().isDeadPosition()` at the call site instead of reading a cached field. Consumers that want the old "checked after every move" behavior call the query themselves in their move loop.

**Step 1.3** — `ValidateNewMove` no longer rejects moves on `DEAD_POSITION_UNWINNABLE_QUICK`. Remove the corresponding check from the move-acceptance precondition. The surviving auto-terminators after this release: checkmate, stalemate, insufficient material. `MoveCheck.GAME_ALREADY_ENDED` continues to fire for those three. _(Fully superseded by the follow-up release: A1 (`d123759f`) removed the gate for the remaining three; A2 (`6d69c3e0`) deleted the `GAME_ALREADY_ENDED` enum values, exception payloads, and message-bundle entry. The move pipeline now consults no game-end predicate and `GAME_ALREADY_ENDED` no longer exists.)_

**Step 1.4** — Decide the `GameStatus.DEAD_POSITION_UNWINNABLE_QUICK` enum value's fate. Two options: keep it (returned by an explicit "what status is this position in" query, never thrown by the move pipeline) or drop it (the analyzer's `UnwinnabilityQuickVerdict` is the only place that concept lives). Recommend keep, narrowed in scope — `Board.calculateGameStatus()` maps the position to a status that can include `DEAD_POSITION_UNWINNABLE_QUICK`, but the move pipeline never throws it. Symmetric with how 13.0.0 handled `FIVE_FOLD_REPETITION_RULE` / `SEVENTY_FIVE_MOVE_RULE`.

### Phase 2 — Corpus and test cleanup

**Step 2.1** — Reactivate the lone remaining legacy fixture `lastMoveAddedAccidentally/02_…_KvK.pgn` into the regular corpus (likely `pgn/realGames/lastMoveAddedAccidentally/`). Generate its `PgnFen` catalog entry via `GenerateTestCaseForPgn`; shrink `TestLegacyPgnParsePlaysBeyondAudit` to zero entries and delete the test class once empty. The `pgnParser/legacy/common/beyond/` tree disappears.

**Step 2.2** — Any test that asserts on `GameStatus.DEAD_POSITION_UNWINNABLE_QUICK` being thrown from the move pipeline switches to asserting on the queryable predicate. Drop the 4 corresponding `test05`–`test06` (insufficient-material) play-beyond tests in `TestStrictPgnParserBeyondTermination` / `TestLenientPgnParserBeyondTermination` once dead-position termination is gone too — by then the `pgnParser/common/beyond/` folder is empty.

**Step 2.3** — Remove the two skip guards in `TestPgnImportAgainstPythonChessOracle` and `TestInsufficientMaterialAgainstPythonChessOracle` that today work around the `canClaim*` crashes — once auto-termination is gone, the python-chess oracle should sweep cleanly at every position with no game-end carve-outs.

**Step 2.4** — `specification.md` §3.1 rule-fidelity table: dead position moves from "automatic" to "queryable" (symmetric with what 13.0.0 did for fivefold / 75-move). Updated `Board` Javadocs for the `isDeadPosition*` family.

### Phase 3 — Release artifacts

**Step 3.1** — Version bump 13.0.0 → 14.0.0 (binary-incompatible: `Board` constructor signatures with the `detectDeadPositionUnwinnable` flag are removed). `CHANGELOG.md` entry above the 13.0.0 release. `tasks.md` section marked done.

---

## Future release — 16.0.0: make threefold and fifty-move report production grade and clean-up

Do not start this immediately after the current release. This section exists so the follow-up work is captured, scoped,
and not rediscovered chaotically later.

The release goal is to turn the repetition/no-progress report work from useful scaffolding into tested, object-level
library behavior. The printout should become a view over analysis objects, not the place where chess/report logic lives.

### Phase 1 — Object-level repetition report model

- [ ] Introduce a report/analysis object for threefold-existing-behind / threefold-and-beyond repetitions.
- [ ] Introduce a report/analysis object for threefold claim-ahead opportunities.
- [ ] Preserve the current printed report shape by deriving it from those objects.
- [ ] Move repetition-line, initial-position inclusion, played-vs-hypothetical, and repetition-count decisions out of the print classes.
- [ ] Keep print classes small: format already-calculated facts only.

### Phase 2 — Test the report objects directly

- [ ] Add direct unit tests for existing threefold-and-beyond analysis.
- [ ] Add direct unit tests for threefold claim-ahead analysis.
- [ ] Add regression coverage for the initial-position repetition case that was previously missing from the printout.
- [ ] Add tests that compare the report printout against the object model, so formatting cannot silently diverge from the analysis.

### Phase 3 — Add from-initial-placement and FEN-start coverage

Create one focused test folder for positions that start counting from move one. The folder covers both the initial piece
placement and games started from an explicit FEN.

- [ ] Initial piece placement, White to move on move 1: run into threefold, fivefold, 50-move, and 75-move conditions from move one.
- [ ] Initial piece placement, Black to move on move 1: same coverage, with special attention to fullmove numbering.
- [ ] Non-initial FEN position, White to move on move 1: run into threefold, fivefold, 50-move, and 75-move conditions from move one.
- [ ] Non-initial FEN position, Black to move on move 1: same coverage, with special attention to fullmove numbering.
- [ ] For each fixture, assert the public query methods detect the condition.
- [ ] For each fixture, assert the object-level report model contains the expected facts.
- [ ] For each fixture, assert the printed report is derived correctly from the object-level facts.

### Phase 4 — Finish the 50-move report output

Reserved for manual implementation. The goal is to get comfortable with the report code by finishing the no-progress /
50-move output on top of the object-level analysis shape, not inside presentation logic.

- [ ] Finish the 50-move output.
- [ ] Add direct tests for the 50-move report object.
- [ ] Add printout tests derived from the object-level expected facts.

### Phase 5 — Per-move claim API (FIDE 9.2 / 9.3 fidelity)

Currently both `canClaimFiftyMoveRule()` / `canClaimThreefoldRepetitionRule()` collapse to position-wide booleans: true
if *any* legal move would satisfy the claim. FIDE 9.2 (threefold) and 9.3 (50-move) actually frame the claim as a
per-move act — the player announces the specific move they intend to play and claims the draw on that announcement.
clean-chess inherited the collapsed shape from python-chess, which has the same gap (`board.can_claim_fifty_moves()`
takes no move parameter). The collapsed shape is what enabled the mate-edge bug fixed in 15.0.0
(`canClaimFiftyMoveRuleWithOwnMove`); a per-move predicate would have surfaced the question naturally during design,
not as an arbiter's catch years later.

- [ ] Add `Board.canClaimFiftyMoveRuleFor(MoveSpecification move)` — true iff `move` is legal, non-pawn, non-capture,
      and `halfMoveClock >= 99`. Validates the FIDE 9.3 act of *this specific announced move* completing the 50.
- [ ] Add `Board.canClaimThreefoldRepetitionRuleFor(MoveSpecification move)` — true iff `move` is legal and the
      resulting position is a threefold occurrence. Validates the FIDE 9.2 act for *this specific announced move*.
- [ ] Add `Board.canClaimDrawFor(MoveSpecification move)` — composed convenience (`canClaimFiftyMoveRuleFor(move) ||
      canClaimThreefoldRepetitionRuleFor(move)`).
- [ ] Keep the existing existence predicates (`canClaimFiftyMoveRule`, `canClaimFiftyMoveRuleWithOwnMove`,
      `canClaimThreefoldRepetitionRule`, `canClaimThreefoldRepetitionRuleWithOwnMove`, `canClaimDraw`) as convenience
      shorthand. Their behavior at the existence level stays unchanged.
- [ ] Tests: pin the mate-in-one edge per-move — `canClaimFiftyMoveRuleFor(Nf7)` returns true on the FEN already used
      in `TestBoardClaimWithOwnMove#canClaimFiftyMoveRuleWithOwnMoveTrueEvenWhenOnlyNonZeroingMoveIsMate`, even though
      Nf7 is itself mate. Symmetric edge for stalemate.
- [ ] Tests: pin that a candidate move whose post-position is *not* the claimed condition returns false (e.g. a pawn
      move resets the clock and is rejected by the 50-move per-move predicate even if other non-zeroing moves exist).
- [ ] Wire the per-move predicates into the threefold claim-ahead report object (Phase 1) so the report's per-move
      entries are computed from a single source of truth.
- [ ] `specification.md` §3.1: update the "claimable rules" paragraph to mention the per-move predicate as the
      FIDE-faithful shape and the existence predicate as the convenience.
- [ ] Reference the python-chess upstream issue (filed during 15.0.0 work) so a future contributor can see the
      cross-library context — both libraries had the same gap; clean-chess closes it here.

### Phase 6 — Decommission `HalfMove`

The short-term rule is: do not break the working repetition analysis casually. The release should first remove
`HalfMove` from `Board` as stored mutable state, then decide whether it can disappear completely.

- [ ] Remove stored `halfMoveList` from `Board`; derive temporary `HalfMove` rows from existing board history where needed.
- [ ] Keep repetition/report behavior unchanged during that first removal.
- [ ] Analyze whether the repetition/report objects can use narrower records instead of `HalfMove`.
- [ ] If possible, replace `HalfMove` entirely with purpose-specific repetition/report rows.
- [ ] Delete `HalfMove`, `HalfMoveUtility`, and related comparators once no longer needed.
- [ ] If full deletion is not safe in this release, leave a precise follow-up note explaining the remaining dependency.

### Phase 7 — Release hygiene

- [ ] Keep the release scoped to report/repetition/no-progress cleanup.
- [ ] Do not mix in the insufficient-material play-beyond release unless explicitly re-scoped.
- [ ] Update `README.md`, `specification.md`, and `CHANGELOG.md` only for behavior or documented workflows that actually changed.
- [ ] Run focused report/repetition tests first, then the full default profile before release.

---

## Future release — python-chess primary cross-validation + PGN/FEN test coverage expansion

The third release. Reactivates the python-chess test path (currently dormant), makes python-chess the main move-test reference, and expands PGN import/export test coverage — especially the FEN-anchored cases that `chesslib` cannot exercise.

### Context

The project historically tested against python-chess via `GeneratePythonTestCases.java`, which generates a Python test script from clean-chess fixtures. **That generator exists in the codebase but there is no active test that runs the generated Python script** — the comparison pipeline is dormant. Reactivating it is part of this release.

Carlos's `chesslib` (`LibraryCarlosBoard`) cannot import PGN from a non-initial-position via the `FEN`/`SetUp` tags. That gap is why python-chess becomes the *primary* cross-validation reference after this release. `chesslib` is retained as a second witness — having two independent oracles is more valuable than having one.

### Pattern recommendation — generation-based, not live invocation

- A Python script using python-chess generates expected outputs (legal moves, FEN, SAN, LAN, repetition counts, halfmove clock, dead-position verdicts) for a battery of fixtures, writes to a fixed file path.
- Java tests read the file and compare to clean-chess output.
- The Python script runs only when fixtures are added or regenerated, **not** during `mvn test`.
- Chess outputs are deterministic per input; cached reference data doesn't go stale.

### Discussion items to settle before coding

- [ ] Inventory exactly what python-chess will be used as reference for: legal-move generation, SAN/LAN, FEN, repetition counts, fifty-move clock, threefold/fivefold, dead-position (does python-chess support this directly or via heuristic?), CHA-style unwinnability (it doesn't — that stays unique to clean-chess).
- [ ] Decide: gradual migration (both `chesslib` and python-chess as references during transition) or hard cutover. Lean: gradual — keep `chesslib` as a second witness permanently.
- [ ] Document the toolchain requirement: contributors need Python 3 + `pip install chess`. Goes in `setup.md`.
- [ ] Plan the regeneration workflow: how is "I added a fixture; now regenerate the python-chess-expected outputs" triggered cleanly? Maven goal? Script? Make target?

### Reactivation work

- [ ] Audit `GeneratePythonTestCases.java` — current state, what it produces, what's still wired up after the dormancy period
- [ ] Decide and document the file format for stored expected outputs (JSON? line-based? CSV?)
- [ ] Refactor (or replace) the generator to produce the agreed format
- [ ] Build the Java-side consumer: read the expected-outputs file, compare to clean-chess output, fail loudly on mismatch
- [ ] Migrate at least one cross-validation test from `chesslib` to python-chess as a proof-of-concept

### python-chess as primary reference

- [ ] Migrate cross-validation tests from `chesslib` to python-chess for the surface python-chess covers
- [ ] Keep `LibraryCarlosBoard` as a second oracle — do not delete; two independent witnesses is the right shape

### PGN import/export test coverage expansion

The area `chesslib` cannot test and python-chess can: PGN imported from a non-initial position via the `FEN`/`SetUp` tags. Currently the test corpus skews toward initial-position games; expanding here is overdue, and python-chess being primary makes it feasible for the first time.

- [ ] Catalog the missing PGN-import-with-FEN test cases: short examples per side-to-move, per castling-right combination, per en-passant target square, per non-trivial half-move-clock / full-move-number
- [ ] Cross-validate each against python-chess output
- [ ] PGN export coverage: round-trip tests for PGN files that started with a non-initial `FEN` tag — both archival and semantic export modes
- [ ] FEN export coverage: round-trip from python-chess-generated FEN strings (real-world FEN exporters produce inputs the strict parser may not love)

---

## Future release — publish to Maven Central

The capstone release. Publish to Central only when the library has stabilised — every prior release done, identity questions settled, and any tasks that surface during the prerequisite work itself addressed first. Maven Central artifacts are immutable: once published, an artifactId+version pair lives forever in the public record. The bar for moving from JitPack to Central is therefore "we are confident this artifact represents the project well, indefinitely."

### Prerequisites — must be true before any Central work begins
- [ ] DeepSquare release complete (Auto-CHA + Zobrist + pawn-wall classifier + foundational refactors)
- [ ] Bitboard release complete (performance acceptable, differential-test harness green)
- [ ] python-chess primary + PGN/FEN coverage release complete
- [ ] Rename decision resolved — clean-chess → DeepSquare or final name. Once published, the artifactId is permanent
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
