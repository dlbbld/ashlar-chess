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

## Switchover release — production hot paths use `BitboardPosition`; `StaticPosition` relocates to `src/test/`

This release **only proceeds if the differential-test harness from the bitboard release has stayed green across the full corpus.** Per the Project invariant at the top of this file: this release switches the production path, and at the same time moves the `StaticPosition` subtree from `src/main/` to `src/test/` — relocated, not deleted, and remains as the permanent differential-test oracle from that point on.

### Implementation plan

Commit-sized steps suitable for Codex review. The bitboard release (commit `246a66ae`) shipped a verified parallel implementation; this release flips production. Each step lands one logical change, validated against the existing test surface plus the bitboard differential tests from the prior release.

#### Status

- ✅ **Step 1.1** — `915cf866` — `Board.getBitboardPosition()` returning a per-call computed `BitboardPosition` (no caching yet). Pure additive.
- ✅ **Step 1.2** — `bb85f09e` — bitboard cached as `bitboardPositionList` field on `Board`, maintained per `move()`/`unmove()`. O(1) `getBitboardPosition()` via `Nulls.getLast`.
- ✅ **Step 1.3** — `c752bd5e` — `Board`'s `isCheck` computation switches to `BitboardPosition.isInCheck`; drops the unused `AbstractAttackedSquares` / `Set` imports. Phase 1 complete.
- ✅ **Step 2.1** — `4c7cb4e9` — `BitboardLegalMoveFactory.toLegalMove` converts a bare `MoveSpecification` into a fully-typed `LegalMove` (movingPiece, capturedPiece, kind), differential-tested against `board.getLegalMoves()` on every corpus fixture.
- ✅ **Step 2.2** — `a235d363` — `Board.getLegalMoves()` population switches to `BitboardLegalMoveFactory.calculateLegalMoves` (bitboard for non-castling + public bridge to `KingCastlingLegalMoves` for castling). Full suite ~46s vs. ~54s before — the bitboard pipeline is faster than the StaticPosition path it replaces.
- ✅ **Codex P2 fix** — `ed4e20eb` — `TestBitboardPositionLegalMoves` and `TestBitboardLegalMoveFactory` were silently turned self-referential by Step 2.2; both now use `AbstractLegalMoves.calculateLegalMoves` directly as the independent StaticPosition-backed oracle. Plus a Step 1.2 doc-mismatch fix.
- ✅ **Step 2.3a** — `732e356f` — bitboard variants added to `UnwinnabilityMaterial` (pure additive; differential-tested against the StaticPosition surface).
- ✅ **Step 2.3b** — `33e03a96` — `UnwinnableQuickAnalyzer`'s five private helpers (`calculateHasOnlyPawnsBishopsAndKings`, `calculateIsAlmostOnlyPawnsBishopsAndKings`, `calculateIsBlockedCandidate`, `calculateNumberOfBlockedPawns`, `calculateHasLonelyPawns`) now consume `BitboardPosition`; call sites route through `board.getBitboardPosition()`. Plus bitboard variant of `SemiOpenFilesUtility.calculateHasSemiOpenFile`. Drops `StaticPosition`, `Piece`, `Square` imports from `UnwinnableQuickAnalyzer`. Suite runtime drops to ~31s.
- ⬜ **Step 2.4** — current — port `Mobility` to consume `BitboardPosition` (next analyzer leaf used by `UnwinnableQuickAnalyzer.unwinnableQuick`)
- ⬜ Steps 2.5 → 7.x — pending

#### Note on the original Step 1.4

The original plan had a Step 1.4 — switch `Board.getLegalMoves()` to compose `bitboardPosition.legalMoves(...)` with castling. In practice this needs a `MoveSpecification → LegalMove` converter (because `getLegalMoves()` returns `LegalMove` records with `LegalMoveKind` + captured-piece info, while the bitboard returns bare `MoveSpecification`s) plus a castling generator. The converter is also needed by every unwinnability analyzer in Phase 2 — so it lives in Phase 2 as the lead-in, and the legal-move switch on `Board` lands once the converter exists.

#### Cross-cutting decisions (settled upfront)

- **One hot path at a time.** Production callers switch over individually, with the existing test suite green after each step. No "big bang" cutover.
- **`StaticPosition` computation stays on `Board` through Phases 1-5.** Only Phase 6 removes it. Until then `BitboardPosition` rides alongside as the primary, `StaticPosition` as a fallback for any callers not yet ported.
- **Magic bitboards are profile-gated.** Only land if classical ray loops show as the actual bottleneck after the lean analyzer board is in place.
- **Mutable make/unmake is conditional.** Only land if the lean analyzer board's tree search actually demands it; immutable `afterMove` might be sufficient.
- **The bitboard-release test bridges stay.** `SlidingAttacksTestOracle` and `LegalMovesTestOracle` under `src/test/java/com.dlb.chess.squares` / `com.dlb.chess.moves` become permanent — they outlive the bitboard release as long as those reference classes exist.

---

### Phase 1 — `Board` carries `BitboardPosition`; primary queries switch

**Step 1.1** — Add `Board.getBitboardPosition()` returning a fresh `BitboardPositionUtility.fromStaticPosition(getStaticPosition())` per call. No field caching yet. Test: returned bitboard agrees with the existing `StaticPosition` on every fixture in the corpus.

**Step 1.2** — Cache the bitboard as a field on `Board`: a `List<BitboardPosition>` parallel to `dynamicPositionList`, appended on every `move()` (via `BitboardPositionUtility.fromStaticPosition(afterStaticPosition)`) and popped on every `unmove()`. `getBitboardPosition()` becomes O(1) via `Nulls.getLast`. Incremental computation via `BitboardPosition.afterMove` is a follow-on if profiling shows the per-move recomputation is hot. Test: corpus final positions agree with `fromStaticPosition`, plus a hand-played five-move sequence (e4, e5, Nf3, Nc6, Bb5) followed by full `unmove()` walk, asserting the cache is bit-exact at every intermediate state.

**Step 1.3** — Switch `Board.isCheck()` to consume `bitboardPosition.isInCheck(side)`. Test: existing Board check tests + bitboard `TestBitboardPositionIsInCheck`. Phase 1 complete after this step.

---

### Phase 2 — `MoveSpecification → LegalMove` converter, Board's legal-move switch, port unwinnability analyzers

**Step 2.1** — Build a `MoveSpecification → LegalMove` converter on the bitboard side. Given a `BitboardPosition` and a non-castling `MoveSpecification`, it determines the moving piece, captured piece (regular or EP), and the `LegalMoveKind` (NORMAL / EN_PASSANT_CAPTURE / PAWN_TWO_SQUARE_ADVANCE / PROMOTION). Castling moves get a separate, simpler converter that just emits the existing castling shape.

**Step 2.2** — Switch `Board.getLegalMoves()` population (the call that builds `legalMoveListPerPly` in the constructor and `move()`) to compose `bitboardPosition.legalMoves(...)` with castling-MoveSpecs from the existing castling path, plus the Step 2.1 converter to produce `LegalMove` records.

**Step 2.3** — Port `UnwinnableQuickAnalyzer` to consume `BitboardPosition`. Reuses Phase 4-5 bitboard primitives. Test: existing CHA-quick tests.

**Step 2.4** — Port `UnwinnableSemiStatic` + supporting analysis classes (`Mobility`, `SemiOpenFilesUtility`, etc.).

**Step 2.5** — Port `Score`, `GoingToCorner`, and the per-side helpmate-eval helpers.

**Step 2.6** — Port `UnwinnableFullAnalyzer` and `FindHelpMateInterrupt`. `FindHelpmateExhaust` deferred to Phase 3 where it merges with the lean board.

---

### Phase 3 — Lean analyzer board for `FindHelpmateExhaust`

**Step 3.1** — Design + new lightweight position class. Minimal state: `BitboardPosition` + side-to-move + en-passant target + castling rights + halfmove clock. No SAN/LAN lists, no disambiguation, no full history. Keyed on `BitboardPosition.zobristPieces()` + side/castling/EP contributions for transposition.

**Step 3.2** — `FindHelpmateExhaust` consumes the lean board. Helpmate transposition map switches from `DynamicPosition` to `long` Zobrist key.

**Step 3.3** — Baseline `findHelpMate` runtime on representative unwinnability fixtures. Record in tasks.md.

---

### Phase 4 — Mutable make/unmake (conditional)

**Step 4.1** — Only if Phase 3's tree-search profile shows immutable `afterMove` allocations are a bottleneck. Add mutable variant on the lean analyzer board (NOT on `BitboardPosition` itself — preserves the record's immutability).

---

### Phase 5 — Magic bitboards (conditional)

**Step 5.1** — Only if profiling after Phases 3-4 shows classical ray loops are the bottleneck on the now-hot bitboard path. Drop in magic bitboards behind the existing `(int squareOrdinal, long occupied) -> long` API in `BishopAttacks` / `RookAttacks` — no caller changes needed.

---

### Phase 6 — Relocate `StaticPosition` subtree to `src/test/`

**Step 6.1** — Audit: confirm no `src/main/` class still references `StaticPosition`, `StaticPositionUtility`, the `com.dlb.chess.squares.*` consumers, or the `com.dlb.chess.moves.*` consumers. Any holdouts get ported or fixed.

**Step 6.2** — Drop `Board`'s `staticPosition` field. `getStaticPosition()` becomes a derived view computed from the bitboard via `BitboardPositionUtility.toStaticPosition()` — and stays as a public API for callers that want the human-readable mailbox form.

**Step 6.3** — Move the subtree from `src/main/java/` to `src/test/java/`: `StaticPosition`, `StaticPositionUtility`, the `com.dlb.chess.squares.*` family, the `com.dlb.chess.moves.*` family. Test: full suite still green (differential tests now drive the relocated oracle against the bitboard).

**Step 6.4** — Formalize the permanent differential-test layer in tasks.md / specification.md: every primitive on `BitboardPosition` is asserted against the relocated `StaticPosition` oracle for every fixture in the corpus, for every supported release going forward. Project policy from this point on.

---

### Phase 7 — Performance baseline + release notes

**Step 7.1** — Measure `findHelpMate` runtime against the representative unwinnability fixtures. Target: within 5× of `chesslib` on the same fixtures.

**Step 7.2** — Update `tasks.md` (move switchover to Done), `README.md` (note bitboard backend), and `specification.md` (architecture section now describes the bitboard primary / StaticPosition oracle split).

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
