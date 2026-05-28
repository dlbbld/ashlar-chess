package com.dlb.chess.test.pgn.export.linebreaks;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.constants.ConfigurationConstants;
import com.dlb.chess.pgn.LenientPgnParser;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.pgn.PgnWriter;
import com.dlb.chess.pgn.WriteMode;
import com.dlb.chess.test.FileComparison;
import com.dlb.chess.test.pgntest.constants.PgnTestConstants;

public class TestPgnExportLineBreaks {

  private static final Logger logger = Nulls.getLogger(TestPgnExportLineBreaks.class);

  private static final Path TEST_SOURCE_FILE_PATH = Nulls
      .pathResolve(PgnTestConstants.PGN_EXPORT_LINE_BREAKS_TEST_ROOT_FOLDER_PATH, "01_linux.pgn");

  private static final Path TEST_DESTINATION_FILE_PATH = Nulls.pathResolve(ConfigurationConstants.TEMP_FOLDER_PATH,
      "test_write_line_breaks.pgn");

  @SuppressWarnings("static-method")
  @Test
  void test() {
    final String pgn = """
                [Event "World Blitz Championship"]
                [Site "St Petersburg RUS"]
                [Date "2018.12.29"]
                [Round "1"]
                [White "Dmitry Anikonov"]
                [Black "Sergei Zhigalko"]
                [Result "1/2-1/2"]
                [ECO "A62"]
                [WhiteElo "2511"]
                [BlackElo "2693"]
                [EventDate "2018.??.??"]
                [EventType "blitz"]

                1. d4 Nf6 2. c4 e6 3. g3 c5 4. d5 exd5 5. cxd5 d6 6. Nc3 g6 7. Bg2 Bg7 8. Nf3
                O-O 9. O-O Re8 10. Bf4 a6 11. a4 Nh5 12. Bg5 Qc7 13. Re1 h6 14. Bd2 Nd7 15. e4
                Rb8 16. Bf1 Nhf6 17. Rc1 b6 18. h3 Bb7 19. Kh2 Re7 20. Qc2 Rbe8 21. Bg2 Qb8 22.
                Bf4 Nh5 23. Be3 b5 24. axb5 axb5 25. Nxb5 Rxe4 26. Nc3 Rb4 27. Qd2 Kh7 28. g4
                Nhf6 29. Bf4 Ba8 30. Rxe8 Nxe8 31. Rc2 Nb6 32. Bg3 Nc4 33. Qe2 Nxb2 34. g5 h5
                35. Nh4 Na4 36. Ne4 Qd8 37. Rd2 Nc3 38. Nxc3 Bxc3 39. Rd3 Be5 40. Rf3 Rxh4 41.
                Bxe5 dxe5 42. Qxe5 Qd6 43. Rxf7+ Kg8 44. Qxd6 Nxd6 45. Rd7 Nf7 46. Ra7 Nxg5 47.
                Rxa8+ Kg7 48. Ra7+ Kf6 49. d6 Rd4 50. d7 Ne6 51. Bc6 Nd8 52. Bb5 Ke7 53. Ra6
                Rd6 54. Ra4 Rd4 55. Ra6 Rd6 56. Ra3 Rb6 57. Ba4 Rb4 58. Re3+ Kd6 59. Bc2 Kxd7
                60. Bxg6 h4 61. Bf5+ Kc7 62. Re7+ Kc6 63. Bg4 Rd4 64. Bf3+ Kd6 65. Rh7 c4 66.
                Rh6+ Kc5 67. Rh5+ Kb4 68. Bg4 c3 69. Bf5 Nc6 70. Rh6 Ne5 71. Kg2 Kb3 72. Rb6+
                Rb4 73. Re6 Rb5 74. f4 Nc4 75. Be4 c2 76. Bxc2+ Kxc2 77. Kf3 Kd3 78. Kg4 Ne3+
                79. Kxh4 Rf5 80. Kg3 Rf8 81. Re5 Kd4 82. Re6 Nd5 83. Ra6 Nxf4 84. Ra4+ Ke5 85.
                Ra5+ Kd6 86. Ra6+ Kc5 87. Ra5+ Kb6 88. Ra4 Nd5 89. h4 Kc6 90. Ra6+ Kd7 91. h5
                Ke7 92. Kg4 Kf7 93. Kg5 Rg8+ 94. Kf5 Ne7+ 95. Kf4 Rg1 96. h6 Rh1 97. Kg5 Rh2
                98. Rf6+ Kg8 99. Ra6 Rg2+ 100. Kf4 Ng6+ 101. Kf5 Nh4+ 102. Kf4 Kh7 103. Ra7+
                Kxh6 104. Ra8 Ng6+ 105. Kf5 Rf2+ 106. Ke4 Kg5 107. Rg8 Kf6 108. Ra8 Rf4+ 109.
                Ke3 Rb4 110. Ra5 Ne5 111. Ra8 Nd7 112. Ra6+ Ke5 113. Ra5+ Kd6 114. Ra8 Nc5 115.
                Rh8 Re4+ 116. Kf3 Re7 117. Rh5 Nd3 118. Ra5 Ne5+ 119. Kf4 Nc6 120. Ra8 Kd5 121.
                Ra1 Re4+ 122. Kf3 Ne5+ 123. Kf2 Rb4 124. Rd1+ Ke4 125. Rd8 Rb2+ 126. Kg3 Rc2
                127. Ra8 Rc3+ 128. Kg2 Ng6 129. Ra4+ Kf5 130. Ra8 Kf4 131. Ra4+ Kg5 132. Ra8
                Kg4 133. Kf2 Rd3 134. Rb8 Ra3 135. Rc8 Ne7 136. Rb8 Nd5 137. Ke2 Kf4 138. Rf8+
                Ke4 139. Re8+ Kd4 140. Kf2 Nf6 141. Rb8 Ke5 142. Ke2 Kf4 143. Rf8 Ke5 144. Rb8
                Kd4 145. Rb4+ Kd5 146. Rb8 Rh3 147. Ra8 Kd4 148. Ra4+ Ke5 149. Ra8 Nd5 150.
                Re8+ Kd4 151. Ra8 Rh2+ 152. Kf3 Rh7 153. Ra4+ Ke5 154. Ra8 Rf7+ 155. Ke2 Kd4
                156. Ra4+ Kc5 157. Ra8 Kc4 158. Ra4+ Kb3 159. Ra8 Re7+ 160. Kf3 Rf7+ 161. Ke4
                Nc3+ 162. Ke3 Kc4 163. Rc8+ Kb4 164. Ra8 Re7+ 165. Kf3 Nd5 166. Rc8 Kb5 1/2-1/2

        """;

    logger.info(TEST_SOURCE_FILE_PATH.getFileName());

    final PgnGame pgnGame = LenientPgnParser.parseText(pgn);
    // Archival mode: the inline source uses a different tag order than the on-disk reference fixture (canonical
    // sort order, ECO after BlackElo/EventDate). The lenient parser preserves input order; archival emission
    // sorts into canonical order, matching the reference.
    PgnWriter.writePgn(pgnGame, TEST_DESTINATION_FILE_PATH, WriteMode.ARCHIVAL);

    assertTrue(FileComparison.checkWithLineEndingsConversion(TEST_SOURCE_FILE_PATH, TEST_DESTINATION_FILE_PATH));
  }

}
