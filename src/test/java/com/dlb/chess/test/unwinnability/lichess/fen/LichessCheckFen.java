package com.dlb.chess.test.unwinnability.lichess.fen;

import java.nio.file.Path;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.fen.FenSideSymbol;
import com.dlb.chess.test.common.utility.FileUtility;
import com.dlb.chess.test.unwinnability.identifier.UnwinnabilityQuickVerdictIdentifier;
import com.dlb.chess.unwinnability.UnwinnabilityQuickVerdict;
import com.dlb.chess.unwinnability.UnwinnableQuickAnalyzer;

public class LichessCheckFen extends AbstractLichessCheckFen {

  private static final Logger logger = Nulls.getLogger(LichessCheckFen.class);

  public static void main(String[] args) {
    checkFen();
  }

  private static void checkFen() {
    final var folder = FEN_FOLDER_PATH.toFile();
    if (!folder.exists()) {
      throw new IllegalArgumentException("\"" + FEN_FOLDER_PATH + "\" directory does not exist");
    }

    final var fenFilePathIn = Nulls.pathResolve(FEN_FOLDER_PATH, FEN_FILE_NAME_IN);
    final Path fenFilePathOut = Nulls.pathResolve(FEN_FOLDER_PATH, FEN_FILE_NAME_MINE_RESULT);

    boolean isResumeFromLichessGameId;
    String lastProcessedLichessGameId;
    if (!FileUtility.exists(fenFilePathOut)) {
      isResumeFromLichessGameId = false;
      lastProcessedLichessGameId = "NA";
    } else {
      isResumeFromLichessGameId = true;
      final List<String> fenFileListProcessed = FileUtility.readFileLines(fenFilePathOut);
      final String lastLine = Nulls.getLast(fenFileListProcessed);
      final var lastLineArr = lastLine.split(";");
      // fen;lichessGameId;mode;result;duration
      final var lichessGameId = lastLineArr[1];
      lastProcessedLichessGameId = lichessGameId;
    }

    boolean isStartAnalysis;
    if (isResumeFromLichessGameId) {
      isStartAnalysis = false;
    } else {
      isStartAnalysis = true;
    }

    final List<String> fenFileListIn = FileUtility.readFileLines(fenFilePathIn);

    logger.printf(Level.INFO, "Total test FEN's: %d", fenFileListIn.size());

    var countAlreadyProcessed = 0;

    var countProcessed = 0;

    for (final String line : fenFileListIn) {

      final var sepPos = line.lastIndexOf(" ");
      final var fen = Nulls.substring(line, 0, sepPos);
      final var lichessGameId = line.substring(sepPos + 1);

      if (isStartAnalysis) {
        final Board board = new Board(fen);
        final Side testingSide = board.getHavingMove().getOppositeSide();
        final var beforeMilliSeconds = System.currentTimeMillis();
        final UnwinnabilityQuickVerdict unwinnableQuick = UnwinnableQuickAnalyzer.unwinnableQuick(board, testingSide)
            .verdict();
        final var durationMilliSeconds = System.currentTimeMillis() - beforeMilliSeconds;

        final StringBuilder outputLine = new StringBuilder();
        outputLine.append(fen).append(";");
        outputLine.append(lichessGameId).append(";");
        outputLine.append("quick").append(";");
        outputLine.append(FenSideSymbol.calculate(testingSide).sideLetter()).append(";");
        outputLine.append(UnwinnabilityQuickVerdictIdentifier.getIdentifier(unwinnableQuick)).append(";");
        outputLine.append(durationMilliSeconds);

        final String outputLineStr = Nulls.toString(outputLine);

        FileUtility.appendFile(fenFilePathOut, outputLineStr);

        countProcessed++;
        if (countProcessed % 1000 == 0) {
          logger.info(outputLineStr);
          logger.printf(Level.INFO, "Processed FEN's: %d", countProcessed);
        }
      } else {
        countAlreadyProcessed++;
        if (lastProcessedLichessGameId.equals(lichessGameId)) {
          isStartAnalysis = true;
          logger.printf(Level.INFO, "Total processed FEN's: %d", countAlreadyProcessed);
          logger.printf(Level.INFO, "Total remaining FEN's: %d", fenFileListIn.size() - countAlreadyProcessed);
        }
      }
    }
  }

}
