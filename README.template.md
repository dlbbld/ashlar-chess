ashlar-chess
===========

[![Maven Central](https://img.shields.io/maven-central/v/io.github.dlbbld/ashlar-chess.svg)](https://central.sonatype.com/artifact/io.github.dlbbld/ashlar-chess)

ashlar-chess is a Java chess library focused on rule correctness, production usability, and reproducible validation.
It implements SAN, FEN, and PGN parsing, validation, and export with strict/lenient parser pairs, and includes a Java
port of the [Chess Unwinnability Analyzer (CHA)](https://github.com/miguel-ambrona/D3-Chess) as a flagship feature.

## What it is

ashlar-chess is an orthodox-chess rules and data-handling library. It is useful when an application needs to validate
moves, parse and export PGN/FEN/SAN, report draw claims, adjudicate flagfall/resignation under the FIDE mating-material
exception, or analyze unwinnability/dead positions.

It is not a chess engine, does not search for best moves, and does not support Chess960, PGN variation trees, tablebases,
opening books, or GUI/tournament-management workflows.

## Documentation

- [manual.md](manual.md) — how to use the library: Board, move execution, SAN/FEN/PGN validation, reports,
  adjudication, and unwinnability.
- [specification.md](specification.md) — design goals, architecture, invariants, rule-level contracts, and deliberate
  deviations from external specifications.
- [CHANGELOG.md](CHANGELOG.md) — release history and migration notes.
- [setup.md](setup.md), [workflows.md](workflows.md), [CONTRIBUTING.md](CONTRIBUTING.md) — contributor setup and
  maintainer procedures.

The code snippets in this README and in the manual are generated from compiled test sources and their shown output is
captured from running them. If they drift, the test suite fails.

## Dependency

Requires JDK 17 or later at runtime. Published to Maven Central.

### Maven

```xml
<dependency>
  <groupId>io.github.dlbbld</groupId>
  <artifactId>ashlar-chess</artifactId>
  <version>19.1.0</version>
</dependency>
```

## Quick Start

The full manual has focused sections for each domain. These two compiled examples show the main public entry points:
strict/lenient notation handling, PGN import, and unwinnability/dead-position checks.

### Notation Input

<!-- readme:code id=readme-notation-input -->

### Unwinnability

<!-- readme:code id=readme-unwinnability -->

## Building From Source

```
$ git clone git@github.com:dlbbld/ashlar-chess.git
$ cd ashlar-chess/
$ mvn clean compile package install
```

For the full Eclipse contributor workflow, see [setup.md](setup.md). Day-to-day tests run with `mvn test`; release
validation uses the full profile described in [workflows.md](workflows.md).

## License

Copyright (C) 2020-2026  Daniel Bächli

ashlar-chess is free software, licensed under the GNU General Public License, version 3 (GPL v3). See [LICENSE](LICENSE)
for the full text.

The unwinnability and dead-position detection is a Java port of the
[Chess Unwinnability Analyzer (CHA)](https://github.com/miguel-ambrona/D3-Chess) by Miguel Ambrona, also licensed under
GPL v3.
