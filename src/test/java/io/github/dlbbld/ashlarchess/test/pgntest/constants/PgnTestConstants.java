package io.github.dlbbld.ashlarchess.test.pgntest.constants;

import java.nio.file.Path;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.test.ConfigurationTestConstants;

public abstract class PgnTestConstants {

  public static final Path PGN_TEST_ROOT_FOLDER_PATH = Nulls
      .pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH, "src/test/resources/pgn");

  public static final Path PGN_PARSER_TEST_ROOT_FOLDER_PATH = Nulls
      .pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH, "src/test/resources/pgnParser");

  public static final Path LENIENT_PGN_PARSER_TEST_ROOT_FOLDER_PATH = Nulls
      .pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH, "src/test/resources/pgnParser/lenient");

  public static final Path STRICT_PGN_PARSER_TEST_ROOT_FOLDER_PATH = Nulls
      .pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH, "src/test/resources/pgnParser/strict");

  public static final Path LENIENT_PGN_PARSER_LINE_BREAKS_TEST_ROOT_FOLDER_PATH = Nulls.pathResolve(
      ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH, "src/test/resources/pgnParser/lenient/lineBreaks");

  public static final Path LENIENT_PGN_PARSER_UTF8_TEST_ROOT_FOLDER_PATH = Nulls
      .pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH, "src/test/resources/pgnParser/lenient/utf8");

  public static final Path PGN_EXPORT_TEST_ROOT_FOLDER_PATH = Nulls
      .pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH, "src/test/resources/pgnExport");

  public static final Path PGN_EXPORT_LINE_BREAKS_TEST_ROOT_FOLDER_PATH = Nulls
      .pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH, "src/test/resources/pgnExport/lineBreaks");

  public static final Path PGN_EXPORT_UTF8_TEST_ROOT_FOLDER_PATH = Nulls
      .pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH, "src/test/resources/pgnExport/utf8");
}
