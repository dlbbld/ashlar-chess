package io.github.dlbbld.ashlarchess.test.generate;

import java.io.File;
import java.nio.file.Path;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.constants.ChessConstants;
import io.github.dlbbld.ashlarchess.common.exceptions.FileSystemAccessException;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.test.common.utility.PgnExtensionUtility;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

public class GenerateTestCaseForPgnFolder extends AbstractGenerateTestCaseForPgn {

  // the folder can only contain PGN files
  private static final Path PGN_FOLDER_PATH = PgnTest.CHA_BASIC_MATE_HELPMATE_AROUND_MAX.getFolderPath();

  public static void main(String[] args) throws Exception {
    generateTestCaseForFolder(PGN_FOLDER_PATH);
  }

  private static void generateTestCaseForFolder(Path pgnFolderPath) throws Exception {
    final File folder = pgnFolderPath.toFile();
    if (!folder.isDirectory()) {
      throw new IllegalArgumentException("\"" + pgnFolderPath + "\" is not a directory");
    }

    final File[] filesList = folder.listFiles();
    if (filesList == null) {
      throw new FileSystemAccessException("File list retrieval for \"" + pgnFolderPath + "\" failed");
    }

    for (final File file : filesList) {
      if (file == null) {
        throw new ProgrammingMistakeException("Wrong assumption about API behaviour");
      }
      final String pgnName = Nulls.getName(file);
      if (!PgnExtensionUtility.hasPgnExtension(pgnName)) {
        throw new IllegalArgumentException("All files in the folder must be valid PGN files and have the extension \""
            + ChessConstants.PGN_EXTENSION + "\". The file \"" + pgnName + " does not meet the extension expectation");
      }
      final String testCaseValues = generate(pgnName);
      System.out.println(testCaseValues);
    }
  }

}
