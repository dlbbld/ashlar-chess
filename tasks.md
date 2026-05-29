# Tasks

Order within each section is the source of truth. Completed tasks move to **Done** at the bottom.

---

## The story when releases are done

*ashlar-chess started as a correctness-first reference implementation, built from the FIDE rules without consulting existing libraries. It found correctness bugs in python-chess and ScalaChess along the way. Once the rules were stable, a bitboard backend was added alongside the reference layer and verified bit-exact against it. Production then switched to the bitboard path; the reference layer was relocated into the test tree and remains as the permanent differential-test oracle. Cross-validation against python-chess was reactivated as primary, with `chesslib` retained as a second witness. Only then published to Maven Central.*

---

## Current release 17.0.0 — publish to Maven Central

The capstone release. Publish to Central only when the library has stabilised - every
prior release done, identity questions settled, and any tasks that surface during the
prerequisite work itself addressed first. Maven Central artifacts are immutable: once
published, a groupId+artifactId+version triple lives forever in the public record, and
the Java package names become part of the public API contract. The bar for moving from
JitPack to Central is "we are confident this artifact represents the project well,
indefinitely."

Identity decision (settled): the project's full public identity becomes `ashlar-chess`
everywhere it is the project's own to name - GitHub repo `dlbbld/ashlar-chess`,
coordinate `io.github.dlbbld:ashlar-chess`, Java packages `io.github.dlbbld.ashlarchess.*`.
The old `com.dlb.chess` package is dropped: it reverse-maps to `dlb.com`, a domain owned
by a third party, so it was never a namespace this project could legitimately claim. All
identity churn is done on this branch, before the first Central publish - the one window
where it is cheap (no consumers exist yet).

### Step 0 — Repository rename (do now, on this branch)
- [x] Rename GitHub repo `dlbbld/clean-chess` -> `dlbbld/ashlar-chess` (Settings -> repository name)
- [x] `git remote set-url origin https://github.com/dlbbld/ashlar-chess.git` in the main
      checkout (covers worktrees - they share repo config; optional, redirects work)
- [x] Keep the LOCAL directory `C:\Users\danie\git\clean-chess` unchanged - only the
      GitHub remote is renamed (Eclipse project + worktree paths key off the local name)
- Cautions: never recreate a `dlbbld/clean-chess` repo later (breaks redirects); do not
  cut a JitPack release while `main` still says `clean-chess` - finish the PR first.

### Prerequisites
- [x] Add SPDX header to each Java file. ASCII-only per coding-conventions.md, so the
      two-line GPL form uses `Baechli`, not the umlaut:
        // Copyright (C) 2020-2026 Daniel Baechli
        // SPDX-License-Identifier: GPL-3.0-only
      (SPDX-only is the lighter alternative if you prefer no copyright line in source.)
      Java-only phase - if SPDX is later extended to XML/properties/Markdown, each file
      type needs its own comment syntax, NOT `//`.
- [x] Add `tools/java-license-headers.ps1` to check/fix the exact Java header before releases
- [x] Eclipse template adding the chosen header on every new Java class (obsolete as adds second maintenance location)
- [x] Rename decision resolved: ashlar-chess is the final name (executed in Step 0 + below)
- [x] DeepSquare / Bitboard / python-chess prior releases complete
- [x] Mention that chesslib is used for testing
- [x] Update README.md (jitpack no longer used)
- [ ] Final maturity re-check at publish time

### pom.xml — coordinates + metadata (do now on this branch)
- [x] `<groupId>` `com.github.dlbbld` -> `io.github.dlbbld`
- [x] `<artifactId>` `clean-chess` -> `ashlar-chess`
- [x] Add `<name>ashlar-chess</name>`, `<description>`, `<url>` (github.com/dlbbld/ashlar-chess)
- [x] Add `<inceptionYear>2020</inceptionYear>`
- [x] Add `<licenses>` (GPL-3.0-only, full URL, distribution=repo)
- [x] Add `<developers>`
- [x] Add `<scm>` -> ashlar-chess connection/developerConnection/url
- `<version>` already valid semver - no change

### Java package rename — `com.dlb.chess.*` -> `io.github.dlbbld.ashlarchess.*` (do now, own phase)
- [x] Eclipse Refactor -> Rename Package with "rename subpackages" across the WHOLE project
      (src/main AND src/test - the relocated StaticPosition oracle is also under com.dlb.chess)
- [x] Each `package-info.java` (with `@NonNullByDefault`) moves with its package - verify none lost
- [x] Sweep NON-Java references (refactor will not catch these):
      - log4j2 config (logger names / package paths)
      - checkstyle.xml + suppressions (package-scoped rules / regex paths)
      - src/main/resources + src/test/resources (any package path or key)
      - reflection string literals (Class.forName, getResource by package path)
      - test-fixture resource paths + generated-test-case references
      - README / Javadoc / specification.md / coding-conventions.md / workflows.md snippets
      FOLLOW-UP FIX: the ResourceBundle backing file was missed on the first pass - it
      was still at src/main/resources/com/dlb/chess/messages/messages.properties while
      Message loads it by package name (io.github.dlbbld.ashlarchess.messages.messages),
      so it failed at runtime with MissingResourceException. Moved (git mv) to
      src/main/resources/io/github/dlbbld/ashlarchess/messages/messages.properties.
      Verified: TestSanValidationProblemMessage green (22/22). No other src refs to the
      old package path remain.
- [x] Verify: full profile green (`mvn -Pfull -Dtest.excludes= test`) + javadoc:
        mvn javadoc:javadoc -Dshow=private
        mvn javadoc:test-javadoc -Dshow=private
      -> full suite green after the messages-bundle fix: 1266 tests, 0 failures/errors,
         1 skipped (incl. unwinnability via -Dtest.excludes=). Both javadoc gates green
         (no errors/warnings) - no stale @link refs from the rename.

### Copyright alignment (do now)
- [x] LICENSE copyright `2024-2026` -> `2020-2026` (LICENSE/README keep the umlaut spelling)
- [x] SPDX/copyright headers (prerequisite above) applied consistently after the package rename

### pom.xml — required plugins (all in a new `release` profile)
- [x] central-publishing-maven-plugin (`extensions=true`; in the `release` profile with
      signing + javadoc + sources, so `mvn -Prelease deploy` is the only Central-aware command)
      -> version 0.7.0, publishingServerId `central`, autoPublish=false (stages for manual review)
- [x] maven-gpg-plugin (sign) -> version 3.2.7, sign goal bound to `verify`; no passphrase in
      the pom (gpg-agent / Pinentry supplies it at deploy time)
- [x] maven-javadoc-plugin `jar` execution (plugin already configured globally for validation;
      add the jar goal in the profile) -> `attach-javadocs`, inherits global doclint/failOnError
- [x] move maven-source-plugin's `jar` execution into the `release` profile too (currently
      global) - keeps normal `mvn package` Java-only and the whole release bundle in one place
- [x] keep the jitpack `<repositories>` block (needed for the chesslib test dependency, which is
      not on Maven Central) but ensure the Central-published pom carries no `<repositories>` -
      flatten-maven-plugin or a build-only profile
      -> flatten-maven-plugin 1.6.0, oss mode + `<pomElements><repositories>remove`. Verified:
         `mvn -Prelease -DskipTests package` is green; .flattened-pom.xml keeps name/description/
         url/licenses/developers/scm and the 4 compile deps, drops test deps and <repositories>.
         (signing/deploy not exercised here - they need the passphrase + live Portal upload)

### README / docs (this branch)
- [x] Replace project-name mentions clean-chess -> ashlar-chess
- (JitPack-block -> Central-snippet swap stays a publish-time task below)

### Sonatype Central Portal setup (publish time, manual - your account)
- [x] Create Sonatype Central account at https://central.sonatype.com, sign in via GitHub
- [x] Confirm the `io.github.dlbbld` namespace is verified. GitHub sign-in usually
      auto-provisions it; if not, follow Sonatype's GitHub namespace verification flow.
- [x] Generate a GPG key, record the keyID
      -> RSA 4096 [SC], uid "Daniel Bächli <daniel.baechli@gmail.com>", created 2026-05-29
      -> key ID 6A4D42B96FD6045B
- [x] Publish the public key to a keyserver and confirm it resolves:
        gpg --keyserver keyserver.ubuntu.com --send-keys 6A4D42B96FD6045B
        gpg --keyserver keyserver.ubuntu.com --recv-keys 6A4D42B96FD6045B   # verify round-trip
- [x] Configure `~/.m2/settings.xml` with Portal credentials (`<server><id>central</id>` =
      generated user token; matches central-publishing-maven-plugin's default publishingServerId)
      -> GPG passphrase NOT stored on disk; signing uses gpg-agent / Pinentry at deploy time

### JAR-content audit at publish time
- [x] Re-audit `src/main/resources` end-to-end (nothing dev/test/env-specific should ship)
      -> clean; the only shipped resource is messages/messages.properties (runtime SAN-validation /
         report strings). No dev/test/env-specific files.
- [x] Re-audit `src/main/java` for classes that should be package-private
      -> 96/252 top-level types package-private after this pass. The 16 public bitboard types split into:
         (a) 4 genuine cross-package production API (BitboardPosition, BitboardPositionUtility,
         BitboardLegalMoveFactory, KingAttacks) - must stay public, no JPMS; (b) 11 generators
         (non-king *Attacks + *Moves) public so corpus-walk differential tests in test.bitboard can call
         them as the bitboard side of the StaticPosition-oracle comparison - deliberate, documented
         design (see test/.../test/bitboard/package-info.java), kept public; (c) ZobristKeys - had zero
         external refs (only BitboardPosition uses it in-package), so tightened to package-private.
- [x] Safety net for any stray test-fixture message keys or similar
      -> clean; no test-fixture keys leaked into messages.properties. The lone main-tree System.out is
         the documented Reporter.printReport stdout API; the lone TODO (dead Lemma 5/6 predicates) has
         since been removed.
- [x] Strip the embedded original POM from the jar
      -> maven-jar-plugin addMavenDescriptor=false. Deployed POM is the flattened one (compile deps
         only); the embedded META-INF/maven/.../pom.xml would otherwise carry the JitPack repo and
         junit/chesslib test deps into the published artifact. Verified absent in a clean build.

### First publish + workflow (publish time)
- [x] README: drop the JitPack `<repositories>` block, leave only the plain Maven snippet
- [x] Drop the JitPack URL and related framing from README and other docs
- [ ] Pre-deploy checks before touching Central:
        .\tools\java-license-headers.ps1 -Check
        mvn -Prelease help:active-profiles   # confirm the `release` profile is active
        mvn -Prelease verify                 # confirm signing / javadoc jar / source jar wiring
- [ ] First publish via the Central Portal - staged release, manual approval the first time
- [ ] Verify the artifact at https://central.sonatype.com/artifact/io.github.dlbbld/ashlar-chess
- [x] Document the per-release workflow (version bump -> tag -> `mvn -Prelease deploy` ->
      Portal release)
      -> updated workflows.md "Cutting a release" (the existing recurring-procedure doc; not a new
         release.md). Added step 4 "Publish to Maven Central" (pre-deploy checks, gpg-agent passphrase
         note, `mvn -Prelease deploy`, autoPublish=false manual Portal approval) and renumbered
         post-release to step 5 with the artifact-verify URL + the tasks.md archive convention.

### Post-publish
- [x] Decide whether JitPack stays available in parallel (free, harmless) or is deprecated
      -> deprecated; Maven Central is the single supported repository (old JitPack releases remain but are unsupported)
- [ ] (Optional) Add a Maven Central status badge to the README

---

## Backlog — captured but unscheduled

Items here are not assigned to any release. Captured so they don't get lost; revisit if/when scope or motivation aligns.

### Records carry data, not behavior — sweep for violations
The project rule (documented in `coding-conventions.md`): records carry data; domain logic that operates on them lives in dedicated utility / service classes. Permitted on a record: compact-constructor validation, `Comparable` when ordering is intrinsic, and language-provided `equals` / `hashCode` / `toString`. Domain-operation methods are not.
Example: `StaticPosition`: the record carries multiple non-data methods — `createChangedPosition` etc.
---

## Obsolete

Items deemed no longer worth pursuing. Captured so the decision is visible.

### Replace `EnumConstants` constant interface
`io.github.dlbbld.ashlarchess.common.constants.EnumConstants` is a `public interface` whose only purpose is to expose ~90 `public static final` aliases for `Square.*`, `Side.*`, `Piece.*`, `PieceType.*`, `Rank.*`, `File.*` so implementing classes inherit them unqualified. This is the classic "constant interface" anti-pattern (Effective Java item 22): interfaces should describe a contract/behavior, not be a convenience-inheritance vehicle for constants. The mechanism reads as beginner Java and leaks an internal vocabulary choice into the public type surface — `ChessBoard extends EnumConstants` is the clearest symptom (the chess contract has nothing to do with how implementers prefer to spell `Square.E4`). Used by 43 files under `src/main` plus tests.

