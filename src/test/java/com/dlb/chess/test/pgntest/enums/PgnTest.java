package com.dlb.chess.test.pgntest.enums;

import java.nio.file.Path;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.test.pgntest.constants.PgnTestConstants;

public enum PgnTest {

  BASIC_MOVING_PIECE_WHITE(true, "basic/movingPiece/white"),
  BASIC_MOVING_PIECE_BLACK(true, "basic/movingPiece/black"),
  BASIC_CAPTURE_WHITE(true, "basic/capture/systematic/white"),
  BASIC_CAPTURE_BLACK(true, "basic/capture/systematic/black"),
  BASIC_CAPTURE_LAST_MOVE(true, "basic/capture/lastMove"),
  BASIC_EN_PASSANT_CAPTURE_WHITE(true, "basic/enPassantCapture/white"),
  BASIC_EN_PASSANT_CAPTURE_BLACK(true, "basic/enPassantCapture/black"),
  BASIC_PROMOTION_PIECE_WHITE(true, "basic/promotionPiece/white"),
  BASIC_PROMOTION_PIECE_BLACK(true, "basic/promotionPiece/black"),
  BASIC_PROMOTION_SQUARE_WHITE(true, "basic/promotionSquare/white"),
  BASIC_PROMOTION_SQUARE_BLACK(true, "basic/promotionSquare/black"),
  BASIC_CHECK_WHITE(true, "basic/check/white"),
  BASIC_CHECK_BLACK(true, "basic/check/black"),
  BASIC_CHECKMATE_WHITE(true, "basic/checkmate/rbnqkVariations/white"),
  BASIC_CHECKMATE_BLACK(true, "basic/checkmate/rbnqkVariations/black"),
  BASIC_CHECKMATE_VARIOUS_WHITE(true, "basic/checkmate/various/white"),
  BASIC_CHECKMATE_VARIOUS_BLACK(true, "basic/checkmate/various/black"),

  BASIC_DOUBLE_CHECK_WHITE(true, "basic/doubleCheck/noCheckmate/white"),
  BASIC_DOUBLE_CHECK_BLACK(true, "basic/doubleCheck/noCheckmate/black"),
  BASIC_CHECKMATE_DOUBLE_CHECK_WHITE(true, "basic/doubleCheck/checkmate/white"),
  BASIC_CHECKMATE_DOUBLE_CHECK_BLACK(true, "basic/doubleCheck/checkmate/black"),

  BASIC_STALEMATE(true, "basic/stalemate"),
  BASIC_INSUFFICIENT_MATERIAL_BOTH(true, "basic/insufficientMaterial/both"),
  BASIC_INSUFFICIENT_MATERIAL_ONLY_WHITE(true, "basic/insufficientMaterial/onlyWhite"),
  BASIC_INSUFFICIENT_MATERIAL_ONLY_BLACK(true, "basic/insufficientMaterial/onlyBlack"),
  BASIC_INSUFFICIENT_MATERIAL_NONE(true, "basic/insufficientMaterial/none"),
  BASIC_THREEFOLD(true, "basic/threefold"),
  BASIC_FIFTY(true, "basic/fifty"),
  BASIC_FIVEFOLD(true, "basic/fivefold"),
  BASIC_SEVENTY_FIVE(true, "basic/seventyFive"),
  BASIC_INTERVENING(true, "basic/intervening"),
  BASIC_DOUBLE_DRAW(true, "basic/doubleDraw"),
  BASIC_CASTLING_WHITE(true, "basic/castling/simplest/white"),
  BASIC_CASTLING_BLACK(true, "basic/castling/simplest/black"),
  BASIC_CASTLING_SPECIAL_WHITE(true, "basic/castling/special/white"),
  BASIC_CASTLING_SPECIAL_BLACK(true, "basic/castling/special/black"),
  BASIC_FORCED(true, "basic/forced"),
  BASIC_REPORT_NO_PROGRESS_SEQUENCES_WHITE(true, "basic/report/noProgress/white"),
  BASIC_REPORT_NO_PROGRESS_SEQUENCES_BLACK(true, "basic/report/noProgress/black"),
  BASIC_REPORT_REPETITION(false, "basic/report/repetition"),
  BASIC_REPORT_MAX_NO_PROGRESS(false, "basic/report/maxNoProgress"),

  PARSER_FROM_FEN(false, "parserFenMechanics/"),

  BASIC_FROM_FEN_NO_PROGRESS_BLACK(false, "basic/fromFenNoProgress/black"),
  BASIC_FROM_FEN_NO_PROGRESS_WHITE(false, "basic/fromFenNoProgress/white"),

  // skipped when testing against scalachess for cannot manage so long games in
  // testing approach
  MAX_MOVES(false, "edgeCases/max/maxMoves"),
  MAX_SAME_PIECE_PROMOTION_WHITE(false, "edgeCases/max/maxPieces/white"),
  MAX_SAME_PIECE_PROMOTION_BLACK(false, "edgeCases/max/maxPieces/black"),
  MAX_SAME_PIECE_PROMOTION_COMBINED(false, "edgeCases/max/maxPieces/combined"),
  DOUBLE_CHECK_CHECKMATE_BIZARRE_CHECKMATE_WHITE(false, "edgeCases/doubleCheckCheckmateBizarre/white"),
  DOUBLE_CHECK_CHECKMATE_BIZARRE_CHECKMATE_BLACK(false, "edgeCases/doubleCheckCheckmateBizarre/black"),
  SPECIAL(false, "edgeCases/special"),

  // skipped when testing against scalachess for cannot manage so long games in
  // testing approach
  RANDOM_NO_REPETITION(false, "random/noRepetition"),

  RANDOM_CHECKMATE(false, "random/checkmate"),
  RANDOM_STALEMATE(false, "random/stalemate"),
  RANDOM_INSUFFICIENT_MATERIAL(false, "random/insufficientMaterial"),
  RANDOM_FIFTY(false, "random/fifty"),
  RANDOM_SEVENTY_FIVE(false, "random/seventyFive"),
  RANDOM_THREEFOLD(false, "random/threefold"),
  RANDOM_FIVEFOLD(false, "random/fivefold"),

  VARIOUS(false, "realGames/various"),
  WCC2021(false, "review/wcc2021"),
  FIVEFOLD_CORRECT(false, "realGames/fivefold/correct"),
  FIVEFOLD_BEYOND(false, "realGames/fivefold/beyond"),
  FIFTY_GENERAL(false, "realGames/fifty/general"),
  FIFTY_PATTERN(false, "realGames/fifty/pattern"),

  SEVENTY_FIVE_CORRECT(false, "realGames/seventyFive/correct"),
  SEVENTY_FIVE_BEYOND(false, "realGames/seventyFive/beyond"),
  EARLY_DRAW(false, "realGames/earlyDraw"),
  LONG(false, "long"),
  LONGEST_MATE(false, "longestMate"),
  WIKIPEDIA_THREEFOLD(false, "review/wikipedia/threefold"),
  WIKIPEDIA_FIFTY_MOVE(false, "review/wikipedia/fiftyMove"),

  CHA_LICHESS_QUICK_DEPTH_THREE(false, "cha/lichess/quick/depthThree"),
  CHA_LICHESS_QUICK_DEPTH_FOUR(false, "cha/lichess/quick/depthFour"),
  CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR(false, "cha/lichess/quick/depthAboveFour"),
  CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR_WINNABLE_FOR_FLAGGING_WITH_HELPMATE(false, "cha/lichess/quick/depthAboveFour/winnableForFlaggingWithHelpmate"),
  CHA_AMBRONA(false, "cha/ambrona"),
  CHA_PAWN_WALL_YES(false, "cha/pawnWall/yes"),
  CHA_PAWN_WALL_NO(false, "cha/pawnWall/no"),
  CHA_SHALLOW_TERMINATION(false, "cha/shallowTermination"),
  CHA_HELPMATE_BEYOND_FIVEFOLD(false, "cha/helpmateBeyond/fivefold"),
  CHA_HELPMATE_BEYOND_SEVENTY_FIVE(false, "cha/helpmateBeyond/seventyFive"),
  CHA_BASIC_MATE_DRAW(false, "cha/basicCheckmate/draw"),
  CHA_BASIC_MATE_HELPMATE_04(false, "cha/basicCheckmate/helpmate04"),
  CHA_BASIC_MATE_HELPMATE_10(false, "cha/basicCheckmate/helpmate10"),
  CHA_BASIC_MATE_HELPMATE_AROUND_MAX(false, "cha/basicCheckmate/helpmateAroundMax"),

  LAST_MOVE_ADDED_ACCIDENTALLY(false, "realGames/lastMoveAddedAccidentally"),

  MONSTER_BLOG_INSTANT(false, "review/blog/instant"),
  MONSTER_BLOG_PREDRAW(false, "review/blog/predraw"),
  MONSTER_BLOG_TIMEOUT(false, "review/blog/timeout"),

  REPETITION_QUIZ_ONE(false, "review/repetitionQuiz/one"),
  REPETITION_QUIZ_TWO(false, "review/repetitionQuiz/two");

  private final boolean isBasicTest;
  private final String folderPart;

  PgnTest(boolean isBasicTest, String folderPart) {
    this.isBasicTest = isBasicTest;
    this.folderPart = folderPart;
  }

  public Path getFolderPath() {
    return Nulls.pathResolve(PgnTestConstants.PGN_TEST_ROOT_FOLDER_PATH, folderPart);
  }

  public boolean getIsBasicTest() {
    return isBasicTest;
  }

  public static boolean existsFolderPath(Path folderPath) {
    for (final PgnTest pgnTest : PgnTest.values()) {
      if (pgnTest.getFolderPath().equals(folderPath)) {
        return true;
      }
    }
    return false;
  }

}
