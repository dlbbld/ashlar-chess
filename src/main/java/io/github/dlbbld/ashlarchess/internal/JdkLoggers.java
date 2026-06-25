// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.internal;

import java.util.logging.Logger;

import org.eclipse.jdt.annotation.Nullable;

import io.github.dlbbld.ashlarchess.exceptions.ProgrammingMistakeException;

public final class JdkLoggers {

  private JdkLoggers() {
  }

  public static Logger getLogger(Class<?> type) {
    final @Nullable Logger logger = Logger.getLogger(type.getName());
    if (logger == null) {
      throw new ProgrammingMistakeException("Logger factory returned null");
    }
    return logger;
  }

}
