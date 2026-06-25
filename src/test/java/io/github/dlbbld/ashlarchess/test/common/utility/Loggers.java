// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.common.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.annotation.Nullable;

import io.github.dlbbld.ashlarchess.exceptions.ProgrammingMistakeException;

public final class Loggers {

  private Loggers() {
  }

  public static Logger getLogger(Class<?> type) {
    final @Nullable Logger logger = LogManager.getLogger(type);
    if (logger == null) {
      throw new ProgrammingMistakeException("Logger factory returned null");
    }
    return logger;
  }

}
