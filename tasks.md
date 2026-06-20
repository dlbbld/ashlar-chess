# Tasks

Live planning only: current release work, backlog, and obsolete decisions. Shipped release history lives in
**CHANGELOG.md**; recurring procedures live in **workflows.md**. Order within each live section is the source of truth.

---

## The story when releases are done

*ashlar-chess started as a correctness-first reference implementation, built from the FIDE rules without consulting existing libraries. It found correctness bugs in python-chess and ScalaChess along the way. Once the rules were stable, a bitboard backend was added alongside the reference layer and verified bit-exact against it. Production then switched to the bitboard path; the reference layer was relocated into the test tree and remains as the permanent differential-test oracle. Cross-validation against python-chess was reactivated as primary, with `chesslib` retained as a second witness. Only then published to Maven Central.*

---

## 20.0.0 — JPMS module boundary / API-surface reset

The dedicated module-and-API-boundary release. Breaking package / FQN changes that don't belong in 19.0.0 are parked here.

### Group exceptions, models, enums, and constants by domain (package-by-feature)

The codebase currently mixes "group by kind" with "feature-inline", and *duplicates* the by-kind buckets at two levels — `ashlarchess.exceptions` **and** `ashlarchess.common.exceptions`; `ashlarchess.enums` **and** `common.enums`; `ashlarchess.model` **and** `common.model` — with no rule for which one a type lands in, plus single-file kind sub-packages (e.g. `fen.constants` holds one class). A newcomer cannot predict any type's package from a rule.

Rule to adopt: **package by feature/domain first; package by kind only for genuinely cross-cutting foundations.**

- Feature-specific exceptions / enums / models / constants live *inline* in their feature package (FEN's in `fen`, PGN's in `pgn`, SAN's in `san`) — beside the code they serve. Not in a central bucket, and not in `fen.exceptions`-style kind sub-packages either (keep them next to the parser, as agreed).
- Collapse the duplicate buckets: never both a top-level `<kind>` and a `common.<kind>`. Keep one shared-core home for genuinely cross-cutting types only — base exceptions (`UsageException`, `ProgrammingMistakeException`, `NonePointerException`), `ChessConstants`, the core chess vocabulary (`Side` / `Piece` / `Square` / …).
- No single-file kind sub-packages.
- Done = given any type, one rule predicts its package.

A large, purely mechanical, compiler-checked FQN reset with no behavior change, and a prerequisite for a clean `module-info`: sensible `exports` / `opens` and package-private boundaries are impossible while features are split across two `common.*` junk drawers. (The FEN-local slice — folding the FEN validation problem enum into `fen` and dropping the single-file `fen.constants` — was carved out for 19.0.0; this is the global reset across all domains.)

### Tighten remaining mutable return types on internal-but-public surfaces

A few `public static` helpers still return a freshly-built mutable `Set` / `List` typed as the mutable interface instead of a Guava `Immutable*`: `BitboardPositionUtility.toSquareSet(long)` (a `TreeSet` → `ImmutableSortedSet`), and the move-generation helpers `PromotionUtility.performPromotionMovements`, `CastlingUtility.performCastlingMovements`, `EnPassantCaptureUtility.performEnPassantCaptureMovements`, and the `EmptyBoardMoveUtility` overloads. Each returns a fresh per-call copy, so there is no aliasing bug today — tightening the declared type is pure polish. Parked here rather than 19.0.0 because these are exactly the internal-but-accidentally-public move-gen / bitboard surfaces this release narrows or hides behind the module boundary: fix the return types in the same pass that decides which of them stay public at all.

### Position-as-value ergonomics: `mirror()` and an immutable `play(move)` (audit M3)

Two related "treat a position as an immutable value" capabilities that Class-A libraries (python-chess, shakmaty, scalachess) expose and ashlar does not surface cleanly:

- **`mirror()` / flip / transform** — return a new position that is the original vertically flipped *and* colour-swapped (the same position from the other side; turn, castling, en-passant all mirrored). Valuable for symmetry tests (`f(mirror(P))` must equal the mirror of `f(P)` — catches colour-handedness bugs), dataset augmentation, and halving case analysis. ashlar has none at board level today, only `SquareUtility.flip(Square)`.
- **Clean immutable `play(move) → position`** — the next position as a fresh value, without mutating a `Board` or carrying its history. The machinery already exists immutably as `BitboardPosition.afterMove(...)`; it is just not surfaced as a clean public position API (today the only path is `copyCurrentPositionWithoutHistory()` then `move()`).

Low–medium effort (the immutable apply-move exists; mirror is a straightforward bitboard transform — vertical flip = byte-reverse the 12 longs, colour swap = swap the white/black bitboards). It belongs here because it is **additive public API**, to be designed together with the position-API boundary (what the clean public position type is once the bitboard layer is internalised) and the still-open "light-analysis toolkit?" direction — `play()` / `mirror()` are most valuable to analysis / ML users.

The one slice worth doing regardless of that direction, with no public-API commitment: an internal **mirror used by the test suite for symmetry checks** (e.g. unwinnability of `P` for a side ⟺ unwinnability of `mirror(P)` for the other) — pure added test coverage.

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
