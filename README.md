clean-chess
===========

clean-chess is a Java chess library focused on rule correctness and reproducibility.
It implements SAN, FEN, and PGN parsing, validation, and export with a strict/lenient parser pair,
and includes a Java port of the [Chess Unwinnability Analyzer (CHA)](https://github.com/miguel-ambrona/D3-Chess) as a flagship feature.

It's not a chess engine — it does not calculate best moves for a given position.

It's also not built for performance; if you need fast move generation, use for example Stockfish.
It is built for correctness and comprehension — for example, it produces meaningful messages for SAN, FEN, and PGN validation.

For the design philosophy, architecture, and rule-level decisions, see [specification.md](specification.md).

clean-chess includes a Java port of the Chess Unwinnability Analyzer (CHA) by Miguel Ambrona, used for unwinnability and dead-position detection.

The test suite also cross-validates selected behavior against external chess libraries, currently python-chess as the primary oracle and chesslib by Carlos B. Langonijr as a secondary witness. These libraries are used for testing only and are not runtime dependencies of clean-chess.

## Not supported

- PGN move variations
- PGN Numeric Annotation Glyphs (NAGs, e.g. `$1`, `$10`)
- Multi-game PGN files (one game per file)

UTF-8 byte-order marks (BOM) are accepted by the lenient parser (stripped on input) and rejected by the strict parser. PGN move suffix annotations (`!`, `?`, `!!`, `??`, `!?`, `?!`) are fully parsed, modeled, and round-tripped on export by both parsers.

# Using clean-chess as a dependency

Requires JDK 17 or later at runtime. Available via the [JitPack](https://jitpack.io) repository.

## Maven

```xml
<repositories>
  ...
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```

```xml
<dependency>
  <groupId>com.github.dlbbld</groupId>
  <artifactId>clean-chess</artifactId>
  <version>17.0.0</version>
</dependency>
```

## Gradle

```groovy
repositories {
    ...
    maven { url 'https://jitpack.io' }
}
```

```groovy
dependencies {
    ...
    implementation 'com.github.dlbbld:clean-chess:17.0.0'
    ...
}
```

# Building from source

```
$ git clone git@github.com:dlbbld/clean-chess.git
$ cd clean-chess/
$ mvn clean compile package install
```

For the full Eclipse contributor workflow (project import, Checkstyle, formatter, save actions), see [setup.md](setup.md).

# Basic usage example
```java
  final Board board = new Board();

  board.moveStrict("e4"); // specifying the SAN
  board.movesStrict("e5", "Bc4"); // specifying multiple SAN's

  final var newMove = new MoveSpecification(Square.F8, Square.C5);
  board.move(newMove); // move specification without SAN

  board.unmove(); // undoes last move

  board.movesStrict("Bc5", "Qf3", "h6", "Qxf7#");

  System.out.println(board.isCheckmate()); // true
```

# Motivation for the chess library
Below I write my motivation for programming this chess library.

## Threefold repetition and fifty-moves
When I wanted to check a game for the occurrence of a threefold repetition or the fifty moves, I could not find any software
providing a reasonable way to do it.

The only way to check this I found in dozens of chess programs is playing through the game move by move, and the software then announces threefold for fifty moves if it occurs.

To check a game for possible threefold claims on the next move, so possibly the player missed a chance to claim threefold, I found nothing, nada, nothing at all.

For this reason, I implemented a report which shows the threefolds and fifty moves in a game.

## Unwinnability and dead position
Current chess programs cannot correctly determine unwinnability and dead positions for all positions, and as a result, the game result for some games is incorrect.
The [Chess Unwinnability Analyzer (CHA)](https://github.com/miguel-ambrona/D3-Chess) provides an algorithm to close the gap. I implemented the algorithm in Java.

## Java chess library
There are several Java chess libraries, but because this chess library is about game-deciding situations of having a potential draw or not, I did not
want to rely on other chess libraries, for I want to be sure that what I have is 100% correct. Because I heavily rely on tests and PGNs are indispensable in tests, I implemented a PGN reader and writer. And because these tests must be accurate, I spent a lot of time making the PGN reader
accurate for every situation.

# Threefold repetition and fifty-moves
## Threefold repetition claim ahead
The following game ended with a threefold repetition claim ahead according to [Wikipedia](https://en.wikipedia.org/wiki/Threefold_repetition#Portisch_versus_Korchnoi,_1970):

```java
  final var pgn = """
      1. Nf3 c5 2. c4 Nf6 3. Nc3 Nc6 4. d4 cxd4 5. Nxd4 e6 6. g3 Qb6 7. Nb3 Ne5 8. e4
      Bb4 9. Qe2 O-O 10. f4 Nc6 11. e5 Ne8 12. Bd2 f6 13. c5 Qd8 14. a3 Bxc3 15. Bxc3
      fxe5 16. Bxe5 b6 17. Bg2 Nxe5 18. Bxa8 Nf7 19. Bg2 bxc5 20. Nxc5 Qb6 21. Qf2
      Qb5 22. Bf1 Qc6 23. Bg2 Qb5 24. Bf1 Qc6 25. Bg2""";

  Reporter.printReport(pgn);
```

The report mentions the possible claim ahead:
```
Valid threefold claims ahead:
25... Qb5

Threefolds and beyond:
None

Sequences without capture and pawn move starting from 25 moves:
None

Fifty moves without capture and pawn move:
No
```
Black could have claimed a threefold on the 25th move with writing (but not playing) 25... Qb5. White's possible claims are along the move number
followed by a dot (for example, 20. Ra2). Possible claims for Black are along the move number followed by three dots (for example, 25... Qb5).

## Threefold repetition on the board
The following game contains a threefold repetition according to [Wikipedia](https://en.wikipedia.org/wiki/Threefold_repetition#Capablanca_versus_Lasker,_1921):

```java
  final var pgn = """
      1. d4 d5 2. Nf3 Nf6 3. c4 e6 4. Bg5 Nbd7 5. e3 Be7 6. Nc3 O-O 7. Rc1 b6 8. cxd5
      exd5 9. Qa4 c5 10. Qc6 Rb8 11. Nxd5 Bb7 12. Nxe7+ Qxe7 13. Qa4 Rbc8 14. Qa3 Qe6
      15. Bxf6 Qxf6 16. Ba6 Bxf3 17. Bxc8 Rxc8 18. gxf3 Qxf3 19. Rg1 Re8 20. Qd3 g6
      21. Kf1 Re4 22. Qd1 Qh3+ 23. Rg2 Nf6 24. Kg1 cxd4 25. Rc4 dxe3 26. Rxe4 Nxe4 27.
      Qd8+ Kg7 28. Qd4+ Nf6 29. fxe3 Qe6 30. Rf2 g5 31. h4 gxh4 32. Qxh4 Ng4 33. Qg5+
      Kf8 34. Rf5 h5 35. Qd8+ Kg7 36. Qg5+ Kf8 37. Qd8+ Kg7 38. Qg5+ Kf8 39. b3 Qd6
      40. Qf4 Qd1+ 41. Qf1 Qd7 42. Rxh5 Nxe3 43. Qf3 Qd4 44. Qa8+ Ke7 45. Qb7+ Kf8 46.
      Qb8+ *""";

  Reporter.printReport(pgn);
```

Output:
```
Valid threefold claims ahead:
38... Kf8
39. Qd8+

Threefolds and beyond:
34...h5 (A - 1/3) 36...Kf8 (A - 2/3) 38...Kf8 (A - 3/3)

Sequences without capture and pawn move starting from 25 moves:
None

Fifty moves without capture and pawn move:
No
```
The repetitions are indicated in order as occurred on the board. As different positions can repeat, letters A, B, C etc., indicate the different positions in the order of the first occurrence.

## Fifty-moves
According to [Wikipedia](https://en.wikipedia.org/wiki/Fifty-move_rule#Karpov_vs._Kasparov,_1991), the next game ends with a series above fifty moves without capture and pawn move. The number of halfmoves without capture and pawn moves are in the brackets. (1) for the series start, (100) - fifty moves reached, (103) - end of series.

```java
  final var pgn = """
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

  Reporter.printReport(pgn);
```

Output:
```
Valid threefold claims ahead:
None

Threefolds and beyond:
None

Sequences without capture and pawn move starting from 25 moves:
63...Rg8 (0.5) 113.Ng5 (50) 114...Rf6+ (51.5)

Fifty moves without capture and pawn move:
Yes
```
The numbers in parentheses are the number of full moves. So "0.5" is one halfmove, "50" is 100 halfmoves and "51.5" is 103 halfmoves.
The halfmove series always indicates the first halfmove with (0.5), fifty halfmoves with (50), and seventy-five halfmoves if reached as (75) 
and finally, the last halfmove in the series.

# Game adjudication
The game-ending logic is easiest to understand when written out directly. First decide which player would otherwise
win, then run the material-only check, then run the CHA quick position check.

## Flagfall
Under [FIDE 6.9](https://handbook.fide.com/chapter/e012023), a player who runs out of time loses, unless the opponent
cannot checkmate by any possible series of legal moves. The recommended procedure is:

```text
on flagfall(flaggingPlayer):
    wouldBeWinner = opponent(flaggingPlayer)

    if board.isInsufficientMaterial(wouldBeWinner):
        return draw

    if board.isUnwinnableQuick(wouldBeWinner) == UNWINNABLE:
        return draw

    return loss for flaggingPlayer
```

The insufficient-material check is material-wise and very quick. It covers the standard cases such as a lone kings, or a
king and bishop against a lone king.

The unwinnable-quick check is position-wise. It is the CHA extension that also sees blocked positions such as pawn walls.
If it returns `UNWINNABLE`, the game is drawn. Otherwise, the flagging player loses.

`Board.isUnwinnableFull(Side)` can additionally be used for analysis, studies, or offline review. It is not the
recommended live-game path because it performs a bounded search, can take much longer in rare positions, and can return
`UNDETERMINED`.

## Resignation
Under [FIDE 5.1.2](https://handbook.fide.com/chapter/e012023), resignation has the same exception as flagfall: the game
is drawn if the opponent cannot checkmate by any possible series of legal moves. So a resignation should run exactly the
same test and report exactly the same result:

```text
on resignation(resigningPlayer):
    return adjudicate as for flagfall(resigningPlayer)
```

If the opponent has insufficient material, the game is a draw. If the opponent is `UNWINNABLE` by the quick analyzer, the
game is also a draw. Otherwise, the resigning player loses.

## Dead position during play
Under [FIDE 5.2.2](https://handbook.fide.com/chapter/e012023), the game is drawn as soon as a dead position arises:
neither player can checkmate by any possible series of legal moves.

The standard material-only dead positions should still be checked during play:

```text
after each move:
    if board.isInsufficientMaterial():
        return draw
```

With this library, a server could additionally check for position-wise dead positions detected by CHA quick, for example
blocked pawn walls:

```text
after each move:
    if board.isDeadPositionQuick() == DEAD_POSITION:
        return draw
```

This quick check is computationally practical, but my recommendation for live games is not to run the CHA dead-position
check after every move. These positions are rare. It is enough to check them at the end of the game, especially when a
player flags or resigns. This cannot be unfair: once a game has entered a dead position, no later legal play can make it
winnable again. If the players continue in a blocked position until flagfall, resignation, or draw agreement, the
adjudication above still returns a draw.

The trade-off is timing, not outcome. Checking during play gives the exact FIDE 5.2.2 termination point; checking at the
end preserves the final result.

# Unwinnability API

The library implements the [Chess Unwinnability Analyzer (CHA)](https://github.com/miguel-ambrona/D3-Chess). As such, everything here achieved is due to CHA. Also, all relevant examples below are from the [CHA](https://github.com/miguel-ambrona/D3-Chess), which elaborates on the subject in every aspect.

A position is unwinnable for a player if there is no legal sequence that can end with that player giving checkmate,
even if the opponent cooperates. If the position is unwinnable for both players, it's a dead position.

> **Note:** quick/full dead-position detection is caller-invoked. `Board` does not run the quick analyzer during
> construction or after each move; callers that want to adjudicate analyzer-driven dead positions can query
> `Board.isDeadPositionQuick()` / `Board.isDeadPositionFull()` or the side-specific unwinnability APIs.

## Methods
The library provides an implementation of CHA. So for both situations, there is a quick and a full method.

The quick method is speedy by design but might miss some corrections. The full method is slower and complete when it
returns WINNABLE or UNWINNABLE; bounded search may return UNDETERMINED.

### Unwinnability
The quick method has three return values:
* UNWINNABLE - the position is not winnable by the player
* WINNABLE - the position is winnable by the player
* POSSIBLY_WINNABLE - the position is most likely winnable by the player, but it might also be unwinnable in some rare cases

`Board.isUnwinnableQuick(Side)` returns this verdict directly. `UnwinnableQuickAnalyzer.unwinnableQuick(...)` returns
`UnwinnabilityQuickAnalysis`, which includes the verdict and the helpmate line when the quick result is `WINNABLE`.

The full method also has three return values:
* UNWINNABLE - the position is not winnable by the player
* WINNABLE - the position is winnable by the player
* UNDETERMINED - the limits in the code interrupted the search

Performance: The limit regarding "UNDETERMINED" is 500'000 positions. It takes around one minute to reach. Most positions evaluate below one second. 

### Dead position
The quick method has three return values:
* DEAD_POSITION - the position is a dead position
* NON_DEAD_POSITION - the position is not a dead position
* POSSIBLY_NON_DEAD_POSITION - the position is most likely a non-dead position, but it might also be a dead position in some rare cases

The full method also has three return values:
* DEAD_POSITION - the position is a dead position
* NON_DEAD_POSITION - the position is not a dead position
* UNDETERMINED - the limits in the code interrupted the search

Performance: The comment from the Unwinnablity section for UNDETERMINED applies here. However, it checks both sides so that it can take double the time.

## Examples

### Unwinnable

#### Insufficient material
The most common situations of unwinnable are if one side has insufficient material.
These are treated correctly by all standard chess libraries.
For example, if White flags with the king and rook against the lone king of Black. Then, Black cannot potentially mate with the king alone.
[Position](https://lichess.org/analysis/8/8/4k3/3R4/2K5/8/8/8_w_-_-_0_50)

```java
  final Board board = new Board("8/8/4k3/3R4/2K5/8/8/8 w - - 0 50");
  System.out.println(board.isUnwinnableQuick(Side.BLACK)); // UNWINNABLE
  System.out.println(board.isUnwinnableFull(Side.BLACK)); // UNWINNABLE
```

#### Pawn walls
Pawn walls are blocked positions, both players cannot mate and cannot make progress, so they are dead positions. They are not detected
by most common chess libraries. 
[Game](https://lichess.org/c3ew66ZV#123)

```java
  final Board board = new Board("8/8/3k4/1p2p1p1/pP1pP1P1/P2P4/1K6/8 b - - 32 62");
  System.out.println(board.isUnwinnableQuick(Side.BLACK)); // UNWINNABLE
  System.out.println(board.isUnwinnableFull(Side.BLACK)); // UNWINNABLE
```

#### Forced moves
There are everyday situations mainly in lower time controls like Bullet, where the game could only continue with a few
forced moves, and the game outcome is determined. Here Black flags, but there is no game continuation possible where
White could have won.
[Game](https://lichess.org/OawUhnkq#101)

```java
  final Board board = new Board("5r1k/6P1/7K/5q2/8/8/8/8 b - - 0 51");
  System.out.println(board.isUnwinnableQuick(Side.WHITE)); // UNWINNABLE
  System.out.println(board.isUnwinnableFull(Side.WHITE)); // UNWINNABLE
```

#### Common positions
When there are still a lot of pieces on the board, so a mate is very likely, the quick algorithm says POSSIBLY_WINNABLE.
It makes an educated guess only. In this example, the full algorithm calculates an actual mate; in harder positions,
the bounded search may return UNDETERMINED.
[Game](https://lichess.org/SCKpvJQX#57)

```java
  final Board board = new Board("q4r2/pR3pkp/1p2p1p1/4P3/6P1/1P3Q2/1Pr2PK1/3R4 b - - 3 29");
  System.out.println(board.isUnwinnableQuick(Side.WHITE)); // POSSIBLY_WINNABLE
  System.out.println(board.isUnwinnableFull(Side.WHITE)); // WINNABLE
```

#### Positions the quick algorithm does not see
The following is an example of a position where the quick algorithm says POSSIBLY_WINNABLE, but the position is winnable. [Game](https://lichess.org/bKHPqNEw#81)

```java
  final Board board = new Board("1k6/1P5p/BP3p2/1P6/8/8/5PKP/8 b - - 0 41");
  System.out.println(board.isUnwinnableQuick(Side.WHITE)); // POSSIBLY_WINNABLE
  System.out.println(board.isUnwinnableFull(Side.WHITE)); // UNWINNABLE
```

### Dead positions
Because dead positions are just unwinnable positions for both sides, there is not much more substantially to say.

#### Insufficient material
The most straightforward dead position is when one player already has insufficient material, and the other becomes insufficient due to capture. All chess libraries detect this case.

[Position](https://lichess.org/analysis/8/8/3kn3/8/2K5/8/8/8_w_-_-_0_50)
```java
  final Board board = new Board("8/8/3kn3/8/2K5/8/8/8 w - - 0 50");
  System.out.println(board.isDeadPositionQuick()); // DEAD_POSITION
  System.out.println(board.isDeadPositionFull()); // DEAD_POSITION
```

#### Pawn walls
Pawn walls are dead positions, but most common chess libraries do not detect them. Here is another example.
[Game](https://lichess.org/V08kX4kz#121)

```java
  final Board board = new Board("8/6b1/1p3k2/1Pp1p1p1/2P1PpP1/5P2/8/5K2 b - - 11 61");
  System.out.println(board.isDeadPositionQuick()); // DEAD_POSITION
  System.out.println(board.isDeadPositionFull()); // DEAD_POSITION
```

#### Forced moves
Positions can also often be dead due to forced moves.
[Game](https://lichess.org/8FUSHxUV#115)

```java
  final Board board = new Board("k7/P1K5/8/8/8/8/8/8 b - - 2 58");
  System.out.println(board.isDeadPositionQuick()); // DEAD_POSITION
  System.out.println(board.isDeadPositionFull()); // DEAD_POSITION
```

# PGN functionality
      
## PGN parser
      
### Lenient PGN parser
The common PGN parser — reads the file with best effort. For example, the space after `[` below is ignored. See the [Not supported](#not-supported) section above for what neither parser accepts.

clean-chess ships **lenient parsers for all three input languages it consumes** — SAN, PGN, and FEN. Each one applies a typed syntactic-tolerance pass and surfaces tolerated deviations as forgiven items on the validation result, then delegates the heavy lifting to the corresponding strict parser. The PGN flavour (described in this section) routes its SAN tokens through the lenient SAN layer and its `FEN` tag through the lenient FEN layer, so a single lenient PGN parse picks up deviations across all three languages. The lenient FEN layer is reachable directly via `Board.fromFenLenient(String)` for callers that consume FEN strings outside the PGN context (engine output, lichess/chess.com exports, hand-edited fixtures); see `specification.md` §3.3.3 for the strict-vs-lenient × raw-vs-advanced contract and the full `ForgivenFenItemCode` taxonomy.

In addition to structural tolerances (whitespace, missing tags, optional termination markers), the lenient parser accepts a defined set of SAN deviations from canonical — see [PGN SAN tolerances](#pgn-san-tolerances) below.

#### PGN valid

```java
   final var pgn = """
        [ Event "Spring Classic"]

        1. e4 e5   2. Nf3
        Nf6
          3. Bc4 Bc5
                """;

    final PgnGame pgnGame = LenientPgnParser.parseText(pgn);
    final Board board = PgnUtility.calculateBoardPerLastMove(pgnGame);
    board.moveStrict("a3");

```

#### PGN transformation to export format

The parser does a bit more than a standard parser should do. It converts the imported PGN to a PGN object which when exported will adhere to the export format. That is it for example adds missing tags and sorts them if necessary.

```java
    final var pgn = """
                [Black "Jane Doe"]
                [White "John Doe"]
                [ Event "Spring Classic"]

                1. e4 e5   2. Nf3
                Nf6
                3. Bc4 Bc5
        """;

    final PgnGame pgnGame = LenientPgnParser.parseText(pgn);
    System.out.println(PgnCreate.createPgnString(pgnGame));
    // [Event "Spring Classic"]
    // [Site "?"]
    // [Date "?"]
    // [Round "?"]
    // [White "John Doe"]
    // [Black "Jane Doe"]
    // [Result "*"]
    //
    // 1. e4 e5 2. Nf3 Nf6 3. Bc4 Bc5 *
    // 
```

#### PGN SAN tolerances

The lenient PGN parser accepts SAN moves that deviate from canonical SAN in any of the following ways. Each accepted deviation is surfaced as a typed `ForgivenItem` via `LenientPgnParserValidationResult.sanForgivenItems()`, so consumers can either silently accept or warn the user. The full taxonomy (21 codes) is documented in `specification.md` §3.3.1.

- **Castling**: `0-0` / `0-0-0` (zero instead of letter O)
- **Notation form**: long algebraic (`e2-e4`, `Nb1-d7`), UCI (`e2e4`, `e7e8q`, `e1g1` for castling), explicit pawn letter (`Pe4`)
- **Disambiguation**: redundant file/rank/square (e.g. `Nbd7` when `Nd7` would suffice), non-canonical rank-instead-of-file (`R1d4` where canonical is `Rad4`)
- **Capture marker**: missing (`Be5` when actually a capture) or spurious (`Bxe5` to an empty square)
- **Check / mate suffix**: missing, spurious, or wrong (`Nd7+` when actually mate, `Nd7#` when only check, `Nd7` when actually check, etc.)
- **Promotion**: missing `=` (`e8Q`)
- **Case**: lowercase piece letter (`nf3`), uppercase file letter (`NF3`), uppercase capture marker (`BXe5`), lowercase promotion piece (`e8=q`)

```java
    final var pgn = """
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
    System.out.println(result.isValid()); // true
    for (final ForgivenItem item : result.sanForgivenItems()) {
      System.out.println(item.code() + ": " + item.originalToken() + " -> " + item.canonicalSan());
    }
    // ZERO_INSTEAD_OF_O_CASTLING: 0-0 -> O-O
    // LOWERCASE_PIECE_LETTER: nf6 -> Nf6
```

The strict pipeline that performs the actual chess validation is reused unchanged; the lenient layer only translates input shape and recovers from a defined set of strict rejections. A move that's not a legal chess move (regardless of how it was written) is still rejected.

#### PGN invalid

When parsing fails, error messages are designed to be as descriptive as possible.

```java
    final var pgn = """
        [ Event "Spring Classic"]

        1. e4 e5   2. Nf4
        Nf6
          3. Bc4 Bc5
                """;

    final PgnGame pgnGame;
    try {
      pgnGame = LenientPgnParser.parseText(pgn);
      System.out.println(PgnUtility.calculateBoardPerLastMove(pgnGame).isCheck()); // not reached
    } catch (final LenientPgnParserValidationException e) {
      System.out.println(e.getMessage());
      // The validation for 2. Nf4 failed. Reason: The move specification is invalid because there is no knight which
      // can move to square f4.
      return;
    }
```

#### File parsing

```java
    final PgnGame pgnGame = LenientPgnParser.parse("C:\\temp\\myFile.pgn");
    final Board board = PgnUtility.calculateBoardPerLastMove(pgnGame);
    System.out.println(board.isCheckmate());
```

### Strict PGN parser
The strict PGN parser does not allow inconsistencies as the lenient PGN parser. It expects the PGN to be in the export format according to the PGN specification.

#### PGN valid

```java
    final var pgn = """
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
    final Board board = PgnUtility.calculateBoardPerLastMove(pgnGame);
    board.moveStrict("a3");
```
    
#### PGN invalid syntax

```java
    final var pgn = """
        [ Event "Spring Classic"]

        1. e4 e5 2. Nf3 Nf6 3. Bc4 Bc5
        """;

    final PgnGame pgnGame;
    try {
      pgnGame = StrictPgnParser.parseText(pgn);
      System.out.println(PgnUtility.calculateBoardPerLastMove(pgnGame).isCheck()); // not reached
    } catch (final StrictPgnParserValidationException e) {
      System.out.println(e.getMessage());
      // The left square bracket [ must be followed by the tag name, but a space was found.
      return;
    }
```

#### PGN invalid form

```java
    final var pgn = """
        [Event "Spring Classic"]

        1. e4 e5 2. Nf3 Nf6 3. Bc4 Bc5
        """;

    final PgnGame pgnGame;
    try {
      pgnGame = StrictPgnParser.parseText(pgn);
      System.out.println(PgnUtility.calculateBoardPerLastMove(pgnGame).isCheck()); // not reached
    } catch (final StrictPgnParserValidationException e) {
      System.out.println(e.getMessage());
      // Not all tags from the seven tag roster (Event, Site, Date, Round, White, Black, Result) are set. The first not
      // found tag is "Site".
      return;
    }
```

#### File parsing

```java
    final PgnGame pgnGame = StrictPgnParser.parse("C:\\temp\\myFile.pgn");
    final Board board = PgnUtility.calculateBoardPerLastMove(pgnGame);
    System.out.println(board.isThreefoldRepetition());
```
      
## PGN creation

### Create PGN for game

You can create the PGN for a game played in the library or export an imported PGN.

```java
    final Board board = new Board();
    board.movesStrict("e4", "e5", "Nf3", "Nf6", "Bc4", "Bc5");

    final PgnGame pgnGame = PgnCreate.createPgnGame(board);
    System.out.println(PgnCreate.createPgnString(pgnGame));
    // [Event "?"]
    // [Site "?"]
    // [Date "<today>"]
    // [Round "?"]
    // [White "?"]
    // [Black "?"]
    // [Result "*"]
    //
    // 1. e4 e5 2. Nf3 Nf6 3. Bc4 Bc5 *
    //
```

### PGN format

The PGN is created in the unique export format as defined by the PGN specification so passes validation by the lenient and strict PGN parser.

```java
    final Board board = new Board();
    board.movesStrict("e4", "e5", "Nf3", "Nf6", "Bc4", "Bc5");

    final PgnGame pgnGame = PgnCreate.createPgnGame(board);

    final String pgnString = PgnCreate.createPgnString(pgnGame);
    System.out.println(LenientPgnParser.validateText(pgnString).isValid()); // true
    System.out.println(StrictPgnParser.validateText(pgnString).isValid()); // true
```

## PGN export

A PGN can be written to the file system as below.

```java
    final Board board = new Board();
    board.movesStrict("e4", "e5", "Nf3", "Nf6", "Bc4", "Bc5");

    final PgnGame pgnGame = PgnCreate.createPgnGame(board);
    PgnWriter.writePgn(pgnGame, "C:\\temp\\myFile.pgn");
```
    
## PGN validation

### PGN lenient validation
Checks whether a PGN can be parsed using the PGN lenient parser.

#### PGN valid

```java
    final var pgn = """
        [ Event "Spring Classic"]

        1. e4 e5   2. Nf3
        Nf6
          3. Bc4 Bc5
                """;
    final LenientPgnParserValidationResult result = LenientPgnParser.validateText(pgn);
    System.out.println(result.isValid()); // true
```

#### PGN invalid

```java
    final var pgn = """
        [ Event "Spring Classic"]

        1. e4 e5   2. Nf3
        Nf6
          3. Bc4 Bc5 4. Y1
                """;
    final LenientPgnParserValidationResult result = LenientPgnParser.validateText(pgn);
    System.out.println(result.isValid()); // false
    System.out.println(result.message());
    // The movetext is invalid because a SAN contains an invalid character of "Y".
```

#### File validation

```java
    final LenientPgnParserValidationResult result = LenientPgnParser.validate("C:\\temp\\myFile.pgn");
    System.out.println(result.isValid());
```

### PGN strict validation

Checks whether a PGN adheres to the export format per the PGN specification.

#### PGN valid

```java
    final var pgn = """
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

#### PGN invalid

```java
    final var pgn = """
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
    System.out.println(result.isValid()); // false
    System.out.println(result.message());
    // The movetext does not continue with move number "3. " as expected
```
    
#### File validation

```java
    final StrictPgnParserValidationResult result = StrictPgnParser.validate("C:\\temp\\myFile.pgn");
    System.out.println(result.isValid());
```

# License

Copyright (C) 2024-2026  Daniel Bächli

clean-chess is free software, licensed under the GNU General Public License, version 3 (GPL v3). See [LICENSE](LICENSE) for the full text.

The unwinnability and dead-position detection is a Java port of the [Chess Unwinnability Analyzer (CHA)](https://github.com/miguel-ambrona/D3-Chess) by Miguel Ambrona, also licensed under GPL v3.
