# Tasks

Live planning only: current release work, backlog, and obsolete decisions. Shipped release history lives in
**CHANGELOG.md**; recurring procedures live in **workflows.md**. Order within each live section is the source of truth.

---

## The story when releases are done

*ashlar-chess started as a correctness-first reference implementation, built from the FIDE rules without consulting existing libraries. It found correctness bugs in python-chess and ScalaChess along the way. Once the rules were stable, a bitboard backend was added alongside the reference layer and verified bit-exact against it. Production then switched to the bitboard path; the reference layer was relocated into the test tree and remains as the permanent differential-test oracle. Cross-validation against python-chess was reactivated as primary, with `chesslib` retained as a second witness. Only then published to Maven Central.*

---

## Current release 18.0.0 — endgame theorem + unwinnability API

Breaking release. Adds the basic-checkmate-reachability theorem shortcut in the full analyzer, realigns the quick
analyzer to a faithful two-valued port of CHA 2.6.1, and simplifies the dead-position / game-end API (drops the
`DeadPosition*` enums, the `GameEndFacts` record, and the `Board.isDeadPosition*` / `isGameEnd` methods; splits the full
`WINNABLE` verdict). See CHANGELOG `[18.0.0]` for the full list. Tag + Central deploy follow the workflows.md procedure.

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

### Replace `EnumConstants` constant interface
`io.github.dlbbld.ashlarchess.common.constants.EnumConstants` is a `public interface` whose only purpose is to expose ~90 `public static final` aliases for `Square.*`, `Side.*`, `Piece.*`, `PieceType.*`, `Rank.*`, `File.*` so implementing classes inherit them unqualified. This is the classic "constant interface" anti-pattern (Effective Java item 22): interfaces should describe a contract/behavior, not be a convenience-inheritance vehicle for constants. The mechanism reads as beginner Java and leaks an internal vocabulary choice into the public type surface — `ChessBoard extends EnumConstants` is the clearest symptom (the chess contract has nothing to do with how implementers prefer to spell `Square.E4`). Used by 43 files under `src/main` plus tests.
