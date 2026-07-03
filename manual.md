# ashlar-chess Manual

This is the user-facing guide for ashlar-chess. It explains how to call the public API and how to read the results.
The deeper reasons behind the contracts live in [specification.md](specification.md).

The examples in this manual are generated from compiled test sources. Their output is captured from running the
examples, so the shown snippets are checked by the test suite.

## Documentation Map

- [README.md](README.md) is the project front door: scope, dependency snippets, and a quick start.
- This manual is the practical usage guide.
- [specification.md](specification.md) is the technical contract: scope, architecture, invariants, FIDE/PGN decisions,
  and deliberate deviations.
- [setup.md](setup.md), [workflows.md](workflows.md), and [CONTRIBUTING.md](CONTRIBUTING.md) are contributor documents.

## Installation

ashlar-chess requires JDK 17 or later. It is published to Maven Central. See the
[README dependency section](README.md#dependency) for the current Maven and Gradle coordinates.

From 20.0.0 onward, ashlar-chess is a named JPMS module:

```java
module your.module {
    requires io.github.dlbbld.ashlarchess;
}
```

The module exports only the documented public API packages. Unsupported internals are inaccessible to modular consumers.

## Board Basics

`Board` is the central public type. It represents a chess game, not only a piece placement: it keeps the current
position, move history, legal moves, SAN/LAN strings, repetition state, halfmove clock, castling-right facts, and
rule-level predicates.

Use one `Board` per thread. It is mutable and not thread-safe.

The examples below are the fastest tour through the everyday board API.

### Creating a Board

Common entry points:

- `new Board()` — initial position.
- `Board.fromFenStrict(String)` — strict FEN import.
- `Board.fromFenLenient(String)` — tolerant FEN import.
- `new Board(Fen)` — start from an already parsed FEN value.

```java
final Board initial = new Board();

final Board strictFen = Board.fromFenStrict("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

final Board lenientFen = Board.fromFenLenient(" rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR W kqKQ - ");

final Fen parsedFen = StrictFenParser.parse("8/8/4k3/3R4/2K5/8/8/8 w - - 0 50");
final Board fromFenModel = new Board(parsedFen);

System.out.println(initial.getSideToMove()); // WHITE
System.out.println(strictFen.getFen()); // rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
System.out.println(lenientFen.getCastlingRightWhite()); // KING_AND_QUEEN_SIDE
System.out.println(fromFenModel.isInsufficientMaterial(Side.BLACK)); // true
```

Move execution:

- `moveStrict(String)` parses and plays canonical SAN.
- `moveLenient(String)` parses and plays a tolerated SAN spelling, returning forgiven items.
- `movesStrict(String...)` and `movesLenient(String...)` play a sequence.
- `move(MoveSpecification)` plays a programmatic move specification.
- `unmove()` undoes the last move.

```java
final Board board = new Board();

board.moveStrict("e4");

final LenientSanParseResult e5 = board.moveLenient("e7-e5");
for (final ForgivenSanItem forgiven : e5.forgivenItems()) {
  System.out.println(forgiven.originalToken() + " -> " + forgiven.canonicalSan()); // e7-e5 -> e5
}

board.movesLenient("nf3", "Nc6");

final MoveSpecification bishopMove = StrictSanParser.parse("Bc4", board);
board.move(bishopMove);

board.unmove();
board.movesStrict("Bc4", "Bc5");

System.out.println(board.getPerformedMovesAsSan()); // [e4, e5, Nf3, Nc6, Bc4, Bc5]
System.out.println(board.getFen()); // r1bqk1nr/pppp1ppp/2n5/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4
```

Move execution validates the requested move against the current legal-move set. Checkmate and stalemate naturally reject
further moves because no legal moves remain. Other draw states such as fifty-move, fivefold repetition, and dead-position
analysis are queryable; the caller decides when to adjudicate.

```java
final Board board = new Board();
board.movesStrict("e4", "e5", "Qh5", "Nc6", "Bc4", "Nf6", "Qxf7#");

System.out.println(board.getSan()); // Qxf7#
System.out.println(board.getPerformedMoveCount()); // 7
System.out.println(board.getSideToMove()); // BLACK
System.out.println(board.isCheck()); // true
System.out.println(board.isCheckmate()); // true
System.out.println(board.getLegalMovesAsSan()); // []
```

## Move Values

`MoveSpecification` is the move request value. Ordinary moves are created from their source and destination squares.
Promotions additionally carry a `PromotionPieceType`. Castling is created from the `CastlingMove` enum:
`new MoveSpecification(CastlingMove.KING_SIDE)` or `new MoveSpecification(CastlingMove.QUEEN_SIDE)`.

`LegalMove` is the validated move value returned by legal-move APIs and performed-move history. It exposes conveniences
such as `isCapture()`, `isCastling()`, `isPromotion()`, `isEnPassant()`, and `enPassantCapturedPawnSquare()`.

### Castling

Create castling requests with the `CastlingMove` enum. When you need the king and rook squares touched by the move,
resolve them from `CastlingMove` and the moving side:

```java
// Create a castling request with the CastlingMove enum, then resolve the touched
// king and rook squares from the enum and moving side.
final MoveSpecification specification = new MoveSpecification(CastlingMove.KING_SIDE);

if (specification.isCastling()) {
  final CastlingMove castling = specification.castlingMove();
  System.out.println(castling.kingFromSquare(Side.WHITE)); // e1
  System.out.println(castling.kingToSquare(Side.WHITE)); // g1
  System.out.println(castling.rookFromSquare(Side.WHITE)); // h1
  System.out.println(castling.rookToSquare(Side.WHITE)); // f1
}
```

### En Passant

For an en-passant move, the captured pawn is not on the move destination square. Read it from the validated
`LegalMove.enPassantCapturedPawnSquare()` rather than deriving it from `MoveSpecification.toSquare()`.

## Rule Reports

`Reporter.report(String)` parses a PGN and returns human-readable rule reports for threefold claims, actual
repetitions, fifty-move claims, and fifty/75-move sequences.

### Threefold Claim Ahead

This game ended with a threefold repetition claim ahead according to
[Wikipedia](https://en.wikipedia.org/wiki/Threefold_repetition#Portisch_versus_Korchnoi,_1970):

```java
final String pgn = """
    1. Nf3 c5 2. c4 Nf6 3. Nc3 Nc6 4. d4 cxd4 5. Nxd4 e6 6. g3 Qb6 7. Nb3 Ne5 8. e4
    Bb4 9. Qe2 O-O 10. f4 Nc6 11. e5 Ne8 12. Bd2 f6 13. c5 Qd8 14. a3 Bxc3 15. Bxc3
    fxe5 16. Bxe5 b6 17. Bg2 Nxe5 18. Bxa8 Nf7 19. Bg2 bxc5 20. Nxc5 Qb6 21. Qf2
    Qb5 22. Bf1 Qc6 23. Bg2 Qb5 24. Bf1 Qc6 25. Bg2""";
Reporter.report(pgn).forEach(System.out::println);
```

Output:

```
Valid threefold claims ahead (asterisk denotes also the last ahead move has been played):
21... Qb5 23... Qb5 25... Qb5 (A - 3)

Threefolds and beyond:
None

Valid fifty-move claims ahead (only listed when the sequence does not reach the 50-move threshold in actual play):
None

Fifty moves and beyond:
None
```

Black could have claimed a threefold by writing, but not yet playing, `25... Qb5`.

### Threefold On The Board

This game contains a threefold repetition according to
[Wikipedia](https://en.wikipedia.org/wiki/Threefold_repetition#Capablanca_versus_Lasker,_1921):

```java
final String pgn = """
    1. d4 d5 2. Nf3 Nf6 3. c4 e6 4. Bg5 Nbd7 5. e3 Be7 6. Nc3 O-O 7. Rc1 b6 8. cxd5
    exd5 9. Qa4 c5 10. Qc6 Rb8 11. Nxd5 Bb7 12. Nxe7+ Qxe7 13. Qa4 Rbc8 14. Qa3 Qe6
    15. Bxf6 Qxf6 16. Ba6 Bxf3 17. Bxc8 Rxc8 18. gxf3 Qxf3 19. Rg1 Re8 20. Qd3 g6
    21. Kf1 Re4 22. Qd1 Qh3+ 23. Rg2 Nf6 24. Kg1 cxd4 25. Rc4 dxe3 26. Rxe4 Nxe4 27.
    Qd8+ Kg7 28. Qd4+ Nf6 29. fxe3 Qe6 30. Rf2 g5 31. h4 gxh4 32. Qxh4 Ng4 33. Qg5+
    Kf8 34. Rf5 h5 35. Qd8+ Kg7 36. Qg5+ Kf8 37. Qd8+ Kg7 38. Qg5+ Kf8 39. b3 Qd6
    40. Qf4 Qd1+ 41. Qf1 Qd7 42. Rxh5 Nxe3 43. Qf3 Qd4 44. Qa8+ Ke7 45. Qb7+ Kf8 46.
    Qb8+ *""";
Reporter.report(pgn).forEach(System.out::println);
```

Output:

```
Valid threefold claims ahead (asterisk denotes also the last ahead move has been played):
34... h5 36... Kf8 38... Kf8 (A* - 3)
35. Qd8+ 37. Qd8+ 39. Qd8+ (B - 3)

Threefolds and beyond:
34... h5 36... Kf8 38... Kf8 (A - 3)

Valid fifty-move claims ahead (only listed when the sequence does not reach the 50-move threshold in actual play):
None

Fifty moves and beyond:
None
```

The letters `A`, `B`, `C`, ... distinguish different repeated positions in order of first occurrence.

### Fifty-Move Runs

This game reaches a long no-progress run according to
[Wikipedia](https://en.wikipedia.org/wiki/Fifty-move_rule#Karpov_vs._Kasparov,_1991):

```java
final String pgn = """
    1. d4 Nf6 2. c4 g6 3. Nc3 Bg7 4. e4 d6 5. Nf3 O-O 6. Be2 e5 7. O-O Nc6 8. d5
    Ne7 9. Nd2 a5 10. Rb1 Nd7 11. a3 f5 12. b4 Kh8 13. f3 Ng8 14. Qc2 Ngf6 15. Nb5
    axb4 16. axb4 Nh5 17. g3 Ndf6 18. c5 Bd7 19. Rb3 Nxg3 20. hxg3 Nh5 21. f4 exf4
    22. c6 bxc6 23. dxc6 Nxg3 24. Rxg3 fxg3 25. cxd7 g2 26. Rf3 Qxd7 27. Bb2 fxe4
    28. Rxf8+ Rxf8 29. Bxg7+ Qxg7 30. Qxe4 Qf6 31. Nf3 Qf4 32. Qe7 Rf7 33. Qe6 Rf6
    34. Qe8+ Rf8 35. Qe7 Rf7 36. Qe6 Rf6 37. Qb3 g5 38. Nxc7 g4 39. Nd5 Qc1+ 40.
    Qd1 Qxd1+ 41. Bxd1 Rf5 42. Ne3 Rf4 43. Ne1 Rxb4 44. Bxg4 h5 45. Bf3 d5 46.
    N3xg2 h4 47. Nd3 Ra4 48. Ngf4 Kg7 49. Kg2 Kf6 50. Bxd5 Ra5 51. Bc6 Ra6 52. Bb7
    Ra3 53. Be4 Ra4 54. Bd5 Ra5 55. Bc6 Ra6 56. Bf3 Kg5 57. Bb7 Ra1 58. Bc8 Ra4 59.
    Kf3 Rc4 60. Bd7 Kf6 61. Kg4 Rd4 62. Bc6 Rd8 63. Kxh4 Rg8 64. Be4 Rg1 65. Nh5+
    Ke6 66. Ng3 Kf6 67. Kg4 Ra1 68. Bd5 Ra5 69. Bf3 Ra1 70. Kf4 Ke6 71. Nc5+ Kd6
    72. Nge4+ Ke7 73. Ke5 Rf1 74. Bg4 Rg1 75. Be6 Re1 76. Bc8 Rc1 77. Kd4 Rd1+ 78.
    Nd3 Kf7 79. Ke3 Ra1 80. Kf4 Ke7 81. Nb4 Rc1 82. Nd5+ Kf7 83. Bd7 Rf1+ 84. Ke5
    Ra1 85. Ng5+ Kg6 86. Nf3 Kg7 87. Bg4 Kg6 88. Nf4+ Kg7 89. Nd4 Re1+ 90. Kf5 Rc1
    91. Be2 Re1 92. Bh5 Ra1 93. Nfe6+ Kh6 94. Be8 Ra8 95. Bc6 Ra1 96. Kf6 Kh7 97.
    Ng5+ Kh8 98. Nde6 Ra6 99. Be8 Ra8 100. Bh5 Ra1 101. Bg6 Rf1+ 102. Ke7 Ra1 103.
    Nf7+ Kg8 104. Nh6+ Kh8 105. Nf5 Ra7+ 106. Kf6 Ra1 107. Ne3 Re1 108. Nd5 Rg1
    109. Bf5 Rf1 110. Ndf4 Ra1 111. Ng6+ Kg8 112. Ne7+ Kh8 113. Ng5 Ra6+ 114. Kf7
    Rf6+""";
Reporter.report(pgn).forEach(System.out::println);
```

Output:

```
Valid threefold claims ahead (asterisk denotes also the last ahead move has been played):
None

Threefolds and beyond:
None

Valid fifty-move claims ahead (only listed when the sequence does not reach the 50-move threshold in actual play):
None

Fifty moves and beyond:
63... Rg8 (0/1) - 113. Ng5 (50/50) - 114... Rf6+ (51/52)
```

Each bracket gives the moves by each player since the last capture or pawn move, written `(White/Black)`.

## Game Adjudication

Flagfall and resignation are external losing events, but FIDE gives the same exception in both cases: the game is drawn
if the opponent could not have checkmated by any possible series of legal moves.

`Adjudicator` applies that exception. You pass the board and the side whose flag fell or who resigned.

Each event has a quick and full variant:

- Quick: bounded, live-play-safe, rules `DRAW` or `LOSS`.
- Full: deeper analysis, rules `DRAW`, `LOSS`, or `UNDETERMINED`.

### Flagfall

```java
// A flag falls: rule draw-or-loss with the bounded, live-play-safe quick analyzer.

// White flags with only a lone king opposing the rook: the would-be winner
// (Black) cannot mate, so the game is drawn, not lost.
final Board loneKing = Board.fromFenStrict("8/8/4k3/3R4/2K5/8/8/8 w - - 0 50");
System.out.println(Adjudicator.adjudicateFlagfallQuick(loneKing, Side.WHITE)); // DRAW

// White flags behind a blocked pawn wall: Black can never break through, so
// the quick analyzer draws this non-material position too.
final Board pawnWall = Board.fromFenStrict("8/8/3k4/1p2p1p1/pP1pP1P1/P2P4/1K6/8 b - - 32 62");
System.out.println(Adjudicator.adjudicateFlagfallQuick(pawnWall, Side.WHITE)); // DRAW

// Black flags with both sides still able to play for a win: the flag stands.
final Board winnable = Board.fromFenStrict("q4r2/pR3pkp/1p2p1p1/4P3/6P1/1P3Q2/1Pr2PK1/3R4 b - - 3 29");
System.out.println(Adjudicator.adjudicateFlagfallQuick(winnable, Side.BLACK)); // LOSS
```

The quick variant draws when it can prove the opponent cannot win. Otherwise the flag stands.

```java
// The full analyzer additionally proves wins and may report UNDETERMINED.

// Black flags in a position the full search proves White can win: a real loss.
final Board provenWin = Board.fromFenStrict("q4r2/pR3pkp/1p2p1p1/4P3/6P1/1P3Q2/1Pr2PK1/3R4 b - - 3 29");
System.out.println(Adjudicator.adjudicateFlagfallFull(provenWin, Side.BLACK)); // LOSS

// White flags in the rare position whose full search exhausts its node bound.
final Board undecided = Board.fromFenStrict("2b5/1p6/pPp3k1/2Pp3p/P2PpBpP/4P1P1/5K2/8 b - - 46 59");
System.out.println(Adjudicator.adjudicateFlagfallFull(undecided, Side.WHITE)); // UNDETERMINED
```

The full variant can also prove wins and can report `UNDETERMINED` when its search bound is exhausted.

### Resignation

```java
// Resignation carries the identical FIDE exception, so it adjudicates exactly like a flag-fall.
final Board board = Board.fromFenStrict("8/8/4k3/3R4/2K5/8/8/8 w - - 0 50");
System.out.println(Adjudicator.adjudicateResignationQuick(board, Side.WHITE)); // DRAW
System.out.println(Adjudicator.adjudicateResignationFull(board, Side.WHITE)); // DRAW
```

## Dead Position During Play

Adjudication above corrects the final result after an external event. A dead position is different: under FIDE 5.2.2,
the game is drawn at the moment neither side can checkmate by any possible series of legal moves.

ashlar-chess does not run the analyzer automatically after each move. Callers that want this termination point query it:

```java
final Board board = Board.fromFenStrict("8/8/3k4/1p2p1p1/pP1pP1P1/P2P4/1K6/8 b - - 32 62");

if (board.isInsufficientMaterial()) {
  // draw by insufficient material
} else if (board.deadPositionQuick() == DeadPositionQuickVerdict.DEAD) {
  // draw by dead position
}
```

The quick dead-position check is computationally cheap. Running it during live play is a product decision: it gives the
exact FIDE termination moment, while checking only at flagfall/resignation still preserves the final result because a
dead position cannot later become winnable.

## Unwinnability API

A position is unwinnable for a side if no legal sequence can end with that side giving checkmate, even if the opponent
cooperates. A dead position is unwinnable for both sides.

The quick analyzers prove unwinnability cheaply. The full analyzers search deeper and can prove concrete wins or report
`UNDETERMINED` when the search limit is exhausted.

### Legal positions only

These analyses are defined for — and guaranteed only on — **legal positions** (positions reachable from the start by
legal moves). That is exactly what game play and PGNs produce, so it covers the intended use: flag-fall / resignation
adjudication and dead-position detection. ashlar cannot validate full legality — a complete check would need an
infeasible retrograde search — so **submitting only legal positions is your responsibility.** On an illegal position
the result is undefined and the quick and full verdicts may disagree; in practice it is still correct on the large
majority of illegal positions, with only a small set of unreachable constructions decided wrongly (for example an
impossible double-bishop-check mate called unwinnable, or certain illegal `KBNvK` / `KBBvK` positions reported
`WINNABLE`). Such positions never arise in a legally played game and are out of scope.

### Reading Quick Verdicts

The verdict enums are proof results, not booleans. Read the exact constant:

- Side-specific quick: `UNWINNABLE` means proven unwinnable; `WINNABLE` means proven winnable (the bounded search met
  a checkmate by the intended winner - it fires only on quickly matable positions and carries no mate line);
  `POSSIBLY_WINNABLE` means not decided either way.
- Dead-position quick: `DEAD` means both sides are proven unwinnable; `POSSIBLY_ALIVE` means at least one side was not
  proven unwinnable.

Do not read `!= UNWINNABLE` as “winnable”; it also includes the undecided `POSSIBLY_WINNABLE`.

### Reading Full Verdicts

The full side-specific verdict has three public outcomes:

- `WINNABLE` means proven winnable.
- `UNWINNABLE` means proven unwinnable.
- `UNDETERMINED` means the full search stopped at its bound.

When you call `UnwinnableFullAnalyzer` directly, `UnwinnabilityFullAnalysis` also tells you how a `WINNABLE` result was
proved: `winnableProof()` returns `WinnableProof.THEOREM` for theorem-certified wins and `WinnableProof.HELPMATE` for
searched wins (where `mateLine()` carries a concrete UCI helpmate line). The `Board.unwinnableFull(Side)` convenience
method exposes only the public verdict.

Dead-position full uses its own whole-position vocabulary: `DEAD`, `ALIVE`, or `UNDETERMINED`.

### Side-Specific Examples

Insufficient material:

```java
final Board board = Board.fromFenStrict("8/8/4k3/3R4/2K5/8/8/8 w - - 0 50");
System.out.println(board.unwinnableQuick(Side.BLACK)); // UNWINNABLE
System.out.println(board.unwinnableFull(Side.BLACK)); // UNWINNABLE
```

Forced moves:

```java
final Board board = Board.fromFenStrict("5r1k/6P1/7K/5q2/8/8/8/8 b - - 0 51");
System.out.println(board.unwinnableQuick(Side.WHITE)); // UNWINNABLE
System.out.println(board.unwinnableFull(Side.WHITE)); // UNWINNABLE
```

Pawn walls:

```java
final Board board = Board.fromFenStrict("8/8/3k4/1p2p1p1/pP1pP1P1/P2P4/1K6/8 b - - 32 62");
System.out.println(board.unwinnableQuick(Side.BLACK)); // UNWINNABLE
System.out.println(board.unwinnableFull(Side.BLACK)); // UNWINNABLE
```

Common positions where quick leaves the question open and full proves a win:

```java
final Board board = Board.fromFenStrict("q4r2/pR3pkp/1p2p1p1/4P3/6P1/1P3Q2/1Pr2PK1/3R4 b - - 3 29");
System.out.println(board.unwinnableQuick(Side.WHITE)); // POSSIBLY_WINNABLE
System.out.println(board.unwinnableFull(Side.WHITE)); // WINNABLE
```

Blocked positions the quick algorithm proves:

```java
final Board board = Board.fromFenStrict("2b1k3/8/8/1p1p1p1p/1P1P1P1P/8/8/2B1K3 w - - 0 40");
System.out.println(board.unwinnableQuick(Side.WHITE)); // UNWINNABLE
System.out.println(board.unwinnableFull(Side.WHITE)); // UNWINNABLE
```

### Dead-Position Examples

Insufficient material:

```java
final Board board = Board.fromFenStrict("8/8/3kn3/8/2K5/8/8/8 w - - 0 50");
System.out.println(board.deadPositionQuick()); // DEAD
System.out.println(board.deadPositionFull()); // DEAD
```

Pawn walls:

```java
final Board board = Board.fromFenStrict("8/6b1/1p3k2/1Pp1p1p1/2P1PpP1/5P2/8/5K2 b - - 11 61");
System.out.println(board.deadPositionQuick()); // DEAD
System.out.println(board.deadPositionFull()); // DEAD
```

Forced moves:

```java
final Board board = Board.fromFenStrict("k7/P1K5/8/8/8/8/8/8 b - - 2 58");
System.out.println(board.deadPositionQuick()); // DEAD
System.out.println(board.deadPositionFull()); // DEAD
```

Positions quick does not prove dead:

```java
final Board board = Board.fromFenStrict("q4r2/pR3pkp/1p2p1p1/4P3/6P1/1P3Q2/1Pr2PK1/3R4 b - - 3 29");

System.out.println(board.deadPositionQuick()); // POSSIBLY_ALIVE
System.out.println(board.deadPositionFull()); // ALIVE
System.out.println(board.unwinnableQuick(Side.WHITE)); // POSSIBLY_WINNABLE
System.out.println(board.unwinnableFull(Side.WHITE)); // WINNABLE
```

For quick side-specific unwinnability, `POSSIBLY_WINNABLE` is intentionally conservative: it means "not decided either
way". In the project statistics, more than 99.99% of these quick-open positions are in fact winnable, but callers that
need a proof should use the full verdict (or rely on quick's own `WINNABLE`, which fires on quickly matable
positions).

## Validation Model

The library validates the three notations it consumes:

- SAN: moves in Standard Algebraic Notation.
- FEN: single-position import/export.
- PGN: game import/export, with SAN movetext and optional FEN tags.

Each domain has a strict and lenient public path.

Strict means canonical input. Lenient means ashlar-chess accepts a defined set of recoverable syntactic deviations,
reports them as typed forgiven items, and then applies the same chess validation as strict input. Lenient does not turn
illegal chess into legal chess.

## SAN Validation

Strict SAN accepts canonical SAN only:

- uppercase piece letters;
- lowercase files;
- canonical disambiguation;
- `=Q`-style promotion;
- `O-O` / `O-O-O` castling with letter `O`;
- correct optional `+` / `#` suffix.

Use it through `StrictSanParser.parse(String, Board)` or `Board.moveStrict(String)`.

Lenient SAN accepts common real-world spelling deviations when they uniquely identify a legal move. Use it through
`LenientSanParser.parse(String, Board)` or `Board.moveLenient(String)`.

Accepted deviations are reported with typed codes
(`io.github.dlbbld.ashlarchess.san.LenientSanValidationProblem`):

| Category | Code | Example |
|---|---|---|
| **Suffix mismatch** (6) | `MISSING_CHECK_SUFFIX` | `Nd7` when actually check |
| | `MISSING_CHECKMATE_SUFFIX` | `Nd7` when actually mate |
| | `SPURIOUS_CHECK_SUFFIX` | `Nd7+` when not check |
| | `SPURIOUS_CHECKMATE_SUFFIX` | `Nd7#` when not mate |
| | `WRONG_CHECK_SUFFIX_FOR_CHECKMATE` | `Nd7+` when actually mate |
| | `WRONG_CHECKMATE_SUFFIX_FOR_CHECK` | `Nd7#` when only check |
| **Capture marker** (2) | `MISSING_CAPTURE_MARKER` | `Be5` (piece) or `ed5` (pawn) when actually a capture |
| | `SPURIOUS_CAPTURE_MARKER` | `Bxe5` when destination empty |
| **Disambiguation** (4) | `OVERSPECIFIED_FILE_DISAMBIGUATION` | `Nbd7` when `Nd7` would suffice |
| | `OVERSPECIFIED_RANK_DISAMBIGUATION` | `N3d7` when `Nd7` would suffice |
| | `OVERSPECIFIED_SQUARE_DISAMBIGUATION` | `Nb3d7` when less would suffice |
| | `NON_STANDARD_RANK_DISAMBIGUATION` | `R1d4` where canonical uses file (`Rad4`) |
| **Notation form** (4) | `LONG_ALGEBRAIC_NOTATION` | `e2-e4`, `Nb1-d7` |
| | `UCI_NOTATION` | `e2e4`, `e7e8q`, `e1g1` (castling) |
| | `ZERO_INSTEAD_OF_O_CASTLING` | `0-0`, `0-0-0` |
| | `EXPLICIT_PAWN_LETTER` | `Pe4` |
| **Promotion form** (1) | `MISSING_PROMOTION_EQUALS` | `e8Q` |
| **Case variation** (4) | `LOWERCASE_PIECE_LETTER` | `nf3` |
| | `UPPERCASE_FILE_LETTER` | `NF3` |
| | `UPPERCASE_CAPTURE_MARKER` | `BXe5` |
| | `LOWERCASE_PROMOTION_PIECE` | `e8=q` |

Codes are not collapsed: each distinguishable deviation has its own code, and a single move can carry multiple codes
(for example, `nbxd7+` when actually mate emits `LOWERCASE_PIECE_LETTER`,
`OVERSPECIFIED_FILE_DISAMBIGUATION`, and `WRONG_CHECK_SUFFIX_FOR_CHECKMATE`).

Mixed castling such as `0-O` is rejected. Pawn spurious capture marker recovery is also rejected because it would
silently change the user's intended pawn.

The PGN lenient parser routes movetext SAN through this same lenient SAN layer:

```java
final String pgn = """
    [Event "?"]
    [Site "?"]
    [Date "?"]
    [Round "?"]
    [White "?"]
    [Black "?"]
    [Result "*"]

    1. e4 e5 2. Nf3 Nc6 3. Bc4 Bc5 4. 0-0 nf6 *
    """;
final LenientPgnParserValidationResult result = LenientPgnParser.validateText(pgn);
System.out.println(result.isValid());
for (final ForgivenSanItem item : result.sanForgivenItems()) {
  System.out.println(item.code() + ": " + item.originalToken() + " -> " + item.canonicalSan());
}
```

Output:

```
true
ZERO_INSTEAD_OF_O_CASTLING: 0-0 -> O-O
LOWERCASE_PIECE_LETTER: nf6 -> Nf6
```

## FEN Validation

Strict FEN entry points:

- `StrictFenParser.parse(String)`
- `StrictFenParser.validate(String)`
- `Board.fromFenStrict(String)`

Strict FEN requires canonical six-field FEN and validates the position:

- piece placement is eight legal ranks and each rank has exactly eight squares;
- each side has exactly one king and physically possible piece counts;
- no pawn on rank 1 or rank 8;
- the side that just moved is not still in check;
- side to move is `w` or `b`;
- castling rights are canonical and consistent with king/rook placement;
- en-passant target square is structurally and legally possible;
- halfmove clock is non-negative, zero when an en-passant target is set, and consistent with the fullmove number;
- fullmove number is positive and within the library's maximum.

Lenient FEN entry points:

- `LenientFenParser.parse(String)`
- `LenientFenParser.validate(String)`
- `Board.fromFenLenient(String)`

Lenient FEN performs syntactic recovery, reports every forgiven item, then runs strict validation. It tolerates:

- leading/trailing whitespace;
- extra whitespace between fields;
- tab/newline field separators;
- missing halfmove/fullmove counters;
- uppercase side-to-move or en-passant file;
- non-canonical castling order;
- non-ASCII dash for no en-passant square;
- trailing garbage token;
- halfmove/fullmove inconsistency, corrected by reconstructing a conservative fullmove number.

It does not forgive semantic impossibilities such as missing kings, pawns on illegal ranks, impossible double-checks, or
illegal en-passant geometry.

Lenient PGN routes its `FEN` tag through lenient FEN. Strict PGN routes the `FEN` tag through strict FEN.

## PGN Functionality

### Limitations

PGN support intentionally does not include:

- recursive annotation variations;
- Numeric Annotation Glyphs such as `$1`;
- multi-game PGN files.

UTF-8 byte-order marks are accepted by the lenient parser and rejected by the strict parser. PGN move suffix annotations
(`!`, `?`, `!!`, `??`, `!?`, `?!`) are parsed, modeled, and round-tripped by both parsers.

### Parse Model

PGN handling separates four jobs:

- Parse: read text into `PgnGame`.
- Validation reporting: expose tolerated deviations.
- Semantic export: write the parsed model honestly, preserving tag presence/order and termination-marker presence.
- Archival export: produce PGN spec §8.1.1 archival output with filled/sorted Seven Tag Roster.

The default export mode is semantic. Archival export is opt-in via `WriteMode.ARCHIVAL`.

### Lenient PGN Parser

The lenient parser accepts real-world PGN shape issues such as whitespace variation, missing tags, optional termination
markers, SAN spelling deviations, and deficient FEN tags.

```java
final String pgn = """
    [ Event "Spring Classic"]

    1. e4 e5   2. Nf3
    Nf6
      3. Bc4 Bc5
    """;
final PgnGame pgnGame = LenientPgnParser.parseText(pgn);
final Board board = PgnUtility.toBoard(pgnGame);
board.moveStrict("a3");
```

Transformation to export format:

```java
final String pgn = """
    [Black "Jane Doe"]
    [White "John Doe"]
    [ Event "Spring Classic"]

    1. e4 e5   2. Nf3
    Nf6
    3. Bc4 Bc5
    """;
final PgnGame pgnGame = LenientPgnParser.parseText(pgn);
System.out.println(PgnCreate.toPgnString(pgnGame, WriteMode.ARCHIVAL));
```

Output:

```
[Event "Spring Classic"]
[Site "?"]
[Date "????.??.??"]
[Round "?"]
[White "John Doe"]
[Black "Jane Doe"]
[Result "*"]

1. e4 e5 2. Nf3 Nf6 3. Bc4 Bc5 *
```

Invalid input produces typed exceptions with detailed messages:

```java
final String pgn = """
    [ Event "Spring Classic"]

    1. e4 e5   2. Nf4
    Nf6
      3. Bc4 Bc5
    """;
try {
  final PgnGame pgnGame = LenientPgnParser.parseText(pgn);
  System.out.println(PgnUtility.toBoard(pgnGame).isCheck()); // not reached
} catch (final LenientPgnParserValidationException e) {
  System.out.println(e.getMessage());
}
```

Output:

```
The validation for 2. Nf4 failed. Reason: The lenient SAN parser could not parse 'Nf4': No knight can reach square f4.
```

File parsing:

```java
final PgnGame pgnGame = LenientPgnParser.parsePath("game.pgn");
final Board board = PgnUtility.toBoard(pgnGame);
System.out.println(board.isCheckmate());
```

### Strict PGN Parser

The strict parser enforces the PGN import-format syntax plus ashlar-chess's semantic essentials: the Result tag must be
present and match the termination marker, and SetUp/FEN coupling must hold.

It does not require the full Seven Tag Roster for parsing; that is an archival-output rule.

Valid strict PGN:

```java
final String pgn = """
    [Event "Spring Classic"]
    [Site "Somewhere"]
    [Date "2024.01.01"]
    [Round "1"]
    [White "Player1"]
    [Black "Player2"]
    [Result "*"]

    1. e4 e5 2. Nf3 Nf6 3. Bc4 Bc5 *

    """;
final PgnGame pgnGame = StrictPgnParser.parseText(pgn);
final Board board = PgnUtility.toBoard(pgnGame);
board.moveStrict("a3");
```

Invalid syntax:

```java
final String pgn = """
    [ Event "Spring Classic"]

    1. e4 e5 2. Nf3 Nf6 3. Bc4 Bc5

    """;
try {
  final PgnGame pgnGame = StrictPgnParser.parseText(pgn);
  System.out.println(PgnUtility.toBoard(pgnGame).isCheck()); // not reached
} catch (final StrictPgnParserValidationException e) {
  System.out.println(e.getMessage());
}
```

Output:

```
The left square bracket [ must be followed by the tag name, but a space was found.
```

Invalid form:

```java
final String pgn = """
    [Event "Spring Classic"]

    1. e4 e5 2. Nf3 Nf6 3. Bc4 Bc5 *

    """;
try {
  final PgnGame pgnGame = StrictPgnParser.parseText(pgn);
  System.out.println(PgnUtility.toBoard(pgnGame).isCheck()); // not reached
} catch (final StrictPgnParserValidationException e) {
  System.out.println(e.getMessage());
}
```

Output:

```
The Result tag is required. PGN spec section 8.1.1 archival storage requires the full seven tag roster, but the strict parser only enforces the semantic essentials: a Result tag (whose value must match the termination marker) and the SetUp/FEN coupling. Other roster tags are archival-storage concerns only.
```

File parsing:

```java
final PgnGame pgnGame = StrictPgnParser.parsePath("game.pgn");
final Board board = PgnUtility.toBoard(pgnGame);
System.out.println(board.isThreefoldRepetition());
```

### PGN Creation And Export

Create a `PgnGame` from a `Board`:

```java
final Board board = new Board();
board.movesStrict("e4", "e5", "Nf3", "Nf6", "Bc4", "Bc5");

final PgnGame pgnGame = PgnCreate.createPgnGame(board);
System.out.println(PgnCreate.toPgnString(pgnGame, WriteMode.ARCHIVAL));
```

Output:

```
[Event "?"]
[Site "?"]
[Date "????.??.??"]
[Round "?"]
[White "?"]
[Black "?"]
[Result "*"]

1. e4 e5 2. Nf3 Nf6 3. Bc4 Bc5 *
```

Format a `PgnGame` as text:

```java
final Board board = new Board();
board.movesStrict("e4", "e5", "Nf3", "Nf6", "Bc4", "Bc5");

final PgnGame pgnGame = PgnCreate.createPgnGame(board);
final String pgnString = PgnCreate.toPgnString(pgnGame, WriteMode.ARCHIVAL);
System.out.println(LenientPgnParser.validateText(pgnString).isValid()); // true
System.out.println(StrictPgnParser.validateText(pgnString).isValid()); // true
```

Write a PGN to the file system:

```java
final Board board = new Board();
board.movesStrict("e4", "e5", "Nf3", "Nf6", "Bc4", "Bc5");

final PgnGame pgnGame = PgnCreate.createPgnGame(board);
PgnWriter.writePgn(pgnGame, "game.pgn", WriteMode.ARCHIVAL);
```

### PGN Validation

Lenient validation:

```java
final String pgn = """
    [ Event "Spring Classic"]

    1. e4 e5   2. Nf3
    Nf6
      3. Bc4 Bc5
    """;
final LenientPgnParserValidationResult result = LenientPgnParser.validateText(pgn);
System.out.println(result.isValid()); // true
```

```java
final String pgn = """
    [ Event "Spring Classic"]

    1. e4 e5   2. Nf3
    Nf6
      3. Bc4 Bc5 4. Y1
    """;
final LenientPgnParserValidationResult result = LenientPgnParser.validateText(pgn);
System.out.println(result.isValid());
System.out.println(result.message());
```

Output:

```
false
The movetext is invalid because a SAN contains an invalid character of "Y".
```

File validation:

```java
final LenientPgnParserValidationResult result = LenientPgnParser.validatePath("game.pgn");
System.out.println(result.isValid());
```

Strict validation:

```java
final String pgn = """
    [Event "Spring Classic"]
    [Site "Somewhere"]
    [Date "2024.01.01"]
    [Round "1"]
    [White "Player1"]
    [Black "Player2"]
    [Result "*"]

    1. e4 e5 2. Nf3 Nf6 3. Bc4 Bc5 *

    """;
final StrictPgnParserValidationResult result = StrictPgnParser.validateText(pgn);
System.out.println(result.isValid()); // true
```

```java
final String pgn = """
    [Event "Spring Classic"]
    [Site "Somewhere"]
    [Date "2024.01.01"]
    [Round "1"]
    [White "Player1"]
    [Black "Player2"]
    [Result "*"]

    1. e4 e5 2. Nf3 Nf6 2. Bc4 Bc5 *

    """;
final StrictPgnParserValidationResult result = StrictPgnParser.validateText(pgn);
System.out.println(result.isValid());
System.out.println(result.message());
```

Output:

```
false
The movetext numbering does not continue with "3." as expected.
```

File validation:

```java
final StrictPgnParserValidationResult result = StrictPgnParser.validatePath("game.pgn");
System.out.println(result.isValid());
```

## Specification Cross-References

Use [specification.md](specification.md) when you need the exact project contract:

- §3.1: FIDE rule fidelity and game termination.
- §3.3.1: full lenient SAN taxonomy and algorithm.
- §3.3.2: PGN parse model and write modes.
- §3.3.3: FEN parser tiers and strict invariants.
- §5: PGN deviations from the PGN specification.
- §6: testing strategy and release-gate rationale.
