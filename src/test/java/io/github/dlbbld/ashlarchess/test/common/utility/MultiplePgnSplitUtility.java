package io.github.dlbbld.ashlarchess.test.common.utility;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.exceptions.ChessApiRuntimeException;
import io.github.dlbbld.ashlarchess.common.utility.BasicUtility;

public final class MultiplePgnSplitUtility {

  private static final Logger logger = Nulls.getLogger(MultiplePgnSplitUtility.class);

  private MultiplePgnSplitUtility() {
  }

  public static void createSinglePgnFromPgnMultipleFile(Path multiplePgnPath, Path outputFolderPath) {
    FileUtility.deleteFilesInDirectory(outputFolderPath);

    logger.printf(Level.INFO, "Processing file %s", multiplePgnPath);

    int writtenFileCounter = 0;
    int chess960Counter = 0;
    final int fenCounter = 0;
    int blankLineCounter = 0;
    boolean isChess960 = false;
    boolean isFen = false;
    List<String> currentFileLines = new ArrayList<>();

    final File file = multiplePgnPath.toFile();
    if (!file.isFile()) {
      throw new IllegalArgumentException("\"" + multiplePgnPath + "\" is not a file");
    }
    try (final Scanner myReader = new Scanner(file, StandardCharsets.UTF_8)) {
      while (myReader.hasNextLine()) {
        final String line = Nulls.nextLine(myReader);

        if (line.length() == 0) {
          blankLineCounter++;
        }
        if (!isChess960 && line.indexOf("chess 960") != -1) {
          isChess960 = true;
        }
        if (!isFen && line.toUpperCase().indexOf("[FEN") != -1) {
          isFen = true;
        }

        if (blankLineCounter == 2) {
          if (!isChess960 && !isFen) {
            writtenFileCounter++;

            currentFileLines.add("");

            final String pgnName = PgnExtensionUtility.addPgnExtension(padNumber(writtenFileCounter, -1));

            FileUtility.writeFile(outputFolderPath, pgnName, currentFileLines);
          } else {
            chess960Counter++;
          }
          blankLineCounter = 0;
          isChess960 = false;
          isFen = false;
          currentFileLines = new ArrayList<>();
          if (writtenFileCounter % 1000 == 0) {
            logger.printf(Level.INFO, "Created %s files", writtenFileCounter);
          }
          continue;
        }
        currentFileLines.add(line);
      }
    } catch (final IOException ioe) {
      final String message = BasicUtility.getMessage(ioe);
      throw new ChessApiRuntimeException(message);
    }

    logger.printf(Level.INFO, "Created %s files", writtenFileCounter);
    logger.printf(Level.INFO, "Ignored %s chess960 games", chess960Counter);
    logger.printf(Level.INFO, "Ignored %s games continuing from FEN", fenCounter);
  }

  private static String padNumber(int number, int totalFiles) {
    final int maxDigits = (int) StrictMath.floor(StrictMath.log10(totalFiles)) + 1;
    final StringBuilder padded = new StringBuilder();
    padded.append(number);
    while (padded.length() < maxDigits) {
      padded.insert(0, "0");
    }
    return Nulls.toString(padded);
  }
}
