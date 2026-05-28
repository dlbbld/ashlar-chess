package com.dlb.chess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.pgn.LenientPgnParser;
import com.dlb.chess.pgn.PgnCreate;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.pgn.WriteMode;
import com.dlb.chess.test.pgntest.constants.PgnTestConstants;

public class TestLenientPgnParserUtf8 {

  private static final Logger logger = Nulls.getLogger(TestLenientPgnParserUtf8.class);

  @SuppressWarnings("static-method")
  @Test
  void test1() {
    final String pgn = """
        [Event "Live Chess ä, ö, ü, ß"]
        [Site "Chess.com 你好"]
        [Date "2020.07.11"]
        [Round "?"]
        [White "almtwali नमस्ते"]
        [Black "danielbaechli Привет"]
        [Result "1-0"]
        [SpecialForTesting "€, ©, ™, ✓, ∞"]

        1. d4 e6 2. c4 f5 3. Nf3 Nf6 4. e3 Be7 5. Ne5 O-O 6. h4 d6 7. h5 dxe5 8. h6 g6
        9. d5 exd5 10. Qb3 c6 11. Nd2 Qd7 12. Nf3 e4 13. Ne5 Qe6 14. Nxg6 Re8 15. Nf4
        Qd6 16. a3 Be6 17. Bd2 Na6 18. O-O-O Rab8 19. Kb1 Kh8 20. Qc3 Rg8 21. Nh5 Rbf8
        22. Ng7 Nc7 23. Qb3 b6 24. Bb4 c5 25. Bc3 Qd8 26. Nxe6 Nxe6 27. cxd5 Ng5 28. d6
        Bxd6 29. Be5 Nf7 30. Qd5 Nxe5 31. Qxd6 Qxd6 32. Rxd6 Nc4 33. Bxc4 Rxg2 34. Rhd1
        Rgg8 35. Bxg8 Nxg8 36. Rd8 Rxd8 37. Rxd8 a5 38. Kc2 b5 39. b4 cxb4 40. axb4
        axb4 41. Kb3 f4 42. exf4 e3 43. Rxg8+ Kxg8 44. fxe3 Kf7 45. e4 Kf6 46. Kxb4 Ke6
        47. Kxb5 Kd6 48. Kc4 Kc6 49. Kd4 Kd6 50. e5+ Ke6 51. Ke4 Kf7 52. f5 Ke7 53. Kf4
        Kd7 54. Kg5 Ke7 55. f6+ Kf7 56. Kf5 Kf8 57. Ke6 Ke8 58. f7+ Kf8 59. Kf5 Ke7 60.
        e6 Kf8 61. Ke5 Ke7 62. Kf4 Kf8 63. Kg5 Ke7 64. Kf5 Kf8 65. Ke5 Ke7 66. Kd5 Kf8
        67. Ke4 Ke7 68. Kf4 Kf8 69. Ke4 Ke7 70. Kd4 Kf8 71. Kc5 Ke7 72. Kb6 Kf8 73. Kb7
        Ke7 74. Kb8 Kf8 75. Kc7 Ke7 76. Kc6 Kf8 77. Kc7 Ke7 78. Kc8 Kf8 79. Kb7 Ke7 80.
        Kb6 Kf8 81. Kb5 Ke7 82. Kb4 Kf8 83. Kc3 Ke7 84. Kc2 Kf8 85. Kd1 Ke7 86. Ke2 Kf8
        87. Kf2 Ke7 88. Kg2 Kf8 89. Kh3 Ke7 90. Kh4 Kf8 91. Kg4 Ke7 92. Kg5 Kf8 93. Kf4
        Ke7 94. Kf3 Kf8 95. Ke2 Ke7 96. Kd1 Kf8 97. Kc1 Ke7 98. Kb1 Kf8 99. Ka2 Ke7
        100. Ka1 Kf8 101. Kb1 Ke7 102. Kc1 Kf8 103. Kc2 Ke7 104. Kb3 Kf8 105. Ka4 Ke7
        106. f8=Q+ Kxf8 107. Kb5 Ke7 108. Kb6 Kf8 109. Kc6 Ke7 110. Kd5 Kf8 111. Ke5
        Ke7 112. Kf5 Kf8 113. Kf6 Kg8 114. e7 1-0

                """;
    check(pgn, "01_utf8.pgn");
  }

  @SuppressWarnings("static-method")
  @Test
  void test2() {
    // intentionally not formatting nicely
    // to additionally check comparing logical content
    final String pgn = """
        [Site "ñ, á, é, í, ó, ú, ¡, ¿ Chess.com 你好"]
        [Event "é, è, ê, ë, à, â, ç Live Chess ä, ö, ü, ß"]
        [White "à, è, é, ì, ò, ù almtwali नमस्ते"]
        [Black "ą, ć, ę, ł, ń, ó, ś, ź, ż danielbaechli Привет"]
        [SpecialForTesting1 "á, é, ő, ű"]
        [SpecialForTesting2 "ă, ș, ț, î, â"]
        [Date "2020.07.11"]
        [Round "?"]
        [Result "1-0"]

        1. d4 e6 2. c4 f5 3. Nf3
         Nf6 4. e3 Be7 5. Ne5 O-O 6. h4 d6 7. h5 dxe5 8. h6 g6
        9. d5 exd5 10. Qb3 c6 11. Nd2 Qd7 12. Nf3 e4 13. Ne5 Qe6 14. Nxg6 Re8 15. Nf4
        Qd6 16. a3 Be6 17. Bd2 Na6 18. O-O-O Rab8 19. Kb1 Kh8 20. Qc3 Rg8 21. Nh5 Rbf8
        22. Ng7 Nc7 23. Qb3 b6 24. Bb4 c5 25. Bc3 Qd8 26. Nxe6 Nxe6 27. cxd5 Ng5 28. d6
        Bxd6 29. Be5 Nf7 30. Qd5 Nxe5 31. Qxd6 Qxd6 32. Rxd6 Nc4 33. Bxc4 Rxg2 34. Rhd1
        Rgg8 35. Bxg8 Nxg8 36. Rd8 Rxd8 37. Rxd8 a5 38. Kc2 b5 39.
        b4 cxb4 40. axb4
        axb4 41. Kb3 f4 42. exf4 e3 43. Rxg8+ Kxg8 44. fxe3 Kf7 45. e4 Kf6 46. Kxb4 Ke6
        47. Kxb5 Kd6 48. Kc4 Kc6 49. Kd4 Kd6 50. e5+ Ke6 51. Ke4 Kf7 52. f5 Ke7 53. Kf4
        Kd7 54. Kg5 Ke7
         55. f6+ Kf7 56. Kf5 Kf8 57. Ke6 Ke8 58. f7+ Kf8 59. Kf5 Ke7 60.
        e6 Kf8 61. Ke5 Ke7 62. Kf4 Kf8 63. Kg5 Ke7 64. Kf5 Kf8 65. Ke5 Ke7 66. Kd5 Kf8
        67. Ke4 Ke7 68. Kf4 Kf8 69. Ke4 Ke7 70. Kd4 Kf8 71. Kc5 Ke7 72. Kb6 Kf8 73. Kb7
        Ke7 74. Kb8 Kf8 75. Kc7 Ke7 76. Kc6 Kf8 77. Kc7 Ke7 78. Kc8 Kf8 79. Kb7 Ke7 80.
        Kb6 Kf8 81. Kb5 Ke7 82. Kb4 Kf8 83. Kc3 Ke7 84. Kc2 Kf8 85. Kd1 Ke7 86. Ke2 Kf8
        87. Kf2 Ke7 88. Kg2 Kf8 89. Kh3 Ke7 90. Kh4 Kf8 91. Kg4 Ke7 92. Kg5 Kf8 93. Kf4
        Ke7 94. Kf3 Kf8 95. Ke2 Ke7 96. Kd1 Kf8 97. Kc1 Ke7 98. Kb1 Kf8 99. Ka2 Ke7
        100. Ka1 Kf8 101. Kb1 Ke7 102. Kc1 Kf8 103. Kc2 Ke7 104. Kb3 Kf8 105. Ka4 Ke7
        106. f8=Q+ Kxf8 107. Kb5 Ke7 108. Kb6 Kf8 109. Kc6 Ke7 110. Kd5 Kf8 111. Ke5
        Ke7 112. Kf5 Kf8 113. Kf6 Kg8
        114. e7 1-0


                        """;
    check(pgn, "02_utf8.pgn");
  }

  @SuppressWarnings("static-method")
  @Test
  void test3() {
    // intentionally not formatting nicely
    // to additionally check comparing logical content
    final String pgn = """
               [Event "é, è, ê, ë, à, â, ç ≤, ≥Live Chess ä, ö, ü, ß"]
        [Site "ñ, á, é, í, ó, ú, ¡, ¿ Chess.com 你好"]
        [White "à, è, é, ì, ò, ù almtwali नमस्ते ∞∞∞"]
        [Black "ą, ć, ę, ł, ń, ó, ś, ź, ż danielbaechli Привет"]
        [Date "2020.07.îî ăă:áá"]
        [Result "1-0"]
        [SpecialForTesting2 "ă, ș, ț, î, â"]
        [SpecialForTesting1 "á, é, ő, ű α, β, γ"]
        [SamplingUtf8 "∞, ×, ÷, ≈, ≠, ≤, ≥, £, ¥, €, ₹, →, ←, ↑, ↓, ⇔, │, ─, ┼, ┌, ┘, α, β, γ, δ, Ω, ✓, ✗, ♥, ★, ☺, ·, ¶, §, °, •"]
        [Round "ñ"]

        1. d4 e6 2. c4 f5 3. Nf3 Nf6 4. e3 Be7 5. Ne5
         O-O 6. h4 d6 7. h5 dxe5 8. h6 g6
        9. d5 exd5 10. Qb3 c6 11. Nd2 Qd7 12. Nf3 e4 13. Ne5 Qe6 14. Nxg6 Re8 15. Nf4
        Qd6 16. a3 Be6 17. Bd2 Na6 18. O-O-O Rab8 19. Kb1 Kh8 20. Qc3 Rg8 21. Nh5 Rbf8
        22. Ng7 Nc7 23. Qb3 b6 24. Bb4 c5 25. Bc3 Qd8 26. Nxe6 Nxe6 27. cxd5 Ng5 28. d6
        Bxd6 29.
         Be5 Nf7 30. Qd5 Nxe5 31. Qxd6 Qxd6 32. Rxd6 Nc4 33. Bxc4 Rxg2 34. Rhd1
        Rgg8 35. Bxg8
         Nxg8 36. Rd8 Rxd8 37. Rxd8 a5 38. Kc2 b5 39. b4 cxb4 40. axb4
        axb4 41. Kb3 f4 42. exf4 e3 43. Rxg8+ Kxg8 44. fxe3 Kf7 45. e4 Kf6 46. Kxb4 Ke6
        47. Kxb5 Kd6 48. Kc4 Kc6 49. Kd4 Kd6 50. e5+ Ke6 51. Ke4 Kf7 52. f5 Ke7 53. Kf4
        Kd7 54. Kg5 Ke7 55. f6+ Kf7 56. Kf5 Kf8 57. Ke6 Ke8 58. f7+ Kf8 59. Kf5 Ke7 60. e6 Kf8 61. Ke5 Ke7 62. Kf4 Kf8 63. Kg5 Ke7 64. Kf5 Kf8 65. Ke5 Ke7 66. Kd5 Kf8
        67. Ke4 Ke7 68. Kf4 Kf8 69. Ke4 Ke7 70. Kd4 Kf8 71. Kc5 Ke7 72. Kb6 Kf8 73. Kb7
        Ke7 74. Kb8 Kf8 75. Kc7 Ke7 76. Kc6 Kf8 77. Kc7 Ke7 78. Kc8 Kf8 79. Kb7 Ke7 80.
        Kb6 Kf8 81. Kb5 Ke7 82. Kb4 Kf8 83. Kc3 Ke7 84. Kc2 Kf8 85. Kd1 Ke7 86. Ke2 Kf8
        87. Kf2 Ke7 88. Kg2 Kf8 89. Kh3 Ke7 90. Kh4 Kf8 91. Kg4 Ke7 92. Kg5 Kf8 93. Kf4
        Ke7 94. Kf3 Kf8
        95. Ke2 Ke7 96. Kd1 Kf8 97. Kc1 Ke7 98. Kb1 Kf8 99. Ka2 Ke7
        100. Ka1 Kf8 101. Kb1 Ke7 102. Kc1 Kf8 103. Kc2 Ke7 104. Kb3 Kf8 105. Ka4 Ke7
        106. f8=Q+ Kxf8 107. Kb5 Ke7 108. Kb6 Kf8 109. Kc6 Ke7 110. Kd5 Kf8 111. Ke5
        Ke7 112. Kf5 Kf8 113. Kf6 Kg8
        114. e7 1-0



                        """;
    check(pgn, "03_utf8.pgn");
  }

  private static void check(String pgn, String fileName) {

    logger.info(pgn);

    final PgnGame pgnGameExpected = LenientPgnParser.parseText(pgn);
    final Path filePath = Nulls.pathResolve(PgnTestConstants.LENIENT_PGN_PARSER_UTF8_TEST_ROOT_FOLDER_PATH, fileName);
    final PgnGame pgnGameActual = LenientPgnParser.parse(filePath);

    // Compare under archival normalisation: the two inputs differ in tag order (test2/test3 use
    // intentionally-shuffled tag order to exercise lenient acceptance), which the parser preserves but archival
    // export reconciles to canonical order.
    assertEquals(PgnCreate.createPgnString(pgnGameExpected, WriteMode.ARCHIVAL),
        PgnCreate.createPgnString(pgnGameActual, WriteMode.ARCHIVAL));
  }

}
