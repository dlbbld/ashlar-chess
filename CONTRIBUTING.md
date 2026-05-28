# Contributing to clean-chess

Thanks for your interest in contributing.

## Getting set up

clean-chess is developed in Eclipse with Checkstyle, Maven, and project-specific JDT compiler / formatter / save-actions settings — all checked into the repo. See **[setup.md](setup.md)** for the full installation procedure (JDK 17, Eclipse, Checkstyle plug-in, project import).

The project also builds and tests cleanly from the command line: `mvn clean test` works from any plain JDK 17 install.

## Code style

The project follows the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) with a small number of project-specific tightenings — see **[coding-conventions.md](coding-conventions.md)** for the rules and rationale. Highlights:

- Pure camelCase for all identifiers; one tag-prefix exception for test methods.
- All code must compile under the project's Eclipse JDT settings with **zero warnings**.
- Records carry data, not behavior — domain logic operating on records lives in dedicated utility/service classes (the M-vs-Service split from MVC).
- JavaDoc documents contracts and decisions, not call chains the IDE already shows.

The Checkstyle and JDT settings are in the repo and apply automatically on Eclipse import. If you use a different IDE, run the `mvn` build before pushing — the same checks fire there.

## Commit messages

See **[agents.md](agents.md)** for the convention. Short imperative subject by default; body only for non-obvious design decisions, migrations, or rule semantics. No PR-style summaries or routine test-result lines in commit bodies.

## Tests

Run the full test suite before opening a PR:

```
mvn test
```

For release-time validation, run the full profile (the heavy regression suites are gated by default):

```
mvn test -Pfull
```

`-Pfull` is a precondition for tagging a release. See `specification.md` §6.1 for what it covers.

## Where to put new work

- Production rule code: `src/main/java/io/github/dlbbld/ashlarchess/<package>/`
- Test fixtures: `src/test/resources/pgn/<category>/`
- Test code: `src/test/java/io/github/dlbbld/ashlarchess/test/<package>/`

For concrete procedures — adding a PGN test fixture, running test subsets, cutting a release — see **[workflows.md](workflows.md)**.

Open issues / pull requests against the active development branch (currently `lenient_san_validation`). The `main` branch tracks the most recent shipped release.

## Active and planned work

Current and future releases are tracked in **[tasks.md](tasks.md)**. Larger changes are usually discussed there before implementation; small fixes (typos, doc updates, single-bug fixes) can go straight to a PR.

## Questions

Open an issue on GitHub.
