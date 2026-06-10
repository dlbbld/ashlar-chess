// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.readme;

import java.util.List;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParser;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParserValidationException;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParserValidationResult;
import io.github.dlbbld.ashlarchess.pgn.PgnCreate;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.PgnUtility;
import io.github.dlbbld.ashlarchess.pgn.PgnWriter;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParser;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParserValidationException;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParserValidationResult;
import io.github.dlbbld.ashlarchess.pgn.WriteMode;
import io.github.dlbbld.ashlarchess.report.Reporter;
import io.github.dlbbld.ashlarchess.san.ForgivenItem;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnableFullAnalyzer;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnableQuickAnalyzer;

/**
 * Source of truth for the runnable code examples shown in {@code README.md}. Each method is one README example: the
 * lines between its {@code // <readme:ID>} and {@code // </readme:ID>} markers are sliced verbatim into the rendered
 * README as the shown snippet, and the method's captured {@code stdout} becomes the cited output block beneath it.
 *
 * <p>
 * {@link ReadmeDoc} performs the slicing and capture; {@code TestReadmeUpToDate} fails the build unless the committed
 * {@code README.md} matches a fresh render. Together these guarantee that every shown snippet is real, compiled code
 * and every shown output is exactly what that code printed - the README cannot drift from the library.
 *
 * <p>
 * {@link #examples()} maps each example id (matching its markers and its {@code README.template.md} placeholders) to
 * the method that produces it.
 */
public final class ReadmeExamples {

  private ReadmeExamples() {
  }

  /**
   * The registered examples, each identified by the id used in its source markers and its {@code README.template.md}
   * placeholders. Order is for readability only - the template controls where each example renders.
   */
  public static List<ReadmeExample> examples() {
    return Nulls.listOf(new ReadmeExample("threefold-claim-ahead", ReadmeExamples::threefoldClaimAhead, true),
        new ReadmeExample("threefold-on-board", ReadmeExamples::threefoldOnBoard, true),
        new ReadmeExample("fifty-move", ReadmeExamples::fiftyMove, true),
        new ReadmeExample("basic-usage", ReadmeExamples::basicUsage, true),
        new ReadmeExample("unwinnable-insufficient-material", ReadmeExamples::unwinnableInsufficientMaterial, true),
        new ReadmeExample("unwinnable-forced-moves", ReadmeExamples::unwinnableForcedMoves, true),
        new ReadmeExample("unwinnable-pawn-walls", ReadmeExamples::unwinnablePawnWalls, true),
        new ReadmeExample("unwinnable-common-positions", ReadmeExamples::unwinnableCommonPositions, true),
        new ReadmeExample("unwinnable-blocked-quick", ReadmeExamples::unwinnableBlockedQuick, true),
        new ReadmeExample("dead-insufficient-material", ReadmeExamples::deadInsufficientMaterial, true),
        new ReadmeExample("dead-pawn-walls", ReadmeExamples::deadPawnWalls, true),
        new ReadmeExample("dead-forced-moves", ReadmeExamples::deadForcedMoves, true),
        new ReadmeExample("pgn-lenient-valid", ReadmeExamples::pgnLenientValid, true),
        new ReadmeExample("pgn-lenient-export-transform", ReadmeExamples::pgnLenientExportTransform, true),
        new ReadmeExample("pgn-san-tolerances", ReadmeExamples::pgnSanTolerances, true),
        new ReadmeExample("pgn-lenient-invalid", ReadmeExamples::pgnLenientInvalid, true),
        new ReadmeExample("pgn-lenient-file-parsing", ReadmeExamples::pgnLenientFileParsing, false),
        new ReadmeExample("pgn-strict-valid", ReadmeExamples::pgnStrictValid, true),
        new ReadmeExample("pgn-strict-invalid-syntax", ReadmeExamples::pgnStrictInvalidSyntax, true),
        new ReadmeExample("pgn-strict-invalid-form", ReadmeExamples::pgnStrictInvalidForm, true),
        new ReadmeExample("pgn-strict-file-parsing", ReadmeExamples::pgnStrictFileParsing, false),
        new ReadmeExample("pgn-create-game", ReadmeExamples::pgnCreateGame, true),
        new ReadmeExample("pgn-format", ReadmeExamples::pgnFormat, true),
        new ReadmeExample("pgn-export", ReadmeExamples::pgnExport, false),
        new ReadmeExample("pgn-lenient-validation-valid", ReadmeExamples::pgnLenientValidationValid, true),
        new ReadmeExample("pgn-lenient-validation-invalid", ReadmeExamples::pgnLenientValidationInvalid, true),
        new ReadmeExample("pgn-lenient-validation-file", ReadmeExamples::pgnLenientValidationFile, false),
        new ReadmeExample("pgn-strict-validation-valid", ReadmeExamples::pgnStrictValidationValid, true),
        new ReadmeExample("pgn-strict-validation-invalid", ReadmeExamples::pgnStrictValidationInvalid, true),
        new ReadmeExample("pgn-strict-validation-file", ReadmeExamples::pgnStrictValidationFile, false));
  }

  public static void threefoldClaimAhead() {
    // <readme:threefold-claim-ahead>
    final String pgn = """
        1. Nf3 c5 2. c4 Nf6 3. Nc3 Nc6 4. d4 cxd4 5. Nxd4 e6 6. g3 Qb6 7. Nb3 Ne5 8. e4
        Bb4 9. Qe2 O-O 10. f4 Nc6 11. e5 Ne8 12. Bd2 f6 13. c5 Qd8 14. a3 Bxc3 15. Bxc3
        fxe5 16. Bxe5 b6 17. Bg2 Nxe5 18. Bxa8 Nf7 19. Bg2 bxc5 20. Nxc5 Qb6 21. Qf2
        Qb5 22. Bf1 Qc6 23. Bg2 Qb5 24. Bf1 Qc6 25. Bg2""";
    Reporter.printReport(pgn);
    // </readme:threefold-claim-ahead>
  }

  public static void threefoldOnBoard() {
    // <readme:threefold-on-board>
    final String pgn = """
        1. d4 d5 2. Nf3 Nf6 3. c4 e6 4. Bg5 Nbd7 5. e3 Be7 6. Nc3 O-O 7. Rc1 b6 8. cxd5
        exd5 9. Qa4 c5 10. Qc6 Rb8 11. Nxd5 Bb7 12. Nxe7+ Qxe7 13. Qa4 Rbc8 14. Qa3 Qe6
        15. Bxf6 Qxf6 16. Ba6 Bxf3 17. Bxc8 Rxc8 18. gxf3 Qxf3 19. Rg1 Re8 20. Qd3 g6
        21. Kf1 Re4 22. Qd1 Qh3+ 23. Rg2 Nf6 24. Kg1 cxd4 25. Rc4 dxe3 26. Rxe4 Nxe4 27.
        Qd8+ Kg7 28. Qd4+ Nf6 29. fxe3 Qe6 30. Rf2 g5 31. h4 gxh4 32. Qxh4 Ng4 33. Qg5+
        Kf8 34. Rf5 h5 35. Qd8+ Kg7 36. Qg5+ Kf8 37. Qd8+ Kg7 38. Qg5+ Kf8 39. b3 Qd6
        40. Qf4 Qd1+ 41. Qf1 Qd7 42. Rxh5 Nxe3 43. Qf3 Qd4 44. Qa8+ Ke7 45. Qb7+ Kf8 46.
        Qb8+ *""";
    Reporter.printReport(pgn);
    // </readme:threefold-on-board>
  }

  public static void fiftyMove() {
    // <readme:fifty-move>
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
    Reporter.printReport(pgn);
    // </readme:fifty-move>
  }

  public static void basicUsage() {
    // <readme:basic-usage>
    final Board board = new Board();

    board.moveStrict("e4"); // specifying the SAN
    board.movesStrict("e5", "Bc4"); // specifying multiple SAN's

    final MoveSpecification newMove = new MoveSpecification(Square.F8, Square.C5);
    board.move(newMove); // move specification without SAN

    board.unmove(); // undoes last move

    board.movesStrict("Bc5", "Qf3", "h6", "Qxf7#");

    System.out.println(board.isCheckmate()); // [out]
    // </readme:basic-usage>
  }

  public static void unwinnableInsufficientMaterial() {
    // <readme:unwinnable-insufficient-material>
    final Board board = new Board("8/8/4k3/3R4/2K5/8/8/8 w - - 0 50");
    System.out.println(board.isUnwinnableQuick(Side.BLACK)); // [out]
    System.out.println(board.isUnwinnableFull(Side.BLACK)); // [out]
    // </readme:unwinnable-insufficient-material>
  }

  public static void unwinnableForcedMoves() {
    // <readme:unwinnable-forced-moves>
    final Board board = new Board("5r1k/6P1/7K/5q2/8/8/8/8 b - - 0 51");
    System.out.println(board.isUnwinnableQuick(Side.WHITE)); // [out]
    System.out.println(board.isUnwinnableFull(Side.WHITE)); // [out]
    // </readme:unwinnable-forced-moves>
  }

  public static void unwinnablePawnWalls() {
    // <readme:unwinnable-pawn-walls>
    final Board board = new Board("8/8/3k4/1p2p1p1/pP1pP1P1/P2P4/1K6/8 b - - 32 62");
    System.out.println(board.isUnwinnableQuick(Side.BLACK)); // [out]
    System.out.println(board.isUnwinnableFull(Side.BLACK)); // [out]
    // </readme:unwinnable-pawn-walls>
  }

  public static void unwinnableCommonPositions() {
    // <readme:unwinnable-common-positions>
    final Board board = new Board("q4r2/pR3pkp/1p2p1p1/4P3/6P1/1P3Q2/1Pr2PK1/3R4 b - - 3 29");
    System.out.println(board.isUnwinnableQuick(Side.WHITE)); // [out]
    System.out.println(board.isUnwinnableFull(Side.WHITE)); // [out]
    // </readme:unwinnable-common-positions>
  }

  public static void unwinnableBlockedQuick() {
    // <readme:unwinnable-blocked-quick>
    final Board board = new Board("1k6/1P5p/BP3p2/1P6/8/8/5PKP/8 b - - 0 41");
    System.out.println(board.isUnwinnableQuick(Side.WHITE)); // [out]
    System.out.println(board.isUnwinnableFull(Side.WHITE)); // [out]
    // </readme:unwinnable-blocked-quick>
  }

  public static void deadInsufficientMaterial() {
    // <readme:dead-insufficient-material>
    final Board board = new Board("8/8/3kn3/8/2K5/8/8/8 w - - 0 50");
    System.out.println(UnwinnableQuickAnalyzer.unwinnableQuick(board)); // [out] (dead)
    System.out.println(UnwinnableFullAnalyzer.unwinnableFull(board)); // [out] (dead)
    // </readme:dead-insufficient-material>
  }

  public static void deadPawnWalls() {
    // <readme:dead-pawn-walls>
    final Board board = new Board("8/6b1/1p3k2/1Pp1p1p1/2P1PpP1/5P2/8/5K2 b - - 11 61");
    System.out.println(UnwinnableQuickAnalyzer.unwinnableQuick(board)); // [out] (dead)
    System.out.println(UnwinnableFullAnalyzer.unwinnableFull(board)); // [out] (dead)
    // </readme:dead-pawn-walls>
  }

  public static void deadForcedMoves() {
    // <readme:dead-forced-moves>
    final Board board = new Board("k7/P1K5/8/8/8/8/8/8 b - - 2 58");
    System.out.println(UnwinnableQuickAnalyzer.unwinnableQuick(board)); // [out] (dead)
    System.out.println(UnwinnableFullAnalyzer.unwinnableFull(board)); // [out] (dead)
    // </readme:dead-forced-moves>
  }

  public static void pgnLenientValid() {
    // <readme:pgn-lenient-valid>
    final String pgn = """
        [ Event "Spring Classic"]

        1. e4 e5   2. Nf3
        Nf6
          3. Bc4 Bc5
        """;
    final PgnGame pgnGame = LenientPgnParser.parseText(pgn);
    final Board board = PgnUtility.calculateBoard(pgnGame);
    board.moveStrict("a3");
    // </readme:pgn-lenient-valid>
  }

  public static void pgnLenientExportTransform() {
    // <readme:pgn-lenient-export-transform>
    final String pgn = """
        [Black "Jane Doe"]
        [White "John Doe"]
        [ Event "Spring Classic"]

        1. e4 e5   2. Nf3
        Nf6
        3. Bc4 Bc5
        """;
    final PgnGame pgnGame = LenientPgnParser.parseText(pgn);
    System.out.println(PgnCreate.createPgnString(pgnGame, WriteMode.ARCHIVAL));
    // </readme:pgn-lenient-export-transform>
  }

  public static void pgnSanTolerances() {
    // <readme:pgn-san-tolerances>
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
    for (final ForgivenItem item : result.sanForgivenItems()) {
      System.out.println(item.code() + ": " + item.originalToken() + " -> " + item.canonicalSan());
    }
    // </readme:pgn-san-tolerances>
  }

  public static void pgnLenientInvalid() {
    // <readme:pgn-lenient-invalid>
    final String pgn = """
        [ Event "Spring Classic"]

        1. e4 e5   2. Nf4
        Nf6
          3. Bc4 Bc5
        """;
    try {
      final PgnGame pgnGame = LenientPgnParser.parseText(pgn);
      System.out.println(PgnUtility.calculateBoard(pgnGame).isCheck()); // not reached
    } catch (final LenientPgnParserValidationException e) {
      System.out.println(e.getMessage());
    }
    // </readme:pgn-lenient-invalid>
  }

  public static void pgnLenientFileParsing() {
    // <readme:pgn-lenient-file-parsing>
    final PgnGame pgnGame = LenientPgnParser.parse("C:\\temp\\myFile.pgn");
    final Board board = PgnUtility.calculateBoard(pgnGame);
    System.out.println(board.isCheckmate());
    // </readme:pgn-lenient-file-parsing>
  }

  public static void pgnStrictValid() {
    // <readme:pgn-strict-valid>
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
    final Board board = PgnUtility.calculateBoard(pgnGame);
    board.moveStrict("a3");
    // </readme:pgn-strict-valid>
  }

  public static void pgnStrictInvalidSyntax() {
    // <readme:pgn-strict-invalid-syntax>
    final String pgn = """
        [ Event "Spring Classic"]

        1. e4 e5 2. Nf3 Nf6 3. Bc4 Bc5

        """;
    try {
      final PgnGame pgnGame = StrictPgnParser.parseText(pgn);
      System.out.println(PgnUtility.calculateBoard(pgnGame).isCheck()); // not reached
    } catch (final StrictPgnParserValidationException e) {
      System.out.println(e.getMessage());
    }
    // </readme:pgn-strict-invalid-syntax>
  }

  public static void pgnStrictInvalidForm() {
    // <readme:pgn-strict-invalid-form>
    final String pgn = """
        [Event "Spring Classic"]

        1. e4 e5 2. Nf3 Nf6 3. Bc4 Bc5 *

        """;
    try {
      final PgnGame pgnGame = StrictPgnParser.parseText(pgn);
      System.out.println(PgnUtility.calculateBoard(pgnGame).isCheck()); // not reached
    } catch (final StrictPgnParserValidationException e) {
      System.out.println(e.getMessage());
    }
    // </readme:pgn-strict-invalid-form>
  }

  public static void pgnStrictFileParsing() {
    // <readme:pgn-strict-file-parsing>
    final PgnGame pgnGame = StrictPgnParser.parse("C:\\temp\\myFile.pgn");
    final Board board = PgnUtility.calculateBoard(pgnGame);
    System.out.println(board.isThreefoldRepetition());
    // </readme:pgn-strict-file-parsing>
  }

  public static void pgnCreateGame() {
    // <readme:pgn-create-game>
    final Board board = new Board();
    board.movesStrict("e4", "e5", "Nf3", "Nf6", "Bc4", "Bc5");

    final PgnGame pgnGame = PgnCreate.createPgnGame(board);
    System.out.println(PgnCreate.createPgnString(pgnGame, WriteMode.ARCHIVAL));
    // </readme:pgn-create-game>
  }

  public static void pgnFormat() {
    // <readme:pgn-format>
    final Board board = new Board();
    board.movesStrict("e4", "e5", "Nf3", "Nf6", "Bc4", "Bc5");

    final PgnGame pgnGame = PgnCreate.createPgnGame(board);
    final String pgnString = PgnCreate.createPgnString(pgnGame, WriteMode.ARCHIVAL);
    System.out.println(LenientPgnParser.validateText(pgnString).isValid()); // [out]
    System.out.println(StrictPgnParser.validateText(pgnString).isValid()); // [out]
    // </readme:pgn-format>
  }

  public static void pgnExport() {
    // <readme:pgn-export>
    final Board board = new Board();
    board.movesStrict("e4", "e5", "Nf3", "Nf6", "Bc4", "Bc5");

    final PgnGame pgnGame = PgnCreate.createPgnGame(board);
    PgnWriter.writePgn(pgnGame, "C:\\temp\\myFile.pgn", WriteMode.ARCHIVAL);
    // </readme:pgn-export>
  }

  public static void pgnLenientValidationValid() {
    // <readme:pgn-lenient-validation-valid>
    final String pgn = """
        [ Event "Spring Classic"]

        1. e4 e5   2. Nf3
        Nf6
          3. Bc4 Bc5
        """;
    final LenientPgnParserValidationResult result = LenientPgnParser.validateText(pgn);
    System.out.println(result.isValid()); // [out]
    // </readme:pgn-lenient-validation-valid>
  }

  public static void pgnLenientValidationInvalid() {
    // <readme:pgn-lenient-validation-invalid>
    final String pgn = """
        [ Event "Spring Classic"]

        1. e4 e5   2. Nf3
        Nf6
          3. Bc4 Bc5 4. Y1
        """;
    final LenientPgnParserValidationResult result = LenientPgnParser.validateText(pgn);
    System.out.println(result.isValid());
    System.out.println(result.message());
    // </readme:pgn-lenient-validation-invalid>
  }

  public static void pgnLenientValidationFile() {
    // <readme:pgn-lenient-validation-file>
    final LenientPgnParserValidationResult result = LenientPgnParser.validate("C:\\temp\\myFile.pgn");
    System.out.println(result.isValid());
    // </readme:pgn-lenient-validation-file>
  }

  public static void pgnStrictValidationValid() {
    // <readme:pgn-strict-validation-valid>
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
    System.out.println(result.isValid()); // [out]
    // </readme:pgn-strict-validation-valid>
  }

  public static void pgnStrictValidationInvalid() {
    // <readme:pgn-strict-validation-invalid>
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
    // </readme:pgn-strict-validation-invalid>
  }

  public static void pgnStrictValidationFile() {
    // <readme:pgn-strict-validation-file>
    final StrictPgnParserValidationResult result = StrictPgnParser.validate("C:\\temp\\myFile.pgn");
    System.out.println(result.isValid());
    // </readme:pgn-strict-validation-file>
  }
}
