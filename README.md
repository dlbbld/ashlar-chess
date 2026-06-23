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

### Gradle

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.dlbbld:ashlar-chess:19.1.0'
}
```

## Quick Start

```java
final Board board = new Board();

board.moveStrict("e4"); // specifying the SAN
board.movesStrict("e5", "Bc4"); // specifying multiple SAN's

final MoveSpecification newMove = new MoveSpecification(Square.F8, Square.C5);
board.move(newMove); // move specification without SAN

board.unmove(); // undoes last move

board.movesStrict("Bc5", "Qf3", "h6", "Qxf7#");

System.out.println(board.isCheckmate()); // true
```

## Castling Moves

A castling `MoveSpecification` carries only the `CastlingMove` (`KING_SIDE` / `QUEEN_SIDE`); its from/to squares are
deliberately `Square.NONE`. Detect a castling move with `isCastling()`, then resolve the touched squares from the
`CastlingMove` and the side to move:

```java
// A castling MoveSpecification carries only the CastlingMove; its from/to squares are Square.NONE.
// Detect a castling move with isCastling(), then resolve the squares it touches from the
// CastlingMove, given the side.
final MoveSpecification specification = new MoveSpecification(CastlingMove.KING_SIDE);

if (specification.isCastling()) {
  final CastlingMove castling = specification.castlingMove();
  System.out.println(castling.kingFromSquare(Side.WHITE)); // e1
  System.out.println(castling.kingToSquare(Side.WHITE)); // g1
  System.out.println(castling.rookFromSquare(Side.WHITE)); // h1
  System.out.println(castling.rookToSquare(Side.WHITE)); // f1
}
```

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
