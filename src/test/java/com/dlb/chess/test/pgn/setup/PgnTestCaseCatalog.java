package com.dlb.chess.test.pgn.setup;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.annotation.NonNull;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.enums.InsufficientMaterial;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.report.CheckmateOrStalemate;
import com.dlb.chess.test.RestrictTestConstants;
import com.dlb.chess.test.common.utility.FileUtility;
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgntest.enums.PgnTest;

public class PgnTestCaseCatalog {

  private static PgnTestCaseList calculateTestCaseList(PgnTest pgnTest) {

    return switch (pgnTest) {
      case BASIC_CASTLING_WHITE -> createTestCasesBasicCastlingWhite();
      case BASIC_CASTLING_BLACK -> createTestCasesBasicCastlingBlack();
      case BASIC_CASTLING_SPECIAL_WHITE -> createTestCasesBasicCastlingSpecialWhite();
      case BASIC_CASTLING_SPECIAL_BLACK -> createTestCasesBasicCastlingSpecialBlack();
      case BASIC_MOVING_PIECE_WHITE -> createTestCasesBasicMovingPieceWhite();
      case BASIC_MOVING_PIECE_BLACK -> createTestCasesBasicMovingPieceBlack();
      case BASIC_CAPTURE_WHITE -> createTestCasesBasicCaptureWhite();
      case BASIC_CAPTURE_BLACK -> createTestCasesBasicCaptureBlack();
      case BASIC_CAPTURE_LAST_MOVE -> createTestBasicCaptureLastMove();
      case BASIC_EN_PASSANT_CAPTURE_WHITE -> createTestCasesBasicEnPassantCaptureWhite();
      case BASIC_EN_PASSANT_CAPTURE_BLACK -> createTestCasesBasicEnPassantCaptureBlack();
      case BASIC_PROMOTION_PIECE_WHITE -> createTestCasesBasicPromotionPieceWhite();
      case BASIC_PROMOTION_PIECE_BLACK -> createTestCasesBasicPromotionPieceBlack();
      case BASIC_PROMOTION_SQUARE_WHITE -> createTestCasesBasicPromotionSquareWhite();
      case BASIC_PROMOTION_SQUARE_BLACK -> createTestCasesBasicPromotionSquareBlack();
      case BASIC_CHECK_WHITE -> createTestCasesBasicCheckWhite();
      case BASIC_CHECK_BLACK -> createTestCasesBasicCheckBlack();
      case BASIC_DOUBLE_CHECK_WHITE -> createTestCasesBasicDoubleCheckWhite();
      case BASIC_DOUBLE_CHECK_BLACK -> createTestCasesBasicDoubleCheckBlack();
      case BASIC_CHECKMATE_WHITE -> createTestCasesBasicCheckmateWhite();
      case BASIC_CHECKMATE_BLACK -> createTestCasesBasicCheckmateBlack();
      case BASIC_CHECKMATE_VARIOUS_WHITE -> createTestCasesBasicCheckmateVariousWhite();
      case BASIC_CHECKMATE_VARIOUS_BLACK -> createTestCasesBasicCheckmateVariousBlack();
      case BASIC_CHECKMATE_DOUBLE_CHECK_WHITE -> createTestCasesBasicCheckmateDoubleCheckWhite();
      case BASIC_CHECKMATE_DOUBLE_CHECK_BLACK -> createTestCasesBasicCheckmateDoubleCheckBlack();
      case BASIC_DOUBLE_DRAW -> createTestCasesBasicDoubleDraw();
      case BASIC_FIFTY -> createTestCasesBasicFifty();
      case BASIC_FIVEFOLD -> createTestCasesBasicFivefold();
      case BASIC_FORCED -> createTestCasesBasicForced();
      case BASIC_REPORT_NO_PROGRESS_SEQUENCES_WHITE -> createTestCasesBasicNoProgressSequencesWhite();
      case BASIC_REPORT_NO_PROGRESS_SEQUENCES_BLACK -> createTestCasesBasicNoProgressSequencesBlack();
      case BASIC_REPORT_MAX_NO_PROGRESS -> createTestCasesBasicMaxNoProgress();

      case PARSER_FROM_FEN -> createTestCasesParserFromFen();
      case BASIC_INSUFFICIENT_MATERIAL_BOTH -> createTestCasesBasicInsufficientMaterial(InsufficientMaterial.BOTH,
          PgnTest.BASIC_INSUFFICIENT_MATERIAL_BOTH);
      case BASIC_INSUFFICIENT_MATERIAL_ONLY_WHITE -> createTestCasesBasicInsufficientMaterial(
          InsufficientMaterial.WHITE_ONLY, PgnTest.BASIC_INSUFFICIENT_MATERIAL_ONLY_WHITE);
      case BASIC_INSUFFICIENT_MATERIAL_ONLY_BLACK -> createTestCasesBasicInsufficientMaterial(
          InsufficientMaterial.BLACK_ONLY, PgnTest.BASIC_INSUFFICIENT_MATERIAL_ONLY_BLACK);
      case BASIC_INSUFFICIENT_MATERIAL_NONE -> createTestCasesBasicInsufficientMaterial(InsufficientMaterial.NONE,
          PgnTest.BASIC_INSUFFICIENT_MATERIAL_NONE);
      case BASIC_INTERVENING -> createTestCasesBasicIntervening();
      case BASIC_SEVENTY_FIVE -> createTestCasesBasicSeventyFive();
      case BASIC_STALEMATE -> createTestCasesBasicStalemate();
      case BASIC_THREEFOLD -> createTestCasesBasicThreefold();
      case EARLY_DRAW -> createTestCasesEarlyDraw();
      case FIFTY_GENERAL -> createTestCasesFiftyGeneral();
      case FIFTY_PATTERN -> createTestCasesFiftyPattern();
      case FIVEFOLD_CORRECT -> createTestCasesFivefoldCorrect();
      case VARIOUS -> createTestCasesVarious();
      case WCC2021 -> createTestCasesWcc201();
      case LAST_MOVE_ADDED_ACCIDENTALLY -> createTestCasesLastMoveAddedAccidentally();
      case CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR -> createTestCasesChaLichessQuickNotDepthThree();
      case CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR_HELPMATE -> createTestCasesChaLichessQuickNotDepthThreeHelpmate();
      case CHA_LICHESS_QUICK_DEPTH_THREE -> createTestCasesChaLichessQuickDepthThree();
      case CHA_LICHESS_QUICK_DEPTH_FOUR -> createTestCasesChaLichessQuickDepthFour();
      case CHA_AMBRONA -> createTestCasesChaAmbrona();
      case MAX_MOVES -> createTestCasesLongestPossible();
      case MAX_SAME_PIECE_PROMOTION_WHITE -> createTestCasesMaxSamePiecePromotionWhite();
      case MAX_SAME_PIECE_PROMOTION_BLACK -> createTestCasesMaxSamePiecePromotionBlack();
      case MAX_SAME_PIECE_PROMOTION_COMBINED -> createTestCasesMaxSamePiecePromotionByCombined();
      case CHA_PAWN_WALL_YES -> createTestCasesPawnWallYes();
      case CHA_PAWN_WALL_NO -> createTestCasesPawnWallNo();
      case CHA_SHALLOW_TERMINATION -> createTestCasesShallowTermination();
      case CHA_HELPMATE_BEYOND_FIVEFOLD -> createTestCasesHelpmateBeyondFivefold();
      case CHA_HELPMATE_BEYOND_SEVENTY_FIVE -> createTestCasesHelpmateBeyondSeventyFive();
      case CHA_BASIC_MATE_DRAW -> createTestCasesBasicMateDraw();
      case CHA_BASIC_MATE_HELPMATE_04 -> createTestCasesBasicMateHelpmate04();
      case CHA_BASIC_MATE_HELPMATE_10 -> createTestCasesBasicMateHelpmate10();
      case CHA_BASIC_MATE_HELPMATE_AROUND_MAX -> createTestCasesBasicMateHelpmateAroundMax();
      case RANDOM_CHECKMATE -> createTestCasesRandomCheckmate();
      case RANDOM_FIFTY -> createTestCasesRandomFifty();
      case RANDOM_FIVEFOLD -> createTestCasesRandomFivefold();
      case RANDOM_INSUFFICIENT_MATERIAL -> createTestCasesRandomInsufficientMaterial();
      case RANDOM_NO_REPETITION -> createTestCasesRandomNoRepetition();
      case RANDOM_SEVENTY_FIVE -> createTestCasesRandomSeventyFive();
      case RANDOM_STALEMATE -> createTestCasesRandomStalemate();
      case RANDOM_THREEFOLD -> createTestCasesRandomThreefold();
      case BASIC_REPORT_REPETITION -> createTestCasesSequence();
      case SEVENTY_FIVE_CORRECT -> createTestCasesSeventyFiveCorrect();
      case SPECIAL -> createTestCasesSpecial();
      case WIKIPEDIA_FIFTY_MOVE -> createTestCasesWikipediaFiftyMove();
      case WIKIPEDIA_THREEFOLD -> createTestCasesWikipediaThreefold();
      case DOUBLE_CHECK_CHECKMATE_BIZARRE_CHECKMATE_WHITE -> createTestCasesDoubleCheckCheckmateBizarreWhite();
      case DOUBLE_CHECK_CHECKMATE_BIZARRE_CHECKMATE_BLACK -> createTestCasesDoubleCheckCheckmateBizarreBlack();
      case MONSTER_BLOG_INSTANT -> createTestCasesBlogInstant();
      case MONSTER_BLOG_PREDRAW -> createTestCasesBlogPredraw();
      case MONSTER_BLOG_TIMEOUT -> createTestCasesBlogTimeout();
      case REPETITION_QUIZ_ONE -> createTestCasesRepetitionQuizOne();
      case REPETITION_QUIZ_TWO -> createTestCasesRepetitionQuizTwo();
      default -> throw new IllegalArgumentException();
    };
  }

  private static final EnumMap<PgnTest, PgnTestCaseList> allTestCaseListMap = Nulls.newEnumMap(PgnTest.class);

  private static final List<PgnTestCaseList> allTestCaseListList = new ArrayList<>();

  private static final List<PgnTestCaseList> restricedTestCaseListList = new ArrayList<>();

  static {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = calculateTestCaseList(pgnTest);
      allTestCaseListMap.put(pgnTest, testCaseList);
      allTestCaseListList.add(testCaseList);

      if (pgnTest == PgnTest.MAX_MOVES) {
        switch (RestrictTestConstants.PGN_TEST_INCLUSION) {
          case ALL:
          case ONLY_LONGEST_POSSIBLE:
            restricedTestCaseListList.add(testCaseList);
            break;
          case ALL_EXCEPT_LONGEST_POSSIBLE:
            break;
          default:
            throw new IllegalArgumentException();
        }
      } else {
        switch (RestrictTestConstants.PGN_TEST_INCLUSION) {
          case ALL:
          case ALL_EXCEPT_LONGEST_POSSIBLE:
            restricedTestCaseListList.add(testCaseList);
            break;
          case ONLY_LONGEST_POSSIBLE:
            break;
          default:
            throw new IllegalArgumentException();
        }
      }
    }

    checkUniqueFileNames(allTestCaseListList);
  }

  public static PgnTestCaseList getTestList(PgnTest pgnTest) {
    if (!allTestCaseListMap.containsKey(pgnTest)) {
      throw new ProgrammingMistakeException("The test list was not constructed correctly");
    }

    @SuppressWarnings("null") final PgnTestCaseList testCaseList = allTestCaseListMap.get(pgnTest);
    return testCaseList;
  }

  public static List<PgnTestCaseList> getTestList(@NonNull PgnTest... pgnTestArray) {
    final List<PgnTestCaseList> resultListList = new ArrayList<>();
    for (final PgnTest pgnTest : pgnTestArray) {
      if (!allTestCaseListMap.containsKey(pgnTest)) {
        throw new ProgrammingMistakeException("The test list was not constructed correctly");
      }
      resultListList.add(Nulls.get(allTestCaseListMap, pgnTest));
    }
    return resultListList;
  }

  public static PgnTest findPgnTest(String testPgnName) {
    for (final PgnTestCaseList testCaseList : allTestCaseListList) {
      for (final PgnTestCase testCase : testCaseList.list()) {
        if (testCase.pgnName().equals(testPgnName)) {
          return testCaseList.pgnTest();
        }
      }
    }
    throw new IllegalArgumentException("No such file exists");
  }

  public static PgnTest findPgnTestPgnNotListed(String testPgnName) {
    for (final PgnTestCaseList testCaseList : allTestCaseListList) {
      if (FileUtility.exists(testCaseList.pgnTest().getFolderPath(), testPgnName)) {
        return testCaseList.pgnTest();
      }
    }
    throw new IllegalArgumentException("No such file exists");
  }

  public static PgnTestCase findTestCase(String testPgnName) {
    for (final PgnTestCaseList testCaseList : allTestCaseListList) {
      for (final PgnTestCase testCase : testCaseList.list()) {
        if (testCase.pgnName().equals(testPgnName)) {
          return testCase;
        }
      }
    }
    throw new IllegalArgumentException("The file " + testPgnName + " does not exist");
  }

  public static List<PgnTestCaseList> getRestrictedTestListList() {
    return restricedTestCaseListList;
  }

  // -------------------------------------------------------------------------------------------------
  // Smoke subsets for broad integration tests
  //
  // The strict-vs-lenient parser comparison and the export/import roundtrip tests are sweeps over the PGN corpus.
  // Using every basic fixture (~591 files across 23 buckets) for these is an order of magnitude more work than
  // needed for their purpose — detailed feature coverage already exists in the dedicated parser, SAN, and basic
  // tests. These two curated lists keep broad diversity (piece movement, capture, en passant, promotion, castling,
  // check, checkmate, stalemate, custom starting positions) without iterating hundreds of near-identical variants.
  //
  // The lists are built at class-load time; any missing file fails fast during init with a clear message, so a
  // future rename of a referenced fixture surfaces immediately rather than during a long test run.
  // -------------------------------------------------------------------------------------------------

  private static final List<PgnTestCaseList> parserIntegrationSmokeList = buildParserIntegrationSmokeList();
  private static final List<PgnTestCaseList> exportRoundtripSmokeList = buildExportRoundtripSmokeList();

  /**
   * Smoke subset used by {@code TestStrictPgnParserAgainstLenientPgnParser} — broad enough to exercise every major
   * parser code path (piece movement, capture, en passant, promotion, castling with check/checkmate, checkmate by
   * various pieces, stalemate, custom starting position via FEN, repetition-sensitive en passant setup). About 45 files
   * in total rather than ~591.
   */
  public static List<PgnTestCaseList> getParserIntegrationSmokeList() {
    return parserIntegrationSmokeList;
  }

  /**
   * Smoke subset used by {@code TestPgnExportIdempotency} and {@code TestPgnImportAgainstExport} — tighter than the
   * parser smoke set because export tests only vary over structural shape, not chess-rule details. About 20 files
   * covering ordinary SAN movement, promotion, castling, en passant, and custom starting position.
   */
  public static List<PgnTestCaseList> getExportRoundtripSmokeList() {
    return exportRoundtripSmokeList;
  }

  private static List<PgnTestCaseList> buildParserIntegrationSmokeList() {
    final List<PgnTestCaseList> result = new ArrayList<>();
    // Small buckets — keep whole.
    result.add(getTestList(PgnTest.BASIC_MOVING_PIECE_WHITE));
    result.add(getTestList(PgnTest.BASIC_PROMOTION_PIECE_WHITE));
    result.add(getTestList(PgnTest.BASIC_STALEMATE));
    // Curated samples from larger buckets.
    result.add(
        filterBucket(PgnTest.BASIC_CAPTURE_WHITE, "01_white_capture_rook_rook.pgn", "06_white_capture_knight_rook.pgn",
            "11_white_capture_bishop_rook.pgn", "16_white_capture_queen_rook.pgn", "21_white_capture_king_rook.pgn"));
    result.add(filterBucket(PgnTest.BASIC_EN_PASSANT_CAPTURE_WHITE, "01_white_en_passant_capture_right_a6.pgn",
        "07_white_en_passant_capture_right_g6.pgn", "08_white_en_passant_capture_left_b6.pgn",
        "14_white_en_passant_capture_left_h6.pgn"));
    result.add(filterBucket(PgnTest.BASIC_CASTLING_SPECIAL_WHITE, "01_white_castling_special_kingside_check.pgn",
        "02_white_castling_special_kingside_checkmate.pgn", "06_white_castling_special_queenside_check.pgn",
        "07_white_castling_special_queenside_checkmate.pgn"));
    result.add(filterBucket(PgnTest.BASIC_CHECKMATE_WHITE, "01_white_checkmate_rook_direct_adjacent.pgn",
        "04_white_checkmate_knight_direct.pgn", "07_white_checkmate_bishop_direct_adjacent.pgn",
        "10_white_checkmate_queen_direct_orthogonal_adjacent.pgn", "14_white_checkmate_king_discover_orthogonal.pgn"));
    // One black-side fixture to exercise side-to-move plumbing end-to-end.
    result.add(filterBucket(PgnTest.BASIC_CHECKMATE_BLACK, "01_black_checkmate_rook_direct_adjacent.pgn"));
    result.add(filterBucket(PgnTest.PARSER_FROM_FEN, "from_fen_no_move_half_move_clock_099_white_to_move.pgn",
        "from_fen_no_move_half_move_clock_099_black_to_move.pgn"));
    return result;
  }

  private static List<PgnTestCaseList> buildExportRoundtripSmokeList() {
    final List<PgnTestCaseList> result = new ArrayList<>();
    // Whole small buckets covering the main SAN shapes.
    result.add(getTestList(PgnTest.BASIC_MOVING_PIECE_WHITE));
    result.add(getTestList(PgnTest.BASIC_PROMOTION_PIECE_WHITE));
    // Two-file samples of the structurally interesting cases.
    result.add(filterBucket(PgnTest.BASIC_CASTLING_SPECIAL_WHITE, "01_white_castling_special_kingside_check.pgn",
        "06_white_castling_special_queenside_check.pgn"));
    result.add(filterBucket(PgnTest.BASIC_EN_PASSANT_CAPTURE_WHITE, "01_white_en_passant_capture_right_a6.pgn",
        "08_white_en_passant_capture_left_b6.pgn"));
    result.add(filterBucket(PgnTest.PARSER_FROM_FEN, "from_fen_no_move_half_move_clock_099_white_to_move.pgn"));
    return result;
  }

  /**
   * Builds a {@link PgnTestCaseList} for the given bucket containing only the entries whose file name is in
   * {@code wantedFileNames}. Fails fast with a precise message if any requested file is missing from the bucket —
   * catches fixture renames the first time the class is loaded rather than mid test-run.
   */
  private static PgnTestCaseList filterBucket(PgnTest pgnTest, String... wantedFileNames) {
    final Set<String> wanted = new TreeSet<>();
    for (var i = 0; i < wantedFileNames.length; i++) {
      wanted.add(Nulls.get(wantedFileNames, i));
    }
    final List<PgnTestCase> filtered = new ArrayList<>();
    final Set<String> found = new TreeSet<>();
    for (final PgnTestCase tc : getTestList(pgnTest).list()) {
      if (wanted.contains(tc.pgnName())) {
        filtered.add(tc);
        found.add(tc.pgnName());
      }
    }
    if (found.size() != wanted.size()) {
      final Set<String> missing = new TreeSet<>(wanted);
      missing.removeAll(found);
      throw new ProgrammingMistakeException(
          "Curated smoke-subset file(s) missing from bucket " + pgnTest + ": " + missing);
    }
    return new PgnTestCaseList(pgnTest, filtered);
  }

  // We want to have unique file names for the test cases. For the convenience
  // that o can run a test case for testing by
  // only specifying it's name.
  private static void checkUniqueFileNames(List<PgnTestCaseList> testCaseListList) {
    final Set<String> fileNames = new TreeSet<>();
    for (final PgnTestCaseList testCaseList : testCaseListList) {
      for (final PgnTestCase testCase : testCaseList.list()) {
        final String pgnName = testCase.pgnName();
        final var isNotContained = !fileNames.add(pgnName);
        if (isNotContained) {
          throw new ProgrammingMistakeException("The PGN test cases files names are not unique");
        }
      }
    }
  }

  private static PgnTestCaseList createTestCasesBasicMovingPieceWhite() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_white_moving_piece_rook.pgn", "", "", -1, 1, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqkbnr/1ppppppp/8/p7/8/P7/RPPPPPPP/1NBQKBNR b Kkq - 1 2"));
    list.add(new PgnTestCase("02_white_moving_piece_knight.pgn", "", "", -1, 1, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqkbnr/pppppppp/8/8/8/2N5/PPPPPPPP/R1BQKBNR b KQkq - 1 1"));
    list.add(new PgnTestCase("03_white_moving_piece_bishop.pgn", "", "", -1, 1, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqkbnr/ppp1pppp/8/3p4/8/1P6/PBPPPPPP/RN1QKBNR b KQkq - 1 2"));
    list.add(new PgnTestCase("04_white_moving_piece_queen.pgn", "", "", -1, 1, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqkbnr/ppp1pppp/8/3p4/3P4/3Q4/PPP1PPPP/RNB1KBNR b KQkq - 1 2"));
    list.add(new PgnTestCase("05_white_moving_piece_king.pgn", "", "", -1, 1, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPPKPPP/RNBQ1BNR b kq - 1 2"));
    list.add(new PgnTestCase("06_white_moving_piece_pawn.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"));

    return new PgnTestCaseList(PgnTest.BASIC_MOVING_PIECE_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesBasicMovingPieceBlack() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_black_moving_piece_rook.pgn", "", "", -1, 1, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqkbn1/pppppppr/7p/8/3PP3/8/PPP2PPP/RNBQKBNR w KQq - 1 3"));
    list.add(new PgnTestCase("02_black_moving_piece_knight.pgn", "", "", -1, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqkb1r/pppppppp/5n2/8/8/2N5/PPPPPPPP/R1BQKBNR w KQkq - 2 2"));
    list.add(new PgnTestCase("03_black_moving_piece_bishop.pgn", "", "", -1, 1, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqk1nr/ppppppbp/6p1/8/3PP3/8/PPP2PPP/RNBQKBNR w KQkq - 1 3"));
    list.add(new PgnTestCase("04_black_moving_piece_queen.pgn", "", "", -1, 1, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnb1kbnr/pppp1ppp/8/4p3/3PP2q/8/PPP2PPP/RNBQKBNR w KQkq - 1 3"));
    list.add(new PgnTestCase("05_black_moving_piece_king.pgn", "", "", -1, 1, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbq1bnr/ppppkppp/8/4p3/3PP3/8/PPP2PPP/RNBQKBNR w KQ - 1 3"));
    list.add(new PgnTestCase("06_black_moving_piece_pawn.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 2"));

    return new PgnTestCaseList(PgnTest.BASIC_MOVING_PIECE_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesBasicCaptureWhite() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_white_capture_rook_rook.pgn", "", "", 7, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "1nbqkbnr/1ppppppp/1R6/p7/P7/8/1PPPPPPP/1NBQKBNR b Kk - 0 4"));
    list.add(new PgnTestCase("02_white_capture_rook_knight.pgn", "", "", 7, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r1bqkb1r/pppppppp/5n2/4R3/P7/8/1PPPPPPP/1NBQKBNR b Kkq - 0 4"));
    list.add(new PgnTestCase("03_white_capture_rook_bishop.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqk1nr/pppp1ppp/8/4p3/7P/R7/PPPPPPP1/RNBQKBN1 b Qkq - 0 3"));
    list.add(new PgnTestCase("04_white_capture_rook_queen.pgn", "", "", 5, 1, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnb1kbnr/pppp1ppp/8/4p2P/7R/8/PPPPPPP1/RNBQKBN1 b Qkq - 0 3"));
    list.add(new PgnTestCase("05_white_capture_rook_pawn.pgn", "", "", 7, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r1bqkbnr/2pppppp/2n5/pR6/P7/8/1PPPPPPP/1NBQKBNR b Kkq - 0 4"));
    list.add(new PgnTestCase("06_white_capture_knight_rook.pgn", "", "", 7, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "N1bqkbnr/p1pppppp/8/1p6/3n4/8/PPPPPPPP/R1BQKBNR b KQk - 0 4"));
    list.add(new PgnTestCase("07_white_capture_knight_knight.pgn", "", "", 5, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r1bqkb1r/pppppppp/2n2N2/8/8/8/PPPPPPPP/R1BQKBNR b KQkq - 0 3"));
    list.add(new PgnTestCase("08_white_capture_knight_bishop.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqk1nr/pppp1ppp/3Np3/8/8/8/PPPPPPPP/R1BQKBNR b KQkq - 0 3"));
    list.add(new PgnTestCase("09_white_capture_knight_queen.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnb1kbnr/pppp1ppp/4pN2/8/8/8/PPPPPPPP/R1BQKBNR b KQkq - 0 3"));
    list.add(new PgnTestCase("10_white_capture_knight_pawn.pgn", "", "", 3, 1, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqkbnr/ppp1pppp/8/3N4/8/8/PPPPPPPP/R1BQKBNR b KQkq - 0 2"));
    list.add(new PgnTestCase("11_white_capture_bishop_rook.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r1bqkbnB/pppppp1p/2n3p1/8/8/1P6/P1PPPPPP/RN1QKBNR b KQq - 0 3"));
    list.add(new PgnTestCase("12_white_capture_bishop_knight.pgn", "", "", 5, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r1bqkb1r/pppppppp/2n2B2/8/3P4/8/PPP1PPPP/RN1QKBNR b KQkq - 0 3"));
    list.add(new PgnTestCase("13_white_capture_bishop_bishop.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqk1nr/ppppppBp/6p1/8/8/1P6/P1PPPPPP/RN1QKBNR b KQkq - 0 3"));
    list.add(new PgnTestCase("14_white_capture_bishop_queen.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnb1kbnr/pppp1ppp/4pB2/8/8/1P6/P1PPPPPP/RN1QKBNR b KQkq - 0 3"));
    list.add(new PgnTestCase("15_white_capture_bishop_pawn.pgn", "", "", 5, 1, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqkbnr/pp2Bppp/3p4/2p5/8/3P4/PPP1PPPP/RN1QKBNR b KQkq - 0 3"));
    list.add(new PgnTestCase("16_white_capture_queen_rook.pgn", "", "", 5, 1, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "Qnbqkbnr/2pppppp/1p6/p7/8/4P3/PPPP1PPP/RNB1KBNR b KQk - 0 3"));
    list.add(new PgnTestCase("17_white_capture_queen_knight.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r1bqkbnr/1ppppppp/2Q5/p7/8/2P5/PP1PPPPP/RNB1KBNR b KQkq - 0 3"));
    list.add(new PgnTestCase("18_white_capture_queen_bishop.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rn1qkbnr/ppp1pppp/3p4/8/6Q1/4P3/PPPP1PPP/RNB1KBNR b KQkq - 0 3"));
    list.add(new PgnTestCase("19_white_capture_queen_queen.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnb1kbnr/pppp1ppp/4p3/8/7Q/4P3/PPPP1PPP/RNB1KBNR b KQkq - 0 3"));
    list.add(new PgnTestCase("20_white_capture_queen_pawn.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r1bqkbnr/pppp1ppQ/2n5/4p3/8/4P3/PPPP1PPP/RNB1KBNR b KQkq - 0 3"));
    list.add(new PgnTestCase("21_white_capture_king_rook.pgn", "", "", 9, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "1nbqkbnr/1ppppppp/8/p7/4P3/3K1N2/PPPP1PPP/RNBQ1B1R b k - 0 5"));
    list.add(new PgnTestCase("22_white_capture_king_knight.pgn", "", "", 7, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r1bqkb1r/pppppppp/5n2/8/3KP3/8/PPPP1PPP/RNBQ1BNR b kq - 0 4"));
    list.add(new PgnTestCase("23_white_capture_king_bishop.pgn", "", "", 9, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqk2r/pppp1pp1/5n1p/4p3/1K2P3/8/PPPP1PPP/RNBQ1BNR b kq - 0 5"));
    list.add(new PgnTestCase("24_white_capture_king_queen.pgn", "", "", 7, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnb1kbnr/pppp1ppp/8/4p3/4P3/P3K3/1PPP1PPP/RNBQ1BNR b kq - 0 4"));
    list.add(new PgnTestCase("25_white_capture_king_pawn.pgn", "", "", 7, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r1bqkbnr/pppnpppp/8/8/3KP3/8/PPPP1PPP/RNBQ1BNR b kq - 0 4"));

    return new PgnTestCaseList(PgnTest.BASIC_CAPTURE_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesBasicCaptureBlack() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_black_capture_rook_rook.pgn", "", "", 8, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "1nbqkbnr/1ppppppp/8/p2r4/P7/8/1PPPPPPP/1NBQKBNR w Kk - 0 5"));
    list.add(new PgnTestCase("02_black_capture_rook_knight.pgn", "", "", 6, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "1nbqkbnr/rppppppp/8/p7/8/8/PPPPPPPP/R1BQKBNR w KQk - 0 4"));
    list.add(new PgnTestCase("03_black_capture_rook_bishop.pgn", "", "", 6, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "1nbqkbnr/1ppppppp/7r/p7/3P2P1/8/PPP1PP1P/RN1QKBNR w KQk - 0 4"));
    list.add(new PgnTestCase("04_black_capture_rook_queen.pgn", "", "", 6, 1, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqkbn1/ppppppp1/8/7r/4P2p/8/PPPP1PPP/RNB1KBNR w KQq - 0 4"));
    list.add(new PgnTestCase("05_black_capture_rook_pawn.pgn", "", "", 6, 1, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqkbn1/ppppppp1/3r4/7p/8/8/PPP1PPPP/RNBQKBNR w KQq - 0 4"));
    list.add(new PgnTestCase("06_black_capture_knight_rook.pgn", "", "", 8, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r1bqkbnr/pppppppp/8/8/2PPP3/8/PP1K1PPP/nNBQ1BNR w kq - 0 5"));
    list.add(new PgnTestCase("07_black_capture_knight_knight.pgn", "", "", 8, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r1bqkbnr/pppppppp/8/8/3PP3/2N5/PPP1KPPP/R1BQ1BnR w kq - 0 5"));
    list.add(new PgnTestCase("08_black_capture_knight_bishop.pgn", "", "", 8, 1, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqkb1r/pppppppp/8/8/PPPP4/8/4PPPP/RNBQKnNR w KQkq - 0 5"));
    list.add(new PgnTestCase("09_black_capture_knight_queen.pgn", "", "", 8, 1, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqkb1r/pppppppp/8/2P5/1P1P4/8/P3PPPP/RNBnKBNR w KQkq - 0 5"));
    list.add(new PgnTestCase("10_black_capture_knight_pawn.pgn", "", "", 4, 1, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r1bqkbnr/pppppppp/8/8/3nP3/8/PPP2PPP/RNBQKBNR w KQkq - 0 3"));
    list.add(new PgnTestCase("11_black_capture_bishop_rook.pgn", "", "", 6, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rn1qkbnr/p1pppppp/1p6/4N3/8/6P1/PPPPPP1P/RNBQKB1b w Qkq - 0 4"));
    list.add(new PgnTestCase("12_black_capture_bishop_knight.pgn", "", "", 6, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rn1qkbnr/ppp1pppp/3p4/6N1/8/2P5/PP1PPPPP/RbBQKB1R w KQkq - 0 4"));
    list.add(new PgnTestCase("13_black_capture_bishop_bishop.pgn", "", "", 6, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rn1qkbnr/p1pppppp/1p6/4N3/8/4P3/PPPP1PPP/RNBQKb1R w KQkq - 0 4"));
    list.add(new PgnTestCase("14_black_capture_bishop_queen.pgn", "", "", 6, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rn1qkbnr/ppp1pppp/8/3p4/4P3/2N4P/PPPP1PP1/R1BbKBNR w KQkq - 0 4"));
    list.add(new PgnTestCase("15_black_capture_bishop_pawn.pgn", "", "", 6, 1, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rn1qkbnr/ppp1pppp/8/3pP3/8/5P2/PPPP2bP/RNBQKBNR w KQkq - 0 4"));
    list.add(new PgnTestCase("16_black_capture_queen_rook.pgn", "", "", 6, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnb1kbnr/pppp1ppp/4p3/8/8/BP6/P1PPPPPP/qN1QKBNR w Kkq - 0 4"));
    list.add(new PgnTestCase("17_black_capture_queen_knight.pgn", "", "", 8, 1, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnb1kbnr/ppp1pppp/8/3p2P1/7P/2P5/PP1PPP2/RqBQKBNR w KQkq - 0 5"));
    list.add(new PgnTestCase("18_black_capture_queen_bishop.pgn", "", "", 4, 1, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnb1kbnr/pppp1ppp/8/4p1q1/3P4/8/PPP1PPPP/RN1QKBNR w KQkq - 0 3"));
    list.add(new PgnTestCase("19_black_capture_queen_queen.pgn", "", "", 6, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnb1kbnr/pppp1ppp/8/4p3/3P1q2/8/PPP1PPPP/RNB1KBNR w KQkq - 0 4"));
    list.add(new PgnTestCase("20_black_capture_queen_pawn.pgn", "", "", 6, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnb1kbnr/pppp1ppp/8/3Np3/4P3/8/PPPP1qPP/R1BQKBNR w KQkq - 0 4"));
    list.add(new PgnTestCase("21_black_capture_king_rook.pgn", "", "", 16, 10, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbq1bnr/ppp1pppp/7B/3p4/3P2N1/1P1Q4/P1P1PPPP/k3KBNR w K - 0 9"));
    list.add(new PgnTestCase("22_black_capture_king_knight.pgn", "", "", 22, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "2bq1bnr/rpppp1pp/p1n2p2/3B1R1P/4P3/5P2/PPPP2P1/RNBQK1k1 w Q - 0 12"));
    list.add(new PgnTestCase("23_black_capture_king_bishop.pgn", "", "", 16, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbq1bnr/ppp1pppp/8/3p4/3P4/P1P2NPQ/1P2PPBP/RNk1K2R w KQ - 0 9"));
    list.add(new PgnTestCase("24_black_capture_king_queen.pgn", "", "", 6, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r1bq1bnr/pppppkpp/2n2p2/8/8/2P5/PP1PPPPP/RNB1KBNR w KQ - 0 4"));
    list.add(new PgnTestCase("25_black_capture_king_pawn.pgn", "", "", 18, 10, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbq1b1r/ppp1pppp/1P3n2/3p4/3P4/5N2/k1PNPPPP/1RBQKB1R w K - 0 10"));

    return new PgnTestCaseList(PgnTest.BASIC_CAPTURE_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesBasicEnPassantCaptureWhite() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_white_en_passant_capture_right_a6.pgn", "", "", 2, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "4k3/8/P7/8/8/8/8/4K3 b - - 0 101"));
    list.add(new PgnTestCase("02_white_en_passant_capture_right_b6.pgn", "", "", 2, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/p7/1P6/8/8/8/8/4K3 b - - 0 101"));
    list.add(new PgnTestCase("03_white_en_passant_capture_right_c6.pgn", "", "", 2, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/pp6/2P5/8/8/8/8/4K3 b - - 0 101"));
    list.add(new PgnTestCase("04_white_en_passant_capture_right_d6.pgn", "", "", 2, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/ppp5/3P4/8/8/8/8/4K3 b - - 0 101"));
    list.add(new PgnTestCase("05_white_en_passant_capture_right_e6.pgn", "", "", 2, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/pppp4/4P3/8/8/8/8/4K3 b - - 0 101"));
    list.add(new PgnTestCase("06_white_en_passant_capture_right_f6.pgn", "", "", 2, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/ppppp3/5P2/8/8/8/8/4K3 b - - 0 101"));
    list.add(new PgnTestCase("07_white_en_passant_capture_right_g6.pgn", "", "", 2, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/pppppp2/6P1/8/8/8/8/4K3 b - - 0 101"));
    list.add(new PgnTestCase("08_white_en_passant_capture_left_b6.pgn", "", "", 2, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/2pppppp/1P6/8/8/8/8/4K3 b - - 0 101"));
    list.add(new PgnTestCase("09_white_en_passant_capture_left_c6.pgn", "", "", 2, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/1p1ppppp/2P5/8/8/8/8/4K3 b - - 0 101"));
    list.add(new PgnTestCase("10_white_en_passant_capture_left_d6.pgn", "", "", 2, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/1pp1pppp/3P4/8/8/8/8/4K3 b - - 0 101"));
    list.add(new PgnTestCase("11_white_en_passant_capture_left_e6.pgn", "", "", 2, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/1ppp1ppp/4P3/8/8/8/8/4K3 b - - 0 101"));
    list.add(new PgnTestCase("12_white_en_passant_capture_left_f6.pgn", "", "", 2, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/1pppp1pp/5P2/8/8/8/8/4K3 b - - 0 101"));
    list.add(new PgnTestCase("13_white_en_passant_capture_left_g6.pgn", "", "", 2, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/1ppppp1p/6P1/8/8/8/8/4K3 b - - 0 101"));
    list.add(new PgnTestCase("14_white_en_passant_capture_left_h6.pgn", "", "", 2, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/1pppppp1/7P/8/8/8/8/4K3 b - - 0 101"));

    return new PgnTestCaseList(PgnTest.BASIC_EN_PASSANT_CAPTURE_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesBasicEnPassantCaptureBlack() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_black_en_passant_capture_left_a3.pgn", "", "", 2, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "4k3/8/8/8/2pppppp/p7/8/4K3 w - - 0 101"));
    list.add(new PgnTestCase("02_black_en_passant_capture_left_b3.pgn", "", "", 2, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "4k3/8/8/8/3ppppp/1p6/8/4K3 w - - 0 101"));
    list.add(new PgnTestCase("03_black_en_passant_capture_left_c3.pgn", "", "", 2, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "4k3/8/8/8/4pppp/2p5/8/4K3 w - - 0 101"));
    list.add(new PgnTestCase("04_black_en_passant_capture_left_d3.pgn", "", "", 2, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "4k3/8/8/8/5ppp/3p4/8/4K3 w - - 0 101"));
    list.add(new PgnTestCase("05_black_en_passant_capture_left_e3.pgn", "", "", 2, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "4k3/8/8/8/6pp/4p3/8/4K3 w - - 0 101"));
    list.add(new PgnTestCase("06_black_en_passant_capture_left_f3.pgn", "", "", 2, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "4k3/8/8/8/7p/5p2/8/4K3 w - - 0 101"));
    list.add(new PgnTestCase("07_black_en_passant_capture_left_g3.pgn", "", "", 2, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "4k3/8/8/8/8/6p1/8/4K3 w - - 0 101"));
    list.add(new PgnTestCase("08_black_en_passant_capture_right_b3.pgn", "", "", 2, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/8/8/8/8/1p6/2PPPPPP/4K3 w - - 0 101"));
    list.add(new PgnTestCase("09_black_en_passant_capture_right_c3.pgn", "", "", 2, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/8/8/8/8/2p5/1P1PPPPP/4K3 w - - 0 101"));
    list.add(new PgnTestCase("10_black_en_passant_capture_right_d3.pgn", "", "", 2, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/8/8/8/8/3p4/1PP1PPPP/4K3 w - - 0 101"));
    list.add(new PgnTestCase("11_black_en_passant_capture_right_e3.pgn", "", "", 2, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/8/8/8/8/4p3/1PPP1PPP/4K3 w - - 0 101"));
    list.add(new PgnTestCase("12_black_en_passant_capture_right_f3.pgn", "", "", 2, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/8/8/8/8/5p2/1PPPP1PP/4K3 w - - 0 101"));
    list.add(new PgnTestCase("13_black_en_passant_capture_right_g3.pgn", "", "", 2, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/8/8/8/8/6p1/1PPPPP1P/4K3 w - - 0 101"));
    list.add(new PgnTestCase("14_black_en_passant_capture_right_h3.pgn", "", "", 2, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/8/8/8/8/7p/1PPPPPP1/4K3 w - - 0 101"));

    return new PgnTestCaseList(PgnTest.BASIC_EN_PASSANT_CAPTURE_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesBasicPromotionPieceWhite() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_white_promotion_piece_capture_no_rook.pgn", "", "", 5, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rRbqkbnr/p1pppppp/n7/7Q/2P5/8/P2PPPPP/RNB1KBNR b KQkq - 0 9"));
    list.add(new PgnTestCase("02_white_promotion_piece_capture_no_knight.pgn", "", "", 5, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rNbqkbnr/p1pppppp/n7/7Q/2P5/8/P2PPPPP/RNB1KBNR b KQkq - 0 9"));
    list.add(new PgnTestCase("03_white_promotion_piece_capture_no_bishop.pgn", "", "", 5, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rBbqkbnr/p1pppppp/n7/7Q/2P5/8/P2PPPPP/RNB1KBNR b KQkq - 0 9"));
    list.add(new PgnTestCase("04_white_promotion_piece_capture_no_queen.pgn", "", "", 5, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rQbqkbnr/p1pppppp/n7/7Q/2P5/8/P2PPPPP/RNB1KBNR b KQkq - 0 9"));
    list.add(new PgnTestCase("05_white_promotion_piece_capture_yes_rook.pgn", "", "", 3, 1, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "R2qkbnr/2pppppp/n7/8/8/8/1PPPPPPP/RNBQKBNR b KQk - 0 5"));
    list.add(new PgnTestCase("06_white_promotion_piece_capture_yes_knight.pgn", "", "", 3, 1, CheckmateOrStalemate.NA,
        1, InsufficientMaterial.NONE, "N2qkbnr/2pppppp/n7/8/8/8/1PPPPPPP/RNBQKBNR b KQk - 0 5"));
    list.add(new PgnTestCase("07_white_promotion_piece_capture_yes_bishop.pgn", "", "", 3, 1, CheckmateOrStalemate.NA,
        1, InsufficientMaterial.NONE, "B2qkbnr/2pppppp/n7/8/8/8/1PPPPPPP/RNBQKBNR b KQk - 0 5"));
    list.add(new PgnTestCase("08_white_promotion_piece_capture_yes_queen.pgn", "", "", 3, 1, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "Q2qkbnr/2pppppp/n7/8/8/8/1PPPPPPP/RNBQKBNR b KQk - 0 5"));

    return new PgnTestCaseList(PgnTest.BASIC_PROMOTION_PIECE_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesBasicPromotionPieceBlack() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_black_promotion_piece_capture_no_rook.pgn", "", "", 4, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnb1kbnr/pppp1pp1/8/4p3/q7/7R/PPPPPPP1/RNBQKBNr w Qkq - 0 9"));
    list.add(new PgnTestCase("02_black_promotion_piece_capture_no_knight.pgn", "", "", 4, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnb1kbnr/pppp1pp1/8/4p3/q7/7R/PPPPPPP1/RNBQKBNn w Qkq - 0 9"));
    list.add(new PgnTestCase("03_black_promotion_piece_capture_no_bishop.pgn", "", "", 4, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnb1kbnr/pppp1pp1/8/4p3/q7/7R/PPPPPPP1/RNBQKBNb w Qkq - 0 9"));
    list.add(new PgnTestCase("04_black_promotion_piece_capture_no_queen.pgn", "", "", 4, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnb1kbnr/pppp1pp1/8/4p3/q7/7R/PPPPPPP1/RNBQKBNq w Qkq - 0 9"));
    list.add(new PgnTestCase("05_black_promotion_piece_capture_yes_rook.pgn", "", "", 4, 1, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqkbnr/pppp1ppp/8/8/8/5NP1/P3PPBP/RNrQK2R w KQkq - 0 6"));
    list.add(new PgnTestCase("06_black_promotion_piece_capture_yes_knight.pgn", "", "", 4, 1, CheckmateOrStalemate.NA,
        1, InsufficientMaterial.NONE, "rnbqkbnr/pppp1ppp/8/8/8/5NP1/P3PPBP/RNnQK2R w KQkq - 0 6"));
    list.add(new PgnTestCase("07_black_promotion_piece_capture_yes_bishop.pgn", "", "", 4, 1, CheckmateOrStalemate.NA,
        1, InsufficientMaterial.NONE, "rnbqkbnr/pppp1ppp/8/8/8/5NP1/P3PPBP/RNbQK2R w KQkq - 0 6"));
    list.add(new PgnTestCase("08_black_promotion_piece_capture_yes_queen.pgn", "", "", 4, 1, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqkbnr/pppp1ppp/8/8/8/5NP1/P3PPBP/RNqQK2R w KQkq - 0 6"));

    return new PgnTestCaseList(PgnTest.BASIC_PROMOTION_PIECE_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesBasicPromotionSquareWhite() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_white_promotion_square_straight_a8.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "Q7/1PPPPPPP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnTestCase("02_white_promotion_square_straight_b8.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "1Q6/P1PPPPPP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnTestCase("03_white_promotion_square_straight_c8.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "2Q5/PP1PPPPP/2B5/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnTestCase("04_white_promotion_square_straight_d8.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "3Q4/PPP1PPPP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnTestCase("05_white_promotion_square_straight_e8.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "4Q3/PPPP1PPP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnTestCase("06_white_promotion_square_straight_f8.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "5Q2/PPPPP1PP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnTestCase("07_white_promotion_square_straight_g8.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "6Q1/PPPPPP1P/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnTestCase("08_white_promotion_square_straight_h8.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "7Q/PPPPPPP1/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnTestCase("09_white_promotion_square_right_a8.pgn", "", "", 1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "Qrrrbrrr/P1PPPPPP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnTestCase("10_white_promotion_square_right_b8.pgn", "", "", 1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rQrrbrrr/PP1PPPPP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnTestCase("11_white_promotion_square_right_c8.pgn", "", "", 1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rrQrbrrr/PPP1PPPP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnTestCase("12_white_promotion_square_right_d8.pgn", "", "", 1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rrrQbrrr/PPPP1PPP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnTestCase("13_white_promotion_square_right_e8.pgn", "", "", 1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rrrrQrrr/PPPPP1PP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnTestCase("14_white_promotion_square_right_f8.pgn", "", "", 1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rrrrbQrr/PPPPPP1P/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnTestCase("15_white_promotion_square_right_g8.pgn", "", "", 1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rrrrbrQr/PPPPPPP1/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnTestCase("16_white_promotion_square_left_b8.pgn", "", "", 1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "nQnnnnnn/1PPPPPPP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnTestCase("17_white_promotion_square_left_c8.pgn", "", "", 1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "nnQnnnnn/P1PPPPPP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnTestCase("18_white_promotion_square_left_d8.pgn", "", "", 1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "nnnQnnnn/PP1PPPPP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnTestCase("19_white_promotion_square_left_e8.pgn", "", "", 1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "nnnnQnnn/PPP1PPPP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnTestCase("20_white_promotion_square_left_f8.pgn", "", "", 1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "nnnnnQnn/PPPP1PPP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnTestCase("21_white_promotion_square_left_g8.pgn", "", "", 1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "nnnnnnQn/PPPPP1PP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnTestCase("22_white_promotion_square_left_h8.pgn", "", "", 1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "nnnnnnnQ/PPPPPP1P/8/8/8/8/8/2k1K3 b - - 0 100"));

    return new PgnTestCaseList(PgnTest.BASIC_PROMOTION_SQUARE_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesBasicPromotionSquareBlack() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_black_promotion_square_straight_a1.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "3k1K2/8/8/8/8/8/1ppppppp/q7 w - - 0 101"));
    list.add(new PgnTestCase("02_black_promotion_square_straight_b1.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "3k1K2/8/8/8/8/8/p1pppppp/1q6 w - - 0 101"));
    list.add(new PgnTestCase("03_black_promotion_square_straight_c1.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "3k1K2/8/8/8/8/8/pp1ppppp/2q5 w - - 0 101"));
    list.add(new PgnTestCase("04_black_promotion_square_straight_d1.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "3k1K2/8/8/8/8/8/ppp1pppp/3q4 w - - 0 101"));
    list.add(new PgnTestCase("05_black_promotion_square_straight_e1.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "3k1K2/8/8/8/8/8/pppp1ppp/4q3 w - - 0 101"));
    list.add(new PgnTestCase("06_black_promotion_square_straight_f1.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "3k1K2/8/8/8/8/5b2/ppppp1pp/5q2 w - - 0 101"));
    list.add(new PgnTestCase("07_black_promotion_square_straight_g1.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "3k1K2/8/8/8/8/8/pppppp1p/6q1 w - - 0 101"));
    list.add(new PgnTestCase("08_black_promotion_square_straight_h1.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "3k1K2/8/8/8/8/8/ppppppp1/7q w - - 0 101"));
    list.add(new PgnTestCase("09_black_promotion_square_left_a1.pgn", "", "", 1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3k1K2/8/8/8/8/8/p1pppppp/qQQNQQQQ w - - 0 101"));
    list.add(new PgnTestCase("10_black_promotion_square_left_b1.pgn", "", "", 1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3k1K2/8/8/8/8/8/pp1ppppp/QqQNQQQQ w - - 0 101"));
    list.add(new PgnTestCase("11_black_promotion_square_left_c1.pgn", "", "", 1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3k1K2/8/8/8/8/8/ppp1pppp/QQqNQQQQ w - - 0 101"));
    list.add(new PgnTestCase("12_black_promotion_square_left_d1.pgn", "", "", 1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3k1K2/8/8/8/8/8/pppp1ppp/QQQqQQQQ w - - 0 101"));
    list.add(new PgnTestCase("13_black_promotion_square_left_e1.pgn", "", "", 1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3k1K2/8/8/8/8/8/ppppp1pp/QQQNqQQQ w - - 0 101"));
    list.add(new PgnTestCase("14_black_promotion_square_left_f1.pgn", "", "", 1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3k1K2/8/8/8/8/8/pppppp1p/QQQNQqQQ w - - 0 101"));
    list.add(new PgnTestCase("15_black_promotion_square_left_g1.pgn", "", "", 1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3k1K2/8/8/8/8/8/ppppppp1/QQQNQQqQ w - - 0 101"));
    list.add(new PgnTestCase("16_black_promotion_square_right_b1.pgn", "", "", 1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3k1K2/8/8/8/8/8/1ppppppp/BqBNBBBB w - - 0 101"));
    list.add(new PgnTestCase("17_black_promotion_square_right_c1.pgn", "", "", 1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3k1K2/8/8/8/8/8/p1pppppp/BBqNBBBB w - - 0 101"));
    list.add(new PgnTestCase("18_black_promotion_square_right_d1.pgn", "", "", 1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3k1K2/8/8/8/8/8/pp1ppppp/BBBqBBBB w - - 0 101"));
    list.add(new PgnTestCase("19_black_promotion_square_right_e1.pgn", "", "", 1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3k1K2/8/8/8/8/8/ppp1pppp/BBBNqBBB w - - 0 101"));
    list.add(new PgnTestCase("20_black_promotion_square_right_f1.pgn", "", "", 1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3k1K2/8/8/8/8/8/pppp1ppp/BBBNBqBB w - - 0 101"));
    list.add(new PgnTestCase("21_black_promotion_square_right_g1.pgn", "", "", 1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3k1K2/8/8/8/8/8/ppppp1pp/BBBNBBqB w - - 0 101"));
    list.add(new PgnTestCase("22_black_promotion_square_right_h1.pgn", "", "", 1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3k1K2/8/8/8/8/8/pppppp1p/BBBNBBBq w - - 0 101"));

    return new PgnTestCaseList(PgnTest.BASIC_PROMOTION_SQUARE_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesBasicCheckWhite() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_white_check_rook_direct_adjacent.pgn", "", "", -1, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbq1bnr/pppp1ppp/3Rk3/4p3/P7/8/1PPPPPPP/1NBQKBNR b K - 5 4"));
    list.add(new PgnTestCase("02_white_check_rook_direct_range.pgn", "", "", -1, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbq1bnr/pppp1ppp/1R1k4/4p3/P7/8/1PPPPPPP/1NBQKBNR b K - 5 4"));
    list.add(new PgnTestCase("03_white_check_rook_discover.pgn", "", "", -1, 10, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqkb1r/ppp1pppp/3pn3/P2R4/Q7/2P5/1P1PPPPP/1NB1KBNR b Kkq - 10 8"));
    list.add(new PgnTestCase("04_white_check_knight_direct.pgn", "", "", -1, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqkb1r/pppppppp/5N2/8/4n3/8/PPPPPPPP/R1BQKBNR b KQkq - 5 3"));
    list.add(new PgnTestCase("05_white_check_knight_discover_orthogonal.pgn", "", "", -1, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbq1bnr/pppp2pp/PN6/R4k2/4pp2/5N2/1PPPPPPP/2BQKB1R b K - 5 8"));
    list.add(new PgnTestCase("06_white_check_knight_discover_diagonal.pgn", "", "", -1, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r1bqkb1r/ppp1pppp/3p1n2/8/Q2n4/N1P5/PP1PPPPP/R1B1KBNR b KQkq - 7 5"));
    list.add(new PgnTestCase("07_white_check_bishop_direct_adjacent.pgn", "", "", -1, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqkb1r/pppppBpp/5p1n/8/8/4P3/PPPP1PPP/RNBQK1NR b KQkq - 3 3"));
    list.add(new PgnTestCase("08_white_check_bishop_direct_range.pgn", "", "", -1, 1, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqkbnr/ppp1pppp/3p4/1B6/8/4P3/PPPP1PPP/RNBQK1NR b KQkq - 1 2"));
    list.add(new PgnTestCase("09_white_check_bishop_discover.pgn", "", "", -1, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r1bq1bnr/pppp1ppp/P1n5/R3k3/2B1p3/4P3/1PPP1PPP/1NBQK1NR b K - 4 7"));
    list.add(
        new PgnTestCase("10_white_check_queen_direct_orthogonal_adjacent.pgn", "", "", 3, 3, CheckmateOrStalemate.NA, 1,
            InsufficientMaterial.NONE, "r2qkb1r/pbppQppp/1pn2P2/8/8/8/PPP1PPPP/RNB1KBNR b KQkq - 3 6"));
    list.add(new PgnTestCase("11_white_check_queen_direct_orthogonal_range.pgn", "", "", 3, 2, CheckmateOrStalemate.NA,
        1, InsufficientMaterial.NONE, "r1bqkb1r/p1pp1ppp/1pn2P2/8/8/4Q3/PPP1PPPP/RNB1KBNR b KQkq - 1 5"));
    list.add(
        new PgnTestCase("12_white_check_queen_direct_diagonal_adjacent.pgn", "", "", -1, 3, CheckmateOrStalemate.NA, 1,
            InsufficientMaterial.NONE, "r1bqkbnr/pppppQpp/2n2p2/8/8/2P5/PP1PPPPP/RNB1KBNR b KQkq - 3 3"));
    list.add(new PgnTestCase("13_white_check_queen_direct_diagonal_range.pgn", "", "", -1, 1, CheckmateOrStalemate.NA,
        1, InsufficientMaterial.NONE, "rnbqkbnr/ppppp1pp/5p2/7Q/8/4P3/PPPP1PPP/RNB1KBNR b KQkq - 1 2"));
    list.add(new PgnTestCase("14_white_check_king_discover_orthogonal.pgn", "", "", 3, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqkb1r/p1pp2pp/1p3Pn1/8/8/5K2/PPP2PPP/RNB1QBNR b kq - 3 8"));
    list.add(new PgnTestCase("15_white_check_king_discover_diagonal.pgn", "", "", -1, 13, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r1bqkb1r/ppp1pppp/3p4/4n2n/QK6/2P5/PP1PPPPP/RNB2BNR b kq - 13 8"));

    return new PgnTestCaseList(PgnTest.BASIC_CHECK_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesBasicCheckBlack() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_black_check_rook_direct_adjacent.pgn", "", "", -1, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "1nbqkbnr/1ppppppp/8/p7/2rKP3/8/PPPP1PPP/RNBQ1BNR w k - 6 5"));
    list.add(new PgnTestCase("02_black_check_rook_direct_range.pgn", "", "", 7, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "1nbqkbnr/1ppppppp/8/8/2N2P2/r4K2/PPPPP1PP/R1BQ1BNR w k - 3 6"));
    list.add(new PgnTestCase("03_black_check_rook_discover.pgn", "", "", -1, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "2b1kbnr/pr1ppppp/2n5/q1p3B1/4N3/1p1P1NPP/PPP1PPB1/1R1QK2R w Kk - 1 10"));
    list.add(new PgnTestCase("04_black_check_knight_direct.pgn", "", "", -1, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r1bqkbnr/pppppppp/1N6/8/8/5n2/PPPPPPPP/R1BQKBNR w KQkq - 6 4"));
    list.add(new PgnTestCase("05_black_check_knight_discover_orthogonal.pgn", "", "", 3, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r1b1kbnr/ppp1pppp/4q1n1/3P4/8/1PN3N1/P1PP1PPP/R1BQKB1R w KQkq - 1 7"));
    list.add(new PgnTestCase("06_black_check_knight_discover_diagonal.pgn", "", "", -1, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnb1kb1r/pp1ppppp/1qp1n3/8/8/2N2P1N/PPPPPKPP/R1BQ1B1R w kq - 4 7"));
    list.add(new PgnTestCase("07_black_check_bishop_direct_adjacent.pgn", "", "", -1, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqk1nr/pppp1ppp/4p3/8/8/1PN2P2/P1PPPbPP/R1BQKBNR w KQkq - 1 4"));
    list.add(new PgnTestCase("08_black_check_bishop_direct_range.pgn", "", "", -1, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rn1qkbnr/pbpppppp/1p6/8/8/6PB/PPPPPPKP/RNBQ2NR w kq - 6 5"));
    list.add(new PgnTestCase("09_black_check_bishop_discover.pgn", "", "", 3, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rn2kbnr/ppp1qppp/8/8/6b1/2N2N2/PPPP1PPP/R1BQKB1R w KQkq - 4 6"));
    list.add(
        new PgnTestCase("10_black_check_queen_direct_orthogonal_adjacent.pgn", "", "", -1, 6, CheckmateOrStalemate.NA,
            1, InsufficientMaterial.NONE, "rnb1kbnr/ppp1pppp/3p4/8/3N4/2N1P3/PPPPqPPP/R1BQKB1R w KQkq - 6 5"));
    list.add(new PgnTestCase("11_black_check_queen_direct_orthogonal_range.pgn", "", "", 3, 3, CheckmateOrStalemate.NA,
        1, InsufficientMaterial.NONE, "rnb1kbnr/ppp1pppp/8/3Pq3/8/2N5/PPPP1PPP/R1BQKBNR w KQkq - 3 4"));
    list.add(
        new PgnTestCase("12_black_check_queen_direct_diagonal_adjacent.pgn", "", "", -1, 4, CheckmateOrStalemate.NA, 1,
            InsufficientMaterial.NONE, "rnb1kbnr/pppp1ppp/4p3/8/8/2NP1N2/PPPqPPPP/R1BQKB1R w KQkq - 4 4"));
    list.add(new PgnTestCase("13_black_check_queen_direct_diagonal_range.pgn", "", "", -1, 2, CheckmateOrStalemate.NA,
        1, InsufficientMaterial.NONE, "rnb1kbnr/pp1ppppp/1qp5/8/8/5P2/PPPPPKPP/RNBQ1BNR w kq - 2 3"));
    list.add(new PgnTestCase("14_black_check_king_discover_orthogonal.pgn", "", "", 3, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rn2qbnr/pppb1ppp/3k4/8/8/2NB1N2/PPPP1PPP/R1BQK2R w KQ - 8 8"));
    list.add(new PgnTestCase("15_black_check_king_discover_diagonal.pgn", "", "", -1, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbq2nr/pppp1p1p/4p1pb/8/3N2k1/1PNP4/PBPKPPPP/R2Q1B1R w - - 7 8"));

    return new PgnTestCaseList(PgnTest.BASIC_CHECK_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesBasicDoubleCheckWhite() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_white_double_check_rook.pgn", "", "", -1, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "5k2/1n6/8/8/5R2/B7/8/4K3 b - - 3 101"));
    list.add(new PgnTestCase("02_white_double_check_knight_orthogonal.pgn", "", "", -1, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3n1k2/3N4/8/8/8/8/5R2/4K3 b - - 3 101"));
    list.add(new PgnTestCase("03_white_double_check_knight_diagonal.pgn", "", "", -1, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "2n2k2/8/4N3/8/8/B7/8/4K3 b - - 3 101"));
    list.add(new PgnTestCase("04_white_double_check_bishop.pgn", "", "", -1, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "5k2/8/3B4/3n4/8/8/5R2/4K3 b - - 3 101"));

    return new PgnTestCaseList(PgnTest.BASIC_DOUBLE_CHECK_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesBasicDoubleCheckBlack() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_black_double_check_rook.pgn", "", "", -1, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/6R1/8/7b/8/8/4Kr2/8 w - - 3 102"));
    list.add(new PgnTestCase("02_black_double_check_knight_orthogonal.pgn", "", "", -1, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "6k1/3r4/8/8/5n2/3K4/5B2/8 w - - 3 102"));
    list.add(new PgnTestCase("03_black_double_check_knight_diagonal.pgn", "", "", -1, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "6k1/7q/2B5/8/8/4n3/2K5/8 w - - 3 102"));
    list.add(new PgnTestCase("04_black_double_check_bishop.pgn", "", "", -1, 1, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3Q4/1K5r/8/3b4/6k1/8/8/8 w - - 1 102"));

    return new PgnTestCaseList(PgnTest.BASIC_DOUBLE_CHECK_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesBasicCheckmateWhite() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_white_checkmate_rook_direct_adjacent.pgn", "", "", -1, 3,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.BLACK_ONLY, "2kR4/R7/8/8/8/8/8/3RK3 b - - 3 101"));
    list.add(new PgnTestCase("02_white_checkmate_rook_direct_range.pgn", "", "", -1, 4, CheckmateOrStalemate.CHECKMATE,
        1, InsufficientMaterial.BLACK_ONLY, "1k5R/6R1/8/8/8/8/8/3RK3 b - - 4 101"));
    list.add(new PgnTestCase("03_white_checkmate_rook_discover.pgn", "", "", -1, 3, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "2k5/R6R/4B3/7p/8/8/8/1R1RRK2 b - - 1 102"));
    list.add(new PgnTestCase("04_white_checkmate_knight_direct.pgn", "", "", -1, 1, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "3k4/1Q6/2N5/7p/8/8/8/2RKR3 b - - 1 101"));
    list.add(new PgnTestCase("05_white_checkmate_knight_discover_orthogonal.pgn", "", "", -1, 5,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE, "3k4/8/5N2/4R3/8/6b1/3R4/2RK4 b - - 5 102"));
    list.add(new PgnTestCase("06_white_checkmate_knight_discover_diagonal.pgn", "", "", -1, 5,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE, "3k2N1/1R6/8/4R1B1/3b4/8/8/2RK4 b - - 5 102"));
    list.add(new PgnTestCase("07_white_checkmate_bishop_direct_adjacent.pgn", "", "", -1, 5,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.BLACK_ONLY, "5B2/8/R4Q2/1k6/2B5/2K5/8/8 b - - 5 102"));
    list.add(new PgnTestCase("08_white_checkmate_bishop_direct_range.pgn", "", "", -1, 5,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.BLACK_ONLY, "5B2/8/R4Q2/3k4/8/2K2B2/8/8 b - - 5 102"));
    list.add(new PgnTestCase("09_white_checkmate_bishop_discover.pgn", "", "", -1, 1, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "8/2B5/1R6/2k2Q2/7p/2K5/8/8 b - - 1 102"));
    list.add(new PgnTestCase("10_white_checkmate_queen_direct_orthogonal_adjacent.pgn", "", "", -1, 1,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE, "8/8/1RQ5/2k4p/5B2/2K5/8/8 b - - 1 101"));
    list.add(new PgnTestCase("11_white_checkmate_queen_direct_orthogonal_range.pgn", "", "", -1, 3,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.BLACK_ONLY, "8/k7/8/Q1K5/1R6/8/8/8 b - - 3 101"));
    list.add(new PgnTestCase("12_white_checkmate_queen_direct_diagonal_adjacent.pgn", "", "", -1, 5,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.BLACK_ONLY, "k7/1QK5/8/7R/1R6/8/8/8 b - - 5 102"));
    list.add(new PgnTestCase("13_white_checkmate_queen_direct_diagonal_range.pgn", "", "", -1, 1,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE, "8/2Q5/4k1K1/1R6/8/1Q6/8/4q3 b - - 1 101"));
    list.add(new PgnTestCase("14_white_checkmate_king_discover_orthogonal.pgn", "", "", -1, 3,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.BLACK_ONLY, "Q7/4k2R/R6K/8/8/8/8/8 b - - 3 101"));
    list.add(new PgnTestCase("15_white_checkmate_king_discover_diagonal.pgn", "", "", -1, 1,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE, "3k4/1K6/8/BN4p1/8/7B/8/4R3 b - - 1 102"));

    return new PgnTestCaseList(PgnTest.BASIC_CHECKMATE_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesBasicCheckmateBlack() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_black_checkmate_rook_direct_adjacent.pgn", "", "", -1, 3,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.WHITE_ONLY, "2Kr4/7q/8/8/8/8/8/2kr4 w - - 3 102"));
    list.add(new PgnTestCase("02_black_checkmate_rook_direct_range.pgn", "", "", -1, 5, CheckmateOrStalemate.CHECKMATE,
        1, InsufficientMaterial.NONE, "4kr2/3q4/8/8/7b/8/6rN/4K3 w - - 5 103"));
    list.add(new PgnTestCase("03_black_checkmate_rook_discover.pgn", "", "", -1, 5, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "3K4/1r6/3k4/b3r3/5N2/8/8/2r5 w - - 5 103"));
    list.add(new PgnTestCase("04_black_checkmate_knight_direct.pgn", "", "", -1, 1, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "Q1r5/8/7r/3K4/1r3n2/8/8/3kr3 w - - 1 102"));
    list.add(new PgnTestCase("05_black_checkmate_knight_discover_orthogonal.pgn", "", "", -1, 5,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE, "4k3/8/5n1r/8/8/3N4/5q2/7K w - - 5 103"));
    list.add(new PgnTestCase("06_black_checkmate_knight_discover_diagonal.pgn", "", "", -1, 1,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE, "Q4q2/3rk3/n7/b7/8/8/7r/4K3 w - - 1 103"));
    list.add(new PgnTestCase("07_black_checkmate_bishop_direct_adjacent.pgn", "", "", -1, 1,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE, "4k3/P7/8/5r1r/8/8/q6b/6KN w - - 1 102"));
    list.add(new PgnTestCase("08_black_checkmate_bishop_direct_range.pgn", "", "", -1, 1,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE, "Q7/8/8/2b1kr1r/8/8/q7/6K1 w - - 1 102"));
    list.add(new PgnTestCase("09_black_checkmate_bishop_discover.pgn", "", "", -1, 5, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "6q1/P7/4k3/1B2br1r/8/8/8/6K1 w - - 5 103"));
    list.add(new PgnTestCase("10_black_checkmate_queen_direct_orthogonal_adjacent.pgn", "", "", -1, 1,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE, "1Q6/8/4k3/7r/8/8/r5q1/6K1 w - - 1 102"));
    list.add(new PgnTestCase("11_black_checkmate_queen_direct_orthogonal_range.pgn", "", "", -1, 1,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE, "1Q6/8/4k3/4nrqr/8/8/8/6K1 w - - 1 102"));
    list.add(new PgnTestCase("12_black_checkmate_queen_direct_diagonal_adjacent.pgn", "", "", -1, 1,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE, "Q7/8/4k3/4nr1r/8/8/5q2/6K1 w - - 1 102"));
    list.add(new PgnTestCase("13_black_checkmate_queen_direct_diagonal_range.pgn", "", "", -1, 1,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE, "Q7/8/1q2k3/3bnr1r/8/8/8/6K1 w - - 1 102"));
    list.add(new PgnTestCase("14_black_checkmate_king_discover_orthogonal.pgn", "", "", -1, 5,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE, "5q2/8/8/8/3kr3/3q4/N7/4K3 w - - 5 103"));
    list.add(new PgnTestCase("15_black_checkmate_king_discover_diagonal.pgn", "", "", -1, 5,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE, "2K5/8/6N1/bnnn2k1/6b1/8/7b/8 w - - 5 103"));

    return new PgnTestCaseList(PgnTest.BASIC_CHECKMATE_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesBasicCheckmateVariousWhite() {
    final List<PgnTestCase> list = new ArrayList<>();
    list.add(new PgnTestCase("01_checkmate_by_white_capture_yes.pgn", "", "", 7, 5, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "1r1qkbnr/pbp2Qp1/2pp3p/4p1N1/4P3/8/PPPP1PPP/RNB1K2R b KQk - 0 8"));
    list.add(new PgnTestCase("02_checkmate_by_white_capture_no.pgn", "", "", -1, 5, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "r2qkbnr/pbpppQ1p/1pn3p1/4Np2/4P3/8/PPPP1PPP/RNB1KB1R b KQkq - 1 6"));
    list.add(new PgnTestCase("03_checkmate_by_white_discover_check.pgn", "", "", 4, 19, CheckmateOrStalemate.CHECKMATE,
        1, InsufficientMaterial.NONE, "r1b2bnr/1ppp1ppp/p2Q4/4p3/4P2k/3P4/PPPBBPP1/RN2K1NR b Q - 19 18"));
    list.add(new PgnTestCase("04_checkmate_fools_mate_by_white.pgn", "", "", -1, 1, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "rnbqkbnr/ppppp2p/5p2/6pQ/4P3/2N5/PPPP1PPP/R1B1KBNR b KQkq - 1 3"));
    list.add(new PgnTestCase("05_checkmate_scholars_mate_by_white.pgn", "", "", 7, 4, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "r1bqk1nr/pppp1Qpp/2n5/2b1p3/2B1P3/8/PPPP1PPP/RNB1K1NR b KQkq - 0 4"));
    return new PgnTestCaseList(PgnTest.BASIC_CHECKMATE_VARIOUS_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesBasicCheckmateVariousBlack() {
    final List<PgnTestCase> list = new ArrayList<>();
    list.add(new PgnTestCase("01_checkmate_by_black_capture_yes.pgn", "", "", 3, 2, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "rnb1kbnr/1p1p1pp1/p1B4p/2P5/8/P1N1q1PR/1PP1P3/R1BK2q1 w kq - 0 14"));
    list.add(new PgnTestCase("02_checkmate_by_black_capture_no.pgn", "", "", 5, 10, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "r7/ppp3pp/2N1k3/1B6/3np3/1b3n2/1P1b1PPP/RN1K3R w - - 7 21"));
    list.add(new PgnTestCase("03_checkmate_by_black_discover_check.pgn", "", "", 6, 3, CheckmateOrStalemate.CHECKMATE,
        1, InsufficientMaterial.NONE, "1nbq4/1p1p2pk/7n/1N2pp1P/1p2Pb2/7N/r7/r2KQ2R w - - 0 21"));
    list.add(new PgnTestCase("04_checkmate_fools_mate_by_black.pgn", "", "", -1, 1, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "rnb1kbnr/pppp1ppp/8/4p3/6Pq/5P2/PPPPP2P/RNBQKBNR w KQkq - 1 3"));
    list.add(new PgnTestCase("05_checkmate_scholars_mate_by_black.pgn", "", "", 8, 4, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "rnb1k1nr/pppp1ppp/8/2b1p3/2B1P3/2NP4/PPP2qPP/R1BQK1NR w KQkq - 0 5"));
    return new PgnTestCaseList(PgnTest.BASIC_CHECKMATE_VARIOUS_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesBasicCheckmateDoubleCheckWhite() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_white_double_check_checkmate_rook.pgn", "", "", -1, 3, CheckmateOrStalemate.CHECKMATE,
        1, InsufficientMaterial.NONE, "8/2rpp3/1R2k3/7R/8/8/B7/B3K3 b - - 3 101"));
    list.add(new PgnTestCase("02_white_double_check_checkmate_knight_orthogonal.pgn", "", "", -1, 3,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE, "8/4p3/3pk1n1/2N4R/8/8/4R3/4KQ2 b - - 3 101"));
    list.add(new PgnTestCase("03_white_double_check_checkmate_knight_diagonal.pgn", "", "", -1, 3,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE,
        "8/2pp1p2/2n1pk2/3N3R/8/8/1B4R1/4K3 b - - 3 101"));
    list.add(new PgnTestCase("04_white_double_check_checkmate_bishop.pgn", "", "", -1, 3,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE, "8/3p1pp1/4pkn1/R7/7B/8/8/1B2KR2 b - - 3 101"));

    return new PgnTestCaseList(PgnTest.BASIC_CHECKMATE_DOUBLE_CHECK_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesBasicCheckmateDoubleCheckBlack() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_black_double_check_checkmate_rook.pgn", "", "", -1, 3, CheckmateOrStalemate.CHECKMATE,
        1, InsufficientMaterial.NONE, "4k3/8/8/b7/8/8/R6r/1r2K3 w - - 3 102"));
    list.add(new PgnTestCase("02_black_double_check_checkmate_knight_orthogonal.pgn", "", "", -1, 3,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE, "3k4/8/4r3/8/8/5n2/R7/3NKBb1 w - - 3 102"));
    list.add(new PgnTestCase("03_black_double_check_checkmate_knight_diagonal.pgn", "", "", -1, 3,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE, "b4k2/6b1/3n4/8/4KP2/7r/Q7/8 w - - 3 102"));
    list.add(new PgnTestCase("04_black_double_check_checkmate_bishop.pgn", "", "", -1, 3,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE, "1r2k3/8/8/2r5/4n3/8/3b4/2KRN3 w - - 3 102"));

    return new PgnTestCaseList(PgnTest.BASIC_CHECKMATE_DOUBLE_CHECK_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesBasicStalemate() {
    final List<PgnTestCase> list = new ArrayList<>();
    list.add(new PgnTestCase("01_stalemate_last_move_black.pgn", "", "", 4, 12, CheckmateOrStalemate.STALEMATE, 1,
        InsufficientMaterial.NONE, "r4knr/p2b1p2/3p4/p1pPp2p/P1PnP1pP/8/2q5/K7 w - - 0 26"));
    list.add(new PgnTestCase("02_stalemate_last_move_white.pgn", "", "", 33, 10, CheckmateOrStalemate.STALEMATE, 1,
        InsufficientMaterial.NONE, "8/5Q2/1p3p1k/pP1p1Pp1/P1pPp1Pp/2P1P2P/8/RNB1KBNR b KQ - 0 24"));
    return new PgnTestCaseList(PgnTest.BASIC_STALEMATE, list);
  }

  private static PgnTestCaseList createTestCasesBasicInsufficientMaterial(InsufficientMaterial insufficientMaterial,
      PgnTest pgnTest) {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("insufficient_material_KBwBb_K.pgn", "", "", 3, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "6k1/8/3K4/8/8/8/3BB3/8 b - - 0 33"));

    list.add(new PgnTestCase("insufficient_material_KBwN_K.pgn", "", "", 3, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "8/6k1/3K4/4N3/8/7B/8/8 w - - 0 36"));
    list.add(new PgnTestCase("insufficient_material_KBbN_K.pgn", "", "", 3, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "8/6k1/3K4/8/8/5N2/8/2B5 b - - 0 33"));

    list.add(new PgnTestCase("insufficient_material_KBw_K.pgn", "", "", 3, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BOTH, "8/8/3K2k1/8/8/8/8/5B2 b - - 0 33"));
    list.add(new PgnTestCase("insufficient_material_KBb_K.pgn", "", "", 3, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BOTH, "8/8/8/4k3/8/4K3/8/2B5 w - - 0 35"));

    list.add(new PgnTestCase("insufficient_material_KBw_KBb.pgn", "", "", 3, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/3b2k1/8/3K2B1/8/8/8 b - - 0 35"));
    list.add(new PgnTestCase("insufficient_material_KBb_KBw.pgn", "", "", 3, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/k3b3/8/5K2/8/8/2B5 b - - 0 35"));

    list.add(new PgnTestCase("insufficient_material_KBw_KBw.pgn", "", "", 3, 17, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BOTH, "5K2/8/8/1B6/8/k7/6b1/8 w - - 0 39"));
    list.add(new PgnTestCase("insufficient_material_KBb_KBb.pgn", "", "", 3, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BOTH, "8/8/8/4k3/5b2/3K4/8/2B5 w - - 0 33"));

    list.add(new PgnTestCase("insufficient_material_KN_KN.pgn", "", "", 3, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/4K3/8/1n6/8/5k1N/8 w - - 0 37"));

    list.add(new PgnTestCase("insufficient_material_KNN_K.pgn", "", "", 3, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "5k2/8/5N2/2K5/8/5N2/8/8 w - - 0 34"));

    list.add(new PgnTestCase("insufficient_material_KN_K.pgn", "", "", 3, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BOTH, "4k3/8/8/2K5/8/5N2/8/8 w - - 0 35"));

    // K vs K reached on the last halfmove via captures — game ends at dead position, no moves past.
    list.add(new PgnTestCase("insufficient_material_K_K.pgn", "", "", 3, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BOTH, "2K5/8/8/8/8/8/4k3/8 b - - 0 38"));

    list.add(new PgnTestCase("insufficient_material_K_KBw.pgn", "", "", 3, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BOTH, "2b5/8/2k5/8/4K3/8/8/8 b - - 0 34"));
    list.add(new PgnTestCase("insufficient_material_K_KBb.pgn", "", "", 3, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BOTH, "8/8/8/2b5/2K5/8/4k3/8 w - - 0 34"));

    list.add(new PgnTestCase("insufficient_material_K_KBwBb.pgn", "", "", 3, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "4k3/8/8/4K3/8/b7/4b3/8 b - - 0 34"));

    list.add(new PgnTestCase("insufficient_material_K_KBwN.pgn", "", "", 3, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "4k3/8/8/2K1n3/8/8/4b3/8 b - - 0 35"));
    list.add(new PgnTestCase("insufficient_material_K_KBbN.pgn", "", "", 3, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "1n2k3/8/8/8/8/b4K2/8/8 b - - 0 33"));

    list.add(new PgnTestCase("insufficient_material_K_KN.pgn", "", "", 3, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BOTH, "4k3/8/8/1K2n3/8/8/8/8 b - - 0 36"));

    list.add(new PgnTestCase("insufficient_material_K_KNN.pgn", "", "", 3, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "1n2k3/8/3n4/8/8/8/3K4/8 b - - 0 35"));

    // more than two bishops one side same square and only kings - is insufficient
    // material
    list.add(new PgnTestCase("insufficient_material_KBwBw_K.pgn", "", "", 7, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BOTH, "2B5/3B4/8/6K1/8/1k6/8/8 w - - 0 32"));
    list.add(new PgnTestCase("insufficient_material_KBbBb_K.pgn", "", "", 3, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BOTH, "5B2/8/2k5/8/8/1K6/8/2B5 b - - 0 36"));
    list.add(new PgnTestCase("insufficient_material_K_KBwBw.pgn", "", "", 4, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BOTH, "3k4/1b6/2b5/8/8/8/3K4/8 w - - 0 27"));
    list.add(new PgnTestCase("insufficient_material_K_KBbBb.pgn", "", "", 4, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BOTH, "5b2/3k4/8/8/5K2/2b5/8/8 b - - 0 25"));

    // KN against king and single piece
    list.add(new PgnTestCase("insufficient_material_KN_KR.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "7r/3k4/8/8/8/8/3K4/1N6 w - - 0 21"));
    list.add(new PgnTestCase("insufficient_material_KN_KBw.pgn", "", "", 5, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/k2b4/8/8/2K5/8/8/1N6 w - - 0 23"));
    list.add(new PgnTestCase("insufficient_material_KN_KBb.pgn", "", "", 5, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "5b2/8/3k4/8/8/4K3/8/1N6 w - - 0 24"));
    list.add(new PgnTestCase("insufficient_material_KN_KQ.pgn", "", "", 5, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "4k3/8/8/8/8/5K2/3q4/1N6 w - - 0 24"));
    list.add(new PgnTestCase("insufficient_material_KN_KP.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3k4/8/3p4/8/8/4K3/8/1N6 w - - 0 23"));

    // KBw against king and single piece
    list.add(new PgnTestCase("insufficient_material_KBw_KR.pgn", "", "", 5, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "r7/8/3K4/8/8/2k5/8/5B2 w - - 0 28"));
    list.add(new PgnTestCase("insufficient_material_KBw_KN.pgn", "", "", 5, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "1n6/8/8/2k1K3/8/8/8/5B2 w - - 0 26"));
    list.add(new PgnTestCase("insufficient_material_KBw_KQ.pgn", "", "", 5, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "3K4/8/8/4q3/k7/8/8/5B2 b - - 0 30"));
    list.add(new PgnTestCase("insufficient_material_KBw_KP.pgn", "", "", 5, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/2p1K3/8/1k6/8/5B2 w - - 0 27"));

    // KBb against king and single piece
    list.add(new PgnTestCase("insufficient_material_KBb_KR.pgn", "", "", 5, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/r7/k7/8/1K6/2B5 b - - 0 25"));
    list.add(new PgnTestCase("insufficient_material_KBb_KN.pgn", "", "", 5, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "1n6/8/8/8/4K3/1k6/1B6/8 b - - 3 27"));
    list.add(new PgnTestCase("insufficient_material_KBb_KQ.pgn", "", "", 5, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/2q5/8/2BK4/k7/8 b - - 0 28"));
    list.add(new PgnTestCase("insufficient_material_KBb_KP.pgn", "", "", 5, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/6B1/8/2p5/8/1k6/8/1K6 b - - 0 26"));

    // King and single piece against KN
    list.add(new PgnTestCase("insufficient_material_KR_KN.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "1n6/8/8/2k5/4K3/8/7R/8 b - - 0 22"));
    // we have insufficientMaterial_KBw_KN already tested above
    // we have insufficientMaterial_KBb_KN already tested above
    list.add(new PgnTestCase("insufficient_material_KQ_KN.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "1n6/8/8/4Q3/k3K3/8/8/8 b - - 0 24"));
    list.add(new PgnTestCase("insufficient_material_KQQ_KN.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "1n6/7Q/8/2Q5/8/k2K4/8/8 b - - 0 28"));
    list.add(new PgnTestCase("insufficient_material_KP_KN.pgn", "", "", 5, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "1n6/8/8/2k5/8/8/6KP/8 b - - 0 24"));

    // King and single piece against KBw
    list.add(new PgnTestCase("insufficient_material_KR_KBw.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "2b5/8/8/8/2k5/8/8/2K4R w - - 0 24"));
    list.add(new PgnTestCase("insufficient_material_KR_KBwBw.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "2b5/8/8/1k6/3R4/8/4K3/1b6 b - - 0 26"));
    // we have insufficientMaterial_KN_KBw already tested above
    list.add(new PgnTestCase("insufficient_material_KQ_KBw.pgn", "", "", 5, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "2b5/2Q5/8/8/8/k7/8/2K5 w - - 0 25"));
    list.add(new PgnTestCase("insufficient_material_KQBw_KBw.pgn", "", "", 5, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "4B3/8/1k6/8/4b3/2K5/7Q/8 w - - 0 30"));
    list.add(new PgnTestCase("insufficient_material_KQQ_KBw.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "3Q4/2Q5/b7/8/k7/8/8/2K5 b - - 0 26"));
    list.add(new PgnTestCase("insufficient_material_KQ_KBwBw.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "2b5/8/8/8/k7/8/K7/3b3Q b - - 0 27"));
    list.add(new PgnTestCase("insufficient_material_KQQ_KBwBw.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "1Qb5/7Q/8/8/k7/2K5/8/3b4 b - - 0 30"));
    list.add(new PgnTestCase("insufficient_material_KP_KBw.pgn", "", "", 5, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/3P4/k5b1/8/8/2K5 w - - 0 27"));

    // King and single piece against KBb
    list.add(new PgnTestCase("insufficient_material_KR_KBb.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "8/5kb1/8/8/8/3K4/8/7R w - - 0 24"));
    list.add(new PgnTestCase("insufficient_material_KR_KBbBb.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "5b2/8/6k1/8/8/R1K5/8/4b3 w - - 0 29"));

    // we have insufficientMaterial_KN_KBb already tested above
    list.add(new PgnTestCase("insufficient_material_KQ_KBb.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "8/8/7k/4Q3/8/b2K4/8/8 w - - 0 24"));
    list.add(new PgnTestCase("insufficient_material_KQBb_KBb.pgn", "", "", 5, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "5B2/3Q4/7b/3K3k/8/8/8/8 b - - 0 29"));
    list.add(new PgnTestCase("insufficient_material_KQQ_KBb.pgn", "", "", 5, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "4Qb2/6k1/8/8/Q7/3K4/8/8 w - - 0 29"));
    list.add(new PgnTestCase("insufficient_material_KQ_KBbBb.pgn", "", "", 5, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "5b2/7k/8/8/K7/1Q6/8/4b3 w - - 0 31"));
    list.add(new PgnTestCase("insufficient_material_KQQ_KBbBb.pgn", "", "", 5, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "5b2/Q7/8/8/7k/1K6/3Q4/4b3 w - - 0 34"));

    list.add(new PgnTestCase("insufficient_material_KP_KBb.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "5b2/8/4P3/3K2k1/8/8/8/8 b - - 0 25"));

    list.add(new PgnTestCase("insufficient_material_KBw_KBwQ.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "8/1b2q3/8/k4K2/8/8/8/5B2 w - - 0 25"));
    list.add(new PgnTestCase("insufficient_material_KBw_KBwR.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "r1b5/8/8/8/1k6/8/3K4/5B2 b - - 0 24"));
    list.add(new PgnTestCase("insufficient_material_KBw_KQR.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "r7/3B4/8/8/1k3q2/8/8/1K6 b - - 0 24"));
    list.add(new PgnTestCase("insufficient_material_KBw_KBwQR.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "r1b5/8/8/8/1k3q2/8/8/1K3B2 w - - 0 23"));

    list.add(new PgnTestCase("insufficient_material_KBb_KBbQ.pgn", "", "", 3, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "4kb2/6q1/8/8/8/8/3K4/2B5 w - - 0 28"));
    list.add(new PgnTestCase("insufficient_material_KBb_KBbR.pgn", "", "", 3, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "4kb2/8/8/8/4r3/8/3K4/2B5 w - - 0 28"));
    list.add(new PgnTestCase("insufficient_material_KBb_KQR.pgn", "", "", 3, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "8/3k3r/8/8/5q2/8/3K4/2B5 w - - 0 28"));
    list.add(new PgnTestCase("insufficient_material_KBb_KBbQR.pgn", "", "", 3, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "4kb2/7r/8/3q4/8/8/3K4/2B5 w - - 0 27"));

    list.add(new PgnTestCase("insufficient_material_KBb_KQBb.pgn", "", "", 3, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "5b2/5k2/8/4q3/2K5/8/8/2B5 b - - 1 31"));
    list.add(new PgnTestCase("insufficient_material_KBb_KQQ.pgn", "", "", 3, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/4k3/7q/8/K7/3B1q2/8 b - - 0 32"));
    list.add(new PgnTestCase("insufficient_material_KBbBb_KQ.pgn", "", "", 3, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "7B/4k3/8/8/8/1q6/3BK3/8 b - - 0 34"));
    list.add(new PgnTestCase("insufficient_material_KBbBb_KQQ.pgn", "", "", 5, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "8/3q4/8/8/k2B4/B5K1/3q4/8 b - - 0 29"));
    list.add(new PgnTestCase("insufficient_material_KBbBb_KR.pgn", "", "", 5, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/7B/k7/8/8/1B4r1/4K3 b - - 1 32"));
    list.add(new PgnTestCase("insufficient_material_KBbQ_KBb.pgn", "", "", 5, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "Q7/8/8/1k6/8/8/3K4/b1B5 w - - 0 28"));
    list.add(new PgnTestCase("insufficient_material_KBbQR_KBb.pgn", "", "", 5, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "3R3Q/8/7b/8/k7/8/4K3/2B5 b - - 0 31"));
    list.add(new PgnTestCase("insufficient_material_KBbR_KBb.pgn", "", "", 5, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "3R1b2/8/8/8/k7/8/4K3/2B5 w - - 0 33"));
    list.add(new PgnTestCase("insufficient_material_KBw_KQBw.pgn", "", "", 5, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/5q2/1k1B4/8/1b1K4 w - - 0 31"));
    list.add(new PgnTestCase("insufficient_material_KBw_KQQ.pgn", "", "", 5, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "1q6/8/8/8/5q2/1k6/6K1/5B2 w - - 0 29"));
    list.add(new PgnTestCase("insufficient_material_KBwBw_KQ.pgn", "", "", 5, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "q3B3/8/8/8/8/1k4K1/8/5B2 b - - 0 34"));
    list.add(new PgnTestCase("insufficient_material_KBwBw_KQQ.pgn", "", "", 5, 11, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "q1B5/8/4B1q1/8/1k6/8/8/5K2 w - - 0 32"));
    list.add(new PgnTestCase("insufficient_material_KBwBw_KR.pgn", "", "", 5, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "r1B5/8/8/8/8/2k5/8/3K1B2 b - - 0 32"));
    list.add(new PgnTestCase("insufficient_material_KBwQ_KBw.pgn", "", "", 5, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "8/8/k7/8/6B1/5b2/3Q4/2K5 w - - 0 35"));
    list.add(new PgnTestCase("insufficient_material_KBwQR_KBw.pgn", "", "", 5, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "5R1Q/1b1B4/8/8/2k5/8/8/2K5 b - - 0 38"));
    list.add(new PgnTestCase("insufficient_material_KBwR_KBw.pgn", "", "", 3, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "1R6/8/3k4/8/5K2/7B/4b3/8 w - - 0 30"));
    list.add(new PgnTestCase("insufficient_material_KN_KQQ.pgn", "", "", 5, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/3k4/6q1/8/5K2/8/1Nq5 w - - 0 27"));
    list.add(new PgnTestCase("insufficient_material_KQR_KBb.pgn", "", "", 3, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "1R6/3k4/8/b7/8/8/5K2/1Q6 w - - 0 30"));
    list.add(new PgnTestCase("insufficient_material_KQR_KBw.pgn", "", "", 3, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "1R6/8/4k3/8/8/3b4/3Q4/4K3 w - - 0 27"));
    return filterInsufficientMaterial(pgnTest, list, insufficientMaterial);
  }

  private static PgnTestCaseList filterInsufficientMaterial(PgnTest pgnTest, List<PgnTestCase> list,
      InsufficientMaterial insufficientMaterial) {
    final List<PgnTestCase> result = new ArrayList<>();
    for (final PgnTestCase testCase : list) {
      if (testCase.insufficientMaterial() == insufficientMaterial) {
        result.add(testCase);
      }
    }
    return new PgnTestCaseList(pgnTest, result);
  }

  private static PgnTestCaseList createTestCasesBasicThreefold() {
    final List<PgnTestCase> list = new ArrayList<>();
    list.add(new PgnTestCase("01_threefold_moves_very_low_one_before_first_threefold.pgn", "", "", -1, 7,
        CheckmateOrStalemate.NA, 2, InsufficientMaterial.NONE,
        "rnbqkb1r/pppppppp/5n2/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 7 4"));
    list.add(new PgnTestCase("02_threefold_moves_very_low_end_with_first_threefold.pgn", "repPos=3: 2...Ng8 4...Ng8",
        "", -1, 8, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 8 5"));
    list.add(
        new PgnTestCase("03_threefold_moves_low_one_before_first_threefold.pgn", "", "", -1, 9, CheckmateOrStalemate.NA,
            2, InsufficientMaterial.NONE, "r1bqk2r/pppp1ppp/2n2n2/2b1p3/4P3/2N2N2/PPPP1PPP/R1BQKB1R b KQkq - 9 7"));
    list.add(new PgnTestCase("04_threefold_moves_low_end_with_first_threefold.pgn", "repPos=3: 3...Nc6 5...Bf8 7...Bf8",
        "", -1, 10, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "r1bqkb1r/pppp1ppp/2n2n2/4p3/4P3/2N2N2/PPPP1PPP/R1BQKB1R w KQkq - 10 8"));
    list.add(new PgnTestCase("05_threefold_moves_medium_one_before_first_threefold.pgn", "", "", -1, 14,
        CheckmateOrStalemate.NA, 2, InsufficientMaterial.NONE,
        "r1bq1rk1/pp1nbppp/2n1p3/1BppP3/3P1P2/2N1BN2/PPP3PP/R2QR1K1 w - - 14 13"));
    list.add(new PgnTestCase("06_threefold_moves_medium_end_with_first_threefold.pgn", "repPos=3: 9.O-O 11.Rf1 13.Rf1",
        "", -1, 15, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "r1bq1rk1/pp1nbppp/2n1p3/1BppP3/3P1P2/2N1BN2/PPP3PP/R2Q1RK1 b - - 15 13"));
    list.add(new PgnTestCase("07_threefold_moves_high_one_before_first_threefold.pgn", "", "", -1, 10,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "r4rk1/pp1b1pbp/q1n1p1p1/2ppPn2/P2P4/1P3NP1/2P1NPBP/R1BQ1RK1 b - - 10 17"));
    list.add(new PgnTestCase("08_threefold_moves_high_end_with_first_threefold.pgn",
        "repPos=3: 13...Bg7 15...Bg7 17...Nfe7", "", -1, 11, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "r4rk1/pp1bnpbp/q1n1p1p1/2ppP3/P2P4/1P3NP1/2P1NPBP/R1BQ1RK1 w - - 11 18"));
    list.add(new PgnTestCase("09_threefold_moves_very_high_one_before_first_threefold.pgn", "", "", 23, 11,
        CheckmateOrStalemate.NA, 2, InsufficientMaterial.NONE, "1R4R1/5p1k/4p2p/3NP1pP/3r4/8/5PPK/8 w - - 11 49"));
    list.add(new PgnTestCase("10_threefold_moves_very_high_end_with_first_threefold.pgn",
        "repPos=3: 45.Rh8+ 47.Rh8+ 49.Rh8+", "", 23, 12, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "1R5R/5p1k/4p2p/3NP1pP/3r4/8/5PPK/8 b - - 12 49"));
    list.add(
        new PgnTestCase("11_threefold_castling_one_before_first_threefold.pgn", "", "", -1, 9, CheckmateOrStalemate.NA,
            2, InsufficientMaterial.NONE, "rnbqkb1r/1ppppppp/p7/8/3Nn3/8/PPPPPPPP/RNBQKB1R b KQk - 9 10"));
    list.add(new PgnTestCase("12_threefold_castling_end_with_first_threefold.pgn", "repPos=3: 6...Ra7 8...Ra7 10...Ra7",
        "", -1, 10, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "1nbqkb1r/rppppppp/p7/8/3Nn3/8/PPPPPPPP/RNBQKB1R w KQk - 10 11"));
    list.add(new PgnTestCase("13_threefold_en_passant_capture_one_before_first_threefold.pgn", "", "", -1, 8,
        CheckmateOrStalemate.NA, 2, InsufficientMaterial.NONE,
        "rnbqkb1r/ppp1pppp/5n2/3pP3/8/8/PPPP1PPP/RNBQKBNR w KQkq - 8 7"));
    list.add(new PgnTestCase("14_threefold_en_passant_capture_end_with_first_threefold.pgn",
        "repPos=3: 3.Bc4 5.Bc4 7.Bc4", "", -1, 9, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "rnbqkb1r/ppp1pppp/5n2/3pP3/2B5/8/PPPP1PPP/RNBQK1NR b KQkq - 9 7"));
    list.add(new PgnTestCase("15_threefold_en_passant_capture_castling_one_before_first_threefold.pgn", "", "", -1, 16,
        CheckmateOrStalemate.NA, 2, InsufficientMaterial.NONE,
        "rnbqkb1r/ppp1pppp/5n2/3pP3/8/8/PPPP1PPP/RNBQKBNR w KQ - 16 11"));
    list.add(new PgnTestCase("16_threefold_en_passant_capture_castling_end_with_first_threefold.pgn",
        "repPos=3: 8...Ke8 10...Ke8 12...Nb8", "", -1, 20, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "rnbqkb1r/ppp1pppp/5n2/3pP3/8/8/PPPP1PPP/RNBQKBNR w KQ - 20 13"));
    list.add(new PgnTestCase("17_threefold_two_threefolds_end_with_threefold.pgn",
        "repPos=3: 2...d5 4...Bc8 6...Bc8; repPos=3: 8.g3 10.Bc1 12.Bc1", "", -1, 8, CheckmateOrStalemate.NA, 3,
        InsufficientMaterial.NONE, "rnbqkbnr/ppp2pp1/8/3pp2p/3PP2P/6P1/PPP2P2/RNBQKBNR b KQkq - 8 12"));
    list.add(new PgnTestCase("18_threefold_two_threefolds_beyond.pgn",
        "repPos=3: 2...d5 4...Bc8 6...Bc8; repPos=3: 8.g3 10.Bc1 12.Bc1", "", -1, 10, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r1bqk1nr/2p3p1/ppnb1p2/3pp1Bp/1P1PP1QP/N1P3P1/P4P2/R3KBNR b KQkq - 1 17"));

    list.add(new PgnTestCase("19_threefold_multiple_threefolds_end_with_threefold.pgn",
        "repPos=4: 2...d5 4...Bc8 6...Bc8 8...Bc8; repPos=3: 3.Bb5+ 5.Bb5+ 7.Bb5+; repPos=3: 3...Bd7 5...Bd7 7...Bd7; repPos=3: 4.Bf1 6.Bf1 8.Bf1; repPos=3: 10.g3 12.Bc1 14.Bc1; repPos=3: 10...Bb4+ 12...Bb4+ 14...Bb4+; repPos=3: 11.Bd2 13.Bd2 15.Bd2; repPos=3: 11...Bf8 13...Bf8 15...Bf8",
        "", -1, 12, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "rnbqkbnr/ppp2pp1/8/3pp2p/3PP2P/6P1/PPPB1P2/RN1QKBNR w KQkq - 11 16"));

    list.add(new PgnTestCase("20_threefold_multiple_threefolds_beyond.pgn",
        "repPos=4: 2...d5 4...Bc8 6...Bc8 8...Bc8; repPos=3: 3.Bb5+ 5.Bb5+ 7.Bb5+; repPos=3: 3...Bd7 5...Bd7 7...Bd7; repPos=3: 4.Bf1 6.Bf1 8.Bf1; repPos=4: 10.g3 12.Bc1 14.Bc1 16.Bc1; repPos=3: 10...Bb4+ 12...Bb4+ 14...Bb4+; repPos=3: 11.Bd2 13.Bd2 15.Bd2; repPos=3: 11...Bf8 13...Bf8 15...Bf8",
        "", 37, 13, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "2b1kbnr/Q4p2/2pq4/3ppP2/1n1PP3/5B2/1PP1N3/RNBQK1r1 w Qk - 0 30"));

    list.add(new PgnTestCase("21_threefold_max_position_repetition_castling_end_with_first_threefold.pgn",
        "repPos=3: 19...Rh8 21...Rh8 23...Nb8", "", -1, 40, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "rnbqkbnr/1ppp1pp1/8/p3p2p/P3P2P/8/1PPP1PP1/RNBQKBNR w - - 40 24"));

    list.add(new PgnTestCase(
        "22_threefold_max_position_repetition_castling_and_en_passant_capture_end_with_first_threefold.pgn",
        "repPos=3: 22...Rh8 24...Bc8 26...Nb8", "", -1, 44, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "rnbqkbnr/2p1ppp1/p6p/1p1pP3/8/P6P/1PPP1PP1/RNBQKBNR w - - 44 27"));

    list.add(new PgnTestCase("24_threefold_max_visual_position_repetition_not_varying_end_with_first_threefold.pgn",
        "repPos=3: 40...Bc8 42...Bc8 49...Bc8", "", -1, 90, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "rnbqkbnr/1pp1ppp1/8/p2pP2p/P6P/8/1PPP1PP1/RNBQKBNR w - - 90 50"));
    list.add(new PgnTestCase("25_threefold_max_visual_position_repetition_varying_end_with_first_threefold.pgn",
        "repPos=3: 40...Bc8 42...Bc8 49...Bc8", "", -1, 90, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "rnbqkbnr/1pp1ppp1/8/p2pP2p/P6P/8/1PPP1PP1/RNBQKBNR w - - 90 50"));

    list.add(new PgnTestCase("26_threefold_two_threefolds_not_varying_end_with_threefold.pgn",
        "repPos=3: 40.Rh1 42.Bf1 49.Bf1; repPos=3: 40...Bc8 42...Bc8 49...Bc8", "", -1, 90, CheckmateOrStalemate.NA, 3,
        InsufficientMaterial.NONE, "rnbqkbnr/1pp1ppp1/8/p2pP2p/P6P/8/1PPP1PP1/RNBQKBNR w - - 90 50"));
    list.add(new PgnTestCase("26_threefold_two_threefolds_varying_end_with_threefold.pgn",
        "repPos=3: 40.Rh1 42.Qd1 49.Qd1; repPos=3: 40...Bc8 42...Bc8 49...Bc8", "", -1, 90, CheckmateOrStalemate.NA, 3,
        InsufficientMaterial.NONE, "rnbqkbnr/1pp1ppp1/8/p2pP2p/P6P/8/1PPP1PP1/RNBQKBNR w - - 90 50"));

    list.add(new PgnTestCase("28_threefold_castling_right_lost_position_allows_castling_end_with_first_threefold.pgn",
        "repPos=3: 5...Nb8 7...Nb8 9...Bc5", "", -1, 16, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "rnbqk2r/pppp1ppp/5n2/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w kq - 16 10"));
    list.add(
        new PgnTestCase("29_threefold_castling_right_lost_position_disallows_castling_end_with_first_threefold.pgn",
            "repPos=3: 3...Nb8 5...Nb8 7...Ng8", "", -1, 12, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
            "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w kq - 12 8"));
    list.add(new PgnTestCase("30_threefold_castling_white_both_sides_lost_end_with_first_threefold.pgn",
        "repPos=3: 5...Ke8 7...Nb8 9...Nb8", "", -1, 16, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w - - 16 10"));
    list.add(new PgnTestCase("31_threefold_castling_white_king_side_lost_end_with_first_threefold.pgn",
        "repPos=3: 5...Ng8 7...Ng8 9...Ng8", "", -1, 16, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "rnbqkbnr/ppppppp1/8/7p/7P/8/PPPPPPP1/RNBQKBNR w Qkq - 16 10"));
    list.add(new PgnTestCase("32_threefold_castling_white_queen_side_lost_end_with_first_threefold.pgn",
        "repPos=3: 5...Nb8 7...Nb8 9...Nb8", "", -1, 16, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "rnbqkbnr/1ppppppp/8/p7/P7/8/1PPPPPPP/RNBQKBNR w Kkq - 16 10"));

    list.add(new PgnTestCase("33_threefold_two_square_advance_no_pawn_same_rank_one_before_first_threefold.pgn", "", "",
        -1, 7, CheckmateOrStalemate.NA, 2, InsufficientMaterial.NONE,
        "rnbqkb1r/pp2pppp/8/2pp4/4n3/2N1PN2/PPPP1PPP/R1BQKB1R b KQkq - 7 7"));
    list.add(new PgnTestCase("34_threefold_two_square_advance_no_pawn_same_rank_end_with_first_threefold.pgn",
        "repPos=3: 3...d5 5...Nf6 7...Nf6", "", -1, 8, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "rnbqkb1r/pp2pppp/5n2/2pp4/8/2N1PN2/PPPP1PPP/R1BQKB1R w KQkq - 8 8"));
    list.add(new PgnTestCase("35_threefold_two_square_advance_no_pawn_same_rank_beyond.pgn",
        "repPos=3: 3...d5 5...Nf6 7...Nf6", "", -1, 8, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "rn1qkb1r/pp1bppp1/5n2/1Bpp3p/P7/2N1PN2/1PPP1PPP/R1BQK2R w KQkq - 2 10"));

    list.add(
        new PgnTestCase("36_threefold_en_passant_capture_situation_capture_allowed_yes_end_with_first_threefold.pgn",
            "repPos=3: 4...Ng8 6...Ng8 8...Bc8", "", -1, 12, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
            "r1bqkbnr/ppp1pppp/2n5/3pP3/8/8/PPPP1PPP/RNBQKBNR w KQkq - 12 9"));

    list.add(new PgnTestCase(
        "38_threefold_en_passant_capture_situation_capture_allowed_no_leaving_own_king_in_check_mine_end_with_first_threefold.pgn",
        "repPos=3: 6...d5+ 8...Qe8+ 10...Qe8+", "", -1, 8, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "rnb1qbnr/ppp1kppp/4p3/1K1pP3/8/8/PPPP1PPP/RNBQ1BNR w - - 8 11"));

    list.add(new PgnTestCase(
        "39_threefold_en_passant_capture_situation_capture_allowed_no_exposing_own_king_to_check_from_forum_end_with_first_threefold.pgn",
        "repPos=4: 32.f4 34.Kg1 36.Kg1 38.Kg1", "", 12, 12, CheckmateOrStalemate.NA, 4, InsufficientMaterial.NONE,
        "6k1/8/8/8/5Pp1/8/6R1/6K1 b - - 12 38"));
    list.add(new PgnTestCase(
        "40_threefold_en_passant_capture_situation_capture_allowed_no_leaving_own_king_in_check_from_forum_end_with_first_threefold.pgn",
        "repPos=4: 37.d4+ 39.Ra2+ 41.Ra2+ 43.Ra2+", "", 4, 12, CheckmateOrStalemate.NA, 4, InsufficientMaterial.NONE,
        "8/8/8/8/3Pp3/8/R6k/K7 b - - 12 43"));

    return new PgnTestCaseList(PgnTest.BASIC_THREEFOLD, list);
  }

  private static PgnTestCaseList createTestCasesBasicFifty() {
    final List<PgnTestCase> list = new ArrayList<>();
    list.add(new PgnTestCase("01_fifty_end_with_half_move_clock_99.pgn", "", "", -1, 99, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "1r1nk2r/2pb1pp1/1p5Q/p2ppnbp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 b - - 99 56"));
    list.add(new PgnTestCase("02_fifty_end_with_half_move_clock_100.pgn", "", "7.Ba6 (1) 56...Nd6 (100)", -1, 100,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp1bp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 w - - 100 57"));
    list.add(new PgnTestCase("03_fifty_half_move_clock_99_then_checkmate_by_white_with_capture.pgn", "", "", 6, 99,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE,
        "1rbqkb1r/pp1p1Qpp/2p5/3Bp3/PR1NP3/Bn2N3/2PP1PPP/5RK1 b k - 0 57"));
    list.add(new PgnTestCase("04_fifty_half_move_clock_99_then_checkmate_by_black_with_capture.pgn", "", "", 3, 99,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE,
        "4rrk1/pppbnppp/8/8/1n1N4/4bN2/PP1PPqPP/R1BQKB1R w - - 0 54"));
    list.add(new PgnTestCase("05_fifty_half_move_clock_99_then_checkmate_by_white_without_capture_or_pawn_move.pgn", "",
        "7...Rb8 (1) 57.Qe7# (100)", 6, 100, CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE,
        "1rbnk2r/pp1pQppp/1bp5/q2Bp3/PBRNP3/4N3/2PP1PPP/2R3K1 b k - 100 57"));
    list.add(new PgnTestCase("06_fifty_half_move_clock_99_then_checkmate_by_black_without_capture_or_pawn_move.pgn", "",
        "4.Qc2 (1) 53...Qd1# (100)", 3, 100, CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE,
        "rk6/ppp1Nppp/3r1n2/8/bn1Q4/4bN2/PP1PPPPP/R1BqKB1R w KQ - 100 54"));
    list.add(new PgnTestCase("07_fifty_half_move_clock_99_then_stalemate_by_white_move.pgn", "",
        "32...Kb8 (1) 82.Qb6 (100)", 3, 100, CheckmateOrStalemate.STALEMATE, 1, InsufficientMaterial.NONE,
        "k7/2R5/1Q3N2/5pP1/5P2/8/PPP1P3/K7 b - - 100 82"));
    list.add(new PgnTestCase("08_fifty_half_move_clock_99_then_stalemate_by_black_move.pgn", "",
        "24.Ke3 (1) 73...Qc2 (100)", 5, 100, CheckmateOrStalemate.STALEMATE, 1, InsufficientMaterial.NONE,
        "8/8/1p1p4/p1pPp1p1/P1P1P3/8/2q4r/K2k4 w - - 100 74"));
    list.add(new PgnTestCase("09_fifty_bishop_walk_end_with_half_move_clock_110.pgn", "",
        "3.Na3 (1) 52...Nb3 (100) 57...Bc8 (110)", -1, 110, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "r1bqkbnr/ppp2ppp/1N6/3pp3/3PP3/1n6/PPP2PPP/R1BQKBNR w KQkq - 110 58"));
    list.add(new PgnTestCase("10_fifty_half_move_clock_99_fifty_moves_claimable_yes.pgn", "", "", -1, 99,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "r2qkbnr/pppb1ppp/1N6/n2pp1Q1/3PPB2/8/PPP2PPP/R3KBNR b KQkq - 99 52"));
    list.add(new PgnTestCase("11_fifty_half_move_clock_99_fifty_moves_claimable_no_forced_capture.pgn", "", "", -1, 99,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "r2qkbnr/pppbQppp/1N6/n2pp3/3PP3/4B3/PPP2PPP/R3KBNR b KQkq - 99 52"));
    list.add(new PgnTestCase("12_fifty_half_move_clock_99_fifty_moves_claimable_no_forced_pawn_move.pgn", "", "", -1,
        99, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1rb3n1/pppq1pp1/2n1r3/2bpp2p/3PP1Qk/4K2N/PPP2PPP/RNBB3R b - - 99 53"));
    return new PgnTestCaseList(PgnTest.BASIC_FIFTY, list);
  }

  private static PgnTestCaseList createTestCasesBasicFivefold() {
    final List<PgnTestCase> list = new ArrayList<>();
    list.add(new PgnTestCase("01_fivefold_one_before_first_fivefold.pgn", "repPos=4: 5...a6 7...Qd8 9...Qd8 11...Qd8",
        "", -1, 12, CheckmateOrStalemate.NA, 4, InsufficientMaterial.NONE,
        "r1bqk1nr/1pp2ppp/p1np4/2b1p3/2B1P3/3P1N1P/PPP2PP1/RNBQK2R w KQkq - 12 12"));
    list.add(new PgnTestCase("02_fivefold_end_with_first_fivefold.pgn",
        "repPos=5: 5...a6 7...Qd8 9...Qd8 11...Qd8 13...Nc6", "", -1, 16, CheckmateOrStalemate.NA, 5,
        InsufficientMaterial.NONE, "r1bqk1nr/1pp2ppp/p1np4/2b1p3/2B1P3/3P1N1P/PPP2PP1/RNBQK2R w KQkq - 16 14"));
    list.add(new PgnTestCase("03_fivefold_one_before_first_fivefold.pgn",
        "repPos=4: 5...a6 7...Qd8 9...Qd8 11...Qd8; repPos=4: 6.Bg5 8.Bg5 10.Bg5 12.Bg5; repPos=4: 6...Qd7 8...Qd7 10...Qd7 12...Qd7; repPos=4: 7.Bc1 9.Bc1 11.Bc1 13.Bc1",
        "", -1, 15, CheckmateOrStalemate.NA, 4, InsufficientMaterial.NONE,
        "r1b1k1nr/1ppq1ppp/p1np4/2b1p3/2B1P3/3P1N1P/PPP2PP1/RNBQK2R b KQkq - 15 13"));

    final var dyamicPositionRepetition = "repPos=5: 5...a6 7...Qd8 9...Qd8 11...Qd8 13...Qd8; repPos=4: 6.Bg5 8.Bg5 10.Bg5 12.Bg5; repPos=4: 6...Qd7 8...Qd7 10...Qd7 12...Qd7; repPos=4: 7.Bc1 9.Bc1 11.Bc1 13.Bc1";

    list.add(new PgnTestCase("04_fivefold_end_with_first_fivefold.pgn", dyamicPositionRepetition, "", -1, 16,
        CheckmateOrStalemate.NA, 5, InsufficientMaterial.NONE,
        "r1bqk1nr/1pp2ppp/p1np4/2b1p3/2B1P3/3P1N1P/PPP2PP1/RNBQK2R w KQkq - 16 14"));
    list.add(new PgnTestCase("07_fivefold_max_position_repetitions_castling_end_with_first_fivefold.pgn",
        "repPos=5: 19...Rh8 21...Rh8 23...Nb8 25...Nb8 27...Ng8", "", -1, 48, CheckmateOrStalemate.NA, 5,
        InsufficientMaterial.NONE, "rnbqkbnr/1ppp1pp1/8/p3p2p/P3P2P/8/1PPP1PP1/RNBQKBNR w - - 48 28"));

    list.add(new PgnTestCase(
        "08_fivefold_max_position_repetitions_castling_and_en_passant_capture_end_with_first_fivefold.pgn",
        "repPos=5: 22...Rh8 24...Bc8 26...Nb8 28...Ke8 30...Qd8", "", -1, 52, CheckmateOrStalemate.NA, 5,
        InsufficientMaterial.NONE, "rnbqkbnr/2p1ppp1/p6p/1p1pP3/8/P6P/1PPP1PP1/RNBQKBNR w - - 52 31"));

    return new PgnTestCaseList(PgnTest.BASIC_FIVEFOLD, list);
  }

  private static PgnTestCaseList createTestCasesBasicSeventyFive() {
    final List<PgnTestCase> list = new ArrayList<>();
    list.add(new PgnTestCase("01_seventy_five_end_with_half_move_clock_149.pgn", "",
        "7.Ba6 (1) 56...Nd6 (100) 81.Rb3 (149)", -1, 149, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1n2r1/2pk1ppR/1pbn4/p2pp2p/P1B1P2P/1RKP1Q1N/1PP1NPP1/1qB3b1 b - - 149 81"));
    list.add(new PgnTestCase("02_seventy_five_end_with_half_move_clock_150.pgn", "",
        "7.Ba6 (1) 56...Nd6 (100) 81...Kc8 (150)", -1, 150, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1rkn2r1/2p2ppR/1pbn4/p2pp2p/P1B1P2P/1RKP1Q1N/1PP1NPP1/1qB3b1 w - - 150 82"));
    list.add(new PgnTestCase("11_seventy_five_half_move_clock_149_then_checkmate_by_white_with_capture.pgn", "",
        "4...Be7 (1) 54.Nf6 (100) 78...Qc4 (149)", 5, 149, CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE,
        "5rrk/ppp2ppQ/3n1N2/2bbp3/n1qPB3/B1P5/PPN2PPP/1KR4R b - - 0 79"));
    list.add(new PgnTestCase("12_seventy_five_half_move_clock_149_then_checkmate_by_black_with_capture.pgn", "",
        "4.Kd2 (1) 53...Qe6 (100) 78.Nb6 (149)", 156, 149, CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE,
        "2b4r/ppp1ppb1/kN1r3p/3p2p1/P2P2n1/3n1QP1/1PPBPP1q/R3NBRK w - - 0 79"));
    list.add(
        new PgnTestCase("13_seventy_five_half_move_clock_149_then_checkmate_by_white_without_capture_or_pawn_move.pgn",
            "", "4...Be7 (1) 54.Nf6 (100) 79.Qe8# (150)", 5, 150, CheckmateOrStalemate.CHECKMATE, 1,
            InsufficientMaterial.NONE, "4Q1k1/ppp2ppp/1b4rr/1n1bp3/1BqPB1N1/2P3n1/PPN2PPP/1KR4R b - - 150 79"));
    list.add(
        new PgnTestCase("14_seventy_five_half_move_clock_149_then_checkmate_by_black_without_capture_or_pawn_move.pgn",
            "", "4.Kd2 (1) 53...Qe6 (100) 78...Qg4# (150)", -1, 150, CheckmateOrStalemate.CHECKMATE, 1,
            InsufficientMaterial.NONE, "3r1n1b/pppbpp2/k1r4p/3pQ1pK/Pn1P2q1/RNN3P1/1PPBPPBP/6R1 w - - 150 79"));

    list.add(new PgnTestCase("15_seventy_five_half_move_clock_149_then_stalemate_by_white_move.pgn", "",
        "27...Kf6 (1) 77.Kc6 (100) 102.Qh5 (150)", 5, 150, CheckmateOrStalemate.STALEMATE, 1, InsufficientMaterial.NONE,
        "8/4K3/1NB4P/p1pp3Q/PpP1k1p1/1P3pP1/1B3P2/3R2NR b - - 150 102"));

    list.add(new PgnTestCase("16_seventy_five_half_move_clock_149_then_stalemate_by_black_move.pgn", "",
        "22.Kg2 (1) 71...Qd3 (100) 96...Qf2 (150)", 6, 150, CheckmateOrStalemate.STALEMATE, 1,
        InsufficientMaterial.NONE, "1r3n1r/kbp1n2p/1p2p2b/pP1pP1p1/P2P1pP1/5P2/5q2/7K w - - 150 97"));
    return new PgnTestCaseList(PgnTest.BASIC_SEVENTY_FIVE, list);
  }

  private static PgnTestCaseList createTestCasesBasicIntervening() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_intervening_threefold_encapsulating_threefold.pgn",
        "repPos=3: 1...e5 3...Qd8 9...Ng8; repPos=3: 4...Nh6 6...Nh6 8...Nh6", "", -1, 16, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbqkbnr/1ppp1ppp/p7/4p3/4P3/P7/1PPP1PPP/RNBQKBNR w KQkq - 0 11"));

    list.add(new PgnTestCase("03_intervening_fivefold_encapsulating_threefold.pgn",
        "repPos=5: 2...g5 4...Ng8 6...Ng8 8...Bf8 16...Nb8; repPos=3: 10...Ne5 12...Ne5 14...Ne5", "", -1, 28,
        CheckmateOrStalemate.NA, 5, InsufficientMaterial.NONE,
        "rnbqkbnr/ppppp2p/8/5pp1/5PP1/8/PPPPP2P/RNBQKBNR w KQkq - 28 17"));

    list.add(new PgnTestCase("05_intervening_threefold_interlocked_threefold.pgn",
        "repPos=3: 2...g6 4...Bf8 8...Bf8; repPos=3: 5...Bh6 7...Bh6 9...Bh6", "", -1, 16, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r1bqk1nr/p1pppp1p/1pn3pb/8/8/1PN3PB/P1PPPP1P/R1BQK1NR w KQkq - 16 11"));

    list.add(new PgnTestCase("06_intervening_threefold_interlocked_fivefold.pgn",
        "repPos=3: 2...e5 4...Bc8 12...Bf8; repPos=5: 5...Bc5 7...Bc5 9...Bc5 11...Bc5 14...Bc5", "", -1, 24,
        CheckmateOrStalemate.NA, 5, InsufficientMaterial.NONE,
        "rnbqk1nr/p1pp1ppp/8/1pb1p3/1PB1P3/8/P1PP1PPP/RNBQK1NR w KQkq - 24 15"));

    return new PgnTestCaseList(PgnTest.BASIC_INTERVENING, list);
  }

  private static PgnTestCaseList createTestCasesBasicDoubleDraw() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_double_draw_threefold_and_fifty_move.pgn", "repPos=3: 4...g6 6...Bc8 54...Qd8",
        "5.Bb2 (1) 54...Qd8 (100)", -1, 100, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "rnbqkbnr/p1p2p1p/1p4p1/3pp3/3PP3/1P4P1/P1P2P1P/RNBQKBNR w KQkq - 100 55"));
    list.add(new PgnTestCase("02_double_draw_threefold_and_seventy_five_move.pgn", "repPos=3: 4...g6 6...Bc8 79...Qd8",
        "5.Bb2 (1) 54...Nf6 (100) 79...Qd8 (150)", -1, 150, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "rnbqkbnr/p1p2p1p/1p4p1/3pp3/3PP3/1P4P1/P1P2P1P/RNBQKBNR w KQkq - 150 80"));
    list.add(new PgnTestCase("03_double_draw_fivefold_and_fifty_move.pgn",
        "repPos=5: 4...g6 6...Bc8 49...Qd8 51...Qd8 54...Qd8", "5.Bb2 (1) 54...Qd8 (100)", -1, 100,
        CheckmateOrStalemate.NA, 5, InsufficientMaterial.NONE,
        "rnbqkbnr/p1p2p1p/1p4p1/3pp3/3PP3/1P4P1/P1P2P1P/RNBQKBNR w KQkq - 100 55"));
    list.add(new PgnTestCase("04_double_draw_fivefold_and_seventy_five_move.pgn",
        "repPos=5: 4...g6 6...Bc8 71...Qd8 73...Nb8 79...Bf8", "5.Bb2 (1) 54...Nf6 (100) 79...Bf8 (150)", -1, 150,
        CheckmateOrStalemate.NA, 5, InsufficientMaterial.NONE,
        "rnbqkbnr/p1p2p1p/1p4p1/3pp3/3PP3/1P4P1/P1P2P1P/RNBQKBNR w KQkq - 150 80"));

    return new PgnTestCaseList(PgnTest.BASIC_DOUBLE_DRAW, list);

  }

  private static PgnTestCaseList createTestCasesBasicCastlingWhite() {
    final List<PgnTestCase> list = new ArrayList<>();
    list.add(new PgnTestCase("01_white_castling_king_side.pgn", "", "", -1, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r1bqk1nr/pppp1ppp/2n5/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQ1RK1 b kq - 5 4"));
    list.add(new PgnTestCase("02_white_castling_queen_side.pgn", "", "", -1, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r2qkbnr/ppp2ppp/2n1p3/3p1b2/3P1B2/2NQ4/PPP1PPPP/2KR1BNR b kq - 1 5"));
    return new PgnTestCaseList(PgnTest.BASIC_CASTLING_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesBasicCastlingBlack() {
    final List<PgnTestCase> list = new ArrayList<>();
    list.add(new PgnTestCase("01_black_castling_king_side.pgn", "", "", -1, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r1bq1rk1/pppp1ppp/2n2n2/2b1p3/2B1P3/3P1N1P/PPP2PP1/RNBQK2R w KQ - 1 6"));
    list.add(new PgnTestCase("02_black_castling_queen_side.pgn", "", "", -1, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "2kr1bnr/pppq1ppp/2n1p3/1B1p1b2/3P1B2/2N1PN2/PPP2PPP/R2QK2R w KQ - 4 7"));
    return new PgnTestCaseList(PgnTest.BASIC_CASTLING_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesBasicCastlingSpecialWhite() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_white_castling_special_kingside_check.pgn", "", "", 6, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbq1b1r/pppppkpp/8/PB5n/4p3/7N/1PPP2PP/RNBQ1RK1 b - - 2 8"));
    list.add(new PgnTestCase("02_white_castling_special_kingside_checkmate.pgn", "", "", 30, 17,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE,
        "rnbq1bnr/pppp1ppp/8/Q7/3PP3/1PBB1N1N/P1P2PPP/1k3RK1 b - - 6 21"));
    list.add(new PgnTestCase("03_white_castling_special_kingside_fifty_move.pgn", "", "18...Nec6 (1) 68.O-O (100)", 3,
        100, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "2Qqb3/5k1r/2B3p1/2NPBp2/P3n1p1/N7/1n5r/R4RK1 b - - 100 68"));
    list.add(new PgnTestCase("04_white_castling_special_kingside_seventy_five_move.pgn", "",
        "18...Nec6 (1) 68.Nc4 (100) 93.O-O (150)", 3, 150, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "B7/1bn1k1r1/4N1p1/3PBp2/P1N3p1/2q5/1n1Q3r/R4RK1 b - - 150 93"));
    list.add(new PgnTestCase("05_white_castling_special_kingside_stalemate.pgn", "", "", 3, 7,
        CheckmateOrStalemate.STALEMATE, 1, InsufficientMaterial.NONE,
        "8/8/8/p5N1/P1PP4/R2Q2P1/1k3PBP/5RK1 b - - 6 36"));
    list.add(new PgnTestCase("06_white_castling_special_queenside_check.pgn", "", "", 8, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbq3r/pppk1pp1/4pn1p/7Q/5B2/5N2/PPP1PPPP/2KR1BNR b - - 3 8"));
    list.add(new PgnTestCase("07_white_castling_special_queenside_checkmate.pgn", "", "", 16, 6,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE,
        "rnb1qbnr/ppppp1pp/5p2/1B4N1/3PPBPP/2N2P2/PPPQ4/2KR3k b - - 2 14"));
    list.add(new PgnTestCase("08_white_castling_special_queenside_fifty_move.pgn", "", "16...Kf7 (1) 66.O-O-O (100)", 7,
        100, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "2b4Q/2qnk3/1Q3r2/p1p3b1/PpP2Pp1/1N2PNPp/1B2B2P/2KR3R b - - 100 66"));
    list.add(new PgnTestCase("09_white_castling_special_queenside_seventy_five_move.pgn", "",
        "16...Kf7 (1) 66.Qh7+ (100) 91.O-O-O (150)", 7, 150, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "6Q1/3bk1r1/1q3n2/p1p1b1Q1/PpP2Pp1/1N2PNPp/1B2B2P/2KR3R b - - 150 91"));
    list.add(new PgnTestCase("10_white_castling_special_queenside_stalemate.pgn", "", "", 18, 7,
        CheckmateOrStalemate.STALEMATE, 1, InsufficientMaterial.NONE,
        "8/8/4B1P1/pP6/P1p1P1PP/2N2Q2/1BPP3k/2KR4 b - - 2 35"));

    return new PgnTestCaseList(PgnTest.BASIC_CASTLING_SPECIAL_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesBasicCastlingSpecialBlack() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_black_castling_special_kingside_check.pgn", "", "", 3, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rnbq1rk1/ppppb2p/7n/8/8/8/PPPPPKPP/RNBQ1BNR w - - 2 6"));
    list.add(new PgnTestCase("02_black_castling_special_kingside_checkmate.pgn", "", "", 15, 6,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE,
        "K4rk1/p1pppp1p/6p1/np1n4/3b4/8/PPP1PPPP/RNBQ1BNR w - - 2 15"));
    list.add(new PgnTestCase("03_black_castling_special_kingside_fifty_move.pgn", "", "17.Rg5 (1) 66...O-O (100)", 5,
        100, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "5rk1/1Q2n3/R6p/8/1b1B1p1K/1P6/P1P5/RN1Q1BNb w - - 100 67"));
    list.add(new PgnTestCase("04_black_castling_special_kingside_seventy_five_move.pgn", "",
        "17.Rg5 (1) 66...Bc3 (100) 91...O-O (150)", 5, 150, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "5rk1/1Q2n3/R6p/1N6/3B1p1K/1Pb2b2/P1P5/R2Q1BN1 w - - 150 92"));
    list.add(new PgnTestCase("05_black_castling_special_kingside_stalemate.pgn", "", "", 6, 4,
        CheckmateOrStalemate.STALEMATE, 1, InsufficientMaterial.NONE,
        "5rk1/K2n1pb1/2qp3p/pp2p1pP/6P1/8/8/8 w - - 2 31"));
    list.add(new PgnTestCase("06_black_castling_special_queenside_check.pgn", "", "", 3, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "2kr1bnr/ppq2pp1/2n4p/5b2/5B2/2N5/PPPKPPPP/R2Q1BNR w - - 2 8"));
    list.add(new PgnTestCase("07_black_castling_special_queenside_checkmate.pgn", "", "", 15, 3,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE,
        "2kr3K/pbppq2p/1pn2p2/4p3/5Pn1/8/PPPPP1PP/RNBQ1BNR w - - 1 12"));
    list.add(new PgnTestCase("08_black_castling_special_queenside_fifty_move.pgn", "", "10.Nh3 (1) 59...O-O-O (100)", 3,
        100, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "2kr3r/q2b1p1p/2n2npB/p7/4P3/bQN3PN/1P3P1P/R3KB1R w K - 100 60"));
    list.add(new PgnTestCase("09_black_castling_special_queenside_seventy_five_move.pgn", "",
        "10.Nh3 (1) 59...Rg8 (100) 84...O-O-O (150)", 3, 150, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "2kr4/3b1prp/1qn2npB/p7/4P3/b1N3PN/QP3PBP/R3K1R1 w - - 150 85"));
    list.add(new PgnTestCase("10_black_castling_special_queenside_stalemate.pgn", "", "", 4, 15,
        CheckmateOrStalemate.STALEMATE, 1, InsufficientMaterial.NONE,
        "2kr4/1bpp3K/1p3q2/pP2pP2/P5n1/3n4/8/8 w - - 15 42"));

    return new PgnTestCaseList(PgnTest.BASIC_CASTLING_SPECIAL_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesBasicForced() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_forced_checkmate.pgn", "", "", 9, 16, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "7k/q5Q1/p4PPK/6PP/8/5P2/8/8 b - - 16 40"));
    list.add(new PgnTestCase("02_forced_checkmate_played.pgn", "", "", 9, 16, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "6k1/5PP1/6PK/6PP/8/p7/8/8 b - - 0 45"));

    list.add(new PgnTestCase("03_forced_insufficient_material.pgn", "", "", 5, 23, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "r7/K7/2k5/8/8/8/8/8 w - - 11 43"));
    list.add(new PgnTestCase("04_forced_insufficient_material_played.pgn", "", "", 5, 23, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BOTH, "K7/8/2k5/8/8/8/8/8 b - - 0 43"));

    list.add(new PgnTestCase("05_forced_stalemate.pgn", "", "", 5, 23, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "1r4k1/6Q1/8/8/8/q7/P7/K7 b - - 4 36"));
    list.add(new PgnTestCase("06_forced_stalemate_played.pgn", "", "", 5, 23, CheckmateOrStalemate.STALEMATE, 1,
        InsufficientMaterial.NONE, "1r6/6k1/8/8/8/q7/P7/K7 w - - 0 37"));

    list.add(new PgnTestCase("07_forced_stalemate.pgn", "", "", 8, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "6kr/1p1N1p1p/1P3P1P/P6P/3q1RQr/7p/4pN1P/4B1KR b - - 2 41"));
    list.add(new PgnTestCase("07_forced_stalemate_played.pgn", "", "", 8, 8, CheckmateOrStalemate.STALEMATE, 1,
        InsufficientMaterial.NONE, "6kr/1p1N1p1p/1P3P1P/P6P/6N1/7p/4p2P/4B1KR b - - 0 43"));

    return new PgnTestCaseList(PgnTest.BASIC_FORCED, list);
  }

  private static PgnTestCaseList createTestCasesParserFromFen() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("from_fen_capture_first_move_half_move_clock_100_black_to_move.pgn", "",
        "50...NA (1) 100.NA (100)", 1, 100, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "k7/2R1Q3/5N2/5pP1/5P2/8/PPP1P3/K7 w - - 0 101"));
    list.add(new PgnTestCase("from_fen_capture_first_move_half_move_clock_100_white_to_move.pgn", "",
        "50.NA (1) 99...NA (100)", 1, 100, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp1Np/P3P2P/1R1PR3/1PP2PP1/qBB1K1N1 b - - 0 100"));
    list.add(new PgnTestCase("from_fen_capture_first_move_half_move_clock_101_black_to_move.pgn", "",
        "50.NA (1) 99...NA (100) 100.NA (101)", 1, 101, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1nk2r/2p2pp1/1p1n3Q/p2pp1bp/P3P2P/1R1PR2b/1PP2PP1/qBB1K1N1 w - - 0 101"));
    list.add(new PgnTestCase("from_fen_capture_first_move_half_move_clock_101_white_to_move.pgn", "",
        "49...NA (1) 99.NA (100) 99...NA (101)", 1, 101, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1nk2r/2pb1pp1/1R1n3Q/p2pp1bp/P3P2P/3PR2N/1PP2PP1/qBB1K1N1 b - - 0 100"));
    list.add(new PgnTestCase("from_fen_capture_first_move_half_move_clock_149_black_to_move.pgn", "",
        "26.NA (1) 75...NA (100) 100.NA (149)", 1, 149, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1nk2r/2pb1pp1/1p5Q/p2ppnbp/q3P2P/1R1PR2N/1PP2PP1/1BB1K1N1 w - - 0 101"));
    list.add(new PgnTestCase("from_fen_capture_first_move_half_move_clock_149_white_to_move.pgn", "",
        "25...NA (1) 75.NA (100) 99...NA (149)", 1, 149, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1rbqkb1r/pp1p1ppp/2p5/3Bp3/PR2P3/BN2NQ2/2PP1PPP/5RK1 b k - 0 100"));
    list.add(new PgnTestCase("from_fen_capture_first_move_half_move_clock_099_black_to_move.pgn", "", "", 1, 99,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1nk2r/2pb1pp1/1p5Q/p2ppnbp/P3P2P/1R1PR2N/1qP2PP1/1BB1K1N1 w - - 0 101"));
    list.add(new PgnTestCase("from_fen_capture_first_move_half_move_clock_099_white_to_move.pgn", "", "", 1, 99,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1rbqkb1r/pR1p1ppp/2p5/3Bp3/P2NP3/Bn2NQ2/2PP1PPP/5RK1 b k - 0 100"));
    list.add(new PgnTestCase("from_fen_capture_second_move_half_move_clock_100_black_to_move.pgn", "",
        "50...NA (1) 100.NA (100) 100...Kb8 (101)", 2, 101, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1k6/2R5/5N2/5pP1/5P2/Q7/PPP1P3/K7 b - - 0 101"));
    list.add(new PgnTestCase("from_fen_capture_second_move_half_move_clock_100_white_to_move.pgn", "",
        "50.NA (1) 99...NA (100) 100.Qe6+ (101)", 2, 101, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1nk2r/2p2pp1/1p1nb3/p2pp1bp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 w - - 0 101"));
    list.add(new PgnTestCase("from_fen_capture_second_move_half_move_clock_101_black_to_move.pgn", "",
        "50.NA (1) 99...NA (100) 100...Qa3 (102)", 2, 102, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1nk2r/2pb1pQ1/1p1n4/p2pp1bp/P3P2P/qR1PR2N/1PP2PP1/1BB1K1N1 b - - 0 101"));
    list.add(new PgnTestCase("from_fen_capture_second_move_half_move_clock_101_white_to_move.pgn", "",
        "49...NA (1) 99.NA (100) 100.Kf1 (102)", 2, 102, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp2p/P3P2P/1R1Pb2N/1PP2PP1/qBB2KN1 w - - 0 101"));
    list.add(new PgnTestCase("from_fen_capture_second_move_half_move_clock_099_black_to_move.pgn", "",
        "51.NA (1) 100...Qa3 (100)", 2, 100, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1nk2r/2pb1pp1/1p5Q/p2ppnbp/P3P2P/R2PR2N/1PP2PP1/1BB1K1N1 b - - 0 101"));
    list.add(new PgnTestCase("from_fen_capture_second_move_half_move_clock_099_white_to_move.pgn", "",
        "50...NA (1) 100.Qf6 (100)", 2, 100, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1rbqkb1r/pp1p1p1p/2p2p2/3Bp3/PR1NP3/Bn2N3/2PP1PPP/5RK1 w k - 0 101"));
    list.add(new PgnTestCase("from_fen_no_move_half_move_clock_100_black_to_move.pgn", "", "50...NA (1) 100.NA (100)",
        -1, 100, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "k7/2R1Q3/5N2/5pP1/5P2/8/PPP1P3/K7 b - - 100 100"));
    list.add(new PgnTestCase("from_fen_no_move_half_move_clock_100_white_to_move.pgn", "", "50.NA (1) 99...NA (100)",
        -1, 100, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp1bp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 w - - 100 100"));
    list.add(new PgnTestCase("from_fen_no_move_half_move_clock_101_black_to_move.pgn", "",
        "50.NA (1) 99...NA (100) 100.NA (101)", -1, 101, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp1bp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 b - - 101 100"));
    list.add(new PgnTestCase("from_fen_no_move_half_move_clock_101_white_to_move.pgn", "",
        "49...NA (1) 99.NA (100) 99...NA (101)", -1, 101, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp1bp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 w - - 101 100"));
    list.add(new PgnTestCase("from_fen_no_move_half_move_clock_149_black_to_move.pgn", "",
        "26.NA (1) 75...NA (100) 100.NA (149)", -1, 149, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1nk2r/2pb1pp1/1p5Q/p2ppnbp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 b - - 149 100"));
    list.add(new PgnTestCase("from_fen_no_move_half_move_clock_149_white_to_move.pgn", "",
        "25...NA (1) 75.NA (100) 99...NA (149)", -1, 149, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1rbqkb1r/pp1p1ppp/2p5/3Bp3/PR1NP3/Bn2NQ2/2PP1PPP/5RK1 w k - 149 100"));
    list.add(new PgnTestCase("from_fen_no_move_half_move_clock_150_black_to_move.pgn", "",
        "25...NA (1) 75.NA (100) 100.NA (150)", -1, 150, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "k7/2R1Q3/5N2/5pP1/5P2/8/PPP1P3/K7 b - - 150 100"));
    list.add(new PgnTestCase("from_fen_no_move_half_move_clock_150_white_to_move.pgn", "",
        "25.NA (1) 74...NA (100) 99...NA (150)", -1, 150, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp1bp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 w - - 150 100"));
    list.add(new PgnTestCase("from_fen_no_move_half_move_clock_099_black_to_move.pgn", "", "", -1, 99,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1nk2r/2pb1pp1/1p5Q/p2ppnbp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 b - - 99 100"));
    list.add(new PgnTestCase("from_fen_no_move_half_move_clock_099_white_to_move.pgn", "", "", -1, 99,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1rbqkb1r/pp1p1ppp/2p5/3Bp3/PR1NP3/Bn2NQ2/2PP1PPP/5RK1 w k - 99 100"));
    list.add(new PgnTestCase("from_fen_one_move_half_move_clock_100_black_to_move.pgn", "",
        "50...NA (1) 100.NA (100) 100...Kb8 (101)", -1, 101, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1k6/2R1Q3/5N2/5pP1/5P2/8/PPP1P3/K7 w - - 101 101"));
    list.add(new PgnTestCase("from_fen_one_move_half_move_clock_100_white_to_move.pgn", "",
        "50.NA (1) 99...NA (100) 100.Qe6+ (101)", -1, 101, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1nk2r/2pb1pp1/1p1nQ3/p2pp1bp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 b - - 101 100"));
    list.add(new PgnTestCase("from_fen_one_move_half_move_clock_101_black_to_move.pgn", "",
        "50.NA (1) 99...NA (100) 100...Qa3 (102)", -1, 102, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp1bp/P3P2P/qR1PR2N/1PP2PP1/1BB1K1N1 w - - 102 101"));
    list.add(new PgnTestCase("from_fen_one_move_half_move_clock_101_white_to_move.pgn", "",
        "49...NA (1) 99.NA (100) 100.Kf1 (102)", -1, 102, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp1bp/P3P2P/1R1PR2N/1PP2PP1/qBB2KN1 b - - 102 100"));
    list.add(new PgnTestCase("from_fen_one_move_half_move_clock_149_black_to_move.pgn", "",
        "26.NA (1) 75...NA (100) 100...Rb7 (150)", -1, 150, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "3nk2r/1rpb1pp1/1p5Q/p2ppnbp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 w - - 150 101"));
    list.add(new PgnTestCase("from_fen_one_move_half_move_clock_149_white_to_move.pgn", "",
        "25...NA (1) 75.NA (100) 100.Kh1 (150)", -1, 150, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1rbqkb1r/pp1p1ppp/2p5/3Bp3/PR1NP3/Bn2NQ2/2PP1PPP/5R1K b k - 150 100"));
    list.add(new PgnTestCase("from_fen_one_move_half_move_clock_099_black_to_move.pgn", "", "51.NA (1) 100...Qa3 (100)",
        -1, 100, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1nk2r/2pb1pp1/1p5Q/p2ppnbp/P3P2P/qR1PR2N/1PP2PP1/1BB1K1N1 w - - 100 101"));
    list.add(new PgnTestCase("from_fen_one_move_half_move_clock_099_white_to_move.pgn", "", "50...NA (1) 100.Qf6 (100)",
        -1, 100, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1rbqkb1r/pp1p1ppp/2p2Q2/3Bp3/PR1NP3/Bn2N3/2PP1PPP/5RK1 b k - 100 100"));
    list.add(new PgnTestCase("from_fen_pawn_first_move_half_move_clock_100_black_to_move.pgn", "",
        "50...NA (1) 100.NA (100)", -1, 100, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "k7/2R1Q3/5N2/6P1/5p2/5P2/PPP1P3/K7 w - - 0 101"));
    list.add(new PgnTestCase("from_fen_pawn_first_move_half_move_clock_100_white_to_move.pgn", "",
        "50.NA (1) 99...NA (100)", -1, 100, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp1bp/P3P2P/1R1PR1PN/1PP2P2/qBB1K1N1 b - - 0 100"));
    list.add(new PgnTestCase("from_fen_pawn_first_move_half_move_clock_101_black_to_move.pgn", "",
        "50.NA (1) 99...NA (100) 100.NA (101)", -1, 101, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1nk2r/2pb2p1/1p1n3Q/p2pppbp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 w - f6 0 101"));
    list.add(new PgnTestCase("from_fen_pawn_first_move_half_move_clock_101_white_to_move.pgn", "",
        "49...NA (1) 99.NA (100) 99...NA (101)", -1, 101, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp1bp/P2PP2P/1R2R2N/1PP2PP1/qBB1K1N1 b - - 0 100"));
    list.add(new PgnTestCase("from_fen_pawn_first_move_half_move_clock_149_black_to_move.pgn", "",
        "26.NA (1) 75...NA (100) 100.NA (149)", -1, 149, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1nk2r/2pb1pp1/7Q/pp1ppnbp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 w - - 0 101"));
    list.add(new PgnTestCase("from_fen_pawn_first_move_half_move_clock_149_white_to_move.pgn", "",
        "25...NA (1) 75.NA (100) 99...NA (149)", -1, 149, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1rbqkb1r/pp1p1ppp/2p5/P2Bp3/1R1NP3/Bn2NQ2/2PP1PPP/5RK1 b k - 0 100"));
    list.add(new PgnTestCase("from_fen_pawn_first_move_half_move_clock_099_black_to_move.pgn", "", "", -1, 99,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1nk2r/2pb1pp1/7Q/pp1ppnbp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 w - - 0 101"));
    list.add(new PgnTestCase("from_fen_pawn_first_move_half_move_clock_099_white_to_move.pgn", "", "", -1, 99,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1rbqkb1r/pp1p1ppp/2p5/3Bp3/PR1NP2P/Bn2NQ2/2PP1PP1/5RK1 b k h3 0 100"));
    list.add(new PgnTestCase("from_fen_repetition_from_one_move_black_to_move_fivefold.pgn",
        "repPos=5: 17...Qd7 19...Nb8 21...Bf8 23...Qd7 25...Qd7", "", -1, 17, CheckmateOrStalemate.NA, 5,
        InsufficientMaterial.NONE, "1n2kb1r/p2q1ppp/8/4p1B1/4P3/8/PPP2PPP/2KR4 w k - 17 26"));
    list.add(new PgnTestCase("from_fen_repetition_from_one_move_black_to_move_fourfold.pgn",
        "repPos=4: 17...Qd7 19...Nb8 21...Bf8 23...Qd7", "", -1, 13, CheckmateOrStalemate.NA, 4,
        InsufficientMaterial.NONE, "1n2kb1r/p2q1ppp/8/4p1B1/4P3/8/PPP2PPP/2KR4 w k - 13 24"));
    list.add(new PgnTestCase("from_fen_repetition_from_one_move_black_to_move_threefold.pgn",
        "repPos=3: 17...Qd7 19...Nb8 21...Bf8", "", -1, 9, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "1n2kb1r/p2q1ppp/8/4p1B1/4P3/8/PPP2PPP/2KR4 w k - 9 22"));
    list.add(new PgnTestCase("from_fen_repetition_from_one_move_white_to_move_fivefold.pgn",
        "repPos=5: 17.Qa4 19.Qa4 21.Qa4 23.Qa4 25.Rf1", "", -1, 17, CheckmateOrStalemate.NA, 5,
        InsufficientMaterial.NONE, "r1b2r2/pp1pk1pp/8/7q/Q2pP1n1/5N1P/PP3PP1/3R1RK1 b - - 17 25"));
    list.add(new PgnTestCase("from_fen_repetition_from_one_move_white_to_move_fourfold.pgn",
        "repPos=4: 17.Qa4 19.Qa4 21.Qa4 23.Qa4", "", -1, 13, CheckmateOrStalemate.NA, 4, InsufficientMaterial.NONE,
        "r1b2r2/pp1pk1pp/8/7q/Q2pP1n1/5N1P/PP3PP1/3R1RK1 b - - 13 23"));
    list.add(new PgnTestCase("from_fen_repetition_from_one_move_white_to_move_threefold.pgn",
        "repPos=3: 17.Qa4 19.Qa4 21.Qa4", "", -1, 9, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "r1b2r2/pp1pk1pp/8/7q/Q2pP1n1/5N1P/PP3PP1/3R1RK1 b - - 9 21"));
    list.add(new PgnTestCase("from_fen_repetition_from_three_moves_black_to_move_fivefold.pgn",
        "repPos=5: 6...g3 8...Nb8 10...Bf8 12...Qh4 14...Qh4", "", -1, 16, CheckmateOrStalemate.NA, 5,
        InsufficientMaterial.NONE, "rnb1kbnr/pppp1p1p/8/4N3/2B1Pp1q/6p1/PPPP2PP/RNBQ1K1R w kq - 16 15"));
    list.add(new PgnTestCase("from_fen_repetition_from_three_moves_black_to_move_fourfold.pgn",
        "repPos=4: 6...g3 8...Nb8 10...Bf8 12...Qh4", "", -1, 12, CheckmateOrStalemate.NA, 4, InsufficientMaterial.NONE,
        "rnb1kbnr/pppp1p1p/8/4N3/2B1Pp1q/6p1/PPPP2PP/RNBQ1K1R w kq - 12 13"));
    list.add(new PgnTestCase("from_fen_repetition_from_three_moves_black_to_move_threefold.pgn",
        "repPos=3: 6...g3 8...Nb8 10...Bf8", "", -1, 8, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "rnb1kbnr/pppp1p1p/8/4N3/2B1Pp1q/6p1/PPPP2PP/RNBQ1K1R w kq - 8 11"));
    list.add(new PgnTestCase("from_fen_repetition_from_three_moves_white_to_move_fivefold.pgn",
        "repPos=5: 14.f4 16.Qc3 18.Nd2 20.Ra1 22.Kg1", "", -1, 16, CheckmateOrStalemate.NA, 5,
        InsufficientMaterial.NONE, "r4rk1/p1pq1ppp/1pn1p3/1b2P3/1Pp2Pn1/2Q3P1/PB1NP1BP/R4RK1 b - - 16 22"));
    list.add(new PgnTestCase("from_fen_repetition_from_three_moves_white_to_move_fourfold.pgn",
        "repPos=4: 14.f4 16.Qc3 18.Nd2 20.Ra1", "", -1, 12, CheckmateOrStalemate.NA, 4, InsufficientMaterial.NONE,
        "r4rk1/p1pq1ppp/1pn1p3/1b2P3/1Pp2Pn1/2Q3P1/PB1NP1BP/R4RK1 b - - 12 20"));
    list.add(new PgnTestCase("from_fen_repetition_from_three_moves_white_to_move_threefold.pgn",
        "repPos=3: 14.f4 16.Qc3 18.Nd2", "", -1, 8, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "r4rk1/p1pq1ppp/1pn1p3/1b2P3/1Pp2Pn1/2Q3P1/PB1NP1BP/R4RK1 b - - 8 18"));
    list.add(new PgnTestCase("from_fen_repetition_from_two_moves_black_to_move_fivefold.pgn",
        "repPos=5: 13.a4 15.Qd1 17.Nc3 19.Bc4 21.Ng5", "", -1, 16, CheckmateOrStalemate.NA, 5,
        InsufficientMaterial.NONE, "r2qk2r/pppbnpp1/1bn4p/4p1N1/P1BP4/2N5/5PPP/R1BQR1K1 b kq - 16 21"));
    list.add(new PgnTestCase("from_fen_repetition_from_two_moves_black_to_move_fourfold.pgn",
        "repPos=4: 13.a4 15.Qd1 17.Nc3 19.Bc4", "", -1, 12, CheckmateOrStalemate.NA, 4, InsufficientMaterial.NONE,
        "r2qk2r/pppbnpp1/1bn4p/4p1N1/P1BP4/2N5/5PPP/R1BQR1K1 b kq - 12 19"));
    list.add(new PgnTestCase("from_fen_repetition_from_two_moves_black_to_move_threefold.pgn",
        "repPos=3: 13.a4 15.Qd1 17.Nc3", "", -1, 8, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "r2qk2r/pppbnpp1/1bn4p/4p1N1/P1BP4/2N5/5PPP/R1BQR1K1 b kq - 8 17"));
    list.add(new PgnTestCase("from_fen_repetition_from_two_moves_white_to_move_fivefold.pgn",
        "repPos=5: 23...Qc8 25...Rf8 27...Nh5 29...Kg8 31...Bd7", "", -1, 18, CheckmateOrStalemate.NA, 5,
        InsufficientMaterial.NONE, "r1q2rk1/3b1ppp/nb1p4/1p1Pp2n/1P2P3/4BN1P/1Q2RPP1/RB3NK1 w - - 18 32"));
    list.add(new PgnTestCase("from_fen_repetition_from_two_moves_white_to_move_fourfold.pgn",
        "repPos=4: 23...Qc8 25...Rf8 27...Nh5 29...Kg8", "", -1, 14, CheckmateOrStalemate.NA, 4,
        InsufficientMaterial.NONE, "r1q2rk1/3b1ppp/nb1p4/1p1Pp2n/1P2P3/4BN1P/1Q2RPP1/RB3NK1 w - - 14 30"));
    list.add(new PgnTestCase("from_fen_repetition_from_two_moves_white_to_move_threefold.pgn",
        "repPos=3: 23...Qc8 25...Rf8 27...Nh5", "", -1, 10, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "r1q2rk1/3b1ppp/nb1p4/1p1Pp2n/1P2P3/4BN1P/1Q2RPP1/RB3NK1 w - - 10 28"));
    list.add(new PgnTestCase("from_fen_repetition_from_zero_moves_black_to_move_fivefold.pgn",
        "repPos=5: 57.Qe3 59.Qe3 61.Nd2 63.Kg1", "", -1, 16, CheckmateOrStalemate.NA, 5, InsufficientMaterial.NONE,
        "r2q2n1/1bpk1pp1/p2b3p/1p2pN2/P2PP3/R1P1Q3/1P1N1PPP/2B2RK1 b - - 16 63"));
    list.add(new PgnTestCase("from_fen_repetition_from_zero_moves_black_to_move_fourfold.pgn",
        "repPos=4: 57.Qe3 59.Qe3 61.Nd2", "", -1, 12, CheckmateOrStalemate.NA, 4, InsufficientMaterial.NONE,
        "r2q2n1/1bpk1pp1/p2b3p/1p2pN2/P2PP3/R1P1Q3/1P1N1PPP/2B2RK1 b - - 12 61"));
    list.add(new PgnTestCase("from_fen_repetition_from_zero_moves_black_to_move_threefold.pgn",
        "repPos=3: 57.Qe3 59.Qe3", "", -1, 8, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "r2q2n1/1bpk1pp1/p2b3p/1p2pN2/P2PP3/R1P1Q3/1P1N1PPP/2B2RK1 b - - 8 59"));
    list.add(new PgnTestCase("from_fen_repetition_from_zero_moves_white_to_move_fivefold.pgn",
        "repPos=5: 51...Nf6 53...Nf6 55...Nf6 57...Nf6", "", -1, 26, CheckmateOrStalemate.NA, 5,
        InsufficientMaterial.NONE, "r1bqkb1r/1pp2ppp/p1p2n2/4p2Q/P3P3/8/1PPP1PPP/RNB1K1NR w KQkq - 26 58"));
    list.add(new PgnTestCase("from_fen_repetition_from_zero_moves_white_to_move_fourfold.pgn",
        "repPos=4: 51...Nf6 53...Nf6 55...Nf6", "", -1, 22, CheckmateOrStalemate.NA, 4, InsufficientMaterial.NONE,
        "r1bqkb1r/1pp2ppp/p1p2n2/4p2Q/P3P3/8/1PPP1PPP/RNB1K1NR w KQkq - 22 56"));
    list.add(new PgnTestCase("from_fen_repetition_from_zero_moves_white_to_move_threefold.pgn",
        "repPos=3: 51...Nf6 53...Nf6", "", -1, 18, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "r1bqkb1r/1pp2ppp/p1p2n2/4p2Q/P3P3/8/1PPP1PPP/RNB1K1NR w KQkq - 18 54"));
    list.add(new PgnTestCase("from_fen_three_moves_half_move_clock_100_black_to_move.pgn", "",
        "50...NA (1) 100.NA (100) 101...Kc8 (103)", -1, 103, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "2k5/3RQ3/5N2/5pP1/5P2/8/PPP1P3/K7 w - - 103 102"));
    list.add(new PgnTestCase("from_fen_three_moves_half_move_clock_100_white_to_move.pgn", "",
        "50.NA (1) 99...NA (100) 101.Rb5 (103)", -1, 103, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1n1k1r/2pb1pp1/1p1nQ3/pR1pp1bp/P3P2P/3PR2N/1PP2PP1/qBB1K1N1 b - - 103 101"));
    list.add(new PgnTestCase("from_fen_three_moves_half_move_clock_101_black_to_move.pgn", "",
        "50.NA (1) 99...NA (100) 101...Nc6 (104)", -1, 104, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r2k2r/2pb1pp1/1pnn3Q/p2pp1bp/P3P2P/qR1P3N/1PP1RPP1/1BB1K1N1 w - - 104 102"));
    list.add(new PgnTestCase("from_fen_three_moves_half_move_clock_101_white_to_move.pgn", "",
        "49...NA (1) 99.NA (100) 101.Rf3 (104)", -1, 104, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1n1k1r/2pb1pp1/1p1n3Q/p2pp1bp/P3P2P/1R1P1R1N/1PP2PP1/qBB2KN1 b - - 104 101"));
    list.add(new PgnTestCase("from_fen_three_moves_half_move_clock_099_black_to_move.pgn", "",
        "51.NA (1) 100...Qa3 (100) 101...Ne6 (102)", -1, 102, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r2k2r/2pb1pp1/1p2n2Q/p2ppnbp/P3P2P/qR1PR2N/1PP1NPP1/1BB1K3 w - - 102 102"));
    list.add(new PgnTestCase("from_fen_three_moves_half_move_clock_099_white_to_move.pgn", "",
        "50...NA (1) 100.Qf6 (100) 101.Qf3 (102)", -1, 102, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1rb1kb1r/pp1pqppp/2p5/3Bp3/PR1NP3/Bn2NQ2/2PP1PPP/5RK1 b k - 102 101"));
    list.add(new PgnTestCase("from_fen_two_moves_half_move_clock_100_black_to_move.pgn", "",
        "50...NA (1) 100.NA (100) 101.Rd7 (102)", -1, 102, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1k6/3RQ3/5N2/5pP1/5P2/8/PPP1P3/K7 b - - 102 101"));
    list.add(new PgnTestCase("from_fen_two_moves_half_move_clock_100_white_to_move.pgn", "",
        "50.NA (1) 99...NA (100) 100...Kf8 (102)", -1, 102, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1n1k1r/2pb1pp1/1p1nQ3/p2pp1bp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 w - - 102 101"));
    list.add(new PgnTestCase("from_fen_two_moves_half_move_clock_101_black_to_move.pgn", "",
        "50.NA (1) 99...NA (100) 101.Re2 (103)", -1, 103, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp1bp/P3P2P/qR1P3N/1PP1RPP1/1BB1K1N1 b - - 103 101"));
    list.add(new PgnTestCase("from_fen_two_moves_half_move_clock_101_white_to_move.pgn", "",
        "49...NA (1) 99.NA (100) 100...Kf8 (103)", -1, 103, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1n1k1r/2pb1pp1/1p1n3Q/p2pp1bp/P3P2P/1R1PR2N/1PP2PP1/qBB2KN1 w - - 103 101"));
    list.add(new PgnTestCase("from_fen_two_moves_half_move_clock_099_black_to_move.pgn", "",
        "51.NA (1) 100...Qa3 (100) 101.Ne2 (101)", -1, 101, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1r1nk2r/2pb1pp1/1p5Q/p2ppnbp/P3P2P/qR1PR2N/1PP1NPP1/1BB1K3 b - - 101 101"));
    list.add(new PgnTestCase("from_fen_two_moves_half_move_clock_099_white_to_move.pgn", "",
        "50...NA (1) 100.Qf6 (100) 100...Qe7 (101)", -1, 101, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1rb1kb1r/pp1pqppp/2p2Q2/3Bp3/PR1NP3/Bn2N3/2PP1PPP/5RK1 w k - 101 101"));

    return new PgnTestCaseList(PgnTest.PARSER_FROM_FEN, list);
  }

  private static PgnTestCaseList createTestCasesBasicNoProgressSequencesWhite() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_white_from_fen_no_progress_fifty_reoccuring_fifty.pgn", "",
        "50.NA (1) 99...NA (100); 100...Qb6 (1) 150.Qd4+ (100)", -1, 100, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/3p4/1kp1p3/8/2qQ3P/5PPK/8/8 b - - 100 150"));
    list.add(new PgnTestCase("02_white_from_fen_no_progress_fifty_reoccuring_seventy_five.pgn", "",
        "50.NA (1) 99...NA (100); 100...Qb6 (1) 150.Qd4+ (100) 175.Qc8 (150)", -1, 150, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "2Q5/3p1q2/2pkp3/8/7P/5PPK/8/8 b - - 150 175"));
    list.add(new PgnTestCase("03_white_from_fen_no_progress_fifty_reoccuring_above_fifty.pgn", "",
        "50.NA (1) 99...NA (100); 100...Qb6 (1) 150.Qd4+ (100) 174...Qf7 (149)", -1, 149, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "Q7/3p1q2/2pkp3/8/7P/5PPK/8/8 w - - 149 175"));
    list.add(new PgnTestCase("09_white_from_fen_no_progress_above_fifty_reoccuring_fifty.pgn", "",
        "45.NA (1) 94...NA (100) 99...NA (110); 100...Qb6 (1) 150.Qd4+ (100)", -1, 110, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/3p4/1kp1p3/8/2qQ3P/5PPK/8/8 b - - 100 150"));
    list.add(new PgnTestCase("10_white_from_fen_no_progress_above_fifty_reoccuring_seventy_five.pgn", "",
        "44...NA (1) 94.NA (100) 99...NA (111); 100...Qb6 (1) 150.Qd4+ (100) 175.Qc8 (150)", -1, 150,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "2Q5/3p1q2/2pkp3/8/7P/5PPK/8/8 b - - 150 175"));
    list.add(new PgnTestCase("11_white_from_fen_no_progress_above_fifty_reoccuring_above_fifty.pgn", "",
        "44.NA (1) 93...NA (100) 99...NA (112); 100...Qb6 (1) 150.Qd4+ (100) 174...Qf7 (149)", -1, 149,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "Q7/3p1q2/2pkp3/8/7P/5PPK/8/8 w - - 149 175"));

    return new PgnTestCaseList(PgnTest.BASIC_REPORT_NO_PROGRESS_SEQUENCES_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesBasicNoProgressSequencesBlack() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_black_from_fen_no_progress_fifty_reoccuring_fifty.pgn", "",
        "50...NA (1) 100.NA (100); 101.Qd3 (1) 150...Qe3 (100)", 1, 100, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/6p1/7k/6n1/2NQ4/4q3/2P1P3/1K6 w - - 100 151"));
    list.add(new PgnTestCase("02_black_from_fen_no_progress_fifty_reoccuring_seventy_five.pgn", "",
        "50...NA (1) 100.NA (100); 101.Qd3 (1) 150...Qe3 (100) 175...Qe3 (150)", 1, 150, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/6pk/8/6n1/2NQ4/4q3/1KP1P3/8 w - - 150 176"));
    list.add(new PgnTestCase("03_black_from_fen_no_progress_fifty_reoccuring_above_fifty.pgn", "",
        "50...NA (1) 100.NA (100); 101.Qd3 (1) 150...Qe3 (100) 175.Qd4 (149)", 1, 149, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/6pk/8/4q1n1/2NQ4/8/1KP1P3/8 b - - 149 175"));
    list.add(new PgnTestCase("09_black_from_fen_no_progress_above_fifty_reoccuring_fifty.pgn", "",
        "45...NA (1) 95.NA (100) 100.NA (110); 101.Qd3 (1) 150...Qe3 (100)", 1, 110, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/6p1/7k/6n1/2NQ4/4q3/2P1P3/1K6 w - - 100 151"));
    list.add(new PgnTestCase("10_black_from_fen_no_progress_above_fifty_reoccuring_seventy_five.pgn", "",
        "45.NA (1) 94...NA (100) 100.NA (111); 101.Qd3 (1) 150...Qe3 (100) 175...Qe3 (150)", 1, 150,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "8/6pk/8/6n1/2NQ4/4q3/1KP1P3/8 w - - 150 176"));
    list.add(new PgnTestCase("11_black_from_fen_no_progress_above_fifty_reoccuring_above_fifty.pgn", "",
        "44...NA (1) 94.NA (100) 100.NA (112); 101.Qd3 (1) 150...Qe3 (100) 175.Qd4 (149)", 1, 149,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "8/6pk/8/4q1n1/2NQ4/8/1KP1P3/8 b - - 149 175"));

    return new PgnTestCaseList(PgnTest.BASIC_REPORT_NO_PROGRESS_SEQUENCES_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesLongestPossible() {
    final List<PgnTestCase> list = new ArrayList<>();

    // longest possible game seventy-five-move rule
    list.add(new PgnTestCase("longest_possible_tom_murphy.pgn", "",
        "1.Na3 (1) 50...Ng6 (100) 75.Nf3 (149); 76.Ne5 (1) 125...Qd8 (100) 150.Nc3 (149); 151.Ng5 (1) 200...Ne5 (100) 225.Ne5 (149); 226.Nb5 (1) 275...Bf5 (100) 300.Nd5 (149); 301.Ng6 (1) 350...Nf4 (100) 375.Nc4 (149); 376.Nb4 (1) 425...Nb4 (100) 450.Nf4 (149); 451.Rg1 (1) 500...Ne4 (100) 525.Rg1 (149); 526.Rh1 (1) 575...Ng3 (100) 600.Rh1 (149); 601.Rg1 (1) 650...Be4 (100) 675.Rg1 (149); 676.Rh1 (1) 725...Bd3 (100) 750.Rh1 (149); 751.Rg1 (1) 800...Qd8 (100) 824...Qd8 (148); 825...Nh6 (1) 875.Be5 (100) 899...Nc6 (149); 900...Ba6 (1) 950.Bf6 (100) 974...Nd4 (149); 975...Qb6 (1) 1025.Qb2 (100) 1049...Nb3 (149); 1050...Bg7 (1) 1100.Qb1 (100) 1124...Bb7 (149); 1125...Qb8 (1) 1175.Qc2 (100) 1199...Rc8 (149); 1200...Bc6 (1) 1250.Qd3 (100) 1274...Rc6 (149); 1275...Rf6 (1) 1325.Qb2 (100) 1349...Rd6 (149); 1350...Nh6 (1) 1400.Rh1 (100) 1424...Re6 (149); 1425...Nh6 (1) 1475.Qb2 (100) 1499...Rd6 (149); 1500...Qc7 (1) 1550.Qg3 (100) 1574...Re6 (149); 1575...Bh6 (1) 1625.Qc1 (100) 1649...Nh6 (149); 1650...Rg6 (1) 1700.Qe5 (100) 1724...Bh6 (149); 1725...Re3 (1) 1775.Bb2 (100) 1799...Kf8 (149); 1800...Ke8 (1) 1850.Qd5 (100) 1874...Ke8 (149); 1875...Re5 (1) 1925.Na5 (100) 1949...Ke8 (149); 1950...Rg8 (1) 2000.Qc2 (100) 2024...Kf8 (149); 2025...Nf6 (1) 2075.Bc1 (100) 2099...Ke8 (149); 2100...Rg8 (1) 2150.Qg3 (100) 2174...Bg5 (149); 2175...Kf7 (1) 2225.Qe5 (100) 2249...Bh4 (149); 2250...Bg3 (1) 2300.Qf8+ (100) 2324...Bg3 (149); 2325...Rg6 (1) 2375.Nd8 (100) 2399...Qc8 (149); 2400...Rh6 (1) 2450.Qb3 (100) 2474...Kc7 (149); 2475...Kd8 (1) 2525.Qg7 (100) 2549...Kb6 (149); 2550...Qa5 (1) 2600.Nb3 (100) 2624...Rf6 (149); 2625...Qa5 (1) 2675.Qge5 (100) 2699...Re6 (149); 2700...Kb5 (1) 2750.Rh6 (100) 2774...Rf6 (149); 2775...Rh6 (1) 2825.Qgg6 (100) 2849...Re6 (149); 2850...Re4 (1) 2900.Qcc3 (100) 2924...Rf6 (149); 2925...Qa4 (1) 2975.Qf6 (100) 2999...Rf6 (149); 3000...Rc6 (1) 3050.Qe5 (100) 3074...Rg8 (149); 3075...Rgg6 (1) 3125.Nc6 (100) 3149...Rgg6 (149); 3150...Rde6 (1) 3200.Ng7 (100) 3224...Rge6 (149); 3225...Rd3 (1) 3275.Nf6 (100) 3299...Rf6 (149); 3300...Rd3 (1) 3350.Nb4 (100) 3374...Rc6 (149); 3375...Rc8 (1) 3425.Qh8 (100) 3449...Qa6 (149); 3450...Qa5 (1) 3500.Rg1 (100) 3524...Qa5 (149); 3525...Qa2 (1) 3575.Ng4 (100) 3599...Qa6 (149); 3600...Kc7 (1) 3650.Qf7 (100) 3674...Qa5 (149); 3675...Rh6 (1) 3725.Qe4 (100) 3749...Qa6 (149); 3750...Kc7 (1) 3800.Nb4 (100) 3824...Qa5 (149); 3825...Rd6 (1) 3875.Qc2 (100) 3899...Qa6 (149); 3900...Qa5 (1) 3950.Rg1 (100) 3974...Qa6 (149); 3975...Qa3 (1) 4025.Qc3 (100) 4049...Qa5 (149); 4050...Rg6 (1) 4100.Ng7 (100) 4124...Qa6 (149); 4125...Rf7 (1) 4175.Qh3 (100) 4199...Qa5 (149); 4200...Qa6 (1) 4250.Qd1 (100) 4274...Qa5 (149); 4275...Qa3 (1) 4325.Nb8 (100) 4349...Qa5 (149); 4350...Kb7 (1) 4400.Qh3 (100) 4424...Qa3 (149); 4425...Kc7 (1) 4475.Qf3+ (100) 4499.Ra3 (148); 4500.Qg5 (1) 4549...Kc6 (100) 4574.Kh2 (149); 4575.Ne3 (1) 4624...Kb6 (100) 4649.Bd3 (149); 4650.Rc3 (1) 4699...Kb5 (100) 4724.Qf4 (149); 4725.Ne4 (1) 4774...Rd4 (100) 4799.Ng7 (149); 4800.Nb1 (1) 4849...Kb5 (100) 4874.Be4 (149); 4875.Qe3 (1) 4924...Rg5 (100) 4949.Ne6 (149); 4950.Rc3 (1) 4999...Re2 (100) 5024.Nf6 (149); 5025.Ra6+ (1) 5074...Ra6 (100) 5099.Ng8 (149); 5100.Ra1 (1) 5149...Ra4 (100) 5174.Nf6 (149); 5175.Re3 (1) 5224...Rh4 (100) 5249.Rd3 (149); 5250.Re1 (1) 5299...Bd1 (100) 5324.Bb2 (149); 5325.Nf1 (1) 5374...Bf5 (100) 5399.Bc3 (149); 5400.Qh4 (1) 5449...Bg2 (100) 5474.Bb2 (149); 5475.Qf1 (1) 5524...Kb5 (100) 5549.Bc3 (149); 5550.Qf1 (1) 5599...Re1+ (100) 5624.Bb2 (149); 5625.Nd8 (1) 5674...Be4 (100) 5699.Bc3 (149); 5700.Nb2 (1) 5749...Kc7 (100) 5774.Ba5+ (149); 5775.Nb2 (1) 5824...Rf1 (100) 5849.Nf4 (149); 5850.Qf5 (1) 5899...Rb4 (100) 5924.Ne2 (149); 5925.Qh4 (1) 5974...Kb6 (100) 5999.Nf4 (149); 6000.Qg7 (1) 6049...Re2 (100) 6074.Ne2 (149); 6075.Rd8 (1) 6124...Ra5 (100) 6149.Ng4 (149); 6150.Nb1 (1) 6199...Ra1 (100) 6224.Nge3 (149); 6225.Rg1 (1) 6274...Rg4 (100) 6299.Nf5 (149); 6300.Qd4+ (1) 6349...Kd6 (100) 6374.Nfe3 (149); 6375.Qh5 (1) 6424...Be4 (100) 6449.Re1 (149); 6450.Nb1 (1) 6499...Bf5 (100) 6524.Qf3 (149); 6525.Qd4+ (1) 6574...Rac3 (100) 6599.Kg2 (149); 6600.Kf2 (1) 6649...Bd3 (100) 6674.Re1 (149); 6675.Rd8 (1) 6724...Bd3 (100) 6749.Qfh5 (149); 6750.Nc2 (1) 6799...Bd3 (100) 6824.Ng4 (149); 6825.Rd8 (1) 6874...Rg2+ (100) 6899.Nc4+ (149); 6900.Q8h7 (1) 6949...Ka4 (100) 6974.Nb2 (149); 6975.Ra8 (1) 7024...Rg8 (100) 7049.Rg1 (149); 7050.Q8e5 (1) 7099...Re6 (100) 7124.Rg2 (149); 7125.Qc5+ (1) 7174...Ne2 (100) 7199.Kf3 (149); 7200.Kf2 (1) 7249...Rd4 (100) 7274.Kf2 (149); 7275.Qf7 (1) 7324...Rhh4 (100) 7349.Qh2 (149); 7350.Rg3 (1) 7399...Rc1 (100) 7424.Kf3 (149); 7425.Q8h6 (1) 7474...Rf1+ (100) 7499.Kf4 (149); 7500.Kf5 (1) 7549...Rhh1 (100) 7574.Qh2 (149); 7575.Q8e5 (1) 7624...Na2 (100) 7649.Kf6 (149); 7650.Q8h5 (1) 7699...Nc3 (100) 7724.Rg3 (149); 7725.Qg2 (1) 7774...Rcc1 (100) 7799.Rg2 (149); 7800.Q2h5 (1) 7849...Bc4 (100) 7874.Nd3 (149); 7875.Rf2 (1) 7924...Kb5 (100) 7949.Rg1 (149); 7950.Re4 (1) 7999...Rgg1 (100) 8024.Q8h3 (149); 8025.Rf7 (1) 8074...Rgd1 (100) 8099.Ra8 (149); 8100.Qd6+ (1) 8149...Rh8 (100) 8174.Rb8+ (149); 8175.Qf2+ (1) 8224...Bc4 (100) 8248...Qe1 (148); 8249...Kc6 (1) 8299.Qf6 (100) 8323...Bf7 (149); 8324...Kb6 (1) 8374.Kf5 (100) 8398...Rg5 (149); 8399...Rf3+ (1) 8449.Qc1 (100) 8473...Ka6 (149); 8474...Bd5 (1) 8524.Qa7+ (100) 8548...Kb6 (149); 8549...Ka7 (1) 8599.Ke5 (100) 8623...Kc5 (149); 8624...Rgg1 (1) 8674.Qa5 (100) 8698...Kc6 (149); 8699...Kb6 (1) 8749.Qe5 (100) 8773...Rg7 (149); 8774...Kd8 (1) 8824.Qd2+ (100) 8849.Qd7# (150)",
        750, 150, CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.BLACK_ONLY,
        "3k4/3Q4/4K3/8/8/8/8/8 b - - 150 8849"));
    // longest possible game fifty-five-move rule
    list.add(new PgnTestCase("longest_possible_francois_labelle.pgn", "",
        "1.Nf3 (1) 50...Nf4 (100) 75.Rh1 (149); 76.Nc4 (1) 125...Qg4 (100) 150.Rg1 (149); 151.Nh3 (1) 200...Rf8 (100) 225.Nf6 (149); 226.Rg1 (1) 275...Rdb8 (100) 300.Nc4 (149); 301.Nf4 (1) 350...Rh8 (100) 375.Rh1 (149); 376.Ne6 (1) 425...Rd7 (100) 450.Ng3 (149); 451.Nf5 (1) 500...Ra8 (100) 525.Ne8 (149); 526.Nec7 (1) 575...Ne6 (100) 600.Nf5 (149); 601.Ng3 (1) 650...Qe5 (100) 675.Ra1 (149); 676.Nf5 (1) 725...Rg4 (100) 750.Rg1 (149); 751.Ng3 (1) 800...Re8 (100) 825.Nf3 (149); 826.Nf4 (1) 875...Ka4 (100) 900.N8c7 (149); 901.Rb1 (1) 950...Rh4 (100) 975.Na3 (149); 976.Nc4 (1) 1025...Bg7 (100) 1049...Rh8 (148); 1050...Re8 (1) 1100.Nge4 (100) 1124...Ne5 (149); 1125...Bd8 (1) 1175.Kg2 (100) 1199...Nd5 (149); 1200...Bb5 (1) 1250.Bb2 (100) 1274...Bg7 (149); 1275...Rg8 (1) 1325.Rf1 (100) 1349...Bg5 (149); 1350...Re4 (1) 1400.Bd5 (100) 1424...Re4 (149); 1425...Rg8 (1) 1475.Bd5 (100) 1499...Na3 (149); 1500...Bb1 (1) 1550.Qd8 (100) 1574...Ka6 (149); 1575...Bf7 (1) 1625.Rh1 (100) 1649...Nc4 (149); 1650...Na3 (1) 1700.Rg2 (100) 1724...Rd8 (149); 1725...Rg4 (1) 1775.Qg7 (100) 1799...Bc6 (149); 1800...Rh1 (1) 1850.Ne1 (100) 1874...Qc6 (149); 1875...Bd5 (1) 1925.Rab1 (100) 1949...Qg8 (149); 1950...Bd6 (1) 2000.Kb2 (100) 2024...Kb8 (149); 2025...Qc6 (1) 2075.Rh2 (100) 2099...Rh8 (149); 2100...Re8 (1) 2150.Ra4 (100) 2174...Bc6 (149); 2175...Bf4 (1) 2225.Qf3 (100) 2249...Be4 (149); 2250...Bg3 (1) 2300.Rg1 (100) 2324...Re1 (149); 2325...Qg3 (1) 2375.Rc1 (100) 2399...Be7 (149); 2400...Rf3 (1) 2450.Nh6 (100) 2474...Bb7 (149); 2475...Bd5 (1) 2525.Rc1 (100) 2549...Rh2 (149); 2550...Be5 (1) 2600.Nd6 (100) 2624...Bf6 (149); 2625...Qe7 (1) 2675.Nh6 (100) 2699...Re8 (149); 2700...Rd8 (1) 2750.Nf6 (100) 2774...Rc8 (149); 2775...Ng8 (1) 2825.Bb2 (100) 2849...Rc8 (149); 2850...Ne4 (1) 2900.Bb4 (100) 2924...Rc8 (149); 2925...Kf8 (1) 2975.Bb4 (100) 2999...Rh2 (149); 3000...Kf8 (1) 3050.Ne4 (100) 3074...Rh1 (149); 3075...Rh3 (1) 3125.Ncd2 (100) 3149...Qc6 (149); 3150...Rg3 (1) 3200.Qba1 (100) 3224...Kg7 (149); 3225...Qg2 (1) 3275.Qb8 (100) 3299...Rf2 (149); 3300...Qe1 (1) 3350.Qdc6 (100) 3374...Rd8 (149); 3375...Rc1 (1) 3425.Qbb4 (100) 3449...Qd6 (149); 3450...Re8 (1) 3500.Qab2 (100) 3524...Rb8 (149); 3525...Kf8 (1) 3575.Ka2 (100) 3599...Qd6 (149); 3600...Re8 (1) 3650.Bf2 (100) 3674...Qb7 (149); 3675...Rg8 (1) 3725.Q4g3 (100) 3749...Qd6 (149); 3750...Qe5 (1) 3800.Qab2 (100) 3824...Qb8 (149); 3825...Ke8 (1) 3875.Rg5 (100) 3899...Qg8 (149); 3900...Qd8 (1) 3950.Bb4 (100) 3974...Qc6 (149); 3975...Ke7 (1) 4025.Bc2 (100) 4049...Kf6 (149); 4050...Kf7 (1) 4100.Qfg2 (100) 4124...Kg7 (149); 4125...Kf6 (1) 4175.Ra2 (100) 4199.Qge1 (148); 4200.Qeg1 (1) 4249...Kf8 (100) 4274.Qf4 (149); 4275.Qc2 (1) 4324...Kg7 (100) 4349.Rf2 (149); 4350.Bg3 (1) 4399...Kg7 (100) 4424.Qgg2 (149); 4425.Qbb1 (1) 4474...Kg7 (100) 4499.Qge4 (149); 4500.Q4d3 (1) 4549...Qe8 (100) 4574.Nf2 (149); 4575.Q2a1 (1) 4624...Qd5 (100) 4649.Q2e3 (149); 4650.Qd6 (1) 4699...Kf7 (100) 4724.Qed1 (149); 4725.Qgb3 (1) 4774...Kg8 (100) 4799.Q4b2 (149); 4800.Qb1c1 (1) 4849...Qf6 (100) 4874.Qhh1 (149); 4875.Bf6 (1) 4924...Qd6 (100) 4949.Qgf3 (149); 4950.Qfd6 (1) 4999...Kg7 (100) 5024.Rb4 (149); 5025.Qcc5 (1) 5074...Qf7 (100) 5099.Qec7 (149); 5100.Qdd6 (1) 5149...Qe5 (100) 5174.Qbc5 (149); 5175.Qa8 (1) 5224...Qc4 (100) 5249.Q2h1 (149); 5250.Q5a2 (1) 5299...Qb1 (100) 5324.Qca3 (149); 5325.Qhd6 (1) 5374...Qh4 (100) 5399.Bd6 (149); 5400.Qac1 (1) 5449...Qef7 (100) 5474.Qc8 (149); 5475.Qbd4 (1) 5524...Qfe7 (100) 5549.Qbb1 (149); 5550.Q8e5 (1) 5599...Qh4 (100) 5624.Qeg7 (149); 5625.Qgc7 (1) 5674...Qae3 (100) 5699.Q3d2 (149); 5700.Nf7 (1) 5749...Qc5 (100) 5774.Qad4 (149); 5775.Qdd5 (1) 5824...Qh8 (100) 5849.Qed8 (149); 5850.Q7e7 (1) 5899...Qaf3 (100) 5924.Qcc1 (149); 5925.Q3c2 (1) 5974...Qee8 (100) 5999.Qbb5 (149); 6000.Qgd3 (1) 6049...Qc2 (100) 6074.Be3 (149); 6075.Bd2 (1) 6124...Qd3 (100) 6149.Qdf8 (149); 6150.Rc1 (1) 6199...Q4b4 (100) 6224.Ne3 (149); 6225.Qbd1 (1) 6274...Q6e6 (100) 6299.Q6f5 (149); 6300.Qff8 (1) 6349...Qfh4 (100) 6374.Qdd1 (149); 6375.Qed6 (1) 6424...Qc1 (100) 6449.Qda3 (149); 6450.Bh2 (1) 6499...Qfd5 (100) 6524.Qdb3 (149); 6525.Qff2 (1) 6574...Qcc4 (100) 6599.Qgd8 (149); 6600.Qbb8 (1) 6649...Qdc5 (100) 6674.Qhh6 (149); 6675.Q2d2 (1) 6724...Qhe1 (100) 6749.Qd2 (149); 6750.Qfe8 (1) 6799...Q4d4 (100) 6824.Qde5 (149); 6825.Qff6 (1) 6874...Qfg1 (100) 6899.Qed8 (149); 6900.Qbe8 (1) 6949...Qff6 (100) 6974.Qd7 (149); 6975.Qff6 (1) 7024...Qgh6 (100) 7049.Qce6 (149); 7050.Qg4 (1) 7099...Q8h5 (100) 7124.Qb6b7 (149); 7125.Qf8 (1) 7174...Qhf7 (100) 7199.Qbc8 (149); 7200.Qf2 (1) 7249...Qcd2 (100) 7274.Qdd1 (149); 7275.Qd2 (1) 7324...Qef4 (100) 7349.Qdb7 (149); 7350.Qbc2 (1) 7399...Qgf2 (100) 7424.Qg5 (149); 7425.Qb1 (1) 7474...Qda1 (100) 7499.Qaa5 (149); 7500.Qf6 (1) 7549...Qg6 (100) 7574.Qc4 (149); 7575.Qc7 (1) 7624...Qdd1 (100) 7649.Kb8 (149); 7650.Qb5 (1) 7699...Kh4 (100) 7724.Qf6+ (149); 7725.Kc7 (1) 7774...Qee5+ (100) 7798...Qff5 (148); 7799...Qfc2 (1) 7849.Kb8 (100) 7873...Kd6 (149); 7874...Qfc1 (1) 7924.Kc8 (100) 7948...Qhd2 (149); 7949...Qgg1 (1) 7999.Ka7 (100) 8023...Qdf2 (149); 8024...Q2e2 (1) 8074.Kf7 (100) 8098...Qaa1 (149); 8099...Qe1e4 (1) 8149.Kg8 (100) 8173...Q3b1 (149); 8174...Qf2 (1) 8224.Qe4 (100) 8248...Qab8 (149); 8249...Q1d2 (1) 8299.Qf6 (100) 8323...Qh7+ (149); 8324...Qba2 (1) 8374.Qd8 (100) 8398...Qee1 (149); 8399...Qgc5 (1) 8449.Qd6 (100) 8473...Qb2 (149); 8474...Qba1 (1) 8524.Qb5 (100) 8548...Qa7 (149); 8549...Qf6 (1) 8599.Qc7 (100) 8623...Qga8 (149); 8624...Qc4 (1) 8674.Qf5 (100) 8698...Qf5 (149); 8699...Kh4 (1) 8749.Kc5 (100) 8773...Qe1 (149); 8774...Kg7 (1) 8824.Qf3+ (100) 8849.Qg4# (150)",
        4199, 150, CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.BLACK_ONLY,
        "8/8/8/5K2/6Qk/8/8/8 b - - 150 8849"));

    return new PgnTestCaseList(PgnTest.MAX_MOVES, list);
  }

  private static PgnTestCaseList createTestCasesRandomNoRepetition() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("05_random_no_repetition.pgn", "", "", 18, 79, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "4k3/8/3B4/8/8/8/4q3/K7 b - - 79 182"));

    return new PgnTestCaseList(PgnTest.RANDOM_NO_REPETITION, list);
  }

  private static PgnTestCaseList createTestCasesRandomCheckmate() {
    final List<PgnTestCase> list = new ArrayList<>();
    list.add(new PgnTestCase("01_random_checkmate.pgn", "", "", 5, 3, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "3qkbnr/pb1pp2p/p1r2p2/2p1P1pQ/7P/2P4N/PPNP1PP1/1RB1K2R b k - 3 12"));
    list.add(new PgnTestCase("02_random_checkmate.pgn", "", "", 9, 15, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "5k1Q/R4P2/8/p7/7P/4p2N/7K/3r4 b - - 2 82"));
    list.add(new PgnTestCase("03_random_checkmate.pgn", "", "", 19, 64, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "2b5/8/K7/2q4p/5k2/8/8/8 w - - 13 180"));
    list.add(new PgnTestCase("04_random_checkmate.pgn", "", "", 4, 17, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "5k2/8/1b6/7K/4p3/3R2q1/p6r/8 w - - 7 119"));
    list.add(new PgnTestCase("05_random_checkmate.pgn", "", "", 15, 89, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "4K3/4q3/3k4/8/8/7N/8/8 w - - 8 215"));

    return new PgnTestCaseList(PgnTest.RANDOM_CHECKMATE, list);
  }

  private static PgnTestCaseList createTestCasesRandomStalemate() {
    final List<PgnTestCase> list = new ArrayList<>();
    list.add(new PgnTestCase("01_random_stalemate.pgn", "", "", 13, 93, CheckmateOrStalemate.STALEMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "K7/8/1q4k1/8/8/8/8/8 w - - 93 233"));
    list.add(new PgnTestCase("02_random_stalemate.pgn", "", "", 21, 92, CheckmateOrStalemate.STALEMATE, 1,
        InsufficientMaterial.NONE, "k7/8/1Q6/3K4/p7/P1B5/8/8 b - - 92 191"));
    list.add(new PgnTestCase("03_random_stalemate.pgn", "", "", 14, 40, CheckmateOrStalemate.STALEMATE, 1,
        InsufficientMaterial.NONE, "5K2/3k1P2/p7/6r1/8/8/8/8 w - - 1 122"));
    list.add(new PgnTestCase("04_random_stalemate.pgn", "", "", 13, 9, CheckmateOrStalemate.STALEMATE, 1,
        InsufficientMaterial.NONE, "6k1/6P1/p3N2P/P1p5/2P1B3/N2K4/7P/R4R2 b - - 2 46"));
    list.add(new PgnTestCase("05_random_stalemate.pgn", "", "", 16, 93, CheckmateOrStalemate.STALEMATE, 1,
        InsufficientMaterial.NONE, "8/8/6p1/3b2P1/8/8/2k5/K7 w - - 72 250"));

    return new PgnTestCaseList(PgnTest.RANDOM_STALEMATE, list);
  }

  private static PgnTestCaseList createTestCasesRandomInsufficientMaterial() {
    final List<PgnTestCase> list = new ArrayList<>();
    list.add(new PgnTestCase("01_random_insufficient_material.pgn", "", "", 16, 38, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BOTH, "8/5k2/8/8/8/8/8/2K5 b - - 0 153"));
    list.add(new PgnTestCase("02_random_insufficient_material.pgn", "", "", 19, 66, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BOTH, "8/7k/8/8/8/8/6K1/8 b - - 0 191"));
    list.add(new PgnTestCase("03_random_insufficient_material.pgn", "", "", 10, 95, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BOTH, "8/8/8/8/8/8/1K6/5k2 w - - 0 177"));
    list.add(new PgnTestCase("04_random_insufficient_material.pgn", "", "", 16, 82, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BOTH, "8/8/4K3/8/5k2/8/8/8 w - - 0 232"));
    list.add(new PgnTestCase("05_random_insufficient_material.pgn", "", "", 10, 95, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BOTH, "8/6k1/8/4N3/8/8/2K5/8 b - - 0 204"));

    return new PgnTestCaseList(PgnTest.RANDOM_INSUFFICIENT_MATERIAL, list);
  }

  private static PgnTestCaseList createTestCasesRandomFifty() {
    final List<PgnTestCase> list = new ArrayList<>();
    list.add(new PgnTestCase("01_random_fifty.pgn", "", "146...Ke8 (1) 196.Kg1 (100)", 9, 100, CheckmateOrStalemate.NA,
        1, InsufficientMaterial.NONE, "8/1k6/8/2B5/8/8/8/1b4K1 b - - 100 196"));
    list.add(new PgnTestCase("02_random_fifty.pgn", "", "164.Rc3+ (1) 213...Kf2 (100)", 11, 100,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "8/8/n7/3K4/3R4/8/5k2/8 w - - 100 214"));
    list.add(new PgnTestCase("03_random_fifty.pgn", "", "126.Kh6 (1) 175...Qb1 (100)", 26, 100, CheckmateOrStalemate.NA,
        1, InsufficientMaterial.WHITE_ONLY, "8/2K5/6k1/8/8/8/8/1q6 w - - 100 176"));
    list.add(new PgnTestCase("04_random_fifty.pgn", "", "179.Kh4 (1) 228...Ka7 (100)", 26, 100, CheckmateOrStalemate.NA,
        1, InsufficientMaterial.BLACK_ONLY, "8/k7/8/5N2/8/7K/6N1/8 w - - 100 229"));
    list.add(new PgnTestCase("05_random_fifty.pgn", "", "137.Kb5 (1) 186...Nd2 (100)", 8, 100, CheckmateOrStalemate.NA,
        1, InsufficientMaterial.NONE, "8/5k2/8/K7/8/8/1B1n4/8 w - - 100 187"));

    return new PgnTestCaseList(PgnTest.RANDOM_FIFTY, list);
  }

  private static PgnTestCaseList createTestCasesRandomSeventyFive() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_random_seventy_five.pgn", "", "176.Bf7 (1) 225...Kd6 (100) 250...Kd6 (150)", 8, 150,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "8/5B2/K2k4/7p/7P/8/8/8 w - - 150 251"));
    list.add(new PgnTestCase("02_random_seventy_five.pgn", "", "198.Re8 (1) 247...Kg2 (100) 272...Ka1 (150)", 15, 150,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.BLACK_ONLY, "8/8/K7/8/8/8/5R2/k7 w - - 150 273"));
    list.add(new PgnTestCase("03_random_seventy_five.pgn", "", "162.Kb2 (1) 211...Ke4 (100) 236...Kh6 (150)", 20, 150,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.WHITE_ONLY, "8/8/3q3k/8/8/8/8/2K5 w - - 150 237"));
    list.add(new PgnTestCase("04_random_seventy_five.pgn", "", "132.Kg8 (1) 181...Ng2 (100) 206...Kc2 (150)", 22, 150,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "2K5/2N5/8/8/8/8/2kn4/8 w - - 150 207"));

    return new PgnTestCaseList(PgnTest.RANDOM_SEVENTY_FIVE, list);
  }

  private static PgnTestCaseList createTestCasesRandomThreefold() {
    final List<PgnTestCase> list = new ArrayList<>();
    list.add(new PgnTestCase("01_random_threefold.pgn", "repPos=3: 110...Bf2 114...Bf2 119...Bf2", "", 16, 27,
        CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE, "8/7K/k7/p6p/P6P/5p2/5b2/8 w - - 27 120"));
    list.add(new PgnTestCase("02_random_threefold.pgn", "repPos=3: 197.Kf7 199.Kf7 201.Kf7", "", 13, 99,
        CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE, "8/5K2/2k5/1p6/pP6/P7/8/8 b - - 99 201"));
    list.add(new PgnTestCase("03_random_threefold.pgn", "repPos=3: 158.Ke3 162.Ke3 171.Ke3", "", 36, 40,
        CheckmateOrStalemate.NA, 3, InsufficientMaterial.BLACK_ONLY, "k7/8/8/P7/8/4K3/8/8 b - - 40 171"));
    list.add(new PgnTestCase("04_random_threefold.pgn", "repPos=3: 188.Kg4 190.Rf5 193.Rf5", "", 32, 96,
        CheckmateOrStalemate.NA, 3, InsufficientMaterial.BLACK_ONLY, "8/6k1/8/5R2/6K1/8/8/8 b - - 96 193"));
    list.add(new PgnTestCase("05_random_threefold.pgn", "repPos=3: 198...Ke3 200...Ke3 202...Ke3", "", 11, 38,
        CheckmateOrStalemate.NA, 3, InsufficientMaterial.WHITE_ONLY, "8/8/8/1K6/8/4k3/7p/8 w - - 26 203"));

    return new PgnTestCaseList(PgnTest.RANDOM_THREEFOLD, list);
  }

  private static PgnTestCaseList createTestCasesRandomFivefold() {
    final List<PgnTestCase> list = new ArrayList<>();
    list.add(new PgnTestCase("01_random_fivefold.pgn",
        "repPos=3: 149.Kxd3 165.Kd3 171.Kd3; repPos=4: 156.Kb2 173.Kb2 183.Kb2 193.Kb2; repPos=4: 156...Kc8 158...Kc8 160...Kc8 183...Kc8; repPos=3: 172...Kb7 189...Kb7 192...Kb7; repPos=5: 173...Ka6 175...Ka6 186...Ka6 188...Ka6 193...Ka6; repPos=3: 175.Kb2 186.Kb2 188.Kb2",
        "", 22, 89, CheckmateOrStalemate.NA, 5, InsufficientMaterial.NONE, "8/8/k7/p7/P7/8/1K6/8 w - - 89 194"));
    list.add(new PgnTestCase("02_random_fivefold.pgn",
        "repPos=3: 136.Nd7 138.Kb4 140.Kb4; repPos=3: 212...Rd3 214...Rd3 216...Rd3; repPos=5: 251...Kb7 255...Rc8 257...Rc8 259...Rc8 261...Rc8; repPos=3: 254...Rc5 258...Rc5 260...Rc5; repPos=4: 255.Kb2 257.Kb2 259.Kb2 261.Kb2",
        "", 13, 92, CheckmateOrStalemate.NA, 5, InsufficientMaterial.WHITE_ONLY, "2r5/1k6/8/8/8/8/1K6/8 w - - 71 262"));

    return new PgnTestCaseList(PgnTest.RANDOM_FIVEFOLD, list);
  }

  private static PgnTestCaseList createTestCasesVarious() {
    final List<PgnTestCase> list = new ArrayList<>();
    list.add(new PgnTestCase("various_almtwali_vs_danielbaechli_2020.pgn", "", "", 14, 91, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "6k1/4P2p/5K1P/8/8/8/8/8 b - - 0 114"));
    list.add(new PgnTestCase("various_krush_zatonski_2008.pgn", "", "", 13, 10, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "6rk/1p1R1pqp/p1p4p/6rP/P7/5QP1/1P3P2/4R1K1 b - - 3 33"));
    list.add(new PgnTestCase("various_krush_zatonski_2008_reconstructed.pgn", "", "", 13, 11, CheckmateOrStalemate.NA,
        1, InsufficientMaterial.NONE, "r5qk/1R2Rprp/5Q1p/p6P/P7/2P3P1/5P2/6K1 w - - 11 47"));
    list.add(new PgnTestCase("various_rikikits_vs_demchenko_2016_amended.pgn",
        "repPos=3: 48.Kc7 50.Kc7 52.Kc7; repPos=3: 48...Bd5 50...Bd5 52...Bd5; repPos=3: 49.Kd6 51.Kd6 53.Kd6; repPos=3: 49...Be6 51...Be6 53...Be6; repPos=4: 73.Kf4 75.Kf4 81.Kf4 85.Kf4; repPos=3: 76...Kb5 80...Be6 86...Kb5",
        "112.Kd2 (1) 161...Bb5 (100) 163.Bh8 (103)", 7, 103, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "7B/5p2/2k5/K3P3/1P6/3b4/8/8 b - - 103 163"));
    list.add(new PgnTestCase("various_demchenko_vs_verdenotte_2018_amended.pgn", "",
        "88...Rb3 (1) 138.Ra1 (100) 141...Rb6 (107)", 6, 107, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "8/8/1r6/3B4/1k1K4/8/R7/8 w - - 107 142"));
    list.add(new PgnTestCase("various_demchenko_vs_chesspanda123_2017_amended.pgn", "",
        "61...Re1+ (1) 111.Kc5 (100) 136.Rg8+ (150)", 39, 150, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "6R1/8/8/8/4B3/4K3/5r2/6k1 b - - 150 136"));
    list.add(new PgnTestCase("various_savic_vs_bueble_2020.pgn",
        "repPos=3: 76.Rb6 80.Kh6 84.Rb6; repPos=3: 76...Ra8 80...Ra8 84...Ra8", "", 12, 31, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BOTH, "5k2/8/6K1/8/8/8/8/8 w - - 0 92"));
    list.add(new PgnTestCase("various_blatny_holzke_1997.pgn", "", "", 8, 24, CheckmateOrStalemate.NA, 2,
        InsufficientMaterial.NONE, "8/3R2bp/4n1p1/1p1k1pP1/5P1P/1K2P3/8/8 b - - 24 64"));
    list.add(new PgnTestCase("various_gvetadze_milliet_2014.pgn", "repPos=3: 25.Qg6+ 27.Qg6+ 29.Qg6+", "", 10, 9,
        CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "2q2rk1/2r1b3/1p2p1Q1/4Pp2/8/3B4/1P3PPP/3R2K1 b - - 9 29"));
    // O-O-O+ was played in this game!!!
    list.add(new PgnTestCase("various_hikaru_vs_penguingm1_2014.pgn", "", "", 11, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "2k5/1pp1r3/p5pp/2P5/1P6/P2pP1PP/4PK2/8 w - - 0 36"));

    // "threefold repetition" with initial en passant
    list.add(new PgnTestCase("various_gmjoey1_vs_bugsbunny444_2013.pgn", "repPos=3: 18.Qg6+ 20.Qg6+ 22.Qg6+", "", 18, 9,
        CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "rn1q1rk1/pp1b4/4p1Q1/bN1pPp2/3P4/PpPB4/5PPP/R3K2R b KQ - 9 22"));
    list.add(new PgnTestCase("various_gmjoey1_vs_tiohoracio_2015.pgn",
        "repPos=3: 23.Qh7+ 25.Qh7+ 27.Qh7+; repPos=3: 23...Kf7 25...Kf7 27...Kf7", "", 6, 10, CheckmateOrStalemate.NA,
        3, InsufficientMaterial.NONE, "r1b2r2/1p3kpQ/4p3/4Pp2/p6R/2q5/P5PP/1R5K w - - 10 28"));
    list.add(new PgnTestCase("various_gmjoey1_vs_dulerile_2018.pgn",
        "repPos=3: 45.Rc8+ 47.Rc8+ 49.Rc8+; repPos=3: 45...Kb7 47...Kb7 49...Kb7", "", 6, 10, CheckmateOrStalemate.NA,
        3, InsufficientMaterial.NONE, "2R5/1k3p2/1p4p1/pP1b3p/P4B1P/6P1/1r6/5K2 w - - 10 50"));

    // checkmating with castling
    list.add(new PgnTestCase("various_lasker_vs_alan_thomas_1912.pgn", "", "", 9, 6, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "rn3r2/pbppq1p1/1p2pN2/8/3P2NP/6P1/PPP1BP1R/2KR2k1 b - - 6 18"));

    // fooling around as long as I could
    list.add(new PgnTestCase("various_mickeymousetest_donaldducktest_2021.pgn", "", "339.Qd6 (1) 388...Ne3 (100)", 49,
        100, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "rnbq1b1r/4k3/8/7N/4P2p/4n3/8/R1BQKBNR w - - 100 389"));

    list.add(new PgnTestCase("various_jobava_so_2017.pgn", "repPos=3: 37.Rxa7+ 39.Ra7+ 41.Ra7+", "", 5, 8,
        CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE, "8/R3k3/8/6N1/5p1P/P1Pr4/1P2r3/2K5 b - - 8 41"));

    // fifty-move claim but only 67 half-moves
    list.add(new PgnTestCase("various_gunina_harika_2019.pgn", "", "", 15, 67, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "R7/1n6/2K5/8/8/8/2k5/8 b - - 67 101"));

    // knight-bishop checkmate in blitz unsuccessful

    // 75-move-rule exceeded by 116.5 full moves - no arbiter call
    list.add(new PgnTestCase("various_anikonov_zhigalko_2018.pgn", "", "104.Ra8 (1) 153...Ke5 (100) 166...Kb5 (126)", 8,
        126, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "2R5/4r3/8/1k1n4/8/5K2/8/8 w - - 126 167"));

    // KRvKRB played over fifty moves, under seventy-five moves

    // KRvKRN - white about being mated lost on time
    list.add(new PgnTestCase("various_harikrishna_hovhannisyan_2018.pgn", "", "", 32, 91, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/K1k5/3n1R2/8/7r/8/8/8 w - - 91 128"));

    // KvKNN - unclear why game was stopped
    list.add(new PgnTestCase("various_bazeev_riazantsev_2018.pgn", "", "59.Ke3 (1) 108...Ng6+ (100) 109...Nf8+ (102)",
        8, 102, CheckmateOrStalemate.NA, 1, InsufficientMaterial.WHITE_ONLY, "5n2/5k1K/8/8/6n1/8/8/8 w - - 102 110"));

    // Blitz, was it fivefold? No, only threefold
    list.add(new PgnTestCase("various_khademalsharieh_bodnaruk_2018.pgn", "repPos=3: 77...Re1 79...Kg7 83...Kg7", "",
        21, 49, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "4Q3/3K4/6k1/8/5R2/8/8/4r3 b - - 0 109"));

    // we need more skillful arbiters
    list.add(new PgnTestCase("various_grischuk_mamedyarov_2017.pgn", "repPos=4: 61.Rb2 63.Kg2 65.Kg2 67.Rb2", "", 7, 25,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "8/8/8/8/4p3/4Bk1K/5Pr1/8 w - - 2 88"));

    // youtube video - why savic so angry?
    list.add(new PgnTestCase("various_pranav_savic_2021_incomplete_speculative.pgn", "", "", 7, 73,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "8/5k2/2R5/5p2/5P2/6P1/6K1/r7 w - - 73 101"));
    list.add(new PgnTestCase("various_pranav_savic_2021_incomplete_speculative_from_last_capture.pgn", "", "", 1, 73,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "8/5k2/2R5/5p2/5P2/6P1/6K1/r7 w - - 73 101"));

    // repetition after promotion
    list.add(new PgnTestCase("various_keres_fischer_1962.pgn", "", "", 20, 17, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/p4Q1k/1p6/1P6/8/7K/8/6q1 b - - 5 77"));
    list.add(new PgnTestCase("various_keres_fischer_1962_changed.pgn", "repPos=3: 76.Qf8+ 78.Qf8+ 80.Qf8+", "", 20, 17,
        CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE, "5Q1k/p7/1p6/1P6/8/7K/8/6q1 b - - 11 80"));

    // Lichess issue - game import
    list.add(new PgnTestCase("various_swiercz_karjakin_2015.pgn", "repPos=3: 42.Rf8+ 44.Rf8+ 46.Rf8+", "", 24, 11,
        CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE, "5R2/p3r3/P1R1pkp1/5p1p/5P1P/r5P1/4PK2/8 b - - 9 46"));

    // the threefold I did not see
    list.add(new PgnTestCase("various_gwendolus_vs_danielbaechli_2022.pgn", "repPos=3: 50.Bd4 52.Rh6 54.Rh6", "", 12,
        15, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE, "2k1r3/1p6/7R/3p4/1P1Bn3/2P2K2/P7/8 b - - 10 54"));

    // import on Lichess did not work
    list.add(new PgnTestCase("various_tal_bronstein_1961.pgn", "", "", 3, 10, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/5p2/1p2p2p/6pk/r6q/4Q3/5P2/2R3K1 w - - 6 41"));

    list.add(new PgnTestCase("various_gundavaa_tari_2022.pgn", "", "", 8, 13, CheckmateOrStalemate.NA, 2,
        InsufficientMaterial.NONE, "R7/2q2p2/5k1Q/pp2r3/2p1P1P1/7P/6K1/8 b - - 13 42"));

    return new PgnTestCaseList(PgnTest.VARIOUS, list);
  }

  private static PgnTestCaseList createTestCasesWcc201() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("wcc_2021_round_01_nepomniachtchi_carlsen.pgn", "", "", 17, 14, CheckmateOrStalemate.NA, 2,
        InsufficientMaterial.NONE, "8/4r2p/p2rnkp1/2p2p2/2N4P/2PP1P2/2K2P2/R3R3 b - - 14 45"));
    list.add(new PgnTestCase("wcc_2021_round_01_nepomniachtchi_carlsen_amended.pgn",
        "repPos=3: 41...Re7 43...Rc6 45...Rc6", "", 17, 15, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "8/4r2p/p1r1nkp1/2p2p2/2N4P/2PP1P2/2K2P2/R3R3 w - - 15 46"));
    list.add(new PgnTestCase("wcc_2021_round_02_carlsen_nepomniachtchi.pgn", "", "", 12, 9, CheckmateOrStalemate.NA, 2,
        InsufficientMaterial.NONE, "8/6kp/7R/6Pp/r4P2/8/5K2/8 w - - 9 59"));
    list.add(new PgnTestCase("wcc_2021_round_03_nepomniachtchi_carlsen.pgn", "", "", 28, 6, CheckmateOrStalemate.NA, 2,
        InsufficientMaterial.NONE, "8/3b4/7p/p1p1k3/P1p2p1P/2P2P2/2B2KP1/8 b - - 6 41"));
    list.add(new PgnTestCase("wcc_2021_round_04_carlsen_nepomniachtchi.pgn", "", "", 5, 7, CheckmateOrStalemate.NA, 2,
        InsufficientMaterial.NONE, "r4nk1/4Rp1p/1p3Np1/1P1P2P1/8/p2r4/5P1P/R5K1 b - - 7 33"));
    list.add(new PgnTestCase("wcc_2021_round_04_carlsen_nepomniachtchi_amended.pgn",
        "repPos=3: 29...a3 31...Kg7 33...Kg7", "", 5, 8, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "r4n2/4Rpkp/1p3Np1/1P1P2P1/8/p2r4/5P1P/R5K1 w - - 8 34"));
    list.add(new PgnTestCase("wcc_2021_round_05_nepomniachtchi_carlsen.pgn", "", "", 17, 14, CheckmateOrStalemate.NA, 2,
        InsufficientMaterial.NONE, "4kb2/2p3p1/4np1p/4pN1P/4P1P1/2P1BP2/4K3/r2R4 b - - 14 43"));
    list.add(new PgnTestCase("wcc_2021_round_05_nepomniachtchi_carlsen_amended.pgn",
        "repPos=3: 39...Ra2+ 41...Ra2+ 43...Ra2+", "", 17, 15, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "4kb2/2p3p1/4np1p/4pN1P/4P1P1/2P1BP2/r3K3/3R4 w - - 15 44"));
    list.add(new PgnTestCase("wcc_2021_round_06_carlsen_nepomniachtchi.pgn", "", "", 13, 55, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3k4/5RN1/4P3/5P2/7K/8/8/6q1 b - - 2 136"));
    list.add(new PgnTestCase("wcc_2021_round_07_nepomniachtchi_carlsen.pgn",
        "repPos=3: 34...Kg7 36...Kg7 38...Kg7; repPos=3: 35.Ra5 37.Ra5 39.Ra5; repPos=3: 35...Kf6 37...Kf6 39...Kf6",
        "", 17, 15, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "8/R4pk1/6p1/7p/7P/2r3P1/5PK1/8 b - - 15 41"));
    list.add(new PgnTestCase("wcc_2021_round_08_carlsen_nepomniachtchi.pgn", "", "", 6, 12, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/7k/8/3P4/6P1/1P2qQ1p/6P1/7K b - - 1 46"));
    list.add(new PgnTestCase("wcc_2021_round_09_nepomniachtchi_carlsen.pgn", "", "", 18, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r3b1k1/P4pp1/4p3/2N1P2p/7P/5Pn1/8/R5K1 w - - 1 40"));
    list.add(new PgnTestCase("wcc_2021_round_10_carlsen_nepomniachtchi.pgn", "", "", 5, 12, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/pp6/2pk1n2/3pN3/3P4/2P1K3/PP6/8 b - - 1 41"));
    list.add(new PgnTestCase("wcc_2021_round_11_nepomniachtchi_carlsen.pgn", "", "", 20, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "K7/5pk1/1R4p1/P1q5/5P2/4P3/8/8 w - - 7 50"));

    return new PgnTestCaseList(PgnTest.WCC2021, list);
  }

  private static PgnTestCaseList createTestCasesFivefoldCorrect() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("fivefold_correct_akshat_li_2018.pgn",
        "repPos=5: 60...Rc1 62...Rc1 68...Rc1 73...Rc1 75...Rc1", "", 19, 32, CheckmateOrStalemate.NA, 5,
        InsufficientMaterial.NONE, "8/Pk6/5P2/1K1p3p/3Bp2P/4P3/8/2r5 w - - 32 76"));

    list.add(new PgnTestCase("fivefold_correct_miton_yakubboev_2018.pgn",
        "repPos=5: 38...Rd5+ 40...Rd5+ 42...Rd5+ 44...Rd5+ 46...Rd5+; repPos=4: 39.Ke4 41.Ke4 43.Ke4 45.Ke4; repPos=3: 39...Re5+ 43...Re5+ 45...Re5+; repPos=3: 40.Kd4 44.Kd4 46.Kd4",
        "", 25, 23, CheckmateOrStalemate.NA, 5, InsufficientMaterial.NONE,
        "8/p7/1p2k2p/1P1r4/P1pK4/4P2P/8/2R5 w - - 23 47"));
    list.add(new PgnTestCase("fivefold_correct_potapov_adly_2018.pgn",
        "repPos=5: 43.Qf5+ 45.Qf5+ 47.Qf5+ 49.Qf5+ 51.Qf5+", "", 20, 24, CheckmateOrStalemate.NA, 5,
        InsufficientMaterial.NONE, "8/6rk/7p/p4Q2/8/Pr5P/3n1P2/3N3K b - - 24 51"));
    list.add(new PgnTestCase("fivefold_correct_sethuraman_guseinov_2017.pgn",
        "repPos=5: 51.Rxf7+ 53.Rf7+ 55.Rf7+ 57.Rf7+ 59.Rf7+; repPos=4: 51...Kg6 53...Kg6 55...Kg6 57...Kg6; repPos=4: 52.Rf6+ 54.Rf6+ 56.Rf6+ 58.Rf6+; repPos=4: 52...Kg7 54...Kg7 56...Kg7 58...Kg7",
        "", 14, 19, CheckmateOrStalemate.NA, 5, InsufficientMaterial.NONE,
        "1r6/1P3Rkp/3Np3/4P3/1r4p1/8/7P/7K b - - 16 59"));
    list.add(new PgnTestCase("fivefold_correct_paichadze_ter-sahakyan_2019.pgn",
        "repPos=5: 58...Re4+ 60...Re4+ 62...Re4+ 64...Re4+ 66...Re4+; repPos=4: 59.Kf2 61.Kf2 63.Kf2 65.Kf2; repPos=4: 59...Rf4 61...Rf4 63...Rf4 65...Rf4; repPos=4: 60.Ke3 62.Ke3 64.Ke3 66.Ke3",
        "", 5, 18, CheckmateOrStalemate.NA, 5, InsufficientMaterial.NONE, "8/8/8/4k3/4r3/4KR2/8/8 w - - 18 67"));
    list.add(new PgnTestCase("fivefold_correct_robson_moranda_2019.pgn",
        "repPos=5: 34...Kf8 36...Kf8 38...Kf8 40...Kf8 42...Kf8; repPos=4: 35.Raf7+ 37.Raf7+ 39.Raf7+ 41.Raf7+; repPos=4: 35...Ke8 37...Ke8 39...Ke8 41...Ke8; repPos=4: 36.Ra7 38.Ra7 40.Ra7 42.Ra7",
        "", 6, 17, CheckmateOrStalemate.NA, 5, InsufficientMaterial.NONE,
        "5k2/R5R1/3pp2p/4p3/4P2P/1r1r2PK/8/8 w - - 17 43"));
    return new PgnTestCaseList(PgnTest.FIVEFOLD_CORRECT, list);
  }

  private static PgnTestCaseList createTestCasesFiftyGeneral() {
    final List<PgnTestCase> list = new ArrayList<>();
    list.add(new PgnTestCase("fifty_general_adly_flores_2016.pgn",
        "repPos=3: 112.Rh5 114.Kc6 116.Rh5; repPos=3: 112...Nc3 114...Nc3 116...Nc3",
        "92...Nb3+ (1) 142.Ke6 (100) 143...Nb8 (103)", 26, 103, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "1R2k3/8/4K3/8/8/8/8/8 b - - 0 144"));
    list.add(new PgnTestCase("fifty_general_cheparinov_anand_2018.pgn", "", "61...Kb5 (1) 111.Ra7 (100) 130.Rb1 (138)",
        11, 138, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "8/8/8/2KB4/k7/r7/8/1R6 b - - 138 130"));
    list.add(new PgnTestCase("fifty_general_giri_grischuk_2014.pgn",
        "repPos=3: 86...Kc5 114...Kc5 116...Kc5; repPos=3: 87.Ka4 115.Ka4 117.Ka4; repPos=4: 87...Kb6 89...Nc6 115...Kb6 117...Kb6",
        "85.Rc8 (1) 134...Kc5 (100) 146...Nc6 (124)", 6, 124, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "8/8/3P1k2/3K4/8/8/8/8 b - - 1 156"));
    list.add(
        new PgnTestCase("fifty_general_carlsen_liem_2014.pgn", "", "52...Kc7 (1) 102.Bb7 (100) 103.Ba6 (102)", 11, 102,
            CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "8/5p2/Bp2pP1k/1p2P3/2b2K2/P1P5/8/8 b - - 102 103"));
    list.add(new PgnTestCase("fifty_general_pruijssers_inarkiev_2015.pgn", "", "59.Rb8 (1) 108...Ra2 (100)", 37, 100,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE, "8/8/8/7P/8/6Pk/6r1/2R2K1r w - - 11 128"));
    list.add(new PgnTestCase("fifty_general_riazantsev_levin_2016.pgn",
        "repPos=3: 61...Kg7 64...Qa2 81...Qa2; repPos=3: 77...Qd5 87...Qd5 96...Kg7; repPos=3: 152...Qg1+ 154...Qg1+ 156...Qg1+",
        "46...Qe4 (1) 96.Rf4 (100) 113...Qd5 (135)", 10, 135, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "5Rqk/8/6PP/8/5P2/5NK1/8/8 b - - 4 181"));
    list.add(new PgnTestCase("fifty_general_radjabov_deac_2016.pgn", "", "69...Kf5 (1) 119.Rh2 (100) 126...Ke5 (115)",
        17, 115, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "8/8/8/4k3/4pP2/4P1K1/r6R/8 b - f3 0 127"));
    list.add(new PgnTestCase("fifty_general_alekseenko_grachev_2017.pgn", "repPos=3: 64.Rf2 66.Bh5 70.Rf2",
        "55...Rg8 (1) 105.Kf4 (100) 117...Rh3 (125)", 18, 125, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "8/2kB1K2/4P3/4P3/4p3/2P5/1P3q2/8 w - - 2 126"));
    list.add(new PgnTestCase("fifty_general_vaisser_karpov_2017.pgn", "", "82...Kd7 (1) 132.Ke4 (100) 152.Bc3+ (140)",
        11, 140, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "6r1/8/7R/k7/2K5/2B5/8/8 b - - 140 152"));
    list.add(new PgnTestCase("fifty_general_yu_korobov_2017.pgn", "", "60...Bc7 (1) 110.Ra4+ (100) 123.Rb2 (126)", 17,
        126, CheckmateOrStalemate.NA, 1, InsufficientMaterial.BLACK_ONLY, "8/8/8/8/k1K5/8/1R3b2/8 b - - 126 123"));
    list.add(new PgnTestCase("fifty_general_yu_shimanov_2018.pgn", "repPos=3: 128.Ra1 130.Ra1 132.Ra1",
        "88.Ra2+ (1) 137...Kg7 (100) 155...Rc1 (136)", 9, 136, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/5k2/6p1/6K1 w - - 0 163"));
    list.add(new PgnTestCase("fifty_general_ju_sebenik_2018.pgn", "", "64.Rg3+ (1) 113...Kb7 (100) 128.Rf2 (129)", 19,
        129, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "8/8/8/8/k7/6Kp/5R1P/5b2 b - - 129 128"));
    list.add(new PgnTestCase("fifty_general_wang_wen_2018.pgn",
        "repPos=3: 131.Ra2 137.Ke3 139.Ra2; repPos=4: 131...Kg1 137...Kg1 139...Kg1 141...Rf8; repPos=3: 133...Kf1 135...Kf1 143...Kf1",
        "95...Kb5 (1) 145.Be4 (100) 169.Rc1 (148)", 12, 148, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "8/8/8/8/4BK2/1r6/7k/2R5 b - - 148 169"));
    list.add(new PgnTestCase("fifty_general_meier_dominguez_2018.pgn",
        "repPos=4: 89...Rd3 91...Rd3 93...Kg8 95...Rd3; repPos=3: 98...Rd1 100...Rd1 104...Rd1",
        "46...Kf7 (1) 96.Ra7 (100) 106...Rd1 (121)", 12, 121, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "5R2/5P1k/3B3p/3K3P/8/8/8/8 b - - 0 115"));
    list.add(new PgnTestCase("fifty_general_bindrich_kovalev_2018.pgn", "", "57...Ra3+ (1) 107.Kg2 (100) 118.Kf3 (122)",
        15, 122, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "8/8/8/6k1/1B5p/5K1P/7r/8 b - - 3 121"));
    list.add(new PgnTestCase("fifty_general_jobava_salem_2018.pgn", "", "36.Ne1 (1) 85...Nb4 (100) 92...Nb4 (114)", 13,
        114, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "4k3/1q1b4/2p1p3/2p1Pp2/1BPp1Pp1/1PbP2Pp/2NN3P/2KQ4 b - - 0 93"));
    list.add(new PgnTestCase("fifty_general_sarana_chirila_2019.pgn",
        "repPos=3: 68.Ne3 76.Ne3 104.Ne3; repPos=3: 68...Bd3 76...Bd3 104...Bd3; repPos=3: 69.Nd1 77.Nd1 105.Nd1; repPos=3: 69...Bc2 77...Bc2 105...Bc2",
        "63...Bd3 (1) 113.Ke3 (100) 116...Kf7 (107)", 10, 107, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "8/7p/4k3/3NPb1P/3K4/8/8/8 b - - 66 150"));
    list.add(new PgnTestCase("fifty_general_topalov_dominguez_2019.pgn", "", "38...Qc3 (1) 88.Kg3 (100) 90.Kg3 (104)",
        22, 104, CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE,
        "8/4p3/3p1k2/3P4/2P2pQ1/5P1K/8/7q w - - 15 109"));
    return new PgnTestCaseList(PgnTest.FIFTY_GENERAL, list);
  }

  private static PgnTestCaseList createTestCasesFiftyPattern() {
    final List<PgnTestCase> list = new ArrayList<>();

    // because below 100 not displayed
    // 55.Nc4 (1) 103...Kh5 (98)
    list.add(new PgnTestCase("fifty_pattern_098_start_white_demchenko_martinez_2019.pgn",
        "repPos=3: 70...Ng5 83...Kh5 103...Kh5", "", 29, 98, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "8/8/4p3/6nk/6p1/4N1P1/5PK1/8 w - - 98 104"));

    // because below 100 not displayed
    // 68...Rh2 (1) 117.Kd3 (98)
    list.add(new PgnTestCase("fifty_pattern_098_start_black_sjugirov_inarkiev_2019.pgn", "", "", 4, 98,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "8/8/8/8/r7/3K4/1R3N2/6k1 b - - 98 117"));

    // because below 100 not displayed
    // 115.Kg2 (1) 163...Kc5 (98); 164.Kh1 (99)
    list.add(new PgnTestCase("fifty_pattern_099_start_white_so_nakamura_2018.pgn",
        "repPos=3: 145...Kb2 153...Kb2 157...Kb2; repPos=3: 146.Kh1 154.Kh1 158.Kh1", "", 15, 99,
        CheckmateOrStalemate.NA, 2, InsufficientMaterial.WHITE_ONLY, "8/8/8/2k1b3/8/8/7p/7K b - - 99 164"));

    // because below 100 not displayed
    // 59...Bg6 (1) 108.Nb5 (98); 108...Bh5 (99)
    list.add(new PgnTestCase("fifty_pattern_099_start_black_naroditsky_boruchovsky_2018.pgn",
        "repPos=3: 72...Ke6 90...Ke6 92...Be8; repPos=3: 75.Nd4+ 97.Nd4+ 107.Nd4+; repPos=3: 75...Ke5 97...Ke5 107...Ke5; repPos=3: 76.Nb5 98.Nb5 108.Nb5; repPos=4: 76...Bh5 98...Bh5 102...Bh5 108...Bh5; repPos=3: 84...Ke6 96...Ke6 106...Ke6",
        "", 27, 99, CheckmateOrStalemate.NA, 4, InsufficientMaterial.NONE, "8/6K1/5P2/1N2k2b/8/8/8/8 w - - 99 109"));

    list.add(new PgnTestCase("fifty_pattern_100_start_white_baryshpolets_steingrimsson_2019.pgn",
        "repPos=3: 77...Qg7+ 83...Qg7+ 95...Qg7+; repPos=3: 78.Kb6 84.Kb6 96.Kb6; repPos=3: 78...Qd4+ 84...Qd4+ 96...Qd4+; repPos=3: 79.Ka6 85.Ka6 97.Ka6",
        "77.Kc7 (1) 126...Nf4+ (100)", 8, 100, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1k6/4K3/6Q1/4q3/5n2/8/8/8 w - - 100 127"));

    list.add(new PgnTestCase("fifty_pattern_100_start_black_nakamura_radjabov_2014.pgn",
        "repPos=3: 68...Ref8 70...Be6 72...Be6", "27...Rh5 (1) 77.Rd3 (100)", 8, 100, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "1kbb1r1r/2p2pp1/1p6/p1pNPn2/P1P2P1p/1P1R1N1P/5BPK/3R4 b - - 100 77"));

    list.add(new PgnTestCase("fifty_pattern_101_start_white_nakamura_vachier-lagrave_2018.pgn",
        "repPos=3: 111.Kf6 119.Kf6 121.Kf6; repPos=3: 111...Kf1 119...Kf1 121...Kf1",
        "72.Bf4 (1) 121...Kf1 (100) 122.Kf7 (101)", 5, 101, CheckmateOrStalemate.NA, 2, InsufficientMaterial.NONE,
        "8/5K2/8/6Bp/4p2P/4Pb2/8/5k2 b - - 101 122"));

    list.add(new PgnTestCase("fifty_pattern_101_start_black_sevian_andriasian_2019.pgn", "",
        "74...Kd5 (1) 124.Kc6 (100) 124...Rc1+ (101)", 10, 101, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "2k5/7R/2K5/4N3/8/8/8/2r5 w - - 101 125"));

    list.add(new PgnTestCase("fifty_pattern_101_start_black_grischuk_vachier-lagrave_2017.pgn",
        "repPos=3: 67...Kd6 75...Kd6 86...Kd6; repPos=4: 68.Rh8 74.Rh8 76.Rh8 87.Rh8; repPos=3: 68...Kc6 76...Kc6 87...Kc6; repPos=4: 69.Rc8+ 73.Rc8+ 77.Rc8+ 88.Rc8+",
        "39...Kf6 (1) 89.Ra8 (100) 89...Kd6 (101)", 3, 101, CheckmateOrStalemate.NA, 2, InsufficientMaterial.NONE,
        "R7/8/3kp3/3p4/3Pr3/3KP3/8/8 w - - 101 90"));

    list.add(new PgnTestCase("fifty_pattern_102_start_white_harikrishna_yu_2017.pgn", "",
        "52.Rf3+ (1) 101...Rg1 (100) 102...Ke5 (102)", 17, 102, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "8/8/1R1b3K/4k3/8/8/8/6r1 w - - 102 103"));

    list.add(new PgnTestCase("fifty_pattern_102_start_black_riazantsev_deac_2017.pgn", "",
        "91...Ne3 (1) 141.Ke3 (100) 142.Kf3 (102)", 7, 102, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "8/8/8/3nk3/R7/5K2/8/8 b - - 102 142"));

    return new PgnTestCaseList(PgnTest.FIFTY_PATTERN, list);
  }

  private static PgnTestCaseList createTestCasesSeventyFiveCorrect() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("seventy_five_correct_firouzja_demchenko_2019.pgn",
        "repPos=3: 100...Kg8 102...Ra3 104...Kg8", "52.Kf2 (1) 101...Ra5+ (100) 126...Kg7 (150)", 15, 150,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "8/6k1/4R2p/4K1pP/5rP1/5P2/8/8 w - - 150 127"));
    list.add(new PgnTestCase("seventy_five_correct_topalov_nakamura_2016.pgn", "",
        "58.Kc8 (1) 107...Nc4 (100) 132...Rg8 (150)", 15, 150, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "6r1/8/8/8/5k2/R6K/3n4/8 w - - 150 133"));
    list.add(new PgnTestCase("seventy_five_correct_yudasin_erenburg_2017.pgn", "",
        "123.Kf2 (1) 172...Ke1 (100) 197...Ne7 (150)", 5, 150, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "4B3/4n3/8/2k2p2/6p1/4K1P1/8/8 w - - 150 198"));
    return new PgnTestCaseList(PgnTest.SEVENTY_FIVE_CORRECT, list);
  }

  private static PgnTestCaseList createTestCasesEarlyDraw() {
    final List<PgnTestCase> list = new ArrayList<>();
    list.add(new PgnTestCase("early_draw_grischuk_giri_2019.pgn", "repPos=3: 18.Kf1 20.Kf1 22.Kf1", "", 11, 10,
        CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "r4rk1/p4pp1/np2p3/q5N1/3PP3/P7/5P1P/R2Q1KR1 b - - 10 22"));
    list.add(new PgnTestCase("early_draw_karjakin_nepomniachtchi_2019.pgn", "repPos=3: 17.Nb5 19.Nb5 21.Nb5", "", 18,
        13, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "rn1r2k1/1pqbppbp/2p2np1/pN6/8/4BNP1/PPQ1PPBP/R2R2K1 b - - 13 21"));
    list.add(new PgnTestCase("early_draw_vachier-lagrave_aronian_2019.pgn", "repPos=3: 21.Ra1 23.Nc4 25.Nc4", "", 21,
        13, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "5rk1/1np1q1pp/p1n1p3/Pr2p3/1pNpP3/1N1P3P/1PP2PP1/R2QR1K1 b - - 13 25"));
    list.add(new PgnTestCase("early_draw_vachier-lagrave_ding_liren_2019.pgn", "repPos=3: 16...Qg6 18...Qg6 20...Qg6",
        "", 19, 19, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "r4rk1/bpp1n1pp/p2pp1q1/4p3/PP2Pn2/N1PPBN1P/R4PPK/3QR3 w - - 19 21"));
    return new PgnTestCaseList(PgnTest.EARLY_DRAW, list);
  }

  private static PgnTestCaseList createTestCasesWikipediaThreefold() {
    final List<PgnTestCase> list = new ArrayList<>();
    list.add(new PgnTestCase("wikipedia_threefold_2_0_1_spassky_fischer_1972_seventeenth.pgn", "", "", 11, 20,
        CheckmateOrStalemate.NA, 2, InsufficientMaterial.NONE,
        "8/1p2ppk1/p1np4/6p1/2R1P3/1P4KP/P1R1r1P1/8 b - - 7 45"));
    list.add(new PgnTestCase("wikipedia_threefold_2_0_1_spassky_fischer_1972_seventeenth_changed.pgn",
        "repPos=3: 41...g5 43...Re1 45...Re1", "", 11, 20, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "8/1p2ppk1/p1np4/6p1/2R1P3/1P4KP/P1R3P1/4r3 w - - 8 46"));
    list.add(new PgnTestCase("wikipedia_threefold_2_0_2_fischer_spassky_1972_eighteenth.pgn", "", "", 8, 21,
        CheckmateOrStalemate.NA, 2, InsufficientMaterial.NONE, "2r5/5R1Q/1kqr1p2/4p3/pP6/Pp4P1/1P5P/KR6 w - - 21 48"));
    list.add(new PgnTestCase("wikipedia_threefold_2_0_2_fischer_spassky_1972_eighteenth_changed.pgn",
        "repPos=3: 44.Qh6 46.Qh6 48.Qh6", "", 8, 22, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "2r5/5R2/1kqr1p1Q/4p3/pP6/Pp4P1/1P5P/KR6 b - - 22 48"));
    list.add(new PgnTestCase("wikipedia_threefold_2_1_fischer_petrosian_1971.pgn", "repPos=3: 30.Qe2 32.Qe2 34.Qe2", "",
        8, 11, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "8/pp3p1k/2p2q1p/3r1P2/5R2/7P/P1P1QP2/7K b - - 10 34"));
    list.add(new PgnTestCase("wikipedia_threefold_2_2_1_ponomariov_adams_2005_wijk_aan_zee.pgn", "", "", 5, 8,
        CheckmateOrStalemate.NA, 2, InsufficientMaterial.NONE, "7k/6p1/1p1p4/nP1p3p/3P4/R1P3rP/4K3/5B2 w - - 8 42"));
    list.add(new PgnTestCase("wikipedia_threefold_2_2_1_ponomariov_adams_2005_wijk_aan_zee_changed.pgn",
        "repPos=3: 38.Kd2 40.Kd2 42.Kd2", "", 5, 9, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "7k/6p1/1p1p4/nP1p3p/3P4/R1P3rP/3K4/5B2 b - - 9 42"));
    list.add(new PgnTestCase("wikipedia_threefold_2_2_2_adams_ponomariov_2005_sofia.pgn", "", "", 5, 11,
        CheckmateOrStalemate.NA, 2, InsufficientMaterial.NONE, "r7/p1r2ppp/6k1/3p4/6R1/4RP2/PP3P1P/1K6 b - - 10 27"));
    list.add(new PgnTestCase("wikipedia_threefold_2_2_2_adams_ponomariov_2005_sofia_changed.pgn",
        "repPos=3: 23...Rc7 25...Kf6 27...Kf6", "", 5, 11, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "r7/p1r2ppp/5k2/3p4/6R1/4RP2/PP3P1P/1K6 w - - 11 28"));
    list.add(new PgnTestCase("wikipedia_threefold_2_2_3_ponomariov_adams_2005_sofia.pgn", "", "", 17, 12,
        CheckmateOrStalemate.NA, 2, InsufficientMaterial.NONE, "3Q4/5pk1/7R/1p3Pp1/1P6/7P/6P1/4q2K w - - 7 53"));
    list.add(new PgnTestCase("wikipedia_threefold_2_2_3_ponomariov_adams_2005_sofia_changed.pgn",
        "repPos=3: 49...Qe5+ 51...Qe5+ 53...Qe5+", "", 17, 12, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "3Q4/5pk1/7R/1p2qPp1/1P6/7P/6PK/8 w - - 9 54"));
    list.add(new PgnTestCase("wikipedia_threefold_2_3_capablanca_lasker_1921.pgn",
        "repPos=3: 34...h5 36...Kf8 38...Kf8", "", 15, 8, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1Q3k2/p4p2/1p6/7R/3q4/1P2n3/P7/6K1 b - - 7 46"));
    list.add(
        new PgnTestCase("wikipedia_threefold_2_4_1_alekhine_lasker_1914.pgn", "", "", 6, 5, CheckmateOrStalemate.NA, 1,
            InsufficientMaterial.NONE, "r2q1r1k/p1p1b3/4pnQp/3p4/8/2NB4/PPP2PPP/R5K1 b - - 3 16"));
    list.add(new PgnTestCase("wikipedia_threefold_2_4_1_alekhine_lasker_1914_changed.pgn",
        "repPos=3: 17.Qxh6+ 19.Qh6+ 21.Qh6+", "", 6, 8, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "r3qr1k/p1p1b3/4pn1Q/3p4/8/2NB4/PPP2PPP/R5K1 b - - 8 21"));
    list.add(new PgnTestCase("wikipedia_threefold_2_4_2_lasker_alekhine_1914.pgn", "repPos=3: 21.Qd4 23.Kg1 25.Kg1", "",
        3, 11, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "7r/2p1bppp/2p1k3/2P1n3/3QN3/5q2/PP3P1P/R1BR2K1 b - - 11 25"));

    list.add(new PgnTestCase("wikipedia_threefold_2_5_korchnoi_portisch_1970_game_1.pgn",
        "repPos=3: 64.Kh5 66.Kh5 68.Rb8", "", 35, 19, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "1R6/5nk1/3r4/4p1pK/4P2p/7N/8/8 b - - 19 68"));
    list.add(new PgnTestCase("wikipedia_threefold_2_5_korchnoi_portisch_1970_game_2.pgn", "", "", 14, 9,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "2rr1k2/1b3pp1/pp2pq1p/2bP4/P7/2N5/BP2QPPP/3RR1K1 w - - 9 32"));
    list.add(new PgnTestCase("wikipedia_threefold_2_5_korchnoi_portisch_1970_game_3.pgn", "", "", 26, 14,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "3N2k1/8/8/pPn4p/1p5P/8/6P1/6K1 w - - 1 64"));
    list.add(new PgnTestCase("wikipedia_threefold_2_5_portisch_korchnoi_1970_game_4.pgn", "", "", 8, 10,
        CheckmateOrStalemate.NA, 2, InsufficientMaterial.NONE,
        "2b1nrk1/p2p1npp/2q1p3/2N5/5P2/P5P1/1P3QBP/R3K2R b KQ - 10 25"));
    list.add(new PgnTestCase("wikipedia_threefold_2_5_portisch_korchnoi_1970_game_4_changed.pgn",
        "repPos=3: 21...Qb5 23...Qb5 25...Qb5", "", 8, 11, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "2b1nrk1/p2p1npp/4p3/1qN5/5P2/P5P1/1P3QBP/R3K2R w KQ - 11 26"));

    list.add(new PgnTestCase("wikipedia_threefold_2_6_kasparov_deep_blue_1997.pgn", "", "", 8, 8,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "8/pp4P1/8/8/1kp2N2/1n2R1P1/3r4/1K6 w - - 1 50"));
    list.add(new PgnTestCase("wikipedia_threefold_2_6_kasparov_deep_blue_1997_changed.pgn",
        "repPos=3: 50.g8=Q 52.Kb1 54.Kb1", "", 8, 8, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "6Q1/pp6/8/8/1kp2N2/1n2R1P1/3r4/1K6 b - - 8 54"));
    list.add(new PgnTestCase("wikipedia_threefold_2_7_1_giuoco_piano.pgn", "", "", 8, 10, CheckmateOrStalemate.NA, 2,
        InsufficientMaterial.NONE, "r1bqrknQ/ppp3pB/3p1p2/3P4/6PR/8/PP3P1P/R5K1 b - - 10 22"));
    list.add(new PgnTestCase("wikipedia_threefold_2_7_1_giuoco_piano_changed.pgn", "repPos=3: 20.Bh7 22.Bh7 24.Bh7", "",
        8, 14, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "r1bqrknQ/ppp3pB/3p1p2/3P4/6PR/8/PP3P1P/R5K1 b - - 14 24"));
    list.add(new PgnTestCase("wikipedia_threefold_2_7_2_pirc_defense.pgn", "", "", 16, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rn1Nk2r/pp2p2p/3p2p1/1bp5/5Pn1/2N5/PPP2bPP/R1BQK2R w kq - 5 14"));
    list.add(new PgnTestCase("wikipedia_threefold_2_7_2_pirc_defense_changed.pgn", "repPos=3: 12.Kd2 14.Kd2 16.Kd2", "",
        16, 10, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "rn1Nk2r/pp2p2p/3p2p1/1bp5/5Pn1/2N5/PPPK1bPP/R1BQ3R b kq - 10 16"));
    list.add(new PgnTestCase("wikipedia_threefold_3_1_karpov_miles_1986.pgn", "", "", 7, 9, CheckmateOrStalemate.NA, 2,
        InsufficientMaterial.NONE, "r3kb1r/5ppp/4p3/1N6/4P3/8/Pn1BK1PP/R6R b k - 9 26"));
    list.add(new PgnTestCase("wikipedia_threefold_3_1_karpov_miles_1986_changed.pgn",
        "repPos=3: 22...Ra4 24...Ra4 26...Ra4", "", 7, 10, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "4kb1r/5ppp/4p3/1N6/r3P3/8/Pn1BK1PP/R6R w k - 10 27"));
    list.add(new PgnTestCase("wikipedia_threefold_3_2_fischer_spassky_1972_twentieth.pgn", "", "", 6, 30,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "8/8/3k2b1/1p2p2p/p2n2p1/P1K1N1P1/1PP4P/4N3 w - - 30 55"));
    list.add(new PgnTestCase("wikipedia_threefold_3_2_fischer_spassky_1972_twentieth_changed.pgn",
        "repPos=3: 48.Kc3 50.Ne1 58.Kc3", "", 6, 37, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "8/8/3k2b1/1p2p2p/p2n2p1/P1K1N1P1/1PP4P/4N3 b - - 37 58"));
    list.add(new PgnTestCase("wikipedia_threefold_4_1_pillsbury_burn_1898.pgn",
        "repPos=3: 42...Qe3 46...Kg7 50...Kg7; repPos=3: 43.Qb2 47.Qb2 51.Qb2", "", 8, 42, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/5Q2/8/P6p/7P/1q2p1P1/kp5K/8 w - - 2 91"));
    return new PgnTestCaseList(PgnTest.WIKIPEDIA_THREEFOLD, list);
  }

  private static PgnTestCaseList createTestCasesWikipediaFiftyMove() {
    final List<PgnTestCase> list = new ArrayList<>();
    list.add(new PgnTestCase("wikipedia_fifty_move_1_filipowicz_smederevac_1966.pgn", "", "21.Qe2 (1) 70...Rd7 (100)",
        -1, 100, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "4q3/r1br1nk1/1pb1p1p1/p1pnPp1p/P2p1P1P/NP1P1BPN/2PB3K/R3R2Q w - - 100 71"));
    list.add(new PgnTestCase("wikipedia_fifty_move_2_1_timman_lutz_1995.pgn", "",
        "69...Rh1 (1) 119.Rg5 (100) 121...Rb5+ (105)", 16, 105, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "8/7k/8/1r3KR1/5B2/8/8/8 w - - 105 122"));
    list.add(new PgnTestCase("wikipedia_fifty_move_2_2_karpov_kasparov_1991.pgn", "",
        "63...Rg8 (1) 113.Ng5 (100) 114...Rf6+ (103)", 30, 103, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "7k/4NK2/5r2/5BN1/8/8/8/8 w - - 103 115"));
    list.add(new PgnTestCase("wikipedia_fifty_move_2_3_lputian_harutjunyan_2001.pgn", "",
        "86...Qe2+ (1) 136.Qd4+ (100) 142.Qf6+ (112)", 19, 112, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "7k/5q2/5Q1P/6P1/8/6K1/8/8 b - - 112 142"));
    list.add(
        new PgnTestCase("wikipedia_fifty_move_2_4_nguyen_vachier-lagrave_2008.pgn", "", "71...Ra5+ (1) 121.Bc5+ (100)",
            6, 100, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "4R3/kr6/2K5/2B5/8/8/8/8 b - - 100 121"));
    return new PgnTestCaseList(PgnTest.WIKIPEDIA_FIFTY_MOVE, list);
  }

  private static PgnTestCaseList createTestCasesSequence() {
    final List<PgnTestCase> list = new ArrayList<>();
    list.add(new PgnTestCase("01_sequence.pgn",
        "repPos=3: 1.Nf3 3.Nf3 5.Nf3; repPos=3: 1...Nf6 3...Nf6 5...Nf6; repPos=3: 2.Ng1 4.Ng1 6.Ng1; repPos=3: 2...Ng8 4...Ng8",
        "", -1, 11, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "rnbqkb1r/pppppppp/5n2/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 11 6"));
    list.add(new PgnTestCase("02_sequence.pgn",
        "repPos=3: 1.Nf3 3.Nf3 5.Nf3; repPos=3: 1...Nf6 3...Nf6 5...Nf6; repPos=3: 2.Ng1 4.Ng1 6.Ng1; repPos=4: 2...Ng8 4...Ng8 6...Ng8",
        "", -1, 12, CheckmateOrStalemate.NA, 4, InsufficientMaterial.NONE,
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 12 7"));
    list.add(new PgnTestCase("03_sequence.pgn",
        "repPos=4: 1.Nf3 3.Nf3 5.Nf3 7.Nf3; repPos=4: 1...Nf6 3...Nf6 5...Nf6 7...Nf6; repPos=4: 2.Ng1 4.Ng1 6.Ng1 8.Ng1; repPos=5: 2...Ng8 4...Ng8 6...Ng8 8...Ng8",
        "", -1, 16, CheckmateOrStalemate.NA, 5, InsufficientMaterial.NONE,
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 16 9"));
    return new PgnTestCaseList(PgnTest.BASIC_REPORT_REPETITION, list);
  }

  private static PgnTestCaseList createTestBasicCaptureLastMove() {
    final List<PgnTestCase> list = new ArrayList<>();
    list.add(new PgnTestCase("01_capture_last_move_none.pgn", "", "", -1, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r1bq1rk1/1pp2p2/pbnp1n1p/3Np1p1/1PB1P1P1/P2P1N1P/2P2P2/R1BQK2R w KQ - 2 11"));
    list.add(new PgnTestCase("02_capture_last_move_piece.pgn", "", "", 21, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r1bq1rk1/1pp2p2/pbnp1N1p/4p1p1/1PB1P1P1/P2P1N1P/2P2P2/R1BQK2R b KQ - 0 11"));
    list.add(new PgnTestCase("03_capture_last_move_en_passant.pgn", "", "", 25, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r1bq1rk1/1p3p2/pbPp1n1p/n2Np1p1/4P1P1/PB1P1N1P/2P2P2/R1BQK2R b KQ - 0 13"));
    list.add(new PgnTestCase("04_capture_last_move_mate.pgn", "", "", 37, 16, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "r1b1n2k/1ppqNp1Q/pbnp3p/4pBp1/PP2P1P1/3P1N1P/2P2P2/R1B1K2R b KQ - 0 19"));
    return new PgnTestCaseList(PgnTest.BASIC_CAPTURE_LAST_MOVE, list);
  }

  private static PgnTestCaseList createTestCasesBasicMaxNoProgress() {
    final List<PgnTestCase> list = new ArrayList<>();
    list.add(new PgnTestCase("05_capture_last_move_piece_more_moves_max_no_progress_unchanged.pgn", "", "", 21, 4,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "r1b1r2k/1ppq1p2/pbnp1N1p/4p1p1/1PB1P1PP/P2P1N2/2P2P2/R1BQK2R b KQ - 4 14"));
    list.add(new PgnTestCase("06_capture_last_move_en_passant_capture_more_moves_max_no_progress_unchanged.pgn", "", "",
        25, 4, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "Q1b2r2/2b2pk1/p2p1nNp/n3p1p1/4P1P1/PB1P1N1P/2Q2P2/R1B1K2R b KQ - 4 18"));
    list.add(new PgnTestCase("07_capture_last_move_piece_more_moves_max_no_progress_changed.pgn",
        "repPos=3: 11...Kg7 13...Kg7 15...Kg7", "", 21, 9, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "r1bq1r2/1pp2pk1/pbnp1N1p/4p1p1/1PB1P1P1/P2P1N1P/2P2P2/R1BQK2R w KQ - 9 16"));
    list.add(new PgnTestCase("08_capture_last_move_en_passant_capture_more_moves_max_no_progress_changed.pgn", "", "",
        25, 9, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "r1bq1rk1/1p3p2/1bPp3p/p2Np1pn/B2NP1P1/P2Pn2P/2PQKP2/R1B3R1 b - - 9 19"));
    return new PgnTestCaseList(PgnTest.BASIC_REPORT_MAX_NO_PROGRESS, list);
  }

  private static PgnTestCaseList createTestCasesSpecial() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("special_nigel_short_tweet_white_to_move.pgn", "", "", 5, 13, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "K1n5/2k5/P7/8/8/8/8/8 w - - 1 37"));
    list.add(new PgnTestCase("special_nigel_short_tweet_black_to_move.pgn", "", "", 3, 12, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "K1n5/2k5/P7/8/8/8/8/8 b - - 0 44"));

    // king against all
    list.add(new PgnTestCase("special_king_against_all_white_only_king_white_to_move.pgn", "", "", 6, 8,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.WHITE_ONLY, "rnbqkbnr/pppppppp/8/8/8/4K3/8/8 w kq - 8 33"));
    list.add(new PgnTestCase("special_king_against_all_white_only_king_black_to_move.pgn", "", "", 6, 9,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.WHITE_ONLY, "rnbqkbnr/pppppppp/8/8/8/4K3/8/8 b kq - 9 33"));
    list.add(new PgnTestCase("special_king_against_all_black_only_king_black_to_move.pgn", "", "", 5, 10,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.BLACK_ONLY, "8/8/3k4/8/8/8/PPPPPPPP/RNBQKBNR b KQ - 10 36"));
    list.add(new PgnTestCase("special_king_against_all_black_only_king_white_to_move.pgn", "", "", 5, 11,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.BLACK_ONLY, "8/8/8/3k4/8/8/PPPPPPPP/RNBQKBNR w KQ - 11 37"));

    // norgaard buchanan
    list.add(new PgnTestCase("special_norgaard_buchanan_strategems_2002_alive.pgn", "", "", 10, 9,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "Bb2kb2/bKp1p1p1/1pP1P1P1/pP6/6P1/P7/8/8 b - - 2 46"));
    list.add(new PgnTestCase("special_norgaard_buchanan_strategems_2002_alive_played_out.pgn", "", "", 10, 19,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE,
        "1bK1kb2/b1p1pBp1/1pP1P1P1/1P4P1/p7/P7/8/8 b - - 1 57"));

    list.add(new PgnTestCase("special_norgaard_buchanan_strategems_2002_dead.pgn", "repPos=3: 74.Bc8 84.Bc8 86.Bc8", "",
        14, 49, CheckmateOrStalemate.NA, 3, InsufficientMaterial.NONE,
        "KbB1kb2/b1p1p1p1/1pP1P1P1/1P4P1/p7/P7/8/8 b - - 26 86"));

    list.add(new PgnTestCase("special_forced_checkmate_white_only.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "k1r5/2P5/KP6/P7/8/n7/6R1/7B w - - 0 50"));

    list.add(new PgnTestCase("special_both_kings_blocked_on_the_side_with_pawns.pgn", "", "", 11, 8,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "8/6p1/1p3pP1/1P3Ppk/1Pp3p1/KpP3P1/1P6/8 w - - 0 44"));

    list.add(new PgnTestCase(
        "special_both_kings_blocked_in_a_corner_with_pawns_and_bishop_pawn_moves_not_exhausted.pgn", "", "", 24, 19,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "k1b5/1p1p4/1PpP4/8/8/1pPp4/1P1P4/K1B5 w - - 1 36"));

    list.add(new PgnTestCase("special_both_kings_blocked_in_a_corner_with_pawns_and_bishop_pawn_moves_exhausted.pgn",
        "", "", 24, 19, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "k1b5/1p1p4/1P1P4/2p5/2P5/1p1p4/1P1P4/K1B5 w - - 0 37"));

    list.add(new PgnTestCase("special_white_king_blocked_in_a_corner_with_pawns_and_bishop_no_piece_capturable.pgn", "",
        "", 24, 19, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "k7/8/8/8/2p5/1pPp4/1P1P4/K1B5 w - - 1 43"));

    list.add(new PgnTestCase("special_white_king_trapped_on_side_with_pawns_capturing_only_piece_stalemates.pgn", "",
        "", 11, 8, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "k7/8/1p6/1P6/1Pp5/KpP5/1P6/8 w - - 0 37"));

    return new PgnTestCaseList(PgnTest.SPECIAL, list);
  }

  private static PgnTestCaseList createTestCasesChaLichessQuickNotDepthThree() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("lichess_V7eJ1RR9.pgn", "", "", 14, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "1k6/2P5/K7/2q5/8/8/8/8 b - - 0 56"));

    list.add(new PgnTestCase("lichess_02dMeCVV.pgn", "", "", 19, 32, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/4k3/2p4p/p1Pp1p1P/P2P1P2/8/6K1 w - - 32 58"));
    list.add(new PgnTestCase("lichess_03VIxJUJ.pgn", "", "", 6, 13, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/1pp5/k1b5/Qr6/8/8/p1K5/8 b - - 1 55"));
    list.add(new PgnTestCase("lichess_0dKUIfwq.pgn", "", "", 6, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "7K/6qP/8/8/8/4k3/8/8 w - - 1 76"));
    list.add(new PgnTestCase("lichess_0ehvDno9.pgn", "", "", 6, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/4RP2/8/P6k/PB6/4q1K1 w - - 0 47"));
    list.add(new PgnTestCase("lichess_0pZSCKZV.pgn", "", "", 11, 12, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/5k2/1p2p3/1P2Pp1p/3K1P1P/8/8/8 w - - 1 56"));
    list.add(new PgnTestCase("lichess_0u6eh8ZP.pgn", "", "", 9, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/1k6/8/Kp6/1P6/8/8/8 w - - 0 51"));
    list.add(new PgnTestCase("lichess_0xMzcUDT.pgn", "", "", 21, 14, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "2K5/2q5/4P3/8/5P2/3P4/k7/8 w - - 0 68"));
    list.add(new PgnTestCase("lichess_0yJiwUDU.pgn", "", "", 25, 46, CheckmateOrStalemate.NA, 2,
        InsufficientMaterial.NONE, "8/8/2p1k2p/p1Pp2pP/P2P2P1/8/3K4/8 b - - 46 71"));
    list.add(new PgnTestCase("lichess_14SOSdb3.pgn", "", "", 22, 14, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/1k1p3p/2pPp2p/p1P1P2P/P3K3/8/8 b - - 5 59"));
    list.add(new PgnTestCase("lichess_16myWOwv.pgn", "", "", 15, 10, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/8/2p5/8/2n2K2/3kQ3 b - - 2 61"));
    list.add(new PgnTestCase("lichess_1rcB0Tru.pgn", "", "", 10, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "6Rk/8/2b1p2K/pp2r3/2p5/8/8/8 b - - 1 42"));
    list.add(new PgnTestCase("lichess_1TWhVJGR.pgn", "", "", 12, 11, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "7k/5K1P/8/8/8/8/8/8 b - - 2 61"));
    list.add(new PgnTestCase("lichess_29moHRC8.pgn", "", "", 10, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/6p1/7p/6p1/4K1Qk/8/8/8 b - - 5 52"));
    list.add(new PgnTestCase("lichess_2gCvxApk.pgn", "", "", 16, 21, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/4Q3/2k5/P7/6q1/6K1 w - - 0 61"));
    list.add(new PgnTestCase("lichess_2k5piUl6.pgn", "", "", 14, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "R7/6k1/8/8/8/1P6/KPP5/r7 w - - 1 50"));
    list.add(new PgnTestCase("lichess_2Lb1nhb5.pgn", "", "", 4, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "k7/5R2/1Q6/8/2P5/6Pp/P4PKP/8 w - - 0 50"));
    list.add(new PgnTestCase("lichess_2mgjVvcC.pgn", "", "", 10, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3Q4/2p4p/p6k/5pp1/3b3K/6P1/PP5P/5q2 w - - 0 32"));
    list.add(new PgnTestCase("lichess_2nK9yweT.pgn", "", "", 14, 16, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/2p1k3/p1p1p1p1/P1PpP1P1/2bP1K2/8/8 b - - 16 60"));
    list.add(new PgnTestCase("lichess_2pwjX9B1.pgn", "", "", 5, 10, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "6Q1/7k/p7/1p4K1/8/5r2/8/4qr2 b - - 0 47"));
    list.add(new PgnTestCase("lichess_2sFHWmg3.pgn", "", "", 3, 11, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "7b/5k1K/7P/8/8/8/8/8 w - - 0 57"));
    list.add(new PgnTestCase("lichess_2wVTK5vV.pgn", "", "", 11, 14, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "5qK1/7P/8/k7/8/8/8/8 w - - 3 62"));
    list.add(new PgnTestCase("lichess_33Tpyihf.pgn", "", "", 7, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/2p5/8/2PK4/3Q4/7Q/2k5 b - - 0 53"));
    list.add(new PgnTestCase("lichess_3bKyjTHM.pgn", "", "", 18, 10, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/p7/5pp1/8/Qp3K2/k7 b - - 3 54"));
    list.add(new PgnTestCase("lichess_3cYOGNS6.pgn", "", "", 11, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "Q7/8/4Q3/7k/5Pp1/5KP1/7P/8 w - - 0 43"));
    list.add(new PgnTestCase("lichess_3jesiit8.pgn", "", "", 11, 22, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/8/8/3Q4/P1P2PP1/1k4Kq w - - 22 54"));
    list.add(new PgnTestCase("lichess_3sU6yQo5.pgn", "", "", 35, 64, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/1k6/6p1/3p1pP1/p1pP1Pp1/P1P3P1/8/5K2 b - - 24 111"));
    list.add(new PgnTestCase("lichess_3uoeIjQi.pgn", "", "", 17, 49, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/1k6/p1p1p1p1/P1P1P1Pb/7K/4B3/8 w - - 49 80"));
    list.add(new PgnTestCase("lichess_3xFc21eB.pgn", "", "", 15, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "6Kr/4k1P1/8/8/8/5R2/8/8 w - - 4 53"));
    list.add(new PgnTestCase("lichess_43ghpltO.pgn", "", "", 12, 22, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/7k/8/p2p1p1p/P2P1P1P/2K5/8/8 w - - 22 71"));
    list.add(new PgnTestCase("lichess_47GuK7dy.pgn", "", "", 16, 19, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "7k/7P/5K2/8/8/8/8/8 b - - 2 116"));
    list.add(new PgnTestCase("lichess_49QwULSR.pgn", "", "", 10, 11, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/6K1/8/6p1/7p/7k/6Q1/8 b - - 3 64"));
    list.add(new PgnTestCase("lichess_4KKvXluk.pgn", "", "", 5, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/5k2/1p1p4/1P1Pp1p1/4P1P1/3K2P1/8 w - - 2 51"));
    list.add(new PgnTestCase("lichess_55wBEu8Z.pgn", "", "", 11, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/p1r5/5R1p/Pp1p2qk/3PpK1P/4Pp1Q/5P2/8 w - - 9 43"));
    list.add(new PgnTestCase("lichess_5inggIqz.pgn", "", "", 3, 18, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/2k3p1/p1p1p1P1/P1P1P1P1/3K4/8 w - - 11 60"));
    list.add(new PgnTestCase("lichess_5YEU1wHP.pgn", "", "", 10, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/2b5/4pkp1/8/5pP1/4q3/3r4/5K2 w - - 2 41"));
    list.add(new PgnTestCase("lichess_6HdGxhSR.pgn", "", "", 13, 15, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/p1p2p1k/P1P2P1p/3K3P/8/8 w - - 15 53"));
    list.add(new PgnTestCase("lichess_6kfJPvsA.pgn", "", "", 6, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/7p/8/1K6/8/6p1/7k/7Q b - - 1 57"));
    list.add(new PgnTestCase("lichess_6u1akjTw.pgn", "", "", 31, 38, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/4k3/p1p2p1p/PpP2P1P/1P2K3/8 b - - 38 90"));
    list.add(new PgnTestCase("lichess_6XQZaBGI.pgn", "", "", 14, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/4bkp1/8/K2nP2p/3r4/8/8/5q2 w - - 0 50"));
    list.add(new PgnTestCase("lichess_78XhRaIr.pgn", "", "", 32, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/6qK/7P/8/8/2k5/8/8 w - - 0 73"));
    list.add(new PgnTestCase("lichess_7alMRe6y.pgn", "", "", 3, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "2kR4/2p5/1pK2p2/r3p3/8/8/8/8 b - - 0 47"));
    list.add(new PgnTestCase("lichess_7CxnEuV4.pgn", "", "", 15, 42, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/p5k1/Pp1p1p1p/1P1P1P1P/1K6/8/8 w - - 42 68"));
    list.add(new PgnTestCase("lichess_7fWsFh0J.pgn", "", "", 6, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/8/5K2/6pk/7R/7q b - - 1 60"));
    list.add(new PgnTestCase("lichess_7uopV5nZ.pgn", "", "", 18, 33, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "6k1/8/8/1p1p2p1/1P1P2P1/5K2/8/8 w - - 33 59"));
    list.add(new PgnTestCase("lichess_83SCI1Fg.pgn", "", "", 9, 48, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "6k1/8/8/1p2p1p1/pPp1P1P1/P1P5/8/K7 w - - 48 78"));
    list.add(new PgnTestCase("lichess_85kfBN8B.pgn", "", "", 7, 46, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/2k5/1p6/1Pp1p3/2P1Pp1p/5P1P/8/5K2 b - - 46 75"));
    list.add(new PgnTestCase("lichess_87JajHeg.pgn", "", "", 6, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/3k4/1p2p2p/1P2P2P/8/6K1/8 b - - 0 50"));
    list.add(new PgnTestCase("lichess_8B3XZBsv.pgn", "", "", 14, 73, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/2k5/4p3/p1p1P1p1/P1P3P1/8/1K6/8 b - - 73 83"));
    list.add(new PgnTestCase("lichess_8cTl7SrQ.pgn", "", "", 15, 15, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/7R/8/5bp1/6rk/5n2/5K2/8 b - - 0 48"));
    list.add(new PgnTestCase("lichess_8FUSHxUV.pgn", "", "", 6, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "k7/P1K5/8/8/8/8/8/8 b - - 2 58"));
    list.add(new PgnTestCase("lichess_8sompKsV.pgn", "", "", 8, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/4R3/5p1k/5Pp1/6Pp/8/6KP/7B b - - 3 60"));
    list.add(new PgnTestCase("lichess_8TigzhbJ.pgn", "repPos=3: 22...Bd7 24...Rac8 26...Rac8", "", 22, 90,
        CheckmateOrStalemate.NA, 2, InsufficientMaterial.NONE, "8/2k4p/1p2p1pP/1P1pPpP1/3P1P2/8/8/K7 b - - 90 94"));
    list.add(new PgnTestCase("lichess_8uFm7Zvw.pgn", "", "", 9, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3R4/5p1k/1N3Q2/7q/5P1K/1P4P1/7P/8 w - - 5 45"));
    list.add(new PgnTestCase("lichess_992nwupc.pgn", "", "", 18, 23, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/1k6/4p3/3pP1p1/1p1P2P1/1P6/8/1K6 b - - 23 56"));
    list.add(new PgnTestCase("lichess_9AqVp2F8.pgn", "", "", 16, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/5p2/8/p4pK1/k4N2/1R6/P7/8 b - - 0 55"));
    list.add(new PgnTestCase("lichess_9FpfMBVq.pgn", "", "", 7, 11, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/p7/Pp1p1p1k/1P1P1Pp1/3b2P1/3K1B2/8 b - - 4 54"));
    list.add(new PgnTestCase("lichess_9jxoYRyn.pgn", "", "", 9, 37, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/6k1/5p2/1p2pP1p/1P2P2P/7K/8/8 b - - 37 72"));
    list.add(new PgnTestCase("lichess_9T0wrl30.pgn", "", "", 20, 26, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/p7/Pp1k2p1/1P1p1pPp/3P1P1P/6K1/8/8 b - - 26 52"));
    list.add(new PgnTestCase("lichess_9vMq768Z.pgn", "", "", 5, 17, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "1rK1k3/2P2R2/4Pp2/5P2/8/8/8/8 w - - 17 66"));

    list.add(new PgnTestCase("lichess_a2l4gphm.pgn", "", "", 37, 20, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "5k2/8/3p4/3p1p1p/p1pP1P1P/PpP2K2/1P5B/8 w - - 20 67"));

    list.add(new PgnTestCase("lichess_A4aWYoPF.pgn", "", "", 10, 30, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/8/5p2/p2p1Pp1/P2P2Pp/1K5P/8/8 w - - 30 62"));
    list.add(new PgnTestCase("lichess_aefWMnIP.pgn", "", "", 3, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/5pk1/5Pp1/7p/7P/2r3PK/1q6/8 b - - 0 51"));
    list.add(new PgnTestCase("lichess_AHPAU56z.pgn", "", "", 5, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/p6p/5kp1/5pP1/5P1K/1r5P/8/8 b - - 0 47"));
    list.add(new PgnTestCase("lichess_AigeGQsz.pgn", "", "", 12, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/5kp1/2r2pP1/5K2/1q6/2q5/8 b - - 0 54"));
    list.add(new PgnTestCase("lichess_Altbmg1n.pgn", "", "", 6, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/4k2p/1p2p1pP/pP2P1P1/P3K3/8/8 b - - 2 42"));
    list.add(new PgnTestCase("lichess_aoXPcDW6.pgn", "", "", 6, 34, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/5k2/1p2p1p1/pP2P1P1/P7/4K3/8 b - - 34 55"));
    list.add(new PgnTestCase("lichess_APEww4mD.pgn", "", "", 5, 31, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/8/8/5K2/2r4p/6Rk b - - 31 84"));
    list.add(new PgnTestCase("lichess_aWI97MHZ.pgn", "", "", 18, 16, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/1k1p2p1/2pPp1P1/1pP1P1K1/1P6/8 w - - 16 51"));
    list.add(new PgnTestCase("lichess_aXUqSEe1.pgn", "", "", 5, 20, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/6k1/1p1p1p1p/1P1P1P1P/8/8/7K w - - 0 70"));
    list.add(new PgnTestCase("lichess_B3mIj9Su.pgn", "", "", 18, 21, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/5p2/8/4p3/3r2p1/4K1kR b - - 21 75"));
    list.add(new PgnTestCase("lichess_B8bpMZ71.pgn", "", "", 11, 13, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "2Q5/8/5Q2/6B1/8/1P2P1P1/P3kPq1/5RK1 w - - 1 55"));
    list.add(new PgnTestCase("lichess_bDxnc7TH.pgn", "", "", 14, 12, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/1p1k2p1/pP1p1pP1/P2P1P2/3K4/8/8 b - - 2 58"));
    list.add(new PgnTestCase("lichess_BFfZguIQ.pgn", "", "", 10, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/p4pk1/3b4/5Pp1/6q1/8/8/7K w - - 2 43"));
    list.add(new PgnTestCase("lichess_bfJbt6GP.pgn", "", "", 7, 12, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/7p/4npp1/1pk5/1P6/2K5/1r1r4/8 b - - 0 52"));
    list.add(new PgnTestCase("lichess_bQ0EXLal.pgn", "", "", 10, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "1b3Rk1/1p4pp/p7/8/8/7r/2K5/8 b - - 1 47"));
    list.add(new PgnTestCase("lichess_bXkWOZ49.pgn", "", "", 3, 20, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/3k4/6p1/3p1pP1/p1pP1P1K/P1P5/8/8 w - - 20 64"));
    list.add(new PgnTestCase("lichess_c3ew66ZV.pgn", "", "", 9, 32, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/3k4/1p2p1p1/pP1pP1P1/P2P4/1K6/8 b - - 32 62"));
    list.add(new PgnTestCase("lichess_CezFfltE.pgn", "", "", 11, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/p1p1k1p1/P1P1p1P1/4Pp1p/2K2P1P/8 b - - 4 48"));
    list.add(new PgnTestCase("lichess_cLnhkfGh.pgn", "", "", 16, 10, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/4k3/1p1p4/1p1P1p2/1P3P1p/4K2P/8/8 w - - 2 50"));
    list.add(new PgnTestCase("lichess_CQI03gYb.pgn", "", "", 5, 10, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/8/1p6/1p1p2p1/1P1Pp1P1/1P2P3/8/4K3 w - - 10 46"));
    list.add(new PgnTestCase("lichess_cZ8QxYP8.pgn", "", "", 7, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/k7/7P/8/1PPPPbP1/1NBQKBNR w K - 0 23"));
    list.add(new PgnTestCase("lichess_D342JjRk.pgn", "", "", 14, 70, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/6k1/1p1p4/1PpPp1p1/2P1P1p1/6P1/8/5K2 w - - 40 100"));
    list.add(new PgnTestCase("lichess_D4vIOthl.pgn", "", "", 6, 13, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/6Q1/7k/7p/3K4/8/6p1/8 b - - 0 65"));
    list.add(new PgnTestCase("lichess_dBergEkt.pgn", "", "", 17, 12, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "1K4Qk/8/8/8/6p1/5p2/8/8 b - - 0 55"));
    list.add(new PgnTestCase("lichess_DiKq9jcy.pgn", "", "", 9, 14, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/6kP/3Q2P1/3qP3/1BK5/2P5/8/8 w - - 3 65"));
    list.add(new PgnTestCase("lichess_DQxKRa2g.pgn", "", "", 11, 36, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/6p1/k2p1pP1/1p1P1P2/pP6/P7/2K5 b - - 15 68"));
    list.add(new PgnTestCase("lichess_dZhy51vS.pgn", "", "", 12, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "2R5/8/1p6/kP6/p6p/P7/6KP/1R6 b - - 1 48"));
    list.add(new PgnTestCase("lichess_E0TisOA3.pgn", "", "", 4, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/5Q2/4R3/3p2k1/3P3p/P4PKP/8 w - - 0 55"));
    list.add(new PgnTestCase("lichess_E8I7AMGj.pgn", "", "", 10, 18, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3k4/2p5/1pPp2p1/1P1P1pPp/5P1P/2K5/8/8 w - - 18 58"));
    list.add(new PgnTestCase("lichess_E9P918aj.pgn", "", "", 18, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/8/3k2qK/7P/8/8 w - - 0 51"));
    list.add(new PgnTestCase("lichess_eC0QpBxu.pgn", "", "", 9, 47, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/3k4/p2p2p1/P2P2P1/8/8/1K6/8 b - - 47 77"));
    list.add(new PgnTestCase("lichess_eExHpNDf.pgn", "", "", 11, 34, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3NR3/4P3/8/8/4k3/6P1/6q1/6K1 w - - 17 84"));
    list.add(new PgnTestCase("lichess_efAanuaT.pgn", "", "", 10, 54, CheckmateOrStalemate.NA, 2,
        InsufficientMaterial.NONE, "8/4k3/3p2p1/p1pP2P1/P1P5/4K3/8/8 b - - 35 89"));
    list.add(new PgnTestCase("lichess_EFd9NOSF.pgn", "", "", 15, 49, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/p2k4/Pp1p2p1/1P1Pp1P1/4P3/8/6K1 b - - 49 89"));
    list.add(new PgnTestCase("lichess_eKwKl6Y9.pgn", "", "", 17, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/7P/8/6qK/8/2k5 w - - 0 70"));
    list.add(new PgnTestCase("lichess_EPTBN53W.pgn", "", "", 3, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/8/5k1p/7K w - - 2 58"));
    list.add(new PgnTestCase("lichess_EQCMW0jB.pgn", "", "", 6, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/7r/p4K1k/8/7R/6q1 b - - 0 58"));
    list.add(new PgnTestCase("lichess_EsFX1Urt.pgn", "", "", 7, 10, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/7p/4K2k/6R1/8/8/8 b - - 0 54"));
    list.add(new PgnTestCase("lichess_EstAIWqd.pgn", "", "", 15, 13, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/8/8/1K4p1/6rk/7R b - - 0 65"));
    list.add(new PgnTestCase("lichess_Ew7uTqu0.pgn", "", "", 13, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "6kR/p4pp1/6b1/8/5K2/r7/8/8 b - - 3 48"));
    list.add(new PgnTestCase("lichess_f7x3bSzh.pgn", "", "", 12, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "k6r/p4p1p/2b3p1/1B6/8/N3b1P1/P4p1P/1R5K w - - 0 34"));
    list.add(new PgnTestCase("lichess_fAjvL1hG.pgn", "", "", 15, 52, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/7k/1p2p1p1/pP2P1Pp/P6P/8/2K5/8 w - - 52 75"));
    list.add(new PgnTestCase("lichess_FBqHYgEu.pgn",
        "repPos=3: 40.Ke2 68.Ke2 70.Ke2; repPos=3: 40...Kd6 68...Kd6 70...Kd6", "", 23, 88, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/4k3/p1p2p1p/PpP2P1P/1P6/1K6/8 b - - 22 96"));
    list.add(new PgnTestCase("lichess_fbzlzlUK.pgn", "", "", 4, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "5b2/5pk1/3r1P2/2n4K/4b3/6q1/8/8 b - - 0 55"));
    list.add(new PgnTestCase("lichess_FMDN8gJ7.pgn", "", "", 5, 12, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/2p5/2kb4/1Q6/8/4K3 b - - 12 68"));
    list.add(new PgnTestCase("lichess_fN4vRqp0.pgn", "", "", 5, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/6Q1/8/7k/5p2/5RK1 w - - 0 73"));
    list.add(new PgnTestCase("lichess_fPnoYQtW.pgn", "", "", 11, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/1K5p/8/8/6pQ/7k b - - 4 52"));
    list.add(new PgnTestCase("lichess_FSZeGcrT.pgn", "", "", 5, 60, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/k7/2p1p3/1pP1P1p1/pP4P1/P7/2K5 w - - 60 84"));
    list.add(new PgnTestCase("lichess_fuNe9BZw.pgn", "", "", 3, 97, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/4k3/p1p2p1p/P1P2P1P/8/3K4/8 w - - 19 95"));
    list.add(new PgnTestCase("lichess_FYTeO80q.pgn", "", "", 7, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/6Qp/p3B1p1/7k/P4Pq1/7K/1P3R1P/8 w - - 0 38"));
    list.add(new PgnTestCase("lichess_G0xDBYwE.pgn", "", "", 6, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "7k/8/8/KqN5/2P5/8/8/8 w - - 0 59"));
    list.add(new PgnTestCase("lichess_G8i5Bn7F.pgn", "", "", 11, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "7Q/7k/5K2/8/8/8/6p1/8 b - - 2 57"));
    list.add(new PgnTestCase("lichess_gcJZWCKq.pgn", "", "", 6, 19, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "7k/7P/5K2/8/8/8/8/8 b - - 0 79"));
    list.add(new PgnTestCase("lichess_GCuwJ5bP.pgn", "", "", 6, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/Kq6/P5k1/8/8/8 w - - 0 52"));
    list.add(new PgnTestCase("lichess_GgiPdCBU.pgn", "", "", 19, 10, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/1p1k4/1Pp1p1p1/p1P1PpP1/P4P2/8/5K2 b - - 10 48"));
    list.add(new PgnTestCase("lichess_GH6YBJIu.pgn", "", "", 12, 14, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/2p2k2/p1Pp2p1/P2P2P1/1K6/8/8 b - - 9 52"));
    list.add(new PgnTestCase("lichess_GibMYT4i.pgn", "", "", 26, 46, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/4k3/4b3/1p1p1p1p/1P1P1P1P/8/8/7K w - - 46 78"));
    list.add(new PgnTestCase("lichess_GIwBNz5S.pgn", "", "", 14, 11, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/1p6/p7/8/3K4/5b2/5kR1 b - - 0 56"));
    list.add(new PgnTestCase("lichess_GpCPpsRz.pgn", "", "", 28, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "7k/5Q2/8/p1p1NBP1/P1P2P2/7p/6KP/8 w - - 0 47"));
    list.add(new PgnTestCase("lichess_gPxJdo7u.pgn", "", "", 17, 45, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/1k6/1p2p1p1/1P2P1P1/8/8/6K1/8 b - - 40 92"));
    list.add(new PgnTestCase("lichess_gtzPUYWo.pgn", "", "", 13, 10, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/8/4pp2/3Rkp2/4r3/5K2 b - - 0 66"));
    list.add(new PgnTestCase("lichess_Gw5OAmSo.pgn", "", "", 16, 13, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/3k4/3p4/p1pPp2p/P1P1P2P/8/2K5/8 w - - 13 64"));
    list.add(new PgnTestCase("lichess_GysBobWK.pgn", "", "", 11, 31, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/5k2/1p1p2p1/1P1P2P1/8/4K3/8 w - - 31 73"));
    list.add(new PgnTestCase("lichess_GzoCTk46.pgn", "", "", 9, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/7p/5p2/5kp1/4Q3/8/4K3 b - - 5 58"));
    list.add(new PgnTestCase("lichess_h2INNt6T.pgn", "", "", 3, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "7R/6pk/6rp/p6K/P6P/8/8/6r1 b - - 3 40"));
    list.add(new PgnTestCase("lichess_H4MIp43q.pgn", "", "", 5, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/1k6/p1p3p1/P1Pp1pP1/K2P1P2/8/8/8 b - - 2 53"));
    list.add(new PgnTestCase("lichess_H968wWnH.pgn", "", "", 3, 15, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/5k2/8/2p2p2/p1P2P1p/P3K2P/8/8 w - - 15 51"));
    list.add(new PgnTestCase("lichess_hdott1S4.pgn", "", "", 25, 11, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/5R2/6PN/5B1K/5k2/7r/8 w - - 1 59"));
    list.add(new PgnTestCase("lichess_hHLqO2Sb.pgn", "", "", 5, 49, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/2k4p/1p1p2pP/1P1P2P1/8/5K2/8 b - - 49 67"));
    list.add(new PgnTestCase("lichess_hkiG96je.pgn", "", "", 10, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "6qK/8/7P/8/8/k7/8/8 w - - 0 59"));
    list.add(new PgnTestCase("lichess_hRedYjtT.pgn", "", "", 7, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "7R/6pk/p5p1/8/1b2p3/4P3/b1r5/K7 b - - 5 39"));
    list.add(new PgnTestCase("lichess_HX7UOzXi.pgn", "", "", 20, 25, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/8/5k1p/7K w - - 2 85"));
    list.add(new PgnTestCase("lichess_I0zJt0qZ.pgn", "", "", 13, 22, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/2k4p/1p2p1pP/1P2P1P1/8/3K4 b - - 15 79"));
    list.add(new PgnTestCase("lichess_I9MLIaiu.pgn", "", "", 20, 29, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/4k3/4p2p/p1p1P2P/P1P2K2/8/8/8 w - - 29 68"));
    list.add(new PgnTestCase("lichess_IcjtzT9i.pgn", "", "", 25, 26, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/8/2k3qK/5P1P/8/8 w - - 0 77"));
    list.add(new PgnTestCase("lichess_IdZxNXzF.pgn", "", "", 23, 19, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/5k2/7p/7K w - - 0 79"));
    list.add(new PgnTestCase("lichess_if6AKvpb.pgn", "", "", 5, 11, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/3k4/1P2R3/4KP2/P2q4/8 w - - 7 51"));
    list.add(new PgnTestCase("lichess_ig7fudY7.pgn", "", "", 8, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/8/5K1R/1r5k/6p1/8 b - - 0 53"));
    list.add(new PgnTestCase("lichess_iNx1gFMc.pgn", "", "", 26, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/1p2k1p1/pP1p1pPp/P2P1P1P/5K2/8/8 b - - 0 49"));
    list.add(new PgnTestCase("lichess_Ir1xVUbe.pgn", "", "", 12, 11, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/3k4/p2p2p1/P2P2P1/1K6/8/8 b - - 7 55"));
    list.add(new PgnTestCase("lichess_IT4IGHvQ.pgn", "", "", 17, 21, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/p1k3p1/P1p2pPp/2P2P1P/4K3/8 b - - 21 48"));
    list.add(new PgnTestCase("lichess_Ity8OMJy.pgn", "", "", 9, 10, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/4k2p/p1p2p1P/P1P2Pp1/4K1P1/8/8 w - - 10 53"));
    list.add(new PgnTestCase("lichess_Iy1VEUMC.pgn", "", "", 9, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3Kq3/2P3k1/8/8/P7/8/8/8 w - - 4 59"));
    list.add(new PgnTestCase("lichess_j17qnxd0.pgn", "", "", 16, 38, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4K2k/6Q1/7p/8/8/8/8/8 b - - 38 67"));
    list.add(new PgnTestCase("lichess_j4phmaNj.pgn", "", "", 11, 18, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "5Kq1/8/4k3/8/7P/8/8/8 w - - 1 67"));
    list.add(new PgnTestCase("lichess_jehHS0Jj.pgn", "", "", 10, 11, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/6k1/8/6qK/8/7P/8/8 w - - 0 65"));
    list.add(new PgnTestCase("lichess_JfHZLRvD.pgn", "", "", 6, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r7/pp6/3p1p2/2p1k3/2q1Q2P/5KP1/5P2/3R3R b - - 0 33"));
    list.add(new PgnTestCase("lichess_JIyquz2Q.pgn", "", "", 8, 25, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/2k5/6p1/1p1p1pPp/pP1P1P1P/P7/5K2/8 b - - 25 55"));
    list.add(new PgnTestCase("lichess_jK46bYGo.pgn", "", "", 12, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "6kQ/p4rp1/4r3/3K4/8/8/8/8 b - - 6 48"));
    list.add(new PgnTestCase("lichess_jnox9kxK.pgn", "", "", 36, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/5K2/8/p7/1Q6/k1q5 b - - 0 86"));
    list.add(new PgnTestCase("lichess_jnsJhG8T.pgn", "", "", 27, 48, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/1k6/1p1pB3/pPpP4/P1Pp1p1p/3PbP1P/6K1/8 w - - 48 85"));
    list.add(new PgnTestCase("lichess_jnvJUlDw.pgn", "", "", 11, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/2p4Q/3rn1pk/6p1/2K5/8/8/8 b - - 2 48"));
    list.add(new PgnTestCase("lichess_JQpTTEF7.pgn", "", "", 5, 10, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/2k5/p1p5/P1P1p1p1/P3P1Pp/4K2P/8/8 w - - 1 40"));
    list.add(new PgnTestCase("lichess_K5d1o8ea.pgn", "", "", 11, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/8/5k1p/7K w - - 2 61"));
    list.add(new PgnTestCase("lichess_KbEG3yK3.pgn", "", "", 20, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/7p/8/8/8/b4K2/kQ6 b - - 0 61"));
    list.add(new PgnTestCase("lichess_kbutR5IJ.pgn", "", "", 8, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "2k1Q3/2p5/2K5/2N5/2q5/4q3/8/8 b - - 0 53"));
    list.add(new PgnTestCase("lichess_Kdf0f1OE.pgn", "", "", 21, 11, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/8/5K2/7p/7k/6R1 b - - 0 69"));
    list.add(new PgnTestCase("lichess_KE47c49r.pgn", "", "", 6, 11, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "7R/5ppk/6bp/8/4r3/2K5/8/8 b - - 11 46"));
    list.add(new PgnTestCase("lichess_KeK8Chul.pgn", "", "", 17, 14, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/2k5/1p1p2p1/1P1P2Pp/7P/8/6K1 b - - 14 52"));
    list.add(new PgnTestCase("lichess_kj6Dk7Fb.pgn", "", "", 5, 39, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/6k1/p2p1p1p/P2P1P1P/8/8/4K3 b - - 39 64"));
    list.add(new PgnTestCase("lichess_knQsF7R8.pgn", "", "", 7, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "5qK1/3k3P/8/8/6P1/8/8/8 w - - 5 49"));
    list.add(new PgnTestCase("lichess_KP23RUWa.pgn", "", "", 11, 17, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/5k2/7K/8/1Q5q/P7/8 w - - 17 61"));
    list.add(new PgnTestCase("lichess_KPvcgsfS.pgn", "", "", 11, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "1rK5/2R5/4k3/8/8/6P1/P7/8 w - - 0 48"));
    list.add(new PgnTestCase("lichess_KQKPJzpe.pgn", "", "", 5, 11, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/k4p2/p1p2p1p/P1P2P1P/5K2/8 b - - 11 48"));
    list.add(new PgnTestCase("lichess_KQvOkgie.pgn", "", "", 12, 58, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/1k6/3p2p1/2pP1pP1/p1P2P2/P3K3/8/8 b - - 58 75"));
    list.add(new PgnTestCase("lichess_Ks0yR98N.pgn", "", "", 4, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "7Q/6P1/8/8/7r/5k1K/8/8 w - - 0 61"));
    list.add(new PgnTestCase("lichess_l8Wwo7if.pgn", "", "", 6, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/7p/3q1kp1/6P1/4K3/3r4/8/8 b - - 0 56"));
    list.add(new PgnTestCase("lichess_LbP5CIDy.pgn", "", "", 21, 39, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/2k1p1p1/1p1pPpPp/1P1P1P1P/1K6/8/8 b - - 38 105"));
    list.add(new PgnTestCase("lichess_LC76ur18.pgn", "", "", 3, 15, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/2p5/3b4/8/2K5/R7/kq6 b - - 1 65"));
    list.add(new PgnTestCase("lichess_Lf3eQe81.pgn", "", "", 3, 11, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/4k3/1p1p2p1/1P1P2P1/1P1K4/8/8 w - - 6 57"));
    list.add(new PgnTestCase("lichess_lHtqM0fz.pgn", "", "", 22, 19, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/8/6K1/8/7p/6Rk b - - 7 74"));
    list.add(new PgnTestCase("lichess_lk42iihk.pgn", "", "", 5, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/R7/8/8/6k1/6P1/5Q1K/7q w - - 2 42"));
    list.add(new PgnTestCase("lichess_LqTNOgyT.pgn", "", "", 23, 11, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/3Q4/8/P6P/5k2/8/3q1K2 w - - 0 53"));
    list.add(new PgnTestCase("lichess_LTFQx2YH.pgn", "", "", 7, 17, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/4k3/p1p2p1p/P1P2P1P/K7/8/8 b - - 17 66"));
    list.add(new PgnTestCase("lichess_Lv0hWAvD.pgn", "", "", 21, 34, CheckmateOrStalemate.NA, 2,
        InsufficientMaterial.NONE, "8/8/p4k1p/Pp1p2pP/1P1P2P1/4K3/8/8 b - - 4 67"));
    list.add(new PgnTestCase("lichess_lYf34Uiq.pgn", "", "", 14, 13, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/2K5/8/8/7p/5Qk1 b - - 5 69"));
    list.add(new PgnTestCase("lichess_m5WDCW16.pgn", "", "", 17, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/6k1/8/7R/8/6PP/5PBK/7r w - - 5 42"));
    list.add(new PgnTestCase("lichess_M9Xic74G.pgn", "", "", 15, 31, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "6k1/8/6p1/1p2p1Pp/1P2P2P/6K1/8/8 w - - 31 67"));
    list.add(new PgnTestCase("lichess_mBrCKInG.pgn", "", "", 9, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3r4/5pk1/p3pPp1/1p2P3/4K3/r7/3q4/8 b - - 0 45"));
    list.add(new PgnTestCase("lichess_McLow5Hz.pgn", "", "", 5, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/7P/8/4k3/4B3/5PR1/6PK/7r w - - 1 43"));
    list.add(new PgnTestCase("lichess_MgqytYeQ.pgn", "", "", 8, 15, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/6k1/5p2/1p2pP1p/1P2P2P/5K2/8/8 b - - 15 52"));
    list.add(new PgnTestCase("lichess_Mm4HIjDh.pgn", "", "", 18, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/2p1k1p1/1pP1p1P1/1P2P3/7K/8/8 b - - 5 43"));
    list.add(new PgnTestCase("lichess_MOyPDylu.pgn", "repPos=3: 46.Bc2 56.Bc2 60.Ke2", "", 14, 56,
        CheckmateOrStalemate.NA, 2, InsufficientMaterial.NONE, "8/8/8/k1p1p1p1/1pPpP1Pp/1P1PbK1P/2B5/8 b - - 12 80"));
    list.add(new PgnTestCase("lichess_MvEif0NV.pgn", "", "", 19, 27, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/2k3qK/6PP/8/8/8 w - - 0 67"));
    list.add(new PgnTestCase("lichess_MXeVCHSn.pgn", "", "", 3, 46, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "1k6/8/8/p1p2p2/P1P2P1p/2P2P1P/3K4/8 w - - 37 75"));
    list.add(new PgnTestCase("lichess_mXxGD2IV.pgn", "", "", 12, 90, CheckmateOrStalemate.NA, 2,
        InsufficientMaterial.NONE, "8/6k1/8/2p2p1p/1pP1pP1P/1P2P3/4K3/8 w - - 90 100"));
    list.add(new PgnTestCase("lichess_N6AYM22R.pgn", "", "", 4, 16, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/5pp1/6kp/7P/5n2/p7/6r1/7K b - - 0 58"));
    list.add(new PgnTestCase("lichess_ncuz3jy9.pgn", "", "", 6, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/7p/6pk/4p3/6P1/5q2/3K1P2/1q6 w - - 0 47"));
    list.add(new PgnTestCase("lichess_nECJszX7.pgn", "", "", 18, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/p1p5/Ppkp4/1P6/K7/2q5/7p/8 b - - 0 46"));
    list.add(new PgnTestCase("lichess_NmQfkdhK.pgn", "", "", 16, 11, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4Rk2/5p2/5Pp1/6P1/8/6p1/5r1r/6K1 b - - 6 52"));
    list.add(new PgnTestCase("lichess_nNXmBTlz.pgn", "", "", 5, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/p1k5/Pp1p2p1/1P1P2Pp/3K3P/8/8 b - - 0 46"));
    list.add(new PgnTestCase("lichess_nvxFBquo.pgn", "", "", 10, 10, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "7K/2P2k1r/3P4/8/8/6R1/8/8 w - - 0 64"));
    list.add(new PgnTestCase("lichess_o6TGucU0.pgn", "", "", 5, 20, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/p5kp/5n1P/8/7K/5q2/8/6r1 b - - 0 57"));
    list.add(new PgnTestCase("lichess_OCeJcw04.pgn", "", "", 24, 27, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/5k2/1p1p4/1P1P1p2/1K3Pp1/1P4P1/8/8 b - - 4 74"));
    list.add(new PgnTestCase("lichess_odke8ePj.pgn", "", "", 12, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/5pkp/8/7P/6p1/5pP1/5P1K/5q2 w - - 1 57"));
    list.add(new PgnTestCase("lichess_Oejr5ZFX.pgn", "", "", 19, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/5Q2/8/7R/6k1/P5p1/7K w - - 0 47"));
    list.add(new PgnTestCase("lichess_ogehooFw.pgn", "", "", 21, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/p1k5/P1p2p1p/2P2P1P/3K4/8/8 b - - 3 48"));
    list.add(new PgnTestCase("lichess_om0bCR5w.pgn", "", "", 15, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "R4k2/8/5K1p/8/8/6r1/8/7q b - - 1 48"));
    list.add(new PgnTestCase("lichess_Omq6Fdhm.pgn", "", "", 12, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/8/pp6/k7/1Q2K3/8 b - - 2 69"));
    list.add(new PgnTestCase("lichess_ooSZuhhE.pgn", "", "", 5, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "1Q6/2k1K3/2p5/8/8/8/8/8 b - - 0 40"));
    list.add(new PgnTestCase("lichess_oT7sOSw5.pgn", "", "", 11, 21, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/2p1k3/p1Pp2p1/Pp1P1pPp/1P3P1P/5P2/3K4 b - - 21 58"));
    list.add(new PgnTestCase("lichess_p2ujQM8o.pgn", "", "", 19, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/6pk/5rP1/7K/5q1P/5P2/8 b - - 0 39"));
    list.add(new PgnTestCase("lichess_p4CeCK3J.pgn", "", "", 6, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4Q3/8/8/2Q5/8/1P6/1KP5/q6k w - - 1 54"));
    list.add(new PgnTestCase("lichess_PAjZeTkN.pgn", "", "", 6, 18, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/Nk6/8/R7/Kr6/P7/8 w - - 0 64"));
    list.add(new PgnTestCase("lichess_pBFolKHd.pgn", "", "", 5, 33, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/1k6/3p3p/1p1P1p1P/1P2pP1K/4P3/8/8 w - - 33 67"));
    list.add(new PgnTestCase("lichess_pFsUTatm.pgn", "", "", 22, 27, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/5q2/2k1K3/2P1Q3/3P4/4P3/8/8 w - - 1 57"));
    list.add(new PgnTestCase("lichess_PHCKkpCX.pgn", "", "", 6, 51, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/1p2k3/1P1p2p1/3P2Pp/7P/1K6/8 b - - 51 70"));
    list.add(new PgnTestCase("lichess_pIgctI4e.pgn", "", "", 20, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "6k1/R7/4NB2/6p1/8/6PK/5P1P/8 b - - 0 40"));
    list.add(new PgnTestCase("lichess_PIL4PUtT.pgn", "", "", 19, 10, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/3Q4/2P5/1PB3k1/P7/5P1K/R4Rq1 w - - 0 45"));
    list.add(new PgnTestCase("lichess_PkZ6qiA6.pgn", "", "", 7, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/7p/3p4/2pk4/4Q3/p7/Kb6/7q b - - 5 50"));
    list.add(new PgnTestCase("lichess_pqiyPXvP.pgn", "", "", 10, 23, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/2k5/p2p2p1/Pp1Pp1P1/1P2P3/5K2/8 w - - 23 70"));
    list.add(new PgnTestCase("lichess_pqsImnAT.pgn", "", "", 6, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/4k3/1p1p2p1/pP1P2P1/P7/4K3/8/8 b - - 6 52"));
    list.add(new PgnTestCase("lichess_PUA2eCrY.pgn", "", "", 14, 25, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/3k4/1p2p1p1/1P1pP1Pp/1P1P3P/8/8/6K1 w - - 21 63"));
    list.add(new PgnTestCase("lichess_qDcEiBsg.pgn", "", "", 8, 81, CheckmateOrStalemate.NA, 2,
        InsufficientMaterial.NONE, "8/2k5/4p1p1/p2pPpPp/P2P1P1P/3K4/8/8 b - - 81 91"));
    list.add(new PgnTestCase("lichess_QirOKYTP.pgn", "", "", 11, 10, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "r1b5/ppppn2p/5Qp1/8/1P2q3/N1Pkb3/P4P1P/3RKR2 b - - 1 20"));
    list.add(new PgnTestCase("lichess_qle7LoyU.pgn", "", "", 10, 79, CheckmateOrStalemate.NA, 2,
        InsufficientMaterial.NONE, "8/8/1k1p4/p1pP1p1p/P1P2P1P/8/3K4/8 b - - 79 83"));
    list.add(new PgnTestCase("lichess_QnFE4Znl.pgn", "", "", 3, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/8/8/6QP/6PK/2k3q1 w - - 6 51"));
    list.add(new PgnTestCase("lichess_QPdYSKDq.pgn", "", "", 18, 16, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "6r1/8/4kn1K/8/2q2P2/8/8/8 w - - 16 72"));
    list.add(new PgnTestCase("lichess_QpptleSj.pgn", "", "", 4, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/1p6/pPp5/P1P5/8/4nkp1/6p1/6K1 b - - 1 71"));
    list.add(new PgnTestCase("lichess_qpXuukKR.pgn", "", "", 18, 35, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/3p1k1p/p1pPp1pP/P1P1P1P1/8/3K4 w - - 14 67"));
    list.add(new PgnTestCase("lichess_QSTM2G7V.pgn", "", "", 22, 33, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/3k1p2/4pPp1/2p1P1Pp/1pP4P/1P6/8/6K1 b - - 33 56"));
    list.add(new PgnTestCase("lichess_quMnfy5i.pgn", "", "", 6, 19, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/5k2/5p1p/4pP1P/2p1P3/1pP3K1/1P6/8 b - - 3 52"));
    list.add(new PgnTestCase("lichess_QwKzanAX.pgn", "", "", 6, 10, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/2p2pkp/7P/8/8/7K/5qr1/8 b - - 0 38"));
    list.add(new PgnTestCase("lichess_r1SzzM60.pgn", "", "", 17, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "2k5/2P5/8/1KN5/8/8/8/8 b - - 0 66"));
    list.add(new PgnTestCase("lichess_rbPFjPRm.pgn", "", "", 14, 39, CheckmateOrStalemate.NA, 2,
        InsufficientMaterial.NONE, "8/1k6/3p2p1/1p1P1pPp/pP3P1P/P7/2K5/8 b - - 39 69"));
    list.add(new PgnTestCase("lichess_RF0MOp86.pgn", "", "", 7, 13, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "Q7/k1p5/1p1p4/p7/2r5/r7/8/3K4 b - - 13 57"));
    list.add(new PgnTestCase("lichess_RJ6gw7gD.pgn", "", "", 13, 14, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/6k1/5p2/2p1pPp1/1pP1P1P1/1P3K2/8/8 b - - 4 69"));
    list.add(new PgnTestCase("lichess_rSC90mAs.pgn", "", "", 17, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "k7/P1K5/8/8/8/8/8/8 b - - 2 62"));
    list.add(new PgnTestCase("lichess_RSqLhejA.pgn", "", "", 12, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/4n3/4p3/8/2p5/5K1k/7R/2r3q1 b - - 3 62"));
    list.add(new PgnTestCase("lichess_RvN1zpCB.pgn", "", "", 3, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "6nr/6pp/4pp2/8/1PP5/P4P1k/3q3Q/3B1RK1 b - - 2 25"));
    list.add(new PgnTestCase("lichess_rVN4T4Tz.pgn", "", "", 7, 11, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "K7/1q6/8/P7/8/8/8/6k1 w - - 0 69"));
    list.add(new PgnTestCase("lichess_RXlEuUhF.pgn", "", "", 8, 39, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "7k/8/7p/2p2p1P/1pP2P2/pP2K3/P7/8 w - - 39 64"));
    list.add(new PgnTestCase("lichess_s2ZKBPfv.pgn", "", "", 12, 22, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/1p1k4/pPp5/P1Pp2p1/3P2P1/5K2/8/8 w - - 22 54"));
    list.add(new PgnTestCase("lichess_sBwTHJz0.pgn", "", "", 15, 60, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/7k/p2p2p1/Pp1P2Pp/1P1K3P/8/8 w - - 60 67"));
    list.add(new PgnTestCase("lichess_SdlLyZvq.pgn", "", "", 5, 27, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/4k3/1p2p1p1/1P2P1Pp/7P/7K/8/8 b - - 27 76"));
    list.add(new PgnTestCase("lichess_SDy9Y3D0.pgn", "", "", 20, 11, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/4p3/3Qk1K1/3r4/8/8 b - - 0 54"));
    list.add(new PgnTestCase("lichess_Sdz1yTzS.pgn", "", "", 6, 15, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/6qK/8/8/6P1/P7/4k3 w - - 15 59"));
    list.add(new PgnTestCase("lichess_sMv8Hh43.pgn", "", "", 11, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/6p1/5kP1/7K/2r2q2/8/8/8 b - - 1 52"));
    list.add(new PgnTestCase("lichess_sp0oSyY2.pgn", "", "", 4, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/pp6/8/kQ5K/8/8/b7/2q5 b - - 0 40"));
    list.add(new PgnTestCase("lichess_SrLwzxpB.pgn", "", "", 5, 14, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/3k4/p2p2p1/Pp1P2P1/1P6/8/1K6 b - - 8 54"));
    list.add(new PgnTestCase("lichess_ST89KN4m.pgn", "", "", 5, 10, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/6k1/5n2/6pK/8 w - - 2 67"));
    list.add(new PgnTestCase("lichess_SwCSdv8K.pgn", "", "", 17, 11, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/6p1/8/8/3K4/8/3kQ3 b - - 0 58"));
    list.add(new PgnTestCase("lichess_SX3iSehH.pgn", "", "", 16, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "5KQ1/5q2/4k3/P7/8/8/8/8 w - - 0 55"));
    list.add(new PgnTestCase("lichess_Sx7EyNdc.pgn", "", "", 17, 47, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3k4/8/2p5/1pP1p1p1/1P2P1P1/3K4/8/8 w - - 47 67"));
    list.add(new PgnTestCase("lichess_Szu1FAr2.pgn", "", "", 3, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "7R/pp4pk/2p3p1/8/3q4/8/6K1/8 b - - 7 46"));
    list.add(new PgnTestCase("lichess_tapdr97m.pgn", "", "", 14, 15, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "7k/6pP/6P1/5K2/8/8/8/8 w - - 1 67"));
    list.add(new PgnTestCase("lichess_tDwQdtb5.pgn", "", "", 18, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/5k2/7p/7K w - - 0 73"));
    list.add(new PgnTestCase("lichess_tEtllzqk.pgn", "", "", 17, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/7R/6pk/5p2/5bpK/8/r7/8 b - - 1 53"));
    list.add(new PgnTestCase("lichess_TjtEtQJ4.pgn", "", "", 5, 29, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "k7/2p5/p1Pp4/P2P1p2/5Pp1/3K2P1/8/8 w - - 29 66"));
    list.add(new PgnTestCase("lichess_tn1wmL3G.pgn", "", "", 23, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/1p4pk/p1b5/6Pp/7P/r7/7K w - - 0 46"));
    list.add(new PgnTestCase("lichess_TTb9iG4x.pgn", "", "", 15, 45, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/7k/5p1p/2p1pP1P/1pP1P3/1P1K4/8/8 w - - 45 61"));
    list.add(new PgnTestCase("lichess_TV4G8kFN.pgn", "", "", 20, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/6pk/6Pp/7K/3q3P/8/4p3/8 b - - 0 60"));
    list.add(new PgnTestCase("lichess_u2fQpBN9.pgn", "", "", 11, 13, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3Q4/5R2/4p3/2p1P3/2N1k2p/8/6KP/8 b - - 13 46"));
    list.add(new PgnTestCase("lichess_U5uv4wPs.pgn", "", "", 10, 18, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/7R/6pk/4n1r1/8/7K/8 b - - 3 78"));
    list.add(new PgnTestCase("lichess_U8Rj7Y9F.pgn", "", "", 4, 18, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/4k3/6p1/1p2p1Pp/1P2P2P/8/8/4K3 b - - 18 63"));
    list.add(new PgnTestCase("lichess_uBSFb2kx.pgn", "", "", 16, 41, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/3k4/8/8/8/6BR/6PK/7r w - - 41 88"));
    list.add(new PgnTestCase("lichess_UDdCTvWG.pgn", "", "", 20, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "7k/5K1P/8/8/8/8/8/8 b - - 2 62"));
    list.add(new PgnTestCase("lichess_UdUE6AvZ.pgn", "", "", 16, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/Kq6/P7/8/6k1/8/8/8 w - - 1 50"));
    list.add(new PgnTestCase("lichess_UsN4NLQ4.pgn", "", "", 6, 13, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/6p1/5pkp/8/7P/3r2r1/7K w - - 0 45"));
    list.add(new PgnTestCase("lichess_UuNO3dGf.pgn", "", "", 5, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/1P6/8/1R6/P4k2/8/1r3K2 w - - 4 72"));
    list.add(new PgnTestCase("lichess_UVt2ZNXy.pgn", "", "", 21, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/6pk/8/6Pp/6nP/1r6/6K1 w - - 3 59"));
    list.add(new PgnTestCase("lichess_uwwUPCFw.pgn", "", "", 5, 12, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/7p/7k/6R1/4K3/8/8 b - - 0 58"));
    list.add(new PgnTestCase("lichess_ux9sCCZ8.pgn", "", "", 5, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4rR1k/p5p1/6P1/2pp1K2/1p1b2P1/4q3/P1P5/8 b - - 3 34"));
    list.add(new PgnTestCase("lichess_V08kX4kz.pgn", "", "", 20, 11, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/6b1/1p3k2/1Pp1p1p1/2P1PpP1/5P2/8/5K2 b - - 11 61"));
    list.add(new PgnTestCase("lichess_v4xJHVln.pgn", "", "", 3, 11, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/8/7p/6p1/3K2pk/7Q b - - 3 59"));
    list.add(new PgnTestCase("lichess_vaTHx2Ow.pgn", "", "", 6, 22, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "7k/7P/5K2/8/8/8/8/8 b - - 0 74"));
    list.add(new PgnTestCase("lichess_VCzr8ukR.pgn", "", "", 29, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/3k4/p3p2p/Pp1pP2P/1P1P4/5K2/8 w - - 0 44"));
    list.add(new PgnTestCase("lichess_VdYuP1l4.pgn", "", "", 20, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/p7/8/8/1Q5K/k7 b - - 0 62"));
    list.add(new PgnTestCase("lichess_VeCbG5uw.pgn", "", "", 12, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/8/5kP1/8/5q2/5K2 w - - 0 59"));
    list.add(new PgnTestCase("lichess_VIdrelSz.pgn", "", "", 4, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "7r/2PR4/6pk/6q1/5P1K/r7/8/8 w - - 0 40"));
    list.add(new PgnTestCase("lichess_Vnqfiwd6.pgn", "", "", 5, 17, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "6kR/5pp1/1K2p1p1/3r4/8/8/8/8 b - - 3 48"));
    list.add(new PgnTestCase("lichess_VpZzyQaL.pgn", "", "", 20, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/3k4/3p4/2pP1p1p/p1P2P1P/P2K4/8/8 b - - 6 46"));
    list.add(new PgnTestCase("lichess_vS3VdvtP.pgn", "", "", 3, 10, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3Q2k1/6pp/4K3/4p3/3q4/2b5/8/8 b - - 0 44"));
    list.add(new PgnTestCase("lichess_vWvNhvif.pgn", "", "", 6, 22, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/5k2/3p2p1/1p1Pp1P1/pP2Pp2/P4P2/8/2K5 b - - 16 65"));
    list.add(new PgnTestCase("lichess_vytUGbcM.pgn", "", "", 19, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "7K/6Pq/8/3k4/8/8/8/8 w - - 0 74"));
    list.add(new PgnTestCase("lichess_vZUevQUB.pgn", "", "", 3, 56, CheckmateOrStalemate.NA, 2,
        InsufficientMaterial.NONE, "8/8/4k2p/1p1p2pP/1P1P2P1/8/2K5/8 b - - 56 73"));
    list.add(new PgnTestCase("lichess_W4wkO3cX.pgn", "", "", 11, 36, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "5k2/6p1/1p2p1P1/pP1pP2K/P2P4/8/8/8 b - - 26 81"));
    list.add(new PgnTestCase("lichess_W5huoA6x.pgn", "", "", 6, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "2Q5/pk6/8/2K5/8/8/8/8 b - - 1 69"));
    list.add(new PgnTestCase("lichess_w5N015UJ.pgn", "", "", 23, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "k7/P7/2K5/8/8/8/8/8 b - - 2 59"));
    list.add(new PgnTestCase("lichess_wCL9HARO.pgn", "", "", 6, 15, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "6K1/6q1/8/8/8/7k/7P/8 w - - 0 56"));
    list.add(new PgnTestCase("lichess_WdedP6nc.pgn", "", "", 6, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/6K1/8/1p6/k7/Q7 b - - 0 59"));
    list.add(new PgnTestCase("lichess_WeqThgc0.pgn", "", "", 14, 11, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/p1k5/P1p5/2Pp3p/3P1p1P/5P1K/8/8 b - - 0 47"));
    list.add(new PgnTestCase("lichess_WEYtzOxf.pgn", "", "", 10, 43, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/5k2/1p3p1p/1Pp1pP1P/2P1P3/3K4/8/8 b - - 7 69"));
    list.add(new PgnTestCase("lichess_WIQLYQ5T.pgn", "", "", 12, 21, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/1p6/pPp1p2k/P1PpP1p1/3P2P1/8/8/6K1 b - - 1 58"));
    list.add(new PgnTestCase("lichess_wJymwD30.pgn", "", "", 7, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/8/7R/6pk/r6p/7K b - - 3 59"));
    list.add(new PgnTestCase("lichess_wOF3za6s.pgn", "", "", 21, 75, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/2k5/1p2p1p1/1P1pP1Pp/3P3P/8/3K4/8 w - - 75 90"));
    list.add(new PgnTestCase("lichess_wUCLya3K.pgn", "", "", 12, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/5p2/1K4p1/8/8/8/3Q4/3k4 b - - 0 67"));
    list.add(new PgnTestCase("lichess_wuHnMP2q.pgn", "", "", 4, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "7R/7p/p5p1/8/2p4P/5P1k/P2q3Q/5RK1 b - - 3 33"));
    list.add(new PgnTestCase("lichess_WxdB1E0N.pgn", "", "", 4, 11, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/8/p1k5/K7 w - - 2 62"));
    list.add(new PgnTestCase("lichess_xD85FRxa.pgn", "", "", 9, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "6qK/8/4k3/6B1/8/8/5P2/8 w - - 0 55"));
    list.add(new PgnTestCase("lichess_xfvaW7PK.pgn", "", "", 12, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "6qK/5k1P/8/8/8/8/8/8 w - - 1 51"));
    list.add(new PgnTestCase("lichess_xgHJ9FM2.pgn", "", "", 4, 40, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "k7/8/6p1/1p1p1pP1/pP1PpP2/P3P3/3K4/8 w - - 40 62"));
    list.add(new PgnTestCase("lichess_XIfR5l9e.pgn", "", "", 31, 34, CheckmateOrStalemate.NA, 2,
        InsufficientMaterial.NONE, "8/8/4b3/5k2/p1p1pBp1/PpP1P1Pp/1P3K1P/8 w - - 34 76"));
    list.add(new PgnTestCase("lichess_xmpqlKFo.pgn", "", "", 9, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/Kq6/8/8/8/8/P7/5k2 w - - 0 54"));
    list.add(new PgnTestCase("lichess_XooYe68z.pgn", "", "", 7, 26, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/6k1/6p1/4p1P1/p1p1P1K1/P1P5/2P5/8 b - - 26 54"));
    list.add(new PgnTestCase("lichess_Xp7OUyAx.pgn", "", "", 4, 19, CheckmateOrStalemate.NA, 2,
        InsufficientMaterial.NONE, "8/3k4/1p2p1p1/1P2P1Pp/3K3P/8/8/8 w - - 19 55"));
    list.add(new PgnTestCase("lichess_xqzt1PdW.pgn", "", "", 6, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "7k/7P/5K2/8/8/8/8/8 b - - 0 58"));
    list.add(new PgnTestCase("lichess_XtJJQxwF.pgn", "", "", 10, 18, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/8/8/5K1k/6p1/7R b - - 0 63"));
    list.add(new PgnTestCase("lichess_XtvgUXHD.pgn", "", "", 16, 15, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/p6p/6Qk/6q1/1K6/8/8/8 b - - 5 72"));
    list.add(new PgnTestCase("lichess_Y6dvuR4W.pgn", "", "", 6, 9, CheckmateOrStalemate.NA, 2,
        InsufficientMaterial.NONE, "8/8/8/1k4p1/1p2p1P1/1P2P3/2K5/8 w - - 9 48"));
    list.add(new PgnTestCase("lichess_yEtZYyMQ.pgn", "", "", 8, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/4k3/4p1p1/2p1P1Pp/1pP2K1P/1P6/8/8 w - - 3 42"));
    list.add(new PgnTestCase("lichess_YGj1C2WB.pgn", "", "", 13, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/3K4/8/5kQ1/4p3/8 b - - 5 69"));
    list.add(new PgnTestCase("lichess_YI1t9qvC.pgn", "", "", 10, 13, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "7k/5K1P/8/8/8/8/8/8 b - - 2 74"));
    list.add(new PgnTestCase("lichess_YictG6WZ.pgn", "", "", 10, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/6k1/8/5RPK/7r/8/8/8 w - - 6 51"));
    list.add(new PgnTestCase("lichess_yjfLthhQ.pgn", "", "", 6, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "1K6/8/8/8/3p4/4kQ2/3p4/8 b - - 6 58"));
    list.add(new PgnTestCase("lichess_YoQvXlEO.pgn", "", "", 12, 14, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/6Q1/5Q1p/7k/3P4/1PP2RBK/7P/5q2 w - - 5 54"));
    list.add(new PgnTestCase("lichess_yQOHgRpo.pgn", "", "", 14, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "7k/6pp/7P/8/8/4q3/2r5/7K w - - 6 57"));
    list.add(new PgnTestCase("lichess_yrg4ColU.pgn", "", "", 9, 18, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/2k5/2p3p1/p1P1p1P1/P3P3/6K1/8 b - - 18 53"));
    list.add(new PgnTestCase("lichess_YRvWOIpy.pgn", "", "", 3, 10, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/R7/Kr6/P6k/8/8/8/8 w - - 1 57"));
    list.add(new PgnTestCase("lichess_YUaykh8t.pgn", "", "", 12, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/8/1p4P1/1QK5/p4P2/k7 w - - 0 49"));
    list.add(new PgnTestCase("lichess_yVPekOlc.pgn", "", "", 3, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/8/5k1p/7K w - - 0 67"));
    list.add(new PgnTestCase("lichess_yxiNrimN.pgn", "", "", 3, 28, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "k7/8/p1p2p1p/P1P2P1P/4K3/8/8/8 w - - 28 69"));
    list.add(new PgnTestCase("lichess_yxUT9b2S.pgn", "", "", 12, 49, CheckmateOrStalemate.NA, 2,
        InsufficientMaterial.NONE, "8/2k5/2p1p1p1/1pP1P1Pp/1P5P/7K/8/8 b - - 49 78"));
    list.add(new PgnTestCase("lichess_z3xkCyBY.pgn", "", "", 14, 11, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "7k/2Q4q/7K/2p3P1/2P5/8/8/8 w - - 6 59"));
    list.add(new PgnTestCase("lichess_Z5BCc8QN.pgn", "", "", 4, 10, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/8/p1k5/K7 w - - 2 59"));
    list.add(new PgnTestCase("lichess_z9uuT4Ei.pgn", "", "", 6, 30, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/4k3/p1p2p2/P1P2P1p/2P2P1P/2K5/8/8 b - - 30 66"));
    list.add(new PgnTestCase("lichess_zDsWuY13.pgn", "", "", 16, 15, CheckmateOrStalemate.NA, 2,
        InsufficientMaterial.NONE, "8/8/3k2p1/1p2p1Pp/1P2P2P/3K4/8/8 b - - 15 54"));
    list.add(new PgnTestCase("lichess_zIC5NqLC.pgn", "", "", 5, 13, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "k7/8/KP6/P7/8/1r6/8/8 w - - 1 58"));
    list.add(new PgnTestCase("lichess_zmelXKvA.pgn", "", "", 23, 10, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/6p1/8/8/6p1/6rk/3K3R/8 b - - 10 58"));
    list.add(new PgnTestCase("lichess_ZNs9RrhS.pgn", "", "", 19, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "7k/5R2/6RK/7r/6PP/8/8/8 w - - 2 54"));
    list.add(new PgnTestCase("lichess_Zqc7ug8u.pgn", "", "", 11, 14, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "7R/6pk/8/6K1/2n1r3/8/8/8 b - - 9 61"));

    list.add(new PgnTestCase("lichess_f6c1lu7R.pgn", "", "", 13, 70, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/7p/5p1P/5p1K/5Pp1/6P1/1k6 w - - 70 83"));

    return new PgnTestCaseList(PgnTest.CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR, list);
  }

  private static PgnTestCaseList createTestCasesChaLichessQuickNotDepthThreeHelpmate() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("lichess_03VIxJUJ_helpmate.pgn", "", "", 6, 13, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/1p6/2p5/8/6k1/8/1r4K1 w - - 8 70"));
    list.add(new PgnTestCase("lichess_0dKUIfwq_helpmate.pgn", "", "", 6, 24, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "8/8/8/8/k1K5/8/8/Q7 b - - 24 89"));
    list.add(new PgnTestCase("lichess_0ehvDno9_helpmate.pgn", "", "", 6, 8, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "5Q2/P6k/8/8/4Q3/8/1B6/4R1K1 b - - 2 61"));
    list.add(new PgnTestCase("lichess_0u6eh8ZP_helpmate.pgn", "", "", 9, 10, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "1k1Q4/8/K7/8/8/8/8/8 b - - 10 61"));
    list.add(new PgnTestCase("lichess_0xMzcUDT_helpmate.pgn", "", "", 21, 18, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "k7/8/1K6/8/5Q2/8/8/Q3Q3 b - - 18 88"));
    list.add(new PgnTestCase("lichess_16myWOwv_helpmate.pgn", "", "", 15, 20, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "7q/8/8/8/8/8/8/5k1K w - - 5 78"));
    list.add(new PgnTestCase("lichess_1rcB0Tru_helpmate.pgn", "", "", 10, 6, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/4K1k1/2b2q2/1p6/8/8/8/2q5 w - - 3 52"));
    list.add(new PgnTestCase("lichess_29moHRC8_helpmate.pgn", "", "", 10, 6, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/6p1/8/8/8/7k/5K2/4q2q w - - 4 65"));
    list.add(new PgnTestCase("lichess_2gCvxApk_helpmate.pgn", "", "", 16, 21, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "Q7/8/1k6/Q7/8/8/8/5K2 b - - 4 69"));
    list.add(new PgnTestCase("lichess_2k5piUl6_helpmate.pgn", "", "", 14, 8, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "k1Q5/8/K7/8/8/8/8/8 b - - 0 72"));
    list.add(new PgnTestCase("lichess_2mgjVvcC_helpmate.pgn", "", "", 10, 5, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "8/2p4p/p6k/5pQ1/3b3K/6P1/PP5P/5q2 b - - 0 32"));
    list.add(new PgnTestCase("lichess_2pwjX9B1_helpmate.pgn", "", "", 5, 10, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/p7/6k1/8/5r1K/1q2q3 w - - 9 60"));
    list.add(new PgnTestCase("lichess_2sFHWmg3_helpmate.pgn", "", "", 3, 20, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "8/8/8/8/k1K5/8/8/Q7 b - - 20 70"));
    list.add(new PgnTestCase("lichess_2wVTK5vV_helpmate.pgn", "", "", 11, 18, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "8/8/8/k1K5/8/8/8/Q7 b - - 18 72"));
    list.add(new PgnTestCase("lichess_3bKyjTHM_helpmate.pgn", "", "", 18, 10, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/7q/8/7K/8/p7/k7/5qq1 w - - 2 65"));
    list.add(new PgnTestCase("lichess_3cYOGNS6_helpmate.pgn", "", "", 11, 8, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "Q4Q2/8/8/8/8/1k1K3Q/7P/1Q6 b - - 8 57"));
    list.add(new PgnTestCase("lichess_3jesiit8_helpmate.pgn", "", "", 11, 22, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "Q7/8/1k1Q4/8/2P2P2/6P1/4K3/8 b - - 2 69"));
    list.add(new PgnTestCase("lichess_3xFc21eB_helpmate.pgn", "", "", 15, 48, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "k1R5/8/K7/8/8/8/8/8 b - - 9 82"));
    list.add(new PgnTestCase("lichess_49QwULSR_helpmate.pgn", "", "", 10, 11, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/7k/8/3q3K w - - 3 76"));
    list.add(new PgnTestCase("lichess_55wBEu8Z_helpmate.pgn", "", "", 11, 9, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "8/p1r5/5R1p/Pp1p2Pk/3PpK2/4Pp1Q/5P2/8 b - - 0 43"));
    list.add(new PgnTestCase("lichess_6kfJPvsA_helpmate.pgn", "", "", 6, 12, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/7k/5K2/4q2q w - - 12 73"));
    list.add(new PgnTestCase("lichess_78XhRaIr_helpmate.pgn", "", "", 32, 26, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "8/8/8/8/2K5/k7/8/Q7 b - - 26 87"));
    list.add(new PgnTestCase("lichess_7alMRe6y_helpmate.pgn", "", "", 3, 7, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/2p5/r3qk2/8/8/7K/1b3q2 w - - 2 68"));
    list.add(new PgnTestCase("lichess_7fWsFh0J_helpmate.pgn", "", "", 6, 9, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/3q1K1k/7q/8 w - - 6 66"));
    list.add(new PgnTestCase("lichess_8cTl7SrQ_helpmate.pgn", "", "", 15, 32, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/k1b5/b7/K7 w - - 32 70"));
    list.add(new PgnTestCase("lichess_APEww4mD_helpmate.pgn", "", "", 5, 31, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/2r5/6K1/6q1/6k1 w - - 6 89"));
    list.add(new PgnTestCase("lichess_B8bpMZ71_helpmate.pgn", "", "", 11, 13, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "2Q5/8/8/6B1/3QkPK1/PP2P1P1/8/5R2 b - - 2 61"));
    list.add(new PgnTestCase("lichess_bfJbt6GP_helpmate.pgn", "", "", 7, 12, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/7p/8/6p1/8/2Kq3k/8/1q5n w - - 2 73"));
    list.add(new PgnTestCase("lichess_bQ0EXLal_helpmate.pgn", "", "", 10, 11, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "1b6/1p4pp/8/8/8/k7/8/K6r w - - 11 59"));
    list.add(new PgnTestCase("lichess_cZ8QxYP8_helpmate.pgn", "", "", 7, 12, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "7k/7Q/7K/8/2PP3P/4P1P1/8/2BQ1B1R b - - 10 52"));
    list.add(new PgnTestCase("lichess_D4vIOthl_helpmate.pgn", "", "", 6, 13, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/6k1/8/8/8/8/5K2/4q2q w - - 10 76"));
    list.add(new PgnTestCase("lichess_dBergEkt_helpmate.pgn", "", "", 17, 26, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/6k1/8/q5K1/1q6 w - - 26 74"));
    list.add(new PgnTestCase("lichess_DiKq9jcy_helpmate.pgn", "", "", 9, 14, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "4Q2Q/8/5k2/3Q4/1BK5/2P5/8/8 b - - 0 70"));
    list.add(new PgnTestCase("lichess_E9P918aj_helpmate.pgn", "", "", 18, 30, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "1k1Q4/8/K7/8/8/8/8/8 b - - 30 70"));
    list.add(new PgnTestCase("lichess_eExHpNDf_helpmate.pgn", "", "", 11, 34, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "6Q1/k7/8/8/Q7/3K4/8/1R6 b - - 8 99"));
    list.add(new PgnTestCase("lichess_eKwKl6Y9_helpmate.pgn", "", "", 17, 30, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "k7/1Q6/K7/8/8/8/8/8 b - - 30 88"));
    list.add(new PgnTestCase("lichess_EQCMW0jB_helpmate.pgn", "", "", 6, 8, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/r7/q4K1k/7q/8 w - - 8 66"));
    list.add(new PgnTestCase("lichess_EsFX1Urt_helpmate.pgn", "", "", 7, 14, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/7k/6q1/6K1 w - - 14 67"));
    list.add(new PgnTestCase("lichess_EstAIWqd_helpmate.pgn", "", "", 15, 13, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/5K1k/8/4rq2 w - - 10 76"));
    list.add(new PgnTestCase("lichess_Ew7uTqu0_helpmate.pgn", "", "", 13, 12, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/5pp1/8/8/p7/3b3k/8/r6K w - - 12 59"));
    list.add(new PgnTestCase("lichess_f7x3bSzh_helpmate.pgn", "", "", 12, 9, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "k6r/p4p1p/2B3p1/8/8/N3b1P1/P4p1P/1R5K b - - 0 34"));
    list.add(new PgnTestCase("lichess_FMDN8gJ7_helpmate.pgn", "", "", 5, 12, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/1k2b3/2p5/2K5 w - - 2 73"));
    list.add(new PgnTestCase("lichess_fN4vRqp0_helpmate.pgn", "", "", 5, 9, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "8/8/8/8/8/8/5KQk/5R2 b - - 2 74"));
    list.add(new PgnTestCase("lichess_fPnoYQtW_helpmate.pgn", "", "", 11, 9, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/7k/5K2/4q2q w - - 8 66"));
    list.add(new PgnTestCase("lichess_FYTeO80q_helpmate.pgn", "", "", 7, 9, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "8/6Qp/p5p1/7k/P4PB1/7K/1P3R1P/8 b - - 0 38"));
    list.add(new PgnTestCase("lichess_G0xDBYwE_helpmate.pgn", "", "", 6, 20, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "8/k1Q5/1N6/K7/8/8/8/8 b - - 20 72"));
    list.add(new PgnTestCase("lichess_G8i5Bn7F_helpmate.pgn", "", "", 11, 28, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/7k/8/q6K w - - 28 73"));
    list.add(new PgnTestCase("lichess_GCuwJ5bP_helpmate.pgn", "", "", 6, 18, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "1k1Q4/8/K7/8/8/8/8/8 b - - 18 64"));
    list.add(new PgnTestCase("lichess_GIwBNz5S_helpmate.pgn", "", "", 14, 12, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/7q/8/8/8/8/8/5k1K w - - 12 80"));
    list.add(new PgnTestCase("lichess_gtzPUYWo_helpmate.pgn", "", "", 13, 10, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/4kp2/4rp2/3q1K2 w - - 0 71"));
    list.add(new PgnTestCase("lichess_GzoCTk46_helpmate.pgn", "", "", 9, 9, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/7p/4qK1k/8/6q1 w - - 6 74"));
    list.add(new PgnTestCase("lichess_hdott1S4_helpmate.pgn", "", "", 25, 30, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "7N/5Q2/5k1K/8/8/8/7B/8 b - - 1 78"));
    list.add(new PgnTestCase("lichess_hkiG96je_helpmate.pgn", "", "", 10, 24, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "8/8/8/8/k1K5/8/8/Q7 b - - 24 73"));
    list.add(new PgnTestCase("lichess_IcjtzT9i_helpmate.pgn", "", "", 25, 28, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "k7/8/1K6/8/8/6Q1/8/Q7 b - - 28 100"));
    list.add(new PgnTestCase("lichess_if6AKvpb_helpmate.pgn", "", "", 5, 11, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "k3R3/8/K7/P7/1P3P2/8/8/8 b - - 2 60"));
    list.add(new PgnTestCase("lichess_ig7fudY7_helpmate.pgn", "", "", 8, 8, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/5K1k/8/4rq2 w - - 8 59"));
    list.add(new PgnTestCase("lichess_Iy1VEUMC_helpmate.pgn", "", "", 9, 8, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "4K3/8/Q7/8/8/k7/8/1Q6 b - - 6 67"));
    list.add(new PgnTestCase("lichess_j17qnxd0_helpmate.pgn", "", "", 16, 38, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/7k/8/q6K w - - 24 85"));
    list.add(new PgnTestCase("lichess_j4phmaNj_helpmate.pgn", "", "", 11, 20, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "8/8/8/8/k1K5/8/8/Q7 b - - 20 81"));
    list.add(new PgnTestCase("lichess_jehHS0Jj_helpmate.pgn", "", "", 10, 11, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "8/8/8/8/2K5/k7/8/Q7 b - - 10 75"));
    list.add(new PgnTestCase("lichess_JfHZLRvD_helpmate.pgn", "", "", 6, 8, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "r7/pp6/3p1p2/2p1k3/4q2P/5KP1/5P2/3R3R w - - 0 34"));
    list.add(new PgnTestCase("lichess_jK46bYGo_helpmate.pgn", "", "", 12, 11, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/6pk/8/q5K1 w - - 2 68"));
    list.add(new PgnTestCase("lichess_jnox9kxK_helpmate.pgn", "", "", 36, 12, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/7q/8/8/8/8/8/5k1K w - - 12 100"));
    list.add(new PgnTestCase("lichess_jnvJUlDw_helpmate.pgn", "", "", 11, 8, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/2p5/8/8/8/7k/4K3/3q2qn w - - 2 69"));
    list.add(new PgnTestCase("lichess_KbEG3yK3_helpmate.pgn", "", "", 20, 18, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/7k/b7/3q3K w - - 18 76"));
    list.add(new PgnTestCase("lichess_kbutR5IJ_helpmate.pgn", "", "", 8, 7, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "2k1q3/2p5/2K5/2N5/2q5/8/8/8 w - - 0 54"));
    list.add(new PgnTestCase("lichess_Kdf0f1OE_helpmate.pgn", "", "", 21, 16, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/7k/8/q6K w - - 16 80"));
    list.add(new PgnTestCase("lichess_KE47c49r_helpmate.pgn", "", "", 6, 13, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/6p1/6b1/8/8/7k/8/3q3K w - - 13 66"));
    list.add(new PgnTestCase("lichess_knQsF7R8_helpmate.pgn", "", "", 7, 12, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "k7/4K3/8/8/8/8/1Q6/Q7 b - - 12 60"));
    list.add(new PgnTestCase("lichess_KP23RUWa_helpmate.pgn", "", "", 11, 17, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "k7/8/8/8/2K5/Q7/8/1Q6 b - - 6 75"));
    list.add(new PgnTestCase("lichess_KPvcgsfS_helpmate.pgn", "", "", 11, 6, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "k5Q1/8/K7/8/8/8/P7/8 b - - 0 58"));
    list.add(new PgnTestCase("lichess_Ks0yR98N_helpmate.pgn", "", "", 4, 32, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "k7/8/1K6/8/8/6Q1/8/Q7 b - - 32 78"));
    list.add(new PgnTestCase("lichess_LC76ur18_helpmate.pgn", "", "", 3, 15, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/3b4/3Kq3/8/8/2qk4 w - - 2 75"));
    list.add(new PgnTestCase("lichess_lHtqM0fz_helpmate.pgn", "", "", 22, 19, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/7k/8/q6K w - - 12 82"));
    list.add(new PgnTestCase("lichess_lk42iihk_helpmate.pgn", "", "", 5, 29, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "k6Q/8/K7/8/8/8/R7/8 b - - 29 58"));
    list.add(new PgnTestCase("lichess_LqTNOgyT_helpmate.pgn", "", "", 23, 11, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "4Q2Q/5k2/8/8/8/3K4/8/3Q4 b - - 2 64"));
    list.add(new PgnTestCase("lichess_lYf34Uiq_helpmate.pgn", "", "", 14, 22, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/7k/6q1/6K1 w - - 22 82"));
    list.add(new PgnTestCase("lichess_m5WDCW16_helpmate.pgn", "", "", 17, 14, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "5Q2/2k4R/K7/8/6PP/8/8/7B b - - 14 58"));
    list.add(new PgnTestCase("lichess_McLow5Hz_helpmate.pgn", "", "", 5, 22, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "5Q1Q/2k3R1/8/1K6/8/8/B5P1/8 b - - 22 60"));
    list.add(new PgnTestCase("lichess_MvEif0NV_helpmate.pgn", "", "", 19, 27, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "k7/8/1K6/8/8/8/7Q/Q7 b - - 24 88"));
    list.add(new PgnTestCase("lichess_N6AYM22R_helpmate.pgn", "", "", 4, 16, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/6p1/7p/8/8/7k/q7/5q1K w - - 0 73"));
    list.add(new PgnTestCase("lichess_nvxFBquo_helpmate.pgn", "", "", 10, 10, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "2QQ4/5k1K/8/8/8/5R2/8/8 b - - 2 68"));
    list.add(new PgnTestCase("lichess_om0bCR5w_helpmate.pgn", "", "", 15, 8, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "q7/8/8/5k2/8/8/5K2/1r2q3 w - - 8 61"));
    list.add(new PgnTestCase("lichess_Omq6Fdhm_helpmate.pgn", "", "", 12, 9, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "7q/8/8/8/8/8/8/1q3k1K w - - 2 83"));
    list.add(new PgnTestCase("lichess_ooSZuhhE_helpmate.pgn", "", "", 5, 26, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/7k/8/q6K w - - 26 59"));
    list.add(new PgnTestCase("lichess_p4CeCK3J_helpmate.pgn", "", "", 6, 13, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "k7/1Q6/K7/8/8/8/8/6Q1 b - - 13 76"));
    list.add(new PgnTestCase("lichess_PAjZeTkN_helpmate.pgn", "", "", 6, 18, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "1Q6/k7/N7/8/R7/K7/8/8 b - - 0 69"));
    list.add(new PgnTestCase("lichess_pFsUTatm_helpmate.pgn", "", "", 22, 27, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "2QQ4/k4K2/8/8/4P3/8/8/Q7 b - - 2 66"));
    list.add(new PgnTestCase("lichess_PIL4PUtT_helpmate.pgn", "", "", 19, 10, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "Q1Q5/8/7Q/8/1PB4k/8/5P2/R4RK1 b - - 2 54"));
    list.add(new PgnTestCase("lichess_PkZ6qiA6_helpmate.pgn", "", "", 7, 8, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/3p4/4k3/8/6K1/8/6qq w - - 2 65"));
    list.add(new PgnTestCase("lichess_QirOKYTP_helpmate.pgn", "", "", 11, 10, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "r1b5/ppppn2p/5Qp1/8/1P2q3/N1Pk4/P2b1P1P/3RKR2 w - - 2 21"));
    list.add(new PgnTestCase("lichess_QnFE4Znl_helpmate.pgn", "", "", 3, 12, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "6QQ/2k1Q3/8/1K6/8/8/8/8 b - - 12 69"));
    list.add(new PgnTestCase("lichess_RF0MOp86_helpmate.pgn", "", "", 7, 13, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/1kp5/3p4/8/p7/r7/2rK4/1q6 w - - 2 66"));
    list.add(new PgnTestCase("lichess_RSqLhejA_helpmate.pgn", "", "", 12, 14, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/4p3/8/5Knk/2p1q3/2r5 w - - 14 73"));
    list.add(new PgnTestCase("lichess_RvN1zpCB_helpmate.pgn", "", "", 3, 3, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "6nr/6pp/4pp2/8/1PP5/P4P1k/7q/3B1RK1 w - - 0 26"));
    list.add(new PgnTestCase("lichess_rVN4T4Tz_helpmate.pgn", "", "", 7, 18, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "1k1Q4/8/K7/8/8/8/8/8 b - - 18 81"));
    list.add(new PgnTestCase("lichess_SDy9Y3D0_helpmate.pgn", "", "", 20, 11, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/7k/8/4q2K w - - 0 66"));
    list.add(new PgnTestCase("lichess_Sdz1yTzS_helpmate.pgn", "", "", 6, 26, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "1k1Q4/8/K7/8/8/8/8/8 b - - 26 80"));
    list.add(new PgnTestCase("lichess_sMv8Hh43_helpmate.pgn", "", "", 11, 8, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "8/6p1/5kP1/2r4K/5q2/8/8/8 w - - 2 53"));
    list.add(new PgnTestCase("lichess_sp0oSyY2_helpmate.pgn", "", "", 4, 8, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/1p6/8/8/7k/b7/q6K w - - 2 57"));
    list.add(new PgnTestCase("lichess_SwCSdv8K_helpmate.pgn", "", "", 17, 22, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/7k/6q1/6K1 w - - 22 75"));
    list.add(new PgnTestCase("lichess_SX3iSehH_helpmate.pgn", "", "", 16, 10, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "Q4K2/8/1k6/Q7/8/8/8/8 b - - 10 63"));
    list.add(new PgnTestCase("lichess_Szu1FAr2_helpmate.pgn", "", "", 3, 13, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/pp4p1/6p1/8/8/7k/8/q6K w - - 13 60"));
    list.add(new PgnTestCase("lichess_U5uv4wPs_helpmate.pgn", "", "", 10, 18, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/6p1/8/7k/8/r6K w - - 5 90"));
    list.add(new PgnTestCase("lichess_uBSFb2kx_helpmate.pgn", "", "", 16, 41, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "5B1k/8/8/8/8/7R/Q5K1/8 b - - 10 100"));
    list.add(new PgnTestCase("lichess_UdUE6AvZ_helpmate.pgn", "", "", 16, 16, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "1k1Q4/8/K7/8/8/8/8/8 b - - 16 59"));
    list.add(new PgnTestCase("lichess_UuNO3dGf_helpmate.pgn", "", "", 5, 12, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "kQ6/8/8/8/8/8/7Q/5K2 b - - 12 85"));
    list.add(new PgnTestCase("lichess_uwwUPCFw_helpmate.pgn", "", "", 5, 14, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/7k/6q1/6K1 w - - 14 71"));
    list.add(new PgnTestCase("lichess_ux9sCCZ8_helpmate.pgn", "", "", 5, 5, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "5r1k/p5p1/6P1/2pp1K2/1p1b2P1/4q3/P1P5/8 w - - 0 35"));
    list.add(new PgnTestCase("lichess_v4xJHVln_helpmate.pgn", "", "", 3, 15, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/7k/4Kn2/4q2q w - - 4 76"));
    list.add(new PgnTestCase("lichess_VdYuP1l4_helpmate.pgn", "", "", 20, 14, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "7q/8/8/8/8/8/8/5k1K w - - 14 74"));
    list.add(new PgnTestCase("lichess_VeCbG5uw_helpmate.pgn", "", "", 12, 26, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "1k1Q4/8/K7/8/8/8/8/8 b - - 26 77"));
    list.add(new PgnTestCase("lichess_VIdrelSz_helpmate.pgn", "", "", 4, 5, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "7r/2PR4/6pk/6P1/7K/r7/8/8 b - - 0 40"));
    list.add(new PgnTestCase("lichess_Vnqfiwd6_helpmate.pgn", "", "", 5, 17, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/6p1/5p2/8/6k1/8/q5K1/1q6 w - - 8 67"));
    list.add(new PgnTestCase("lichess_vS3VdvtP_helpmate.pgn", "", "", 3, 12, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/6k1/8/5K2/4q2q w - - 12 68"));
    list.add(new PgnTestCase("lichess_vytUGbcM_helpmate.pgn", "", "", 19, 22, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "8/8/8/8/2K5/k7/8/Q7 b - - 22 86"));
    list.add(new PgnTestCase("lichess_W5huoA6x_helpmate.pgn", "", "", 6, 26, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/7k/8/q6K w - - 26 89"));
    list.add(new PgnTestCase("lichess_wCL9HARO_helpmate.pgn", "", "", 6, 15, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "1k1Q4/8/K7/8/8/8/8/8 b - - 14 75"));
    list.add(new PgnTestCase("lichess_WdedP6nc_helpmate.pgn", "", "", 6, 22, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "7q/8/8/8/8/8/8/5k1K w - - 22 73"));
    list.add(new PgnTestCase("lichess_wUCLya3K_helpmate.pgn", "", "", 12, 7, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/4qK1k/8/6q1 w - - 6 86"));
    list.add(new PgnTestCase("lichess_wuHnMP2q_helpmate.pgn", "", "", 4, 7, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "7R/7p/p5p1/8/2p4P/5P1k/P6q/5RK1 w - - 0 34"));
    list.add(new PgnTestCase("lichess_xD85FRxa_helpmate.pgn", "", "", 9, 30, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "1k1Q4/8/K7/8/8/8/8/8 b - - 30 81"));
    list.add(new PgnTestCase("lichess_xfvaW7PK_helpmate.pgn", "", "", 12, 22, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "8/8/8/8/2K5/k7/8/Q7 b - - 22 62"));
    list.add(new PgnTestCase("lichess_xmpqlKFo_helpmate.pgn", "", "", 9, 18, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "1k1Q4/8/K7/8/8/8/8/8 b - - 18 69"));
    list.add(new PgnTestCase("lichess_XtJJQxwF_helpmate.pgn", "", "", 10, 18, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/7k/6q1/6K1 w - - 10 69"));
    list.add(new PgnTestCase("lichess_XtvgUXHD_helpmate.pgn", "", "", 16, 15, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/6k1/8/q7/1q5K w - - 14 93"));
    list.add(new PgnTestCase("lichess_YGj1C2WB_helpmate.pgn", "", "", 13, 16, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/7k/6q1/6K1 w - - 16 79"));
    list.add(new PgnTestCase("lichess_YictG6WZ_helpmate.pgn", "", "", 10, 37, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "k1K5/8/8/8/8/8/8/R7 b - - 37 73"));
    list.add(new PgnTestCase("lichess_yjfLthhQ_helpmate.pgn", "", "", 6, 12, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/5K1k/6q1/2q5 w - - 12 70"));
    list.add(new PgnTestCase("lichess_YRvWOIpy_helpmate.pgn", "", "", 3, 10, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.BLACK_ONLY, "8/8/KQk5/8/8/8/8/3R4 b - - 10 64"));
    list.add(new PgnTestCase("lichess_z3xkCyBY_helpmate.pgn", "", "", 14, 11, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "7k/7Q/7K/2p3P1/2P5/8/8/8 b - - 0 59"));
    list.add(new PgnTestCase("lichess_zmelXKvA_helpmate.pgn", "", "", 23, 14, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/q7/8/8/8/8/K1k5/4b3 w - - 14 80"));
    list.add(new PgnTestCase("test_lichess_V7eJ1RR9_helpmate.pgn", "", "", 14, 34, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/7k/8/q6K w - - 34 74"));

    return new PgnTestCaseList(PgnTest.CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR_HELPMATE, list);
  }

  private static PgnTestCaseList createTestCasesChaLichessQuickDepthThree() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("lichess_pUEeHLfu.pgn", "", "", 20, 18, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "7k/5Q2/6Q1/6P1/5P1p/6K1/7P/8 w - - 0 78"));
    list.add(new PgnTestCase("lichess_UNX9jAKK.pgn", "", "", 9, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/p4kp1/6Pp/7K/2rq4/7P/8/8 b - - 0 51"));

    return new PgnTestCaseList(PgnTest.CHA_LICHESS_QUICK_DEPTH_THREE, list);
  }

  private static PgnTestCaseList createTestCasesChaLichessQuickDepthFour() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("lichess_mf4MFw9v.pgn", "", "", 23, 12, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/8/pp6/k1p5/1qQ5/K7 w - - 2 72"));

    return new PgnTestCaseList(PgnTest.CHA_LICHESS_QUICK_DEPTH_FOUR, list);
  }

  private static PgnTestCaseList createTestCasesChaAmbrona() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("ambrona_01.pgn", "", "", -1, 10, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "5brk/4p1p1/3pP1P1/1B1P2p1/3p2p1/3P4/4K1P1/8 w - - 10 100"));
    list.add(new PgnTestCase("ambrona_02.pgn", "", "", -1, 10, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "8/1p4p1/1Pp3p1/k1P3p1/1pP3Pb/1P4p1/6P1/7K w - - 10 100"));
    list.add(new PgnTestCase("ambrona_03.pgn", "", "", -1, 10, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "8/1k5B/7b/8/1p1p1p1p/1PpP1P1P/2P3K1/N3b3 b - - 10 100"));
    list.add(new PgnTestCase("ambrona_04.pgn", "", "", -1, 10, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "7b/1k5B/7b/8/1p1p1p1p/1PpP1P1P/2P3K1/N7 b - - 10 100"));
    list.add(new PgnTestCase("ambrona_04_proof_game.pgn", "", "", 4, 10, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "7b/1k5B/7b/8/1p1p1p1p/1PpP1P1P/2P1P1K1/N7 b - - 6 56"));
    list.add(new PgnTestCase("ambrona_05.pgn", "", "", -1, 10, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "4K3/8/8/8/8/p1p2p1p/P1pppp1P/bnrqkrnb b - - 10 100"));
    list.add(new PgnTestCase("ambrona_06.pgn", "", "", -1, 10, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "k1bK4/1p1p4/1PpPp3/2P1Pp2/2p1pP2/2p1P3/2P5/8 w - - 10 100"));
    list.add(new PgnTestCase("ambrona_07.pgn", "", "", -1, 10, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "Bb2kb2/bKp1p1p1/1pP1P1P1/pP6/6P1/P7/8/8 b - - 10 100"));
    list.add(new PgnTestCase("ambrona_08.pgn", "", "", -1, 10, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "Bb2kb2/bKp1p1p1/1pP1P1P1/1P6/p5P1/P7/8/8 b - - 10 100"));

    // cannot use as position illegal (too many white light-squared bishops)
    // nineth example from Ambrona, file name would become as below, but no such file as not used
    // ae_09.pgn

    list.add(new PgnTestCase("ambrona_10.pgn", "", "", -1, 10, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "7k/8/1p6/1Pp5/2Pp4/pB1Pp1p1/P1B1P1P1/3B2K1 b - - 10 100"));

    list.add(new PgnTestCase("ambrona_11_lichess_FKr42ZRT.pgn", "", "", 17, 16, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/7p/5p1P/5p1K/5Pp1/6P1/5kb1 b - - 13 63"));
    list.add(new PgnTestCase("ambrona_12_lichess_bKHPqNEw.pgn", "", "", 5, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "1k6/1P5p/BP3p2/1P6/8/8/5PKP/8 b - - 0 41"));
    list.add(new PgnTestCase("ambrona_13_lichess_OawUhnkq.pgn", "", "", 6, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "5r1k/6P1/7K/5q2/8/8/8/8 b - - 0 51"));
    list.add(new PgnTestCase("ambrona_14.pgn", "", "", -1, 10, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "k7/Q6r/2b5/1pBp1p1p/1P1P1P1P/KP6/1P6/8 b - - 10 100"));
    list.add(new PgnTestCase("ambrona_15_lichess_QRvIMh3z.pgn", "", "", 16, 46, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "2b5/1p6/pPp3k1/2Pp3p/P2PpBpP/4P1P1/5K2/8 b - - 46 59"));
    list.add(new PgnTestCase("ambrona_16.pgn", "", "", -1, 10, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1k6/p1p1p1p1/P1P1P1P1/p1p1p1p1/8/8/P1P1P1P1/4K3 w - - 10 100"));
    list.add(new PgnTestCase("ambrona_17.pgn", "", "", -1, 10, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "rnb1b3/pk1p4/p1pPp1p1/P1P1P1P1/RBP5/P7/5B2/7K w - - 10 100"));

    return new PgnTestCaseList(PgnTest.CHA_AMBRONA, list);
  }

  // Fixtures the geometric pawn-wall classifier accepts as YES: chain spans file a to h, both kings on opposite
  // sides, every pawn on the board is either a chain element or attacks a chain element.
  private static PgnTestCaseList createTestCasesPawnWallYes() {
    final List<PgnTestCase> list = new ArrayList<>();

    // pawn walls without bishops - clean horizontal / zig-zag
    list.add(new PgnTestCase("pawn_wall_horizontal_1.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/8/8/p1p1p1p1/P1P1P1P1/8/8/4K3 w - - 0 50"));

    list.add(new PgnTestCase("pawn_wall_horizontal_2.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/8/8/p1p2p1p/P1P2P1P/8/8/4K3 w - - 0 50"));

    list.add(new PgnTestCase("pawn_wall_zig_zag_no_holes.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/5p1p/4pPpP/1p1pP1P1/pPpP4/P1P5/8/4K3 w - - 0 50"));

    list.add(new PgnTestCase("pawn_wall_zig_zag_holes_1.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/5p1p/4pP1P/3pP3/p1pP4/P1P5/8/4K3 w - - 0 50"));

    list.add(new PgnTestCase("pawn_wall_zig_zag_holes_2.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/5p1p/1p2pP1P/pPp1P3/P1P5/8/8/4K3 w - - 0 50"));

    // pawn walls with bishops - colour-locked, all pawns involved
    list.add(new PgnTestCase("pawn_wall_bishop_1.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/6k1/p1p1p3/P1P1Pp1p/5P1P/2B5/6K1/8 b - - 0 50"));

    list.add(new PgnTestCase("pawn_wall_bishop_2.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "2b5/6k1/p1p1p3/P1P1Pp1p/5P1P/8/6K1/8 b - - 0 50"));

    list.add(new PgnTestCase("pawn_wall_bishop_3.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "2b5/6k1/p1p1p3/P1P1Pp1p/5P1P/2B5/6K1/8 b - - 0 50"));

    list.add(new PgnTestCase("pawn_wall_bishop_4.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/6k1/p1p1p3/P1P1Pp1p/5P1P/1bB5/6K1/8 b - - 0 50"));

    list.add(new PgnTestCase("pawn_wall_bishop_5.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3B4/1b4k1/p1p1p3/P1P1Pp1p/5P1P/8/6K1/8 b - - 0 50"));

    // interlocked pawn structure: rank-3 own pawns attack the rank-4 chain elements (involved)
    list.add(new PgnTestCase("pawn_wall_without_en_passant_capture_1.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/8/8/p1p1p1p1/PpPpPpPp/1P1P1P1P/8/4K3 b - - 0 50"));

    list.add(new PgnTestCase("pawn_wall_without_en_passant_capture_2.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/8/8/p1p1p1p1/P1PpPpPp/3P1P1P/8/4K3 b - - 0 50"));

    list.add(new PgnTestCase("pawn_wall_without_en_passant_capture_3.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/8/7p/p1p2p1P/P1P1pP2/4P3/8/4K3 b - - 0 50"));

    // Ambrona 83
    list.add(new PgnTestCase("pawn_wall_ambrona_real_game.pgn", "", "", 5, 83, CheckmateOrStalemate.NA, 2,
        InsufficientMaterial.NONE, "1k6/5p1p/1p2pP1P/1P2P3/8/1K6/8/8 b - - 83 95"));

    return new PgnTestCaseList(PgnTest.CHA_PAWN_WALL_YES, list);
  }

  // Fixtures that look like a pawn wall but are not - the geometric verdict must return UNKNOWN.
  // Reasons vary: kings on the wrong side of the chain (no barrier between them), en-passant rights
  // breaching the wall, or chains that fail the all-pawns-involved gate (Norgaard examples and the
  // Norgaard "beyond dead position" examples - the chain does not span from leftmost to rightmost file).
  private static PgnTestCaseList createTestCasesPawnWallNo() {
    final List<PgnTestCase> list = new ArrayList<>();

    // pawn walls but king on wrong side - position is actually winnable
    list.add(new PgnTestCase("pawn_wall_both_kings_on_white_side.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/p1p1p1p1/P1P1P1P1/8/1k6/4K3 w - - 0 50"));

    list.add(new PgnTestCase("pawn_wall_both_kings_on_black_side.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3k2K1/1p6/pPp2p1p/P1P2P1P/8/8/8/8 w - - 0 50"));

    // en-passant capture breaches the wall - the position is winnable
    list.add(new PgnTestCase("pawn_wall_with_en_passant_capture_1.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/8/8/p1p1p1p1/PpPpPpPp/1P1P1P1P/8/4K3 b - a3 0 50"));

    list.add(new PgnTestCase("pawn_wall_with_en_passant_capture_2.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/8/8/p1p1p1p1/P1PpPpPp/3P1P1P/8/4K3 b - e3 0 50"));

    list.add(new PgnTestCase("pawn_wall_with_en_passant_capture_3.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/8/7p/p1p2p1P/P1P1pP2/4P3/8/4K3 b - f3 0 50"));

    // en-passant with bishops - legal en-passant capture breaches the wall
    list.add(new PgnTestCase("pawn_wall_bishop_en_passant_capture_legal.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4k3/8/8/p1p1p3/P1P1Pp1p/1B3P1P/8/4K3 b - e3 0 50"));

    // Norgaard "dead position" - chain is not all-pawns-involved (floating pawns)
    list.add(new PgnTestCase("pawn_wall_norgaard_additional_own_pawns_not_marched_up.pgn", "", "", 5, 6,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "6k1/1p1p1p1p/1P1P1P1P/8/8/4K3/1P1P1P1P/8 w - - 0 39"));

    // TODO today's Ambrona website (full or quick) sees unwinnable for both sides
    list.add(new PgnTestCase("pawn_wall_norgaard_additional_own_pawns_not_marched_up_with_opponent_pawns_between.pgn",
        "", "", 5, 3, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE,
        "1k6/p1p1p1p1/P1P1P1P1/p1p1p1p1/8/8/P1P1P1P1/4K3 w - - 0 34"));

    // Norgaard "beyond dead position" - chain has no a-file (resp. h-file) pawn, so it does not span
    // from leftmost to rightmost file; the geometric chain check rejects.
    list.add(new PgnTestCase("pawn_wall_norgaard_additional_own_pawns_marched_up_example_1.pgn", "",
        "638...Kg8 (1) 688.Ka3 (100)", 5, 100, CheckmateOrStalemate.NA, 2, InsufficientMaterial.NONE,
        "7k/1p1p1p1p/1P1P1P1P/1P1P1P1P/8/K7/8/8 b - - 100 688"));

    list.add(new PgnTestCase("pawn_wall_norgaard_additional_own_pawns_marched_up_example_2.pgn", "",
        "833...Kb8 (1) 883.Kg2 (100)", 5, 100, CheckmateOrStalemate.NA, 2, InsufficientMaterial.NONE,
        "k7/p1p1p1p1/P1P1P1P1/P1P1P1P1/8/8/6K1/8 b - - 100 883"));

    return new PgnTestCaseList(PgnTest.CHA_PAWN_WALL_NO, list);
  }

  // Test fixtures for ShallowTerminationOracle. Each fixture exercises a specific depth (1, 2, or 3) at which the
  // oracle should first find a terminal status, plus a control (no termination within 3 plies). Tests both
  // White-to-move and Black-to-move variants with independent (non-mirror) positions.
  private static PgnTestCaseList createTestCasesShallowTermination() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_m1_white_to_move.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "6k1/5ppp/8/8/8/8/5PPP/R3K3 w Q - 0 1"));
    list.add(new PgnTestCase("02_m1_black_to_move.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "1q6/8/8/P7/8/n2k4/8/3K4 b - - 0 1"));
    list.add(new PgnTestCase("03_m2_white_to_move.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "kbK5/pp6/1P6/8/8/8/8/R7 w - - 0 1"));
    list.add(new PgnTestCase("04_m2_black_to_move.pgn", "", "", -1, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "rNq1k2K/P1p1pp2/2Rp1P2/5b2/2p3r1/8/8/8 b q - 5 3"));
    list.add(new PgnTestCase("05_helpmate2_white_to_move.pgn", "", "", -1, 1, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/8/8/5k2/2p5/4K3 w - - 1 50"));
    list.add(new PgnTestCase("06_helpmate2_black_to_move.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "8/5K2/7k/8/8/8/6Q1/8 b - - 0 50"));
    list.add(new PgnTestCase("07_helpmate3_white_to_move.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "8/5K1k/4N3/2B5/8/8/8/8 w - - 0 50"));
    list.add(new PgnTestCase("08_helpmate3_black_to_move.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/5b2/8/2b3k1/8/6K1 b - - 0 50"));
    list.add(new PgnTestCase("09_control_white_to_move.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/8/rk6/8/8/1K6/8 w - - 0 50"));
    list.add(new PgnTestCase("10_control_black_to_move.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "k2bb1K1/8/8/8/8/8/8/8 b - - 0 50"));

    return new PgnTestCaseList(PgnTest.CHA_SHALLOW_TERMINATION, list);
  }

  private static PgnTestCaseList createTestCasesHelpmateBeyondFivefold() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_beyond_fivefold.pgn",
        "repPos=4: 50.Kc8 52.Kc8 54.Kc8 56.Kc8; repPos=4: 50...Kc6 52...Kc6 54...Kc6 56...Kc6; repPos=4: 51.Kd8 53.Kd8 55.Kd8 57.Kd8; repPos=5: 51...Kd6 53...Kd6 55...Kd6 57...Kd6",
        "", -1, 34, CheckmateOrStalemate.NA, 5, InsufficientMaterial.NONE, "3K4/2P5/3k4/4r3/8/1r6/8/8 w - - 34 58"));

    list.add(new PgnTestCase("02_beyond_fivefold.pgn",
        "repPos=4: 3...Kxg8 5...Kg8 7...Kg8 9...Kg8; repPos=4: 4.Kh6 6.Kh6 8.Kh6 10.Kh6; repPos=4: 4...Kh8 6...Kh8 8...Kh8 10...Kh8; repPos=4: 5.Kg6 7.Kg6 9.Kg6 11.Kg6",
        "", 1, 15, CheckmateOrStalemate.NA, 4, InsufficientMaterial.BLACK_ONLY, "7k/8/2R3K1/8/8/8/8/8 b - - 15 11"));

    return new PgnTestCaseList(PgnTest.CHA_HELPMATE_BEYOND_FIVEFOLD, list);
  }

  private static PgnTestCaseList createTestCasesHelpmateBeyondSeventyFive() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_beyond_seventy_five.pgn", "", "26.NA (1) 75...NA (100) 99...NA (148)", -1, 148,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "8/5r2/p3k3/P7/8/1K6/8/8 w - - 148 100"));

    list.add(new PgnTestCase("02_beyond_seventy_five.pgn", "", "26.NA (1) 75...NA (100) 100.NA (149)", -1, 149,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.BLACK_ONLY, "7k/8/2R3K1/8/8/8/8/8 b - - 149 100"));

    return new PgnTestCaseList(PgnTest.CHA_HELPMATE_BEYOND_SEVENTY_FIVE, list);
  }

  private static PgnTestCaseList createTestCasesBasicMateDraw() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_draw_KR_K.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "8/8/8/8/8/8/1R1K4/k7 b - - 0 50"));
    list.add(new PgnTestCase("02_draw_KQ_K.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "8/8/8/8/8/8/Q2K4/1k6 b - - 0 50"));
    list.add(new PgnTestCase("03_draw_KBwBb_K.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "8/8/8/8/8/5B2/4K2B/6k1 b - - 0 50"));
    list.add(new PgnTestCase("04_draw_KBwN_K.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "8/8/8/8/8/5B2/4K2N/6k1 b - - 0 1"));

    return new PgnTestCaseList(PgnTest.CHA_BASIC_MATE_DRAW, list);
  }

  private static PgnTestCaseList createTestCasesBasicMateHelpmate04() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_helpmate_04_KR_K.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "8/8/8/8/8/1K1R4/8/1k6 b - - 0 50"));
    list.add(new PgnTestCase("02_helpmate_04_KQ_K.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "8/8/8/8/k7/2K5/3Q4/8 b - - 0 50"));
    list.add(new PgnTestCase("03_helpmate_04_KBwBb_K.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "2k5/8/K7/8/BB6/8/8/8 b - - 0 50"));
    list.add(new PgnTestCase("04_helpmate_04_KBwN_K.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "2k5/8/K7/4N3/B7/8/8/8 b - - 0 1"));

    return new PgnTestCaseList(PgnTest.CHA_BASIC_MATE_HELPMATE_04, list);
  }

  private static PgnTestCaseList createTestCasesBasicMateHelpmate10() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_helpmate_10_KR_K.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "8/8/k7/6K1/8/8/8/1R6 b - - 0 50"));
    list.add(new PgnTestCase("02_helpmate_10_KQ_K.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "8/8/k7/6K1/8/8/8/1Q6 b - - 0 50"));
    list.add(new PgnTestCase("03_helpmate_10_KBwBb_K.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "1k6/8/5B2/5B2/8/K7/8/8 b - - 0 50"));
    list.add(new PgnTestCase("04_helpmate_10_KBwN_K.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "8/k7/8/KB6/8/8/8/6N1 b - - 0 50"));

    return new PgnTestCaseList(PgnTest.CHA_BASIC_MATE_HELPMATE_10, list);
  }

  private static PgnTestCaseList createTestCasesBasicMateHelpmateAroundMax() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_helpmate_around_max_KR_K.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "7K/6R1/8/8/8/8/8/k7 b - - 0 50"));
    list.add(new PgnTestCase("02_helpmate_around_max_KQ_K.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "7K/6Q1/8/8/8/8/8/k7 b - - 0 50"));
    list.add(new PgnTestCase("03_helpmate_around_max_KBwBb_K.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "5BBK/8/8/8/8/8/8/k7 b - - 0 50"));
    list.add(new PgnTestCase("04_helpmate_around_max_KBwN_K.pgn", "", "", -1, 0, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "5NBK/8/8/8/8/8/8/k7 b - - 0 50"));

    return new PgnTestCaseList(PgnTest.CHA_BASIC_MATE_HELPMATE_AROUND_MAX, list);
  }

  private static PgnTestCaseList createTestCasesLastMoveAddedAccidentally() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_last_move_added_accidentally_result_white_win_last_move_white_draws.pgn", "", "", 27,
        21, CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "8/6Pk/2b5/2P1K3/8/8/8/8 b - - 2 87"));
    list.add(new PgnTestCase("03_last_move_added_accidentally_result_draw_black_last_move_loses.pgn", "", "", 7, 18,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "8/8/8/4k1pP/6K1/8/8/8 w - - 2 69"));
    list.add(new PgnTestCase("04_last_move_added_accidentally_result_draw_black_last_move_loses.pgn", "", "", 8, 13,
        CheckmateOrStalemate.NA, 1, InsufficientMaterial.NONE, "8/P1R5/7p/4kppP/6P1/8/1K6/r7 w - - 10 66"));

    return new PgnTestCaseList(PgnTest.LAST_MOVE_ADDED_ACCIDENTALLY, list);
  }

  private static PgnTestCaseList createTestCasesMaxSamePiecePromotionWhite() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_white_max_piece_ten_rooks.pgn", "", "", 47, 48, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "1R6/1R6/2R1kbR1/8/R7/2NB1N2/3BKR2/3Q4 b - - 0 85"));
    list.add(new PgnTestCase("02_white_max_piece_ten_knights.pgn", "", "", 47, 34, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "8/kN6/NNN2b1p/2N5/N1N4R/2N5/n1QB1KB1/1R6 b - - 0 75"));
    list.add(new PgnTestCase("03_white_max_piece_ten_bishops_nine_light_squared.pgn", "", "", 47, 11,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE,
        "6Br/2k4p/4B3/3B2N1/5b2/1B1B1B2/4B1B1/2qK1B1R w - - 0 67"));
    list.add(new PgnTestCase("04_white_max_piece_ten_bishops_nine_darks_squared.pgn", "", "", 47, 11,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE,
        "6Br/2k4p/4B3/3B2N1/5b2/1B1B1B2/4B1B1/2qK1B1R w - - 0 67"));
    list.add(new PgnTestCase("05_white_max_piece_nine_queens.pgn", "", "", 47, 16, CheckmateOrStalemate.STALEMATE, 1,
        InsufficientMaterial.NONE, "2Q2Q2/k7/bn5B/1Q6/3QQQ1K/Q7/8/5BNR b - - 4 72"));

    return new PgnTestCaseList(PgnTest.MAX_SAME_PIECE_PROMOTION_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesMaxSamePiecePromotionBlack() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_black_max_piece_ten_rooks.pgn", "", "", 56, 63, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "2rk3r/2rbQ1b1/6r1/1n3r2/KB3r1r/nN6/3r3r/8 b - - 0 118"));
    list.add(new PgnTestCase("02_black_max_piece_ten_knights.pgn", "", "", 42, 42, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "r1bB1b1K/1BR1n1r1/Q3nnn1/1q3Nkn/1N1n2nn/nn6/8/2R5 w - - 42 130"));
    list.add(new PgnTestCase("03_black_max_piece_ten_bishops_nine_light_squared.pgn", "", "", 56, 27,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE,
        "1nb2bn1/5b2/5k2/3b1b2/1Rb3b1/2Pb4/r3b1b1/2K4r w - - 2 102"));
    list.add(new PgnTestCase("04_black_max_piece_ten_bishops_nine_dark_squared.pgn", "", "", 56, 32,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE,
        "4kb2/8/2n2n2/3b2b1/3b1b2/P1b1b2r/1b3bR1/7K w - - 0 96"));
    list.add(new PgnTestCase("05_black_max_piece_nine_queens.pgn", "", "", 42, 35, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.NONE, "r1b1qb1K/1n4r1/8/2n2k2/2Bq4/8/8/7q w - - 0 139"));

    return new PgnTestCaseList(PgnTest.MAX_SAME_PIECE_PROMOTION_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesMaxSamePiecePromotionByCombined() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_combined_max_piece_twenty_rooks.pgn", "", "", 5, 28, CheckmateOrStalemate.CHECKMATE, 1,
        InsufficientMaterial.WHITE_ONLY, "8/5k2/1r6/8/8/3r4/2r5/1r3K2 w - - 1 114"));
    list.add(new PgnTestCase("02_combined_max_piece_twenty_knights.pgn", "", "", 7, 34, CheckmateOrStalemate.CHECKMATE,
        1, InsufficientMaterial.NONE, "7k/8/3n4/4n3/3n4/nn1n4/8/1KnN4 w - - 6 126"));
    list.add(new PgnTestCase("03_combined_max_piece_twenty_bishops.pgn", "", "", 7, 22, CheckmateOrStalemate.CHECKMATE,
        1, InsufficientMaterial.NONE, "2B1B3/3B2kR/4BrB1/2B5/B7/6K1/2B3B1/8 b - - 4 98"));
    list.add(new PgnTestCase("04_combined_max_piece_eighteen_queens.pgn", "", "", 7, 35, CheckmateOrStalemate.CHECKMATE,
        1, InsufficientMaterial.BLACK_ONLY, "8/8/8/8/8/8/5KQk/8 b - - 14 110"));

    return new PgnTestCaseList(PgnTest.MAX_SAME_PIECE_PROMOTION_COMBINED, list);
  }

  private static PgnTestCaseList createTestCasesDoubleCheckCheckmateBizarreWhite() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_white_double_check_checkmate_bizarre_one_empty_square.pgn", "", "", -1, 1,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE, "8/2B5/4NN2/2bNkN2/2NNNN2/8/8/4K3 b - - 1 100"));
    list.add(new PgnTestCase("02_white_double_check_checkmate_bizarre_all_empty_squares.pgn", "", "", -1, 1,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE, "8/2B2N2/7R/2b1k3/1R6/8/3R1R2/4K3 b - - 1 100"));

    return new PgnTestCaseList(PgnTest.DOUBLE_CHECK_CHECKMATE_BIZARRE_CHECKMATE_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesDoubleCheckCheckmateBizarreBlack() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_black_double_check_checkmate_bizarre_one_empty_square.pgn", "", "", -1, 1,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE, "1k6/3b4/nn2B3/nKn5/nnnn4/8/8/8 w - - 1 101"));
    list.add(new PgnTestCase("02_black_double_check_checkmate_bizarre_all_empty_squares.pgn", "", "", -1, 1,
        CheckmateOrStalemate.CHECKMATE, 1, InsufficientMaterial.NONE, "2rkr3/r7/3K4/r7/2n5/4B3/7b/8 w - - 1 101"));

    return new PgnTestCaseList(PgnTest.DOUBLE_CHECK_CHECKMATE_BIZARRE_CHECKMATE_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesBlogInstant() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_blog_instant_K_K.pgn", "", "", 5, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BOTH, "8/8/6k1/8/5K2/8/8/8 b - - 0 26"));
    list.add(new PgnTestCase("02_blog_instant_KN_K.pgn", "", "", 5, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BOTH, "8/5k2/8/8/8/8/8/1NK5 b - - 0 26"));

    return new PgnTestCaseList(PgnTest.MONSTER_BLOG_INSTANT, list);
  }

  private static PgnTestCaseList createTestCasesBlogPredraw() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_blog_predraw_KBw_KBb.pgn", "", "", 17, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/3B1k2/7b/8/8/8/5K2/8 b - - 0 29"));
    list.add(new PgnTestCase("02_blog_predraw_KN_KB.pgn", "", "", 5, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "2b5/3k4/8/6K1/8/8/3N4/8 b - - 0 27"));
    list.add(new PgnTestCase("03_blog_predraw_KB_KN.pgn", "", "", 7, 8, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/6k1/7n/8/8/7B/5K2/8 b - - 0 30"));
    list.add(new PgnTestCase("04_blog_predraw_KNN_K.pgn", "", "", 8, 13, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.BLACK_ONLY, "8/8/7k/8/4N3/3N4/6K1/8 b - - 0 37"));
    list.add(new PgnTestCase("05_blog_predraw_KN_KN.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "1n6/8/1k6/8/3K4/8/8/1N6 b - - 0 24"));

    return new PgnTestCaseList(PgnTest.MONSTER_BLOG_PREDRAW, list);
  }

  private static PgnTestCaseList createTestCasesBlogTimeout() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_blog_timeout_K_more.pgn", "", "", 5, 3, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "1nbk1b2/1qpp1p2/8/8/3K4/8/8/8 b - - 3 20"));
    list.add(new PgnTestCase("02_blog_timeout_KN_KQ.pgn", "", "", 5, 2, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/1q6/3k4/8/2N5/8/5K2 b - - 1 26"));
    list.add(new PgnTestCase("03_blog_timeout_KN_KNP_forced_mate.pgn", "", "", 5, 14, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3N2nk/4p3/5pKp/6p1/8/8/8/8 b - - 14 36"));
    list.add(new PgnTestCase("04_blog_timeout_KN_KP.pgn", "", "", 23, 9, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "2k5/8/p7/8/8/2N1K3/8/8 b - - 2 37"));
    list.add(new PgnTestCase("05_blog_timeout_KN_KR.pgn", "", "", 10, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/2k4N/8/8/8/3r4/4K3/8 b - - 1 29"));
    list.add(new PgnTestCase("06_blog_timeout_KB_KR.pgn", "", "", 20, 7, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "7r/2k5/8/8/5B2/8/4K3/8 b - - 1 37"));
    list.add(new PgnTestCase("07_blog_timeout_KBw_KRBw.pgn", "", "", 17, 15, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.WHITE_ONLY, "8/8/2r2k2/3b4/8/8/2BK4/8 b - - 15 40"));
    list.add(new PgnTestCase("08_blog_timeout_KB_KNP_forced_mate.pgn", "", "", 22, 27, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "knB5/3p4/pKp5/p7/1p6/8/8/8 b - - 12 60"));
    list.add(new PgnTestCase("09_blog_timeout_KB_KP.pgn", "", "", 5, 6, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/5p2/8/4k3/1K6/8/8/5B2 b - - 0 28"));
    list.add(new PgnTestCase("10_blog_timeout_KBb_KRBw.pgn", "", "", 9, 4, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "4r3/3b4/4k3/8/5K2/8/3B4/8 b - - 4 37"));
    list.add(new PgnTestCase("11_blog_timeout_KNN_KP_forced_mate.pgn", "", "", 10, 15, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "7k/8/5N1K/8/5N1p/8/8/8 b - - 1 49"));
    list.add(new PgnTestCase("12_blog_timeout_KNN_KP.pgn", "", "", 15, 5, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/8/8/2kp4/8/8/2NKN3/8 b - - 5 41"));
    list.add(new PgnTestCase("13_blog_timeout_KNN_KB.pgn", "", "", 25, 17, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "k7/1bKN4/8/1N6/8/8/8/8 b - - 17 51"));
    list.add(new PgnTestCase("14_blog_timeout_KNN_KQQQQQQQQQ.pgn", "", "", 13, 26, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "3q4/3N3K/8/5N2/8/7k/8/qqqqqqqq b - - 1 88"));

    return new PgnTestCaseList(PgnTest.MONSTER_BLOG_TIMEOUT, list);
  }

  private static PgnTestCaseList createTestCasesRepetitionQuizOne() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_claim_for_board_position_incorrect_initial_en_passant.pgn", "", "", -1, 10,
        CheckmateOrStalemate.NA, 2, InsufficientMaterial.NONE, "4R3/4b2p/3kn1p1/1p3pP1/5P2/3KP3/7P/8 w - - 8 75"));
    list.add(new PgnTestCase("02_claim_for_own_move_correct.pgn", "", "", 1, 10, CheckmateOrStalemate.NA, 1,
        InsufficientMaterial.NONE, "8/pp3p1k/2p2q1p/3r1P2/5R2/3Q3P/P1P2P2/7K w - - 7 74"));
    list.add(new PgnTestCase("03_claim_for_own_move_incorrect_castling_right_lost_for_king_move.pgn", "", "", 16, 8,
        CheckmateOrStalemate.NA, 2, InsufficientMaterial.NONE,
        "rn1Nk2r/pp2p2p/3p2p1/1bp5/5Pn1/2N1b3/PPP3PP/R1BQK2R b kq - 8 15"));
    list.add(new PgnTestCase("04_claim_for_own_move_incorrect.pgn", "", "", 1, 10, CheckmateOrStalemate.NA, 2,
        InsufficientMaterial.NONE, "R7/2q2p2/5k1Q/pp2r3/2p1P1P1/7P/6K1/8 b - - 6 73"));
    list.add(new PgnTestCase("05_claim_for_own_move_correct_but_makes_move_on_board.pgn", "repPos=3: 72.Ke6 74.Ke6", "",
        -1, 18, CheckmateOrStalemate.NA, 3, InsufficientMaterial.BLACK_ONLY, "8/6k1/4KN2/3B4/8/8/8/8 b - - 18 74"));
    list.add(new PgnTestCase("06_claim_for_board_position_correct.pgn",
        "repPos=3: 71.Qxh6+ 73.Qh6+ 75.Qh6+; repPos=3: 71...Kg8 73...Kg8 75...Kg8", "", 2, 11, CheckmateOrStalemate.NA,
        3, InsufficientMaterial.NONE, "r3qrk1/p1p1b3/4pn1Q/3p4/8/2NB4/PPP2PPP/R5K1 w - - 9 76"));
    list.add(
        new PgnTestCase("07_claim_for_board_position_correct_but_touch_move.pgn", "repPos=3: 70.Kxc6 72.Kc6 74.Kc6", "",
            1, 10, CheckmateOrStalemate.NA, 3, InsufficientMaterial.BLACK_ONLY, "3k4/8/2K5/2P5/8/8/8/8 b - - 8 74"));
    list.add(new PgnTestCase("08_claim_for_own_move_correct.pgn", "", "", -1, 10, CheckmateOrStalemate.NA, 2,
        InsufficientMaterial.NONE, "3Q1k2/5p1p/1p2q3/2p5/6n1/4P3/PP2R3/6K1 b - - 7 74"));
    list.add(new PgnTestCase("09_claim_for_board_position_incorrect_side_to_move_changed.pgn", "", "", 1, 10,
        CheckmateOrStalemate.NA, 2, InsufficientMaterial.NONE, "8/8/2k5/6r1/2B5/1KR5/8/8 b - - 10 75"));

    return new PgnTestCaseList(PgnTest.REPETITION_QUIZ_ONE, list);
  }

  private static PgnTestCaseList createTestCasesRepetitionQuizTwo() {
    final List<PgnTestCase> list = new ArrayList<>();

    list.add(new PgnTestCase("01_KRvKN.pgn", "", "", 1, 12, CheckmateOrStalemate.NA, 2, InsufficientMaterial.NONE,
        "8/8/4k3/8/2n5/8/2K1R3/8 b - - 12 76"));
    list.add(new PgnTestCase("02_KRvKB_fivefold.pgn", "repPos=5: 70.Kxc4 72.Rd4 76.Rd4 78.Rd4 83.Rd4", "", 1, 26,
        CheckmateOrStalemate.NA, 5, InsufficientMaterial.BLACK_ONLY, "8/3k4/3b4/8/2KR4/8/8/8 b - - 26 83"));
    list.add(new PgnTestCase("03_KRBvKR.pgn", "repPos=5: 70.Rxg8+ 72.Rg8+ 74.Rg8+ 78.Rg8+ 82.Rg8+", "", 1, 24,
        CheckmateOrStalemate.NA, 5, InsufficientMaterial.NONE, "4k1R1/8/5r2/3K4/8/3B4/8/8 b - - 24 82"));

    return new PgnTestCaseList(PgnTest.REPETITION_QUIZ_TWO, list);
  }
}
