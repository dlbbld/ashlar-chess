# Coding conventions

## Java style

We follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html), enforced with Checkstyle and auto-formatting on save within Eclipse, with a small number of project-specific tightenings.

The active Checkstyle configuration is named *Google checks adapted* (referenced from [`.checkstyle`](.checkstyle)). It's the *Google Checks* preset that ships with the Eclipse Checkstyle plugin, with project-local edits.

### Project-specific tightenings

**Method names.** Pure camelCase is the default. The single exception is test methods with a tag prefix — a short alphanumeric label of letters followed by digits (`v01`, `r3`, `t003`), separated from the description by an underscore: `v01_pregameCommentaryOnly`. The underscore disambiguates the digit-letter boundary and marks the boundary between test metadata and test description.

Underscore between two camelCase phrases is **not** allowed: `postTermination_x` and `parseAgreesAcrossParsers_x` are rejected.

Enforced by:

```
^(?:[a-z]+[0-9]+_)?[a-z][a-z0-9][a-zA-Z0-9]*$
```

**JUnit method references.** Avoid hardcoded method names as string arguments in JUnit annotations (e.g. `@MethodSource("supplierName")`). Prefer the no-argument `@MethodSource` (which picks up a static method with the same name as the test) or `@ArgumentsSource(Provider.class)` — both are compiler-checked. The string form silently drifts from the method name under refactor and is only caught at test runtime. Allow only where the alternatives are materially worse.

## Chess naming

One player's action is a **move**. Use `move`, `played move`, `moveIndex` (0-based list index), and `performedMoveCount` (1-based count of played moves) in the domain model, move history, reports, PGN parsing/writing, tests, JavaDoc, and user-facing messages.

Do not use standalone `halfmove`, `half-move`, `half move`, `ply`, or `plies` for a played move. The only exceptions are explicitly protocol- or engine-scoped:

- **FEN/PGN protocol counters.** In prose, use the standards spelling `halfmove clock` and `fullmove number`, with `halfmove` / `fullmove` always one word — including standalone and plural uses (`halfmoves`, `fullmoves`) — never `half move` / `full move` / `half-move` / `full-move`. In Java identifiers, keep normal Java word boundaries: `halfMoveClock`, `fullMoveNumber`, `HALF_MOVE_CLOCK`, `FULL_MOVE_NUMBER`. This is an intentional split: prose follows the protocol field name; code follows Java naming.
- **PGN `PlyCount` tag.** Keep `PlyCount` / `StandardTag.PLY_COUNT` because that is the tag name.
- **Engine/search internals.** `ply` is allowed for search depth, per-depth buffers, make/unmake stacks, and engine-style prose such as "3-ply search". Do not let it leak into `Board`, move history, PGN model, or report vocabulary.
- **External mirrors.** Test fixtures or adapters that deliberately mirror an external library or data contract may keep that source's vocabulary, for example python-chess `perPly` data or chesslib half-move naming.

When in doubt, ask whether the term names a public chess-library concept or a protocol field. Public concept: `move`. Protocol field: `halfmove clock` / `fullmove number` in prose, `halfMoveClock` / `fullMoveNumber` in code.

## Records carry data, not behavior

Records are immutable value objects (the M in MVC). Computational and business logic that operates on them lives in dedicated utility/service classes — never on the record itself.

Allowed on records:

- **Compact-constructor validation and defensive copying.** The "errors at the construction boundary" pattern. `PgnCommentary` is the canonical example: its compact constructor validates the brace/control-character contract of PGN commentary content, so any `PgnCommentary` instance is by construction valid. `PgnGame` defensively copies its list components in the compact constructor.
- **`Comparable` when the ordering is intrinsic to the type's identity.** `Tag` orders by standard-roster position. `MoveSpecification` orders by `(fromSquare, toSquare, castlingMove, promotionPieceType)`. Ordering that's a property of the type itself (not "one possible sort key out of several") fits on the record.
- **Static factory constants for meaningful empty/initial values.** `PgnCommentary.EMPTY`. These are part of the type's value lattice, not behavior.
- **Auto-generated `equals` / `hashCode` / `toString`** — the language provides these from the record's components. Don't override.

Not allowed on records:

- **Domain-operation methods that compute results from the record's fields combined with anything else.** Those belong in the relevant utility class. *Example: don't put `staticPosition.afterMove(side, moveSpec)` on `StaticPosition`; put `StaticPositionUtility.createPositionAfterMove(staticPosition, side, moveSpec)` instead.* Records describe *what something is*; utilities describe *what you do with it*.
- **Convenience constructors that hide validation logic in non-canonical paths.** Validation belongs in the canonical compact constructor, not scattered across overloads — otherwise a caller can bypass validation by reaching for the canonical constructor directly.
- **Methods that simulate state changes** (records are values, not actors).

## JavaDoc and comments

JavaDoc should document contracts that are not obvious from the declaration: public API semantics, invariants, rule decisions, edge cases, and non-obvious test intent.

Do not write JavaDoc that merely restates names, call chains, package structure, filesystem location, or implementation facts that IDE navigation already provides. In particular, avoid comments whose content would become stale after a routine rename or refactor.

For tests, prefer descriptive test names and assertions over class-level JavaDoc. Add test JavaDoc only when the test protects a non-obvious rule, regression, or design decision that cannot be inferred from the test body.

Rule of thumb: if an AI or IDE could regenerate the comment from the code, the comment is a maintenance liability.

## ASCII-only source

Java source files are ASCII-only. This applies to comments, JavaDoc, and string literals: use plain ASCII rather than "smart" Unicode punctuation. Replace em/en-dashes with `-`, curly quotes with straight `"`/`'`, ellipsis with `...`, arrows (`->`, `<->`, `=>`) with their ASCII forms, the section sign with `section`, and the multiplication sign with `x`.

Rationale: non-ASCII punctuation is silently prone to double-encoding mojibake — a file read as Windows-1252 and re-saved as UTF-8 turns `—` into `â€"` and corrupts the source without any compiler complaint. Keeping source ASCII removes the entire failure mode. (The 16.1.0 release repaired ~180 such sequences and normalized the tree.)

The only permitted non-ASCII is where a character is *functionally* the subject under test or a runtime feature, not incidental typography. The current exceptions, which must stay non-ASCII:

- `LenientFenParser` — the Unicode-dash recognition table (the parser exists to forgive real Unicode dashes in FEN input).
- `TestLenientFenParser` — non-standard-dash FEN inputs that exercise that table.
- The UTF-8 round-trip / commentary corpora: `TestFileUtility`, `TestPgnExportUtf8`, `TestLenientPgnParserUtf8`, `TestPgnCommentary`.
- Genuine letters in proper nouns inside test data (e.g. `í` in a player name).

When non-ASCII is genuinely required, it should be obviously intentional test/feature data — never a stray em-dash or curly quote in prose.

## Eclipse compiler warnings

All code must compile under the project's Eclipse JDT compiler settings with **zero warnings**. Many diagnostics are configured as errors rather than warnings — null-annotation violations, unused imports, missing `@Override`, raw types, etc. Treat the warning ceiling as a hard rule, not a guideline: fix the cause, do not silence with `@SuppressWarnings` unless the reason is genuinely localized and documented inline.

## Setup

The Checkstyle XML rules (`checkstyle.xml`, `checkstyle-suppressions.xml`, `checkstyle-xpath-suppressions.xml`) and the Eclipse JDT compiler / formatter / cleanup / save-actions settings (`.settings/org.eclipse.jdt.core.prefs`, `.settings/org.eclipse.jdt.ui.prefs`) are checked into the repo and auto-loaded after import. See [setup.md](setup.md) for the install procedure.
