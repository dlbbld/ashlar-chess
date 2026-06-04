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
| `mvn test -Pfull` | Full regression suite. Sets `ashlar-chess.full=true`, which flips the [`RestrictTestConstants.IS_FULL`](src/test/java/io/github/dlbbld/ashlarchess/test/RestrictTestConstants.java) flag and re-enables the long-running audits. **Precondition for tagging a release.** |
| `mvn test -Dtest=TestClassName` | Single test class |
| `mvn test -Dtest=TestClassName#methodName` | Single test method |
| `mvn test -Dtest.excludes=` | Override the default exclusion (re-enable the unwinnability suite) |
| `mvn test -Pfull -Dtest.excludes=` | True full-suite run — full profile *plus* the unwinnability suite |

The long-running gates currently live in [`RestrictTestConstants`](src/test/java/io/github/dlbbld/ashlarchess/test/RestrictTestConstants.java) as `IS_EXCLUDE_LONG_RUNNING_*` constants. Each is driven by `!IS_FULL`, so flipping the `full` profile is the standard way to enable them.

### Running cross-validation oracles

The python-chess oracle reads pre-generated `.jsonl` files committed under `src/test/resources/oracle/python-chess/`. `mvn test` consumes them; the Python generator is only re-run when fixtures are added or regenerated. See `setup.md` and the generator module docstrings under `src/test/python/` for the regeneration procedure.

---

## Cutting a release

Release tags follow strict semver and match the `<version>` in `pom.xml`. The procedure is GitHub-PR-based, and the
order is load-bearing: a release title is chosen first and reused verbatim everywhere; artifacts are bumped before any
gate runs; the full release bundle is built and signed **on the branch** before the PR, so a packaging/signing failure
is fixed there rather than after the merge (direct pushes to `main` are not allowed, so a late failure forces a
brand-new branch + PR); the version bump must reach `main` before the tag; the tag must exist before the published
binary is built; and the irreversible Central Portal publish is always the very last step.

**Order at a glance:** name the release -> update artifacts on a branch -> push -> pre-flight (full tests + javadoc +
headers) -> `mvn -Prelease verify` (build+sign dry-run, on the branch, no upload) -> open the PR (titled with the
release title) -> merge to `main` -> delete the branch -> tag `main` (annotated, message = release title) ->
`mvn -Prelease deploy` (stages) -> review + publish on the Central Portal (irreversible) -> GitHub Release (version as
the title field, release title as the notes H1).

The detailed procedure:

### 1. Define the release title

Choose one short, human-readable **release title** for this version (for example "Endgame theorem and unwinnability
API"). It is set once, here, and then reused verbatim in four places so every surface tells the same story:

1. the `CHANGELOG.md` header (step 2),
2. the PR title (step 5),
3. the annotated tag's message (step 6),
4. the GitHub Release notes (step 8) - as the leading `# ` H1 of the body.

Keep the version and the release title in **separate slots**; never concatenate them into one string (no
"X.Y.Z Release Title" - it reads as clutter). The version lives where Git and GitHub attach it structurally (the
`[X.Y.Z]` `CHANGELOG.md` bracket, the annotated tag, the GitHub Release *title field*); the release title is the
human-readable heading shown next to it. So the PR title is the release title alone, and the GitHub Release sets its
*title field* to the version while the notes body opens with the release title as an H1.

The `tasks.md` "current release" heading is a good source - it already carries a one-line description of the release.

### 2. Update artifacts

Artifacts go next, so every gate below runs against the actual release version. Update the version string (single
change, no version drift):

- [`pom.xml`](pom.xml) line 9 — `<version>X.Y.Z</version>`
- [`README.md`](README.md) — both the Maven `<version>` snippet and the Gradle `implementation '...:ashlar-chess:X.Y.Z'` snippet

Add the `CHANGELOG.md` entry above `[Unreleased]`. The header carries the **release title** between the version and the
date - `## [X.Y.Z] - Release Title - YYYY-MM-DD`:

```markdown
## [X.Y.Z] - Release Title - YYYY-MM-DD

One-paragraph release summary.

### Notable
- Bullet per major change.

### Behavioral
- Bullet per behavior change visible to consumers, with migration notes.

### Breaking
- (Only if applicable.) Bullet per binary-incompatible change.
```

Browse prior entries in `CHANGELOG.md` for tone and depth. (Entries before this convention use the older
`## [X.Y.Z] - YYYY-MM-DD` header with no title; leave them as shipped.)

Move the relevant `tasks.md` section to **Done** at the bottom of the file.

Commit these on a release branch and push the branch - this is the content the PR ships. Do **not** tag yet.

### 3. Pre-flight

Run on the release branch, with the artifacts from step 2 already committed:

- Worktree is clean; everything intended for the release is committed.
- Java license headers exact: `.\tools\java-license-headers.ps1 -Check`. Use `-Fix` before committing if the check reports drift.
- `mvn test -Pfull` green. **Required.** (`-Pfull` keeps the default `test.excludes` unwinnability exclusion; add `-Dtest.excludes=` to also exercise that package - a release should.)
- JavaDoc gates green. **Required.** Both goals must run with `-Dshow=private` — many main classes (the `io.github.dlbbld.ashlarchess.report` records, package-private helpers) and all test classes are package-private, and at javadoc's default `protected` visibility doclint silently skips them, so stale `@link` / malformed HTML go uncaught:
  - `mvn javadoc:javadoc -Dshow=private` — all main docs.
  - `mvn javadoc:test-javadoc -Dshow=private` — all test docs.
  - (`mvn javadoc:jar` stays at default visibility — it ships only the public API.)
- All tasks for the release are marked done in `tasks.md`.

### 4. Release build dry-run (on the branch, before the PR)

**This is the step that must happen on the branch.** Build and GPG-sign the full release bundle locally with **no
upload**, so any packaging/signing failure is caught while it can still be fixed on the branch with another commit.
After the merge it is expensive to fix: direct pushes to `main` are not allowed, so a failure found later forces a new
branch + PR.

```
mvn -Prelease help:active-profiles   # confirm the `release` profile is active
mvn -Prelease verify                 # build + GPG-sign main/sources/javadoc jars; full dry run of the bundle (no upload)
```

`verify` (and `deploy` in step 7) need the GPG signing passphrase. It is **not** stored on disk — gpg-agent / Pinentry prompts for it at sign time. The signing key (`6A4D42B96FD6045B`, RSA 4096) must be in the local keyring and published to `keyserver.ubuntu.com`; Portal credentials (user token) live in `~/.m2/settings.xml` under `<server><id>central</id>`.

If `verify` fails, fix it on the branch (new commit), then re-run pre-flight + this dry-run. Only open the PR once this is green.

### 5. Open the PR and merge to main

The branch is now fully validated (pre-flight + release dry-run green). Done on the GitHub website, no command line needed:

- Open a pull request from the release branch into `main`. **PR title = the release title** from step 1.
- Review, then **merge** it. After merging, **delete the branch**.

The version bump must be on `main` (carried in by the merge) before you tag, so the tag and the published artifact
reference the exact same commit. Do not tag the release branch.

### 6. Tag the release on main

The tag goes on `main`, after the merge.

- Pull `main` locally so your checkout is exactly the merged release commit.
- Create an **annotated** tag `X.Y.Z` on `main`'s HEAD whose **message is the release title** from step 1, then push it.
  In Eclipse: Git Repositories view -> the repo -> right-click `Tags` -> Create Tag (or right-click the commit in
  History -> Create Tag); enter `X.Y.Z` as the tag name and the **release title** in the message/description field, then
  Push Tags. Equivalent command line: `git tag -a X.Y.Z -m "Release Title" && git push origin X.Y.Z`.
- The tag is annotated so it carries the release title as its message; no GPG signing of the tag itself is required by
  repo policy. (Releases before this convention used unannotated tags; from here on, tags are annotated.)

Tag **before** the Maven deploy in the next step: the artifact is built from this commit, so the tag and the published
jar are provably the same source.

### 7. Publish to Maven Central

Run from your local `main` checkout at the tag you just created - the artifact must be built from the tagged commit.
The release bundle was already built and signed on the branch in step 4, so this step is just **stage -> review ->
publish**, and only the final publish is irreversible.

Distribution is the Sonatype Central Portal via `central-publishing-maven-plugin`, wired into the `release` profile alongside GPG signing and the sources / javadoc jars. `mvn -Prelease deploy` is the only Central-aware command — it builds + signs the main / sources / javadoc jars and uploads a single staged deployment. (It prompts for the same GPG passphrase as the step-4 dry-run.)

```
mvn -Prelease deploy                 # uploads a staged deployment to the Central Portal
```

`autoPublish=false`, so the upload **stages** but does not go live. Approve it manually at <https://central.sonatype.com/publishing/deployments>:

- Review the staged contents (the main + sources + javadoc jars, their `.asc` signatures, and the flattened POM) before releasing. Releasing is the one immutable, irreversible step — once released, the `groupId:artifactId:version` triple is permanent and cannot be changed or unpublished.

### 8. GitHub Release

Create the GitHub Release for the tag you pushed in step 6 (GitHub website -> Releases -> Draft a new release):

- **Choose the existing tag** `X.Y.Z` (do not create a new one - it already exists on `main`).
- **Release title field = `X.Y.Z`** (the version; GitHub also defaults the title to the tag if left blank). This echoes
  the tag label rather than repeating the release title.
- **Notes body opens with `# <release title>`** - the release title from step 1 as the body's leading H1, followed by
  the `CHANGELOG.md` `[X.Y.Z]` notes (summary + Notable / Behavioral / Breaking). The page then reads "X.Y.Z" as the
  release heading with the descriptive release title as the first H1 of the notes beneath it. The GitHub Release is the
  public, human-facing copy of the changelog entry; keep the two consistent.

### 9. Post-release

- Verify the artifact resolves at <https://central.sonatype.com/artifact/io.github.dlbbld/ashlar-chess> before announcing (index propagation can take minutes to a couple of hours).
- Archive the shipped release in `tasks.md`: move its section to **Done** at the bottom (or collapse it to a one-line "X.Y.Z — published YYYY-MM-DD, see CHANGELOG"). The recurring procedure lives here in workflows.md and the consumer-facing summary in CHANGELOG.md, so the granular one-time checklist can be pruned without losing anything.

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
