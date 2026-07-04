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

Both emit lines of the shape `list.add(new PgnFen("file.pgn", "endPositionFen"));`.

Run from Eclipse (Run As → Java Application) or from the command line:

`mvn -q exec:java -Dexec.mainClass=io.github.dlbbld.ashlarchess.test.generate.GenerateTestCaseForPgn -Dexec.classpathScope=test`

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

Release tags follow strict semver and match the `<version>` in `pom.xml`. Use the runbook first; read the explanations
only when you need context.

### Release runbook

1. **Clear Eclipse warnings and infos**
   - Problems view: 0 errors, 0 warnings, 0 infos.
   - Fix warnings instead of suppressing them.
   - Add missing `package-info.java` / `@NonNullByDefault` when JDT reports a missing default nullness annotation.

2. **Format, clean up, and regenerate generated docs**
   - Eclipse: select `src/main/java` and `src/test/java`.
   - Run Source -> Format.
   - Run Source -> Clean Up.
   - Regenerate README/manual examples: `mvn -o -q test-compile exec:java -Dexec.mainClass=io.github.dlbbld.ashlarchess.test.readme.GenerateReadme -Dexec.classpathScope=test`.

   - Review the diff as mechanical only.
   - Commit this cleanup separately before the version bump.

3. **Choose the release title**
   - Pick one short title, for example `Endgame theorem and unwinnability API`.
   - Reuse it verbatim in the changelog header, PR title/body, annotated tag message, and GitHub Release notes body.

4. **Update release artifacts on a release branch**
   - Update [`pom.xml`](pom.xml): `<version>X.Y.Z</version>`.
   - Update Maven version snippets in [`README.md`](README.md) and [`manual.md`](manual.md).
   - Add the `CHANGELOG.md` entry above `[Unreleased]`.
   - Changelog header: `## [X.Y.Z] - Release Title - YYYY-MM-DD`.
   - Include a one-paragraph release summary.
   - Add `### Notable`, `### Behavioral`, and `### Breaking` sections as needed.

   - Move the relevant `tasks.md` section to **Done**.
   - Commit and push the release branch.
   - Do **not** tag yet.

5. **Run pre-flight on the release branch**
   - Confirm the worktree is clean.
   - Check Java license headers: `.\tools\java-license-headers.ps1 -Check`.
   - If needed, fix and commit header drift: `.\tools\java-license-headers.ps1 -Fix`.
   - Run full tests: `mvn test -Pfull`.
   - Also exercise the normally excluded unwinnability package for a release: `mvn test -Pfull -Dtest.excludes=`.
   - Run JavaDoc gates from a clean `target/`: `mvn clean javadoc:javadoc javadoc:test-javadoc -Dshow=private`.

   - Confirm all release tasks are done in `tasks.md`.
   - If board logic changed materially, run the board burn-in and update the baseline: `mvn -o -q exec:java -Dexec.classpathScope=test -Dexec.mainClass=io.github.dlbbld.ashlarchess.test.performance.BoardApiBurnInSurvey`.

6. **Run the release build dry-run on the branch**

   - Confirm the release profile: `mvn -Prelease help:active-profiles`.
   - Verify the release build: `mvn -Prelease verify`.

   - If this fails, fix it on the branch, commit, then re-run pre-flight and the dry-run.
   - Open the PR only after this is green.

7. **Open the PR and merge to main**
   - PR target: `main`.
   - PR title field: release title only, no version.
   - PR body starts with `## <release title>`.
   - Merge the PR.
   - Delete the release branch.

8. **Tag the release on main**
   - Pull `main` locally so it is exactly the merged release commit.
   - Create an annotated tag `X.Y.Z` on `main` HEAD.
   - Tag message: release title.
   - Push the tag.

   Command-line equivalent: `git tag -a X.Y.Z -m "Release Title"`, then `git push origin X.Y.Z`.

9. **Publish to Maven Central**
   - From local `main` at the tagged commit: `mvn -Prelease deploy`.

   - Review the staged deployment at <https://central.sonatype.com/publishing/deployments>.
   - Publish only after the staged contents look correct.

10. **Create the GitHub Release**
    - Choose the existing tag `X.Y.Z`.
    - Release title field: `X.Y.Z`.
    - Notes body starts with `# <release title>`.
    - Paste/adapt the `CHANGELOG.md` `[X.Y.Z]` entry below that H1.

11. **Post-release**
    - Verify the artifact resolves at <https://central.sonatype.com/artifact/io.github.dlbbld/ashlar-chess>.
    - Archive the shipped release in `tasks.md`.

### Release explanations

#### Order matters

The release procedure is GitHub-PR-based, and the order is load-bearing: the working tree is cleared of Eclipse warnings
and brought to a uniform auto-formatted / cleaned-up state; a release title is chosen up front and reused verbatim
everywhere; artifacts are bumped before any gate runs; the full release bundle is built and signed **on the branch**
before the PR, so a packaging/signing failure is fixed there rather than after the merge; the version bump must reach
`main` before the tag; the tag must exist before the published binary is built; and the irreversible Central Portal
publish is always the very last step.

Direct pushes to `main` are not allowed, so a packaging/signing failure found after merge forces a brand-new branch and
PR. That is why the release dry-run happens before the PR.

#### Clean source first

A release ships from a clean compiler state **and** a uniformly formatted source tree. The project should have **zero
Eclipse / JDT warnings and zero info messages** in the Problems view before the release artifacts are bumped.

Warnings include unused imports / locals, raw types, missing `@Override`, dead code, narrowing conversions, and similar
cleanup. Info messages are most often JDT null-analysis notes such as "A default nullness annotation has not been
specified", raised when a package lacks its `package-info.java` carrying `@NonNullByDefault`.

Auto-format can reflow the sliced example bodies in
[`ReadmeExamples.java`](src/test/java/io/github/dlbbld/ashlarchess/test/readme/ReadmeExamples.java). Since `README.md`
and `manual.md` are verbatim renders of those slices, regenerate them after formatting. A plain `mvn test-compile` does
**not** catch stale generated Markdown; `mvn test` catches it through `TestReadmeUpToDate`.

The clean-up commit should be purely mechanical and behavior-preserving. The first release after this step was
introduced may have a larger diff as accumulated drift is normalized; steady-state releases should produce little or
nothing.

#### Release title

Keep the version and the release title in **separate slots**. Do not concatenate them as `X.Y.Z Release Title`.

The version lives where Git and GitHub attach it structurally: the `[X.Y.Z]` `CHANGELOG.md` bracket, the annotated tag,
and the GitHub Release title field. The release title is the human-readable heading shown next to it. The PR title is
the release title alone, and the GitHub Release sets its title field to the version while the notes body opens with the
release title as an H1.

The `tasks.md` "current release" heading is often a good source for the release title.

#### Changelog entry

Browse prior entries in `CHANGELOG.md` for tone and depth. Entries before this convention use the older
`## [X.Y.Z] - YYYY-MM-DD` header with no title; leave them as shipped.

#### JavaDoc gates

The pre-flight JavaDoc command must run both main and test docs with `-Dshow=private`: many main classes and all test
classes are package-private, and at javadoc's default `protected` visibility doclint silently skips them. Running from a
clean `target/` avoids a misleading `error: No source files for package <some random package>` caused by stale state.

The pre-flight report goal does **not** prove the shipped javadoc jar builds. It runs at `-Dshow=private`; the released
`javadoc:jar` runs at default visibility and can fail where this passes, notably on a **type-less package** whose only
`.java` file is `package-info.java`. JDK 21's `javadoc` rejects that with `error: No source files for package X`. The
step-6 release dry-run catches this. Rule: never leave a `package-info`-only package - a package must either carry types
or have no `package-info.java` at all.

#### Board burn-in

The board performance regression check is required when board logic changed materially: move / unmove, the
per-position `BoardState` record, repetition tracking, `hashCode` / `equals`, or legal-move caching.

Compare the per-method `us/ply` and scaling `ratio` against
`src/test/java/io/github/dlbbld/ashlarchess/test/performance/board-burn-in-baseline.md`. No scalar/boolean accessor may
turn superlinear: a `ratio` that rises with game length is O(history) and is the regression to catch.

Absolute `us/ply` values are machine-relative. For a real before/after, build the previous release from its tag in a
worktree (`git worktree add --detach ../ashlar-<prev> <tag>`), port the survey's renamed method names if the Board API
changed, and run both in the **same boot session**. Append the new release's numbers and the comparison verdict to the
baseline file.

#### Release dry-run and signing

`mvn -Prelease verify` builds and GPG-signs the full release bundle locally with **no upload**. This is also the only
pre-merge gate that builds the shipped `javadoc:jar` at default visibility and the GPG signatures.

`verify` and `deploy` need the GPG signing passphrase. It is **not** stored on disk; gpg-agent / Pinentry prompts for it
at sign time. The signing key (`6A4D42B96FD6045B`, RSA 4096) must be in the local keyring and published to
`keyserver.ubuntu.com`. Portal credentials (user token) live in `~/.m2/settings.xml` under
`<server><id>central</id>`.

#### PR, tag, and deploy ordering

The version bump must be on `main` before tagging, so the tag and the published artifact reference the exact same
commit. Do not tag the release branch.

The tag is annotated so it carries the release title as its message; no GPG signing of the tag itself is required by
repo policy. Releases before this convention used unannotated tags; from here on, tags are annotated.

Tag **before** Maven deploy: the artifact is built from the tagged commit, so the tag and the published jar are provably
the same source.

#### Maven Central publishing

Distribution is the Sonatype Central Portal via `central-publishing-maven-plugin`, wired into the `release` profile
alongside GPG signing and the sources / javadoc jars. `mvn -Prelease deploy` builds and signs the main / sources /
javadoc jars and uploads a single staged deployment.

`autoPublish=false`, so the upload **stages** but does not go live. Review the main + sources + javadoc jars, their
`.asc` signatures, and the flattened POM before releasing. Releasing is immutable and irreversible: once released, the
`groupId:artifactId:version` triple is permanent and cannot be changed or unpublished.

#### GitHub Release shape

The GitHub Release is the public, human-facing copy of the changelog entry. Use the existing tag `X.Y.Z`; do not create
a new one. Set the Release title field to `X.Y.Z`, and open the notes body with `# <release title>` followed by the
`CHANGELOG.md` `[X.Y.Z]` notes.

#### Post-release archive

Index propagation can take minutes to a couple of hours. Verify the Central artifact before announcing.

Archive the shipped release in `tasks.md`: move its section to **Done** at the bottom, or collapse it to a one-line
`X.Y.Z - published YYYY-MM-DD, see CHANGELOG`. The recurring procedure lives here in `workflows.md`, and the
consumer-facing summary lives in `CHANGELOG.md`, so the granular one-time checklist can be pruned without losing
anything.

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
