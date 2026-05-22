# Changelog

Releases from 3.3 onward. Earlier history is in git tags only.

## [Unreleased]

## [12.0.0] - 2026-05-22

The **Helpmate analyzer board release**. `FindHelpmateExhaust` and `FindHelpMateInterrupt` no longer recurse on the full `Board` at every node — SAN/LAN lists, full move history, repetition counts, halfmove-clock list, etc., are all cost the tree search never used. A new package-private `HelpmateSearchBoard` (`com.dlb.chess.unwinnability`) carries only what the search needs: a `DynamicPosition` plus the raw EP target plus cached derived state (legal moves, isCheck, isCheckmate, isStalemate). One `HelpmateSearchBoard.from(Board)` at entry; the recursion runs on it.

### Notable

- **Helpmate search uses `HelpmateSearchBoard` on every recursion node.** Package-private in `com.dlb.chess.unwinnability`. No public API addition. Built once from the caller's `Board`; the caller's `Board` is never mutated by the search.
- **Transposition cache keys on `DynamicPosition`.** Already EP-normalized and king-safety-aware — no Zobrist composition needed in the search layer. The map is `HashMap<DynamicPosition, Integer>`.
- **`HelpmateSearchBoard` caches derived state per ply.** `legalMoves`, `isCheck`, `isCheckmate`, `isStalemate` are computed once via `refreshDerivedState()` on `move()` / construction; restored from a per-ply snapshot on `unmove()`.
- **`TestHelpmateSearchBoard`** asserts state parity with `Board` (DynamicPosition, bitboardPosition, side, EP, castling, legalMoves, isCheck, isCheckmate, isStalemate, both-side insufficient-material) across a recursive search tree on representative castling / EP / pawn-promotion / queen-mate / king-corner FENs at depths 0–2.
- **`UnwinnabilityMaterialBitboard.calculateIsInsufficientMaterial`** added — a single side / opposite-side material query that `HelpmateSearchBoard.isInsufficientMaterial` consumes, mirroring the rules in `InsufficientMaterialUtility` on the bitboard layer.
- **New test classes for the Lichess "not adjudicated correctly" corpus.** `TestUnwinnableFullForLichessGamesNotAdjudicatedCorrectly` and `TestUnwinnableQuickForLichessGamesNotAdjudicatedCorrectly` assert UNWINNABLE for the non-flagging side on real Lichess games that Lichess mis-adjudicated as wins on time despite the position being a forced draw. Regression-set coverage anchored on the FIDE-rule expectation, not on another analyzer.
- **Lichess unwinnable corpus reorganization.** The `cha/lichess/quick/notDepthThree` folder is renamed to `cha/lichess/quick/depthAboveFour`; the `CHA_LICHESS_QUICK_NOT_DEPTH_THREE` enum and its `_HELPMATE` companion are renamed to `CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR(_HELPMATE)`. The `CHA_LICHESS_NOT_QUICK` enum (single fixture) is folded into `DEPTH_ABOVE_FOUR` and dropped. The single `test_lichess_V7eJ1RR9_helpmate.pgn` fixture is renamed to `lichess_V7eJ1RR9_helpmate.pgn` to match the convention used by all other helpmate fixtures.
- **`TestUnwinnableFullForLichessGamesHavingHelpMate` split into two `@Test` methods.** `verdictsAreWinnable` asserts the analyzer's verdict; `mateLinesActuallyCheckmate` asserts the returned mate line, played out, delivers checkmate. A regression in either now reports separately.
- **Quick unwinnability analysis now exposes mate lines.** `UnwinnableQuickAnalyzer.unwinnableQuick(...)` returns `UnwinnabilityQuickAnalysis`, mirroring the full analyzer's `UnwinnabilityFullAnalysis`: callers get the quick verdict plus the helpmate line when the verdict is `WINNABLE`. The `Board.isUnwinnableQuick(Side)` convenience method still returns only `UnwinnabilityQuickVerdict`.

### Internal

- **`FindHelpmateExhaust.calculateHelpmate(Board, int)`** keeps its public signature; internally constructs `HelpmateSearchBoard.from(board)` and delegates to a private overload. The board-was-changed invariant check now compares `DynamicPosition` / EP target (cheap) instead of re-serializing FEN.
- **`FindHelpMateInterrupt.calculateHelpmate(Board, Side)`** follows the same pattern: public Board entry, private `HelpmateSearchBoard` recursion.
- **`calculateStockfishFen` debug helper** rebuilt to derive FEN from `HelpmateSearchBoard` directly (`BitboardPositionUtility.calculatePiecePlacement`, manual castling-rights assembly, EP normalization). Still gated on `IS_DEBUG = false`.
- **`CheckAgainstChaFull` removed** (144 lines) — superseded by the new `*NotAdjudicatedCorrectly` tests above.

### Breaking

`UnwinnableQuickAnalyzer.unwinnableQuick(...)` now returns `UnwinnabilityQuickAnalysis` instead of `UnwinnabilityQuickVerdict`. Use `.verdict()` for the previous behaviour, or `.mateLine()` when a quick `WINNABLE` result should be replayed. `Board.isUnwinnableQuick(Side)` is unchanged.

## [11.0.0] - 2026-05-21

The **Role-inversion release**. With 10.0.0 in hand the per-move data path was bitboard-only, but `StaticPosition` was still a peer of `BitboardPosition` in `src/main/` — the public `Fen` record carried a `StaticPosition` field, `Board.getStaticPosition()` was a public derived view, the SAN / FEN / analyzer layers all still consumed `StaticPosition` through their public surface. This release finishes the inversion: production speaks only `BitboardPosition`, and the entire `StaticPosition` subtree (record, `StaticPositionUtility`, `com.dlb.chess.squares.*` consumer subset, `AbstractLegalMoves` + per-piece `*LegalMoves`, `UnwinnabilityMaterial`) is physically relocated to `src/test/java/` as the permanent differential-test oracle. **Not deleted. Relocated.** Project policy from this point on: every primitive on `BitboardPosition` is asserted against the relocated `StaticPosition` oracle on every fixture in the corpus, for every supported release going forward. See `specification.md` §4.1 and §6.1.

### Notable

- **`StaticPosition` subtree physically relocated to `src/test/java/`.** All 36 files moved via `git mv` preserving history. Original package names preserved (no `reference/` rename) — Java/Maven allow a package to span source roots; `com.dlb.chess.board`, `com.dlb.chess.squares`, `com.dlb.chess.moves`, `com.dlb.chess.unwinnability` now have a test-side surface alongside their production-side surface.
- **Public `Fen` record reshaped.** `Fen` now carries `BitboardPosition bitboardPosition` instead of `StaticPosition staticPosition`. The new shape: `(String fen, BitboardPosition bitboardPosition, Side havingMove, CastlingRight castlingRightWhite, CastlingRight castlingRightBlack, Square enPassantCaptureTargetSquare, int halfMoveClock, int fullMoveNumber)`. `Board(Fen)` reads `fen.bitboardPosition()`.
- **`Board.getStaticPosition()` / `getStaticPositionBeforeLastMove()` dropped.** No deprecated derived view. After this release no `src/main/` class references `StaticPosition`. `Board.toString()` returns `getFen()` instead of routing through the (now-test-side) bridge.
- **SAN layer ported off `StaticPosition`.** Six classes: `StrictSanParser`, `SanValidateLegalMoves`, `SanValidateDestination`, `SanValidatePieceExists`, `SanPieceCheck`, `LenientSanShapeNormalize`. New bitboard helpers on `BitboardPosition`: `kingSquare(Side)`, `potentialToSquares(Square, long)`, `isOwnPiece(Square, Side, PieceType)`, `withRelocatedPiece(Piece, Square from, Square to)`. New bitboard overload of `EnPassantCaptureUtility.calculateIsPotentialEnPassantCapture`. Differential test for `potentialToSquares` against `AbstractPotentialToSquares.calculatePotentialToSquare` on the full corpus.
- **Board internals and analyzer layer ported off `StaticPosition`:** `ChessRuleAnalyzer`, `InsufficientMaterialUtility`, `BoardMaterial`, `ValidateNewMove`, `UciMoveUtility`, `SemiOpenFilesUtility` (StaticPosition variant), `UnwinnableFullAnalyzer` / `UnwinnableQuickAnalyzer` (Fen pass-throughs).
- **FEN layer ported off `StaticPosition`.** `FenParserAdvanced` builds `BitboardPosition` directly. `FenBoard`, `FenMaterialCount`, `FenConstants` updated. Plus a Codex P2 predicate-precedence fix in `FenParserAdvanced.validateEnPassantCaptureTargetSquareAgainstBitboardPosition`: `A || (B && C)` corrected to `A || B || C` (Java operator precedence), with regression tests in `TestFenParserAdvanced`.
- **Production-side move utilities cleaned up.** `CastlingUtility` dropped all StaticPosition overloads (`calculateQueenSideCastlingCheck`, `calculateKingSideCastlingCheck`, `calculate*CheckCondition`, `calculate*IsOriginalPosition`, `calculate*IsEmptySquaresBetweenRookAndKing`, `calculateIsAllEmpty`, `calculateIsEmptySquare`). `EnPassantCaptureUtility` dropped its StaticPosition overloads; `performEnPassantCaptureMovements(Side, MoveSpecification)` no longer takes a StaticPosition. `StandardMoveUtility.performStandardMovements(Piece movingPiece, MoveSpecification)` takes the moving Piece directly instead of looking it up on a StaticPosition. `BitboardLegalMoveFactory.calculateLegalMoves` inlines its own castling generation (it no longer depends on the relocated `AbstractLegalMoves`).
- **`BitboardPosition.INITIAL_POSITION` / `EMPTY_POSITION` rewritten as standalone bit constants** — no longer derived from `StaticPosition.INITIAL_POSITION` via the bridge, since `StaticPosition` is now test-side. `EMPTY_POSITION.zobristPieces()` is `0L` (XOR of nothing).
- **`StaticPositionBridge` (test-side)** carries the `StaticPosition` ⇄ `BitboardPosition` round-trip helpers (`fromStaticPosition`, `toStaticPosition`). Lives in `src/test/java/com/dlb/chess/bitboard/` alongside production `BitboardPosition` — same package, different source root. `BitboardPositionUtility` (production-side) holds only StaticPosition-free helpers.

### Internal

- **Phase 0** — `tasks.md` restructure: 8-phase plan with audit findings (~25 src/main classes still on StaticPosition after 10.0.0).
- **Phase 1** — SAN layer ported. Bitboard helpers added on `BitboardPosition`. Differential test for `potentialToSquares` lands.
- **Phase 2** — Board internals and analyzers ported.
- **Phase 3+4** — FEN parser/serializer ported; `Fen` record reshape (binary-incompatible).
- **Phase 5** — `Board.getStaticPosition()` / `getStaticPositionBeforeLastMove()` dropped. `Board.toString()` returns `getFen()`.
- **Phase 6** — Physical `git mv` of the relocation subtree (`d6ef72fb`-adjacent commits `617411bf` and `5ea36bc3`). Production-side move utilities (`CastlingUtility`, `EnPassantCaptureUtility`, `StandardMoveUtility`) cleaned up to drop their StaticPosition surfaces.
- **Phase 7** — `specification.md` formalises the differential-test layer as permanent policy (`d6ef72fb`).
- **Codex review fixes during the release** — P1 self-referential test in `TestBoardGetBitboardPosition` (rewritten with `StaticPositionUtility.createPositionAfterMove` as the independent oracle); P2 `Board.toString()` routing through the soon-to-relocate bridge (rerouted via `getFen()`); P2 FEN predicate precedence (`A || (B && C)` → `A || B || C`); P3 stale comment correction in `TestFenParserAdvanced`.
- **Codex post-release-cut fixes** (`4ddae3d1`) — P1 javadoc doclint: six stale `@link` references in `src/main` Javadoc fixed (five pointed at the relocated `StaticPosition` subtree from `src/main` scope where it no longer resolves; one pointed at `com.dlb.chess.board.DynamicPosition`, a path that never existed — the record lives in `com.dlb.chess.common.model`). All converted to `@code` prose for relocated types, or repointed to the correct package. `mvn javadoc:javadoc` now passes. P2 castling oracle independence: the test-side `KingCastlingLegalMoves.calculateKingCastlingLegalMoves(StaticPosition, ...)` overload was bridging through `StaticPositionBridge.fromStaticPosition` plus the production `CastlingUtility` bitboard checks — the same checks `BitboardLegalMoveFactory` drives — which weakened the differential oracle for castling. Restored independence: the test-side overload now re-implements the StaticPosition castling check end-to-end on the mailbox surface (`StaticPosition.get(Square)` + `AbstractAttackedSquares.calculateAttackedSquares(StaticPosition, Side)`), duplicating the required-empty corridors, king-travel and king-destination squares, original-position predicates, and the four `CastlingCheck` states.

### Breaking

`Fen` record shape changed: now `(String fen, BitboardPosition bitboardPosition, Side havingMove, CastlingRight castlingRightWhite, CastlingRight castlingRightBlack, Square enPassantCaptureTargetSquare, int halfMoveClock, int fullMoveNumber)`. Callers reading `fen.staticPosition()` must switch to `fen.bitboardPosition()`. Callers constructing `Fen` directly must pass a `BitboardPosition` in slot 2.

`Board.getStaticPosition()` and `Board.getStaticPositionBeforeLastMove()` are gone. Callers wanting the mailbox view must construct a `StaticPosition` themselves — but note that `StaticPosition` itself is now under `src/test/java/`, so this is only available on the test classpath. Typical end-user code does not need either.

`InsufficientMaterialUtility.calculateIs*(StaticPosition, ...)` overloads, `ChessRuleAnalyzer.analyze*(StaticPosition, ...)` overloads, and other `StaticPosition`-taking public surfaces are gone. The bitboard-taking variants remain.

`CastlingUtility`'s StaticPosition overloads (`calculateQueenSideCastlingCheck`, `calculateKingSideCastlingCheck`, `calculate*CheckCondition`, `calculate*IsOriginalPosition`, etc.), `EnPassantCaptureUtility`'s StaticPosition overloads, and `StandardMoveUtility.performStandardMovements(StaticPosition, ...)` are gone. Direct callers must switch to the bitboard variants.

`BitboardPositionUtility.fromStaticPosition` / `toStaticPosition` are gone from `src/main/` — they relocated to `StaticPositionBridge` (test-side). Production code that mixed the two representations should consume `BitboardPosition` directly; test code can use the bridge.

The packages `com.dlb.chess.board`, `com.dlb.chess.squares`, `com.dlb.chess.moves`, and `com.dlb.chess.unwinnability` now span both source roots. Production classes are unchanged but their package-mate StaticPosition consumers (`AbstractAttackedSquares`, `*PotentialToSquares`, `*AttackedSquares`, `*RangeSquares`, `*LegalMoves`, `AbstractLegalMoves`, `UnwinnabilityMaterial`) are only available on the test classpath. Production code that imported them must port to `BitboardPosition` primitives.

### Migration

Typical use (read a PGN, play moves, query check / legal moves / unwinnability): no source change. `Board`'s public API around game progression is preserved.

If your code reads `Board.getStaticPosition()`: switch to `board.getBitboardPosition()` and ask the bitboard directly (`get`, `getPiece`, `isEmpty`, `occupied`, `attackedSquares`, `isInCheck`, `legalMoves`, `afterMove`). The bitboard exposes everything the mailbox did plus more (attack maps, pin rays, Zobrist).

If your code unpacks a `Fen` record: change `fen.staticPosition()` → `fen.bitboardPosition()`.

If your code called `BitboardPositionUtility.fromStaticPosition`/`toStaticPosition` from production: it shouldn't have, but if it did, switch the calling code to flow `BitboardPosition` throughout instead of round-tripping through the mailbox.

### Release gates

`mvn test` (restricted): **1132 / 0 / 0 / 4** — green. `mvn javadoc:javadoc`: green. `mvn test -Pfull`: **1132 / 0 / 0 / 1** — green. The corpus cleanup that originally unblocked `-Pfull` shipped in [10.0.0] (see *Corpus cleanup* there); this release inherits the green state.

## [10.0.0] - 2026-05-19

The **Switchover release**. With 9.0.0 in hand the bitboard layer was a verified parallel implementation but `Board.move()` still computed `StaticPosition` per ply and the parallel `bitboardPositionList` cache derived its entries via `BitboardPositionUtility.fromStaticPosition(afterStaticPosition)`. This release moves the per-move data path entirely onto the bitboard: the incremental `BitboardPosition.afterMove` becomes the only computation, `StaticPosition` no longer rides alongside `DynamicPosition`, and the per-move `StaticPositionUtility.createPositionAfterMove` call is gone. `Board.getStaticPosition()` survives as a derived view via `BitboardPositionUtility.toStaticPosition()` on the cached bitboard — public API preserved, internal data path bitboard-only.

Physical relocation of the `StaticPosition` subtree to `src/test/` is **not** part of this release. The audit at the start of 10.0.0 surfaced ~25 `src/main/` classes outside the relocation subtree that still consume `StaticPosition` (including the public `Fen` record, the SAN classes, `FenParserAdvanced`, `ChessRuleAnalyzer`, `InsufficientMaterialUtility`, `BoardMaterial`, `ValidateNewMove`, `UciMoveUtility`, `SemiOpenFilesUtility`, the FEN-pass-through analyzers). Porting all of those off `StaticPosition` is a multi-release effort and is now scoped as its own dedicated future release in `tasks.md` — *Future release — StaticPosition subtree relocation to `src/test/`*.

### Notable

- **Per-move incremental bitboard hot swap.** `Board.performMoveWithoutValidation` no longer derives the next-position bitboard from a freshly-computed `afterStaticPosition`. It calls `Nulls.getLast(dynamicPositionList).bitboardPosition().afterMove(moveSpecification, havingMove)` directly. `BitboardPosition.afterMove` was implemented and corpus-spine-tested in 9.0.0 (Step 7.1, commit `efac6be4`); this release flips the call site.
- **`BitboardPosition` carried on `DynamicPosition`.** The 9.0.0 parallel `Board.bitboardPositionList` field is gone. `DynamicPosition` gains a `bitboardPosition` field and travels as the unified per-ply position record. `Board.getBitboardPosition()` now reads off the last `DynamicPosition`. `RepetitionUtility.equals` compares bitboards (twelve `long`s) instead of static positions (sixty-four `Piece` fields).
- **`DynamicPosition.staticPosition` field dropped.** `Board.getStaticPosition()` / `getStaticPositionBeforeLastMove()` derive their result on demand from the cached bitboard via `BitboardPositionUtility.toStaticPosition()`. The public method signatures and return values are unchanged — every external caller of `Board.getStaticPosition()` still works without source change. What's gone is the cached `StaticPosition` instance per ply.
- **Per-move `StaticPositionUtility.createPositionAfterMove` call removed from `Board.move()`.** With `afterStaticPosition` no longer flowing downstream, `BitboardLegalMoveFactory.calculateLegalMoves` drops its `StaticPosition` parameter (castling generation goes through a new bitboard-shaped bridge `AbstractLegalMoves.calculateCastlingLegalMoves(BitboardPosition, ...)`), and `Board.calculateIsEnPassantCapturePossible` switches to a bitboard-shaped signature that uses `BitboardPosition.afterMove(...).isInCheck(...)` for its king-safety check.
- **Bitboard-shaped castling primitives added.** `CastlingUtility.calculateQueenSideCastlingCheck(BitboardPosition, ...)` and `calculateKingSideCastlingCheck(BitboardPosition, ...)` sit alongside their `StaticPosition` siblings (which remain for the reference layer). `KingCastlingLegalMoves` and `AbstractLegalMoves` each gain a `BitboardPosition`-shaped overload of their respective public bridge methods. The corridor-empty-square constants and king-travel/destination square helpers are reused across both variants.
- **Board's private `calculateLegalMove(StaticPosition, ...)` helper deleted.** Its responsibilities (movingPiece/capturedPiece/kind derivation for a known-legal `MoveSpecification`) are equivalent to and now provided by `BitboardLegalMoveFactory.toLegalMove(BitboardPosition, MoveSpecification, Side)` (added in 9.0.0 Step 2.1 and corpus-tested against `AbstractLegalMoves.calculateLegalMoves` as the StaticPosition-backed oracle).

### Internal

- `Step 1` — per-move incremental hot swap (`cb136e49`).
- `Step 2` — move `BitboardPosition` onto `DynamicPosition`; drop the parallel `Board.bitboardPositionList` field (`15659571`).
- `Step 3` — drop `DynamicPosition.staticPosition`; `Board.getStaticPosition()` becomes derived view; `RepetitionUtility.equals` switches to `BitboardPosition.equals` (`e7fd7815`).
- `Step 4` — remove per-move `StaticPosition` computation entirely from `Board`; add bitboard-shaped castling primitives to `CastlingUtility` / `KingCastlingLegalMoves` / `AbstractLegalMoves`; `BitboardLegalMoveFactory.calculateLegalMoves` drops its `StaticPosition` parameter; private `Board.calculateLegalMove` helper deleted; `Board.calculateIsEnPassantCapturePossible` ported to `BitboardPosition` (`fdff3f5a`).
- `LibraryCarlosBoard` (test oracle): builds its `DynamicPosition` via `BitboardPositionUtility.fromStaticPosition(getStaticPosition())` since it has no native bitboard of its own. Pure mechanical update across Steps 2 and 3.
- `tasks.md` restructured at the top of 10.0.0 (`7dde3b61`) — Switchover release scope reframed to scope B; the lean-analyzer / magics / perf phases moved into a dedicated *Lean bitboard winnability release* section; the physical relocation captured in a dedicated *Future release — StaticPosition subtree relocation to src/test/* section that documents the audit findings.

### Breaking

`DynamicPosition` record shape changed twice: it gained a `BitboardPosition bitboardPosition` field (Step 2) and lost its `StaticPosition staticPosition` field (Step 3). The post-10.0.0 shape is `(Side havingMove, BitboardPosition bitboardPosition, Square enPassantCaptureTargetSquare, CastlingRight castlingRightWhite, CastlingRight castlingRightBlack)`. Any caller that constructed `DynamicPosition` directly or read `dynamicPosition.staticPosition()` must update — typical end-user code does not touch `DynamicPosition` directly.

`Board.bitboardPositionList` is gone (the field was added in 9.0.0 Step 1.2 — it was never public, but custom subclasses or reflection consumers that touched it must read via `dynamicPositionList` and `.bitboardPosition()` instead).

`BitboardLegalMoveFactory.calculateLegalMoves(BitboardPosition, StaticPosition, Side, CastlingRight, long)` is replaced by `BitboardLegalMoveFactory.calculateLegalMoves(BitboardPosition, Side, CastlingRight, long)` — the `StaticPosition` parameter is dropped. Direct callers (uncommon: this is an internal-feeling API even though public) must drop the argument.

### Migration

Typical use (read a PGN, play moves, query check / legal moves / unwinnability): no source change. The bitboard backend is transparent — same APIs, same semantics. `Board.getStaticPosition()` still returns the same `StaticPosition` you'd expect, just computed on demand instead of cached.

If your code constructed `DynamicPosition` directly: rebuild against the new shape; pass `bitboardPosition` instead of `staticPosition`. If you computed `BitboardPositionUtility.fromStaticPosition(...)` for the bitboard component, that still works.

If your code called `BitboardLegalMoveFactory.calculateLegalMoves(bitboard, staticPosition, side, castling, enPassantBit)`: drop the `staticPosition` argument. The function now uses the bitboard for castling generation too (via the new `AbstractLegalMoves.calculateCastlingLegalMoves(BitboardPosition, ...)` bridge).

### Corpus cleanup

Pre-existing test-fixture debt cleared so all release gates pass green:

- **28 CHA test fixtures** under `src/test/resources/pgn/cha/` had malformed trailing whitespace (missing or extra empty lines, sometimes missing the result token entirely). The strict PGN parser requires exactly two empty lines per file — one after the last tag block, one at end of file. Normalised all 28 files to end with `...<result>\n\n`. Four observed shapes (tag-only with no movetext, movetext with one trailing newline, movetext with no trailing newline, movetext with three trailing newlines). Caught by `TestSetupPgnCorpusNotPlaysBeyondAudit`, whose catch net is broader than its "plays beyond a dead position" name suggests — none of these files actually played past termination; they failed the strict parser at the file-structure pre-scan.
- **`test_lichess_V7eJ1RR9_helpmate.pgn`** had a double space at move 56 (`bxc7+  Qxc7`) flagged as "a half-move must be followed by a single space before the next token." Collapsed to single space.
- **`01_beyond_fivefold.pgn`** had its movetext start with `10. Kc8 Kc6 ... 17. Kd8 Kd6` while the FEN tag specified fullmove number 50. Renumbered the movetext to `50. ... 57.` to match the FEN. The repetition pattern (Kc8/Kd8 vs. Kc6/Kd6) is unchanged.
- **`TestLegacyPgnParsePlaysBeyondAudit`** asserted a hardcoded expected count of 101 legacy fixtures, but the actual `pgnParser/legacy/common/beyond/` folder and the test's own `EXPECTED` map both have 99. Updated the constant to 99.

Release gates after the cleanup: `mvn test` 1132/0/0/4, `mvn javadoc:javadoc` green, `mvn test -Pfull` 1132/0/0/1 (one suite-level `@assumeFalse` skip). No pre-existing failures remain.

## [9.0.0] - 2026-05-19

The **Bitboard release**. A second piece-placement representation (`BitboardPosition`, twelve `long`s indexed by `Square.ordinal()` little-endian rank-file) is built alongside `StaticPosition`, verified bit-exact against it on the full PGN/FEN corpus for every primitive, and then production hot paths switch to consume it. `StaticPosition` and the surrounding mailbox-rich-board reference layer remain in `src/main/` per the **Project Invariant** documented in `tasks.md`: that implementation represents the project's correctness ground truth and is never deleted — only relocated to `src/test/` as the permanent differential-test oracle in the next release.

Suite runtime drops from ~54s on the StaticPosition baseline to ~31s on the bitboard primary path, on the same corpus. Sliding attacks use classical ray loops in this release; magic bitboards stay profile-gated.

### Notable

- **`BitboardPosition` record** in the new `com.dlb.chess.bitboard` package. Twelve `long`s — one per real `Piece`, each bit indexed by `Square.ordinal()`. Immutable; the compact constructor enforces pairwise-disjointness (no square may carry two pieces). Public methods cover the full board surface: `get`, `isEmpty`, `occupied`, `occupied(Side)`, `attackedSquares`, `isInCheck`, `attackersTo`, `legalKingTargets` (XRAY-aware), `pinRay`, `pinnedPieces`, `legalMoves(Side, long enPassantBit)`, immutable `afterMove(MoveSpecification, Side)`, `zobristPieces()`, `hashDelta(MoveSpecification, Side)`.
- **Differential-test layer** drives every bitboard primitive against the StaticPosition reference on the full PGN/FEN corpus — piece queries, per-piece attacks (knight, king, two pawn tables, bishop/rook/queen via classical ray loops), pseudo-legal moves, aggregate attacks, `attackersTo`, `legalKingTargets`, pin detection, full legal-move generation, immutable `afterMove`, Zobrist piece-square hash, incremental `hashDelta`. Two spine-level assertions: legal-move agreement (`BitboardPosition.legalMoves` vs. `AbstractLegalMoves`, every fixture × side-to-move) and afterMove state agreement (`BitboardPosition.afterMove` vs. `StaticPositionUtility.createPositionAfterMove`, every fixture × every legal move including the four promotion targets and both castling sides). Disagreement is a correctness signal; the bitboard side must yield.
- **Production hot paths now consume `BitboardPosition`:**
  - `Board.isCheck()` → `bitboardPosition.isInCheck(side)`.
  - `Board.getLegalMoves()` → `BitboardLegalMoveFactory.calculateLegalMoves` (bitboard for non-castling + bridge to the existing `KingCastlingLegalMoves` for castling).
  - All five `UnwinnableQuickAnalyzer` private helpers (`calculateHasOnlyPawnsBishopsAndKings`, `calculateIsAlmostOnlyPawnsBishopsAndKings`, `calculateIsBlockedCandidate`, `calculateNumberOfBlockedPawns`, `calculateHasLonelyPawns`).
  - `Mobility.mobility`, `Score.score`, `GoingToCorner.goingToCorner`.
  - `FindHelpmateExhaust` (KingOnly / HasNoPawns / needLoserPromotion guards, the `Score.score` call, the `calculateHasQueen` check, the EP-erase adjacency check, and the Lemma 5 / Lemma 6 helpers) and `FindHelpMateInterrupt`.
- **`Board.getBitboardPosition()`** — new public accessor returning the cached current-position bitboard in O(1). The bitboard is maintained as a parallel `bitboardPositionList` alongside `dynamicPositionList`, appended on every `move()` and popped on every `unmove()`.
- **`BitboardPositionUtility`** — bit-exact conversion (`fromStaticPosition` / `toStaticPosition`) and a `toSquareSet(long)` decoder used by the differential-test harness.
- **`ZobristKeys`** — 768 fixed-seed piece-square keys (12 pieces × 64 squares) deterministic across JVM runs, for transposition keys persisted across processes. Side-to-move / castling / en-passant keys deferred to the next release (lean helpmate board).
- **`UnwinnabilityMaterial` split** into `UnwinnabilityMaterial` (StaticPosition reference oracle, only called from the differential test) and `UnwinnabilityMaterialBitboard` (production, called from all `src/main/` consumers). Pre-positions the StaticPosition variant for the next release's relocation to `src/test/` as a single `git mv`.
- **`AbstractLegalMoves.calculateCastlingLegalMoves`** exposed as a public bridge so the bitboard legal-move pipeline can compose castling without duplicating the castling-rights logic.
- **`PawnAttacks` geometric across all 64 from-squares.** Reverse-attack identity (used by `attackersTo`) needs pawn attack patterns defined on the back ranks too; the table is now purely geometric rather than mirroring the StaticPosition "pawns only on ranks 2-7" convention.
- **Project Invariant** documented prominently in `tasks.md` (Bitboard release section): the `StaticPosition` reference implementation and its rich-board consumer tree (`AbstractAttackedSquares`, `com.dlb.chess.squares.*`, `AbstractLegalMoves`, `com.dlb.chess.moves.*`, `StaticPositionUtility`) represent several years of correctness-first work, hand-derived independently from the FIDE rules. They are **never deleted**; they relocate to `src/test/` at the end of the switchover and stay as the permanent differential-test oracle. If the migration can't reach that end state, the migration doesn't happen.

### Internal

- **Phase 0-9** of the bitboard release (purely additive) covered the parallel build-out: record + conversion → non-sliding attacks → classical-ray sliding attacks → aggregate attacks + check → pseudo-legal moves → legal moves with pins and XRAY king-safety → immutable `afterMove` → Zobrist piece-square hash + incremental delta. Each phase landed on `use_bitboard_for_rich_board` as one reviewable commit with its differential test attached. See `tasks.md` for per-step commit boundaries.
- **Phase 1-2** of the switchover release flipped production: `Board.getBitboardPosition()` added → bitboard cached on `Board` → `isCheck` and `getLegalMoves` switch → all unwinnability analyzers ported.
- **Phase 3+ deferred** to a separate next release: lean analyzer board for `FindHelpmateExhaust` with state-keyed transposition (side / castling / EP keys), conditional mutable make/unmake, conditional magic bitboards, the `StaticPosition` subtree relocation to `src/test/`, and the performance baseline. WIP for Phase 3 is preserved on `origin/feature/lean-bitboard-helpmate-wip` as the resume base.
- Eclipse JDT null-analysis cleanup: all `Square.REAL.get(int)` callsites routed through `Nulls.get(Square.REAL, ordinal)` so the result is `@NonNull` rather than `@Nullable`.
- Two Codex P2 fixes worth noting: (1) the legal-move tests were silently turned self-referential when Step 2.2 switched `Board.getLegalMoves` to the bitboard pipeline — the oracle is now pinned to `AbstractLegalMoves.calculateLegalMoves` directly. (2) `BitboardPosition`'s compact constructor was missing a popcount-vs-union check for the disjointness invariant.

### Breaking

None on the public API. The bitboard backend is additive infrastructure; existing entry points (`Board`, the parsers, the reporters) keep their signatures and semantics. `Board` gains the new public method `getBitboardPosition()`. `AbstractLegalMoves` gains a new public `calculateCastlingLegalMoves` method (was previously package-private). `UnwinnabilityMaterialBitboard` is a new package-private class.

### Migration

For typical use (read a PGN, play moves, query check / legal moves / unwinnability): no source change. The bitboard backend is transparent — same APIs, same semantics, just faster. If you want a `BitboardPosition` directly (e.g. to write your own engine code against it), call `board.getBitboardPosition()`.

## [8.0.0] - 2026-05-17

The **DeepSquare moment**. clean-chess closes FIDE 5.2.2 dead-position gap to high extend (auto-CHA unwinnability quick per move) and is now cross-validated against the D3-Chess (Ambrona FUN22) reference oracle at three levels. Higher coverage with unwinnability full in evaluation for later release.

### Notable
- **Auto-CHA per move.** `DEAD_POSITION_UNWINNABLE_QUICK` is the sixth FIDE-automatic termination. The validation pipeline — both `ValidateNewMove` and `StrictSanParser` — rejects moves attempted on a dead position with `GAME_ALREADY_ENDED`, the thrown exception carrying the originating `GameStatus`. Consumers that previously had to query unwinnability themselves and stop the game manually can delete that code.
- **Ambrona oracle cross-validation.** D3-Chess oracles imported at three levels — `mobility`, `semi-static`, `unwinnability-full` — and run as JUnit comparisons against clean-chess. Three TSV accepted-differences tables document the small, justified deviations; the suite fails on any silent disagreement. The largest test asset of the release.
- **CHA algorithms paper-aligned.** `UnwinnableQuickAnalyzer`, `UnwinnableSemiStatic`, `Mobility`, and `FindHelpMateInterrupt` realigned with FUN22 / D3-Chess. `FindHelpMateInterrupt` now implements Figure 5 search with a transposition table + Figure 12 score-based depth adjustment; the helpmate search ignores 75-move / fivefold per the paper.
- **Pawn-wall geometric classifier overhauled.** Sound tri-state verdict (`YES` / `NO` / `UNKNOWN`), all-pawns-involved soundness gate, BFS king-walk oracle, corpus split into `yes/` and `no/` subfolders, cross-checked against `UnwinnableQuick`.
- **Helpmate transposition key.** `FindHelpmateExhaust`'s visited-position store keys on `DynamicPosition` (with normalised en-passant) instead of full-FEN strings — no per-node FEN serialisation, exact equality.
- **`Board.copyCurrentPositionWithoutHistory(boolean)`.** Clean snapshot API for the helpmate search. Halfmove clock reset to 0, move history dropped — the new position is insensitive to 75-move and fivefold per the CHA paper. Replaces the previous detection-suppression hack.
- **Game-ended rejection tests cover all six terminations symmetrically** in both pipelines (`TestValidateNewMoveGameEnded` and `TestSanValidationGameEnded`) — including two scenarios for the new dead-position-quick row: born dead from FEN and played into the wall.
- **Basic-checkmate CHA test matrix.** K+R, K+Q, K+2B (opposite colours), K+R+B vs K — must-capture-draw / mate-in-4-plies / mate-in-10-plies / kings-at-opposite-edges per material.
- **Test-corpus parse and FEN caches.** `PgnCacheForLenientPgnParserTestCases` and `FenCacheForTestCases` cache the parsed `PgnGame` and parsed `Fen` per corpus fixture for the test-JVM lifetime; `PgnTestCase.game()` and `finalPosition()` route through them.
- **Empty PGN input rejection.** Both `LenientPgnParser` and `StrictPgnParser` reject zero-byte and whitespace-only input as `FILE_EMPTY` — previously parsed silently to an initial-position game with zero moves.

### Breaking
- `GameStatus.INSUFFICIENT_MATERIAL_BOTH` renamed to `DEAD_POSITION_INSUFFICIENT_MATERIAL`; per-side variants renamed to `INSUFFICIENT_MATERIAL_WHITE_ONLY` / `INSUFFICIENT_MATERIAL_BLACK_ONLY`.
- New `GameStatus.DEAD_POSITION_UNWINNABLE_QUICK`; `GameStatus.isAutomaticTermination()` returns `true` for it. Existing consumers that played past such a position will now receive `InvalidMoveException` / `SanValidationException` with `GAME_ALREADY_ENDED`.
- `Board(String fen)` and `Board()` default to dead-position auto-detection enabled. Tests and bulk PGN replayers that pass through positions the quick analyzer would classify as dead must construct with the explicit `Board(fen, false)` form. Both PGN parsers already do this for you.
- `Board.copyCurrentPositionWithoutHistory(boolean)` replaces the previous detection-suppression API used by the helpmate search.
- Test-side rename `PgnFileTestCase` → `PgnTestCase`; PGN test paths and class names rename `pgnFile` segment → `pgn`; `position()` / `game(PgnTest)` paths now named `finalPosition()` / `game(PgnTest)` on `PgnTestCase` (cheap vs expensive choice now visible at every call site).

### Migration
For typical use (read a PGN, play moves, query unwinnability): no source change. Dead-position auto-termination now happens automatically; delete any manual `if (UnwinnabilityQuick.unwinnable(...)) stop()` logic at the call site.

If you replay positions through `Board` where the CHA quick analyzer would classify the intermediate as dead, construct with `new Board(fen, false)` to suppress auto-detection.

If your code matches on `GameStatus.INSUFFICIENT_MATERIAL_BOTH`, rename to `DEAD_POSITION_INSUFFICIENT_MATERIAL`. If you branch on "dead position" generally, also branch on `DEAD_POSITION_UNWINNABLE_QUICK`.

## [7.0.0] - 2026-05-14

### Notable
- **Lenient FEN parser added.** `LenientFenParser` (and `Board.fromFenLenient(String)`) runs a syntactic-tolerance pre-pass before delegating to `FenParserAdvanced`. Forgives whitespace deviations (leading/trailing/extra/tab-or-newline), missing trailing counters (four-field FENs from Stockfish UCI, five-field FENs with missing fullmove), uppercase side-to-move, non-canonical castling order, non-ASCII dashes in the en-passant field (em-dash, en-dash, etc.), uppercase en-passant target square, and trailing garbage tokens. The lenient layer also auto-corrects the half-move-clock-vs-full-move-number inconsistency by bumping the full-move number up to `halfMoveClock` rounded up to the next multiple of ten — a generous reserve over the strict minimum (so `... 15 1` becomes `... 15 20`, not `... 15 9`); the round-numbered placeholder signals a reconstructed value rather than a measured one. Every transform that fires surfaces as a typed `ForgivenFenItem` on the validation result; strict semantic invariants are unchanged (a FEN with a missing king, a pawn on rank 1, or an impossible double-check still fails). Twelve `ForgivenFenItemCode` values; one test fixture per code plus an end-to-end deficient-FEN case.
- **Half-move-clock vs full-move-number consistency check promoted into `FenParserAdvanced`.** A FEN like `... 15 1` (15 half-moves played, claiming move 1) is physically impossible and is now rejected by strict FEN parsing as `INVALID_HALF_MOVE_CLOCK_TOO_BIG_RELATIVE_TO_FULL_MOVE_NUMBER`. The check previously lived in the test-only `FenParserAdvancedFurther` (which was kept out of main because of the practical incompatibility with FEN exporters that emit `fullMoveNumber=1` as a placeholder); the lenient FEN parser's auto-correction makes that practical concern moot. The `fullMoveNumber=1 ⇒ initial-or-after-first-move` branch from `FenParserAdvancedFurther` is dropped entirely — too unfriendly to real-world exporters.
- **`LenientPgnParser` routes the FEN tag through `LenientFenParser`.** Symmetry with movetext leniency: a deficient FEN tag in a lenient-parsed PGN no longer fails, the FEN-level forgiveness is applied silently. Strict PGN parsing reads the FEN tag through strict advanced parsing — unchanged.
- **PGN parse, semantics, and export separated into four explicit jobs.** Parsers now preserve input as given — no fabrication into the parse model, no tag-list normalisation. Export gains an explicit `WriteMode { SEMANTIC, ARCHIVAL }` axis: semantic emits the parse model as-given (the default), archival produces a PGN spec §8.1.1-conformant artifact (opt-in). The library's posture is honest preservation by default; archival storage is a mode the caller asks for, not a tax the parser levies.
- **Strict parser stops requiring the full Seven Tag Roster.** PGN spec §8.1.1 introduces STR as required *"for archival storage of PGN data,"* not for general spec-compliant PGN. Strict parsing now requires only the semantic essentials — a Result tag (whose value must match the termination marker) and the SetUp/FEN coupling. A four-tag PGN (Result + a few extras) parses cleanly through `StrictPgnParser`.
- **`PgnFile` carries a new `terminationMarker` field.** The movetext game-termination marker is now an independent signal from the `Result` tag in `tagList()`. Both, either, or neither may be present in lenient-parsed input. Strict-parsed input always has both (and they match). This replaces the previous implicit conflation where "Result tag absent" was indistinguishable from "ongoing."
- **New `tagForgivenItems()` channel on `LenientPgnParserValidationResult`** lists tag-level deviations the lenient layer tolerated (missing STR entries, Result tag absent, FEN-without-SetUp, redundant initial-position FEN/SetUp). Mirrors the existing `sanForgivenItems()` channel for SAN-level deviations.
- **`PgnCreate.createPgnFile(Board)` produces the minimal honest shape** — empty `tagList` for an initial-position board, `[SetUp, FEN]` for a non-initial position, `terminationMarker` derived from the board's game-status. STR fabrication moves to the archival export path.
- **Spec-correct placeholder values in archival output.** Date now uses `????.??.??` per PGN spec §8.1.1.3 (previously `?`). Result defaults to `*` per §8.1.1.7 when neither tag nor termination marker is provided.
- **`specification.md` §3.3.2** documents the four-jobs contract and the strict parser's revised mandate.

### Breaking
- `FenAdvancedValidationProblem` gains `INVALID_HALF_MOVE_CLOCK_TOO_BIG_RELATIVE_TO_FULL_MOVE_NUMBER`. FENs with half-move-clock greater than the maximum consistent with the full-move number (e.g. `... 15 1`) now fail strict FEN parsing — they previously passed. Callers that constructed `Board` via `new Board(String)` from such FENs should switch to `Board.fromFenLenient(String)` for auto-correction.
- `PgnFile` record gains a 5th component `@Nullable ResultTagValue terminationMarker`. Any direct constructor call needs the extra argument.
- `LenientPgnParserValidationResult` record gains a 6th component `ImmutableList<ForgivenTagItem> tagForgivenItems`. Any direct constructor call needs the extra argument.
- `LenientPgnParserValidationException` 5-argument constructor replaced by a 6-argument constructor that also carries the accumulated tag-level forgiven items.
- `StrictPgnParserValidationProblem.TAG_NOT_ALL_REQUIRED_TAGS_SET` renamed to `TAG_RESULT_MISSING` — the strict parser only requires the Result tag now, not the full STR.
- `TagPlaceHolderUtility` (package-private) deleted; archival fill now lives in `PgnArchivalNormalization`.
- `PgnCreate.createPgnFile(Board)` no longer fabricates the Seven Tag Roster into the model. Consumers who relied on the produced `PgnFile` containing STR placeholders should switch to `WriteMode.ARCHIVAL` on the writer.
- `PgnWriter.writePgnFile(PgnFile, ...)` defaults to `WriteMode.SEMANTIC` — the output preserves the parse model rather than fabricating canonical form. Pass `WriteMode.ARCHIVAL` explicitly for the prior fabrication behavior.

### Migration
For typical use (parse a PGN, replay it, write it back): no source change needed — the writer defaults to semantic mode, which round-trips the parse model. If your previous workflow depended on the writer producing a canonical (STR-filled, sorted) artifact, pass `WriteMode.ARCHIVAL` to the relevant `PgnWriter` / `PgnCreate` entry point.

If your code matches on `StrictPgnParserValidationProblem.TAG_NOT_ALL_REQUIRED_TAGS_SET`, rename to `TAG_RESULT_MISSING`. The strict parser now only rejects when the Result tag is missing; other STR-omitted PGNs that previously raised this error now parse successfully and surface the missing tags as lenient-parser forgiven items if processed via the lenient path.

If your code constructs `Board` from FEN strings with `fullMoveNumber=1` placeholders on non-initial positions (a common "speculative" pattern from FEN exporters that don't know the actual move number), switch to `Board.fromFenLenient(String)`. The lenient FEN parser auto-corrects the inconsistent counter (bumps `fullMoveNumber` up to `halfMoveClock` rounded up to the next multiple of ten) and surfaces a `HALF_MOVE_CLOCK_INCONSISTENT_WITH_FULL_MOVE_NUMBER` forgiven item.

## [6.0.0] - 2026-05-13

Technical-cleanup release. No new features. The model is tighter, generator-printed code is gone, a research-only dual code path is retired, and one pawn-wall bug is fixed (with the known remaining false-positive class scoped to the DeepSquare release).

### Notable
- `LegalMove` now carries a `LegalMoveKind` category (`NORMAL` / `CASTLING` / `EN_PASSANT_CAPTURE` / `PAWN_TWO_SQUARE_ADVANCE` / `PROMOTION`). Consumers stop recomputing the category from `MoveSpecification` fields.
- FEN single-letter vocabulary moved off chess-domain types into `FenSideSymbol` / `FenPieceSymbol`. The chess `Side` / `Piece` enums no longer know FEN syntax.
- En-passant threefold-repetition research dual-path retired. The FIDE-correct path is the only path.
- Square geometry consolidated to compact precomputed tables; ~thousand-line generated tables collapsed to small static initializers.
- Large generated enums replaced with computed lookups: `UciValidateHelper` (1984 lines) and the seven SAN-validator strict enums (5105 lines combined).
- Pawn-wall classifier: bishop-reachability now BFS-correct (closes a previously-known false negative). The known false-positive class is documented in [pawn-wall-soundness.md](pawn-wall-soundness.md) and deferred to the DeepSquare release.
- FEN-validation documentation no longer overclaims "no real game could reach"; reframed as structural and rule-consistency validation.
- CHA / unwinnability documentation reframed: README and `unwinnability/package-info.java` now describe unwinnability as "no legal sequence can end with that side giving checkmate, even if the opponent cooperates" (replacing the misleading "worst-case play" framing). Same pass corrects the README's "CHA full is 100% accurate" to align with the documented `UNDETERMINED` outcome.
- `NonNullWrapperCommon` renamed to `Nulls` (pervasively used; short name was overdue).
- Javadoc tightened: `doclint=all,-missing` with `failOnError=true`. Opt-in (not bound to `mvn package`) — run explicitly via `mvn javadoc:javadoc` or `mvn javadoc:jar`. 15 broken `{@link}` references uncovered when the gate was turned on are fixed; the published javadoc now builds clean.

### Breaking
- `LegalMove` constructor: `LegalMoveKind` is now mandatory; the three-argument form is removed.
- `EnPassantRole` enum removed. Use `legalMove.kind() == LegalMoveKind.EN_PASSANT_CAPTURE` / `LegalMoveKind.PAWN_TWO_SQUARE_ADVANCE`.
- `EnPassantCaptureRuleThreefold` enum and the dual-path repetition fields on `Report` removed.
- `UciValidateHelper` enum removed (internal).
- `Side.calculate(String)`, `Side.getFenLetter()`, `Piece.getLetter()` removed — FEN-letter knowledge belongs to the FEN parser, not chess-domain types.
- `NonNullWrapperCommon` class renamed to `Nulls`.

### Migration
For typical use (`Board`, the parsers, the reporters): none. The breaking surface is internal vocabulary plus a single record shape change.

## [5.0.0] - 2026-05-11

Reduce public API surface release. No feature changes; the surface is narrowed to what was always intended — play chess correctly and report rule-true outcomes. Material arithmetic and other internal helpers that supported that intent are now internal.

### Breaking — packages removed
- `com.dlb.chess.utility` — split into the feature packages that own each helper.
- `com.dlb.chess.range` — absorbed into `com.dlb.chess.squares` (the only consumer).
- `com.dlb.chess.distance` — absorbed into `com.dlb.chess.unwinnability` and made internal.

### Breaking — types no longer public
- `MaterialUtility`, `InsufficientMaterialUtility` — material-arithmetic helpers that were never part of the contract. Insufficient-material termination remains observable via `Board.isInsufficientMaterial(Side)` and `Board.calculateInsufficientMaterial()`.
- `ChessBoard` interface — collapsed into `Board`. There is one board type.
- Numerous internal helpers across `squares`, `moves`, `san`, `unwinnability`, `pgn`, `report` that were public only because they sat in `src/main/java`. After this release the implementation classes that exist to serve a public entry point are package-private.

### Migration
For typical use (`Board`, the parsers, the reporters): none.

If your code referenced one of the removed-or-demoted types, it was reaching into internals. The current public API is the supported surface; please open an issue if you have a legitimate use case that no longer fits.

### Notable
- Sub-package flattens for `squares`, `moves`, `unwinnability`, `san`, `pgn`, `report` — each feature is now a single coherent package rather than a tree of mostly-trivial sub-packages.
- Removed unused public enum convenience methods for double-step file/rank movement and thin rank predicates.

## [4.0.0] - 2026-05-10

Lenient SAN release. New parser pipeline accepts a defined set of forgivable deviations from canonical SAN; the move-execution and parser API is renamed across the board to make the strict / lenient axis explicit at every call site.

### Breaking — move execution
- `Board.performMove(String)` → `Board.moveStrict(String)`; now returns `StrictSanParserValidationResult` (the resolved `MoveSpecification` is on the result) rather than `boolean`.
- `Board.performMove(MoveSpecification)` → `Board.move(MoveSpecification)`; still returns `boolean`.
- `Board.performMoves(String...)` → `Board.movesStrict(String...)`; still returns `boolean`.
- `Board.unperformMove()` → `Board.unmove()`.
- New `Board.moveLenient(String)` returns `LenientSanParserValidationResult` carrying the resolved `MoveSpecification` together with the list of forgiven SAN deviations.
- `ChessBoard` interface methods renamed to match (`move`, `moveStrict`, `moveLenient`, `movesStrict`, `unmove`).

### Breaking — SAN parser
- `SanValidation` renamed to `StrictSanParser`. Entry-point method `validateSan(String, ChessBoard)` renamed to `parseText(String, ChessBoard)`. Return type changed from `MoveSpecification` to `StrictSanParserValidationResult` (one-field record); to keep the prior shape, append `.moveSpecification()`.

### Migration
For most callers, the change is mechanical:
| Before | After |
|---|---|
| `board.performMove("e4")` | `board.moveStrict("e4")` |
| `board.performMove(moveSpec)` | `board.move(moveSpec)` |
| `board.performMoves("e4", "e5")` | `board.movesStrict("e4", "e5")` |
| `board.performMoveLenient("nf3")` | `board.moveLenient("nf3")` |
| `board.unperformMove()` | `board.unmove()` |
| `SanValidation.validateSan(san, board)` | `StrictSanParser.parseText(san, board).moveSpecification()` |

### Notable
- New lenient SAN pipeline (`com.dlb.chess.san.lenient.LenientSanParser`) with a 21-code forgiven-item taxonomy: castling-with-zero, UCI / long-algebraic notation, explicit pawn letter, missing promotion equals, missing / spurious capture marker, six check / checkmate suffix mismatches, three over-specification cases, non-standard rank disambiguation, and four case-variation codes. Every accepted deviation surfaces as a typed `ForgivenItem` with the original token and the canonical-SAN equivalent — consumers can silently accept or warn. See `specification.md` §3.3.1.
- `LenientPgnParser` now wires the lenient SAN pipeline into the movetext path. `LenientPgnParserValidationResult` gains `pgnFile` and `sanForgivenItems` fields; `validateText` is now the rich entry point that returns the parsed file alongside the validation status.
- `LenientSanParserValidationException` carries `GameStatus` so the `GAME_ALREADY_ENDED` propagation works end-to-end through the PGN layer.
- PGN tokenizer recognises `0-0` / `0-0-0` as castling SAN tokens rather than (invalid) termination markers.
- README "Lenient PGN parser" section documents the SAN tolerances with a worked example.

### Internal
- Strict SAN pipeline reused unchanged for chess validation; the lenient layer is a thin input-shape transformer plus a strict-replay-with-recovery loop. Deliberately not recovered: mixed `0-O` castling, pawn `SPURIOUS_CAPTURE_MARKER` (no clean string mutation; recovery would silently swap the user's intended pawn), and game-already-ended (top-of-pipeline guard, identical to strict).

## [3.3.0] - 2026-05-09

Cleanup follow-through release. Documentation, naming, packaging, and design-consistency polish across the library.

### Breaking
- Package `com.dlb.chess.internationalization` renamed to `com.dlb.chess.messages`.
- Type prefix `Yawn*` renamed to `NoProgress*` (`YawnHalfMove` → `NoProgressHalfMove`, etc.) — matches FIDE / chess-community terminology.
- `ChessBoard.isGameEnd()` / `isGameDraw()` removed (were unused; carried a contested CHA-opt-in semantics).
- `InvalidMoveException` now extends `UsageException` (was extending `RuntimeException` directly).
- `Board.calculateLegalMove(...)` narrowed to package-private (was public-static; carried a contract no external caller could uphold).
- `Board.createPositionAfterMove(...)` moved to `StaticPositionUtility` (pure transformation, doesn't belong on the game class).

### Notable
- New `Reporter.calculateReportText(...)` returns the report as a string — for non-CLI consumers (web responses, file writes, GUIs).
- `log4j-core` dropped from runtime dependencies; `log4j2.xml` no longer ships in the JAR. Consumers no longer inherit the library's logging backend or configuration.
- `FileUtility.writeFile(...)` now propagates `IOException` (was silently swallowing).
- Javadoc on the public API; `mvn package` now produces a `-javadoc.jar`.
- JAR manifest carries `Implementation-Title` / `-Version` / `-Vendor`.
- Thread-safety contract documented in `specification.md` §2.4.
- New `CHANGELOG.md` (this file) and `CONTRIBUTING.md`.

### Internal
- All DGT-derived paid-work content removed.
- Test fixture tree reorganized (`pgn/cua` → `pgn/cha`, lichess subtree given hierarchical layout).
- `ConfigurationConstants.LOCALE` switched from `Locale.US` to `Locale.ROOT`.
- Test-only `GameStatusAnalysis`, `PROJECT_ROOT_FOLDER_PATH` relocated out of `src/main`.
- Many smaller items — see `tasks.md` "Current release — cleanup follow-through" for the per-task breakdown.
