package com.dlb.chess.test.scalachess;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.constants.CastlingConstants;
import com.dlb.chess.common.constants.ConfigurationConstants;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.common.model.HalfMove;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.common.ucimove.utility.UciMoveUtility;
import com.dlb.chess.common.utility.BasicUtility;
import com.dlb.chess.fen.constants.FenConstants;
import com.dlb.chess.model.PgnHalfMove;
import com.dlb.chess.moves.CastlingUtility;
import com.dlb.chess.moves.PromotionUtility;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.report.Report;
import com.dlb.chess.report.Reporter;
import com.dlb.chess.test.common.utility.FileUtility;
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

public class GenerateScalaChessTestCases implements EnumConstants {

  // if true test for folder is ignored
  private static final boolean IS_GENERATE_FOR_PGN_NAME = true;
  private static final String GENERATE_PGN_NAME = "insufficient_material_KBbBb_K.pgn";
  private static final PgnTest GENERATE_PGN_NAME_PGN_TEST = PgnTestCaseCatalog.findPgnTest(GENERATE_PGN_NAME);

  // is ignored if test for file is true
  private static final boolean IS_GENERATE_ONLY_FOR_TEST_CASE = true;
  private static final PgnTest GENERATE_TEST_CASE = PgnTest.BASIC_CASTLING_SPECIAL_WHITE;

  // true to generate until and excluding specified game below,
  // false to generate from and including specified game below
  private static final boolean IS_GAME_TO_INCLUDING_GAME = false;
  private static final boolean IS_GAME_FROM_INCLUDING_GAME = false;

  private static final String GAME_TO_OR_FROM_PGN_NAME = "insufficient_material_KBbBb_K.pgn";

  private static final Logger logger = Nulls.getLogger(GenerateScalaChessTestCases.class);

  private static final int PRINT_MOVES_INTERVAL = 100;
  private static final int PRINT_GENERATED_LINES_INTERVAL = 1000;

  private static final int WRITE_LINE_INTERVAL = 100000;
  private static final Path SCALA_SCRIPT = Nulls.pathResolve(ConfigurationConstants.TEMP_FOLDER_PATH,
      "TestDaniAgainstScalaChess.scala");

  public static void main(String[] args) throws Exception {
    generateScalaChessTestCase();
  }

  protected static void generateScalaChessTestCase() throws Exception {

    System.out.println("Output file is: " + SCALA_SCRIPT);

    FileUtility.deleteFile(SCALA_SCRIPT);

    logger.info("BEGIN generating code");
    List<String> codeLineList = new ArrayList<>();
    final List<Boolean> counterList = new ArrayList<>();

    // class header
    processScalaChessCodeLine("package chess", counterList, codeLineList);
    processScalaChessCodeLine("", counterList, codeLineList);
    processScalaChessCodeLine("import com.dlb.chess.board.Board", counterList, codeLineList);
    processScalaChessCodeLine("", counterList, codeLineList);
    processScalaChessCodeLine("class TestDaniAgainstScalaChess extends TestDaniAgainstScalaChessSuper {", counterList,
        codeLineList);
    processScalaChessCodeLine("", counterList, codeLineList);

    for (final PgnTestCaseList testCaseList : PgnTestCaseCatalog.getRestrictedTestListList()) {

      if (isContinueFolderLevel(IS_GENERATE_FOR_PGN_NAME, IS_GENERATE_ONLY_FOR_TEST_CASE, testCaseList)) {
        continue;
      }

      // below must be created separately as together the resulting test case is too big for Scala too handle
      switch (testCaseList.pgnTest()) {
        case MAX_MOVES:
        case RANDOM_NO_REPETITION:
          break;
        // $CASES-OMITTED$
        default:
          continue;
      }

      if (!IS_GENERATE_FOR_PGN_NAME) {
        final Path folderPath = testCaseList.pgnTest().getFolderPath();

        logger.info("Processing folder " + folderPath);

        processScalaChessCodeLine("println(\"Processing folder " + folderPath + "\")", counterList, codeLineList);
        processScalaChessCodeLine("", counterList, codeLineList);
      }

      var isBefore = true;
      for (final PgnTestCase testCase : testCaseList.list()) {

        var isJustHitGame = false;
        if (isBefore && GAME_TO_OR_FROM_PGN_NAME.equals(testCase.pgnName())) {
          isBefore = false;
          isJustHitGame = true;
        }

        if (isContinueFileLevel(IS_GENERATE_FOR_PGN_NAME, IS_GAME_TO_INCLUDING_GAME, IS_GAME_FROM_INCLUDING_GAME,
            testCase, isBefore, isJustHitGame)) {
          continue;
        }

        logger.info("Processing game " + testCase.pgnName());

        final Report report = Reporter.calculateReport(testCaseList.pgnTest().getFolderPath(), testCase.pgnName());
        processScalaChessCodeLine("", counterList, codeLineList);
        processScalaChessCodeLine("  println(\"Declaring test case for " + testCase.pgnName() + "\")", counterList,
            codeLineList);
        processScalaChessCodeLine("  \"" + testCase.pgnName() + "\" should {", counterList, codeLineList);

        // initialize variables to use throughout the game
        processScalaChessCodeLine("    val board = new Board()", counterList, codeLineList);
        processScalaChessCodeLine("    val uciAdaptedList = new java.util.ArrayList[String]()", counterList,
            codeLineList);

        final Board boardPlayAlong = new Board();
        final List<HalfMove> halfMoveList = report.halfMoveList();
        for (var i = 0; i < halfMoveList.size(); i++) {
          final HalfMove halfMove = Nulls.get(halfMoveList, i);

          boardPlayAlong.move(halfMove.moveSpecification());
          final var isMadeByWhite = halfMove.havingMove().getIsWhite();
          if (isMadeByWhite && halfMove.fullMoveNumber() % PRINT_MOVES_INTERVAL == 0) {
            final var moveDescription = halfMove.fullMoveNumber() + "." + halfMove.san();
            processScalaChessCodeLine(
                "    println(\"" + testCase.pgnName() + " - performed " + moveDescription + "\")", counterList,
                codeLineList);
          }

          final String uciForScala = convertMoveSpecificationToUciForScala(halfMove.havingMove(),
              halfMove.moveSpecification());
          processScalaChessCodeLine("    uciAdaptedList.add(\"" + uciForScala + "\")", counterList, codeLineList);

          // makes the moves from first until current
          final StringBuilder arguments = new StringBuilder();

          arguments.append("board");
          arguments.append(", ");
          arguments.append("uciAdaptedList");

          final String argumentsStr = Nulls.toString(arguments);
          processScalaChessCodeLine("    testAll(" + argumentsStr + ")", counterList, codeLineList);

          if (codeLineList.size() >= WRITE_LINE_INTERVAL) {
            FileUtility.appendFile(SCALA_SCRIPT, codeLineList);
            codeLineList = new ArrayList<>();
          }
        }
        processScalaChessCodeLine("  }", counterList, codeLineList);
      }
    }
    processScalaChessCodeLine("}", counterList, codeLineList);
    // write remaining
    FileUtility.appendFile(SCALA_SCRIPT, codeLineList);
    logger.info("END generating code");
  }

  // we calculate because if we put it directly into the method it can raise unused code warnings
  private static boolean isContinueFolderLevel(boolean isGenerateForFileOnly, boolean isGenerateForTestCaseOnly,
      PgnTestCaseList testCaseList) {
    if (isGenerateForFileOnly) {
      if (testCaseList.pgnTest() != GENERATE_PGN_NAME_PGN_TEST) {
        return true;
      }
    } else if (isGenerateForTestCaseOnly && testCaseList.pgnTest() != GENERATE_TEST_CASE) {
      return true;
    }
    return false;
  }

  // we calculate because if we put it directly into the method it can raise unused code warnings
  private static boolean isContinueFileLevel(boolean isGenerateForFileOnly, boolean isGameToIncludingGame,
      boolean isGameFromIncludingGame, PgnTestCase testCase, boolean isBefore, boolean isJustHitGame) {
    // code to only look at one specific game
    if (isGenerateForFileOnly && !GENERATE_PGN_NAME.equals(testCase.pgnName())) {
      return true;
    }

    if (isGameToIncludingGame && !isGameFromIncludingGame && !isBefore && !isJustHitGame) {
      return true;
    }
    if (!isGameToIncludingGame && isGameFromIncludingGame && isBefore && !isJustHitGame) {
      return true;
    }
    return false;
  }

  protected static void generateFixedScalaSquareRelatedCode() {
    for (final Square square : Square.REAL) {
      System.out.println("case \"" + square.getName() + "\" => return Pos." + square + "");
    }
  }

  protected static void generateScalaMoveList() {
    final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases
        .getPgn(PgnTest.BASIC_INSUFFICIENT_MATERIAL_BOTH.getFolderPath(), "insufficient_material_KBbBb_K.pgn");

    if (pgnGame.startFen() != FenConstants.FEN_INITIAL) {
      throw new ProgrammingMistakeException("Only test cases from initial FEN are supported");
    }
    final Board board = new Board();
    for (final PgnHalfMove move : pgnGame.halfMoveList()) {
      board.moveStrict(move.san());
      System.out.println(calculateScalaMove(board.getLastMove().havingMove(), board.getLastMove().moveSpecification()));
    }
  }

  private static List<String> generateScalaMoveList(List<HalfMove> halfMoveList, int endIndex) {
    final List<String> scalaMoveList = new ArrayList<>();
    for (var i = 0; i <= endIndex; i++) {
      final HalfMove halfMove = Nulls.get(halfMoveList, i);
      final String description = calculateScalaMove(halfMove.havingMove(), halfMove.moveSpecification());
      scalaMoveList.add(description);
    }
    return scalaMoveList;
  }

  private static void processScalaChessCodeLine(String codeLine, List<Boolean> counterList, List<String> codeLineList) {
    // System.out.println(codeLine);
    codeLineList.add(codeLine);
    counterList.add(true);
    // the code line list is emptied after appending, so we need a separate list for the count
    if (counterList.size() % PRINT_GENERATED_LINES_INTERVAL == 0) {
      System.out.println("Generated " + counterList.size() + " lines");
    }
  }

  protected static String generateUciForScalaList(List<HalfMove> halfMoveList, int endIndex) {
    final List<String> sanQuotedMoveList = new ArrayList<>();
    for (var i = 0; i <= endIndex; i++) {
      final HalfMove halfMove = Nulls.get(halfMoveList, i);
      final String uciAdapted = convertMoveSpecificationToUciForScala(halfMove.havingMove(),
          halfMove.moveSpecification());
      sanQuotedMoveList.add("\"" + uciAdapted + "\"");
    }
    return BasicUtility.calculateCommaSeparatedList(sanQuotedMoveList);
  }

  protected static String generateScalaMoveString(List<HalfMove> halfMoveList, int endIndex) {
    final List<String> scalaMoveList = generateScalaMoveList(halfMoveList, endIndex);
    return BasicUtility.calculateCommaSeparatedList(scalaMoveList);
  }

  protected static List<String> generateScalaMoveListWithTrailingComma(List<HalfMove> halfMoveList, int endIndex) {
    final List<String> scalaMoveList = generateScalaMoveList(halfMoveList, endIndex);

    final List<String> scalaMoveListWithTrailingComma = new ArrayList<>();
    for (var i = 0; i <= endIndex - 1; i++) {
      final var description = Nulls.get(scalaMoveList, i) + ",";
      scalaMoveListWithTrailingComma.add(description);
    }
    // last entry gets no trailing comma
    final var lastDescription = Nulls.get(scalaMoveList, endIndex) + ",";
    scalaMoveListWithTrailingComma.add(lastDescription);
    return scalaMoveListWithTrailingComma;
  }

  private static String calculateScalaMove(Side havingMove, MoveSpecification moveSpecification) {
    final StringBuilder result = new StringBuilder();
    result.append("(");
    result.append(calculateScalaMoveFrom(havingMove, moveSpecification));
    result.append(", ");
    result.append(calculateScalaMoveTo(havingMove, moveSpecification));
    result.append(", ");
    result.append(calculateScalaMovePromotionPiece(moveSpecification));
    result.append(")");
    return Nulls.toString(result);
  }

  private static Square calculateScalaMoveFrom(Side havingMove, MoveSpecification moveSpecification) {
    if (CastlingUtility.calculateIsCastlingMove(moveSpecification)) {
      return CastlingUtility.calculateKingCastlingFrom(havingMove, moveSpecification);
    }
    return moveSpecification.fromSquare();
  }

  private static Square calculateScalaMoveTo(Side havingMove, MoveSpecification moveSpecification) {
    if (CastlingUtility.calculateIsCastlingMove(moveSpecification)) {
      return CastlingUtility.calculateKingCastlingTo(havingMove, moveSpecification);
    }
    return moveSpecification.toSquare();
  }

  private static String calculateScalaMovePromotionPiece(MoveSpecification moveSpecification) {
    if (PromotionUtility.calculateIsPromotion(moveSpecification)) {
      return switch (moveSpecification.promotionPieceType()) {
        case ROOK -> "Option(Rook)";
        case KNIGHT -> "Option(Knight)";
        case BISHOP -> "Option(Bishop)";
        case QUEEN -> "Option(Queen)";
        case NONE -> throw new IllegalArgumentException();
        default -> throw new IllegalArgumentException();
      };
    }
    return "None";
  }

  public static String convertMoveSpecificationToUciForScala(Side havingMove, MoveSpecification moveSpecification) {
    if (CastlingUtility.calculateIsCastlingMove(moveSpecification)) {
      return switch (moveSpecification.castlingMove()) {
        case KING_SIDE -> CastlingConstants.SAN_CASTLING_KING_SIDE;
        case QUEEN_SIDE -> CastlingConstants.SAN_CASTLING_QUEEN_SIDE;
        case NONE -> throw new IllegalArgumentException();
        default -> throw new IllegalArgumentException();
      };
    }

    return UciMoveUtility.convertMoveSpecificationToUci(havingMove, moveSpecification).text();
  }
}
