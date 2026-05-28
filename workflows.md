# Developer workflows

Concrete how-tos for recurring tasks. See [CONTRIBUTING.md](CONTRIBUTING.md) for high-level "what to do and where to put things"; this file is the "how to actually do them."

---

## Adding a new PGN test fixture

The test corpus lives under `src/test/resources/pgn/<category>/`. Every PGN on disk must have a corresponding entry in [`PgnTestCaseCatalog`](src/test/java/io/github/dlbbld/ashlarchess/test/pgn/setup/PgnTestCaseCatalog.java) — the [`TestSetupPgnRegistration`](src/test/java/io/github/dlbbld/ashlarchess/test/pgn/setup/TestSetupPgnRegistration.java) test asserts the two stay in sync. Adding a fixture is therefore a three-step procedure: place the file, generate the catalog entry, paste it in.

### 1. Place the PGN file

Pick the right `<category>` folder. The taxonomy is documented by the [`PgnTest`](src/test/java/io/github/dlbbld/ashlarchess/test/pgntest/enums/PgnTest.java) enum — each entry maps a logical category to a folder under `src/test/resources/pgn/`. Common buckets:

- `basic/<feature>/` — focused unit fixtures per rule feature (checkmate, fivefold, intervening, etc.)
- `realGames/<category>/` — real-game PGNs
- `random/<category>/` — synthetic random games
- `cha/<sub-bucket>/` — CHA / unwinnability fixtures
- `edgeCases/<sub-bucket>/` — synthetic edge cases (max-moves, longest mate, etc.)
- `parserFenMechanics/` — PGNs with non-trivial `FEN` / `SetUp` tag mechanics

If no existing category fits, add one — see [_Adding a new corpus category_](#adding-a-new-corpus-category) below.

### 2. Generate the catalog entry

Two helpers under [`src/test/java/io/github/dlbbld/ashlarchess/test/generate/`](src/test/java/io/github/dlbbld/ashlarchess/test/generate/):

- [`GenerateTestCaseForPgn`](src/test/java/io/github/dlbbld/ashlarchess/test/generate/GenerateTestCaseForPgn.java) — emit the catalog line for a single PGN file. Set the file-name constant in the class, run `main`.
- [`GenerateTestCaseForPgnFolder`](src/test/java/io/github/dlbbld/ashlarchess/test/generate/GenerateTestCaseForPgnFolder.java) — emit catalog lines for every PGN in a folder. Set the `PGN_FOLDER_PATH` to the target `PgnTest` enum value, run `main`.

Both emit lines of the shape:

```java
list.add(new PgnFen("file.pgn", "endPositionFen"));
```

Run from Eclipse (Run As → Java Application) or from the command line:

```
mvn -q exec:java -Dexec.mainClass=io.github.dlbbld.ashlarchess.test.generate.GenerateTestCaseForPgn -Dexec.classpathScope=test
```

### 3. Paste the entry into PgnTestCaseCatalog

Find the `createTestCases<Category>` function matching the target `PgnTest` enum value in [`PgnTestCaseCatalog.java`](src/test/java/io/github/dlbbld/ashlarchess/test/pgn/setup/PgnTestCaseCatalog.java). Append the generated `list.add(...)` line in the natural sort order of the existing entries.

Run `mvn test -Dtest=TestSetupPgnRegistration` to confirm the corpus-vs-registry diff is now empty.

### Adding a new corpus category

If the target folder doesn't yet exist in `PgnTest`:

1. Add a new enum entry to [`PgnTest.java`](src/test/java/io/github/dlbbld/ashlarchess/test/pgntest/enums/PgnTest.java) — `MY_NEW_CATEGORY(false, "path/under/pgn")`. The first argument (`isBasicTest`) is `true` only for the per-feature unit buckets under `basic/`.
2. Add a `case MY_NEW_CATEGORY -> createTestCasesMyNewCategory();` line to the `switch` in `PgnTestCaseCatalog.calculateTestCaseList`. The `default` branch throws, so the `case` must be present before the test sources will compile.
3. Add a `createTestCasesMyNewCategory()` function returning `new PgnTestCaseList(PgnTest.MY_NEW_CATEGORY, list)`. Use `GenerateTestCaseForPgnFolder` to populate `list`.

---

## Running tests

The default `mvn test` runs the fast subset — most of the corpus, but with the long-running audit and oracle-sweep tests gated off. Defaults are tuned for iterative development.

| Command | Scope |
| --- | --- |
| `mvn test` | Default: most of the corpus, long-running audits gated off, `io.github.dlbbld.ashlarchess.test.unwinnability` excluded |
| `mvn test -Pfull` | Full regression suite. Sets `clean-chess.full=true`, which flips the [`RestrictTestConstants.IS_FULL`](src/test/java/io/github/dlbbld/ashlarchess/test/RestrictTestConstants.java) flag and re-enables the long-running audits. **Precondition for tagging a release.** |
| `mvn test -Dtest=TestClassName` | Single test class |
| `mvn test -Dtest=TestClassName#methodName` | Single test method |
| `mvn test -Dtest.excludes=` | Override the default exclusion (re-enable the unwinnability suite) |
| `mvn test -Pfull -Dtest.excludes=` | True full-suite run — full profile *plus* the unwinnability suite |

The long-running gates currently live in [`RestrictTestConstants`](src/test/java/io/github/dlbbld/ashlarchess/test/RestrictTestConstants.java) as `IS_EXCLUDE_LONG_RUNNING_*` constants. Each is driven by `!IS_FULL`, so flipping the `full` profile is the standard way to enable them.

### Running cross-validation oracles

The python-chess oracle reads pre-generated `.jsonl` files committed under `src/test/resources/oracle/python-chess/`. `mvn test` consumes them; the Python generator is only re-run when fixtures are added or regenerated. See `setup.md` and the generator module docstrings under `src/test/python/` for the regeneration procedure.

---

## Cutting a release

Release tags follow strict semver and match the `<version>` in `pom.xml`. The release procedure:

### 1. Pre-flight

- Active branch is on the release-candidate state (worktree is clean; everything intended for the release is merged or committed).
- Java license headers exact: `.\tools\java-license-headers.ps1 -Check`. Use `-Fix` before committing if the check reports drift.
- `mvn test -Pfull` green from a clean checkout. **Required.**
- JavaDoc gates green. **Required.** Both goals must run with `-Dshow=private` — many main classes (the `io.github.dlbbld.ashlarchess.report` records, package-private helpers) and all test classes are package-private, and at javadoc's default `protected` visibility doclint silently skips them, so stale `@link` / malformed HTML go uncaught:
  - `mvn javadoc:javadoc -Dshow=private` — all main docs.
  - `mvn javadoc:test-javadoc -Dshow=private` — all test docs.
  - (`mvn javadoc:jar` stays at default visibility — it ships only the public API.)
- All tasks for the release are marked done in `tasks.md`.

### 2. Update artifacts

Update the version string in three places (single change, no version drift):

- [`pom.xml`](pom.xml) line 9 — `<version>X.Y.Z</version>`
- [`README.md`](README.md) — both the Maven `<version>` snippet and the Gradle `implementation '...:clean-chess:X.Y.Z'` snippet

Add the `CHANGELOG.md` entry above `[Unreleased]`, following the established format:

```markdown
## [X.Y.Z] - YYYY-MM-DD

One-paragraph release summary.

### Notable
- Bullet per major change.

### Behavioral
- Bullet per behavior change visible to consumers, with migration notes.

### Breaking
- (Only if applicable.) Bullet per binary-incompatible change.
```

Browse prior entries in `CHANGELOG.md` for the tone and depth.

Move the relevant `tasks.md` section to **Done** at the bottom of the file.

### 3. Commit + tag + push

```
git add pom.xml README.md CHANGELOG.md tasks.md
git commit -m "X.Y.Z release artifacts: pom + README + CHANGELOG"
git tag X.Y.Z
git push origin <branch>
git push origin X.Y.Z
```

The tag is unannotated (matches the convention of prior tags) — no signing required by repo policy.

### 4. Post-release

If publishing to a registry (JitPack picks up tags automatically; Maven Central is the future destination), verify the artifact resolves at the published coordinates before announcing.

### Version bumps

- **Major (X.0.0)** — binary-incompatible: API removal / signature change / observable behavior change at the move pipeline.
- **Minor (X.Y.0)** — backward-compatible additions (new methods, new test infrastructure, new oracle integrations).
- **Patch (X.Y.Z)** — non-breaking fixes.

The project has historically used major bumps liberally during the pre-Maven-Central phase; once published to Central, minor / patch bumps will be the norm.

---

## Related docs

- [CONTRIBUTING.md](CONTRIBUTING.md) — entry-point contributor guide
- [setup.md](setup.md) — first-time Eclipse / JDK install
- [coding-conventions.md](coding-conventions.md) — code style
- [specification.md](specification.md) — chess-rule semantics
- [agents.md](agents.md) — commit-message convention
