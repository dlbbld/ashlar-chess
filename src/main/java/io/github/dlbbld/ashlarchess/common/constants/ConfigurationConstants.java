// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.common.constants;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public abstract class ConfigurationConstants {

  @SuppressWarnings("null")
  public static final Path TEMP_FOLDER_PATH = Paths.get(System.getProperty("java.io.tmpdir"));

  // Locale.ROOT - locale-neutral default. Library messages have no locale-sensitive MessageFormat constructs today
  // (no {0,number}, no {0,date}); using ROOT keeps formatting predictable regardless of where the JVM runs and avoids
  // becoming load-bearing the moment a future message does use such a construct.
  @SuppressWarnings("null")
  public static final Locale LOCALE = Locale.ROOT;
}
