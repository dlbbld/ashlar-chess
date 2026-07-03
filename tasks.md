# Tasks

Live planning only: current release work, backlog, and obsolete decisions. Shipped release history lives in
**CHANGELOG.md**; recurring procedures live in **workflows.md**. Order within each live section is the source of truth.

---

## The story when releases are done

*ashlar-chess started as a correctness-first reference implementation, built from the FIDE rules without consulting existing libraries. It found correctness bugs in python-chess and ScalaChess along the way. Once the rules were stable, a bitboard backend was added alongside the reference layer and verified bit-exact against it. Production then switched to the bitboard path; the reference layer was relocated into the test tree and remains as the permanent differential-test oracle. Cross-validation against python-chess was reactivated as primary, with `chesslib` retained as a second witness. Only then published to Maven Central.*

---

## 22.0.0 — The FUN22 paper formulation replaces the cha port

Branch `implement-fun22`. The unwinnability engine becomes ashlar's own clean-room implementation of Ambrona's FUN 2022
paper (*A Practical Algorithm for Chess Unwinnability*, Figures 5–13, Lemmas 5/6, Theorem 12), vendored from the
validated `fun22-reference` project (branch `semi-static-v1`: whole-corpus sweeps vs chasolver with 0 contradictions).
The cha-C++-mirroring internals (including the 21.0.0 pawn-intruders correction, which the paper formulation subsumes)
are deleted. The governing document is the clean-room spec (`fun22-spec.md`, brought over from fun22-reference);
**we honor the spec** — algorithm code must be traceable to the paper, never to cha.

Design decisions (agreed 2026-07-03):

- **Public API kept** (final state): `UnwinnableFullAnalyzer`, `UnwinnableQuickAnalyzer`, `DeadPositionAnalyzer`,
  the two analysis records and the four verdict enums stay; `WinnableProof` was later removed together with the
  theorem relocation (see the revised extensions bullet below). Everything package-private is swapped wholesale.
- **Quick becomes three-valued** (paper Figure 10): `UnwinnabilityQuickVerdict` gains `WINNABLE` (fires when the
  bounded DFS meets a mate for the intended winner before the first depth-`D` interrupt). Breaking change, allowed and
  documented in a major release; `Adjudicator`/`DeadPositionAnalyzer` compare against `UNWINNABLE` only and are
  unaffected.
- **Ashlar extensions kept, clearly layered on top of the paper**: the mate line on searched wins
  (`UnwinnabilityFullAnalysis.mateLine()`, mechanical bookkeeping in the Figure 5 search). **Revised 2026-07-03
  (user decision)**: the `BasicHelpmateExistenceTheorem` shortcut is OUT of production - the full analyzer is
  Figure 9 pure - and the theorem lives on as a test-side oracle (agreement tests over the curated
  elementary-material corpus). With it, `WinnableProof` is removed and `UnwinnabilityFullAnalysis` becomes
  `(verdict, mateLine)`: every `WINNABLE` is search-proven with its witnessing line. Side effect: on the 19
  retro-illegal theorem counterexamples the full analyzer now proves `UNWINNABLE` by exhausting the tiny caged
  graphs - full, quick and chasolver agree even on that illegal input, retiring the documented out-of-domain
  disagreement.
- **Full = Figure 9 pure**: semi-static shortcut → iterative deepening (the interim theorem step was removed with
  the theorem relocation - see the revised extensions bullet below). The cha-specific forced-move pre-advance is
  dropped from the full analyzer (the search decides forced lines within budget); the quick analyzer keeps its
  forced-move advance because that is paper (Figure 10 step 1, loop-guarded per footnote a).
- **Budget envelope kept from 21.x**: 500 000 global node budget across deepening iterations (the paper leaves
  `bound(d)` as a parameter), depth cap 100, per-iteration node bound = remaining global budget. Transposition table
  is **per-iteration, and the key includes the footnote-b reward-chain flag** (final state after the Codex review:
  cross-iteration sharing — briefly implemented for performance — can let a stale depth-cut entry suppress a later
  iteration's interrupt and fake an exhaustion witness → potential false `UNWINNABLE`; see `fun22-spec.md` §6). The
  post-loop fall-through returns `UNDETERMINED`, fixing the old code's theoretically unsound `UNWINNABLE`
  fall-through.
- **The cha material predicate (`UnwinnabilityMaterialBitboard`) is retired with the port**; Ambrona's Lemmas 5/6 live
  on in the clean-room `MaterialLemmas` (same proven lemmas, paper-traceable formulation — this does not reopen the
  "material is correct" decision, it re-derives the identical predicate from the paper).
- **Oracle contract shifts from adherence to implication**: vs cha/chasolver oracles, soundness contradictions are
  bugs; completeness differences (who resolves more `UNDETERMINED`) are expected (paper ≠ cha extensions, text
  footnote 12) and go to the accepted-differences fixtures.

Work items:

- **Vendor the semi-static layer** (SquareGeometry, Predecessors, semi-static Position model, Mobility = Fig 6/7
  fixpoint, UnwinnableSemiStatic = Fig 8 with the α(s) reading verified at 500 dpi). ✅ DONE 2026-07-03 — the ported
  Theorem 12 soundness sweep over the newly committed D3-Chess corpus reproduces fun22-reference exactly
  (1723 analysed / 80 strict-FEN-rejected / 699 sound UNWINNABLE verdicts).
- **Vendor the search layer** (MaterialLemmas, Score = Fig 12, GoingToCorner = Fig 13, FindHelpmate = Fig 5 with
  footnote-b reward chaining + mate-line tracking) and rewire the public analyzers (Fig 9 full, Fig 10 quick).
  ✅ DONE 2026-07-03 — `BasicHelpmateExistenceTheorem` now carries its three bitboard shape helpers itself (the cha
  material class is gone).
- **Delete the cha-port internals and their unit tests; port the fun22-reference unit tests.** ✅ DONE 2026-07-03 —
  27 main classes and 22 test files deleted (including the mobility/semistatic internal-oracle comparisons and their
  generators, which oracled cha internals that no longer exist); 6 ported test classes added.
- **Docs**: package-info rewritten (clean-room paper implementation, no longer a cha port), spec brought into
  `fun22-spec.md`, CHANGELOG entry. ✅ DONE 2026-07-03 — README/manual regenerated (the "blocked positions the quick
  algorithm proves" example now uses the paper-provable bishop fortress; the old example has a semi-open file, which
  Figure 10's gate declines by design). Default profile green (1302 tests).
- **Full-suite sign-off**: run the excluded unwinnability suite, re-baseline the accepted-differences fixtures
  (soundness contradictions block release), measure performance vs 21.1.0 and decide whether a fast-board pass is
  needed before release. ✅ DONE 2026-07-03 — whole suite (default + excluded) green: **1348 tests, 0 failures,
  0 soundness contradictions against cha and chasolver**. Verdict movement vs the cha port, in full: the FULL
  analyzer regressed on exactly **one verdict** (Norgaard pawn-wall, White side, `UNWINNABLE`→`UNDETERMINED` — the
  pure locked fortress only a beyond-paper semi-static proves; Black side was already `UNDETERMINED` since 21.0.0)
  and gained one class of strength (quick `WINNABLE` on quickly matable positions, ~17 corpus positions). The QUICK
  analyzer regressed on two fortress positions (`lichess_f6c1lu7R`, Norgaard-White). **Correction after the Codex
  review**: the shared-transposition-table change that briefly recovered f6c1lu7R for the full analyzer (and cut the
  suite from 22:03 to 14:39 min) was reverted as theoretically unsound (stale depth-cut entries can fake the
  exhaustion witness — see the budget-envelope decision above), so the full analyzer's regression is f6c1lu7R (both
  sides) plus Norgaard-White, all sound-direction and allowlisted. Test-harness changes: quick oracle
  comparisons moved to implication semantics (a two-valued oracle's "undetermined" is consistent with a definite
  ashlar verdict); Lichess pin tests now report all failing positions instead of failing fast; the retired
  cha-internal mobility/semistatic oracles (fixtures, generators, C++ sources) are deleted.

- **Codex review round (2026-07-03).** ✅ DONE — all three findings addressed plus the test gap:
  - **P1 (transposition key)**: the reward-chain flag is now part of the search-state key, and the analysis exposed a
    deeper issue in the briefly-shared transposition table (stale depth-cut entries can fake the exhaustion witness →
    potential false `UNWINNABLE`); reverted to the paper-literal per-iteration table. Costs vs the shared version:
    f6c1lu7R full is `UNDETERMINED` again (allowlisted; quick already was), and the honest exact-state keying now
    agrees with both oracles on `chasolver_node_limit_exception.pgn` (`UNDETERMINED`; the old engine's `WINNABLE`
    there rode on the unsoundly coarse key) — both legacy allowlist rows deleted.
  - **P2**: `UnwinnabilityQuickAnalysis` javadoc and the manual's "Reading Quick Verdicts" section now describe the
    three-valued quick verdict; README/manual regenerated.
  - **P3**: the pom description no longer says "CHA-based" (now "implementing Ambrona's FUN 2022 algorithm").
  - **Test gap**: direct Figure 12/13 unit tests added (`TestScoreFigure12`, `TestGoingToCornerFigure13`, one case
    per figure line) plus a footnote-b end-to-end pin (K+Q vs K+pawn deep helpmate, outside every theorem class).
    **Internal-layer oracle revived with an implication contract**: measured over the archived CHA C++ mobility dump,
    cha's regions are bit-identical to the paper's Figure 6/7 least fixpoint on 16758 of 16845 piece rows and a
    strict subset on the remaining 87 (all locked pawn structures — cha's beyond-paper "steady pieces" tightening);
    `TestMobilityAgainstChaMobilityOracle` pins cha ⊆ paper per piece (a cha square outside our fixpoint would mean
    we under-approximate and break Theorem 12 admissibility) plus the exact split. The retired `semistatic.tsv` layer
    stays retired: no implication holds in either direction there (cha case-splitting vs the paper's stronger
    α-reading), so that layer is covered by the D3-Chess Theorem 12 soundness sweep instead.

Remaining before release:

- **Performance decision**: the curated chasolver sweep runs 831 s vs ~404 s on the cha-port engine (~2×; the gap is
  concentrated in hard positions that burn the full 500 000-node budget at public-`Board` per-node cost). Decide
  whether 22.0.0 ships as-is (single-query production impact is millisecond-scale on typical positions) or first
  gets a fast-board pass (drive the Figure 5 search and Figure 10 DFS over an internal make/unmake board).
  ✅ DONE 2026-07-03 (user decision: the fast board MUST be used - it was wrongly deleted with the cha port despite
  being engine-agnostic machinery). `HelpmateSearchBoard`/`HelpmateSearchKey`/`LegalMoveBuffer`/`UndoState` and their
  lock-step tests restored from git history; `FindHelpmate` (Fig 5) and the Fig 10 quick DFS now run on the search
  board; `Score`/`GoingToCorner` refactored to bitboard-level signatures. Verdicts identical (all oracle comparisons
  and pins green with zero re-baselining). Curated sweep: **318 s** - 2.6× faster than the Board-driven engine and
  ~21% faster than the old cha-port engine.
- **Release steps** per workflows.md (version bump, CHANGELOG date, README/manual regeneration is already done,
  sign+publish from the notebook).

---

## 21.1.0 — Oracle housekeeping

Published 2026-07-03 (tag `21.1.0`, on Maven Central); see **CHANGELOG.md** for the consumer-facing summary. Shipped from branch `further-improving`.

- **Correct the 21.0.0 release-notes overclaim about the KNNvK/KRRvK/KQQvK theorem.** ✅ DONE 2026-07-02 — the `CHANGELOG.md` `[21.0.0]` bullet is reworded (performance + proof-provenance improvement, not completeness; the search already proved these classes `WINNABLE`), the summary framing is now "A correctness release", and the published GitHub Release notes were edited in place to match. The correction is also called out in the `[21.1.0]` entry.

- **Document the legal-position contract for the unwinnability analyzers.** ✅ DONE 2026-06-28 — stated in the `unwinnability` package-info (a "Legal positions only" section), the manual's Unwinnability API, and a pointer on each public analyzer (`UnwinnableFullAnalyzer` / `UnwinnableQuickAnalyzer` / `DeadPositionAnalyzer`): the analysis is guaranteed only on strictly legal positions; on illegal input the result is undefined and may disagree between `unwinnableQuick` and `unwinnableFull` (and with chasolver). Legality is decidable only by infeasible retrograde / proof-game search, so it is not enforced — submitting legal positions is the caller's responsibility; the verdict is still correct on the large majority of illegal positions. Out-of-domain examples cited: `8/8/8/8/2N5/8/k1K5/1B6 b` (KBNvK) and `8/8/8/8/8/B7/B7/k1K5 w` (KBBvK), `WINNABLE` via the theorem while `unwinnableQuick` and chasolver return `UNWINNABLE`.
- **Add the known retro-illegal theorem-counterexamples to the tests** (the two above plus the ~3+15 white / 1 black representatives enumerated in basic-helpmate-existence) as documented out-of-domain regression cases, asserting ashlar's current behaviour and noting they are illegal / out-of-scope. ✅ DONE 2026-07-02 — `TestRetroIllegalTheoremCounterexamples` locks down all 19 representatives from the bhe README ("Illegal positions not satisfying the conclusion"): strict FEN accepts, full = theorem-certified `WINNABLE` (no mate line), quick = `UNWINNABLE` both sides. **Decision 2026-06-28: document + test only.** Both alternatives declined: per-position hardcoding (couples ashlar to bhe's per-class enumeration; false sense of general illegal-position handling), and a general last-move legality check at FEN import (feasible but still incomplete — documentation is needed regardless — and blurs the library's unwinnability focus for zero legal-game benefit, since these positions cannot arise in a legal game).
- **Clarify the README / `unwinnability` package-info that ashlar ports the C++ CHA (`cha`), not Ambrona's Rust `chasolver`.** ✅ DONE 2026-06-28 — the `github.com/miguel-ambrona/D3-Chess` URL now redirects to the Rust `chasolver`, so the docs now state ashlar ports the earlier C++ implementation (and the CHA paper), not the Rust one.

---

## 21.0.0 — Unwinnability soundness and endgame helpmate theorems

Correctness-and-completeness release for the CHA unwinnability / adjudication path, plus stricter FEN legality. See **CHANGELOG.md** for the consumer-facing summary.

- **Semi-static pawn-visitor soundness fix** ✅ DONE 2026-06-27 — removed the unsound `isIgnorePawns` shortcut in `UnwinnableSemiStatic` that over-claimed `UNWINNABLE` on pawn-net helpmates. Trade-off: pawn-wall fortresses it also (correctly) proved are now `UNDETERMINED` (sound, less complete) — accepted-differences rows added to the cha + chasolver oracles.
- **Impossible-check strict-FEN validation** ✅ DONE 2026-06-27 — strict parsing (and PGN `SetUp`/`FEN` import) rejects unreachable check configurations (double bishop/rook/queen/knight check, three+ checkers); new `StrictFenSemanticValidationProblem.INVALID_POSITION_IMPOSSIBLE_CHECK`.
- **chasolver (Rust) cross-validation oracle** ✅ DONE 2026-06-27 — validated that Ambrona's Rust `chasolver` adheres to his C++ `cha`; curated soundness set kept.
- **KNNvK / KRRvK / KQQvK helpmate-existence theorem** ✅ DONE 2026-06-27 — `BasicHelpmateExistenceTheorem` tracks basic-helpmate-existence 1.2.0, deciding these classes by theorem instead of search.

---

## 20.0.0 — JPMS module boundary / API-surface reset

The dedicated module-and-API-boundary release. Breaking package / FQN changes that don't belong in 19.0.0 are parked here.

### Drop the Guava dependency — JDK collections only ✅ DONE 2026-06-22

The public API exposed Guava `Immutable{List,Set,Map}` on return types, record components, and constants, forcing every consumer onto Guava (observed: a downstream module had to add Guava to its POM just to call `Board`). Guava was also used pervasively *internally*, yet only for `Immutable*` collection factories that JDK 17 fully covers (`List.copyOf`/`Set.copyOf`/`Map.copyOf`, `List.of`/…).

Removed entirely, in two commits:

- **Tier 1 — public surface.** `Board` getters, the vocabulary enums' `REAL` constants, the parser result records + validation-exception accessors, `Reporter`, `PgnCreate.toPgnLines`, the bitboard accessors, `UnwinnabilityFullAnalysis`, etc. now declare JDK `List`/`Set`. Behaviour-preserving (still returned the same Guava objects internally at that stage), so consumers no longer compile against Guava and `module-info` needs no `requires` for it.
- **Tier 2 — internal sweep + dependency removed from `pom.xml`.** The `Nulls` / `ImmutableUtility` hubs and every call site now build JDK collections. Determinism preserved by rule: `copyOfSet` → unmodifiable `LinkedHashSet`, `copyOfMap` / `immutableEnumMap` → unmodifiable `LinkedHashMap` / `EnumMap` — **never** `Set.copyOf` / `Map.copyOf`, which randomise iteration order per JVM run. Verified with `-Pfull` (golden-output, perft, and python-chess oracle suites all green).

Result: zero Guava in the artifact; under `module-info` Guava is not a `requires` at all. Resolves §5.2 of `20.0.0-jpms-plan.md`.

### Group exceptions, models, enums, and constants by domain (package-by-feature)

The codebase currently mixes "group by kind" with "feature-inline", and *duplicates* the by-kind buckets at two levels — `ashlarchess.exceptions` **and** `ashlarchess.common.exceptions`; `ashlarchess.enums` **and** `common.enums`; `ashlarchess.model` **and** `common.model` — with no rule for which one a type lands in, plus single-file kind sub-packages (e.g. `fen.constants` holds one class). A newcomer cannot predict any type's package from a rule.

Rule to adopt: **package by feature/domain first; package by kind only for genuinely cross-cutting foundations.**

- Feature-specific exceptions / enums / models / constants live *inline* in their feature package (FEN's in `fen`, PGN's in `pgn`, SAN's in `san`) — beside the code they serve. Not in a central bucket, and not in `fen.exceptions`-style kind sub-packages either (keep them next to the parser, as agreed).
- Collapse the duplicate buckets: never both a top-level `<kind>` and a `common.<kind>`. Keep one shared-core home for genuinely cross-cutting types only — base exceptions (`UsageException`, `ProgrammingMistakeException`, `NonePointerException`), `ChessConstants`, the core chess vocabulary (`Side` / `Piece` / `Square` / …).
- No single-file kind sub-packages.
- Done = given any type, one rule predicts its package.

A large, purely mechanical, compiler-checked FQN reset with no behavior change, and a prerequisite for a clean `module-info`: sensible `exports` / `opens` and package-private boundaries are impossible while features are split across two `common.*` junk drawers. (The FEN-local slice — folding the FEN validation problem enum into `fen` and dropping the single-file `fen.constants` — was carved out for 19.0.0; this is the global reset across all domains.)

**Status (2026-06-22, branch `real-jpms`):** core collapse ✅ DONE in four compile-checked slices (commits `e5dc4d0a`→`5b711ecf`), full suite green (1276 tests). Decision: **concentrate public vocabulary on `board`** (base exceptions on top-level `exceptions`).

1. feature inlining → `fen`/`pgn`/`san` (incl. dissolving the single-file `fen.constants`)
2. exceptions: base hierarchy → top-level `exceptions`, `InvalidMoveException` → `board`; `common.exceptions` removed
3. enums: move-analysis checks → `moves`, `Termination` → `board`; `enums` + `common.enums` removed
4. model: public vocabulary (`LegalMove`, `LegalMoveKind`, `UciMove`, `MoveSpecification`, `Outcome`) + board-internal (`DynamicPosition`, `ClaimableMove`, `ClaimRights`) → `board`; `EmptyBoardMove`, `CastlingRightBoth` → `moves`; `model` + `common.model` removed

The headline goal is met — no duplicate by-kind buckets, public surface predictable. **The internal `common.*` tidy is DONE** in the Phase 4 consolidation (below): `common`/`common.utility`/`common.constants` collapsed into a single non-exported `internal` package; `ImmutableUtility` deleted (dead post-Guava); `common.ucimove.utility` + `DynamicPositionConstants` → `board.internal`; `CastlingConstants` kept in `internal` (genuinely cross-cutting); `messages.Message` left as its own clean single-purpose non-exported package. Package-info accuracy pass done as part of the moves. (Test-side `model`/`common.model`/`common.constants` packages still hold test-only types — `PseudoLegalMove`, `LegalMoveCalculation`, `TestOutcome`, `EnumConstants` — which is fine; they are test-only and never enter the production surface.)

### JPMS module descriptor (`module-info.java`) ✅ DONE 2026-06-22

`src/main/java/module-info.java` added — `module io.github.dlbbld.ashlarchess` (the name already published as `Automatic-Module-Name` in 19.1.0, so no rename break). Exports the settled public API (`board`, `board.enums`, `fen`, `fen.model`, `pgn`, `san`, `adjudication`, `report`, `unwinnability`, `exceptions`, `bitboard`); `requires java.logging` plus `requires static transitive org.eclipse.jdt.annotation` (compile-time API annotations; not a runtime dependency). Guava, log4j, and commons-lang3 are not production dependencies. Export closure verified clean (import audit + `jdeps --api-only`). Build config the descriptor forced (all in `pom.xml`):

- **surefire** `--add-opens io.github.dlbbld.ashlarchess/…board=org.apache.commons.lang3` — the white-box test tree (no `module-info`) is patched into the module by surefire; `TestBoardUnperformMove` reflects into `Board`'s private fields via commons-lang3 `EqualsBuilder.reflectionEquals`. Test-only; deliberately kept out of `module-info`.
- **maven-jar-plugin** — dropped the now-redundant `Automatic-Module-Name` manifest entry (the real descriptor supersedes it).
- **maven-javadoc-plugin** — excludes `module-info.java` from source scanning: 3.11.2 aborts a single-module `module-info` project with "aggregated report contains named and unnamed modules" in its pre-scan. The descriptor still compiles into the jar (modular consumers unaffected), and the public javadoc jar still renders `module-summary.html` from the compiled descriptor.

Verified green: `mvn -o test` + `-Pfull` (1276 tests, 0 failures), `javadoc:jar`, and `javadoc:javadoc`/`test-javadoc -Dshow=private`.

**Phase 3 (hide internals) ✅ DONE 2026-06-22.** Narrowed the incidental `public` helpers that `exports` was dragging into the API into non-exported `*.internal` subpackages: `bitboard.internal` (move-gen engine), `pgn.internal` (`TagUtility`/`StandardTag`), `fen.internal` (`FenBoard`/`FenConstants`/symbol enums/`FenField`), `san.internal` (SAN-conversion model + notation enums + format utilities/validators). `report`/`adjudication`/`unwinnability`/`board.enums`/`exceptions` were already clean. Each `*.internal` is in `module-info`'s hidden set and the javadoc `excludePackageNames`; `-javadoc.jar` verified to carry zero `*.internal` entries while keeping the public API.

**Phase 4 (API-surface audit + consolidation) ✅ DONE 2026-06-23.** A package-by-package audit of the ~88 public types across the exported packages found the surface already ~92% genuine; the residual incidental publics were hidden: `board.MoveNumberFormat` (+ its `AddSpace` flag) → `board.internal`; the SAN/LAN generators `MoveToSan`/`MoveToLan` and `SanTerminalMarker` (+ collaborators `SanSourceSpecification`/`SanDisambiguationUtility`) → `san.internal` (the SAN/LAN feature stays public via `Board.getSan()`/`getLan()`); the `SquareUtility`/`RankUtility` geometry helpers → `board.enums.internal`. Confirmed genuine (kept): `SquareType` (anchored by `Square.getSquareType()`), the `unwinnability` analyzers + their result records (README-advertised), `pgn` (`PgnCreate`/`PgnUtility`/`PgnWriter` advertised), `report` (only `Reporter` public). The `common.*` tree was consolidated into a single non-exported `internal` package; `ImmutableUtility` deleted (dead post-Guava); UCI helpers + `DynamicPositionConstants` → `board.internal`; dead `common.ucimove` removed. Verified: 1276 tests green, `-javadoc.jar` clean. Evaluated and **declined**: relocating the theme-organized white-box tests to gain package-private (the test taxonomy and cross-cluster tests make `public`-in-non-exported the correct design — see boundary docs).

**Phase 5 (first-consumer feedback) ✅ DONE 2026-06-23.** A trial migration of a downstream consumer (otb-chess) against `real-jpms` confirmed the package reset behaves as intended (relocated public types = import changes; `UpdateSquare` correctly internal; Guava genuinely gone) and surfaced one genuine API gap: **castling geometry was unreachable**. A castling `MoveSpecification` stores `Square.NONE` for from/to, and the king/rook square logic had moved into non-exported packages (`moves.CastlingUtility`, `board.enums.internal.SquareUtility`), so consumers had to hard-code it. Fixed by adding public geometry accessors on `CastlingMove`: `kingFromSquare(Side)`, `kingToSquare(Side)`, `rookFromSquare(Side)`, `rookToSquare(Side)` (pinned to the internal `CastlingConstants` by a test so public API and engine can't drift; documented in the manual). Follow-up (second consumer pass): added the symmetric `MoveSpecification.isCastling()` convenience (mirroring the existing `isPromotion()`) and deduped the internal `CastlingUtility.isCastlingMove` to delegate to it. Also bumped the pom version `19.1.0` → **`20.0.0-SNAPSHOT`** (the branch was colliding with the released 19.1.0 automatic-module artifact). Public Markdown examples are generated from tested `ReadmeExamples` snippets (the castling example is a verified, executed snippet) — edit `README.template.md` / `manual.template.md` / `ReadmeExamples` and regenerate, never `README.md` or `manual.md` directly. Verified: 1288 tests green, `-javadoc.jar` clean.

**Phase 6 (cooldown deep-dive) ✅ DONE 2026-06-23.** Adversarial multi-agent review of the JPMS/API surface. **No breaking gap found** — the export closure is clean (a flagged "Message-in-enum-constructor leak" was a false positive: intra-module access to non-exported packages is legal, and `Message` is in no public signature). Closed the genuine class-A gaps: (1) **en-passant captured-pawn square** was the symmetric analog of the castling gap (resolver was in non-exported `moves`) → added `LegalMove.enPassantCapturedPawnSquare()` + `isCapture()`/`isCastling()`/`isPromotion()`/`isEnPassant()` predicates; (2) **forced dependencies** — dropped `commons-lang3` (inlined `Nulls.normalizeSpace`, deleted dead `capitalize`) and `log4j` (production now uses JDK `java.util.logging`; the 64 tests that shared `Nulls.getLogger` rerouted to log4j's own `LogManager`, log4j now test-scope) → final module `requires` is just `java.logging` + `static transitive jdt.annotation`, with no forced external runtime dependency on consumers; (3) **no boundary guard** → added `TestModuleBoundary` (exports == intended API; javadoc `excludePackageNames` == non-exported production packages); (4) **no migration doc** → wrote the `[Unreleased]`/20.0.0 CHANGELOG breaking/added section. Verified: 1296 tests green, module descriptor clean. Remaining for 20.0.0: the release flow.

---

## Backlog — captured but unscheduled

Items here are not assigned to any release. Captured so they don't get lost; revisit if/when scope or motivation aligns.

### Tighten remaining mutable return types on internal-but-public surfaces

A few `public static` move-generation helpers still return a freshly-built mutable `Set` / `List` that callers could mutate: `PromotionUtility.performPromotionMovements`, `CastlingUtility.performCastlingMovements`, `EnPassantCaptureUtility.performEnPassantCaptureMovements`, and the `EmptyBoardMoveUtility` overloads. Each returns a fresh per-call copy, so there is no aliasing bug today — wrapping the result as an unmodifiable JDK collection (e.g. via `Nulls.copyOfList` / `Nulls.copyOfSet`, now that Guava is gone) is pure polish. Parked here rather than 19.0.0 because these are exactly the internal-but-accidentally-public move-gen surfaces this release narrows or hides behind the module boundary: fix the return types in the same pass that decides which of them stay public at all. (`BitboardPositionUtility.toSquares` already returns an unmodifiable `Set`.)

### Position-as-value ergonomics: `mirror()` and an immutable `play(move)` (audit M3)

Two related "treat a position as an immutable value" capabilities that Class-A libraries (python-chess, shakmaty, scalachess) expose and ashlar does not surface cleanly:

- **`mirror()` / flip / transform** — return a new position that is the original vertically flipped *and* colour-swapped (the same position from the other side; turn, castling, en-passant all mirrored). Valuable for symmetry tests (`f(mirror(P))` must equal the mirror of `f(P)` — catches colour-handedness bugs), dataset augmentation, and halving case analysis. ashlar has none at board level today, only `SquareUtility.flip(Square)`.
- **Clean immutable `play(move) → position`** — the next position as a fresh value, without mutating a `Board` or carrying its history. The machinery already exists immutably as `BitboardPosition.afterMove(...)`; it is just not surfaced as a clean public position API (today the only path is `copyCurrentPositionWithoutHistory()` then `move()`).

Low–medium effort (the immutable apply-move exists; mirror is a straightforward bitboard transform — vertical flip = byte-reverse the 12 longs, colour swap = swap the white/black bitboards). It belongs here because it is **additive public API**, to be designed together with the position-API boundary (what the clean public position type is once the bitboard layer is internalised) and the still-open "light-analysis toolkit?" direction — `play()` / `mirror()` are most valuable to analysis / ML users.

The one slice worth doing regardless of that direction, with no public-API commitment: an internal **mirror used by the test suite for symmetry checks** (e.g. unwinnability of `P` for a side ⟺ unwinnability of `mirror(P)` for the other) — pure added test coverage.

### Lenient PGN: accept consecutive comments

The lenient PGN parser allows exactly one commentary per slot. Two consecutive comments — a brace comment
immediately followed by a `;` end-of-line comment, or `{c} {c}` — are rejected with
`MOVETEXT_COMMENTARY_NOT_ALLOWED_IN_SAN` ("a commentary cannot occur where a SAN move is expected"). Real-world
exports (lichess, ChessBase) sometimes emit multiple comments between moves, so for a best-in-class *lenient*
importer this is a genuine gap. Supporting it is a data-model change — coalesce the consecutive comments into one
`PgnCommentary`, or model a per-move comment list — so it is parked rather than squeezed into 19.0.0. The strict
parser deliberately stays canonical (single comment, export format). Found by `ParserStressSurvey`'s
comment-placement isolation; the per-move brace and game-start comment forms already parse, including a 7 MB /
17,697-move heavy-comment document in ~0.7 s (linear, no quadratic comment handling).

### Report-layer long-game cost (secondary `buildEntry` O(n^2); inherent claim-check constant)

`Reporter.report(board)` is fast for real games (a 271-ply game reports in ~90 ms) but takes ~7 s on the synthetic
17,697-ply MAX_MOVES game. Two contributors, both pathological-only (`ReportScalingSurvey` measures them):

- **Inherent O(n) with a fat constant** — `ThreefoldClaimAheadReportBuilder.collectClaimAheadsAtCurrentMove` calls
  `canClaimThreefoldRepetitionRuleFor` for every legal move at every position (~175 us/position). This is the dominant
  cost and is not a defect: it is the work an exhaustive claim-ahead report does. Reducing it would mean changing what
  the report computes, not just how.
- **Secondary O(n^2)** — `ThreefoldClaimAheadReportBuilder.build` calls `buildEntry` per claim-ahead, and `buildEntry`
  does an O(n) `playedMoveRecords.contains(...)` plus an O(n) prior-occurrence scan. With O(n) claim-aheads this is
  O(n^2), but it only bites games with many threefold boundaries (real games have ~0) and is minor next to the
  claim-check constant. Fix when motivated: precompute a `Set<MoveRecord>` for `contains` and a
  `Map<DynamicPosition, List<MoveRecord>>` of played records for prior occurrences (the same shape as the
  `RepetitionGrouping` O(n^2) -> O(n) fix already shipped in 19.0.0).

Not scheduled because real-game reporting is already fast; revisit if long-game (correspondence / adjournment /
synthetic) reporting becomes a real use case.

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

### Replace `EnumConstants` constant interface ✅ DONE (19.0.0)
`EnumConstants` was a `public interface` exposing ~90 `public static final` aliases for `Square.*`/`Side.*`/`Piece.*`/`PieceType.*`/`Rank.*`/`File.*` so implementing classes inherited them unqualified — the classic "constant interface" anti-pattern (Effective Java item 22), with `ChessBoard extends EnumConstants` the clearest symptom. **Resolved:** it was removed from `src/main` entirely (0 production references) and now lives test-only as `io.github.dlbbld.ashlarchess.common.constants.EnumConstants`, a `public final class` (a plain test-fixture constants holder, no longer an interface and never on the production/API surface).
