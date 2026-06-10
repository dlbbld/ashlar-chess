# Tasks

Live planning only: current release work, backlog, and obsolete decisions. Shipped release history lives in
**CHANGELOG.md**; recurring procedures live in **workflows.md**. Order within each live section is the source of truth.

---

## The story when releases are done

*ashlar-chess started as a correctness-first reference implementation, built from the FIDE rules without consulting existing libraries. It found correctness bugs in python-chess and ScalaChess along the way. Once the rules were stable, a bitboard backend was added alongside the reference layer and verified bit-exact against it. Production then switched to the bitboard path; the reference layer was relocated into the test tree and remains as the permanent differential-test oracle. Cross-validation against python-chess was reactivated as primary, with `chesslib` retained as a second witness. Only then published to Maven Central.*

---

## Current release 18.1.0 — lone-minor unwinnability proofs + generated README

Minor release. The non-cosmetic core extends the full analyzer's basic-helpmate-existence theorem to two further
material classes proved by Miguel Ambrona (the CHA author): the side intending to win holding **only king + bishop** or
**only king + knight**, so the full analyzer decides their unwinnability directly from the proof instead of by
cooperative-mate search. The rest is README work: regenerate the now-stale repetition / fifty-move report output and
move the README onto a template + generator pipeline so every snippet provably compiles and prints exactly the output
shown, and tighten the long-winded motivation into a one-paragraph charter. Cut + Central deploy follow the workflows.md
procedure.

scalachess is **out of scope** for 18.1.0 — it was a one-time correctness check that has served its purpose; see
[_scalachess as a permanent differential oracle_](#scalachess-as-a-permanent-differential-oracle) under Obsolete.

### Task 3 (major, non-cosmetic) — extend the unwinnability theorem to lone-bishop / lone-knight winners

Build Miguel Ambrona's proofs (his PDF / written assessment of the CHA algorithm) for two additional material classes
into the full unwinnability analysis: the intended winner holding **only K+B** and **only K+N**. These are
"insufficient material for the side to win" classes — the proof characterises exactly when that side has no helpmate
(unwinnable) versus the exceptional positions where one still exists.

- Mechanism mirrors the 18.0.0 theorem in
  [`BasicHelpmateExistenceTheorem`](src/main/java/io/github/dlbbld/ashlarchess/unwinnability/BasicHelpmateExistenceTheorem.java)
  (covered classes today: KRvK, KQvK, KBBvK opposite-coloured, KBNvK, KRvKB, KRvKN). Extend `isCoveredClass` / `decide`
  to the two new classes, or add a sibling theorem, wired into
  [`UnwinnableFullAnalyzer`](src/main/java/io/github/dlbbld/ashlarchess/unwinnability/UnwinnableFullAnalyzer.java) the
  same way. A theorem-decided unwinnable position reports the existing `UNWINNABLE` verdict — no new enum value.
- Transcribe the exact case conditions from Ambrona's PDF during implementation — **do not guess them**. Record the
  proof reference (author, document, version/date) in the class JavaDoc next to the existing basic-helpmate-existence
  citation.
- **Deviation from python-chess.** This makes ashlar more decisive than python-chess's lone-side
  `has_insufficient_material` heuristic on these classes; the cross-validation oracle will disagree on the affected
  positions. Handle exactly as prior documented divergences (cf. the 15.0.0 fifty-move-claim corner case): a contained,
  documented skip-guard or accepted-difference entry in the python-chess (and scalachess) insufficient-material /
  unwinnability oracle harness, with the rationale spelled out in the test and in the CHANGELOG `Behavioral` section.
- **New test cases.** Add fixtures covering both the proved-unwinnable and the exceptional-still-winnable sub-cases for
  K+B-only and K+N-only winners. Follow the workflows.md "Adding a new PGN test fixture" procedure (or position-only
  fixtures where a PGN replay is not needed, as the focused insufficient-material oracle already does). Pin the theorem
  decision against the search-based path on representative FENs.

### Task 1 (cosmetic) — doctest the README: every snippet compiles and prints exactly what is shown

**Hard requirement (from the user).** Every code example in the README must be real, compilable Java that — run with
the inputs shown — produces *exactly* the output cited beneath it. No hand-copied code, no hand-typed output. The
repetition / fifty-move `Reporter.printReport` blocks (§"Threefold repetition and fifty-moves") are the immediate
trigger (stale since the 16.0.0 format change, still maturing), but the guarantee applies to every real-Java example.
The mechanism is the author's choice; what matters is that "it compiles" is enforced by `javac` and "it prints this" is
enforced by actually running it.

Decided approach — generate the README from compiled, executed example source (this is what makes the guarantee real
rather than aspirational):

- One real test-tree source of example methods (e.g. `io.github.dlbbld.ashlarchess.test.readme.ReadmeExamples`), one
  method per README example, written as ordinary compilable Java and emitting output via captured `System.out` (or a
  returned string). Region markers (e.g. `// <readme:threefold-claim-ahead> … // </readme:…>`) delimit the slice shown
  in the README; imports and the method/class wrapper are compiled but elided from the display.
- A checked-in `README.template.md` holds the prose plus a placeholder per example (code slot + output slot).
- `GenerateReadme` (test-tree `main`, mirroring the `GenerateTestCaseForPgn*` helpers) runs each example, captures its
  output, slices the marked source, and writes `README.md` = template with both substituted.
- Drift guard: a JUnit test regenerates in-memory and asserts equality with the committed `README.md` (same pattern as
  `TestSetupPgnRegistration`). The build then fails on any hand-edit of `README.md`, or any API/output change that
  wasn't regenerated — so the compile-and-output guarantee is enforced every run, not trusted.

Edge cases (not every example is deterministic, self-contained Java):
- **Inline `// result` outputs** (`System.out.println(board.isCheckmate()); // true`) vs. **separate fenced output
  blocks** (the report examples). Both must be generated; the inline form substitutes the captured value into the
  trailing comment.
- **Non-deterministic output** — `PgnCreate` emits `[Date "<today>"]`. Normalize (inject a fixed date, or keep the
  `<today>` placeholder) so regeneration is stable.
- **Filesystem side effects** — `PgnWriter.writePgn(…, "C:\\temp\\…")` / `parse("C:\\temp\\…")` compile but can't run
  against a literal path. Run against a generator-managed temp path while displaying an illustrative one, or mark them
  compile-only (compiled, no output asserted).
- **Pseudocode blocks** — the game-adjudication `on flagfall(…)` blocks are ` ```text ` pseudocode, not Java; out of
  scope for compilation.

### Task 2 (cosmetic) — tighten the motivation into a charter

The README "Motivation for the chess library" section is long-winded. Replace the lead with a tight charter capturing
the founding observation:

> Games that could have been drawn are lost because a player misses a threefold repetition — the position is hard for a
> human to spot, because the brain deals poorly with repetition. Closing that gap is what this tool was built for.

Condense the three sub-sections (threefold / fifty-move, unwinnability / dead-position, Java-library rationale) around
that thesis; keep the substance, cut the throat-clearing.

### Release notes

- **Version.** 18.1.0 = minor: the analyzer becomes more decisive on two material classes with no public API or enum
  change; the python-chess deviation is a documented behavioral refinement contained to the test oracle. (If
  implementation surfaces a public-surface change, revisit per the workflows.md version-bump rules.)
- **scalachess out of scope.** Decided: 18.1.0 ships the three tasks only. scalachess was a one-time validation (all
  verdicts matched), not a standing oracle — recorded under Obsolete; its branch stays unmerged. At cut, the
  `[Unreleased]` block becomes `[18.1.0] - <release title> - <date>` (workflows.md step 3).
- **Release title** is finalised at cut, sourced from this heading.
- Artifacts already bumped to 18.1.0 ahead of the cut: `pom.xml` and both README dependency snippets.

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
