# Tasks

Live planning only: current release work, backlog, and obsolete decisions. Shipped release history lives in
**CHANGELOG.md**; recurring procedures live in **workflows.md**. Order within each live section is the source of truth.

---

## The story when releases are done

*ashlar-chess started as a correctness-first reference implementation, built from the FIDE rules without consulting existing libraries. It found correctness bugs in python-chess and ScalaChess along the way. Once the rules were stable, a bitboard backend was added alongside the reference layer and verified bit-exact against it. Production then switched to the bitboard path; the reference layer was relocated into the test tree and remains as the permanent differential-test oracle. Cross-validation against python-chess was reactivated as primary, with `chesslib` retained as a second witness. Only then published to Maven Central.*

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

### Records carry data, not behavior — sweep for violations
The project rule (documented in `coding-conventions.md`): records carry data; domain logic that operates on them lives in dedicated utility / service classes. Permitted on a record: compact-constructor validation, `Comparable` when ordering is intrinsic, and language-provided `equals` / `hashCode` / `toString`. Domain-operation methods are not.
Example: `StaticPosition`: the record carries multiple non-data methods — `createChangedPosition` etc.

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
