# Tasks

Order within each section is the source of truth. Completed tasks move to **Done** at the bottom.

---

## The story when releases are done

*clean-chess started as a correctness-first reference implementation, built from the FIDE rules without consulting existing libraries. It found correctness bugs in python-chess and ScalaChess along the way. Once the rules were stable, a bitboard backend was added alongside the reference layer and verified bit-exact against it. Production then switched to the bitboard path; the reference layer was relocated into the test tree and remains as the permanent differential-test oracle. Cross-validation against python-chess was reactivated as primary, with `chesslib` retained as a second witness. Only then published to Maven Central.*

---

## Current Bitboard release — project invariant — the `StaticPosition` reference implementation is never lost

The mailbox `StaticPosition` representation and every piece of the rich board implementation built on top of it — attack queries (`AbstractAttackedSquares` and the per-piece-type classes under `com.dlb.chess.squares`), legal-move generation (`AbstractLegalMoves` and per-piece classes under `com.dlb.chess.moves`), `StaticPositionUtility`, insufficient-material detection, repetition logic, the `Board`-level FIDE-rules machinery, the SAN/LAN encoding paths that consume `StaticPosition` — represents several years of correctness-first work, hand-derived independently from the FIDE rules. **This implementation is the project's correctness ground truth. It is not deleted. Ever.**

### What this means concretely across the bitboard migration

1. **The bitboard release is purely additive.** `BitboardPosition` is built alongside `StaticPosition`. Throughout that entire release every existing `StaticPosition`-based code path stays compilable, callable, and tested. Production hot paths are NOT switched in this release.
2. **Every bitboard primitive is differential-tested bit-exact** against the corresponding `StaticPosition`-based code, on the full PGN/FEN corpus. Disagreement is a correctness signal; the bitboard side is the one that must yield.
3. **Switchover (the release after the bitboard release) is gated** on the differential-test harness being green across the full corpus.
4. **At switchover, the `StaticPosition` subtree moves from `src/main/java/` to `src/test/java/`.** Not deleted. Relocated. The classes that move together include at minimum: `StaticPosition`, `StaticPositionUtility`, the `com.dlb.chess.squares.*` family, the `com.dlb.chess.moves.*` family (those that consume `StaticPosition`), and any utility classes whose only consumers are this subtree. After the move, no `src/main/` code references any of them.
5. **A permanent differential-test layer is maintained** driving the relocated `StaticPosition` oracle against `BitboardPosition`, on every test fixture, for every primitive — piece queries, attacks, legal moves, make-move, check, insufficient-material, repetition equivalents. The two representations continue to agree on every test position, forever.

### Non-negotiable precondition

**If at any point it becomes clear that the migration cannot reach the end-state of "`StaticPosition` lives in `src/test/` as the perpetual oracle, with a full differential-test layer driving the bitboard against it," the migration does not happen.** Performance is not bought at the cost of deleting the reference implementation. The slow-and-right phase stays available as the witness — whether under `src/main/` (today) or under `src/test/` (after switchover).

### Why this matters

clean-chess's identity is "correctness-first reference implementation, derived independently of every other chess library." The `StaticPosition` path is that identity expressed in code. It is what enables the bitboard release to be verifiable in the first place. Two independently-derived representations agreeing is information-theoretically stronger than one fast representation alone. The project keeps both, permanently.

This rule overrides any task in any release section below. Tasks that conflict with it are wrong as stated and must be reframed.

---

## Current Bitboard release — Implementation plan

Commit-sized steps suitable for Codex review. Each step is one PR-style commit on `use_bitboard_for_rich_board`, with the differential test attached to the step that needs it. Sliding attacks use classical ray loops in this release (magics deferred to the switchover release where they actually pay off); make-move is immutable-only here (mutable make/unmake belongs to the lean-analyzer release).

### Status

- ✅ **Phase 0** — `13e56fbe` — unwinnability tests disabled
- ✅ **Step 1.1** — `2c671f16` — `BitboardPosition` record + package skeleton
- ✅ **Step 1.2** — `fb4132d1` — `BitboardPosition` ⇄ `StaticPosition` conversion + round-trip differential test
- ✅ **Step 1.3** — `4427957d` — `BitboardPosition.get(Square)` / `isEmpty(Square)` + differential piece-query test
- ✅ **Step 2.1** — `acb6cc0d` — `KnightAttacks` precomputed `long[64]` table + differential test (also adds `BitboardPositionUtility.toSquareSet`)
- ✅ **Step 2.2** — `94485cd6` — `KingAttacks` precomputed `long[64]` table + differential test (castling stays on `Board`)
- ✅ **Step 2.3** — `cbfa1766` — `PawnAttacks` two `long[64]` tables (per side) + differential test. Phase 2 complete.
- ✅ **Step 3.1** — `38dfae2a` — `BishopAttacks.attacks(int, long)` via classical ray loops + differential test (adds `BitboardPosition.occupied()` and the `SlidingAttacksTestOracle` test bridge)
- ✅ **Step 3.2** — `b81f7c4e` — `RookAttacks.attacks(int, long)` via classical ray loops + differential test
- ✅ **Step 3.3** — `039d28a5` — `QueenAttacks.attacks(int, long)` = bishop | rook + differential test. Phase 3 complete.
- ✅ **Codex P2 fix** — `66bee5fa` — `BitboardPosition` compact constructor rejects overlapping piece bitboards
- ✅ **Codex open Q** — `8fec1bac` — `BitboardPosition.occupied(Side)` + differential test (positions Phase 4 cleanly)
- ✅ **Step 4.1** — `fb73f2da` — `BitboardPosition.attackedSquares(Side)` + differential test against `AbstractAttackedSquares.calculateAttackedSquares`
- ✅ **Step 4.2** — `a0ff8af7` — `BitboardPosition.isInCheck(Side)` + differential test against `StaticPositionUtility.calculateIsCheck`
- ✅ **PawnAttacks geometric** — `ca75a3e0` — expand `PawnAttacks` to all 64 from-squares (enables the reverse-attack identity at the back ranks)
- ✅ **Step 4.3** — `ff678853` — `BitboardPosition.attackersTo(Square, Side)` + differential test. Phase 4 complete.
- ✅ **Step 5.1** — `b1f73147` — per-piece pseudo-legal target generators (`KnightMoves`, `KingMoves`, `BishopMoves`, `RookMoves`, `QueenMoves`) + differential test
- ✅ **Step 5.2** — `7fdeb435` — `PawnMoves.pushes` (single + double + promotion) + differential test
- ✅ **Step 5.3** — `920ebce0` — `PawnMoves.captures` (regular + en-passant) + differential test. Phase 5 complete.
- ✅ **Step 6.1** — `b2d4cf5b` — `BitboardPosition.legalKingTargets(Side)` (XRAY-aware king-safety filter) + differential test against `KingNonCastlingLegalMoves`. Adds `attackedSquares(Side, long occupiedOverride)` overload and the `LegalMovesTestOracle` test bridge.
- ✅ **Step 6.2** — `745251a0` — `BitboardPosition.pinRay(Square, Side)` and `pinnedPieces(Side)` + differential test against a "remove and re-check enemy slider attackers" reference oracle
- ✅ **Step 6.3** — `71c87937` — `BitboardPosition.legalMoves(Side, long enPassantBit)` + **spine differential test** against `Board.getLegalMoves()` (castling-filtered) for every fixture × side-to-move. Phase 6 complete.
- ✅ **Step 7.1** — `efac6be4` — immutable `BitboardPosition.afterMove(MoveSpecification, Side) -> BitboardPosition` + **second spine differential test** against `StaticPositionUtility.createPositionAfterMove` for every fixture × every legal move (normal, capture, EP, four promotion targets, both castling sides). Phase 7 complete.
- ✅ **Steps 8.1 + 8.2** — `9b406562` — `ZobristKeys` (768-entry piece-square table from a fixed seed) + `BitboardPosition.zobristPieces()` (piece-placement hash) + collision-free / equal-position-equal-hash / move-changes-hash property tests
- ✅ **Step 8.3** — `58081891` — `BitboardPosition.hashDelta(MoveSpecification, Side)` (incremental Zobrist XOR) + corpus differential test against fresh recomputation. Phase 8 complete.
- ✅ **Step 9.1** — `60f07052` — re-enabled all 18 unwinnability test classes disabled in Phase 0. Phase 9 complete.

**Bitboard backend release: COMPLETE.** Suite 1125 tests / 0 failures / 0 errors / 4 skipped (the 4 are pre-existing `IS_EXCLUDE_LONG_RUNNING_*` exclusions). Runtime ~54 s on this machine — well under the previous baseline. The bitboard layer is a fully-verified parallel implementation of `StaticPosition`-based move generation, move application, and Zobrist hashing across the entire corpus. Per the Project invariant, `StaticPosition` and every class derived from it remain in `src/main/` unchanged; production hot paths still consume them.

**Baseline for the switchover release (Phase 9.2):** `findHelpMate` continues to run on the `StaticPosition` pipeline. The full unwinnability test suite contributes ~13 s to the 54 s total. This is the slow-and-right baseline against which the switchover release's lean-analyzer-board perf work will be measured.

### Cross-cutting decisions (settled upfront)

- **Bit layout**: little-endian rank-file, `bit_i = Square.values()[i].ordinal()`. Already true from the `Square` enum's declaration order — no remapping code.
- **Representation**: 12 `long` fields on `BitboardPosition` (one per `Piece` value `WHITE_PAWN`..`BLACK_KING`), record-shaped. `occupied(Side)` / `occupied()` are derived methods. Records carry data only (project rule).
- **Sliding attacks**: classical ray loops on bitboards. Magics are a follow-on perf step. Bitboard *shape* is what unlocks the lean analyzer board (release 3); bitboard *speed* via magics is separable.
- **Make/unmake**: immutable `afterMove(MoveSpecification) -> BitboardPosition` only. Mirrors `StaticPositionUtility.createPositionAfterMove`, so differential testing is direct equality. Mutable make/unmake belongs to the lean-analyzer release.
- **Differential-test harness**: a `BitboardDifferentialAssert` helper that the existing PGN-corpus tests can call from inside their per-position loops. Piggybacks on existing traversal — no separate corpus walk.
- **Production callers unchanged**: `Board`, the `squares/*AttackedSquares` classes, and the `moves/*LegalMoves` classes keep using `StaticPosition`. This release is pure background.
- **No `Board` integration in this release.** That's explicitly release 3.

---

### Phase 0 — Disable unwinnability tests

**Step 0.1** — Mark every test class under `src/test/java/com/dlb/chess/unwinnability/` and `src/test/java/com/dlb/chess/test/unwinnability/` (the `Test*.java` files) with:

```java
@Disabled("Suspended for the bitboard backend release; re-enabled in Phase 9.")
```

Plain JUnit `@Disabled` rather than a new `RestrictTestConstants` flag — simplest to unwind in Phase 9, and the reason is self-documenting.

Single commit. Verify the suite still passes and is meaningfully faster.

---

### Phase 1 — Foundation

**Step 1.1** — Create package `com.dlb.chess.bitboard` with `package-info.java` (`@NonNullByDefault`). Add `BitboardPosition` record with 12 `long` fields (one per real `Piece` value, field order matches `Piece.REAL`). No methods or constants yet beyond what records auto-generate. *(`INITIAL_POSITION` / `EMPTY_POSITION` constants moved to Step 1.2 where they become trivial one-liners off `fromStaticPosition`.)*

**Step 1.2** — `BitboardPosition.fromStaticPosition(StaticPosition)` + `toStaticPosition()`. Add `INITIAL_POSITION` and `EMPTY_POSITION` constants, computed off `fromStaticPosition(StaticPosition.INITIAL_POSITION)` and `fromStaticPosition(StaticPosition.EMPTY_POSITION)`. First differential test class `TestBitboardPositionRoundTrip`: for every `PgnTestCase.finalPosition()` in the corpus, assert `BitboardPosition.fromStaticPosition(sp).toStaticPosition().equals(sp)`. This is the spine — every later step rides on this being green.

**Step 1.3** — `BitboardPosition.get(Square) -> Piece` and `isEmpty(Square)`. Differential test: corpus × all 64 squares, both representations agree.

---

### Phase 2 — Non-sliding attacks

**Step 2.1** — `KnightAttacks` class with precomputed `long[64]` table. Differential test against `KnightAttackedSquares.calculateKnightAttackedSquares` for every knight on every corpus position.

**Step 2.2** — `KingAttacks` class with precomputed `long[64]` table (non-castling attacks only — castling lives on `Board`, not on `BitboardPosition`). Differential test against `KingNonCastlingAttackedSquares`.

**Step 2.3** — `PawnAttacks` class with two `long[64]` tables (white-from-square, black-from-square). Differential test against `PawnAttackedSquares`.

---

### Phase 3 — Sliding attacks (classical)

**Step 3.1** — `BishopAttacks.attacks(int sq, long occupied) -> long` via four ray loops (NW, NE, SW, SE) with edge masks. Differential test against `BishopAttackedSquares`.

**Step 3.2** — `RookAttacks.attacks(int sq, long occupied) -> long`. Differential test against `RookAttackedSquares`.

**Step 3.3** — `QueenAttacks` = bishop | rook. Differential test against `QueenAttackedSquares`.

---

### Phase 4 — Aggregate attacks + check

**Step 4.1** — `BitboardPosition.attackedSquares(Side) -> long` (union of all piece attacks). Differential test against `AbstractAttackedSquares.calculateAttackedSquares`.

**Step 4.2** — `BitboardPosition.isInCheck(Side)`. Differential test against `Board.isCheck()` re-derived from `StaticPosition` (need a small test-side helper that takes `(StaticPosition, Side)` to compare cleanly).

**Step 4.3** — `BitboardPosition.attackersTo(Square, Side) -> long` (bitset of `Side`'s pieces attacking the square). No direct production counterpart, so the oracle is "enumerate own pieces, ask each whether its attack set contains the target square." Differential test against that derivation. Used in Phase 6 for pin detection.

---

### Phase 5 — Pseudo-legal moves

**Step 5.1** — Per-piece pseudo-legal generation returning `long` target sets, intersected with `~own`. Five small classes (`KnightMoves`, `BishopMoves`, `RookMoves`, `QueenMoves`, `KingMoves` — non-castling). Differential test: take each legal move from `AbstractLegalMoves`, strip pin/check filtering by re-running on `StaticPosition` without the king-safety filter — *or* simpler, write a small `StaticPositionPseudoLegalOracle` test helper that walks `StaticPosition` and lists pseudo-legal targets directly. The helper becomes the oracle.

**Step 5.2** — Pawn pushes (single + double + promotion). Bitboard form using shift + mask. Differential test against `PawnForwardNonPromotionLegalMoves` + `PawnForwardPromotionLegalMoves` re-derived on `StaticPosition` without king-safety.

**Step 5.3** — Pawn captures + en-passant. The en-passant target square is passed in (the bitboard layer is stateless about EP). Differential test against the corresponding pawn-capture legal-move classes.

---

### Phase 6 — Legal moves

**Step 6.1** — Legal king moves (filter pseudo-legal king targets by `attackedSquares(opposite)`). Differential test against `KingNonCastlingLegalMoves`.

**Step 6.2** — Pin detection: xray rook/bishop rays through the friendly king to find pinned own pieces and their pin-rays. Differential test against an oracle helper built from `StaticPosition` (for each own piece, hypothetically remove it and check whether the king becomes attacked along the same ray).

**Step 6.3** — Full legal-move generation: filter pseudo-legal moves by check evasion (when in check, restrict to king moves, block-the-ray, capture-the-checker) + pin filter. **Closes the loop**: differential test the bitboard `Set<LegalMove>` against `AbstractLegalMoves.calculate(Board)` for every fixture × every legal halfmove.

Castling is **not** included in `BitboardPosition.legalMoves` — castling rights live on `Board`. This stays a Board-layer concern. Document this explicitly in the bitboard package's `package-info.java`.

---

### Phase 7 — Immutable make-move

**Step 7.1** — `BitboardPosition.afterMove(MoveSpecification) -> BitboardPosition`. Handles regular moves, captures, promotions, en-passant capture, and the *piece movement* part of castling (rook + king both move). Differential test against `StaticPositionUtility.createPositionAfterMove`: for every fixture × every legal move, both representations agree on the resulting position.

This is the second spine assertion. Once green, the bitboard side is a faithful parallel implementation of the entire `StaticPosition` surface.

---

### Phase 8 — Zobrist hash

(From tasks.md — naturally lands here because the XOR sites coincide with `afterMove`.)

**Step 8.1** — Zobrist random tables (per piece × square; side-to-move; castling rights; en-passant file). Static, initialized once. Add `BitboardPosition.zobristPieces() -> long` (the piece-placement-only component — side/castling/EP are added by Board callers when they need a full key).

**Step 8.2** — Differential property test: equal `BitboardPosition`s → equal hash; deliberately mutated positions across the corpus → different hash (no collisions observed).

**Step 8.3** — Incremental Zobrist update inside `afterMove`. Differential test against full recomputation on the result.

This step is the only one in this release where bitboards earn perf today: `FindHelpmateExhaust` could swap its `DynamicPosition`-keyed map to a `long` Zobrist key. **But that's a Board-layer change** — defer to release 3 along with the lean analyzer board, or do it as a tiny separate commit at the end of this release if the change stays inside `FindHelpmateExhaust` and re-enables under existing tests.

---

### Phase 9 — Re-enable unwinnability tests

**Step 9.1** — Remove the `@Disabled` annotations from Phase 0. Suite runs full, on the existing `StaticPosition` path — no perf change yet, but back in CI.

**Step 9.2** — Note the current `findHelpMate` runtime as the baseline for release 3 (lean analyzer board). Capture in `tasks.md`.

**Step 9.3** — Update `tasks.md`: move the bitboard release to Done; surface the deferred work — magics, mutable make/unmake, Board integration, lean analyzer — into release 3's section.

---

### What I deliberately deferred

- **Magic bitboards** — perf optimization layered on top once the parallel-and-verified property is established.
- **Mutable make/unmake** — only useful for the lean analyzer board (release 3).
- **`Board` integration** — explicitly out of scope; this release is pure background.
- **Porting unwinnability analyzers to `BitboardPosition`** — tasks.md lists this in the bitboard release; it actually belongs to release 3, since the perf win comes from the lean analyzer board, not from the bitboard substrate alone.

---


## Current Bitboard release — general

The performance overhaul. Same library, faster — same answers verified bit-exact against the existing `StaticPosition` reference. Ships before Maven Central because the public-facing library needs acceptable performance: users expect engine-class speed, not reference-implementation-class speed. People reach for Carlos's `chesslib` over alternatives because it has bitboards.

**Governing rule for this release: see _Project invariant — the `StaticPosition` reference implementation is never lost_ at the top of this file.** This release is purely additive. No production hot path is switched to `BitboardPosition` here. No `StaticPosition`-based class is deleted, deprecated, or relocated here. The switchover and the relocation of `StaticPosition` into `src/test/` are a **separate later release**, gated on the differential-test harness being green across the full corpus.

### Approach — differential testing

The existing `StaticPosition` (square-array, slow-and-right) becomes the test oracle for a new `BitboardPosition` (bitboard, fast). Both representations live alongside; every test position runs through both and results must agree bit-exact. This is the classic differential-testing pattern (SQLite's TH3, LLVM's optimization-level cross-checks).

The architectural advantage clean-chess has: the two representations are independently derived from the FIDE rules, not from a common ancestor — so when they disagree, that's a real signal. Most chess engines added bitboards without a pre-existing reference; clean-chess's slow-and-right phase becomes the gift that pays back here.

After this release the `StaticPosition` path remains in `src/main/` and continues to be the production code path. It moves to `src/test/` only in the dedicated switchover release that follows, and only if the differential-test harness has stayed green throughout.

The action items for this release are expressed as commit-sized Steps in *Current Bitboard release — Implementation plan* above. The harness, not a perf number, is the deliverable of this release. The bitboard path being a verified parallel implementation is the contract that unlocks the switchover release.

### Explicitly NOT in this release (see Project invariant)

- Switching any production hot path in `Board` to consume `BitboardPosition`.
- Porting the unwinnability analyzers (`FindHelpMateInterrupt`, `FindHelpmateExhaust`, `UnwinnableQuickAnalyzer`, `UnwinnableFullAnalyzer`, `UnwinnableSemiStatic`, `Mobility`, `Score`, `GoingToCorner`) to `BitboardPosition`.
- Relocating `StaticPosition` or any of its consumers from `src/main/` to `src/test/`.
- Magic bitboards.
- Mutable make/unmake / lean analyzer board for tree search.

All of the above belong to the dedicated switchover release that follows, and only proceed once the differential-test harness has stayed green across the full corpus.

### Notes

- Auto-CHA per-move (in the DeepSquare release) uses `isUnwinnableQuick`, which is already cheap — no bitboard dependency there. The performance pain that motivates this overall arc is `findHelpMate` (full unwinnability search), but that perf win lands in the switchover release, not this one.
- The DeepSquare-release Zobrist task partially addressed `findHelpMate` performance without bitboards (FEN-string visited set → structured record key). The properly-bitboard-aware incremental Zobrist hash lands in this release; the actual swap of `findHelpmate`'s visited-position map to that key is part of the switchover release.

---

## Switchover release — `Board` consumes `BitboardPosition` end-to-end; per-move hot swap; drop cached `Board.staticPosition`

Per the Project invariant at the top of this file: this release moves the per-move data path entirely onto the bitboard and stops caching `StaticPosition` on `Board`. `Board.getStaticPosition()` is preserved as a public API but becomes a derived view computed on demand via `BitboardPositionUtility.toStaticPosition()`. **No physical relocation of the `StaticPosition` subtree happens in this release** — that is deferred (see *Future release — StaticPosition subtree relocation* below) because the audit at the start of the 10.0.0 work found ~25 src/main classes outside the relocation subtree still consume `StaticPosition` (including the public `Fen` record, `DynamicPosition`, the SAN classes, `FenParserAdvanced`, `ChessRuleAnalyzer`, `InsufficientMaterialUtility`, etc.). Porting all of those off `StaticPosition` is a multi-release effort and is not bundled into one switchover.

### Implementation plan

Commit-sized steps suitable for Codex review. The bitboard release (9.0.0, tip `65ac873d`) shipped a verified parallel implementation and switched the unwinnability hot paths and `Board.getLegalMoves()` / `Board.isCheck()` to consume `BitboardPosition`; what 9.0.0 left in place was the per-move `StaticPositionUtility.createPositionAfterMove` → `BitboardPositionUtility.fromStaticPosition` chain on `Board.move()`, plus the cached `staticPosition` field carried by every `DynamicPosition`. This release closes both. Each step lands one logical change, validated against the existing test surface plus the bitboard differential tests from the prior release.

#### Status

- ✅ **Step 1.1** — `915cf866` — `Board.getBitboardPosition()` returning a per-call computed `BitboardPosition` (no caching yet). Pure additive.
- ✅ **Step 1.2** — `bb85f09e` — bitboard cached as `bitboardPositionList` field on `Board`, maintained per `move()`/`unmove()`. O(1) `getBitboardPosition()` via `Nulls.getLast`.
- ✅ **Step 1.3** — `c752bd5e` — `Board`'s `isCheck` computation switches to `BitboardPosition.isInCheck`; drops the unused `AbstractAttackedSquares` / `Set` imports. Phase 1 complete.
- ✅ **Step 2.1** — `4c7cb4e9` — `BitboardLegalMoveFactory.toLegalMove` converts a bare `MoveSpecification` into a fully-typed `LegalMove` (movingPiece, capturedPiece, kind), differential-tested against `AbstractLegalMoves.calculateLegalMoves` (the StaticPosition-backed oracle) on every corpus fixture. The original Step 2.1 test compared against `board.getLegalMoves()`; that became self-referential once Step 2.2 switched `Board` to the bitboard pipeline, and the oracle was repointed at the `AbstractLegalMoves` surface in `ed4e20eb` (see below).
- ✅ **Step 2.2** — `a235d363` — `Board.getLegalMoves()` population switches to `BitboardLegalMoveFactory.calculateLegalMoves` (bitboard for non-castling + public bridge to `KingCastlingLegalMoves` for castling). Full suite ~46s vs. ~54s before — the bitboard pipeline is faster than the StaticPosition path it replaces.
- ✅ **Codex P2 fix** — `ed4e20eb` — `TestBitboardPositionLegalMoves` and `TestBitboardLegalMoveFactory` were silently turned self-referential by Step 2.2; both now use `AbstractLegalMoves.calculateLegalMoves` directly as the independent StaticPosition-backed oracle. Plus a Step 1.2 doc-mismatch fix.
- ✅ **Step 2.3a** — `732e356f` — bitboard variants added to `UnwinnabilityMaterial` (pure additive; differential-tested against the StaticPosition surface).
- ✅ **Step 2.3b** — `33e03a96` — `UnwinnableQuickAnalyzer`'s five private helpers (`calculateHasOnlyPawnsBishopsAndKings`, `calculateIsAlmostOnlyPawnsBishopsAndKings`, `calculateIsBlockedCandidate`, `calculateNumberOfBlockedPawns`, `calculateHasLonelyPawns`) now consume `BitboardPosition`; call sites route through `board.getBitboardPosition()`. Plus bitboard variant of `SemiOpenFilesUtility.calculateHasSemiOpenFile`. Drops `StaticPosition`, `Piece`, `Square` imports from `UnwinnableQuickAnalyzer`. Suite runtime drops to ~31s.
- ✅ **Step 2.4** — `692f96ce` — `Mobility.mobility` now consumes `board.getBitboardPosition()` (the only two StaticPosition queries: build the piecePlacementList from occupied squares + first-round empty-target check). The class is otherwise self-contained on its `PiecePlacement`/`MobilitySolution` data structures.
- ✅ **Step 2.5** — `9abc0926` — `Score.score`, `GoingToCorner.goingToCorner`, and `FindHelpmateExhaust.calculateIsNeedLoserPromotion` (+ its two helpers) all swap from `StaticPosition` to `BitboardPosition`. The `findHelpmate` recursion call site extracts `board.getBitboardPosition()` once and reuses it for the KingOnly / HasNoPawns / needLoserPromotion guards, the `Score.score` call, and the `calculateHasQueen` check. `UnwinnableSemiStatic` did not need porting — it consumes only `Board`'s public surface, which has been bitboard-backed since Phase 1.
- ✅ **Step 2.6** — `b3ed0edf` — `FindHelpmateExhaust`'s last StaticPosition queries gone. `calculateIsEraseEnPassantCaptureTargetSquare` uses an inline bitboard adjacency check (kept inline because `EnPassantCaptureUtility` lives in the doomed-to-relocate `com.dlb.chess.moves` subtree). `calculateIsUnwinnableAccordingLemma5/6` ported to `BitboardPosition` with a TODO marker — they're declared but not yet wired into the analyzer flow; wiring them in is a follow-on.
- ✅ **Step 2.7** — `FindHelpMateInterrupt` has no `StaticPosition` use; `UnwinnableFullAnalyzer` has only the `Fen` pass-through. Phase 2 closed on the analyzer side.

#### 9.0.0 boundary

The 9.0.0 (Bitboard) release ended at the Phase 2 boundary above. Production hot paths consume `BitboardPosition`; the `StaticPosition` reference layer remains in `src/main/` per the Project Invariant. The lean-analyzer / magics / relocation / perf phases that were sketched as "Phases 3-7" in the 9.0.0 plan have been split into two dedicated future releases (see *Lean bitboard winnability release* and *Future release — StaticPosition subtree relocation* below).

Phase 3 work-in-progress (`a524c9b8` LeanBoard, `befe521b` castling fix + stronger tests, `4b63738e` ZobristKeys side/castling/EP keys + `LeanBoard.zobristKey` + `legalMoves -> LegalMove`) was reverted from the 9.0.0 release at `722e481` and preserved on `origin/feature/lean-bitboard-helpmate-wip` as the starting point for the Lean bitboard winnability release.

#### 10.0.0 scope — steps

Five commit-sized steps, each leaving the suite green:

- ✅ **Step 0** — `7dde3b61` — Restructure `tasks.md`: 10.0.0 scope reframed to scope B; lean-analyzer / magics / perf phases moved into the dedicated *Lean bitboard winnability release* section; relocation captured in the dedicated *Future release — StaticPosition subtree relocation* section with audit findings.
- ✅ **Step 1** — `cb136e49` — Per-move incremental bitboard hot swap. `Board.performMoveWithoutValidation` replaces `BitboardPositionUtility.fromStaticPosition(afterStaticPosition)` with `Nulls.getLast(bitboardPositionList).afterMove(moveSpecification, havingMove)`. Suite 1132 / 0 / 0 / 4.
- ✅ **Step 2** — `15659571` — `BitboardPosition` moves onto `DynamicPosition`; `Board.bitboardPositionList` field dropped. `DynamicPosition` shape: `(Side, StaticPosition, BitboardPosition, Square, CastlingRight, CastlingRight)`. `Board.getBitboardPosition()` now reads off the last `DynamicPosition`. Suite 1132 / 0 / 0 / 4.
- ✅ **Step 3** — `e7fd7815` — `DynamicPosition.staticPosition` dropped; `DynamicPosition` shrinks to `(Side, BitboardPosition, Square, CastlingRight, CastlingRight)`. `Board.getStaticPosition()` / `getStaticPositionBeforeLastMove()` derived via `BitboardPositionUtility.toStaticPosition()`. `RepetitionUtility.equals` switches to `bitboardPosition().equals(...)`. Public API surface preserved. Suite 1132 / 0 / 0 / 4.
- ✅ **Step 4** — `fdff3f5a` — Per-move `StaticPosition` computation gone from `Board`. `CastlingUtility` / `KingCastlingLegalMoves` / `AbstractLegalMoves` gain `BitboardPosition`-shaped overloads. `BitboardLegalMoveFactory.calculateLegalMoves` drops its `StaticPosition` parameter. `Board.calculateIsEnPassantCapturePossible` switches to `BitboardPosition` and uses `afterMove(...).isInCheck(...)` for king-safety. Private `Board.calculateLegalMove` helper replaced by `BitboardLegalMoveFactory.toLegalMove`. Suite 1132 / 0 / 0 / 4.
- ✅ **Step 5** — Version bump 9.0.0 → 10.0.0 (`pom.xml`, `README.md` ×2), `[10.0.0]` `CHANGELOG.md` entry above `[9.0.0]`, `tasks.md` updated.

#### Cross-cutting decisions (settled upfront)

- **One hot path at a time.** Each step leaves the suite green; no big-bang cutover. The 10.0.0 scope is bitboard-on-`Board`-data-path only — public API and the reference-layer relocation are not bundled in.
- **`Board.getStaticPosition()` survives as a derived view.** `StaticPosition` itself stays as a public type in `src/main/` for this release. External callers consuming it (FEN parser, SAN classes, etc.) keep working unchanged — they go through the derived view. The physical relocation is the separate future release.
- **The bitboard-release test bridges stay.** `SlidingAttacksTestOracle` and `LegalMovesTestOracle` under `src/test/java/com.dlb.chess.squares` / `com.dlb.chess.moves` become permanent — they outlive the bitboard release as long as those reference classes exist.

---

## ✅ Helpmate analyzer board release (12.0.0) — `HelpmateSearchBoard` replaces `LeanBoard`

The motivation is the `findHelpMate` cost in `UnwinnableFullAnalyzer` / `UnwinnableFullAnalyzer` / `FindHelpMateInterrupt`. Even with bitboards everywhere on the per-move path (10.0.0), the helpmate tree search still pays for full `Board` construction at every recursion node — SAN/LAN lists, full history, repetition counts, halfmove-clock list, etc. — none of which the search uses.

### Trajectory

- **First attempt — `LeanBoard` (fast_bitboard_for_tree_search).** Resumed from `origin/feature/lean-bitboard-helpmate-wip` (tip `ba4958fc`). Added a `LeanBoard` carrying `BitboardPosition` + side + EP + castling + halfmove, public class in `com.dlb.chess.bitboard`, transposition cache keyed on a 64-bit `LeanBoard.zobristKey()` composed from `BitboardPosition.zobristPieces()` + side/castling/EP keys added to `ZobristKeys`. Codex review flagged: (P2) public `LeanBoard` + new public `ZobristKeys` helpers leak the speedboard into public API, (P2) `LeanBoard.zobristKey()` not directly tested, (P3) stale "phantom EP NOT normalized" comment after the EP-normalization fix.

- **Final design — `HelpmateSearchBoard` (12.0.0).** Replaces `LeanBoard` with a package-private analyzer board in `com.dlb.chess.unwinnability`. Stores a `DynamicPosition` directly (already EP-normalized + king-safety-aware) — no separate Zobrist composition, no new public surface, no collision risk. Caches `legalMoves` / `isCheck` / `isCheckmate` / `isStalemate` per ply on `refreshDerivedState()` so the recursion doesn't recompute on every read. `TestHelpmateSearchBoard` differential-tests against `Board` across a recursive tree on representative castling / EP / pawn-promotion / queen-mate / king-corner FENs.

### What shipped

- `HelpmateSearchBoard` (package-private, `com.dlb.chess.unwinnability`).
- `TestHelpmateSearchBoard` recursive differential test.
- `FindHelpmateExhaust` + `FindHelpMateInterrupt` rewritten to consume `HelpmateSearchBoard` internally; public `(Board, …)` entry points unchanged.
- Transposition cache: `HashMap<DynamicPosition, Integer>`.
- `UnwinnabilityMaterialBitboard.calculateIsInsufficientMaterial` added (consumed by `HelpmateSearchBoard.isInsufficientMaterial`).
- `LeanBoard` + `TestLeanBoard` deleted. `ZobristKeys` reverted to its pre-WIP shape (only the piece-square helper).
- `TestUnwinnableFullForLichessGamesHavingHelpMate.calculateCorrespondingLichessGame` fixed to also strip the leading `test_` prefix (pre-existing fixture-name bug for `test_lichess_V7eJ1RR9_helpmate.pgn` ↔ `lichess_V7eJ1RR9.pgn`).

### Deferred — conditional follow-ons (not in 12.0.0)

- **Mutable make/unmake.** Only if profiling shows immutable `afterMove` allocations dominate the tree-search hot path. Add mutable variant on `HelpmateSearchBoard` (NOT on `BitboardPosition` — preserves the record's immutability).
- **Magic bitboards.** Only if profiling shows classical ray loops dominate. Drop in behind the existing `(int squareOrdinal, long occupied) -> long` API in `BishopAttacks` / `RookAttacks` — no caller changes.
- **Performance baseline.** Measure `findHelpMate` runtime against representative unwinnability fixtures. Target: within 5× of `chesslib` on the same fixtures.

---

## Helpmate hot-path release (12.1.0) — mutable `HelpmateSearchBoard`, per-ply move buffers, exact transposition key

12.0.0 made `findHelpMate` cheaper per node by collapsing the wrapper down to `HelpmateSearchBoard`, but every search move still allocates: a fresh `BitboardPosition` from `afterMove(...)`, a new `DynamicPosition`, an `ImmutableList<LegalMove>`, a `TreeSet`/`MoveSpecification`/`LegalMove` graph, and sometimes an extra `afterMove()` for EP normalization. After 12.0.0's `MoveGenerationPerformanceSurvey` baseline the production bitboard path sits at ~3.5–4× `chesslib`. This release closes the allocation gap inside `com.dlb.chess.unwinnability` only. Public `BitboardPosition` remains an immutable record; `StaticPosition` and the differential-test oracle layer are not touched.

### Goals

- `HelpmateSearchBoard` makes and unmakes moves in place — no per-move `BitboardPosition` / `DynamicPosition` / move-list allocation along the tree-search hot path.
- `getLegalMoves()` on `HelpmateSearchBoard` returns a per-ply reusable buffer with **byte-for-byte / order-equivalent** contents vs. today — the mate-line output and search cutoffs are observable behavior pinned by tests.
- Transposition cache keyed by an **exact** package-private structural key over the mutable board fields. No Zobrist as a correctness-bearing key in this release.
- `MoveGenerationPerformanceSurvey` ratio target: within ~1.5–2× of `chesslib` on the production bitboard path.

### Non-goals

- Touching `BitboardPosition` mutability, `StaticPosition`, `AbstractLegalMoves`, or any class on the differential-test oracle side.
- New public API. Every change in this release lives in `com.dlb.chess.unwinnability` and is package-private.
- Magic bitboards as a first move. Magics are profile-gated to Phase E and only land if Phases B–D leave the ratio outside target.
- Probabilistic / Zobrist-keyed transposition tables as the first-correctness move. (Zobrist may return later, behind equality verification or explicit collision handling.)

### Phase boundaries

- **Phase A — differential-test scaffolding for `HelpmateSearchBoard`.** Lock-step `HelpmateSearchBoard` ↔ `Board` parity across recursive trees. Default tree depth 3; depth 4 only for deliberately tiny / forced positions so failure traces stay reviewable. At every node both representations must agree on: 12 piece bitboards, side to move, raw EP, normalized EP, castling rights, cached check flags (`isCheck` / `isCheckmate` / `isStalemate`), and `getLegalMoves()` iteration order. Fixtures **enumerated in the test or test helper before any implementation** — failures must be reproducible by fixture name, not by "whatever the corpus happened to surface." Required categories: castling rights, legal EP, pinned/illegal EP normalization (the case the EP-normalization extra-`afterMove` exists for), promotion, check/evasion, stalemate terminal, checkmate terminal, plus at least one helpmate fixture where move-iteration order is load-bearing for the mate line. Plus quick + full helpmate fixture-regression on the existing analyzer outputs (mate-line equality, not just verdict equality). This phase is the behavioral oracle the rest of the release rides on.

- **Phase B — mutable `HelpmateSearchBoard` + explicit undo stack.** `HelpmateSearchBoard` owns mutable 12 piece bitboards, side to move, raw EP, normalized EP, castling rights, cached derived flags. `make(move)` mutates in place; `unmake()` pops an undo record (per-ply deltas: flipped bits, captured piece, castling-rights/EP/halfmove deltas, prior cached-flag values). No per-move `BitboardPosition.afterMove(...)` allocation, no per-move `DynamicPosition` allocation, no extra `afterMove()` for EP normalization. Phase A's full differential-test set must remain green. **Additional gate: a dedicated `make → unmake` round-trip test** asserts every observable field is byte-identical to its pre-`make` value — raw EP, normalized EP, castling rights, cached check / checkmate / stalemate flags, legal-move buffer contents and count, transposition-key material. This gate has to be green before any caller switches to the mutable path.

- **Phase C — per-ply reusable `MoveBuffer`.** Replace `ImmutableList<LegalMove>` per-ply allocation with a per-depth reusable buffer. **One buffer per depth, NOT one shared global** — parent buffers must survive child recursion. Iteration order must remain byte-for-byte equivalent to the current `TreeSet`-derived order; if a `TreeSet` replacement is part of the win, the equivalence is asserted by Phase A tests, not assumed. Treat the returned buffer as read-only at callsites.

- **Phase D — exact structural transposition key.** Replace `HashMap<DynamicPosition, Integer>` with a package-private exact structural key over the mutable board fields, or with a custom exact table. Equality semantics match today's `DynamicPosition.equals`. Do not use public `ZobristKeys` helpers as the correctness-bearing key — Zobrist becomes a re-evaluation candidate only after this release ships and only behind explicit collision handling or equality verification.

- **Phase E (deferred, profile-gated) — magic bitboards.** Only if the post-D `MoveGenerationPerformanceSurvey` ratio is still outside the ~1.5–2× target and profiling identifies sliders as the remaining cost. Drop in behind the existing `BishopAttacks.attacks(int, long)` / `RookAttacks.attacks(int, long)` API — no caller changes. Magics do not touch the larger allocation paths in `BitboardLegalMoveFactory.java:94` or `BitboardPosition.legalMoves`, which is exactly why this is last.

- **Phase F — re-measure, version bump, CHANGELOG, gates.** Re-run `MoveGenerationPerformanceSurvey` and record the new ratios in `CHANGELOG.md`. Version bump to `12.1.0` (or `13.0.0` only if a breaking change has actually surfaced — none is expected; all changes are internal to `com.dlb.chess.unwinnability`). Update `pom.xml`, both `README.md` copies, and the `CHANGELOG.md` entry. Mark this release done in `tasks.md`.

### Gates (all three green before tagging)

- `mvn test` (smoke).
- `mvn javadoc:javadoc`.
- `mvn test -Pfull -Dtest.excludes=` (full corpus, including the now-capped `TestAmbronaSemiStaticOracleComparison`).

Plus the per-phase behavioral gates: Phase A's differential and fixture-regression suites must stay green from B onward; Phase B's `make → unmake` round-trip gate must be green before any caller is switched onto the mutable path.

---

## Role-inversion release — `StaticPosition` subtree moves to `src/test/`

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

## Next release — drop auto-CHA-per-move; dead-position queries become request-based

The construction we have today is too complicated and does work the library doesn't need. Today every `Board.move()` (and every `Board` constructor) runs the unwinnability quick analyzer on the new position and caches the verdict in `isDeadPositionUnwinnableQuickList`. The cached value drives `Board.isDeadPositionUnwinnableQuick()`, feeds `Board.isDeadPosition()` (alongside the cheap mechanical `isInsufficientMaterial`), and through `ValidateNewMove` causes the move pipeline to throw `MoveCheck.GAME_ALREADY_ENDED` with `GameStatus.DEAD_POSITION_UNWINNABLE_QUICK` if a consumer tries to play on. The whole apparatus exists to model FIDE 5.2.2 "dead position" as an automatic termination.

**The premise the auto-termination relies on is false in practice.** Playing on in a dead position is harmless: no win is reachable, the position can only resolve to a draw. In a real game the practical outcomes are all draws — flagfall in a dead position is a draw by adjudication, resignation in a dead position is a draw under current FIDE rules (no win available to the opponent), and the players can always agree to a draw. Nothing changes if they keep moving. So the library does not need to *enforce* dead-position termination at the move-pipeline boundary; it only needs to make it *queryable* so consumers that want to surface it can do so.

The current eager-per-move construction also costs more than just complexity: it makes every move pay the analyzer cost (mitigated but not erased by the bitboard work), it adds a constructor flag (`detectDeadPositionUnwinnable`) that has to be threaded through every `Board` overload, it requires a recursion-suppression guard inside the analyzer when it builds throwaway sub-boards, and it leaks into the test corpus — fixtures get relocated to `pgnParser/legacy/common/beyond/` whenever they happen to step into a dead position even though the recorded games are otherwise well-formed PGN. The fix simplifies all of that.

### Phase 1 — Drop auto-detection from `Board`

**Step 1.1** — Remove the per-ply cache and computation. Drop `Board.isDeadPositionUnwinnableQuickList`, `Board.computeDeadPositionUnwinnableQuick()`, the `isDetectDeadPositionUnwinnable` field, every `Board` constructor overload that took the `detectDeadPositionUnwinnable` flag (including `copyCurrentPositionWithoutHistory(boolean)`), and the eager calls from `Board` constructors and `performMoveWithoutValidation`. `Board.isDeadPositionUnwinnableQuick()` as a stateful accessor is removed.

**Step 1.2** — Reshape `Board.isDeadPosition()` and the `isDeadPosition*` family as pure on-demand queries that run the analyzer when called. `isDeadPositionQuick()` and `isDeadPositionFull()` are already that today (`Board` lines ~951 / ~999); the change is that `isDeadPosition()` joins them — it computes `isInsufficientMaterial() || isDeadPositionQuick().isDeadPosition()` at the call site instead of reading a cached field. Consumers that want the old "checked after every move" behavior call the query themselves in their move loop.

**Step 1.3** — `ValidateNewMove` no longer rejects moves on `DEAD_POSITION_UNWINNABLE_QUICK`. Remove the corresponding check from the move-acceptance precondition (the auto-terminators that remain: checkmate, stalemate, insufficient material, plus fivefold and 75-move if Phase 2 below is not adopted). `MoveCheck.GAME_ALREADY_ENDED` continues to fire for the auto-terminators that survive.

**Step 1.4** — Decide the `GameStatus.DEAD_POSITION_UNWINNABLE_QUICK` enum value's fate. Two options: keep it (returned by an explicit "what status is this position in" query, never thrown by the move pipeline) or drop it (the analyzer's `UnwinnabilityQuickVerdict` is the only place that concept lives). Recommend keep, narrowed in scope — `Board.calculateGameStatus()` or equivalent maps the position to a status that can include `DEAD_POSITION_UNWINNABLE_QUICK`, but the move pipeline never throws it.

### Phase 2 — Tentative: drop auto-fivefold and auto-75-move termination

**Not committed yet — captured for discussion.** The same line of reasoning applies to FIDE 9.6.1 (fivefold) and 9.6.2 (75-move): both are automatic terminations under the current model, but playing on past either is harmless (same outcome arguments as dead position). Dropping the auto-termination would unlock tests the corpus cannot currently express — sixfold, sevenfold, longer-than-75-move sequences after the 75th — that today either fail with `GAME_ALREADY_ENDED` or have to live as legacy fixtures.

The trade-off the user flagged: if these stop being automatic terminators, consumers that *do* want to surface them have to query the predicates themselves (`isFivefoldRepetition()`, `isSeventyFiveMove()`). The signal is "presented twice" in the sense that the consumer first sees the rule fire and then has to keep asking on subsequent moves whether the game has progressed past it. That is a real ergonomics cost for the typical consumer.

If adopted, the work mirrors Phase 1: remove the corresponding checks from `ValidateNewMove`, leave `isFivefoldRepetition()` / `isSeventyFiveMove()` as queryable predicates. The 75-move-rule fixtures currently parked in `pgnParser/legacy/common/beyond/` (~30 files) move back into the regular corpus.

**To decide before any code lands.** Listed here as a follow-on so the question stays visible alongside Phase 1, not because it's settled.

### Phase 3 — Corpus and test cleanup

**Step 3.1** — Legacy fixtures that exist only because their game played past a dead position move back into the regular corpus. The relocation under `pgnParser/legacy/common/beyond/` was specifically to keep `TestSetupPgnCorpusNotPlaysBeyondAudit` green; if dead position is no longer an auto-terminator, the audit no longer flags them. Identifying the affected files comes out of grepping the legacy tree for fixtures whose recorded termination is `DEAD_POSITION_*`.

**Step 3.2** — `TestLegacyPgnParsePlaysBeyondAudit`'s `EXPECTED` map and the expected-count constant update accordingly. Any test that asserts on `GameStatus.DEAD_POSITION_UNWINNABLE_QUICK` being thrown from the move pipeline switches to asserting on the queryable predicate.

**Step 3.3** — `CHANGELOG.md`, `README.md` "Notable features" if the auto-detection is called out there, and `specification.md` §3.1 ("FIDE rule fidelity and game termination") get the table updated: dead position moves from "automatic" to "queryable" (and same for fivefold / 75-move if Phase 2 lands).

### Phase 4 — Release artifacts

**Step 4.1** — Version bump (TBD; this is binary-incompatible if the `Board` constructor signatures with the `detectDeadPositionUnwinnable` flag are removed). `CHANGELOG.md` entry above the current release. `tasks.md` section marked done.

### Why before the python-chess release

Reactivating python-chess as the primary cross-validation reference means matching what python-chess actually does. python-chess does not auto-terminate on a dead position; it exposes the query and lets the caller decide. Aligning the library's termination model with python-chess's first makes the cross-validation pass cleaner (fewer "we say game-ended, python-chess says still-playable" disagreements that have to be papered over in the bridge).

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
