# Tasks

Live planning only: current release work, backlog, and obsolete decisions. Shipped release history lives in
**CHANGELOG.md**; recurring procedures live in **workflows.md**. Order within each live section is the source of truth.

---

## The story when releases are done

*ashlar-chess started as a correctness-first reference implementation, built from the FIDE rules without consulting existing libraries. It found correctness bugs in python-chess and ScalaChess along the way. Once the rules were stable, a bitboard backend was added alongside the reference layer and verified bit-exact against it. Production then switched to the bitboard path; the reference layer was relocated into the test tree and remains as the permanent differential-test oracle. Cross-validation against python-chess was reactivated as primary, with `chesslib` retained as a second witness. Only then published to Maven Central.*

---

## Backlog — captured but unscheduled

Items here are not assigned to any release. Captured so they don't get lost; revisit if/when scope or motivation aligns.

No unscheduled items currently.

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
