package com.dlb.chess.test.pgn.setup;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.annotation.NonNull;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.test.RestrictTestConstants;
import com.dlb.chess.test.common.utility.FileUtility;
import com.dlb.chess.test.model.PgnFen;
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
      case BASIC_FROM_FEN_NO_PROGRESS_BLACK -> createTestCasesBasicFromFenNoProgressBlack();
      case BASIC_FROM_FEN_NO_PROGRESS_WHITE -> createTestCasesBasicFromFenNoProgressWhite();
      case FIVEFOLD_BEYOND -> createTestCasesFivefoldBeyond();
      case SEVENTY_FIVE_BEYOND -> createTestCasesSeventyFiveBeyond();
      case LONG -> createTestCasesLong();
      case LONGEST_MATE -> createTestCasesLongestMate();
      case BASIC_INSUFFICIENT_MATERIAL_BOTH -> createTestCasesBasicInsufficientMaterialBoth();
      case BASIC_INSUFFICIENT_MATERIAL_ONLY_WHITE -> createTestCasesBasicInsufficientMaterialOnlyWhite();
      case BASIC_INSUFFICIENT_MATERIAL_ONLY_BLACK -> createTestCasesBasicInsufficientMaterialOnlyBlack();
      case BASIC_INSUFFICIENT_MATERIAL_NONE -> createTestCasesBasicInsufficientMaterialNone();
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
      case CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR_WINNABLE_FOR_FLAGGING_WITH_HELPMATE -> createTestCasesChaLichessQuickNotDepthThreeHelpmate();
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
      for (final PgnFen testCase : testCaseList.list()) {
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

  public static PgnFen findTestCase(String testPgnName) {
    for (final PgnTestCaseList testCaseList : allTestCaseListList) {
      for (final PgnFen testCase : testCaseList.list()) {
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
  // needed for their purpose - detailed feature coverage already exists in the dedicated parser, SAN, and basic
  // tests. These two curated lists keep broad diversity (piece movement, capture, en passant, promotion, castling,
  // check, checkmate, stalemate, custom starting positions) without iterating hundreds of near-identical variants.
  //
  // The lists are built at class-load time; any missing file fails fast during init with a clear message, so a
  // future rename of a referenced fixture surfaces immediately rather than during a long test run.
  // -------------------------------------------------------------------------------------------------

  private static final List<PgnTestCaseList> parserIntegrationSmokeList = buildParserIntegrationSmokeList();
  private static final List<PgnTestCaseList> exportRoundtripSmokeList = buildExportRoundtripSmokeList();

  /**
   * Smoke subset used by {@code TestStrictPgnParserAgainstLenientPgnParser} - broad enough to exercise every major
   * parser code path (piece movement, capture, en passant, promotion, castling with check/checkmate, checkmate by
   * various pieces, stalemate, custom starting position via FEN, repetition-sensitive en passant setup). About 45 files
   * in total rather than ~591.
   */
  public static List<PgnTestCaseList> getParserIntegrationSmokeList() {
    return parserIntegrationSmokeList;
  }

  /**
   * Smoke subset used by {@code TestPgnExportIdempotency} and {@code TestPgnImportAgainstExport} - tighter than the
   * parser smoke set because export tests only vary over structural shape, not chess-rule details. About 20 files
   * covering ordinary SAN movement, promotion, castling, en passant, and custom starting position.
   */
  public static List<PgnTestCaseList> getExportRoundtripSmokeList() {
    return exportRoundtripSmokeList;
  }

  private static List<PgnTestCaseList> buildParserIntegrationSmokeList() {
    final List<PgnTestCaseList> result = new ArrayList<>();
    // Small buckets - keep whole.
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
   * {@code wantedFileNames}. Fails fast with a precise message if any requested file is missing from the bucket -
   * catches fixture renames the first time the class is loaded rather than mid test-run.
   */
  private static PgnTestCaseList filterBucket(PgnTest pgnTest, String... wantedFileNames) {
    final Set<String> wanted = new TreeSet<>();
    for (int i = 0; i < wantedFileNames.length; i++) {
      wanted.add(Nulls.get(wantedFileNames, i));
    }
    final List<PgnFen> filtered = new ArrayList<>();
    final Set<String> found = new TreeSet<>();
    for (final PgnFen tc : getTestList(pgnTest).list()) {
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
      for (final PgnFen testCase : testCaseList.list()) {
        final String pgnName = testCase.pgnName();
        final boolean isNotContained = !fileNames.add(pgnName);
        if (isNotContained) {
          throw new ProgrammingMistakeException("The PGN test cases files names are not unique");
        }
      }
    }
  }

  private static PgnTestCaseList createTestCasesBasicMovingPieceWhite() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_white_moving_piece_rook.pgn", "rnbqkbnr/1ppppppp/8/p7/8/P7/RPPPPPPP/1NBQKBNR b Kkq - 1 2"));
    list.add(
        new PgnFen("02_white_moving_piece_knight.pgn", "rnbqkbnr/pppppppp/8/8/8/2N5/PPPPPPPP/R1BQKBNR b KQkq - 1 1"));
    list.add(
        new PgnFen("03_white_moving_piece_bishop.pgn", "rnbqkbnr/ppp1pppp/8/3p4/8/1P6/PBPPPPPP/RN1QKBNR b KQkq - 1 2"));
    list.add(new PgnFen("04_white_moving_piece_queen.pgn",
        "rnbqkbnr/ppp1pppp/8/3p4/3P4/3Q4/PPP1PPPP/RNB1KBNR b KQkq - 1 2"));
    list.add(
        new PgnFen("05_white_moving_piece_king.pgn", "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPPKPPP/RNBQ1BNR b kq - 1 2"));
    list.add(
        new PgnFen("06_white_moving_piece_pawn.pgn", "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"));

    return new PgnTestCaseList(PgnTest.BASIC_MOVING_PIECE_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesBasicMovingPieceBlack() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(
        new PgnFen("01_black_moving_piece_rook.pgn", "rnbqkbn1/pppppppr/7p/8/3PP3/8/PPP2PPP/RNBQKBNR w KQq - 1 3"));
    list.add(
        new PgnFen("02_black_moving_piece_knight.pgn", "rnbqkb1r/pppppppp/5n2/8/8/2N5/PPPPPPPP/R1BQKBNR w KQkq - 2 2"));
    list.add(
        new PgnFen("03_black_moving_piece_bishop.pgn", "rnbqk1nr/ppppppbp/6p1/8/3PP3/8/PPP2PPP/RNBQKBNR w KQkq - 1 3"));
    list.add(
        new PgnFen("04_black_moving_piece_queen.pgn", "rnb1kbnr/pppp1ppp/8/4p3/3PP2q/8/PPP2PPP/RNBQKBNR w KQkq - 1 3"));
    list.add(
        new PgnFen("05_black_moving_piece_king.pgn", "rnbq1bnr/ppppkppp/8/4p3/3PP3/8/PPP2PPP/RNBQKBNR w KQ - 1 3"));
    list.add(
        new PgnFen("06_black_moving_piece_pawn.pgn", "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 2"));

    return new PgnTestCaseList(PgnTest.BASIC_MOVING_PIECE_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesBasicCaptureWhite() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(
        new PgnFen("01_white_capture_rook_rook.pgn", "1nbqkbnr/1ppppppp/1R6/p7/P7/8/1PPPPPPP/1NBQKBNR b Kk - 0 4"));
    list.add(
        new PgnFen("02_white_capture_rook_knight.pgn", "r1bqkb1r/pppppppp/5n2/4R3/P7/8/1PPPPPPP/1NBQKBNR b Kkq - 0 4"));
    list.add(
        new PgnFen("03_white_capture_rook_bishop.pgn", "rnbqk1nr/pppp1ppp/8/4p3/7P/R7/PPPPPPP1/RNBQKBN1 b Qkq - 0 3"));
    list.add(
        new PgnFen("04_white_capture_rook_queen.pgn", "rnb1kbnr/pppp1ppp/8/4p2P/7R/8/PPPPPPP1/RNBQKBN1 b Qkq - 0 3"));
    list.add(
        new PgnFen("05_white_capture_rook_pawn.pgn", "r1bqkbnr/2pppppp/2n5/pR6/P7/8/1PPPPPPP/1NBQKBNR b Kkq - 0 4"));
    list.add(
        new PgnFen("06_white_capture_knight_rook.pgn", "N1bqkbnr/p1pppppp/8/1p6/3n4/8/PPPPPPPP/R1BQKBNR b KQk - 0 4"));
    list.add(new PgnFen("07_white_capture_knight_knight.pgn",
        "r1bqkb1r/pppppppp/2n2N2/8/8/8/PPPPPPPP/R1BQKBNR b KQkq - 0 3"));
    list.add(new PgnFen("08_white_capture_knight_bishop.pgn",
        "rnbqk1nr/pppp1ppp/3Np3/8/8/8/PPPPPPPP/R1BQKBNR b KQkq - 0 3"));
    list.add(
        new PgnFen("09_white_capture_knight_queen.pgn", "rnb1kbnr/pppp1ppp/4pN2/8/8/8/PPPPPPPP/R1BQKBNR b KQkq - 0 3"));
    list.add(
        new PgnFen("10_white_capture_knight_pawn.pgn", "rnbqkbnr/ppp1pppp/8/3N4/8/8/PPPPPPPP/R1BQKBNR b KQkq - 0 2"));
    list.add(new PgnFen("11_white_capture_bishop_rook.pgn",
        "r1bqkbnB/pppppp1p/2n3p1/8/8/1P6/P1PPPPPP/RN1QKBNR b KQq - 0 3"));
    list.add(new PgnFen("12_white_capture_bishop_knight.pgn",
        "r1bqkb1r/pppppppp/2n2B2/8/3P4/8/PPP1PPPP/RN1QKBNR b KQkq - 0 3"));
    list.add(new PgnFen("13_white_capture_bishop_bishop.pgn",
        "rnbqk1nr/ppppppBp/6p1/8/8/1P6/P1PPPPPP/RN1QKBNR b KQkq - 0 3"));
    list.add(new PgnFen("14_white_capture_bishop_queen.pgn",
        "rnb1kbnr/pppp1ppp/4pB2/8/8/1P6/P1PPPPPP/RN1QKBNR b KQkq - 0 3"));
    list.add(new PgnFen("15_white_capture_bishop_pawn.pgn",
        "rnbqkbnr/pp2Bppp/3p4/2p5/8/3P4/PPP1PPPP/RN1QKBNR b KQkq - 0 3"));
    list.add(
        new PgnFen("16_white_capture_queen_rook.pgn", "Qnbqkbnr/2pppppp/1p6/p7/8/4P3/PPPP1PPP/RNB1KBNR b KQk - 0 3"));
    list.add(new PgnFen("17_white_capture_queen_knight.pgn",
        "r1bqkbnr/1ppppppp/2Q5/p7/8/2P5/PP1PPPPP/RNB1KBNR b KQkq - 0 3"));
    list.add(new PgnFen("18_white_capture_queen_bishop.pgn",
        "rn1qkbnr/ppp1pppp/3p4/8/6Q1/4P3/PPPP1PPP/RNB1KBNR b KQkq - 0 3"));
    list.add(new PgnFen("19_white_capture_queen_queen.pgn",
        "rnb1kbnr/pppp1ppp/4p3/8/7Q/4P3/PPPP1PPP/RNB1KBNR b KQkq - 0 3"));
    list.add(new PgnFen("20_white_capture_queen_pawn.pgn",
        "r1bqkbnr/pppp1ppQ/2n5/4p3/8/4P3/PPPP1PPP/RNB1KBNR b KQkq - 0 3"));
    list.add(
        new PgnFen("21_white_capture_king_rook.pgn", "1nbqkbnr/1ppppppp/8/p7/4P3/3K1N2/PPPP1PPP/RNBQ1B1R b k - 0 5"));
    list.add(
        new PgnFen("22_white_capture_king_knight.pgn", "r1bqkb1r/pppppppp/5n2/8/3KP3/8/PPPP1PPP/RNBQ1BNR b kq - 0 4"));
    list.add(new PgnFen("23_white_capture_king_bishop.pgn",
        "rnbqk2r/pppp1pp1/5n1p/4p3/1K2P3/8/PPPP1PPP/RNBQ1BNR b kq - 0 5"));
    list.add(
        new PgnFen("24_white_capture_king_queen.pgn", "rnb1kbnr/pppp1ppp/8/4p3/4P3/P3K3/1PPP1PPP/RNBQ1BNR b kq - 0 4"));
    list.add(new PgnFen("25_white_capture_king_pawn.pgn", "r1bqkbnr/pppnpppp/8/8/3KP3/8/PPPP1PPP/RNBQ1BNR b kq - 0 4"));

    return new PgnTestCaseList(PgnTest.BASIC_CAPTURE_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesBasicCaptureBlack() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(
        new PgnFen("01_black_capture_rook_rook.pgn", "1nbqkbnr/1ppppppp/8/p2r4/P7/8/1PPPPPPP/1NBQKBNR w Kk - 0 5"));
    list.add(
        new PgnFen("02_black_capture_rook_knight.pgn", "1nbqkbnr/rppppppp/8/p7/8/8/PPPPPPPP/R1BQKBNR w KQk - 0 4"));
    list.add(new PgnFen("03_black_capture_rook_bishop.pgn",
        "1nbqkbnr/1ppppppp/7r/p7/3P2P1/8/PPP1PP1P/RN1QKBNR w KQk - 0 4"));
    list.add(
        new PgnFen("04_black_capture_rook_queen.pgn", "rnbqkbn1/ppppppp1/8/7r/4P2p/8/PPPP1PPP/RNB1KBNR w KQq - 0 4"));
    list.add(
        new PgnFen("05_black_capture_rook_pawn.pgn", "rnbqkbn1/ppppppp1/3r4/7p/8/8/PPP1PPPP/RNBQKBNR w KQq - 0 4"));
    list.add(
        new PgnFen("06_black_capture_knight_rook.pgn", "r1bqkbnr/pppppppp/8/8/2PPP3/8/PP1K1PPP/nNBQ1BNR w kq - 0 5"));
    list.add(new PgnFen("07_black_capture_knight_knight.pgn",
        "r1bqkbnr/pppppppp/8/8/3PP3/2N5/PPP1KPPP/R1BQ1BnR w kq - 0 5"));
    list.add(
        new PgnFen("08_black_capture_knight_bishop.pgn", "rnbqkb1r/pppppppp/8/8/PPPP4/8/4PPPP/RNBQKnNR w KQkq - 0 5"));
    list.add(new PgnFen("09_black_capture_knight_queen.pgn",
        "rnbqkb1r/pppppppp/8/2P5/1P1P4/8/P3PPPP/RNBnKBNR w KQkq - 0 5"));
    list.add(
        new PgnFen("10_black_capture_knight_pawn.pgn", "r1bqkbnr/pppppppp/8/8/3nP3/8/PPP2PPP/RNBQKBNR w KQkq - 0 3"));
    list.add(new PgnFen("11_black_capture_bishop_rook.pgn",
        "rn1qkbnr/p1pppppp/1p6/4N3/8/6P1/PPPPPP1P/RNBQKB1b w Qkq - 0 4"));
    list.add(new PgnFen("12_black_capture_bishop_knight.pgn",
        "rn1qkbnr/ppp1pppp/3p4/6N1/8/2P5/PP1PPPPP/RbBQKB1R w KQkq - 0 4"));
    list.add(new PgnFen("13_black_capture_bishop_bishop.pgn",
        "rn1qkbnr/p1pppppp/1p6/4N3/8/4P3/PPPP1PPP/RNBQKb1R w KQkq - 0 4"));
    list.add(new PgnFen("14_black_capture_bishop_queen.pgn",
        "rn1qkbnr/ppp1pppp/8/3p4/4P3/2N4P/PPPP1PP1/R1BbKBNR w KQkq - 0 4"));
    list.add(
        new PgnFen("15_black_capture_bishop_pawn.pgn", "rn1qkbnr/ppp1pppp/8/3pP3/8/5P2/PPPP2bP/RNBQKBNR w KQkq - 0 4"));
    list.add(
        new PgnFen("16_black_capture_queen_rook.pgn", "rnb1kbnr/pppp1ppp/4p3/8/8/BP6/P1PPPPPP/qN1QKBNR w Kkq - 0 4"));
    list.add(new PgnFen("17_black_capture_queen_knight.pgn",
        "rnb1kbnr/ppp1pppp/8/3p2P1/7P/2P5/PP1PPP2/RqBQKBNR w KQkq - 0 5"));
    list.add(new PgnFen("18_black_capture_queen_bishop.pgn",
        "rnb1kbnr/pppp1ppp/8/4p1q1/3P4/8/PPP1PPPP/RN1QKBNR w KQkq - 0 3"));
    list.add(new PgnFen("19_black_capture_queen_queen.pgn",
        "rnb1kbnr/pppp1ppp/8/4p3/3P1q2/8/PPP1PPPP/RNB1KBNR w KQkq - 0 4"));
    list.add(
        new PgnFen("20_black_capture_queen_pawn.pgn", "rnb1kbnr/pppp1ppp/8/3Np3/4P3/8/PPPP1qPP/R1BQKBNR w KQkq - 0 4"));
    list.add(
        new PgnFen("21_black_capture_king_rook.pgn", "rnbq1bnr/ppp1pppp/7B/3p4/3P2N1/1P1Q4/P1P1PPPP/k3KBNR w K - 0 9"));
    list.add(new PgnFen("22_black_capture_king_knight.pgn",
        "2bq1bnr/rpppp1pp/p1n2p2/3B1R1P/4P3/5P2/PPPP2P1/RNBQK1k1 w Q - 0 12"));
    list.add(new PgnFen("23_black_capture_king_bishop.pgn",
        "rnbq1bnr/ppp1pppp/8/3p4/3P4/P1P2NPQ/1P2PPBP/RNk1K2R w KQ - 0 9"));
    list.add(
        new PgnFen("24_black_capture_king_queen.pgn", "r1bq1bnr/pppppkpp/2n2p2/8/8/2P5/PP1PPPPP/RNB1KBNR w KQ - 0 4"));
    list.add(new PgnFen("25_black_capture_king_pawn.pgn",
        "rnbq1b1r/ppp1pppp/1P3n2/3p4/3P4/5N2/k1PNPPPP/1RBQKB1R w K - 0 10"));

    return new PgnTestCaseList(PgnTest.BASIC_CAPTURE_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesBasicEnPassantCaptureWhite() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_white_en_passant_capture_right_a6.pgn", "4k3/8/P7/8/8/8/8/4K3 b - - 0 101"));
    list.add(new PgnFen("02_white_en_passant_capture_right_b6.pgn", "4k3/p7/1P6/8/8/8/8/4K3 b - - 0 101"));
    list.add(new PgnFen("03_white_en_passant_capture_right_c6.pgn", "4k3/pp6/2P5/8/8/8/8/4K3 b - - 0 101"));
    list.add(new PgnFen("04_white_en_passant_capture_right_d6.pgn", "4k3/ppp5/3P4/8/8/8/8/4K3 b - - 0 101"));
    list.add(new PgnFen("05_white_en_passant_capture_right_e6.pgn", "4k3/pppp4/4P3/8/8/8/8/4K3 b - - 0 101"));
    list.add(new PgnFen("06_white_en_passant_capture_right_f6.pgn", "4k3/ppppp3/5P2/8/8/8/8/4K3 b - - 0 101"));
    list.add(new PgnFen("07_white_en_passant_capture_right_g6.pgn", "4k3/pppppp2/6P1/8/8/8/8/4K3 b - - 0 101"));
    list.add(new PgnFen("08_white_en_passant_capture_left_b6.pgn", "4k3/2pppppp/1P6/8/8/8/8/4K3 b - - 0 101"));
    list.add(new PgnFen("09_white_en_passant_capture_left_c6.pgn", "4k3/1p1ppppp/2P5/8/8/8/8/4K3 b - - 0 101"));
    list.add(new PgnFen("10_white_en_passant_capture_left_d6.pgn", "4k3/1pp1pppp/3P4/8/8/8/8/4K3 b - - 0 101"));
    list.add(new PgnFen("11_white_en_passant_capture_left_e6.pgn", "4k3/1ppp1ppp/4P3/8/8/8/8/4K3 b - - 0 101"));
    list.add(new PgnFen("12_white_en_passant_capture_left_f6.pgn", "4k3/1pppp1pp/5P2/8/8/8/8/4K3 b - - 0 101"));
    list.add(new PgnFen("13_white_en_passant_capture_left_g6.pgn", "4k3/1ppppp1p/6P1/8/8/8/8/4K3 b - - 0 101"));
    list.add(new PgnFen("14_white_en_passant_capture_left_h6.pgn", "4k3/1pppppp1/7P/8/8/8/8/4K3 b - - 0 101"));

    return new PgnTestCaseList(PgnTest.BASIC_EN_PASSANT_CAPTURE_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesBasicEnPassantCaptureBlack() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_black_en_passant_capture_left_a3.pgn", "4k3/8/8/8/2pppppp/p7/8/4K3 w - - 0 101"));
    list.add(new PgnFen("02_black_en_passant_capture_left_b3.pgn", "4k3/8/8/8/3ppppp/1p6/8/4K3 w - - 0 101"));
    list.add(new PgnFen("03_black_en_passant_capture_left_c3.pgn", "4k3/8/8/8/4pppp/2p5/8/4K3 w - - 0 101"));
    list.add(new PgnFen("04_black_en_passant_capture_left_d3.pgn", "4k3/8/8/8/5ppp/3p4/8/4K3 w - - 0 101"));
    list.add(new PgnFen("05_black_en_passant_capture_left_e3.pgn", "4k3/8/8/8/6pp/4p3/8/4K3 w - - 0 101"));
    list.add(new PgnFen("06_black_en_passant_capture_left_f3.pgn", "4k3/8/8/8/7p/5p2/8/4K3 w - - 0 101"));
    list.add(new PgnFen("07_black_en_passant_capture_left_g3.pgn", "4k3/8/8/8/8/6p1/8/4K3 w - - 0 101"));
    list.add(new PgnFen("08_black_en_passant_capture_right_b3.pgn", "4k3/8/8/8/8/1p6/2PPPPPP/4K3 w - - 0 101"));
    list.add(new PgnFen("09_black_en_passant_capture_right_c3.pgn", "4k3/8/8/8/8/2p5/1P1PPPPP/4K3 w - - 0 101"));
    list.add(new PgnFen("10_black_en_passant_capture_right_d3.pgn", "4k3/8/8/8/8/3p4/1PP1PPPP/4K3 w - - 0 101"));
    list.add(new PgnFen("11_black_en_passant_capture_right_e3.pgn", "4k3/8/8/8/8/4p3/1PPP1PPP/4K3 w - - 0 101"));
    list.add(new PgnFen("12_black_en_passant_capture_right_f3.pgn", "4k3/8/8/8/8/5p2/1PPPP1PP/4K3 w - - 0 101"));
    list.add(new PgnFen("13_black_en_passant_capture_right_g3.pgn", "4k3/8/8/8/8/6p1/1PPPPP1P/4K3 w - - 0 101"));
    list.add(new PgnFen("14_black_en_passant_capture_right_h3.pgn", "4k3/8/8/8/8/7p/1PPPPPP1/4K3 w - - 0 101"));

    return new PgnTestCaseList(PgnTest.BASIC_EN_PASSANT_CAPTURE_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesBasicPromotionPieceWhite() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_white_promotion_piece_capture_no_rook.pgn",
        "rRbqkbnr/p1pppppp/n7/7Q/2P5/8/P2PPPPP/RNB1KBNR b KQkq - 0 9"));
    list.add(new PgnFen("02_white_promotion_piece_capture_no_knight.pgn",
        "rNbqkbnr/p1pppppp/n7/7Q/2P5/8/P2PPPPP/RNB1KBNR b KQkq - 0 9"));
    list.add(new PgnFen("03_white_promotion_piece_capture_no_bishop.pgn",
        "rBbqkbnr/p1pppppp/n7/7Q/2P5/8/P2PPPPP/RNB1KBNR b KQkq - 0 9"));
    list.add(new PgnFen("04_white_promotion_piece_capture_no_queen.pgn",
        "rQbqkbnr/p1pppppp/n7/7Q/2P5/8/P2PPPPP/RNB1KBNR b KQkq - 0 9"));
    list.add(new PgnFen("05_white_promotion_piece_capture_yes_rook.pgn",
        "R2qkbnr/2pppppp/n7/8/8/8/1PPPPPPP/RNBQKBNR b KQk - 0 5"));
    list.add(new PgnFen("06_white_promotion_piece_capture_yes_knight.pgn",
        "N2qkbnr/2pppppp/n7/8/8/8/1PPPPPPP/RNBQKBNR b KQk - 0 5"));
    list.add(new PgnFen("07_white_promotion_piece_capture_yes_bishop.pgn",
        "B2qkbnr/2pppppp/n7/8/8/8/1PPPPPPP/RNBQKBNR b KQk - 0 5"));
    list.add(new PgnFen("08_white_promotion_piece_capture_yes_queen.pgn",
        "Q2qkbnr/2pppppp/n7/8/8/8/1PPPPPPP/RNBQKBNR b KQk - 0 5"));

    return new PgnTestCaseList(PgnTest.BASIC_PROMOTION_PIECE_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesBasicPromotionPieceBlack() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_black_promotion_piece_capture_no_rook.pgn",
        "rnb1kbnr/pppp1pp1/8/4p3/q7/7R/PPPPPPP1/RNBQKBNr w Qkq - 0 9"));
    list.add(new PgnFen("02_black_promotion_piece_capture_no_knight.pgn",
        "rnb1kbnr/pppp1pp1/8/4p3/q7/7R/PPPPPPP1/RNBQKBNn w Qkq - 0 9"));
    list.add(new PgnFen("03_black_promotion_piece_capture_no_bishop.pgn",
        "rnb1kbnr/pppp1pp1/8/4p3/q7/7R/PPPPPPP1/RNBQKBNb w Qkq - 0 9"));
    list.add(new PgnFen("04_black_promotion_piece_capture_no_queen.pgn",
        "rnb1kbnr/pppp1pp1/8/4p3/q7/7R/PPPPPPP1/RNBQKBNq w Qkq - 0 9"));
    list.add(new PgnFen("05_black_promotion_piece_capture_yes_rook.pgn",
        "rnbqkbnr/pppp1ppp/8/8/8/5NP1/P3PPBP/RNrQK2R w KQkq - 0 6"));
    list.add(new PgnFen("06_black_promotion_piece_capture_yes_knight.pgn",
        "rnbqkbnr/pppp1ppp/8/8/8/5NP1/P3PPBP/RNnQK2R w KQkq - 0 6"));
    list.add(new PgnFen("07_black_promotion_piece_capture_yes_bishop.pgn",
        "rnbqkbnr/pppp1ppp/8/8/8/5NP1/P3PPBP/RNbQK2R w KQkq - 0 6"));
    list.add(new PgnFen("08_black_promotion_piece_capture_yes_queen.pgn",
        "rnbqkbnr/pppp1ppp/8/8/8/5NP1/P3PPBP/RNqQK2R w KQkq - 0 6"));

    return new PgnTestCaseList(PgnTest.BASIC_PROMOTION_PIECE_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesBasicPromotionSquareWhite() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_white_promotion_square_straight_a8.pgn", "Q7/1PPPPPPP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnFen("02_white_promotion_square_straight_b8.pgn", "1Q6/P1PPPPPP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnFen("03_white_promotion_square_straight_c8.pgn", "2Q5/PP1PPPPP/2B5/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnFen("04_white_promotion_square_straight_d8.pgn", "3Q4/PPP1PPPP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnFen("05_white_promotion_square_straight_e8.pgn", "4Q3/PPPP1PPP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnFen("06_white_promotion_square_straight_f8.pgn", "5Q2/PPPPP1PP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnFen("07_white_promotion_square_straight_g8.pgn", "6Q1/PPPPPP1P/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnFen("08_white_promotion_square_straight_h8.pgn", "7Q/PPPPPPP1/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnFen("09_white_promotion_square_right_a8.pgn", "Qrrrbrrr/P1PPPPPP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnFen("10_white_promotion_square_right_b8.pgn", "rQrrbrrr/PP1PPPPP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnFen("11_white_promotion_square_right_c8.pgn", "rrQrbrrr/PPP1PPPP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnFen("12_white_promotion_square_right_d8.pgn", "rrrQbrrr/PPPP1PPP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnFen("13_white_promotion_square_right_e8.pgn", "rrrrQrrr/PPPPP1PP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnFen("14_white_promotion_square_right_f8.pgn", "rrrrbQrr/PPPPPP1P/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnFen("15_white_promotion_square_right_g8.pgn", "rrrrbrQr/PPPPPPP1/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnFen("16_white_promotion_square_left_b8.pgn", "nQnnnnnn/1PPPPPPP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnFen("17_white_promotion_square_left_c8.pgn", "nnQnnnnn/P1PPPPPP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnFen("18_white_promotion_square_left_d8.pgn", "nnnQnnnn/PP1PPPPP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnFen("19_white_promotion_square_left_e8.pgn", "nnnnQnnn/PPP1PPPP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnFen("20_white_promotion_square_left_f8.pgn", "nnnnnQnn/PPPP1PPP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnFen("21_white_promotion_square_left_g8.pgn", "nnnnnnQn/PPPPP1PP/8/8/8/8/8/2k1K3 b - - 0 100"));
    list.add(new PgnFen("22_white_promotion_square_left_h8.pgn", "nnnnnnnQ/PPPPPP1P/8/8/8/8/8/2k1K3 b - - 0 100"));

    return new PgnTestCaseList(PgnTest.BASIC_PROMOTION_SQUARE_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesBasicPromotionSquareBlack() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_black_promotion_square_straight_a1.pgn", "3k1K2/8/8/8/8/8/1ppppppp/q7 w - - 0 101"));
    list.add(new PgnFen("02_black_promotion_square_straight_b1.pgn", "3k1K2/8/8/8/8/8/p1pppppp/1q6 w - - 0 101"));
    list.add(new PgnFen("03_black_promotion_square_straight_c1.pgn", "3k1K2/8/8/8/8/8/pp1ppppp/2q5 w - - 0 101"));
    list.add(new PgnFen("04_black_promotion_square_straight_d1.pgn", "3k1K2/8/8/8/8/8/ppp1pppp/3q4 w - - 0 101"));
    list.add(new PgnFen("05_black_promotion_square_straight_e1.pgn", "3k1K2/8/8/8/8/8/pppp1ppp/4q3 w - - 0 101"));
    list.add(new PgnFen("06_black_promotion_square_straight_f1.pgn", "3k1K2/8/8/8/8/5b2/ppppp1pp/5q2 w - - 0 101"));
    list.add(new PgnFen("07_black_promotion_square_straight_g1.pgn", "3k1K2/8/8/8/8/8/pppppp1p/6q1 w - - 0 101"));
    list.add(new PgnFen("08_black_promotion_square_straight_h1.pgn", "3k1K2/8/8/8/8/8/ppppppp1/7q w - - 0 101"));
    list.add(new PgnFen("09_black_promotion_square_left_a1.pgn", "3k1K2/8/8/8/8/8/p1pppppp/qQQNQQQQ w - - 0 101"));
    list.add(new PgnFen("10_black_promotion_square_left_b1.pgn", "3k1K2/8/8/8/8/8/pp1ppppp/QqQNQQQQ w - - 0 101"));
    list.add(new PgnFen("11_black_promotion_square_left_c1.pgn", "3k1K2/8/8/8/8/8/ppp1pppp/QQqNQQQQ w - - 0 101"));
    list.add(new PgnFen("12_black_promotion_square_left_d1.pgn", "3k1K2/8/8/8/8/8/pppp1ppp/QQQqQQQQ w - - 0 101"));
    list.add(new PgnFen("13_black_promotion_square_left_e1.pgn", "3k1K2/8/8/8/8/8/ppppp1pp/QQQNqQQQ w - - 0 101"));
    list.add(new PgnFen("14_black_promotion_square_left_f1.pgn", "3k1K2/8/8/8/8/8/pppppp1p/QQQNQqQQ w - - 0 101"));
    list.add(new PgnFen("15_black_promotion_square_left_g1.pgn", "3k1K2/8/8/8/8/8/ppppppp1/QQQNQQqQ w - - 0 101"));
    list.add(new PgnFen("16_black_promotion_square_right_b1.pgn", "3k1K2/8/8/8/8/8/1ppppppp/BqBNBBBB w - - 0 101"));
    list.add(new PgnFen("17_black_promotion_square_right_c1.pgn", "3k1K2/8/8/8/8/8/p1pppppp/BBqNBBBB w - - 0 101"));
    list.add(new PgnFen("18_black_promotion_square_right_d1.pgn", "3k1K2/8/8/8/8/8/pp1ppppp/BBBqBBBB w - - 0 101"));
    list.add(new PgnFen("19_black_promotion_square_right_e1.pgn", "3k1K2/8/8/8/8/8/ppp1pppp/BBBNqBBB w - - 0 101"));
    list.add(new PgnFen("20_black_promotion_square_right_f1.pgn", "3k1K2/8/8/8/8/8/pppp1ppp/BBBNBqBB w - - 0 101"));
    list.add(new PgnFen("21_black_promotion_square_right_g1.pgn", "3k1K2/8/8/8/8/8/ppppp1pp/BBBNBBqB w - - 0 101"));
    list.add(new PgnFen("22_black_promotion_square_right_h1.pgn", "3k1K2/8/8/8/8/8/pppppp1p/BBBNBBBq w - - 0 101"));

    return new PgnTestCaseList(PgnTest.BASIC_PROMOTION_SQUARE_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesBasicCheckWhite() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_white_check_rook_direct_adjacent.pgn",
        "rnbq1bnr/pppp1ppp/3Rk3/4p3/P7/8/1PPPPPPP/1NBQKBNR b K - 5 4"));
    list.add(new PgnFen("02_white_check_rook_direct_range.pgn",
        "rnbq1bnr/pppp1ppp/1R1k4/4p3/P7/8/1PPPPPPP/1NBQKBNR b K - 5 4"));
    list.add(new PgnFen("03_white_check_rook_discover.pgn",
        "rnbqkb1r/ppp1pppp/3pn3/P2R4/Q7/2P5/1P1PPPPP/1NB1KBNR b Kkq - 10 8"));
    list.add(
        new PgnFen("04_white_check_knight_direct.pgn", "rnbqkb1r/pppppppp/5N2/8/4n3/8/PPPPPPPP/R1BQKBNR b KQkq - 5 3"));
    list.add(new PgnFen("05_white_check_knight_discover_orthogonal.pgn",
        "rnbq1bnr/pppp2pp/PN6/R4k2/4pp2/5N2/1PPPPPPP/2BQKB1R b K - 5 8"));
    list.add(new PgnFen("06_white_check_knight_discover_diagonal.pgn",
        "r1bqkb1r/ppp1pppp/3p1n2/8/Q2n4/N1P5/PP1PPPPP/R1B1KBNR b KQkq - 7 5"));
    list.add(new PgnFen("07_white_check_bishop_direct_adjacent.pgn",
        "rnbqkb1r/pppppBpp/5p1n/8/8/4P3/PPPP1PPP/RNBQK1NR b KQkq - 3 3"));
    list.add(new PgnFen("08_white_check_bishop_direct_range.pgn",
        "rnbqkbnr/ppp1pppp/3p4/1B6/8/4P3/PPPP1PPP/RNBQK1NR b KQkq - 1 2"));
    list.add(new PgnFen("09_white_check_bishop_discover.pgn",
        "r1bq1bnr/pppp1ppp/P1n5/R3k3/2B1p3/4P3/1PPP1PPP/1NBQK1NR b K - 4 7"));
    list.add(new PgnFen("10_white_check_queen_direct_orthogonal_adjacent.pgn",
        "r2qkb1r/pbppQppp/1pn2P2/8/8/8/PPP1PPPP/RNB1KBNR b KQkq - 3 6"));
    list.add(new PgnFen("11_white_check_queen_direct_orthogonal_range.pgn",
        "r1bqkb1r/p1pp1ppp/1pn2P2/8/8/4Q3/PPP1PPPP/RNB1KBNR b KQkq - 1 5"));
    list.add(new PgnFen("12_white_check_queen_direct_diagonal_adjacent.pgn",
        "r1bqkbnr/pppppQpp/2n2p2/8/8/2P5/PP1PPPPP/RNB1KBNR b KQkq - 3 3"));
    list.add(new PgnFen("13_white_check_queen_direct_diagonal_range.pgn",
        "rnbqkbnr/ppppp1pp/5p2/7Q/8/4P3/PPPP1PPP/RNB1KBNR b KQkq - 1 2"));
    list.add(new PgnFen("14_white_check_king_discover_orthogonal.pgn",
        "rnbqkb1r/p1pp2pp/1p3Pn1/8/8/5K2/PPP2PPP/RNB1QBNR b kq - 3 8"));
    list.add(new PgnFen("15_white_check_king_discover_diagonal.pgn",
        "r1bqkb1r/ppp1pppp/3p4/4n2n/QK6/2P5/PP1PPPPP/RNB2BNR b kq - 13 8"));

    return new PgnTestCaseList(PgnTest.BASIC_CHECK_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesBasicCheckBlack() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_black_check_rook_direct_adjacent.pgn",
        "1nbqkbnr/1ppppppp/8/p7/2rKP3/8/PPPP1PPP/RNBQ1BNR w k - 6 5"));
    list.add(new PgnFen("02_black_check_rook_direct_range.pgn",
        "1nbqkbnr/1ppppppp/8/8/2N2P2/r4K2/PPPPP1PP/R1BQ1BNR w k - 3 6"));
    list.add(new PgnFen("03_black_check_rook_discover.pgn",
        "2b1kbnr/pr1ppppp/2n5/q1p3B1/4N3/1p1P1NPP/PPP1PPB1/1R1QK2R w Kk - 1 10"));
    list.add(
        new PgnFen("04_black_check_knight_direct.pgn", "r1bqkbnr/pppppppp/1N6/8/8/5n2/PPPPPPPP/R1BQKBNR w KQkq - 6 4"));
    list.add(new PgnFen("05_black_check_knight_discover_orthogonal.pgn",
        "r1b1kbnr/ppp1pppp/4q1n1/3P4/8/1PN3N1/P1PP1PPP/R1BQKB1R w KQkq - 1 7"));
    list.add(new PgnFen("06_black_check_knight_discover_diagonal.pgn",
        "rnb1kb1r/pp1ppppp/1qp1n3/8/8/2N2P1N/PPPPPKPP/R1BQ1B1R w kq - 4 7"));
    list.add(new PgnFen("07_black_check_bishop_direct_adjacent.pgn",
        "rnbqk1nr/pppp1ppp/4p3/8/8/1PN2P2/P1PPPbPP/R1BQKBNR w KQkq - 1 4"));
    list.add(new PgnFen("08_black_check_bishop_direct_range.pgn",
        "rn1qkbnr/pbpppppp/1p6/8/8/6PB/PPPPPPKP/RNBQ2NR w kq - 6 5"));
    list.add(new PgnFen("09_black_check_bishop_discover.pgn",
        "rn2kbnr/ppp1qppp/8/8/6b1/2N2N2/PPPP1PPP/R1BQKB1R w KQkq - 4 6"));
    list.add(new PgnFen("10_black_check_queen_direct_orthogonal_adjacent.pgn",
        "rnb1kbnr/ppp1pppp/3p4/8/3N4/2N1P3/PPPPqPPP/R1BQKB1R w KQkq - 6 5"));
    list.add(new PgnFen("11_black_check_queen_direct_orthogonal_range.pgn",
        "rnb1kbnr/ppp1pppp/8/3Pq3/8/2N5/PPPP1PPP/R1BQKBNR w KQkq - 3 4"));
    list.add(new PgnFen("12_black_check_queen_direct_diagonal_adjacent.pgn",
        "rnb1kbnr/pppp1ppp/4p3/8/8/2NP1N2/PPPqPPPP/R1BQKB1R w KQkq - 4 4"));
    list.add(new PgnFen("13_black_check_queen_direct_diagonal_range.pgn",
        "rnb1kbnr/pp1ppppp/1qp5/8/8/5P2/PPPPPKPP/RNBQ1BNR w kq - 2 3"));
    list.add(new PgnFen("14_black_check_king_discover_orthogonal.pgn",
        "rn2qbnr/pppb1ppp/3k4/8/8/2NB1N2/PPPP1PPP/R1BQK2R w KQ - 8 8"));
    list.add(new PgnFen("15_black_check_king_discover_diagonal.pgn",
        "rnbq2nr/pppp1p1p/4p1pb/8/3N2k1/1PNP4/PBPKPPPP/R2Q1B1R w - - 7 8"));

    return new PgnTestCaseList(PgnTest.BASIC_CHECK_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesBasicDoubleCheckWhite() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_white_double_check_rook.pgn", "5k2/1n6/8/8/5R2/B7/8/4K3 b - - 3 101"));
    list.add(new PgnFen("02_white_double_check_knight_orthogonal.pgn", "3n1k2/3N4/8/8/8/8/5R2/4K3 b - - 3 101"));
    list.add(new PgnFen("03_white_double_check_knight_diagonal.pgn", "2n2k2/8/4N3/8/8/B7/8/4K3 b - - 3 101"));
    list.add(new PgnFen("04_white_double_check_bishop.pgn", "5k2/8/3B4/3n4/8/8/5R2/4K3 b - - 3 101"));

    return new PgnTestCaseList(PgnTest.BASIC_DOUBLE_CHECK_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesBasicDoubleCheckBlack() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_black_double_check_rook.pgn", "4k3/6R1/8/7b/8/8/4Kr2/8 w - - 3 102"));
    list.add(new PgnFen("02_black_double_check_knight_orthogonal.pgn", "6k1/3r4/8/8/5n2/3K4/5B2/8 w - - 3 102"));
    list.add(new PgnFen("03_black_double_check_knight_diagonal.pgn", "6k1/7q/2B5/8/8/4n3/2K5/8 w - - 3 102"));
    list.add(new PgnFen("04_black_double_check_bishop.pgn", "3Q4/1K5r/8/3b4/6k1/8/8/8 w - - 1 102"));

    return new PgnTestCaseList(PgnTest.BASIC_DOUBLE_CHECK_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesBasicCheckmateWhite() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_white_checkmate_rook_direct_adjacent.pgn", "2kR4/R7/8/8/8/8/8/3RK3 b - - 3 101"));
    list.add(new PgnFen("02_white_checkmate_rook_direct_range.pgn", "1k5R/6R1/8/8/8/8/8/3RK3 b - - 4 101"));
    list.add(new PgnFen("03_white_checkmate_rook_discover.pgn", "2k5/R6R/4B3/7p/8/8/8/1R1RRK2 b - - 1 102"));
    list.add(new PgnFen("04_white_checkmate_knight_direct.pgn", "3k4/1Q6/2N5/7p/8/8/8/2RKR3 b - - 1 101"));
    list.add(
        new PgnFen("05_white_checkmate_knight_discover_orthogonal.pgn", "3k4/8/5N2/4R3/8/6b1/3R4/2RK4 b - - 5 102"));
    list.add(
        new PgnFen("06_white_checkmate_knight_discover_diagonal.pgn", "3k2N1/1R6/8/4R1B1/3b4/8/8/2RK4 b - - 5 102"));
    list.add(new PgnFen("07_white_checkmate_bishop_direct_adjacent.pgn", "5B2/8/R4Q2/1k6/2B5/2K5/8/8 b - - 5 102"));
    list.add(new PgnFen("08_white_checkmate_bishop_direct_range.pgn", "5B2/8/R4Q2/3k4/8/2K2B2/8/8 b - - 5 102"));
    list.add(new PgnFen("09_white_checkmate_bishop_discover.pgn", "8/2B5/1R6/2k2Q2/7p/2K5/8/8 b - - 1 102"));
    list.add(
        new PgnFen("10_white_checkmate_queen_direct_orthogonal_adjacent.pgn", "8/8/1RQ5/2k4p/5B2/2K5/8/8 b - - 1 101"));
    list.add(new PgnFen("11_white_checkmate_queen_direct_orthogonal_range.pgn", "8/k7/8/Q1K5/1R6/8/8/8 b - - 3 101"));
    list.add(new PgnFen("12_white_checkmate_queen_direct_diagonal_adjacent.pgn", "k7/1QK5/8/7R/1R6/8/8/8 b - - 5 102"));
    list.add(
        new PgnFen("13_white_checkmate_queen_direct_diagonal_range.pgn", "8/2Q5/4k1K1/1R6/8/1Q6/8/4q3 b - - 1 101"));
    list.add(new PgnFen("14_white_checkmate_king_discover_orthogonal.pgn", "Q7/4k2R/R6K/8/8/8/8/8 b - - 3 101"));
    list.add(new PgnFen("15_white_checkmate_king_discover_diagonal.pgn", "3k4/1K6/8/BN4p1/8/7B/8/4R3 b - - 1 102"));

    return new PgnTestCaseList(PgnTest.BASIC_CHECKMATE_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesBasicCheckmateBlack() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_black_checkmate_rook_direct_adjacent.pgn", "2Kr4/7q/8/8/8/8/8/2kr4 w - - 3 102"));
    list.add(new PgnFen("02_black_checkmate_rook_direct_range.pgn", "4kr2/3q4/8/8/7b/8/6rN/4K3 w - - 5 103"));
    list.add(new PgnFen("03_black_checkmate_rook_discover.pgn", "3K4/1r6/3k4/b3r3/5N2/8/8/2r5 w - - 5 103"));
    list.add(new PgnFen("04_black_checkmate_knight_direct.pgn", "Q1r5/8/7r/3K4/1r3n2/8/8/3kr3 w - - 1 102"));
    list.add(new PgnFen("05_black_checkmate_knight_discover_orthogonal.pgn", "4k3/8/5n1r/8/8/3N4/5q2/7K w - - 5 103"));
    list.add(new PgnFen("06_black_checkmate_knight_discover_diagonal.pgn", "Q4q2/3rk3/n7/b7/8/8/7r/4K3 w - - 1 103"));
    list.add(new PgnFen("07_black_checkmate_bishop_direct_adjacent.pgn", "4k3/P7/8/5r1r/8/8/q6b/6KN w - - 1 102"));
    list.add(new PgnFen("08_black_checkmate_bishop_direct_range.pgn", "Q7/8/8/2b1kr1r/8/8/q7/6K1 w - - 1 102"));
    list.add(new PgnFen("09_black_checkmate_bishop_discover.pgn", "6q1/P7/4k3/1B2br1r/8/8/8/6K1 w - - 5 103"));
    list.add(
        new PgnFen("10_black_checkmate_queen_direct_orthogonal_adjacent.pgn", "1Q6/8/4k3/7r/8/8/r5q1/6K1 w - - 1 102"));
    list.add(
        new PgnFen("11_black_checkmate_queen_direct_orthogonal_range.pgn", "1Q6/8/4k3/4nrqr/8/8/8/6K1 w - - 1 102"));
    list.add(
        new PgnFen("12_black_checkmate_queen_direct_diagonal_adjacent.pgn", "Q7/8/4k3/4nr1r/8/8/5q2/6K1 w - - 1 102"));
    list.add(
        new PgnFen("13_black_checkmate_queen_direct_diagonal_range.pgn", "Q7/8/1q2k3/3bnr1r/8/8/8/6K1 w - - 1 102"));
    list.add(new PgnFen("14_black_checkmate_king_discover_orthogonal.pgn", "5q2/8/8/8/3kr3/3q4/N7/4K3 w - - 5 103"));
    list.add(new PgnFen("15_black_checkmate_king_discover_diagonal.pgn", "2K5/8/6N1/bnnn2k1/6b1/8/7b/8 w - - 5 103"));

    return new PgnTestCaseList(PgnTest.BASIC_CHECKMATE_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesBasicCheckmateVariousWhite() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(new PgnFen("01_checkmate_by_white_capture_yes.pgn",
        "1r1qkbnr/pbp2Qp1/2pp3p/4p1N1/4P3/8/PPPP1PPP/RNB1K2R b KQk - 0 8"));
    list.add(new PgnFen("02_checkmate_by_white_capture_no.pgn",
        "r2qkbnr/pbpppQ1p/1pn3p1/4Np2/4P3/8/PPPP1PPP/RNB1KB1R b KQkq - 1 6"));
    list.add(new PgnFen("03_checkmate_by_white_discover_check.pgn",
        "r1b2bnr/1ppp1ppp/p2Q4/4p3/4P2k/3P4/PPPBBPP1/RN2K1NR b Q - 19 18"));
    list.add(new PgnFen("04_checkmate_fools_mate_by_white.pgn",
        "rnbqkbnr/ppppp2p/5p2/6pQ/4P3/2N5/PPPP1PPP/R1B1KBNR b KQkq - 1 3"));
    list.add(new PgnFen("05_checkmate_scholars_mate_by_white.pgn",
        "r1bqk1nr/pppp1Qpp/2n5/2b1p3/2B1P3/8/PPPP1PPP/RNB1K1NR b KQkq - 0 4"));
    return new PgnTestCaseList(PgnTest.BASIC_CHECKMATE_VARIOUS_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesBasicCheckmateVariousBlack() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(new PgnFen("01_checkmate_by_black_capture_yes.pgn",
        "rnb1kbnr/1p1p1pp1/p1B4p/2P5/8/P1N1q1PR/1PP1P3/R1BK2q1 w kq - 0 14"));
    list.add(new PgnFen("02_checkmate_by_black_capture_no.pgn",
        "r7/ppp3pp/2N1k3/1B6/3np3/1b3n2/1P1b1PPP/RN1K3R w - - 7 21"));
    list.add(new PgnFen("03_checkmate_by_black_discover_check.pgn",
        "1nbq4/1p1p2pk/7n/1N2pp1P/1p2Pb2/7N/r7/r2KQ2R w - - 0 21"));
    list.add(new PgnFen("04_checkmate_fools_mate_by_black.pgn",
        "rnb1kbnr/pppp1ppp/8/4p3/6Pq/5P2/PPPPP2P/RNBQKBNR w KQkq - 1 3"));
    list.add(new PgnFen("05_checkmate_scholars_mate_by_black.pgn",
        "rnb1k1nr/pppp1ppp/8/2b1p3/2B1P3/2NP4/PPP2qPP/R1BQK1NR w KQkq - 0 5"));
    return new PgnTestCaseList(PgnTest.BASIC_CHECKMATE_VARIOUS_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesBasicCheckmateDoubleCheckWhite() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_white_double_check_checkmate_rook.pgn", "8/2rpp3/1R2k3/7R/8/8/B7/B3K3 b - - 3 101"));
    list.add(new PgnFen("02_white_double_check_checkmate_knight_orthogonal.pgn",
        "8/4p3/3pk1n1/2N4R/8/8/4R3/4KQ2 b - - 3 101"));
    list.add(new PgnFen("03_white_double_check_checkmate_knight_diagonal.pgn",
        "8/2pp1p2/2n1pk2/3N3R/8/8/1B4R1/4K3 b - - 3 101"));
    list.add(new PgnFen("04_white_double_check_checkmate_bishop.pgn", "8/3p1pp1/4pkn1/R7/7B/8/8/1B2KR2 b - - 3 101"));

    return new PgnTestCaseList(PgnTest.BASIC_CHECKMATE_DOUBLE_CHECK_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesBasicCheckmateDoubleCheckBlack() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_black_double_check_checkmate_rook.pgn", "4k3/8/8/b7/8/8/R6r/1r2K3 w - - 3 102"));
    list.add(
        new PgnFen("02_black_double_check_checkmate_knight_orthogonal.pgn", "3k4/8/4r3/8/8/5n2/R7/3NKBb1 w - - 3 102"));
    list.add(
        new PgnFen("03_black_double_check_checkmate_knight_diagonal.pgn", "b4k2/6b1/3n4/8/4KP2/7r/Q7/8 w - - 3 102"));
    list.add(new PgnFen("04_black_double_check_checkmate_bishop.pgn", "1r2k3/8/8/2r5/4n3/8/3b4/2KRN3 w - - 3 102"));

    return new PgnTestCaseList(PgnTest.BASIC_CHECKMATE_DOUBLE_CHECK_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesBasicStalemate() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(new PgnFen("01_stalemate_last_move_black.pgn", "r4knr/p2b1p2/3p4/p1pPp2p/P1PnP1pP/8/2q5/K7 w - - 0 26"));
    list.add(
        new PgnFen("02_stalemate_last_move_white.pgn", "8/5Q2/1p3p1k/pP1p1Pp1/P1pPp1Pp/2P1P2P/8/RNB1KBNR b KQ - 0 24"));
    return new PgnTestCaseList(PgnTest.BASIC_STALEMATE, list);
  }

  private static PgnTestCaseList createTestCasesBasicInsufficientMaterialBoth() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("insufficient_material_KBw_K.pgn", "8/8/3K2k1/8/8/8/8/5B2 b - - 0 33"));
    list.add(new PgnFen("insufficient_material_KBb_K.pgn", "8/8/8/4k3/8/4K3/8/2B5 w - - 0 35"));

    list.add(new PgnFen("insufficient_material_KBw_KBw.pgn", "5K2/8/8/1B6/8/k7/6b1/8 w - - 0 39"));
    list.add(new PgnFen("insufficient_material_KBb_KBb.pgn", "8/8/8/4k3/5b2/3K4/8/2B5 w - - 0 33"));

    list.add(new PgnFen("insufficient_material_KN_K.pgn", "4k3/8/8/2K5/8/5N2/8/8 w - - 0 35"));

    // K vs K reached on the last halfmove via captures - game ends at dead position, no moves past.
    list.add(new PgnFen("insufficient_material_K_K.pgn", "2K5/8/8/8/8/8/4k3/8 b - - 0 38"));

    list.add(new PgnFen("insufficient_material_K_KBw.pgn", "2b5/8/2k5/8/4K3/8/8/8 b - - 0 34"));
    list.add(new PgnFen("insufficient_material_K_KBb.pgn", "8/8/8/2b5/2K5/8/4k3/8 w - - 0 34"));

    list.add(new PgnFen("insufficient_material_K_KN.pgn", "4k3/8/8/1K2n3/8/8/8/8 b - - 0 36"));

    // more than two bishops one side same square and only kings - is insufficient
    // material
    list.add(new PgnFen("insufficient_material_KBwBw_K.pgn", "2B5/3B4/8/6K1/8/1k6/8/8 w - - 0 32"));
    list.add(new PgnFen("insufficient_material_KBbBb_K.pgn", "5B2/8/2k5/8/8/1K6/8/2B5 b - - 0 36"));
    list.add(new PgnFen("insufficient_material_K_KBwBw.pgn", "3k4/1b6/2b5/8/8/8/3K4/8 w - - 0 27"));
    list.add(new PgnFen("insufficient_material_K_KBbBb.pgn", "5b2/3k4/8/8/5K2/2b5/8/8 b - - 0 25"));
    return new PgnTestCaseList(PgnTest.BASIC_INSUFFICIENT_MATERIAL_BOTH, list);
  }

  private static PgnTestCaseList createTestCasesBasicInsufficientMaterialOnlyWhite() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("insufficient_material_K_KBwBb.pgn", "4k3/8/8/4K3/8/b7/4b3/8 b - - 0 34"));

    list.add(new PgnFen("insufficient_material_K_KBwN.pgn", "4k3/8/8/2K1n3/8/8/4b3/8 b - - 0 35"));
    list.add(new PgnFen("insufficient_material_K_KBbN.pgn", "1n2k3/8/8/8/8/b4K2/8/8 b - - 0 33"));

    list.add(new PgnFen("insufficient_material_K_KNN.pgn", "1n2k3/8/3n4/8/8/8/3K4/8 b - - 0 35"));
    list.add(new PgnFen("insufficient_material_KN_KQ.pgn", "4k3/8/8/8/8/5K2/3q4/1N6 w - - 0 24"));

    // KBw against king and single piece
    list.add(new PgnFen("insufficient_material_KBw_KR.pgn", "r7/8/3K4/8/8/2k5/8/5B2 w - - 0 28"));
    list.add(new PgnFen("insufficient_material_KBw_KQ.pgn", "3K4/8/8/4q3/k7/8/8/5B2 b - - 0 30"));

    // KBb against king and single piece
    list.add(new PgnFen("insufficient_material_KBb_KR.pgn", "8/8/8/r7/k7/8/1K6/2B5 b - - 0 25"));
    list.add(new PgnFen("insufficient_material_KBb_KQ.pgn", "8/8/8/2q5/8/2BK4/k7/8 b - - 0 28"));

    list.add(new PgnFen("insufficient_material_KBw_KBwQ.pgn", "8/1b2q3/8/k4K2/8/8/8/5B2 w - - 0 25"));
    list.add(new PgnFen("insufficient_material_KBw_KBwR.pgn", "r1b5/8/8/8/1k6/8/3K4/5B2 b - - 0 24"));
    list.add(new PgnFen("insufficient_material_KBw_KQR.pgn", "r7/3B4/8/8/1k3q2/8/8/1K6 b - - 0 24"));
    list.add(new PgnFen("insufficient_material_KBw_KBwQR.pgn", "r1b5/8/8/8/1k3q2/8/8/1K3B2 w - - 0 23"));

    list.add(new PgnFen("insufficient_material_KBb_KBbQ.pgn", "4kb2/6q1/8/8/8/8/3K4/2B5 w - - 0 28"));
    list.add(new PgnFen("insufficient_material_KBb_KBbR.pgn", "4kb2/8/8/8/4r3/8/3K4/2B5 w - - 0 28"));
    list.add(new PgnFen("insufficient_material_KBb_KQR.pgn", "8/3k3r/8/8/5q2/8/3K4/2B5 w - - 0 28"));
    list.add(new PgnFen("insufficient_material_KBb_KBbQR.pgn", "4kb2/7r/8/3q4/8/8/3K4/2B5 w - - 0 27"));

    list.add(new PgnFen("insufficient_material_KBb_KQBb.pgn", "5b2/5k2/8/4q3/2K5/8/8/2B5 b - - 1 31"));
    list.add(new PgnFen("insufficient_material_KBb_KQQ.pgn", "8/8/4k3/7q/8/K7/3B1q2/8 b - - 0 32"));
    list.add(new PgnFen("insufficient_material_KBbBb_KQ.pgn", "7B/4k3/8/8/8/1q6/3BK3/8 b - - 0 34"));
    list.add(new PgnFen("insufficient_material_KBbBb_KQQ.pgn", "8/3q4/8/8/k2B4/B5K1/3q4/8 b - - 0 29"));
    list.add(new PgnFen("insufficient_material_KBbBb_KR.pgn", "8/8/7B/k7/8/8/1B4r1/4K3 b - - 1 32"));
    list.add(new PgnFen("insufficient_material_KBw_KQBw.pgn", "8/8/8/8/5q2/1k1B4/8/1b1K4 w - - 0 31"));
    list.add(new PgnFen("insufficient_material_KBw_KQQ.pgn", "1q6/8/8/8/5q2/1k6/6K1/5B2 w - - 0 29"));
    list.add(new PgnFen("insufficient_material_KBwBw_KQ.pgn", "q3B3/8/8/8/8/1k4K1/8/5B2 b - - 0 34"));
    list.add(new PgnFen("insufficient_material_KBwBw_KQQ.pgn", "q1B5/8/4B1q1/8/1k6/8/8/5K2 w - - 0 32"));
    list.add(new PgnFen("insufficient_material_KBwBw_KR.pgn", "r1B5/8/8/8/8/2k5/8/3K1B2 b - - 0 32"));
    list.add(new PgnFen("insufficient_material_KN_KQQ.pgn", "8/8/3k4/6q1/8/5K2/8/1Nq5 w - - 0 27"));
    return new PgnTestCaseList(PgnTest.BASIC_INSUFFICIENT_MATERIAL_ONLY_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesBasicInsufficientMaterialOnlyBlack() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("insufficient_material_KBwBb_K.pgn", "6k1/8/3K4/8/8/8/3BB3/8 b - - 0 33"));

    list.add(new PgnFen("insufficient_material_KBwN_K.pgn", "8/6k1/3K4/4N3/8/7B/8/8 w - - 0 36"));
    list.add(new PgnFen("insufficient_material_KBbN_K.pgn", "8/6k1/3K4/8/8/5N2/8/2B5 b - - 0 33"));

    list.add(new PgnFen("insufficient_material_KNN_K.pgn", "5k2/8/5N2/2K5/8/5N2/8/8 w - - 0 34"));
    // we have insufficientMaterial_KBw_KN already tested above
    // we have insufficientMaterial_KBb_KN already tested above
    list.add(new PgnFen("insufficient_material_KQ_KN.pgn", "1n6/8/8/4Q3/k3K3/8/8/8 b - - 0 24"));
    list.add(new PgnFen("insufficient_material_KQQ_KN.pgn", "1n6/7Q/8/2Q5/8/k2K4/8/8 b - - 0 28"));

    // King and single piece against KBw
    list.add(new PgnFen("insufficient_material_KR_KBw.pgn", "2b5/8/8/8/2k5/8/8/2K4R w - - 0 24"));
    list.add(new PgnFen("insufficient_material_KR_KBwBw.pgn", "2b5/8/8/1k6/3R4/8/4K3/1b6 b - - 0 26"));
    // we have insufficientMaterial_KN_KBw already tested above
    list.add(new PgnFen("insufficient_material_KQ_KBw.pgn", "2b5/2Q5/8/8/8/k7/8/2K5 w - - 0 25"));
    list.add(new PgnFen("insufficient_material_KQBw_KBw.pgn", "4B3/8/1k6/8/4b3/2K5/7Q/8 w - - 0 30"));
    list.add(new PgnFen("insufficient_material_KQQ_KBw.pgn", "3Q4/2Q5/b7/8/k7/8/8/2K5 b - - 0 26"));
    list.add(new PgnFen("insufficient_material_KQ_KBwBw.pgn", "2b5/8/8/8/k7/8/K7/3b3Q b - - 0 27"));
    list.add(new PgnFen("insufficient_material_KQQ_KBwBw.pgn", "1Qb5/7Q/8/8/k7/2K5/8/3b4 b - - 0 30"));

    // King and single piece against KBb
    list.add(new PgnFen("insufficient_material_KR_KBb.pgn", "8/5kb1/8/8/8/3K4/8/7R w - - 0 24"));
    list.add(new PgnFen("insufficient_material_KR_KBbBb.pgn", "5b2/8/6k1/8/8/R1K5/8/4b3 w - - 0 29"));

    // we have insufficientMaterial_KN_KBb already tested above
    list.add(new PgnFen("insufficient_material_KQ_KBb.pgn", "8/8/7k/4Q3/8/b2K4/8/8 w - - 0 24"));
    list.add(new PgnFen("insufficient_material_KQBb_KBb.pgn", "5B2/3Q4/7b/3K3k/8/8/8/8 b - - 0 29"));
    list.add(new PgnFen("insufficient_material_KQQ_KBb.pgn", "4Qb2/6k1/8/8/Q7/3K4/8/8 w - - 0 29"));
    list.add(new PgnFen("insufficient_material_KQ_KBbBb.pgn", "5b2/7k/8/8/K7/1Q6/8/4b3 w - - 0 31"));
    list.add(new PgnFen("insufficient_material_KQQ_KBbBb.pgn", "5b2/Q7/8/8/7k/1K6/3Q4/4b3 w - - 0 34"));
    list.add(new PgnFen("insufficient_material_KBbQ_KBb.pgn", "Q7/8/8/1k6/8/8/3K4/b1B5 w - - 0 28"));
    list.add(new PgnFen("insufficient_material_KBbQR_KBb.pgn", "3R3Q/8/7b/8/k7/8/4K3/2B5 b - - 0 31"));
    list.add(new PgnFen("insufficient_material_KBbR_KBb.pgn", "3R1b2/8/8/8/k7/8/4K3/2B5 w - - 0 33"));
    list.add(new PgnFen("insufficient_material_KBwQ_KBw.pgn", "8/8/k7/8/6B1/5b2/3Q4/2K5 w - - 0 35"));
    list.add(new PgnFen("insufficient_material_KBwQR_KBw.pgn", "5R1Q/1b1B4/8/8/2k5/8/8/2K5 b - - 0 38"));
    list.add(new PgnFen("insufficient_material_KBwR_KBw.pgn", "1R6/8/3k4/8/5K2/7B/4b3/8 w - - 0 30"));
    list.add(new PgnFen("insufficient_material_KQR_KBb.pgn", "1R6/3k4/8/b7/8/8/5K2/1Q6 w - - 0 30"));
    list.add(new PgnFen("insufficient_material_KQR_KBw.pgn", "1R6/8/4k3/8/8/3b4/3Q4/4K3 w - - 0 27"));
    return new PgnTestCaseList(PgnTest.BASIC_INSUFFICIENT_MATERIAL_ONLY_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesBasicInsufficientMaterialNone() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("insufficient_material_KBw_KBb.pgn", "8/8/3b2k1/8/3K2B1/8/8/8 b - - 0 35"));
    list.add(new PgnFen("insufficient_material_KBb_KBw.pgn", "8/8/k3b3/8/5K2/8/8/2B5 b - - 0 35"));

    list.add(new PgnFen("insufficient_material_KN_KN.pgn", "8/8/4K3/8/1n6/8/5k1N/8 w - - 0 37"));

    // KN against king and single piece
    list.add(new PgnFen("insufficient_material_KN_KR.pgn", "7r/3k4/8/8/8/8/3K4/1N6 w - - 0 21"));
    list.add(new PgnFen("insufficient_material_KN_KBw.pgn", "8/k2b4/8/8/2K5/8/8/1N6 w - - 0 23"));
    list.add(new PgnFen("insufficient_material_KN_KBb.pgn", "5b2/8/3k4/8/8/4K3/8/1N6 w - - 0 24"));
    list.add(new PgnFen("insufficient_material_KN_KP.pgn", "3k4/8/3p4/8/8/4K3/8/1N6 w - - 0 23"));
    list.add(new PgnFen("insufficient_material_KBw_KN.pgn", "1n6/8/8/2k1K3/8/8/8/5B2 w - - 0 26"));
    list.add(new PgnFen("insufficient_material_KBw_KP.pgn", "8/8/8/2p1K3/8/1k6/8/5B2 w - - 0 27"));
    list.add(new PgnFen("insufficient_material_KBb_KN.pgn", "1n6/8/8/8/4K3/1k6/1B6/8 b - - 3 27"));
    list.add(new PgnFen("insufficient_material_KBb_KP.pgn", "8/6B1/8/2p5/8/1k6/8/1K6 b - - 0 26"));

    // King and single piece against KN
    list.add(new PgnFen("insufficient_material_KR_KN.pgn", "1n6/8/8/2k5/4K3/8/7R/8 b - - 0 22"));
    list.add(new PgnFen("insufficient_material_KP_KN.pgn", "1n6/8/8/2k5/8/8/6KP/8 b - - 0 24"));
    list.add(new PgnFen("insufficient_material_KP_KBw.pgn", "8/8/8/3P4/k5b1/8/8/2K5 w - - 0 27"));

    list.add(new PgnFen("insufficient_material_KP_KBb.pgn", "5b2/8/4P3/3K2k1/8/8/8/8 b - - 0 25"));
    return new PgnTestCaseList(PgnTest.BASIC_INSUFFICIENT_MATERIAL_NONE, list);
  }

  private static PgnTestCaseList createTestCasesBasicThreefold() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(new PgnFen("01_threefold_moves_very_low_one_before_first_threefold.pgn",
        "rnbqkb1r/pppppppp/5n2/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 7 4"));
    list.add(new PgnFen("02_threefold_moves_very_low_end_with_first_threefold.pgn",
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 8 5"));
    list.add(new PgnFen("03_threefold_moves_low_one_before_first_threefold.pgn",
        "r1bqk2r/pppp1ppp/2n2n2/2b1p3/4P3/2N2N2/PPPP1PPP/R1BQKB1R b KQkq - 9 7"));
    list.add(new PgnFen("04_threefold_moves_low_end_with_first_threefold.pgn",
        "r1bqkb1r/pppp1ppp/2n2n2/4p3/4P3/2N2N2/PPPP1PPP/R1BQKB1R w KQkq - 10 8"));
    list.add(new PgnFen("05_threefold_moves_medium_one_before_first_threefold.pgn",
        "r1bq1rk1/pp1nbppp/2n1p3/1BppP3/3P1P2/2N1BN2/PPP3PP/R2QR1K1 w - - 14 13"));
    list.add(new PgnFen("06_threefold_moves_medium_end_with_first_threefold.pgn",
        "r1bq1rk1/pp1nbppp/2n1p3/1BppP3/3P1P2/2N1BN2/PPP3PP/R2Q1RK1 b - - 15 13"));
    list.add(new PgnFen("07_threefold_moves_high_one_before_first_threefold.pgn",
        "r4rk1/pp1b1pbp/q1n1p1p1/2ppPn2/P2P4/1P3NP1/2P1NPBP/R1BQ1RK1 b - - 10 17"));
    list.add(new PgnFen("08_threefold_moves_high_end_with_first_threefold.pgn",
        "r4rk1/pp1bnpbp/q1n1p1p1/2ppP3/P2P4/1P3NP1/2P1NPBP/R1BQ1RK1 w - - 11 18"));
    list.add(new PgnFen("09_threefold_moves_very_high_one_before_first_threefold.pgn",
        "1R4R1/5p1k/4p2p/3NP1pP/3r4/8/5PPK/8 w - - 11 49"));
    list.add(new PgnFen("10_threefold_moves_very_high_end_with_first_threefold.pgn",
        "1R5R/5p1k/4p2p/3NP1pP/3r4/8/5PPK/8 b - - 12 49"));
    list.add(new PgnFen("11_threefold_castling_one_before_first_threefold.pgn",
        "rnbqkb1r/1ppppppp/p7/8/3Nn3/8/PPPPPPPP/RNBQKB1R b KQk - 9 10"));
    list.add(new PgnFen("12_threefold_castling_end_with_first_threefold.pgn",
        "1nbqkb1r/rppppppp/p7/8/3Nn3/8/PPPPPPPP/RNBQKB1R w KQk - 10 11"));
    list.add(new PgnFen("13_threefold_en_passant_capture_one_before_first_threefold.pgn",
        "rnbqkb1r/ppp1pppp/5n2/3pP3/8/8/PPPP1PPP/RNBQKBNR w KQkq - 8 7"));
    list.add(new PgnFen("14_threefold_en_passant_capture_end_with_first_threefold.pgn",
        "rnbqkb1r/ppp1pppp/5n2/3pP3/2B5/8/PPPP1PPP/RNBQK1NR b KQkq - 9 7"));
    list.add(new PgnFen("15_threefold_en_passant_capture_castling_one_before_first_threefold.pgn",
        "rnbqkb1r/ppp1pppp/5n2/3pP3/8/8/PPPP1PPP/RNBQKBNR w KQ - 16 11"));
    list.add(new PgnFen("16_threefold_en_passant_capture_castling_end_with_first_threefold.pgn",
        "rnbqkb1r/ppp1pppp/5n2/3pP3/8/8/PPPP1PPP/RNBQKBNR w KQ - 20 13"));
    list.add(new PgnFen("17_threefold_two_threefolds_end_with_threefold.pgn",
        "rnbqkbnr/ppp2pp1/8/3pp2p/3PP2P/6P1/PPP2P2/RNBQKBNR b KQkq - 8 12"));
    list.add(new PgnFen("18_threefold_two_threefolds_beyond.pgn",
        "r1bqk1nr/2p3p1/ppnb1p2/3pp1Bp/1P1PP1QP/N1P3P1/P4P2/R3KBNR b KQkq - 1 17"));

    list.add(new PgnFen("19_threefold_multiple_threefolds_end_with_threefold.pgn",
        "rnbqkbnr/ppp2pp1/8/3pp2p/3PP2P/6P1/PPPB1P2/RN1QKBNR w KQkq - 11 16"));

    list.add(new PgnFen("20_threefold_multiple_threefolds_beyond.pgn",
        "2b1kbnr/Q4p2/2pq4/3ppP2/1n1PP3/5B2/1PP1N3/RNBQK1r1 w Qk - 0 30"));

    list.add(new PgnFen("21_threefold_max_position_repetition_castling_end_with_first_threefold.pgn",
        "rnbqkbnr/1ppp1pp1/8/p3p2p/P3P2P/8/1PPP1PP1/RNBQKBNR w - - 40 24"));

    list.add(
        new PgnFen("22_threefold_max_position_repetition_castling_and_en_passant_capture_end_with_first_threefold.pgn",
            "rnbqkbnr/2p1ppp1/p6p/1p1pP3/8/P6P/1PPP1PP1/RNBQKBNR w - - 44 27"));

    list.add(new PgnFen("24_threefold_max_visual_position_repetition_not_varying_end_with_first_threefold.pgn",
        "rnbqkbnr/1pp1ppp1/8/p2pP2p/P6P/8/1PPP1PP1/RNBQKBNR w - - 90 50"));
    list.add(new PgnFen("25_threefold_max_visual_position_repetition_varying_end_with_first_threefold.pgn",
        "rnbqkbnr/1pp1ppp1/8/p2pP2p/P6P/8/1PPP1PP1/RNBQKBNR w - - 90 50"));

    list.add(new PgnFen("26_threefold_two_threefolds_not_varying_end_with_threefold.pgn",
        "rnbqkbnr/1pp1ppp1/8/p2pP2p/P6P/8/1PPP1PP1/RNBQKBNR w - - 90 50"));
    list.add(new PgnFen("26_threefold_two_threefolds_varying_end_with_threefold.pgn",
        "rnbqkbnr/1pp1ppp1/8/p2pP2p/P6P/8/1PPP1PP1/RNBQKBNR w - - 90 50"));

    list.add(new PgnFen("28_threefold_castling_right_lost_position_allows_castling_end_with_first_threefold.pgn",
        "rnbqk2r/pppp1ppp/5n2/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w kq - 16 10"));
    list.add(new PgnFen("29_threefold_castling_right_lost_position_disallows_castling_end_with_first_threefold.pgn",
        "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w kq - 12 8"));
    list.add(new PgnFen("30_threefold_castling_white_both_sides_lost_end_with_first_threefold.pgn",
        "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w - - 16 10"));
    list.add(new PgnFen("31_threefold_castling_white_king_side_lost_end_with_first_threefold.pgn",
        "rnbqkbnr/ppppppp1/8/7p/7P/8/PPPPPPP1/RNBQKBNR w Qkq - 16 10"));
    list.add(new PgnFen("32_threefold_castling_white_queen_side_lost_end_with_first_threefold.pgn",
        "rnbqkbnr/1ppppppp/8/p7/P7/8/1PPPPPPP/RNBQKBNR w Kkq - 16 10"));

    list.add(new PgnFen("33_threefold_two_square_advance_no_pawn_same_rank_one_before_first_threefold.pgn",
        "rnbqkb1r/pp2pppp/8/2pp4/4n3/2N1PN2/PPPP1PPP/R1BQKB1R b KQkq - 7 7"));
    list.add(new PgnFen("34_threefold_two_square_advance_no_pawn_same_rank_end_with_first_threefold.pgn",
        "rnbqkb1r/pp2pppp/5n2/2pp4/8/2N1PN2/PPPP1PPP/R1BQKB1R w KQkq - 8 8"));
    list.add(new PgnFen("35_threefold_two_square_advance_no_pawn_same_rank_beyond.pgn",
        "rn1qkb1r/pp1bppp1/5n2/1Bpp3p/P7/2N1PN2/1PPP1PPP/R1BQK2R w KQkq - 2 10"));

    list.add(new PgnFen("36_threefold_en_passant_capture_situation_capture_allowed_yes_end_with_first_threefold.pgn",
        "r1bqkbnr/ppp1pppp/2n5/3pP3/8/8/PPPP1PPP/RNBQKBNR w KQkq - 12 9"));

    list.add(new PgnFen(
        "38_threefold_en_passant_capture_situation_capture_allowed_no_leaving_own_king_in_check_mine_end_with_first_threefold.pgn",
        "rnb1qbnr/ppp1kppp/4p3/1K1pP3/8/8/PPPP1PPP/RNBQ1BNR w - - 8 11"));

    list.add(new PgnFen(
        "39_threefold_en_passant_capture_situation_capture_allowed_no_exposing_own_king_to_check_from_forum_end_with_first_threefold.pgn",
        "6k1/8/8/8/5Pp1/8/6R1/6K1 b - - 12 38"));
    list.add(new PgnFen(
        "40_threefold_en_passant_capture_situation_capture_allowed_no_leaving_own_king_in_check_from_forum_end_with_first_threefold.pgn",
        "8/8/8/8/3Pp3/8/R6k/K7 b - - 12 43"));

    // Reactivated from pgnParser/legacy/common/beyond - filename says "threefold" but the game actually plays past
    // the first fivefold (the original test category was misleading).
    list.add(new PgnFen(
        "37_threefold_en_passant_capture_situation_capture_allowed_no_exposing_own_king_to_check_mine_end_with_first_threefold.pgn",
        "r1b1kb1r/ppp1qppp/2n2n2/3pP3/8/5N2/PPPP1PPP/RNBQKB1R w KQkq - 20 16"));

    return new PgnTestCaseList(PgnTest.BASIC_THREEFOLD, list);
  }

  private static PgnTestCaseList createTestCasesBasicFifty() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(new PgnFen("01_fifty_end_with_half_move_clock_99.pgn",
        "1r1nk2r/2pb1pp1/1p5Q/p2ppnbp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 b - - 99 56"));
    list.add(new PgnFen("02_fifty_end_with_half_move_clock_100.pgn",
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp1bp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 w - - 100 57"));
    list.add(new PgnFen("03_fifty_half_move_clock_99_then_checkmate_by_white_with_capture.pgn",
        "1rbqkb1r/pp1p1Qpp/2p5/3Bp3/PR1NP3/Bn2N3/2PP1PPP/5RK1 b k - 0 57"));
    list.add(new PgnFen("04_fifty_half_move_clock_99_then_checkmate_by_black_with_capture.pgn",
        "4rrk1/pppbnppp/8/8/1n1N4/4bN2/PP1PPqPP/R1BQKB1R w - - 0 54"));
    list.add(new PgnFen("05_fifty_half_move_clock_99_then_checkmate_by_white_without_capture_or_pawn_move.pgn",
        "1rbnk2r/pp1pQppp/1bp5/q2Bp3/PBRNP3/4N3/2PP1PPP/2R3K1 b k - 100 57"));
    list.add(new PgnFen("06_fifty_half_move_clock_99_then_checkmate_by_black_without_capture_or_pawn_move.pgn",
        "rk6/ppp1Nppp/3r1n2/8/bn1Q4/4bN2/PP1PPPPP/R1BqKB1R w KQ - 100 54"));
    list.add(new PgnFen("07_fifty_half_move_clock_99_then_stalemate_by_white_move.pgn",
        "k7/2R5/1Q3N2/5pP1/5P2/8/PPP1P3/K7 b - - 100 82"));
    list.add(new PgnFen("08_fifty_half_move_clock_99_then_stalemate_by_black_move.pgn",
        "8/8/1p1p4/p1pPp1p1/P1P1P3/8/2q4r/K2k4 w - - 100 74"));
    list.add(new PgnFen("09_fifty_bishop_walk_end_with_half_move_clock_110.pgn",
        "r1bqkbnr/ppp2ppp/1N6/3pp3/3PP3/1n6/PPP2PPP/R1BQKBNR w KQkq - 110 58"));
    list.add(new PgnFen("10_fifty_half_move_clock_99_fifty_moves_claimable_yes.pgn",
        "r2qkbnr/pppb1ppp/1N6/n2pp1Q1/3PPB2/8/PPP2PPP/R3KBNR b KQkq - 99 52"));
    list.add(new PgnFen("11_fifty_half_move_clock_99_fifty_moves_claimable_no_forced_capture.pgn",
        "r2qkbnr/pppbQppp/1N6/n2pp3/3PP3/4B3/PPP2PPP/R3KBNR b KQkq - 99 52"));
    list.add(new PgnFen("12_fifty_half_move_clock_99_fifty_moves_claimable_no_forced_pawn_move.pgn",
        "1rb3n1/pppq1pp1/2n1r3/2bpp2p/3PP1Qk/4K2N/PPP2PPP/RNBB3R b - - 99 53"));
    return new PgnTestCaseList(PgnTest.BASIC_FIFTY, list);
  }

  private static PgnTestCaseList createTestCasesBasicFivefold() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(new PgnFen("01_fivefold_one_before_first_fivefold.pgn",
        "r1bqk1nr/1pp2ppp/p1np4/2b1p3/2B1P3/3P1N1P/PPP2PP1/RNBQK2R w KQkq - 12 12"));
    list.add(new PgnFen("02_fivefold_end_with_first_fivefold.pgn",
        "r1bqk1nr/1pp2ppp/p1np4/2b1p3/2B1P3/3P1N1P/PPP2PP1/RNBQK2R w KQkq - 16 14"));
    list.add(new PgnFen("03_fivefold_one_before_first_fivefold.pgn",
        "r1b1k1nr/1ppq1ppp/p1np4/2b1p3/2B1P3/3P1N1P/PPP2PP1/RNBQK2R b KQkq - 15 13"));

    list.add(new PgnFen("04_fivefold_end_with_first_fivefold.pgn",
        "r1bqk1nr/1pp2ppp/p1np4/2b1p3/2B1P3/3P1N1P/PPP2PP1/RNBQK2R w KQkq - 16 14"));
    list.add(new PgnFen("07_fivefold_max_position_repetitions_castling_end_with_first_fivefold.pgn",
        "rnbqkbnr/1ppp1pp1/8/p3p2p/P3P2P/8/1PPP1PP1/RNBQKBNR w - - 48 28"));

    list.add(
        new PgnFen("08_fivefold_max_position_repetitions_castling_and_en_passant_capture_end_with_first_fivefold.pgn",
            "rnbqkbnr/2p1ppp1/p6p/1p1pP3/8/P6P/1PPP1PP1/RNBQKBNR w - - 52 31"));

    // Reactivated from pgnParser/legacy/common/beyond - play continues past first fivefold.
    list.add(new PgnFen("05_fivefold_beyond.pgn",
        "r1bqk1nr/1pp2p1p/pbnp2p1/4p3/1PB1P3/P2P1N1P/2P2PP1/RNBQK2R w KQkq - 1 16"));
    list.add(new PgnFen("06_fivefold_end_with_first_sixfold.pgn",
        "r1bqk1nr/1pp2ppp/p1np4/2b1p3/2B1P3/3P1N1P/PPP2PP1/RNBQK2R w KQkq - 20 16"));

    return new PgnTestCaseList(PgnTest.BASIC_FIVEFOLD, list);
  }

  private static PgnTestCaseList createTestCasesBasicSeventyFive() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(new PgnFen("01_seventy_five_end_with_half_move_clock_149.pgn",
        "1r1n2r1/2pk1ppR/1pbn4/p2pp2p/P1B1P2P/1RKP1Q1N/1PP1NPP1/1qB3b1 b - - 149 81"));
    list.add(new PgnFen("02_seventy_five_end_with_half_move_clock_150.pgn",
        "1rkn2r1/2p2ppR/1pbn4/p2pp2p/P1B1P2P/1RKP1Q1N/1PP1NPP1/1qB3b1 w - - 150 82"));
    list.add(new PgnFen("11_seventy_five_half_move_clock_149_then_checkmate_by_white_with_capture.pgn",
        "5rrk/ppp2ppQ/3n1N2/2bbp3/n1qPB3/B1P5/PPN2PPP/1KR4R b - - 0 79"));
    list.add(new PgnFen("12_seventy_five_half_move_clock_149_then_checkmate_by_black_with_capture.pgn",
        "2b4r/ppp1ppb1/kN1r3p/3p2p1/P2P2n1/3n1QP1/1PPBPP1q/R3NBRK w - - 0 79"));
    list.add(new PgnFen("13_seventy_five_half_move_clock_149_then_checkmate_by_white_without_capture_or_pawn_move.pgn",
        "4Q1k1/ppp2ppp/1b4rr/1n1bp3/1BqPB1N1/2P3n1/PPN2PPP/1KR4R b - - 150 79"));
    list.add(new PgnFen("14_seventy_five_half_move_clock_149_then_checkmate_by_black_without_capture_or_pawn_move.pgn",
        "3r1n1b/pppbpp2/k1r4p/3pQ1pK/Pn1P2q1/RNN3P1/1PPBPPBP/6R1 w - - 150 79"));

    list.add(new PgnFen("15_seventy_five_half_move_clock_149_then_stalemate_by_white_move.pgn",
        "8/4K3/1NB4P/p1pp3Q/PpP1k1p1/1P3pP1/1B3P2/3R2NR b - - 150 102"));

    list.add(new PgnFen("16_seventy_five_half_move_clock_149_then_stalemate_by_black_move.pgn",
        "1r3n1r/kbp1n2p/1p2p2b/pP1pP1p1/P2P1pP1/5P2/5q2/7K w - - 150 97"));

    // Reactivated from pgnParser/legacy/common/beyond - play continues past first 75-move.
    list.add(new PgnFen("03_seventy_five_end_with_half_move_clock_154.pgn",
        "1r1n2r1/k1p2ppR/1pbn4/pR1pp2p/P1B1P2P/1K1P1Q1N/1PP1NPP1/1qB3b1 w - - 154 84"));
    list.add(new PgnFen("04_seventy_five_beyond_half_move_clock_154_end_with_half_move_clock_99.pgn",
        "3nrr2/kqp2pp1/1pb1BRn1/pQ2p1Np/P2RPB1P/3P1K2/1PP1NPPb/8 w - - 99 134"));
    list.add(new PgnFen("05_seventy_five_beyond_half_move_clock_154_end_with_half_move_clock_100.pgn",
        "3nrr2/kqp2pp1/1pb1BRn1/p2Qp1Np/P2RPB1P/3P1K2/1PP1NPPb/8 b - - 100 134"));
    list.add(new PgnFen("06_seventy_five_beyond_half_move_clock_154_end_with_half_move_clock_117.pgn",
        "2Qnrr2/k1pB1ppN/1p2R1n1/p2bp2p/P2RPB1P/3P1K2/1PPqNPPb/8 w - - 117 143"));
    list.add(new PgnFen("07_seventy_five_beyond_half_move_clock_154_end_with_half_move_clock_149.pgn",
        "r3n2Q/k1p1rpp1/1p2RB2/p2bp1Np/P2RPn1P/3P1K2/1PPN1PPb/1B4q1 w - - 149 159"));
    list.add(new PgnFen("08_seventy_five_beyond_half_move_clock_154_end_with_half_move_clock_150.pgn",
        "r3n3/k1p1rpp1/1p2RB1Q/p2bp1Np/P2RPn1P/3P1K2/1PPN1PPb/1B4q1 b - - 150 159"));
    list.add(new PgnFen("09_seventy_five_beyond_half_move_clock_154_end_with_half_move_clock_178.pgn",
        "r2r1B1Q/1kp1Rpp1/1p3n2/p3p1Np/P2RPn1P/1b1P1K2/1PPN1PPb/1B2q3 b - - 178 173"));
    list.add(new PgnFen(
        "10_seventy_five_beyond_half_move_clock_154_beyond_half_move_clock_178_end_with_half_move_clock_10.pgn",
        "rn2RB2/2p2ppQ/kp1N2n1/p3p2p/P1r1P2P/1b1P1K1N/1PP2PPb/1B2q3 w - - 10 179"));

    return new PgnTestCaseList(PgnTest.BASIC_SEVENTY_FIVE, list);
  }

  private static PgnTestCaseList createTestCasesBasicIntervening() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_intervening_threefold_encapsulating_threefold.pgn",
        "rnbqkbnr/1ppp1ppp/p7/4p3/4P3/P7/1PPP1PPP/RNBQKBNR w KQkq - 0 11"));

    list.add(new PgnFen("03_intervening_fivefold_encapsulating_threefold.pgn",
        "rnbqkbnr/ppppp2p/8/5pp1/5PP1/8/PPPPP2P/RNBQKBNR w KQkq - 28 17"));

    list.add(new PgnFen("05_intervening_threefold_interlocked_threefold.pgn",
        "r1bqk1nr/p1pppp1p/1pn3pb/8/8/1PN3PB/P1PPPP1P/R1BQK1NR w KQkq - 16 11"));

    list.add(new PgnFen("06_intervening_threefold_interlocked_fivefold.pgn",
        "rnbqk1nr/p1pp1ppp/8/1pb1p3/1PB1P3/8/P1PP1PPP/RNBQK1NR w KQkq - 24 15"));

    // Reactivated from pgnParser/legacy/common/beyond - play continues past first fivefold.
    list.add(new PgnFen("02_intervening_threefold_encapsulating_fivefold.pgn",
        "r1bqkbnr/p2ppppp/2n5/1pp5/1PP5/2N5/P2PPPPP/R1BQKBNR w KQkq - 30 18"));
    list.add(new PgnFen("04_intervening_fivefold_encapsulating_fivefold.pgn",
        "rnbqkb1r/pp1p1ppp/2p1pn2/8/8/2P1PN2/PP1P1PPP/RNBQKB1R w KQkq - 34 20"));
    list.add(new PgnFen("07_intervening_fivefold_interlocked_threefold.pgn",
        "r1bqkb1r/ppp2ppp/2n5/3pp1N1/3PP1n1/2N5/PPP2PPP/R1BQKB1R w KQkq - 38 22"));
    list.add(new PgnFen("08_intervening_fivefold_interlocked_fivefold.pgn",
        "rnq1kbnr/p1p1pp1p/bp4p1/3p4/3P4/BP4P1/P1P1PP1P/RNQ1KBNR w KQkq - 40 24"));

    return new PgnTestCaseList(PgnTest.BASIC_INTERVENING, list);
  }

  private static PgnTestCaseList createTestCasesBasicDoubleDraw() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_double_draw_threefold_and_fifty_move.pgn",
        "rnbqkbnr/p1p2p1p/1p4p1/3pp3/3PP3/1P4P1/P1P2P1P/RNBQKBNR w KQkq - 100 55"));
    list.add(new PgnFen("02_double_draw_threefold_and_seventy_five_move.pgn",
        "rnbqkbnr/p1p2p1p/1p4p1/3pp3/3PP3/1P4P1/P1P2P1P/RNBQKBNR w KQkq - 150 80"));
    list.add(new PgnFen("03_double_draw_fivefold_and_fifty_move.pgn",
        "rnbqkbnr/p1p2p1p/1p4p1/3pp3/3PP3/1P4P1/P1P2P1P/RNBQKBNR w KQkq - 100 55"));
    list.add(new PgnFen("04_double_draw_fivefold_and_seventy_five_move.pgn",
        "rnbqkbnr/p1p2p1p/1p4p1/3pp3/3PP3/1P4P1/P1P2P1P/RNBQKBNR w KQkq - 150 80"));

    return new PgnTestCaseList(PgnTest.BASIC_DOUBLE_DRAW, list);

  }

  private static PgnTestCaseList createTestCasesBasicCastlingWhite() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(new PgnFen("01_white_castling_king_side.pgn",
        "r1bqk1nr/pppp1ppp/2n5/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQ1RK1 b kq - 5 4"));
    list.add(new PgnFen("02_white_castling_queen_side.pgn",
        "r2qkbnr/ppp2ppp/2n1p3/3p1b2/3P1B2/2NQ4/PPP1PPPP/2KR1BNR b kq - 1 5"));
    return new PgnTestCaseList(PgnTest.BASIC_CASTLING_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesBasicCastlingBlack() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(new PgnFen("01_black_castling_king_side.pgn",
        "r1bq1rk1/pppp1ppp/2n2n2/2b1p3/2B1P3/3P1N1P/PPP2PP1/RNBQK2R w KQ - 1 6"));
    list.add(new PgnFen("02_black_castling_queen_side.pgn",
        "2kr1bnr/pppq1ppp/2n1p3/1B1p1b2/3P1B2/2N1PN2/PPP2PPP/R2QK2R w KQ - 4 7"));
    return new PgnTestCaseList(PgnTest.BASIC_CASTLING_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesBasicCastlingSpecialWhite() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_white_castling_special_kingside_check.pgn",
        "rnbq1b1r/pppppkpp/8/PB5n/4p3/7N/1PPP2PP/RNBQ1RK1 b - - 2 8"));
    list.add(new PgnFen("02_white_castling_special_kingside_checkmate.pgn",
        "rnbq1bnr/pppp1ppp/8/Q7/3PP3/1PBB1N1N/P1P2PPP/1k3RK1 b - - 6 21"));
    list.add(new PgnFen("03_white_castling_special_kingside_fifty_move.pgn",
        "2Qqb3/5k1r/2B3p1/2NPBp2/P3n1p1/N7/1n5r/R4RK1 b - - 100 68"));
    list.add(new PgnFen("04_white_castling_special_kingside_seventy_five_move.pgn",
        "B7/1bn1k1r1/4N1p1/3PBp2/P1N3p1/2q5/1n1Q3r/R4RK1 b - - 150 93"));
    list.add(new PgnFen("05_white_castling_special_kingside_stalemate.pgn",
        "8/8/8/p5N1/P1PP4/R2Q2P1/1k3PBP/5RK1 b - - 6 36"));
    list.add(new PgnFen("06_white_castling_special_queenside_check.pgn",
        "rnbq3r/pppk1pp1/4pn1p/7Q/5B2/5N2/PPP1PPPP/2KR1BNR b - - 3 8"));
    list.add(new PgnFen("07_white_castling_special_queenside_checkmate.pgn",
        "rnb1qbnr/ppppp1pp/5p2/1B4N1/3PPBPP/2N2P2/PPPQ4/2KR3k b - - 2 14"));
    list.add(new PgnFen("08_white_castling_special_queenside_fifty_move.pgn",
        "2b4Q/2qnk3/1Q3r2/p1p3b1/PpP2Pp1/1N2PNPp/1B2B2P/2KR3R b - - 100 66"));
    list.add(new PgnFen("09_white_castling_special_queenside_seventy_five_move.pgn",
        "6Q1/3bk1r1/1q3n2/p1p1b1Q1/PpP2Pp1/1N2PNPp/1B2B2P/2KR3R b - - 150 91"));
    list.add(new PgnFen("10_white_castling_special_queenside_stalemate.pgn",
        "8/8/4B1P1/pP6/P1p1P1PP/2N2Q2/1BPP3k/2KR4 b - - 2 35"));

    return new PgnTestCaseList(PgnTest.BASIC_CASTLING_SPECIAL_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesBasicCastlingSpecialBlack() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_black_castling_special_kingside_check.pgn",
        "rnbq1rk1/ppppb2p/7n/8/8/8/PPPPPKPP/RNBQ1BNR w - - 2 6"));
    list.add(new PgnFen("02_black_castling_special_kingside_checkmate.pgn",
        "K4rk1/p1pppp1p/6p1/np1n4/3b4/8/PPP1PPPP/RNBQ1BNR w - - 2 15"));
    list.add(new PgnFen("03_black_castling_special_kingside_fifty_move.pgn",
        "5rk1/1Q2n3/R6p/8/1b1B1p1K/1P6/P1P5/RN1Q1BNb w - - 100 67"));
    list.add(new PgnFen("04_black_castling_special_kingside_seventy_five_move.pgn",
        "5rk1/1Q2n3/R6p/1N6/3B1p1K/1Pb2b2/P1P5/R2Q1BN1 w - - 150 92"));
    list.add(new PgnFen("05_black_castling_special_kingside_stalemate.pgn",
        "5rk1/K2n1pb1/2qp3p/pp2p1pP/6P1/8/8/8 w - - 2 31"));
    list.add(new PgnFen("06_black_castling_special_queenside_check.pgn",
        "2kr1bnr/ppq2pp1/2n4p/5b2/5B2/2N5/PPPKPPPP/R2Q1BNR w - - 2 8"));
    list.add(new PgnFen("07_black_castling_special_queenside_checkmate.pgn",
        "2kr3K/pbppq2p/1pn2p2/4p3/5Pn1/8/PPPPP1PP/RNBQ1BNR w - - 1 12"));
    list.add(new PgnFen("08_black_castling_special_queenside_fifty_move.pgn",
        "2kr3r/q2b1p1p/2n2npB/p7/4P3/bQN3PN/1P3P1P/R3KB1R w K - 100 60"));
    list.add(new PgnFen("09_black_castling_special_queenside_seventy_five_move.pgn",
        "2kr4/3b1prp/1qn2npB/p7/4P3/b1N3PN/QP3PBP/R3K1R1 w - - 150 85"));
    list.add(new PgnFen("10_black_castling_special_queenside_stalemate.pgn",
        "2kr4/1bpp3K/1p3q2/pP2pP2/P5n1/3n4/8/8 w - - 15 42"));

    return new PgnTestCaseList(PgnTest.BASIC_CASTLING_SPECIAL_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesBasicForced() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_forced_checkmate.pgn", "7k/q5Q1/p4PPK/6PP/8/5P2/8/8 b - - 16 40"));
    list.add(new PgnFen("02_forced_checkmate_played.pgn", "6k1/5PP1/6PK/6PP/8/p7/8/8 b - - 0 45"));

    list.add(new PgnFen("03_forced_insufficient_material.pgn", "r7/K7/2k5/8/8/8/8/8 w - - 11 43"));
    list.add(new PgnFen("04_forced_insufficient_material_played.pgn", "K7/8/2k5/8/8/8/8/8 b - - 0 43"));

    list.add(new PgnFen("05_forced_stalemate.pgn", "1r4k1/6Q1/8/8/8/q7/P7/K7 b - - 4 36"));
    list.add(new PgnFen("06_forced_stalemate_played.pgn", "1r6/6k1/8/8/8/q7/P7/K7 w - - 0 37"));

    list.add(new PgnFen("07_forced_stalemate.pgn", "6kr/1p1N1p1p/1P3P1P/P6P/3q1RQr/7p/4pN1P/4B1KR b - - 2 41"));
    list.add(new PgnFen("07_forced_stalemate_played.pgn", "6kr/1p1N1p1p/1P3P1P/P6P/6N1/7p/4p2P/4B1KR b - - 0 43"));

    return new PgnTestCaseList(PgnTest.BASIC_FORCED, list);
  }

  private static PgnTestCaseList createTestCasesParserFromFen() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("from_fen_capture_first_move_half_move_clock_100_black_to_move.pgn",
        "k7/2R1Q3/5N2/5pP1/5P2/8/PPP1P3/K7 w - - 0 101"));
    list.add(new PgnFen("from_fen_capture_first_move_half_move_clock_100_white_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp1Np/P3P2P/1R1PR3/1PP2PP1/qBB1K1N1 b - - 0 100"));
    list.add(new PgnFen("from_fen_capture_first_move_half_move_clock_101_black_to_move.pgn",
        "1r1nk2r/2p2pp1/1p1n3Q/p2pp1bp/P3P2P/1R1PR2b/1PP2PP1/qBB1K1N1 w - - 0 101"));
    list.add(new PgnFen("from_fen_capture_first_move_half_move_clock_101_white_to_move.pgn",
        "1r1nk2r/2pb1pp1/1R1n3Q/p2pp1bp/P3P2P/3PR2N/1PP2PP1/qBB1K1N1 b - - 0 100"));
    list.add(new PgnFen("from_fen_capture_first_move_half_move_clock_149_black_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p5Q/p2ppnbp/q3P2P/1R1PR2N/1PP2PP1/1BB1K1N1 w - - 0 101"));
    list.add(new PgnFen("from_fen_capture_first_move_half_move_clock_149_white_to_move.pgn",
        "1rbqkb1r/pp1p1ppp/2p5/3Bp3/PR2P3/BN2NQ2/2PP1PPP/5RK1 b k - 0 100"));
    list.add(new PgnFen("from_fen_capture_first_move_half_move_clock_099_black_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p5Q/p2ppnbp/P3P2P/1R1PR2N/1qP2PP1/1BB1K1N1 w - - 0 101"));
    list.add(new PgnFen("from_fen_capture_first_move_half_move_clock_099_white_to_move.pgn",
        "1rbqkb1r/pR1p1ppp/2p5/3Bp3/P2NP3/Bn2NQ2/2PP1PPP/5RK1 b k - 0 100"));
    list.add(new PgnFen("from_fen_capture_second_move_half_move_clock_100_black_to_move.pgn",
        "1k6/2R5/5N2/5pP1/5P2/Q7/PPP1P3/K7 b - - 0 101"));
    list.add(new PgnFen("from_fen_capture_second_move_half_move_clock_100_white_to_move.pgn",
        "1r1nk2r/2p2pp1/1p1nb3/p2pp1bp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 w - - 0 101"));
    list.add(new PgnFen("from_fen_capture_second_move_half_move_clock_101_black_to_move.pgn",
        "1r1nk2r/2pb1pQ1/1p1n4/p2pp1bp/P3P2P/qR1PR2N/1PP2PP1/1BB1K1N1 b - - 0 101"));
    list.add(new PgnFen("from_fen_capture_second_move_half_move_clock_101_white_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp2p/P3P2P/1R1Pb2N/1PP2PP1/qBB2KN1 w - - 0 101"));
    list.add(new PgnFen("from_fen_capture_second_move_half_move_clock_099_black_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p5Q/p2ppnbp/P3P2P/R2PR2N/1PP2PP1/1BB1K1N1 b - - 0 101"));
    list.add(new PgnFen("from_fen_capture_second_move_half_move_clock_099_white_to_move.pgn",
        "1rbqkb1r/pp1p1p1p/2p2p2/3Bp3/PR1NP3/Bn2N3/2PP1PPP/5RK1 w k - 0 101"));
    list.add(new PgnFen("from_fen_no_move_half_move_clock_100_black_to_move.pgn",
        "k7/2R1Q3/5N2/5pP1/5P2/8/PPP1P3/K7 b - - 100 100"));
    list.add(new PgnFen("from_fen_no_move_half_move_clock_100_white_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp1bp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 w - - 100 100"));
    list.add(new PgnFen("from_fen_no_move_half_move_clock_101_black_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp1bp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 b - - 101 100"));
    list.add(new PgnFen("from_fen_no_move_half_move_clock_101_white_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp1bp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 w - - 101 100"));
    list.add(new PgnFen("from_fen_no_move_half_move_clock_149_black_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p5Q/p2ppnbp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 b - - 149 100"));
    list.add(new PgnFen("from_fen_no_move_half_move_clock_149_white_to_move.pgn",
        "1rbqkb1r/pp1p1ppp/2p5/3Bp3/PR1NP3/Bn2NQ2/2PP1PPP/5RK1 w k - 149 100"));
    list.add(new PgnFen("from_fen_no_move_half_move_clock_150_black_to_move.pgn",
        "k7/2R1Q3/5N2/5pP1/5P2/8/PPP1P3/K7 b - - 150 100"));
    list.add(new PgnFen("from_fen_no_move_half_move_clock_150_white_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp1bp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 w - - 150 100"));
    list.add(new PgnFen("from_fen_no_move_half_move_clock_099_black_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p5Q/p2ppnbp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 b - - 99 100"));
    list.add(new PgnFen("from_fen_no_move_half_move_clock_099_white_to_move.pgn",
        "1rbqkb1r/pp1p1ppp/2p5/3Bp3/PR1NP3/Bn2NQ2/2PP1PPP/5RK1 w k - 99 100"));
    list.add(new PgnFen("from_fen_one_move_half_move_clock_100_black_to_move.pgn",
        "1k6/2R1Q3/5N2/5pP1/5P2/8/PPP1P3/K7 w - - 101 101"));
    list.add(new PgnFen("from_fen_one_move_half_move_clock_100_white_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p1nQ3/p2pp1bp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 b - - 101 100"));
    list.add(new PgnFen("from_fen_one_move_half_move_clock_101_black_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp1bp/P3P2P/qR1PR2N/1PP2PP1/1BB1K1N1 w - - 102 101"));
    list.add(new PgnFen("from_fen_one_move_half_move_clock_101_white_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp1bp/P3P2P/1R1PR2N/1PP2PP1/qBB2KN1 b - - 102 100"));
    list.add(new PgnFen("from_fen_one_move_half_move_clock_149_black_to_move.pgn",
        "3nk2r/1rpb1pp1/1p5Q/p2ppnbp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 w - - 150 101"));
    list.add(new PgnFen("from_fen_one_move_half_move_clock_149_white_to_move.pgn",
        "1rbqkb1r/pp1p1ppp/2p5/3Bp3/PR1NP3/Bn2NQ2/2PP1PPP/5R1K b k - 150 100"));
    list.add(new PgnFen("from_fen_one_move_half_move_clock_099_black_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p5Q/p2ppnbp/P3P2P/qR1PR2N/1PP2PP1/1BB1K1N1 w - - 100 101"));
    list.add(new PgnFen("from_fen_one_move_half_move_clock_099_white_to_move.pgn",
        "1rbqkb1r/pp1p1ppp/2p2Q2/3Bp3/PR1NP3/Bn2N3/2PP1PPP/5RK1 b k - 100 100"));
    list.add(new PgnFen("from_fen_pawn_first_move_half_move_clock_100_black_to_move.pgn",
        "k7/2R1Q3/5N2/6P1/5p2/5P2/PPP1P3/K7 w - - 0 101"));
    list.add(new PgnFen("from_fen_pawn_first_move_half_move_clock_100_white_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp1bp/P3P2P/1R1PR1PN/1PP2P2/qBB1K1N1 b - - 0 100"));
    list.add(new PgnFen("from_fen_pawn_first_move_half_move_clock_101_black_to_move.pgn",
        "1r1nk2r/2pb2p1/1p1n3Q/p2pppbp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 w - f6 0 101"));
    list.add(new PgnFen("from_fen_pawn_first_move_half_move_clock_101_white_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp1bp/P2PP2P/1R2R2N/1PP2PP1/qBB1K1N1 b - - 0 100"));
    list.add(new PgnFen("from_fen_pawn_first_move_half_move_clock_149_black_to_move.pgn",
        "1r1nk2r/2pb1pp1/7Q/pp1ppnbp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 w - - 0 101"));
    list.add(new PgnFen("from_fen_pawn_first_move_half_move_clock_149_white_to_move.pgn",
        "1rbqkb1r/pp1p1ppp/2p5/P2Bp3/1R1NP3/Bn2NQ2/2PP1PPP/5RK1 b k - 0 100"));
    list.add(new PgnFen("from_fen_pawn_first_move_half_move_clock_099_black_to_move.pgn",
        "1r1nk2r/2pb1pp1/7Q/pp1ppnbp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 w - - 0 101"));
    list.add(new PgnFen("from_fen_pawn_first_move_half_move_clock_099_white_to_move.pgn",
        "1rbqkb1r/pp1p1ppp/2p5/3Bp3/PR1NP2P/Bn2NQ2/2PP1PP1/5RK1 b k h3 0 100"));
    list.add(new PgnFen("from_fen_repetition_from_one_move_black_to_move_fivefold.pgn",
        "1n2kb1r/p2q1ppp/8/4p1B1/4P3/8/PPP2PPP/2KR4 w k - 17 26"));
    list.add(new PgnFen("from_fen_repetition_from_one_move_black_to_move_fourfold.pgn",
        "1n2kb1r/p2q1ppp/8/4p1B1/4P3/8/PPP2PPP/2KR4 w k - 13 24"));
    list.add(new PgnFen("from_fen_repetition_from_one_move_black_to_move_threefold.pgn",
        "1n2kb1r/p2q1ppp/8/4p1B1/4P3/8/PPP2PPP/2KR4 w k - 9 22"));
    list.add(new PgnFen("from_fen_repetition_from_one_move_white_to_move_fivefold.pgn",
        "r1b2r2/pp1pk1pp/8/7q/Q2pP1n1/5N1P/PP3PP1/3R1RK1 b - - 17 25"));
    list.add(new PgnFen("from_fen_repetition_from_one_move_white_to_move_fourfold.pgn",
        "r1b2r2/pp1pk1pp/8/7q/Q2pP1n1/5N1P/PP3PP1/3R1RK1 b - - 13 23"));
    list.add(new PgnFen("from_fen_repetition_from_one_move_white_to_move_threefold.pgn",
        "r1b2r2/pp1pk1pp/8/7q/Q2pP1n1/5N1P/PP3PP1/3R1RK1 b - - 9 21"));
    list.add(new PgnFen("from_fen_repetition_from_three_moves_black_to_move_fivefold.pgn",
        "rnb1kbnr/pppp1p1p/8/4N3/2B1Pp1q/6p1/PPPP2PP/RNBQ1K1R w kq - 16 15"));
    list.add(new PgnFen("from_fen_repetition_from_three_moves_black_to_move_fourfold.pgn",
        "rnb1kbnr/pppp1p1p/8/4N3/2B1Pp1q/6p1/PPPP2PP/RNBQ1K1R w kq - 12 13"));
    list.add(new PgnFen("from_fen_repetition_from_three_moves_black_to_move_threefold.pgn",
        "rnb1kbnr/pppp1p1p/8/4N3/2B1Pp1q/6p1/PPPP2PP/RNBQ1K1R w kq - 8 11"));
    list.add(new PgnFen("from_fen_repetition_from_three_moves_white_to_move_fivefold.pgn",
        "r4rk1/p1pq1ppp/1pn1p3/1b2P3/1Pp2Pn1/2Q3P1/PB1NP1BP/R4RK1 b - - 16 22"));
    list.add(new PgnFen("from_fen_repetition_from_three_moves_white_to_move_fourfold.pgn",
        "r4rk1/p1pq1ppp/1pn1p3/1b2P3/1Pp2Pn1/2Q3P1/PB1NP1BP/R4RK1 b - - 12 20"));
    list.add(new PgnFen("from_fen_repetition_from_three_moves_white_to_move_threefold.pgn",
        "r4rk1/p1pq1ppp/1pn1p3/1b2P3/1Pp2Pn1/2Q3P1/PB1NP1BP/R4RK1 b - - 8 18"));
    list.add(new PgnFen("from_fen_repetition_from_two_moves_black_to_move_fivefold.pgn",
        "r2qk2r/pppbnpp1/1bn4p/4p1N1/P1BP4/2N5/5PPP/R1BQR1K1 b kq - 16 21"));
    list.add(new PgnFen("from_fen_repetition_from_two_moves_black_to_move_fourfold.pgn",
        "r2qk2r/pppbnpp1/1bn4p/4p1N1/P1BP4/2N5/5PPP/R1BQR1K1 b kq - 12 19"));
    list.add(new PgnFen("from_fen_repetition_from_two_moves_black_to_move_threefold.pgn",
        "r2qk2r/pppbnpp1/1bn4p/4p1N1/P1BP4/2N5/5PPP/R1BQR1K1 b kq - 8 17"));
    list.add(new PgnFen("from_fen_repetition_from_two_moves_white_to_move_fivefold.pgn",
        "r1q2rk1/3b1ppp/nb1p4/1p1Pp2n/1P2P3/4BN1P/1Q2RPP1/RB3NK1 w - - 18 32"));
    list.add(new PgnFen("from_fen_repetition_from_two_moves_white_to_move_fourfold.pgn",
        "r1q2rk1/3b1ppp/nb1p4/1p1Pp2n/1P2P3/4BN1P/1Q2RPP1/RB3NK1 w - - 14 30"));
    list.add(new PgnFen("from_fen_repetition_from_two_moves_white_to_move_threefold.pgn",
        "r1q2rk1/3b1ppp/nb1p4/1p1Pp2n/1P2P3/4BN1P/1Q2RPP1/RB3NK1 w - - 10 28"));
    list.add(new PgnFen("from_fen_repetition_from_zero_moves_black_to_move_fivefold.pgn",
        "r2q2n1/1bpk1pp1/p2b3p/1p2pN2/P2PP3/R1P1Q3/1P1N1PPP/2B2RK1 b - - 16 63"));
    list.add(new PgnFen("from_fen_repetition_from_zero_moves_black_to_move_fourfold.pgn",
        "r2q2n1/1bpk1pp1/p2b3p/1p2pN2/P2PP3/R1P1Q3/1P1N1PPP/2B2RK1 b - - 12 61"));
    list.add(new PgnFen("from_fen_repetition_from_zero_moves_black_to_move_threefold.pgn",
        "r2q2n1/1bpk1pp1/p2b3p/1p2pN2/P2PP3/R1P1Q3/1P1N1PPP/2B2RK1 b - - 8 59"));
    list.add(new PgnFen("from_fen_repetition_from_zero_moves_white_to_move_fivefold.pgn",
        "r1bqkb1r/1pp2ppp/p1p2n2/4p2Q/P3P3/8/1PPP1PPP/RNB1K1NR w KQkq - 26 58"));
    list.add(new PgnFen("from_fen_repetition_from_zero_moves_white_to_move_fourfold.pgn",
        "r1bqkb1r/1pp2ppp/p1p2n2/4p2Q/P3P3/8/1PPP1PPP/RNB1K1NR w KQkq - 22 56"));
    list.add(new PgnFen("from_fen_repetition_from_zero_moves_white_to_move_threefold.pgn",
        "r1bqkb1r/1pp2ppp/p1p2n2/4p2Q/P3P3/8/1PPP1PPP/RNB1K1NR w KQkq - 18 54"));
    list.add(new PgnFen("from_fen_three_moves_half_move_clock_100_black_to_move.pgn",
        "2k5/3RQ3/5N2/5pP1/5P2/8/PPP1P3/K7 w - - 103 102"));
    list.add(new PgnFen("from_fen_three_moves_half_move_clock_100_white_to_move.pgn",
        "1r1n1k1r/2pb1pp1/1p1nQ3/pR1pp1bp/P3P2P/3PR2N/1PP2PP1/qBB1K1N1 b - - 103 101"));
    list.add(new PgnFen("from_fen_three_moves_half_move_clock_101_black_to_move.pgn",
        "1r2k2r/2pb1pp1/1pnn3Q/p2pp1bp/P3P2P/qR1P3N/1PP1RPP1/1BB1K1N1 w - - 104 102"));
    list.add(new PgnFen("from_fen_three_moves_half_move_clock_101_white_to_move.pgn",
        "1r1n1k1r/2pb1pp1/1p1n3Q/p2pp1bp/P3P2P/1R1P1R1N/1PP2PP1/qBB2KN1 b - - 104 101"));
    list.add(new PgnFen("from_fen_three_moves_half_move_clock_099_black_to_move.pgn",
        "1r2k2r/2pb1pp1/1p2n2Q/p2ppnbp/P3P2P/qR1PR2N/1PP1NPP1/1BB1K3 w - - 102 102"));
    list.add(new PgnFen("from_fen_three_moves_half_move_clock_099_white_to_move.pgn",
        "1rb1kb1r/pp1pqppp/2p5/3Bp3/PR1NP3/Bn2NQ2/2PP1PPP/5RK1 b k - 102 101"));
    list.add(new PgnFen("from_fen_two_moves_half_move_clock_100_black_to_move.pgn",
        "1k6/3RQ3/5N2/5pP1/5P2/8/PPP1P3/K7 b - - 102 101"));
    list.add(new PgnFen("from_fen_two_moves_half_move_clock_100_white_to_move.pgn",
        "1r1n1k1r/2pb1pp1/1p1nQ3/p2pp1bp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 w - - 102 101"));
    list.add(new PgnFen("from_fen_two_moves_half_move_clock_101_black_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp1bp/P3P2P/qR1P3N/1PP1RPP1/1BB1K1N1 b - - 103 101"));
    list.add(new PgnFen("from_fen_two_moves_half_move_clock_101_white_to_move.pgn",
        "1r1n1k1r/2pb1pp1/1p1n3Q/p2pp1bp/P3P2P/1R1PR2N/1PP2PP1/qBB2KN1 w - - 103 101"));
    list.add(new PgnFen("from_fen_two_moves_half_move_clock_099_black_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p5Q/p2ppnbp/P3P2P/qR1PR2N/1PP1NPP1/1BB1K3 b - - 101 101"));
    list.add(new PgnFen("from_fen_two_moves_half_move_clock_099_white_to_move.pgn",
        "1rb1kb1r/pp1pqppp/2p2Q2/3Bp3/PR1NP3/Bn2N3/2PP1PPP/5RK1 w k - 101 101"));

    // Reactivated from pgnParser/legacy/common/beyond - halfmove clocks at and above the 75-move threshold (150 / 151)
    // and sixfold-repetition variants. No longer rejected at FEN import or replay.
    list.add(new PgnFen("from_fen_capture_first_move_half_move_clock_150_black_to_move.pgn",
        "k7/2q1Q3/5N2/5pP1/5P2/8/PPP1P3/K7 w - - 0 101"));
    list.add(new PgnFen("from_fen_capture_first_move_half_move_clock_150_white_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p1Q4/p2pp1bp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 b - - 0 100"));
    list.add(new PgnFen("from_fen_capture_first_move_half_move_clock_151_black_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p5Q/p2pp1bp/P3n2P/1R1PR2N/1PP2PP1/qBB1K1N1 w - - 0 101"));
    list.add(new PgnFen("from_fen_capture_first_move_half_move_clock_151_white_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p1n4/p2pp1bQ/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 b - - 0 100"));
    list.add(new PgnFen("from_fen_capture_second_move_half_move_clock_149_black_to_move.pgn",
        "3nk2r/1rpb1pp1/1Q6/p2ppnbp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 b - - 0 101"));
    list.add(new PgnFen("from_fen_capture_second_move_half_move_clock_149_white_to_move.pgn",
        "1rbqkb1r/pp1p1ppp/8/3pp3/PR1NP3/Bn2NQ2/2PP1PPP/5R1K w k - 0 101"));
    list.add(new PgnFen("from_fen_capture_second_move_half_move_clock_150_black_to_move.pgn",
        "1k6/2R5/5N2/5pP1/5P2/Qr6/PPP1P3/K7 b - - 0 101"));
    list.add(new PgnFen("from_fen_capture_second_move_half_move_clock_150_white_to_move.pgn",
        "1r1nk2r/2pb1p2/1p1n3p/p2pp1bp/P3PN1P/1R1PR3/1PP2PP1/qBB1K1N1 w - - 0 101"));
    list.add(new PgnFen("from_fen_capture_second_move_half_move_clock_151_black_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p5Q/pR1pp1bp/P3P2P/3PR2N/1PP2PP1/qBB1K1N1 b - - 0 101"));
    list.add(new PgnFen("from_fen_capture_second_move_half_move_clock_151_white_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp2p/P3P2P/1R1PbN1N/1PP2PP1/qBB1K3 w - - 0 101"));
    list.add(new PgnFen("from_fen_one_move_half_move_clock_150_black_to_move.pgn",
        "1k6/2R1Q3/5N2/5pP1/5P2/8/PPP1P3/K7 w - - 151 101"));
    list.add(new PgnFen("from_fen_one_move_half_move_clock_150_white_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp1bp/P3PN1P/1R1PR3/1PP2PP1/qBB1K1N1 b - - 151 100"));
    list.add(new PgnFen("from_fen_one_move_half_move_clock_151_black_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p5Q/pn1pp1bp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 w - - 152 101"));
    list.add(new PgnFen("from_fen_one_move_half_move_clock_151_white_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp1bp/P3P2P/1R1PRN1N/1PP2PP1/qBB1K3 b - - 152 100"));
    list.add(new PgnFen("from_fen_pawn_first_move_half_move_clock_150_black_to_move.pgn",
        "k7/2R1Q3/5N2/6P1/5p2/5P2/PPP1P3/K7 w - - 0 101"));
    list.add(new PgnFen("from_fen_pawn_first_move_half_move_clock_150_white_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp1bp/P3P2P/1R1PR1PN/1PP2P2/qBB1K1N1 b - - 0 100"));
    list.add(new PgnFen("from_fen_pawn_first_move_half_move_clock_151_black_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p1n3Q/p3p1bp/P2pP2P/1R1PR2N/1PP2PP1/qBB1K1N1 w - - 0 101"));
    list.add(new PgnFen("from_fen_pawn_first_move_half_move_clock_151_white_to_move.pgn",
        "1r1nk2r/2pb1pp1/1p1n3Q/p2pp1bp/P3P1PP/1R1PR2N/1PP2P2/qBB1K1N1 b - g3 0 100"));
    list.add(new PgnFen("from_fen_repetition_from_one_move_black_to_move_sixfold.pgn",
        "1n2kb1r/p2q1ppp/8/4p1B1/4P3/8/PPP2PPP/2KR4 w k - 21 28"));
    list.add(new PgnFen("from_fen_repetition_from_one_move_white_to_move_sixfold.pgn",
        "r1b2r2/pp1pk1pp/8/7q/Q2pP1n1/5N1P/PP3PP1/3R1RK1 b - - 21 27"));
    list.add(new PgnFen("from_fen_repetition_from_three_moves_black_to_move_sixfold.pgn",
        "rnb1kbnr/pppp1p1p/8/4N3/2B1Pp1q/6p1/PPPP2PP/RNBQ1K1R w kq - 20 17"));
    list.add(new PgnFen("from_fen_repetition_from_three_moves_white_to_move_sixfold.pgn",
        "r4rk1/p1pq1ppp/1pn1p3/1b2P3/1Pp2Pn1/2Q3P1/PB1NP1BP/R4RK1 b - - 20 24"));
    list.add(new PgnFen("from_fen_repetition_from_two_moves_black_to_move_sixfold.pgn",
        "r2qk2r/pppbnpp1/1bn4p/4p1N1/P1BP4/2N5/5PPP/R1BQR1K1 b kq - 20 23"));
    list.add(new PgnFen("from_fen_repetition_from_two_moves_white_to_move_sixfold.pgn",
        "r1q2rk1/3b1ppp/nb1p4/1p1Pp2n/1P2P3/4BN1P/1Q2RPP1/RB3NK1 w - - 22 34"));
    list.add(new PgnFen("from_fen_repetition_from_zero_moves_black_to_move_sixfold.pgn",
        "r2q2n1/1bpk1pp1/p2b3p/1p2pN2/P2PP3/R1P1Q3/1P1N1PPP/2B2RK1 b - - 20 65"));
    list.add(new PgnFen("from_fen_repetition_from_zero_moves_white_to_move_sixfold.pgn",
        "r1bqkb1r/1pp2ppp/p1p2n2/4p2Q/P3P3/8/1PPP1PPP/RNB1K1NR w KQkq - 30 60"));
    list.add(new PgnFen("from_fen_three_moves_half_move_clock_149_black_to_move.pgn",
        "3nk2r/r1pb1pp1/1p5Q/p2ppnbp/P3P2P/1R1PR2N/1PP2PP1/qBBK2N1 w - - 152 102"));
    list.add(new PgnFen("from_fen_three_moves_half_move_clock_149_white_to_move.pgn",
        "1rb1kb1r/pp1p1ppp/2p5/3Bp3/PR1NP2q/Bn2N3/2PP1PPP/3Q1R1K b k - 152 101"));
    list.add(new PgnFen("from_fen_three_moves_half_move_clock_150_black_to_move.pgn",
        "k7/1R2Q3/5N2/5pP1/5P2/8/PPP1P3/K7 w - - 153 102"));
    list.add(new PgnFen("from_fen_three_moves_half_move_clock_150_white_to_move.pgn",
        "1r1nk2r/1npb1pp1/1p5Q/p2pp1bp/P3P2P/1R1PR3/1PP1NPP1/qBB1K1N1 b - - 153 101"));
    list.add(new PgnFen("from_fen_three_moves_half_move_clock_151_black_to_move.pgn",
        "1r1nk2r/2pb1pp1/1pQ5/p2pp1bp/P2nP2P/1R1PR2N/1PP2PP1/qBB1K1N1 w - - 154 102"));
    list.add(new PgnFen("from_fen_three_moves_half_move_clock_151_white_to_move.pgn",
        "1r2k2r/1npb1pp1/1p1n3Q/p2pp1bp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 b - - 154 101"));
    list.add(new PgnFen("from_fen_two_moves_half_move_clock_149_black_to_move.pgn",
        "3nk2r/1rpb1pp1/1p5Q/p2ppnbp/P3P2P/1R1PR2N/1PP2PP1/qBBK2N1 b - - 151 101"));
    list.add(new PgnFen("from_fen_two_moves_half_move_clock_149_white_to_move.pgn",
        "1rb1kb1r/pp1p1ppp/2p5/3Bp3/PR1NP2q/Bn2NQ2/2PP1PPP/5R1K w k - 151 101"));
    list.add(new PgnFen("from_fen_two_moves_half_move_clock_150_black_to_move.pgn",
        "1k6/1R2Q3/5N2/5pP1/5P2/8/PPP1P3/K7 b - - 152 101"));
    list.add(new PgnFen("from_fen_two_moves_half_move_clock_150_white_to_move.pgn",
        "1r1nk2r/1npb1pp1/1p5Q/p2pp1bp/P3PN1P/1R1PR3/1PP2PP1/qBB1K1N1 w - - 152 101"));
    list.add(new PgnFen("from_fen_two_moves_half_move_clock_151_black_to_move.pgn",
        "1r1nk2r/2pb1pp1/1pQ5/pn1pp1bp/P3P2P/1R1PR2N/1PP2PP1/qBB1K1N1 b - - 153 101"));
    list.add(new PgnFen("from_fen_two_moves_half_move_clock_151_white_to_move.pgn",
        "1r2k2r/1npb1pp1/1p1n3Q/p2pp1bp/P3P2P/1R1PRN1N/1PP2PP1/qBB1K3 w - - 153 101"));

    return new PgnTestCaseList(PgnTest.PARSER_FROM_FEN, list);
  }

  private static PgnTestCaseList createTestCasesBasicNoProgressSequencesWhite() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_white_from_fen_no_progress_fifty_reoccuring_fifty.pgn",
        "8/3p4/1kp1p3/8/2qQ3P/5PPK/8/8 b - - 100 150"));
    list.add(new PgnFen("02_white_from_fen_no_progress_fifty_reoccuring_seventy_five.pgn",
        "2Q5/3p1q2/2pkp3/8/7P/5PPK/8/8 b - - 150 175"));
    list.add(new PgnFen("03_white_from_fen_no_progress_fifty_reoccuring_above_fifty.pgn",
        "Q7/3p1q2/2pkp3/8/7P/5PPK/8/8 w - - 149 175"));
    list.add(new PgnFen("09_white_from_fen_no_progress_above_fifty_reoccuring_fifty.pgn",
        "8/3p4/1kp1p3/8/2qQ3P/5PPK/8/8 b - - 100 150"));
    list.add(new PgnFen("10_white_from_fen_no_progress_above_fifty_reoccuring_seventy_five.pgn",
        "2Q5/3p1q2/2pkp3/8/7P/5PPK/8/8 b - - 150 175"));
    list.add(new PgnFen("11_white_from_fen_no_progress_above_fifty_reoccuring_above_fifty.pgn",
        "Q7/3p1q2/2pkp3/8/7P/5PPK/8/8 w - - 149 175"));

    return new PgnTestCaseList(PgnTest.BASIC_REPORT_NO_PROGRESS_SEQUENCES_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesBasicNoProgressSequencesBlack() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_black_from_fen_no_progress_fifty_reoccuring_fifty.pgn",
        "8/6p1/7k/6n1/2NQ4/4q3/2P1P3/1K6 w - - 100 151"));
    list.add(new PgnFen("02_black_from_fen_no_progress_fifty_reoccuring_seventy_five.pgn",
        "8/6pk/8/6n1/2NQ4/4q3/1KP1P3/8 w - - 150 176"));
    list.add(new PgnFen("03_black_from_fen_no_progress_fifty_reoccuring_above_fifty.pgn",
        "8/6pk/8/4q1n1/2NQ4/8/1KP1P3/8 b - - 149 175"));
    list.add(new PgnFen("09_black_from_fen_no_progress_above_fifty_reoccuring_fifty.pgn",
        "8/6p1/7k/6n1/2NQ4/4q3/2P1P3/1K6 w - - 100 151"));
    list.add(new PgnFen("10_black_from_fen_no_progress_above_fifty_reoccuring_seventy_five.pgn",
        "8/6pk/8/6n1/2NQ4/4q3/1KP1P3/8 w - - 150 176"));
    list.add(new PgnFen("11_black_from_fen_no_progress_above_fifty_reoccuring_above_fifty.pgn",
        "8/6pk/8/4q1n1/2NQ4/8/1KP1P3/8 b - - 149 175"));

    return new PgnTestCaseList(PgnTest.BASIC_REPORT_NO_PROGRESS_SEQUENCES_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesLongestPossible() {
    final List<PgnFen> list = new ArrayList<>();

    // longest possible game seventy-five-move rule
    list.add(new PgnFen("longest_possible_tom_murphy.pgn", "3k4/3Q4/4K3/8/8/8/8/8 b - - 150 8849"));
    // longest possible game fifty-five-move rule
    list.add(new PgnFen("longest_possible_francois_labelle.pgn", "8/8/8/5K2/6Qk/8/8/8 b - - 150 8849"));

    return new PgnTestCaseList(PgnTest.MAX_MOVES, list);
  }

  private static PgnTestCaseList createTestCasesRandomNoRepetition() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("05_random_no_repetition.pgn", "4k3/8/3B4/8/8/8/4q3/K7 b - - 79 182"));

    // Reactivated from pgnParser/legacy/common/beyond - long random games crossing 75-move.
    list.add(new PgnFen("01_random_no_repetition.pgn", "7K/8/8/8/8/1R6/8/k7 w - - 1968 1146"));
    list.add(new PgnFen("02_random_no_repetition.pgn", "8/8/8/8/1k6/8/4r3/7K b - - 1400 880"));
    list.add(new PgnFen("03_random_no_repetition.pgn", "7k/3K4/8/8/8/6R1/8/8 w - - 704 458"));
    list.add(new PgnFen("04_random_no_repetition.pgn", "8/8/8/8/4K3/8/4R3/7k w - - 156 325"));

    return new PgnTestCaseList(PgnTest.RANDOM_NO_REPETITION, list);
  }

  private static PgnTestCaseList createTestCasesRandomCheckmate() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(
        new PgnFen("01_random_checkmate.pgn", "3qkbnr/pb1pp2p/p1r2p2/2p1P1pQ/7P/2P4N/PPNP1PP1/1RB1K2R b k - 3 12"));
    list.add(new PgnFen("02_random_checkmate.pgn", "5k1Q/R4P2/8/p7/7P/4p2N/7K/3r4 b - - 2 82"));
    list.add(new PgnFen("03_random_checkmate.pgn", "2b5/8/K7/2q4p/5k2/8/8/8 w - - 13 180"));
    list.add(new PgnFen("04_random_checkmate.pgn", "5k2/8/1b6/7K/4p3/3R2q1/p6r/8 w - - 7 119"));
    list.add(new PgnFen("05_random_checkmate.pgn", "4K3/4q3/3k4/8/8/7N/8/8 w - - 8 215"));

    return new PgnTestCaseList(PgnTest.RANDOM_CHECKMATE, list);
  }

  private static PgnTestCaseList createTestCasesRandomStalemate() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(new PgnFen("01_random_stalemate.pgn", "K7/8/1q4k1/8/8/8/8/8 w - - 93 233"));
    list.add(new PgnFen("02_random_stalemate.pgn", "k7/8/1Q6/3K4/p7/P1B5/8/8 b - - 92 191"));
    list.add(new PgnFen("03_random_stalemate.pgn", "5K2/3k1P2/p7/6r1/8/8/8/8 w - - 1 122"));
    list.add(new PgnFen("04_random_stalemate.pgn", "6k1/6P1/p3N2P/P1p5/2P1B3/N2K4/7P/R4R2 b - - 2 46"));
    list.add(new PgnFen("05_random_stalemate.pgn", "8/8/6p1/3b2P1/8/8/2k5/K7 w - - 72 250"));

    return new PgnTestCaseList(PgnTest.RANDOM_STALEMATE, list);
  }

  private static PgnTestCaseList createTestCasesRandomInsufficientMaterial() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(new PgnFen("01_random_insufficient_material.pgn", "8/5k2/8/8/8/8/8/2K5 b - - 0 153"));
    list.add(new PgnFen("02_random_insufficient_material.pgn", "8/7k/8/8/8/8/6K1/8 b - - 0 191"));
    list.add(new PgnFen("03_random_insufficient_material.pgn", "8/8/8/8/8/8/1K6/5k2 w - - 0 177"));
    list.add(new PgnFen("04_random_insufficient_material.pgn", "8/8/4K3/8/5k2/8/8/8 w - - 0 232"));
    list.add(new PgnFen("05_random_insufficient_material.pgn", "8/6k1/8/4N3/8/8/2K5/8 b - - 0 204"));

    return new PgnTestCaseList(PgnTest.RANDOM_INSUFFICIENT_MATERIAL, list);
  }

  private static PgnTestCaseList createTestCasesRandomFifty() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(new PgnFen("01_random_fifty.pgn", "8/1k6/8/2B5/8/8/8/1b4K1 b - - 100 196"));
    list.add(new PgnFen("02_random_fifty.pgn", "8/8/n7/3K4/3R4/8/5k2/8 w - - 100 214"));
    list.add(new PgnFen("03_random_fifty.pgn", "8/2K5/6k1/8/8/8/8/1q6 w - - 100 176"));
    list.add(new PgnFen("04_random_fifty.pgn", "8/k7/8/5N2/8/7K/6N1/8 w - - 100 229"));
    list.add(new PgnFen("05_random_fifty.pgn", "8/5k2/8/K7/8/8/1B1n4/8 w - - 100 187"));

    return new PgnTestCaseList(PgnTest.RANDOM_FIFTY, list);
  }

  private static PgnTestCaseList createTestCasesRandomSeventyFive() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_random_seventy_five.pgn", "8/5B2/K2k4/7p/7P/8/8/8 w - - 150 251"));
    list.add(new PgnFen("02_random_seventy_five.pgn", "8/8/K7/8/8/8/5R2/k7 w - - 150 273"));
    list.add(new PgnFen("03_random_seventy_five.pgn", "8/8/3q3k/8/8/8/8/2K5 w - - 150 237"));
    list.add(new PgnFen("04_random_seventy_five.pgn", "2K5/2N5/8/8/8/8/2kn4/8 w - - 150 207"));

    return new PgnTestCaseList(PgnTest.RANDOM_SEVENTY_FIVE, list);
  }

  private static PgnTestCaseList createTestCasesRandomThreefold() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(new PgnFen("01_random_threefold.pgn", "8/7K/k7/p6p/P6P/5p2/5b2/8 w - - 27 120"));
    list.add(new PgnFen("02_random_threefold.pgn", "8/5K2/2k5/1p6/pP6/P7/8/8 b - - 99 201"));
    list.add(new PgnFen("03_random_threefold.pgn", "k7/8/8/P7/8/4K3/8/8 b - - 40 171"));
    list.add(new PgnFen("04_random_threefold.pgn", "8/6k1/8/5R2/6K1/8/8/8 b - - 96 193"));
    list.add(new PgnFen("05_random_threefold.pgn", "8/8/8/1K6/8/4k3/7p/8 w - - 26 203"));

    return new PgnTestCaseList(PgnTest.RANDOM_THREEFOLD, list);
  }

  private static PgnTestCaseList createTestCasesRandomFivefold() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(new PgnFen("01_random_fivefold.pgn", "8/8/k7/p7/P7/8/1K6/8 w - - 89 194"));
    list.add(new PgnFen("02_random_fivefold.pgn", "2r5/1k6/8/8/8/8/1K6/8 w - - 71 262"));

    return new PgnTestCaseList(PgnTest.RANDOM_FIVEFOLD, list);
  }

  private static PgnTestCaseList createTestCasesVarious() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(new PgnFen("various_almtwali_vs_danielbaechli_2020.pgn", "6k1/4P2p/5K1P/8/8/8/8/8 b - - 0 114"));
    list.add(new PgnFen("various_krush_zatonski_2008.pgn", "6rk/1p1R1pqp/p1p4p/6rP/P7/5QP1/1P3P2/4R1K1 b - - 3 33"));
    list.add(new PgnFen("various_krush_zatonski_2008_reconstructed.pgn",
        "r5qk/1R2Rprp/5Q1p/p6P/P7/2P3P1/5P2/6K1 w - - 11 47"));
    list.add(new PgnFen("various_rikikits_vs_demchenko_2016_amended.pgn", "7B/5p2/2k5/K3P3/1P6/3b4/8/8 b - - 103 163"));
    list.add(new PgnFen("various_demchenko_vs_verdenotte_2018_amended.pgn", "8/8/1r6/3B4/1k1K4/8/R7/8 w - - 107 142"));
    list.add(
        new PgnFen("various_demchenko_vs_chesspanda123_2017_amended.pgn", "6R1/8/8/8/4B3/4K3/5r2/6k1 b - - 150 136"));
    list.add(new PgnFen("various_savic_vs_bueble_2020.pgn", "5k2/8/6K1/8/8/8/8/8 w - - 0 92"));
    list.add(new PgnFen("various_blatny_holzke_1997.pgn", "8/3R2bp/4n1p1/1p1k1pP1/5P1P/1K2P3/8/8 b - - 24 64"));
    list.add(
        new PgnFen("various_gvetadze_milliet_2014.pgn", "2q2rk1/2r1b3/1p2p1Q1/4Pp2/8/3B4/1P3PPP/3R2K1 b - - 9 29"));
    // O-O-O+ was played in this game!!!
    list.add(new PgnFen("various_hikaru_vs_penguingm1_2014.pgn", "2k5/1pp1r3/p5pp/2P5/1P6/P2pP1PP/4PK2/8 w - - 0 36"));

    // "threefold repetition" with initial en passant
    list.add(new PgnFen("various_gmjoey1_vs_bugsbunny444_2013.pgn",
        "rn1q1rk1/pp1b4/4p1Q1/bN1pPp2/3P4/PpPB4/5PPP/R3K2R b KQ - 9 22"));
    list.add(
        new PgnFen("various_gmjoey1_vs_tiohoracio_2015.pgn", "r1b2r2/1p3kpQ/4p3/4Pp2/p6R/2q5/P5PP/1R5K w - - 10 28"));
    list.add(
        new PgnFen("various_gmjoey1_vs_dulerile_2018.pgn", "2R5/1k3p2/1p4p1/pP1b3p/P4B1P/6P1/1r6/5K2 w - - 10 50"));

    // checkmating with castling
    list.add(new PgnFen("various_lasker_vs_alan_thomas_1912.pgn",
        "rn3r2/pbppq1p1/1p2pN2/8/3P2NP/6P1/PPP1BP1R/2KR2k1 b - - 6 18"));

    // fooling around as long as I could
    list.add(new PgnFen("various_mickeymousetest_donaldducktest_2021.pgn",
        "rnbq1b1r/4k3/8/7N/4P2p/4n3/8/R1BQKBNR w - - 100 389"));

    list.add(new PgnFen("various_jobava_so_2017.pgn", "8/R3k3/8/6N1/5p1P/P1Pr4/1P2r3/2K5 b - - 8 41"));

    // fifty-move claim but only 67 half-moves
    list.add(new PgnFen("various_gunina_harika_2019.pgn", "R7/1n6/2K5/8/8/8/2k5/8 b - - 67 101"));

    // knight-bishop checkmate in blitz unsuccessful

    // 75-move-rule exceeded by 116.5 full moves - no arbiter call
    list.add(new PgnFen("various_anikonov_zhigalko_2018.pgn", "2R5/4r3/8/1k1n4/8/5K2/8/8 w - - 126 167"));

    // KRvKRB played over fifty moves, under seventy-five moves

    // KRvKRN - white about being mated lost on time
    list.add(new PgnFen("various_harikrishna_hovhannisyan_2018.pgn", "8/K1k5/3n1R2/8/7r/8/8/8 w - - 91 128"));

    // KvKNN - unclear why game was stopped
    list.add(new PgnFen("various_bazeev_riazantsev_2018.pgn", "5n2/5k1K/8/8/6n1/8/8/8 w - - 102 110"));

    // Blitz, was it fivefold? No, only threefold
    list.add(new PgnFen("various_khademalsharieh_bodnaruk_2018.pgn", "4Q3/3K4/6k1/8/5R2/8/8/4r3 b - - 0 109"));

    // we need more skillful arbiters
    list.add(new PgnFen("various_grischuk_mamedyarov_2017.pgn", "8/8/8/8/4p3/4Bk1K/5Pr1/8 w - - 2 88"));

    // youtube video - why savic so angry?
    list.add(new PgnFen("various_pranav_savic_2021_incomplete_speculative.pgn",
        "8/5k2/2R5/5p2/5P2/6P1/6K1/r7 w - - 73 101"));
    list.add(new PgnFen("various_pranav_savic_2021_incomplete_speculative_from_last_capture.pgn",
        "8/5k2/2R5/5p2/5P2/6P1/6K1/r7 w - - 73 101"));

    // repetition after promotion
    list.add(new PgnFen("various_keres_fischer_1962.pgn", "8/p4Q1k/1p6/1P6/8/7K/8/6q1 b - - 5 77"));
    list.add(new PgnFen("various_keres_fischer_1962_changed.pgn", "5Q1k/p7/1p6/1P6/8/7K/8/6q1 b - - 11 80"));

    // Lichess issue - game import
    list.add(new PgnFen("various_swiercz_karjakin_2015.pgn", "5R2/p3r3/P1R1pkp1/5p1p/5P1P/r5P1/4PK2/8 b - - 9 46"));

    // the threefold I did not see
    list.add(
        new PgnFen("various_gwendolus_vs_danielbaechli_2022.pgn", "2k1r3/1p6/7R/3p4/1P1Bn3/2P2K2/P7/8 b - - 10 54"));

    // import on Lichess did not work
    list.add(new PgnFen("various_tal_bronstein_1961.pgn", "8/5p2/1p2p2p/6pk/r6q/4Q3/5P2/2R3K1 w - - 6 41"));

    list.add(new PgnFen("various_gundavaa_tari_2022.pgn", "R7/2q2p2/5k1Q/pp2r3/2p1P1P1/7P/6K1/8 b - - 13 42"));

    return new PgnTestCaseList(PgnTest.VARIOUS, list);
  }

  private static PgnTestCaseList createTestCasesWcc201() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("wcc_2021_round_01_nepomniachtchi_carlsen.pgn",
        "8/4r2p/p2rnkp1/2p2p2/2N4P/2PP1P2/2K2P2/R3R3 b - - 14 45"));
    list.add(new PgnFen("wcc_2021_round_01_nepomniachtchi_carlsen_amended.pgn",
        "8/4r2p/p1r1nkp1/2p2p2/2N4P/2PP1P2/2K2P2/R3R3 w - - 15 46"));
    list.add(new PgnFen("wcc_2021_round_02_carlsen_nepomniachtchi.pgn", "8/6kp/7R/6Pp/r4P2/8/5K2/8 w - - 9 59"));
    list.add(new PgnFen("wcc_2021_round_03_nepomniachtchi_carlsen.pgn",
        "8/3b4/7p/p1p1k3/P1p2p1P/2P2P2/2B2KP1/8 b - - 6 41"));
    list.add(new PgnFen("wcc_2021_round_04_carlsen_nepomniachtchi.pgn",
        "r4nk1/4Rp1p/1p3Np1/1P1P2P1/8/p2r4/5P1P/R5K1 b - - 7 33"));
    list.add(new PgnFen("wcc_2021_round_04_carlsen_nepomniachtchi_amended.pgn",
        "r4n2/4Rpkp/1p3Np1/1P1P2P1/8/p2r4/5P1P/R5K1 w - - 8 34"));
    list.add(new PgnFen("wcc_2021_round_05_nepomniachtchi_carlsen.pgn",
        "4kb2/2p3p1/4np1p/4pN1P/4P1P1/2P1BP2/4K3/r2R4 b - - 14 43"));
    list.add(new PgnFen("wcc_2021_round_05_nepomniachtchi_carlsen_amended.pgn",
        "4kb2/2p3p1/4np1p/4pN1P/4P1P1/2P1BP2/r3K3/3R4 w - - 15 44"));
    list.add(new PgnFen("wcc_2021_round_06_carlsen_nepomniachtchi.pgn", "3k4/5RN1/4P3/5P2/7K/8/8/6q1 b - - 2 136"));
    list.add(new PgnFen("wcc_2021_round_07_nepomniachtchi_carlsen.pgn", "8/R4pk1/6p1/7p/7P/2r3P1/5PK1/8 b - - 15 41"));
    list.add(new PgnFen("wcc_2021_round_08_carlsen_nepomniachtchi.pgn", "8/7k/8/3P4/6P1/1P2qQ1p/6P1/7K b - - 1 46"));
    list.add(new PgnFen("wcc_2021_round_09_nepomniachtchi_carlsen.pgn",
        "r3b1k1/P4pp1/4p3/2N1P2p/7P/5Pn1/8/R5K1 w - - 1 40"));
    list.add(
        new PgnFen("wcc_2021_round_10_carlsen_nepomniachtchi.pgn", "8/pp6/2pk1n2/3pN3/3P4/2P1K3/PP6/8 b - - 1 41"));
    list.add(new PgnFen("wcc_2021_round_11_nepomniachtchi_carlsen.pgn", "K7/5pk1/1R4p1/P1q5/5P2/4P3/8/8 w - - 7 50"));

    return new PgnTestCaseList(PgnTest.WCC2021, list);
  }

  private static PgnTestCaseList createTestCasesFivefoldCorrect() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("fivefold_correct_akshat_li_2018.pgn", "8/Pk6/5P2/1K1p3p/3Bp2P/4P3/8/2r5 w - - 32 76"));

    list.add(new PgnFen("fivefold_correct_miton_yakubboev_2018.pgn", "8/p7/1p2k2p/1P1r4/P1pK4/4P2P/8/2R5 w - - 23 47"));
    list.add(new PgnFen("fivefold_correct_potapov_adly_2018.pgn", "8/6rk/7p/p4Q2/8/Pr5P/3n1P2/3N3K b - - 24 51"));
    list.add(
        new PgnFen("fivefold_correct_sethuraman_guseinov_2017.pgn", "1r6/1P3Rkp/3Np3/4P3/1r4p1/8/7P/7K b - - 16 59"));
    list.add(new PgnFen("fivefold_correct_paichadze_ter-sahakyan_2019.pgn", "8/8/8/4k3/4r3/4KR2/8/8 w - - 18 67"));
    list.add(new PgnFen("fivefold_correct_robson_moranda_2019.pgn", "5k2/R5R1/3pp2p/4p3/4P2P/1r1r2PK/8/8 w - - 17 43"));
    return new PgnTestCaseList(PgnTest.FIVEFOLD_CORRECT, list);
  }

  private static PgnTestCaseList createTestCasesFiftyGeneral() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(new PgnFen("fifty_general_adly_flores_2016.pgn", "1R2k3/8/4K3/8/8/8/8/8 b - - 0 144"));
    list.add(new PgnFen("fifty_general_cheparinov_anand_2018.pgn", "8/8/8/2KB4/k7/r7/8/1R6 b - - 138 130"));
    list.add(new PgnFen("fifty_general_giri_grischuk_2014.pgn", "8/8/3P1k2/3K4/8/8/8/8 b - - 1 156"));
    list.add(new PgnFen("fifty_general_carlsen_liem_2014.pgn", "8/5p2/Bp2pP1k/1p2P3/2b2K2/P1P5/8/8 b - - 102 103"));
    list.add(new PgnFen("fifty_general_pruijssers_inarkiev_2015.pgn", "8/8/8/7P/8/6Pk/6r1/2R2K1r w - - 11 128"));
    list.add(new PgnFen("fifty_general_riazantsev_levin_2016.pgn", "5Rqk/8/6PP/8/5P2/5NK1/8/8 b - - 4 181"));
    list.add(new PgnFen("fifty_general_radjabov_deac_2016.pgn", "8/8/8/4k3/4pP2/4P1K1/r6R/8 b - f3 0 127"));
    list.add(new PgnFen("fifty_general_alekseenko_grachev_2017.pgn", "8/2kB1K2/4P3/4P3/4p3/2P5/1P3q2/8 w - - 2 126"));
    list.add(new PgnFen("fifty_general_vaisser_karpov_2017.pgn", "6r1/8/7R/k7/2K5/2B5/8/8 b - - 140 152"));
    list.add(new PgnFen("fifty_general_yu_korobov_2017.pgn", "8/8/8/8/k1K5/8/1R3b2/8 b - - 126 123"));
    list.add(new PgnFen("fifty_general_yu_shimanov_2018.pgn", "8/8/8/8/8/5k2/6p1/6K1 w - - 0 163"));
    list.add(new PgnFen("fifty_general_ju_sebenik_2018.pgn", "8/8/8/8/k7/6Kp/5R1P/5b2 b - - 129 128"));
    list.add(new PgnFen("fifty_general_wang_wen_2018.pgn", "8/8/8/8/4BK2/1r6/7k/2R5 b - - 148 169"));
    list.add(new PgnFen("fifty_general_meier_dominguez_2018.pgn", "5R2/5P1k/3B3p/3K3P/8/8/8/8 b - - 0 115"));
    list.add(new PgnFen("fifty_general_bindrich_kovalev_2018.pgn", "8/8/8/6k1/1B5p/5K1P/7r/8 b - - 3 121"));
    list.add(new PgnFen("fifty_general_jobava_salem_2018.pgn",
        "4k3/1q1b4/2p1p3/2p1Pp2/1BPp1Pp1/1PbP2Pp/2NN3P/2KQ4 b - - 0 93"));
    list.add(new PgnFen("fifty_general_sarana_chirila_2019.pgn", "8/7p/4k3/3NPb1P/3K4/8/8/8 b - - 66 150"));
    list.add(new PgnFen("fifty_general_topalov_dominguez_2019.pgn", "8/4p3/3p1k2/3P4/2P2pQ1/5P1K/8/7q w - - 15 109"));
    return new PgnTestCaseList(PgnTest.FIFTY_GENERAL, list);
  }

  private static PgnTestCaseList createTestCasesFiftyPattern() {
    final List<PgnFen> list = new ArrayList<>();

    // because below 100 not displayed
    // 55.Nc4 (1) 103...Kh5 (98)
    list.add(new PgnFen("fifty_pattern_098_start_white_demchenko_martinez_2019.pgn",
        "8/8/4p3/6nk/6p1/4N1P1/5PK1/8 w - - 98 104"));

    // because below 100 not displayed
    // 68...Rh2 (1) 117.Kd3 (98)
    list.add(new PgnFen("fifty_pattern_098_start_black_sjugirov_inarkiev_2019.pgn",
        "8/8/8/8/r7/3K4/1R3N2/6k1 b - - 98 117"));

    // because below 100 not displayed
    // 115.Kg2 (1) 163...Kc5 (98); 164.Kh1 (99)
    list.add(new PgnFen("fifty_pattern_099_start_white_so_nakamura_2018.pgn", "8/8/8/2k1b3/8/8/7p/7K b - - 99 164"));

    // because below 100 not displayed
    // 59...Bg6 (1) 108.Nb5 (98); 108...Bh5 (99)
    list.add(new PgnFen("fifty_pattern_099_start_black_naroditsky_boruchovsky_2018.pgn",
        "8/6K1/5P2/1N2k2b/8/8/8/8 w - - 99 109"));

    list.add(new PgnFen("fifty_pattern_100_start_white_baryshpolets_steingrimsson_2019.pgn",
        "1k6/4K3/6Q1/4q3/5n2/8/8/8 w - - 100 127"));

    list.add(new PgnFen("fifty_pattern_100_start_black_nakamura_radjabov_2014.pgn",
        "1kbb1r1r/2p2pp1/1p6/p1pNPn2/P1P2P1p/1P1R1N1P/5BPK/3R4 b - - 100 77"));

    list.add(new PgnFen("fifty_pattern_101_start_white_nakamura_vachier-lagrave_2018.pgn",
        "8/5K2/8/6Bp/4p2P/4Pb2/8/5k2 b - - 101 122"));

    list.add(new PgnFen("fifty_pattern_101_start_black_sevian_andriasian_2019.pgn",
        "2k5/7R/2K5/4N3/8/8/8/2r5 w - - 101 125"));

    list.add(new PgnFen("fifty_pattern_101_start_black_grischuk_vachier-lagrave_2017.pgn",
        "R7/8/3kp3/3p4/3Pr3/3KP3/8/8 w - - 101 90"));

    list.add(
        new PgnFen("fifty_pattern_102_start_white_harikrishna_yu_2017.pgn", "8/8/1R1b3K/4k3/8/8/8/6r1 w - - 102 103"));

    list.add(
        new PgnFen("fifty_pattern_102_start_black_riazantsev_deac_2017.pgn", "8/8/8/3nk3/R7/5K2/8/8 b - - 102 142"));

    return new PgnTestCaseList(PgnTest.FIFTY_PATTERN, list);
  }

  private static PgnTestCaseList createTestCasesSeventyFiveCorrect() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(
        new PgnFen("seventy_five_correct_firouzja_demchenko_2019.pgn", "8/6k1/4R2p/4K1pP/5rP1/5P2/8/8 w - - 150 127"));
    list.add(new PgnFen("seventy_five_correct_topalov_nakamura_2016.pgn", "6r1/8/8/8/5k2/R6K/3n4/8 w - - 150 133"));
    list.add(
        new PgnFen("seventy_five_correct_yudasin_erenburg_2017.pgn", "4B3/4n3/8/2k2p2/6p1/4K1P1/8/8 w - - 150 198"));
    return new PgnTestCaseList(PgnTest.SEVENTY_FIVE_CORRECT, list);
  }

  private static PgnTestCaseList createTestCasesEarlyDraw() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(
        new PgnFen("early_draw_grischuk_giri_2019.pgn", "r4rk1/p4pp1/np2p3/q5N1/3PP3/P7/5P1P/R2Q1KR1 b - - 10 22"));
    list.add(new PgnFen("early_draw_karjakin_nepomniachtchi_2019.pgn",
        "rn1r2k1/1pqbppbp/2p2np1/pN6/8/4BNP1/PPQ1PPBP/R2R2K1 b - - 13 21"));
    list.add(new PgnFen("early_draw_vachier-lagrave_aronian_2019.pgn",
        "5rk1/1np1q1pp/p1n1p3/Pr2p3/1pNpP3/1N1P3P/1PP2PP1/R2QR1K1 b - - 13 25"));
    list.add(new PgnFen("early_draw_vachier-lagrave_ding_liren_2019.pgn",
        "r4rk1/bpp1n1pp/p2pp1q1/4p3/PP2Pn2/N1PPBN1P/R4PPK/3QR3 w - - 19 21"));
    return new PgnTestCaseList(PgnTest.EARLY_DRAW, list);
  }

  private static PgnTestCaseList createTestCasesWikipediaThreefold() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(new PgnFen("wikipedia_threefold_2_0_1_spassky_fischer_1972_seventeenth.pgn",
        "8/1p2ppk1/p1np4/6p1/2R1P3/1P4KP/P1R1r1P1/8 b - - 7 45"));
    list.add(new PgnFen("wikipedia_threefold_2_0_1_spassky_fischer_1972_seventeenth_changed.pgn",
        "8/1p2ppk1/p1np4/6p1/2R1P3/1P4KP/P1R3P1/4r3 w - - 8 46"));
    list.add(new PgnFen("wikipedia_threefold_2_0_2_fischer_spassky_1972_eighteenth.pgn",
        "2r5/5R1Q/1kqr1p2/4p3/pP6/Pp4P1/1P5P/KR6 w - - 21 48"));
    list.add(new PgnFen("wikipedia_threefold_2_0_2_fischer_spassky_1972_eighteenth_changed.pgn",
        "2r5/5R2/1kqr1p1Q/4p3/pP6/Pp4P1/1P5P/KR6 b - - 22 48"));
    list.add(new PgnFen("wikipedia_threefold_2_1_fischer_petrosian_1971.pgn",
        "8/pp3p1k/2p2q1p/3r1P2/5R2/7P/P1P1QP2/7K b - - 10 34"));
    list.add(new PgnFen("wikipedia_threefold_2_2_1_ponomariov_adams_2005_wijk_aan_zee.pgn",
        "7k/6p1/1p1p4/nP1p3p/3P4/R1P3rP/4K3/5B2 w - - 8 42"));
    list.add(new PgnFen("wikipedia_threefold_2_2_1_ponomariov_adams_2005_wijk_aan_zee_changed.pgn",
        "7k/6p1/1p1p4/nP1p3p/3P4/R1P3rP/3K4/5B2 b - - 9 42"));
    list.add(new PgnFen("wikipedia_threefold_2_2_2_adams_ponomariov_2005_sofia.pgn",
        "r7/p1r2ppp/6k1/3p4/6R1/4RP2/PP3P1P/1K6 b - - 10 27"));
    list.add(new PgnFen("wikipedia_threefold_2_2_2_adams_ponomariov_2005_sofia_changed.pgn",
        "r7/p1r2ppp/5k2/3p4/6R1/4RP2/PP3P1P/1K6 w - - 11 28"));
    list.add(new PgnFen("wikipedia_threefold_2_2_3_ponomariov_adams_2005_sofia.pgn",
        "3Q4/5pk1/7R/1p3Pp1/1P6/7P/6P1/4q2K w - - 7 53"));
    list.add(new PgnFen("wikipedia_threefold_2_2_3_ponomariov_adams_2005_sofia_changed.pgn",
        "3Q4/5pk1/7R/1p2qPp1/1P6/7P/6PK/8 w - - 9 54"));
    list.add(new PgnFen("wikipedia_threefold_2_3_capablanca_lasker_1921.pgn",
        "1Q3k2/p4p2/1p6/7R/3q4/1P2n3/P7/6K1 b - - 7 46"));
    list.add(new PgnFen("wikipedia_threefold_2_4_1_alekhine_lasker_1914.pgn",
        "r2q1r1k/p1p1b3/4pnQp/3p4/8/2NB4/PPP2PPP/R5K1 b - - 3 16"));
    list.add(new PgnFen("wikipedia_threefold_2_4_1_alekhine_lasker_1914_changed.pgn",
        "r3qr1k/p1p1b3/4pn1Q/3p4/8/2NB4/PPP2PPP/R5K1 b - - 8 21"));
    list.add(new PgnFen("wikipedia_threefold_2_4_2_lasker_alekhine_1914.pgn",
        "7r/2p1bppp/2p1k3/2P1n3/3QN3/5q2/PP3P1P/R1BR2K1 b - - 11 25"));

    list.add(new PgnFen("wikipedia_threefold_2_5_korchnoi_portisch_1970_game_1.pgn",
        "1R6/5nk1/3r4/4p1pK/4P2p/7N/8/8 b - - 19 68"));
    list.add(new PgnFen("wikipedia_threefold_2_5_korchnoi_portisch_1970_game_2.pgn",
        "2rr1k2/1b3pp1/pp2pq1p/2bP4/P7/2N5/BP2QPPP/3RR1K1 w - - 9 32"));
    list.add(new PgnFen("wikipedia_threefold_2_5_korchnoi_portisch_1970_game_3.pgn",
        "3N2k1/8/8/pPn4p/1p5P/8/6P1/6K1 w - - 1 64"));
    list.add(new PgnFen("wikipedia_threefold_2_5_portisch_korchnoi_1970_game_4.pgn",
        "2b1nrk1/p2p1npp/2q1p3/2N5/5P2/P5P1/1P3QBP/R3K2R b KQ - 10 25"));
    list.add(new PgnFen("wikipedia_threefold_2_5_portisch_korchnoi_1970_game_4_changed.pgn",
        "2b1nrk1/p2p1npp/4p3/1qN5/5P2/P5P1/1P3QBP/R3K2R w KQ - 11 26"));

    list.add(new PgnFen("wikipedia_threefold_2_6_kasparov_deep_blue_1997.pgn",
        "8/pp4P1/8/8/1kp2N2/1n2R1P1/3r4/1K6 w - - 1 50"));
    list.add(new PgnFen("wikipedia_threefold_2_6_kasparov_deep_blue_1997_changed.pgn",
        "6Q1/pp6/8/8/1kp2N2/1n2R1P1/3r4/1K6 b - - 8 54"));
    list.add(new PgnFen("wikipedia_threefold_2_7_1_giuoco_piano.pgn",
        "r1bqrknQ/ppp3pB/3p1p2/3P4/6PR/8/PP3P1P/R5K1 b - - 10 22"));
    list.add(new PgnFen("wikipedia_threefold_2_7_1_giuoco_piano_changed.pgn",
        "r1bqrknQ/ppp3pB/3p1p2/3P4/6PR/8/PP3P1P/R5K1 b - - 14 24"));
    list.add(new PgnFen("wikipedia_threefold_2_7_2_pirc_defense.pgn",
        "rn1Nk2r/pp2p2p/3p2p1/1bp5/5Pn1/2N5/PPP2bPP/R1BQK2R w kq - 5 14"));
    list.add(new PgnFen("wikipedia_threefold_2_7_2_pirc_defense_changed.pgn",
        "rn1Nk2r/pp2p2p/3p2p1/1bp5/5Pn1/2N5/PPPK1bPP/R1BQ3R b kq - 10 16"));
    list.add(new PgnFen("wikipedia_threefold_3_1_karpov_miles_1986.pgn",
        "r3kb1r/5ppp/4p3/1N6/4P3/8/Pn1BK1PP/R6R b k - 9 26"));
    list.add(new PgnFen("wikipedia_threefold_3_1_karpov_miles_1986_changed.pgn",
        "4kb1r/5ppp/4p3/1N6/r3P3/8/Pn1BK1PP/R6R w k - 10 27"));
    list.add(new PgnFen("wikipedia_threefold_3_2_fischer_spassky_1972_twentieth.pgn",
        "8/8/3k2b1/1p2p2p/p2n2p1/P1K1N1P1/1PP4P/4N3 w - - 30 55"));
    list.add(new PgnFen("wikipedia_threefold_3_2_fischer_spassky_1972_twentieth_changed.pgn",
        "8/8/3k2b1/1p2p2p/p2n2p1/P1K1N1P1/1PP4P/4N3 b - - 37 58"));
    list.add(new PgnFen("wikipedia_threefold_4_1_pillsbury_burn_1898.pgn", "8/5Q2/8/P6p/7P/1q2p1P1/kp5K/8 w - - 2 91"));

    // Reactivated from pgnParser/legacy/common/beyond - Wikipedia 3-fold examples that actually play past 5-fold.
    list.add(new PgnFen("wikipedia_threefold_4_0_1_pest_paris.pgn",
        "1r3rk1/p1b3pp/1np5/2N5/2pP4/2Nb4/PP2RPPP/R1B3K1 w - - 23 28"));
    list.add(new PgnFen("wikipedia_threefold_4_0_1_pest_paris_six_fold.pgn",
        "1r3rk1/p1b3pp/1np5/5b2/2pPN3/2N5/PP2RPPP/R1B3K1 w - - 25 29"));

    return new PgnTestCaseList(PgnTest.WIKIPEDIA_THREEFOLD, list);
  }

  private static PgnTestCaseList createTestCasesWikipediaFiftyMove() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(new PgnFen("wikipedia_fifty_move_1_filipowicz_smederevac_1966.pgn",
        "4q3/r1br1nk1/1pb1p1p1/p1pnPp1p/P2p1P1P/NP1P1BPN/2PB3K/R3R2Q w - - 100 71"));
    list.add(new PgnFen("wikipedia_fifty_move_2_1_timman_lutz_1995.pgn", "8/7k/8/1r3KR1/5B2/8/8/8 w - - 105 122"));
    list.add(new PgnFen("wikipedia_fifty_move_2_2_karpov_kasparov_1991.pgn", "7k/4NK2/5r2/5BN1/8/8/8/8 w - - 103 115"));
    list.add(
        new PgnFen("wikipedia_fifty_move_2_3_lputian_harutjunyan_2001.pgn", "7k/5q2/5Q1P/6P1/8/6K1/8/8 b - - 112 142"));
    list.add(new PgnFen("wikipedia_fifty_move_2_4_nguyen_vachier-lagrave_2008.pgn",
        "4R3/kr6/2K5/2B5/8/8/8/8 b - - 100 121"));
    return new PgnTestCaseList(PgnTest.WIKIPEDIA_FIFTY_MOVE, list);
  }

  private static PgnTestCaseList createTestCasesSequence() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(new PgnFen("01_sequence.pgn", "rnbqkb1r/pppppppp/5n2/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 11 6"));
    list.add(new PgnFen("02_sequence.pgn", "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 12 7"));
    list.add(new PgnFen("03_sequence.pgn", "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 16 9"));
    return new PgnTestCaseList(PgnTest.BASIC_REPORT_REPETITION, list);
  }

  private static PgnTestCaseList createTestBasicCaptureLastMove() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(new PgnFen("01_capture_last_move_none.pgn",
        "r1bq1rk1/1pp2p2/pbnp1n1p/3Np1p1/1PB1P1P1/P2P1N1P/2P2P2/R1BQK2R w KQ - 2 11"));
    list.add(new PgnFen("02_capture_last_move_piece.pgn",
        "r1bq1rk1/1pp2p2/pbnp1N1p/4p1p1/1PB1P1P1/P2P1N1P/2P2P2/R1BQK2R b KQ - 0 11"));
    list.add(new PgnFen("03_capture_last_move_en_passant.pgn",
        "r1bq1rk1/1p3p2/pbPp1n1p/n2Np1p1/4P1P1/PB1P1N1P/2P2P2/R1BQK2R b KQ - 0 13"));
    list.add(new PgnFen("04_capture_last_move_mate.pgn",
        "r1b1n2k/1ppqNp1Q/pbnp3p/4pBp1/PP2P1P1/3P1N1P/2P2P2/R1B1K2R b KQ - 0 19"));
    return new PgnTestCaseList(PgnTest.BASIC_CAPTURE_LAST_MOVE, list);
  }

  private static PgnTestCaseList createTestCasesBasicMaxNoProgress() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(new PgnFen("05_capture_last_move_piece_more_moves_max_no_progress_unchanged.pgn",
        "r1b1r2k/1ppq1p2/pbnp1N1p/4p1p1/1PB1P1PP/P2P1N2/2P2P2/R1BQK2R b KQ - 4 14"));
    list.add(new PgnFen("06_capture_last_move_en_passant_capture_more_moves_max_no_progress_unchanged.pgn",
        "Q1b2r2/2b2pk1/p2p1nNp/n3p1p1/4P1P1/PB1P1N1P/2Q2P2/R1B1K2R b KQ - 4 18"));
    list.add(new PgnFen("07_capture_last_move_piece_more_moves_max_no_progress_changed.pgn",
        "r1bq1r2/1pp2pk1/pbnp1N1p/4p1p1/1PB1P1P1/P2P1N1P/2P2P2/R1BQK2R w KQ - 9 16"));
    list.add(new PgnFen("08_capture_last_move_en_passant_capture_more_moves_max_no_progress_changed.pgn",
        "r1bq1rk1/1p3p2/1bPp3p/p2Np1pn/B2NP1P1/P2Pn2P/2PQKP2/R1B3R1 b - - 9 19"));
    return new PgnTestCaseList(PgnTest.BASIC_REPORT_MAX_NO_PROGRESS, list);
  }

  private static PgnTestCaseList createTestCasesSpecial() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("special_nigel_short_tweet_white_to_move.pgn", "K1n5/2k5/P7/8/8/8/8/8 w - - 1 37"));
    list.add(new PgnFen("special_nigel_short_tweet_black_to_move.pgn", "K1n5/2k5/P7/8/8/8/8/8 b - - 0 44"));

    // king against all
    list.add(new PgnFen("special_king_against_all_white_only_king_white_to_move.pgn",
        "rnbqkbnr/pppppppp/8/8/8/4K3/8/8 w kq - 8 33"));
    list.add(new PgnFen("special_king_against_all_white_only_king_black_to_move.pgn",
        "rnbqkbnr/pppppppp/8/8/8/4K3/8/8 b kq - 9 33"));
    list.add(new PgnFen("special_king_against_all_black_only_king_black_to_move.pgn",
        "8/8/3k4/8/8/8/PPPPPPPP/RNBQKBNR b KQ - 10 36"));
    list.add(new PgnFen("special_king_against_all_black_only_king_white_to_move.pgn",
        "8/8/8/3k4/8/8/PPPPPPPP/RNBQKBNR w KQ - 11 37"));

    // norgaard buchanan
    list.add(new PgnFen("special_norgaard_buchanan_strategems_2002_alive.pgn",
        "Bb2kb2/bKp1p1p1/1pP1P1P1/pP6/6P1/P7/8/8 b - - 2 46"));
    list.add(new PgnFen("special_norgaard_buchanan_strategems_2002_alive_played_out.pgn",
        "1bK1kb2/b1p1pBp1/1pP1P1P1/1P4P1/p7/P7/8/8 b - - 1 57"));

    list.add(new PgnFen("special_norgaard_buchanan_strategems_2002_dead.pgn",
        "KbB1kb2/b1p1p1p1/1pP1P1P1/1P4P1/p7/P7/8/8 b - - 26 86"));

    list.add(new PgnFen("special_forced_checkmate_white_only.pgn", "k1r5/2P5/KP6/P7/8/n7/6R1/7B w - - 0 50"));

    list.add(new PgnFen("special_both_kings_blocked_on_the_side_with_pawns.pgn",
        "8/6p1/1p3pP1/1P3Ppk/1Pp3p1/KpP3P1/1P6/8 w - - 0 44"));

    list.add(new PgnFen("special_both_kings_blocked_in_a_corner_with_pawns_and_bishop_pawn_moves_not_exhausted.pgn",
        "k1b5/1p1p4/1PpP4/8/8/1pPp4/1P1P4/K1B5 w - - 1 36"));

    list.add(new PgnFen("special_both_kings_blocked_in_a_corner_with_pawns_and_bishop_pawn_moves_exhausted.pgn",
        "k1b5/1p1p4/1P1P4/2p5/2P5/1p1p4/1P1P4/K1B5 w - - 0 37"));

    list.add(new PgnFen("special_white_king_blocked_in_a_corner_with_pawns_and_bishop_no_piece_capturable.pgn",
        "k7/8/8/8/2p5/1pPp4/1P1P4/K1B5 w - - 1 43"));

    list.add(new PgnFen("special_white_king_trapped_on_side_with_pawns_capturing_only_piece_stalemates.pgn",
        "k7/8/1p6/1P6/1Pp5/KpP5/1P6/8 w - - 0 37"));

    return new PgnTestCaseList(PgnTest.SPECIAL, list);
  }

  private static PgnTestCaseList createTestCasesChaLichessQuickNotDepthThree() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("lichess_V7eJ1RR9.pgn", "1k6/2P5/K7/2q5/8/8/8/8 b - - 0 56"));

    list.add(new PgnFen("lichess_02dMeCVV.pgn", "8/8/4k3/2p4p/p1Pp1p1P/P2P1P2/8/6K1 w - - 32 58"));
    list.add(new PgnFen("lichess_03VIxJUJ.pgn", "8/1pp5/k1b5/Qr6/8/8/p1K5/8 b - - 1 55"));
    list.add(new PgnFen("lichess_0dKUIfwq.pgn", "7K/6qP/8/8/8/4k3/8/8 w - - 1 76"));
    list.add(new PgnFen("lichess_0ehvDno9.pgn", "8/8/8/4RP2/8/P6k/PB6/4q1K1 w - - 0 47"));
    list.add(new PgnFen("lichess_0pZSCKZV.pgn", "8/5k2/1p2p3/1P2Pp1p/3K1P1P/8/8/8 w - - 1 56"));
    list.add(new PgnFen("lichess_0u6eh8ZP.pgn", "8/1k6/8/Kp6/1P6/8/8/8 w - - 0 51"));
    list.add(new PgnFen("lichess_0xMzcUDT.pgn", "2K5/2q5/4P3/8/5P2/3P4/k7/8 w - - 0 68"));
    list.add(new PgnFen("lichess_0yJiwUDU.pgn", "8/8/2p1k2p/p1Pp2pP/P2P2P1/8/3K4/8 b - - 46 71"));
    list.add(new PgnFen("lichess_14SOSdb3.pgn", "8/8/1k1p3p/2pPp2p/p1P1P2P/P3K3/8/8 b - - 5 59"));
    list.add(new PgnFen("lichess_16myWOwv.pgn", "8/8/8/8/2p5/8/2n2K2/3kQ3 b - - 2 61"));
    list.add(new PgnFen("lichess_1rcB0Tru.pgn", "6Rk/8/2b1p2K/pp2r3/2p5/8/8/8 b - - 1 42"));
    list.add(new PgnFen("lichess_1TWhVJGR.pgn", "7k/5K1P/8/8/8/8/8/8 b - - 2 61"));
    list.add(new PgnFen("lichess_29moHRC8.pgn", "8/6p1/7p/6p1/4K1Qk/8/8/8 b - - 5 52"));
    list.add(new PgnFen("lichess_2gCvxApk.pgn", "8/8/8/4Q3/2k5/P7/6q1/6K1 w - - 0 61"));
    list.add(new PgnFen("lichess_2k5piUl6.pgn", "R7/6k1/8/8/8/1P6/KPP5/r7 w - - 1 50"));
    list.add(new PgnFen("lichess_2Lb1nhb5.pgn", "k7/5R2/1Q6/8/2P5/6Pp/P4PKP/8 w - - 0 50"));
    list.add(new PgnFen("lichess_2mgjVvcC.pgn", "3Q4/2p4p/p6k/5pp1/3b3K/6P1/PP5P/5q2 w - - 0 32"));
    list.add(new PgnFen("lichess_2nK9yweT.pgn", "8/8/2p1k3/p1p1p1p1/P1PpP1P1/2bP1K2/8/8 b - - 16 60"));
    list.add(new PgnFen("lichess_2pwjX9B1.pgn", "6Q1/7k/p7/1p4K1/8/5r2/8/4qr2 b - - 0 47"));
    list.add(new PgnFen("lichess_2sFHWmg3.pgn", "7b/5k1K/7P/8/8/8/8/8 w - - 0 57"));
    list.add(new PgnFen("lichess_2wVTK5vV.pgn", "5qK1/7P/8/k7/8/8/8/8 w - - 3 62"));
    list.add(new PgnFen("lichess_33Tpyihf.pgn", "8/8/2p5/8/2PK4/3Q4/7Q/2k5 b - - 0 53"));
    list.add(new PgnFen("lichess_3bKyjTHM.pgn", "8/8/8/p7/5pp1/8/Qp3K2/k7 b - - 3 54"));
    list.add(new PgnFen("lichess_3cYOGNS6.pgn", "Q7/8/4Q3/7k/5Pp1/5KP1/7P/8 w - - 0 43"));
    list.add(new PgnFen("lichess_3jesiit8.pgn", "8/8/8/8/8/3Q4/P1P2PP1/1k4Kq w - - 22 54"));
    list.add(new PgnFen("lichess_3sU6yQo5.pgn", "8/1k6/6p1/3p1pP1/p1pP1Pp1/P1P3P1/8/5K2 b - - 24 111"));
    list.add(new PgnFen("lichess_3uoeIjQi.pgn", "8/8/1k6/p1p1p1p1/P1P1P1Pb/7K/4B3/8 w - - 49 80"));
    list.add(new PgnFen("lichess_3xFc21eB.pgn", "6Kr/4k1P1/8/8/8/5R2/8/8 w - - 4 53"));
    list.add(new PgnFen("lichess_43ghpltO.pgn", "8/7k/8/p2p1p1p/P2P1P1P/2K5/8/8 w - - 22 71"));
    list.add(new PgnFen("lichess_47GuK7dy.pgn", "7k/7P/5K2/8/8/8/8/8 b - - 2 116"));
    list.add(new PgnFen("lichess_49QwULSR.pgn", "8/6K1/8/6p1/7p/7k/6Q1/8 b - - 3 64"));
    list.add(new PgnFen("lichess_4KKvXluk.pgn", "8/8/5k2/1p1p4/1P1Pp1p1/4P1P1/3K2P1/8 w - - 2 51"));
    list.add(new PgnFen("lichess_55wBEu8Z.pgn", "8/p1r5/5R1p/Pp1p2qk/3PpK1P/4Pp1Q/5P2/8 w - - 9 43"));
    list.add(new PgnFen("lichess_5inggIqz.pgn", "8/8/8/2k3p1/p1p1p1P1/P1P1P1P1/3K4/8 w - - 11 60"));
    list.add(new PgnFen("lichess_5YEU1wHP.pgn", "8/2b5/4pkp1/8/5pP1/4q3/3r4/5K2 w - - 2 41"));
    list.add(new PgnFen("lichess_6HdGxhSR.pgn", "8/8/8/p1p2p1k/P1P2P1p/3K3P/8/8 w - - 15 53"));
    list.add(new PgnFen("lichess_6kfJPvsA.pgn", "8/7p/8/1K6/8/6p1/7k/7Q b - - 1 57"));
    list.add(new PgnFen("lichess_6u1akjTw.pgn", "8/8/8/4k3/p1p2p1p/PpP2P1P/1P2K3/8 b - - 38 90"));
    list.add(new PgnFen("lichess_6XQZaBGI.pgn", "8/4bkp1/8/K2nP2p/3r4/8/8/5q2 w - - 0 50"));
    list.add(new PgnFen("lichess_78XhRaIr.pgn", "8/6qK/7P/8/8/2k5/8/8 w - - 0 73"));
    list.add(new PgnFen("lichess_7alMRe6y.pgn", "2kR4/2p5/1pK2p2/r3p3/8/8/8/8 b - - 0 47"));
    list.add(new PgnFen("lichess_7CxnEuV4.pgn", "8/8/p5k1/Pp1p1p1p/1P1P1P1P/1K6/8/8 w - - 42 68"));
    list.add(new PgnFen("lichess_7fWsFh0J.pgn", "8/8/8/8/5K2/6pk/7R/7q b - - 1 60"));
    list.add(new PgnFen("lichess_7uopV5nZ.pgn", "6k1/8/8/1p1p2p1/1P1P2P1/5K2/8/8 w - - 33 59"));
    list.add(new PgnFen("lichess_83SCI1Fg.pgn", "6k1/8/8/1p2p1p1/pPp1P1P1/P1P5/8/K7 w - - 48 78"));
    list.add(new PgnFen("lichess_85kfBN8B.pgn", "8/2k5/1p6/1Pp1p3/2P1Pp1p/5P1P/8/5K2 b - - 46 75"));
    list.add(new PgnFen("lichess_87JajHeg.pgn", "8/8/3k4/1p2p2p/1P2P2P/8/6K1/8 b - - 0 50"));
    list.add(new PgnFen("lichess_8B3XZBsv.pgn", "8/2k5/4p3/p1p1P1p1/P1P3P1/8/1K6/8 b - - 73 83"));
    list.add(new PgnFen("lichess_8cTl7SrQ.pgn", "8/7R/8/5bp1/6rk/5n2/5K2/8 b - - 0 48"));
    list.add(new PgnFen("lichess_8FUSHxUV.pgn", "k7/P1K5/8/8/8/8/8/8 b - - 2 58"));
    list.add(new PgnFen("lichess_8sompKsV.pgn", "8/4R3/5p1k/5Pp1/6Pp/8/6KP/7B b - - 3 60"));
    list.add(new PgnFen("lichess_8TigzhbJ.pgn", "8/2k4p/1p2p1pP/1P1pPpP1/3P1P2/8/8/K7 b - - 90 94"));
    list.add(new PgnFen("lichess_8uFm7Zvw.pgn", "3R4/5p1k/1N3Q2/7q/5P1K/1P4P1/7P/8 w - - 5 45"));
    list.add(new PgnFen("lichess_992nwupc.pgn", "8/1k6/4p3/3pP1p1/1p1P2P1/1P6/8/1K6 b - - 23 56"));
    list.add(new PgnFen("lichess_9AqVp2F8.pgn", "8/5p2/8/p4pK1/k4N2/1R6/P7/8 b - - 0 55"));
    list.add(new PgnFen("lichess_9FpfMBVq.pgn", "8/8/p7/Pp1p1p1k/1P1P1Pp1/3b2P1/3K1B2/8 b - - 4 54"));
    list.add(new PgnFen("lichess_9jxoYRyn.pgn", "8/6k1/5p2/1p2pP1p/1P2P2P/7K/8/8 b - - 37 72"));
    list.add(new PgnFen("lichess_9T0wrl30.pgn", "8/p7/Pp1k2p1/1P1p1pPp/3P1P1P/6K1/8/8 b - - 26 52"));
    list.add(new PgnFen("lichess_9vMq768Z.pgn", "1rK1k3/2P2R2/4Pp2/5P2/8/8/8/8 w - - 17 66"));

    list.add(new PgnFen("lichess_a2l4gphm.pgn", "5k2/8/3p4/3p1p1p/p1pP1P1P/PpP2K2/1P5B/8 w - - 20 67"));

    list.add(new PgnFen("lichess_A4aWYoPF.pgn", "4k3/8/5p2/p2p1Pp1/P2P2Pp/1K5P/8/8 w - - 30 62"));
    list.add(new PgnFen("lichess_aefWMnIP.pgn", "8/5pk1/5Pp1/7p/7P/2r3PK/1q6/8 b - - 0 51"));
    list.add(new PgnFen("lichess_AHPAU56z.pgn", "8/p6p/5kp1/5pP1/5P1K/1r5P/8/8 b - - 0 47"));
    list.add(new PgnFen("lichess_AigeGQsz.pgn", "8/8/5kp1/2r2pP1/5K2/1q6/2q5/8 b - - 0 54"));
    list.add(new PgnFen("lichess_Altbmg1n.pgn", "8/8/4k2p/1p2p1pP/pP2P1P1/P3K3/8/8 b - - 2 42"));
    list.add(new PgnFen("lichess_aoXPcDW6.pgn", "8/8/5k2/1p2p1p1/pP2P1P1/P7/4K3/8 b - - 34 55"));
    list.add(new PgnFen("lichess_APEww4mD.pgn", "8/8/8/8/8/5K2/2r4p/6Rk b - - 31 84"));
    list.add(new PgnFen("lichess_aWI97MHZ.pgn", "8/8/8/1k1p2p1/2pPp1P1/1pP1P1K1/1P6/8 w - - 16 51"));
    list.add(new PgnFen("lichess_aXUqSEe1.pgn", "8/8/6k1/1p1p1p1p/1P1P1P1P/8/8/7K w - - 0 70"));
    list.add(new PgnFen("lichess_B3mIj9Su.pgn", "8/8/8/5p2/8/4p3/3r2p1/4K1kR b - - 21 75"));
    list.add(new PgnFen("lichess_B8bpMZ71.pgn", "2Q5/8/5Q2/6B1/8/1P2P1P1/P3kPq1/5RK1 w - - 1 55"));
    list.add(new PgnFen("lichess_bDxnc7TH.pgn", "8/8/1p1k2p1/pP1p1pP1/P2P1P2/3K4/8/8 b - - 2 58"));
    list.add(new PgnFen("lichess_BFfZguIQ.pgn", "8/p4pk1/3b4/5Pp1/6q1/8/8/7K w - - 2 43"));
    list.add(new PgnFen("lichess_bfJbt6GP.pgn", "8/7p/4npp1/1pk5/1P6/2K5/1r1r4/8 b - - 0 52"));
    list.add(new PgnFen("lichess_bQ0EXLal.pgn", "1b3Rk1/1p4pp/p7/8/8/7r/2K5/8 b - - 1 47"));
    list.add(new PgnFen("lichess_bXkWOZ49.pgn", "8/3k4/6p1/3p1pP1/p1pP1P1K/P1P5/8/8 w - - 20 64"));
    list.add(new PgnFen("lichess_c3ew66ZV.pgn", "8/8/3k4/1p2p1p1/pP1pP1P1/P2P4/1K6/8 b - - 32 62"));
    list.add(new PgnFen("lichess_CezFfltE.pgn", "8/8/8/p1p1k1p1/P1P1p1P1/4Pp1p/2K2P1P/8 b - - 4 48"));
    list.add(new PgnFen("lichess_cLnhkfGh.pgn", "8/4k3/1p1p4/1p1P1p2/1P3P1p/4K2P/8/8 w - - 2 50"));
    list.add(new PgnFen("lichess_CQI03gYb.pgn", "4k3/8/1p6/1p1p2p1/1P1Pp1P1/1P2P3/8/4K3 w - - 10 46"));
    list.add(new PgnFen("lichess_cZ8QxYP8.pgn", "8/8/8/k7/7P/8/1PPPPbP1/1NBQKBNR w K - 0 23"));
    list.add(new PgnFen("lichess_D342JjRk.pgn", "8/6k1/1p1p4/1PpPp1p1/2P1P1p1/6P1/8/5K2 w - - 40 100"));
    list.add(new PgnFen("lichess_D4vIOthl.pgn", "8/6Q1/7k/7p/3K4/8/6p1/8 b - - 0 65"));
    list.add(new PgnFen("lichess_dBergEkt.pgn", "1K4Qk/8/8/8/6p1/5p2/8/8 b - - 0 55"));
    list.add(new PgnFen("lichess_DiKq9jcy.pgn", "8/6kP/3Q2P1/3qP3/1BK5/2P5/8/8 w - - 3 65"));
    list.add(new PgnFen("lichess_DQxKRa2g.pgn", "8/8/6p1/k2p1pP1/1p1P1P2/pP6/P7/2K5 b - - 15 68"));
    list.add(new PgnFen("lichess_dZhy51vS.pgn", "2R5/8/1p6/kP6/p6p/P7/6KP/1R6 b - - 1 48"));
    list.add(new PgnFen("lichess_E0TisOA3.pgn", "8/8/5Q2/4R3/3p2k1/3P3p/P4PKP/8 w - - 0 55"));
    list.add(new PgnFen("lichess_E8I7AMGj.pgn", "3k4/2p5/1pPp2p1/1P1P1pPp/5P1P/2K5/8/8 w - - 18 58"));
    list.add(new PgnFen("lichess_E9P918aj.pgn", "8/8/8/8/3k2qK/7P/8/8 w - - 0 51"));
    list.add(new PgnFen("lichess_eC0QpBxu.pgn", "8/3k4/p2p2p1/P2P2P1/8/8/1K6/8 b - - 47 77"));
    list.add(new PgnFen("lichess_eExHpNDf.pgn", "3NR3/4P3/8/8/4k3/6P1/6q1/6K1 w - - 17 84"));
    list.add(new PgnFen("lichess_efAanuaT.pgn", "8/4k3/3p2p1/p1pP2P1/P1P5/4K3/8/8 b - - 35 89"));
    list.add(new PgnFen("lichess_EFd9NOSF.pgn", "8/8/p2k4/Pp1p2p1/1P1Pp1P1/4P3/8/6K1 b - - 49 89"));
    list.add(new PgnFen("lichess_eKwKl6Y9.pgn", "8/8/8/7P/8/6qK/8/2k5 w - - 0 70"));
    list.add(new PgnFen("lichess_EPTBN53W.pgn", "8/8/8/8/8/8/5k1p/7K w - - 2 58"));
    list.add(new PgnFen("lichess_EQCMW0jB.pgn", "8/8/8/7r/p4K1k/8/7R/6q1 b - - 0 58"));
    list.add(new PgnFen("lichess_EsFX1Urt.pgn", "8/8/7p/4K2k/6R1/8/8/8 b - - 0 54"));
    list.add(new PgnFen("lichess_EstAIWqd.pgn", "8/8/8/8/8/1K4p1/6rk/7R b - - 0 65"));
    list.add(new PgnFen("lichess_Ew7uTqu0.pgn", "6kR/p4pp1/6b1/8/5K2/r7/8/8 b - - 3 48"));
    list.add(new PgnFen("lichess_f7x3bSzh.pgn", "k6r/p4p1p/2b3p1/1B6/8/N3b1P1/P4p1P/1R5K w - - 0 34"));
    list.add(new PgnFen("lichess_fAjvL1hG.pgn", "8/7k/1p2p1p1/pP2P1Pp/P6P/8/2K5/8 w - - 52 75"));
    list.add(new PgnFen("lichess_FBqHYgEu.pgn", "8/8/4k3/p1p2p1p/PpP2P1P/1P6/1K6/8 b - - 22 96"));
    list.add(new PgnFen("lichess_fbzlzlUK.pgn", "5b2/5pk1/3r1P2/2n4K/4b3/6q1/8/8 b - - 0 55"));
    list.add(new PgnFen("lichess_FMDN8gJ7.pgn", "8/8/8/2p5/2kb4/1Q6/8/4K3 b - - 12 68"));
    list.add(new PgnFen("lichess_fN4vRqp0.pgn", "8/8/8/6Q1/8/7k/5p2/5RK1 w - - 0 73"));
    list.add(new PgnFen("lichess_fPnoYQtW.pgn", "8/8/8/1K5p/8/8/6pQ/7k b - - 4 52"));
    list.add(new PgnFen("lichess_FSZeGcrT.pgn", "8/8/k7/2p1p3/1pP1P1p1/pP4P1/P7/2K5 w - - 60 84"));
    list.add(new PgnFen("lichess_fuNe9BZw.pgn", "8/8/4k3/p1p2p1p/P1P2P1P/8/3K4/8 w - - 19 95"));
    list.add(new PgnFen("lichess_FYTeO80q.pgn", "8/6Qp/p3B1p1/7k/P4Pq1/7K/1P3R1P/8 w - - 0 38"));
    list.add(new PgnFen("lichess_G0xDBYwE.pgn", "7k/8/8/KqN5/2P5/8/8/8 w - - 0 59"));
    list.add(new PgnFen("lichess_G8i5Bn7F.pgn", "7Q/7k/5K2/8/8/8/6p1/8 b - - 2 57"));
    list.add(new PgnFen("lichess_gcJZWCKq.pgn", "7k/7P/5K2/8/8/8/8/8 b - - 0 79"));
    list.add(new PgnFen("lichess_GCuwJ5bP.pgn", "8/8/8/Kq6/P5k1/8/8/8 w - - 0 52"));
    list.add(new PgnFen("lichess_GgiPdCBU.pgn", "8/8/1p1k4/1Pp1p1p1/p1P1PpP1/P4P2/8/5K2 b - - 10 48"));
    list.add(new PgnFen("lichess_GH6YBJIu.pgn", "8/8/2p2k2/p1Pp2p1/P2P2P1/1K6/8/8 b - - 9 52"));
    list.add(new PgnFen("lichess_GibMYT4i.pgn", "8/4k3/4b3/1p1p1p1p/1P1P1P1P/8/8/7K w - - 46 78"));
    list.add(new PgnFen("lichess_GIwBNz5S.pgn", "8/8/1p6/p7/8/3K4/5b2/5kR1 b - - 0 56"));
    list.add(new PgnFen("lichess_GpCPpsRz.pgn", "7k/5Q2/8/p1p1NBP1/P1P2P2/7p/6KP/8 w - - 0 47"));
    list.add(new PgnFen("lichess_gPxJdo7u.pgn", "8/1k6/1p2p1p1/1P2P1P1/8/8/6K1/8 b - - 40 92"));
    list.add(new PgnFen("lichess_gtzPUYWo.pgn", "8/8/8/8/4pp2/3Rkp2/4r3/5K2 b - - 0 66"));
    list.add(new PgnFen("lichess_Gw5OAmSo.pgn", "8/3k4/3p4/p1pPp2p/P1P1P2P/8/2K5/8 w - - 13 64"));
    list.add(new PgnFen("lichess_GysBobWK.pgn", "8/8/5k2/1p1p2p1/1P1P2P1/8/4K3/8 w - - 31 73"));
    list.add(new PgnFen("lichess_GzoCTk46.pgn", "8/8/7p/5p2/5kp1/4Q3/8/4K3 b - - 5 58"));
    list.add(new PgnFen("lichess_h2INNt6T.pgn", "7R/6pk/6rp/p6K/P6P/8/8/6r1 b - - 3 40"));
    list.add(new PgnFen("lichess_H4MIp43q.pgn", "8/1k6/p1p3p1/P1Pp1pP1/K2P1P2/8/8/8 b - - 2 53"));
    list.add(new PgnFen("lichess_H968wWnH.pgn", "8/5k2/8/2p2p2/p1P2P1p/P3K2P/8/8 w - - 15 51"));
    list.add(new PgnFen("lichess_hdott1S4.pgn", "8/8/5R2/6PN/5B1K/5k2/7r/8 w - - 1 59"));
    list.add(new PgnFen("lichess_hHLqO2Sb.pgn", "8/8/2k4p/1p1p2pP/1P1P2P1/8/5K2/8 b - - 49 67"));
    list.add(new PgnFen("lichess_hkiG96je.pgn", "6qK/8/7P/8/8/k7/8/8 w - - 0 59"));
    list.add(new PgnFen("lichess_hRedYjtT.pgn", "7R/6pk/p5p1/8/1b2p3/4P3/b1r5/K7 b - - 5 39"));
    list.add(new PgnFen("lichess_HX7UOzXi.pgn", "8/8/8/8/8/8/5k1p/7K w - - 2 85"));
    list.add(new PgnFen("lichess_I0zJt0qZ.pgn", "8/8/8/2k4p/1p2p1pP/1P2P1P1/8/3K4 b - - 15 79"));
    list.add(new PgnFen("lichess_I9MLIaiu.pgn", "8/4k3/4p2p/p1p1P2P/P1P2K2/8/8/8 w - - 29 68"));
    list.add(new PgnFen("lichess_IcjtzT9i.pgn", "8/8/8/8/2k3qK/5P1P/8/8 w - - 0 77"));
    list.add(new PgnFen("lichess_IdZxNXzF.pgn", "8/8/8/8/8/5k2/7p/7K w - - 0 79"));
    list.add(new PgnFen("lichess_if6AKvpb.pgn", "8/8/8/3k4/1P2R3/4KP2/P2q4/8 w - - 7 51"));
    list.add(new PgnFen("lichess_ig7fudY7.pgn", "8/8/8/8/5K1R/1r5k/6p1/8 b - - 0 53"));
    list.add(new PgnFen("lichess_iNx1gFMc.pgn", "8/8/1p2k1p1/pP1p1pPp/P2P1P1P/5K2/8/8 b - - 0 49"));
    list.add(new PgnFen("lichess_Ir1xVUbe.pgn", "8/8/3k4/p2p2p1/P2P2P1/1K6/8/8 b - - 7 55"));
    list.add(new PgnFen("lichess_IT4IGHvQ.pgn", "8/8/8/p1k3p1/P1p2pPp/2P2P1P/4K3/8 b - - 21 48"));
    list.add(new PgnFen("lichess_Ity8OMJy.pgn", "8/8/4k2p/p1p2p1P/P1P2Pp1/4K1P1/8/8 w - - 10 53"));
    list.add(new PgnFen("lichess_Iy1VEUMC.pgn", "3Kq3/2P3k1/8/8/P7/8/8/8 w - - 4 59"));
    list.add(new PgnFen("lichess_j17qnxd0.pgn", "4K2k/6Q1/7p/8/8/8/8/8 b - - 38 67"));
    list.add(new PgnFen("lichess_j4phmaNj.pgn", "5Kq1/8/4k3/8/7P/8/8/8 w - - 1 67"));
    list.add(new PgnFen("lichess_jehHS0Jj.pgn", "8/6k1/8/6qK/8/7P/8/8 w - - 0 65"));
    list.add(new PgnFen("lichess_JfHZLRvD.pgn", "r7/pp6/3p1p2/2p1k3/2q1Q2P/5KP1/5P2/3R3R b - - 0 33"));
    list.add(new PgnFen("lichess_JIyquz2Q.pgn", "8/2k5/6p1/1p1p1pPp/pP1P1P1P/P7/5K2/8 b - - 25 55"));
    list.add(new PgnFen("lichess_jK46bYGo.pgn", "6kQ/p4rp1/4r3/3K4/8/8/8/8 b - - 6 48"));
    list.add(new PgnFen("lichess_jnox9kxK.pgn", "8/8/8/5K2/8/p7/1Q6/k1q5 b - - 0 86"));
    list.add(new PgnFen("lichess_jnsJhG8T.pgn", "8/1k6/1p1pB3/pPpP4/P1Pp1p1p/3PbP1P/6K1/8 w - - 48 85"));
    list.add(new PgnFen("lichess_jnvJUlDw.pgn", "8/2p4Q/3rn1pk/6p1/2K5/8/8/8 b - - 2 48"));
    list.add(new PgnFen("lichess_JQpTTEF7.pgn", "8/2k5/p1p5/P1P1p1p1/P3P1Pp/4K2P/8/8 w - - 1 40"));
    list.add(new PgnFen("lichess_K5d1o8ea.pgn", "8/8/8/8/8/8/5k1p/7K w - - 2 61"));
    list.add(new PgnFen("lichess_KbEG3yK3.pgn", "8/8/7p/8/8/8/b4K2/kQ6 b - - 0 61"));
    list.add(new PgnFen("lichess_kbutR5IJ.pgn", "2k1Q3/2p5/2K5/2N5/2q5/4q3/8/8 b - - 0 53"));
    list.add(new PgnFen("lichess_Kdf0f1OE.pgn", "8/8/8/8/5K2/7p/7k/6R1 b - - 0 69"));
    list.add(new PgnFen("lichess_KE47c49r.pgn", "7R/5ppk/6bp/8/4r3/2K5/8/8 b - - 11 46"));
    list.add(new PgnFen("lichess_KeK8Chul.pgn", "8/8/2k5/1p1p2p1/1P1P2Pp/7P/8/6K1 b - - 14 52"));
    list.add(new PgnFen("lichess_kj6Dk7Fb.pgn", "8/8/6k1/p2p1p1p/P2P1P1P/8/8/4K3 b - - 39 64"));
    list.add(new PgnFen("lichess_knQsF7R8.pgn", "5qK1/3k3P/8/8/6P1/8/8/8 w - - 5 49"));
    list.add(new PgnFen("lichess_KP23RUWa.pgn", "8/8/5k2/7K/8/1Q5q/P7/8 w - - 17 61"));
    list.add(new PgnFen("lichess_KPvcgsfS.pgn", "1rK5/2R5/4k3/8/8/6P1/P7/8 w - - 0 48"));
    list.add(new PgnFen("lichess_KQKPJzpe.pgn", "8/8/8/k4p2/p1p2p1p/P1P2P1P/5K2/8 b - - 11 48"));
    list.add(new PgnFen("lichess_KQvOkgie.pgn", "8/1k6/3p2p1/2pP1pP1/p1P2P2/P3K3/8/8 b - - 58 75"));
    list.add(new PgnFen("lichess_Ks0yR98N.pgn", "7Q/6P1/8/8/7r/5k1K/8/8 w - - 0 61"));
    list.add(new PgnFen("lichess_l8Wwo7if.pgn", "8/7p/3q1kp1/6P1/4K3/3r4/8/8 b - - 0 56"));
    list.add(new PgnFen("lichess_LbP5CIDy.pgn", "8/8/2k1p1p1/1p1pPpPp/1P1P1P1P/1K6/8/8 b - - 38 105"));
    list.add(new PgnFen("lichess_LC76ur18.pgn", "8/8/2p5/3b4/8/2K5/R7/kq6 b - - 1 65"));
    list.add(new PgnFen("lichess_Lf3eQe81.pgn", "8/8/4k3/1p1p2p1/1P1P2P1/1P1K4/8/8 w - - 6 57"));
    list.add(new PgnFen("lichess_lHtqM0fz.pgn", "8/8/8/8/6K1/8/7p/6Rk b - - 7 74"));
    list.add(new PgnFen("lichess_lk42iihk.pgn", "8/R7/8/8/6k1/6P1/5Q1K/7q w - - 2 42"));
    list.add(new PgnFen("lichess_LqTNOgyT.pgn", "8/8/3Q4/8/P6P/5k2/8/3q1K2 w - - 0 53"));
    list.add(new PgnFen("lichess_LTFQx2YH.pgn", "8/8/4k3/p1p2p1p/P1P2P1P/K7/8/8 b - - 17 66"));
    list.add(new PgnFen("lichess_Lv0hWAvD.pgn", "8/8/p4k1p/Pp1p2pP/1P1P2P1/4K3/8/8 b - - 4 67"));
    list.add(new PgnFen("lichess_lYf34Uiq.pgn", "8/8/8/2K5/8/8/7p/5Qk1 b - - 5 69"));
    list.add(new PgnFen("lichess_m5WDCW16.pgn", "8/6k1/8/7R/8/6PP/5PBK/7r w - - 5 42"));
    list.add(new PgnFen("lichess_M9Xic74G.pgn", "6k1/8/6p1/1p2p1Pp/1P2P2P/6K1/8/8 w - - 31 67"));
    list.add(new PgnFen("lichess_mBrCKInG.pgn", "3r4/5pk1/p3pPp1/1p2P3/4K3/r7/3q4/8 b - - 0 45"));
    list.add(new PgnFen("lichess_McLow5Hz.pgn", "8/7P/8/4k3/4B3/5PR1/6PK/7r w - - 1 43"));
    list.add(new PgnFen("lichess_MgqytYeQ.pgn", "8/6k1/5p2/1p2pP1p/1P2P2P/5K2/8/8 b - - 15 52"));
    list.add(new PgnFen("lichess_Mm4HIjDh.pgn", "8/8/2p1k1p1/1pP1p1P1/1P2P3/7K/8/8 b - - 5 43"));
    list.add(new PgnFen("lichess_MOyPDylu.pgn", "8/8/8/k1p1p1p1/1pPpP1Pp/1P1PbK1P/2B5/8 b - - 12 80"));
    list.add(new PgnFen("lichess_MvEif0NV.pgn", "8/8/8/2k3qK/6PP/8/8/8 w - - 0 67"));
    list.add(new PgnFen("lichess_MXeVCHSn.pgn", "1k6/8/8/p1p2p2/P1P2P1p/2P2P1P/3K4/8 w - - 37 75"));
    list.add(new PgnFen("lichess_mXxGD2IV.pgn", "8/6k1/8/2p2p1p/1pP1pP1P/1P2P3/4K3/8 w - - 90 100"));
    list.add(new PgnFen("lichess_N6AYM22R.pgn", "8/5pp1/6kp/7P/5n2/p7/6r1/7K b - - 0 58"));
    list.add(new PgnFen("lichess_ncuz3jy9.pgn", "8/7p/6pk/4p3/6P1/5q2/3K1P2/1q6 w - - 0 47"));
    list.add(new PgnFen("lichess_nECJszX7.pgn", "8/p1p5/Ppkp4/1P6/K7/2q5/7p/8 b - - 0 46"));
    list.add(new PgnFen("lichess_NmQfkdhK.pgn", "4Rk2/5p2/5Pp1/6P1/8/6p1/5r1r/6K1 b - - 6 52"));
    list.add(new PgnFen("lichess_nNXmBTlz.pgn", "8/8/p1k5/Pp1p2p1/1P1P2Pp/3K3P/8/8 b - - 0 46"));
    list.add(new PgnFen("lichess_nvxFBquo.pgn", "7K/2P2k1r/3P4/8/8/6R1/8/8 w - - 0 64"));
    list.add(new PgnFen("lichess_o6TGucU0.pgn", "8/p5kp/5n1P/8/7K/5q2/8/6r1 b - - 0 57"));
    list.add(new PgnFen("lichess_OCeJcw04.pgn", "8/5k2/1p1p4/1P1P1p2/1K3Pp1/1P4P1/8/8 b - - 4 74"));
    list.add(new PgnFen("lichess_odke8ePj.pgn", "8/5pkp/8/7P/6p1/5pP1/5P1K/5q2 w - - 1 57"));
    list.add(new PgnFen("lichess_Oejr5ZFX.pgn", "8/8/5Q2/8/7R/6k1/P5p1/7K w - - 0 47"));
    list.add(new PgnFen("lichess_ogehooFw.pgn", "8/8/p1k5/P1p2p1p/2P2P1P/3K4/8/8 b - - 3 48"));
    list.add(new PgnFen("lichess_om0bCR5w.pgn", "R4k2/8/5K1p/8/8/6r1/8/7q b - - 1 48"));
    list.add(new PgnFen("lichess_Omq6Fdhm.pgn", "8/8/8/8/pp6/k7/1Q2K3/8 b - - 2 69"));
    list.add(new PgnFen("lichess_ooSZuhhE.pgn", "1Q6/2k1K3/2p5/8/8/8/8/8 b - - 0 40"));
    list.add(new PgnFen("lichess_oT7sOSw5.pgn", "8/8/2p1k3/p1Pp2p1/Pp1P1pPp/1P3P1P/5P2/3K4 b - - 21 58"));
    list.add(new PgnFen("lichess_p2ujQM8o.pgn", "8/8/6pk/5rP1/7K/5q1P/5P2/8 b - - 0 39"));
    list.add(new PgnFen("lichess_p4CeCK3J.pgn", "4Q3/8/8/2Q5/8/1P6/1KP5/q6k w - - 1 54"));
    list.add(new PgnFen("lichess_PAjZeTkN.pgn", "8/8/Nk6/8/R7/Kr6/P7/8 w - - 0 64"));
    list.add(new PgnFen("lichess_pBFolKHd.pgn", "8/1k6/3p3p/1p1P1p1P/1P2pP1K/4P3/8/8 w - - 33 67"));
    list.add(new PgnFen("lichess_pFsUTatm.pgn", "8/5q2/2k1K3/2P1Q3/3P4/4P3/8/8 w - - 1 57"));
    list.add(new PgnFen("lichess_PHCKkpCX.pgn", "8/8/1p2k3/1P1p2p1/3P2Pp/7P/1K6/8 b - - 51 70"));
    list.add(new PgnFen("lichess_pIgctI4e.pgn", "6k1/R7/4NB2/6p1/8/6PK/5P1P/8 b - - 0 40"));
    list.add(new PgnFen("lichess_PIL4PUtT.pgn", "8/8/3Q4/2P5/1PB3k1/P7/5P1K/R4Rq1 w - - 0 45"));
    list.add(new PgnFen("lichess_PkZ6qiA6.pgn", "8/7p/3p4/2pk4/4Q3/p7/Kb6/7q b - - 5 50"));
    list.add(new PgnFen("lichess_pqiyPXvP.pgn", "8/8/2k5/p2p2p1/Pp1Pp1P1/1P2P3/5K2/8 w - - 23 70"));
    list.add(new PgnFen("lichess_pqsImnAT.pgn", "8/4k3/1p1p2p1/pP1P2P1/P7/4K3/8/8 b - - 6 52"));
    list.add(new PgnFen("lichess_PUA2eCrY.pgn", "8/3k4/1p2p1p1/1P1pP1Pp/1P1P3P/8/8/6K1 w - - 21 63"));
    list.add(new PgnFen("lichess_qDcEiBsg.pgn", "8/2k5/4p1p1/p2pPpPp/P2P1P1P/3K4/8/8 b - - 81 91"));
    list.add(new PgnFen("lichess_QirOKYTP.pgn", "r1b5/ppppn2p/5Qp1/8/1P2q3/N1Pkb3/P4P1P/3RKR2 b - - 1 20"));
    list.add(new PgnFen("lichess_qle7LoyU.pgn", "8/8/1k1p4/p1pP1p1p/P1P2P1P/8/3K4/8 b - - 79 83"));
    list.add(new PgnFen("lichess_QnFE4Znl.pgn", "8/8/8/8/8/6QP/6PK/2k3q1 w - - 6 51"));
    list.add(new PgnFen("lichess_QPdYSKDq.pgn", "6r1/8/4kn1K/8/2q2P2/8/8/8 w - - 16 72"));
    list.add(new PgnFen("lichess_QpptleSj.pgn", "8/1p6/pPp5/P1P5/8/4nkp1/6p1/6K1 b - - 1 71"));
    list.add(new PgnFen("lichess_qpXuukKR.pgn", "8/8/8/3p1k1p/p1pPp1pP/P1P1P1P1/8/3K4 w - - 14 67"));
    list.add(new PgnFen("lichess_QSTM2G7V.pgn", "8/3k1p2/4pPp1/2p1P1Pp/1pP4P/1P6/8/6K1 b - - 33 56"));
    list.add(new PgnFen("lichess_quMnfy5i.pgn", "8/5k2/5p1p/4pP1P/2p1P3/1pP3K1/1P6/8 b - - 3 52"));
    list.add(new PgnFen("lichess_QwKzanAX.pgn", "8/2p2pkp/7P/8/8/7K/5qr1/8 b - - 0 38"));
    list.add(new PgnFen("lichess_r1SzzM60.pgn", "2k5/2P5/8/1KN5/8/8/8/8 b - - 0 66"));
    list.add(new PgnFen("lichess_rbPFjPRm.pgn", "8/1k6/3p2p1/1p1P1pPp/pP3P1P/P7/2K5/8 b - - 39 69"));
    list.add(new PgnFen("lichess_RF0MOp86.pgn", "Q7/k1p5/1p1p4/p7/2r5/r7/8/3K4 b - - 13 57"));
    list.add(new PgnFen("lichess_RJ6gw7gD.pgn", "8/6k1/5p2/2p1pPp1/1pP1P1P1/1P3K2/8/8 b - - 4 69"));
    list.add(new PgnFen("lichess_rSC90mAs.pgn", "k7/P1K5/8/8/8/8/8/8 b - - 2 62"));
    list.add(new PgnFen("lichess_RSqLhejA.pgn", "8/4n3/4p3/8/2p5/5K1k/7R/2r3q1 b - - 3 62"));
    list.add(new PgnFen("lichess_RvN1zpCB.pgn", "6nr/6pp/4pp2/8/1PP5/P4P1k/3q3Q/3B1RK1 b - - 2 25"));
    list.add(new PgnFen("lichess_rVN4T4Tz.pgn", "K7/1q6/8/P7/8/8/8/6k1 w - - 0 69"));
    list.add(new PgnFen("lichess_RXlEuUhF.pgn", "7k/8/7p/2p2p1P/1pP2P2/pP2K3/P7/8 w - - 39 64"));
    list.add(new PgnFen("lichess_s2ZKBPfv.pgn", "8/1p1k4/pPp5/P1Pp2p1/3P2P1/5K2/8/8 w - - 22 54"));
    list.add(new PgnFen("lichess_sBwTHJz0.pgn", "8/8/7k/p2p2p1/Pp1P2Pp/1P1K3P/8/8 w - - 60 67"));
    list.add(new PgnFen("lichess_SdlLyZvq.pgn", "8/4k3/1p2p1p1/1P2P1Pp/7P/7K/8/8 b - - 27 76"));
    list.add(new PgnFen("lichess_SDy9Y3D0.pgn", "8/8/8/4p3/3Qk1K1/3r4/8/8 b - - 0 54"));
    list.add(new PgnFen("lichess_Sdz1yTzS.pgn", "8/8/6qK/8/8/6P1/P7/4k3 w - - 15 59"));
    list.add(new PgnFen("lichess_sMv8Hh43.pgn", "8/6p1/5kP1/7K/2r2q2/8/8/8 b - - 1 52"));
    list.add(new PgnFen("lichess_sp0oSyY2.pgn", "8/pp6/8/kQ5K/8/8/b7/2q5 b - - 0 40"));
    list.add(new PgnFen("lichess_SrLwzxpB.pgn", "8/8/3k4/p2p2p1/Pp1P2P1/1P6/8/1K6 b - - 8 54"));
    list.add(new PgnFen("lichess_ST89KN4m.pgn", "8/8/8/8/6k1/5n2/6pK/8 w - - 2 67"));
    list.add(new PgnFen("lichess_SwCSdv8K.pgn", "8/8/6p1/8/8/3K4/8/3kQ3 b - - 0 58"));
    list.add(new PgnFen("lichess_SX3iSehH.pgn", "5KQ1/5q2/4k3/P7/8/8/8/8 w - - 0 55"));
    list.add(new PgnFen("lichess_Sx7EyNdc.pgn", "3k4/8/2p5/1pP1p1p1/1P2P1P1/3K4/8/8 w - - 47 67"));
    list.add(new PgnFen("lichess_Szu1FAr2.pgn", "7R/pp4pk/2p3p1/8/3q4/8/6K1/8 b - - 7 46"));
    list.add(new PgnFen("lichess_tapdr97m.pgn", "7k/6pP/6P1/5K2/8/8/8/8 w - - 1 67"));
    list.add(new PgnFen("lichess_tDwQdtb5.pgn", "8/8/8/8/8/5k2/7p/7K w - - 0 73"));
    list.add(new PgnFen("lichess_tEtllzqk.pgn", "8/7R/6pk/5p2/5bpK/8/r7/8 b - - 1 53"));
    list.add(new PgnFen("lichess_TjtEtQJ4.pgn", "k7/2p5/p1Pp4/P2P1p2/5Pp1/3K2P1/8/8 w - - 29 66"));
    list.add(new PgnFen("lichess_tn1wmL3G.pgn", "8/8/1p4pk/p1b5/6Pp/7P/r7/7K w - - 0 46"));
    list.add(new PgnFen("lichess_TTb9iG4x.pgn", "8/7k/5p1p/2p1pP1P/1pP1P3/1P1K4/8/8 w - - 45 61"));
    list.add(new PgnFen("lichess_TV4G8kFN.pgn", "8/6pk/6Pp/7K/3q3P/8/4p3/8 b - - 0 60"));
    list.add(new PgnFen("lichess_u2fQpBN9.pgn", "3Q4/5R2/4p3/2p1P3/2N1k2p/8/6KP/8 b - - 13 46"));
    list.add(new PgnFen("lichess_U5uv4wPs.pgn", "8/8/7R/6pk/4n1r1/8/7K/8 b - - 3 78"));
    list.add(new PgnFen("lichess_U8Rj7Y9F.pgn", "8/4k3/6p1/1p2p1Pp/1P2P2P/8/8/4K3 b - - 18 63"));
    list.add(new PgnFen("lichess_uBSFb2kx.pgn", "8/3k4/8/8/8/6BR/6PK/7r w - - 41 88"));
    list.add(new PgnFen("lichess_UDdCTvWG.pgn", "7k/5K1P/8/8/8/8/8/8 b - - 2 62"));
    list.add(new PgnFen("lichess_UdUE6AvZ.pgn", "8/Kq6/P7/8/6k1/8/8/8 w - - 1 50"));
    list.add(new PgnFen("lichess_UsN4NLQ4.pgn", "8/8/6p1/5pkp/8/7P/3r2r1/7K w - - 0 45"));
    list.add(new PgnFen("lichess_UuNO3dGf.pgn", "8/8/1P6/8/1R6/P4k2/8/1r3K2 w - - 4 72"));
    list.add(new PgnFen("lichess_UVt2ZNXy.pgn", "8/8/6pk/8/6Pp/6nP/1r6/6K1 w - - 3 59"));
    list.add(new PgnFen("lichess_uwwUPCFw.pgn", "8/8/7p/7k/6R1/4K3/8/8 b - - 0 58"));
    list.add(new PgnFen("lichess_ux9sCCZ8.pgn", "4rR1k/p5p1/6P1/2pp1K2/1p1b2P1/4q3/P1P5/8 b - - 3 34"));
    list.add(new PgnFen("lichess_V08kX4kz.pgn", "8/6b1/1p3k2/1Pp1p1p1/2P1PpP1/5P2/8/5K2 b - - 11 61"));
    list.add(new PgnFen("lichess_v4xJHVln.pgn", "8/8/8/8/7p/6p1/3K2pk/7Q b - - 3 59"));
    list.add(new PgnFen("lichess_vaTHx2Ow.pgn", "7k/7P/5K2/8/8/8/8/8 b - - 0 74"));
    list.add(new PgnFen("lichess_VCzr8ukR.pgn", "8/8/3k4/p3p2p/Pp1pP2P/1P1P4/5K2/8 w - - 0 44"));
    list.add(new PgnFen("lichess_VdYuP1l4.pgn", "8/8/8/p7/8/8/1Q5K/k7 b - - 0 62"));
    list.add(new PgnFen("lichess_VeCbG5uw.pgn", "8/8/8/8/5kP1/8/5q2/5K2 w - - 0 59"));
    list.add(new PgnFen("lichess_VIdrelSz.pgn", "7r/2PR4/6pk/6q1/5P1K/r7/8/8 w - - 0 40"));
    list.add(new PgnFen("lichess_Vnqfiwd6.pgn", "6kR/5pp1/1K2p1p1/3r4/8/8/8/8 b - - 3 48"));
    list.add(new PgnFen("lichess_VpZzyQaL.pgn", "8/3k4/3p4/2pP1p1p/p1P2P1P/P2K4/8/8 b - - 6 46"));
    list.add(new PgnFen("lichess_vS3VdvtP.pgn", "3Q2k1/6pp/4K3/4p3/3q4/2b5/8/8 b - - 0 44"));
    list.add(new PgnFen("lichess_vWvNhvif.pgn", "8/5k2/3p2p1/1p1Pp1P1/pP2Pp2/P4P2/8/2K5 b - - 16 65"));
    list.add(new PgnFen("lichess_vytUGbcM.pgn", "7K/6Pq/8/3k4/8/8/8/8 w - - 0 74"));
    list.add(new PgnFen("lichess_vZUevQUB.pgn", "8/8/4k2p/1p1p2pP/1P1P2P1/8/2K5/8 b - - 56 73"));
    list.add(new PgnFen("lichess_W4wkO3cX.pgn", "5k2/6p1/1p2p1P1/pP1pP2K/P2P4/8/8/8 b - - 26 81"));
    list.add(new PgnFen("lichess_W5huoA6x.pgn", "2Q5/pk6/8/2K5/8/8/8/8 b - - 1 69"));
    list.add(new PgnFen("lichess_w5N015UJ.pgn", "k7/P7/2K5/8/8/8/8/8 b - - 2 59"));
    list.add(new PgnFen("lichess_wCL9HARO.pgn", "6K1/6q1/8/8/8/7k/7P/8 w - - 0 56"));
    list.add(new PgnFen("lichess_WdedP6nc.pgn", "8/8/8/6K1/8/1p6/k7/Q7 b - - 0 59"));
    list.add(new PgnFen("lichess_WeqThgc0.pgn", "8/p1k5/P1p5/2Pp3p/3P1p1P/5P1K/8/8 b - - 0 47"));
    list.add(new PgnFen("lichess_WEYtzOxf.pgn", "8/5k2/1p3p1p/1Pp1pP1P/2P1P3/3K4/8/8 b - - 7 69"));
    list.add(new PgnFen("lichess_WIQLYQ5T.pgn", "8/1p6/pPp1p2k/P1PpP1p1/3P2P1/8/8/6K1 b - - 1 58"));
    list.add(new PgnFen("lichess_wJymwD30.pgn", "8/8/8/8/7R/6pk/r6p/7K b - - 3 59"));
    list.add(new PgnFen("lichess_wOF3za6s.pgn", "8/2k5/1p2p1p1/1P1pP1Pp/3P3P/8/3K4/8 w - - 75 90"));
    list.add(new PgnFen("lichess_wUCLya3K.pgn", "8/5p2/1K4p1/8/8/8/3Q4/3k4 b - - 0 67"));
    list.add(new PgnFen("lichess_wuHnMP2q.pgn", "7R/7p/p5p1/8/2p4P/5P1k/P2q3Q/5RK1 b - - 3 33"));
    list.add(new PgnFen("lichess_WxdB1E0N.pgn", "8/8/8/8/8/8/p1k5/K7 w - - 2 62"));
    list.add(new PgnFen("lichess_xD85FRxa.pgn", "6qK/8/4k3/6B1/8/8/5P2/8 w - - 0 55"));
    list.add(new PgnFen("lichess_xfvaW7PK.pgn", "6qK/5k1P/8/8/8/8/8/8 w - - 1 51"));
    list.add(new PgnFen("lichess_xgHJ9FM2.pgn", "k7/8/6p1/1p1p1pP1/pP1PpP2/P3P3/3K4/8 w - - 40 62"));
    list.add(new PgnFen("lichess_XIfR5l9e.pgn", "8/8/4b3/5k2/p1p1pBp1/PpP1P1Pp/1P3K1P/8 w - - 34 76"));
    list.add(new PgnFen("lichess_xmpqlKFo.pgn", "8/Kq6/8/8/8/8/P7/5k2 w - - 0 54"));
    list.add(new PgnFen("lichess_XooYe68z.pgn", "8/6k1/6p1/4p1P1/p1p1P1K1/P1P5/2P5/8 b - - 26 54"));
    list.add(new PgnFen("lichess_Xp7OUyAx.pgn", "8/3k4/1p2p1p1/1P2P1Pp/3K3P/8/8/8 w - - 19 55"));
    list.add(new PgnFen("lichess_xqzt1PdW.pgn", "7k/7P/5K2/8/8/8/8/8 b - - 0 58"));
    list.add(new PgnFen("lichess_XtJJQxwF.pgn", "8/8/8/8/8/5K1k/6p1/7R b - - 0 63"));
    list.add(new PgnFen("lichess_XtvgUXHD.pgn", "8/p6p/6Qk/6q1/1K6/8/8/8 b - - 5 72"));
    list.add(new PgnFen("lichess_Y6dvuR4W.pgn", "8/8/8/1k4p1/1p2p1P1/1P2P3/2K5/8 w - - 9 48"));
    list.add(new PgnFen("lichess_yEtZYyMQ.pgn", "8/4k3/4p1p1/2p1P1Pp/1pP2K1P/1P6/8/8 w - - 3 42"));
    list.add(new PgnFen("lichess_YGj1C2WB.pgn", "8/8/8/3K4/8/5kQ1/4p3/8 b - - 5 69"));
    list.add(new PgnFen("lichess_YI1t9qvC.pgn", "7k/5K1P/8/8/8/8/8/8 b - - 2 74"));
    list.add(new PgnFen("lichess_YictG6WZ.pgn", "8/6k1/8/5RPK/7r/8/8/8 w - - 6 51"));
    list.add(new PgnFen("lichess_yjfLthhQ.pgn", "1K6/8/8/8/3p4/4kQ2/3p4/8 b - - 6 58"));
    list.add(new PgnFen("lichess_YoQvXlEO.pgn", "8/6Q1/5Q1p/7k/3P4/1PP2RBK/7P/5q2 w - - 5 54"));
    list.add(new PgnFen("lichess_yQOHgRpo.pgn", "7k/6pp/7P/8/8/4q3/2r5/7K w - - 6 57"));
    list.add(new PgnFen("lichess_yrg4ColU.pgn", "8/8/2k5/2p3p1/p1P1p1P1/P3P3/6K1/8 b - - 18 53"));
    list.add(new PgnFen("lichess_YRvWOIpy.pgn", "8/R7/Kr6/P6k/8/8/8/8 w - - 1 57"));
    list.add(new PgnFen("lichess_YUaykh8t.pgn", "8/8/8/8/1p4P1/1QK5/p4P2/k7 w - - 0 49"));
    list.add(new PgnFen("lichess_yVPekOlc.pgn", "8/8/8/8/8/8/5k1p/7K w - - 0 67"));
    list.add(new PgnFen("lichess_yxiNrimN.pgn", "k7/8/p1p2p1p/P1P2P1P/4K3/8/8/8 w - - 28 69"));
    list.add(new PgnFen("lichess_yxUT9b2S.pgn", "8/2k5/2p1p1p1/1pP1P1Pp/1P5P/7K/8/8 b - - 49 78"));
    list.add(new PgnFen("lichess_z3xkCyBY.pgn", "7k/2Q4q/7K/2p3P1/2P5/8/8/8 w - - 6 59"));
    list.add(new PgnFen("lichess_Z5BCc8QN.pgn", "8/8/8/8/8/8/p1k5/K7 w - - 2 59"));
    list.add(new PgnFen("lichess_z9uuT4Ei.pgn", "8/4k3/p1p2p2/P1P2P1p/2P2P1P/2K5/8/8 b - - 30 66"));
    list.add(new PgnFen("lichess_zDsWuY13.pgn", "8/8/3k2p1/1p2p1Pp/1P2P2P/3K4/8/8 b - - 15 54"));
    list.add(new PgnFen("lichess_zIC5NqLC.pgn", "k7/8/KP6/P7/8/1r6/8/8 w - - 1 58"));
    list.add(new PgnFen("lichess_zmelXKvA.pgn", "8/6p1/8/8/6p1/6rk/3K3R/8 b - - 10 58"));
    list.add(new PgnFen("lichess_ZNs9RrhS.pgn", "7k/5R2/6RK/7r/6PP/8/8/8 w - - 2 54"));
    list.add(new PgnFen("lichess_Zqc7ug8u.pgn", "7R/6pk/8/6K1/2n1r3/8/8/8 b - - 9 61"));

    list.add(new PgnFen("lichess_f6c1lu7R.pgn", "8/8/7p/5p1P/5p1K/5Pp1/6P1/1k6 w - - 70 83"));

    return new PgnTestCaseList(PgnTest.CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR, list);
  }

  private static PgnTestCaseList createTestCasesChaLichessQuickNotDepthThreeHelpmate() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("lichess_03VIxJUJ_helpmate.pgn", "8/8/1p6/2p5/8/6k1/8/1r4K1 w - - 8 70"));
    list.add(new PgnFen("lichess_0dKUIfwq_helpmate.pgn", "8/8/8/8/k1K5/8/8/Q7 b - - 24 89"));
    list.add(new PgnFen("lichess_0ehvDno9_helpmate.pgn", "5Q2/P6k/8/8/4Q3/8/1B6/4R1K1 b - - 2 61"));
    list.add(new PgnFen("lichess_0u6eh8ZP_helpmate.pgn", "1k1Q4/8/K7/8/8/8/8/8 b - - 10 61"));
    list.add(new PgnFen("lichess_0xMzcUDT_helpmate.pgn", "k7/8/1K6/8/5Q2/8/8/Q3Q3 b - - 18 88"));
    list.add(new PgnFen("lichess_16myWOwv_helpmate.pgn", "7q/8/8/8/8/8/8/5k1K w - - 5 78"));
    list.add(new PgnFen("lichess_1rcB0Tru_helpmate.pgn", "8/4K1k1/2b2q2/1p6/8/8/8/2q5 w - - 3 52"));
    list.add(new PgnFen("lichess_29moHRC8_helpmate.pgn", "8/6p1/8/8/8/7k/5K2/4q2q w - - 4 65"));
    list.add(new PgnFen("lichess_2gCvxApk_helpmate.pgn", "Q7/8/1k6/Q7/8/8/8/5K2 b - - 4 69"));
    list.add(new PgnFen("lichess_2k5piUl6_helpmate.pgn", "k1Q5/8/K7/8/8/8/8/8 b - - 0 72"));
    list.add(new PgnFen("lichess_2mgjVvcC_helpmate.pgn", "8/2p4p/p6k/5pQ1/3b3K/6P1/PP5P/5q2 b - - 0 32"));
    list.add(new PgnFen("lichess_2pwjX9B1_helpmate.pgn", "8/8/8/p7/6k1/8/5r1K/1q2q3 w - - 9 60"));
    list.add(new PgnFen("lichess_2sFHWmg3_helpmate.pgn", "8/8/8/8/k1K5/8/8/Q7 b - - 20 70"));
    list.add(new PgnFen("lichess_2wVTK5vV_helpmate.pgn", "8/8/8/k1K5/8/8/8/Q7 b - - 18 72"));
    list.add(new PgnFen("lichess_3bKyjTHM_helpmate.pgn", "8/7q/8/7K/8/p7/k7/5qq1 w - - 2 65"));
    list.add(new PgnFen("lichess_3cYOGNS6_helpmate.pgn", "Q4Q2/8/8/8/8/1k1K3Q/7P/1Q6 b - - 8 57"));
    list.add(new PgnFen("lichess_3jesiit8_helpmate.pgn", "Q7/8/1k1Q4/8/2P2P2/6P1/4K3/8 b - - 2 69"));
    list.add(new PgnFen("lichess_3xFc21eB_helpmate.pgn", "k1R5/8/K7/8/8/8/8/8 b - - 9 82"));
    list.add(new PgnFen("lichess_49QwULSR_helpmate.pgn", "8/8/8/8/8/7k/8/3q3K w - - 3 76"));
    list.add(new PgnFen("lichess_55wBEu8Z_helpmate.pgn", "8/p1r5/5R1p/Pp1p2Pk/3PpK2/4Pp1Q/5P2/8 b - - 0 43"));
    list.add(new PgnFen("lichess_6kfJPvsA_helpmate.pgn", "8/8/8/8/8/7k/5K2/4q2q w - - 12 73"));
    list.add(new PgnFen("lichess_78XhRaIr_helpmate.pgn", "8/8/8/8/2K5/k7/8/Q7 b - - 26 87"));
    list.add(new PgnFen("lichess_7alMRe6y_helpmate.pgn", "8/8/2p5/r3qk2/8/8/7K/1b3q2 w - - 2 68"));
    list.add(new PgnFen("lichess_7fWsFh0J_helpmate.pgn", "8/8/8/8/8/3q1K1k/7q/8 w - - 6 66"));
    list.add(new PgnFen("lichess_8cTl7SrQ_helpmate.pgn", "8/8/8/8/8/k1b5/b7/K7 w - - 32 70"));
    list.add(new PgnFen("lichess_APEww4mD_helpmate.pgn", "8/8/8/8/2r5/6K1/6q1/6k1 w - - 6 89"));
    list.add(new PgnFen("lichess_B8bpMZ71_helpmate.pgn", "2Q5/8/8/6B1/3QkPK1/PP2P1P1/8/5R2 b - - 2 61"));
    list.add(new PgnFen("lichess_bfJbt6GP_helpmate.pgn", "8/7p/8/6p1/8/2Kq3k/8/1q5n w - - 2 73"));
    list.add(new PgnFen("lichess_bQ0EXLal_helpmate.pgn", "1b6/1p4pp/8/8/8/k7/8/K6r w - - 11 59"));
    list.add(new PgnFen("lichess_cZ8QxYP8_helpmate.pgn", "7k/7Q/7K/8/2PP3P/4P1P1/8/2BQ1B1R b - - 10 52"));
    list.add(new PgnFen("lichess_D4vIOthl_helpmate.pgn", "8/6k1/8/8/8/8/5K2/4q2q w - - 10 76"));
    list.add(new PgnFen("lichess_dBergEkt_helpmate.pgn", "8/8/8/8/6k1/8/q5K1/1q6 w - - 26 74"));
    list.add(new PgnFen("lichess_DiKq9jcy_helpmate.pgn", "4Q2Q/8/5k2/3Q4/1BK5/2P5/8/8 b - - 0 70"));
    list.add(new PgnFen("lichess_E9P918aj_helpmate.pgn", "1k1Q4/8/K7/8/8/8/8/8 b - - 30 70"));
    list.add(new PgnFen("lichess_eExHpNDf_helpmate.pgn", "6Q1/k7/8/8/Q7/3K4/8/1R6 b - - 8 99"));
    list.add(new PgnFen("lichess_eKwKl6Y9_helpmate.pgn", "k7/1Q6/K7/8/8/8/8/8 b - - 30 88"));
    list.add(new PgnFen("lichess_EQCMW0jB_helpmate.pgn", "8/8/8/8/r7/q4K1k/7q/8 w - - 8 66"));
    list.add(new PgnFen("lichess_EsFX1Urt_helpmate.pgn", "8/8/8/8/8/7k/6q1/6K1 w - - 14 67"));
    list.add(new PgnFen("lichess_EstAIWqd_helpmate.pgn", "8/8/8/8/8/5K1k/8/4rq2 w - - 10 76"));
    list.add(new PgnFen("lichess_Ew7uTqu0_helpmate.pgn", "8/5pp1/8/8/p7/3b3k/8/r6K w - - 12 59"));
    list.add(new PgnFen("lichess_f7x3bSzh_helpmate.pgn", "k6r/p4p1p/2B3p1/8/8/N3b1P1/P4p1P/1R5K b - - 0 34"));
    list.add(new PgnFen("lichess_FMDN8gJ7_helpmate.pgn", "8/8/8/8/8/1k2b3/2p5/2K5 w - - 2 73"));
    list.add(new PgnFen("lichess_fN4vRqp0_helpmate.pgn", "8/8/8/8/8/8/5KQk/5R2 b - - 2 74"));
    list.add(new PgnFen("lichess_fPnoYQtW_helpmate.pgn", "8/8/8/8/8/7k/5K2/4q2q w - - 8 66"));
    list.add(new PgnFen("lichess_FYTeO80q_helpmate.pgn", "8/6Qp/p5p1/7k/P4PB1/7K/1P3R1P/8 b - - 0 38"));
    list.add(new PgnFen("lichess_G0xDBYwE_helpmate.pgn", "8/k1Q5/1N6/K7/8/8/8/8 b - - 20 72"));
    list.add(new PgnFen("lichess_G8i5Bn7F_helpmate.pgn", "8/8/8/8/8/7k/8/q6K w - - 28 73"));
    list.add(new PgnFen("lichess_GCuwJ5bP_helpmate.pgn", "1k1Q4/8/K7/8/8/8/8/8 b - - 18 64"));
    list.add(new PgnFen("lichess_GIwBNz5S_helpmate.pgn", "8/7q/8/8/8/8/8/5k1K w - - 12 80"));
    list.add(new PgnFen("lichess_gtzPUYWo_helpmate.pgn", "8/8/8/8/8/4kp2/4rp2/3q1K2 w - - 0 71"));
    list.add(new PgnFen("lichess_GzoCTk46_helpmate.pgn", "8/8/8/8/7p/4qK1k/8/6q1 w - - 6 74"));
    list.add(new PgnFen("lichess_hdott1S4_helpmate.pgn", "7N/5Q2/5k1K/8/8/8/7B/8 b - - 1 78"));
    list.add(new PgnFen("lichess_hkiG96je_helpmate.pgn", "8/8/8/8/k1K5/8/8/Q7 b - - 24 73"));
    list.add(new PgnFen("lichess_IcjtzT9i_helpmate.pgn", "k7/8/1K6/8/8/6Q1/8/Q7 b - - 28 100"));
    list.add(new PgnFen("lichess_if6AKvpb_helpmate.pgn", "k3R3/8/K7/P7/1P3P2/8/8/8 b - - 2 60"));
    list.add(new PgnFen("lichess_ig7fudY7_helpmate.pgn", "8/8/8/8/8/5K1k/8/4rq2 w - - 8 59"));
    list.add(new PgnFen("lichess_Iy1VEUMC_helpmate.pgn", "4K3/8/Q7/8/8/k7/8/1Q6 b - - 6 67"));
    list.add(new PgnFen("lichess_j17qnxd0_helpmate.pgn", "8/8/8/8/8/7k/8/q6K w - - 24 85"));
    list.add(new PgnFen("lichess_j4phmaNj_helpmate.pgn", "8/8/8/8/k1K5/8/8/Q7 b - - 20 81"));
    list.add(new PgnFen("lichess_jehHS0Jj_helpmate.pgn", "8/8/8/8/2K5/k7/8/Q7 b - - 10 75"));
    list.add(new PgnFen("lichess_JfHZLRvD_helpmate.pgn", "r7/pp6/3p1p2/2p1k3/4q2P/5KP1/5P2/3R3R w - - 0 34"));
    list.add(new PgnFen("lichess_jK46bYGo_helpmate.pgn", "8/8/8/8/8/6pk/8/q5K1 w - - 2 68"));
    list.add(new PgnFen("lichess_jnox9kxK_helpmate.pgn", "8/7q/8/8/8/8/8/5k1K w - - 12 100"));
    list.add(new PgnFen("lichess_jnvJUlDw_helpmate.pgn", "8/2p5/8/8/8/7k/4K3/3q2qn w - - 2 69"));
    list.add(new PgnFen("lichess_KbEG3yK3_helpmate.pgn", "8/8/8/8/8/7k/b7/3q3K w - - 18 76"));
    list.add(new PgnFen("lichess_kbutR5IJ_helpmate.pgn", "2k1q3/2p5/2K5/2N5/2q5/8/8/8 w - - 0 54"));
    list.add(new PgnFen("lichess_Kdf0f1OE_helpmate.pgn", "8/8/8/8/8/7k/8/q6K w - - 16 80"));
    list.add(new PgnFen("lichess_KE47c49r_helpmate.pgn", "8/6p1/6b1/8/8/7k/8/3q3K w - - 13 66"));
    list.add(new PgnFen("lichess_knQsF7R8_helpmate.pgn", "k7/4K3/8/8/8/8/1Q6/Q7 b - - 12 60"));
    list.add(new PgnFen("lichess_KP23RUWa_helpmate.pgn", "k7/8/8/8/2K5/Q7/8/1Q6 b - - 6 75"));
    list.add(new PgnFen("lichess_KPvcgsfS_helpmate.pgn", "k5Q1/8/K7/8/8/8/P7/8 b - - 0 58"));
    list.add(new PgnFen("lichess_Ks0yR98N_helpmate.pgn", "k7/8/1K6/8/8/6Q1/8/Q7 b - - 32 78"));
    list.add(new PgnFen("lichess_LC76ur18_helpmate.pgn", "8/8/8/3b4/3Kq3/8/8/2qk4 w - - 2 75"));
    list.add(new PgnFen("lichess_lHtqM0fz_helpmate.pgn", "8/8/8/8/8/7k/8/q6K w - - 12 82"));
    list.add(new PgnFen("lichess_lk42iihk_helpmate.pgn", "k6Q/8/K7/8/8/8/R7/8 b - - 29 58"));
    list.add(new PgnFen("lichess_LqTNOgyT_helpmate.pgn", "4Q2Q/5k2/8/8/8/3K4/8/3Q4 b - - 2 64"));
    list.add(new PgnFen("lichess_lYf34Uiq_helpmate.pgn", "8/8/8/8/8/7k/6q1/6K1 w - - 22 82"));
    list.add(new PgnFen("lichess_m5WDCW16_helpmate.pgn", "5Q2/2k4R/K7/8/6PP/8/8/7B b - - 14 58"));
    list.add(new PgnFen("lichess_McLow5Hz_helpmate.pgn", "5Q1Q/2k3R1/8/1K6/8/8/B5P1/8 b - - 22 60"));
    list.add(new PgnFen("lichess_MvEif0NV_helpmate.pgn", "k7/8/1K6/8/8/8/7Q/Q7 b - - 24 88"));
    list.add(new PgnFen("lichess_N6AYM22R_helpmate.pgn", "8/6p1/7p/8/8/7k/q7/5q1K w - - 0 73"));
    list.add(new PgnFen("lichess_nvxFBquo_helpmate.pgn", "2QQ4/5k1K/8/8/8/5R2/8/8 b - - 2 68"));
    list.add(new PgnFen("lichess_om0bCR5w_helpmate.pgn", "q7/8/8/5k2/8/8/5K2/1r2q3 w - - 8 61"));
    list.add(new PgnFen("lichess_Omq6Fdhm_helpmate.pgn", "7q/8/8/8/8/8/8/1q3k1K w - - 2 83"));
    list.add(new PgnFen("lichess_ooSZuhhE_helpmate.pgn", "8/8/8/8/8/7k/8/q6K w - - 26 59"));
    list.add(new PgnFen("lichess_p4CeCK3J_helpmate.pgn", "k7/1Q6/K7/8/8/8/8/6Q1 b - - 13 76"));
    list.add(new PgnFen("lichess_PAjZeTkN_helpmate.pgn", "1Q6/k7/N7/8/R7/K7/8/8 b - - 0 69"));
    list.add(new PgnFen("lichess_pFsUTatm_helpmate.pgn", "2QQ4/k4K2/8/8/4P3/8/8/Q7 b - - 2 66"));
    list.add(new PgnFen("lichess_PIL4PUtT_helpmate.pgn", "Q1Q5/8/7Q/8/1PB4k/8/5P2/R4RK1 b - - 2 54"));
    list.add(new PgnFen("lichess_PkZ6qiA6_helpmate.pgn", "8/8/3p4/4k3/8/6K1/8/6qq w - - 2 65"));
    list.add(new PgnFen("lichess_QirOKYTP_helpmate.pgn", "r1b5/ppppn2p/5Qp1/8/1P2q3/N1Pk4/P2b1P1P/3RKR2 w - - 2 21"));
    list.add(new PgnFen("lichess_QnFE4Znl_helpmate.pgn", "6QQ/2k1Q3/8/1K6/8/8/8/8 b - - 12 69"));
    list.add(new PgnFen("lichess_RF0MOp86_helpmate.pgn", "8/1kp5/3p4/8/p7/r7/2rK4/1q6 w - - 2 66"));
    list.add(new PgnFen("lichess_RSqLhejA_helpmate.pgn", "8/8/8/4p3/8/5Knk/2p1q3/2r5 w - - 14 73"));
    list.add(new PgnFen("lichess_RvN1zpCB_helpmate.pgn", "6nr/6pp/4pp2/8/1PP5/P4P1k/7q/3B1RK1 w - - 0 26"));
    list.add(new PgnFen("lichess_rVN4T4Tz_helpmate.pgn", "1k1Q4/8/K7/8/8/8/8/8 b - - 18 81"));
    list.add(new PgnFen("lichess_SDy9Y3D0_helpmate.pgn", "8/8/8/8/8/7k/8/4q2K w - - 0 66"));
    list.add(new PgnFen("lichess_Sdz1yTzS_helpmate.pgn", "1k1Q4/8/K7/8/8/8/8/8 b - - 26 80"));
    list.add(new PgnFen("lichess_sMv8Hh43_helpmate.pgn", "8/6p1/5kP1/2r4K/5q2/8/8/8 w - - 2 53"));
    list.add(new PgnFen("lichess_sp0oSyY2_helpmate.pgn", "8/8/1p6/8/8/7k/b7/q6K w - - 2 57"));
    list.add(new PgnFen("lichess_SwCSdv8K_helpmate.pgn", "8/8/8/8/8/7k/6q1/6K1 w - - 22 75"));
    list.add(new PgnFen("lichess_SX3iSehH_helpmate.pgn", "Q4K2/8/1k6/Q7/8/8/8/8 b - - 10 63"));
    list.add(new PgnFen("lichess_Szu1FAr2_helpmate.pgn", "8/pp4p1/6p1/8/8/7k/8/q6K w - - 13 60"));
    list.add(new PgnFen("lichess_U5uv4wPs_helpmate.pgn", "8/8/8/6p1/8/7k/8/r6K w - - 5 90"));
    list.add(new PgnFen("lichess_uBSFb2kx_helpmate.pgn", "5B1k/8/8/8/8/7R/Q5K1/8 b - - 10 100"));
    list.add(new PgnFen("lichess_UdUE6AvZ_helpmate.pgn", "1k1Q4/8/K7/8/8/8/8/8 b - - 16 59"));
    list.add(new PgnFen("lichess_UuNO3dGf_helpmate.pgn", "kQ6/8/8/8/8/8/7Q/5K2 b - - 12 85"));
    list.add(new PgnFen("lichess_uwwUPCFw_helpmate.pgn", "8/8/8/8/8/7k/6q1/6K1 w - - 14 71"));
    list.add(new PgnFen("lichess_ux9sCCZ8_helpmate.pgn", "5r1k/p5p1/6P1/2pp1K2/1p1b2P1/4q3/P1P5/8 w - - 0 35"));
    list.add(new PgnFen("lichess_v4xJHVln_helpmate.pgn", "8/8/8/8/8/7k/4Kn2/4q2q w - - 4 76"));
    list.add(new PgnFen("lichess_VdYuP1l4_helpmate.pgn", "7q/8/8/8/8/8/8/5k1K w - - 14 74"));
    list.add(new PgnFen("lichess_VeCbG5uw_helpmate.pgn", "1k1Q4/8/K7/8/8/8/8/8 b - - 26 77"));
    list.add(new PgnFen("lichess_VIdrelSz_helpmate.pgn", "7r/2PR4/6pk/6P1/7K/r7/8/8 b - - 0 40"));
    list.add(new PgnFen("lichess_Vnqfiwd6_helpmate.pgn", "8/6p1/5p2/8/6k1/8/q5K1/1q6 w - - 8 67"));
    list.add(new PgnFen("lichess_vS3VdvtP_helpmate.pgn", "8/8/8/8/6k1/8/5K2/4q2q w - - 12 68"));
    list.add(new PgnFen("lichess_vytUGbcM_helpmate.pgn", "8/8/8/8/2K5/k7/8/Q7 b - - 22 86"));
    list.add(new PgnFen("lichess_W5huoA6x_helpmate.pgn", "8/8/8/8/8/7k/8/q6K w - - 26 89"));
    list.add(new PgnFen("lichess_wCL9HARO_helpmate.pgn", "1k1Q4/8/K7/8/8/8/8/8 b - - 14 75"));
    list.add(new PgnFen("lichess_WdedP6nc_helpmate.pgn", "7q/8/8/8/8/8/8/5k1K w - - 22 73"));
    list.add(new PgnFen("lichess_wUCLya3K_helpmate.pgn", "8/8/8/8/8/4qK1k/8/6q1 w - - 6 86"));
    list.add(new PgnFen("lichess_wuHnMP2q_helpmate.pgn", "7R/7p/p5p1/8/2p4P/5P1k/P6q/5RK1 w - - 0 34"));
    list.add(new PgnFen("lichess_xD85FRxa_helpmate.pgn", "1k1Q4/8/K7/8/8/8/8/8 b - - 30 81"));
    list.add(new PgnFen("lichess_xfvaW7PK_helpmate.pgn", "8/8/8/8/2K5/k7/8/Q7 b - - 22 62"));
    list.add(new PgnFen("lichess_xmpqlKFo_helpmate.pgn", "1k1Q4/8/K7/8/8/8/8/8 b - - 18 69"));
    list.add(new PgnFen("lichess_XtJJQxwF_helpmate.pgn", "8/8/8/8/8/7k/6q1/6K1 w - - 10 69"));
    list.add(new PgnFen("lichess_XtvgUXHD_helpmate.pgn", "8/8/8/8/6k1/8/q7/1q5K w - - 14 93"));
    list.add(new PgnFen("lichess_YGj1C2WB_helpmate.pgn", "8/8/8/8/8/7k/6q1/6K1 w - - 16 79"));
    list.add(new PgnFen("lichess_YictG6WZ_helpmate.pgn", "k1K5/8/8/8/8/8/8/R7 b - - 37 73"));
    list.add(new PgnFen("lichess_yjfLthhQ_helpmate.pgn", "8/8/8/8/8/5K1k/6q1/2q5 w - - 12 70"));
    list.add(new PgnFen("lichess_YRvWOIpy_helpmate.pgn", "8/8/KQk5/8/8/8/8/3R4 b - - 10 64"));
    list.add(new PgnFen("lichess_z3xkCyBY_helpmate.pgn", "7k/7Q/7K/2p3P1/2P5/8/8/8 b - - 0 59"));
    list.add(new PgnFen("lichess_zmelXKvA_helpmate.pgn", "8/q7/8/8/8/8/K1k5/4b3 w - - 14 80"));
    list.add(new PgnFen("lichess_V7eJ1RR9_helpmate.pgn", "8/8/8/8/8/7k/8/q6K w - - 34 74"));

    return new PgnTestCaseList(PgnTest.CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR_WINNABLE_FOR_FLAGGING_WITH_HELPMATE, list);
  }

  private static PgnTestCaseList createTestCasesChaLichessQuickDepthThree() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("lichess_pUEeHLfu.pgn", "7k/5Q2/6Q1/6P1/5P1p/6K1/7P/8 w - - 0 78"));
    list.add(new PgnFen("lichess_UNX9jAKK.pgn", "8/p4kp1/6Pp/7K/2rq4/7P/8/8 b - - 0 51"));

    return new PgnTestCaseList(PgnTest.CHA_LICHESS_QUICK_DEPTH_THREE, list);
  }

  private static PgnTestCaseList createTestCasesChaLichessQuickDepthFour() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("lichess_mf4MFw9v.pgn", "8/8/8/8/pp6/k1p5/1qQ5/K7 w - - 2 72"));

    return new PgnTestCaseList(PgnTest.CHA_LICHESS_QUICK_DEPTH_FOUR, list);
  }

  private static PgnTestCaseList createTestCasesChaAmbrona() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("ambrona_01.pgn", "5brk/4p1p1/3pP1P1/1B1P2p1/3p2p1/3P4/4K1P1/8 w - - 10 100"));
    list.add(new PgnFen("ambrona_02.pgn", "8/1p4p1/1Pp3p1/k1P3p1/1pP3Pb/1P4p1/6P1/7K w - - 10 100"));
    list.add(new PgnFen("ambrona_03.pgn", "8/1k5B/7b/8/1p1p1p1p/1PpP1P1P/2P3K1/N3b3 b - - 10 100"));
    list.add(new PgnFen("ambrona_04.pgn", "7b/1k5B/7b/8/1p1p1p1p/1PpP1P1P/2P3K1/N7 b - - 10 100"));
    list.add(new PgnFen("ambrona_04_proof_game.pgn", "7b/1k5B/7b/8/1p1p1p1p/1PpP1P1P/2P1P1K1/N7 b - - 6 56"));
    list.add(new PgnFen("ambrona_05.pgn", "4K3/8/8/8/8/p1p2p1p/P1pppp1P/bnrqkrnb b - - 10 100"));
    list.add(new PgnFen("ambrona_06.pgn", "k1bK4/1p1p4/1PpPp3/2P1Pp2/2p1pP2/2p1P3/2P5/8 w - - 10 100"));
    list.add(new PgnFen("ambrona_07.pgn", "Bb2kb2/bKp1p1p1/1pP1P1P1/pP6/6P1/P7/8/8 b - - 10 100"));
    list.add(new PgnFen("ambrona_08.pgn", "Bb2kb2/bKp1p1p1/1pP1P1P1/1P6/p5P1/P7/8/8 b - - 10 100"));

    // cannot use as position illegal (too many white light-squared bishops)
    // nineth example from Ambrona, file name would become as below, but no such file as not used
    // ae_09.pgn

    list.add(new PgnFen("ambrona_10.pgn", "7k/8/1p6/1Pp5/2Pp4/pB1Pp1p1/P1B1P1P1/3B2K1 b - - 10 100"));

    list.add(new PgnFen("ambrona_11_lichess_FKr42ZRT.pgn", "8/8/7p/5p1P/5p1K/5Pp1/6P1/5kb1 b - - 13 63"));
    list.add(new PgnFen("ambrona_12_lichess_bKHPqNEw.pgn", "1k6/1P5p/BP3p2/1P6/8/8/5PKP/8 b - - 0 41"));
    list.add(new PgnFen("ambrona_13_lichess_OawUhnkq.pgn", "5r1k/6P1/7K/5q2/8/8/8/8 b - - 0 51"));
    list.add(new PgnFen("ambrona_14.pgn", "k7/Q6r/2b5/1pBp1p1p/1P1P1P1P/KP6/1P6/8 b - - 10 100"));
    list.add(new PgnFen("ambrona_15_lichess_QRvIMh3z.pgn", "2b5/1p6/pPp3k1/2Pp3p/P2PpBpP/4P1P1/5K2/8 b - - 46 59"));
    list.add(new PgnFen("ambrona_16.pgn", "1k6/p1p1p1p1/P1P1P1P1/p1p1p1p1/8/8/P1P1P1P1/4K3 w - - 10 100"));
    list.add(new PgnFen("ambrona_17.pgn", "rnb1b3/pk1p4/p1pPp1p1/P1P1P1P1/RBP5/P7/5B2/7K w - - 10 100"));

    return new PgnTestCaseList(PgnTest.CHA_AMBRONA, list);
  }

  // Fixtures the geometric pawn-wall classifier accepts as YES: chain spans file a to h, both kings on opposite
  // sides, every pawn on the board is either a chain element or attacks a chain element.
  private static PgnTestCaseList createTestCasesPawnWallYes() {
    final List<PgnFen> list = new ArrayList<>();

    // pawn walls without bishops - clean horizontal / zig-zag
    list.add(new PgnFen("pawn_wall_horizontal_1.pgn", "4k3/8/8/p1p1p1p1/P1P1P1P1/8/8/4K3 w - - 0 50"));

    list.add(new PgnFen("pawn_wall_horizontal_2.pgn", "4k3/8/8/p1p2p1p/P1P2P1P/8/8/4K3 w - - 0 50"));

    list.add(new PgnFen("pawn_wall_zig_zag_no_holes.pgn", "4k3/5p1p/4pPpP/1p1pP1P1/pPpP4/P1P5/8/4K3 w - - 0 50"));

    list.add(new PgnFen("pawn_wall_zig_zag_holes_1.pgn", "4k3/5p1p/4pP1P/3pP3/p1pP4/P1P5/8/4K3 w - - 0 50"));

    list.add(new PgnFen("pawn_wall_zig_zag_holes_2.pgn", "4k3/5p1p/1p2pP1P/pPp1P3/P1P5/8/8/4K3 w - - 0 50"));

    // pawn walls with bishops - colour-locked, all pawns involved
    list.add(new PgnFen("pawn_wall_bishop_1.pgn", "8/6k1/p1p1p3/P1P1Pp1p/5P1P/2B5/6K1/8 b - - 0 50"));

    list.add(new PgnFen("pawn_wall_bishop_2.pgn", "2b5/6k1/p1p1p3/P1P1Pp1p/5P1P/8/6K1/8 b - - 0 50"));

    list.add(new PgnFen("pawn_wall_bishop_3.pgn", "2b5/6k1/p1p1p3/P1P1Pp1p/5P1P/2B5/6K1/8 b - - 0 50"));

    list.add(new PgnFen("pawn_wall_bishop_4.pgn", "8/6k1/p1p1p3/P1P1Pp1p/5P1P/1bB5/6K1/8 b - - 0 50"));

    list.add(new PgnFen("pawn_wall_bishop_5.pgn", "3B4/1b4k1/p1p1p3/P1P1Pp1p/5P1P/8/6K1/8 b - - 0 50"));

    // interlocked pawn structure: rank-3 own pawns attack the rank-4 chain elements (involved)
    list.add(new PgnFen("pawn_wall_without_en_passant_capture_1.pgn",
        "4k3/8/8/p1p1p1p1/PpPpPpPp/1P1P1P1P/8/4K3 b - - 0 50"));

    list.add(
        new PgnFen("pawn_wall_without_en_passant_capture_2.pgn", "4k3/8/8/p1p1p1p1/P1PpPpPp/3P1P1P/8/4K3 b - - 0 50"));

    list.add(new PgnFen("pawn_wall_without_en_passant_capture_3.pgn", "4k3/8/7p/p1p2p1P/P1P1pP2/4P3/8/4K3 b - - 0 50"));

    // Ambrona 83
    list.add(new PgnFen("pawn_wall_ambrona_lichess_Ob5ozxgG.pgn", "1k6/5p1p/1p2pP1P/1P2P3/8/1K6/8/8 b - - 83 95"));

    return new PgnTestCaseList(PgnTest.CHA_PAWN_WALL_YES, list);
  }

  // Fixtures that look like a pawn wall but are not - the geometric verdict must return UNKNOWN.
  // Reasons vary: kings on the wrong side of the chain (no barrier between them), en-passant rights
  // breaching the wall, or chains that fail the all-pawns-involved gate (Norgaard examples and the
  // Norgaard "beyond dead position" examples - the chain does not span from leftmost to rightmost file).
  private static PgnTestCaseList createTestCasesPawnWallNo() {
    final List<PgnFen> list = new ArrayList<>();

    // pawn walls but king on wrong side - position is actually winnable
    list.add(new PgnFen("pawn_wall_both_kings_on_white_side.pgn", "8/8/8/p1p1p1p1/P1P1P1P1/8/1k6/4K3 w - - 0 50"));

    list.add(new PgnFen("pawn_wall_both_kings_on_black_side.pgn", "3k2K1/1p6/pPp2p1p/P1P2P1P/8/8/8/8 w - - 0 50"));

    // en-passant capture breaches the wall - the position is winnable
    list.add(
        new PgnFen("pawn_wall_with_en_passant_capture_1.pgn", "4k3/8/8/p1p1p1p1/PpPpPpPp/1P1P1P1P/8/4K3 b - a3 0 50"));

    list.add(
        new PgnFen("pawn_wall_with_en_passant_capture_2.pgn", "4k3/8/8/p1p1p1p1/P1PpPpPp/3P1P1P/8/4K3 b - e3 0 50"));

    list.add(new PgnFen("pawn_wall_with_en_passant_capture_3.pgn", "4k3/8/7p/p1p2p1P/P1P1pP2/4P3/8/4K3 b - f3 0 50"));

    // en-passant with bishops - legal en-passant capture breaches the wall
    list.add(new PgnFen("pawn_wall_bishop_en_passant_capture_legal.pgn",
        "4k3/8/8/p1p1p3/P1P1Pp1p/1B3P1P/8/4K3 b - e3 0 50"));

    // Norgaard "dead position" - chain is not all-pawns-involved (floating pawns)
    list.add(new PgnFen("pawn_wall_norgaard_additional_own_pawns_not_marched_up.pgn",
        "6k1/1p1p1p1p/1P1P1P1P/8/8/4K3/1P1P1P1P/8 w - - 0 39"));

    // TODO today's Ambrona website (full or quick) sees unwinnable for both sides
    list.add(new PgnFen("pawn_wall_norgaard_additional_own_pawns_not_marched_up_with_opponent_pawns_between.pgn",
        "1k6/p1p1p1p1/P1P1P1P1/p1p1p1p1/8/8/P1P1P1P1/4K3 w - - 0 34"));

    // Norgaard "beyond dead position" - chain has no a-file (resp. h-file) pawn, so it does not span
    // from leftmost to rightmost file; the geometric chain check rejects.
    list.add(new PgnFen("pawn_wall_norgaard_additional_own_pawns_marched_up_example_1.pgn",
        "7k/1p1p1p1p/1P1P1P1P/1P1P1P1P/8/K7/8/8 b - - 100 688"));

    list.add(new PgnFen("pawn_wall_norgaard_additional_own_pawns_marched_up_example_2.pgn",
        "k7/p1p1p1p1/P1P1P1P1/P1P1P1P1/8/8/6K1/8 b - - 100 883"));

    return new PgnTestCaseList(PgnTest.CHA_PAWN_WALL_NO, list);
  }

  // Test fixtures for ShallowTerminationOracle. Each fixture exercises a specific depth (1, 2, or 3) at which the
  // oracle should first find a terminal status, plus a control (no termination within 3 plies). Tests both
  // White-to-move and Black-to-move variants with independent (non-mirror) positions.
  private static PgnTestCaseList createTestCasesShallowTermination() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_m1_white_to_move.pgn", "6k1/5ppp/8/8/8/8/5PPP/R3K3 w Q - 0 1"));
    list.add(new PgnFen("02_m1_black_to_move.pgn", "1q6/8/8/P7/8/n2k4/8/3K4 b - - 0 1"));
    list.add(new PgnFen("03_m2_white_to_move.pgn", "kbK5/pp6/1P6/8/8/8/8/R7 w - - 0 1"));
    list.add(new PgnFen("04_m2_black_to_move.pgn", "rNq1k2K/P1p1pp2/2Rp1P2/5b2/2p3r1/8/8/8 b q - 5 3"));
    list.add(new PgnFen("05_helpmate2_white_to_move.pgn", "8/8/8/8/8/5k2/2p5/4K3 w - - 1 50"));
    list.add(new PgnFen("06_helpmate2_black_to_move.pgn", "8/5K2/7k/8/8/8/6Q1/8 b - - 0 50"));
    list.add(new PgnFen("07_helpmate3_white_to_move.pgn", "8/5K1k/4N3/2B5/8/8/8/8 w - - 0 50"));
    list.add(new PgnFen("08_helpmate3_black_to_move.pgn", "8/8/8/5b2/8/2b3k1/8/6K1 b - - 0 50"));
    list.add(new PgnFen("09_control_white_to_move.pgn", "8/8/8/rk6/8/8/1K6/8 w - - 0 50"));
    list.add(new PgnFen("10_control_black_to_move.pgn", "k2bb1K1/8/8/8/8/8/8/8 b - - 0 50"));

    return new PgnTestCaseList(PgnTest.CHA_SHALLOW_TERMINATION, list);
  }

  private static PgnTestCaseList createTestCasesHelpmateBeyondFivefold() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_beyond_fivefold.pgn", "3K4/2P5/3k4/4r3/8/1r6/8/8 w - - 34 58"));

    list.add(new PgnFen("02_beyond_fivefold.pgn", "7k/8/2R3K1/8/8/8/8/8 b - - 15 11"));

    return new PgnTestCaseList(PgnTest.CHA_HELPMATE_BEYOND_FIVEFOLD, list);
  }

  private static PgnTestCaseList createTestCasesHelpmateBeyondSeventyFive() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_beyond_seventy_five.pgn", "8/5r2/p3k3/P7/8/1K6/8/8 w - - 148 100"));

    list.add(new PgnFen("02_beyond_seventy_five.pgn", "7k/8/2R3K1/8/8/8/8/8 b - - 149 100"));

    return new PgnTestCaseList(PgnTest.CHA_HELPMATE_BEYOND_SEVENTY_FIVE, list);
  }

  private static PgnTestCaseList createTestCasesBasicMateDraw() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_draw_KR_K.pgn", "8/8/8/8/8/8/1R1K4/k7 b - - 0 50"));
    list.add(new PgnFen("02_draw_KQ_K.pgn", "8/8/8/8/8/8/Q2K4/1k6 b - - 0 50"));
    list.add(new PgnFen("03_draw_KBwBb_K.pgn", "8/8/8/8/8/5B2/4K2B/6k1 b - - 0 50"));
    list.add(new PgnFen("04_draw_KBwN_K.pgn", "8/8/8/8/8/5B2/4K2N/6k1 b - - 0 1"));

    return new PgnTestCaseList(PgnTest.CHA_BASIC_MATE_DRAW, list);
  }

  private static PgnTestCaseList createTestCasesBasicMateHelpmate04() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_helpmate_04_KR_K.pgn", "8/8/8/8/8/1K1R4/8/1k6 b - - 0 50"));
    list.add(new PgnFen("02_helpmate_04_KQ_K.pgn", "8/8/8/8/k7/2K5/3Q4/8 b - - 0 50"));
    list.add(new PgnFen("03_helpmate_04_KBwBb_K.pgn", "2k5/8/K7/8/BB6/8/8/8 b - - 0 50"));
    list.add(new PgnFen("04_helpmate_04_KBwN_K.pgn", "2k5/8/K7/4N3/B7/8/8/8 b - - 0 1"));

    return new PgnTestCaseList(PgnTest.CHA_BASIC_MATE_HELPMATE_04, list);
  }

  private static PgnTestCaseList createTestCasesBasicMateHelpmate10() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_helpmate_10_KR_K.pgn", "8/8/k7/6K1/8/8/8/1R6 b - - 0 50"));
    list.add(new PgnFen("02_helpmate_10_KQ_K.pgn", "8/8/k7/6K1/8/8/8/1Q6 b - - 0 50"));
    list.add(new PgnFen("03_helpmate_10_KBwBb_K.pgn", "1k6/8/5B2/5B2/8/K7/8/8 b - - 0 50"));
    list.add(new PgnFen("04_helpmate_10_KBwN_K.pgn", "8/k7/8/KB6/8/8/8/6N1 b - - 0 50"));

    return new PgnTestCaseList(PgnTest.CHA_BASIC_MATE_HELPMATE_10, list);
  }

  private static PgnTestCaseList createTestCasesBasicMateHelpmateAroundMax() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_helpmate_around_max_KR_K.pgn", "7K/6R1/8/8/8/8/8/k7 b - - 0 50"));
    list.add(new PgnFen("02_helpmate_around_max_KQ_K.pgn", "7K/6Q1/8/8/8/8/8/k7 b - - 0 50"));
    list.add(new PgnFen("03_helpmate_around_max_KBwBb_K.pgn", "5BBK/8/8/8/8/8/8/k7 b - - 0 50"));
    list.add(new PgnFen("04_helpmate_around_max_KBwN_K.pgn", "5NBK/8/8/8/8/8/8/k7 b - - 0 50"));

    return new PgnTestCaseList(PgnTest.CHA_BASIC_MATE_HELPMATE_AROUND_MAX, list);
  }

  private static PgnTestCaseList createTestCasesLastMoveAddedAccidentally() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_last_move_added_accidentally_result_white_win_last_move_white_draws.pgn",
        "8/6Pk/2b5/2P1K3/8/8/8/8 b - - 2 87"));
    list.add(
        new PgnFen("02_last_move_added_accidentally_result_draw_one_move_in_KvK.pgn", "8/8/8/3K4/8/8/k7/8 w - - 0 69"));
    list.add(new PgnFen("03_last_move_added_accidentally_result_draw_black_last_move_loses.pgn",
        "8/8/8/4k1pP/6K1/8/8/8 w - - 2 69"));
    list.add(new PgnFen("04_last_move_added_accidentally_result_draw_black_last_move_loses.pgn",
        "8/P1R5/7p/4kppP/6P1/8/1K6/r7 w - - 10 66"));

    return new PgnTestCaseList(PgnTest.LAST_MOVE_ADDED_ACCIDENTALLY, list);
  }

  private static PgnTestCaseList createTestCasesMaxSamePiecePromotionWhite() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_white_max_piece_ten_rooks.pgn", "1R6/1R6/2R1kbR1/8/R7/2NB1N2/3BKR2/3Q4 b - - 0 85"));
    list.add(new PgnFen("02_white_max_piece_ten_knights.pgn", "8/kN6/NNN2b1p/2N5/N1N4R/2N5/n1QB1KB1/1R6 b - - 0 75"));
    list.add(new PgnFen("03_white_max_piece_ten_bishops_nine_light_squared.pgn",
        "6Br/2k4p/4B3/3B2N1/5b2/1B1B1B2/4B1B1/2qK1B1R w - - 0 67"));
    list.add(new PgnFen("04_white_max_piece_ten_bishops_nine_darks_squared.pgn",
        "6Br/2k4p/4B3/3B2N1/5b2/1B1B1B2/4B1B1/2qK1B1R w - - 0 67"));
    list.add(new PgnFen("05_white_max_piece_nine_queens.pgn", "2Q2Q2/k7/bn5B/1Q6/3QQQ1K/Q7/8/5BNR b - - 4 72"));

    return new PgnTestCaseList(PgnTest.MAX_SAME_PIECE_PROMOTION_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesMaxSamePiecePromotionBlack() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_black_max_piece_ten_rooks.pgn", "2rk3r/2rbQ1b1/6r1/1n3r2/KB3r1r/nN6/3r3r/8 b - - 0 118"));
    list.add(new PgnFen("02_black_max_piece_ten_knights.pgn",
        "r1bB1b1K/1BR1n1r1/Q3nnn1/1q3Nkn/1N1n2nn/nn6/8/2R5 w - - 42 130"));
    list.add(new PgnFen("03_black_max_piece_ten_bishops_nine_light_squared.pgn",
        "1nb2bn1/5b2/5k2/3b1b2/1Rb3b1/2Pb4/r3b1b1/2K4r w - - 2 102"));
    list.add(new PgnFen("04_black_max_piece_ten_bishops_nine_dark_squared.pgn",
        "4kb2/8/2n2n2/3b2b1/3b1b2/P1b1b2r/1b3bR1/7K w - - 0 96"));
    list.add(new PgnFen("05_black_max_piece_nine_queens.pgn", "r1b1qb1K/1n4r1/8/2n2k2/2Bq4/8/8/7q w - - 0 139"));

    return new PgnTestCaseList(PgnTest.MAX_SAME_PIECE_PROMOTION_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesMaxSamePiecePromotionByCombined() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_combined_max_piece_twenty_rooks.pgn", "8/5k2/1r6/8/8/3r4/2r5/1r3K2 w - - 1 114"));
    list.add(new PgnFen("02_combined_max_piece_twenty_knights.pgn", "7k/8/3n4/4n3/3n4/nn1n4/8/1KnN4 w - - 6 126"));
    list.add(new PgnFen("03_combined_max_piece_twenty_bishops.pgn", "2B1B3/3B2kR/4BrB1/2B5/B7/6K1/2B3B1/8 b - - 4 98"));
    list.add(new PgnFen("04_combined_max_piece_eighteen_queens.pgn", "8/8/8/8/8/8/5KQk/8 b - - 14 110"));

    return new PgnTestCaseList(PgnTest.MAX_SAME_PIECE_PROMOTION_COMBINED, list);
  }

  private static PgnTestCaseList createTestCasesDoubleCheckCheckmateBizarreWhite() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_white_double_check_checkmate_bizarre_one_empty_square.pgn",
        "8/2B5/4NN2/2bNkN2/2NNNN2/8/8/4K3 b - - 1 100"));
    list.add(new PgnFen("02_white_double_check_checkmate_bizarre_all_empty_squares.pgn",
        "8/2B2N2/7R/2b1k3/1R6/8/3R1R2/4K3 b - - 1 100"));

    return new PgnTestCaseList(PgnTest.DOUBLE_CHECK_CHECKMATE_BIZARRE_CHECKMATE_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesDoubleCheckCheckmateBizarreBlack() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_black_double_check_checkmate_bizarre_one_empty_square.pgn",
        "1k6/3b4/nn2B3/nKn5/nnnn4/8/8/8 w - - 1 101"));
    list.add(new PgnFen("02_black_double_check_checkmate_bizarre_all_empty_squares.pgn",
        "2rkr3/r7/3K4/r7/2n5/4B3/7b/8 w - - 1 101"));

    return new PgnTestCaseList(PgnTest.DOUBLE_CHECK_CHECKMATE_BIZARRE_CHECKMATE_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesBlogInstant() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_blog_instant_K_K.pgn", "8/8/6k1/8/5K2/8/8/8 b - - 0 26"));
    list.add(new PgnFen("02_blog_instant_KN_K.pgn", "8/5k2/8/8/8/8/8/1NK5 b - - 0 26"));

    return new PgnTestCaseList(PgnTest.MONSTER_BLOG_INSTANT, list);
  }

  private static PgnTestCaseList createTestCasesBlogPredraw() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_blog_predraw_KBw_KBb.pgn", "8/3B1k2/7b/8/8/8/5K2/8 b - - 0 29"));
    list.add(new PgnFen("02_blog_predraw_KN_KB.pgn", "2b5/3k4/8/6K1/8/8/3N4/8 b - - 0 27"));
    list.add(new PgnFen("03_blog_predraw_KB_KN.pgn", "8/6k1/7n/8/8/7B/5K2/8 b - - 0 30"));
    list.add(new PgnFen("04_blog_predraw_KNN_K.pgn", "8/8/7k/8/4N3/3N4/6K1/8 b - - 0 37"));
    list.add(new PgnFen("05_blog_predraw_KN_KN.pgn", "1n6/8/1k6/8/3K4/8/8/1N6 b - - 0 24"));

    return new PgnTestCaseList(PgnTest.MONSTER_BLOG_PREDRAW, list);
  }

  private static PgnTestCaseList createTestCasesBlogTimeout() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_blog_timeout_K_more.pgn", "1nbk1b2/1qpp1p2/8/8/3K4/8/8/8 b - - 3 20"));
    list.add(new PgnFen("02_blog_timeout_KN_KQ.pgn", "8/8/1q6/3k4/8/2N5/8/5K2 b - - 1 26"));
    list.add(new PgnFen("03_blog_timeout_KN_KNP_forced_mate.pgn", "3N2nk/4p3/5pKp/6p1/8/8/8/8 b - - 14 36"));
    list.add(new PgnFen("04_blog_timeout_KN_KP.pgn", "2k5/8/p7/8/8/2N1K3/8/8 b - - 2 37"));
    list.add(new PgnFen("05_blog_timeout_KN_KR.pgn", "8/2k4N/8/8/8/3r4/4K3/8 b - - 1 29"));
    list.add(new PgnFen("06_blog_timeout_KB_KR.pgn", "7r/2k5/8/8/5B2/8/4K3/8 b - - 1 37"));
    list.add(new PgnFen("07_blog_timeout_KBw_KRBw.pgn", "8/8/2r2k2/3b4/8/8/2BK4/8 b - - 15 40"));
    list.add(new PgnFen("08_blog_timeout_KB_KNP_forced_mate.pgn", "knB5/3p4/pKp5/p7/1p6/8/8/8 b - - 12 60"));
    list.add(new PgnFen("09_blog_timeout_KB_KP.pgn", "8/5p2/8/4k3/1K6/8/8/5B2 b - - 0 28"));
    list.add(new PgnFen("10_blog_timeout_KBb_KRBw.pgn", "4r3/3b4/4k3/8/5K2/8/3B4/8 b - - 4 37"));
    list.add(new PgnFen("11_blog_timeout_KNN_KP_forced_mate.pgn", "7k/8/5N1K/8/5N1p/8/8/8 b - - 1 49"));
    list.add(new PgnFen("12_blog_timeout_KNN_KP.pgn", "8/8/8/2kp4/8/8/2NKN3/8 b - - 5 41"));
    list.add(new PgnFen("13_blog_timeout_KNN_KB.pgn", "k7/1bKN4/8/1N6/8/8/8/8 b - - 17 51"));
    list.add(new PgnFen("14_blog_timeout_KNN_KQQQQQQQQQ.pgn", "3q4/3N3K/8/5N2/8/7k/8/qqqqqqqq b - - 1 88"));

    return new PgnTestCaseList(PgnTest.MONSTER_BLOG_TIMEOUT, list);
  }

  private static PgnTestCaseList createTestCasesRepetitionQuizOne() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_claim_for_board_position_incorrect_initial_en_passant.pgn",
        "4R3/4b2p/3kn1p1/1p3pP1/5P2/3KP3/7P/8 w - - 8 75"));
    list.add(new PgnFen("02_claim_for_own_move_correct.pgn", "8/pp3p1k/2p2q1p/3r1P2/5R2/3Q3P/P1P2P2/7K w - - 7 74"));
    list.add(new PgnFen("03_claim_for_own_move_incorrect_castling_right_lost_for_king_move.pgn",
        "rn1Nk2r/pp2p2p/3p2p1/1bp5/5Pn1/2N1b3/PPP3PP/R1BQK2R b kq - 8 15"));
    list.add(new PgnFen("04_claim_for_own_move_incorrect.pgn", "R7/2q2p2/5k1Q/pp2r3/2p1P1P1/7P/6K1/8 b - - 6 73"));
    list.add(
        new PgnFen("05_claim_for_own_move_correct_but_makes_move_on_board.pgn", "8/6k1/4KN2/3B4/8/8/8/8 b - - 18 74"));
    list.add(new PgnFen("06_claim_for_board_position_correct.pgn",
        "r3qrk1/p1p1b3/4pn1Q/3p4/8/2NB4/PPP2PPP/R5K1 w - - 9 76"));
    list.add(new PgnFen("07_claim_for_board_position_correct_but_touch_move.pgn", "3k4/8/2K5/2P5/8/8/8/8 b - - 8 74"));
    list.add(new PgnFen("08_claim_for_own_move_correct.pgn", "3Q1k2/5p1p/1p2q3/2p5/6n1/4P3/PP2R3/6K1 b - - 7 74"));
    list.add(new PgnFen("09_claim_for_board_position_incorrect_side_to_move_changed.pgn",
        "8/8/2k5/6r1/2B5/1KR5/8/8 b - - 10 75"));

    return new PgnTestCaseList(PgnTest.REPETITION_QUIZ_ONE, list);
  }

  private static PgnTestCaseList createTestCasesRepetitionQuizTwo() {
    final List<PgnFen> list = new ArrayList<>();

    list.add(new PgnFen("01_KRvKN.pgn", "8/8/4k3/8/2n5/8/2K1R3/8 b - - 12 76"));
    list.add(new PgnFen("02_KRvKB_fivefold.pgn", "8/3k4/3b4/8/2KR4/8/8/8 b - - 26 83"));
    list.add(new PgnFen("03_KRBvKR.pgn", "4k1R1/8/5r2/3K4/8/3B4/8/8 b - - 24 82"));

    return new PgnTestCaseList(PgnTest.REPETITION_QUIZ_TWO, list);
  }

  private static PgnTestCaseList createTestCasesBasicFromFenNoProgressBlack() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(new PgnFen("04_black_from_fen_no_progress_fifty_reoccuring_above_seventy_five.pgn",
        "1Q6/6pk/8/6n1/2N5/6q1/1KP1P3/8 w - - 158 180"));
    list.add(new PgnFen("05_black_from_fen_no_progress_seventy_five_reoccuring_fifty.pgn",
        "8/6p1/7k/6n1/2NQ4/4q3/2P1P3/1K6 w - - 100 151"));
    list.add(new PgnFen("06_black_from_fen_no_progress_seventy_five_reoccuring_seventy_five.pgn",
        "8/6pk/8/6n1/2NQ4/4q3/1KP1P3/8 w - - 150 176"));
    list.add(new PgnFen("07_black_from_fen_no_progress_seventy_five_reoccuring_above_fifty.pgn",
        "8/6pk/8/4q1n1/2NQ4/8/1KP1P3/8 b - - 149 175"));
    list.add(new PgnFen("08_black_from_fen_no_progress_seventy_five_reoccuring_above_seventy_five.pgn",
        "1Q6/6pk/8/6n1/2N5/6q1/1KP1P3/8 w - - 158 180"));
    list.add(new PgnFen("12_black_from_fen_no_progress_above_fifty_reoccuring_above_seventy_five.pgn",
        "1Q6/6pk/8/6n1/2N5/6q1/1KP1P3/8 w - - 158 180"));
    list.add(new PgnFen("13_black_from_fen_no_progress_above_seventy_five_reoccuring_fifty.pgn",
        "8/6p1/7k/6n1/2NQ4/4q3/2P1P3/1K6 w - - 100 151"));
    list.add(new PgnFen("14_black_from_fen_no_progress_above_seventy_five_reoccuring_seventy_five.pgn",
        "8/6pk/8/6n1/2NQ4/4q3/1KP1P3/8 w - - 150 176"));
    list.add(new PgnFen("15_black_from_fen_no_progress_above_seventy_five_reoccuring_above_fifty.pgn",
        "8/6pk/8/4q1n1/2NQ4/8/1KP1P3/8 b - - 149 175"));
    list.add(new PgnFen("16_black_from_fen_no_progress_above_seventy_five_reoccuring_above_seventy_five.pgn",
        "1Q6/6pk/8/6n1/2N5/6q1/1KP1P3/8 w - - 158 180"));
    return new PgnTestCaseList(PgnTest.BASIC_FROM_FEN_NO_PROGRESS_BLACK, list);
  }

  private static PgnTestCaseList createTestCasesBasicFromFenNoProgressWhite() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(new PgnFen("04_white_from_fen_no_progress_fifty_reoccuring_above_seventy_five.pgn",
        "8/3pk3/2p1p3/8/7P/qQ3PP1/6K1/8 b - - 178 189"));
    list.add(new PgnFen("05_white_from_fen_no_progress_seventy_five_reoccuring_fifty.pgn",
        "8/3p4/1kp1p3/8/2qQ3P/5PPK/8/8 b - - 100 150"));
    list.add(new PgnFen("06_white_from_fen_no_progress_seventy_five_reoccuring_seventy_five.pgn",
        "2Q5/3p1q2/2pkp3/8/7P/5PPK/8/8 b - - 150 175"));
    list.add(new PgnFen("07_white_from_fen_no_progress_seventy_five_reoccuring_above_fifty.pgn",
        "Q7/3p1q2/2pkp3/8/7P/5PPK/8/8 w - - 149 175"));
    list.add(new PgnFen("08_white_from_fen_no_progress_seventy_five_reoccuring_above_seventy_five.pgn",
        "8/3pk3/2p1p3/8/7P/qQ3PP1/6K1/8 b - - 178 189"));
    list.add(new PgnFen("12_white_from_fen_no_progress_above_fifty_reoccuring_above_seventy_five.pgn",
        "8/3pk3/2p1p3/8/7P/qQ3PP1/6K1/8 b - - 178 189"));
    list.add(new PgnFen("13_white_from_fen_no_progress_above_seventy_five_reoccuring_fifty.pgn",
        "8/3p4/1kp1p3/8/2qQ3P/5PPK/8/8 b - - 100 150"));
    list.add(new PgnFen("14_white_from_fen_no_progress_above_seventy_five_reoccuring_seventy_five.pgn",
        "2Q5/3p1q2/2pkp3/8/7P/5PPK/8/8 b - - 150 175"));
    list.add(new PgnFen("15_white_from_fen_no_progress_above_seventy_five_reoccuring_above_fifty.pgn",
        "Q7/3p1q2/2pkp3/8/7P/5PPK/8/8 w - - 149 175"));
    list.add(new PgnFen("16_white_from_fen_no_progress_above_seventy_five_reoccuring_above_seventy_five.pgn",
        "8/3pk3/2p1p3/8/7P/qQ3PP1/6K1/8 b - - 178 189"));
    return new PgnTestCaseList(PgnTest.BASIC_FROM_FEN_NO_PROGRESS_WHITE, list);
  }

  private static PgnTestCaseList createTestCasesFivefoldBeyond() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(new PgnFen("fivefold_beyond_savchenko_yu_y2017.pgn", "8/8/1k2K3/1P2p3/2n1P3/8/8/3n4 w - - 0 96"));
    list.add(new PgnFen("fivefold_beyond_wang_yu_2017.pgn", "8/5p2/8/4N1P1/8/b2P1pk1/3K4/8 w - - 0 69"));
    list.add(new PgnFen("fivefold_beyond_yu_alekseenko_2018.pgn", "8/2p5/p3r3/1p1Q2R1/2k2P2/P3q3/1P4K1/8 b - - 32 97"));
    return new PgnTestCaseList(PgnTest.FIVEFOLD_BEYOND, list);
  }

  private static PgnTestCaseList createTestCasesSeventyFiveBeyond() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(
        new PgnFen("seventy_five_beyond_anton_guijarro_antipov_2015.pgn", "8/3n4/3P4/8/6k1/4n3/7K/8 w - - 171 148"));
    list.add(new PgnFen("seventy_five_beyond_aronian_navara_2017.pgn", "8/3R4/5K1k/7p/7P/5b2/8/8 w - - 167 148"));
    list.add(new PgnFen("seventy_five_beyond_cheparinov_jones_2019.pgn", "8/8/6r1/K6R/2k5/8/2n5/8 w - - 152 145"));
    list.add(new PgnFen("seventy_five_beyond_moiseenko_radjabov_2016.pgn", "1k6/3R4/8/1K1N4/8/8/8/1r6 w - - 191 183"));
    list.add(new PgnFen("seventy_five_beyond_onischuk_guseinov_2014.pgn", "8/8/3Bn3/8/4B1k1/4K3/8/8 b - - 153 138"));
    // "various_*" historical games: filename uses "various" but the gameplay characteristic is crossing the 75-move
    // threshold, so they live under SEVENTY_FIVE_BEYOND rather than the catch-all VARIOUS bucket.
    list.add(new PgnFen("various_drozdova_tan_2018.pgn", "8/8/8/8/8/3n1k1b/8/6K1 w - - 168 156"));
    list.add(new PgnFen("various_kevlishvili_zhigalko_2018.pgn", "8/8/8/8/5N1n/4K3/8/3k4 b - - 0 192"));
    return new PgnTestCaseList(PgnTest.SEVENTY_FIVE_BEYOND, list);
  }

  private static PgnTestCaseList createTestCasesLong() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(new PgnFen("long_nikolic_arsovic_1989.pgn", "8/6r1/8/8/3K4/3B4/5R2/3k4 w - - 205 270"));
    return new PgnTestCaseList(PgnTest.LONG, list);
  }

  private static PgnTestCaseList createTestCasesLongestMate() {
    final List<PgnFen> list = new ArrayList<>();
    list.add(new PgnFen("longest_mate_seven_pieces_rank_1.pgn", "8/8/6Qk/5Kb1/8/8/8/8 b - - 20 586"));
    list.add(new PgnFen("longest_mate_seven_pieces_rank_2.pgn", "8/8/2K5/k7/8/Q7/8/8 b - - 8 584"));
    list.add(new PgnFen("longest_mate_seven_pieces_rank_3.pgn", "8/8/2K5/k7/8/Q7/8/8 b - - 8 590"));
    list.add(new PgnFen("longest_mate_seven_pieces_rank_4.pgn", "8/8/5K2/6Qk/8/8/8/8 b - - 4 585"));
    list.add(new PgnFen("longest_mate_seven_pieces_rank_5.pgn", "8/8/2K5/k7/8/Q7/8/8 b - - 8 574"));
    list.add(new PgnFen("longest_mate_seven_pieces_rank_6.pgn", "8/8/8/2K5/kQ6/8/8/8 b - - 6 576"));
    list.add(new PgnFen("longest_mate_seven_pieces_rank_7.pgn", "8/8/6Qk/5Kb1/8/8/8/8 b - - 20 570"));
    list.add(new PgnFen("longest_mate_seven_pieces_rank_8.pgn", "8/8/8/2K5/kQ6/8/8/8 b - - 6 516"));
    return new PgnTestCaseList(PgnTest.LONGEST_MATE, list);
  }
}
