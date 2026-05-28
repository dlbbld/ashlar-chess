# Changelog

Releases from 3.3 onward. Earlier history is in git tags only.

## [Unreleased]

## [16.1.0] - 2026-05-28

Test-scaffolding cleanup. Removes one-shot and superseded test-data generators and dormant external-library cross-validation harnesses from the test tree. No `src/main` change; no API, behaviour, or output change.

### Internal

- Removed superseded test generators: `GeneratePythonTestCases` (replaced by the committed-JSONL python-chess oracle in 12.2.0), `GenerateScalaChessTestCases`, `GenerateAmbronaHelpMateTestCases`, `GenerateChaTestCases`, `GenerateChaLichessReport`, `GenerateLibraryCarlosInsufficientMaterialTestCases`, `GeneratePgnInformationUtility`, `GeneratePiecePositions`.
- Removed dormant single-use cross-validation scaffolding: the `com.dlb.chess.test.scalachess` and `com.dlb.chess.test.chessbase` packages and the `com.dlb.chess.test.unwinnability.lichess` subtree (FEN and PGN check harnesses).
- `TestUciMoveUtility` no longer depends on the removed `GenerateScalaChessTestCases`; its ScalaChess UCI-encoding helper is inlined. The test still runs unchanged.
- `PgnTest`: removed two stale ScalaChess-related comments; no enum values changed.
- Docs: `setup.md` adds an optional python-chess oracle-regeneration section; `tasks.md` marks the 12.2.0 python-chess cross-validation release complete; `workflows.md` reference corrected.

## [16.0.0] - 2026-05-27

The threefold and 50-move report becomes a first-class object model with print classes as a derived view. Per-move FIDE 9.2 / 9.3 claim API. New `GameEndFacts` snapshot pairs the raw condition booleans with the precedence-projected `Outcome`. `Outcome` is now never null. `HalfMove` is no longer stored on `Board`.

### Notable

- Object-level report model for threefold and 50-move: `ThreefoldClaimAheadReport`, `ThreefoldExistingReport`, `FiftyMoveClaimAheadReport`, `FiftyMoveSequenceReport` plus their builders. `Reporter.printReport` derives its sections from these objects.
- Per-move claim API on `Board`: `canClaimFiftyMoveRuleFor(MoveSpecification|String)`, `canClaimThreefoldRepetitionRuleFor(MoveSpecification|String)`, `canClaimDrawFor(MoveSpecification)`. Throw on invalid input. SAN overloads use the lenient parser.
- New records `ClaimableMove` and `ClaimRights`; `Board.calculateFiftyMoveRuleClaimRights()` and `Board.calculateThreefoldRepetitionRuleClaimRights()` return the list of claimable moves paired with canonical SAN.
- New record `GameEndFacts`; `Board.calculateGameEndFacts()` returns the full snapshot, `Board.isGameEnd()` is the convenience boolean. Condition predicates report raw facts independent of precedence; `Outcome` is the projection.
- `Termination.NONE` added; `Outcome.ONGOING` singleton; `BasicChessUtility.calculateOutcome` returns non-null. `Outcome.winner` is `Side` (with `Side.NONE` for non-checkmate) instead of `@Nullable Side`.
- 50-move report output: per-ply claim-ahead boundary entries with placeholder rendering, missed-opportunity filter, sequences section mirrors the threefold "and beyond" section.
- `Board.halfMoveList` removed from stored state. `getHalfMoveList()` is now a derived `O(plies)` view; new `getLastHalfMove()` for `O(1)` access. `HalfMove` the type is retained as a compatibility row.

### Behavioral

- `Board.isFiftyMove()` / `isSeventyFiveMove()`: report the raw clock-threshold condition. Previously returned `false` at game-end positions where a higher-precedence termination also held; now return `true`. `Outcome` precedence is unchanged.
- Per-move claim predicates (new in 16.0.0) throw `IllegalArgumentException` for a `MoveSpecification` not in the legal-moves set and `LenientSanParserValidationException` for a SAN the lenient parser cannot resolve.
- python-chess oracle harness skips the `isFiftyMove` / `isSeventyFiveMove` / `isFivefoldRepetition` / `canClaim*Rule` comparisons at positions where checkmate, stalemate, or insufficient material holds — the suppressed-predicate divergence is documented and contained.
- Threefold claim-ahead lines sorted lexicographically by displayed half-move-count sequence; entries for the same dynamic position cluster together by length.

### Breaking

- `Termination` enum gains `NONE`. Exhaustive `switch` over `Termination` in downstream code needs a `NONE` arm.
- `Outcome.winner`: `@Nullable Side` → `Side`. Construction sites passing `null` for non-checkmate outcomes must use `Side.NONE`.
- `BasicChessUtility.calculateOutcome(Board)`: return type changes from `@Nullable Outcome` to `Outcome`. Ongoing positions return `Outcome.ONGOING` (`termination == Termination.NONE`); the `outcome == null` branch is no longer reachable.
- `Board.isFiftyMove()` / `isSeventyFiveMove()` behaviour change at game-end positions (see Behavioral above).
- Per-move claim predicates throw on invalid input rather than silently returning `false`.
- `Reporter.printReport()` output format reshaped: the old single-line "Fifty moves without capture and pawn move: Yes/No" is replaced by two sections ("Valid fifty-move claims ahead", "Fifty moves and beyond"); threefold line order changed; section titles updated. Consumers parsing the printed output need to adapt.

## [15.0.0] - 2026-05-26

The **termination-is-information release**. The move pipeline no longer consults any game-end predicate: checkmate, stalemate, mutual insufficient material, fivefold repetition, the 75-move rule, and analyzer-driven dead positions are all surfaced as queryable artifacts the caller polls to decide whether to adjudicate. At checkmate and stalemate the natural barrier is the empty legal-move set (a move attempt fails through ordinary legality); at the other terminations legal moves still exist and the pipeline accepts them. The `GameStatus` enum is replaced by a structured `Outcome` record carrying `Termination` and the winner — python-chess parity at the API boundary, the cross-validation oracle that's been the project's reference since 12.2.0. The motivation: clean-chess corpus and tooling already needed to replay historical PGN that continues a move or two past an automatic termination, the python-chess oracle returns terminations as information rather than enforcement, and the previous in-pipeline gate forced ugly workarounds in both the oracle harness and the lenient parser. Dropping the gate eliminates the impedance mismatch.

### Notable

- **Move pipeline ungated at all FIDE-automatic terminations.** Neither `ValidateNewMove.validateNewMove` (MoveSpecification pipeline) nor `StrictSanParser.parseText` (SAN pipeline) consults any termination predicate. The `validateGameNotEnded` pre-check is gone from both. At checkmate and stalemate the legal-move generator returns empty, so any attempted move fails through ordinary legality (own-piece occupation, king-into-check, etc.); at mutual insufficient material, fivefold, and 75-move the pipeline accepts further moves and the caller decides whether to adjudicate. Symmetric extension of 13.0.0 (which had already ungated fivefold and 75-move) and 14.0.0 (which had already ungated analyzer-driven dead positions); 15.0.0 closes the gap on the remaining three.
- **`GameStatus` enum replaced by `Outcome` record + `Termination` enum.** `BasicChessUtility.calculateGameStatus(Board) → GameStatus` becomes `BasicChessUtility.calculateOutcome(Board) → @Nullable Outcome`. The record carries `(Termination termination, @Nullable Side winner)`; a compact constructor enforces the invariant that `winner` is non-null iff `termination == CHECKMATE`. python-chess `chess.Outcome(termination, winner)` parity. `Outcome` lives in `com.dlb.chess.common.model`; `Termination` in `com.dlb.chess.common.enums`.
- **`Termination` enum has five values.** `CHECKMATE`, `INSUFFICIENT_MATERIAL`, `STALEMATE`, `SEVENTY_FIVE_MOVES`, `FIVEFOLD_REPETITION`. The `DEAD_POSITION_UNWINNABLE_QUICK` and single-side insufficient-material values from the old `GameStatus` are intentionally not represented: the analyzer-driven verdict remains accessible via `Board.isDeadPositionQuick()` / `isDeadPositionFull()` (invoking it from `calculateOutcome` would silently make every status query expensive), and single-side insufficient material is a diagnostic position state queryable via `Board.isInsufficientMaterial(Side)`, not an automatic termination.
- **`calculateOutcome` precedence order reflects python-chess.** When two or more terminations apply at the same position, the precedence is `CHECKMATE > INSUFFICIENT_MATERIAL > STALEMATE > SEVENTY_FIVE_MOVES > FIVEFOLD_REPETITION`. The two precedence differences from the previous clean-chess `calculateGameStatus` ordering are: mutual insufficient material now beats stalemate (a KBvK stalemate, for instance, reports `INSUFFICIENT_MATERIAL` rather than `STALEMATE`), and 75-move now beats fivefold when both apply.
- **`GAME_ALREADY_ENDED` machinery removed.** `MoveCheck.GAME_ALREADY_ENDED` and `SanValidationProblem.GAME_ALREADY_ENDED` enum values are deleted. The `@Nullable GameStatus` payload, third-argument constructor, and `getGameStatus()` getter are removed from `InvalidMoveException`, `SanValidationException`, `StrictPgnParserValidationException`, `LenientPgnParserValidationException`, and `LenientSanParserValidationException`. `GameStatus.isAutomaticTermination()` is gone with the enum. `validation.san.gameAlreadyEnded` is removed from `messages.properties`. The three downstream propagation hops (`StrictPgnParser`, `LenientPgnParser`, `LenientSanParser`) no longer pass a `GameStatus` along the chain.
- **`Reporter` slimmed to a print-only surface.** The `Report` record carrying analytical data is removed; `Reporter` retains only `printReport(Board)` / `printReport(String pgn)` / `printReport(Path, String)` — human-readable summary to stdout. Programmatic consumers that previously inspected the `Report` fields now call the dedicated `Board` predicates and the analysis helpers in `com.dlb.chess.report` directly. Internal `HalfMove` model slimmed in the same pass. Pre-existing repetition report bug fixed (the initial position is now included in repetition-list output where it should have been all along).
- **Test corpus shape: `PgnTestCase` reduced to `(pgnName, finalFen)` and renamed `PgnFen`.** The six non-FEN snapshot fields that existed to be compared against a report generated from the same FEN were self-referential regression — kept only the PGN filename and the cached final-position FEN. ~1400 catalog entries rewritten mechanically. `PgnTestCaseCatalog` and `PgnTestCaseList` type names kept (catalogs / lists *of* `PgnFen`). `BASIC_INSUFFICIENT_MATERIAL_*` registration split into four dedicated methods so the runtime `filterInsufficientMaterial` no longer reads the field.
- **`Board.isFiftyMove()` / `isSeventyFiveMove()` aligned with python-chess and FIDE.** Both predicates now require `halfmove_clock >= N AND !getLegalMoves().isEmpty()`, previously pure threshold checks. At a checkmate or stalemate position where the clock is past the threshold, the predicates now return `false` (the rule cannot fire — the game has ended by a higher-precedence termination). Matches python-chess `is_fifty_moves()` / `is_seventyfive_moves()` and the FIDE rule reading. `calculateOutcome` is unaffected (its ordering puts checkmate / stalemate above these). Side effect on the claim path: `canClaimFiftyMoveRule()` no longer returns `true` at checkmate-with-clock-past-threshold — consistent with FIDE 9.3 (no draw to claim once the game is over).
- **`canClaimFiftyMoveRuleWithOwnMove()` follows the strict FIDE 9.3 reading — deliberate divergence from python-chess at one corner case.** FIDE 9.3 frames the 50-move claim as announced *before* the move is played; the 50 moves are about history, and the candidate move's outcome is incidental to whether the no-progress condition has been met. clean-chess returns `true` at halfmove clock ≥ 99 if any legal move is non-pawn and non-capture — even if that move would deliver checkmate. python-chess (`can_claim_fifty_moves`, per commit `1064bf59`) pushes the candidate and re-checks `is_fifty_moves` on the post-position; the `any(legal_moves)` guard inside `is_fifty_moves` is deliberately there for the precedence stack when checking the *current* position (the maintainer's tests pin "once checkmated, it is too late to claim" and "a stalemate is a draw"), and transitively rejects candidate moves that themselves deliver mate. That rejection is collateral from the code reuse, not from a separate documented intent — no python-chess test or docstring addresses the candidate-move-is-mate case as distinct from the current-position case. clean-chess takes the strict FIDE 9.3 reading at this edge. No corpus fixture surfaces the disagreement in practice; pinned in `TestBoardClaimWithOwnMove#canClaimFiftyMoveRuleWithOwnMoveTrueEvenWhenOnlyNonZeroingMoveIsMate`. Cross-library context tracked upstream at [niklasf/python-chess#1188](https://github.com/niklasf/python-chess/issues/1188).
- **Python-chess oracle skip guard removed.** `TestPgnImportAgainstPythonChessOracle` had a workaround clause that skipped `isFiftyMove` / `isSeventyFiveMove` / `canClaimThreefoldRepetitionRule` / `canClaimFiftyMoveRule` assertions at game-end positions, originally because `Board.move()` threw inside the canClaim path at auto-terminated positions and because the threshold predicates diverged from python-chess. Both issues are now resolved (the pipeline ungating closes the throw, the predicate alignment closes the divergence). The four assertions now run at every one of ~35,000 plies in the import corpus.

### Behavioral

- **Outcome precedence change visible at simultaneous-condition positions.** A KBvK or KNvK stalemate position previously reported `STALEMATE` (the higher-precedence value in the old ordering); now it reports `INSUFFICIENT_MATERIAL`. A position simultaneously at the 150-halfmove threshold and at five identical positions previously reported `FIVE_FOLD_REPETITION_RULE`; now reports `SEVENTY_FIVE_MOVES`. Consumers that distinguished cases by the precise enum value will see different values at these tie positions; consumers that branch on "is it terminated, draw or win, and who won" via `Outcome` will see the same conclusion.
- **Validation rejection reasons at checkmate / stalemate change shape.** Pre-15.0.0: any move attempted on a board at checkmate, stalemate, or mutual insufficient material was rejected with `InvalidMoveException(MoveCheck.GAME_ALREADY_ENDED, gameStatus)` / `SanValidationException(SanValidationProblem.GAME_ALREADY_ENDED, message, gameStatus)`. Post-15.0.0: that top-of-pipeline gate is gone. At checkmate and stalemate the attempted move now fails with the specific `MoveCheck` / `SanValidationProblem` that describes why the particular move is illegal (typically `MOVEMENT_TO_SQUARE_OCCUPIED_BY_OWN_PIECE`, `KING_MOVES_TO_ATTACKED_EMPTY_SQUARE`, or `ALL_BUT_KING_KING_LEFT_IN_CHECK`, depending on the move). At mutual insufficient material the move now succeeds.
- **`isFiftyMove` / `isSeventyFiveMove` semantic change at game-end positions.** A board at checkmate or stalemate where the halfmove clock has reached the threshold previously returned `true` for these predicates; now returns `false`. Consumers that polled the pure threshold without other context should query `Board.getHalfMoveClock()` directly instead. The change is invisible to consumers that combined the predicate with a "game ongoing" check (`!isCheckmate() && !isStalemate()` first), and invisible to consumers that consume `Outcome` (precedence already covers it).
- **`PgnCreate.calculateResultTagValue`** unchanged in observable behavior. Internally now derives the winning side from `outcome.winner()` rather than inverting `board.getHavingMove()` — surfaces the structural benefit of the new type at the consumer.

### Breaking

- `com.dlb.chess.common.enums.GameStatus` deleted. Replaced by `com.dlb.chess.common.enums.Termination` (5 values, no `ONGOING` — `calculateOutcome` returns `null` for ongoing positions) and `com.dlb.chess.common.model.Outcome` (record).
- `BasicChessUtility.calculateGameStatus(Board) → GameStatus` deleted. Replaced by `BasicChessUtility.calculateOutcome(Board) → @Nullable Outcome`. Migration:
  ```java
  // before
  GameStatus status = BasicChessUtility.calculateGameStatus(board);
  switch (status) { case CHECKMATE -> ...; case ONGOING -> ...; case INSUFFICIENT_MATERIAL_WHITE_ONLY -> ...; ... }

  // after
  Outcome outcome = BasicChessUtility.calculateOutcome(board);
  if (outcome == null) {
    // game ongoing; query board.isInsufficientMaterial(side) for the single-side diagnostic case
  } else {
    switch (outcome.termination()) { case CHECKMATE -> ...; case INSUFFICIENT_MATERIAL -> ...; ... }
  }
  ```
- `MoveCheck.GAME_ALREADY_ENDED` enum value removed. Consumers that switched on this case can delete the branch; the case is no longer reachable.
- `SanValidationProblem.GAME_ALREADY_ENDED` enum value removed. Same.
- `InvalidMoveException(String, MoveCheck, GameStatus)` three-argument constructor removed; the corresponding `getGameStatus()` getter is gone. Equivalent constructors removed from `SanValidationException`, `StrictPgnParserValidationException`, `LenientPgnParserValidationException`, and `LenientSanParserValidationException`.
- `GameStatus.isAutomaticTermination()` method removed (with the enum).
- `validation.san.gameAlreadyEnded` message-bundle key removed from `messages.properties`. Custom message bundles can drop the entry.
- `com.dlb.chess.report.Report` record deleted. `Reporter` is now print-only. Programmatic consumers should query the underlying analyses directly (`RepetitionUtility`, `NoProgressMoveUtility`, `ThreefoldClaimAheadUtility`, the `Board.is*` predicates, `BasicChessUtility.calculateOutcome`).
- `com.dlb.chess.test.model.PgnTestCase` renamed to `com.dlb.chess.test.model.PgnFen` and reduced to two fields `(pgnName, finalFen)`. Test-tree only; consumers of the production API are unaffected.

## [14.0.0] - 2026-05-24

The **dead-position-query release**. Analyzer-driven dead-position detection (`DEAD_POSITION_UNWINNABLE_QUICK`) no
longer runs automatically during `Board` construction or after every move, and no longer blocks further play. Quick and
full unwinnability/dead-position checks remain available as request-based analysis APIs; callers decide whether to
adjudicate a draw.

### Notable

- **Removed eager quick-unwinnability state from `Board`.** The per-ply `isDeadPositionUnwinnableQuick` cache and the
  `detectDeadPositionUnwinnable` configuration flag are gone. `Board.isDeadPosition()` now evaluates the cheap
  structural insufficient-material predicate first and invokes the quick dead-position query only when called.
- **`GameStatus.isAutomaticTermination()` semantics change.** `DEAD_POSITION_UNWINNABLE_QUICK` now returns `false`.
  The move-blocking terminations are `CHECKMATE`, `STALEMATE`, and `DEAD_POSITION_INSUFFICIENT_MATERIAL`.
- **`BasicChessUtility.calculateGameStatus(...)` no longer invokes the analyzer.** The method now keeps to cheap
  predicates and never returns `DEAD_POSITION_UNWINNABLE_QUICK`; the enum value remains as a status word callers can
  map onto. Callers that want the analyzer-driven verdict invoke `Board.isDeadPositionQuick()` /
  `Board.isDeadPositionFull()` directly. Avoids the footgun of a status query that silently runs the analyzer.

### Breaking

- Removed the boolean `Board` constructor overloads that controlled quick dead-position auto-detection:
  `Board(boolean)`, `Board(Fen, boolean)`, and `Board(String, boolean)`.
- Removed `Board.copyCurrentPositionWithoutHistory(boolean)`; use `Board.copyCurrentPositionWithoutHistory()`.
- Removed the stateful `Board.isDeadPositionUnwinnableQuick()` accessor. Use `Board.isDeadPositionQuick() ==
  DeadPositionQuick.DEAD_POSITION` directly; `BasicChessUtility.calculateGameStatus(...)` no longer surfaces this verdict.
- `BasicChessUtility.calculateGameStatus(...)` no longer returns `GameStatus.DEAD_POSITION_UNWINNABLE_QUICK`.
  Consumers that need the verdict call `Board.isDeadPositionQuick()` themselves; the enum value remains as a status
  word and downstream `switch` expressions over `GameStatus` should keep the case present for exhaustiveness.
- Calling `Board.move(...)` / `StrictSanParser.parseText(...)` on a board whose quick dead-position query reports
  `DEAD_POSITION` no longer throws `GAME_ALREADY_ENDED`.

## [13.0.0] - 2026-05-24

The **reallow-play-beyond release**. Fivefold repetition (FIDE 9.6.1) and the 75-move rule (FIDE 9.6.2) are no longer enforced as automatic terminations at the move pipeline — both remain as queryable predicates on `Board`. The position itself is not necessarily drawn at the threshold (mating material can still be present, pawn moves and captures can still happen, a later checkmate can still occur); the library is permissive here for corpus and tooling compatibility — historical PGN databases routinely contain games whose recorded play continues a move or two past the threshold — and the caller decides whether to adjudicate the draw. The FEN parser's halfmove-clock-≤-150 cap drops alongside as the matching serialized-form change. The motivation surfaced during the 12.2.0 python-chess cross-validation work: python-chess returns the rules as game-over outcomes while still allowing `push_uci()` / `push_san()` to continue, and clean-chess's eager auto-termination forced two skip guards in the oracle suite plus a crash in `canClaimThreefoldRepetitionRule()` / `canClaimFiftyMoveRule()` on auto-terminated positions. Dropping the auto-termination unblocks both.

### Notable

- **`GameStatus.isAutomaticTermination()` semantics change.** Returns `false` for `FIVE_FOLD_REPETITION_RULE` and `SEVENTY_FIVE_MOVE_RULE` (previously `true`). The four surviving enforced terminations are `CHECKMATE`, `STALEMATE`, `DEAD_POSITION_INSUFFICIENT_MATERIAL`, `DEAD_POSITION_UNWINNABLE_QUICK`. The move pipeline (`ValidateNewMove`, `StrictSanParser`, lenient mirrors) accepts further moves at and past the fivefold / 75-move thresholds. `Board.isFivefoldRepetition()` / `Board.isSeventyFiveMove()` remain queryable predicates with unchanged signatures.
- **`BasicChessUtility.calculateGameStatus(...)` precedence reorder.** Hard blockers (the four enforced terminations) now take precedence over fivefold / 75-move when both apply to the same position. Without this, a position that is simultaneously dead-position-insufficient-material AND 75-move (e.g. a KvK ending with halfmove clock at 150) would resolve as 75-move, and the no-longer-blocking status would let the move pipeline accept play through the dead position. New order: CHECKMATE → STALEMATE → DEAD_POSITION_INSUFFICIENT_MATERIAL → DEAD_POSITION_UNWINNABLE_QUICK → FIVE_FOLD_REPETITION_RULE → SEVENTY_FIVE_MOVE_RULE → INSUFFICIENT_MATERIAL_*_ONLY → ONGOING.
- **FEN halfmove-clock-above-150 rejection dropped.** `FenParserAdvanced` no longer rejects FENs with halfmove clock above 150. The corresponding `FenAdvancedValidationProblem.INVALID_HALF_MOVE_CLOCK_BEYOND_SEVENTY_FIVE_MOVE_RULE` enum value is removed. The halfmove-clock-vs-fullmove-number consistency check (`INVALID_HALF_MOVE_CLOCK_TOO_BIG_RELATIVE_TO_FULL_MOVE_NUMBER`) and the EP-target-square-vs-clock check are unchanged.
- **All 99 legacy fixtures reactivated into the regular corpus; `pgnParser/legacy/common/beyond/` tree removed.** 98 PGN files move back into the regular `src/test/resources/pgn/` tree at their pre-feature locations, picking up `PgnFen` catalog entries with full metadata generated via `Reporter.calculateReport`. Six new `PgnTest` enum entries (`BASIC_FROM_FEN_NO_PROGRESS_BLACK` / `_WHITE`, `FIVEFOLD_BEYOND`, `SEVENTY_FIVE_BEYOND`, `LONG`, `LONGEST_MATE`) cover the new or parallel destinations. The remaining KvK fixture (`02_last_move_added_accidentally_result_draw_one_move_in_KvK.pgn`) had its trailing play-beyond move stripped and joins the corpus at `pgn/realGames/lastMoveAddedAccidentally/`. `TestLegacyPgnParsePlaysBeyondAudit` becomes empty and is deleted in the same change.
- **Obsolete play-beyond fixtures + tests removed.** The four `07–10_play_beyond_{fivefold,seventy_five}_*.pgn` fixtures under `pgnParser/common/beyond/` and the corresponding `test07`–`test10` methods in `TestStrictPgnParserBeyondTermination` / `TestLenientPgnParserBeyondTermination` are gone — fivefold / 75-move "one move past threshold" cases are no longer interesting under the new behavior.
- **New positive companion tests.** `TestValidateNewMoveGameEnded.testMoveAcceptedAt{Fivefold,SeventyFiveMove}Threshold()` and `TestSanValidationGameEnded.testSanAcceptedAt{Fivefold,SeventyFiveMove}Threshold()` pin down that the predicates fire at the threshold and the move pipeline accepts the next move.
- **`specification.md` §3.1 introduces a third "queryable" termination mode.** Sits between "automatic" (no further moves accepted) and "claimable" (caller-decided). Fivefold and 75-move move into the queryable bucket with the rationale paragraph spelling out why.
- **New `workflows.md` documenting recurring developer procedures.** Covers adding a new PGN test fixture (with the `GenerateTestCaseForPgn` / `GenerateTestCaseForPgnFolder` helpers and full `PgnTestCaseCatalog` integration), running test subsets, and cutting a release. `CONTRIBUTING.md` links to it.

### Breaking

- `GameStatus.isAutomaticTermination()` behavior change for `FIVE_FOLD_REPETITION_RULE` and `SEVENTY_FIVE_MOVE_RULE` — consumers that relied on these returning `true` to short-circuit further move logic will need to call the explicit predicates (`Board.isFivefoldRepetition()` / `Board.isSeventyFiveMove()`) themselves.
- `FenAdvancedValidationProblem.INVALID_HALF_MOVE_CLOCK_BEYOND_SEVENTY_FIVE_MOVE_RULE` enum value removed.
- Calling `Board.move(...)` / `StrictSanParser.parseText(...)` on a board where `isFivefoldRepetition()` or `isSeventyFiveMove()` is true no longer throws `InvalidMoveException` / `SanValidationException` with `GAME_ALREADY_ENDED`. Consumers that want to surface those terminations must check the predicates themselves.

## [12.2.0] - 2026-05-24

The **python-chess primary cross-validation release**. Reactivates the dormant python-chess test path, makes python-chess the main move-test reference, and expands PGN import / export / move-generation coverage against it. ChessLib (`LibraryCarlosBoard`) is retained as a second witness — two independent oracles is the right shape — but python-chess is now the primary reference because it can import PGN from a non-initial position via the `FEN` / `SetUp` tags, which ChessLib cannot. The release ships the oracle infrastructure and the cross-validation tests; the findings surfaced during the work motivate the next release rather than being acted on here.

### Notable

- **Generation-based oracle pattern.** A Python script using python-chess generates expected outputs into a committed JSONL file at fixture-add / regeneration time; `mvn test` reads the file and never invokes Python. Two oracle file structures: PGN-import oracle under `src/test/resources/oracle/python-chess/<folderPart>.jsonl` (mirrors the `src/test/resources/pgn/` tree), and move-generation oracle under `src/test/resources/oracle/python-chess/move-gen/<folderPart>.jsonl` (per-position legal-UCI sets, set equality). Generator scripts under `src/test/python/`; python-chess 1.11.2 pinned via `src/test/python/requirements.txt` for byte-stable regeneration.
- **PGN-import oracle — 51 buckets, 647 fixtures, 35,001 plies.** Covers PARSER_FROM_FEN, all BASIC_*, and the curated real-games / Wikipedia / WCC buckets. Per ply, asserts that clean-chess and python-chess agree on `fenAfter`, `halfmoveClock`, `fullmoveNumber`, `isCheck`, `isCheckmate`, `isStalemate`, `isInsufficientMaterial` (combined + per-side), `is_repetition(2)` / `(3)` / `(4)`, `isFivefoldRepetition`, canonical SAN, canonical LAN. New `TestPgnImportAgainstPythonChessOracle`.
- **Move-generation oracle — 22 buckets, 300 fixtures, 3537 positions.** Separate oracle file structure. Asserts sorted-UCI set equality between clean-chess `Board.getLegalMovesUci()` and python-chess `board.legal_moves` at every visited position. Bucket scope is intentionally narrower than the import oracle — long-game corpora omitted because move generation is already heavily exercised internally against the relocated `StaticPosition` oracle. New `TestLegalMovesAgainstPythonChessOracle`.
- **PGN-export round-trip oracle — 647 fixtures × 2 modes = 1294 round-trips.** For every fixture: clean-chess parses the original, emits in each `WriteMode` (`SEMANTIC` and `ARCHIVAL`), re-parses the emitted string, and asserts that the re-played UCI sequence + `startFen` + `finalFen` still match the python-chess view of the source. Transitive semantic equivalence — python-chess parsing the emitted artifact would see the same content as it sees on the source. No new oracle data needed; reuses the import oracle JSONL. New `TestPgnExportRoundTripAgainstPythonChessOracle`.
- **Focused insufficient-material oracle — BOTH=13 / ONLY_WHITE=28 / ONLY_BLACK=28 / NONE=15, 84 fixtures.** Position-only (no PGN replay): constructs `new Board(record.finalFen(), false)` and asserts the three insufficient-material predicates against the python-chess values for the same final position. Isolated from the broader per-ply sweep so the predicate subject is visible in the test name and the summary line. New `TestInsufficientMaterialAgainstPythonChessOracle`. Also refactored `TestInsufficientMaterial` to operate on the final position only (via the existing `PgnFen.finalPosition()` helper) — the play-through-then-check pattern was a category error that conflated "PGN replay works" with "material predicate is correct on this position."

### Behavioral

- **`Board.getLan()` output format fixed to canonical LAN.** Previous output was UCI-style (`Ng1f3`, `e2e4`) — missing the non-capture dash separator. New output follows the grammar:
  ```
  <LAN piece move> ::= <piece symbol><from square>('-'|'x')<to square>
  <LAN pawn move>  ::= <from square>('-'|'x')<to square>['=' <promoted to>]
  <LAN castle>     ::= 'O-O' | 'O-O-O'
  ```
  Plus the terminal marker (`+` / `#`) appended to every move shape including castling. Matches python-chess `board.lan(move)` on the full 35,001-ply corpus. Captures (`e2xe4`, `Ng1xf3`, `h5xg6`) and castling (`O-O` / `O-O-O`) were already correct; the fix affects only non-capture move emission and the missing terminal-marker on castling that delivers check / mate.
  - Method name, signature, and call sites unchanged.
  - 69 pinned LAN string assertions across `TestLanCalculation`, `TestPerformMoveMainlyStaticPositionState`, `TestPerformMoveSeveralStates` updated to the new format.
  - Existing code that consumes `Board.getLan()` and parses it back to squares — if any consumer was relying on the broken format — will need an update. The library has no internal LAN-format-sensitive consumers; the change is purely external-facing.
- **`Board.getFullMoveNumber()` renamed to follow the FEN convention.** Previous behaviour returned the "just-played move's number"; new behaviour returns the FEN-convention "number of the move about to be played" — matching what `Board.getFen()` writes and what python-chess `board.fullmove_number` returns. The previous backward-looking sense lives on as `Board.getLastPlayedFullMoveNumber()` (throws on the initial position; calling it before any move was played is undefined). Eleven src/main + test call sites updated.

### Findings surfaced (motivate the next release, not acted on here)

The cross-validation work surfaced two real semantic disagreements between clean-chess and python-chess. Both are documented in the affected test code (with explicit skip guards so the suite passes), and both feed directly into the next release's scope:

- **`isFiftyMove()` / `isSeventyFiveMove()` semantics at game-end positions.** python-chess requires `halfmove_clock >= N AND ≥1 legal move`; clean-chess returns the pure threshold. At checkmate / stalemate positions with the clock past the threshold, python-chess says `false`, clean-chess says `true`. Both internally consistent; the cleaner reading is python-chess's "can the rule actually be invoked?" framing.
- **`canClaimThreefoldRepetitionRule()` / `canClaimFiftyMoveRule()` crash on auto-terminated games.** Both methods simulate legal moves via `Board.move()` to test for claim opportunities; on positions where the game has auto-terminated (fivefold, 75-move), `Board.move()` throws `InvalidMoveException`. python-chess returns a clean boolean in the same situation. Same root cause: clean-chess's eager auto-termination is what makes these methods partial.

Both findings argue for dropping clean-chess's auto-termination on dead-position / fivefold / 75-move (already captured in `tasks.md` as the current release plus the tentative Phase 2). Confirming Phase 2 — fivefold and 75-move auto-termination shouldn't be at the same level as checkmate / stalemate — is the lesson learned that surfaced from the oracle work itself.

### Internal

- **Test contract cleanup.** Stale TODO-style comments in `Board.getFullMoveNumber()` and a commented-out parity assertion in `CommonTestUtility` (cryptic "// in super" note) removed.
- **`requirements.txt` + script docstring schema source-of-truth.** Each Python generator's docstring carries the full JSONL schema (Record + Move sub-schema); Java records and tests point to the docstring rather than duplicating the schema.
- **Two record types in `com.dlb.chess.test.oracle.python`** — `OracleRecord` / `OracleMove` for PGN-import oracle, `LegalMovesRecord` / `LegalMovesPly` for move-gen oracle. One tiny JSONL reader (`OracleJsonlReader`) is the schema-flexible parser; a sibling reader `LegalMovesJsonlReader` reuses it via a package-visible `parseLineToObject` hook.

## [12.1.0] - 2026-05-23

The **helpmate hot-path release**. 12.0.0 made `findHelpMate` cheaper per node by collapsing the search wrapper down to `HelpmateSearchBoard`; this release closes the per-move allocation gap inside that wrapper. `HelpmateSearchBoard` now owns mutable 12 piece bitboards and a growable, pre-allocated undo stack, fills a per-depth reusable legal-move buffer through a new sink-based generator overload on the shared bitboard layer, and consults the transposition cache via an exact structural key that mirrors `DynamicPosition` equality without the nested `BitboardPosition` record allocation. The mutable state and the buffer / key are scoped to `com.dlb.chess.unwinnability`; `BitboardPosition` stays an immutable record.

### Performance

- The helpmate search's `move(spec) + unmove()` cycle — make + unmake + post-make `refreshDerivedState` (sink-based legal-move generation, `isInCheck`, `isCheckmate` / `isStalemate` flags) — measured within **0.96× – 1.13×** of ChessLib's legal-move generation on the surveyed corpora (`MAX_MOVES`, `RANDOM_NO_REPETITION`, `WCC2021`, `CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR`). The comparison is apples-to-oranges in the cycle's favor: ChessLib measures generation only, the cycle additionally applies and reverses the move and refreshes derived state. The 12.1.0 cycle therefore does strictly more work per measurement and still lands at ChessLib parity. New `HelpmateSearchBoardPerformanceSurvey` captures this measurement.
- The Board public path (`Board.getLegalMoves()` via `BitboardLegalMoveFactory.calculateLegalMoves`) is faster as a side effect of Phase C's sink-overload refactor: it now bypasses the inner `TreeSet<MoveSpecification>` that the old `BitboardPosition.legalMoves` allocated. Existing `MoveGenerationPerformanceSurvey` ratios drop from `3.5×–4.0×` ChessLib (12.0.0) to `1.9×–2.8×` ChessLib (12.1.0), depending on the corpus.
- Transposition-cache key construction (`HelpmateSearchKey`) adds 0.013–0.038 μs per cycle — 1–3% overhead.

### Notable

- **Mutable `HelpmateSearchBoard`** (`com.dlb.chess.unwinnability`, package-private). Twelve mutable piece-bitboard `long` fields plus `Side havingMove`, raw + normalized EP target squares, castling rights for each side, and cached derived flags. `move(MoveSpecification)` mutates the bitboards in place; `unmove()` pops the prior snapshot off a growable, pre-allocated `UndoState[]` stack (no per-move record allocation along the search hot path).
- **Per-depth `LegalMoveBuffer`** (`com.dlb.chess.unwinnability`, package-private, extends `AbstractList<LegalMove>`). One buffer per search depth; the parent's buffer at depth N is preserved untouched while recursion fills depth N+1. Eliminates the per-ply `TreeSet<MoveSpecification>` + `TreeSet<LegalMove>` + `ImmutableList<LegalMove>` allocations the prior release paid on every node.
- **Sink-based legal-move generator overloads** on the shared bitboard layer. `BitboardPosition.legalMovesInto(Consumer<MoveSpecification>, Side, long)` and `BitboardLegalMoveFactory.calculateLegalMovesInto(Consumer<LegalMove>, BitboardPosition, Side, CastlingRight, long)` emit moves directly to a caller-supplied sink in the generator's natural traversal order. The existing `legalMoves` / `calculateLegalMoves` methods stay as `TreeSet`-collecting wrappers — Board's public sorted-output contract is preserved unchanged.
- **`HelpmateSearchKey`** (package-private, `com.dlb.chess.unwinnability`) — exact structural transposition-cache key over `havingMove`, the twelve piece bitboards, the normalized EP target, and both sides' castling rights. Equivalent equality semantics to `DynamicPosition` but with the bitboards inlined as record components, so `currentTranspositionKey()` costs exactly one record allocation per cache touch. `FindHelpmateExhaust.transpositionMap` switches from `HashMap<DynamicPosition, Integer>` to `HashMap<HelpmateSearchKey, Integer>`.
- **`BitboardPosition.isInCheckAfterEnPassantCapture(Square, Square, Side)`** — public allocation-free EP king-safety probe lifted from the existing private `epExposesKing` helper. `HelpmateSearchBoard`'s EP-normalization probe and the legal-move generator's pawn-handler both route through it; the previous `bitboardPosition.afterMove(epMoveSpec, mover).isInCheck(mover)` allocation per EP candidate is gone.

### Behavioral

- **Move-iteration order inside `HelpmateSearchBoard` is no longer sorted.** Per the move-order policy adopted for this release, the search board's iteration order is an internal performance choice; the generator's natural traversal order is what the per-depth buffer holds. Public `Board.getLegalMoves()` order remains stable (still `TreeSet`-sorted via the wrapper). The `TestHelpmateSearchBoard` parity test relaxes from ordered-list equality to legal-move **set** equality, with a paired size assertion to catch any duplicate emission.
- **Helpmate search verdicts are mildly order-sensitive in policy.** Because the bounded-depth search (`FindHelpMateInterrupt` depth 9, `FindHelpmateExhaust` node-bounded) explores moves in the generator's order, an internal-order change can shift which mate line is found first or flip a bounded `WINNABLE` / `UNDETERMINED` result. Four previously-recorded accepted-difference entries in the Ambrona oracle comparison TSVs flipped between `WINNABLE` / `POSSIBLY_WINNABLE` / `UNDETERMINED` and were pruned. No unsound `UNWINNABLE` regressions; `UNWINNABLE` and `DEAD_POSITION` outputs are unchanged.

### Internal

- **`TestHelpmateSearchBoard`** extended with four hand-constructed minimal scenarios (check-with-king-only-evasions, double-check-king-only, EP-capture-as-check-response, plus relabeling the prior "mate-adjacent" scenario as stalemate-terminal); all ten scenarios promoted to named `SearchCase` constants with inline doc comments so failures point at fixtures by name.
- **`TestHelpmateSearchBoardMakeUnmakeRoundTrip`** — Phase B's make/unmake gate. For every legal move at every node of a recursive walk across the scenario set, asserts every observable field (piece bitboards, side, raw + normalized EP, castling, legal-move list, cached flags, transposition key) is byte-identical post-unmove.
- **`TestHelpmateSearchKey`** — Phase D's differential test. Lock-step recursive walker assertion that key equality mirrors `DynamicPosition` equality at every node, plus five positive controls (one per distinguishing field: side-to-move, normalized EP, white castling, black castling, piece placement).
- **`TestBitboardPositionIsInCheckAfterEnPassantCapture`** — bit-exact differential test against the allocating `afterMove(...).isInCheck(...)` reference on five EP fixtures (legal / illegal EP for each side, EP resolving a pawn check).
- **`TestAmbronaSemiStaticOracleComparison`** capped at 10 FENs in smoke mode (full 1249 in `-Pfull`). The full comparison was the single largest test in the unwinnability suite (~60s); smoke runs now keep the dev-loop fast without dropping release-gate coverage.

### Breaking

- None. All public API additions are additive (new methods on existing types); no signatures changed.

### Deferred — profile-gated future work

- **Raw-long `BitboardPosition.isInCheck(long whitePawns, …, Side)` overload** to eliminate the remaining per-move `BitboardPosition` snapshot in `refreshDerivedState`. Phase F measurement showed the cycle already at ChessLib parity; the snapshot isn't a bottleneck worth the public-API addition right now.
- **Magic bitboards** behind the existing `BishopAttacks.attacks(int, long)` / `RookAttacks.attacks(int, long)` API. Same reason: sliding attacks aren't the bottleneck on the surveyed corpora. Stays as a profile-gated option for a later release if a future measurement justifies it.

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
- **Test-corpus parse and FEN caches.** `PgnCacheForLenientPgnParserTestCases` and `FenCacheForTestCases` cache the parsed `PgnGame` and parsed `Fen` per corpus fixture for the test-JVM lifetime; `PgnFen.game()` and `finalPosition()` route through them.
- **Empty PGN input rejection.** Both `LenientPgnParser` and `StrictPgnParser` reject zero-byte and whitespace-only input as `FILE_EMPTY` — previously parsed silently to an initial-position game with zero moves.

### Breaking
- `GameStatus.INSUFFICIENT_MATERIAL_BOTH` renamed to `DEAD_POSITION_INSUFFICIENT_MATERIAL`; per-side variants renamed to `INSUFFICIENT_MATERIAL_WHITE_ONLY` / `INSUFFICIENT_MATERIAL_BLACK_ONLY`.
- New `GameStatus.DEAD_POSITION_UNWINNABLE_QUICK`; `GameStatus.isAutomaticTermination()` returns `true` for it. Existing consumers that played past such a position will now receive `InvalidMoveException` / `SanValidationException` with `GAME_ALREADY_ENDED`.
- `Board(String fen)` and `Board()` default to dead-position auto-detection enabled. Tests and bulk PGN replayers that pass through positions the quick analyzer would classify as dead must construct with the explicit `Board(fen, false)` form. Both PGN parsers already do this for you.
- `Board.copyCurrentPositionWithoutHistory(boolean)` replaces the previous detection-suppression API used by the helpmate search.
- Test-side rename `PgnFileTestCase` → `PgnFen`; PGN test paths and class names rename `pgnFile` segment → `pgn`; `position()` / `game(PgnTest)` paths now named `finalPosition()` / `game(PgnTest)` on `PgnFen` (cheap vs expensive choice now visible at every call site).

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
